package me.kafuuneko.rpclient.feature.groupchat.presentation

import android.net.Uri
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatActivationStrategy
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatCharacterCardMode

/** 群聊页面可接收的全部用户意图和生命周期事件。 */
sealed class GroupChatUiIntent {
    /** 从摘要额度提示前往全局设置。 */
    data object OpenSummarySettings : GroupChatUiIntent()

    data object RetryImageReply : GroupChatUiIntent()
    data class ImageAction(val action: MessageImageAction) : GroupChatUiIntent()
    /** 打开系统文件选择器，读取文本文件内容填入输入框。 */
    data object OpenFilePicker : GroupChatUiIntent()
    /** 文件选择器返回的已选文件。 */
    data class FilesPicked(val uris: List<Uri>) : GroupChatUiIntent()
    /** 打开图库挑选对话框（从已导入的图片里挑，不是从手机相册导入）。 */
    data object OpenGalleryPicker : GroupChatUiIntent()
    /** 关闭图库挑选对话框。 */
    data object CloseGalleryPicker : GroupChatUiIntent()
    /** 图库挑选结果：文件 UUID，按用户勾选顺序。 */
    data class AttachGalleryImages(val uuids: List<String>) : GroupChatUiIntent()
    data class Init(val sessionId: String?) : GroupChatUiIntent()
    data object Resume : GroupChatUiIntent()
    data object LoadOlderMessages : GroupChatUiIntent()
    /** 让模型替玩家写一句发言，填入输入框待确认。 */
    data object ImpersonateUser : GroupChatUiIntent()

    data object Back : GroupChatUiIntent()
    data object OpenSettings : GroupChatUiIntent()
    data object ShowSessionLoreDialog : GroupChatUiIntent()
    data object OpenWorldBookManager : GroupChatUiIntent()
    data class ChangeSessionLorebookDialogQuery(val value: String) : GroupChatUiIntent()
    data class ToggleSessionLorebookDialogEntry(val entryId: Long) : GroupChatUiIntent()
    data class ToggleSessionLorebookDialogGroup(val lorebookId: Long) : GroupChatUiIntent()
    data object ConfirmSessionLorebookSelection : GroupChatUiIntent()
    data object OpenPromptInspector : GroupChatUiIntent()

    data class CopyPromptItem(val text: String) : GroupChatUiIntent()
    data object CloseSettings : GroupChatUiIntent()
    data class ChangeInputDraft(val value: String) : GroupChatUiIntent()
    data class SelectSpeaker(val characterId: Long) : GroupChatUiIntent()
    data class ToggleMemberMuted(val characterId: Long) : GroupChatUiIntent()
    data object SendMessage : GroupChatUiIntent()

    /** 切换到指定场景；传 null 表示回到「不限场景」，全员在场。 */
    data class SwitchScene(val sceneId: String?) : GroupChatUiIntent()

    /** 对某个成员做一个动作；长按头像触发，或从「+」菜单里选动作。 */
    data class PokeMember(val memberName: String, val actionId: String = "") : GroupChatUiIntent()
    data object StopGeneration : GroupChatUiIntent()
    data object SummarizeNow : GroupChatUiIntent()
    data object RestorePreviousSummary : GroupChatUiIntent()
    data class ChangeTitle(val value: String) : GroupChatUiIntent()
    data class ChangeScenario(val value: String) : GroupChatUiIntent()
    data class ChangeUserNote(val value: String) : GroupChatUiIntent()

    /**
     * 开关本群聊的独立人设。
     *
     * 关掉时把本地值清空，回到继承全局——不这么做的话，
     * 关掉开关但值还在，用户会以为没生效。
     */
    data class ToggleOverridePersona(val enabled: Boolean) : GroupChatUiIntent()

    /** 改本群聊里的 user 显示名；清空即继承全局。 */
    data class ChangeUserName(val value: String) : GroupChatUiIntent()

    /** 改本群聊里的 user 人设；清空即继承全局。 */
    data class ChangeUserDescription(val value: String) : GroupChatUiIntent()
    data class ChangeSummary(val value: String) : GroupChatUiIntent()
    data class ToggleAutoSummaryPaused(val paused: Boolean) : GroupChatUiIntent()
    data class ChangeSystemPrompt(val value: String) : GroupChatUiIntent()
    data class ChangeGroupNudgePrompt(val value: String) : GroupChatUiIntent()
    data class ChangeNewGroupChatPrompt(val value: String) : GroupChatUiIntent()
    data object SaveSettings : GroupChatUiIntent()
    data class SelectActivationStrategy(
        val strategy: GroupChatActivationStrategy
    ) : GroupChatUiIntent()
    data class SelectCharacterCardMode(
        val mode: GroupChatCharacterCardMode
    ) : GroupChatUiIntent()
    data class ToggleIncludeMutedCards(val enabled: Boolean) : GroupChatUiIntent()
    data class ToggleAutoMode(val enabled: Boolean) : GroupChatUiIntent()
    data class ToggleTrimOtherSpeakers(val enabled: Boolean) : GroupChatUiIntent()
    data class ToggleAllowSelfResponses(val enabled: Boolean) : GroupChatUiIntent()
    data class ChangeLorebookQuery(val value: String) : GroupChatUiIntent()
    data class ToggleLorebook(val lorebookId: Long) : GroupChatUiIntent()
    data class ToggleLorebookEntry(val entryId: Long) : GroupChatUiIntent()
    data class AddMember(val characterId: Long) : GroupChatUiIntent()
    data class RemoveMember(val characterId: Long) : GroupChatUiIntent()
    data class MoveMember(val characterId: Long, val offset: Int) : GroupChatUiIntent()
    data class ReorderMember(
        val fromCharacterId: Long,
        val toCharacterId: Long
    ) : GroupChatUiIntent()
    data object CommitMemberOrder : GroupChatUiIntent()
    data class CopyMessage(val messageId: Long) : GroupChatUiIntent()
    data class ToggleThinkBlock(val blockId: String) : GroupChatUiIntent()
    data class StartEditMessage(val messageId: Long) : GroupChatUiIntent()
    data class ChangeEditingMessageDraft(val value: String) : GroupChatUiIntent()
    data object SaveEditingMessage : GroupChatUiIntent()
    data object CancelEditingMessage : GroupChatUiIntent()
    data class DeleteMessageClick(val messageId: Long) : GroupChatUiIntent()
    data object ConfirmDeleteMessage : GroupChatUiIntent()
    data class RegenerateMessage(val messageId: Long) : GroupChatUiIntent()
    data object ContinueLast : GroupChatUiIntent()

    /** 清空上下文重来：消息与摘要全清，群聊条目与成员保留。 */
    data object ClearConversationContextClick : GroupChatUiIntent()

    /** 确认清空上下文。 */
    data object ConfirmClearConversationContext : GroupChatUiIntent()

    data object DeleteSessionClick : GroupChatUiIntent()
    data object ConfirmDeleteSession : GroupChatUiIntent()

    /** 打开场景编辑器；sceneId 为 null 表示新建。 */
    data class OpenSceneEditor(val sceneId: String? = null) : GroupChatUiIntent()
    data class ChangeSceneEditorName(val value: String) : GroupChatUiIntent()
    data class ChangeSceneEditorDescription(val value: String) : GroupChatUiIntent()

    /** 切换某个角色是否驻扎在该场景；空集合代表全员在场。 */
    data class ToggleSceneEditorMember(val characterId: Long) : GroupChatUiIntent()
    data object SaveSceneEditor : GroupChatUiIntent()
    data class DeleteScene(val sceneId: String) : GroupChatUiIntent()

    data object OpenProviderSettings : GroupChatUiIntent()
    data object DismissDialog : GroupChatUiIntent()
}
