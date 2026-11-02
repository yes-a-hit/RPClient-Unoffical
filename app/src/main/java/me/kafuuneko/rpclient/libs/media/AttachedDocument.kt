package me.kafuuneko.rpclient.libs.media

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅。
 */

/**
 * 一份随消息发出的文档。
 *
 * 和图片附件不同，文档进不了输入框——一段几万字的正文塞进 `TextField` 既看不清也改不动。
 * 正文在发出前就落到 [DocumentStore]，消息体里只留一个引用标记，
 * 这样气泡里是一行文件名，模型那边由提示词注入全文。
 */
data class AttachedDocument(
    /** 落盘主键；消息体里的引用标记靠它找回正文。 */
    val id: String,
    /** 展示用文件名，例如「会议纪要.docx」。 */
    val name: String,
    /** 抽好的纯文本正文。 */
    val text: String
) {
    /** 字数，给用户一个「这份东西有多大」的直觉。 */
    val charCount: Int get() = text.length

    /**
     * 消息体里的引用标记。
     *
     * 不带正文：正文一进消息就跟着聊天记录走，几万字会顶穿气泡，
     * 也会在每次请求里重复计费。正文放在磁盘上按需取。
     */
    fun renderForMessage(): String = "[文件：" + name + "#" + id + "]"
}

/** 从正文里认出的一个文档引用。 */
data class DocumentReference(val id: String, val name: String)

/** 消息正文里文件引用标记的通用解析，提示词构建与界面渲染共用同一套规则。 */
object DocumentReferencePattern {

    private val SLIM = Regex("\\[文件：([^\\]\\n#]+)#([A-Za-z0-9_-]+)]")

    /**
     * 旧的写法：正文直接夹在两个标记之间。
     *
     * 保留它只为读懂老消息——那时候正文还没外置，直接在气泡里。
     */
    private val LEGACY =
        Regex("\\[文件：([^\\]\\n]+)]\\n(.*?)\\n\\[/文件]", RegexOption.DOT_MATCHES_ALL)

    /** 扫出正文里所有文档引用，按出现顺序；重复的 id 只留一次。 */
    fun referencesOf(content: String): List<DocumentReference> =
        SLIM.findAll(content)
            .map { DocumentReference(id = it.groupValues[2], name = it.groupValues[1].trim()) }
            .distinctBy { it.id }
            .toList()

    /** 旧格式里夹着的正文，按文件名索引；用于渲染历史消息时兜底。 */
    fun legacyBodies(content: String): Map<String, String> =
        LEGACY.findAll(content)
            .associate { it.groupValues[1].trim() to it.groupValues[2].trim() }
}
