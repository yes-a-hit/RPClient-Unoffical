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
internal fun SummaryPanel(
    state: MainSummarySettingsState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.summary_memory),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MainSummarySettingsTab.entries.forEach { tab ->
                    val isSelected = tab == state.selectedTab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { MainUiIntent.SelectSummarySettingsTab(tab).emit() },
                        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        shadowElevation = if (isSelected) 1.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(tab.titleRes()),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            when (state.selectedTab) {
                MainSummarySettingsTab.General -> GeneralSummarySettings(state, emit)
                MainSummarySettingsTab.Conversation -> ConversationSummarySettings(state, emit)
            }
        }
    }
}

@Composable
internal fun GeneralSummarySettings(
    state: MainSummarySettingsState,
    emit: MainUiIntent.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SummaryProviderSelector(
            selectedProviderId = state.selectedProviderId,
            providers = state.providers,
            onSelect = { MainUiIntent.SelectSummaryProvider(it).emit() }
        )
        NumberSettingRow(
            title = stringResource(R.string.summary_target_words),
            value = state.wordsLimit.toString(),
            onValueChange = { MainUiIntent.ChangeSummaryWordsLimit(it).emit() }
        )
        NumberSettingRow(
            title = stringResource(R.string.summary_response_tokens),
            value = state.responseTokens.toString(),
            onValueChange = { MainUiIntent.ChangeSummaryResponseTokens(it).emit() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SummaryProviderSelector(
    selectedProviderId: Long,
    providers: List<MainProviderItem>,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProvider = providers.firstOrNull { it.id == selectedProviderId }
    val selectedName = selectedProvider?.summaryDisplayName()
        ?: stringResource(R.string.follow_global_model)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.summary_model_config)) },
            supportingText = { Text(stringResource(R.string.summary_model_config_helper)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.follow_global_model)) },
                onClick = {
                    onSelect(0L)
                    expanded = false
                }
            )
            providers.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.summaryDisplayName()) },
                    onClick = {
                        onSelect(provider.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
internal fun MainProviderItem.summaryDisplayName(): String {
    val displayName = name.ifBlank { stringResource(R.string.unnamed_model_config) }
    return if (isEnabled) {
        displayName
    } else {
        stringResource(R.string.disabled_model_config_format, displayName)
    }
}

@Composable
internal fun ConversationSummarySettings(
    state: MainSummarySettingsState,
    emit: MainUiIntent.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SettingSwitchRow(
            Icons.Rounded.AutoAwesome,
            stringResource(R.string.auto_summarize),
            stringResource(R.string.auto_summarize_desc),
            state.autoSummaryEnabled,
            onCheckedChange = { MainUiIntent.ToggleAutoSummaryEnabled(it).emit() }
        )
        NumberSettingRow(
            title = stringResource(R.string.summary_update_every_messages),
            value = state.triggerMessageCount.toString(),
            onValueChange = { MainUiIntent.ChangeSummaryTriggerMessageCount(it).emit() }
        )
        NumberSettingRow(
            title = stringResource(R.string.summary_max_messages_per_request),
            value = state.maxMessagesPerRequest.toString(),
            helper = stringResource(R.string.summary_max_messages_helper),
            onValueChange = { MainUiIntent.ChangeSummaryMaxMessagesPerRequest(it).emit() }
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.summary_injection_position),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryInjectionPosition.entries.forEach { position ->
                    FilterChip(
                        selected = position == state.injectionState.position,
                        onClick = {
                            MainUiIntent.SelectSummaryInjectionPosition(position).emit()
                        },
                        label = { Text(stringResource(position.titleRes())) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
        val injectionState = state.injectionState
        if (injectionState is MainSummaryInjectionState.InChat) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumberSettingRow(
                    title = stringResource(R.string.summary_injection_depth),
                    value = injectionState.depth.toString(),
                    onValueChange = {
                        MainUiIntent.ChangeSummaryInjectionDepth(it).emit()
                    }
                )
                Text(
                    text = stringResource(R.string.summary_injection_role),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryInjectionRole.entries.forEach { role ->
                        FilterChip(
                            selected = role == injectionState.role,
                            onClick = {
                                MainUiIntent.SelectSummaryInjectionRole(role).emit()
                            },
                            label = { Text(stringResource(role.titleRes())) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }
    }
}

internal fun MainSummarySettingsTab.titleRes(): Int {
    return when (this) {
        MainSummarySettingsTab.General -> R.string.general_summary_memory
        MainSummarySettingsTab.Conversation -> R.string.conversation_summary_memory
    }
}

@Composable
internal fun NumberSettingRow(
    title: String,
    value: String,
    helper: String? = null,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            if (!helper.isNullOrBlank()) {
                Text(
                    helper,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.width(100.dp),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

internal fun SummaryInjectionPosition.titleRes(): Int {
    return when (this) {
        SummaryInjectionPosition.None -> R.string.summary_position_none
        SummaryInjectionPosition.BeforeMain -> R.string.summary_position_before_main
        SummaryInjectionPosition.AfterMain -> R.string.summary_position_after_main
        SummaryInjectionPosition.InChat -> R.string.summary_position_in_chat
    }
}

internal fun SummaryInjectionRole.titleRes(): Int {
    return when (this) {
        SummaryInjectionRole.System -> R.string.summary_role_system
        SummaryInjectionRole.User -> R.string.summary_role_user
        SummaryInjectionRole.Assistant -> R.string.summary_role_assistant
    }
}

internal fun AppThemeMode.titleRes(): Int {
    return when (this) {
        AppThemeMode.FollowSystem -> R.string.theme_follow_system
        AppThemeMode.Light -> R.string.theme_light
        AppThemeMode.Dark -> R.string.theme_dark
    }
}

internal object MaterialThemeLike {
    @Composable
    fun background() = MaterialTheme.colorScheme.background
}
