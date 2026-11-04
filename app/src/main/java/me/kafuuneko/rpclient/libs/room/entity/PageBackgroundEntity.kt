package me.kafuuneko.rpclient.libs.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 按页面区分的背景配置。
 *
 * `page` 是自由格式的键，本分支用它承载会话级背景：
 * - `chat:<sessionId>` 单聊会话
 * - `group:<sessionId>` 群聊会话
 *
 * 字段照搬上游 `PageBackgroundEntity`，包括目前没接上 UI 的模糊与分栏透明度。
 * 这样以后要接整套外观体系时不用再迁移一次数据库——字段空着不花钱，
 * 加一列却要动 schema、迁移脚本和三处调用点。
 *
 * 与上游的两处必要偏离：
 * 1. 上游把 `fileUuid` 建成唯一索引（一个文件只服务一个页面）。会话级背景下
 *    多个会话完全可能用同一张图，唯一索引会直接插入失败，所以改成普通索引。
 * 2. 上游外键是 `RESTRICT`（文件被引用时禁止删除）。这里用 `CASCADE`：
 *    文件没了背景行跟着走，比留一条指向空文件的行更好收拾。
 */
@Entity(
    tableName = "page_backgrounds",
    foreignKeys = [ForeignKey(
        entity = FileEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["fileUuid"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["fileUuid"])]
)
data class PageBackgroundEntity(
    /** 页面键，见类注释。 */
    @PrimaryKey val page: String,
    /** 背景图文件；为空表示这一页没有独立背景，回落到全局设置。 */
    val fileUuid: String?,
    val blur: Int,
    val scrim: Float,
    val opacity: Float,
    val focalX: Float,
    val focalY: Float,
    @ColumnInfo(defaultValue = "1.0") val topBarOpacity: Float = 1f,
    @ColumnInfo(defaultValue = "1.0") val bottomBarOpacity: Float = 1f,
    @ColumnInfo(defaultValue = "1.0") val bubbleOpacity: Float = 1f,
    @ColumnInfo(defaultValue = "0") val topBarBlur: Int = 0,
    @ColumnInfo(defaultValue = "0") val bottomBarBlur: Int = 0,
    @ColumnInfo(defaultValue = "0") val bubbleBlur: Int = 0,
    @ColumnInfo(defaultValue = "0") val contentBlur: Int = 0,
    @ColumnInfo(defaultValue = "1") val followGeneral: Boolean = true,
    @ColumnInfo(defaultValue = "0") val useCharacterBackground: Boolean = false
) {
    companion object {
        /** 单聊会话的页面键。 */
        fun chatPage(sessionId: Long): String = "chat:" + sessionId

        /** 群聊会话的页面键。 */
        fun groupPage(sessionId: Long): String = "group:" + sessionId

        /**
         * 新建一条只指定了图片的配置，其余参数取默认值。
         *
         * 会话级背景目前只让用户选图，模糊与透明度沿用全局那一套，
         * 所以这些字段填中性值即可。
         */
        fun of(page: String, fileUuid: String?): PageBackgroundEntity = PageBackgroundEntity(
            page = page,
            fileUuid = fileUuid,
            blur = 0,
            scrim = 0f,
            opacity = 1f,
            focalX = 0.5f,
            focalY = 0.5f
        )
    }
}
