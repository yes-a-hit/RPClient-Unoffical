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
internal fun EditorDialogSwitch(
    state: StoryEditorUiState.Normal,
    emit: StoryEditorUiIntent.() -> Unit
) {
    when (val dialogState = state.dialogState) {
        StoryEditorDialogState.None -> Unit
        StoryEditorDialogState.SummaryTokenLimit -> AppConfirmDialog(
            onDismissRequest = { StoryEditorUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.summary_token_limit_title),
            message = stringResource(
                R.string.summary_token_limit_message,
                stringResource(R.string.summary_memory),
                stringResource(R.string.general_summary_memory),
                stringResource(R.string.summary_response_tokens)
            ),
            confirmText = stringResource(R.string.summary_go_to_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { StoryEditorUiIntent.OpenSummarySettings.emit() }
        )

        is StoryEditorDialogState.ModelSettingsGuide -> AppConfirmDialog(
            onDismissRequest = { StoryEditorUiIntent.DismissDialog.emit() },
            title = dialogState.title,
            message = dialogState.message,
            confirmText = stringResource(R.string.go_to_model_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { StoryEditorUiIntent.OpenProviderSettings.emit() }
        )
        is StoryEditorDialogState.PromptInspector -> PromptInspectorDialog(
            inspection = dialogState.inspection,
            onDismissRequest = { StoryEditorUiIntent.DismissDialog.emit() },
            onCopyRequest = { StoryEditorUiIntent.CopyPromptItem(it).emit() }
        )

        StoryEditorDialogState.FileActions -> FileActionsDialog(emit)
        is StoryEditorDialogState.ImportPreview -> ImportPreviewDialog(dialogState, emit)
        is StoryEditorDialogState.StructureTitleEditor -> AppInputDialog(
            onDismissRequest = {
                if (!dialogState.isSaving) StoryEditorUiIntent.DismissDialog.emit()
            },
            title = when (dialogState.target) {
                StoryStructureTitleTarget.NewVolume -> stringResource(R.string.story_add_volume)
                is StoryStructureTitleTarget.NewChapter -> stringResource(R.string.story_add_chapter)
                is StoryStructureTitleTarget.Volume,
                is StoryStructureTitleTarget.Chapter -> stringResource(R.string.rename)
            },
            value = dialogState.title,
            onValueChange = { StoryEditorUiIntent.ChangeStructureTitle(it).emit() },
            label = stringResource(R.string.story_structure_title),
            enabled = !dialogState.isSaving,
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            confirmEnabled = dialogState.title.isNotBlank() && !dialogState.isSaving,
            isConfirmLoading = dialogState.isSaving,
            onConfirm = { StoryEditorUiIntent.ConfirmStructureTitle.emit() }
        )

        is StoryEditorDialogState.DeleteVolume -> AppDangerDialog(
            onDismissRequest = {
                if (!dialogState.isSaving) StoryEditorUiIntent.DismissDialog.emit()
            },
            title = stringResource(R.string.story_delete_volume),
            message = stringResource(R.string.story_delete_volume_message, dialogState.title),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            confirmEnabled = !dialogState.isSaving,
            isConfirmLoading = dialogState.isSaving,
            onConfirm = { StoryEditorUiIntent.ConfirmDeleteVolume.emit() }
        )

        is StoryEditorDialogState.DeleteChapter -> AppDangerDialog(
            onDismissRequest = {
                if (!dialogState.isSaving) StoryEditorUiIntent.DismissDialog.emit()
            },
            title = stringResource(R.string.story_delete_chapter),
            message = stringResource(R.string.story_delete_chapter_message, dialogState.title),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            confirmEnabled = !dialogState.isSaving,
            isConfirmLoading = dialogState.isSaving,
            onConfirm = { StoryEditorUiIntent.ConfirmDeleteChapter.emit() }
        )

        is StoryEditorDialogState.MoveChapter -> StoryMoveChapterDialog(
            onDismissRequest = {
                if (!dialogState.isSaving) StoryEditorUiIntent.DismissDialog.emit()
            },
            chapterTitle = dialogState.title,
            options = buildList {
                add(
                    StoryChapterDestinationOption(
                        volumeId = null,
                        title = stringResource(R.string.story_ungrouped_chapters),
                        chapterCount = state.structureState.ungroupedChapters.size
                    )
                )
                state.structureState.volumes.forEach { volume ->
                    add(
                        StoryChapterDestinationOption(
                            volumeId = volume.id,
                            title = volume.title,
                            chapterCount = volume.chapters.size
                        )
                    )
                }
            },
            selectedVolumeId = when (val destination = dialogState.selectedDestination) {
                StoryChapterDestination.Ungrouped -> null
                is StoryChapterDestination.Volume -> destination.volumeId
            },
            isSaving = dialogState.isSaving,
            onDestinationSelected = { volumeId ->
                StoryEditorUiIntent.SelectChapterDestination(
                    volumeId?.let(StoryChapterDestination::Volume)
                        ?: StoryChapterDestination.Ungrouped
                ).emit()
            },
            onConfirm = { StoryEditorUiIntent.ConfirmMoveStoryChapter.emit() }
        )

        StoryEditorDialogState.SummarizingStory -> LoadingDialog(
            title = stringResource(R.string.story_summarizing),
            description = stringResource(R.string.story_summary_desc),
            onCancel = { StoryEditorUiIntent.CancelStorySummary.emit() }
        )

        is StoryEditorDialogState.StorySummaryPreview -> StorySummaryPreviewDialog(
            dialogState,
            emit
        )
    }
}

@Composable
internal fun StorySummaryPreviewDialog(
    state: StoryEditorDialogState.StorySummaryPreview,
    emit: StoryEditorUiIntent.() -> Unit
) {
    AppDialogScaffold(
        onDismissRequest = { StoryEditorUiIntent.DismissDialog.emit() },
        title = stringResource(R.string.story_summary_preview),
        badgeIcon = Icons.Rounded.Description,
        badgeTone = DialogBadgeTone.Primary,
        compactHeader = true,
        confirmText = stringResource(R.string.confirm),
        dismissText = stringResource(R.string.cancel),
        onConfirm = { StoryEditorUiIntent.ConfirmStorySummary.emit() }
    ) {
        RpScrollableOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.content,
            onValueChange = {},
            minLines = 5,
            maxLines = 12,
            readOnly = true,
            outputTransformation = rememberPromptMacroOutputTransformation(),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
internal fun FileActionsDialog(emit: StoryEditorUiIntent.() -> Unit) {
    AppActionListDialog(
        onDismissRequest = { StoryEditorUiIntent.DismissDialog.emit() },
        title = stringResource(R.string.story_file_actions),
        badgeIcon = Icons.Rounded.FolderOpen,
        badgeTone = DialogBadgeTone.Primary,
        actions = listOf(
            AppActionItem(
                icon = Icons.Rounded.FileDownload,
                title = stringResource(R.string.story_import_text),
                onClick = { StoryEditorUiIntent.ImportTextClick.emit() }
            ),
            AppActionItem(
                icon = Icons.Rounded.FileDownload,
                title = stringResource(R.string.story_import_archive),
                onClick = { StoryEditorUiIntent.ImportStoryClick.emit() }
            ),
            AppActionItem(
                icon = Icons.Rounded.FileUpload,
                title = stringResource(R.string.story_export_txt),
                onClick = { StoryEditorUiIntent.ExportTextClick(StoryTextExportFormat.Text).emit() }
            ),
            AppActionItem(
                icon = Icons.Rounded.FileUpload,
                title = stringResource(R.string.story_export_markdown),
                onClick = {
                    StoryEditorUiIntent.ExportTextClick(StoryTextExportFormat.Markdown).emit()
                }
            ),
            AppActionItem(
                icon = Icons.Rounded.FileUpload,
                title = stringResource(R.string.story_export_archive),
                onClick = { StoryEditorUiIntent.ExportStoryClick.emit() }
            )
        )
    )
}

@Composable
internal fun ImportPreviewDialog(
    state: StoryEditorDialogState.ImportPreview,
    emit: StoryEditorUiIntent.() -> Unit
) {
    val preview = state.preview
    AppDialogScaffold(
        onDismissRequest = {
            if (!preview.isSaving) StoryEditorUiIntent.DismissDialog.emit()
        },
        title = stringResource(R.string.story_import_preview),
        badgeIcon = Icons.Rounded.FileDownload,
        badgeTone = DialogBadgeTone.Primary,
        confirmText = stringResource(R.string.story_import_create),
        dismissText = stringResource(R.string.cancel),
        confirmEnabled = preview.title.isNotBlank() && !preview.isSaving,
        isConfirmLoading = preview.isSaving,
        onConfirm = { StoryEditorUiIntent.ConfirmImport.emit() },
        onDismiss = {
            if (!preview.isSaving) StoryEditorUiIntent.DismissDialog.emit()
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = preview.title,
                onValueChange = { StoryEditorUiIntent.ChangeImportTitle(it).emit() },
                label = { Text(stringResource(R.string.story_title)) },
                singleLine = true,
                enabled = !preview.isSaving,
                shape = RoundedCornerShape(14.dp)
            )
            Text(
                text = stringResource(
                    R.string.story_import_summary,
                    preview.draft.totalCharacterCount,
                    preview.draft.chapterCount,
                    preview.draft.characterHints.size,
                    preview.draft.lorebookHints.size
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun ChapterSortDialog(emit: StoryEditorUiIntent.() -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.story_sort_chapters)) },
        text = {
            Column {
                ChapterSortAlgorithm.entries.forEach { algorithm ->
                    val label = when (algorithm) {
                        ChapterSortAlgorithm.ByIndex -> stringResource(R.string.story_sort_by_index)
                        ChapterSortAlgorithm.ByTitle -> stringResource(R.string.story_sort_by_title)
                        ChapterSortAlgorithm.Reverse -> stringResource(R.string.story_sort_reverse)
                    }
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                StoryEditorUiIntent.ApplyChapterSort(algorithm).emit()
                            }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.script_picker_done))
            }
        }
    )
}
