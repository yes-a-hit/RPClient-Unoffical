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
internal fun AttachmentTray(
    attachments: List<AssistantAttachment>,
    emit: AssistantUiIntent.() -> Unit
) {
    var previewTarget by remember { mutableStateOf<AssistantAttachment?>(null) }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attachments, key = { it.uri }) { attachment ->
            AttachmentCard(
                attachment = attachment,
                onPreview = { previewTarget = attachment },
                onRemove = { AssistantUiIntent.RemoveAttachment(attachment.uri).emit() }
            )
        }
    }
    previewTarget?.let { target ->
        AttachmentPreviewDialog(target) { previewTarget = null }
    }
}

/** 单个附件卡片：图片走缩略图，文本走图标。 */

@Composable
internal fun AttachmentCard(
    attachment: AssistantAttachment,
    onPreview: () -> Unit,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.width(96.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPreview)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (attachment.isImage) {
                    AttachmentThumbnail(attachment.uri)
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = attachment.name,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatSize(attachment.byteCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).size(20.dp)
        ) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = stringResource(R.string.assistant_send),
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** 图片缩略图：按目标尺寸采样解码，避免大图占内存。 */

@Composable
internal fun AttachmentThumbnail(uri: String) {
    val context = LocalContext.current
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val parsed = android.net.Uri.parse(uri)
                val options = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(parsed)?.use {
                    android.graphics.BitmapFactory.decodeStream(it, null, options)
                }
                // 按最长边 256 采样，卡片只有 64dp 高
                var sample = 1
                val longest = maxOf(options.outWidth, options.outHeight)
                while (longest / sample > 256) sample *= 2
                val decodeOptions = android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
                val bitmap = context.contentResolver.openInputStream(parsed)?.use {
                    android.graphics.BitmapFactory.decodeStream(it, null, decodeOptions)
                }
                bitmap?.asImageBitmap()
            }.getOrNull()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/** 预览对话框：图片铺满，文本显示前若干行。 */

@Composable
internal fun AttachmentPreviewDialog(
    attachment: AssistantAttachment,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(attachment.name) },
        text = {
            if (attachment.isImage) {
                val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, attachment.uri) {
                    value = withContext(Dispatchers.IO) {
                        runCatching {
                            val parsed = android.net.Uri.parse(attachment.uri)
                            val options = android.graphics.BitmapFactory.Options().apply { inSampleSize = 2 }
                            context.contentResolver.openInputStream(parsed)?.use {
                                android.graphics.BitmapFactory.decodeStream(it, null, options)?.asImageBitmap()
                            }
                        }.getOrNull()
                    }
                }
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                } ?: Text(stringResource(R.string.assistant_attach_file))
            } else {
                val text by produceState("", attachment.uri) {
                    value = withContext(Dispatchers.IO) {
                        runCatching {
                            context.contentResolver.openInputStream(android.net.Uri.parse(attachment.uri))
                                ?.bufferedReader()?.use { reader -> reader.readText().take(4000) }
                        }.getOrNull().orEmpty()
                    }
                }
                Text(text = text, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.script_picker_done))
            }
        }
    )
}

/** 人类可读的体积。 */

internal fun formatSize(bytes: Long): String = when {
    bytes <= 0L -> ""
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    else -> String.format("%.1f MB", bytes / 1024.0 / 1024.0)
}

/**
 * 会话列表对话框。
 *
 * 展示全部会话与消息条数，可新建、切换、归档与删除；
 * 顶部标注当前占用额度，超限时由存储层自动归档最旧的会话。
 */

internal val IMAGE_MARKER = Regex("<image\\s+uuid=\"([^\"]+)\"[^>]*/?>")

/** 消息里的图片：按 UUID 从文件库取样加载。 */

@Composable
internal fun MessageImage(uuid: String) {
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, uuid) {
        val loaded: androidx.compose.ui.graphics.ImageBitmap? = withContext(Dispatchers.IO) {
            runCatching {
                val repository = org.koin.core.context.GlobalContext.get()
                    .get<me.kafuuneko.rpclient.libs.room.repository.FileRepository>()
                repository.loadSampledBitmap(uuid, 720, 720)?.asImageBitmap()
            }.getOrNull()
        }
        value = loaded
    }
    val image = bitmap
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(
            text = "（图片加载失败）",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
