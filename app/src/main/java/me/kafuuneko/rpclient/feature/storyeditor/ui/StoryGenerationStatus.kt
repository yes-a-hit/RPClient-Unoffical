package me.kafuuneko.rpclient.feature.storyeditor.ui
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryChapterDestination
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryChapterDropPosition
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryChapterDropTarget
import me.kafuuneko.rpclient.feature.storyeditor.model.ChapterSortAlgorithm
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryChapterOutlineItem
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryCharacterActivationMode
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryCharacterOptionItem
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryEditorDocument
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryEditorSnapshot
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryLorebookEntryItem
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryLorebookGroupItem
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryStructureTitleTarget
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryTextExportFormat
import me.kafuuneko.rpclient.feature.storyeditor.model.StoryVolumeOutlineItem
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryContinuationInputState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorContentState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorDialogState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorPageState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorReferenceState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorStructureState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorTopBarState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorUiIntent
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryEditorUiState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryGenerationFailure
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryGenerationPhase
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StoryGenerationState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StorySaveState
import me.kafuuneko.rpclient.feature.storyeditor.presentation.StorySettingsSection
import me.kafuuneko.rpclient.ui.dialog.AppActionItem
import me.kafuuneko.rpclient.ui.dialog.AppActionListDialog
import me.kafuuneko.rpclient.ui.dialog.AppConfirmDialog
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.dialog.AppInputDialog
import me.kafuuneko.rpclient.ui.dialog.DialogBadgeTone
import me.kafuuneko.rpclient.ui.dialog.LoadingDialog
import me.kafuuneko.rpclient.ui.dialog.PromptInspectorDialog
import me.kafuuneko.rpclient.ui.dialog.StoryChapterDestinationOption
import me.kafuuneko.rpclient.ui.dialog.StoryMoveChapterDialog
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.NoProviderBanner
import me.kafuuneko.rpclient.ui.widgets.DraggableItem
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpTagRow
import me.kafuuneko.rpclient.ui.widgets.StoryUserPersonaCard
import me.kafuuneko.rpclient.ui.widgets.MarkdownMessageText
import me.kafuuneko.rpclient.utils.MarkdownHighlightMapper
import me.kafuuneko.rpclient.libs.AppModel
import me.kafuuneko.rpclient.ui.widgets.dragContainer
import me.kafuuneko.rpclient.ui.widgets.draggableScrollIndicator
import me.kafuuneko.rpclient.ui.widgets.rememberLazyListDragDropState
import me.kafuuneko.rpclient.utils.rememberPromptMacroOutputTransformation
import androidx.compose.ui.platform.LocalLocale

@Composable
internal fun StoryGenerationStatusCard(
    generationState: StoryGenerationState,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val streaming = generationState as? StoryGenerationState.Streaming
    val elapsedSeconds = streaming?.let {
        rememberGenerationElapsedSeconds(it.startedAtElapsedRealtime)
    }
    val hasReasoning = streaming?.reasoningPreview?.isNotBlank() == true
    val isExpanded = streaming?.isReasoningExpanded == true
    val hapticFeedback = LocalHapticFeedback.current
    val statusTitle = generationState.statusTitle()
    val statusSubtitle = generationState.statusSubtitle(elapsedSeconds ?: 0L)
    // 仅在确实收到推理内容后允许展开，避免无反馈的伪交互
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(start = 12.dp, end = 12.dp, top = 8.dp)
        .clickable(enabled = hasReasoning) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            StoryEditorUiIntent.ToggleGenerationReasoning.emit()
        }
    Surface(
        modifier = cardModifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.42f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题区保持紧凑，在小屏上优先压缩状态文本而不是操作图标
            StoryGenerationStatusHeader(
                title = statusTitle,
                subtitle = statusSubtitle,
                elapsedSeconds = elapsedSeconds,
                hasReasoning = hasReasoning,
                isExpanded = isExpanded
            )
            // 推理详情限制最大高度，避免长构思挤占整个编辑器
            StoryGenerationReasoningDetail(
                reasoningDetail = streaming?.reasoningDetail.orEmpty(),
                visible = isExpanded
            )
        }
    }
}

/** 状态卡的标题、耗时和展开指示区。 */

@Composable
internal fun StoryGenerationStatusHeader(
    title: String,
    subtitle: String,
    elapsedSeconds: Long?,
    hasReasoning: Boolean,
    isExpanded: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 图标与文字沿用故事页的圆角、弱强调信息密度
        StoryGenerationStatusIcon()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.72f),
                    maxLines = if (isExpanded) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // 耗时和展开箭头固定在尾部，状态标题在窄屏下优先让出空间
        elapsedSeconds?.let { StoryGenerationElapsedBadge(it) }
        if (hasReasoning) {
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Rounded.KeyboardArrowUp
                } else {
                    Icons.Rounded.KeyboardArrowDown
                },
                contentDescription = stringResource(
                    if (isExpanded) R.string.hide else R.string.show
                ),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** 展开显示受限高度的模型构思详情。 */

@Composable
internal fun StoryGenerationReasoningDetail(
    reasoningDetail: String,
    visible: Boolean
) {
    val scrollState = rememberScrollState()
    val isUserDragging by scrollState.interactionSource.collectIsDraggedAsState()
    var shouldFollowBottom by remember { mutableStateOf(true) }
    var manualScrollInProgress by remember { mutableStateOf(false) }
    // 用户开始拖动时立即暂停自动跟随，避免新到达的 token 与手势争夺滚动位置。
    LaunchedEffect(isUserDragging) {
        if (isUserDragging) {
            manualScrollInProgress = true
            shouldFollowBottom = false
        }
    }
    // 等待拖动及其惯性滚动完全结束，再依据最终位置决定是否恢复自动跟随。
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (!isScrolling && manualScrollInProgress) {
                    shouldFollowBottom = scrollState.isAtBottom()
                    manualScrollInProgress = false
                }
            }
    }
    // 仅在保持底部跟随时监听内容高度变化；collectLatest 可及时追上快速流式增量。
    LaunchedEffect(scrollState, visible, shouldFollowBottom) {
        if (!visible || !shouldFollowBottom) return@LaunchedEffect
        snapshotFlow { scrollState.maxValue }
            .distinctUntilChanged()
            .collectLatest { bottom -> scrollState.animateScrollTo(bottom) }
    }
    AnimatedVisibility(
        visible = visible && reasoningDetail.isNotBlank(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        // 内层 Surface 将模型文本与可操作的外层状态卡作视觉分组
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
            border = BorderStroke(
                0.5.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = reasoningDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp)
                    .draggableScrollIndicator(scrollState)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** 是否已经位于推理详情底部；保留少量像素容差以吸收布局取整。 */

internal fun ScrollState.isAtBottom(): Boolean {
    return maxValue - value <= REASONING_SCROLL_BOTTOM_TOLERANCE_PX
}

/** 状态卡左侧的轻量动态标识。 */

@Composable
internal fun StoryGenerationStatusIcon() {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(34.dp),
            strokeWidth = 1.8.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        )
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

/** 用弱强调胶囊展示当前生成任务已经持续的秒数。 */

@Composable
internal fun StoryGenerationElapsedBadge(elapsedSeconds: Long) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.70f)
    ) {
        Text(
            text = stringResource(R.string.story_generation_elapsed_seconds, elapsedSeconds),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** 以单调时钟刷新纯展示用耗时，组合离开后协程会自动取消。 */

@Composable
internal fun rememberGenerationElapsedSeconds(startedAtElapsedRealtime: Long): Long {
    var elapsedSeconds by remember(startedAtElapsedRealtime) { mutableLongStateOf(0L) }
    LaunchedEffect(startedAtElapsedRealtime) {
        while (true) {
            elapsedSeconds = (
                SystemClock.elapsedRealtime() - startedAtElapsedRealtime
            ).coerceAtLeast(0L) / 1_000L
            delay(1_000L)
        }
    }
    return elapsedSeconds
}

/** 将生成状态映射为底栏使用的本地化主标题。 */

@Composable
internal fun StoryGenerationState.statusTitle(): String {
    val resId = when (this) {
        StoryGenerationState.Preparing -> R.string.story_generation_status_preparing
        is StoryGenerationState.Streaming -> when (phase) {
            StoryGenerationPhase.AwaitingResponse -> R.string.story_generation_status_waiting
            StoryGenerationPhase.Connected -> R.string.story_generation_status_connected
            StoryGenerationPhase.Reasoning -> R.string.story_generation_status_reasoning
            StoryGenerationPhase.Writing -> R.string.story_generation_status_writing
        }
        StoryGenerationState.Applying -> R.string.story_generation_status_applying
        else -> return ""
    }
    return stringResource(resId)
}

/** 根据真实阶段提供短提示，长时间首字等待时给出可停止说明。 */

@Composable
internal fun StoryGenerationState.statusSubtitle(elapsedSeconds: Long): String {
    if (this !is StoryGenerationState.Streaming) return ""
    if (phase == StoryGenerationPhase.Reasoning && reasoningPreview.isNotBlank()) {
        return reasoningPreview
    }
    if (
        elapsedSeconds >= SLOW_GENERATION_HINT_SECONDS &&
        phase != StoryGenerationPhase.Writing
    ) {
        return stringResource(R.string.story_generation_slow_hint)
    }
    return ""
}

internal const val SLOW_GENERATION_HINT_SECONDS = 12L

internal const val REASONING_SCROLL_BOTTOM_TOLERANCE_PX = 2

/** 章节排序对话框：选算法后按容器逐个重排。 */
