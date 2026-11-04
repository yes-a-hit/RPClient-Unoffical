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
internal fun DialogueDefinitionPanel(
    form: CharacterEditForm,
    emit: CharacterEditUiIntent.() -> Unit
) {
    val personalityLabel = stringResource(R.string.character_personality)
    val scenarioLabel = stringResource(R.string.character_scenario)

    Panel {
        RpSectionHeader(title = stringResource(R.string.character_definition))
        FormTextField(
            label = personalityLabel,
            value = form.personality,
            minLines = 4,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.Personality).emit()
            },
            onChange = { CharacterEditUiIntent.ChangePersonality(it).emit() }
        )
        FormTextField(
            label = scenarioLabel,
            value = form.scenario,
            minLines = 4,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.Scenario).emit()
            },
            onChange = { CharacterEditUiIntent.ChangeScenario(it).emit() }
        )
    }
}

@Composable
internal fun FirstMessagesPanel(
    firstMessages: List<String>,
    emit: CharacterEditUiIntent.() -> Unit
) {
    Panel {
        RpSectionHeader(
            title = stringResource(R.string.character_first_messages),
            action = stringResource(R.string.add),
            onAction = { CharacterEditUiIntent.AddFirstMessage.emit() }
        )
        firstMessages.forEachIndexed { index, message ->
            val label = stringResource(R.string.character_first_message_index, index + 1)
            ListTextField(
                label = label,
                value = message,
                minLines = 3,
                showMacroBar = true,
                onExpandFullscreen = {
                    CharacterEditUiIntent.ShowPromptEditor(
                        CharacterPromptField.FirstMessage(index)
                    ).emit()
                },
                onValueChange = { CharacterEditUiIntent.ChangeFirstMessage(index, it).emit() },
                onDelete = { CharacterEditUiIntent.DeleteFirstMessage(index).emit() }
            )
        }
    }
}

@Composable
internal fun DialogueExamplesPanel(
    form: CharacterEditForm,
    emit: CharacterEditUiIntent.() -> Unit
) {
    val dialogueLabel = stringResource(R.string.character_examples_of_dialogue)

    Panel {
        RpSectionHeader(title = stringResource(R.string.character_dialogue))
        FormTextField(
            label = dialogueLabel,
            value = form.examplesOfDialogue,
            minLines = 5,
            showMacroBar = true,
            macros = DIALOGUE_EXAMPLE_MACROS,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.DialogueExamples).emit()
            },
            onChange = { CharacterEditUiIntent.ChangeExamplesOfDialogue(it).emit() }
        )
    }
}

@Composable
internal fun AdvancedPanel(
    form: CharacterEditForm,
    availableLorebooks: List<CharacterLorebookItem>,
    availableProviders: List<CharacterProviderItem>,
    emit: CharacterEditUiIntent.() -> Unit
) {
    var isExtensionsExpanded by rememberSaveable(form.id) { mutableStateOf(false) }

    val systemPromptLabel = stringResource(R.string.character_main_prompt_override)
    val postHistoryLabel = stringResource(R.string.character_post_history_instructions)
    val noteLabel = stringResource(R.string.character_note)

    Panel {
        RpSectionHeader(title = stringResource(R.string.advanced_definition))
        CharacterProviderSelector(
            selectedId = form.llmProviderId,
            availableProviders = availableProviders,
            onSelect = { CharacterEditUiIntent.SelectLLMProvider(it).emit() }
        )
        LorebookSelector(
            selectedId = form.characterLorebookId,
            availableLorebooks = availableLorebooks,
            onSelect = { CharacterEditUiIntent.UpdateCharacterLorebook(it).emit() },
            onManage = { CharacterEditUiIntent.OpenWorldBookManager.emit() }
        )
        FormTextField(
            label = systemPromptLabel,
            value = form.systemPrompt,
            minLines = 4,
            maxLines = 8,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.SystemPrompt).emit()
            },
            onChange = { CharacterEditUiIntent.ChangeSystemPrompt(it).emit() }
        )
        FormTextField(
            label = postHistoryLabel,
            value = form.postHistoryInstructions,
            minLines = 4,
            maxLines = 8,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(
                    CharacterPromptField.PostHistoryInstructions
                ).emit()
            },
            onChange = { CharacterEditUiIntent.ChangePostHistoryInstructions(it).emit() }
        )
        FormTextField(
            label = noteLabel,
            value = form.depthPromptPrompt,
            minLines = 4,
            maxLines = 8,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.DepthPrompt).emit()
            },
            onChange = { CharacterEditUiIntent.ChangeDepthPromptPrompt(it).emit() }
        )
        FormTextField(
            label = stringResource(R.string.character_note_depth),
            value = form.depthPromptDepth,
            keyboardType = KeyboardType.Number,
            onChange = { CharacterEditUiIntent.ChangeDepthPromptDepth(it).emit() }
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.entry_role),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            CharacterNoteRoleSelector(
                selectedRole = form.depthPromptRole,
                onRoleSelected = { CharacterEditUiIntent.ChangeDepthPromptRole(it).emit() }
            )
        }
        RpSectionHeader(
            title = stringResource(R.string.character_alternate_greetings),
            action = stringResource(R.string.add),
            onAction = { CharacterEditUiIntent.AddAlternateGreeting.emit() }
        )
        form.alternateGreetings.forEachIndexed { index, greeting ->
            val label = stringResource(R.string.character_alternate_greeting_index, index + 1)
            ListTextField(
                label = label,
                value = greeting,
                minLines = 3,
                maxLines = 6,
                showMacroBar = true,
                onExpandFullscreen = {
                    CharacterEditUiIntent.ShowPromptEditor(
                        CharacterPromptField.AlternateGreeting(index)
                    ).emit()
                },
                onValueChange = { CharacterEditUiIntent.ChangeAlternateGreeting(index, it).emit() },
                onDelete = { CharacterEditUiIntent.DeleteAlternateGreeting(index).emit() }
            )
        }
        RawExtensionsPanel(
            value = form.extensionsJson,
            expanded = isExtensionsExpanded,
            onExpandedChange = { isExtensionsExpanded = it },
            onChange = { CharacterEditUiIntent.ChangeExtensionsJson(it).emit() }
        )
    }
}

@Composable
internal fun RawExtensionsPanel(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.extensions_json), style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = stringResource(R.string.extensions_json_size, value.length),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
                    )
                }
                TextButton(onClick = { onExpandedChange(!expanded) }) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.Edit,
                        contentDescription = null
                    )
                    Text(if (expanded) stringResource(R.string.hide) else stringResource(R.string.edit))
                }
            }
            if (expanded) {
                RpJsonCodeEditorField(
                    value = value,
                    onValueChange = onChange,
                    minHeight = 130.dp,
                    maxHeight = 240.dp
                )
            } else {
                Text(
                    text = value.ifBlank { "{}" }.compactPreview(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
