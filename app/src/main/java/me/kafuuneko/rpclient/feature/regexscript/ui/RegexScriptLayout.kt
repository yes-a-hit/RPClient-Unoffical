package me.kafuuneko.rpclient.feature.regexscript.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import me.kafuuneko.rpclient.ui.widgets.DraggableItem
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.dragContainer
import me.kafuuneko.rpclient.ui.widgets.rememberLazyListDragDropState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CodeOff
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Tune
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.dialog.DialogBadgeTone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.regexscript.presentation.RegexScriptDialogState
import me.kafuuneko.rpclient.feature.regexscript.presentation.RegexScriptUiIntent
import me.kafuuneko.rpclient.feature.regexscript.presentation.RegexScriptUiState
import me.kafuuneko.rpclient.libs.regex.RegexExecutionMode
import me.kafuuneko.rpclient.libs.regex.RegexFindMacroMode
import me.kafuuneko.rpclient.libs.regex.RegexPlacement
import me.kafuuneko.rpclient.libs.regex.RegexScript
import me.kafuuneko.rpclient.libs.regex.RegexScriptScope
import me.kafuuneko.rpclient.ui.theme.AppTheme

@Composable
fun RegexScriptLayout(
    uiState: RegexScriptUiState,
    emitIntent: (RegexScriptUiIntent) -> Unit = {}
) {
    BackHandler(enabled = uiState is RegexScriptUiState.Normal) { emitIntent(RegexScriptUiIntent.Back) }
    when (uiState) {
        RegexScriptUiState.None -> Unit
        is RegexScriptUiState.Finished -> RegexScriptLayout(uiState.previous) {}
        is RegexScriptUiState.Normal -> {
            RegexScriptNormal(uiState, emitIntent)
            DialogSwitch(uiState.dialogState, emitIntent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegexScriptNormal(
    state: RegexScriptUiState.Normal,
    emitIntent: (RegexScriptUiIntent) -> Unit
) {
    val canManageScripts =
        state.scope != RegexScriptScope.Character || state.selectedCharacterId != null
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.regex_script_title),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { emitIntent(RegexScriptUiIntent.Back) }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { emitIntent(RegexScriptUiIntent.ImportClick) },
                            enabled = canManageScripts && !state.transferInProgress
                        ) {
                            Icon(Icons.Rounded.FileDownload, contentDescription = null)
                        }
                        IconButton(
                            onClick = { emitIntent(RegexScriptUiIntent.ExportClick) },
                            enabled = !state.transferInProgress
                        ) {
                            Icon(Icons.Rounded.FileUpload, contentDescription = null)
                        }
                        IconButton(
                            onClick = { emitIntent(RegexScriptUiIntent.CreateScript) },
                            enabled = canManageScripts && !state.transferInProgress
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                if (state.transferInProgress) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    ) { padding ->
        val listState = rememberLazyListState()
        val scriptKeys = remember(state.scripts) { state.scripts.map { it.id }.toSet() }
        val dragDropState = rememberLazyListDragDropState(
            lazyListState = listState,
            isItemDraggable = { key -> key in scriptKeys },
            onMove = { fromKey, toKey ->
                val fromIndex = state.scripts.indexOfFirst { it.id == fromKey }
                val toIndex = state.scripts.indexOfFirst { it.id == toKey }
                if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
                    emitIntent(RegexScriptUiIntent.ReorderScript(fromIndex, toIndex))
                }
            },
            onDragEnd = {
                emitIntent(RegexScriptUiIntent.CommitScriptOrder)
            }
        )

        RpLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .dragContainer(dragDropState),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { ScopeSelector(state, emitIntent) }

            if (state.scope == RegexScriptScope.Character) {
                item {
                    AnimatedVisibility(visible = true) {
                        CharacterSelector(state, emitIntent)
                    }
                }
            }

            if (state.scope == RegexScriptScope.Character) {
                item { AuthorizationCard(state, emitIntent) }
            }

            if (state.scripts.isEmpty()) {
                item { EmptyScriptsCard() }
            } else {
                items(state.scripts, key = { it.id }) { script ->
                    DraggableItem(
                        dragDropState = dragDropState,
                        key = script.id
                    ) { isDragging ->
                        ScriptCard(
                            script = script,
                            isDragging = isDragging,
                            emitIntent = emitIntent
                        )
                    }
                }
            }

            item { TestCard(state, emitIntent) }
        }
    }
}

/** 统一高对比度 FilterChip 组件，自带打勾选态图标与高亮边框 */

@Composable
internal fun DialogSwitch(
    dialogState: RegexScriptDialogState,
    emitIntent: (RegexScriptUiIntent) -> Unit
) {
    when (dialogState) {
        RegexScriptDialogState.None -> Unit
        is RegexScriptDialogState.Editor -> EditorDialog(dialogState, emitIntent)
        is RegexScriptDialogState.DeleteConfirm -> AppDangerDialog(
            onDismissRequest = { emitIntent(RegexScriptUiIntent.DismissDialog) },
            title = stringResource(R.string.regex_delete_title),
            message = dialogState.scriptName,
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { emitIntent(RegexScriptUiIntent.ConfirmDeleteScript) }
        )
    }
}

/** Regex 脚本编辑器，对长表单进行分块分组，并在正则文本域启用 Monospace 字体。 */

@Composable
internal fun RegexScriptScope.label(): String = when (this) {
    RegexScriptScope.Global -> stringResource(R.string.regex_scope_global)
    RegexScriptScope.Character -> stringResource(R.string.regex_scope_character)
}

@Composable
internal fun RegexPlacement.label(): String = when (this) {
    RegexPlacement.MarkdownDisplay -> stringResource(R.string.regex_placement_display)
    RegexPlacement.UserInput -> stringResource(R.string.regex_placement_user)
    RegexPlacement.AiResponse -> stringResource(R.string.regex_placement_ai)
    RegexPlacement.SlashCommand -> stringResource(R.string.regex_placement_slash)
    RegexPlacement.WorldInfo -> stringResource(R.string.regex_placement_world_info)
    RegexPlacement.Reasoning -> stringResource(R.string.regex_placement_reasoning)
}

@Preview(showBackground = true)
@Composable
internal fun RegexScriptLayoutPreview() {
    AppTheme(dynamicColor = false) {
        RegexScriptLayout(
            RegexScriptUiState.Normal(
                scripts = listOf(
                    RegexScript(
                        id = "1",
                        scriptName = "Hide tags",
                        findRegex = "/<tag>.*?<\\/tag>/gis",
                        replaceString = "",
                        placement = listOf(RegexPlacement.AiResponse.value)
                    )
                )
            )
        )
    }
}
