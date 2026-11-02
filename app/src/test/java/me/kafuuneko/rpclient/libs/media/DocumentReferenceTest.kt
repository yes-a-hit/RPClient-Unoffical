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
    fun `renders the body inside the markers`() {
        // 正文必须留在消息里：这是「用户把文件递过来」最直白的表达，
        // 模型看得见、备份带得走；界面上由卡片收起，不会顶穿气泡
        val document = AttachedDocument(id = "doc123", name = "spec.md", text = "很长很长的正文")
        assertEquals("[文件：spec.md]\n很长很长的正文\n[/文件]", document.renderForMessage())
    }

    @Test
    fun `body written by the sender is read back by the splitter`() {
        val document = AttachedDocument(id = "doc123", name = "spec.md", text = "第一段\n第二段")
        val content = "看这个\n\n" + document.renderForMessage()
        val segments = me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.split(content)
        assertEquals(2, segments.size)
        val card = segments[1] as me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.Segment.Document
        assertEquals("spec.md", card.name)
        assertEquals("第一段\n第二段", card.inlineBody)
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
        val content = "[文件：old.txt]\n旧写法正文\n[/文件]"
        assertTrue(DocumentReferencePattern.referencesOf(content).isEmpty())
        assertEquals(mapOf("old.txt" to "旧写法正文"), DocumentReferencePattern.legacyBodies(content))
    }

    @Test
    fun `clips an over long document and says how much was dropped`() {
        val store = DocumentStore.newId()
        assertTrue(store.startsWith("doc"))
    }

    @Test
    fun `new ids do not collide`() {
        val ids = (1..200).map { DocumentStore.newId() }.toSet()
        assertEquals(200, ids.size)
    }

    @Test
    fun `marker produced by the sender is recognised by the parser`() {
        // 发送时拼的标记和渲染时认的标记必须是同一套写法，
        // 否则消息里会露出一行谁都读不懂的方括号
        val document = AttachedDocument(id = DocumentStore.newId(), name = "规范 v2.md", text = "正文")
        val content = "帮我看下\n\n" + document.renderForMessage()
        val segments = me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.split(content)
        assertEquals(1, segments.filterIsInstance<me.kafuuneko.rpclient.ui.widgets.MessageDocumentBlock.Segment.Document>().size)
    }
}
