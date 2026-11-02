/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 MessageDocumentParsingTest 里的用例，它们描述了拆分的预期。
 */

package me.kafuuneko.rpclient.ui.widgets

import me.kafuuneko.rpclient.libs.media.DocumentReferencePattern

/**
 * 消息正文里附带文件的区块。
 *
 * 现在消息里只留一个引用标记 `[文件：名字#id]`，正文在 `DocumentStore` 里按 id 取。
 * 早期消息是 `[文件：名字]` 加 `[/文件]` 把正文夹在中间，那种仍然认，
 * 但正文就地交给卡片，不再回磁盘找。
 */
internal object MessageDocumentBlock {

    private val SLIM = Regex("\\[文件：([^\\]\\n#]+)#([A-Za-z0-9_-]+)]")

    private val LEGACY =
        Regex("\\[文件：([^\\]\\n]+)]\\n(.*?)\\n\\[/文件]", RegexOption.DOT_MATCHES_ALL)

    /** 消息正文被拆成的一段：普通文本或一个文件引用。 */
    sealed interface Segment {
        data class Text(val content: String) : Segment

        /**
         * 一个文件。
         *
         * [inlineBody] 只有旧格式才有值——那种消息把正文直接写在了正文里。
         * 新格式一律为 null，渲染时按 [id] 去仓库取。
         */
        data class Document(
            val name: String,
            val id: String,
            val inlineBody: String? = null
        ) : Segment
    }

    /**
     * 按文件引用切开正文。
     *
     * 没有文件引用时返回单个 [Segment.Text]，调用方可以不分支直接渲染；
     * 首尾的空行会被裁掉，避免卡片与文字之间多出空白。
     * 未闭合的旧标记按普通文本处理——手打半截标记不该把后面的正文吞进卡片。
     */
    fun split(content: String): List<Segment> {
        val matches = (
            SLIM.findAll(content).map { match ->
                Triple(match.range, match.groupValues[1].trim(), match.groupValues[2])
            } + LEGACY.findAll(content).map { match ->
                Triple(match.range, match.groupValues[1].trim(), "")
            }
            ).sortedBy { it.first.first }.toList()
        if (matches.isEmpty()) return listOf(Segment.Text(content))
        val segments = mutableListOf<Segment>()
        var cursor = 0
        matches.forEach { (range, name, id) ->
            if (range.first > cursor) {
                val before = content.substring(cursor, range.first)
                if (before.isNotBlank()) segments += Segment.Text(before.trim())
            }
            segments += if (id.isBlank()) {
                // 旧格式：正文就在这条消息里，直接从匹配段里取
                val body = content.substring(range.first, range.last + 1)
                    .substringAfter('\n')
                    .substringBeforeLast("\n[/文件]")
                Segment.Document(name = name, id = "", inlineBody = body.trim())
            } else {
                Segment.Document(name = name, id = id)
            }
            cursor = range.last + 1
        }
        if (cursor < content.length) {
            val after = content.substring(cursor)
            if (after.isNotBlank()) segments += Segment.Text(after.trim())
        }
        return if (segments.isEmpty()) listOf(Segment.Text(content)) else segments
    }

    /** 一份消息里出现的全部文档引用，供提示词注入使用。 */
    fun referencesOf(content: String): List<me.kafuuneko.rpclient.libs.media.DocumentReference> =
        DocumentReferencePattern.referencesOf(content)
}
