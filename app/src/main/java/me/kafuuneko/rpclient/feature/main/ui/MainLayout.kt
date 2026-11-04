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

internal val USER_PERSONA_MACROS = listOf("{{char}}", "{{user}}")

/** 主页面 Compose 入口，承载首页聊天与故事列表以及全局设置。 */

@Composable
fun MainLayout(
    uiState: MainUiState,
    emit: MainUiIntent.() -> Unit
) {
    BackHandler(enabled = uiState is MainUiState.Normal) { MainUiIntent.Back.emit() }
    when (uiState) {
        MainUiState.None -> Unit
        is MainUiState.Finished -> MainLayout(uiState.previous) {}
        is MainUiState.Normal -> MainNormal(uiState, emit)
    }
}

@Composable
private fun MainNormal(
    uiState: MainUiState.Normal,
    emit: MainUiIntent.() -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialThemeLike.background())
        ) {
            when (uiState.selectedPage) {
                MainPage.Home -> HomePage(uiState.homeState, emit)
                MainPage.Settings -> SettingsPage(uiState.settingsState, emit)
            }

            val selectionState = uiState.homeState.selectionState
                    as? MainHomeSelectionState.Selecting
            if (selectionState != null && uiState.selectedPage == MainPage.Home) {
                MultiSelectBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    selectedCount = selectionState.selectedItems.size,
                    emit = emit
                )
            } else {
                MainBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    selectedPage = uiState.selectedPage,
                    emit = emit
                )
            }
        }
    }

    DialogSwitch(uiState.dialogState, emit)
}

@Composable
private fun MainBottomBar(
    modifier: Modifier = Modifier,
    selectedPage: MainPage,
    emit: MainUiIntent.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        ),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainBottomBarItem(
                selected = selectedPage == MainPage.Home,
                onClick = { MainUiIntent.SelectPage(MainPage.Home).emit() },
                icon = Icons.Rounded.Home,
                label = stringResource(R.string.home),
                modifier = Modifier.weight(1f)
            )
            MainBottomBarItem(
                selected = selectedPage == MainPage.Settings,
                onClick = { MainUiIntent.SelectPage(MainPage.Settings).emit() },
                icon = Icons.Rounded.Settings,
                label = stringResource(R.string.settings),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MultiSelectBottomBar(
    modifier: Modifier = Modifier,
    selectedCount: Int,
    emit: MainUiIntent.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        ),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainBottomBarItem(
                selected = false,
                onClick = { MainUiIntent.ExitMultiSelect.emit() },
                icon = Icons.Rounded.Close,
                label = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f)
            )
            MainBottomBarItem(
                selected = false,
                onClick = { MainUiIntent.ShowDeleteSelectedDialog.emit() },
                icon = Icons.Rounded.Delete,
                label = stringResource(R.string.delete),
                modifier = Modifier.weight(1f),
                enabled = selectedCount > 0
            )
        }
    }
}

@Composable
private fun DialogSwitch(
    dialogState: MainDialogState,
    emit: MainUiIntent.() -> Unit
) {
    when (dialogState) {
        MainDialogState.None -> Unit

        is MainDialogState.DeleteSelectedItems -> AppDangerDialog(
            onDismissRequest = { MainUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.delete_selected_items_title),
            message = stringResource(
                R.string.delete_selected_items_message,
                dialogState.count
            ),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            confirmEnabled = !dialogState.isDeleting,
            isConfirmLoading = dialogState.isDeleting,
            onConfirm = { MainUiIntent.ConfirmDeleteSelected.emit() },
        )

        is MainDialogState.RenameItem -> AppInputDialog(
            onDismissRequest = { MainUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.rename),
            value = dialogState.title,
            onValueChange = { MainUiIntent.ChangeItemTitleDraft(it).emit() },
            label = stringResource(R.string.title),
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            confirmEnabled = dialogState.title.isNotBlank() && !dialogState.isSaving,
            isConfirmLoading = dialogState.isSaving,
            onConfirm = { MainUiIntent.ConfirmItemRename.emit() }
        )

        is MainDialogState.EditGenerationParameter -> NumericEditDialog(
            title = stringResource(dialogState.parameter.titleRes()),
            subtitle = stringResource(dialogState.parameter.subtitleRes()),
            value = dialogState.draftValue,
            decimalInput = dialogState.parameter.isDecimalInput(),
            sliderConfig = dialogState.parameter.sliderConfig(),
            quickOptions = dialogState.parameter.quickOptions(),
            onValueChange = { MainUiIntent.ChangeGenerationParameterDraft(it).emit() },
            onConfirm = { MainUiIntent.ConfirmGenerationParameter.emit() },
            onDismiss = { MainUiIntent.DismissDialog.emit() }
        )

        is MainDialogState.EditUserDescription -> AppPromptEditorDialog(
            onDismissRequest = { MainUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.user_persona_description),
            value = dialogState.draftText,
            availableMacros = USER_PERSONA_MACROS,
            onValueChange = {
                MainUiIntent.ChangeUserDescriptionEditorDraft(it).emit()
            },
            onConfirm = { MainUiIntent.ConfirmUserDescriptionEditor.emit() }
        )

        is MainDialogState.EditUserGender -> AppPromptEditorDialog(
            onDismissRequest = { MainUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.user_gender),
            value = dialogState.draftText,
            onValueChange = { MainUiIntent.ChangeUserGenderDraft(it).emit() },
            onConfirm = { MainUiIntent.ConfirmUserGender.emit() }
        )

        is MainDialogState.EditUserImageMeaning -> AppPromptEditorDialog(
            onDismissRequest = { MainUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.user_image_meaning),
            value = dialogState.draftText,
            onValueChange = { MainUiIntent.ChangeUserImageMeaningDraft(it).emit() },
            onConfirm = { MainUiIntent.ConfirmUserImageMeaning.emit() }
        )

        is MainDialogState.ImportChatCharacterSelection -> ImportChatCharacterDialog(
            state = dialogState,
            emit = emit
        )

        is MainDialogState.ImportSkill -> SkillImportDialog(dialogState, emit)

        is MainDialogState.ReleaseNotes -> ReleaseNotesDialog(
            version = dialogState.version,
            onDismiss = { MainUiIntent.DismissReleaseNotes.emit() }
        )
    }
}

/**
 * 新版本首次打开的更新摘要。
 *
 * 内容走资源文件而不是状态：一版一次性文案，跟着界面语言走才对，
 * 放进状态里就得为每个语言各存一份。
 */

@Composable
private fun ReleaseNotesDialog(version: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.release_notes_title, version)) },
        text = {
            Text(
                text = stringResource(R.string.release_notes_body),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.release_notes_confirm))
            }
        }
    )
}

private fun MainGenerationParameter.titleRes(): Int = when (this) {
    MainGenerationParameter.Temperature -> R.string.temperature
    MainGenerationParameter.TopP -> R.string.top_p
    MainGenerationParameter.MaxTokens -> R.string.max_tokens
    MainGenerationParameter.ContextTokens -> R.string.context
}

private fun MainGenerationParameter.subtitleRes(): Int = when (this) {
    MainGenerationParameter.Temperature -> R.string.parameter_temperature_desc
    MainGenerationParameter.TopP -> R.string.parameter_top_p_desc
    MainGenerationParameter.MaxTokens -> R.string.parameter_max_tokens_desc
    MainGenerationParameter.ContextTokens -> R.string.parameter_context_tokens_desc
}

private fun MainGenerationParameter.isDecimalInput(): Boolean = when (this) {
    MainGenerationParameter.Temperature, MainGenerationParameter.TopP -> true
    MainGenerationParameter.MaxTokens, MainGenerationParameter.ContextTokens -> false
}

@Composable
private fun MainGenerationParameter.sliderConfig(): SliderConfig? = when (this) {
    MainGenerationParameter.Temperature -> SliderConfig(
        range = 0.00f..2.00f,
        step = 0.05f,
        minLabel = stringResource(R.string.parameter_temp_min_label),
        maxLabel = stringResource(R.string.parameter_temp_max_label)
    )

    MainGenerationParameter.TopP -> SliderConfig(
        range = 0.00f..1.00f,
        step = 0.05f,
        minLabel = stringResource(R.string.parameter_topp_min_label),
        maxLabel = stringResource(R.string.parameter_topp_max_label)
    )

    MainGenerationParameter.MaxTokens, MainGenerationParameter.ContextTokens -> null
}

@Composable
private fun MainGenerationParameter.quickOptions(): List<NumericEditQuickOption> = when (this) {
    MainGenerationParameter.Temperature -> listOf(
        NumericEditQuickOption(stringResource(R.string.parameter_preset_precise), "0.20"),
        NumericEditQuickOption(stringResource(R.string.parameter_preset_balanced), "0.70"),
        NumericEditQuickOption(stringResource(R.string.parameter_preset_creative), "1.20")
    )

    MainGenerationParameter.TopP -> listOf(
        NumericEditQuickOption(stringResource(R.string.parameter_preset_topp_focused), "0.50"),
        NumericEditQuickOption(stringResource(R.string.parameter_preset_topp_balanced), "0.80"),
        NumericEditQuickOption(stringResource(R.string.parameter_preset_topp_rich), "0.95"),
        NumericEditQuickOption(stringResource(R.string.parameter_preset_topp_full), "1.00")
    )

    MainGenerationParameter.MaxTokens, MainGenerationParameter.ContextTokens -> {
        TokenPreset.entries.map { preset ->
            NumericEditQuickOption(
                label = preset.displayName,
                value = preset.value.toString()
            )
        }
    }
}

@Composable
private fun MainBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val hapticFeedback = LocalHapticFeedback.current
    val targetContentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val targetContainerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    } else {
        Color.Transparent
    }

    val contentColor by animateColorAsState(
        targetValue = targetContentColor,
        label = "bottomBarContentColor"
    )
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        label = "bottomBarContainerColor"
    )

    Box(
        modifier = modifier
            .clickable(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(containerColor, CircleShape)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SettingsPage(
    state: MainSettingsState,
    emit: MainUiIntent.() -> Unit
) {
    RpLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = 18.dp,
            top = 8.dp,
            end = 18.dp,
            bottom = 110.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.statusBarsPadding())
        }
        item {
            RpPageTitle(
                title = stringResource(R.string.setting_page_title),
                subtitle = stringResource(R.string.setting_subtitle)
            )
        }

        // ================= 1. 用户身份与人设 =================
        item {
            RpSectionHeader(title = stringResource(R.string.user_identity))
        }
        item { UserIdentityPanel(state.identityState, emit) }

        // ================= 2. 模型与推理服务 =================
        item {
            RpSectionHeader(
                title = stringResource(R.string.model_provider),
                action = stringResource(R.string.manage)
            ) { MainUiIntent.OpenProviderManager.emit() }
        }
        when (val providerState = state.providerState) {
            MainProviderSettingsState.Empty -> {
                item { EmptyProviderCard { MainUiIntent.OpenProviderManager.emit() } }
            }

            is MainProviderSettingsState.Available -> {
                items(providerState.providers) { provider ->
                    ProviderCard(
                        provider = provider,
                        selected = provider.id == providerState.selectedProviderId,
                        onClick = { MainUiIntent.SelectProvider(provider.id).emit() }
                    )
                }
                item { ParameterPanel(providerState.generationParametersState, emit) }
            }
        }

        // ================= 3. 提示词与上下文记忆 =================
        item {
            RpSectionHeader(title = stringResource(R.string.prompt_and_memory_section))
        }
        item { PromptPresetEntryCard { MainUiIntent.OpenPromptPreset.emit() } }
        item { PromptBehaviorPanel(state.promptBehaviorState, emit) }
        // 动作配置放在这一组最上面：它属于「我怎么对待角色」，
        // 埋在 SkillPanel 底下时主人根本找不到入口
        item { PokeActionsPanel(state.pokeActionState, emit) }
        item { WorldInfoBudgetPanel(state.worldInfoBudgetState, emit) }
        item { SummaryPanel(state.summaryState, emit) }
        item { ImageSendPanel(state.imageSendState, emit) }
        item { ImageGenPanel(state.imageGenState, emit) }
        item { SkillPanel(state.skillState, emit) }
        // 词典放在提示词这组的最后：写卡写着写着想知道某个占位符叫什么，往回翻到这一栏
        item { MacroDictionaryEntryCard() }

        // ================= 4. 数据与系统 =================
        item {
            RpSectionHeader(title = stringResource(R.string.system_and_data_section))
        }
        item { AppearancePanel(state.appearanceState, emit) }
        item { ChatDataManagementPanel(state.chatDataManagementState, emit) }
        item { TokenUsageEntryCard { emit(MainUiIntent.OpenTokenUsage) } }
        item { BackupRestoreEntryCard { emit(MainUiIntent.OpenBackupRestore) } }
        item { ScriptEditorEntryCard { emit(MainUiIntent.OpenScriptEditor) } }
        // 实验性模块独占一栏：这些开关改的是「角色怎么理解世界」，
        // 和普通设置混在一起会让人误以为它们同样可靠
        item { ExperimentalFeaturesPanel(state.experimentalState, emit) }
        item { DebugPanel(state.debugState, emit) }
        item { AboutEntryCard { emit(MainUiIntent.OpenAbout) } }
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun MainLayoutPreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            uiState = MainUiState.Normal(
                homeState = MainHomeState(
                    resourceState = MainHomeResourceState(
                        totalCharacters = 24,
                        totalWorldBooks = 7
                    ),
                    recentChatsState = MainRecentChatsState.Content(
                        sessionGroups = listOf(
                            MainChatSessionGroup(
                                characterId = "1",
                                characterName = "Luna",
                                sessions = listOf(
                                    MainChatSessionItem(
                                        id = "1",
                                        characterId = "1",
                                        characterName = "Luna",
                                        title = "Night train",
                                        preview = "The city lights recede beyond the window.",
                                        messageCount = 18,
                                        updatedAt = "06-15 21:30",
                                        latestTime = 1L
                                    )
                                )
                            )
                        )
                    ),
                    recentGroupChatsState = MainRecentGroupChatsState.Content(
                        sessions = listOf(
                            MainGroupChatSessionItem(
                                id = "1",
                                title = "Expedition team",
                                memberNames = "Luna, Aster, Rowan",
                                preview = "We should reach the ruins before sunrise.",
                                messageCount = 42,
                                updatedAt = "06-15 22:10",
                                latestTime = 2L
                            )
                        )
                    ),
                    recentStoriesState = MainRecentStoriesState.Content(
                        stories = listOf(
                            MainStoryItem(
                                id = 1L,
                                title = "Rain over the old city",
                                preview = "The station clock stopped at midnight.",
                                contentCharacterCount = 12840,
                                updatedAt = "06-15 22:30",
                                latestTime = 3L
                            )
                        )
                    ),
                    allRecentItems = listOf(
                        MainStoryItem(
                            id = 1L,
                            title = "Rain over the old city",
                            preview = "The station clock stopped at midnight.",
                            contentCharacterCount = 12840,
                            updatedAt = "06-15 22:30",
                            latestTime = 3L
                        ),
                        MainGroupChatSessionItem(
                            id = "1",
                            title = "Expedition team",
                            memberNames = "Luna, Aster, Rowan",
                            preview = "We should reach the ruins before sunrise.",
                            messageCount = 42,
                            updatedAt = "06-15 22:10",
                            latestTime = 2L
                        ),
                        MainChatSessionItem(
                            id = "1",
                            characterId = "1",
                            characterName = "Luna",
                            title = "Night train",
                            preview = "The city lights recede beyond the window.",
                            messageCount = 18,
                            updatedAt = "06-15 21:30",
                            latestTime = 1L
                        )
                    )
                ),
                settingsState = MainSettingsState(
                    appearanceState = MainAppearanceSettingsState(
                        themeMode = AppThemeMode.FollowSystem
                    ),
                    identityState = MainUserIdentityState(
                        userName = "You",
                        userDescription = "",
                        userDescriptionPreview = "",
                        avatarState = MainUserAvatarState.None
                    ),
                    providerState = MainProviderSettingsState.Empty,
                    promptBehaviorState = MainPromptBehaviorState(
                        providerPostProcessingState = MainProviderPostProcessingState.Unavailable,
                        exampleDialogueBehavior = ExampleDialogueBehavior.default,
                        includeThinkInContext = false,
                        maxHistoryMessages = 500,
                        contextTrimmingAlert = true,
                        streamEnabled = true
                    ),
                    worldInfoBudgetState = MainWorldInfoBudgetState(
                        budgetPercent = 25,
                        budgetCap = 0,
                        overflowAlert = true
                    ),
                    summaryState = MainSummarySettingsState(
                        autoSummaryEnabled = false,
                        triggerMessageCount = 20,
                        wordsLimit = 500,
                        maxMessagesPerRequest = 0,
                        responseTokens = 800,
                        injectionState = MainSummaryInjectionState.AfterMain
                    ),
                    debugState = MainDebugSettingsState(enabled = false)
                )
            ),
            emit = {}
        )
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun MainSettingsLayoutPreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            uiState = MainUiState.Normal(
                selectedPage = MainPage.Settings,
                homeState = MainHomeState(
                    resourceState = MainHomeResourceState(
                        totalCharacters = 24,
                        totalWorldBooks = 7
                    ),
                    recentChatsState = MainRecentChatsState.Empty,
                    recentGroupChatsState = MainRecentGroupChatsState.Empty,
                    recentStoriesState = MainRecentStoriesState.Empty
                ),
                settingsState = MainSettingsState(
                    appearanceState = MainAppearanceSettingsState(
                        themeMode = AppThemeMode.FollowSystem
                    ),
                    identityState = MainUserIdentityState(
                        userName = "KafuuNeko",
                        userDescription = "A traveler exploring AI worlds.",
                        userDescriptionPreview = "A traveler exploring AI worlds.",
                        avatarState = MainUserAvatarState.None
                    ),
                    providerState = MainProviderSettingsState.Available(
                        selectedProviderId = 1L,
                        providers = listOf(
                            MainProviderItem(
                                id = 1L,
                                name = "DeepSeek V3",
                                model = "deepseek-chat",
                                baseUrl = "https://api.deepseek.com/v1",
                                isEnabled = true
                            )
                        ),
                        generationParametersState = MainGenerationParametersState(
                            temperature = 0.8f,
                            topP = 0.95f,
                            maxTokens = 4096,
                            contextTokens = 32768
                        )
                    ),
                    promptBehaviorState = MainPromptBehaviorState(
                        providerPostProcessingState = MainProviderPostProcessingState.Available(
                            mode = PromptPostProcessingMode.Strict
                        ),
                        exampleDialogueBehavior = ExampleDialogueBehavior.Normal,
                        includeThinkInContext = true,
                        maxHistoryMessages = 500,
                        contextTrimmingAlert = true,
                        streamEnabled = true
                    ),
                    worldInfoBudgetState = MainWorldInfoBudgetState(
                        budgetPercent = 25,
                        budgetCap = 2048,
                        overflowAlert = true
                    ),
                    summaryState = MainSummarySettingsState(
                        autoSummaryEnabled = true,
                        triggerMessageCount = 20,
                        wordsLimit = 500,
                        maxMessagesPerRequest = 0,
                        responseTokens = 800,
                        injectionState = MainSummaryInjectionState.InChat(
                            depth = 4,
                            role = SummaryInjectionRole.System
                        )
                    ),
                    debugState = MainDebugSettingsState(enabled = true)
                )
            ),
            emit = {}
        )
    }
}

/** 首页的冒险卡片：点一下直接继续那一局。 */
