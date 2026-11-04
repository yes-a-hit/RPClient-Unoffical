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
internal fun TestPanel(
    testState: LLMProviderEditTestState,
    emit: LLMProviderEditUiIntent.() -> Unit
) {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RpIconBubble(
                icon = when (testState) {
                    is LLMProviderEditTestState.Failed -> Icons.Rounded.ErrorOutline
                    is LLMProviderEditTestState.Success -> Icons.Rounded.CloudDone
                    else -> Icons.Rounded.PlayArrow
                },
                contentColor = when (testState) {
                    is LLMProviderEditTestState.Failed -> MaterialTheme.colorScheme.error
                    is LLMProviderEditTestState.Success -> Color(0xFF10B981)
                    else -> MaterialTheme.colorScheme.primary
                },
                containerColor = when (testState) {
                    is LLMProviderEditTestState.Failed -> MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.45f
                    )

                    is LLMProviderEditTestState.Success -> Color(0xFF10B981).copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    stringResource(R.string.model_test),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when (testState) {
                        LLMProviderEditTestState.None -> stringResource(R.string.send_short_message)
                        LLMProviderEditTestState.Testing -> stringResource(R.string.testing)
                        is LLMProviderEditTestState.Success -> testState.message
                            ?: stringResource(R.string.test_success)
                        LLMProviderEditTestState.Failed -> stringResource(R.string.test_failed)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (testState) {
                        is LLMProviderEditTestState.Failed -> MaterialTheme.colorScheme.error
                        is LLMProviderEditTestState.Success -> Color(0xFF059669)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    }
                )
            }
            OutlinedButton(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    if (testState is LLMProviderEditTestState.Testing) {
                        LLMProviderEditUiIntent.CancelTest.emit()
                    } else {
                        LLMProviderEditUiIntent.TestClick.emit()
                    }
                }
            ) {
                val isTesting = testState is LLMProviderEditTestState.Testing
                Icon(
                    imageVector = if (isTesting) Icons.Rounded.Close else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(if (isTesting) R.string.cancel else R.string.test),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(if (isTesting) R.string.cancel else R.string.test))
            }
        }
    }
}

@Composable
internal fun JsonSyntaxHighlightPreview(
    title: String,
    jsonString: String,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 180.dp,
    onEditClick: (() -> Unit)? = null
) {
    if (jsonString.isBlank()) return

    val colors = rememberDefaultJsonSyntaxColors()
    val verticalScrollState = rememberScrollState()
    val annotatedText = remember(jsonString, colors) {
        buildAnnotatedString {
            append(jsonString)
            tokenizeJsonSyntax(jsonString).forEach { token ->
                val style = when (token.type) {
                    JsonSyntaxTokenType.Key -> SpanStyle(
                        color = colors.key,
                        fontWeight = FontWeight.SemiBold
                    )

                    JsonSyntaxTokenType.String -> SpanStyle(color = colors.string)
                    JsonSyntaxTokenType.Number -> SpanStyle(color = colors.number)
                    JsonSyntaxTokenType.Literal -> SpanStyle(
                        color = colors.literal,
                        fontWeight = FontWeight.SemiBold
                    )

                    JsonSyntaxTokenType.Punctuation -> SpanStyle(color = colors.punctuation)
                }
                addStyle(style = style, start = token.start, end = token.end)
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (onEditClick != null) {
                    Text(
                        text = stringResource(R.string.edit),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .draggableScrollIndicator(verticalScrollState)
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(verticalScrollState)
                    .padding(12.dp)
            ) {
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
internal fun ModernRequestBodyPatchControl(
    requestBodyPatchJson: String,
    onEdit: () -> Unit,
    onResetToDefault: () -> Unit
) {
    val hasCustomPatch = remember(requestBodyPatchJson) {
        val trimmed = requestBodyPatchJson.trim()
        trimmed.isNotBlank() && trimmed != "{}"
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.request_body_patch_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = if (hasCustomPatch) {
                    stringResource(R.string.request_body_patch_status_custom)
                } else {
                    stringResource(R.string.request_body_patch_status_default)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = stringResource(R.string.request_body_patch_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.edit))
                }
                if (hasCustomPatch) {
                    TextButton(onClick = onResetToDefault) {
                        Text(
                            stringResource(R.string.reset),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
