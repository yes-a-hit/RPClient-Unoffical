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
internal fun ChatSettingsPage(
    session: ChatSessionItem,
    lorebookState: ChatLorebookState,
    loadState: ChatLoadState,
    pokeActionDrafts: List<PokeAction>,
    sessionPokeActions: List<PokeAction>,
    usesOwnPokeActions: Boolean,
    hasOwnBackground: Boolean,
    emit: ChatUiIntent.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = stringResource(R.string.chat_settings),
            onBack = { ChatUiIntent.CloseChatSettings.emit() }
        )
        RpLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                ),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SettingsSection(title = stringResource(R.string.chat_settings_actions)) {
                    MenuAction(
                        icon = Icons.Rounded.Refresh,
                        title = stringResource(R.string.regenerate_latest_reply),
                        subtitle = stringResource(R.string.regenerate_latest_reply_desc)
                    ) { ChatUiIntent.RegenerateLast.emit() }
                    MenuAction(
                        icon = Icons.Rounded.AutoAwesome,
                        title = stringResource(R.string.continue_latest_reply),
                        subtitle = stringResource(R.string.continue_latest_reply_desc)
                    ) { ChatUiIntent.ContinueLast.emit() }
                    MenuAction(
                        icon = Icons.Rounded.Edit,
                        title = stringResource(R.string.impersonate_user),
                        subtitle = stringResource(R.string.impersonate_user_desc)
                    ) { ChatUiIntent.ImpersonateUser.emit() }
                    MenuAction(
                        icon = Icons.Rounded.AutoAwesome,
                        title = stringResource(R.string.summarize_now),
                        subtitle = stringResource(R.string.summarize_now_desc)
                    ) { ChatUiIntent.SummarizeNow.emit() }
                    MenuAction(
                        icon = Icons.Rounded.Refresh,
                        title = stringResource(R.string.restore_previous_summary),
                        subtitle = stringResource(R.string.restore_previous_summary_desc)
                    ) { ChatUiIntent.RestorePreviousSummary.emit() }
                    MenuAction(
                        icon = Icons.Rounded.FileUpload,
                        title = stringResource(R.string.export_chat),
                        subtitle = stringResource(R.string.export_chat_desc),
                        enabled = loadState == ChatLoadState.None
                    ) { ChatUiIntent.ExportChatClick.emit() }
                    // 清空上下文：消息全删，会话与设置留着
                    MenuAction(
                        icon = Icons.Rounded.Refresh,
                        title = stringResource(R.string.clear_context_action),
                        subtitle = stringResource(R.string.clear_context_message),
                        enabled = loadState == ChatLoadState.None
                    ) { ChatUiIntent.ClearConversationContextClick.emit() }
                    MenuAction(
                        icon = Icons.Rounded.Delete,
                        title = stringResource(R.string.delete_chat_title),
                        subtitle = stringResource(R.string.delete_chat_desc),
                        iconTint = MaterialTheme.colorScheme.error,
                        enabled = loadState == ChatLoadState.None
                    ) { ChatUiIntent.DeleteSessionClick.emit() }
                }
            }
            item {
                // 动作预设：这只是「本会话」的那一份，全局默认在设置页里改。
                // 单人对话只有一个角色，带 {name} 的动作在这里也有明确落点，全都列出来。
                SettingsSection(title = stringResource(R.string.poke_actions_title)) {
                    Text(
                        text = if (usesOwnPokeActions) {
                            stringResource(R.string.poke_actions_session_own)
                        } else {
                            stringResource(R.string.poke_actions_session_inherit)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    sessionPokeActions.forEach { action ->
                        MenuAction(
                            icon = Icons.Rounded.TouchApp,
                            title = action.label,
                            subtitle = action.template
                        ) { ChatUiIntent.OpenPokeActionEditor.emit() }
                    }
                    MenuAction(
                        icon = Icons.Rounded.Edit,
                        title = stringResource(R.string.poke_actions_edit)
                    ) { ChatUiIntent.OpenPokeActionEditor.emit() }
                    if (usesOwnPokeActions) {
                        MenuAction(
                            icon = Icons.Rounded.RestartAlt,
                            title = stringResource(R.string.poke_actions_follow_global)
                        ) { ChatUiIntent.ResetSessionPokeActions.emit() }
                    }
                }
            }
            item {
                // 会话级背景：没设过就跟全局走，设过就压过全局。
                // 单独给「恢复跟随全局」而不是只能清除，是因为清掉之后
                // 用户还要知道回到的是全局那张图。
                SettingsSection(title = stringResource(R.string.session_background_title)) {
                    Text(
                        text = if (hasOwnBackground) {
                            stringResource(R.string.session_background_own)
                        } else {
                            stringResource(R.string.session_background_follow_global)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MenuAction(
                        icon = Icons.Rounded.PhotoLibrary,
                        title = stringResource(R.string.session_background_pick)
                    ) { ChatUiIntent.PickSessionBackground.emit() }
                    if (hasOwnBackground) {
                        MenuAction(
                            icon = Icons.Rounded.RestartAlt,
                            title = stringResource(R.string.session_background_clear)
                        ) { ChatUiIntent.ClearSessionBackground.emit() }
                    }
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.session)) {
                    SummaryPauseRow(
                        paused = session.autoSummaryPaused,
                        onPausedChange = {
                            ChatUiIntent.ToggleAutoSummaryPaused(it).emit()
                        }
                    )
                    AutoSaveTextField(
                        label = stringResource(R.string.title),
                        value = session.title,
                        minLines = 1,
                        maxLines = 1,
                        singleLine = true,
                        onSave = { ChatUiIntent.SaveTitle(it).emit() }
                    )
                    AutoSaveTextField(
                        label = stringResource(R.string.current_summary),
                        value = session.summarize,
                        placeholder = stringResource(R.string.no_summary_yet),
                        minLines = 3,
                        maxLines = 8,
                        onSave = { ChatUiIntent.SaveSummary(it).emit() }
                    )
                    AutoSaveTextField(
                        label = stringResource(R.string.user_note),
                        value = session.userNote,
                        placeholder = stringResource(R.string.empty),
                        minLines = 3,
                        maxLines = 8,
                        onSave = { ChatUiIntent.SaveUserNote(it).emit() }
                    )
                    // 默认继承全局，要单独设才打开；关掉会清空本地值
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.override_persona),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.override_persona_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = session.overridePersona,
                            onCheckedChange = {
                                ChatUiIntent.ToggleOverridePersona(it).emit()
                            }
                        )
                    }
                    if (session.overridePersona) {
                        AutoSaveTextField(
                            label = stringResource(R.string.user_display_name),
                            value = session.userName,
                            minLines = 1,
                            maxLines = 1,
                            singleLine = true,
                            onSave = { ChatUiIntent.SaveUserName(it).emit() }
                        )
                        AutoSaveTextField(
                            label = stringResource(R.string.user_persona_description),
                            value = session.userDescription,
                            placeholder = stringResource(R.string.empty),
                            minLines = 3,
                            maxLines = 8,
                            onSave = { ChatUiIntent.SaveUserDescription(it).emit() }
                        )
                    }
                    AutoSaveTextField(
                        label = stringResource(R.string.creator_notes),
                        value = session.creatorNotes,
                        placeholder = stringResource(R.string.using_character_default_or_empty),
                        minLines = 3,
                        maxLines = 8,
                        onSave = { ChatUiIntent.SaveCreatorNotes(it).emit() }
                    )
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.world_book)) {
                    MenuAction(
                        icon = Icons.Rounded.Book,
                        title = stringResource(R.string.world_book_manager),
                        subtitle = stringResource(R.string.world_book_subtitle)
                    ) { ChatUiIntent.OpenWorldBookManager.emit() }
                    SessionLoreSettings(
                        groups = lorebookState.groups,
                        visibleGroups = lorebookState.visibleGroups,
                        query = lorebookState.query,
                        emit = emit
                    )
                }
            }
        }
    }
}

@Composable
internal fun SummaryPauseRow(
    paused: Boolean,
    onPausedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.pause_auto_summary),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(R.string.pause_auto_summary_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
            )
        }
        Switch(checked = paused, onCheckedChange = onPausedChange)
    }
}

@Composable
internal fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RpSectionHeader(title = title)
            content()
        }
    }
}

@Composable
internal fun AutoSaveTextField(
    label: String,
    value: String,
    placeholder: String? = null,
    minLines: Int,
    maxLines: Int,
    singleLine: Boolean = false,
    onSave: (String) -> Unit
) {
    var text by remember(label) { mutableStateOf(value) }
    var isFocused by remember(label) { mutableStateOf(false) }

    LaunchedEffect(value, isFocused) {
        if (!isFocused && text != value) {
            text = value
        }
    }

    LaunchedEffect(text, value) {
        if (text != value) {
            delay(450)
            onSave(text)
        }
    }
    val placeholderContent: (@Composable () -> Unit)? = placeholder?.let { placeholderText ->
        { Text(placeholderText) }
    }

    RpScrollableOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        value = text,
        onValueChange = { text = it },
        label = { Text(label) },
        placeholder = placeholderContent,
        minLines = minLines,
        maxLines = maxLines,
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
internal fun MenuAction(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) iconTint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    )
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.58f else 0.38f)
                    )
                }
            }
        }
    }
}
