package me.kafuuneko.rpclient.libs.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 主业务数据库 v7→v8：多一张按页面记录背景的表。
 *
 * 新表用 `CREATE TABLE IF NOT EXISTS` 而不是自动迁移：这条链上已经出现过
 * 「版本号相同、结构不同」的库，自动迁移只看版本号会跳过检查，
 * 那样这批库升上来会直接打不开。表存在就跳过，不存在的才建。
 *
 * 字段与上游 `PageBackgroundEntity` 保持一致（含暂时没用上的模糊与分栏透明度），
 * 以后接整套外观体系时不用再迁移一次。
 */
object AppDatabaseMigration7To8 : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `page_backgrounds` (
                `page` TEXT NOT NULL,
                `fileUuid` TEXT,
                `blur` INTEGER NOT NULL,
                `scrim` REAL NOT NULL,
                `opacity` REAL NOT NULL,
                `focalX` REAL NOT NULL,
                `focalY` REAL NOT NULL,
                `topBarOpacity` REAL NOT NULL DEFAULT 1.0,
                `bottomBarOpacity` REAL NOT NULL DEFAULT 1.0,
                `bubbleOpacity` REAL NOT NULL DEFAULT 1.0,
                `topBarBlur` INTEGER NOT NULL DEFAULT 0,
                `bottomBarBlur` INTEGER NOT NULL DEFAULT 0,
                `bubbleBlur` INTEGER NOT NULL DEFAULT 0,
                `contentBlur` INTEGER NOT NULL DEFAULT 0,
                `followGeneral` INTEGER NOT NULL DEFAULT 1,
                `useCharacterBackground` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`page`),
                FOREIGN KEY(`fileUuid`) REFERENCES `files`(`uuid`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        // 同一张图可以给多个会话当背景，所以这里不是唯一索引
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_page_backgrounds_fileUuid` " +
                "ON `page_backgrounds` (`fileUuid`)"
        )
    }
}
