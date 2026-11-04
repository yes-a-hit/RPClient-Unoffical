package me.kafuuneko.rpclient.feature.llmprovideredit.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.llmprovideredit.model.CredentialEditMode
import me.kafuuneko.rpclient.feature.llmprovideredit.model.LLMProviderEditForm
import me.kafuuneko.rpclient.feature.llmprovideredit.model.ProviderPreset
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditDialogState
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditLoadState
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditMode
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditModelCatalogState
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditRequestExtensionsState
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditTestState
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditUiIntent
import me.kafuuneko.rpclient.feature.llmprovideredit.presentation.LLMProviderEditUiState
import me.kafuuneko.rpclient.libs.llm.catalog.LLMModelCatalogFailure
import me.kafuuneko.rpclient.libs.llm.catalog.model.LLMAvailableModel
import me.kafuuneko.rpclient.libs.llm.model.ImageInputSetting
import me.kafuuneko.rpclient.libs.llm.model.ImageTokenEstimatorType
import me.kafuuneko.rpclient.libs.llm.model.LLMProviderProtocol
import me.kafuuneko.rpclient.libs.llm.model.LLMProviderType
import me.kafuuneko.rpclient.libs.llm.model.LocalTokenEstimatorType
import me.kafuuneko.rpclient.libs.prompt.model.PromptPostProcessingMode
import me.kafuuneko.rpclient.libs.room.entity.MAX_TOKEN_ESTIMATE_RESERVE_PERCENT
import me.kafuuneko.rpclient.libs.room.entity.MIN_TOKEN_ESTIMATE_RESERVE_PERCENT
import me.kafuuneko.rpclient.model.TokenPreset
import me.kafuuneko.rpclient.ui.dialog.AppCodeEditorDialog
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.dialog.AppInputDialog
import me.kafuuneko.rpclient.ui.dialog.DialogBadgeTone
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpPanel as Panel
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.draggableScrollIndicator
import me.kafuuneko.rpclient.utils.JsonSyntaxTokenType
import me.kafuuneko.rpclient.utils.rememberDefaultJsonSyntaxColors
import me.kafuuneko.rpclient.utils.tokenizeJsonSyntax

@Composable
fun LLMProviderEditLayout(
    uiState: LLMProviderEditUiState,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    BackHandler(enabled = uiState is LLMProviderEditUiState.Normal) { LLMProviderEditUiIntent.Back.emit() }
    when (uiState) {
        LLMProviderEditUiState.None -> Unit
        is LLMProviderEditUiState.Finished -> LLMProviderEditLayout(uiState.previous) {}
        is LLMProviderEditUiState.Normal -> {
            LLMProviderEditNormal(uiState, emit)
            DialogSwitch(uiState.dialogState, emit)
        }
    }
}

@Composable
private fun LLMProviderEditNormal(
    state: LLMProviderEditUiState.Normal,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = if (state.mode == LLMProviderEditMode.Create) {
                stringResource(R.string.create_model_title)
            } else {
                stringResource(R.string.edit_model_title)
            },
            onBack = { LLMProviderEditUiIntent.Back.emit() },
            actions = {
                TopBarSaveButton(state, emit)
            }
        )
        RpLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                ),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RpPageTitle(
                    title = if (state.mode == LLMProviderEditMode.Create) {
                        stringResource(R.string.create_model_subtitle)
                    } else {
                        state.form.name.ifBlank { stringResource(R.string.model_provider_title) }
                    },
                    subtitle = stringResource(R.string.edit_model_subtitle)
                )
            }
            if (state.mode == LLMProviderEditMode.Create) {
                item {
                    ProviderPresetsSection(
                        selectedType = state.form.providerType,
                        selectedBaseUrl = state.form.baseUrl,
                        onSelectPreset = { preset ->
                            LLMProviderEditUiIntent.ApplyPresetTemplate(preset).emit()
                        }
                    )
                }
            }
            item { BasicPanel(state.form, state.modelCatalogState, emit) }
            item { ParameterPanel(state.form, emit) }
            item {
                CollapsibleAdvancedPanel(
                    form = state.form,
                    requestExtensionsState = state.requestExtensionsState,
                    showImageTokenEstimator = state.showImageTokenEstimator,
                    emit = emit
                )
            }
            item { TestPanel(state.testState, emit) }
        }
    }
}

@Composable
private fun ProviderPresetsSection(
    selectedType: LLMProviderType,
    selectedBaseUrl: String,
    onSelectPreset: (ProviderPreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.quick_presets),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(ProviderPreset.entries) { preset ->
                val isSelected = preset.providerType == selectedType &&
                        (preset == ProviderPreset.Custom || preset.baseUrl == selectedBaseUrl)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPreset(preset) },
                    label = {
                        Text(
                            if (preset == ProviderPreset.Custom) {
                                stringResource(R.string.preset_custom)
                            } else {
                                preset.displayName
                            }
                        )
                    },
                    leadingIcon = {
                        val icon = when (preset) {
                            ProviderPreset.DeepSeek -> Icons.Rounded.Memory
                            ProviderPreset.Gemini -> Icons.Rounded.AutoAwesome
                            ProviderPreset.Claude -> Icons.Rounded.Psychology
                            ProviderPreset.ChatGPT -> Icons.Rounded.SmartToy
                            ProviderPreset.OpenRouter -> Icons.Rounded.Hub
                            ProviderPreset.Grok -> Icons.Rounded.Bolt
                            ProviderPreset.Custom -> Icons.Rounded.Dns
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = 0.7f
                        ),
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun ParameterPanel(
    form: LLMProviderEditForm,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    Panel {
        RpSectionHeader(title = stringResource(R.string.generation_parameters))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FormTextField(
                label = stringResource(R.string.temperature),
                value = form.temperature,
                modifier = Modifier.weight(1f),
                enabled = form.sendTemperature,
                keyboardType = KeyboardType.Decimal,
                onChange = { LLMProviderEditUiIntent.ChangeTemperature(it).emit() }
            )
            FormTextField(
                label = stringResource(R.string.top_p),
                value = form.topP,
                modifier = Modifier.weight(1f),
                enabled = form.sendTopP,
                keyboardType = KeyboardType.Decimal,
                onChange = { LLMProviderEditUiIntent.ChangeTopP(it).emit() }
            )
        }

        ParameterSwitchRow(
            title = stringResource(R.string.provider_send_temperature),
            checked = form.sendTemperature,
            onCheckedChange = { LLMProviderEditUiIntent.ToggleSendTemperature(it).emit() }
        )
        ParameterSwitchRow(
            title = stringResource(R.string.provider_send_top_p),
            checked = form.sendTopP,
            onCheckedChange = { LLMProviderEditUiIntent.ToggleSendTopP(it).emit() }
        )

        TokenPresetField(
            label = stringResource(R.string.max_tokens),
            value = form.maxTokens,
            onChange = { LLMProviderEditUiIntent.ChangeMaxTokens(it).emit() }
        )
        TokenPresetField(
            label = stringResource(R.string.context) + " " + stringResource(R.string.tokens),
            value = form.contextTokens,
            onChange = { LLMProviderEditUiIntent.ChangeContextTokens(it).emit() }
        )
    }
}

@Composable
private fun TopBarSaveButton(
    state: LLMProviderEditUiState.Normal,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    TextButton(
        enabled = state.loadState !is LLMProviderEditLoadState.Saving,
        onClick = { LLMProviderEditUiIntent.SaveClick.emit() }
    ) {
        if (state.loadState is LLMProviderEditLoadState.Saving) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            when {
                state.loadState is LLMProviderEditLoadState.Saving -> stringResource(R.string.saving)
                state.mode == LLMProviderEditMode.Create -> stringResource(R.string.create)
                else -> stringResource(R.string.save)
            }
        )
    }
}

@Composable
private fun DialogSwitch(
    dialogState: LLMProviderEditDialogState,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    when (dialogState) {
        LLMProviderEditDialogState.None -> Unit
        LLMProviderEditDialogState.UnsavedChangesConfirm -> AppDangerDialog(
            onDismissRequest = { LLMProviderEditUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.unsaved_changes_title),
            message = stringResource(R.string.unsaved_changes_message),
            confirmText = stringResource(R.string.discard_changes),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { LLMProviderEditUiIntent.ConfirmDiscardChanges.emit() }
        )

        LLMProviderEditDialogState.ApiKeyEditor -> {
            var value by remember { mutableStateOf("") }
            AppInputDialog(
                onDismissRequest = { LLMProviderEditUiIntent.DismissDialog.emit() },
                title = stringResource(R.string.api_key_editor_title),
                subtitle = stringResource(R.string.credential_editor_privacy_note),
                value = value,
                onValueChange = { value = it },
                label = stringResource(R.string.api_key),
                password = true,
                confirmEnabled = value.isNotBlank(),
                onConfirm = { LLMProviderEditUiIntent.ConfirmApiKeyReplacement(value).emit() }
            )
        }

        is LLMProviderEditDialogState.CustomHeadersEditor -> {
            var value by remember(dialogState.initialValue) { mutableStateOf(dialogState.initialValue) }
            AppCodeEditorDialog(
                onDismissRequest = { LLMProviderEditUiIntent.DismissDialog.emit() },
                title = stringResource(R.string.custom_headers_editor_title),
                subtitle = stringResource(R.string.credential_editor_privacy_note),
                value = value,
                onValueChange = { value = it },
                confirmEnabled = value.isNotBlank(),
                onConfirm = {
                    LLMProviderEditUiIntent.ConfirmCustomHeadersReplacement(value).emit()
                }
            )
        }

        is LLMProviderEditDialogState.RequestBodyPatchEditor -> {
            var value by remember(dialogState.initialValue) { mutableStateOf(dialogState.initialValue) }
            AppCodeEditorDialog(
                onDismissRequest = { LLMProviderEditUiIntent.DismissDialog.emit() },
                title = stringResource(R.string.request_body_patch_editor_title),
                editorNote = stringResource(R.string.request_body_patch_editor_note),
                value = value,
                onValueChange = { value = it },
                onConfirm = { LLMProviderEditUiIntent.ConfirmRequestBodyPatch(value).emit() }
            )
        }

        is LLMProviderEditDialogState.ModelPicker -> ModelPickerDialog(
            state = dialogState,
            emit = emit
        )
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun LLMProviderEditLayoutPreview() {
    AppTheme(dynamicColor = false) {
        LLMProviderEditLayout(
            uiState = LLMProviderEditUiState.Normal(
                mode = LLMProviderEditMode.Create,
                form = LLMProviderEditForm(
                    name = "OpenRouter",
                    providerType = LLMProviderType.OpenRouter,
                    baseUrl = "https://openrouter.ai/api/v1",
                    model = "~anthropic/claude-sonnet-latest",
                    requestBodyPatchJson = "{\n  \"session_id\": \"\$rpclient.routing_session_id\",\n  \"reasoning\": {\n    \"effort\": \"low\"\n  }\n}"
                ),
                requestExtensionsState = LLMProviderEditRequestExtensionsState(
                    isOpenRouter = true
                )
            ),
            emit = {}
        )
    }
}
