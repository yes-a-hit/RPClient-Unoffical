package me.kafuuneko.rpclient.feature.main.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.main.model.MainChatSessionGroup
import me.kafuuneko.rpclient.feature.main.model.MainGenerationParameter
import me.kafuuneko.rpclient.feature.main.model.MainHomeContentTab
import me.kafuuneko.rpclient.feature.main.model.MainHomeItemSelection
import me.kafuuneko.rpclient.feature.main.model.MainHomeItemType
import me.kafuuneko.rpclient.feature.main.model.MainProviderItem
import me.kafuuneko.rpclient.feature.main.model.items.MainChatSessionItem
import me.kafuuneko.rpclient.feature.main.model.items.MainGroupChatSessionItem
import me.kafuuneko.rpclient.feature.main.model.items.MainHomeContentItem
import me.kafuuneko.rpclient.feature.main.model.items.MainAdventureItem
import me.kafuuneko.rpclient.feature.main.model.items.MainStoryItem
import me.kafuuneko.rpclient.feature.main.presentation.MainAppearanceSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainDebugSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainPokeActionState
import me.kafuuneko.rpclient.feature.main.presentation.MainDialogState
import me.kafuuneko.rpclient.feature.main.presentation.MainGenerationParametersState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeResourceState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeSelectionState
import me.kafuuneko.rpclient.feature.main.presentation.MainHomeState
import me.kafuuneko.rpclient.feature.main.presentation.MainPage
import me.kafuuneko.rpclient.feature.main.presentation.MainPromptBehaviorState
import me.kafuuneko.rpclient.feature.main.presentation.MainProviderPostProcessingState
import me.kafuuneko.rpclient.feature.main.presentation.MainProviderSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentChatsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentGroupChatsState
import me.kafuuneko.rpclient.feature.main.presentation.MainRecentStoriesState
import me.kafuuneko.rpclient.feature.main.presentation.MainSettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummaryInjectionState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummarySettingsState
import me.kafuuneko.rpclient.feature.main.presentation.MainSummarySettingsTab
import me.kafuuneko.rpclient.feature.main.presentation.MainUiIntent
import me.kafuuneko.rpclient.feature.main.presentation.MainUiState
import me.kafuuneko.rpclient.feature.main.presentation.MainUserAvatarState
import me.kafuuneko.rpclient.feature.main.presentation.MainUserIdentityState
import me.kafuuneko.rpclient.feature.main.presentation.MainWorldInfoBudgetState
import me.kafuuneko.rpclient.libs.prompt.model.ExampleDialogueBehavior
import me.kafuuneko.rpclient.libs.prompt.model.PromptPostProcessingMode
import me.kafuuneko.rpclient.libs.prompt.model.SummaryInjectionPosition
import me.kafuuneko.rpclient.libs.prompt.model.SummaryInjectionRole
import me.kafuuneko.rpclient.libs.theme.AppThemeMode
import me.kafuuneko.rpclient.model.TokenPreset
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.AppInputDialog
import me.kafuuneko.rpclient.ui.dialog.AppPromptEditorDialog
import me.kafuuneko.rpclient.ui.dialog.NumericEditDialog
import me.kafuuneko.rpclient.ui.dialog.NumericEditQuickOption
import me.kafuuneko.rpclient.ui.dialog.SliderConfig
import me.kafuuneko.rpclient.ui.theme.AccentAmberColor
import me.kafuuneko.rpclient.ui.theme.AccentBlueColor
import me.kafuuneko.rpclient.ui.theme.AccentEmeraldColor
import me.kafuuneko.rpclient.ui.theme.AccentIndigoColor
import me.kafuuneko.rpclient.ui.theme.AccentPinkColor
import me.kafuuneko.rpclient.ui.theme.AccentRedColor
import me.kafuuneko.rpclient.ui.theme.AccentSkyColor
import me.kafuuneko.rpclient.ui.theme.AccentVioletColor
import me.kafuuneko.rpclient.ui.theme.AppTheme
import me.kafuuneko.rpclient.ui.theme.ProviderAvailableColor
import me.kafuuneko.rpclient.ui.theme.ProviderDisabledColor
import me.kafuuneko.rpclient.ui.theme.ProviderPendingColor
import me.kafuuneko.rpclient.ui.theme.getMacaronColor
import me.kafuuneko.rpclient.ui.widgets.MacroDictionaryDialog
import me.kafuuneko.rpclient.ui.widgets.PokeActionEditorDialog
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.RpIconBubble
import me.kafuuneko.rpclient.ui.widgets.RpInfoCard
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpMacroActionBar
import me.kafuuneko.rpclient.ui.widgets.RpMetaRow
import me.kafuuneko.rpclient.ui.widgets.RpPageTitle
import me.kafuuneko.rpclient.ui.widgets.RpPercentageSlider
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.RpSettingsDivider
import me.kafuuneko.rpclient.ui.widgets.RpSettingsGroup
import me.kafuuneko.rpclient.ui.widgets.RpSettingsSwitchTile
import me.kafuuneko.rpclient.ui.widgets.RpSettingsTile
import me.kafuuneko.rpclient.ui.widgets.RpSettingsValueTile
import me.kafuuneko.rpclient.ui.widgets.rememberBoundTextFieldState
import me.kafuuneko.rpclient.utils.rememberPromptMacroOutputTransformation
import androidx.compose.material.icons.rounded.Image as ImageIcon

@Composable
internal fun HomePage(
    state: MainHomeState,
    emit: MainUiIntent.() -> Unit
) {
    val selectionState = state.selectionState as? MainHomeSelectionState.Selecting
    val multiSelectMode = selectionState != null
    val selectedItems = selectionState?.selectedItems.orEmpty()

    RpLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = 18.dp,
            top = 8.dp,
            end = 18.dp,
            bottom = 110.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.statusBarsPadding())
        }
        if (multiSelectMode) {
            item {
                Text(
                    text = stringResource(R.string.selected_count, selectedItems.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }
        } else {
            homeEntryItems(state.resourceState, emit)
        }
        homeContentSection(
            state = state,
            multiSelectMode = multiSelectMode,
            selectedItems = selectedItems,
            emit = emit
        )
    }
}

internal fun LazyListScope.homeEntryItems(
    resourceState: MainHomeResourceState,
    emit: MainUiIntent.() -> Unit
) {
    // 五个一级入口统一成竖排小卡：一行放下五个，标题在图标下方
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeEntryTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.AddComment,
                title = stringResource(R.string.new_session),
                onClick = { MainUiIntent.OpenCreateChat.emit() }
            )
            HomeEntryTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Groups,
                title = stringResource(R.string.group_chat),
                onClick = { MainUiIntent.OpenCreateGroupChat.emit() }
            )
            HomeEntryTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.AutoStories,
                title = stringResource(R.string.story_create_story),
                onClick = { MainUiIntent.OpenCreateStory.emit() }
            )
            HomeEntryTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.SmartToy,
                title = stringResource(R.string.assistant_entry_title),
                onClick = { MainUiIntent.OpenAssistant.emit() }
            )
            HomeEntryTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Explore,
                title = stringResource(R.string.adventure_short),
                onClick = { MainUiIntent.OpenAdventure.emit() }
            )
        }
    }
    item {
        HomeAssetDock(resourceState, emit)
    }
}

internal fun LazyListScope.homeContentSection(
    state: MainHomeState,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.recent_content),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!multiSelectMode) {
                // 用 FlowRow 换行而不是横滑：筛选条一共五项，
                // 藏到屏幕外玩家根本不知道还有一个
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = state.selectedContentTab == MainHomeContentTab.All,
                        onClick = {
                            MainUiIntent.SelectHomeContentTab(MainHomeContentTab.All).emit()
                        },
                        label = { Text(stringResource(R.string.home_session_filter_all)) }
                    )
                    FilterChip(
                        selected = state.selectedContentTab == MainHomeContentTab.Single,
                        onClick = {
                            MainUiIntent.SelectHomeContentTab(MainHomeContentTab.Single).emit()
                        },
                        label = { Text(stringResource(R.string.home_session_filter_single)) }
                    )
                    FilterChip(
                        selected = state.selectedContentTab == MainHomeContentTab.Group,
                        onClick = {
                            MainUiIntent.SelectHomeContentTab(MainHomeContentTab.Group).emit()
                        },
                        label = { Text(stringResource(R.string.home_session_filter_group)) }
                    )
                    FilterChip(
                        selected = state.selectedContentTab == MainHomeContentTab.Story,
                        onClick = {
                            MainUiIntent.SelectHomeContentTab(MainHomeContentTab.Story).emit()
                        },
                        label = { Text(stringResource(R.string.home_content_filter_story)) }
                    )
                    // 冒险单独一档，便于直接找进行中的那一局
                    FilterChip(
                        selected = state.selectedContentTab == MainHomeContentTab.Adventure,
                        onClick = {
                            MainUiIntent.SelectHomeContentTab(MainHomeContentTab.Adventure).emit()
                        },
                        label = { Text(stringResource(R.string.adventure_short)) }
                    )
                }
            }
        }
    }

    when (state.selectedContentTab) {
        // 冒险存档同样汇入全部列表，单独一档时只筛出冒险
        MainHomeContentTab.Adventure -> {
            val adventures = state.allRecentItems.filterIsInstance<MainAdventureItem>()
            if (adventures.isEmpty()) {
                // 没有存档时给一个直达入口，而不是空白提示
                item {
                    RecentAdventureEntryCard { emit(MainUiIntent.OpenAdventure) }
                }
            } else {
                recentAllItems(
                    items = adventures,
                    multiSelectMode = multiSelectMode,
                    selectedItems = selectedItems,
                    emit = emit
                )
            }
        }

        MainHomeContentTab.All -> {
            if (state.allRecentItems.isEmpty()) {
                item {
                    RpInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.ChatBubble,
                        title = stringResource(R.string.no_recent_content),
                        subtitle = stringResource(R.string.no_recent_content_desc)
                    )
                }
            } else {
                recentAllItems(
                    items = state.allRecentItems,
                    multiSelectMode = multiSelectMode,
                    selectedItems = selectedItems,
                    emit = emit
                )
            }
        }

        MainHomeContentTab.Single -> {
            when (state.recentChatsState) {
                MainRecentChatsState.Empty -> item {
                    RpInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.ChatBubble,
                        title = stringResource(R.string.no_recent_chats),
                        subtitle = stringResource(R.string.no_recent_chats_desc)
                    )
                }

                is MainRecentChatsState.Content -> recentChatSessionItems(
                    state = state.recentChatsState,
                    multiSelectMode = multiSelectMode,
                    selectedItems = selectedItems,
                    emit = emit
                )
            }
        }

        MainHomeContentTab.Group -> {
            when (state.recentGroupChatsState) {
                MainRecentGroupChatsState.Empty -> item {
                    RpInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.Groups,
                        title = stringResource(R.string.no_group_chats),
                        subtitle = stringResource(R.string.no_group_chats_desc)
                    )
                }

                is MainRecentGroupChatsState.Content -> recentGroupChatSessionItems(
                    state = state.recentGroupChatsState,
                    multiSelectMode = multiSelectMode,
                    selectedItems = selectedItems,
                    emit = emit
                )
            }
        }

        MainHomeContentTab.Story -> {
            when (state.recentStoriesState) {
                MainRecentStoriesState.Empty -> item {
                    RpInfoCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.AutoStories,
                        title = stringResource(R.string.story_empty_title),
                        subtitle = stringResource(R.string.story_empty_subtitle)
                    )
                }

                is MainRecentStoriesState.Content -> recentStoryItems(
                    state = state.recentStoriesState,
                    multiSelectMode = multiSelectMode,
                    selectedItems = selectedItems,
                    emit = emit
                )
            }
        }
    }
}

internal fun LazyListScope.recentAllItems(
    items: List<MainHomeContentItem>,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    items(
        items = items,
        key = { item ->
            when (item) {
                is MainChatSessionItem -> "session-${item.id}"
                is MainGroupChatSessionItem -> "group-session-${item.id}"
                is MainStoryItem -> "story-${item.id}"
                is MainAdventureItem -> "adventure-${item.playId}"
            }
        }
    ) { item ->
        when (item) {
            is MainChatSessionItem -> RecentChatSessionCard(
                session = item,
                multiSelectMode = multiSelectMode,
                selectedItems = selectedItems,
                emit = emit
            )

            is MainGroupChatSessionItem -> RecentGroupChatSessionCard(
                session = item,
                multiSelectMode = multiSelectMode,
                selectedItems = selectedItems,
                emit = emit
            )

            is MainStoryItem -> RecentStoryCard(
                story = item,
                multiSelectMode = multiSelectMode,
                selectedItems = selectedItems,
                emit = emit
            )

            is MainAdventureItem -> RecentAdventureCard(
                item = item,
                emit = emit
            )
        }
    }
}

internal fun LazyListScope.recentChatSessionItems(
    state: MainRecentChatsState.Content,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    state.sessionGroups.forEach { group ->
        val characterId = group.characterId
        val expanded = characterId !in state.collapsedCharacterIds
        item(key = "character-$characterId") {
            SessionCharacterHeader(
                modifier = Modifier.animateItem(),
                characterName = group.characterName,
                sessionCount = group.sessions.size,
                expanded = expanded,
                onClick = { MainUiIntent.ToggleSessionGroup(characterId).emit() }
            )
        }
        if (!expanded) return@forEach
        items(
            items = group.sessions,
            key = { session -> "session-${session.id}" }
        ) { session ->
            RecentChatSessionCard(
                session = session,
                multiSelectMode = multiSelectMode,
                selectedItems = selectedItems,
                emit = emit
            )
        }
    }
}

internal fun LazyListScope.recentGroupChatSessionItems(
    state: MainRecentGroupChatsState.Content,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    items(
        items = state.sessions,
        key = { "group-session-${it.id}" }
    ) { session ->
        RecentGroupChatSessionCard(
            session = session,
            multiSelectMode = multiSelectMode,
            selectedItems = selectedItems,
            emit = emit
        )
    }
}

internal fun LazyListScope.recentStoryItems(
    state: MainRecentStoriesState.Content,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    items(
        items = state.stories,
        key = { "story-${it.id}" }
    ) { story ->
        RecentStoryCard(
            story = story,
            multiSelectMode = multiSelectMode,
            selectedItems = selectedItems,
            emit = emit
        )
    }
}

@Composable
internal fun LazyItemScope.RecentChatSessionCard(
    session: MainChatSessionItem,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    val selection = MainHomeItemSelection(MainHomeItemType.Chat, session.id)
    HomeContentCard(
        modifier = Modifier.animateItem(),
        accentKey = session.characterName,
        icon = Icons.Rounded.ChatBubble,
        title = session.title,
        preview = session.preview,
        metadata = listOf(
            session.characterName,
            stringResource(R.string.message_count, session.messageCount),
            session.updatedAt
        ),
        multiSelectMode = multiSelectMode,
        selected = selection in selectedItems,
        onClick = {
            if (multiSelectMode) {
                MainUiIntent.ToggleItemSelection(selection).emit()
            } else {
                MainUiIntent.OpenChat(session.id).emit()
            }
        },
        onLongClick = {
            if (!multiSelectMode) MainUiIntent.EnterMultiSelect(selection).emit()
        },
        trailingContent = {
            HomeItemMenu(
                onRename = {
                    MainUiIntent.ShowRenameItemDialog(selection).emit()
                },
                onDelete = {
                    MainUiIntent.EnterMultiSelect(selection).emit()
                    MainUiIntent.ShowDeleteSelectedDialog.emit()
                }
            )
        }
    )
}

@Composable
internal fun LazyItemScope.RecentGroupChatSessionCard(
    session: MainGroupChatSessionItem,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    val selection = MainHomeItemSelection(MainHomeItemType.GroupChat, session.id)
    HomeContentCard(
        modifier = Modifier.animateItem(),
        accentKey = session.title,
        icon = Icons.Rounded.Groups,
        title = session.title,
        preview = session.preview,
        metadata = listOf(
            session.memberNames,
            stringResource(R.string.message_count, session.messageCount),
            session.updatedAt
        ),
        multiSelectMode = multiSelectMode,
        selected = selection in selectedItems,
        onClick = {
            if (multiSelectMode) {
                MainUiIntent.ToggleItemSelection(selection).emit()
            } else {
                MainUiIntent.OpenGroupChat(session.id).emit()
            }
        },
        onLongClick = {
            if (!multiSelectMode) MainUiIntent.EnterMultiSelect(selection).emit()
        },
        trailingContent = {
            HomeItemMenu(
                onRename = {
                    MainUiIntent.ShowRenameItemDialog(selection).emit()
                },
                onDelete = {
                    MainUiIntent.EnterMultiSelect(selection).emit()
                    MainUiIntent.ShowDeleteSelectedDialog.emit()
                }
            )
        }
    )
}

@Composable
internal fun LazyItemScope.RecentStoryCard(
    story: MainStoryItem,
    multiSelectMode: Boolean,
    selectedItems: Set<MainHomeItemSelection>,
    emit: MainUiIntent.() -> Unit
) {
    val selection = MainHomeItemSelection(
        type = MainHomeItemType.Story,
        itemId = story.id.toString()
    )
    HomeContentCard(
        modifier = Modifier.animateItem(),
        accentKey = story.title,
        icon = Icons.Rounded.AutoStories,
        title = story.title,
        preview = story.preview.ifBlank {
            stringResource(R.string.story_empty_document_preview)
        },
        metadata = listOf(
            stringResource(
                R.string.story_character_count,
                story.contentCharacterCount
            ),
            story.updatedAt
        ),
        multiSelectMode = multiSelectMode,
        selected = selection in selectedItems,
        onClick = {
            if (multiSelectMode) {
                MainUiIntent.ToggleItemSelection(selection).emit()
            } else {
                MainUiIntent.OpenStory(story.id).emit()
            }
        },
        onLongClick = {
            if (!multiSelectMode) MainUiIntent.EnterMultiSelect(selection).emit()
        },
        trailingContent = {
            HomeItemMenu(
                onRename = {
                    MainUiIntent.ShowRenameItemDialog(selection).emit()
                },
                onDelete = {
                    MainUiIntent.EnterMultiSelect(selection).emit()
                    MainUiIntent.ShowDeleteSelectedDialog.emit()
                }
            )
        }
    )
}
