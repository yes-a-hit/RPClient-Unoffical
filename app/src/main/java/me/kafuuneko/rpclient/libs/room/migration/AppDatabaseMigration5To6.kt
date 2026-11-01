package me.kafuuneko.rpclient.libs.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 主业务数据库 v5→v6：为群聊会话补上场景相关两列。
 *
 * 这里不用 `AutoMigration` 是有原因的。Room 的自动迁移只按版本号判断该不该跑，版本相同
 * 就直接去做 schema 校验；而历史上存在两个结构不同、却都声明成 version 5 的库
 * （一份带场景列、一份不带，来自不同日期的构建）。用自动迁移时，前者升级正常，后者
 * 会直接撞上「Migration didn't properly handle」而打不开数据库——用户看到的就是应用
 * 起不来，且没有任何可操作的提示。
 *
 * 手写迁移改成按列判断：缺哪列补哪列，已经有了就跳过。这样无论来源库是哪一版，升级
 * 都能收敛到当前 schema。默认值取自导出的 v6 schema，保证补出来的列与 Room 期望一致。
 */
object AppDatabaseMigration5To6 : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 场景配置以 JSON 数组字符串存放；空数组表示这个会话还没配场景。
        db.addColumnIfMissing(
            table = "group_chat_sessions",
            column = "scenesJson",
            definition = "TEXT NOT NULL DEFAULT '[]'"
        )
        // 当前所在场景的 id；空串表示「全员都在」，与「未配置场景」等价。
        db.addColumnIfMissing(
            table = "group_chat_sessions",
            column = "activeSceneId",
            definition = "TEXT NOT NULL DEFAULT ''"
        )
    }
}

/**
 * 只在目标列确实缺失时才执行 `ALTER TABLE`。
 *
 * `ALTER TABLE ... ADD COLUMN` 在列已存在时会抛 SQLiteException，而这个迁移要面对的
 * 恰恰是「可能已经补过列」的库，所以必须先查 `PRAGMA table_info`。
 */
internal fun SupportSQLiteDatabase.addColumnIfMissing(
    table: String,
    column: String,
    definition: String
) {
    if (!tableExists(table)) return
    if (columnExists(table, column)) return
    execSQL("ALTER TABLE `${table}` ADD COLUMN `${column}` $definition")
}

internal fun SupportSQLiteDatabase.tableExists(table: String): Boolean =
    query("SELECT 1 FROM sqlite_master WHERE type='table' AND name=? LIMIT 1", arrayOf(table))
        .use { it.moveToFirst() }

internal fun SupportSQLiteDatabase.columnExists(table: String, column: String): Boolean =
    query("PRAGMA table_info(`${table}`)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        if (nameIndex < 0) return@use false
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == column) return@use true
        }
        return@use false
    }
