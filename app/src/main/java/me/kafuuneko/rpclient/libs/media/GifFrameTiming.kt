package me.kafuuneko.rpclient.libs.media

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 GifFrameTimingTest 里的用例，它们描述了每个字段的预期。
 */

/**
 * 从 GIF 字节流里读出每一帧的显示时间。
 *
 * 为什么不直接用 `Movie.duration()`：`Movie` 是 Android 很早的实现，它对 GIF 的帧延迟
 * 支持并不完整——遇到工具生成、延迟值为 0 的 GIF 时总时长会算成 0，于是「均匀取三帧」
 * 退化成「只取第一帧」，模型看到的永远是开场画面。这正是「读 GIF 只识别头帧」的成因。
 *
 * 这里改为自己走一遍 GIF 结构：只关心 Graphic Control Extension 里的延迟字段
 * （单位 1/100 秒），把每帧的起始时间累加出来。解析失败不抛异常，返回空列表让调用方
 * 回退到 `Movie` 的行为，避免一张稍微不合规的动图直接变成「图片无效」。
 */
internal object GifFrameTiming {

    /**
     * 返回每一帧的起始时间（毫秒）。解析不了就返回空列表。
     *
     * 延迟为 0 的帧（GIF 规范里表示「尽快显示」，渲染器通常按 100ms 处理）会被抬到
     * [minDelayMs]，否则整张图累积时长仍然是 0，等于没修。
     */
    fun frameStartTimes(
        bytes: ByteArray,
        minDelayMs: Int = GifCaptureSettings.DEFAULT_MIN_DELAY_MS
    ): List<Int> {
        // 尾部补 12 个 0：解析器会在若干位置按固定宽度读取，补零后越界也不会抛异常，
        // 少写一堆边界判断。合法 GIF 本来就有结束块，多出的 0 不会被读到。
        val data = bytes + ByteArray(12)
        if (data.size < 14) return emptyList()
        val header = String(data, 0, 6, Charsets.US_ASCII)
        if (header != "GIF87a" && header != "GIF89a") return emptyList()

        var pos = 6
        val screenWidth = u16(data, pos)
        val screenHeight = u16(data, pos + 2)
        if (screenWidth <= 0 || screenHeight <= 0) return emptyList()
        val packed = data[pos + 4].toInt() and 0xFF
        pos += 7
        if (packed and 0x80 != 0) {
            // 全局色表：3 字节一项，项数 2^(N+1)，N 是 packed 的低三位
            pos += 3 * (1 shl ((packed and 0x07) + 1))
        }

        val starts = mutableListOf<Int>()
        var elapsed = 0
        var pendingDelay = -1
        while (pos < data.size) {
            when (data[pos].toInt() and 0xFF) {
                0x3B -> return starts // trailer
                0x21 -> {
                    // 扩展块：标签 1 字节 + 若干数据子块
                    val label = data[pos + 1].toInt() and 0xFF
                    pos += 2
                    if (label == 0xF9 && pos < data.size) {
                        // Graphic Control Extension：块长 1 字节、packed 1 字节、
                        // 延迟 2 字节（小端，单位 1/100 秒）、透明色索引 1 字节、终止符 1 字节
                        val blockSize = data[pos].toInt() and 0xFF
                        if (blockSize >= 4 && pos + 4 < data.size) {
                            pendingDelay = u16(data, pos + 2) * 10
                        }
                    }
                    pos = skipSubBlocks(data, pos)
                }
                0x2C -> {
                    // 图像描述符：左、上、宽、高各 2 字节 + packed 1 字节
                    if (pos + 9 >= data.size) return starts
                    val localPacked = data[pos + 9].toInt() and 0xFF
                    starts += elapsed
                    val delay = if (pendingDelay > 0) pendingDelay else minDelayMs
                    elapsed += delay
                    pendingDelay = -1
                    pos += 10
                    if (localPacked and 0x80 != 0) {
                        pos += 3 * (1 shl ((localPacked and 0x07) + 1))
                    }
                    pos += 1 // LZW 最小码长
                    pos = skipSubBlocks(data, pos)
                }
                else -> return starts // 遇到不认识的块，停在已解析到的部分
            }
        }
        return starts
    }

    /** 从 [pos] 处逐个子块跳过，返回终止符之后的位置。 */
    private fun skipSubBlocks(data: ByteArray, start: Int): Int {
        var pos = start
        while (pos < data.size) {
            val size = data[pos].toInt() and 0xFF
            pos += 1
            if (size == 0) return pos
            pos += size
        }
        return pos
    }

    private fun u16(data: ByteArray, index: Int): Int =
        (data[index].toInt() and 0xFF) or ((data[index + 1].toInt() and 0xFF) shl 8)
}
