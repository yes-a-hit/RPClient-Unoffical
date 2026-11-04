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
internal fun PokeActionsPanel(
    state: MainPokeActionState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        RpSettingsTile(
            title = stringResource(R.string.poke_actions_title),
            subtitle = stringResource(R.string.poke_actions_global_subtitle, state.actions.size),
            icon = Icons.Rounded.TouchApp,
            onClick = { MainUiIntent.OpenPokeActionEditor.emit() }
        )
    }
    if (state.editorVisible) {
        PokeActionEditorDialog(
            drafts = state.drafts,
            onDismiss = { MainUiIntent.ClosePokeActionEditor.emit() },
            onChange = { id, label, template ->
                MainUiIntent.ChangePokeAction(id, label, template).emit()
            },
            onAdd = { MainUiIntent.AddPokeAction.emit() },
            onRemove = { MainUiIntent.RemovePokeAction(it).emit() },
            onSave = { MainUiIntent.SavePokeActions.emit() }
        )
    }
}

@Composable
internal fun MacroDictionaryEntryCard() {
    // 纯查阅，不参与业务状态：用局部状态，免得为一个只读对话框往 ViewModel 塞字段
    var expanded by remember { mutableStateOf(false) }
    RpSettingsGroup {
        RpSettingsTile(
            title = stringResource(R.string.macro_dictionary_title),
            subtitle = stringResource(R.string.macro_dictionary_entry_subtitle),
            icon = Icons.Rounded.DataObject,
            onClick = { expanded = true }
        )
    }
    if (expanded) {
        MacroDictionaryDialog(onDismiss = { expanded = false })
    }
}

@Composable
internal fun AppearancePanel(    state: MainAppearanceSettingsState,
    emit: MainUiIntent.() -> Unit
) {
    val backgroundState = state.backgroundState
    RpSettingsGroup {
        RpSettingsTile(
            icon = Icons.Rounded.Palette,
            iconColor = AccentIndigoColor,
            iconContainerColor = AccentIndigoColor.copy(alpha = 0.14f),
            title = stringResource(R.string.appearance_theme),
            subtitle = stringResource(R.string.appearance_theme_desc)
        )
        RpSettingsDivider(startIndent = false)
        // 使用可换行选项，避免较长译文在小屏设备上被截断。
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppThemeMode.entries.forEach { themeMode ->
                FilterChip(
                    selected = themeMode == state.themeMode,
                    onClick = { MainUiIntent.SelectThemeMode(themeMode).emit() },
                    label = { Text(stringResource(themeMode.titleRes())) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
        RpSettingsDivider(startIndent = false)
        // 聊天背景入口：点击即打开系统图片选择器更换背景
        RpSettingsTile(
            icon = Icons.Rounded.Wallpaper,
            iconColor = AccentSkyColor,
            iconContainerColor = AccentSkyColor.copy(alpha = 0.14f),
            title = stringResource(R.string.chat_background_title),
            subtitle = stringResource(
                if (backgroundState.hasImage) {
                    R.string.chat_background_configured
                } else {
                    R.string.chat_background_desc
                }
            ),
            onClick = { MainUiIntent.PickChatBackgroundClick.emit() },
            trailing = {
                if (backgroundState.hasImage) {
                    TextButton(onClick = { MainUiIntent.ClearChatBackground.emit() }) {
                        Text(stringResource(R.string.chat_background_clear))
                    }
                }
            }
        )
        // 已配置背景时才展示预览与遮罩调节，避免未启用时出现无效控件
        if (backgroundState.hasImage) {
            backgroundState.preview?.let { preview ->
                Image(
                    bitmap = preview,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            RpPercentageSlider(
                title = stringResource(R.string.chat_background_dim_title),
                value = backgroundState.dimPercent,
                helper = stringResource(R.string.chat_background_dim_helper),
                onValueChange = { MainUiIntent.SetChatBackgroundDim(it).emit() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
internal fun TokenUsageEntryCard(onClick: () -> Unit) {
    // 消耗统计不依赖调试模式，因此入口始终显示在数据与系统分组
    RpSettingsGroup {
        RpSettingsTile(
            icon = Icons.Rounded.Numbers,
            iconColor = AccentSkyColor,
            iconContainerColor = AccentSkyColor.copy(alpha = 0.14f),
            title = stringResource(R.string.token_usage),
            subtitle = stringResource(R.string.token_usage_entry_subtitle),
            onClick = onClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
internal fun ScriptEditorEntryCard(onClick: () -> Unit) {
    // 剧本画布属于创作工具，与消耗统计并列放在数据与系统分组
    RpSettingsGroup {
        RpSettingsTile(
            icon = Icons.Rounded.AutoStories,
            iconColor = AccentVioletColor,
            iconContainerColor = AccentVioletColor.copy(alpha = 0.14f),
            title = stringResource(R.string.script_editor_entry_title),
            subtitle = stringResource(R.string.script_editor_entry_desc),
            onClick = onClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
internal fun WorldInfoBudgetPanel(
    state: MainWorldInfoBudgetState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.world_info_budget_section),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.world_info_budget_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
            RpPercentageSlider(
                title = stringResource(R.string.world_info_context_percent),
                value = state.budgetPercent,
                helper = stringResource(R.string.world_info_context_percent_helper),
                onValueChange = { MainUiIntent.ChangeWorldInfoBudgetPercent(it).emit() }
            )
            NumberSettingRow(
                title = stringResource(R.string.world_info_budget_cap),
                value = state.budgetCap.toString(),
                helper = stringResource(R.string.world_info_budget_cap_helper),
                onValueChange = { MainUiIntent.ChangeWorldInfoBudgetCap(it).emit() }
            )
        }
        RpSettingsDivider(startIndent = false)
        RpSettingsSwitchTile(
            icon = Icons.Rounded.Book,
            iconColor = AccentEmeraldColor,
            iconContainerColor = AccentEmeraldColor.copy(alpha = 0.14f),
            title = stringResource(R.string.world_info_overflow_alert),
            subtitle = stringResource(R.string.world_info_overflow_alert_desc),
            checked = state.overflowAlert,
            onCheckedChange = { MainUiIntent.ToggleWorldInfoOverflowAlert(it).emit() }
        )
    }
}

@Composable
internal fun DebugPanel(
    state: MainDebugSettingsState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        RpSettingsSwitchTile(
            icon = Icons.Rounded.BugReport,
            iconColor = AccentRedColor,
            iconContainerColor = AccentRedColor.copy(alpha = 0.14f),
            title = stringResource(R.string.debug_mode),
            subtitle = stringResource(R.string.debug_mode_desc),
            checked = state.enabled,
            onCheckedChange = { MainUiIntent.ToggleDebugModeEnabled(it).emit() }
        )
        if (state.enabled) {
            RpSettingsDivider()
            RpSettingsTile(
                icon = Icons.Rounded.DataObject,
                iconColor = AccentVioletColor,
                iconContainerColor = AccentVioletColor.copy(alpha = 0.14f),
                title = stringResource(R.string.request_logs),
                subtitle = stringResource(R.string.request_logs_entry_subtitle),
                onClick = { MainUiIntent.OpenRequestLogs.emit() },
                trailing = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
internal fun AboutEntryCard(
    onClick: () -> Unit
) {
    RpSettingsGroup {
        RpSettingsTile(
            icon = Icons.Rounded.Info,
            iconColor = MaterialTheme.colorScheme.primary,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            title = stringResource(R.string.about),
            subtitle = stringResource(R.string.about_desc),
            onClick = onClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
internal fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    RpSettingsSwitchTile(
        icon = icon,
        title = title,
        subtitle = subtitle,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}

@Composable
internal fun BackupRestoreEntryCard(onClick: () -> Unit) {
    RpSettingsGroup {
        RpSettingsTile(
            icon = Icons.Rounded.CloudUpload,
            iconColor = AccentSkyColor,
            iconContainerColor = AccentSkyColor.copy(alpha = 0.14f),
            title = stringResource(R.string.backup_and_restore),
            subtitle = stringResource(R.string.backup_and_restore_desc),
            onClick = onClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}
