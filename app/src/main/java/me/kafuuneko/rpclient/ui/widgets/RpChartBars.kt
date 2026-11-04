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

@Composable
fun RpStackedRatioBar(
    segments: List<RpChartSegment>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 10.dp
) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(0.0001f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 分段进度条主体
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                segments.forEach { segment ->
                    val ratio = (segment.value / total).coerceIn(0f, 1f)
                    val animatedRatio by animateFloatAsState(
                        targetValue = ratio,
                        animationSpec = tween(durationMillis = 400),
                        label = "segmentRatio"
                    )
                    if (animatedRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(animatedRatio.coerceAtLeast(0.005f))
                                .fillMaxHeight()
                                .background(segment.color)
                        )
                    }
                }
            }
        }

        // 图例文案行（仅在存在标签时展示）
        val labeledSegments = segments.filter { !it.label.isNullOrBlank() }
        if (labeledSegments.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labeledSegments.forEach { segment ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(segment.color))
                        Text(
                            text = segment.label.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 通用闭合直角坐标系标尺与网格背景层（工业级 L 型坐标框架）。
 *
 * 核心机制：
 * - 左侧 Y 轴标尺轨道（宽度 axisRailWidth）：垂直对齐 Max、Mid、0 刻度与微型刻度引导短线（Ticks）。
 * - 贯通式水平基准地平线（Continuous Base Floor Line）：从左侧 Y 轴原点 0 底部贯穿全宽至最右侧。
 * - 顶部与中部水平参考虚线：从左侧 Y 轴刻度精准引出至右侧边缘。
 * - 左下角自然成为坐标原点 (0, 0)，彻底消除遮挡与空白死角问题。
 *
 * @param maxValue 当前图表的最大量级。
 * @param modifier 布局修饰符。
 * @param chartHeight 图表主体绘制区高度。
 * @param axisRailWidth 左侧 Y 轴标尺轨道的宽度。
 * @param formatter 刻度数值格式化 lambda。
 * @param labelContent 自定义刻度标签渲染 slot。
 */

@Composable
fun RpScrollableBarChart(
    points: List<RpChartDataPoint>,
    selectedKey: String?,
    onSelectPoint: (String) -> Unit,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 135.dp,
    barSlotWidth: Dp = 42.dp,
    axisRailWidth: Dp = 32.dp,
    autoScrollToEndKey: Any? = Unit,
    yAxisFormatter: (Float) -> String = ::formatCompactChartValue,
    yAxisContent: (@Composable BoxScope.(maxValue: Float) -> Unit)? = { maxValue ->
        if (maxValue > 0.0001f) {
            RpChartCoordinateGrid(
                maxValue = maxValue,
                chartHeight = 100.dp,
                axisRailWidth = axisRailWidth,
                formatter = yAxisFormatter
            )
        }
    }
) {
    val isScrollable = points.size > 7
    val scrollState = rememberScrollState()
    val maxValue = points.maxOfOrNull { it.value }?.coerceAtLeast(0.0001f) ?: 1f

    LaunchedEffect(points.size, autoScrollToEndKey) {
        if (isScrollable) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // 背景层：闭合直角坐标系标尺与贯穿基线
        if (yAxisContent != null) {
            yAxisContent(maxValue)
        }

        // 前景层：位于 Y 轴轨道右侧的图表绘制与滑动视口
        val chartContentModifier = if (isScrollable) {
            Modifier
                .fillMaxWidth()
                .padding(start = axisRailWidth)
                .horizontalScroll(scrollState)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(start = axisRailWidth, end = 4.dp)
        }

        Row(
            modifier = chartContentModifier,
            horizontalArrangement = if (isScrollable) Arrangement.spacedBy(6.dp) else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            points.forEach { point ->
                val isSelected = point.key == selectedKey
                val heightRatio = (point.value / maxValue).coerceIn(0.04f, 1.0f)
                val columnModifier = if (isScrollable) {
                    Modifier.width(barSlotWidth)
                } else {
                    Modifier.weight(1f)
                }

                Column(
                    modifier = columnModifier
                        .height(chartHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectPoint(point.key) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // 柱体
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isScrollable) 0.50f else 0.55f)
                            .height((100 * heightRatio).dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(
                                if (point.value == 0f) {
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.50f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    }
                                } else if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                }
                            )
                    ) {
                        if (point.value > 0f && point.segments.isNotEmpty()) {
                            val segmentTotal = point.segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(0.0001f)
                            Column(modifier = Modifier.fillMaxSize()) {
                                point.segments.forEach { segment ->
                                    val segRatio = (segment.value / segmentTotal).coerceAtLeast(0.001f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(segRatio)
                                            .background(segment.color.copy(alpha = if (isSelected) 1f else 0.85f))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // X 轴日期刻度
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else if (point.isCurrent) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                        },
                        fontWeight = if (isSelected || point.isCurrent) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * 通用可横向平滑滚动的贝塞尔渐变面积折线图组件。
 *
 * 核心机制：
 * - 闭合直角坐标系架构，左侧轨道稳固锚定 Y 轴刻度与原点 0。
 * - 贯穿式水平基准线横贯全宽，从原点平稳起笔绘制贝塞尔曲线与微光面积。
 *
 * @param points 数据点列表。
 * @param selectedKey 当前选中的数据点标识。
 * @param onSelectPoint 选中数据点回调。
 * @param modifier 布局修饰符。
 * @param pointWidth 数据点横向间距。
 * @param axisRailWidth 左侧坐标轴轨道宽度。
 * @param lineColor 折线与微光主色彩。
 * @param autoScrollToEndKey 触发自动滚动到末尾的 key。
 * @param yAxisFormatter 简单数值格式化 lambda。
 * @param yAxisContent 完全自定义 Y 轴层 slot lambda（传入 null 可隐藏 Y 轴）。
 */
