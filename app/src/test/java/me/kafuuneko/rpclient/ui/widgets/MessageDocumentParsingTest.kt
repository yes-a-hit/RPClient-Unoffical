/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看这些用例，它们描述了拆分的预期。
 */

package me.kafuuneko.rpclient.ui.widgets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageDocumentParsingTest {

    @Test
    fun `plain content stays a single text segment`() {
        val segments = MessageDocumentBlock.split("今晚吃什么")
        assertEquals(1, segments.size)
        assertEquals(MessageDocumentBlock.Segment.Text("今晚吃什么"), segments[0])
    }

    @Test
    fun `splits text and document in order`() {
        val content = "帮我看看这个规范\n\n[文件：spec.md]\n第一行\n第二行\n[/文件]"
        val segments = MessageDocumentBlock.split(content)
        assertEquals(2, segments.size)
        assertEquals("帮我看看这个规范", (segments[0] as MessageDocumentBlock.Segment.Text).content)
        val doc = segments[1] as MessageDocumentBlock.Segment.Document
        assertEquals("spec.md", doc.name)
        // 文档正文原样保留，段落内的空行不能被吞掉
        assertEquals("第一行\n第二行", doc.body)
    }

    @Test
    fun `keeps multiline document body intact`() {
        val content = "[文件：a.txt]\n标题\n\n正文段落\n\n\n末行\n[/文件]"
        val doc = MessageDocumentBlock.split(content)
            .filterIsInstance<MessageDocumentBlock.Segment.Document>()
            .single()
        assertEquals("标题\n\n正文段落\n\n\n末行", doc.body)
    }

    @Test
    fun `document without trailing text yields only the document`() {
        val content = "[文件：a.txt]\n内容\n[/文件]"
        val segments = MessageDocumentBlock.split(content)
        assertEquals(1, segments.size)
        assertTrue(segments[0] is MessageDocumentBlock.Segment.Document)
    }

    @Test
    fun `two documents split into two cards`() {
        val content = "[文件：一.txt]\nA\n[/文件]\n\n[文件：二.txt]\nB\n[/文件]"
        val docs = MessageDocumentBlock.split(content)
            .filterIsInstance<MessageDocumentBlock.Segment.Document>()
        assertEquals(2, docs.size)
        assertEquals(listOf("一.txt", "二.txt"), docs.map { it.name })
        assertEquals(listOf("A", "B"), docs.map { it.body })
    }

    @Test
    fun `unclosed marker is treated as plain text`() {
        // 手打的半截标记不该把后面的正文吃进卡片里
        val content = "[文件：a.txt]\n没有结束标记"
        val segments = MessageDocumentBlock.split(content)
        assertEquals(1, segments.size)
        assertTrue(segments[0] is MessageDocumentBlock.Segment.Text)
    }
}
