package me.kafuuneko.rpclient.ui.widgets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class RpChartSegment(
    val value: Float,
    val color: Color,
    val label: String? = null
)

/**
 * 时序趋势图通用数据点定义。
 *
 * @param key 数据点唯一标识（如日期、时间戳字符串）。
 * @param label X 轴显示的短标签（如 "08-31"）。
 * @param value 主量值（用于折线走势绘制与峰值计算）。
 * @param segments 内部子分段列表（用于多色分段柱状图）。
 * @param isCurrent 是否属于当前周期或今日高亮节点。
 */

data class RpChartDataPoint(
    val key: String,
    val label: String,
    val value: Float,
    val segments: List<RpChartSegment> = emptyList(),
    val isCurrent: Boolean = false
)

/**
 * 趋势图表支持的展示形态。
 */

enum class RpChartMode {
    /** 分段柱状图。 */
    Bar,
    /** 平滑面积折线图。 */
    Line
}

/**
 * 将数值格式化为通用的紧凑图表数字表达（<1K 展示整数，>=1K 使用 K/M/B 并最多保留 2 位有效小数）。
 */

fun formatCompactChartValue(value: Float): String {
    val longVal = value.toLong()
    if (longVal < 1_000L) {
        return longVal.toString()
    }
    val (num, unit) = when {
        longVal >= 1_000_000_000L -> (value / 1_000_000_000.0) to "B"
        longVal >= 1_000_000L -> (value / 1_000_000.0) to "M"
        else -> (value / 1_000.0) to "K"
    }
    val formattedNumber = "%.2f".format(Locale.US, num).trimEnd('0').trimEnd('.')
    return "$formattedNumber$unit"
}

/**
 * 通用多段横向比例条。
 *
 * @param segments 各分段的量值与色彩配置。
 * @param modifier 修饰符。
 * @param barHeight 比例条高度，默认 10dp。
 */

@Composable
fun RpChartCoordinateGrid(
    maxValue: Float,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 100.dp,
    axisRailWidth: Dp = 32.dp,
    formatter: (Float) -> String = ::formatCompactChartValue,
    labelContent: (@Composable BoxScope.(value: Float, formattedText: String, alignment: Alignment) -> Unit)? = null
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f)
    val baselineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f)
    val maxText = remember(maxValue) { formatter(maxValue) }
    val midText = remember(maxValue) { formatter(maxValue / 2f) }
    val zeroText = remember { formatter(0f) }

    Box(modifier = modifier.fillMaxWidth()) {
        // 画布绘制：贯穿基准线、顶部/中部水平参考虚线与 Y 轴微型刻度引导线
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            val width = size.width
            val height = size.height
            val railPx = axisRailWidth.toPx()
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 6.dp.toPx()), 0f)

            // 顶部上限水平虚线 (y = 0)
            drawLine(
                color = gridColor,
                start = Offset(railPx, 0f),
                end = Offset(width, 0f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )

            // 中部平分水平虚线 (y = height / 2)
            drawLine(
                color = gridColor,
                start = Offset(railPx, height / 2f),
                end = Offset(width, height / 2f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )

            // 贯穿式底部基准实线 (y = height，从 x = 0 贯穿至 x = width)
            drawLine(
                color = baselineColor,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )

            // Y 轴微型刻度引导短线 (Ticks ┤)
            val tickLength = 4.dp.toPx()
            // 顶部刻度短线
            drawLine(
                color = baselineColor,
                start = Offset(railPx - tickLength, 0f),
                end = Offset(railPx, 0f),
                strokeWidth = 1.dp.toPx()
            )
            // 中部刻度短线
            drawLine(
                color = baselineColor,
                start = Offset(railPx - tickLength, height / 2f),
                end = Offset(railPx, height / 2f),
                strokeWidth = 1.dp.toPx()
            )
        }

        // 左侧 Y 轴刻度标签列
        Box(
            modifier = Modifier
                .width(axisRailWidth)
                .height(chartHeight)
                .padding(end = 6.dp)
        ) {
            if (labelContent != null) {
                labelContent(maxValue, maxText, Alignment.TopEnd)
                labelContent(maxValue / 2f, midText, Alignment.CenterEnd)
                labelContent(0f, zeroText, Alignment.BottomEnd)
            } else {
                // 顶部刻度值
                Text(
                    text = maxText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = labelColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                // 中部刻度值
                Text(
                    text = midText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = labelColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                // 底部原点刻度值 0（稳稳落在基线上方）
                Text(
                    text = zeroText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = labelColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

/**
 * 通用图表形态切换微型开关（用于柱形图与走势图切换）。
 */

@Composable
fun RpChartModeToggle(
    currentMode: RpChartMode,
    onModeSelected: (RpChartMode) -> Unit,
    barLabel: String,
    lineLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        RpChartToggleChip(
            selected = currentMode == RpChartMode.Bar,
            icon = Icons.Rounded.BarChart,
            text = barLabel,
            onClick = { onModeSelected(RpChartMode.Bar) }
        )
        RpChartToggleChip(
            selected = currentMode == RpChartMode.Line,
            icon = Icons.AutoMirrored.Rounded.ShowChart,
            text = lineLabel,
            onClick = { onModeSelected(RpChartMode.Line) }
        )
    }
}

@Composable
internal fun RpChartToggleChip(
    selected: Boolean,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        border = if (selected) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)) else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 通用可横向平滑滚动的柱状图组件。
 *
 * 核心机制：
 * - 闭合直角坐标系架构，左侧轨道稳固锚定 Y 轴刻度与原点 0。
 * - 贯穿式水平基准线横贯全宽，彻底消除左下角空白死角。
 * - 柱体与折线位于轨道右侧视口，绝对无任何物理重叠。
 * - 数据点少于或等于 7 时自动均分填满宽度；多于 7 时开启横向惯性平滑滚动。
 *
 * @param points 数据点列表。
 * @param selectedKey 当前选中的数据点标识。
 * @param onSelectPoint 选中数据点回调。
 * @param modifier 布局修饰符。
 * @param chartHeight 柱体绘制区高度。
 * @param barSlotWidth 横向滚动模式下单槽位宽度。
 * @param axisRailWidth 左侧坐标轴轨道宽度。
 * @param autoScrollToEndKey 触发自动滚动到末尾的 key（如周期切换时）。
 * @param yAxisFormatter 简单数值格式化 lambda。
 * @param yAxisContent 完全自定义 Y 轴层 slot lambda（传入 null 可隐藏 Y 轴）。
 */
