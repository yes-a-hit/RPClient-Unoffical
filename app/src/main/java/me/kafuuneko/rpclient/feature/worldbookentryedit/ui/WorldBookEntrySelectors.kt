package me.kafuuneko.rpclient.feature.worldbookentryedit.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.worldbookentryedit.model.WorldBookEntryEditForm
import me.kafuuneko.rpclient.feature.worldbookentryedit.presentation.WorldBookEntryEditDialogState
import me.kafuuneko.rpclient.feature.worldbookentryedit.presentation.WorldBookEntryEditLoadState
import me.kafuuneko.rpclient.feature.worldbookentryedit.presentation.WorldBookEntryEditMode
import me.kafuuneko.rpclient.feature.worldbookentryedit.presentation.WorldBookEntryEditUiIntent
import me.kafuuneko.rpclient.feature.worldbookentryedit.presentation.WorldBookEntryEditUiState
import me.kafuuneko.rpclient.libs.room.entity.LorebookEntry
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppPromptEditorDialog
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.DEFAULT_PROMPT_MACROS
import me.kafuuneko.rpclient.ui.widgets.RpChipInputField
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpJsonCodeEditorField
import me.kafuuneko.rpclient.ui.widgets.RpMacroActionBar
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.rememberBoundTextFieldState
import me.kafuuneko.rpclient.ui.widgets.RpNumberStepper
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpPanel as Panel
import me.kafuuneko.rpclient.ui.widgets.RpPercentageSlider
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpSettingsDivider
import me.kafuuneko.rpclient.ui.widgets.RpSettingsGroup
import me.kafuuneko.rpclient.ui.widgets.RpSettingsSwitchTile
import me.kafuuneko.rpclient.utils.rememberPromptMacroOutputTransformation

@Composable
internal fun RoleSelector(
    roleValue: String,
    enabled: Boolean,
    onRoleSelected: (String) -> Unit
) {
    val currentRole = roleValue.trim().toIntOrNull() ?: LorebookEntry.ROLE_SYSTEM
    val roles = listOf(
        LorebookEntry.ROLE_SYSTEM to stringResource(R.string.role_system),
        LorebookEntry.ROLE_USER to stringResource(R.string.role_user),
        LorebookEntry.ROLE_ASSISTANT to stringResource(R.string.role_assistant)
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        roles.forEach { (roleId, label) ->
            FilterChip(
                selected = currentRole == roleId,
                onClick = { if (enabled) onRoleSelected(roleId.toString()) },
                enabled = enabled,
                shape = RoundedCornerShape(10.dp),
                leadingIcon = if (currentRole == roleId) {
                    {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

/** 次要逻辑选择器与动态人话释义。 */

@Composable
internal fun SelectiveLogicSelector(
    logicValue: String,
    enabled: Boolean,
    onLogicSelected: (String) -> Unit
) {
    val currentLogic = logicValue.trim().toIntOrNull() ?: LorebookEntry.LOGIC_AND_ANY
    val logicOptions = listOf(
        LorebookEntry.LOGIC_AND_ANY to stringResource(R.string.logic_and_any),
        LorebookEntry.LOGIC_AND_ALL to stringResource(R.string.logic_and_all),
        LorebookEntry.LOGIC_NOT_ANY to stringResource(R.string.logic_not_any),
        LorebookEntry.LOGIC_NOT_ALL to stringResource(R.string.logic_not_all)
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            logicOptions.forEach { (logicId, label) ->
                FilterChip(
                    selected = currentLogic == logicId,
                    onClick = { if (enabled) onLogicSelected(logicId.toString()) },
                    enabled = enabled,
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = if (currentLogic == logicId) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }

        Text(
            text = when (currentLogic) {
                LorebookEntry.LOGIC_AND_ALL -> stringResource(R.string.logic_and_all_helper)
                LorebookEntry.LOGIC_NOT_ANY -> stringResource(R.string.logic_not_any_helper)
                LorebookEntry.LOGIC_NOT_ALL -> stringResource(R.string.logic_not_all_helper)
                else -> stringResource(R.string.logic_and_any_helper)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

/** 插入位置 Exposed 下拉菜单选择器。 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PositionSelector(
    positionValue: String,
    enabled: Boolean,
    onPositionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentPosition = positionValue.trim().toIntOrNull() ?: LorebookEntry.POSITION_AT_DEPTH

    val positions = listOf(
        LorebookEntry.POSITION_AT_DEPTH to stringResource(R.string.position_at_depth),
        LorebookEntry.POSITION_BEFORE to stringResource(R.string.position_before_char),
        LorebookEntry.POSITION_AFTER to stringResource(R.string.position_after_char),
        LorebookEntry.POSITION_AN_TOP to stringResource(R.string.position_an_top),
        LorebookEntry.POSITION_AN_BOTTOM to stringResource(R.string.position_an_bottom),
        LorebookEntry.POSITION_EXAMPLE_TOP to stringResource(R.string.position_example_top),
        LorebookEntry.POSITION_EXAMPLE_BOTTOM to stringResource(R.string.position_example_bottom),
        LorebookEntry.POSITION_OUTLET to stringResource(R.string.position_outlet)
    )

    val currentLabel = positions.firstOrNull { it.first == currentPosition }?.second
        ?: stringResource(R.string.position_at_depth)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = currentLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.entry_position)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            positions.forEach { (posId, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onPositionSelected(posId.toString())
                        expanded = false
                    }
                )
            }
        }
    }
}

/** 底部操作按钮栏。 */

@Composable
internal fun FormTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = if (minLines > 1) minLines.coerceAtLeast(6) else 1,
    singleLine: Boolean = minLines == 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    outputTransformation: OutputTransformation = rememberPromptMacroOutputTransformation(),
    showMacroBar: Boolean = false,
    macros: List<String> = DEFAULT_PROMPT_MACROS,
    onExpandFullscreen: (() -> Unit)? = null,
    onChange: (String) -> Unit
) {
    val textFieldState = rememberBoundTextFieldState(value, onChange)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RpScrollableOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (maxLines > 1) Modifier.heightIn(max = 240.dp) else Modifier),
            state = textFieldState,
            label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            minLines = minLines,
            maxLines = maxLines.coerceAtLeast(minLines),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            outputTransformation = outputTransformation,
            shape = RoundedCornerShape(12.dp)
        )
        if (showMacroBar) {
            RpMacroActionBar(
                macros = macros,
                onInsertMacro = { macro ->
                    val currentText = textFieldState.text.toString()
                    val selection = textFieldState.selection
                    val start = selection.min.coerceIn(0, currentText.length)
                    val end = selection.max.coerceIn(0, currentText.length)
                    val before = currentText.substring(0, start)
                    val insertContent = if (macro == "<START>") {
                        if (before.isNotEmpty() && !before.endsWith("\n")) "\n<START>\n" else "<START>\n"
                    } else {
                        macro
                    }
                    val newCursorPos = start + insertContent.length
                    textFieldState.edit {
                        replace(start, end, insertContent)
                        this.selection = TextRange(newCursorPos)
                    }
                },
                onFullscreenClick = onExpandFullscreen
            )
        }
    }
}
