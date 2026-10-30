package me.kafuuneko.rpclient.feature.chat.presentation

import me.kafuuneko.rpclient.libs.core.IViewEvent
import me.kafuuneko.rpclient.libs.media.ImageExportMetadata

/** 单聊页面需要 Activity 执行的剪贴板、文件选择和页面跳转事件。 */
sealed class ChatViewEvent : IViewEvent {
    data object PickImages : ChatViewEvent()

    data object OpenFilePicker : ChatViewEvent()

    /** 使用原图真实类型与扩展名创建保存目标。 */
    data class SaveImage(val metadata: ImageExportMetadata) : ChatViewEvent()

    data class CopyText(val text: String) : ChatViewEvent()

    data class OpenSession(val sessionId: String) : ChatViewEvent()

    data class OpenChatExporter(val fileName: String) : ChatViewEvent()
}
