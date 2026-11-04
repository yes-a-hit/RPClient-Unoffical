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
internal fun ChapterCard(
    index: Int,
    row: ScriptSceneRow,
    generating: String,
    characterNames: List<String>,
    lorebookNames: List<String>,
    galleryKeys: List<String>,
    /** 剧本里定义的全部变量名，供场景选择要显示哪几栏。 */
    variableNames: List<String>,
    allScenes: List<ScriptSceneRow>,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    var expanded by remember(row.id) { mutableStateOf(false) }
    var sceneHint by remember(row.id) { mutableStateOf("") }
    var name by remember(row.id) { mutableStateOf(row.name) }
    var content by remember(row.id) { mutableStateOf(row.entryPrompt) }
    var completion by remember(row.id) { mutableStateOf(row.completionKeyword) }
    var failKeyword by remember(row.id) { mutableStateOf(row.failKeyword) }
    var failRounds by remember(row.id) { mutableStateOf(row.failRounds.toString()) }
    var rounds by remember(row.id) { mutableStateOf(row.maxRounds.toString()) }
    var background by remember(row.id, row.backgroundKey) { mutableStateOf(row.backgroundKey) }
    var castNames by remember(row.id) { mutableStateOf(row.castNames) }
    var bannedNames by remember(row.id) { mutableStateOf(row.bannedNames) }
    var lorebookName by remember(row.id) { mutableStateOf(row.lorebookName) }
    var nextScenes by remember(row.id) { mutableStateOf(row.nextSceneIds) }
    var actions by remember(row.id, row.suggestedActions) { mutableStateOf(row.suggestedActions) }
    var inheritBg by remember(row.id, row.inheritBackground) { mutableStateOf(row.inheritBackground) }
    var checkpoint by remember(row.id) { mutableStateOf(row.checkpoint) }
    var loop by remember(row.id) { mutableStateOf(row.loopEnabled) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "第 ${index + 1} 章",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (row.isEntry) {
                    Text(
                        text = " · " + stringResource(R.string.script_scene_entry),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Box(modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { ScriptEditorUiIntent.DeleteScene(row.id).emit() }) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.script_scene_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.script_scene_prompt),
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                maxLines = 6
            )
            Text(
                text = stringResource(R.string.script_scene_end_condition),
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = completion,
                onValueChange = { completion = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                singleLine = true
            )
            // 背景常显：编辑章节时一眼能看到图，不用先展开
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.script_scene_background),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { ScriptEditorUiIntent.PickBackground(row.id).emit() }
                        ) {
                            Text(stringResource(R.string.script_bg_import))
                        }
                    }
                    Text(
                        text = background.ifBlank {
                            if (inheritBg) {
                                stringResource(R.string.script_scene_inherit_bg)
                            } else {
                                stringResource(R.string.script_bg_none)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (background.isNotBlank() && background !in galleryKeys) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    // JSON 只存引用，图不在本机时明说，别让背景悄悄变空
                    if (background.isNotBlank() && background !in galleryKeys) {
                        Text(
                            text = stringResource(R.string.script_bg_missing),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rounds,
                        onValueChange = { rounds = it.filter(Char::isDigit).take(4) },
                        label = { Text(stringResource(R.string.script_scene_rounds)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // 继承打开时用剧本级那张；关掉且不选图就是不要背景
                    SwitchRow(
                        stringResource(R.string.script_scene_inherit_bg),
                        inheritBg
                    ) { inheritBg = it }
                    SelectorSection(
                        title = stringResource(R.string.script_scene_background),
                        options = galleryKeys,
                        selected = background.toNameSet(),
                        onToggle = { key -> background = if (background == key) "" else key }
                    )
                    SelectorSection(
                        title = stringResource(R.string.script_scene_cast),
                        options = characterNames,
                        selected = castNames.toNameSet(),
                        onToggle = { pick -> castNames = castNames.toggleName(pick) }
                    )
                    SelectorSection(
                        title = stringResource(R.string.script_scene_banned),
                        options = characterNames,
                        selected = bannedNames.toNameSet(),
                        onToggle = { pick -> bannedNames = bannedNames.toggleName(pick) }
                    )
                    SelectorSection(
                        title = stringResource(R.string.script_scene_lorebook),
                        options = lorebookNames,
                        selected = lorebookName.toNameSet(),
                        onToggle = { pick -> lorebookName = if (lorebookName == pick) "" else pick }
                    )
                    OutlinedTextField(
                        value = failKeyword,
                        onValueChange = { failKeyword = it },
                        label = { Text(stringResource(R.string.script_scene_fail_keyword)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = failRounds,
                        onValueChange = { failRounds = it.filter(Char::isDigit).take(4) },
                        label = { Text(stringResource(R.string.script_scene_fail_rounds)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    SelectorSection(
                        title = stringResource(R.string.script_scene_next),
                        options = allScenes.filter { it.id != row.id }.map { it.name },
                        selected = nextScenes.toNameSet(),
                        onToggle = { pick -> nextScenes = nextScenes.toggleName(pick) }
                    )
                    // 事件：规则驱动的核心，加一个玩偶机制就得改一条
                    Text(
                        text = stringResource(R.string.script_events),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    row.events.forEach { event ->
                        TextButton(
                            onClick = {
                                ScriptEditorUiIntent.OpenEventEditor(row.id, event.id).emit()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = event.summary(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { ScriptEditorUiIntent.AddEvent(row.id).emit() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.script_event_add))
                    }
                    // 本场景显示哪几栏：空清单代表按剧本可见性走
                    Text(
                        text = stringResource(R.string.script_scene_dashboard),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    SelectorSection(
                        title = "",
                        options = variableNames,
                        selected = row.dashboardItems.toSet(),
                        onToggle = { picked ->
                            ScriptEditorUiIntent.ToggleSceneDashboardItem(row.id, picked).emit()
                        }
                    )
                    SwitchRow(stringResource(R.string.script_row_checkpoint), checkpoint) {
                        checkpoint = it
                    }
                    SwitchRow(stringResource(R.string.script_scene_loop), loop) { loop = it }
                    // 留空就让 AI 临场决定，写满则固定这三条
                    Text(
                        text = stringResource(R.string.script_scene_actions),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = actions,
                        onValueChange = { actions = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )
                }
            }
            OutlinedTextField(
                value = sceneHint,
                onValueChange = { sceneHint = it },
                label = { Text(stringResource(R.string.script_gen_hint)) },
                placeholder = { Text(stringResource(R.string.script_gen_hint_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                maxLines = 2
            )
            OutlinedButton(
                onClick = {
                    ScriptEditorUiIntent.GenerateSceneContent(row.id, sceneHint).emit()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = generating.isBlank()
            ) {
                Text(
                    if (generating == row.id) {
                        stringResource(R.string.script_auto_filling)
                    } else {
                        stringResource(R.string.script_auto_fill_scene)
                    }
                )
            }
            TextButton(
                onClick = {
                    ScriptEditorUiIntent.UpdateScene(
                        sceneId = row.id,
                        name = name,
                        maxRounds = rounds.toIntOrNull() ?: 0,
                        entryPrompt = content,
                        checkpoint = checkpoint,
                        loopEnabled = loop,
                        isEntry = row.isEntry,
                        backgroundKey = background,
                        castNames = castNames,
                        bannedNames = bannedNames,
                        lorebookName = lorebookName,
                        failKeyword = failKeyword,
                        failRounds = failRounds.toIntOrNull() ?: 0,
                        completionKeyword = completion,
                        nextSceneIds = nextScenes,
                        suggestedActions = actions,
                        inheritBackground = inheritBg
                    ).emit()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.script_scene_save))
            }
        }
    }
}

/**
 * 一组可勾选的候选。
 *
 * 除了勾选现有项，还能手打名字加进去：
 * 本地没有对应角色卡或世界书时，候选是空的，
 * 只给勾选的话就彻底没法编辑了。
 */

@Composable
internal fun SelectorSection(
    title: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    var manualOpen by remember { mutableStateOf(false) }
    var manualText by remember { mutableStateOf("") }
    // 已选中但不在候选里的项也要显示，否则手打进去的东西看不见
    val visible = (options + selected.toList()).distinct()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(modifier = Modifier.weight(1f))
            TextButton(onClick = { manualOpen = !manualOpen }) {
                Text(
                    text = stringResource(R.string.script_selector_manual),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        if (visible.isEmpty()) {
            Text(
                text = stringResource(R.string.script_scene_no_options),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                visible.forEach { option ->
                    val picked = option in selected
                    // 选中态用勾加底色双重标识：只靠底色时两种主题色太接近，
                    // 看起来像全都被选中了，玩家不敢点第二次
                    Text(
                        text = if (picked) "✓ $option" else option,
                        modifier = Modifier
                            .clickable {
                                android.util.Log.d(
                                    "ScriptEditor",
                                    "toggle " + option + " picked=" + picked +
                                        " selected=" + selected.joinToString(",")
                                )
                                onToggle(option)
                            }
                            .background(
                                color = if (picked) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (picked) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
        if (manualOpen) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = manualText,
                    onValueChange = { manualText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.script_selector_manual_hint)) }
                )
                TextButton(
                    onClick = {
                        val name = manualText.trim()
                        if (name.isNotBlank() && name !in selected) onToggle(name)
                        manualText = ""
                    }
                ) {
                    Text(stringResource(R.string.script_selector_add))
                }
            }
        }
    }
}

/** 一行开关。 */

@Composable
internal fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

/** 后继场景选择：列出其它场景，点选即建立链接。 */

@Composable
internal fun LinkPickerDialog(
    row: ScriptSceneRow,
    field: ScriptLinkField,
    allScenes: List<ScriptSceneRow>,
    emit: ScriptEditorUiIntent.() -> Unit
) {
    val candidates = allScenes.filter { it.id != row.id }
    AlertDialog(
        onDismissRequest = { ScriptEditorUiIntent.CloseLinkPanel.emit() },
        title = {
            Text(
                when (field) {
                    ScriptLinkField.Next -> stringResource(R.string.script_scene_next)
                    ScriptLinkField.Unlock -> stringResource(R.string.script_scene_unlock)
                }
            )
        },
        text = {
            if (candidates.isEmpty()) {
                Text(stringResource(R.string.script_scene_no_options))
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    candidates.forEach { scene ->
                        Text(
                            text = scene.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    ScriptEditorUiIntent.ToggleLink(row.id, field, scene.id).emit()
                                }
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { ScriptEditorUiIntent.CloseLinkPanel.emit() }) {
                Text(stringResource(R.string.script_scene_save))
            }
        }
    )
}

/** 把顿号分隔的展示串拆成集合。 */

internal fun String.toNameSet(): Set<String> = this
    .split('、', ',', '，')
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .toSet()

/** 勾选或取消一个名字，返回重新拼好的展示串。 */

internal fun String.toggleName(name: String): String {
    val current = toNameSet().toMutableSet()
    if (!current.add(name)) current.remove(name)
    return current.joinToString("、")
}

/**
 * 剧本信息卡。
 *
 * 梗概同时充当全局背景：剧情里跨章节统一的设定写这里，
 * 具体情节留给各章节。开场白按「发言者|内容」每行一条，
 * 不留发言者即旁白。
 */
