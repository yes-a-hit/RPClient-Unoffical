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
fun ScriptEditorLayout(
    uiState: ScriptEditorUiState,
    emit: ScriptEditorUiIntent.() -> Unit,
    onBack: () -> Unit
) {
    val state = uiState as? ScriptEditorUiState.Ready ?: return
    Scaffold(
        topBar = {
            AppTopBar(
                title = state.packName.ifBlank { stringResource(R.string.script_editor_title) },
                onBack = onBack,
                actions = {
                    IconButton(onClick = { ScriptEditorUiIntent.ExportToFileClick.emit() }) {
                        Icon(
                            Icons.Rounded.FileDownload,
                            contentDescription = stringResource(R.string.script_export_file)
                        )
                    }
                    IconButton(onClick = { ScriptEditorUiIntent.AddScene.emit() }) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.script_add_scene)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ComingSoonCard()
            }
            item {
                PackInfoCard(state = state, emit = emit)
            }
            item {
                VariableCard(state = state, emit = emit)
            }
            item {
                CastAdminCard(state = state, emit = emit)
            }
            itemsIndexed(state.rows) { index, row ->
                ChapterCard(
                    index = index,
                    row = row,
                    generating = state.generating,
                    characterNames = state.characterNames,
                    variableNames = state.variables.map { it.name },
                    lorebookNames = state.lorebookNames,
                    galleryKeys = state.galleryKeys,
                    allScenes = state.rows,
                    emit = emit
                )
            }
            item {
                TextButton(
                    onClick = { ScriptEditorUiIntent.AddScene.emit() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.script_add_scene))
                }
            }
        }
    }

    if (state.eventEditorSceneId.isNotBlank()) {
        EventEditorDialog(state, emit)
    }

    val editingRow = state.editingRow
    val editingField = state.editingField
    if (editingRow != null && editingField != null) {
        LinkPickerDialog(
            row = editingRow,
            field = editingField,
            allScenes = state.rows,
            emit = emit
        )
    }
}

/**
 * 单章卡片。
 *
 * 默认展开内容与结局条件，其余设置收在「更多」里，
 * 免得每章都要滚很久才能看到下一章。
 */
