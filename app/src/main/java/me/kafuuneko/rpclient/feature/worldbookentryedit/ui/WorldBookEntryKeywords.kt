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
internal fun PrimaryKeywordsPanel(
    form: WorldBookEntryEditForm,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = !form.constant
    RpChipInputField(
        title = stringResource(R.string.primary_keywords),
        subtitle = if (form.constant) {
            stringResource(R.string.primary_keywords_constant_desc)
        } else {
            stringResource(R.string.primary_keywords_conditional_desc)
        },
        chips = form.keywords,
        icon = Icons.Rounded.Tag,
        enabled = enabled,
        onChipsChanged = { WorldBookEntryEditUiIntent.SetKeywords(it).emit() },
        addLabel = stringResource(R.string.add_keyword),
        placeholder = stringResource(R.string.keyword_input_placeholder),
        editDialogTitle = stringResource(R.string.edit_keyword_title),
        accentColor = MaterialTheme.colorScheme.primary
    )
}

/** 次要关键词与判定逻辑因果一体化卡片。 */

@Composable
internal fun SecondaryKeywordsAndLogicPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None && !form.constant

    Panel {
        RpSectionHeader(title = stringResource(R.string.secondary_keywords))

        RpChipInputField(
            title = stringResource(R.string.secondary_keywords),
            chips = form.secondaryKeywords,
            icon = Icons.Rounded.FilterList,
            enabled = enabled,
            onChipsChanged = { WorldBookEntryEditUiIntent.SetSecondaryKeywords(it).emit() },
            addLabel = stringResource(R.string.add_keyword),
            placeholder = stringResource(R.string.keyword_input_placeholder),
            editDialogTitle = stringResource(R.string.edit_keyword_title),
            accentColor = MaterialTheme.colorScheme.secondary
        )

        // 判定逻辑选择器
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.entry_logic),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            SelectiveLogicSelector(
                logicValue = form.selectiveLogic,
                enabled = enabled,
                onLogicSelected = { WorldBookEntryEditUiIntent.ChangeSelectiveLogic(it).emit() }
            )
        }
    }
}

/** 触发概率滑块面板。 */

@Composable
internal fun ProbabilityPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    Panel {
        RpPercentageSlider(
            title = stringResource(R.string.entry_probability),
            value = form.probability.toIntOrNull() ?: 100,
            helper = stringResource(R.string.entry_probability_helper),
            enabled = loadState == WorldBookEntryEditLoadState.None,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeProbability(it.toString()).emit() }
        )
    }
}

/** 扫描历史深度与扫描源多选矩阵面板。 */

@Composable
internal fun ScanDepthAndScopePanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None

    Panel {
        RpSectionHeader(title = stringResource(R.string.entry_scan_depth))
        RpNumberStepper(
            title = stringResource(R.string.entry_scan_depth),
            subtitle = stringResource(R.string.entry_scan_depth_desc),
            value = form.scanDepth,
            min = 0,
            max = 200,
            step = 1,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeScanDepth(it).emit() }
        )

        RpSectionHeader(
            title = stringResource(R.string.entry_matching_scope),
            action = stringResource(R.string.entry_matching_scope_select_all),
            onAction = {
                WorldBookEntryEditUiIntent.ChangeMatchCharacterDescription(true).emit()
                WorldBookEntryEditUiIntent.ChangeMatchCharacterPersonality(true).emit()
                WorldBookEntryEditUiIntent.ChangeMatchCharacterDepthPrompt(true).emit()
                WorldBookEntryEditUiIntent.ChangeMatchScenario(true).emit()
                WorldBookEntryEditUiIntent.ChangeMatchPersonaDescription(true).emit()
                WorldBookEntryEditUiIntent.ChangeMatchCreatorNotes(true).emit()
            }
        )

        MatchingScopeMatrix(form = form, enabled = enabled, emit = emit)
    }
}

/** 扫描源 FilterChip 多选胶囊矩阵。 */

@Composable
internal fun MatchingScopeMatrix(
    form: WorldBookEntryEditForm,
    enabled: Boolean,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val scopes = listOf(
        ScopeChipItem(stringResource(R.string.entry_match_description), form.matchCharacterDescription) {
            WorldBookEntryEditUiIntent.ChangeMatchCharacterDescription(it).emit()
        },
        ScopeChipItem(stringResource(R.string.entry_match_personality), form.matchCharacterPersonality) {
            WorldBookEntryEditUiIntent.ChangeMatchCharacterPersonality(it).emit()
        },
        ScopeChipItem(stringResource(R.string.entry_match_character_note), form.matchCharacterDepthPrompt) {
            WorldBookEntryEditUiIntent.ChangeMatchCharacterDepthPrompt(it).emit()
        },
        ScopeChipItem(stringResource(R.string.entry_match_scenario), form.matchScenario) {
            WorldBookEntryEditUiIntent.ChangeMatchScenario(it).emit()
        },
        ScopeChipItem(stringResource(R.string.entry_match_persona_description), form.matchPersonaDescription) {
            WorldBookEntryEditUiIntent.ChangeMatchPersonaDescription(it).emit()
        },
        ScopeChipItem(stringResource(R.string.entry_match_creator_notes), form.matchCreatorNotes) {
            WorldBookEntryEditUiIntent.ChangeMatchCreatorNotes(it).emit()
        }
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        scopes.forEach { item ->
            FilterChip(
                selected = item.selected,
                onClick = { if (enabled) item.onToggle(!item.selected) },
                enabled = enabled,
                shape = RoundedCornerShape(10.dp),
                leadingIcon = if (item.selected) {
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
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

/** 扫描源选项数据封装。 */

internal data class ScopeChipItem(
    val label: String,
    val selected: Boolean,
    val onToggle: (Boolean) -> Unit
)

// -------------------- Tab 3: 高级时序 --------------------

/** 插入位置与模型角色面板。 */
