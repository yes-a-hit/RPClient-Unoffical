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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StoryEditorLandscapeContent(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    paddingValues: PaddingValues,
    onContinue: (() -> Unit)?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val imeVisible = WindowInsets.isImeVisible
    var isGuidanceExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧极简导航轨
        StoryEditorLeftRail(
            state = state,
            document = document,
            editorState = editorState,
            documentMatchesChapter = documentMatchesChapter,
            compactMode = imeVisible,
            emit = emit
        )

        // 中央沉浸稿纸区域
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // IME 打开后优先保留正文和正在输入的工具栏，避免提示再次耗尽纵向空间
            if (!imeVisible) {
                StoryEditorProblemBanners(state, emit)
            }
            StoryEditorLandscapeCenterPane(
                modifier = Modifier.weight(1f),
                state = state,
                document = document,
                editorState = editorState,
                documentMatchesChapter = documentMatchesChapter,
                isGuidanceExpanded = isGuidanceExpanded,
                onContinue = onContinue,
                emit = emit
            )
        }

        // 右侧拇指操作岛
        StoryEditorRightDock(
            characterCount = state.contentState.characterCount,
            contentState = state.contentState,
            continuationInputState = state.continuationInputState,
            generationState = state.generationState,
            canUndoEdit = state.canUndoEdit,
            canRedoEdit = state.canRedoEdit,
            isGuidanceExpanded = isGuidanceExpanded,
            onToggleGuidance = { isGuidanceExpanded = !isGuidanceExpanded },
            onContinue = onContinue,
            compactMode = imeVisible,
            emit = emit
        )
    }
}

/**
 * 横屏左侧极简导航轨，承载全局返回、大纲入口与关键配置能力。
 *
 * 核心交互：
 * - 顶部：返回上一页与保存状态微标。
 * - 中部：章节大纲入口与当前卷章信息。
 * - 底部：Prompt 检查器、导入导出与故事设置入口。
 */

@Composable
internal fun StoryEditorLeftRail(
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    compactMode: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .width(if (compactMode) 42.dp else 48.dp)
                .padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 顶部：返回按钮与极简保存状态
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { StoryEditorUiIntent.Back.emit() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.size(20.dp)
                    )
                }
                SaveStatusMini(saveState = state.topBarState.saveState, emit = emit)
            }

            // 中部：分卷与大纲快速入口
            IconButton(
                onClick = {
                    document?.takeIf { documentMatchesChapter }?.let { currentDoc ->
                        StoryEditorUiIntent.OpenStoryOutline(
                            StoryEditorSnapshot(
                                chapterId = currentDoc.chapterId,
                                content = editorState.text.toString(),
                                isComposing = editorState.composition != null
                            )
                        ).emit()
                    }
                },
                enabled = documentMatchesChapter &&
                        state.contentState.editable &&
                        state.generationState is StoryGenerationState.Idle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = stringResource(R.string.story_outline),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 底部：检查器、导入导出、设置
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (!compactMode) {
                    IconButton(
                        onClick = { StoryEditorUiIntent.OpenPromptInspector.emit() },
                        enabled = state.hasPromptInspection,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = stringResource(R.string.prompt_inspector_title),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(
                        onClick = { StoryEditorUiIntent.OpenFileActions.emit() },
                        enabled = state.generationState is StoryGenerationState.Idle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = stringResource(R.string.story_file_actions),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { StoryEditorUiIntent.OpenStorySettings.emit() },
                    enabled = state.generationState is StoryGenerationState.Idle,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.story_settings),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

/**
 * 适用于横屏侧边栏的紧凑微标保存状态。
 */

@Composable
internal fun StoryEditorRightDock(
    characterCount: Int,
    contentState: StoryEditorContentState,
    continuationInputState: StoryContinuationInputState,
    generationState: StoryGenerationState,
    canUndoEdit: Boolean,
    canRedoEdit: Boolean,
    isGuidanceExpanded: Boolean,
    onToggleGuidance: () -> Unit,
    onContinue: (() -> Unit)?,
    compactMode: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val historyEnabled = contentState.editable && generationState is StoryGenerationState.Idle

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .width(if (compactMode) 44.dp else 52.dp)
                .padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 顶部：字数微型指示
            if (!compactMode) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (characterCount >= 1000) {
                                String.format(LocalLocale.current.platformLocale, "%.1fk", characterCount / 1000f)
                            } else {
                                "$characterCount"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // 中部：灵感引导折叠切换与撤销/重做
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 灵感引导展开按钮
                Surface(
                    modifier = Modifier.clickable(
                        enabled = contentState.editable && generationState is StoryGenerationState.Idle,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleGuidance()
                        }
                    ),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isGuidanceExpanded || continuationInputState.guidanceDraft.isNotBlank()) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    }
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.story_continuation_guidance),
                            modifier = Modifier.size(16.dp),
                            tint = if (isGuidanceExpanded || continuationInputState.guidanceDraft.isNotBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        StoryEditorUiIntent.UndoLastEdit.emit()
                    },
                    enabled = historyEnabled && canUndoEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Undo,
                        contentDescription = stringResource(R.string.story_undo),
                        modifier = Modifier.size(17.dp)
                    )
                }
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        StoryEditorUiIntent.RedoLastEdit.emit()
                    },
                    enabled = historyEnabled && canRedoEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Redo,
                        contentDescription = stringResource(R.string.story_redo),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // 底部：AI 续写大按键
            LandscapeGenerationAction(
                contentState = contentState,
                generationState = generationState,
                onContinue = onContinue,
                hapticFeedback = hapticFeedback,
                emit = emit
            )
        }
    }
}

/** 渲染横屏操作岛尾部的续写、停止或进行中状态。 */

@Composable
internal fun LandscapeGenerationAction(
    contentState: StoryEditorContentState,
    generationState: StoryGenerationState,
    onContinue: (() -> Unit)?,
    hapticFeedback: HapticFeedback,
    emit: StoryEditorUiIntent.() -> Unit
) {
    when (generationState) {
        is StoryGenerationState.Streaming -> Button(
            modifier = Modifier.size(42.dp),
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                StoryEditorUiIntent.StopGeneration.emit()
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.Rounded.Stop,
                contentDescription = stringResource(R.string.story_stop_generation),
                modifier = Modifier.size(20.dp)
            )
        }

        StoryGenerationState.Preparing,
        StoryGenerationState.Applying -> Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        else -> Button(
            modifier = Modifier.size(42.dp),
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onContinue?.invoke()
            },
            enabled = contentState.editable &&
                    generationState is StoryGenerationState.Idle &&
                    onContinue != null,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = stringResource(R.string.story_continue),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 横屏中央沉浸正文容器，承载微型章节信息与全高正文编辑器。
 */

@Composable
internal fun StoryEditorLandscapeCenterPane(
    modifier: Modifier,
    state: StoryEditorUiState.Normal,
    document: StoryEditorDocument?,
    editorState: TextFieldState,
    documentMatchesChapter: Boolean,
    isGuidanceExpanded: Boolean,
    onContinue: (() -> Unit)?,
    emit: StoryEditorUiIntent.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 内联极简章节条
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // 当前章节名称微胶囊
            Surface(
                modifier = Modifier.clickable(
                    enabled = documentMatchesChapter &&
                            state.contentState.editable &&
                            state.generationState is StoryGenerationState.Idle,
                    onClick = {
                        document?.takeIf { documentMatchesChapter }?.let { currentDoc ->
                            StoryEditorUiIntent.OpenStoryOutline(
                                StoryEditorSnapshot(
                                    chapterId = currentDoc.chapterId,
                                    content = editorState.text.toString(),
                                    isComposing = editorState.composition != null
                                )
                            ).emit()
                        }
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = state.structureState.currentChapterTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.story_open_outline),
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // 展开的引导输入框（如果展开）
        AnimatedVisibility(
            visible = isGuidanceExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.continuationInputState.guidanceDraft,
                onValueChange = { StoryEditorUiIntent.ChangeContinuationGuidance(it).emit() },
                enabled = state.contentState.editable && state.generationState is StoryGenerationState.Idle,
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.story_continuation_guidance_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingIcon = {
                    if (state.continuationInputState.guidanceDraft.isNotBlank()) {
                        IconButton(
                            onClick = { StoryEditorUiIntent.ChangeContinuationGuidance("").emit() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.prompt_editor_clear),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 正文编辑器
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

/** 在紧凑高度使用全宽正文与单行底部工具坞。 */
