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
internal fun ProfileBasicPanel(
    form: CharacterEditForm,
    emit: CharacterEditUiIntent.() -> Unit
) {
    Panel {
        RpSectionHeader(title = stringResource(R.string.basic_info))
        // 生成向导入口：用少量种子信息快速产出整张角色卡，再回到表单继续微调
        TextButton(
            onClick = { CharacterEditUiIntent.OpenCharacterWizard.emit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.character_wizard_entry),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        FormTextField(stringResource(R.string.name), form.name) {
            CharacterEditUiIntent.ChangeName(it).emit()
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormTextField(
                label = stringResource(R.string.character_creator),
                value = form.creator,
                modifier = Modifier.weight(1f),
                onChange = { CharacterEditUiIntent.ChangeCreator(it).emit() }
            )
            FormTextField(
                label = stringResource(R.string.character_version),
                value = form.characterVersion,
                modifier = Modifier.weight(1f),
                onChange = { CharacterEditUiIntent.ChangeCharacterVersion(it).emit() }
            )
        }
        FormTextField(
            label = stringResource(R.string.character_description),
            value = form.description,
            minLines = 3,
            showMacroBar = true,
            onExpandFullscreen = {
                CharacterEditUiIntent.ShowPromptEditor(CharacterPromptField.Description).emit()
            },
            onChange = { CharacterEditUiIntent.ChangeDescription(it).emit() }
        )
        FormTextField(
            label = stringResource(R.string.character_creator_notes),
            value = form.creatorNotes,
            minLines = 3,
            onChange = { CharacterEditUiIntent.ChangeCreatorNotes(it).emit() }
        )
    }
}

/**
 * 角色扩展档案面板：感情经历、亲密关系边界与身份补充。
 *
 * 这些字段保存在角色卡 extensions 的 rpclient_profile 命名空间内，随 V2 角色卡一起导入导出，
 * 因此面板不做独立持久化，全部通过表单 Intent 更新，由保存动作统一写回。
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProfileBackstoryPanel(
    form: CharacterEditForm,
    emit: CharacterEditUiIntent.() -> Unit
) {
    val profile = form.profile
    Panel {
        RpSectionHeader(title = stringResource(R.string.character_backstory_section))
        // 当前感情状态为单选，未设定时不会向 Prompt 注入该维度
        ProfileOptionRow(
            label = stringResource(R.string.character_relationship_status),
            options = RelationshipStatus.entries.toList(),
            selected = profile.relationshipStatus,
            labelOf = { stringResource(it.titleRes()) },
            onSelect = {
                CharacterEditUiIntent.ChangeRelationshipStatus(it).emit()
            }
        )
        // 三态布尔用于区分「明确没有」与「作者未填写」，避免模型自行补完经历
        ProfileTriStateRow(
            label = stringResource(R.string.character_has_romantic_history),
            value = profile.hasRomanticHistory,
            onValueChange = {
                CharacterEditUiIntent.ChangeProfileFlag(
                    CharacterProfileFlagField.HasRomanticHistory,
                    it
                ).emit()
            }
        )
        FormTextField(
            label = stringResource(R.string.character_romantic_history),
            value = profile.romanticHistory,
            minLines = 3,
            showMacroBar = true,
            macros = PLACEHOLDER_ONLY_MACROS,
            onChange = {
                CharacterEditUiIntent.ChangeProfileText(
                    CharacterProfileTextField.RomanticHistory,
                    it
                ).emit()
            }
        )
        ProfileTriStateRow(
            label = stringResource(R.string.character_has_sexual_history),
            value = profile.hasSexualHistory,
            onValueChange = {
                CharacterEditUiIntent.ChangeProfileFlag(
                    CharacterProfileFlagField.HasSexualHistory,
                    it
                ).emit()
            }
        )
        FormTextField(
            label = stringResource(R.string.character_sexual_history),
            value = profile.sexualHistory,
            minLines = 3,
            showMacroBar = true,
            macros = PLACEHOLDER_ONLY_MACROS,
            onChange = {
                CharacterEditUiIntent.ChangeProfileText(
                    CharacterProfileTextField.SexualHistory,
                    it
                ).emit()
            }
        )
        // 关系边界会直接进入 Prompt，模型扮演时需要显式遵守
        FormTextField(
            label = stringResource(R.string.character_boundaries),
            value = profile.boundaries,
            minLines = 2,
            showMacroBar = true,
            macros = PLACEHOLDER_ONLY_MACROS,
            onChange = {
                CharacterEditUiIntent.ChangeProfileText(
                    CharacterProfileTextField.Boundaries,
                    it
                ).emit()
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormTextField(
                label = stringResource(R.string.character_likes),
                value = profile.likes,
                modifier = Modifier.weight(1f),
                showMacroBar = true,
                macros = PLACEHOLDER_ONLY_MACROS,
                onChange = {
                    CharacterEditUiIntent.ChangeProfileText(
                        CharacterProfileTextField.Likes,
                        it
                    ).emit()
                }
            )
            FormTextField(
                label = stringResource(R.string.character_dislikes),
                value = profile.dislikes,
                modifier = Modifier.weight(1f),
                showMacroBar = true,
                macros = PLACEHOLDER_ONLY_MACROS,
                onChange = {
                    CharacterEditUiIntent.ChangeProfileText(
                        CharacterProfileTextField.Dislikes,
                        it
                    ).emit()
                }
            )
        }
    }
}

/** 扩展档案中的通用单选行：标签加一组可换行的选项芯片。 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun <T> ProfileOptionRow(
    label: String,
    options: List<T>,
    selected: T,
    labelOf: @Composable (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(labelOf(option)) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}

/** 扩展档案中的三态布尔行：未设定、有、无。 */

@Composable
internal fun ProfileTriStateRow(
    label: String,
    value: Boolean?,
    onValueChange: (Boolean?) -> Unit
) {
    ProfileOptionRow(
        label = label,
        options = listOf<Boolean?>(null, true, false),
        selected = value,
        labelOf = {
            stringResource(
                when (it) {
                    null -> R.string.profile_flag_unspecified
                    true -> R.string.profile_flag_yes
                    false -> R.string.profile_flag_no
                }
            )
        },
        onSelect = onValueChange
    )
}

/** 感情状态对应的界面文案资源。 */

@StringRes
internal fun RelationshipStatus.titleRes(): Int = when (this) {
    RelationshipStatus.Unspecified -> R.string.relationship_unspecified
    RelationshipStatus.Single -> R.string.relationship_single
    RelationshipStatus.Dating -> R.string.relationship_dating
    RelationshipStatus.Married -> R.string.relationship_married
    RelationshipStatus.Divorced -> R.string.relationship_divorced
    RelationshipStatus.Widowed -> R.string.relationship_widowed
    RelationshipStatus.Complicated -> R.string.relationship_complicated
}

@Composable
internal fun TagsPanel(
    tags: List<String>,
    emit: CharacterEditUiIntent.() -> Unit
) {    RpChipInputField(
        title = stringResource(R.string.character_tags),
        chips = tags,
        onChipsChanged = { CharacterEditUiIntent.SetTags(it).emit() },
        addLabel = stringResource(R.string.add_tag),
        placeholder = stringResource(R.string.tag_input_placeholder),
        editDialogTitle = stringResource(R.string.edit_tag_title)
    )
}
