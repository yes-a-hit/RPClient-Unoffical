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
internal fun PlacementPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None

    Panel {
        RpSectionHeader(title = stringResource(R.string.entry_position))

        PositionSelector(
            positionValue = form.position,
            enabled = enabled,
            onPositionSelected = { WorldBookEntryEditUiIntent.ChangePosition(it).emit() }
        )

        AnimatedVisibility(
            visible = form.position.trim() == LorebookEntry.POSITION_OUTLET.toString(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FormTextField(
                    label = stringResource(R.string.entry_outlet),
                    value = form.outletName,
                    onChange = { WorldBookEntryEditUiIntent.ChangeOutletName(it).emit() }
                )
                val macroPreview = "{{outlet::" + form.outletName.ifBlank { "name" } + "}}"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = stringResource(R.string.entry_outlet_preview, macroPreview),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // 注入角色
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.entry_role),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            RoleSelector(
                roleValue = form.role,
                enabled = enabled,
                onRoleSelected = { WorldBookEntryEditUiIntent.ChangeRole(it).emit() }
            )
        }

        // 排序与插入深度（使用标准全宽步进器，保障横向空间与可读性）
        RpNumberStepper(
            title = stringResource(R.string.entry_order),
            subtitle = stringResource(R.string.entry_order_desc),
            value = form.order,
            min = 0,
            max = 99999,
            step = 10,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeOrder(it).emit() }
        )

        RpNumberStepper(
            title = stringResource(R.string.entry_depth),
            subtitle = stringResource(R.string.entry_depth_desc),
            value = form.depth,
            min = 0,
            max = 999,
            step = 1,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeDepth(it).emit() }
        )
    }
}

/** 互斥包含组配置面板。 */

@Composable
internal fun InclusionGroupPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None
    val hasGroup = form.group.isNotBlank()

    Panel {
        RpSectionHeader(title = stringResource(R.string.entry_inclusion_group))
        Text(
            text = stringResource(R.string.entry_inclusion_group_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        FormTextField(
            label = stringResource(R.string.entry_group_name),
            value = form.group,
            onChange = { WorldBookEntryEditUiIntent.ChangeGroup(it).emit() }
        )

        AnimatedVisibility(
            visible = hasGroup,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RpNumberStepper(
                    title = stringResource(R.string.entry_group_weight),
                    subtitle = stringResource(R.string.entry_group_weight_desc),
                    value = form.groupWeight,
                    min = 0,
                    max = 9999,
                    step = 1,
                    enabled = enabled,
                    onValueChange = { WorldBookEntryEditUiIntent.ChangeGroupWeight(it).emit() }
                )

                RpSettingsGroup {
                    RpSettingsSwitchTile(
                        title = stringResource(R.string.entry_use_group_scoring),
                        subtitle = stringResource(R.string.entry_use_group_scoring_desc),
                        checked = form.useGroupScoring,
                        enabled = enabled,
                        onCheckedChange = { WorldBookEntryEditUiIntent.ChangeUseGroupScoring(it).emit() }
                    )
                    RpSettingsDivider(startIndent = false)
                    RpSettingsSwitchTile(
                        title = stringResource(R.string.entry_group_override),
                        subtitle = stringResource(R.string.entry_group_override_desc),
                        checked = form.groupOverride,
                        enabled = enabled,
                        onCheckedChange = { WorldBookEntryEditUiIntent.ChangeGroupOverride(it).emit() }
                    )
                }
            }
        }
    }
}

/** 轮数时序控制面板（Sticky / Cooldown / Delay）。 */

@Composable
internal fun TimingPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None

    Panel {
        RpSectionHeader(title = stringResource(R.string.entry_timing_recursion))
        Text(
            text = stringResource(R.string.entry_timing_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        RpNumberStepper(
            title = stringResource(R.string.entry_sticky),
            subtitle = stringResource(R.string.entry_sticky_desc),
            value = form.sticky,
            min = 0,
            max = 100,
            step = 1,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeSticky(it).emit() }
        )

        RpNumberStepper(
            title = stringResource(R.string.entry_cooldown),
            subtitle = stringResource(R.string.entry_cooldown_desc),
            value = form.cooldown,
            min = 0,
            max = 100,
            step = 1,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeCooldown(it).emit() }
        )

        RpNumberStepper(
            title = stringResource(R.string.entry_delay),
            subtitle = stringResource(R.string.entry_delay_desc),
            value = form.delay,
            min = 0,
            max = 100,
            step = 1,
            enabled = enabled,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeDelay(it).emit() }
        )
    }
}

/** 递归与匹配规则开关面板。 */

@Composable
internal fun RecursionAndMatchingPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None

    Panel {
        RpSectionHeader(title = stringResource(R.string.advanced_definition))

        RpSettingsGroup {
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_whole_words),
                checked = form.matchWholeWords == true,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeMatchWholeWords(it).emit() }
            )
            RpSettingsDivider(startIndent = false)
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_case_sensitive),
                checked = form.caseSensitive == true,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeCaseSensitive(it).emit() }
            )
            RpSettingsDivider(startIndent = false)
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_ignore_budget),
                checked = form.ignoreBudget,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeIgnoreBudget(it).emit() }
            )
            RpSettingsDivider(startIndent = false)
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_prevent_recursion),
                checked = form.preventRecursion,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangePreventRecursion(it).emit() }
            )
            RpSettingsDivider(startIndent = false)
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_delay_until_recursion),
                checked = form.delayUntilRecursion,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeDelayUntilRecursion(it).emit() }
            )
        }
    }
}

/** 扩展 JSON 代码编辑面板。 */

@Composable
internal fun ExtensionsJsonPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None
    Panel {
        RpJsonCodeEditorField(
            label = stringResource(R.string.extensions_json),
            value = form.extensionsJson,
            enabled = enabled,
            minHeight = 120.dp,
            maxHeight = 220.dp,
            onValueChange = { WorldBookEntryEditUiIntent.ChangeExtensionsJson(it).emit() }
        )
    }
}

// -------------------- 辅助选择器组件 --------------------

/** 角色选择胶囊组。 */
