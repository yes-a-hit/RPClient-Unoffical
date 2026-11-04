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
internal fun GroupChatSettingsView(
    state: GroupChatSettingsState,
    members: List<GroupChatMemberItem>,
    hasOwnBackground: Boolean,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = stringResource(R.string.group_chat_settings),
            onBack = { emitIntent(GroupChatUiIntent.CloseSettings) },
            actions = {
                TextButton(onClick = { emitIntent(GroupChatUiIntent.SaveSettings) }) {
                    Text(stringResource(R.string.save))
                }
            }
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
                GroupSettingsSection(
                    title = stringResource(R.string.group_chat_basic_settings)
                ) {
                    RpScrollableOutlinedTextField(
                        value = state.titleDraft,
                        onValueChange = { emitIntent(GroupChatUiIntent.ChangeTitle(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.group_chat_title_label)) },
                        singleLine = true
                    )
                    RpScrollableOutlinedTextField(
                        value = state.scenarioDraft,
                        onValueChange = { emitIntent(GroupChatUiIntent.ChangeScenario(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.group_chat_scenario)) },
                        minLines = 3,
                        maxLines = 8
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
                            checked = state.overridePersona,
                            onCheckedChange = {
                                emitIntent(GroupChatUiIntent.ToggleOverridePersona(it))
                            }
                        )
                    }
                    if (state.overridePersona) {
                        RpScrollableOutlinedTextField(
                            value = state.userNameDraft,
                            onValueChange = { emitIntent(GroupChatUiIntent.ChangeUserName(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.user_display_name)) },
                            singleLine = true
                        )
                        RpScrollableOutlinedTextField(
                            value = state.userDescriptionDraft,
                            onValueChange = {
                                emitIntent(GroupChatUiIntent.ChangeUserDescription(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.user_persona_description)) },
                            minLines = 3,
                            maxLines = 8
                        )
                    }
                    RpScrollableOutlinedTextField(
                        value = state.userNoteDraft,
                        onValueChange = { emitIntent(GroupChatUiIntent.ChangeUserNote(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.group_chat_author_note)) },
                        minLines = 3,
                        maxLines = 8
                    )
                    RpScrollableOutlinedTextField(
                        value = state.summaryDraft,
                        onValueChange = { emitIntent(GroupChatUiIntent.ChangeSummary(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.summary_memory)) },
                        minLines = 4,
                        maxLines = 10
                    )
                    SettingsActionRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = stringResource(R.string.summarize_now),
                        subtitle = stringResource(R.string.summarize_now_desc),
                        onClick = { emitIntent(GroupChatUiIntent.SummarizeNow) }
                    )
                    SettingsActionRow(
                        icon = Icons.Rounded.Refresh,
                        title = stringResource(R.string.restore_previous_summary),
                        subtitle = stringResource(R.string.restore_previous_summary_desc),
                        onClick = {
                            emitIntent(GroupChatUiIntent.RestorePreviousSummary)
                        }
                    )
                    SettingsSwitchRow(
                        title = stringResource(R.string.pause_auto_summary),
                        subtitle = stringResource(R.string.pause_auto_summary_desc),
                        checked = state.autoSummaryPaused,
                        onCheckedChange = {
                            emitIntent(GroupChatUiIntent.ToggleAutoSummaryPaused(it))
                        }
                    )
                }
            }
            item {
                GroupSettingsSection(
                    title = stringResource(R.string.group_chat_turn_strategy)
                ) {
                    Text(
                        text = stringResource(R.string.group_chat_turn_strategy),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GroupChatActivationStrategy.entries.forEach { strategy ->
                            FilterChip(
                                selected = state.activationStrategy == strategy,
                                onClick = {
                                    emitIntent(
                                        GroupChatUiIntent.SelectActivationStrategy(strategy)
                                    )
                                },
                                label = { Text(stringResource(strategy.titleRes())) }
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.group_chat_character_cards),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GroupChatCharacterCardMode.entries.forEach { mode ->
                            FilterChip(
                                selected = state.characterCardMode == mode,
                                onClick = {
                                    emitIntent(GroupChatUiIntent.SelectCharacterCardMode(mode))
                                },
                                label = {
                                    Text(
                                        stringResource(
                                            if (mode == GroupChatCharacterCardMode.Swap) {
                                                R.string.group_chat_card_mode_swap
                                            } else {
                                                R.string.group_chat_card_mode_join
                                            }
                                        )
                                    )
                                }
                            )
                        }
                    }
                    SettingsSwitch(
                        title = stringResource(R.string.group_chat_include_muted_cards),
                        checked = state.includeMutedCards,
                        onCheckedChange = {
                            emitIntent(GroupChatUiIntent.ToggleIncludeMutedCards(it))
                        }
                    )
                    SettingsSwitch(
                        title = stringResource(R.string.group_chat_allow_consecutive),
                        checked = state.allowSelfResponses,
                        onCheckedChange = {
                            emitIntent(GroupChatUiIntent.ToggleAllowSelfResponses(it))
                        }
                    )
                    SettingsSwitch(
                        title = stringResource(R.string.group_chat_auto_mode),
                        checked = state.autoModeEnabled,
                        onCheckedChange = {
                            emitIntent(GroupChatUiIntent.ToggleAutoMode(it))
                        }
                    )
                    SettingsSwitch(
                        title = stringResource(R.string.group_chat_trim_other_speakers),
                        checked = state.trimOtherSpeakers,
                        onCheckedChange = {
                            emitIntent(GroupChatUiIntent.ToggleTrimOtherSpeakers(it))
                        }
                    )
                }
            }
            item {
                GroupSettingsSection(
                    title = stringResource(R.string.group_chat_prompt_overrides)
                ) {
                    Text(
                        text = stringResource(R.string.group_chat_prompt_overrides_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    RpScrollableOutlinedTextField(
                        value = state.systemPromptDraft,
                        onValueChange = {
                            emitIntent(GroupChatUiIntent.ChangeSystemPrompt(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.prompt_main_title)) },
                        minLines = 3,
                        maxLines = 10
                    )
                    RpScrollableOutlinedTextField(
                        value = state.groupNudgePromptDraft,
                        onValueChange = {
                            emitIntent(GroupChatUiIntent.ChangeGroupNudgePrompt(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.prompt_group_nudge_title)) },
                        minLines = 3,
                        maxLines = 10
                    )
                    RpScrollableOutlinedTextField(
                        value = state.newGroupChatPromptDraft,
                        onValueChange = {
                            emitIntent(GroupChatUiIntent.ChangeNewGroupChatPrompt(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.prompt_new_group_chat_title))
                        },
                        minLines = 2,
                        maxLines = 8
                    )
                }
            }
            item {
                GroupSettingsSection(title = stringResource(R.string.group_chat_members)) {
                    GroupMemberSettingsList(members, emitIntent)
                    state.availableCharacters
                        .filterNot { it.alreadyMember }
                        .forEach { character ->
                            SettingsActionRow(
                                icon = Icons.Rounded.Groups,
                                title = stringResource(
                                    R.string.group_chat_add_member,
                                    character.name
                                ),
                                onClick = {
                                    emitIntent(GroupChatUiIntent.AddMember(character.id))
                                }
                            )
                        }
                }
            }
            item {
                // 会话级背景：没设过就跟全局走，设过就压过全局
                GroupSettingsSection(title = stringResource(R.string.session_background_title)) {
                    Text(
                        text = if (hasOwnBackground) {
                            stringResource(R.string.session_background_own)
                        } else {
                            stringResource(R.string.session_background_follow_global)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SettingsActionRow(
                        icon = Icons.Rounded.PhotoLibrary,
                        title = stringResource(R.string.session_background_pick),
                        subtitle = stringResource(R.string.session_background_follow_global),
                        onClick = { emitIntent(GroupChatUiIntent.PickSessionBackground) }
                    )
                    if (hasOwnBackground) {
                        SettingsActionRow(
                            icon = Icons.Rounded.Refresh,
                            title = stringResource(R.string.session_background_clear),
                            onClick = { emitIntent(GroupChatUiIntent.ClearSessionBackground) }
                        )
                    }
                }
            }
            item {
                GroupSettingsSection(title = stringResource(R.string.world_book)) {
                    Text(
                        text = stringResource(R.string.session_lore_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    GroupChatLorebookSelector(
                        groups = state.lorebookGroups,
                        visibleGroups = state.visibleLorebookGroups,
                        query = state.lorebookQuery,
                        onQueryChange = {
                            emitIntent(GroupChatUiIntent.ChangeLorebookQuery(it))
                        },
                        onToggleLorebook = {
                            emitIntent(GroupChatUiIntent.ToggleLorebook(it))
                        },
                        onToggleEntry = {
                            emitIntent(GroupChatUiIntent.ToggleLorebookEntry(it))
                        }
                    )
                }
            }
            item {
                GroupSettingsSection(title = stringResource(R.string.group_chat_scenes)) {
                    // 场景把同一个人在不同地点的状态分开：不在场的角色不参与这一轮发言。
                    // 没有场景时这里只有一句说明和一个新建按钮，不额外占位。
                    if (state.scenes.isEmpty()) {
                        Text(
                            text = stringResource(R.string.group_chat_scene_empty_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // 「全员」也是一个场景条目，只是不过滤成员：给它单独的编辑入口，
                    // 不然它会显得像个特例，连背景都配不了
                    SettingsActionRow(
                        icon = Icons.Rounded.Groups,
                        title = stringResource(R.string.group_chat_scene_all),
                        subtitle = stringResource(R.string.group_chat_scene_all_subtitle),
                        onClick = {
                            emitIntent(
                                GroupChatUiIntent.OpenSceneEditor(
                                    me.kafuuneko.rpclient.libs.groupchat.GroupChatScenes.DEFAULT_SCENE_ID
                                )
                            )
                        }
                    )
                    state.scenes.forEach { scene ->
                        SettingsActionRow(
                            icon = Icons.Rounded.Place,
                            title = scene.name,
                            subtitle = when {
                                !scene.restrictMembers ->
                                    stringResource(R.string.group_chat_scene_all_present)

                                scene.memberNames.isEmpty() ->
                                    stringResource(R.string.group_chat_scene_nobody)

                                else -> scene.memberNames.joinToString("、")
                            },
                            onClick = {
                                emitIntent(GroupChatUiIntent.OpenSceneEditor(scene.id))
                            }
                        )
                    }
                    SettingsActionRow(
                        icon = Icons.Rounded.Add,
                        title = stringResource(R.string.group_chat_scene_add),
                        onClick = { emitIntent(GroupChatUiIntent.OpenSceneEditor(null)) }
                    )
                }
            }
            item {
                GroupSettingsSection(title = stringResource(R.string.chat_settings_actions)) {
                    // 清空上下文：消息与摘要全清，群聊条目、成员和所有配置留着。
                    // 有它才不用为了重开一段对话去重建整个群聊。
                    SettingsActionRow(
                        icon = Icons.Rounded.Refresh,
                        title = stringResource(R.string.clear_context_action),
                        subtitle = stringResource(R.string.clear_context_message),
                        onClick = {
                            emitIntent(GroupChatUiIntent.ClearConversationContextClick)
                        }
                    )
                    SettingsActionRow(
                        icon = Icons.Rounded.Delete,
                        title = stringResource(R.string.group_chat_delete_content_description),
                        subtitle = stringResource(R.string.delete_chat_desc),
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = { emitIntent(GroupChatUiIntent.DeleteSessionClick) }
                    )
                }
            }
            item {
                Button(
                    onClick = { emitIntent(GroupChatUiIntent.SaveSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

/** 使用与单聊设置页一致的卡片式分区容器。 */

@Composable
internal fun GroupSettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
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

/** 展示带图标、标题和可选说明的设置操作项。 */

@Composable
internal fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
internal fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/** 在群聊设置卡片内渲染支持长按拖动的成员列表。 */

@Composable
internal fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
