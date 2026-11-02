package me.kafuuneko.rpclient.libs.media

import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GIF 帧时间解析的用例。
 *
 * 这里的输入是手搓的最小 GIF89a：解析器只看结构，不解 LZW 像素，
 * 所以图像数据用任意子块就能通过，不用真的造一张能显示的图。
 */
class GifFrameTimingTest {

    @Test
    fun `reads delay from each graphic control extension`() {
        val gif = buildGif(delays = listOf(10, 20, 30))
        // 延迟单位是 1/100 秒：10 → 100ms，20 → 200ms，30 → 300ms
        assertEquals(listOf(0, 100, 300), GifFrameTiming.frameStartTimes(gif))
    }

    @Test
    fun `zero delay falls back to a visible interval`() {
        // 很多工具生成的 GIF 延迟全写 0；如果照搬成 0 毫秒，
        // 「按时间均匀取三帧」就退化成三张同一画面。
        val gif = buildGif(delays = listOf(0, 0, 0))
        assertEquals(listOf(0, 100, 200), GifFrameTiming.frameStartTimes(gif))
    }

    @Test
    fun `rejects non gif bytes`() {
        assertTrue(GifFrameTiming.frameStartTimes("not a gif at all".toByteArray()).isEmpty())
    }

    @Test
    fun `stops at trailer`() {
        val gif = buildGif(delays = listOf(5, 5))
        // trailer 之后塞一段假数据，不应该被算成第三帧
        val padded = gif + buildImageBlock(delay = 99)
        assertEquals(listOf(0, 50), GifFrameTiming.frameStartTimes(padded))
    }

    /** 拼一个最小的 GIF89a，含全局色表和 [delays] 指定的若干帧。 */
    private fun buildGif(delays: List<Int>): ByteArray {
        val out = ByteArrayOutputStream()
        out.write("GIF89a".toByteArray(Charsets.US_ASCII))
        // 逻辑屏幕描述符：宽 1、高 1、带全局色表（packed 0x80 | 0x00 表示 2 项）
        out.write(byteArrayOf(0x01, 0x00, 0x01, 0x00, 0x80.toByte(), 0x00, 0x00))
        // 全局色表 2 项 × 3 字节
        out.write(ByteArray(6))
        delays.forEach { out.write(buildImageBlock(it)) }
        out.write(0x3B) // trailer
        return out.toByteArray()
    }

    /** 一帧：Graphic Control Extension（带延迟）+ 图像描述符 + 一段空的 LZW 数据。 */
    private fun buildImageBlock(delay: Int): ByteArray {
        val out = ByteArrayOutputStream()
        // 0x21 0xF9 0x04 <packed> <delay lo> <delay hi> <transparent> 0x00
        out.write(0x21)
        out.write(0xF9)
        out.write(0x04)
        out.write(0x00)
        out.write(delay and 0xFF)
        out.write((delay shr 8) and 0xFF)
        out.write(0x00)
        out.write(0x00)
        // 图像描述符：left/top/width/height 各 2 字节 + packed（无局部色表）
        out.write(0x2C)
        out.write(ByteArray(8))
        out.write(0x00)
        out.write(0x02) // LZW 最小码长
        out.write(0x02) // 数据子块长度
        out.write(ByteArray(2))
        out.write(0x00) // 子块终止
        return out.toByteArray()
    }
}
