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
fun StoryCreateLayout(
    uiState: StoryCreateUiState,
    emit: StoryCreateUiIntent.() -> Unit,
    onPickNovelFiles: () -> Unit
) {
    BackHandler(enabled = uiState is StoryCreateUiState.Normal) {
        StoryCreateUiIntent.Back.emit()
    }
    when (uiState) {
        StoryCreateUiState.None -> Unit
        is StoryCreateUiState.Normal -> StoryCreateNormal(uiState, emit, onPickNovelFiles)
        is StoryCreateUiState.Finished -> StoryCreateLayout(uiState.previous, {}, onPickNovelFiles)
    }
}

@Composable
internal fun StoryCreateNormal(
    state: StoryCreateUiState.Normal,
    emit: StoryCreateUiIntent.() -> Unit,
    onPickNovelFiles: () -> Unit
) {
    var expandedLorebookIds by remember { mutableStateOf(emptySet<Long>()) }
    val searchingLorebooks = state.lorebookQuery.isNotBlank()
    val controlsEnabled = state.loadState == StoryCreateLoadState.Ready

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.story_create_story),
                onBack = { StoryCreateUiIntent.Back.emit() }
            )
        },
        bottomBar = {
            CreateBottomBar(
                loadState = state.loadState,
                emit = emit
            )
        }
    ) { padding ->
        RpLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                RpPageTitle(
                    title = stringResource(R.string.story_create_story),
                    subtitle = stringResource(R.string.story_create_subtitle)
                )
            }
            if (state.loadState == StoryCreateLoadState.Loading) {
                item { LoadingRow() }
            } else {
                item { StoryTitleField(state.form, controlsEnabled, emit) }
                item { NovelImportSection(state, controlsEnabled, onPickNovelFiles, emit) }
                item { UserPersonaOption(state.form, controlsEnabled, emit) }
                item {
                    RpSectionHeader(
                        title = stringResource(R.string.story_character_references),
                        action = stringResource(
                            R.string.selected_count,
                            state.selectedCharacterCount
                        )
                    )
                }
                if (state.characters.isNotEmpty()) {
                    item {
                        CharacterSearchField(
                            query = state.characterQuery,
                            enabled = controlsEnabled,
                            onQueryChange = {
                                StoryCreateUiIntent.ChangeCharacterQuery(it).emit()
                            }
                        )
                    }
                }
                if (state.characters.isEmpty()) {
                    item {
                        EmptyCard(
                            icon = Icons.Rounded.Person,
                            text = stringResource(R.string.story_no_characters)
                        )
                    }
                } else if (state.visibleCharacters.isEmpty()) {
                    item {
                        EmptyCharacterSearchCard()
                    }
                }
                items(state.visibleCharacters, key = { "character-${it.id}" }) { character ->
                    CharacterOption(
                        character = character,
                        selected = character.id in state.form.selectedCharacterIds,
                        activationMode = state.form.activationModeOf(character.id),
                        enabled = controlsEnabled,
                        onClick = { StoryCreateUiIntent.ToggleCharacter(character.id).emit() },
                        onActivationModeClick = { activationMode ->
                            StoryCreateUiIntent.SetCharacterActivationMode(
                                character.id,
                                activationMode
                            ).emit()
                        }
                    )
                }
                item {
                    RpSectionHeader(title = stringResource(R.string.enabled_world_book_entries))
                }
                item {
                    Text(
                        text = stringResource(R.string.story_lorebook_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.lorebookGroups.isNotEmpty()) {
                    item {
                        LorebookSearchField(
                            query = state.lorebookQuery,
                            enabled = controlsEnabled,
                            onQueryChange = {
                                StoryCreateUiIntent.ChangeLorebookQuery(it).emit()
                            }
                        )
                    }
                }
                if (state.lorebookGroups.isEmpty()) {
                    item {
                        EmptyCard(
                            icon = Icons.Rounded.Book,
                            text = stringResource(R.string.no_world_book_entries_selectable)
                        )
                    }
                } else if (state.visibleLorebookGroups.isEmpty()) {
                    item {
                        EmptyCard(
                            icon = Icons.Rounded.Search,
                            text = stringResource(R.string.no_world_book_search_results)
                        )
                    }
                }
                items(state.visibleLorebookGroups, key = { "lorebook-${it.lorebookId}" }) { group ->
                    val selectedCount = state.lorebookGroups
                        .firstOrNull { it.lorebookId == group.lorebookId }
                        ?.entries
                        ?.count { it.id in state.form.selectedLorebookEntryIds }
                        ?: 0
                    LorebookGroupOption(
                        group = group,
                        selectedEntryIds = state.form.selectedLorebookEntryIds,
                        selectedCount = selectedCount,
                        expanded = searchingLorebooks || group.lorebookId in expandedLorebookIds,
                        enabled = controlsEnabled,
                        onExpandedChange = {
                            expandedLorebookIds = expandedLorebookIds.toggle(group.lorebookId)
                        },
                        emit = emit
                    )
                }
            }
        }
    }
}

/**
 * 本地小说导入区块。
 *
 * 同一入口承担两种来源：整本文件由解析器自动分章，散装章节文件按文件名排序合并。
 * 解析结果只做预览，正文保存在 UiState 的预览对象里，创建故事时一次性写入。
 */

@Composable
internal fun CreateBottomBar(
    loadState: StoryCreateLoadState,
    emit: StoryCreateUiIntent.() -> Unit
) {
    val creating = loadState == StoryCreateLoadState.Creating
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.28f)
        )
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            enabled = loadState == StoryCreateLoadState.Ready,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 13.dp),
            onClick = { StoryCreateUiIntent.CreateStory.emit() }
        ) {
            if (creating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.story_create_story))
            }
        }
    }
}

@Composable
internal fun LoadingRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun EmptyCard(
    icon: ImageVector,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RpIconBubble(icon)
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
internal fun StoryCreateLayoutPreview() {
    AppTheme(dynamicColor = false) {
        val lorebookGroup = StoryCreateLorebookGroupItem(
            lorebookId = 2L,
            lorebookName = "Fog Harbor",
            entries = listOf(
                StoryCreateLorebookEntryItem(
                    id = 10L,
                    lorebookName = "Fog Harbor",
                    name = "Old Town",
                    content = "A rain-soaked district surrounding the central archive.",
                    keywords = listOf("old town"),
                    constant = false,
                    order = 100,
                    depth = 0
                )
            )
        )
        StoryCreateLayout(
            uiState = StoryCreateUiState.Normal(
                loadState = StoryCreateLoadState.Ready,
                form = StoryCreateForm(
                    title = "Rain over the old city",
                    characterActivationModes = mapOf(
                        1L to StoryCreateCharacterActivationMode.Auto
                    ),
                    selectedLorebookEntryIds = setOf(10L)
                ),
                characters = listOf(
                    StoryCreateCharacterItem(
                        id = 1L,
                        name = "Lyra",
                        description = "An archivist following a trail through the old city.",
                        tags = listOf("Mystery"),
                        linkedLorebookId = 2L,
                        linkedLorebookName = "Fog Harbor"
                    )
                ),
                lorebookGroups = listOf(lorebookGroup),
                visibleLorebookGroups = listOf(lorebookGroup)
            ),
            emit = {},
            onPickNovelFiles = {}
        )
    }
}
