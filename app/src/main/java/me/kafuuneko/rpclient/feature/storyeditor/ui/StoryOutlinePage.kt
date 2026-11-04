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
internal fun StoryOutlinePage(
    storyTitle: String,
    structureState: StoryEditorStructureState,
    sortDialogVisible: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    var collapsedVolumeIds by remember { mutableStateOf(emptySet<Long>()) }
    if (sortDialogVisible) {
        ChapterSortDialog(
            emit = emit,
            onDismiss = { StoryEditorUiIntent.DismissSortChaptersDialog.emit() }
        )
    }

    val controlsEnabled = !structureState.isUpdating
    val totalChapterCount = structureState.ungroupedChapters.size +
            structureState.volumes.sumOf { it.chapters.size }
    val totalCharacterCount = remember(structureState) {
        structureState.ungroupedChapters.sumOf { it.characterCount } +
                structureState.volumes.sumOf { volume -> volume.chapters.sumOf { it.characterCount } }
    }

    // 通过查找表把可保存的稳定 key 转换为类型化手势目标，避免解析字符串约定。
    val listState = rememberLazyListState()
    val dragNodesByKey = remember(structureState) {
        storyOutlineDragNodes(structureState)
    }
    val dragDropState = rememberLazyListDragDropState(
        lazyListState = listState,
        isItemDraggable = { key -> controlsEnabled && dragNodesByKey[key]?.chapterId != null },
        isItemDropTarget = { key ->
            controlsEnabled && dragNodesByKey[key]?.dropTarget != null
        },
        onMove = { fromKey, toKey ->
            val chapterId = dragNodesByKey[fromKey]?.chapterId
                ?: return@rememberLazyListDragDropState
            val target = dragNodesByKey[toKey]?.dropTarget
                ?: return@rememberLazyListDragDropState
            StoryEditorUiIntent.DragStoryChapter(chapterId, target).emit()
        },
        onDragEnd = { StoryEditorUiIntent.CommitStoryChapterOrder.emit() }
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.story_outline),
                onBack = { StoryEditorUiIntent.CloseStoryOutline.emit() },
                actions = {
                    IconButton(
                        onClick = { StoryEditorUiIntent.ShowSortChaptersDialog.emit() },
                        enabled = controlsEnabled
                    ) {
                        Icon(
                            Icons.Rounded.Sort,
                            contentDescription = stringResource(R.string.story_sort_chapters)
                        )
                    }
                    IconButton(
                        onClick = { StoryEditorUiIntent.ShowCreateVolumeDialog.emit() },
                        enabled = controlsEnabled
                    ) {
                        Icon(
                            Icons.Rounded.Book,
                            contentDescription = stringResource(R.string.story_add_volume)
                        )
                    }
                    IconButton(
                        onClick = { StoryEditorUiIntent.ShowCreateChapterDialog(null).emit() },
                        enabled = controlsEnabled
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.story_add_chapter)
                        )
                    }
                }
            )
        }
    ) { padding ->
        RpLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .dragContainer(dragDropState),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 全书体量与结构概览卡片
            item(key = OUTLINE_SUMMARY_KEY) {
                OutlineSummary(
                    storyTitle = storyTitle,
                    volumeCount = structureState.volumes.size,
                    chapterCount = totalChapterCount,
                    totalCharacterCount = totalCharacterCount,
                    updating = structureState.isUpdating
                )
            }

            // 未分卷章节分组
            val ungroupedTotalWords = structureState.ungroupedChapters.sumOf { it.characterCount }
            item(key = UNGROUPED_HEADER_KEY) {
                OutlineSectionHeader(
                    title = stringResource(R.string.story_ungrouped_chapters),
                    itemCount = structureState.ungroupedChapters.size,
                    totalCharacterCount = ungroupedTotalWords,
                    enabled = controlsEnabled,
                    onAddChapter = {
                        StoryEditorUiIntent.ShowCreateChapterDialog(null).emit()
                    }
                )
            }
            if (structureState.ungroupedChapters.isEmpty()) {
                item(key = UNGROUPED_EMPTY_KEY) {
                    OutlineEmptyMessage(
                        text = stringResource(R.string.story_no_ungrouped_chapters),
                        onAction = { StoryEditorUiIntent.ShowCreateChapterDialog(null).emit() },
                        actionTextRes = R.string.story_add_ungrouped_chapter_button,
                        enabled = controlsEnabled
                    )
                }
            } else {
                items(
                    items = structureState.ungroupedChapters,
                    key = { chapterOutlineKey(it.id) }
                ) { chapter ->
                    val index =
                        structureState.ungroupedChapters.indexOfFirst { it.id == chapter.id }
                    DraggableItem(
                        dragDropState,
                        chapterOutlineKey(chapter.id)
                    ) { isDragging ->
                        StoryChapterOutlineRow(
                            index = index,
                            chapter = chapter,
                            selected = chapter.id == structureState.currentChapterId,
                            isDragging = isDragging,
                            enabled = controlsEnabled,
                            canMoveUp = index > 0,
                            canMoveDown = index in 0 until structureState.ungroupedChapters.lastIndex,
                            canDelete = totalChapterCount > 1,
                            emit = emit
                        )
                    }
                }
                item(key = UNGROUPED_ADD_KEY) {
                    OutlineAddChapterButton(
                        textRes = R.string.story_add_ungrouped_chapter_button,
                        enabled = controlsEnabled,
                        onClick = { StoryEditorUiIntent.ShowCreateChapterDialog(null).emit() }
                    )
                }
            }

            // 各分卷及其包含的章节列表
            structureState.volumes.forEachIndexed { volumeIndex, volume ->
                item(key = volumeHeaderKey(volume.id)) {
                    StoryVolumeOutlineHeader(
                        volume = volume,
                        collapsed = volume.id in collapsedVolumeIds,
                        enabled = controlsEnabled,
                        canMoveUp = volumeIndex > 0,
                        canMoveDown = volumeIndex < structureState.volumes.lastIndex,
                        onToggleCollapsed = {
                            collapsedVolumeIds = if (volume.id in collapsedVolumeIds) {
                                collapsedVolumeIds - volume.id
                            } else {
                                collapsedVolumeIds + volume.id
                            }
                        },
                        onAddChapter = {
                            StoryEditorUiIntent.ShowCreateChapterDialog(volume.id).emit()
                        },
                        emit = emit
                    )
                }
                if (volume.id !in collapsedVolumeIds) {
                    if (volume.chapters.isEmpty()) {
                        item(key = volumeEmptyKey(volume.id)) {
                            OutlineEmptyMessage(
                                text = stringResource(R.string.story_empty_volume_guide),
                                onAction = { StoryEditorUiIntent.ShowCreateChapterDialog(volume.id).emit() },
                                actionTextRes = R.string.story_add_chapter_to_volume,
                                enabled = controlsEnabled
                            )
                        }
                    } else {
                        items(
                            items = volume.chapters,
                            key = { chapterOutlineKey(it.id) }
                        ) { chapter ->
                            val index = volume.chapters.indexOfFirst { it.id == chapter.id }
                            DraggableItem(
                                dragDropState,
                                chapterOutlineKey(chapter.id)
                            ) { isDragging ->
                                StoryChapterOutlineRow(
                                    index = index,
                                    chapter = chapter,
                                    selected = chapter.id == structureState.currentChapterId,
                                    isDragging = isDragging,
                                    enabled = controlsEnabled,
                                    canMoveUp = index > 0,
                                    canMoveDown = index in 0 until volume.chapters.lastIndex,
                                    canDelete = totalChapterCount > 1,
                                    emit = emit
                                )
                            }
                        }
                        item(key = volumeAddKey(volume.id)) {
                            OutlineAddChapterButton(
                                textRes = R.string.story_add_chapter_to_volume,
                                enabled = controlsEnabled,
                                onClick = { StoryEditorUiIntent.ShowCreateChapterDialog(volume.id).emit() }
                            )
                        }
                    }
                }
            }
        }
    }
}

internal const val OUTLINE_SUMMARY_KEY = "outline-summary"

internal const val UNGROUPED_HEADER_KEY = "ungrouped-header"

internal const val UNGROUPED_EMPTY_KEY = "ungrouped-empty"

internal const val UNGROUPED_ADD_KEY = "ungrouped-add-button"

/** 大纲列表节点对应的拖动源和类型化投放目标。 */

internal fun chapterOutlineKey(chapterId: Long): String = "chapter-$chapterId"

internal fun volumeHeaderKey(volumeId: Long): String = "volume-$volumeId"

internal fun volumeEmptyKey(volumeId: Long): String = "volume-$volumeId-empty"

internal fun volumeAddKey(volumeId: Long): String = "volume-$volumeId-add-button"
