package me.kafuuneko.rpclient.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kafuuneko.rpclient.libs.gallery.GalleryImage
import me.kafuuneko.rpclient.libs.gallery.GalleryStore
import me.kafuuneko.rpclient.libs.room.model.MessageImagePolicy
import me.kafuuneko.rpclient.libs.room.repository.FileRepository
import org.koin.core.context.GlobalContext

/**
 * 从应用内图库挑图。
 *
 * 和系统选择器是两条不同的路：系统选择器从手机相册导入新图，这里是从**已经导入过、
 * 打过标签、分过相册**的图里挑。两者都需要——导入是为了把图存进来，挑是为了把
 * 存好的图在聊天里用出去。
 *
 * 数据在对话框内部自己拉，不走 ViewModel：挑图是一次性的浏览行为，选中即回传，
 * 没必要把整份图库塞进聊天页状态里跟着每次重绘一起流动。
 *
 * @param maxSelectable 一张消息最多能带几张图，超出的部分不允许再勾选。
 * @param onDismiss 关闭对话框，不带走任何选择。
 * @param onConfirm 回传选中的图片，按用户点选顺序。
 */
@Composable
fun GalleryPickerDialog(
    maxSelectable: Int = MessageImagePolicy.MAX_IMAGES_PER_MESSAGE,
    onDismiss: () -> Unit,
    onConfirm: (List<GalleryImage>) -> Unit
) {
    val images by produceState<List<GalleryImage>?>(null) {
        value = withContext(Dispatchers.IO) {
            runCatching { GlobalContext.get().get<GalleryStore>().list() }.getOrDefault(emptyList())
        }
    }
    // 选中的是 id 而不是对象，图库刷新后下标不会错位。
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var albumFilter by remember { mutableStateOf("") }

    val all = images
    val albums = remember(all) {
        all.orEmpty().map { it.album }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val shown = remember(all, albumFilter) {
        all.orEmpty()
            .filter { albumFilter.isBlank() || it.album == albumFilter }
            .sortedByDescending { it.addedAt }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("从图库选") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when {
                    all == null -> Text(
                        "正在读取图库…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    all.isEmpty() -> Text(
                        "图库里还没有图片。先去「图库」导入，再回来挑。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    else -> {
                        // 相册筛选：只在真分了相册时才显示，没分过就别摆一排空按钮。
                        if (albums.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(bottom = 8.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = albumFilter.isBlank(),
                                        onClick = { albumFilter = "" },
                                        label = { Text("全部") }
                                    )
                                }
                                items(albums) { album ->
                                    FilterChip(
                                        selected = albumFilter == album,
                                        onClick = { albumFilter = album },
                                        label = { Text(album) }
                                    )
                                }
                            }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(shown, key = { it.id }) { image ->
                                val picked = image.id in selectedIds
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedIds = when {
                                                picked -> selectedIds - image.id
                                                selectedIds.size >= maxSelectable -> selectedIds
                                                else -> selectedIds + image.id
                                            }
                                        }
                                ) {
                                    GalleryPickerThumbnail(uuid = image.fileUuid)
                                    if (picked) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                                )
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            "已选 ${selectedIds.size}/$maxSelectable",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedIds.isNotEmpty(),
                onClick = {
                    val chosen = all.orEmpty().filter { it.id in selectedIds }
                    onConfirm(chosen)
                }
            ) { Text("加入") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

/** 图库缩略图；解码失败显示占位图标，不让整格塌掉。 */
@Composable
private fun GalleryPickerThumbnail(uuid: String) {
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, uuid) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                GlobalContext.get()
                    .get<FileRepository>()
                    .loadSampledBitmap(uuid, 160, 160)
                    ?.asImageBitmap()
            }.getOrNull()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
