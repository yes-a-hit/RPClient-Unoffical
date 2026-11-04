package me.kafuuneko.rpclient.feature.storycreate.ui
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.storycreate.model.NovelImportPreview
import me.kafuuneko.rpclient.feature.storycreate.model.StoryCreateCharacterActivationMode
import me.kafuuneko.rpclient.feature.storycreate.model.StoryImportState
import me.kafuuneko.rpclient.feature.storycreate.model.StoryCreateCharacterItem
import me.kafuuneko.rpclient.feature.storycreate.model.StoryCreateForm
import me.kafuuneko.rpclient.feature.storycreate.model.StoryCreateLorebookEntryItem
import me.kafuuneko.rpclient.feature.storycreate.model.StoryCreateLorebookGroupItem
import me.kafuuneko.rpclient.feature.storycreate.presentation.StoryCreateLoadState
import me.kafuuneko.rpclient.feature.storycreate.presentation.StoryCreateUiIntent
import me.kafuuneko.rpclient.feature.storycreate.presentation.StoryCreateUiState
import me.kafuuneko.rpclient.utils.toggle
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.getMacaronColor
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpTagRow
import me.kafuuneko.rpclient.ui.widgets.StoryUserPersonaCard

@Composable
internal fun NovelImportSection(
    state: StoryCreateUiState.Normal,
    enabled: Boolean,
    onPickNovelFiles: () -> Unit,
    emit: StoryCreateUiIntent.() -> Unit
) {
    val preview = state.importPreview
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.story_import_title),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = stringResource(R.string.story_import_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            when {
                state.importState == StoryImportState.Reading -> ImportReadingRow()
                preview != null -> ImportPreviewContent(state, preview, onPickNovelFiles, emit)
                else -> Button(
                    onClick = onPickNovelFiles,
                    enabled = enabled
                ) {
                    Text(stringResource(R.string.story_import_pick))
                }
            }
        }
    }
}

/** 解析中的占位提示。 */

@Composable
internal fun ImportReadingRow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Text(
            text = stringResource(R.string.story_import_reading),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 已解析内容的统计、章节顺序调整与后续操作。
 *
 * 顺序以章节列表为准：多文件合并时按文件名自然排序只是初值，
 * 文件名不规范时需要人工校正，因此这里直接给出上移下移入口。
 */

@Composable
internal fun ImportPreviewContent(
    state: StoryCreateUiState.Normal,
    preview: NovelImportPreview,
    onPickNovelFiles: () -> Unit,
    emit: StoryCreateUiIntent.() -> Unit
) {
    Text(
        text = stringResource(
            R.string.novel_import_summary,
            preview.fileCount,
            preview.chapterCount,
            preview.characterCount
        ),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    // 章节可能上百条，固定高度内滚动，避免卡片把整页撑开
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 260.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        preview.chapters.forEachIndexed { index, chapter ->
            ImportChapterRow(
                index = index,
                title = chapter.title.ifBlank { "#${index + 1}" },
                canMoveUp = index > 0,
                canMoveDown = index < preview.chapters.lastIndex,
                emit = emit
            )
        }
    }
    Text(
        text = stringResource(R.string.story_import_mode_hint),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary
    )
    // 提取会调用一次模型，运行态与结果态都要可见，避免重复点击
    if (state.extraction.running) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Text(
                text = stringResource(R.string.story_extract_running),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Button(onClick = { StoryCreateUiIntent.ExtractSettingsClick.emit() }) {
            Text(stringResource(R.string.story_extract_action))
        }
        if (state.extraction.hasResult) {
            Text(
                text = stringResource(
                    R.string.story_extract_result,
                    state.extraction.characterCount,
                    state.extraction.entryCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPickNovelFiles) {
            Text(stringResource(R.string.story_import_replace))
        }
        Button(onClick = { StoryCreateUiIntent.ClearImportedNovel.emit() }) {
            Text(stringResource(R.string.story_import_clear))
        }
    }
}

/** 单条章节预览行：序号、标题与上下移动按钮。 */

@Composable
internal fun ImportChapterRow(
    index: Int,
    title: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    emit: StoryCreateUiIntent.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${index + 1}.",
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = { StoryCreateUiIntent.MoveImportedChapter(index, -1).emit() },
            enabled = canMoveUp,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = stringResource(R.string.story_import_move_up),
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(
            onClick = { StoryCreateUiIntent.MoveImportedChapter(index, 1).emit() },
            enabled = canMoveDown,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.story_import_move_down),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
internal fun StoryTitleField(
    form: StoryCreateForm,
    enabled: Boolean,
    emit: StoryCreateUiIntent.() -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = form.title,
        onValueChange = { StoryCreateUiIntent.ChangeTitle(it).emit() },
        label = { Text(stringResource(R.string.story_title)) },
        leadingIcon = { Icon(Icons.Rounded.AutoStories, contentDescription = null) },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}
