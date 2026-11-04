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

internal class StoryMarkdownEditorController(initialPreview: Boolean) {
    var preview by mutableStateOf(initialPreview)
    var onConfirm: (() -> Unit)? = null
}

internal val LocalStoryMarkdownEditorController = compositionLocalOf {
    StoryMarkdownEditorController(initialPreview = true)
}

@Composable
internal fun MarkdownModeToolbarAction() {
    val controller = LocalStoryMarkdownEditorController.current
    if (AppModel.storyMarkdownRenderingEnabled) {
        TextButton(
            onClick = {
                if (controller.preview) {
                    controller.preview = false
                } else {
                    controller.onConfirm?.invoke()
                }
            }
        ) {
            Text(
                stringResource(
                    if (controller.preview) R.string.story_markdown_edit else R.string.confirm
                )
            )
        }
    }
}

@Composable
internal fun CurrentChapterBar(
    state: StoryEditorStructureState,
    enabled: Boolean,
    compact: Boolean,
    onClick: (() -> Unit)?
) {
    // 外层容器采用 Secondary Container 柔和配色与微边框
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 10.dp else 14.dp,
                vertical = if (compact) 6.dp else 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 章节主题图标指示
            Icon(
                imageVector = Icons.Rounded.Description,
                contentDescription = null,
                modifier = Modifier.size(if (compact) 20.dp else 22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            // 紧凑高度只保留章节标题，分卷信息可在大纲页继续查看。
            if (compact) {
                Text(
                    text = state.currentChapterTitle,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = state.currentChapterTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = state.currentVolumeTitle
                            ?: stringResource(R.string.story_ungrouped_chapters),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // 进入大纲的轻量胶囊按钮提示
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (!compact) {
                        Text(
                            text = stringResource(R.string.story_outline),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.story_open_outline),
                        modifier = Modifier.size(16.dp),
                        tint = if (enabled) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.38f)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StoryTextEditor(
    modifier: Modifier,
    document: StoryEditorDocument,
    editorState: TextFieldState,
    editable: Boolean,
    generationState: StoryGenerationState,
    emit: StoryEditorUiIntent.() -> Unit
) {
    // 正文不使用 rememberSaveable，避免长文进入 Activity Bundle；Room 承担进程恢复。
    val editorScrollState = rememberScrollState()
    val markdownPreviewScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var editorTextLayout by remember(document.chapterId) { mutableStateOf<TextLayoutResult?>(null) }
    var editorTextLayoutSyncVersion by remember(document.chapterId) { mutableLongStateOf(-1L) }
    var editorTextLayoutLength by remember(document.chapterId) { mutableIntStateOf(-1) }
    var markdownHighlightedStartY by remember(document.chapterId) { mutableStateOf<Float?>(null) }
    var markdownHighlightedPositionSyncVersion by remember(document.chapterId) { mutableLongStateOf(-1L) }
    var markdownHighlightedPositionLength by remember(document.chapterId) { mutableIntStateOf(-1) }
    var shouldScrollToGeneratedRange by remember(document.chapterId) { mutableStateOf(false) }
    val markdownEnabled = AppModel.storyMarkdownRenderingEnabled
    val markdownController = LocalStoryMarkdownEditorController.current
    val markdownPreview = markdownController.preview
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val imeVisibleState = rememberUpdatedState(WindowInsets.isImeVisible)
    val returnToMarkdownPreview: () -> Unit = {
        // Publish the latest field contents before switching away from raw editing.
        StoryEditorUiIntent.EditorSnapshotChanged(
            StoryEditorSnapshot(
                chapterId = document.chapterId,
                content = editorState.text.toString(),
                isComposing = editorState.composition != null
            )
        ).emit()
        markdownController.preview = true
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }
    SideEffect {
        markdownController.onConfirm = returnToMarkdownPreview
    }
    BackHandler(enabled = markdownEnabled && !markdownPreview) {
        returnToMarkdownPreview()
    }
    LaunchedEffect(markdownPreview, markdownEnabled) {
        if (!markdownEnabled) return@LaunchedEffect

        // Preserve the source viewport as a fraction while the two modes have different
        // content/layout heights. Restore only after any IME transition has finished.
        val sourceScrollState = if (markdownPreview) {
            editorScrollState
        } else {
            markdownPreviewScrollState
        }
        val scrollFraction = if (sourceScrollState.maxValue > 0) {
            (sourceScrollState.value.toFloat() / sourceScrollState.maxValue).coerceIn(0f, 1f)
        } else {
            0f
        }

        if (imeVisibleState.value) {
            keyboardController?.hide()
            // Insets changes can resize the editor after the mode switch. Observe the
            // Compose state rather than reading WindowInsets from a coroutine snapshot.
            withTimeoutOrNull(IME_VISIBILITY_SETTLE_TIMEOUT_MILLIS) {
                snapshotFlow { imeVisibleState.value }.first { isVisible -> !isVisible }
            }
        } else if (!markdownPreview) {
            // Opening Raw Text is a viewing action, not an implicit request to start editing.
            keyboardController?.hide()
        }

        withFrameNanos { }
        withFrameNanos { }

        val targetScrollState = if (markdownPreview) {
            markdownPreviewScrollState
        } else {
            editorScrollState
        }
        targetScrollState.scrollTo(
            (targetScrollState.maxValue * scrollFraction)
                .toInt()
                .coerceIn(0, targetScrollState.maxValue)
        )
    }
    val followStreamingOutput = generationState is StoryGenerationState.Streaming
    val generatedTextColor = MaterialTheme.colorScheme.primary
    val isStreaming = generationState is StoryGenerationState.Streaming ||
            generationState is StoryGenerationState.Preparing ||
            generationState is StoryGenerationState.Applying

    // 流式生成中的呼吸光晕动效
    val infiniteTransition = rememberInfiniteTransition(label = "StoryStreamingGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    val displayedEditedRange = document.latestEditedRange.takeUnless {
        generationState is StoryGenerationState.Streaming ||
                generationState is StoryGenerationState.Applying
    }
    val currentEditedRange by rememberUpdatedState(displayedEditedRange)
    val currentEditedStyle by rememberUpdatedState(
        SpanStyle(
            color = generatedTextColor,
            background = generatedTextColor.copy(alpha = 0.12f),
            fontWeight = FontWeight.Medium
        )
    )
    // 转换实例必须跨流式分片保持稳定，否则 BasicTextField 会反复重建布局状态并闪烁。
    val latestEditedOutputTransformation = remember {
        OutputTransformation {
            val editedRange = currentEditedRange
            if (
                editedRange != null &&
                editedRange.start < editedRange.end &&
                editedRange.end <= length
            ) {
                addStyle(
                    spanStyle = currentEditedStyle,
                    start = editedRange.start,
                    end = editedRange.end
                )
            }
        }
    }
    LaunchedEffect(document.syncVersion) {
        if (editorState.text.toString() != document.content) {
            editorState.syncTextPreservingSelection(document.content)
        }
    }
    LaunchedEffect(followStreamingOutput, editorScrollState) {
        if (!followStreamingOutput) return@LaunchedEffect
        // 流式文本布局完成后 maxValue 会持续增长，跟随它才能稳定滚到最新输出。
        snapshotFlow { editorScrollState.maxValue }.collect { bottom ->
            editorScrollState.scrollTo(bottom)
        }
    }
    LaunchedEffect(followStreamingOutput, markdownPreview, markdownPreviewScrollState) {
        if (!followStreamingOutput || !markdownPreview) return@LaunchedEffect
        var previousMax = markdownPreviewScrollState.maxValue
        snapshotFlow { markdownPreviewScrollState.value to markdownPreviewScrollState.maxValue }
            .collect { (value, max) ->
                val wasFollowingBottom = previousMax == 0 || value >= previousMax - 48
                if (max > previousMax && wasFollowingBottom) {
                    markdownPreviewScrollState.scrollTo(max)
                }
                previousMax = max
            }
    }
    LaunchedEffect(generationState, document.syncVersion, markdownPreview, markdownEnabled) {
        when (generationState) {
            is StoryGenerationState.Applying -> {
                // Only a successfully applied result should trigger an automatic scroll.
                shouldScrollToGeneratedRange = true
                return@LaunchedEffect
            }
            is StoryGenerationState.Failed,
            is StoryGenerationState.Preparing,
            is StoryGenerationState.Streaming -> {
                shouldScrollToGeneratedRange = false
                return@LaunchedEffect
            }
            is StoryGenerationState.Idle -> Unit
        }
        if (!shouldScrollToGeneratedRange) return@LaunchedEffect

        val range = document.latestEditedRange
        if (
            range == null ||
            range.start < 0 ||
            range.start >= range.end ||
            range.end > document.content.length
        ) {
            shouldScrollToGeneratedRange = false
            return@LaunchedEffect
        }

        val targetContent = document.content
        val targetContentLength = targetContent.length
        val targetSyncVersion = document.syncVersion
        // The document and TextFieldState are synchronized asynchronously. Wait for the final
        // content, then wait for a layout tagged with the same document revision and text length.
        snapshotFlow { editorState.text.toString() }.first { it == targetContent }

        if (markdownEnabled && markdownPreview) {
            val targetY = snapshotFlow {
                Triple(
                    markdownHighlightedPositionSyncVersion,
                    markdownHighlightedPositionLength,
                    markdownHighlightedStartY
                )
            }.first { (version, length, _) ->
                version == targetSyncVersion && length == targetContentLength
            }.third
            if (targetY != null) {
                markdownPreviewScrollState.scrollTo(
                    targetY.toInt().coerceIn(0, markdownPreviewScrollState.maxValue)
                )
            }
        } else {
            val textLayout = snapshotFlow {
                Triple(editorTextLayoutSyncVersion, editorTextLayoutLength, editorTextLayout)
            }.first { (version, length, layoutResult) ->
                version == targetSyncVersion && length == targetContentLength && layoutResult != null
            }.third
            val targetY = textLayout
                ?.getCursorRect(range.start)
                ?.top
                ?.toInt()
            if (targetY != null) {
                editorScrollState.scrollTo(targetY.coerceIn(0, editorScrollState.maxValue))
            }
        }
        shouldScrollToGeneratedRange = false
    }
    LaunchedEffect(editorState) {
        snapshotFlow {
            StoryEditorSnapshot(
                chapterId = document.chapterId,
                content = editorState.text.toString(),
                isComposing = editorState.composition != null
            )
        }.distinctUntilChanged().collect { snapshot ->
            StoryEditorUiIntent.EditorSnapshotChanged(snapshot).emit()
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isStreaming) 2.dp else 0.dp,
        border = BorderStroke(
            width = if (isStreaming) 1.5.dp else 1.dp,
            color = if (isStreaming) {
                MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            }
        ),
        color = MaterialTheme.colorScheme.surface
    ) {
        BasicTextField(
            state = editorState,
            modifier = Modifier
                .fillMaxSize()
                .draggableScrollIndicator(editorScrollState),
            enabled = editable && (!markdownEnabled || !markdownPreview),
            textStyle = MaterialTheme.typography.bodyLarge.merge(
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                )
            ),
            scrollState = editorScrollState,
            onTextLayout = { getResult ->
                val layoutResult = getResult()
                if (layoutResult != null && layoutResult.layoutInput.text.text == document.content) {
                    // Never tag a stale layout with the latest document revision.
                    editorTextLayout = layoutResult
                    editorTextLayoutSyncVersion = document.syncVersion
                    editorTextLayoutLength = layoutResult.layoutInput.text.length
                }
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            outputTransformation = latestEditedOutputTransformation,
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    if (markdownEnabled && markdownPreview) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .draggableScrollIndicator(
                                            state = markdownPreviewScrollState,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                            thumbThickness = 6.dp
                                        )
                                        .verticalScroll(markdownPreviewScrollState)
                                        .padding(end = 36.dp)
                                ) {
                                val sourceText = editorState.text.toString()
                                val editedRange = displayedEditedRange?.takeIf {
                                    it.start >= 0 && it.start < it.end && it.end <= sourceText.length
                                }
                                val highlightedText = editedRange?.let { range ->
                                    sourceText.substring(range.start, range.end)
                                }
                                val highlightedOccurrenceIndex = if (
                                    editedRange != null && !highlightedText.isNullOrBlank()
                                ) {
                                    MarkdownHighlightMapper.countOccurrencesBefore(
                                        prefix = sourceText.substring(0, editedRange.start),
                                        highlightedText = highlightedText
                                    )
                                } else {
                                    0
                                }
                                MarkdownMessageText(
                                    content = sourceText,
                                    isUser = false,
                                    modifier = Modifier.fillMaxWidth(),
                                    blockSpacing = 16.dp,
                                    highlightedText = highlightedText,
                                    highlightedOccurrenceIndex = highlightedOccurrenceIndex,
                                    onHighlightedPosition = { position ->
                                        // Layout callbacks can arrive before the TextFieldState has
                                        // caught up with the document flow. Do not label those
                                        // coordinates as belonging to the new document revision.
                                        if (sourceText == document.content) {
                                            markdownHighlightedStartY = position
                                            markdownHighlightedPositionSyncVersion = document.syncVersion
                                            markdownHighlightedPositionLength = sourceText.length
                                        }
                                    }
                                )
                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shadowElevation = 3.dp
                                    ) {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    markdownPreviewScrollState.animateScrollTo(0)
                                                }
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.ArrowUpward,
                                                contentDescription = "Scroll to top",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shadowElevation = 3.dp
                                    ) {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    markdownPreviewScrollState.animateScrollTo(
                                                        markdownPreviewScrollState.maxValue
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.ArrowDownward,
                                                contentDescription = "Scroll to bottom",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (markdownEnabled) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                if (editorState.text.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.story_editor_placeholder),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    } else {
                        if (editorState.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.story_editor_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
internal fun SaveStatus(
    saveState: StorySaveState,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val clickable = saveState == StorySaveState.Failed
    Surface(
        modifier = Modifier.clickable(enabled = clickable) {
            StoryEditorUiIntent.RetrySave.emit()
        },
        shape = RoundedCornerShape(50),
        color = when (saveState) {
            StorySaveState.Failed,
            StorySaveState.Conflict -> MaterialTheme.colorScheme.errorContainer

            StorySaveState.Dirty,
            StorySaveState.Saving -> MaterialTheme.colorScheme.secondaryContainer

            StorySaveState.Saved -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (saveState) {
                    StorySaveState.Saved -> Icons.Rounded.Check
                    StorySaveState.Dirty -> Icons.Rounded.Save
                    StorySaveState.Saving -> Icons.Rounded.HourglassTop
                    StorySaveState.Failed,
                    StorySaveState.Conflict -> Icons.Rounded.ErrorOutline
                },
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = stringResource(
                    when (saveState) {
                        StorySaveState.Saved -> R.string.story_saved
                        StorySaveState.Dirty -> R.string.story_unsaved
                        StorySaveState.Saving -> R.string.story_saving
                        StorySaveState.Failed -> R.string.story_save_failed_short
                        StorySaveState.Conflict -> R.string.story_save_conflict_short
                    }
                ),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

internal const val IME_VISIBILITY_SETTLE_TIMEOUT_MILLIS = 750L
