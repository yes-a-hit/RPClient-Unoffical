package me.kafuuneko.rpclient.feature.script.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.script.presentation.ScriptEditorUiIntent
import me.kafuuneko.rpclient.feature.script.presentation.ScriptEditorUiState
import me.kafuuneko.rpclient.feature.script.presentation.ScriptLinkField
import me.kafuuneko.rpclient.feature.script.presentation.ScriptSceneRow
import me.kafuuneko.rpclient.ui.widgets.AppTopBar

@Composable
internal fun VariableCard(
    state: ScriptEditorUiState.Ready,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.script_variables),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " · " + state.variables.size,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(modifier = Modifier.weight(1f))
                TextButton(onClick = { ScriptEditorUiIntent.AddVariable.emit() }) {
                    Text(stringResource(R.string.script_variable_add))
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            // 剧本级总开关：关掉之后整块面板都不出现，
            // 单栏可见性仍然是每张卡自己的事
            SwitchRow(
                stringResource(R.string.script_dashboard_enabled),
                state.packShowDashboard
            ) { ScriptEditorUiIntent.ToggleDashboard(it).emit() }
            if (expanded) {
                if (state.variables.isEmpty()) {
                    Text(
                        text = stringResource(R.string.script_variable_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.variables.forEach { variable ->
                        VariableRow(variable, emit)
                    }
                }
            }
        }
    }
}

/**
 * 一个变量占多行，按类型只显示相关字段。
 *
 * 数字要上下限、开关要两态文案、文本要默认内容——
 * 全摆出来会让每一行都长得像表单，反而看不清在配什么。
 */

@Composable
internal fun VariableRow(
    variable: me.kafuuneko.rpclient.libs.script.ScriptVariable,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    var name by remember(variable.name) { mutableStateOf(variable.name) }
    var label by remember(variable.label) { mutableStateOf(variable.label) }
    var initial by remember(variable.initial) { mutableStateOf(variable.initial.toString()) }
    var min by remember(variable.min) { mutableStateOf(variable.min.toString()) }
    var max by remember(variable.max) { mutableStateOf(variable.max.toString()) }
    var visible by remember(variable.visible) { mutableStateOf(variable.visible) }
    var kind by remember(variable.kind) { mutableStateOf(variable.kind) }
    var text by remember(variable.text) { mutableStateOf(variable.text) }
    var onLabel by remember(variable.onLabel) { mutableStateOf(variable.onLabel) }
    var offLabel by remember(variable.offLabel) { mutableStateOf(variable.offLabel) }
    var group by remember(variable.group) { mutableStateOf(variable.group) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.script_variable_name)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { ScriptEditorUiIntent.DeleteVariable(variable.name).emit() }
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text(stringResource(R.string.script_variable_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 类型用一排筛选片，比下拉框少一次点击
        Text(
            text = stringResource(R.string.script_variable_kind),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            me.kafuuneko.rpclient.libs.script.ScriptVariableKind.entries.forEach { option ->
                FilterChip(
                    selected = kind == option,
                    onClick = { kind = option },
                    label = { Text(option.displayName(), style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        when (kind) {
            me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Number -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        initial to { v: String -> initial = v },
                        min to { v: String -> min = v },
                        max to { v: String -> max = v }
                    ).forEachIndexed { index, pair ->
                        OutlinedTextField(
                            value = pair.first,
                            onValueChange = {
                                pair.second(it.filter { c -> c.isDigit() || c == '-' })
                            },
                            label = {
                                Text(
                                    stringResource(
                                        when (index) {
                                            0 -> R.string.script_variable_initial
                                            1 -> R.string.script_variable_min
                                            else -> R.string.script_variable_max
                                        }
                                    )
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Switch -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = onLabel,
                        onValueChange = { onLabel = it },
                        label = { Text(stringResource(R.string.script_variable_on)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = offLabel,
                        onValueChange = { offLabel = it },
                        label = { Text(stringResource(R.string.script_variable_off)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = stringResource(R.string.script_variable_switch_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Text -> {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.script_variable_text)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Note -> {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.script_variable_note)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }

        OutlinedTextField(
            value = group,
            onValueChange = { group = it },
            label = { Text(stringResource(R.string.script_variable_group)) },
            placeholder = { Text(stringResource(R.string.script_variable_group_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        SwitchRow(stringResource(R.string.script_variable_visible), visible) { visible = it }
        TextButton(
            onClick = {
                ScriptEditorUiIntent.UpdateVariable(
                    originalName = variable.name,
                    name = name,
                    label = label,
                    initial = initial.toIntOrNull() ?: 0,
                    min = min.toIntOrNull() ?: 0,
                    max = max.toIntOrNull() ?: 100,
                    visible = visible,
                    kind = kind,
                    text = text,
                    onLabel = onLabel,
                    offLabel = offLabel
                ).emit()
            }
        ) {
            Text(stringResource(R.string.script_scene_save))
        }
    }
}

/** 类型的中文名。 */

internal fun me.kafuuneko.rpclient.libs.script.ScriptVariableKind.displayName(): String =
    when (this) {
        me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Number -> "数字"
        me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Switch -> "开关"
        me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Text -> "文本"
        me.kafuuneko.rpclient.libs.script.ScriptVariableKind.Note -> "线索"
    }


/** 事件摘要一行，够认出是哪条就行。 */
