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
fun StoryEditorLayout(
    uiState: StoryEditorUiState,
    document: StoryEditorDocument?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    when (uiState) {
        StoryEditorUiState.None -> EditorLoading()
        is StoryEditorUiState.Normal -> StoryEditorNormal(uiState, document, emit)
        is StoryEditorUiState.Finished -> StoryEditorLayout(uiState.previous, document) {}
    }
}

@Composable
internal fun StoryEditorNormal(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    BackHandler {
        when (state.pageState) {
            StoryEditorPageState.Editor -> StoryEditorUiIntent.Back.emit()
            StoryEditorPageState.Outline -> StoryEditorUiIntent.CloseStoryOutline.emit()
            StoryEditorPageState.LoadingSettings,
            is StoryEditorPageState.Settings -> StoryEditorUiIntent.CloseStorySettings.emit()
        }
    }
    when (val pageState = state.pageState) {
        StoryEditorPageState.Editor -> StoryEditorPage(state, document, emit)
        StoryEditorPageState.Outline -> StoryOutlinePage(
            storyTitle = state.topBarState.title,
            structureState = state.structureState,
            sortDialogVisible = state.sortDialogVisible,
            emit = emit
        )

        StoryEditorPageState.LoadingSettings -> StorySettingsLoadingPage(emit)
        is StoryEditorPageState.Settings -> StorySettingsPage(pageState, emit)
    }
    EditorDialogSwitch(state, emit)
}

@Composable
internal fun StoryEditorPage(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    // 编辑状态位于自适应布局分支之外，窗口尺寸切换时继续复用当前草稿与光标。
    val editorState = remember(document?.chapterId) {
        TextFieldState(document?.content.orEmpty())
    }
    val documentMatchesChapter = document?.chapterId == state.structureState.currentChapterId
    val onContinue = document?.takeIf { documentMatchesChapter }?.let { currentDocument ->
        {
            StoryEditorUiIntent.ContinueStory(
                StoryEditorSnapshot(
                    chapterId = currentDocument.chapterId,
                    content = editorState.text.toString(),
                    isComposing = editorState.composition != null
                )
            ).emit()
        }
    }

    // Keep Markdown preview/edit state above adaptive layout branches so the toolbar can control it.
    val markdownController = remember(document?.chapterId) {
        StoryMarkdownEditorController(AppModel.storyMarkdownRenderingEnabled)
    }
    // 窗口等级不随 IME 开合变化，避免输入时切换布局并打断焦点。
    val layoutMode = storyEditorLayoutMode(currentWindowAdaptiveInfo().windowSizeClass)
    CompositionLocalProvider(LocalStoryMarkdownEditorController provides markdownController) {
        StoryEditorScaffold(
        state = state,
        document = document,
        editorState = editorState,
        documentMatchesChapter = documentMatchesChapter,
        layoutMode = layoutMode,
        onContinue = onContinue,
        emit = emit
        )
    }
}

/** 按窗口约束组织故事编辑器的常规、横屏沉浸或紧凑布局。 */

@Composable
internal fun StoryEditorScaffold(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    layoutMode: StoryEditorLayoutMode,
    onContinue: (() -> Unit)?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 手机横屏模式由左侧导航轨承载，Scaffold 顶栏隐藏以释放纵向空间
            if (layoutMode != StoryEditorLayoutMode.CompactWide) {
                StoryEditorTopBar(state, emit)
            }
        },
        bottomBar = {
            if (layoutMode == StoryEditorLayoutMode.Regular) {
                EditorBottomBar(
                    characterCount = state.contentState.characterCount,
                    referenceState = state.referenceState,
                    contentState = state.contentState,
                    continuationInputState = state.continuationInputState,
                    generationState = state.generationState,
                    canUndoEdit = state.canUndoEdit,
                    canRedoEdit = state.canRedoEdit,
                    onContinue = onContinue,
                    applyBottomInsets = true,
                    emit = emit
                )
            }
        }
    ) { paddingValues ->
        when (layoutMode) {
            StoryEditorLayoutMode.Regular -> StoryEditorRegularContent(
                state = state,
                document = document,
                editorState = editorState,
                documentMatchesChapter = documentMatchesChapter,
                paddingValues = paddingValues,
                emit = emit
            )

            StoryEditorLayoutMode.CompactWide -> StoryEditorLandscapeContent(
                state = state,
                document = document,
                editorState = editorState,
                documentMatchesChapter = documentMatchesChapter,
                paddingValues = paddingValues,
                onContinue = onContinue,
                emit = emit
            )

            StoryEditorLayoutMode.CompactNarrow -> StoryEditorCompactContent(
                state = state,
                document = document,
                editorState = editorState,
                documentMatchesChapter = documentMatchesChapter,
                paddingValues = paddingValues,
                onContinue = onContinue,
                showStatistics = false,
                emit = emit
            )
        }
    }
}

/** 渲染故事编辑器顶栏及页面级操作。 */

@Composable
internal fun StoryEditorTopBar(
    state: StoryEditorUiState.Normal,
    emit: StoryEditorUiIntent.() -> Unit
) {
    AppTopBar(
        title = state.topBarState.title,
        onBack = { StoryEditorUiIntent.Back.emit() },
        actions = {
            // 保存状态常驻顶栏，窗口紧凑时仍能看到草稿是否安全落库。
            SaveStatus(state.topBarState.saveState, emit)
            IconButton(
                onClick = { StoryEditorUiIntent.OpenPromptInspector.emit() },
                enabled = state.hasPromptInspection
            ) {
                Icon(
                    imageVector = Icons.Rounded.Visibility,
                    contentDescription = stringResource(R.string.prompt_inspector_title)
                )
            }
            IconButton(
                onClick = { StoryEditorUiIntent.OpenFileActions.emit() },
                enabled = state.generationState is StoryGenerationState.Idle
            ) {
                Icon(
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = stringResource(R.string.story_file_actions)
                )
            }
            IconButton(
                onClick = { StoryEditorUiIntent.OpenStorySettings.emit() },
                enabled = state.generationState is StoryGenerationState.Idle
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.story_settings)
                )
            }
        }
    )
}

/** 使用原有纵向结构渲染中等及以上高度窗口。 */

@Composable
internal fun StoryEditorRegularContent(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    paddingValues: PaddingValues,
    emit: StoryEditorUiIntent.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 常规高度保留完整问题提示，避免改变竖屏下的信息层级。
        StoryEditorProblemBanners(state, emit)
        StoryEditorMainPane(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 760.dp),
            state = state,
            document = document,
            editorState = editorState,
            documentMatchesChapter = documentMatchesChapter,
            compactChapterBar = false,
            showChapterBar = true,
            emit = emit
        )
    }
}

/**
 * 专为手机横屏与紧凑宽屏环境构建的双翼环抱式沉浸布局。
 *
 * 核心人机工程与视觉设计：
 * - 左右双翼触控：左手掌管大纲与全局导航，右手拇指处于高频续写与历史撤销热区。
 * - 去除顶部全局栏：将全局导航与辅助入口收纳至左侧导航轨，彻底释放纵向高度。
 * - 沉浸稿纸排版：正文直通全屏，配合轻量内联章节条与流式生成微光晕反馈。
 */

@Composable
internal fun SaveStatusMini(
    saveState: StorySaveState,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val clickable = saveState == StorySaveState.Failed
    Surface(
        modifier = Modifier.clickable(enabled = clickable) {
            StoryEditorUiIntent.RetrySave.emit()
        },
        shape = CircleShape,
        color = when (saveState) {
            StorySaveState.Failed,
            StorySaveState.Conflict -> MaterialTheme.colorScheme.errorContainer

            StorySaveState.Dirty,
            StorySaveState.Saving -> MaterialTheme.colorScheme.secondaryContainer

            StorySaveState.Saved -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
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
                modifier = Modifier.size(13.dp),
                tint = when (saveState) {
                    StorySaveState.Failed,
                    StorySaveState.Conflict -> MaterialTheme.colorScheme.onErrorContainer

                    StorySaveState.Dirty,
                    StorySaveState.Saving -> MaterialTheme.colorScheme.onSecondaryContainer

                    StorySaveState.Saved -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * 横屏右侧拇指操作岛，将高频续写、历史回退与灵感面板聚合于右手舒适热区。
 *
 * 核心排布：
 * - 顶部：全书/本章字数微型徽标与状态展示。
 * - 中部：灵感引导触发器与撤销/重做竖排按键。
 * - 底部：高对比度、大触控面积的 AI 续写主控按钮。
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StoryEditorCompactContent(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    paddingValues: PaddingValues,
    onContinue: (() -> Unit)?,
    showStatistics: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val imeVisible = WindowInsets.isImeVisible
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // IME 打开后优先保留正文和正在输入的工具栏，避免提示再次耗尽纵向空间。
        if (!imeVisible) {
            StoryEditorProblemBanners(state, emit)
        }
        StoryEditorMainPane(
            modifier = Modifier.weight(1f),
            state = state,
            document = document,
            editorState = editorState,
            documentMatchesChapter = documentMatchesChapter,
            compactChapterBar = true,
            showChapterBar = !imeVisible,
            emit = emit
        )
        EditorCompactBottomBar(
            characterCount = state.contentState.characterCount,
            referenceState = state.referenceState,
            contentState = state.contentState,
            continuationInputState = state.continuationInputState,
            generationState = state.generationState,
            canUndoEdit = state.canUndoEdit,
            canRedoEdit = state.canRedoEdit,
            onContinue = onContinue,
            showStatistics = showStatistics,
            emit = emit
        )
    }
}

/** 渲染章节入口与正文，使两种自适应结构共享同一编辑器实现。 */

@Composable
internal fun StoryEditorMainPane(
    modifier: Modifier,
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    compactChapterBar: Boolean,
    showChapterBar: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (compactChapterBar) 6.dp else 10.dp)
    ) {
        // 键盘占用紧凑窗口时暂时收起章节入口，为正文保留稳定的可编辑高度。
        if (showChapterBar) {
            CurrentChapterBar(
                state = state.structureState,
                enabled = documentMatchesChapter &&
                        state.contentState.editable &&
                        state.generationState is StoryGenerationState.Idle,
                compact = compactChapterBar,
                onClick = document?.takeIf { documentMatchesChapter }?.let { currentDocument ->
                    {
                        StoryEditorUiIntent.OpenStoryOutline(
                            StoryEditorSnapshot(
                                chapterId = currentDocument.chapterId,
                                content = editorState.text.toString(),
                                isComposing = editorState.composition != null
                            )
                        ).emit()
                    }
                }
            )
        }
        // 文档切换期间保持加载态，避免短暂编辑到上一章节的文本实例。
        if (document == null || !documentMatchesChapter) {
            EditorLoading()
        } else {
            StoryTextEditor(
                modifier = Modifier.weight(1f),
                document = document,
                editorState = editorState,
                editable = state.contentState.editable,
                generationState = state.generationState,
                emit = emit
            )
        }
    }
}

/** 汇总保存、生成和模型配置问题提示。 */

internal fun storyEditorLayoutMode(windowSizeClass: WindowSizeClass): StoryEditorLayoutMode = when {
    windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> {
        StoryEditorLayoutMode.Regular
    }
    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
        StoryEditorLayoutMode.CompactWide
    }
    else -> StoryEditorLayoutMode.CompactNarrow
}

internal enum class StoryEditorLayoutMode {
    Regular,
    CompactWide,
    CompactNarrow
}

/** Shared Markdown mode and confirmation action for the editor and its toolbars. */

@Composable
internal fun EditorLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
internal fun StoryEditorLayoutPreview() {
    AppTheme(dynamicColor = false) {
        StoryEditorLayout(
            uiState = StoryEditorUiState.Normal(
                storyId = 1L,
                topBarState = StoryEditorTopBarState(
                    title = "Rain over the old city"
                ),
                contentState = StoryEditorContentState(
                    characterCount = 128
                ),
                structureState = StoryEditorStructureState(
                    currentChapterId = 1L,
                    currentChapterTitle = "Chapter One",
                    ungroupedChapters = listOf(
                        StoryChapterOutlineItem(1L, "Chapter One", null, 128, 0)
                    )
                ),
                referenceState = StoryEditorReferenceState(
                    hasMemory = true,
                    hasAuthorNote = true,
                    characterCount = 2,
                    lorebookEntryCount = 4
                )
            ),
            document = StoryEditorDocument(
                storyId = 1L,
                chapterId = 1L,
                content = "# Chapter One\n\nRain tapped softly against the station windows.",
                syncVersion = 1L
            ),
            emit = {}
        )
    }
}

@Preview(widthDp = 844, heightDp = 390, showBackground = true)
@Composable
internal fun StoryEditorLayoutLandscapePreview() {
    StoryEditorLayoutPreview()
}

@Preview(widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
internal fun StoryEditorLayoutTabletLandscapePreview() {
    StoryEditorLayoutPreview()
}

@Preview(widthDp = 390, showBackground = true)
@Composable
internal fun StoryGenerationStatusCardPreview() {
    AppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            StoryGenerationStatusCard(
                generationState = StoryGenerationState.Streaming(
                    partialText = "",
                    phase = StoryGenerationPhase.Reasoning,
                    startedAtElapsedRealtime = SystemClock.elapsedRealtime() - 8_000L,
                    reasoningPreview = "正在协调人物动机，并选择适合承接上一段的场景冲突。",
                    reasoningDetail = "先延续雨夜车站的氛围，再通过人物迟疑表现未说出口的矛盾。",
                    isReasoningExpanded = true
                ),
                emit = {}
            )
        }
    }
}
