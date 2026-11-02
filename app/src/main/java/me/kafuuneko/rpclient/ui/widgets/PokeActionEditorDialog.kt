/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 PokeActionsTest 里的用例，它们描述了列表的预期。
 */

package me.kafuuneko.rpclient.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.libs.poke.PokeAction
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold

/**
 * 动作列表编辑对话框，单聊与群聊共用。
 *
 * 只接收回调不接收具体 Intent 类型：两个功能页的意图是各自定义的，
 * 让对话框知道它们中的任何一个都会把它绑死在某一侧。
 */
@Composable
fun PokeActionEditorDialog(
    drafts: List<PokeAction>,
    onDismiss: () -> Unit,
    onChange: (id: String, label: String, template: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (id: String) -> Unit,
    onSave: () -> Unit
) {
    AppDialogScaffold(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.poke_actions_title),
        confirmText = stringResource(R.string.poke_actions_save),
        // 一条都没填时不给保存：全空列表等于把动作功能关掉，容易误触
        confirmEnabled = drafts.any { it.label.isNotBlank() || it.template.isNotBlank() },
        onConfirm = onSave,
        scrollableContent = true
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.poke_actions_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            drafts.forEach { action ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = action.label,
                            onValueChange = { onChange(action.id, it, action.template) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.poke_actions_label)) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = action.template,
                            onValueChange = { onChange(action.id, action.label, it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.poke_actions_template)) },
                            minLines = 1,
                            maxLines = 3
                        )
                    }
                    IconButton(onClick = { onRemove(action.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            TextButton(onClick = onAdd) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.poke_actions_add))
            }
        }
    }
}
