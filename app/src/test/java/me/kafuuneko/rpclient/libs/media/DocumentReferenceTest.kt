/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看这些用例，它们描述了引用标记与正文外置的预期。
 */

package me.kafuuneko.rpclient.libs.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentReferenceTest {

    @Test
    fun `renders a bare reference without the body`() {
        // 正文在仓库里，消息体只放引用：气泡一行、消息表不被撑大、
        // 撤掉待发附件时那份文件仍然留在库里
        val document = AttachedDocument(id = "doc123", name = "spec.md", text = "很长很长的正文")
        assertEquals("[文件：spec.md#doc123]", document.renderForMessage())
    }

    @Test
    fun `marker produced by the sender is recognised by the parser`() {
        // 发送时拼的标记和渲染时认的标记必须是同一套写法，
        // 否则消息里会露出一行谁都读不懂的方括号
        val document = AttachedDocument(id = DocumentStore.newId(), name = "规范 v2.md", text = "正文")
        val content = "帮我看下\n\n" + document.renderForMessage()
        val segments = me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.split(content)
        assertEquals(2, segments.size)
        val card = segments[1] as me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.Segment.Document
        assertEquals("规范 v2.md", card.name)
        assertEquals(document.id, card.id)
        assertEquals(null, card.inlineBody)
    }

    @Test
    fun `reads references out of a message`() {
        val content = "看这个 [文件：spec.md#docABC] 还有 [文件：notes.txt#docXYZ]"
        val references = DocumentReferencePattern.referencesOf(content)
        assertEquals(2, references.size)
        assertEquals("docABC", references[0].id)
        assertEquals("spec.md", references[0].name)
        assertEquals("docXYZ", references[1].id)
    }

    @Test
    fun `repeated reference is only counted once`() {
        // 同一份文件被引用两次只该注入一遍，否则正文会重复占满预算
        val content = "[文件：a.txt#doc1] 再提一次 [文件：a.txt#doc1]"
        assertEquals(1, DocumentReferencePattern.referencesOf(content).size)
    }

    @Test
    fun `legacy inline body is still readable`() {
        // 早期消息把正文直接夹在标记里，那种还得认得出来
        val content = "[文件：old.txt]\n旧写法正文\n[/文件]"
        assertTrue(DocumentReferencePattern.referencesOf(content).isEmpty())
        assertEquals(mapOf("old.txt" to "旧写法正文"), DocumentReferencePattern.legacyBodies(content))
        val segments = me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.split(content)
        val card = segments.single() as me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.Segment.Document
        assertEquals("旧写法正文", card.inlineBody)
    }

    @Test
    fun `new ids do not collide`() {
        val ids = (1..200).map { DocumentStore.newId() }.toSet()
        assertEquals(200, ids.size)
        assertTrue(ids.all { it.startsWith("doc") })
    }
}
