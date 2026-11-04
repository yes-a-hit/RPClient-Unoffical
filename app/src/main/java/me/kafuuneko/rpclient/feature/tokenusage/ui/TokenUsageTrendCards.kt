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
internal fun UsageTrendCard(
    points: List<TokenUsageTrendPoint>,
    selectedPointKey: String?,
    chartMode: TokenUsageChartMode,
    selectedPeriod: TokenUsagePeriod,
    emit: TokenUsageUiIntent.() -> Unit
) {
    // 趋势点未变时复用图表数据；全零区间不显示峰值说明。
    val chartPoints = remember(points) {
        points.map { it.toChartDataPoint() }
    }
    val peakPoint = points.maxByOrNull { it.totalTokens }
    val peakText = if (peakPoint != null && peakPoint.totalTokens > 0L) {
        stringResource(
            R.string.token_usage_peak_day,
            peakPoint.label,
            peakPoint.totalTokens.formatted()
        )
    } else null

    RpTrendChartCard(
        title = stringResource(R.string.token_usage_trend),
        points = chartPoints,
        selectedKey = selectedPointKey,
        chartMode = when (chartMode) {
            TokenUsageChartMode.Bar -> RpChartMode.Bar
            TokenUsageChartMode.Line -> RpChartMode.Line
        },
        onSelectPoint = { key -> TokenUsageUiIntent.SelectTrendPoint(key).emit() },
        onSelectChartMode = { mode ->
            val target = when (mode) {
                RpChartMode.Bar -> TokenUsageChartMode.Bar
                RpChartMode.Line -> TokenUsageChartMode.Line
            }
            TokenUsageUiIntent.SelectChartMode(target).emit()
        },
        barLabel = stringResource(R.string.token_usage_chart_mode_bar),
        lineLabel = stringResource(R.string.token_usage_chart_mode_line),
        peakText = peakText,
        autoScrollKey = selectedPeriod,
        tooltipContent = { selectedDataPoint ->
            val selectedPoint = points.firstOrNull { it.key == selectedDataPoint.key }
            if (selectedPoint != null) {
                TrendPointDetails(selectedPoint)
            }
        }
    )
}

/** 展示当前选中日期的请求数与总量。 */

@Composable
internal fun TrendPointDetails(selectedPoint: TokenUsageTrendPoint) {
    // 仅展示传入日期，选中项不存在时由趋势卡片省略提示。
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedPoint.key} · ${stringResource(R.string.token_usage_request_count, selectedPoint.requestCount)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${stringResource(R.string.token_usage_total)} ${selectedPoint.totalTokens.formatted()}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/** 展示周期筛选并回传选择操作。 */

@Composable
internal fun PeriodFilters(
    selectedPeriod: TokenUsagePeriod,
    emit: TokenUsageUiIntent.() -> Unit
) {
    // 小屏允许横向滚动，周期切换仍由页面 Intent 驱动。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TokenUsagePeriod.entries.forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { TokenUsageUiIntent.SelectPeriod(period).emit() },
                label = { Text(stringResource(period.titleRes())) }
            )
        }
    }
}

/** 组合当前周期的总量、占比与细分指标。 */
/**
 * 账户余额卡。
 *
 * 只展示服务商返回的真实余额：此前这里还有一块「按报价估算的金额」，
 * 但估算永远对不上账单，展示出来只会误导，已整块移除。
 */

@Composable
internal fun BillingOverviewCard(
    balance: TokenUsageBalanceState,
    emit: TokenUsageUiIntent.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BillingHeader(
                icon = Icons.Rounded.Savings,
                title = stringResource(R.string.token_usage_balance_title),
                onRefresh = { TokenUsageUiIntent.RefreshBalance.emit() },
                refreshing = balance is TokenUsageBalanceState.Loading
            )
            Text(
                text = balanceText(balance),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** 计费分区标题行：图标 + 标题 + 右侧刷新按钮。 */

@Composable
internal fun BillingHeader(
    icon: ImageVector,
    title: String,
    onRefresh: () -> Unit,
    refreshing: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        if (refreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        } else {
            IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.token_usage_refresh),
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** 余额说明；不支持与失败分别给出不同文案。 */

@Composable
internal fun balanceText(balance: TokenUsageBalanceState): String = when (balance) {
    TokenUsageBalanceState.Hidden -> stringResource(R.string.token_usage_balance_unknown)
    TokenUsageBalanceState.Loading -> stringResource(R.string.token_usage_balance_loading)
    is TokenUsageBalanceState.Available -> {
        val amount = formatAmount(balance.remaining)
        "${balance.providerName} · ${currencyPrefix(balance.currency)}$amount"
    }

    is TokenUsageBalanceState.Unsupported -> balance.reason
    is TokenUsageBalanceState.Failed -> balance.reason
}

/** 余额金额格式化，统一两位小数。 */

internal fun formatAmount(value: Double): String = String.format(Locale.US, "%.2f", value)

/** 币种前缀；无法识别时回退成币种代码。 */

internal fun currencyPrefix(currency: String): String = when (currency.uppercase()) {
    "USD" -> "US$"
    "CNY", "RMB" -> "¥"
    else -> "$currency "
}
