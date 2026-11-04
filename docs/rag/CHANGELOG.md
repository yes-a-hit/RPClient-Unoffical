# RAG 文档版本变更记录

这份文件按版本记录「实现层面变了什么」，用来回答两个问题：

1. 手上这份文档是不是过时的？（对当前版本号，看最后一节）
2. 某个行为是哪一版改的？上一版是怎么写的？

**与其他文件的分工**：八篇正文永远描述**当前实现**，不写历史；`git log` 记的是提交；`TODO.md` 记的是待办。版本之间的实现差异记在这里。

格式约定：一节一个版本，倒序排列。每条写「变了什么 + 为什么」，不写「优化了体验」这种无法核对的话。

---

## v1.24

- **新增实验性模块框架**（`libs/experimental/`）：`ExperimentalFeature` 登记 + `ExperimentalFlags` 开关，设置页新增独立一栏（`feature/main/ui/MainExperimentalPanel.kt`）。默认全关；`available = false` 的登记项永远返回 false，即使偏好里被写进该 key。
- **首个实验功能**：群聊「关键字自动换场」。`GroupChatScene` 加 `keywords: List<String>`；`GroupChatScenes.matchByKeyword` 按「命中字数最多」取场景，已在当前场景/指令/空输入/全员场景都不匹配。开关关闭时该函数不被调用。
- **场景提示带国际化**：原来 `GroupChatSceneViews.kt` 里「场景「X」」「在场：」「世界书：…」「这次是悄悄走的：…」四条是硬编码中文，改为 `group_chat_scene_banner_title` 等五个键（13 locale）。
- `GroupChatScene` 加 `visibility`（`Open` / `Secret`）与 `backgroundUuid`；`GroupChatScenes` 加 `DEFAULT_SCENE_ID = "*"` 与 `activeForDisplay`。
- **`GroupChatSceneTransition`**：切换前先算人的去向（`companions` / `leftBehind` / `alreadyThere` / `secret`），译文写进历史，再让在场的人应一句。隐蔽行程不写「谁被留下」——说出口等于当众宣布你走了。

## v1.23

- **结构拆解**：13 个超过 800 行的 UI 文件拆到 ≤800（`StoryEditorLayout` 4484→712、`MainLayout` 3211→876、`GroupChatLayout` 2848→798、`ChatLayout` 2375→798 等），新增 56 个按职责命名的文件。ViewModel 与 Repository **有意不拆**，判据见 01 篇。
- **解耦**：正则套用抽成 `feature/chat/logic/ChatRegexApplier.kt` 与 `feature/groupchat/logic/GroupChatRegexApplier.kt`。
- **RAG 建立**：`docs/rag/` 八篇首版。

## v1.22

- 会话级背景（`PageBackgroundEntity` / `PageBackgroundRepository`，DB `version = 8`）。两处偏离上游：`fileUuid` 唯一索引改普通索引（一张图可给多个会话当背景）、外键 `RESTRICT` 改 `CASCADE`。顺手修掉「群聊 `backgroundState` 从来没被赋值」。
- 剧本越界拦截（`ScriptPlayGuard`）：回复落库**之前**判断，命中软规则直接丢弃该轮，命中硬规则转惩罚场景。

## v1.21

- 导出范本（`libs/sample/SampleDocument.kt`）：四类数据的带注释 JSON 范本；占位符词典（`ui/widgets/MacroDictionary.kt`）；首次打开新版本弹更新摘要。

## v1.20 及更早

早于 RAG 建立，实现细节散在 `git log`。摘几条仍在影响当前代码的：

- 文档附件三次改向：正文塞进消息 → 外置成 `[文件：名字#id]` 引用 + `DocumentStore` → 定稿为**引用 + 卡片 + 提示词注入全文**（见 03 篇）。
- 附件「提示成功但界面什么都不显示」：`loadNormalState` 拿进方法时的快照写回，把期间挂上的附件盖成空 → 引入 `updateState`（见 01、07 篇）。
- GIF 只识别头帧：根因是**入库时被压成静图**，不是抽帧代码没跑（见 03 篇）。
- 会话级动作预设（`chat_sessions.pokeActionsJson`，DB `version = 7`）。
