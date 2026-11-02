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
│  └─ core/                MVI 基类
├─ feature/                界面层，一个 feature 一个包
│  ├─ main/                首页与设置
│  ├─ chat/ chatcreate/    单聊
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
├─ presentation/ChatUiIntent.kt       用户意图（sealed）
├─ presentation/ChatUiState.kt        界面状态（data class）
└─ ui/ChatLayout.kt                   纯渲染，不含业务判断
```

## MVI 四件套怎么接

基类在 `app/src/main/java/me/kafuuneko/rpclient/libs/core/`：

- `CoreActivityWithEvent.kt` / `CoreViewModelWithEvent.kt`：带事件通道的版本，本项目主用。
- `@UiIntentObserver(SomeIntent::class)` 标注在 ViewModel 的挂起函数上，框架负责分发。
- 发意图：`ChatUiIntent.XxxClick.emit()`（Compose 里直接用）。
- 改状态：`getOrNull<ChatUiState.Normal>()?.copy(...)?.setup()`。
- 抛一次性事件（Toast、开新页）：`AppViewEvent.Xxx.tryEmit()`，Activity 侧收。

**这里有一个反复出现的坑**：状态字段加在哪一层决定了界面认不认它。`ChatUiState.Normal` 上的字段（如 `pokeActionEditorVisible`）一改就弹；`ChatConversationState` 里的字段只在会话区生效。写错过一次，编译能过、界面永远不响应，只能靠 `grep` 同类字段的位置来定位。

## Koin

容器集中在 `app/src/main/java/me/kafuuneko/rpclient/RPClientApp.kt`。约定：

- 仓储、运行时、编码器一律 `single { }`。
- 构造参数用 `get()`；给默认值是为了让既有调用点不用改（加参数时**必须**带默认值，否则所有 `single` 声明一起炸）。
- 在 Compose 里临时取实例：`org.koin.core.context.GlobalContext.get().get<X>()`。**注意包名**，项目里另有 `libs.core.GlobalContext`，写错了报 unresolved。

## Room

数据库定义在 `app/src/main/java/me/kafuuneko/rpclient/libs/room/AppDatabase.kt`，当前 `version = 7`。

- 新增表 → `AutoMigration`。
- 新增列 → **手写 Migration**，配合 `addColumnIfMissing`（`libs/room/migration/`）。理由见 02 篇。
- 每加一版必须提交 `app/schemas/me.kafuuneko.rpclient.libs.room.AppDatabase/<版本>.json`，否则 `runMigrationsAndValidate` 无从校验。

## 文案

13 个 locale 目录，缺一个就会在对应语言下显示英文：

```
values  values-b+zh+Hans  values-b+zh+Hant  values-b+pt+BR  values-de
values-es  values-fr  values-id  values-it  values-ja  values-ko
values-ru  values-vi
```

新增字符串时**一次补齐 13 个**。注意两个具体的坑：目录名拼路径要写 `"values" + "-b+zh+Hans"`（`values` 自身要特判）；含单引号的译文必须整段用双引号包住，否则资源编译报 `Invalid unicode escape sequence`。

## 最后核对

随 `v1.13`（`dc52dfe0`）核对：`libs/core` 6 文件、`AppDatabase.kt:77 version = 7`、13 个 locale 目录齐全。
