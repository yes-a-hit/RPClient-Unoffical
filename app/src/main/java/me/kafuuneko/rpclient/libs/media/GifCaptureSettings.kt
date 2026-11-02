/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 GifCaptureSettingsTest 里的用例，它们描述了每个字段的预期。
 */

package me.kafuuneko.rpclient.libs.media

/**
 * 动图抽帧的配置。
 *
 * 抽帧不是越多越好：每多一帧就多一份视觉 token，而三帧已经够看清「先怎样、后怎样」。
 * 但不同模型的视觉预算差很多，所以做成可配置而不是写死。
 *
 * @param frames 抽几帧拼成连环画；一帧等于退回只看开头，所以下限是 1、上限是 6。
 * @param minDelayMs 帧延迟小于这个值时按这个值算。很多工具导出的 GIF 把延迟写成 0，
 *   照 0 算会让所有帧落在同一时刻，抽出来的三张全一样。
 */
data class GifCaptureSettings(
    val frames: Int = DEFAULT_FRAMES,
    val minDelayMs: Int = DEFAULT_MIN_DELAY_MS
) {
    init {
        require(frames in MIN_FRAMES..MAX_FRAMES) { "frames 必须在 $MIN_FRAMES..$MAX_FRAMES 之间" }
        require(minDelayMs in MIN_DELAY_MS..MAX_DELAY_MS) {
            "minDelayMs 必须在 $MIN_DELAY_MS..$MAX_DELAY_MS 之间"
        }
    }

    companion object {
        const val DEFAULT_FRAMES = 3
        const val MIN_FRAMES = 1
        const val MAX_FRAMES = 6

        const val DEFAULT_MIN_DELAY_MS = 100
        const val MIN_DELAY_MS = 20
        const val MAX_DELAY_MS = 1000

        /**
         * 从存储的原始值构造，越界时回落到默认值而不是抛异常。
         *
         * 设置项是用户可改的，也可能是旧版本留下的数字；为这个崩掉整条发图链路不值得。
         */
        fun decode(frames: Int, minDelayMs: Int): GifCaptureSettings = GifCaptureSettings(
            frames = frames.coerceIn(MIN_FRAMES, MAX_FRAMES).takeIf { frames > 0 } ?: DEFAULT_FRAMES,
            minDelayMs = minDelayMs.coerceIn(MIN_DELAY_MS, MAX_DELAY_MS)
                .takeIf { minDelayMs > 0 } ?: DEFAULT_MIN_DELAY_MS
        )
    }
}
