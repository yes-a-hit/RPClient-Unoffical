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
internal fun UsageGroupRankCard(index: Int, item: TokenUsageGroupItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UsageGroupHeader(index, item)

            // 极小占比保留最小可见宽度，右侧百分比仍显示实际值。
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(item.ratio.coerceIn(0.01f, 1.0f))
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            UsageGroupMetrics(item)
        }
    }
}

/** 显示前三名专属配色的排名徽章。 */

@Composable
internal fun RankBadge(index: Int) {
    // 前三名使用独立配色，其余名次跟随当前主题。
    val rankBadgeBg = when (index) {
        1 -> RankGoldBadgeColor.copy(alpha = 0.18f)
        2 -> RankSilverBadgeColor.copy(alpha = 0.22f)
        3 -> RankBronzeBadgeColor.copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }
    val rankBadgeText = when (index) {
        1 -> RankGoldTextColor
        2 -> RankSilverTextColor
        3 -> RankBronzeTextColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier.size(28.dp),
        shape = CircleShape,
        color = rankBadgeBg
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = rankBadgeText
            )
        }
    }
}

/** 展示模型、服务端点及其消耗占比。 */

@Composable
internal fun UsageGroupHeader(index: Int, item: TokenUsageGroupItem) {
    // 模型名和端点保留单行省略，避免长名称挤占右侧总量。
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RankBadge(index)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.model,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.endpoint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.totalTokens.formatted(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${(item.ratio * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** 展示同一模型分组的请求数与输入输出总量。 */

@Composable
internal fun UsageGroupMetrics(item: TokenUsageGroupItem) {
    // 分组口径由上层提供，此处不重新聚合请求记录。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.token_usage_request_count, item.requestCount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${stringResource(R.string.token_usage_input)} ${item.inputTokens.formatted()}",
                style = MaterialTheme.typography.labelSmall,
                color = TokenUsageInputColor
            )
            Text(
                text = "${stringResource(R.string.token_usage_output)} ${item.outputTokens.formatted()}",
                style = MaterialTheme.typography.labelSmall,
                color = TokenUsageOutputColor
            )
        }
    }
}

/** 展示单次请求摘要，并在点击后展开统计来源。 */

@Composable
internal fun RecentRequestCard(item: TokenUsageRecordItem) {
    // 展开状态属于当前请求卡片，外围列表以记录 ID 保持身份稳定。
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RequestHeader(item)

            RequestMetadata(item)

            RequestTokenBreakdown(item, isExpanded)

            // 沿用原来的展开与收起动画，详情不参与折叠态布局。
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                RequestDetails(item)
            }
        }
    }
}

/** 展示请求模型、协议与总消耗。 */

@Composable
internal fun RequestHeader(item: TokenUsageRecordItem) {
    // 模型名可省略，协议和总量保持完整标签。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.model,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f)
            ) {
                Text(
                    text = item.protocol.shortName(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f))
        ) {
            Text(
                text = "+${item.totalTokens.formatted()} Tk",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

/** 展示请求时间、耗时、模式及缓存用量。 */

@Composable
internal fun RequestMetadata(item: TokenUsageRecordItem) {
    // 缓存值缺失或为零时省略缓存提示。
    val mode = stringResource(
        if (item.isStreaming) R.string.token_usage_stream else R.string.token_usage_once
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = item.createTimeText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(text = "·", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outlineVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(
                Icons.Rounded.ElectricBolt,
                contentDescription = null,
                tint = TokenUsageLatencyColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = stringResource(R.string.token_usage_duration, item.durationMs),
                style = MaterialTheme.typography.bodySmall,
                color = TokenUsageLatencyColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
        Text(text = "·", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            text = mode,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        if (item.cachedInputTokens != null && item.cachedInputTokens > 0L) {
            Text(text = "·", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = "💾 ${item.cachedInputTokens.formatted()}",
                style = MaterialTheme.typography.bodySmall,
                color = TokenUsageCachedColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

/** 展示输入输出、可用推理用量及展开标识。 */

@Composable
internal fun RequestTokenBreakdown(item: TokenUsageRecordItem, isExpanded: Boolean) {
    // 推理用量作为细分信息显示，不在这里重复计入总量。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "${stringResource(R.string.token_usage_input)} ${item.inputTokens.formatted()}",
                style = MaterialTheme.typography.labelSmall,
                color = TokenUsageInputColor
            )
            Text(
                text = "${stringResource(R.string.token_usage_output)} ${item.outputTokens.formatted()}",
                style = MaterialTheme.typography.labelSmall,
                color = TokenUsageOutputColor
            )
            if (item.reasoningTokens != null && item.reasoningTokens > 0L) {
                Text(
                    text = "🧠 ${item.reasoningTokens.formatted()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TokenUsageReasoningColor
                )
            }
        }

        Icon(
            imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
            modifier = Modifier.size(18.dp)
        )
    }
}

/** 展示服务端点及输入输出计数来源。 */

@Composable
internal fun RequestDetails(item: TokenUsageRecordItem) {
    // 输入与输出可能使用不同计数来源；分词器缺失时不显示名称行。
    val inputSource = stringResource(item.inputSource.titleRes())
    val outputSource = stringResource(item.outputSource.titleRes())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f))
        Text(
            text = stringResource(R.string.token_usage_host, item.endpoint, item.providerName),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        )
        Text(
            text = stringResource(R.string.token_usage_source_format, inputSource, outputSource),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        )
        if (!item.tokenizerName.isNullOrBlank()) {
            Text(
                text = stringResource(R.string.token_usage_tokenizer, item.tokenizerName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}

/** 根据页面状态展示清空记录确认框。 */
