package me.kafuuneko.rpclient.libs.media

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅。
 */

/**
 * 一份随消息发出的文档。
 *
 * 和图片附件不同，文档进不了输入框——一段几万字的正文塞进 `TextField` 既看不清也改不动。
 * 所以这里只留文件名和已抽好的正文：输入框旁边显示成一个可删除的条，
 * 发送时再把正文拼到消息末尾，模型读到的是完整内容，用户看到的是干净的一行。
 */
data class AttachedDocument(
    /** 展示用文件名，例如「会议纪要.docx」。 */
    val name: String,
    /** 抽好的纯文本正文。 */
    val text: String
) {
    /** 字数，给用户一个「这份东西有多大」的直觉。 */
    val charCount: Int get() = text.length

    /** 拼进消息体的样子；用标记包住，模型能分清哪段是用户打的、哪段是文件。 */
    fun renderForMessage(): String = buildString {
        append("[文件：").append(name).append("]\n")
        append(text)
        append("\n[/文件]")
    }
}
