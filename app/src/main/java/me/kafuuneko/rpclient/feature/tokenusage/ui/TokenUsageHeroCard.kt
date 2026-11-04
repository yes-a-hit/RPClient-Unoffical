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
internal fun HeroSummaryCard(summary: TokenUsageSummaryItem) {
    // 分区组件直接沿用此处的间距，保持总览卡片布局稳定。
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SummaryHeader(summary)

            SummaryRatio(summary)

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f))

            SummaryMetrics(summary)
        }
    }
}

/** 展示总消耗及当前周期请求数。 */

@Composable
internal fun SummaryHeader(summary: TokenUsageSummaryItem) {
    // 总量与请求数共用同一份汇总快照，避免筛选切换时口径混用。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.token_usage_total),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = summary.totalTokens.formatted(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.token_usage_request_count, summary.requestCount),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** 展示输入输出在总量中的比例。 */

@Composable
internal fun SummaryRatio(summary: TokenUsageSummaryItem) {
    // 无消耗时隐藏比例条，避免展示没有意义的占比。
    if (summary.totalTokens > 0L) {
        RpStackedRatioBar(
            segments = listOf(
                RpChartSegment(
                    value = summary.inputTokens.toFloat(),
                    color = TokenUsageInputColor,
                    label = "${stringResource(R.string.token_usage_input)} ${(summary.inputRatio * 100).toInt()}%"
                ),
                RpChartSegment(
                    value = summary.outputTokens.toFloat(),
                    color = TokenUsageOutputColor,
                    label = "${stringResource(R.string.token_usage_output)} ${(summary.outputRatio * 100).toInt()}%"
                )
            )
        )
    }
}

/** 展示输入输出及可用的缓存、推理细分指标。 */

@Composable
internal fun SummaryMetrics(summary: TokenUsageSummaryItem) {
    // 缓存和推理仅在有正值时补充展示，不作为额外消耗加入总量。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryMetricCell(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Storage,
            title = stringResource(R.string.token_usage_input),
            value = summary.inputTokens.formatted(),
            accentColor = TokenUsageInputColor
        )
        SummaryMetricCell(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Stream,
            title = stringResource(R.string.token_usage_output),
            value = summary.outputTokens.formatted(),
            accentColor = TokenUsageOutputColor
        )
    }

    if (summary.cachedInputTokens > 0L || summary.reasoningTokens > 0L) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (summary.cachedInputTokens > 0L) {
                SummaryMetricCell(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Savings,
                    title = stringResource(R.string.token_usage_cached_input, "").trim(),
                    value = summary.cachedInputTokens.formatted(),
                    accentColor = TokenUsageCachedColor
                )
            }
            if (summary.reasoningTokens > 0L) {
                SummaryMetricCell(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.AutoGraph,
                    title = stringResource(R.string.token_usage_reasoning, "").trim(),
                    value = summary.reasoningTokens.formatted(),
                    accentColor = TokenUsageReasoningColor
                )
            }
        }
    }
}

/** 按统一样式展示带图标的统计指标。 */

@Composable
internal fun SummaryMetricCell(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    accentColor: Color
) {
    // 指标配色同时用于图标与边框，单行数值溢出时省略。
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
        border = BorderStroke(0.5.dp, accentColor.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** 展示单个模型分组的排名及消耗占比。 */
