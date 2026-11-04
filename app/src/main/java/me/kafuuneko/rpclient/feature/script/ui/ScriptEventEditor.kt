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

internal fun me.kafuuneko.rpclient.libs.script.ScriptEvent.summary(): String {
    val parts = mutableListOf<String>()
    if (name.isNotBlank()) parts += name
    if (atRound > 0) parts += "第${atRound}轮"
    condition?.pattern?.takeIf { it.isNotBlank() }?.let { parts += "「$it」" }
    if (chance < 100) parts += "${chance}%"
    if (effects.isNotEmpty()) {
        parts += effects.joinToString("、") {
            val sign = if (it.delta >= 0) "+" else ""
            it.variable + sign + it.delta
        }
    }
    if (gotoSceneId.isNotBlank()) parts += "→$gotoSceneId"
    return if (parts.isEmpty()) "未命名事件" else parts.joinToString(" ")
}

/**
 * 事件编辑框。
 *
 * 字段多，塞不进卡片，用对话框单独改。副作用按「电量 +2」每行一条，
 * 比一行行加控件省事，也不用为每条效果单独做增删交互。
 */

@Composable
internal fun EventEditorDialog(
    state: ScriptEditorUiState.Ready,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    val scene = state.rows.firstOrNull { it.id == state.eventEditorSceneId } ?: return
    val existing = scene.events.firstOrNull { it.id == state.eventEditorEventId }
    var name by remember(state.eventEditorEventId) { mutableStateOf(existing?.name.orEmpty()) }
    var atRound by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.atRound?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var keyword by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.condition?.pattern.orEmpty())
    }
    var chance by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.chance?.toString() ?: "100")
    }
    var addCast by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.addCast?.joinToString("、").orEmpty())
    }
    var removeCast by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.removeCast?.joinToString("、").orEmpty())
    }
    var gotoSceneId by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.gotoSceneId.orEmpty())
    }
    var narration by remember(state.eventEditorEventId) {
        mutableStateOf(existing?.narration.orEmpty())
    }
    var once by remember(state.eventEditorEventId) { mutableStateOf(existing?.once ?: true) }
    var clues by remember(state.eventEditorEventId) {
        mutableStateOf(
            existing?.effects
                ?.filter { it.appendText.isNotBlank() }
                ?.joinToString("\n") { it.variable + " " + it.appendText }
                .orEmpty()
        )
    }
    var effects by remember(state.eventEditorEventId) {
        mutableStateOf(
            existing?.effects?.joinToString("\n") { e ->
                val sign = if (e.delta >= 0) "+" else ""
                e.variable + " " + sign + e.delta
            }.orEmpty()
        )
    }

    AlertDialog(
        onDismissRequest = { ScriptEditorUiIntent.CloseEventEditor.emit() },
        title = { Text(stringResource(R.string.script_events)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.script_event_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = atRound,
                    onValueChange = { atRound = it.filter(Char::isDigit).take(3) },
                    label = { Text(stringResource(R.string.script_event_round)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text(stringResource(R.string.script_event_keyword)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = chance,
                    onValueChange = { chance = it.filter(Char::isDigit).take(3) },
                    label = { Text(stringResource(R.string.script_event_chance)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = effects,
                    onValueChange = { effects = it },
                    label = { Text(stringResource(R.string.script_event_effects)) },
                    placeholder = { Text(stringResource(R.string.script_event_effects_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    maxLines = 4
                )
                OutlinedTextField(
                    value = clues,
                    onValueChange = { clues = it },
                    label = { Text(stringResource(R.string.script_event_clue)) },
                    placeholder = { Text(stringResource(R.string.script_event_clue_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = addCast,
                    onValueChange = { addCast = it },
                    label = { Text(stringResource(R.string.script_event_add_cast)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = removeCast,
                    onValueChange = { removeCast = it },
                    label = { Text(stringResource(R.string.script_event_remove_cast)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = gotoSceneId,
                    onValueChange = { gotoSceneId = it },
                    label = { Text(stringResource(R.string.script_event_goto)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = narration,
                    onValueChange = { narration = it },
                    label = { Text(stringResource(R.string.script_event_narration)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    maxLines = 3
                )
                SwitchRow(stringResource(R.string.script_event_once), once) { once = it }
                if (existing != null) {
                    TextButton(
                        onClick = {
                            ScriptEditorUiIntent.DeleteEvent(
                                state.eventEditorSceneId,
                                existing.id
                            ).emit()
                        }
                    ) {
                        Text(stringResource(R.string.script_event_delete))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    ScriptEditorUiIntent.SaveEvent(
                        sceneId = state.eventEditorSceneId,
                        eventId = state.eventEditorEventId,
                        name = name,
                        atRound = atRound,
                        keyword = keyword,
                        chance = chance,
                        addCast = addCast,
                        removeCast = removeCast,
                        gotoSceneId = gotoSceneId,
                        narration = narration,
                        once = once,
                        effectsText = effects,
                        clueText = clues
                    ).emit()
                }
            ) {
                Text(stringResource(R.string.script_scene_save))
            }
        },
        dismissButton = {
            TextButton(onClick = { ScriptEditorUiIntent.CloseEventEditor.emit() }) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
