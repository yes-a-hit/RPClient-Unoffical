# 02 · 数据层

一句话：**Room 单库 `AppDatabase`（v8）承载全部持久化；schema 变更一律手写迁移；备份是整库 SQL 快照，不是逐表导出。**

## 表清单

`app/src/main/java/me/kafuuneko/rpclient/libs/room/entity/` 下 23 个实体，按用途分五组：

| 组 | 实体 |
| --- | --- |
| 角色与世界书 | `Character`、`CharacterLLMProviderAssociation`、`Lorebook`、`LorebookEntry` |
| 单聊 | `ChatSession`、`ChatMessage`、`MessageImageEntity` |
| 群聊 | `GroupChatSession`、`GroupChatMember`、`GroupChatMessage`、`GroupChatSummary` |
| 故事与剧本 | `Story`、`StoryChapter`、`StoryVolume`、`StoryCharacter`、`StoryLorebookEntry` |
| 界面 | `PageBackground` |
| 其他 | `LLMProvider`、`LLMRequestLog`、`LLMTokenUsageRecord`、`RegexScriptEntity`、`RegexCharacterAuthorization`、`FileEntity` |

`FileEntity` 是所有附件的索引：图片、文档、头像、备份临时文件都走它。**字节按 hash 共享，uuid 行不共享**——同一个文件被两条消息引用时必须 `copyReference` 出新 uuid（见 03 篇「图库挑图」）。

## 哪些信息没有独立列

角色卡的自定义栏目（年龄、性别、性经历、边界、喜好等）**不建表**，全部塞在 `Character.extensionsJson` 里，命名空间 `rpclient_profile`，由 `app/src/main/java/me/kafuuneko/rpclient/libs/character/CharacterProfileCodec.kt` 负责读写。

群聊场景同理，存在 `GroupChatSession.scenesJson`；场景数量是个位数，跟着会话走才不会出现「会话没了场景还留着」。场景自己的背景图不存字节，只存 `backgroundUuid`（指向 `FileEntity`），和场景一起躺在那个 JSON 里。单聊的动作预设存在 `ChatSession.pokeActionsJson`，**空串表示跟随全局**。

## 会话级背景

`PageBackground` 是唯一一张「一行管一个页面」的表：主键是 `page` 字符串，取值 `chat:<sessionId>` / `group:<sessionId>`（`PageBackgroundEntity.chatPage()` / `groupPage()` 生成）。

两处**有意偏离上游**，不是笔误：

| 上游 | 这里 | 为什么 |
| --- | --- | --- |
| `fileUuid` 唯一索引 | 普通索引 | 同一张图要给多个会话当背景时，唯一索引会直接插入失败 |
| 外键 `onDelete = RESTRICT` | `CASCADE` | 文件被清掉后，背景行跟着走，不留悬空引用 |

上游那套 `blur` / `scrim` / `opacity` / `focalX` / `topBarBlur` 字段全部保留，虽然当前 UI 一个都没用——以后接外观体系时不用再迁一次。

**语义：没有记录 ≠ 没有背景**。`PageBackgroundRepository.get()` 返回 null 时由调用方回落全局 `AppModel.chatBackgroundImage`，所以「清空」必须显式删行，不能靠「没设过」。

删会话时要调 `clearByPrefix(PageBackgroundEntity.chatPage(id))`，否则图会一直占着盘。

## 版本与迁移

当前 `version = 8`（`app/src/main/java/me/kafuuneko/rpclient/libs/room/AppDatabase.kt:77`）。历史：

| 版本 | 变更 | 方式 |
| --- | --- | --- |
| 1→5 | 上游既有演进 | `AutoMigration` |
| 5→6 | `group_chat_sessions` 加 `scenesJson`、`activeSceneId` | 手写 `AppDatabaseMigration5To6` |
| 6→7 | `chat_sessions` 加 `pokeActionsJson` | 手写 `AppDatabaseMigration6To7` |
| 7→8 | 新表 `page_backgrounds` | 手写 `AppDatabaseMigration7To8` |

**为什么从 AutoMigration 改成手写**：自动迁移拿「声明的旧 schema」去比对实际库。同一台机器上可能同时存在结构不同、却都自称 v5 的库（用户装过带场景的构建又回退过）。这种情况下自动迁移直接判定 schema 不符、拒绝打开，表现是**升级后应用起不来**。手写迁移用 `PRAGMA table_info` 逐列判断，列已在就跳过，两条来源都能过。

工具函数在 `app/src/main/java/me/kafuuneko/rpclient/libs/room/migration/`：`addColumnIfMissing`、`tableExists`、`columnExists`。

新增列的动作清单（缺一步就白做）：

1. 实体加字段，**必须带默认值**（`= ""` / `= emptyList()`），否则老数据反序列化成 null。
2. `AppDatabase` 的 `version + 1`，`autoMigrations` 里**不要**加这一对。
3. 写 `AppDatabaseMigrationXToY`，用 `addColumnIfMissing`。
4. `RPClientApp.kt` 的 `.addMigrations(...)` 追加。
5. 跑一次构建，提交新生成的 `app/schemas/.../<version>.json`。
6. 在 `app/src/androidTest/java/me/kafuuneko/rpclient/libs/room/AppDatabaseMigrationTest.kt` 加一条用例，断言默认值。

## 备份与恢复

实现在 `app/src/main/java/me/kafuuneko/rpclient/libs/backup/PortableBackupRepository.kt`，产出 ZIP，内含 `sqlite_master` 的建表语句与逐行数据。

**必须排除 `room_master_table`**。这张表存 Room 的 schema identity hash，恢复时原样重建会把旧 hash 带回来，Room 判定数据损坏、拒绝打开——症状就是「恢复完成后应用起不来」。`android_metadata` 同样排除。仓库里五处（查询、写入判断、表名列表、读旧行、重建）都要排，漏一处就复发一次。

`exportArchive(destination, includeSecrets)` 的 `includeSecrets` 默认 `false`：这个页面最常见的用途是卸载前抢救资料，默认不勾上密钥更安全。

## 测试约定

- 单元测试放 `app/src/test/`，用 `org.junit.Test` + `org.junit.Assert`，**不用 kotlin.test**。
- 需要真库/真仪器跑的放 `app/src/androidTest/`，`testDebugUnitTest` 不会编译到它们——所以 androidTest 坏掉不会挡住主构建，容易积债（曾积了一轮，见 07 篇）。

## 最后核对

随 `v1.24` 核对：23 个实体、`version = 8`、`AppDatabaseMigration5To6` / `6To7` / `7To8` 均在、`7.json` 与 `8.json` 均已入库。

历史变更见 [CHANGELOG](../CHANGELOG.md)。
