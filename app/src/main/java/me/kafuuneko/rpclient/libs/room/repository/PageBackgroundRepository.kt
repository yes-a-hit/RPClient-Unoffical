package me.kafuuneko.rpclient.libs.room.repository

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kafuuneko.rpclient.libs.room.AppDatabase
import me.kafuuneko.rpclient.libs.room.entity.PageBackgroundEntity

/**
 * 页面背景的读写。
 *
 * 会话级背景的语义是「按会话覆盖全局」，所以**没有记录 ≠ 没有背景**：
 * 读不到就让调用方回落到全局设置。这也是为什么清除用删行、不用写空行。
 *
 * 文件顺序固定为「先写新、再删旧」：中途失败最多留一张孤儿图，
 * 不会出现「背景没了、旧图也删了」这种两头空的局面。
 */
class PageBackgroundRepository(
    private val mAppDatabase: AppDatabase,
    private val mFileRepository: FileRepository
) {

    private val mDao get() = mAppDatabase.getPageBackgroundDao()

    /** 读一页的背景；没有记录返回 null，由调用方回落全局。 */
    suspend fun get(page: String): PageBackgroundEntity? = withContext(Dispatchers.IO) {
        mDao.get(page)
    }

    /** 一页当前用的图片 UUID；没有独立背景返回 null。 */
    suspend fun imageUuid(page: String): String? = get(page)?.fileUuid?.takeIf { it.isNotBlank() }

    /**
     * 把系统返回的图片存进应用私有目录，并设为这一页的背景。
     *
     * @return 成功时返回新的文件 UUID；存图失败返回 null，调用方据此提示
     */
    suspend fun setImageFromUri(page: String, uri: Uri): String? = withContext(Dispatchers.IO) {
        val fileUuid = runCatching { mFileRepository.saveFile(uri) }.getOrNull() ?: return@withContext null
        val previous = mDao.get(page)
        val value = (previous ?: PageBackgroundEntity.of(page, fileUuid)).copy(
            page = page,
            fileUuid = fileUuid
        )
        mDao.put(value)
        // 旧引用换掉之后再回收文件：先写后删，失败也不会丢背景
        previous?.fileUuid?.takeIf { it.isNotBlank() && it != fileUuid }?.let { stale ->
            releaseIfUnused(stale)
        }
        fileUuid
    }

    /** 清掉这一页的独立背景，恢复跟随全局。 */
    suspend fun clearImage(page: String) = withContext(Dispatchers.IO) {
        val previous = mDao.get(page)
        mDao.delete(page)
        previous?.fileUuid?.takeIf { it.isNotBlank() }?.let { stale ->
            releaseIfUnused(stale)
        }
    }

    /** 删会话时连带清掉它的背景行。文件交给会话删除流程统一回收。 */
    suspend fun clearByPrefix(prefix: String) = withContext(Dispatchers.IO) {
        mDao.deleteByPrefix(prefix)
    }

    /**
     * 这张图还有别处在用就留着。
     *
     * 会话级背景下同一张图完全可能被几个会话同时选作背景
     * （用户就喜欢那张图）。一处换图就把文件删掉，别的会话会变成空背景。
     */
    private suspend fun releaseIfUnused(uuid: String) {
        // 行已经写完/删完了才调到这里，所以 >0 就是「别处还在用」
        if (mDao.countByFile(uuid) > 0) return
        runCatching { mFileRepository.deleteFile(uuid) }
    }
}
