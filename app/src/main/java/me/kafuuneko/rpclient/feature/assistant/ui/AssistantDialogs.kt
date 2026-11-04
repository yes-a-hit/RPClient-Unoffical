package me.kafuuneko.rpclient.feature.assistant.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SaveAlt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kafuuneko.rpclient.feature.assistant.presentation.AssistantAttachment
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import me.kafuuneko.rpclient.feature.assistant.presentation.AssistantMessageItem
import me.kafuuneko.rpclient.feature.assistant.presentation.AssistantRole
import me.kafuuneko.rpclient.feature.assistant.presentation.AssistantUiIntent
import me.kafuuneko.rpclient.feature.assistant.presentation.AssistantUiState

@Composable
internal fun SessionListDialog(
    state: AssistantUiState.Ready,
    emit: AssistantUiIntent.() -> Unit
) {
    AlertDialog(
        onDismissRequest = { AssistantUiIntent.CloseSessionList.emit() },
        title = {
            Text(
                text = stringResource(R.string.assistant_sessions) + " " +
                    stringResource(R.string.assistant_session_count, state.sessions.count { !it.archived })
            )
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.sessions, key = { it.id }) { session ->
                    val current = session.id == state.currentSessionId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AssistantUiIntent.SwitchSession(session.id).emit() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (session.archived) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Text(
                                text = stringResource(R.string.assistant_session_meta, session.messageCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!session.archived) {
                            IconButton(
                                onClick = { AssistantUiIntent.ArchiveSession(session.id).emit() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Archive,
                                    contentDescription = stringResource(R.string.assistant_session_archive),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { AssistantUiIntent.DeleteSession(session.id).emit() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.assistant_session_delete),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { AssistantUiIntent.NewSession.emit() }) {
                Text(stringResource(R.string.assistant_session_new))
            }
        },
        dismissButton = {
            TextButton(onClick = { AssistantUiIntent.CloseSessionList.emit() }) {
                Text(stringResource(R.string.script_picker_done))
            }
        }
    )
}


/** 沙箱浏览器：列出文件，可删除；文本可长按查看内容。 */

@Composable
internal fun SandboxBrowserDialog(
    state: AssistantUiState.Ready,
    emit: AssistantUiIntent.() -> Unit
) {
    AlertDialog(
        onDismissRequest = { AssistantUiIntent.CloseSandboxBrowser.emit() },
        title = { Text(stringResource(R.string.assistant_sandbox)) },
        text = {
            if (state.sandboxFiles.isEmpty()) {
                Text(stringResource(R.string.assistant_sandbox_empty))
            } else {
                LazyColumn {
                    items(state.sandboxFiles) { file ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = file.path,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                                Text(
                                    text = formatSize(file.size),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    AssistantUiIntent.ExportSandboxFile(file.path).emit()
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.SaveAlt,
                                    contentDescription = "导出",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    AssistantUiIntent.DeleteSandboxFile(file.path).emit()
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
        },
        confirmButton = {
            TextButton(onClick = { AssistantUiIntent.CloseSandboxBrowser.emit() }) {
                Text(stringResource(R.string.script_picker_done))
            }
        }
    )
}

/** 模型发图时写在正文里的标记。 */
