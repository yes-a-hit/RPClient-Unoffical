package me.kafuuneko.rpclient.libs.media

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * 本文件由 AI（Claude）生成，作者已逐行审阅。
 * 如需修改，请先看 PickedFileReaderTest 里的用例，它们描述了每种输入的分流结果。
 */

/**
 * 把系统文件选择器交回来的一批 URI 分成「图片」与「文档」，并把文档读成正文。
 *
 * 单聊与群聊原来各有一份几乎逐字相同的实现：MIME 与扩展名双重判断、docx 抽正文、
 * 单个文件失败不带走整批。两份代码各自修过一次 bug——再修一次就会分叉，
 * 所以收到这里。调用方只需要知道「用户选了什么」和「能用的有哪些」。
 *
 * 图片只回 URI：压缩、GIF 抽帧、进附件表都是图片管线自己的事，
 * 在这里解码一次等于白烧一遍内存。
 */
class PickedFileReader(private val mContext: Context) {

    /** 一批选择的处理结果。 */
    data class Result(
        val images: List<Uri>,
        val documents: List<AttachedDocument>,
        val pickedCount: Int
    ) {
        val hasImages: Boolean get() = images.isNotEmpty()

        val hasDocuments: Boolean get() = documents.isNotEmpty()

        val imageCount: Int get() = images.size

        val documentCount: Int get() = documents.size

        /** 全是图片，或者一份正文都没读出来。 */
        val hasNothingToAttach: Boolean get() = !hasImages && !hasDocuments
    }

    /**
     * 分流并读取。读取失败的文件被跳过，不抛——选择器回来的一批里常有坏文件，
     * 抛出去会让整个意图中断，表现成「选完什么都没发生」。
     */
    suspend fun read(uris: List<Uri>): Result = withContext(Dispatchers.IO) {
        val images = mutableListOf<Uri>()
        val documents = mutableListOf<AttachedDocument>()
        uris.forEach { uri ->
            if (isImage(uri)) {
                images += uri
            } else {
                readDocument(uri)?.let { documents += it }
            }
        }
        Result(images = images, documents = documents, pickedCount = uris.size)
    }

    /**
     * 判断一个 URI 是不是图片。
     *
     * 先看 contentResolver 报的 MIME——大多数来源是准的。有些第三方文件管理器把
     * `.webp`、`.gif` 报成 `application/octet-stream`，所以再按扩展名兜一层。
     */
    fun isImage(uri: Uri): Boolean {
        val mime = runCatching { mContext.contentResolver.getType(uri) }
            .getOrNull().orEmpty().lowercase()
        if (mime.startsWith("image/")) return true
        return hasImageExtension(displayNameOf(uri))
    }

    /** 读一份文档的正文；读不出内容或内容全空白时返回 null。 */
    private fun readDocument(uri: Uri): AttachedDocument? = runCatching {
        val name = displayNameOf(uri) ?: UNNAMED
        // docx 是 zip，当文本读只会得到乱码，单独抽正文
        val text = if (DocumentText.isDocx(name)) {
            DocumentText.readDocx(mContext.contentResolver, uri)
        } else {
            readPlainText(uri)
        }
        text?.takeIf { it.isNotBlank() }
            ?.let { AttachedDocument(id = DocumentStore.newId(), name = name, text = it) }
    }.onFailure {
        Log.w(TAG, "读取失败：" + uri, it)
    }.getOrNull()

    /** 从 contentResolver 查文件的显示名（带扩展名）；查不到退回 URI 末段。 */
    private fun displayNameOf(uri: Uri): String? = runCatching {
        mContext.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
    }.getOrNull() ?: uri.lastPathSegment

    /** 读取单个文件的 UTF-8 文本，超出上限的部分截断；二进制或失败返回 null。 */
    private fun readPlainText(uri: Uri): String? = try {
        mContext.contentResolver.openInputStream(uri)?.use { stream ->
            val buffer = ByteArrayOutputStream()
            val chunk = ByteArray(8192)
            var total = 0
            while (total < MAX_TEXT_BYTES) {
                val read = stream.read(chunk, 0, minOf(chunk.size, MAX_TEXT_BYTES - total))
                if (read < 0) break
                buffer.write(chunk, 0, read)
                total += read
            }
            if (total == 0) {
                null
            } else {
                val text = buffer.toString("UTF-8")
                // 带 \u0000 说明这是二进制格式被当成文本打开，硬塞进提示词只会浪费位置
                if (text.contains('\u0000')) null else text.trim()
            }
        }
    } catch (_: Exception) {
        null
    }

    companion object {
        private const val TAG = "PickedFile"

        private const val UNNAMED = "未命名文件"

        /** 单份文本的读取上限。再长的正文塞进提示词也只会被裁掉，不如在入口截住。 */
        private const val MAX_TEXT_BYTES = 512 * 1024

        /**
         * MIME 不可靠时按这些扩展名把文件认成图片。
         * 与 `MessageImageRuntime.validate` 的白名单保持同一组格式。
         */
        val IMAGE_EXTENSIONS = listOf(
            ".png", ".jpg", ".jpeg", ".webp", ".gif", ".bmp", ".heic", ".heif"
        )

        /** 只看文件名判断是不是图片。分离出来是为了能脱离 Context 写用例。 */
        internal fun hasImageExtension(name: String?): Boolean {
            val lower = name?.lowercase() ?: return false
            return IMAGE_EXTENSIONS.any { lower.endsWith(it) }
        }
    }
}
