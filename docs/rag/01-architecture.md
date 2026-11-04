# 01 · 架构与约定

一句话：**按 feature 竖切、每个 feature 内部四件套（Intent / State / ViewModel / Layout），跨 feature 共享的东西全部下沉到 `libs/`。**

## 目录长相

```
app/src/main/java/me/kafuuneko/rpclient/
├─ RPClientApp.kt          Application：Koin 容器、Room 构建、OkHttp 单例
├─ libs/                   与界面无关的能力层，可被任何 feature 使用
│  ├─ room/                entity / dao / repository / model / migration
│  ├─ llm/                 客户端、适配器、模型目录、Token 用量
│  ├─ prompt/              提示词拼装：ChatPromptBuilder / GroupChatPromptBuilder / 预算裁剪
│  ├─ media/               图片与文档附件：MessageImageRuntime、MessageImageCoordinator、DocumentStore
│  ├─ gallery/             应用内图库
│  ├─ assistant/           助手会话与工具执行
│  ├─ script/ story/       剧本与故事
│  ├─ chat/ groupchat/     会话与群聊的纯逻辑（提示词、场景、动作）
│  ├─ experimental/        实验性模块的登记与开关（默认关闭、显式标风险）
│  └─ core/                MVI 基类
├─ feature/                界面层，一个 feature 一个包
│  ├─ main/                首页与设置（home/ settings/ 各自分文件）
│  ├─ chat/ chatcreate/    单聊（logic/ + presentation/ + ui/）
│  ├─ groupchat/ groupchatcreate/
│  ├─ characterlist/ characteredit/
│  ├─ script/ scriptplay/ scriptadventure/ scripteditor/
│  ├─ assistant/ llmprovideredit/ tokenusage/ about/ …
└─ ui/                     跨 feature 的 Compose 组件（widgets/ dialog/ message/）
```

feature 内部分四层，文件名就是契约：

```
feature/chat/
├─ ChatActivity.kt                    只做注册 ActivityResult、转发生命周期
├─ ChatViewModel.kt                   全部状态与副作用
├─ logic/ChatRegexApplier.kt          可测的纯逻辑（不碰 UiState、不发事件）
├─ presentation/ChatUiIntent.kt       用户意图（sealed）
├─ presentation/ChatUiState.kt        界面状态（data class）
└─ ui/ChatLayout.kt                   纯渲染，不含业务判断
```

**`logic/` 是可选的第五层**，只在 ViewModel 里出现「同一段取数 + 拼装被算了好几遍」时才建。它的判据不是行数，是**这段代码能不能脱离 UiState 单独写单测**：

| 该放 `logic/` 的 | 该留在 ViewModel 的 |
| --- | --- |
| 查库拼装（脚本 + 宏、提示词上下文） | 改 UiState（`onChangeXxx` / `onToggleXxx`） |
| 纯计算（分页、裁剪、去重、归一化） | 发 `AppViewEvent`、弹 toast、导航 |
| 文本解析（标记切分、占位符替换） | 调 `viewModelScope.launch` 编排时序 |

既有例子：`feature/chat/logic/ChatRegexApplier.kt`、`feature/groupchat/logic/GroupChatRegexApplier.kt`。

**UI 层按职责分文件，不按行数切。** 一个 `XxxLayout.kt` 超过 800 行时，按「这块界面在讲什么事」拆，拆完的文件名就是它的职责：

```
feature/storyeditor/ui/
├─ StoryEditorLayout.kt        入口 + 骨架（顶栏、主区、模式切换）
├─ StoryTextEditor.kt          正文编辑器与保存状态
├─ StoryOutlinePage.kt         大纲页
├─ StorySettingsPage.kt        设置页
├─ StoryEditorDialogs.kt       这一页自己的对话框
├─ StoryEditorBottomBars.kt    底部操作条
└─ StoryGenerationStatus.kt    生成状态卡
```

拆的机械约束见 `07-pitfalls.md` 第十七条（`private` 是文件级可见）。

## MVI 四件套怎么接

基类在 `app/src/main/java/me/kafuuneko/rpclient/libs/core/`：

- `CoreActivityWithEvent.kt` / `CoreViewModelWithEvent.kt`：带事件通道的版本，本项目主用。
- `@UiIntentObserver(SomeIntent::class)` 标注在 ViewModel 的挂起函数上，框架负责分发。
- 发意图：`ChatUiIntent.XxxClick.emit()`（Compose 里直接用）。
- 改状态：`getOrNull<ChatUiState.Normal>()?.copy(...)?.setup()`。
- 需要 await 之后再改状态：`updateState { state -> ... }`（`CoreViewModel.kt`）。
  `updateState` 每次读当前值并用 `MutableStateFlow.update` 的 CAS 循环写回，
  不会像 `uiState.copy()` 那样把 IO 期间别人的改动整片盖掉。详见 07 篇第二条变体。
- 抛一次性事件（Toast、开新页）：`AppViewEvent.Xxx.tryEmit()`，Activity 侧收。

**这里有一个反复出现的坑**：状态字段加在哪一层决定了界面认不认它。`ChatUiState.Normal` 上的字段（如 `pokeActionEditorVisible`）一改就弹；`ChatConversationState` 里的字段只在会话区生效。写错过一次，编译能过、界面永远不响应，只能靠 `grep` 同类字段的位置来定位。

## Koin

容器集中在 `app/src/main/java/me/kafuuneko/rpclient/RPClientApp.kt`。约定：

- 仓储、运行时、编码器一律 `single { }`。
- 构造参数用 `get()`；给默认值是为了让既有调用点不用改（加参数时**必须**带默认值，否则所有 `single` 声明一起炸）。
- 在 Compose 里临时取实例：`org.koin.core.context.GlobalContext.get().get<X>()`。**注意包名**，项目里另有 `libs.core.GlobalContext`，写错了报 unresolved。

## Room

数据库定义在 `app/src/main/java/me/kafuuneko/rpclient/libs/room/AppDatabase.kt`，当前 `version = 8`。

- 新增表 → **手写 Migration**（建表 + 建索引两条 SQL，配 `IF NOT EXISTS`）。
- 新增列 → 同样手写，配合 `addColumnIfMissing`（`libs/room/migration/`）。理由见 02 篇。
- 每加一版必须提交 `app/schemas/me.kafuuneko.rpclient.libs.room.AppDatabase/<版本>.json`，否则 `runMigrationsAndValidate` 无从校验。

## 状态更新的两条路

| 场景 | 用什么 |
| --- | --- |
| 同步处理 intent，没有 await | `uiState.copy(...).setup()` 就够 |
| 中间有 `withContext` / `suspend` 调用 | `updateState { state -> state.copy(...) }` |

`CoreViewModel`（`libs/core/CoreViewModel.kt`）里 `setup()` 是直接赋值，`updateState()` 走 `MutableStateFlow.update` 的 CAS 循环。前者用进方法那一刻的快照，后者读的是当下最新的值。

判断标准很简单：**方法体里有没有让出协程的点**。有就一律 `updateState`，别赌「这期间状态不会变」——附件掉过两次之后，这条已经写进 07 篇。

## 实验性模块：高风险功能怎么隔离

有些功能出错的方式不是界面错位，而是**角色说出不该说的话**、或者存档里多出一份自己都解释不清的状态。这类东西不该和普通设置混在一起——用户会当成正式能力去依赖，出了事又说不清是哪个开关。

落在 `libs/experimental/`：

| 文件 | 职责 |
| --- | --- |
| `ExperimentalFeature.kt` | 登记项枚举：`key` / `titleRes` / `descriptionRes` / `risk` / `available` |
| `ExperimentalFlags.kt` | 读写开关，存在 `AppModel.experimentalFeatures`（逗号分隔的 key） |

三条约定：

1. **默认关**。`isEnabled()` 是唯一读入口，没写过的旧安装就是空串 = 全关。
2. **存 key 不存 ordinal**。重排枚举不会让用户的开关错位。
3. **`available = false` 的登记项永远返回 false**，即使偏好里被写进了这个 key（备份带过来、降级安装）——空壳开关不能显示成「已启用」。

这也是插件接口的形状：外部模块接进来只需要提供一个 `ExperimentalFeature` 加上自己的判断，被调用点写成 `if (ExperimentalFlags.isEnabled(X)) ...`，不需要改主流程。

调用点要写成「开关打开后才走这条路」，并且开关关闭时**行为与加这个功能之前逐字一致**。现有样例：`feature/groupchat/GroupChatViewModel.kt` 里用户在发送前判断 `SceneKeywordSwitch`，关闭时连 `matchByKeyword` 都不会被调用。

UI 在 `feature/main/ui/MainExperimentalPanel.kt`，列表由枚举驱动，加一个功能不用改这个文件。

## 文案

13 个 locale 目录，缺一个就会在对应语言下显示英文：

```
values  values-b+zh+Hans  values-b+zh+Hant  values-b+pt+BR  values-de
values-es  values-fr  values-id  values-it  values-ja  values-ko
values-ru  values-vi
```

新增字符串时**一次补齐 13 个**。注意两个具体的坑：目录名拼路径要写 `"values" + "-b+zh+Hans"`（`values` 自身要特判）；含单引号的译文必须整段用双引号包住，否则资源编译报 `Invalid unicode escape sequence`。

## 最后核对

随 `v1.24` 核对：`libs/core` 6 文件、`libs/experimental` 2 文件、`AppDatabase.kt` 里 `version = 8`、13 个 locale 目录齐全、`CoreViewModel.updateState` 已存在、`feature/*/logic/` 下已有 `ChatRegexApplier.kt` 与 `GroupChatRegexApplier.kt`。历史变更见 [CHANGELOG](../CHANGELOG.md)。
