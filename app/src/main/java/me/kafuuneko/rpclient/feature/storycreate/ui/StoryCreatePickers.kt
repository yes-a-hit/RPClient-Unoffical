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
internal fun CharacterOption(
    character: StoryCreateCharacterItem,
    selected: Boolean,
    activationMode: StoryCreateCharacterActivationMode,
    enabled: Boolean,
    onClick: () -> Unit,
    onActivationModeClick: (StoryCreateCharacterActivationMode) -> Unit
) {
    val accent = getMacaronColor(character.name)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            }
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
            RpAvatar(
                text = character.name.firstOrNull()?.uppercase() ?: "?",
                color = accent
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = character.description.ifBlank {
                        stringResource(R.string.no_description)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                if (selected) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            }
            if (selected) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activationMode == StoryCreateCharacterActivationMode.Primary,
                        onClick = {
                            onActivationModeClick(StoryCreateCharacterActivationMode.Primary)
                        },
                        enabled = enabled,
                        label = { Text(stringResource(R.string.story_character_primary)) }
                    )
                    FilterChip(
                        selected = activationMode == StoryCreateCharacterActivationMode.Always,
                        onClick = {
                            onActivationModeClick(StoryCreateCharacterActivationMode.Always)
                        },
                        enabled = enabled,
                        label = { Text(stringResource(R.string.story_character_always)) }
                    )
                    FilterChip(
                        selected = activationMode == StoryCreateCharacterActivationMode.Auto,
                        onClick = {
                            onActivationModeClick(StoryCreateCharacterActivationMode.Auto)
                        },
                        enabled = enabled,
                        label = { Text(stringResource(R.string.story_character_auto)) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun UserPersonaOption(
    form: StoryCreateForm,
    enabled: Boolean,
    emit: StoryCreateUiIntent.() -> Unit
) {
    StoryUserPersonaCard(
        checked = form.includeUserPersona,
        onCheckedChange = {
            StoryCreateUiIntent.SetIncludeUserPersona(it).emit()
        },
        enabled = enabled
    )
}

@Composable
internal fun CharacterSearchField(
    query: String,
    enabled: Boolean,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        placeholder = {
            Text(
                text = stringResource(R.string.search_characters),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
internal fun EmptyCharacterSearchCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(stringResource(R.string.no_matching_characters))
        }
    }
}

@Composable
internal fun LorebookGroupOption(
    group: StoryCreateLorebookGroupItem,
    selectedEntryIds: Set<Long>,
    selectedCount: Int,
    expanded: Boolean,
    enabled: Boolean,
    onExpandedChange: () -> Unit,
    emit: StoryCreateUiIntent.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled, onClick = onExpandedChange),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) {
                        Icons.Rounded.KeyboardArrowDown
                    } else {
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Checkbox(
                    checked = group.entryCount > 0 && selectedCount == group.entryCount,
                    enabled = enabled,
                    onCheckedChange = {
                        StoryCreateUiIntent.ToggleLorebook(group.lorebookId).emit()
                    }
                )
                Spacer(Modifier.width(8.dp))
                RpIconBubble(Icons.Rounded.Book)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.lorebookName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.enabled_entries_count,
                            selectedCount,
                            group.entryCount
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (expanded) {
                group.entries.forEach { entry ->
                    LorebookEntryOption(
                        entry = entry,
                        selected = entry.id in selectedEntryIds,
                        enabled = enabled,
                        onClick = {
                            StoryCreateUiIntent.ToggleLorebookEntry(entry.id).emit()
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun LorebookEntryOption(
    entry: StoryCreateLorebookEntryItem,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val constantLabel = stringResource(R.string.entry_constant)
    val orderDepthLabel = stringResource(
        R.string.entry_order_depth,
        entry.order,
        entry.depth
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }
        ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selected,
                enabled = enabled,
                onCheckedChange = { onClick() }
            )
            Spacer(Modifier.width(8.dp))
            RpIconBubble(Icons.Rounded.Book)
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                RpTagRow(
                    tags = buildList {
                        add(entry.lorebookName)
                        if (entry.constant) add(constantLabel)
                        add(orderDepthLabel)
                    }
                )
            }
            }
        }
    }
}

@Composable
internal fun LorebookSearchField(
    query: String,
    enabled: Boolean,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_world_books),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    enabled = enabled,
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.clear_search)
                    )
                }
            }
        },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}
