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

## 群聊：场景

场景是「现在待在哪儿」的运行时分组，存在 `GroupChatSession.scenesJson`。模型在 `libs/groupchat/model/GroupChatScene.kt`，纯函数在 `libs/groupchat/GroupChatScenes.kt`。

一个场景影响三件事：

| 影响面 | 表现 |
| --- | --- |
| 在场成员 | `characterIds` 里的人说话；其余 `inScene = false`，卡片变暗、标「不在场」、不可点 |
| 世界书 | `lorebookEntryIds` 勾了就**覆盖**会话那批（不是取交集）；没勾就不干预 |
| 提示词 | `GroupChatPromptBuilder.sceneSection()` 写明在场名单与「这些角色此刻在别处」 |

进出场景有两条路，**没有关键字自动匹配**：

1. 顶栏场景标签手动点（`GroupChatUiIntent.SwitchScene`）。
2. 「+」菜单里的动作，选项由 `GroupChatScenes.movements(scenes, activeSceneId)` 生成：列出别的场景 + 一条退回全员。

明确不做自然语言关键字匹配：一句话里出现「厨房」太容易误判，角色说「厨房里好像有动静」不该换场。

还有 `/去 厨房`、`/scene clear` 这类指令，只认行首前缀，**被消费掉不进模型**（喂给 LLM 只会被念成台词）。

## 群聊：动作预设

动作 = 预置的一句话模板，形如 `（摸了摸 {name} 的头）`。

| 层级 | 存放 | 说明 |
| --- | --- | --- |
| 全局默认 | `AppModel.pokeActions`（Kotpref，JSON） | 设置页「提示词与上下文记忆 → 动作」 |
| 单聊会话 | `ChatSession.pokeActionsJson` | 空串跟随全局；改过之后设置页多一条「恢复跟随全局」 |

群聊不持有独立配置，用全局那份。**群聊的「+」只列不带 `{name}` 的动作**（`PokeActions.selfDirected`）——群里没有天然的对象，带占位符的走长按成员卡片，目标由手指决定不会错。模板里 `{name}` 替换成对象名。

## 其他几处容易忘的开关

- **清空上下文**：只删消息与摘要，会话条目、角色绑定、世界书选择全保留。群聊侧要一起清 `GroupChatSummaryDao`，否则模型还从摘要里带着上一轮结论。
- **人设继承**：会话里 `userName` / `userDescription` 为空才回落到全局（`libs/user/UserPersona.kt` 三个纯函数）。回落必须在 ViewModel 构造 context 时做完，**不能在 prompt builder 里做**——那是纯逻辑层，碰全局设置会在单测里炸 `Kotpref has not been initialized`。
- **思维链**：消息内容里 `<think>…</think>` 与正文拼在一起存。读写分离靠 `libs/llm/ThinkBlock.kt` 四个纯函数，编辑时分成两个输入框，思维链清空就不写标签。

## 最后核对

随 `v1.13`（`dc52dfe0`）核对：`libs/media` 11 文件、`libs/groupchat` 6 文件、`MessageImageCoordinator.draftInputs()` 返回 `List<MessageImageInput>`、`ChatSession.pokeActionsJson` 已存在于 v7 schema。
