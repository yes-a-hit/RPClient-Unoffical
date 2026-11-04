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

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
internal fun ChatLayoutPreview() {
    AppTheme(dynamicColor = false) {
        ChatLayout(
            uiState = ChatUiState.Normal(
                session = ChatSessionItem(
                    id = 1,
                    title = "The Seventh File on a Rainy Night",
                    summarize = "",
                    userNote = "",
                    userName = "You",
                    userDescription = "",
                    creatorNotes = "",
                    autoSummaryPaused = false,
                    messageCount = 1,
                    enabledLorebookEntryIds = setOf(1)
                ),
                character = ChatCharacterItem(
                    id = 1,
                    name = "Lyra",
                    description = "",
                    personality = "",
                    scenario = "",
                    examplesOfDialogue = "",
                    postHistoryInstructions = "",
                    creatorNotes = "",
                    avatarText = "L",
                    accentColor = DefaultCharacterAccentColor
                ),
                conversationState = ChatConversationState(
                    messages = listOf(
                        ChatMessageUiModel(
                            id = "1",
                            role = MessageRole.Assistant,
                            speaker = "Lyra",
                            content = "## Archive note\nThe rain kept **tapping** on the archive windows.\n\n- Index the file\n- Check `sealed` shelf",
                            parts = listOf(MessageContentPart.Text("## Archive note\nThe rain kept **tapping** on the archive windows.\n\n- Index the file\n- Check `sealed` shelf")),
                            time = "02:15",
                            tokenCount = 12
                        )
                    ),
                    hasAssistantMessage = true
                ),
                lorebookState = ChatLorebookState(
                    groups = listOf(
                        ChatLorebookGroupItem(
                            lorebookId = 1,
                            lorebookName = "Fog Harbor",
                            enabledCount = 1,
                            totalCount = 1,
                            entries = listOf(
                                ChatLorebookEntryItem(
                                    1,
                                    1,
                                    "Fog Harbor",
                                    "Old District",
                                    listOf("rain"),
                                    emptyList(),
                                    false,
                                    0,
                                    0,
                                    "",
                                    true
                                )
                            )
                        )
                    )
                ),
                streamEnabled = true,
                dialogState = ChatDialogState.SessionLorebook(
                    query = "",
                    visibleGroups = emptyList(),
                    enabledEntryIds = setOf(1)
                )
            ),
            emit = {}
        )
    }
}

@Composable
internal fun AvatarPreview(
    avatarText: String,
    avatarColor: Color,
    image: ImageBitmap?,
    size: Int,
    modifier: Modifier = Modifier,
    /** 长按头像触发；为 null 时不响应，用于用户自己那条。 */
    onLongClick: (() -> Unit)? = null
) {
    val cornerRadius = remember(size) { (size * 0.28).coerceAtLeast(8.0).dp }
    Surface(
        modifier = modifier
            .size(size.dp)
            .then(
                if (onLongClick == null) {
                    Modifier
                } else {
                    // 长按戳一下；短按不做事，头像本来也不是按钮
                    Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = onLongClick
                    )
                }
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = avatarColor.copy(alpha = 0.14f),
        border = BorderStroke(0.5.dp, avatarColor.copy(alpha = 0.30f))
    ) {
        if (image == null) {
            RpAvatar(
                text = avatarText,
                color = avatarColor,
                modifier = Modifier.size(size.dp),
                shape = RoundedCornerShape(cornerRadius)
            )
        } else {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier
                    .size(size.dp)
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/** 同屏对照普通态与编辑态，便于检查图片位置和短文本编辑高度。 */

@Preview(name = "图片消息 · 普通与编辑", widthDp = 360, heightDp = 700)
@Composable
internal fun PreviewImageMessageEditing() {
    PreviewImageMessages(listOf(3 to false, 3 to true))
}

/** 覆盖单图、双图、四图与折叠多图，检查窄屏下的裁切和布局。 */

@Preview(name = "图片消息 · 数量适配", widthDp = 320, heightDp = 1100)
@Composable
internal fun PreviewImageMessageCounts() {
    PreviewImageMessages(listOf(1 to false, 2 to false, 4 to true, 6 to false))
}

@Composable
internal fun PreviewImageMessages(examples: List<Pair<Int, Boolean>>) {
    // 合成横向缩略图只供预览，覆盖裁切边界而不依赖私有图片文件或异步加载。
    val thumbnails = remember {
        (0..5).associate { index ->
            val bitmap = ImageBitmap(240, 160)
            val canvas = Canvas(bitmap)
            canvas.drawRect(0f, 0f, 240f, 160f, Paint().apply { color = Color(0xFF203D50) })
            canvas.drawCircle(Offset(120f, 60f), 32f, Paint().apply { color = Color(0xFFDBAD72) })
            canvas.drawRect(0f, 105f, 240f, 160f, Paint().apply { color = Color(0xFF47706C) })
            index.toString() to bitmap
        }
    }
    val character = ChatCharacterItem(1, "Preview", "", "", "", "", "", "", "P", Color.Blue)
    AppTheme(darkTheme = true, dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            examples.forEachIndexed { index, (count, editing) ->
                val ids = thumbnails.keys.take(count)
                MessageBubble(
                    imageState = MessageImageState(
                        editing = ids,
                        thumbnails = thumbnails,
                        canAddEditing = count < 4
                    ),
                    message = ChatMessageUiModel(
                        id = index.toString(),
                        role = MessageRole.User,
                        speaker = "You",
                        content = "还有很多呢",
                        parts = listOf(MessageContentPart.Text("还有很多呢")),
                        time = "19:28",
                        tokenCount = 0,
                        imageUuids = ids
                    ),
                    character = character,
                    expandedThinkBlockIds = emptySet(),
                    editing = editing,
                    editingDraft = "还有很多呢",
                    isFirstMessage = false,
                    emit = {}
                )
            }
        }
    }
}
