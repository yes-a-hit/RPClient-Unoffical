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
fun RpScrollableLineChart(
    points: List<RpChartDataPoint>,
    selectedKey: String?,
    onSelectPoint: (String) -> Unit,
    modifier: Modifier = Modifier,
    pointWidth: Dp = 44.dp,
    axisRailWidth: Dp = 32.dp,
    lineColor: Color = MaterialTheme.colorScheme.primary,
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

    val totalWidth = if (isScrollable) pointWidth * points.size else 300.dp

    Box(modifier = modifier.fillMaxWidth()) {
        // 背景层：闭合直角坐标系标尺与贯穿基线
        if (yAxisContent != null) {
            yAxisContent(maxValue)
        }

        // 前景滑动层：位于 Y 轴轨道右侧的折线与面积
        val containerModifier = if (isScrollable) {
            Modifier
                .fillMaxWidth()
                .padding(start = axisRailWidth)
                .horizontalScroll(scrollState)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(start = axisRailWidth, end = 4.dp)
        }

        Column(modifier = containerModifier) {
            // 画布折线与渐变面积
            Box(
                modifier = Modifier
                    .width(totalWidth)
                    .height(100.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (points.isEmpty()) return@Canvas

                    val width = size.width
                    val height = size.height
                    val stepX = width / (points.size.coerceAtLeast(2) - 1).toFloat()
                    val topPadding = 6f
                    val availableHeight = height - topPadding

                    val coords = points.mapIndexed { index, point ->
                        val x = index * stepX
                        val ratio = (point.value / maxValue).coerceIn(0f, 1f)
                        val y = topPadding + (1f - ratio) * availableHeight
                        Offset(x, y)
                    }

                    // 构建平滑贝塞尔曲线路径
                    val strokePath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        for (i in 0 until coords.size - 1) {
                            val p0 = coords[i]
                            val p1 = coords[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }

                    // 构建渐变闭合填充路径
                    val fillPath = Path().apply {
                        addPath(strokePath)
                        lineTo(coords.last().x, height)
                        lineTo(coords.first().x, height)
                        close()
                    }

                    // 绘制渐变面积背景
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.28f),
                                lineColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // 绘制主走势折线
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 绘制各数据节点微光圆点
                    coords.forEachIndexed { index, offset ->
                        val isSelected = points[index].key == selectedKey
                        if (isSelected) {
                            drawCircle(
                                color = lineColor.copy(alpha = 0.25f),
                                radius = 9.dp.toPx(),
                                center = offset
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 5.dp.toPx(),
                                center = offset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = offset
                            )
                        } else if (points[index].value > 0f) {
                            drawCircle(
                                color = lineColor,
                                radius = 3.dp.toPx(),
                                center = offset
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // 折线图底部 X 轴标签
            Row(
                modifier = Modifier.width(totalWidth),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { point ->
                    val isSelected = point.key == selectedKey
                    Text(
                        text = point.label,
                        modifier = Modifier
                            .width(pointWidth)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSelectPoint(point.key) }
                            ),
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
 * 通用时序趋势卡片容器。
 *
 * 包装标题、副标题/峰值标签、图表形态切换开关、选中详情气泡、闭合坐标系标尺与图表绘制区。
 *
 * @param title 卡片标题。
 * @param points 数据点列表。
 * @param selectedKey 当前选中的数据点标识。
 * @param chartMode 当前图表展示形态（柱形/折线）。
 * @param onSelectPoint 选中数据点回调。
 * @param onSelectChartMode 切换形态回调。
 * @param barLabel 柱形图选项文案。
 * @param lineLabel 走势图选项文案。
 * @param modifier 布局修饰符。
 * @param peakText 峰值描述文案。
 * @param tooltipContent 选中点浮动气泡 slot。
 * @param autoScrollKey 触发自动滚动到末尾的 key。
 * @param axisRailWidth 左侧坐标轴轨道宽度。
 * @param yAxisFormatter 简单数值格式化 lambda。
 * @param yAxisContent 完全自定义 Y 轴层 slot lambda（支持根据图表模式定制，传入 null 可隐藏 Y 轴）。
 */

@Composable
fun RpTrendChartCard(
    title: String,
    points: List<RpChartDataPoint>,
    selectedKey: String?,
    chartMode: RpChartMode,
    onSelectPoint: (String) -> Unit,
    onSelectChartMode: (RpChartMode) -> Unit,
    barLabel: String,
    lineLabel: String,
    modifier: Modifier = Modifier,
    peakText: String? = null,
    tooltipContent: @Composable ((selectedPoint: RpChartDataPoint) -> Unit)? = null,
    autoScrollKey: Any? = Unit,
    axisRailWidth: Dp = 32.dp,
    yAxisFormatter: (Float) -> String = ::formatCompactChartValue,
    yAxisContent: (@Composable BoxScope.(maxValue: Float, chartMode: RpChartMode) -> Unit)? = { maxValue, _ ->
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
    val selectedPoint = points.firstOrNull { it.key == selectedKey }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 卡片头部
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (chartMode == RpChartMode.Bar) Icons.Rounded.BarChart else Icons.AutoMirrored.Rounded.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                RpChartModeToggle(
                    currentMode = chartMode,
                    onModeSelected = onSelectChartMode,
                    barLabel = barLabel,
                    lineLabel = lineLabel
                )
            }

            // 峰值标签
            if (!peakText.isNullOrBlank()) {
                Text(
                    text = peakText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 选中详情浮动气泡
            AnimatedVisibility(
                visible = selectedPoint != null && tooltipContent != null,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (selectedPoint != null && tooltipContent != null) {
                    tooltipContent(selectedPoint)
                }
            }

            // 图表渲染区域（闭合直角坐标系标尺 + 横向滑动图表）
            val delegatedYAxisContent: (@Composable BoxScope.(maxValue: Float) -> Unit)? = if (yAxisContent != null) {
                { maxValue -> yAxisContent(maxValue, chartMode) }
            } else null

            if (chartMode == RpChartMode.Bar) {
                RpScrollableBarChart(
                    points = points,
                    selectedKey = selectedKey,
                    onSelectPoint = onSelectPoint,
                    autoScrollToEndKey = autoScrollKey,
                    axisRailWidth = axisRailWidth,
                    yAxisFormatter = yAxisFormatter,
                    yAxisContent = delegatedYAxisContent
                )
            } else {
                RpScrollableLineChart(
                    points = points,
                    selectedKey = selectedKey,
                    onSelectPoint = onSelectPoint,
                    autoScrollToEndKey = autoScrollKey,
                    axisRailWidth = axisRailWidth,
                    yAxisFormatter = yAxisFormatter,
                    yAxisContent = delegatedYAxisContent
                )
            }
        }
    }
}
