/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 MessageDocumentParsingTest 里的用例，它们描述了拆分的预期。
 */

package me.kafuuneko.rpclient.ui.widgets

/**
 * 消息正文里附带文件的区块：`[文件：名字]` 与 `[/文件]` 之间是文件全文。
 *
 * 为什么不把文件正文直接当普通文本渲染：一份 docx 动辄几万字，铺在气泡里会把
 * 整段对话顶出屏幕。这里把正文留在消息里（模型读得到、历史存得下），
 * 但界面上只显一张可展开的卡片。
 */
internal object MessageDocumentBlock {

    private val PATTERN =
        Regex("\\[文件：([^\\]\\n]+)]\\n(.*?)\\n\\[/文件]", RegexOption.DOT_MATCHES_ALL)

    /** 消息正文被拆成的一段：普通文本或一个文件块。 */
    sealed interface Segment {
        data class Text(val content: String) : Segment
        data class Document(val name: String, val body: String) : Segment
    }

    /**
     * 按文件块切开正文。
     *
     * 没有文件块时返回单个 [Segment.Text]，调用方可以不分支直接渲染；
     * 首尾的空行会被裁掉，避免卡片与文字之间多出空白。
     */
    fun split(content: String): List<Segment> {
        val matches = PATTERN.findAll(content).toList()
        if (matches.isEmpty()) return listOf(Segment.Text(content))
        val segments = mutableListOf<Segment>()
        var cursor = 0
        matches.forEach { match ->
            if (match.range.first > cursor) {
                val before = content.substring(cursor, match.range.first)
                if (before.isNotBlank()) segments += Segment.Text(before.trim())
            }
            segments += Segment.Document(
                name = match.groupValues[1].trim(),
                body = match.groupValues[2].trim()
            )
            cursor = match.range.last + 1
        }
        if (cursor < content.length) {
            val after = content.substring(cursor)
            if (after.isNotBlank()) segments += Segment.Text(after.trim())
        }
        return if (segments.isEmpty()) listOf(Segment.Text(content)) else segments
    }
}
