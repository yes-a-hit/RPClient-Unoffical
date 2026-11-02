package me.kafuuneko.rpclient.libs.media

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅并在真机上验证过行为。
 * 如需修改，请先看 DocumentStoreTest 里的用例，它们描述了存取与裁剪的预期。
 */

/**
 * 随消息发出的文档正文仓库。
 *
 * 正文不进消息体，只在这里按 id 存成文件。这样做有三个直接好处：
 * 聊天记录不必背着几万字、每次请求也不用把旧附件重发一遍、
 * 界面能只显示一行文件名而正文想看时再展开。
 *
 * 每个文档两个文件：`<id>.txt` 存正文，`<id>.name` 存原始文件名。
 * 名字单独放，是为了让正文文件保持纯文本，出问题时能直接用文本编辑器打开。
 */
class DocumentStore(private val mContext: Context) {

    private val root: File
        get() = File(mContext.filesDir, "chat-documents").apply { if (!exists()) mkdirs() }

    /** 落盘并返回同一个对象，方便调用方链式使用。 */
    suspend fun save(document: AttachedDocument): AttachedDocument = withContext(Dispatchers.IO) {
        val dir = root
        File(dir, document.id + ".txt").writeText(document.text)
        File(dir, document.id + ".name").writeText(document.name)
        document
    }

    /** 按 id 取回；正文或名字缺失都当作不存在，交给调用方降级显示。 */
    suspend fun load(id: String): AttachedDocument? = withContext(Dispatchers.IO) {
        val dir = root
        val body = File(dir, id + ".txt")
        val nameFile = File(dir, id + ".name")
        if (!body.isFile || !nameFile.isFile) return@withContext null
        runCatching {
            AttachedDocument(
                id = id,
                name = nameFile.readText(),
                text = body.readText()
            )
        }.getOrNull()
    }

    /** 批量取，顺序与入参一致，取不到的跳过。 */
    suspend fun loadAll(references: List<DocumentReference>): List<AttachedDocument> =
        withContext(Dispatchers.IO) {
            references.mapNotNull { reference ->
                load(reference.id) ?: AttachedDocument(
                    id = reference.id,
                    name = reference.name,
                    // 正文丢了也要让模型知道「用户发过一个叫这个名字的文件」，
                    // 否则它会以为自己在跟一个凭空出现的名字对话
                    text = ""
                )
            }
        }

    /** 提示词里给每个文件留的篇幅上限，超出部分截断并注明。 */
    suspend fun promptExcerpts(
        references: List<DocumentReference>,
        budgetPerDocument: Int = DEFAULT_PROMPT_BUDGET
    ): List<PromptDocument> = withContext(Dispatchers.IO) {
        loadAll(references).map { document ->
            PromptDocument(name = document.name, text = clip(document.text, budgetPerDocument))
        }
    }

    private fun clip(text: String, budget: Int): String {
        if (budget <= 0 || text.length <= budget) return text
        // 从后往前找个换行，别把一句话拦腰砍断
        val cut = text.lastIndexOf('\n', budget).takeIf { it > budget / 2 } ?: budget
        return text.substring(0, cut) + "\n……（以下省略 " + (text.length - cut) + " 字）"
    }

    companion object {
        /** 单份文件送进提示词的默认上限；再长就该分段读了。 */
        const val DEFAULT_PROMPT_BUDGET = 12_000

        /** 新文档的 id：时间戳加随机后缀，够用且不会撞。 */
        fun newId(): String =
            "doc" + System.currentTimeMillis().toString(36) +
                Integer.toHexString((Math.random() * 0xFFFF).toInt())
    }
}

/** 提示词里的一份文件：只带名字与（可能被裁过的）正文。 */
data class PromptDocument(val name: String, val text: String)
