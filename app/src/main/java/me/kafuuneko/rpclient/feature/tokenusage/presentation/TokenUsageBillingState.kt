package me.kafuuneko.rpclient.feature.tokenusage.presentation

/**
 * 账户余额的展示状态。
 *
 * 只有官方提供余额接口的服务商才能拿到数字，其余情况必须显式区分「不支持」与「查询失败」，
 * 避免把「不知道该服务商有没有余额」误显示成 0。
 */
sealed class TokenUsageBalanceState {
    /** 尚未查询，页面不展示余额卡。 */
    data object Hidden : TokenUsageBalanceState()

    /** 查询中。 */
    data object Loading : TokenUsageBalanceState()

    /** 查询成功。 */
    data class Available(
        /** 模型配置名称，说明余额属于哪个账户。 */
        val providerName: String,
        /** 币种符号或代码。 */
        val currency: String,
        /** 当前可用余额。 */
        val remaining: Double,
        /** 赠送额度，仅部分服务商返回。 */
        val granted: Double? = null,
        /** 充值余额，仅部分服务商返回。 */
        val toppedUp: Double? = null,
        /** 累计已用额度，仅 OpenRouter 返回。 */
        val used: Double? = null
    ) : TokenUsageBalanceState()

    /** 该服务商没有公开余额接口。 */
    data class Unsupported(val reason: String) : TokenUsageBalanceState()

    /** 查询失败。 */
    data class Failed(val reason: String) : TokenUsageBalanceState()
}
