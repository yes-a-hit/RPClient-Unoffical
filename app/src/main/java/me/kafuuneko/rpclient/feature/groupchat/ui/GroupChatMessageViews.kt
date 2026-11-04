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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MessageList(
    imageState: MessageImageState,
    messages: List<GroupChatMessageItem>,
    canLoadOlderMessages: Boolean,
    isLoadingOlderMessages: Boolean,
    expandedThinkBlockIds: Set<String>,
    editingMessageId: Long?,
    editingMessageDraft: String,
    emitIntent: (GroupChatUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isListDragged by listState.interactionSource.collectIsDraggedAsState()
    val latestCanLoadOlderMessages by rememberUpdatedState(canLoadOlderMessages)
    val latestIsLoadingOlderMessages by rememberUpdatedState(isLoadingOlderMessages)
    var shouldFollowBottom by remember { mutableStateOf(true) }
    var isFirstLoad by remember { mutableStateOf(true) }
    var isScrollIndicatorDragged by remember { mutableStateOf(false) }
    var lastTailMessageId by remember { mutableStateOf<Long?>(null) }

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
                    latestCanLoadOlderMessages &&
                    !latestIsLoadingOlderMessages
                ) {
                    emitIntent(GroupChatUiIntent.LoadOlderMessages)
                }
            }
    }

    // 只有尾部消息身份改变时才滚到底部，头部加载历史不会打断用户位置
    val tailMessageId = messages.lastOrNull()?.id
    LaunchedEffect(tailMessageId) {
        if (tailMessageId == null) {
            lastTailMessageId = null
            isFirstLoad = true
        } else if (isFirstLoad || tailMessageId != lastTailMessageId) {
            shouldFollowBottom = true
            listState.scrollToItem(
                messages.size + if (canLoadOlderMessages || isLoadingOlderMessages) 1 else 0
            )
            isFirstLoad = false
        }
        lastTailMessageId = tailMessageId
    }

    // - 内容流式生成或思考块折叠变动时，若处于跟随状态则自动跟随到底部
    LaunchedEffect(
        messages.lastOrNull()?.content,
        expandedThinkBlockIds
    ) {
        if (messages.isNotEmpty()) {
            if (isFirstLoad || shouldFollowBottom) {
                listState.scrollToItem(
                    messages.size + if (canLoadOlderMessages || isLoadingOlderMessages) 1 else 0
                )
                isFirstLoad = false
            }
        }
    }

    // 记录列表视口高度，在软键盘弹出/收起导致视口尺寸变化时，将差值转化为滚动偏移，
    // 从而使当前查看的消息被键盘等高顶起，保持位置不变，顶部溢出部分在标题栏下方自然裁切
    val coroutineScope = rememberCoroutineScope()
    var previousListHeight by remember { mutableIntStateOf(0) }
    if (messages.isEmpty()) {
        EmptyConversation(modifier)
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                val newHeight = size.height
                if (previousListHeight > 0 && newHeight > 0 && newHeight != previousListHeight) {
                    val delta = (previousListHeight - newHeight).toFloat()
                    coroutineScope.launch {
                        if (shouldFollowBottom && delta > 0) {
                            listState.scrollToItem(
                                messages.size + if (canLoadOlderMessages || isLoadingOlderMessages) 1 else 0
                            )
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
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (canLoadOlderMessages || isLoadingOlderMessages) {
            item(key = "older-messages-loader") {
                OlderMessagesLoadIndicator(loading = isLoadingOlderMessages)
            }
        }
        items(
            count = messages.size,
            key = { messages[it].id }
        ) { index ->
            val message = messages[index]
            MessageBubble(
                imageState = imageState,
                message = message,
                editing = editingMessageId == message.id,
                editingDraft = editingMessageDraft
                    .takeIf { editingMessageId == message.id }
                    .orEmpty(),
                expandedThinkBlockIds = expandedThinkBlockIds,
                onToggleThinkBlock = { blockId ->
                    emitIntent(GroupChatUiIntent.ToggleThinkBlock(blockId))
                },
                emitIntent = emitIntent
            )
        }
        item(key = "conversation-end") {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/** 在消息窗口顶部保留稳定高度，并在读取历史时展示轻量进度。 */

@Composable
internal fun OlderMessagesLoadIndicator(loading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        }
    }
}

@Composable
internal fun EmptyConversation(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = stringResource(R.string.group_chat_room_ready),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.group_chat_room_ready_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun MessageBubble(
    imageState: MessageImageState,
    message: GroupChatMessageItem,
    editing: Boolean,
    editingDraft: String,
    expandedThinkBlockIds: Set<String>,
    onToggleThinkBlock: (String) -> Unit,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    val isUser = message.source == GroupChatMessageSource.User
    val isSystem = message.source == GroupChatMessageSource.System
    // 供应商对助手图片的支持不一致，暂只向用户消息开放新增入口，保留所有消息既有附件的编辑能力。
    val canAddImages = editing && isUser
    val accent = getMacaronColor(message.speakerName)
    var showActions by remember(message.id) { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = when {
            isUser -> Arrangement.End
            isSystem -> Arrangement.Center
            else -> Arrangement.Start
        },
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser && !isSystem) {
            RpAvatar(
                text = message.speakerName.firstOrNull()?.uppercase() ?: "?",
                color = accent,
                modifier = Modifier.size(38.dp),
                image = message.avatarImage
            )
            Spacer(modifier = Modifier.width(9.dp))
        }
        Column(
            modifier = Modifier.fillMaxWidth(if (isSystem) 0.92f else if (isUser) 0.82f else 0.88f),
            horizontalAlignment = when {
                isUser -> Alignment.End
                isSystem -> Alignment.CenterHorizontally
                else -> Alignment.Start
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.speakerName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary
                    } else if (isSystem) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        accent
                    }
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                )
                if (canAddImages) {
                    Spacer(Modifier.width(6.dp))
                    MessageImageEditButton(imageState) { emitIntent(GroupChatUiIntent.ImageAction(it)) }
                }
            }
            Surface(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .clickable(enabled = !editing) {
                        showActions = !showActions
                    },
                shape = when {
                    isUser -> RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 4.dp,
                        bottomEnd = 18.dp,
                        bottomStart = 18.dp
                    )

                    isSystem -> RoundedCornerShape(14.dp)
                    else -> RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomEnd = 18.dp,
                        bottomStart = 18.dp
                    )
                },
                color = when {
                    isUser -> MaterialTheme.colorScheme.primary
                    isSystem -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                },
                border = when {
                    isUser -> BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
                    isSystem -> BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
                    )

                    else -> BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
                    )
                },
                shadowElevation = if (isUser) 1.5.dp else 0.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val imageIds = if (editing) imageState.editing else message.imageUuids
                    MessageImageGallery(imageIds, imageState, editing = editing) {
                        emitIntent(GroupChatUiIntent.ImageAction(it))
                    }
                    if (editing) imageState.errorResId?.let { errorResId ->
                        Text(
                            text = stringResource(errorResId),
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (editing) {
                        GroupMessageEditContent(
                            draft = editingDraft,
                            imageState = imageState,
                            isUser = isUser,
                            emitIntent = emitIntent
                        )
                    } else {
                        GroupMessageContent(
                            message = message,
                            isUser = isUser,
                            accent = accent,
                            expandedThinkBlockIds = expandedThinkBlockIds,
                            onToggleThinkBlock = onToggleThinkBlock
                        )
                    }
                    AnimatedVisibility(
                        visible = showActions && !editing && !message.isStreaming,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f),
                            border = BorderStroke(
                                0.5.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                                GroupMessageActions(
                                    message = message,
                                    isUser = isUser,
                                    emitIntent = emitIntent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 在群聊消息气泡内展示编辑输入框。 */

@Composable
internal fun GroupMessageContent(
    message: GroupChatMessageItem,
    isUser: Boolean,
    accent: Color,
    expandedThinkBlockIds: Set<String>,
    onToggleThinkBlock: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (message.isStreaming && (message.content.isBlank() || message.parts.isEmpty())) {
            GroupStreamingStatus(
                text = stringResource(R.string.waiting_for_response),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                indicatorColor = accent
            )
        }
        message.parts.forEach { part ->
            when (part) {
                is MessageContentPart.Text -> {
                    if (part.content.isNotBlank()) {
                        // 附带文件正文收成可展开卡片，和单聊一致
                        MessageDocumentBlock.split(part.content).forEach { segment ->
                            when (segment) {
                                is MessageDocumentBlock.Segment.Text -> MarkdownMessageText(
                                    content = segment.content,
                                    isUser = isUser
                                )

                                is MessageDocumentBlock.Segment.Document -> MessageDocumentCard(
                                    name = segment.name,
                                    body = segment.inlineBody,
                                    isUser = isUser,
                                    documentId = segment.id
                                )
                            }
                        }
                    }
                }

                is MessageContentPart.Think -> GroupThinkBlock(
                    part = part,
                    expanded = part.id in expandedThinkBlockIds,
                    isThinking = message.isStreaming && !part.isComplete,
                    indicatorColor = accent,
                    onToggle = { onToggleThinkBlock(part.id) }
                )
            }
        }
    }
}

@Composable
internal fun GroupStreamingStatus(
    text: String,
    color: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = indicatorColor
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}
