package me.kafuuneko.rpclient.feature.groupchat.ui
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Image as ImageIcon
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.media.MessageImageState
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatGenerationState
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatMemberItem
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatSceneItem
import me.kafuuneko.rpclient.feature.groupchat.model.GroupChatMessageItem
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatConversationState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatDialogState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatLoadState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatPage
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatSettingsState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiIntent
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiState
import me.kafuuneko.rpclient.libs.AppModel
import me.kafuuneko.rpclient.libs.core.ActivityPreview
import me.kafuuneko.rpclient.libs.poke.PokeAction
import me.kafuuneko.rpclient.libs.poke.PokeActions
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatActivationStrategy
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatCharacterCardMode
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatLorebookGroupItem
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatMessageSource
import me.kafuuneko.rpclient.model.MessageContentPart
import me.kafuuneko.rpclient.ui.dialog.AppConfirmDialog
import me.kafuuneko.rpclient.ui.dialog.AppDangerDialog
import me.kafuuneko.rpclient.ui.dialog.PromptInspectorDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialog
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogEntry
import me.kafuuneko.rpclient.ui.dialog.SessionLorebookDialogGroup
import me.kafuuneko.rpclient.ui.message.DraftAttachmentTray
import me.kafuuneko.rpclient.ui.message.MessageImageEditButton
import me.kafuuneko.rpclient.ui.message.MessageImageGallery
import me.kafuuneko.rpclient.ui.dialog.MessageImageViewerDialog
import me.kafuuneko.rpclient.ui.theme.getMacaronColor
import me.kafuuneko.rpclient.ui.dialog.AppDialogScaffold
import me.kafuuneko.rpclient.ui.widgets.AppTopBar
import me.kafuuneko.rpclient.ui.widgets.GalleryPickerDialog
import me.kafuuneko.rpclient.ui.widgets.MarkdownMessageText
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock
import me.kafuuneko.rpclient.ui.widgets.MessageDocumentCard
import me.kafuuneko.rpclient.ui.widgets.NoProviderBanner
import me.kafuuneko.rpclient.ui.widgets.PokeActionEditorDialog
import me.kafuuneko.rpclient.ui.widgets.RpAvatar
import me.kafuuneko.rpclient.ui.widgets.chatBackgroundLayer
import me.kafuuneko.rpclient.ui.widgets.RpLazyColumn
import me.kafuuneko.rpclient.ui.widgets.RpScrollableOutlinedTextField
import me.kafuuneko.rpclient.ui.widgets.RpSectionHeader
import me.kafuuneko.rpclient.ui.widgets.draggableLazyListScrollIndicator
import me.kafuuneko.rpclient.ui.widgets.groupchat.GroupChatLorebookSelector

@Composable
internal fun GroupMemberSettingsList(
    members: List<GroupChatMemberItem>,
    emitIntent: (GroupChatUiIntent) -> Unit
) {
    val currentMembers = rememberUpdatedState(members)
    val dragDropState = rememberGroupMemberDragDropState(
        onMove = { fromCharacterId, toCharacterId ->
            val current = currentMembers.value
            if (current.any { it.id == fromCharacterId } &&
                current.any { it.id == toCharacterId }
            ) {
                emitIntent(
                    GroupChatUiIntent.ReorderMember(fromCharacterId, toCharacterId)
                )
            }
        },
        onDragEnd = { emitIntent(GroupChatUiIntent.CommitMemberOrder) }
    )
    LaunchedEffect(members.map { it.id }) {
        dragDropState.retainCharacters(members.map { it.id }.toSet())
    }
    // 稳定组合键保证成员换位后，手势仍持续追踪同一角色
    members.forEachIndexed { index, member ->
        key(member.id) {
            DraggableGroupMemberSettingsRow(
                member = member,
                dragDropState = dragDropState,
                canMoveUp = index > 0,
                canMoveDown = index < members.lastIndex,
                canRemove = members.size > 2,
                onMoveUp = {
                    emitIntent(GroupChatUiIntent.MoveMember(member.id, -1))
                },
                onMoveDown = {
                    emitIntent(GroupChatUiIntent.MoveMember(member.id, 1))
                },
                onRemove = {
                    emitIntent(GroupChatUiIntent.RemoveMember(member.id))
                }
            )
        }
    }
}

/** 为单个成员行安装长按拖动手势与跟手位移。 */

@Composable
internal fun DraggableGroupMemberSettingsRow(
    member: GroupChatMemberItem,
    dragDropState: GroupMemberDragDropState,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    canRemove: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val dragging = dragDropState.draggingCharacterId == member.id
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                dragDropState.updateBounds(
                    characterId = member.id,
                    top = coordinates.positionInParent().y,
                    height = coordinates.size.height.toFloat()
                )
            }
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer {
                translationY = dragDropState.dragOffset(member.id)
            }
            .pointerInput(dragDropState, member.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { dragDropState.onDragStart(member.id) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDropState.onDrag(member.id, dragAmount.y)
                    },
                    onDragEnd = dragDropState::onDragEnd,
                    onDragCancel = dragDropState::onDragEnd
                )
            }
    ) {
        GroupMemberSettingsRow(
            member = member,
            canMoveUp = canMoveUp,
            canMoveDown = canMoveDown,
            canRemove = canRemove,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onRemove = onRemove
        )
    }
}

/** 创建成员卡片内部使用的长按拖动状态。 */

@Composable
internal fun rememberGroupMemberDragDropState(
    onMove: (fromCharacterId: Long, toCharacterId: Long) -> Unit,
    onDragEnd: () -> Unit
): GroupMemberDragDropState {
    val currentOnMove = rememberUpdatedState(onMove)
    val currentOnDragEnd = rememberUpdatedState(onDragEnd)
    return remember {
        GroupMemberDragDropState(
            mOnMove = { from, to -> currentOnMove.value(from, to) },
            mOnDragEnd = { currentOnDragEnd.value() }
        )
    }
}

/** 群聊设置卡片内成员行的长按拖动状态管理器。 */

internal class GroupMemberDragDropState(
    private val mOnMove: (fromCharacterId: Long, toCharacterId: Long) -> Unit,
    private val mOnDragEnd: () -> Unit
) {
    var draggingCharacterId by mutableStateOf<Long?>(null)
        private set

    private val mBounds = mutableMapOf<Long, GroupMemberBounds>()
    private var mDraggedDelta by mutableFloatStateOf(0f)
    private var mInitialTop = 0f
    private var mLastTargetCharacterId: Long? = null

    /** 清理已从群聊移除成员遗留的布局边界。 */
    fun retainCharacters(characterIds: Set<Long>) {
        mBounds.keys.retainAll(characterIds)
    }

    /** 记录成员在设置卡片坐标系中的最新布局边界。 */
    fun updateBounds(characterId: Long, top: Float, height: Float) {
        val previousTop = mBounds[characterId]?.top
        mBounds[characterId] = GroupMemberBounds(top, height)
        if (characterId == draggingCharacterId && previousTop != null && previousTop != top) {
            mLastTargetCharacterId = null
        }
    }

    /** 计算正在拖动的成员相对最新布局位置所需的视觉偏移。 */
    fun dragOffset(characterId: Long): Float {
        if (characterId != draggingCharacterId) return 0f
        val currentTop = mBounds[characterId]?.top ?: return 0f
        return mInitialTop + mDraggedDelta - currentTop
    }

    /** 从已完成布局的成员行开始一次拖动。 */
    fun onDragStart(characterId: Long) {
        val bounds = mBounds[characterId] ?: return
        draggingCharacterId = characterId
        mInitialTop = bounds.top
        mDraggedDelta = 0f
        mLastTargetCharacterId = null
    }

    /** 根据成员行覆盖相邻行的程度触发一次内存换位。 */
    fun onDrag(characterId: Long, deltaY: Float) {
        if (draggingCharacterId != characterId) return
        mDraggedDelta += deltaY
        val draggingBounds = mBounds[characterId] ?: return
        val start = mInitialTop + mDraggedDelta
        val end = start + draggingBounds.height
        // 仅沿当前手指移动方向查找最近的相邻目标，避免一次事件跨越多行
        val target = if (deltaY >= 0f) {
            mBounds.asSequence()
                .filter { (id, bounds) -> id != characterId && bounds.top > draggingBounds.top }
                .minByOrNull { (_, bounds) -> bounds.top }
                ?.takeIf { (_, bounds) -> end > bounds.top + bounds.height * 0.20f }
        } else {
            mBounds.asSequence()
                .filter { (id, bounds) -> id != characterId && bounds.top < draggingBounds.top }
                .maxByOrNull { (_, bounds) -> bounds.top }
                ?.takeIf { (_, bounds) -> start < bounds.top + bounds.height * 0.80f }
        }
        val targetCharacterId = target?.key ?: return
        if (targetCharacterId == mLastTargetCharacterId) return
        mLastTargetCharacterId = targetCharacterId
        mOnMove(characterId, targetCharacterId)
    }

    /** 结束或取消拖动并提交最终成员顺序。 */
    fun onDragEnd() {
        if (draggingCharacterId != null) mOnDragEnd()
        draggingCharacterId = null
        mDraggedDelta = 0f
        mInitialTop = 0f
        mLastTargetCharacterId = null
    }
}

/** 成员行在设置卡片坐标系中的纵向边界。 */

internal data class GroupMemberBounds(val top: Float, val height: Float)

/** 展示群成员及其排序、移除操作。 */

@Composable
internal fun GroupMemberSettingsRow(
    member: GroupChatMemberItem,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    canRemove: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RpAvatar(
                text = member.name.take(1),
                color = getMacaronColor(member.name),
                modifier = Modifier.size(38.dp),
                image = member.avatarImage
            )
            Text(
                text = member.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                Icon(Icons.Rounded.ArrowUpward, contentDescription = null)
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                Icon(Icons.Rounded.ArrowDownward, contentDescription = null)
            }
            IconButton(onClick = onRemove, enabled = canRemove) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = if (canRemove) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }
        }
    }
}
