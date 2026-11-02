/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 MessageDocumentParsingTest 里的用例，它们描述了拆分的预期。
 */

package me.kafuuneko.rpclient.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 消息气泡里的一份附带文件。
 *
 * 默认收起，只显文件名和字数；点开才把正文铺出来。这样一条带几万字 docx 的消息
 * 在列表里仍然是一行的高度，不至于把上下十几条对话推出屏幕。
 */
@Composable
fun MessageDocumentCard(
    name: String,
    body: String?,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    documentId: String = ""
) {
    var expanded by remember(name) { mutableStateOf(false) }
    // 新消息的正文不在消息体里，展开时才去仓库取；旧消息直接就有
    var loaded by remember(documentId) { mutableStateOf(body) }
    // 取仓库失败不能让整个气泡炸掉——那会表现为「这条消息什么都不显示」
    val store = remember {
        runCatching {
            org.koin.core.context.GlobalContext.get()
                .get<me.kafuuneko.rpclient.libs.media.DocumentStore>()
        }.getOrNull()
    }
    LaunchedEffect(expanded, documentId) {
        if (!expanded || loaded != null || documentId.isBlank()) return@LaunchedEffect
        loaded = withContext(Dispatchers.IO) { runCatching { store?.load(documentId)?.text }.getOrNull() }
    }
    val charCount = loaded?.length ?: 0
    val container = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    }
    val onContainer = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(shape = RoundedCornerShape(12.dp), color = container) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = onContainer
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer,
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                    Text(
                        text = when {
                            loaded != null -> "$charCount 字" + if (expanded) " · 已展开" else " · 点击展开"
                            documentId.isBlank() -> "内容未随消息保存"
                            expanded -> "读取中…"
                            else -> "已附带" + " · 点击展开"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = onContainer.copy(alpha = 0.7f)
                    )
                }
                TextButton(
                    onClick = { expanded = !expanded },
                    colors = ButtonDefaults.textButtonColors(contentColor = onContainer)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(if (expanded) "收起" else "展开")
                }
            }
        }
        AnimatedVisibility(visible = expanded) {
            // 长行横向滚动而不是硬折：规范文档里的表格与代码折行后完全读不懂
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                SelectionContainer {
                    Text(
                        text = loaded ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(10.dp)
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}
