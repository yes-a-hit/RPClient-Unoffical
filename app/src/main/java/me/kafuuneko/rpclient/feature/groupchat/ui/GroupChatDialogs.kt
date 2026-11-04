package me.kafuuneko.rpclient.feature.groupchat.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Image as ImageIcon
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.media.MessageImageState
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatGenerationState
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatMemberItem
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatSceneItem
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatMessageItem
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatConversationState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatDialogState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatLoadState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatPage
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatSettingsState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiIntent
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiState
import me.kafuuneko.rpclient.libs.AppModel
import me.kafuuneko.rpclient.libs.core.ActivityPreview
import me.kafuuneko.rpclient.libs.poke.PokeAction
import me.kafuuneko.rpclient.libs.poke.PokeActions
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatActivationStrategy
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatCharacterCardMode
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatLorebookGroupItem
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatMessageSource
import me.kafuuneko.rpclient.libs.groupchat.model.SceneVisibility
import me.kafuuneko.rpclient.model.MessageContentPart
import me.kafuuneko.rpclient.ui.dialog.AppConfirmDialog
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.PromptInspectorDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogEntry
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogGroup
import me.kafuuneko.rpclient.ui.message.DraftAttachmentTray
import me.kafuuneko.rpclient.ui.message.MessageImageEditButton
import me.kafuuneko.rpclient.ui.message.MessageImageGallery
import me.kafuuneko.rpclient.ui.dialog.MessageImageViewerDialog
import me.kafuuneko.rpclient.ui.theme.getMacaronColor
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.GalleryPickerDialog
import me.kafuuneko.rpclient.ui.widgets.MarkdownMessageText
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentCard
import me.kafuuneko.rpclient.ui.widgets.NoProviderBanner
import me.kafuuneko.rpclient.ui.widgets.PokeActionEditorDialog
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.chatBackgroundLayer
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.draggableLazyListScrollIndicator
import me.kafuuneko.rpclient.ui.widgets.groupchat.GroupChatLorebookSelector

@Composable
internal fun DialogSwitch(
    state: GroupChatUiState.Normal,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    when (val dialogState = state.dialogState) {
        GroupChatDialogState.None -> Unit
        is GroupChatDialogState.SessionLorebook -> GroupSessionLorebookDialog(
            groups = state.settingsState.lorebookGroups,
            dialogState = dialogState,
            emitIntent = emitIntent
        )

        GroupChatDialogState.SummaryTokenLimit -> AppConfirmDialog(
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            title = stringResource(R.string.summary_token_limit_title),
            message = stringResource(
                R.string.summary_token_limit_message,
                stringResource(R.string.summary_memory),
                stringResource(R.string.general_summary_memory),
                stringResource(R.string.summary_response_tokens)
            ),
            confirmText = stringResource(R.string.summary_go_to_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(GroupChatUiIntent.OpenSummarySettings) }
        )

        is GroupChatDialogState.ModelSettingsGuide -> AppConfirmDialog(
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            title = dialogState.title,
            message = dialogState.message,
            confirmText = stringResource(R.string.go_to_model_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(GroupChatUiIntent.OpenProviderSettings) }
        )

        is GroupChatDialogState.PromptInspector -> PromptInspectorDialog(
            inspection = dialogState.inspection,
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            onCopyRequest = { emitIntent(GroupChatUiIntent.CopyPromptItem(it)) },
            onPreviewImages = { ids, index ->
                emitIntent(GroupChatUiIntent.ImageAction(MessageImageAction.Preview(ids, index)))
            }
        )

        is GroupChatDialogState.DeleteMessageConfirm -> AppDangerDialog(
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            title = stringResource(R.string.delete_message_title),
            message = stringResource(R.string.delete_message_confirm),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(GroupChatUiIntent.ConfirmDeleteMessage) }
        )

        GroupChatDialogState.ConfirmClearContext -> AppDangerDialog(
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            title = stringResource(R.string.clear_context_title),
            message = stringResource(R.string.clear_context_message),
            confirmText = stringResource(R.string.clear_context_confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(GroupChatUiIntent.ConfirmClearConversationContext) }
        )

        is GroupChatDialogState.DeleteSessionConfirm -> AppDangerDialog(
            onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
            title = stringResource(R.string.group_chat_delete_title),
            message = stringResource(
                R.string.group_chat_delete_message,
                dialogState.title
            ),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(GroupChatUiIntent.ConfirmDeleteSession) }
        )

        is GroupChatDialogState.SceneEditor -> SceneEditorDialog(
            dialogState = dialogState,
            members = state.members,
            lorebookGroups = state.settingsState.lorebookGroups,
            emitIntent = emitIntent
        )
    }
}

/**
 * 场景编辑对话框。
 *
 * 成员一个都不选等于「全员在场」，这与运行时的过滤口径一致，
 * 所以界面上必须把这句话写出来，否则用户会以为没选就没人说话。
 */

@Composable
internal fun SceneEditorDialog(
    dialogState: GroupChatDialogState.SceneEditor,
    members: List<GroupChatMemberItem>,
    lorebookGroups: List<GroupChatLorebookGroupItem>,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    AppDialogScaffold(
        onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) },
        title = stringResource(
            if (dialogState.sceneId == null) {
                R.string.group_chat_scene_add
            } else {
                R.string.group_chat_scene_edit
            }
        ),
        confirmText = stringResource(R.string.save),
        confirmEnabled = dialogState.name.isNotBlank(),
        onConfirm = { emitIntent(GroupChatUiIntent.SaveSceneEditor) },
        scrollableContent = true
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = dialogState.name,
                onValueChange = { emitIntent(GroupChatUiIntent.ChangeSceneEditorName(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.group_chat_scene_name)) },
                singleLine = true
            )
            OutlinedTextField(
                value = dialogState.description,
                onValueChange = {
                    emitIntent(GroupChatUiIntent.ChangeSceneEditorDescription(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.group_chat_scene_description)) },
                minLines = 2,
                maxLines = 4
            )
            Text(
                text = stringResource(R.string.group_chat_scene_members_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                members.forEach { member ->
                    FilterChip(
                        selected = dialogState.memberIds.contains(member.id),
                        onClick = {
                            emitIntent(GroupChatUiIntent.ToggleSceneEditorMember(member.id))
                        },
                        label = { Text(member.name) }
                    )
                }
            }
            // 世界书：勾了就只剩这几条生效，一条不勾表示这一场不做限制
            if (lorebookGroups.isNotEmpty()) {
                Text(
                    text = "本场世界书（一条不勾表示不限制）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                lorebookGroups.forEach { group ->
                    Text(
                        text = group.lorebookName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        group.entries.forEach { entry ->
                            FilterChip(
                                selected = dialogState.lorebookEntryIds.contains(entry.id),
                                onClick = {
                                    emitIntent(
                                        GroupChatUiIntent.ToggleSceneEditorLorebookEntry(entry.id)
                                    )
                                },
                                label = {
                                    Text(
                                        text = entry.name.ifBlank { "条目 " + entry.id },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
            }
            // 别人看不看得见这次移动：公开走是明面上换个地方，隐蔽则是绕开另外几个人
            Text(
                text = stringResource(R.string.group_chat_scene_visibility),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = dialogState.visibility == SceneVisibility.Open,
                    onClick = {
                        emitIntent(GroupChatUiIntent.ChangeSceneEditorVisibility(SceneVisibility.Open))
                    },
                    label = { Text(stringResource(R.string.group_chat_scene_visibility_open)) }
                )
                FilterChip(
                    selected = dialogState.visibility == SceneVisibility.Secret,
                    onClick = {
                        emitIntent(GroupChatUiIntent.ChangeSceneEditorVisibility(SceneVisibility.Secret))
                    },
                    label = { Text(stringResource(R.string.group_chat_scene_visibility_secret)) }
                )
            }
            // 场景自己的背景：换场景时整页视野跟着换，比只换名单直观
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (dialogState.backgroundUuid.isBlank()) {
                        stringResource(R.string.session_background_follow_global)
                    } else {
                        stringResource(R.string.session_background_own)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { emitIntent(GroupChatUiIntent.PickSceneBackground) }) {
                    Text(stringResource(R.string.session_background_pick))
                }
                if (dialogState.backgroundUuid.isNotBlank()) {
                    TextButton(onClick = { emitIntent(GroupChatUiIntent.ClearSceneBackground) }) {
                        Text(stringResource(R.string.session_background_clear))
                    }
                }
            }
            if (dialogState.sceneId != null) {
                TextButton(
                    onClick = {
                        emitIntent(GroupChatUiIntent.DeleteScene(dialogState.sceneId))
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/** 将群聊世界书状态适配到应用级快捷管理对话框。 */

@Composable
internal fun GroupSessionLorebookDialog(
    groups: List<GroupChatLorebookGroupItem>,
    dialogState: GroupChatDialogState.SessionLorebook,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    // 通用 Dialog 只接收展示字段，确认后的会话级持久化仍由群聊状态层负责。
    val dialogGroups = remember(groups) {
        groups.map(GroupChatLorebookGroupItem::toSessionLorebookDialogGroup)
    }
    val visibleDialogGroups = remember(dialogState.visibleGroups) {
        dialogState.visibleGroups.map(GroupChatLorebookGroupItem::toSessionLorebookDialogGroup)
    }
    SessionLorebookDialog(
        groups = dialogGroups,
        visibleGroups = visibleDialogGroups,
        query = dialogState.query,
        enabledEntryIds = dialogState.enabledEntryIds,
        onQueryChange = {
            emitIntent(GroupChatUiIntent.ChangeSessionLorebookDialogQuery(it))
        },
        onToggleGroup = {
            emitIntent(GroupChatUiIntent.ToggleSessionLorebookDialogGroup(it))
        },
        onToggleEntry = {
            emitIntent(GroupChatUiIntent.ToggleSessionLorebookDialogEntry(it))
        },
        onConfirmSelection = {
            emitIntent(GroupChatUiIntent.ConfirmSessionLorebookSelection)
        },
        onManageWorldBooks = { emitIntent(GroupChatUiIntent.OpenWorldBookManager) },
        onDismissRequest = { emitIntent(GroupChatUiIntent.DismissDialog) }
    )
}

/** 转换群聊世界书分组为通用对话框展示模型。 */

internal fun GroupChatLorebookGroupItem.toSessionLorebookDialogGroup(): SessionLorebookDialogGroup {
    return SessionLorebookDialogGroup(
        id = lorebookId,
        name = lorebookName,
        entries = entries.map { entry ->
            SessionLorebookDialogEntry(
                id = entry.id,
                name = entry.name,
                content = entry.content,
                keywords = entry.keywords,
                constant = entry.constant
            )
        }
    )
}

@Composable
internal fun LoadStateOverlay(loadState: GroupChatLoadState) {
    if (loadState == GroupChatLoadState.None) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    when (loadState) {
                        GroupChatLoadState.Deleting -> stringResource(R.string.deleting)
                        GroupChatLoadState.Saving -> stringResource(R.string.saving)
                        GroupChatLoadState.Summarizing ->
                            stringResource(R.string.updating_summary)

                        else -> stringResource(R.string.loading)
                    }
                )
            }
        }
    }
}

internal fun GroupChatActivationStrategy.titleRes(): Int {
    return when (this) {
        GroupChatActivationStrategy.Manual -> R.string.group_chat_strategy_manual
        GroupChatActivationStrategy.Natural -> R.string.group_chat_strategy_natural
        GroupChatActivationStrategy.List -> R.string.group_chat_strategy_list
        GroupChatActivationStrategy.Pooled -> R.string.group_chat_strategy_pooled
    }
}
