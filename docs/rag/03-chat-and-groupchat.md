# 03 · 会话与群聊

一句话：**单聊和群聊共享「图片管线」和「文档附件」两套基础设施，其余各自独立；群聊多出场景、动作、摘要三件事。**

## 单聊消息流

发一条消息经过的环节，按顺序：

```
ChatViewModel.onSendMessage()
 ├─ ensureProviderConfigured()        没有可用模型就停在这里
 ├─ mImageCoordinator.state.processing  上一次附件还在处理，直接返回
 ├─ attachments 取出并**立刻**从输入区清空
 ├─ mImageCoordinator.draftInputs()   拿到 pending 的图片输入
 ├─ ChatPromptBuilder.build(context)  拼提示词（人设、外观图、世界书、历史、摘要）
 ├─ mLLMRepository.generate/stream(...)
 └─ refreshUiState(...)               重读数据、重建状态、重新发布
```

`refreshUiState` / `loadNormalState` 是这套设计里最危险的地方：**它们重建整个状态对象**，参数表里没列出的字段会被静默回落到默认值。加任何状态字段都必须同时改这两处的参数表并透传，否则表现是「操作看起来生效了，一刷新就没了」（附件丢过一次、角色列表白屏过一次，都是这个原因）。

再加一条纪律：**要 `await` 之后再改状态，一律用 `CoreViewModel.updateState { state -> ... }`，不要拿进方法那一刻的快照去 `copy().setup()`**。快照在 `withContext` 期间已经过期，写回去就是把期间发生的变化整片盖掉。

典型触发场景：从系统文件选择器返回时，`onResume` 的 `loadNormalState`（读库慢）与文件回调（读文件快）几乎同时到。文件先挂上，`loadNormalState` 后回来把状态重建成「附件为空」——用户看到提示说成功了，界面上什么都没有。

配套两条兜底：
- `loadNormalState` / `loadState` 的 `attachedDocuments` 默认值取**当前状态**（`getOrNull<...>()?.conversationState?.attachedDocuments.orEmpty()`），要清空必须显式传 `emptyList()`。
- `inputDraft` 不走这条兜底，仍然靠调用方显式传空串——发送后清空草稿依赖它。

## 图片管线

两个类，职责分得很开：

| 类 | 位置 | 职责 |
| --- | --- | --- |
| `MessageImageRuntime` | `libs/media/MessageImageRuntime.kt` | 单张图的准备、缩放、抽帧、落盘、生成 `LLMImageReference` |
| `MessageImageCoordinator` | `libs/media/MessageImageCoordinator.kt` | 一次发送的草稿集合、处理进度、错误、提交 |

**`mPendingPick` 是这套里的暗礁**：`choose(editing)` 会置一个待选标记，`pick()` 开头取出并清空，取不到就报「图片无法处理」。系统相册入口会调 `choose`，而「附加文件」这条路曾经没调——于是从文件里选出的图片全部报错。现在两条路都调。

`draftInputs()` 返回 `List<MessageImageInput>`，有两种：本轮刚处理的 `Prepared`，和文件早在库里的 `Existing`。**不能只 `filterIsInstance<Prepared>()`**，那样从图库挑的图会被静默丢弃。真正落库前 `Existing` 要 `mutation.copyReference(uuid)`，否则两条消息共用一条 `FileEntity`，删一条会带走另一条的附件。

### GIF

GIF 有三层处理：

1. **入库不压缩**。`process()` 里 `if (settings.mode == Original || isAnimated(source))` 原样拷贝字节。否则 `encodeCompressed` 解码成 `Bitmap` 只有首帧，后面再怎么抽帧都无米下锅。
2. **抽帧按 GIF 结构算**。`libs/media/GifFrameTiming.kt` 自己走 GCE，读每帧 delay（单位 1/100 秒），延迟 0 抬到下限。`Movie.duration()` 对很多工具生成的 GIF 返回 0，靠它必挂。
3. **多帧拼成连环画**。三帧并排放进一张 PNG，中间留 `STRIP_GAP_PX = 12` 的缝。三张独立图在模型眼里是三个画面，连成一排才看得出「先这样、再这样」。

抽帧上限和最小延迟可配：`AppModel.gifSampleFrames` / `AppModel.gifFrameDelayMs`，解码在 `libs/media/GifCaptureSettings.kt`。

## 文档附件

聊天里发文件**不是把正文塞进输入框**。链路是：

- 读取：`libs/media/DocumentText.kt`（docx 走 ZipInputStream 找 `word/document.xml`；纯文本走 512KB 上限 + NUL 检测）。
- 落盘：`libs/media/DocumentStore.kt`，`filesDir/chat-documents/` 下每份存 `<id>.txt` 正文与 `<id>.name` 原名。
- 消息体：只放引用 `[文件：名字#id]`（`AttachedDocument.renderForMessage()`）。
- 界面：`ui/widgets/MessageDocumentBlock.kt` 切段 + `MessageDocumentCard.kt` 折叠卡片，展开时才从 `DocumentStore` 读正文。
- 提示词：`ChatPromptBuilder.buildDocumentPrompt()` 按引用把全文（每份上限 12000 字）注入。

这个设计换来一个关键性质：**用户撤掉待发附件时，那份文件仍在库里，角色下轮照样读得到**。早期把正文直接拼进消息，撤掉就真没了。

### 选中的文件怎么分流

`libs/media/PickedFileReader.kt` 一个类管完，单聊群聊共用（早先两边各抄一份，改一边忘一边）：

- `isImage(uri)`：先看 `contentResolver.getType()` 的 MIME，再退回扩展名（`IMAGE_EXTENSIONS` 含 png/jpg/jpeg/webp/gif/bmp/heic/heif）。
- 图片交给 `MessageImageCoordinator`；其余按文档读（docx 走 `DocumentText.readDocx`，纯文本走 512KB + NUL 嗅探）。
- 返回 `Result(images, documents, pickedCount)`，三种结局都要给回执：全图、读不出、成功。早期群聊这条路连成功回执都没有，用户点完完全没反应。

扩展名判定有个坑：`报告.png.bak` 不是图。用 `endsWith` 前先确认扩展名在列表里，别用 `contains`。

## 会话级背景

`PageBackgroundRepository`（`libs/room/repository/`）按页面键存取，键是 `chat:<sessionId>` / `group:<sessionId>`。表和迁移见 02 篇。

渲染仍是 `ui/widgets/ChatBackground.kt` 的 `Modifier.chatBackgroundLayer(state, scrimColor)`，没有引入上游那套 `PageSurface` 外观体系。

两层回落：会话自己有图就用它，没有（`get()` 返回 null）就用全局 `AppModel.chatBackgroundImage`。`ChatUiState.Normal.hasOwnBackground` / `GroupChatUiState.Normal.hasOwnBackground` 只影响设置页显示「恢复跟随全局」那一行。

**群聊的背景曾经从来没生效过**：`backgroundState` 字段早就存在，但 `GroupChatViewModel` 一次都没给它赋过值，一直是默认空图。接会话级背景时才发现。

## 群聊：场景

场景是「现在待在哪儿」的运行时分组，存在 `GroupChatSession.scenesJson`。模型在 `libs/groupchat/model/GroupChatScene.kt`，纯函数在 `libs/groupchat/GroupChatScenes.kt`，切换语义在 `libs/groupchat/GroupChatSceneTransition.kt`。

场景字段：`id` / `name` / `description` / `characterIds` / `entryPrompt` / `lorebookEntryIds` / `visibility` / `backgroundUuid`。`visibility` 是 `SceneVisibility.Open | Secret`。

一个场景影响四件事：

| 影响面 | 表现 |
| --- | --- |
| 在场成员 | `characterIds` 里的人说话；其余 `inScene = false`，卡片变暗、标「不在场」、不可点 |
| 世界书 | `lorebookEntryIds` 勾了就**覆盖**会话那批（不是取交集）；没勾就不干预 |
| 背景图 | `backgroundUuid` 非空时整页背景换成它；解析顺序是「当前场景 → 会话 → 全局 → 纯色」 |
| 提示词 | `GroupChatPromptBuilder.sceneSection()` 写明在场名单、「这些角色此刻在别处」、是否私下前往、要求对位置变化作出反应 |

「全员」不是一个特例，而是 `GroupChatScenes.DEFAULT_SCENE_ID = "*"` 这条普通场景：它出现在设置页的场景列表里，可以改名、配背景、写描述，但 `allowedCharacterIds` 对 `"*"` 恒返回 null —— 不限制成员。`active()` 取到 `"*"` 时按「没有场景」处理（全员在场、不过滤世界书）。`movements()` 会把 `"*"` 过滤掉，因为退回全员已经由「离开当前场景」那条表达了。

### 切换时先算「人」的去向

`GroupChatSceneTransition.plan(from, to, allMemberIds)` 输出三拨人：

| 集合 | 判据 | 谁会这样 |
| --- | --- | --- |
| `companions` | 前后两场都在 | 跟着玩家走过去的 |
| `leftBehind` | 只在前一场 | 被留在原地的 |
| `alreadyThere` | 只在后一场 | 本来就在目的地等着 |

`secret = (to ?: from)?.visibility == Secret` —— 涉及隐蔽场景的移动都算偷偷，包括**从**秘密地点溜回全员，因为瞒着人这件事在回来的路上并没有结束。

`narration(plan, nameOf)` 把三类结果翻成旁白片段（`Part` sealed：`LeaveToEveryone` / `EnterAlone` / `EnterWith` / `MoveAlone` / `MoveWith` / `SlipAway` / `SlipAwayWith` / `LeftBehind` / `AlreadyThere`），再由 ViewModel 映射成 9 个语言资源键。两条硬规则：

- `to == null`（回全员）时**只**返回一句 `LeaveToEveryone`：「全员」不是具体地点，谈不上谁被留下。
- `plan.secret` 时**不写** `LeftBehind`：写出来等于当众宣布你走了，和「隐蔽」自相矛盾。目的地的人照样知道你到了。

切完立刻让新场景里的一个角色开口（`launchGeneration`），否则界面上就剩一行灰色小字、后面什么都没有——用户会以为功能没生效。

### 进出场景的三条路，**没有关键字自动匹配**

1. 顶栏场景标签手动点（`GroupChatUiIntent.SwitchScene`）。
2. 「+」菜单里的动作，选项由 `GroupChatScenes.movements(scenes, activeSceneId)` 生成：列出别的场景 + 一条退回全员。
3. `/去 厨房`、`/scene clear` 指令，只认行首前缀，**被消费掉不进模型**（喂给 LLM 只会被念成台词）。

明确不做自然语言关键字匹配：一句话里出现「厨房」太容易误判，角色说「厨房里好像有动静」不该换场。

**唯一例外**是设置里的实验性模块「关键字自动换场」（`ExperimentalFeature.SceneKeywordSwitch`，默认关闭）。打开后 `GroupChatScenes.matchByKeyword(scenes, text, activeSceneId)` 会在用户发送前猜一次：

- 判据是「**命中字数最多**的那个场景」——同一个词被两个场景共享时，长的那个更具体。
- 已经在当前场景不算「往这里走」，返回 null，避免原地刷一轮旁白。
- 以 `/` 开头（指令）和空输入直接返回 null。
- `DEFAULT_SCENE_ID`（全员）永不参与匹配。

关键词配在场景的 `keywords` 里，编辑器还没做 UI，目前只能手写 JSON。开关关闭时这个函数**根本不会被调用**，行为与加这个功能之前逐字一致。

## 群聊：动作预设

动作 = 预置的一句话模板，形如 `（摸了摸 {name} 的头）`。

| 层级 | 存放 | 说明 |
| --- | --- | --- |
| 全局默认 | `AppModel.pokeActions`（Kotpref，JSON） | 设置页「提示词与上下文记忆 → 动作」 |
| 单聊会话 | `ChatSession.pokeActionsJson` | 空串跟随全局；改过之后设置页多一条「恢复跟随全局」 |

群聊不持有独立配置，用全局那份。**群聊的「+」只列不带 `{name}` 的动作**（`PokeActions.selfDirected`）——群里没有天然的对象，带占位符的走长按成员卡片，目标由手指决定不会错。模板里 `{name}` 替换成对象名。

## 正则：谁在什么时候套哪一段

正则分三个时机，别混：

| 时机 | 入口 | 拿什么脚本 | 实现 |
| --- | --- | --- | --- |
| 用户输入落库前 | `UserInput` 阶段 | 当场查库 | `ChatRegexApplier.forUserInput` / `GroupChatRegexApplier.applyUserInput` |
| 角色回复落库前 | `Source` 阶段 | 当场查库 | `ChatRegexApplier.forAiResponse` / `applyAiResponse` |
| 流式生成收尾 | `Source` 阶段（只跑一次） | **开播时冻结的快照** | ViewModel 里的 `applyStreamingAiRegex()` |
| 渲染到界面 | `Display` 阶段 | 当场查库 | `mRegexProcessor.applyDisplay(...)` |

**为什么流式那一条不抽走**：收尾可能运行在协程取消后的 `NonCancellable` 区域，此时再查库会与「用户途中改了脚本」打架，同一条回复的前后两半就会套不同规则。所以它必须用快照，快照字段（`mStreamingRegexScripts` / `mStreamingRegexMacros`）也就只能留在 ViewModel 上。

`ChatRegexApplier`（`feature/chat/logic/`）与 `GroupChatRegexApplier`（`feature/groupchat/logic/`）解决的是另一件事：**同一份上下文被算了三遍**——用户输入一遍、角色回复一遍、编辑消息又一遍，每次都重新 `getSessionById` + `getCharacterById` + `activeScripts` + 拼宏。现在群聊那边用 `prepare()` 一次装配、多个发言人复用。

群聊的宏规则单独一条：`{{char}}` 取**当前发言者**，`{{group}}` 取全体成员名单（`joinToString(", ")`，顺序跟着成员列表）。所以 `GroupChatRegexApplier.macros(context, speakerName)` 必须显式传发言人，别用「第一个成员」凑。

## 其他几处容易忘的开关

- **清空上下文**：只删消息与摘要，会话条目、角色绑定、世界书选择全保留。群聊侧要一起清 `GroupChatSummaryDao`，否则模型还从摘要里带着上一轮结论。
- **人设继承**：会话里 `userName` / `userDescription` 为空才回落到全局（`libs/user/UserPersona.kt` 三个纯函数）。回落必须在 ViewModel 构造 context 时做完，**不能在 prompt builder 里做**——那是纯逻辑层，碰全局设置会在单测里炸 `Kotpref has not been initialized`。
- **思维链**：消息内容里 `<think>…</think>` 与正文拼在一起存。读写分离靠 `libs/llm/ThinkBlock.kt` 四个纯函数，编辑时分成两个输入框，思维链清空就不写标签。

## 最后核对

随 `v1.24` 核对：`libs/media` 12 文件、`libs/groupchat` 7 文件（新增 `GroupChatSceneTransition.kt`）、`MessageImageCoordinator.draftInputs()` 返回 `List<MessageImageInput>`、`ChatSession.pokeActionsJson` 在 v7 schema、`PageBackground` 在 v8 schema、`GroupChatScene` 已含 `visibility` 与 `backgroundUuid`。

历史变更见 [CHANGELOG](../CHANGELOG.md)。
