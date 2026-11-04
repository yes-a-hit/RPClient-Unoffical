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

internal const val HISTORY_LOAD_THRESHOLD = 4

/** 群聊页 Compose 入口，根据状态渲染对话、成员与世界书设置。 */

@Composable
fun GroupChatLayout(
    uiState: GroupChatUiState,
    emitIntent: (GroupChatUiIntent) -> Unit = {}
) {
    BackHandler(enabled = uiState is GroupChatUiState.Normal) {
        emitIntent(GroupChatUiIntent.Back)
    }
    when (uiState) {
        GroupChatUiState.None -> Unit
        is GroupChatUiState.Finished -> GroupChatLayout(uiState.previous) {}
        is GroupChatUiState.Normal -> {
            GroupChatNormalView(uiState, emitIntent)
            DialogSwitch(uiState, emitIntent)
            if (uiState.galleryPickerVisible) {
                GalleryPickerDialog(
                    onDismiss = { emitIntent(GroupChatUiIntent.CloseGalleryPicker) },
                    onConfirm = { picked ->
                        emitIntent(GroupChatUiIntent.AttachGalleryImages(picked.map { it.fileUuid }))
                    }
                )
            }
            if (uiState.settingsState.pokeActionEditorVisible) {
                PokeActionEditorDialog(
                    drafts = uiState.settingsState.pokeActionDrafts,
                    onDismiss = { emitIntent(GroupChatUiIntent.ClosePokeActionEditor) },
                    onChange = { id, label, template ->
                        emitIntent(GroupChatUiIntent.ChangePokeAction(id, label, template))
                    },
                    onAdd = { emitIntent(GroupChatUiIntent.AddPokeAction) },
                    onRemove = { emitIntent(GroupChatUiIntent.RemovePokeAction(it)) },
                    onSave = { emitIntent(GroupChatUiIntent.SavePokeActions) }
                )
            }
            LoadStateOverlay(uiState.loadState)
        }
    }
}

@Composable
private fun GroupChatNormalView(
    state: GroupChatUiState.Normal,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    if (state.page == GroupChatPage.Settings) {
        GroupChatSettingsView(state.settingsState, state.members, state.hasOwnBackground, emitIntent)
        return
    }
    val generating = state.conversationState.generationState is GroupChatGenerationState.Generating
    val canContinue = state.conversationState.hasCharacterMessage
    Scaffold(
        topBar = {
            AppTopBar(
                title = state.title,
                onBack = { emitIntent(GroupChatUiIntent.Back) },
                leading = { GroupTopAvatars(state.members) },
                actions = {
                    IconButton(
                        onClick = { emitIntent(GroupChatUiIntent.OpenPromptInspector) },
                        enabled = state.hasPromptInspection
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = stringResource(R.string.prompt_inspector_title)
                        )
                    }
                    IconButton(
                        onClick = {
                            emitIntent(GroupChatUiIntent.ShowSessionLoreDialog)
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Book,
                            contentDescription = stringResource(R.string.session_world_books),
                            tint = if (
                                state.dialogState is GroupChatDialogState.SessionLorebook
                            ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    IconButton(
                        onClick = { emitIntent(GroupChatUiIntent.OpenSettings) },
                        enabled = !generating
                    ) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if ((state.conversationState.generationState as? GroupChatGenerationState.Failed)?.canRetryReply == true) {
                    TextButton(
                        onClick = { emitIntent(GroupChatUiIntent.RetryImageReply) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(R.string.image_retry))
                    }
                }
                MessageImageViewerDialog(state.imageState) { emitIntent(GroupChatUiIntent.ImageAction(it)) }
                // 「+」里只放不指向任何人的动作，带 {name} 的留给长按成员卡
                val selfActions = remember(state.settingsState.pokeActions) {
                    me.kafuuneko.rpclient.libs.poke.PokeActions.selfDirected(
                        state.settingsState.pokeActions.ifEmpty {
                            me.kafuuneko.rpclient.libs.poke.PokeActions.parse(
                                me.kafuuneko.rpclient.libs.AppModel.pokeActions
                            )
                        }
                    )
                }
                Composer(
                    draft = state.conversationState.inputDraft,
                    generating = generating,
                    imageState = state.imageState,
                    onDraftChange = {
                        emitIntent(GroupChatUiIntent.ChangeInputDraft(it))
                    },
                    onSend = { emitIntent(GroupChatUiIntent.SendMessage) },
                    onStop = { emitIntent(GroupChatUiIntent.StopGeneration) },
                    onImageAction = { emitIntent(GroupChatUiIntent.ImageAction(it)) },
                    onOpenFilePicker = { emitIntent(GroupChatUiIntent.OpenFilePicker) },
                    onOpenGalleryPicker = { emitIntent(GroupChatUiIntent.OpenGalleryPicker) },
                    onSwitchScene = { emitIntent(GroupChatUiIntent.SwitchScene(it.ifBlank { null })) },
                    sceneMovements = state.sceneMovements,
                    onPerformSelfAction = {
                        emitIntent(GroupChatUiIntent.PerformSelfAction(it))
                    },
                    selfActions = selfActions,
                    documents = state.conversationState.attachedDocuments,
                    onRemoveDocument = {
                        emitIntent(GroupChatUiIntent.RemoveAttachedDocument(it))
                    },
                    canContinue = canContinue,
                    onContinue = { emitIntent(GroupChatUiIntent.ContinueLast) },
                    onSummarize = { emitIntent(GroupChatUiIntent.SummarizeNow) },
                    onImpersonate = { emitIntent(GroupChatUiIntent.ImpersonateUser) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .chatBackgroundLayer(
                    state = state.backgroundState,
                    scrimColor = MaterialTheme.colorScheme.background
                )
                .padding(padding)
        ) {
            if (!state.hasAvailableProvider) {
                NoProviderBanner(
                    onClick = { emitIntent(GroupChatUiIntent.OpenProviderSettings) }
                )
            }
            GroupHeader(
                strategy = state.activeActivationStrategy,
                generationState = state.conversationState.generationState,
                canContinue = canContinue,
                hasAvailableProvider = state.hasAvailableProvider,
                onContinue = { emitIntent(GroupChatUiIntent.ContinueLast) }
            )
            SceneRail(
                scenes = state.scenes,
                activeSceneId = state.activeSceneId,
                enabled = !generating,
                onSelect = { emitIntent(GroupChatUiIntent.SwitchScene(it)) }
            )
            SceneEffectBanner(
                scenes = state.scenes,
                activeSceneId = state.activeSceneId,
                members = state.members
            )
            MemberRail(
                members = state.members,
                selectedSpeakerId = state.conversationState.selectedSpeakerId,
                enabled = !generating,
                onSelect = {
                    emitIntent(GroupChatUiIntent.SelectSpeaker(it))
                },
                onToggleMuted = {
                    emitIntent(GroupChatUiIntent.ToggleMemberMuted(it))
                },
                onPoke = { name, actionId ->
                    emitIntent(GroupChatUiIntent.PokeMember(name, actionId))
                }
            )
            MessageList(
                imageState = state.imageState,
                messages = state.conversationState.messages,
                canLoadOlderMessages = state.conversationState.canLoadOlderMessages,
                isLoadingOlderMessages = state.conversationState.isLoadingOlderMessages,
                expandedThinkBlockIds = state.conversationState.expandedThinkBlockIds,
                editingMessageId = state.conversationState.editingMessageId,
                editingMessageDraft = state.conversationState.editingMessageDraft,
                emitIntent = emitIntent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Composer(
    draft: String,
    generating: Boolean,
    imageState: MessageImageState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onImageAction: (MessageImageAction) -> Unit,
    onOpenFilePicker: () -> Unit,
    onOpenGalleryPicker: () -> Unit,
    onSwitchScene: (String) -> Unit,
    sceneMovements: List<me.kafuuneko.rpclient.libs.groupchat.GroupChatScenes.SceneMovement>,
    onPerformSelfAction: (String) -> Unit,
    selfActions: List<me.kafuuneko.rpclient.libs.poke.PokeAction>,
    documents: List<me.kafuuneko.rpclient.libs.media.AttachedDocument>,
    onRemoveDocument: (Int) -> Unit,
    canContinue: Boolean,
    onContinue: () -> Unit,
    onSummarize: () -> Unit,
    onImpersonate: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val sendButtonColor by animateColorAsState(
        targetValue = if (generating) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        label = "sendButtonColor"
    )

    Surface(
        tonalElevation = 5.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                )
        ) {
            // 待发送图片抽屉托盘
            DraftAttachmentTray(
                state = imageState,
                enabled = !generating && !imageState.submitting,
                emit = onImageAction
            )

            // 文档附件：只列文件名与字数，正文在发送时才拼进消息
            me.kafuuneko.rpclient.ui.widgets.DocumentAttachmentRow(
                documents = documents,
                enabled = !generating,
                onRemove = onRemoveDocument
            )

            // 快捷操作胶囊条
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionPill(
                    icon = Icons.Rounded.AutoAwesome,
                    label = stringResource(R.string.continue_latest_reply),
                    enabled = canContinue && !generating,
                    onClick = onContinue
                )
                // 扮演自己：让模型写一句玩家发言，填进输入框
                QuickActionPill(
                    icon = Icons.Rounded.Edit,
                    label = stringResource(R.string.group_chat_impersonate),
                    enabled = !generating && !imageState.submitting,
                    onClick = onImpersonate
                )
                QuickActionPill(
                    icon = Icons.Rounded.AutoAwesome,
                    label = stringResource(R.string.summarize_now),
                    enabled = !generating && !imageState.submitting,
                    onClick = onSummarize
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                RpScrollableOutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp, max = 140.dp),
                    enabled = !generating && !imageState.submitting,
                    placeholder = { Text(stringResource(R.string.group_chat_message_hint)) },
                    shape = RoundedCornerShape(18.dp),
                    maxLines = 5,
                    leadingIcon = {
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    menuExpanded = true
                                },
                                enabled = !generating && !imageState.processing && imageState.canAddDraft
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = stringResource(R.string.attach_images),
                                    tint = if (!generating && imageState.canAddDraft) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                    }
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.attach_images)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.ImageIcon,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onImageAction(MessageImageAction.Choose(editing = false))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.attach_file)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.FileUpload,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onOpenFilePicker()
                                    }
                                )
                                // 从图库挑：图已经导进来、也分好类了，聊天里得能直接用。
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.gallery_pick_from_library)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.PhotoLibrary,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onOpenGalleryPicker()
                                    }
                                )
                                // 场景移动：进哪一场、或者从当前这场退回全员。
                                // 靠点击而不是关键字，是因为一句话里出现地名太容易误判。
                                sceneMovements.forEach { movement ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                if (movement.isLeave) {
                                                    stringResource(
                                                        R.string.group_chat_scene_leave_action,
                                                        movement.name
                                                    )
                                                } else {
                                                    stringResource(
                                                        R.string.group_chat_scene_enter_action,
                                                        movement.name
                                                    )
                                                }
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (movement.isLeave) {
                                                    Icons.Rounded.Logout
                                                } else {
                                                    Icons.Rounded.Place
                                                },
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            onSwitchScene(movement.sceneId)
                                        }
                                    )
                                }
                                // 自主动作放进来（环顾四周、叹气这类）；
                                // 带 {name} 的需要指定对象，群里没有天然对象，
                                // 那类走长按成员头像，目标由手指决定不会错。
                                selfActions.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action.label) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.TouchApp,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            onPerformSelfAction(action.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = sendButtonColor,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (generating) onStop()
                        else onSend()
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (generating) {
                                Icons.Rounded.Stop
                            } else {
                                Icons.AutoMirrored.Rounded.Send
                            },
                            contentDescription = stringResource(
                                if (generating) R.string.stop else R.string.send
                            ),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun GroupChatPreview() {
    ActivityPreview(darkTheme = false) {
        GroupChatLayout(
            uiState = GroupChatUiState.Normal(
                sessionId = 1,
                title = "Starlight Crew",
                members = previewMembers,
                activeActivationStrategy = GroupChatActivationStrategy.Natural,
                conversationState = GroupChatConversationState(
                    messages = previewMessages,
                    hasCharacterMessage = true,
                    selectedSpeakerId = 1
                ),
                settingsState = GroupChatSettingsState(
                    activationStrategy = GroupChatActivationStrategy.Natural
                )
            )
        )
    }

}

private val previewMembers = listOf(
    GroupChatMemberItem(1, "Lyra", "", false),
    GroupChatMemberItem(2, "Mina", "", false),
    GroupChatMemberItem(3, "Rowan", "", true)
)

private val previewMessages = listOf(
    GroupChatMessageItem(
        id = 1,
        source = GroupChatMessageSource.User,
        speakerName = "You",
        content = "The signal is coming from the abandoned station.",
        time = "21:04"
    ),
    GroupChatMessageItem(
        id = 2,
        source = GroupChatMessageSource.Character,
        speakerName = "Lyra",
        content = "Then we should approach quietly. Its navigation lights are still active.",
        time = "21:04"
    ),
    GroupChatMessageItem(
        id = 3,
        source = GroupChatMessageSource.Character,
        speakerName = "Mina",
        content = "I will check the archive for its last registered crew.",
        time = "21:05"
    )
)

/**
 * 顶栏群头像。
 *
 * 群聊没有「唯一一张脸」，这里用在场成员的真实头像并排表示——
 * 比一个通用群组图标更有信息量，也顺带回答了「这个群到底有谁」。
 */

@Composable
private fun GroupTopAvatars(members: List<GroupChatMemberItem>) {
    // 没上传头像的角色也要出现——成员本身就是信息，
    // 只显示「有图的那几个」会让整个顶栏在纯文字卡群里直接消失
    val shown = members.take(2)
    if (shown.isEmpty()) return
    // 叠放而不是并排：顶栏宽度要留给标题，两个 22dp 头像叠起来只占 32dp
    val avatarSize = 22.dp
    val overlap = 12.dp
    Box(
        modifier = Modifier
            .width(avatarSize + overlap * (shown.size - 1))
            .height(avatarSize)
    ) {
        shown.forEachIndexed { index, member ->
            val accent = getMacaronColor(member.name)
            val bitmap = member.avatarImage
            Surface(
                modifier = Modifier
                    .offset(x = overlap * index)
                    .size(avatarSize),
                shape = CircleShape,
                color = accent.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.background)
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = member.name,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = member.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
