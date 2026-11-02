package me.kafuuneko.rpclient.libs.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 主业务数据库 v6→v7：单聊会话多一份自己的动作预设。
 *
 * 动机很直接：全局那套动作是「我对所有人都这么打招呼」，但同一张角色卡，
 * 摸头的措辞在不同剧本里该不一样。会话级覆盖留空即跟随全局，
 * 老库升上来全是空串，行为与升级前完全一致。
 *
 * 沿用手写迁移而不是 `AutoMigration`：这条链上已经出现过「版本号相同、结构不同」的库，
 * 按列判断能不能加比按版本号判断更稳。
 */
object AppDatabaseMigration6To7 : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 空串 = 没有独立配置，读取时回落到全局；不存 null 是为了少一种分支。
        db.addColumnIfMissing(
            table = "chat_sessions",
            column = "pokeActionsJson",
            definition = "TEXT NOT NULL DEFAULT ''"
        )
    }
}
