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
internal fun OutlineSummary(
    storyTitle: String,
    volumeCount: Int,
    chapterCount: Int,
    totalCharacterCount: Int,
    updating: Boolean
) {
    // 汇总卡片采用 Primary Container 柔和主色调与通透圆角
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标气泡
            RpIconBubble(Icons.AutoMirrored.Rounded.MenuBook)
            // 标题与聚合统计文案
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = storyTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.story_outline_stats_summary,
                        volumeCount,
                        chapterCount,
                        String.format(LocalLocale.current.platformLocale, "%,d", totalCharacterCount)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (updating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            }
        }
    }
}

@Composable
internal fun OutlineSectionHeader(
    title: String,
    itemCount: Int,
    totalCharacterCount: Int? = null,
    enabled: Boolean,
    onAddChapter: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        if (totalCharacterCount != null) {
            Text(
                text = stringResource(
                    R.string.story_volume_stats,
                    itemCount,
                    String.format(LocalLocale.current.platformLocale, "%,d", totalCharacterCount)
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
        } else {
            Text(
                text = itemCount.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        IconButton(onClick = onAddChapter, enabled = enabled) {
            Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.story_add_chapter))
        }
    }
}

@Composable
internal fun StoryVolumeOutlineHeader(
    volume: StoryVolumeOutlineItem,
    collapsed: Boolean,
    enabled: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onToggleCollapsed: () -> Unit,
    onAddChapter: () -> Unit,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val totalWords = remember(volume.chapters) {
        volume.chapters.sumOf { it.characterCount }
    }
    // 分卷头部采用容器卡片样式，包含章节数与总字数统计
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onToggleCollapsed)
                .padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 折叠与展开图标
            IconButton(onClick = onToggleCollapsed, enabled = enabled) {
                Icon(
                    imageVector = if (collapsed) {
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight
                    } else {
                        Icons.Rounded.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // 分卷标题与统计指标
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = volume.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.story_volume_stats,
                        volume.chapters.size,
                        String.format(LocalLocale.current.platformLocale, "%,d", totalWords)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 快速为本卷添加章节按钮
            IconButton(onClick = onAddChapter, enabled = enabled) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.story_add_chapter_to_volume),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StoryVolumeMenu(
                volumeId = volume.id,
                enabled = enabled,
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                emit = emit
            )
        }
    }
}

@Composable
internal fun StoryChapterOutlineRow(
    index: Int,
    chapter: StoryChapterOutlineItem,
    selected: Boolean,
    isDragging: Boolean = false,
    enabled: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    canDelete: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    // 章节行卡片容器，包含序号、高亮边框、当前编辑状态徽标与拖拽手柄
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                StoryEditorUiIntent.SelectStoryChapter(chapter.id).emit()
            },
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (isDragging) 8.dp else 0.dp,
        tonalElevation = if (selected || isDragging) 3.dp else 0.dp,
        color = when {
            isDragging -> MaterialTheme.colorScheme.surfaceVariant
            selected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (selected || isDragging) 1.5.dp else 1.dp,
            color = when {
                isDragging -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 章节两位工整序号徽标
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                },
                contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ) {
                Text(
                    text = String.format(LocalLocale.current.platformLocale, "%02d", index + 1),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            // 标题与字数/状态行
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (selected) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = stringResource(R.string.story_active_chapter),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.story_character_count,
                            chapter.characterCount
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (chapter.characterCount < 50) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = stringResource(R.string.story_draft_chapter),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    }
                }
            }
            // 拖拽手柄视觉提示
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = stringResource(R.string.story_drag_handle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp)
            )
            StoryChapterMenu(
                chapterId = chapter.id,
                enabled = enabled,
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                canDelete = canDelete,
                emit = emit
            )
        }
    }
}

@Composable
internal fun OutlineAddChapterButton(
    textRes: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
internal fun OutlineEmptyMessage(
    text: String,
    onAction: (() -> Unit)? = null,
    actionTextRes: Int? = null,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onAction != null && actionTextRes != null) {
                TextButton(onClick = onAction, enabled = enabled) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(actionTextRes), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
internal fun StoryVolumeMenu(
    volumeId: Long,
    enabled: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, enabled = enabled) {
            Icon(Icons.Rounded.MoreVert, contentDescription = stringResource(R.string.more))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // 新建章节
            OutlineMenuItem(
                textRes = R.string.story_add_chapter,
                icon = Icons.Rounded.Add,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowCreateChapterDialog(volumeId).emit()
                }
            )
            // 重命名分卷
            OutlineMenuItem(
                textRes = R.string.rename,
                icon = Icons.Rounded.Edit,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowRenameVolumeDialog(volumeId).emit()
                }
            )
            // 上移分卷
            OutlineMenuItem(
                textRes = R.string.move_up,
                icon = Icons.Rounded.ArrowUpward,
                enabled = canMoveUp,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.MoveStoryVolume(volumeId, -1).emit()
                }
            )
            // 下移分卷
            OutlineMenuItem(
                textRes = R.string.move_down,
                icon = Icons.Rounded.ArrowDownward,
                enabled = canMoveDown,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.MoveStoryVolume(volumeId, 1).emit()
                }
            )
            // 删除分卷
            OutlineMenuItem(
                textRes = R.string.delete,
                icon = Icons.Rounded.Delete,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowDeleteVolumeDialog(volumeId).emit()
                }
            )
        }
    }
}

@Composable
internal fun StoryChapterMenu(
    chapterId: Long,
    enabled: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    canDelete: Boolean,
    emit: StoryEditorUiIntent.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, enabled = enabled) {
            Icon(Icons.Rounded.MoreVert, contentDescription = stringResource(R.string.more))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // 重命名章节
            OutlineMenuItem(
                textRes = R.string.rename,
                icon = Icons.Rounded.Edit,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowRenameChapterDialog(chapterId).emit()
                }
            )
            // 移动到其他分卷
            OutlineMenuItem(
                textRes = R.string.story_move_chapter,
                icon = Icons.Rounded.FolderOpen,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowMoveStoryChapterDialog(chapterId).emit()
                }
            )
            // 上移章节
            OutlineMenuItem(
                textRes = R.string.move_up,
                icon = Icons.Rounded.ArrowUpward,
                enabled = canMoveUp,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.MoveStoryChapter(chapterId, -1).emit()
                }
            )
            // 下移章节
            OutlineMenuItem(
                textRes = R.string.move_down,
                icon = Icons.Rounded.ArrowDownward,
                enabled = canMoveDown,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.MoveStoryChapter(chapterId, 1).emit()
                }
            )
            // 删除章节
            OutlineMenuItem(
                textRes = R.string.delete,
                icon = Icons.Rounded.Delete,
                enabled = canDelete,
                onClick = {
                    expanded = false
                    StoryEditorUiIntent.ShowDeleteChapterDialog(chapterId).emit()
                }
            )
        }
    }
}

@Composable
internal fun OutlineMenuItem(
    textRes: Int,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(textRes)) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        enabled = enabled,
        onClick = onClick
    )
}

internal data class StoryOutlineDragNode(
    val chapterId: Long? = null,
    val dropTarget: StoryChapterDropTarget? = null
)

/** 为当前大纲建立稳定列表 key 到拖放语义的查找表。 */

internal fun storyOutlineDragNodes(
    state: StoryEditorStructureState
): Map<Any, StoryOutlineDragNode> = buildMap {
    // 分组头部和空状态接收首部投放，追加按钮接收尾部投放。
    put(
        UNGROUPED_HEADER_KEY,
        StoryOutlineDragNode(
            dropTarget = StoryChapterDropTarget.Container(null, StoryChapterDropPosition.Start)
        )
    )
    put(UNGROUPED_EMPTY_KEY, getValue(UNGROUPED_HEADER_KEY))
    put(
        UNGROUPED_ADD_KEY,
        StoryOutlineDragNode(
            dropTarget = StoryChapterDropTarget.Container(null, StoryChapterDropPosition.End)
        )
    )
    state.volumes.forEach { volume ->
        val startNode = StoryOutlineDragNode(
            dropTarget = StoryChapterDropTarget.Container(
                volume.id,
                StoryChapterDropPosition.Start
            )
        )
        put(volumeHeaderKey(volume.id), startNode)
        put(volumeEmptyKey(volume.id), startNode)
        put(
            volumeAddKey(volume.id),
            StoryOutlineDragNode(
                dropTarget = StoryChapterDropTarget.Container(
                    volume.id,
                    StoryChapterDropPosition.End
                )
            )
        )
    }
    // 章节节点既是拖动源，也是插入到该章节之前的投放锚点。
    (state.ungroupedChapters + state.volumes.flatMap { it.chapters }).forEach { chapter ->
        put(
            chapterOutlineKey(chapter.id),
            StoryOutlineDragNode(
                chapterId = chapter.id,
                dropTarget = StoryChapterDropTarget.Chapter(chapter.id)
            )
        )
    }
}
