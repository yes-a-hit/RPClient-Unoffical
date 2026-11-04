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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditorDialog(
    state: RegexScriptDialogState.Editor,
    emitIntent: (RegexScriptUiIntent) -> Unit
) {
    val draft = state.draft
    AppDialogScaffold(
        onDismissRequest = { emitIntent(RegexScriptUiIntent.DismissDialog) },
        title = stringResource(R.string.regex_editor_title),
        badgeIcon = Icons.Rounded.Tune,
        badgeTone = DialogBadgeTone.Primary,
        confirmText = stringResource(R.string.save),
        dismissText = stringResource(R.string.cancel),
        confirmEnabled = state.validationError == null,
        onConfirm = { emitIntent(RegexScriptUiIntent.SaveDraft) },
        scrollableContent = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 基本信息分组
            DraftGroupHeader(stringResource(R.string.regex_section_basic))
            DraftField(draft.scriptName, R.string.regex_script_name) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(scriptName = it)))
            }

            // 规则与表达式分组
            DraftGroupHeader(stringResource(R.string.regex_section_expressions))
            DraftField(
                value = draft.findRegex,
                labelRes = R.string.regex_find_regex,
                minLines = 2,
                isMonospace = true
            ) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(findRegex = it)))
            }
            DraftField(
                value = draft.replaceString,
                labelRes = R.string.regex_replace_string,
                minLines = 2,
                isMonospace = true
            ) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(replaceString = it)))
            }
            DraftField(
                value = draft.trimStrings,
                labelRes = R.string.regex_trim_strings,
                minLines = 2,
                isMonospace = true
            ) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(trimStrings = it)))
            }

            // 作用位置分组
            DraftGroupHeader(stringResource(R.string.regex_placements))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RegexPlacement.entries.forEach { placement ->
                    val selected = placement.value in draft.placements
                    AppFilterChip(
                        selected = selected,
                        onClick = {
                            val placements = draft.placements.toMutableSet()
                            if (!placements.add(placement.value)) placements.remove(placement.value)
                            emitIntent(
                                RegexScriptUiIntent.UpdateDraft(
                                    draft.copy(placements = placements)
                                )
                            )
                        },
                        label = placement.label()
                    )
                }
            }

            // 功能开关分组
            DraftGroupHeader(stringResource(R.string.regex_section_switches))
            BooleanRow(stringResource(R.string.regex_enabled), !draft.disabled) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(disabled = !it)))
            }
            BooleanRow(stringResource(R.string.regex_markdown_only), draft.markdownOnly) {
                emitIntent(
                    RegexScriptUiIntent.UpdateDraft(
                        draft.copy(
                            markdownOnly = it,
                            promptOnly = if (it) false else draft.promptOnly
                        )
                    )
                )
            }
            BooleanRow(stringResource(R.string.regex_prompt_only), draft.promptOnly) {
                emitIntent(
                    RegexScriptUiIntent.UpdateDraft(
                        draft.copy(
                            promptOnly = it,
                            markdownOnly = if (it) false else draft.markdownOnly
                        )
                    )
                )
            }
            BooleanRow(stringResource(R.string.regex_run_on_edit), draft.runOnEdit) {
                emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(runOnEdit = it)))
            }

            // 高阶功能分组
            DraftGroupHeader(stringResource(R.string.regex_section_advanced))
            Text(
                stringResource(R.string.regex_find_macro_mode),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RegexFindMacroMode.entries.forEach { mode ->
                    AppFilterChip(
                        selected = draft.substituteRegex == mode.value,
                        onClick = {
                            emitIntent(
                                RegexScriptUiIntent.UpdateDraft(
                                    draft.copy(substituteRegex = mode.value)
                                )
                            )
                        },
                        label = mode.name
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = draft.minDepth,
                    onValueChange = {
                        emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(minDepth = it)))
                    },
                    label = { Text(stringResource(R.string.regex_min_depth)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = draft.maxDepth,
                    onValueChange = {
                        emitIntent(RegexScriptUiIntent.UpdateDraft(draft.copy(maxDepth = it)))
                    },
                    label = { Text(stringResource(R.string.regex_max_depth)) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            state.validationError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
internal fun DraftGroupHeader(title: String) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
internal fun DraftField(
    value: String,
    labelRes: Int,
    minLines: Int = 1,
    isMonospace: Boolean = false,
    onValueChange: (String) -> Unit
) {
    RpScrollableOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        textStyle = if (isMonospace) {
            LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
        } else {
            LocalTextStyle.current
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = minLines == 1,
        minLines = minLines,
        maxLines = if (minLines == 1) 1 else 8
    )
}

@Composable
internal fun BooleanRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
