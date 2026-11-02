package me.kafuuneko.rpclient.libs.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** docx 抽正文的用例：只验证标签处理，不用真造 zip。 */
class DocumentTextTest {

    @Test
    fun `keeps run text and drops markup`() {
        val xml = """
            <w:document><w:body>
              <w:p><w:r><w:t>第一段</w:t></w:r></w:p>
              <w:p><w:r><w:t>第二段</w:t></w:r></w:p>
            </w:body></w:document>
        """.trimIndent()
        assertEquals("第一段\n第二段", DocumentText.toPlainText(xml))
    }

    @Test
    fun `does not read text outside w t`() {
        // 属性值和标签名里的字符都不该出现在正文里
        val xml = """<w:p><w:r w:rsidR="TEXT"><w:t>正文</w:t></w:r></w:p>"""
        assertEquals("正文", DocumentText.toPlainText(xml))
    }

    @Test
    fun `converts tab and line break`() {
        val xml = """<w:p><w:r><w:t>a</w:t><w:tab/><w:t>b</w:t><w:br/><w:t>c</w:t></w:r></w:p>"""
        assertEquals("a\tb\nc", DocumentText.toPlainText(xml))
    }

    @Test
    fun `unescapes xml entities`() {
        val xml = """<w:p><w:r><w:t>a &amp; b &lt; c</w:t></w:r></w:p>"""
        assertEquals("a & b < c", DocumentText.toPlainText(xml))
    }

    @Test
    fun `collapses repeated blank lines`() {
        val xml = """<w:p><w:r><w:t>x</w:t></w:r></w:p><w:p/><w:p/><w:p><w:r><w:t>y</w:t></w:r></w:p>"""
        assertEquals("x\n\ny", DocumentText.toPlainText(xml))
    }

    @Test
    fun `detects docx by extension`() {
        assertTrue(DocumentText.isDocx("报告.DOCX"))
        assertTrue(!DocumentText.isDocx("报告.doc"))
        assertTrue(!DocumentText.isDocx(null))
    }
}
