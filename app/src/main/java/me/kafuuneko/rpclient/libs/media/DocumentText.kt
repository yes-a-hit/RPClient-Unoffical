package me.kafuuneko.rpclient.libs.media

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 修改前请先看 DocumentTextTest，用例覆盖了标签识别、实体转义与空行压缩。
 */

/**
 * 把办公文档摊平成纯文本，喂给只能读文字的模型。
 *
 * 只做 `.docx`。它是 zip 包，正文在 `word/document.xml`，抽出来去掉标签就是段落文字。
 * `.doc`（旧二进制格式）不做——那需要一整套 Compound File 解析，收益和成本不成比例，
 * 调用方会拿到 null 并给出「这格式读不了」的提示。
 *
 * 段落标记 `<w:p>` 转成换行，`<w:tab>` 转制表符，`<w:br>` 转换行；其余标签直接丢。
 * 不追求排版还原，只要能读。
 */
internal object DocumentText {

    /** 单个文档最多抽这么多字符，防止一本小说把上下文顶爆。 */
    private const val MAX_CHARS = 200_000

    fun isDocx(displayName: String?): Boolean =
        displayName?.lowercase()?.endsWith(".docx") == true

    /** 读 docx 正文；不是 docx、打不开或没有 `word/document.xml` 都返回 null。 */
    fun readDocx(resolver: ContentResolver, uri: Uri): String? = try {
        resolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input.buffered()).use { zip ->
                var entry = zip.nextEntry
                var xml: String? = null
                while (entry != null) {
                    if (entry.name == "word/document.xml") {
                        xml = readLimited(zip)
                        break
                    }
                    entry = zip.nextEntry
                }
                xml?.let { toPlainText(it) }?.trim()?.takeIf { it.isNotEmpty() }
            }
        }
    } catch (_: Exception) {
        null
    }

    /** 从 WordprocessingML 里抽段落文字。 */
    fun toPlainText(xml: String): String {
        val builder = StringBuilder(xml.length / 4)
        var index = 0
        var insideRun = false
        while (index < xml.length) {
            val open = xml.indexOf('<', index)
            if (open < 0) {
                if (insideRun) builder.append(unescape(xml.substring(index)))
                break
            }
            if (insideRun && open > index) builder.append(unescape(xml.substring(index, open)))
            val close = xml.indexOf('>', open)
            if (close < 0) break
            val tag = xml.substring(open + 1, close)
            when {
                // tag 已经去掉尖括号，所以这里比对的是 "w:p" 而不是 "<w:p>"
                // 只有文档开头不加换行；空段落 <w:p/> 也要占一行，否则段落间距全丢
                tag == "w:p" || tag.startsWith("w:p ") || tag == "w:p/" -> {
                    if (builder.isNotEmpty()) builder.append('\n')
                }
                tag == "w:tab/" -> builder.append('\t')
                tag == "w:br/" -> builder.append('\n')
                // w:t 是文字容器，里面的内容才是正文；其他标签里的属性不能当正文
                tag == "w:t" || tag.startsWith("w:t ") -> insideRun = true
                tag == "/w:t" -> insideRun = false
            }
            index = close + 1
        }
        // 连续空行压成一行，Word 会在段落之间塞很多空 <w:p/>
        return builder.toString().lines().joinToString("\n") { it.trim() }
            .replace(Regex("\n{3,}"), "\n\n")
    }

    private fun readLimited(input: ZipInputStream): String {
        val buffer = ByteArrayOutputStream()
        val chunk = ByteArray(8192)
        var total = 0
        while (total < MAX_CHARS * 4) {
            val read = input.read(chunk)
            if (read < 0) break
            buffer.write(chunk, 0, read)
            total += read
        }
        return buffer.toString("UTF-8")
    }

    private fun unescape(text: String): String = text
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
}
