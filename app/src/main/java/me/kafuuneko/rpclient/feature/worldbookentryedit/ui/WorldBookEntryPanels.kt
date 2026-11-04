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
internal fun BasicInfoPanel(
    form: WorldBookEntryEditForm,
    loadState: WorldBookEntryEditLoadState,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val enabled = loadState == WorldBookEntryEditLoadState.None
    Panel {
        RpSectionHeader(title = stringResource(R.string.basic_info))
        FormTextField(
            label = stringResource(R.string.entry_name),
            value = form.name,
            onChange = { WorldBookEntryEditUiIntent.ChangeName(it).emit() }
        )

        RpSettingsGroup {
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_constant),
                subtitle = stringResource(R.string.entry_constant_desc),
                checked = form.constant,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeConstant(it).emit() }
            )
            RpSettingsDivider(startIndent = false)
            RpSettingsSwitchTile(
                title = stringResource(R.string.entry_disabled),
                subtitle = stringResource(R.string.entry_disabled_desc),
                checked = form.disabled,
                enabled = enabled,
                onCheckedChange = { WorldBookEntryEditUiIntent.ChangeDisabled(it).emit() }
            )
        }
    }
}

/**
 * 增强型设定正文工作台。
 *
 * 核心功能：
 * - 实时显示字符数与 Token 预估指标。
 * - 紧密集成宏变量快捷插入工具栏与全屏专注写作入口。
 */

@Composable
internal fun ContentWorkbenchPanel(
    form: WorldBookEntryEditForm,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    val charCount = form.content.length
    val estimatedTokens = (charCount / 3.5).toInt()

    Panel {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.entry_content),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.50f)
            ) {
                Text(
                    text = stringResource(R.string.entry_char_token_count, charCount, estimatedTokens),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        FormTextField(
            label = stringResource(R.string.entry_content),
            value = form.content,
            minLines = 8,
            maxLines = 18,
            showMacroBar = true,
            onExpandFullscreen = { WorldBookEntryEditUiIntent.OpenPromptEditor.emit() },
            onChange = { WorldBookEntryEditUiIntent.ChangeContent(it).emit() }
        )
    }
}

/** 分类标签编辑面板。 */

@Composable
internal fun CategoriesPanel(
    form: WorldBookEntryEditForm,
    emit: WorldBookEntryEditUiIntent.() -> Unit
) {
    RpChipInputField(
        title = stringResource(R.string.categories),
        chips = form.category,
        icon = Icons.AutoMirrored.Rounded.Label,
        onChipsChanged = { WorldBookEntryEditUiIntent.SetCategories(it).emit() },
        addLabel = stringResource(R.string.add_category),
        placeholder = stringResource(R.string.category_input_placeholder),
        editDialogTitle = stringResource(R.string.edit_category_title),
        accentColor = MaterialTheme.colorScheme.tertiary
    )
}

// -------------------- Tab 2: 触发规则 --------------------

/** 常驻激活提示横幅。 */

@Composable
internal fun ConstantNoticeBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.entry_constant_notice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/** 主关键词输入面板。 */
