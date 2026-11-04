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
internal fun PackInfoCard(
    state: ScriptEditorUiState.Ready,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    var name by remember(state.packName) { mutableStateOf(state.packName) }
    var description by remember(state.description) { mutableStateOf(state.description) }
    var openings by remember(state.openingsText) { mutableStateOf(state.openingsText) }
    var showCompletion by remember(state.showCompletion) { mutableStateOf(state.showCompletion) }
    var shareSettings by remember(state.shareSettings) { mutableStateOf(state.shareSettings) }
    var expanded by remember { mutableStateOf(true) }
    var packHint by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.script_pack_info),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Box(modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (expanded) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.script_pack_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.script_pack_description),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    maxLines = 4
                )
                Text(
                    text = stringResource(R.string.script_pack_openings),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = openings,
                    onValueChange = { openings = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    maxLines = 5
                )
                OutlinedTextField(
                    value = packHint,
                    onValueChange = { packHint = it },
                    label = { Text(stringResource(R.string.script_gen_hint)) },
                    placeholder = { Text(stringResource(R.string.script_gen_hint_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    maxLines = 2
                )
                OutlinedButton(
                    onClick = { ScriptEditorUiIntent.GeneratePackInfo(packHint).emit() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.generating.isBlank()
                ) {
                    Text(
                        if (state.generating == "pack") {
                            stringResource(R.string.script_auto_filling)
                        } else {
                            stringResource(R.string.script_auto_fill_pack)
                        }
                    )
                }
                SelectorSection(
                    title = stringResource(R.string.script_pack_background),
                    options = state.galleryKeys,
                    selected = state.packBackground.toNameSet(),
                    onToggle = { key ->
                        ScriptEditorUiIntent.UpdatePackInfo(
                            name = name,
                            description = description,
                            openingsText = openings,
                            showCompletion = showCompletion,
                            shareSettings = shareSettings,
                            backgroundKey = if (state.packBackground == key) "" else key
                        ).emit()
                    }
                )
                OutlinedButton(
                    onClick = { ScriptEditorUiIntent.PickBackground(null).emit() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.script_bg_import))
                }
                if (state.packBackground.isNotBlank() &&
                    state.packBackground !in state.galleryKeys
                ) {
                    Text(
                        text = stringResource(R.string.script_bg_missing),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                SwitchRow(
                    stringResource(R.string.script_pack_show_completion),
                    showCompletion
                ) { showCompletion = it }
                SwitchRow(
                    stringResource(R.string.script_pack_share),
                    shareSettings
                ) { shareSettings = it }
                TextButton(
                    onClick = {
                        ScriptEditorUiIntent.UpdatePackInfo(
                            name = name,
                            description = description,
                            openingsText = openings,
                            showCompletion = showCompletion,
                            shareSettings = shareSettings
                        ).emit()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.script_scene_save))
                }
            }
        }
    }
}

/**
 * 角色名单超管。
 *
 * 剧本里用到的角色未必都在本地有卡：这里能一键按剧本设定补卡，
 * 也能把不再需要的角色从所有场景名单里摘掉。
 */

@Composable
internal fun CastAdminCard(
    state: ScriptEditorUiState.Ready,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    if (state.castInUse.isEmpty()) return
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.script_cast_admin),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " · " + state.castInUse.size,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (expanded) {
                state.castInUse.forEach { name ->
                    val hasCard = name in state.characterNames
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (hasCard) {
                                    stringResource(R.string.script_cast_has_card)
                                } else {
                                    stringResource(R.string.script_cast_no_card)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasCard) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        if (!hasCard) {
                            TextButton(
                                onClick = {
                                    ScriptEditorUiIntent.CreateCastCharacter(name).emit()
                                }
                            ) {
                                Text(stringResource(R.string.script_cast_create))
                            }
                        }
                        IconButton(
                            onClick = {
                                ScriptEditorUiIntent.RemoveCastCharacter(name).emit()
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 规划中提示。
 *
 * 这几项已经设计但引擎还没实现，明确列出来，
 * 免得玩家把「没这个功能」当成 bug 反复试。
 */

@Composable
internal fun ComingSoonCard() {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = stringResource(R.string.script_coming_soon),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Box(modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (expanded) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    listOf(
                        R.string.script_soon_free,
                        R.string.script_soon_fnaf,
                        R.string.script_soon_daily,
                        R.string.script_soon_group
                    ).forEach { res ->
                        Text(
                            text = "· " + stringResource(res),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 剧本变量面板。
 *
 * 变量是规则驱动的地基：场景解锁、事件触发、成败判定都能引用它。
 * 没有这个面板，作者只能手改 JSON。
 */
