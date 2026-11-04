package me.kafuuneko.rpclient.feature.groupchat.presentation

import me.kafuuneko.rpclient.libs.core.IViewEvent
import me.kafuuneko.rpclient.libs.media.ImageExportMetadata

/** 群聊页交由 Activity 执行的一次性系统动作，避免把消费状态保存在 UiState 中。 */
sealed class GroupChatViewEvent : IViewEvent {
    data object PickImages : GroupChatViewEvent()
    /** 打开系统文件选择器，读取文本文件内容填入输入框。 */
    data object OpenFilePicker : GroupChatViewEvent()
    /** 打开系统选图，只挑一张作为本群聊的背景。 */
    data object PickBackgroundImage : GroupChatViewEvent()
    /** 打开系统选图，给正在编辑的场景挑背景。 */
    data object PickSceneBackgroundImage : GroupChatViewEvent()
    /** 使用原图真实类型与扩展名创建保存目标。 */
    data class SaveImage(val metadata: ImageExportMetadata) : GroupChatViewEvent()
    data class CopyText(val text: String) : GroupChatViewEvent()
}
