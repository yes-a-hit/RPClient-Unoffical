package me.kafuuneko.rpclient.feature.tokenusage.ui
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.tokenusage.model.TokenUsageGroupItem
import me.kafuuneko.rpclient.feature.tokenusage.model.TokenUsageRecordItem
import me.kafuuneko.rpclient.feature.tokenusage.model.TokenUsageSummaryItem
import me.kafuuneko.rpclient.feature.tokenusage.model.TokenUsageTrendPoint
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsageBalanceState
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsageChartMode
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsageDialogState
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsagePeriod
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsageUiIntent
import me.kafuuneko.rpclient.feature.tokenusage.presentation.TokenUsageUiState
import me.kafuuneko.rpclient.libs.llm.model.LLMProviderProtocol
import me.kafuuneko.rpclient.libs.room.entity.LLMTokenUsageSource
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.RankBronzeBadgeColor
import me.kafuuneko.rpclient.ui.theme.RankBronzeTextColor
import me.kafuuneko.rpclient.ui.theme.RankGoldBadgeColor
import me.kafuuneko.rpclient.ui.theme.RankGoldTextColor
import me.kafuuneko.rpclient.ui.theme.RankSilverBadgeColor
import me.kafuuneko.rpclient.ui.theme.RankSilverTextColor
import me.kafuuneko.rpclient.ui.theme.TokenUsageCachedColor
import me.kafuuneko.rpclient.ui.theme.TokenUsageInputColor
import me.kafuuneko.rpclient.ui.theme.TokenUsageLatencyColor
import me.kafuuneko.rpclient.ui.theme.TokenUsageOutputColor
import me.kafuuneko.rpclient.ui.theme.TokenUsageReasoningColor
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.RpChartDataPoint
import me.kafuuneko.rpclient.ui.widgets.RpChartMode
import me.kafuuneko.rpclient.ui.widgets.RpChartSegment
import me.kafuuneko.rpclient.ui.widgets.RpInfoCard
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpStackedRatioBar
import me.kafuuneko.rpclient.ui.widgets.RpTrendChartCard

@Composable
fun TokenUsageLayout(
    uiState: TokenUsageUiState,
    emit: TokenUsageUiIntent.() -> Unit = {}
) {
    BackHandler(enabled = uiState is TokenUsageUiState.Normal) { TokenUsageUiIntent.Back.emit() }
    when (uiState) {
        TokenUsageUiState.None -> Unit
        is TokenUsageUiState.Finished -> TokenUsageLayout(uiState.previous) {}
        is TokenUsageUiState.Normal -> {
            NormalView(uiState, emit)
            DialogSwitch(uiState.dialogState, emit)
        }
    }
}

/** 组合页面导航和统计内容，统一控制空态与清空入口。 */

@Composable
internal fun NormalView(
    uiState: TokenUsageUiState.Normal,
    emit: TokenUsageUiIntent.() -> Unit
) {
    // 清空入口与内容空态采用同一周期内的请求数判断。
    val hasRecords = uiState.summary.requestCount > 0L
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.token_usage),
                onBack = { TokenUsageUiIntent.Back.emit() },
                actions = {
                    if (hasRecords) {
                        IconButton(onClick = { TokenUsageUiIntent.ShowClearConfirmDialog.emit() }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.clear_token_usage)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        UsageContent(uiState, hasRecords, paddingValues, emit)
    }
}

/** 按原顺序组织统计列表，保留各区块的惰性加载边界。 */

@Composable
internal fun UsageContent(
    uiState: TokenUsageUiState.Normal,
    hasRecords: Boolean,
    paddingValues: PaddingValues,
    emit: TokenUsageUiIntent.() -> Unit
) {
    RpLazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 18.dp,
            top = 18.dp,
            end = 18.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            RpPageTitle(
                title = stringResource(R.string.token_usage),
                subtitle = stringResource(R.string.token_usage_subtitle)
            )
        }
        item { PeriodFilters(uiState.selectedPeriod, emit) }
        item { HeroSummaryCard(uiState.summary) }
        item { BillingOverviewCard(uiState.balance, emit) }

        // 空周期保留筛选和总览，只省略趋势、排行与请求明细。
        if (hasRecords) {
            if (uiState.trendPoints.size > 1) {
                item {
                    UsageTrendCard(
                        points = uiState.trendPoints,
                        selectedPointKey = uiState.selectedPointKey,
                        chartMode = uiState.chartMode,
                        selectedPeriod = uiState.selectedPeriod,
                        emit = emit
                    )
                }
            }
            item { RpSectionHeader(stringResource(R.string.token_usage_model_ranking)) }
            itemsIndexed(uiState.groups, key = { _, item -> "${item.model}|${item.endpoint}" }) { index, group ->
                UsageGroupRankCard(index = index + 1, item = group)
            }
            item { RpSectionHeader(stringResource(R.string.token_usage_recent_requests)) }
            // 使用记录 ID 保持卡片展开状态与对应请求绑定。
            items(uiState.recentRecords, key = { it.id }) { record ->
                RecentRequestCard(record)
            }
        } else {
            item {
                RpInfoCard(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Numbers,
                    title = stringResource(R.string.token_usage_no_data),
                    subtitle = stringResource(R.string.token_usage_no_data_desc)
                )
            }
        }

        item {
            RpInfoCard(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.Info,
                title = stringResource(R.string.token_usage_notice_title),
                subtitle = stringResource(R.string.token_usage_notice_desc)
            )
        }
    }
}

/** 渲染趋势与选点提示，将图表操作转换为页面 Intent。 */

@Composable
internal fun DialogSwitch(
    dialogState: TokenUsageDialogState,
    emit: TokenUsageUiIntent.() -> Unit
) {
    // 确认与取消都回传 Intent，由 ViewModel 维护互斥对话框状态。
    when (dialogState) {
        TokenUsageDialogState.None -> Unit
        TokenUsageDialogState.ClearConfirm -> AppDangerDialog(
            onDismissRequest = { TokenUsageUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.clear_token_usage),
            message = stringResource(R.string.clear_token_usage_confirm),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { TokenUsageUiIntent.ConfirmClearRecords.emit() }
        )
    }
}

internal fun TokenUsagePeriod.titleRes(): Int {
    return when (this) {
        TokenUsagePeriod.Today -> R.string.token_usage_today
        TokenUsagePeriod.LastSevenDays -> R.string.token_usage_last_7_days
        TokenUsagePeriod.LastThirtyDays -> R.string.token_usage_last_30_days
        TokenUsagePeriod.AllTime -> R.string.token_usage_all_time
    }
}

internal fun LLMTokenUsageSource.titleRes(): Int {
    return when (this) {
        LLMTokenUsageSource.ProviderReported -> R.string.token_usage_reported
        LLMTokenUsageSource.ModelAwareEstimate -> R.string.token_usage_model_estimate
        LLMTokenUsageSource.ProxyEstimate -> R.string.token_usage_proxy_estimate
    }
}

internal fun LLMProviderProtocol.shortName(): String {
    return when (this) {
        LLMProviderProtocol.OpenAICompatible -> "OpenAI"
        LLMProviderProtocol.Gemini -> "Gemini"
        LLMProviderProtocol.AnthropicMessages -> "Anthropic"
    }
}

internal fun Long.formatted(): String {
    if (this < 1_000L) {
        return this.toString()
    }
    val (value, unit) = when {
        this >= 1_000_000_000L -> (this / 1_000_000_000.0) to "B"
        this >= 1_000_000L -> (this / 1_000_000.0) to "M"
        else -> (this / 1_000.0) to "K"
    }
    val formattedNumber = "%.2f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.')
    return "$formattedNumber$unit"
}

internal fun TokenUsageTrendPoint.toChartDataPoint(): RpChartDataPoint {
    return RpChartDataPoint(
        key = key,
        label = label,
        value = totalTokens.toFloat(),
        segments = listOf(
            RpChartSegment(outputTokens.toFloat(), TokenUsageOutputColor),
            RpChartSegment(inputTokens.toFloat(), TokenUsageInputColor)
        ),
        isCurrent = isCurrent
    )
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
internal fun TokenUsageLayoutPreview() {
    // 固定主题与多种用量来源，便于检查总览、趋势和明细的组合布局。
    AppTheme(dynamicColor = false) {
        TokenUsageLayout(
            TokenUsageUiState.Normal(
                selectedPeriod = TokenUsagePeriod.LastSevenDays,
                summary = TokenUsageSummaryItem(
                    requestCount = 42,
                    inputTokens = 124_680,
                    outputTokens = 38_320,
                    cachedInputTokens = 45_000,
                    reasoningTokens = 6_200
                ),
                trendPoints = listOf(
                    TokenUsageTrendPoint("2026-08-25", "08-25", 32000, 4000, 2000, 3),
                    TokenUsageTrendPoint("2026-08-26", "08-26", 24000, 8000, 5000, 6),
                    TokenUsageTrendPoint("2026-08-27", "08-27", 18000, 6000, 4000, 5),
                    TokenUsageTrendPoint("2026-08-28", "08-28", 35000, 10000, 15000, 10),
                    TokenUsageTrendPoint("2026-08-29", "08-29", 15000, 5000, 3000, 4),
                    TokenUsageTrendPoint("2026-08-30", "08-30", 8000, 2500, 1000, 2),
                    TokenUsageTrendPoint("2026-08-31", "08-31", 12680, 2820, 15000, 12, isCurrent = true)
                ),
                selectedPointKey = "2026-08-28",
                chartMode = TokenUsageChartMode.Bar,
                groups = listOf(
                    TokenUsageGroupItem("gpt-4o-mini", "api.openai.com", 28, 84_680, 25_320, 30000, 0, ratio = 0.67f),
                    TokenUsageGroupItem("claude-3-5-sonnet", "api.anthropic.com", 14, 40_000, 13_000, 15000, 6200, ratio = 0.33f)
                ),
                recentRecords = listOf(
                    TokenUsageRecordItem(
                        id = 1,
                        createTimeText = "08-31 12:30:00",
                        providerName = "ChatGPT",
                        protocol = LLMProviderProtocol.OpenAICompatible,
                        model = "gpt-4o-mini",
                        endpoint = "api.openai.com",
                        isStreaming = true,
                        inputTokens = 2_048,
                        outputTokens = 512,
                        inputSource = LLMTokenUsageSource.ProviderReported,
                        outputSource = LLMTokenUsageSource.ProviderReported,
                        tokenizerName = null,
                        cachedInputTokens = 1_024,
                        reasoningTokens = null,
                        durationMs = 2_340
                    )
                )
            )
        )
    }
}
