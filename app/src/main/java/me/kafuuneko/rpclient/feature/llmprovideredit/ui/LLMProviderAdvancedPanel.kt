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
internal fun CollapsibleAdvancedPanel(
    form: LLMProviderEditForm,
    requestExtensionsState: LLMProviderEditRequestExtensionsState,
    showImageTokenEstimator: Boolean,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RpIconBubble(
                    icon = Icons.Rounded.Tune,
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.advanced_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.advanced_settings_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )

                    Text(
                        stringResource(R.string.provider_type),
                        style = MaterialTheme.typography.titleSmall
                    )
                    EnumChipRow(
                        values = LLMProviderType.entries,
                        selected = form.providerType,
                        label = { it.name },
                        onSelect = { LLMProviderEditUiIntent.ChangeProviderType(it).emit() }
                    )

                    Text(
                        stringResource(R.string.protocol),
                        style = MaterialTheme.typography.titleSmall
                    )
                    EnumChipRow(
                        values = LLMProviderProtocol.entries,
                        selected = form.protocol,
                        label = { it.name },
                        onSelect = { LLMProviderEditUiIntent.ChangeProtocol(it).emit() }
                    )

                    ParameterSwitchRow(
                        title = stringResource(R.string.provider_use_server_reported_usage),
                        checked = form.useServerReportedUsage,
                        onCheckedChange = {
                            LLMProviderEditUiIntent.ToggleUseServerReportedUsage(it).emit()
                        }
                    )
                    Text(
                        text = stringResource(
                            R.string.provider_use_server_reported_usage_description
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ModernCredentialControl(
                        title = stringResource(R.string.custom_headers_json),
                        hasExistingValue = form.hasExistingCustomHeaders,
                        editMode = form.customHeadersEditMode,
                        onEdit = { LLMProviderEditUiIntent.ShowCustomHeadersEditor.emit() },
                        onClear = { LLMProviderEditUiIntent.ClearCustomHeaders.emit() },
                        onKeepExisting = { LLMProviderEditUiIntent.KeepExistingCustomHeaders.emit() }
                    )

                    if (form.customHeadersJson.isNotBlank() && form.customHeadersJson.trim() != "{}" && form.customHeadersEditMode != CredentialEditMode.Clear) {
                        JsonSyntaxHighlightPreview(
                            title = stringResource(R.string.custom_headers_preview),
                            jsonString = form.customHeadersJson,
                            onEditClick = { LLMProviderEditUiIntent.ShowCustomHeadersEditor.emit() }
                        )
                    }

                    if (requestExtensionsState.isOpenRouter) {
                        ParameterSwitchRow(
                            title = stringResource(R.string.openrouter_preferred_provider),
                            checked = requestExtensionsState.usesPreferredProvider,
                            onCheckedChange = {
                                LLMProviderEditUiIntent.ToggleOpenRouterPreferredProvider(it).emit()
                            }
                        )
                        if (requestExtensionsState.usesPreferredProvider) {
                            FormTextField(
                                label = stringResource(R.string.openrouter_provider_slug),
                                value = requestExtensionsState.preferredProvider,
                                onChange = {
                                    LLMProviderEditUiIntent.ChangeOpenRouterPreferredProvider(it)
                                        .emit()
                                }
                            )
                            ParameterSwitchRow(
                                title = stringResource(R.string.openrouter_allow_fallbacks),
                                checked = requestExtensionsState.allowFallbacks,
                                onCheckedChange = {
                                    LLMProviderEditUiIntent.ToggleOpenRouterFallbacks(it).emit()
                                }
                            )
                        }
                        Text(
                            text = stringResource(R.string.openrouter_session_affinity_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    ModernRequestBodyPatchControl(
                        requestBodyPatchJson = form.requestBodyPatchJson,
                        onEdit = { LLMProviderEditUiIntent.ShowRequestBodyPatchDialog.emit() },
                        onResetToDefault = {
                            LLMProviderEditUiIntent.ConfirmRequestBodyPatch("{}").emit()
                        }
                    )

                    if (form.requestBodyPatchJson.isNotBlank() && form.requestBodyPatchJson.trim() != "{}") {
                        JsonSyntaxHighlightPreview(
                            title = stringResource(R.string.request_body_patch_preview),
                            jsonString = form.requestBodyPatchJson,
                            onEditClick = { LLMProviderEditUiIntent.ShowRequestBodyPatchDialog.emit() }
                        )
                    }

                    LocalTokenEstimatorSelector(
                        selected = form.localTokenEstimatorType,
                        onSelect = {
                            LLMProviderEditUiIntent.SelectLocalTokenEstimator(it).emit()
                        }
                    )

                    if (showImageTokenEstimator) {
                        ImageTokenEstimatorSelector(
                            selected = form.imageTokenEstimatorType,
                            onSelect = {
                                LLMProviderEditUiIntent.SelectImageTokenEstimator(it).emit()
                            }
                        )
                    }

                    TokenEstimateReserveSlider(
                        value = form.tokenEstimateReservePercent,
                        onChange = {
                            LLMProviderEditUiIntent.ChangeTokenEstimateReservePercent(it).emit()
                        }
                    )

                    Text(
                        text = stringResource(R.string.prompt_post_processing_provider_title),
                        style = MaterialTheme.typography.titleSmall
                    )
                    EnumChipRow(
                        values = PromptPostProcessingMode.entries,
                        selected = form.promptPostProcessingMode,
                        label = { it.name },
                        onSelect = {
                            LLMProviderEditUiIntent.SelectPostProcessingMode(it).emit()
                        }
                    )
                }
            }
        }
    }
}

/** 渲染模型配置独立的图片 Token 预估器选择项。 */

@Composable
internal fun ImageTokenEstimatorSelector(
    selected: ImageTokenEstimatorType,
    onSelect: (ImageTokenEstimatorType) -> Unit
) {
    val labels = mapOf(
        ImageTokenEstimatorType.Automatic to stringResource(R.string.local_token_estimator_automatic),
        ImageTokenEstimatorType.Generic to stringResource(R.string.image_estimator_generic),
        ImageTokenEstimatorType.OpenAiTile4o to stringResource(R.string.image_estimator_tile_4o),
        ImageTokenEstimatorType.OpenAiTile4oMini to stringResource(R.string.image_estimator_tile_mini),
        ImageTokenEstimatorType.OpenAiPatch41Mini to stringResource(R.string.image_estimator_patch_41),
        ImageTokenEstimatorType.OpenAiPatch54 to stringResource(R.string.image_estimator_patch_54),
        ImageTokenEstimatorType.ClaudeStandard to stringResource(R.string.image_estimator_claude_standard),
        ImageTokenEstimatorType.ClaudeHighResolution to stringResource(R.string.image_estimator_claude_high),
        ImageTokenEstimatorType.Gemini3 to stringResource(R.string.image_estimator_gemini)
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.image_token_estimator),
            style = MaterialTheme.typography.titleSmall
        )
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedCard(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = labels[selected] ?: selected.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                ImageTokenEstimatorType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                labels[type] ?: type.name,
                                fontWeight = if (type == selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        trailingIcon = if (type == selected) {
                            { Icon(Icons.Rounded.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null,
                        onClick = {
                            expanded = false
                            onSelect(type)
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun LocalTokenEstimatorSelector(
    selected: LocalTokenEstimatorType,
    onSelect: (LocalTokenEstimatorType) -> Unit
) {
    // 文案在可组合上下文预先解析，通用 Chip 组件只接收普通字符串映射。
    val labels = mapOf(
        LocalTokenEstimatorType.Automatic to
            stringResource(R.string.local_token_estimator_automatic),
        LocalTokenEstimatorType.Cl100kBase to
            stringResource(R.string.local_token_estimator_cl100k),
        LocalTokenEstimatorType.O200kBase to
            stringResource(R.string.local_token_estimator_o200k)
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.local_token_estimator),
            style = MaterialTheme.typography.titleSmall
        )
        EnumChipRow(
            values = LocalTokenEstimatorType.entries,
            selected = selected,
            label = { labels.getValue(it) },
            onSelect = onSelect
        )
        Text(
            text = stringResource(R.string.local_token_estimator_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun TokenEstimateReserveSlider(
    value: Int,
    onChange: (Int) -> Unit
) {
    val percent = value.coerceIn(
        MIN_TOKEN_ESTIMATE_RESERVE_PERCENT,
        MAX_TOKEN_ESTIMATE_RESERVE_PERCENT
    )
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.token_estimate_reserve),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = percent.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = MIN_TOKEN_ESTIMATE_RESERVE_PERCENT.toFloat()..
                    MAX_TOKEN_ESTIMATE_RESERVE_PERCENT.toFloat()
        )
        Text(
            text = stringResource(R.string.token_estimate_reserve_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun TokenPresetField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FormTextField(
            label = label,
            value = value,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Number,
            onChange = onChange
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TokenPreset.entries) { preset ->
                FilterChip(
                    selected = value.toIntOrNull() == preset.value,
                    onClick = { onChange(preset.value.toString()) },
                    label = { Text(preset.displayName) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
