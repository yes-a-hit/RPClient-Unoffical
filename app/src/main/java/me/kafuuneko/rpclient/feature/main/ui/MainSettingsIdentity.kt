package me.kafuuneko.rpclient.feature.main.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.main.model.MainChatSessionGroup
import me.kafuuneko.rpclient.feature.main.model.MainGenerationParameter
import me.kafuuneko.rpclient.feature.main.model.MainHomeContentTab
import me.kafuuneko.rpclient.feature.main.model.MainHomeItemSelection
import me.kafuuneko.rpclient.feature.main.model.MainHomeItemType
import me.kafuuneko.rpclient.feature.main.model.MainProviderItem
import me.kafuuneko.rpclient.feature.main.model.items.MainChatSessionItem
import me.kafuuneko.rpclient.feature.main.model.items.MainGroupChatSessionItem
import me.kafuuneko.rpclient.feature.main.model.items.MainHomeContentItem
import me.kafuuneko.rpclient.feature.main.model.items.MainAdventureItem
import me.kafuuneko.rpclient.feature.main.model.items.MainStoryItem
import me.kafuuneko.rpclient.feature.main.presentation.MainAppearanceSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainDebugSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainPokeActionState
import me.kafuuneko.rpclient.feature.main.presentation.MainDialogState
import me.kafuuneko.rpclient.feature.main.presentation.MainGenerationParametersState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeResourceState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeSelectionState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeState
import me.kafuuneko.rpclient.feature.main.presentation.MainPage
import me.kafuuneko.rpclient.feature.main.presentation.MainPromptBehaviorState
import me.kafuuneko.rpclient.feature.main.presentation.MainProviderPostProcessingState
import me.kafuuneko.rpclient.feature.main.presentation.MainProviderSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentChatsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentGroupChatsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentStoriesState
import me.kafuuneko.rpclient.feature.main.presentation.MainSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummaryInjectionState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummarySettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummarySettingsTab
import me.kafuuneko.rpclient.feature.main.presentation.MainUiIntent
import me.kafuuneko.rpclient.feature.main.presentation.MainUiState
import me.kafuuneko.rpclient.feature.main.presentation.MainUserAvatarState
import me.kafuuneko.rpclient.feature.main.presentation.MainUserIdentityState
import me.kafuuneko.rpclient.feature.main.presentation.MainWorldInfoBudgetState
import me.kafuuneko.rpclient.libs.prompt.model.ExampleDialogueBehavior
import me.kafuuneko.rpclient.libs.prompt.model.PromptPostProcessingMode
import me.kafuuneko.rpclient.libs.prompt.model.SummaryInjectionPosition
import me.kafuuneko.rpclient.libs.prompt.model.SummaryInjectionRole
import me.kafuuneko.rpclient.libs.theme.AppThemeMode
import me.kafuuneko.rpclient.model.TokenPreset
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppInputDialog
import me.kafuuneko.rpclient.ui.dialog.AppPromptEditorDialog
import me.kafuuneko.rpclient.ui.dialog.NumericEditDialog
import me.kafuuneko.rpclient.ui.dialog.NumericEditQuickOption
import me.kafuuneko.rpclient.ui.dialog.SliderConfig
import me.kafuuneko.rpclient.ui.theme.AccentAmberColor
import me.kafuuneko.rpclient.ui.theme.AccentBlueColor
import me.kafuuneko.rpclient.ui.theme.AccentEmeraldColor
import me.kafuuneko.rpclient.ui.theme.AccentIndigoColor
import me.kafuuneko.rpclient.ui.theme.AccentPinkColor
import me.kafuuneko.rpclient.ui.theme.AccentRedColor
import me.kafuuneko.rpclient.ui.theme.AccentSkyColor
import me.kafuuneko.rpclient.ui.theme.AccentVioletColor
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.ProviderAvailableColor
import me.kafuuneko.rpclient.ui.theme.ProviderDisabledColor
import me.kafuuneko.rpclient.ui.theme.ProviderPendingColor
import me.kafuuneko.rpclient.ui.theme.getMacaronColor
import me.kafuuneko.rpclient.ui.widgets.MacroDictionaryDialog
import me.kafuuneko.rpclient.ui.widgets.PokeActionEditorDialog
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpInfoCard
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpMacroActionBar
import me.kafuuneko.rpclient.ui.widgets.RpMetaRow
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpPercentageSlider
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpSettingsDivider
import me.kafuuneko.rpclient.ui.widgets.RpSettingsGroup
import me.kafuuneko.rpclient.ui.widgets.RpSettingsSwitchTile
import me.kafuuneko.rpclient.ui.widgets.RpSettingsTile
import me.kafuuneko.rpclient.ui.widgets.RpSettingsValueTile
import me.kafuuneko.rpclient.ui.widgets.rememberBoundTextFieldState
import me.kafuuneko.rpclient.utils.rememberPromptMacroOutputTransformation
import androidx.compose.material.icons.rounded.Image as ImageIcon

@Composable
internal fun UserIdentityPanel(
    state: MainUserIdentityState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAvatarPicker(
                    state = state,
                    emit = emit
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = state.userName.ifBlank { stringResource(R.string.user_display_name) },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = state.userDescriptionPreview.ifBlank {
                            stringResource(R.string.user_persona_description)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.userName,
                    onValueChange = { MainUiIntent.ChangeUserName(it).emit() },
                    label = { Text(stringResource(R.string.user_display_name)) },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                UserPersonaDescriptionField(
                    value = state.userDescription,
                    onValueChange = { MainUiIntent.ChangeUserDescription(it).emit() },
                    onExpandFullscreen = {
                        MainUiIntent.ShowUserDescriptionEditor.emit()
                    }
                )
                // 性别与发图语义都是可选补充：留空不注入，填了才生效
                UserPersonaSingleLineField(
                    label = stringResource(R.string.user_gender),
                    value = state.userGender,
                    onClick = { MainUiIntent.ShowUserGenderEditor.emit() }
                )
                UserPersonaSingleLineField(
                    label = stringResource(R.string.user_image_meaning),
                    value = state.userImageMeaning,
                    onClick = { MainUiIntent.ShowUserImageMeaningEditor.emit() }
                )
                if (state.avatarState is MainUserAvatarState.Configured) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { MainUiIntent.ClearUserAvatar.emit() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                stringResource(R.string.clear_user_avatar),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 带宏高亮、光标插入快捷栏和全屏编辑入口的用户人设描述输入框。 */

@Composable
internal fun UserPersonaDescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    onExpandFullscreen: () -> Unit
) {
    val textFieldState = rememberBoundTextFieldState(value, onValueChange)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        RpScrollableOutlinedTextField(
            state = textFieldState,
            label = { Text(stringResource(R.string.user_persona_description)) },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(14.dp),
            outputTransformation = rememberPromptMacroOutputTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        RpMacroActionBar(
            onInsertMacro = { macro ->
                val start = minOf(
                    textFieldState.selection.start,
                    textFieldState.selection.end
                )
                val end = maxOf(
                    textFieldState.selection.start,
                    textFieldState.selection.end
                )
                textFieldState.edit {
                    replace(start, end, macro)
                    selection = TextRange(start + macro.length)
                }
            },
            onFullscreenClick = onExpandFullscreen,
            macros = USER_PERSONA_MACROS
        )
    }
}

@Composable
internal fun UserAvatarPicker(
    state: MainUserIdentityState,
    emit: MainUiIntent.() -> Unit
) {
    val avatarText = state.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val avatarColor = remember(state.userName) {
        getMacaronColor(state.userName.ifBlank { "user" })
    }

    Box(
        modifier = Modifier
            .size(78.dp)
            .clickable { MainUiIntent.PickUserAvatarClick.emit() },
        contentAlignment = Alignment.Center
    ) {
        val avatarState = state.avatarState
        val avatarImage = (avatarState as? MainUserAvatarState.Configured)?.image
        Surface(
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            ),
            shadowElevation = 2.dp
        ) {
            if (avatarImage == null) {
                RpAvatar(
                    text = avatarText,
                    color = avatarColor,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp)
                )
            } else {
                Image(
                    bitmap = avatarImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(26.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.ImageIcon,
                    contentDescription = stringResource(R.string.choose_user_avatar),
                    modifier = Modifier.size(13.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
internal fun PromptBehaviorPanel(
    state: MainPromptBehaviorState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.prompt_behavior_section),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.prompt_post_processing_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
            val postProcessingState = state.providerPostProcessingState
            val selectedMode =
                (postProcessingState as? MainProviderPostProcessingState.Available)?.mode

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PromptPostProcessingMode.entries.forEach { mode ->
                    PromptPostProcessingModeRow(
                        mode = mode,
                        selected = mode == selectedMode,
                        enabled = postProcessingState is MainProviderPostProcessingState.Available,
                        onClick = { MainUiIntent.SelectPostProcessingMode(mode).emit() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.prompt_example_behavior_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.prompt_example_behavior_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExampleDialogueBehavior.entries.forEach { behavior ->
                    FilterChip(
                        selected = behavior == state.exampleDialogueBehavior,
                        onClick = {
                            MainUiIntent.SelectExampleDialogueBehavior(behavior).emit()
                        },
                        label = { Text(stringResource(behavior.titleRes())) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
            NumberSettingRow(
                title = stringResource(R.string.prompt_max_history_messages),
                value = state.maxHistoryMessages.toString(),
                helper = stringResource(R.string.prompt_max_history_messages_helper),
                onValueChange = { MainUiIntent.ChangeMaxPromptHistoryMessages(it).emit() }
            )
        }
        RpSettingsDivider(startIndent = false)
        RpSettingsSwitchTile(
            icon = Icons.Rounded.Psychology,
            iconColor = AccentIndigoColor,
            iconContainerColor = AccentIndigoColor.copy(alpha = 0.14f),
            title = stringResource(R.string.prompt_include_think_context_title),
            subtitle = stringResource(R.string.prompt_include_think_context_desc),
            checked = state.includeThinkInContext,
            onCheckedChange = { MainUiIntent.ToggleIncludeThinkInContext(it).emit() }
        )
        RpSettingsDivider()
        RpSettingsSwitchTile(
            icon = Icons.Rounded.NotificationsActive,
            iconColor = AccentPinkColor,
            iconContainerColor = AccentPinkColor.copy(alpha = 0.14f),
            title = stringResource(R.string.context_trimming_alert),
            subtitle = stringResource(R.string.context_trimming_alert_desc),
            checked = state.contextTrimmingAlert,
            onCheckedChange = { MainUiIntent.ToggleContextTrimmingAlert(it).emit() }
        )
        RpSettingsDivider()
        RpSettingsSwitchTile(
            icon = Icons.Rounded.Stream,
            iconColor = AccentSkyColor,
            iconContainerColor = AccentSkyColor.copy(alpha = 0.14f),
            title = stringResource(R.string.streaming_response),
            subtitle = stringResource(R.string.streaming_response_desc),
            checked = state.streamEnabled,
            onCheckedChange = { MainUiIntent.ToggleStreamEnabled(it).emit() }
        )
    }
}

@Composable
internal fun PromptPostProcessingModeRow(
    mode: PromptPostProcessingMode,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 0.5.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.50f)
            }
        ),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(mode.titleRes()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(mode.descriptionRes()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

internal fun PromptPostProcessingMode.titleRes(): Int {
    return when (this) {
        PromptPostProcessingMode.None -> R.string.prompt_post_processing_none
        PromptPostProcessingMode.Merge -> R.string.prompt_post_processing_merge
        PromptPostProcessingMode.SemiStrict -> R.string.prompt_post_processing_semi_strict
        PromptPostProcessingMode.Strict -> R.string.prompt_post_processing_strict
        PromptPostProcessingMode.SingleUserMessage -> R.string.prompt_post_processing_single_user
    }
}

internal fun PromptPostProcessingMode.descriptionRes(): Int {
    return when (this) {
        PromptPostProcessingMode.None -> R.string.prompt_post_processing_none_desc
        PromptPostProcessingMode.Merge -> R.string.prompt_post_processing_merge_desc
        PromptPostProcessingMode.SemiStrict -> R.string.prompt_post_processing_semi_strict_desc
        PromptPostProcessingMode.Strict -> R.string.prompt_post_processing_strict_desc
        PromptPostProcessingMode.SingleUserMessage -> R.string.prompt_post_processing_single_user_desc
    }
}

internal fun ExampleDialogueBehavior.titleRes(): Int {
    return when (this) {
        ExampleDialogueBehavior.Normal -> R.string.prompt_example_behavior_normal
        ExampleDialogueBehavior.Pinned -> R.string.prompt_example_behavior_pinned
        ExampleDialogueBehavior.Disabled -> R.string.prompt_example_behavior_disabled
    }
}

@Composable
internal fun UserPersonaSingleLineField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.ifBlank { stringResource(R.string.not_set) },
            style = MaterialTheme.typography.bodyMedium,
            color = if (value.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}


/**
 * 备份与恢复入口。
 *
 * 单独放出来是因为安装签名变了就只能卸载重装，一卸载 /data 全清，
 * 用户得在卸载前先把资料导出去。这个入口不藏进调试面板里。
 */
