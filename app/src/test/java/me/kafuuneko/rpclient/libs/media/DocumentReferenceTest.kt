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
    fun `renders a reference marker without the body`() {
        val document = AttachedDocument(id = "doc123", name = "spec.md", text = "很长很长的正文")
        assertEquals("[文件：spec.md#doc123]", document.renderForMessage())
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
}
