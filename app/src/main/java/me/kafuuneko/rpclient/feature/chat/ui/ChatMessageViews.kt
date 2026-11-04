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
internal fun MessageBubble(
    imageState: MessageImageState,
    message: ChatMessageUiModel,
    character: ChatCharacterItem,
    expandedThinkBlockIds: Set<String>,
    editing: Boolean,
    editingDraft: String,
    editingThinkDraft: String = "",
    editingThinkExpanded: Boolean = false,
    isFirstMessage: Boolean,
    emit: ChatUiIntent.() -> Unit
) {
    val isUser = message.role == MessageRole.User
    // 供应商对助手图片的支持不一致，暂只向用户消息开放新增入口，保留所有消息既有附件的编辑能力。
    val canAddImages = editing && isUser
    val imageIds = if (editing) imageState.editing else message.imageUuids
    val hasImageHeader =
        imageIds.isNotEmpty() || message.imageUuids.isNotEmpty() || canAddImages
    var showActions by remember(message.id) { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            AvatarPreview(
                avatarText = if (message.speaker == character.name) {
                    character.avatarText
                } else {
                    message.speaker.take(1).uppercase()
                },
                avatarColor = if (message.speaker == character.name) character.accentColor else NarratorAvatarColor,
                image = if (message.speaker == character.name) character.avatarImage else null,
                size = 34,
                modifier = Modifier.padding(end = 8.dp, top = 2.dp),
                // 长按角色头像戳一下；旁白的头像没有可戳的对象
                onLongClick = if (message.speaker == character.name) {
                    { ChatUiIntent.PokeCharacter.emit() }
                } else {
                    null
                }
            )
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = if (isUser) 310.dp else 295.dp)
                    .clickable(enabled = !editing) { showActions = !showActions },
                shape = when (message.role) {
                    MessageRole.User -> RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 4.dp
                    )

                    MessageRole.Assistant -> RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 18.dp
                    )

                    MessageRole.Narrator -> RoundedCornerShape(14.dp)
                },
                color = when (message.role) {
                    MessageRole.User -> MaterialTheme.colorScheme.primary
                    MessageRole.Assistant -> MaterialTheme.colorScheme.surface
                    MessageRole.Narrator -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
                },
                border = when (message.role) {
                    MessageRole.User -> BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
                    MessageRole.Assistant -> BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
                    )

                    MessageRole.Narrator -> BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
                    )
                },
                shadowElevation = if (isUser) 1.5.dp else 0.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        // 图片消息为标题预留同样高度，添加按钮出现时不推动下方网格。
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (hasImageHeader) 32.dp else 0.dp)
                    ) {
                        Text(
                            text = message.speaker,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                        if (canAddImages) {
                            Spacer(Modifier.width(4.dp))
                            MessageImageEditButton(imageState) {
                                ChatUiIntent.ImageAction(it).emit()
                            }
                        }
                    }
                    MessageImageGallery(imageIds, imageState, editing = editing) {
                        ChatUiIntent.ImageAction(it).emit()
                    }
                    if (editing) imageState.errorResId?.let { errorResId ->
                        Text(
                            text = stringResource(errorResId),
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (editing) {
                        MessageEditContent(
                            draft = editingDraft,
                            thinkDraft = editingThinkDraft,
                            thinkExpanded = editingThinkExpanded,
                            imageState = imageState,
                            isUser = isUser,
                            emit = emit
                        )
                    } else {
                        MessageContent(
                            message = message,
                            expandedThinkBlockIds = expandedThinkBlockIds,
                            isUser = isUser,
                            editingDraft = editingDraft,
                            editingThinkDraft = editingThinkDraft,
                            editingThinkExpanded = editingThinkExpanded,
                            emit = emit
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showActions && !editing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f),
                    border = BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    ),
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MessageActions(message, isFirstMessage, emit)
                    }
                }
            }
        }
    }
}

@Composable
internal fun MessageContent(
    message: ChatMessageUiModel,
    expandedThinkBlockIds: Set<String>,
    isUser: Boolean,
    editingDraft: String = "",
    editingThinkDraft: String = "",
    editingThinkExpanded: Boolean = false,
    emit: ChatUiIntent.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (message.isStreaming && (message.content.isBlank() || message.parts.isEmpty())) {
            StreamingStatus(
                text = stringResource(R.string.waiting_for_response),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f)
            )
        }
        message.parts.forEach { part ->
            when (part) {
                is MessageContentPart.Text -> {
                    if (part.content.isNotBlank()) {
                        // 附带文件的正文留在消息里（模型读得到、历史存得下），
                        // 但界面上收成一张可展开的卡片，不铺满气泡
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

                is MessageContentPart.Think -> ThinkBlock(
                    part = part,
                    expanded = part.id in expandedThinkBlockIds,
                    isThinking = message.isStreaming && !part.isComplete,
                    emit = emit
                )
            }
        }
    }
}

@Composable
internal fun StreamingStatus(
    text: String,
    color: Color,
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
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
internal fun MessageEditContent(
    draft: String,
    thinkDraft: String,
    thinkExpanded: Boolean,
    imageState: MessageImageState,
    isUser: Boolean,
    emit: ChatUiIntent.() -> Unit
) {
    // 编辑框沿用正文排版，短消息仅占一行，长消息在高度上限内滚动。
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // 思维链单独一块：能改、能清空，折叠起来不占地方
        if (thinkExpanded || thinkDraft.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { ChatUiIntent.ToggleEditingThink.emit() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.edit_think_block),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (thinkExpanded) "\u2212" else "+",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (thinkExpanded) {
                RpScrollableOutlinedTextField(
                    enabled = !imageState.submitting,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    value = thinkDraft,
                    onValueChange = { ChatUiIntent.ChangeEditingThinkDraft(it).emit() },
                    minLines = 2,
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(stringResource(R.string.edit_think_hint)) }
                )
            }
        }
        RpScrollableOutlinedTextField(
            enabled = !imageState.submitting,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            value = draft,
            onValueChange = { ChatUiIntent.ChangeEditingMessageDraft(it).emit() },
            minLines = 1,
            maxLines = 8,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                cursorColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                focusedContainerColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.06f) else Color.Transparent,
                unfocusedContainerColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.04f) else Color.Transparent,
                focusedBorderColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f) else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.42f) else MaterialTheme.colorScheme.outline
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { ChatUiIntent.CancelEditingMessage.emit() },
                enabled = !imageState.submitting,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    stringResource(R.string.cancel),
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f) else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { ChatUiIntent.SaveEditingMessage.emit() },
                enabled = !imageState.processing,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    stringResource(if (imageState.submitting) R.string.image_loading else R.string.save),
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                )
            }
            // 改自己的发言时才给这个选项：改别人（或自己）的回复不需要重答
            if (isUser) {
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { ChatUiIntent.SaveEditingMessageAndRegenerate.emit() },
                    enabled = !imageState.processing,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        stringResource(R.string.save_and_regenerate),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
internal fun ThinkBlock(
    part: MessageContentPart.Think,
    expanded: Boolean,
    isThinking: Boolean,
    emit: ChatUiIntent.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { ChatUiIntent.ToggleThinkBlock(part.id).emit() },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isThinking) 0.55f else 0.38f),
        border = BorderStroke(
            0.8.dp,
            if (isThinking) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = if (isThinking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                if (isThinking) {
                    StreamingStatus(
                        text = stringResource(R.string.thinking),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        stringResource(R.string.thought_process),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) stringResource(R.string.hide) else stringResource(
                        R.string.show
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    part.content,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun QuickActionPill(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        },
        border = BorderStroke(
            0.5.dp,
            if (enabled) {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f)
            }
        ),
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.38f
                ),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.38f
                )
            )
        }
    }
}

@Composable
internal fun MessageActions(
    message: ChatMessageUiModel,
    isFirstMessage: Boolean,
    emit: ChatUiIntent.() -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val iconColor = MaterialTheme.colorScheme.onSurfaceVariant

        IconButton(
            onClick = { ChatUiIntent.CopyMessage(message.id).emit() },
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                Icons.Rounded.ContentCopy,
                contentDescription = stringResource(R.string.copy),
                modifier = Modifier.size(17.dp),
                tint = iconColor
            )
        }
        IconButton(
            onClick = { ChatUiIntent.StartEditMessage(message.id).emit() },
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                Icons.Rounded.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier.size(17.dp),
                tint = iconColor
            )
        }
        if (!isFirstMessage) {
            IconButton(
                onClick = { ChatUiIntent.BranchFromMessage(message.id).emit() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.branch_from_message),
                    modifier = Modifier.size(17.dp),
                    tint = iconColor
                )
            }
            if (message.role == MessageRole.Assistant) {
                IconButton(
                    onClick = { ChatUiIntent.RegenerateFromMessage(message.id).emit() },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.regenerate),
                        modifier = Modifier.size(17.dp),
                        tint = iconColor
                    )
                }
            }
            IconButton(
                onClick = { ChatUiIntent.DeleteMessageClick(message.id).emit() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(17.dp),
                    tint = iconColor
                )
            }
        }
    }
}
