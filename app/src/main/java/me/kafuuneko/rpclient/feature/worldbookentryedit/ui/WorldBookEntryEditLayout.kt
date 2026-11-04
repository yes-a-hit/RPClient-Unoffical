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

private enum class WorldBookEntryEditTab {
    Content,
    Trigger,
    Advanced
}

/**
 * 世界书条目完整兼容字段编辑页 Compose 入口。
 *
 * 核心设计：
 * - 纯声明式渲染：接收 UiState 并将用户操作映射为 UiIntent 回传。
 * - 拦截系统返回：存在未保存修改时拦截并提示二次确认。
 * - 终态保持：Finished 分支渲染上一快照以避免销毁时闪白。
 */

@Composable
fun WorldBookEntryEditLayout(
    uiState: WorldBookEntryEditUiState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    BackHandler(enabled = uiState is WorldBookEntryEditUiState.Normal) { WorldBookEntryEditUiIntent.Back.emit() }
    when (uiState) {
        WorldBookEntryEditUiState.None -> Unit
        is WorldBookEntryEditUiState.Finished -> WorldBookEntryEditLayout(uiState.previous) {}
        is WorldBookEntryEditUiState.Normal -> {
            WorldBookEntryEditNormal(uiState, emit)
            DialogSwitch(uiState.dialogState, emit)
        }
    }
}

/**
 * 正常编辑态主布局容器。
 *
 * 流程与职责：
 * - 顶栏呈现标题与保存快捷按钮。
 * - 主体采用分段选项卡（设定正文 / 触发规则 / 高级时序），避免信息过载。
 * - 支持全屏专注写作 Dialog 交互。
 */

@Composable
private fun WorldBookEntryEditNormal(
    state: WorldBookEntryEditUiState.Normal,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(WorldBookEntryEditTab.Content) }

    // 页面主容器
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = if (state.mode == WorldBookEntryEditMode.Create) {
                stringResource(R.string.create_world_book_entry)
            } else {
                stringResource(R.string.edit_world_book_entry)
            },
            onBack = { WorldBookEntryEditUiIntent.Back.emit() },
            actions = { TopBarSaveButton(state, emit) }
        )
        RpLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                ),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RpPageTitle(
                    title = state.form.name.ifBlank { stringResource(R.string.unnamed_entry) },
                    subtitle = stringResource(R.string.world_book_entry_editor_subtitle)
                )
            }
            if (state.loadState == WorldBookEntryEditLoadState.Loading) {
                item { LoadingPanel() }
            } else {
                item {
                    HeroHeaderPanel(
                        form = state.form,
                        loadState = state.loadState,
                        emit = emit
                    )
                }
                item {
                    WorldBookEntryEditTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                when (selectedTab) {
                    WorldBookEntryEditTab.Content -> {
                        item { BasicInfoPanel(state.form, state.loadState, emit) }
                        item {
                            ContentWorkbenchPanel(
                                form = state.form,
                                emit = emit
                            )
                        }
                        item { CategoriesPanel(state.form, emit) }
                    }

                    WorldBookEntryEditTab.Trigger -> {
                        if (state.form.constant) {
                            item { ConstantNoticeBanner() }
                        }
                        item { PrimaryKeywordsPanel(state.form, emit) }
                        item {
                            SecondaryKeywordsAndLogicPanel(
                                form = state.form,
                                loadState = state.loadState,
                                emit = emit
                            )
                        }
                        item { ProbabilityPanel(state.form, state.loadState, emit) }
                        item { ScanDepthAndScopePanel(state.form, state.loadState, emit) }
                    }

                    WorldBookEntryEditTab.Advanced -> {
                        item { PlacementPanel(state.form, state.loadState, emit) }
                        item { InclusionGroupPanel(state.form, state.loadState, emit) }
                        item { TimingPanel(state.form, state.loadState, emit) }
                        item { RecursionAndMatchingPanel(state.form, state.loadState, emit) }
                        item { ExtensionsJsonPanel(state.form, state.loadState, emit) }
                    }
                }

                item { ActionPanel(state, emit) }
            }
        }
    }
}

/** 初始数据加载占位面板。 */

@Composable
private fun LoadingPanel() {
    Panel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Text(stringResource(R.string.loading))
        }
    }
}

/**
 * 顶部 Hero 状态概览看板。
 *
 * 核心特性：
 * - 汇总条目核心参数（常驻/触发、启用状态、角色、深度优先级、触发概率）。
 * - 右侧集成快捷启用 Switch 与删除入口。
 */

@Composable
private fun HeroHeaderPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None
    val roleLabel = when (form.role.trim().toIntOrNull() ?: LorebookEntry.ROLE_SYSTEM) {
        LorebookEntry.ROLE_USER -> stringResource(R.string.role_user)
        LorebookEntry.ROLE_ASSISTANT -> stringResource(R.string.role_assistant)
        else -> stringResource(R.string.role_system)
    }
    val probability = form.probability.toIntOrNull() ?: 100
    val depthVal = form.depth.toIntOrNull() ?: 4
    val orderVal = form.order.toIntOrNull() ?: 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (form.disabled) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.28f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RpIconBubble(
                    icon = Icons.AutoMirrored.Rounded.MenuBook,
                    containerColor = if (form.constant) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    },
                    contentColor = if (form.constant) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = form.name.ifBlank { stringResource(R.string.unnamed_entry) },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (probability < 100) {
                            stringResource(R.string.entry_order_depth_prob, orderVal, depthVal, probability)
                        } else {
                            stringResource(R.string.entry_order_depth, orderVal, depthVal)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }
                Switch(
                    checked = !form.disabled,
                    enabled = enabled,
                    onCheckedChange = { WorldBookEntryEditUiIntent.ChangeDisabled(!it).emit() }
                )
                IconButton(
                    enabled = enabled,
                    onClick = { WorldBookEntryEditUiIntent.DeleteEntryClick.emit() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                    )
                }
            }

            // 状态胶囊徽章列表
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusBadge(
                    text = if (form.constant) stringResource(R.string.entry_status_constant) else stringResource(R.string.entry_status_conditional),
                    color = if (form.constant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
                StatusBadge(
                    text = if (form.disabled) stringResource(R.string.entry_status_disabled) else stringResource(R.string.entry_status_enabled),
                    color = if (form.disabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.tertiary
                )
                StatusBadge(
                    text = roleLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (form.group.isNotBlank()) {
                    StatusBadge(
                        text = stringResource(R.string.entry_group_badge, form.group),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/** 单个弱化状态徽章胶囊。 */

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.25f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

/** 分段选项卡切换器组件。 */

@Composable
private fun WorldBookEntryEditTabBar(
    selectedTab: WorldBookEntryEditTab,
    onTabSelected: (WorldBookEntryEditTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                WorldBookEntryEditTab.Content to stringResource(R.string.tab_entry_content),
                WorldBookEntryEditTab.Trigger to stringResource(R.string.tab_entry_trigger),
                WorldBookEntryEditTab.Advanced to stringResource(R.string.tab_entry_advanced)
            )
            tabs.forEach { (tab, title) ->
                val selected = selectedTab == tab
                Surface(
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// -------------------- Tab 1: 设定正文 --------------------

/** 基础名称与开关面板。 */

@Composable
private fun ActionPanel(
    state: WorldBookEntryEditUiState.Normal,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            enabled = state.loadState == WorldBookEntryEditLoadState.None,
            onClick = { WorldBookEntryEditUiIntent.Back.emit() }
        ) {
            Text(stringResource(R.string.cancel))
        }
        Button(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            enabled = state.loadState == WorldBookEntryEditLoadState.None,
            onClick = { WorldBookEntryEditUiIntent.SaveEntry.emit() }
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null)
            Text(
                when {
                    state.loadState == WorldBookEntryEditLoadState.Saving -> stringResource(R.string.saving)
                    state.mode == WorldBookEntryEditMode.Create -> stringResource(R.string.create)
                    else -> stringResource(R.string.save)
                }
            )
        }
    }
}

/** 顶栏保存操作按钮。 */

@Composable
private fun TopBarSaveButton(
    state: WorldBookEntryEditUiState.Normal,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    TextButton(
        enabled = state.loadState == WorldBookEntryEditLoadState.None,
        onClick = { WorldBookEntryEditUiIntent.SaveEntry.emit() }
    ) {
        Icon(Icons.Rounded.Check, contentDescription = null)
        Text(
            when {
                state.loadState == WorldBookEntryEditLoadState.Saving -> stringResource(R.string.saving)
                state.mode == WorldBookEntryEditMode.Create -> stringResource(R.string.create)
                else -> stringResource(R.string.save)
            }
        )
    }
}

/** 统一对话框路由分发。 */

@Composable
private fun DialogSwitch(
    dialogState: WorldBookEntryEditDialogState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    when (dialogState) {
        WorldBookEntryEditDialogState.None -> Unit
        is WorldBookEntryEditDialogState.DeleteConfirm -> AppDangerDialog(
            onDismissRequest = { WorldBookEntryEditUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.delete_world_book_entry_title),
            message = stringResource(R.string.delete_world_book_entry_message, dialogState.entryName),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { WorldBookEntryEditUiIntent.ConfirmDeleteEntry.emit() }
        )
        WorldBookEntryEditDialogState.UnsavedChangesConfirm -> AppDangerDialog(
            onDismissRequest = { WorldBookEntryEditUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.unsaved_changes_title),
            message = stringResource(R.string.unsaved_changes_message),
            confirmText = stringResource(R.string.discard_changes),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { WorldBookEntryEditUiIntent.ConfirmDiscardChanges.emit() }
        )
        is WorldBookEntryEditDialogState.PromptEditor -> AppPromptEditorDialog(
            onDismissRequest = { WorldBookEntryEditUiIntent.DismissDialog.emit() },
            title = stringResource(R.string.entry_content),
            value = dialogState.draftText,
            availableMacros = DEFAULT_PROMPT_MACROS,
            onValueChange = { WorldBookEntryEditUiIntent.ChangePromptEditorDraft(it).emit() },
            onConfirm = { WorldBookEntryEditUiIntent.ConfirmPromptEditor.emit() }
        )
    }
}

/** 现代化基础表单文本输入框，支持宏变量插入工具栏与全屏写作拓展。 */

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun WorldBookEntryEditLayoutPreview() {
    AppTheme(dynamicColor = false) {
        WorldBookEntryEditLayout(
            uiState = WorldBookEntryEditUiState.Normal(
                mode = WorldBookEntryEditMode.Edit,
                form = WorldBookEntryEditForm(
                    id = 1L,
                    lorebookId = 1L,
                    name = "Old District",
                    keywords = listOf("district", "railway"),
                    secondaryKeywords = listOf("archive"),
                    category = listOf("location"),
                    content = "The old district is divided by three elevated railways."
                )
            ),
            emit = {}
        )
    }
}
