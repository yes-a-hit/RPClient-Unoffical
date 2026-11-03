package me.kafuuneko.rpclient.libs.media

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `hasImageExtension` 是分流的第一道闸：MIME 报得不准时靠它把图片挑出来。
 * 挑错的后果不是「少发一张图」，而是图片被当文本读成一串乱码塞进提示词。
 */
class PickedFileReaderTest {

    @Test
    fun `accepts the formats the image pipeline can handle`() {
        // 与 MessageImageRuntime.validate 的白名单必须一致，
        // 否则这里放过去、那边再拒，用户看到的是「选了图但没发出去」
        listOf(
            "a.png", "a.jpg", "a.jpeg", "a.webp", "a.gif", "a.bmp", "a.heic", "a.heif"
        ).forEach { name ->
            assertTrue(name, PickedFileReader.hasImageExtension(name))
        }
    }

    @Test
    fun `ignores letter case`() {
        assertTrue(PickedFileReader.hasImageExtension("PHOTO.JPG"))
        assertTrue(PickedFileReader.hasImageExtension("Anim.GIF"))
    }

    @Test
    fun `rejects document formats`() {
        listOf("note.txt", "spec.md", "data.json", "log.jsonl", "doc.docx", "a.pdf")
            .forEach { name ->
                assertFalse(name, PickedFileReader.hasImageExtension(name))
            }
    }

    @Test
    fun `rejects a missing name`() {
        // contentResolver 查不到显示名、URI 也没有末段时就会走到这里
        assertFalse(PickedFileReader.hasImageExtension(null))
    }

    @Test
    fun `rejects a name without extension`() {
        assertFalse(PickedFileReader.hasImageExtension("Makefile"))
    }

    @Test
    fun `rejects a longer extension that merely starts with an image one`() {
        // "报告.png.bak" 是备份文件，不是图；后缀匹配必须落在结尾
        assertFalse(PickedFileReader.hasImageExtension("报告.png.bak"))
    }
}
