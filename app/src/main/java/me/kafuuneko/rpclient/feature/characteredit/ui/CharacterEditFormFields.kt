package me.kafuuneko.rpclient.feature.characteredit.ui
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.characteredit.model.CharacterEditForm
import me.kafuuneko.rpclient.feature.characteredit.model.CharacterLorebookItem
import me.kafuuneko.rpclient.feature.characteredit.model.CharacterProviderItem
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterEditDialogState
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterEditLoadState
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterEditMode
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterPromptField
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterEditUiIntent
import me.kafuuneko.rpclient.feature.characteredit.presentation.CharacterEditUiState
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.dialog.AppPromptEditorDialog
import me.kafuuneko.rpclient.ui.dialog.DialogBadgeTone
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.CharacterAccentColors
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.DEFAULT_PROMPT_MACROS
import me.kafuuneko.rpclient.ui.widgets.DIALOGUE_EXAMPLE_MACROS
import me.kafuuneko.rpclient.ui.widgets.PLACEHOLDER_ONLY_MACROS
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.RpChipInputField
import me.kafuuneko.rpclient.ui.widgets.RpJsonCodeEditorField
import me.kafuuneko.rpclient.ui.widgets.RpMacroActionBar
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.libs.character.CharacterProfileFlagField
import me.kafuuneko.rpclient.libs.character.CharacterProfileTextField
import me.kafuuneko.rpclient.libs.character.RelationshipStatus
import me.kafuuneko.rpclient.ui.widgets.rememberBoundTextFieldState
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpPanel as Panel
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.utils.rememberPromptMacroOutputTransformation

@Composable
internal fun CharacterPromptField.editorTitle(): String {
    return when (this) {
        CharacterPromptField.Description -> stringResource(R.string.character_description)
        CharacterPromptField.Personality -> stringResource(R.string.character_personality)
        CharacterPromptField.Scenario -> stringResource(R.string.character_scenario)
        is CharacterPromptField.FirstMessage -> stringResource(
            R.string.character_first_message_index,
            index + 1
        )
        CharacterPromptField.DialogueExamples -> stringResource(R.string.character_examples_of_dialogue)
        CharacterPromptField.SystemPrompt -> stringResource(R.string.character_main_prompt_override)
        CharacterPromptField.PostHistoryInstructions -> stringResource(
            R.string.character_post_history_instructions
        )
        CharacterPromptField.DepthPrompt -> stringResource(R.string.character_note)
        is CharacterPromptField.AlternateGreeting -> stringResource(
            R.string.character_alternate_greeting_index,
            index + 1
        )
    }
}

internal fun CharacterPromptField.availableMacros(): List<String> {
    return if (this == CharacterPromptField.DialogueExamples) {
        DIALOGUE_EXAMPLE_MACROS
    } else {
        DEFAULT_PROMPT_MACROS
    }
}

@Composable
internal fun FormTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = if (minLines > 1) minLines.coerceAtLeast(6) else 1,
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
                .then(if (maxLines > 1) Modifier.heightIn(max = 220.dp) else Modifier),
            state = textFieldState,
            label = { Text(label) },
            minLines = minLines,
            maxLines = maxLines.coerceAtLeast(minLines),
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

@Composable
internal fun ListTextField(
    label: String,
    value: String,
    minLines: Int = 1,
    maxLines: Int = minLines.coerceAtLeast(4),
    leadingIcon: @Composable (() -> Unit)? = null,
    outputTransformation: OutputTransformation = rememberPromptMacroOutputTransformation(),
    showMacroBar: Boolean = false,
    macros: List<String> = DEFAULT_PROMPT_MACROS,
    onExpandFullscreen: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val textFieldState = rememberBoundTextFieldState(value, onValueChange)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RpScrollableOutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .then(if (maxLines > 1) Modifier.heightIn(max = 220.dp) else Modifier),
                state = textFieldState,
                label = { Text(label) },
                minLines = minLines,
                maxLines = maxLines.coerceAtLeast(minLines),
                outputTransformation = outputTransformation,
                shape = RoundedCornerShape(12.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.delete))
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterProviderSelector(
    selectedId: Long,
    availableProviders: List<CharacterProviderItem>,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProvider = availableProviders.firstOrNull { it.id == selectedId }
    val selectedName = selectedProvider?.displayName()
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
            label = { Text(stringResource(R.string.character_model_config)) },
            supportingText = { Text(stringResource(R.string.character_model_config_helper)) },
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
            availableProviders.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.displayName()) },
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
internal fun CharacterProviderItem.displayName(): String {
    val displayName = name.ifBlank { stringResource(R.string.unnamed_model_config) }
    return if (isEnabled) {
        displayName
    } else {
        stringResource(R.string.disabled_model_config_format, displayName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LorebookSelector(
    selectedId: Long,
    availableLorebooks: List<CharacterLorebookItem>,
    onSelect: (Long) -> Unit,
    onManage: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = if (selectedId == 0L) stringResource(R.string.none)
    else availableLorebooks.find { it.id == selectedId }?.name ?: stringResource(R.string.unknown_world_book)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.associated_world_book)) },
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
                    text = { Text(stringResource(R.string.none)) },
                    onClick = {
                        onSelect(0L)
                        expanded = false
                    }
                )
                availableLorebooks.forEach { lorebook ->
                    DropdownMenuItem(
                        text = { Text(lorebook.name.ifBlank { stringResource(R.string.untitled) }) },
                        onClick = {
                            onSelect(lorebook.id)
                            expanded = false
                        }
                    )
                }
            }
        }
        OutlinedButton(
            onClick = onManage,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Text(stringResource(R.string.manage_world_books))
        }
    }
}

@Composable
internal fun CharacterNoteRoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole = selectedRole.trim().toIntOrNull() ?: 0
    val roles = listOf(
        0 to stringResource(R.string.role_system),
        1 to stringResource(R.string.role_user),
        2 to stringResource(R.string.role_assistant)
    )
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        roles.forEach { (roleValue, label) ->
            FilterChip(
                selected = currentRole == roleValue,
                onClick = { onRoleSelected(roleValue.toString()) },
                shape = RoundedCornerShape(10.dp),
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

@Composable
internal fun AvatarPreview(
    avatarText: String,
    avatarColor: Color,
    image: ImageBitmap?,
    size: Int
) {
    if (image == null) {
        RpAvatar(
            text = avatarText,
            color = avatarColor,
            modifier = Modifier.size(size.dp),
            shape = RoundedCornerShape(20.dp)
        )
    } else {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

internal fun CharacterEditForm.avatarText(): String {
    return name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}

internal fun CharacterEditForm.avatarColor(): Color {
    val seed = if (id == 0L) name.hashCode().toLong() else id
    return CharacterAccentColors[kotlin.math.abs(seed % CharacterAccentColors.size).toInt()]
}

internal fun String.compactPreview(limit: Int = 240): String {
    val compact = replace(Regex("\\s+"), " ").trim()
    return if (compact.length <= limit) compact else compact.take(limit).trimEnd() + "..."
}
