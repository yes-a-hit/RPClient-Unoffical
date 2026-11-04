package me.kafuuneko.rpclient.libs.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.kafuuneko.rpclient.libs.room.entity.PageBackgroundEntity

/** 页面背景配置的数据接口；文件的新建与回收由 Repository 统一处理。 */
@Dao
interface PageBackgroundDao {

    /** 观察指定页面的背景；没有记录表示沿用全局。 */
    @Query("SELECT * FROM page_backgrounds WHERE page = :page")
    fun observe(page: String): Flow<PageBackgroundEntity?>

    /** 读一次指定页面的背景；没有记录返回 null。 */
    @Query("SELECT * FROM page_backgrounds WHERE page = :page")
    suspend fun get(page: String): PageBackgroundEntity?

    /** 整条写入，避免滑块参数分几次保存。 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(value: PageBackgroundEntity)

    /** 删除指定页面的背景，代表恢复默认。 */
    @Query("DELETE FROM page_backgrounds WHERE page = :page")
    suspend fun delete(page: String)

    /** 按前缀清一批，用于删会话时连带收拾它自己的背景。 */
    @Query("DELETE FROM page_backgrounds WHERE page LIKE :prefix || '%'")
    suspend fun deleteByPrefix(prefix: String)

    /** 这张图还被几个页面引用；大于 0 说明不能回收文件。 */
    @Query("SELECT COUNT(*) FROM page_backgrounds WHERE fileUuid = :uuid")
    suspend fun countByFile(uuid: String): Int
}
