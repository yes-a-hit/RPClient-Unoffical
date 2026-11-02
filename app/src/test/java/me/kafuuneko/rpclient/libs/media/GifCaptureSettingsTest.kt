/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 GifCaptureSettingsTest 里的用例，它们描述了每个字段的预期。
 */

package me.kafuuneko.rpclient.libs.media

import org.junit.Assert.assertEquals
import org.junit.Test

class GifCaptureSettingsTest {

    @Test
    fun `defaults match the shipped behaviour`() {
        val settings = GifCaptureSettings()
        assertEquals(3, settings.frames)
        assertEquals(100, settings.minDelayMs)
    }

    @Test
    fun `decode keeps in-range values untouched`() {
        val settings = GifCaptureSettings.decode(frames = 5, minDelayMs = 250)
        assertEquals(5, settings.frames)
        assertEquals(250, settings.minDelayMs)
    }

    @Test
    fun `decode clamps values above the upper bound`() {
        val settings = GifCaptureSettings.decode(frames = 99, minDelayMs = 99999)
        assertEquals(GifCaptureSettings.MAX_FRAMES, settings.frames)
        assertEquals(GifCaptureSettings.MAX_DELAY_MS, settings.minDelayMs)
    }

    @Test
    fun `decode falls back to defaults when the stored value is zero`() {
        // 0 是 Kotpref 没写过时的空值，也可能是用户清空了输入框；
        // 当成「用默认」比崩掉整条发图链路合理
        val settings = GifCaptureSettings.decode(frames = 0, minDelayMs = 0)
        assertEquals(GifCaptureSettings.DEFAULT_FRAMES, settings.frames)
        assertEquals(GifCaptureSettings.DEFAULT_MIN_DELAY_MS, settings.minDelayMs)
    }

    @Test
    fun `decode falls back to defaults for negative values too`() {
        // 负数不是「太小」而是「没意义」，与 0 同样处理：回落默认，不钳到下限。
        // 钳到下限会让一次手滑输入悄悄变成「只抽一帧」，用户还以为是模型笨。
        val settings = GifCaptureSettings.decode(frames = -3, minDelayMs = -50)
        assertEquals(GifCaptureSettings.DEFAULT_FRAMES, settings.frames)
        assertEquals(GifCaptureSettings.DEFAULT_MIN_DELAY_MS, settings.minDelayMs)
    }
}
