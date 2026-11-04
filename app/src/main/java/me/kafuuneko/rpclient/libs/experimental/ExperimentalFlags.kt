package me.kafuuneko.rpclient.libs.experimental

import me.kafuuneko.rpclient.libs.AppModel

/**
 * 实验功能的开关状态。
 *
 * 读的时候永远走 [isEnabled]，不要直接读偏好设置：默认值、占位项、
 * 未来从别处注入的开关都收在这一个地方，调用点看不出差别。
 */
object ExperimentalFlags {

    /** 逗号分隔。空串表示一个都没开。 */
    private const val SEPARATOR = ","

    /**
     * 这个功能现在能不能用。
     *
     * 未实装的登记项一律返回 false —— 即使偏好设置里被写进了 key
     * （比如从旧版本或备份里带过来的），也不该让一个空壳开关显示成"已启用"。
     */
    fun isEnabled(feature: ExperimentalFeature): Boolean {
        if (!feature.available) return false
        return enabledKeys().contains(feature.key)
    }

    fun setEnabled(feature: ExperimentalFeature, enabled: Boolean) {
        if (!feature.available) return
        val current = enabledKeys().toMutableSet()
        val changed = if (enabled) current.add(feature.key) else current.remove(feature.key)
        if (!changed) return
        AppModel.experimentalFeatures = current.joinToString(SEPARATOR)
    }

    fun enabledKeys(): Set<String> =
        AppModel.experimentalFeatures
            .split(SEPARATOR)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

    /** 「危险」的那几个现在开着几个，用于在设置页顶部给一句提醒。 */
    fun activeHighRiskCount(): Int =
        ExperimentalFeature.entries.count { it.risk == ExperimentalRisk.High && isEnabled(it) }
}
