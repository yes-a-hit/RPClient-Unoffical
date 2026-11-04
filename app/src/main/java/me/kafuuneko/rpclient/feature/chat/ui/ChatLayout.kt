package me.kafuuneko.rpclient.feature.chat.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Image as ImageIcon
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.chat.model.ChatCharacterItem
import me.kafuuneko.rpclient.feature.chat.model.ChatGenerationState
import me.kafuuneko.rpclient.feature.chat.model.ChatLorebookEntryItem
import me.kafuuneko.rpclient.feature.chat.model.ChatLorebookGroupItem
import me.kafuuneko.rpclient.feature.chat.model.ChatMessageUiModel
import me.kafuuneko.rpclient.feature.chat.model.ChatSessionItem
import me.kafuuneko.rpclient.feature.chat.model.MessageRole
import me.kafuuneko.rpclient.feature.chat.presentation.ChatConversationState
import me.kafuuneko.rpclient.feature.chat.presentation.ChatDialogState
import me.kafuuneko.rpclient.feature.chat.presentation.ChatLoadState
import me.kafuuneko.rpclient.feature.chat.presentation.ChatLorebookState
import me.kafuuneko.rpclient.feature.chat.presentation.ChatPage
import me.kafuuneko.rpclient.feature.chat.presentation.ChatUiIntent
import me.kafuuneko.rpclient.feature.chat.presentation.ChatUiState
import me.kafuuneko.rpclient.libs.AppModel
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.media.MessageImageState
import me.kafuuneko.rpclient.libs.poke.PokeAction
import me.kafuuneko.rpclient.libs.poke.PokeActions
import me.kafuuneko.rpclient.model.MessageContentPart
import me.kafuuneko.rpclient.ui.dialog.AppConfirmDialog
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.LoadingDialog
import me.kafuuneko.rpclient.ui.dialog.MessageImageViewerDialog
import me.kafuuneko.rpclient.ui.dialog.PromptInspectorDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogEntry
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogGroup
import me.kafuuneko.rpclient.ui.message.DraftAttachmentTray
import me.kafuuneko.rpclient.ui.message.MessageImageEditButton
import me.kafuuneko.rpclient.ui.message.MessageImageGallery
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.DefaultCharacterAccentColor
import me.kafuuneko.rpclient.ui.theme.NarratorAvatarColor
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.GalleryPickerDialog
import me.kafuuneko.rpclient.ui.widgets.PokeActionEditorDialog
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentCard
import me.kafuuneko.rpclient.ui.widgets.MarkdownMessageText
import me.kafuuneko.rpclient.ui.widgets.NoProviderBanner
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.chatBackgroundLayer
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpMetaPill
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpTagRow
import me.kafuuneko.rpclient.ui.widgets.draggableLazyListScrollIndicator
import me.kafuuneko.rpclient.utils.toggle

internal const val HISTORY_LOAD_THRESHOLD = 4

/** 单角色聊天页 Compose 入口，根据页面状态切换会话区与设置区。 */

@Composable
fun ChatLayout(
    uiState: ChatUiState,
    emit: ChatUiIntent.() -> Unit
) {
    BackHandler(enabled = uiState is ChatUiState.Normal) { ChatUiIntent.Back.emit() }
    when (uiState) {
        ChatUiState.None -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        is ChatUiState.Finished -> ChatLayout(uiState.previous) {}

        is ChatUiState.Normal -> {
            when (uiState.page) {
                ChatPage.Conversation -> ChatNormal(uiState, emit)
                ChatPage.Settings -> ChatSettingsPage(
                    session = uiState.session,
                    lorebookState = uiState.lorebookState,
                    loadState = uiState.loadState,
                    pokeActionDrafts = uiState.pokeActionDrafts,
                    sessionPokeActions = uiState.sessionPokeActions,
                    usesOwnPokeActions = uiState.usesOwnPokeActions,
                    hasOwnBackground = uiState.hasOwnBackground,
                    emit = emit
                )
            }
            DialogSwitch(uiState, emit)
            if (uiState.galleryPickerVisible) {
                GalleryPickerDialog(
                    onDismiss = { ChatUiIntent.CloseGalleryPicker.emit() },
                    onConfirm = { picked ->
                        ChatUiIntent.AttachGalleryImages(picked.map { it.fileUuid }).emit()
                    }
                )
            }
            if (uiState.pokeActionEditorVisible) {
                PokeActionEditorDialog(
                    drafts = uiState.pokeActionDrafts,
                    onDismiss = { ChatUiIntent.ClosePokeActionEditor.emit() },
                    onChange = { id, label, template ->
                        ChatUiIntent.ChangePokeAction(id, label, template).emit()
                    },
                    onAdd = { ChatUiIntent.AddPokeAction.emit() },
                    onRemove = { ChatUiIntent.RemovePokeAction(it).emit() },
                    onSave = { ChatUiIntent.SavePokeActions.emit() }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ChatNormal(
    state: ChatUiState.Normal,
    emit: ChatUiIntent.() -> Unit
) {
    val listState = rememberLazyListState()
    val isListDragged by listState.interactionSource.collectIsDraggedAsState()
    val canLoadOlderMessages by rememberUpdatedState(
        state.conversationState.canLoadOlderMessages
    )
    val isLoadingOlderMessages by rememberUpdatedState(
        state.conversationState.isLoadingOlderMessages
    )
    var shouldFollowBottom by remember { mutableStateOf(true) }
    var isFirstLoad by remember { mutableStateOf(true) }
    var isScrollIndicatorDragged by remember { mutableStateOf(false) }
    var lastTailMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.canScrollForward to isScrollIndicatorDragged }
            .collect { (canScrollForward, indicatorDragged) ->
                if (indicatorDragged) {
                    shouldFollowBottom = false
                } else if (!canScrollForward) {
                    shouldFollowBottom = true
                }
            }
    }

    LaunchedEffect(isListDragged) {
        if (isListDragged) {
            snapshotFlow { listState.canScrollForward }
                .collect { canScrollForward ->
                    shouldFollowBottom = !canScrollForward
                }
        }
    }

    // 首次定位到底部完成后，用户接近当前窗口顶部才请求更早历史
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to isFirstLoad }
            .distinctUntilChanged()
            .collect { (firstVisibleItemIndex, firstLoad) ->
                if (!firstLoad &&
                    firstVisibleItemIndex <= HISTORY_LOAD_THRESHOLD &&
                    canLoadOlderMessages &&
                    !isLoadingOlderMessages
                ) {
                    ChatUiIntent.LoadOlderMessages.emit()
                }
            }
    }

    // 只有尾部消息身份改变时才滚到底部，头部加载历史不会打断用户位置
    val tailMessageId = state.conversationState.messages.lastOrNull()?.id
    LaunchedEffect(tailMessageId) {
        if (tailMessageId == null) {
            lastTailMessageId = null
            isFirstLoad = true
        } else if (isFirstLoad || tailMessageId != lastTailMessageId) {
            shouldFollowBottom = true
            listState.scrollToItem(state.conversationState.messages.size + 1)
            isFirstLoad = false
        }
        lastTailMessageId = tailMessageId
    }

    // - 内容流式生成或思考块折叠变动时，若处于跟随状态则自动跟随到底部
    LaunchedEffect(
        state.conversationState.messages.lastOrNull()?.content,
        state.conversationState.expandedThinkBlockIds
    ) {
        if (state.conversationState.messages.isNotEmpty()) {
            if (isFirstLoad || shouldFollowBottom) {
                listState.scrollToItem(state.conversationState.messages.size + 1)
                isFirstLoad = false
            }
        }
    }

    // 记录列表视口高度，在软键盘弹出/收起导致视口尺寸变化时，将差值转化为滚动偏移，
    // 从而使当前查看的消息被键盘等高顶起，保持位置不变，顶部溢出部分在标题栏下方自然裁切
    val coroutineScope = rememberCoroutineScope()
    var previousListHeight by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .chatBackgroundLayer(
                state = state.backgroundState,
                scrimColor = MaterialTheme.colorScheme.background
            )
    ) {
        CustomChatTopBar(
            session = state.session,
            character = state.character,
            lorebookState = state.lorebookState,
            generationState = state.conversationState.generationState,
            loadState = state.loadState,
            streamEnabled = state.streamEnabled,
            hasPromptInspection = state.hasPromptInspection,
            hasAvailableProvider = state.hasAvailableProvider,
            sessionLoreDialogVisible = state.dialogState is ChatDialogState.SessionLorebook,
            onBack = { ChatUiIntent.Back.emit() },
            emit = emit
        )
        if (!state.hasAvailableProvider) {
            NoProviderBanner(
                onClick = { ChatUiIntent.OpenProviderSettings.emit() }
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                )
                .onSizeChanged { size ->
                    val newHeight = size.height
                    if (previousListHeight > 0 && newHeight > 0 && newHeight != previousListHeight) {
                        val delta = (previousListHeight - newHeight).toFloat()
                        coroutineScope.launch {
                            if (shouldFollowBottom && delta > 0) {
                                listState.scrollToItem(state.conversationState.messages.size + 1)
                            } else {
                                listState.dispatchRawDelta(delta)
                            }
                        }
                    }
                    previousListHeight = newHeight
                }
                .draggableLazyListScrollIndicator(
                    state = listState,
                    onDragStateChanged = { dragging ->
                        isScrollIndicatorDragged = dragging
                        shouldFollowBottom = if (dragging) {
                            false
                        } else {
                            !listState.canScrollForward
                        }
                    }
                ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.conversationState.canLoadOlderMessages ||
                state.conversationState.isLoadingOlderMessages
            ) {
                item(key = "older-messages-loader") {
                    OlderMessagesLoadIndicator(
                        loading = state.conversationState.isLoadingOlderMessages
                    )
                }
            } else {
                item(key = "conversation-start") {
                    ConversationStartHeader(
                        session = state.session,
                        character = state.character,
                        lorebookState = state.lorebookState,
                        streamEnabled = state.streamEnabled,
                        emit = emit
                    )
                }
            }
            itemsIndexed(
                items = state.conversationState.messages,
                key = { _, message -> message.id },
                contentType = { _, message -> message.role }
            ) { index, message ->
                MessageBubble(
                    imageState = state.imageState,
                    message = message,
                    character = state.character,
                    expandedThinkBlockIds = state.conversationState.expandedThinkBlockIds,
                    editing = message.id == state.conversationState.editingMessageId,
                    editingDraft = state.conversationState.editingMessageDraft,
                    editingThinkDraft = state.conversationState.editingThinkDraft,
                    editingThinkExpanded = state.conversationState.editingThinkExpanded,
                    isFirstMessage = !state.conversationState.canLoadOlderMessages &&
                            !state.conversationState.isLoadingOlderMessages &&
                            index == 0,
                    emit = emit
                )
            }
            item(key = "conversation-end") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        if ((state.conversationState.generationState as? ChatGenerationState.Failed)?.canRetryReply == true) {
            TextButton(
                onClick = { ChatUiIntent.RetryImageReply.emit() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.image_retry))
            }
        }
        MessageImageViewerDialog(state.imageState) { ChatUiIntent.ImageAction(it).emit() }
        ChatInputBar(
            draft = state.conversationState.inputDraft,
            isGenerating = state.conversationState.generationState.isGenerating(),
            hasAssistantMessage = state.conversationState.hasAssistantMessage,
            imageState = state.imageState,
            documents = state.conversationState.attachedDocuments,
            pokeActions = state.sessionPokeActions,
            emit = emit
        )
    }
}

/** 在消息窗口顶部保留稳定高度，并在读取历史时展示轻量进度。 */

@Composable
internal fun CustomChatTopBar(
    session: ChatSessionItem,
    character: ChatCharacterItem,
    lorebookState: ChatLorebookState,
    generationState: ChatGenerationState,
    loadState: ChatLoadState,
    streamEnabled: Boolean,
    hasPromptInspection: Boolean,
    hasAvailableProvider: Boolean = true,
    sessionLoreDialogVisible: Boolean,
    onBack: () -> Unit,
    emit: ChatUiIntent.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AvatarPreview(
                avatarText = character.avatarText,
                avatarColor = character.accentColor,
                image = character.avatarImage,
                size = 36
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = buildString {
                        append(session.title)
                        val status = chatStatusText(
                            loadState,
                            generationState,
                            streamEnabled,
                            hasAvailableProvider
                        )
                        if (status.isNotBlank()) {
                            append(" • ")
                            append(status)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { ChatUiIntent.OpenPromptInspector.emit() },
                enabled = hasPromptInspection,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = stringResource(R.string.prompt_inspector_title),
                    modifier = Modifier.size(20.dp),
                    tint = if (hasPromptInspection) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }
            IconButton(
                onClick = { ChatUiIntent.ShowSessionLoreDialog.emit() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.Book,
                    contentDescription = stringResource(R.string.session_world_book),
                    modifier = Modifier.size(20.dp),
                    tint = if (sessionLoreDialogVisible) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }
            IconButton(
                onClick = { ChatUiIntent.OpenGallery.emit() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.PhotoLibrary,
                    contentDescription = stringResource(R.string.gallery_title),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }
            IconButton(
                onClick = { ChatUiIntent.OpenImageGen.emit() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.ImageIcon,
                    contentDescription = stringResource(R.string.image_gen_title),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }
            IconButton(
                onClick = { ChatUiIntent.OpenChatSettings.emit() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.generation_params),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
internal fun ConversationStartHeader(
    session: ChatSessionItem,
    character: ChatCharacterItem,
    lorebookState: ChatLorebookState,
    streamEnabled: Boolean,
    emit: ChatUiIntent.() -> Unit,
    modifier: Modifier = Modifier
) {
    val enabledLorebookCount = lorebookState.groups.sumOf { it.enabledCount }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AvatarPreview(
                avatarText = character.avatarText,
                avatarColor = character.accentColor,
                image = character.avatarImage,
                size = 68
            )

            Text(
                text = character.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (character.description.isNotBlank()) {
                Text(
                    text = character.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RpMetaPill(stringResource(R.string.messages_count, session.messageCount))
                RpMetaPill(stringResource(R.string.world_books_enabled, enabledLorebookCount))
                RpMetaPill(
                    if (streamEnabled) stringResource(R.string.streaming_on)
                    else stringResource(R.string.streaming_off)
                )
            }
            OutlinedButton(
                onClick = { ChatUiIntent.OpenCharacterEditor.emit() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    0.5.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.edit_character_title))
            }
        }
    }
}

@Composable
internal fun DialogSwitch(
    state: ChatUiState.Normal,
    emit: ChatUiIntent.() -> Unit
) {
    when (val dialogState = state.dialogState) {
        ChatDialogState.None -> Unit

        is ChatDialogState.SessionLorebook -> ChatSessionLorebookDialog(
            groups = state.lorebookState.groups,
            dialogState = dialogState,
            emit = emit
        )

        ChatDialogState.SummaryTokenLimit -> AppConfirmDialog(
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.summary_token_limit_title),
            message = stringResource(
                R.string.summary_token_limit_message,
                stringResource(R.string.summary_memory),
                stringResource(R.string.general_summary_memory),
                stringResource(R.string.summary_response_tokens)
            ),
            confirmText = stringResource(R.string.summary_go_to_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { ChatUiIntent.OpenSummarySettings.emit() }
        )

        is ChatDialogState.ModelSettingsGuide -> AppConfirmDialog(
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            title = dialogState.title,
            message = dialogState.message,
            confirmText = stringResource(R.string.go_to_model_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { ChatUiIntent.OpenProviderSettings.emit() }
        )

        ChatDialogState.Exporting -> LoadingDialog(
            title = stringResource(R.string.exporting_chat),
            description = stringResource(R.string.export_chat_desc)
        )

        ChatDialogState.Summarizing -> LoadingDialog(
            title = stringResource(R.string.updating_summary),
            description = stringResource(R.string.summarize_now_desc),
            onCancel = { ChatUiIntent.CancelSummary.emit() }
        )

        is ChatDialogState.PromptInspector -> PromptInspectorDialog(
            inspection = dialogState.inspection,
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            onCopyRequest = { ChatUiIntent.CopyPromptItem(it).emit() },
            onPreviewImages = { ids, index ->
                ChatUiIntent.ImageAction(MessageImageAction.Preview(ids, index))
                    .emit()
            }
        )

        is ChatDialogState.ImageGen -> ChatImageGenDialog(
            state = dialogState,
            onDismiss = { ChatUiIntent.DismissDialog.emit() },
            emit = emit
        )

        is ChatDialogState.DeleteSessionConfirm -> AppDangerDialog(
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.delete_chat_title),
            message = stringResource(R.string.delete_chat_message, dialogState.sessionTitle),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { ChatUiIntent.ConfirmDeleteSession.emit() }
        )

        // 清空上下文只删消息，会话本身留着，
        // 所以文案要和「删除会话」明确区分开
        ChatDialogState.ConfirmClearContext -> AppDangerDialog(
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.clear_context_title),
            message = stringResource(R.string.clear_context_message),
            confirmText = stringResource(R.string.clear_context_confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { ChatUiIntent.ConfirmClearConversationContext.emit() }
        )

        is ChatDialogState.DeleteMessageConfirm -> AppDangerDialog(
            onDismissRequest = { ChatUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.delete_message_title),
            message = stringResource(R.string.delete_message_confirm),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { ChatUiIntent.ConfirmDeleteMessage(dialogState.messageId).emit() }
        )
    }
}

/** 将单聊世界书状态适配到应用级快捷管理对话框。 */

internal fun ChatGenerationState.isGenerating(): Boolean {
    return this is ChatGenerationState.Requesting || this is ChatGenerationState.Streaming
}

@Composable
internal fun ChatGenerationState.label(streamEnabled: Boolean): String {
    return when (this) {
        ChatGenerationState.Idle -> if (streamEnabled) stringResource(R.string.connected_streaming_enabled) else stringResource(
            R.string.connected
        )

        ChatGenerationState.Requesting -> stringResource(R.string.requesting_model)
        is ChatGenerationState.Streaming -> stringResource(R.string.generating)
        is ChatGenerationState.Failed -> message
    }
}

@Composable
internal fun chatStatusText(
    loadState: ChatLoadState,
    generationState: ChatGenerationState,
    streamEnabled: Boolean,
    hasAvailableProvider: Boolean = true
): String {
    return if (loadState == ChatLoadState.Saving) {
        stringResource(R.string.updating_summary)
    } else if (!hasAvailableProvider && generationState == ChatGenerationState.Idle) {
        stringResource(R.string.no_model_configured)
    } else {
        generationState.label(streamEnabled)
    }
}
