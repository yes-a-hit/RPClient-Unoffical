# 05 · 助手与图库

一句话：**助手是一组受限工具 + 一段系统提示；图库是应用内的图片池，分「能发」和「是角色长什么样」两类。**

## 助手

代码在 `app/src/main/java/me/kafuuneko/rpclient/libs/assistant/`，只有 5 个文件：

| 文件 | 职责 |
| --- | --- |
| `AssistantToolCatalog.kt` | 工具清单与参数 schema（模型能看见什么） |
| `AssistantToolExecutor.kt` | 每个工具的实际执行与返回文案 |
| `AssistantPromptBuilder.kt` | 系统提示 |
| `AssistantStore.kt` | 会话持久化 |
| `AssistantSessionStore.kt` | 单次会话的消息缓冲 |

### 工具与它们的边界

- `create_character` —— **同名直接拒绝，不合并**。早期是「新值非空就覆盖旧字段」，模型一句话就能改写用户精心写好的角色卡。现在返回「已存在同名角色卡「X」（id=N），本次未创建」，要改就走 `update_character`。
- `update_character` —— 支持 `profile` 字段级合并，只改传了的项。
- `create_worldbook` —— 先按名字查现存，同名复用 id 不新建；支持 `entries` 一次写入。**这是「造一大堆重复空书」的根因修复**：旧实现每次都 insert 一条新书。
- `add_worldbook_entry` —— 写入前查同名条目，重复就返回「已经有同名条目」。
- `list_worldbooks` —— 条目数 0 的标注「（空书，没有条目）」，并在末尾提示「新建前先在这里找同名书」。

### 扩展档案要按新模板填

角色卡的自定义栏目（年龄、性别、职业、性取向、关系状态、是否有恋爱史、恋爱史、是否有性经历、性经历、边界、喜欢、不喜欢、补充）存在 `Character.extensionsJson`，命名空间 `rpclient_profile`，由 `libs/character/CharacterProfileCodec.kt` 读写。

助手建卡/改卡时通过 `profile` 参数写这套字段，`parseProfile` 只收非空项，`mergeProfile` 补丁式合并。**不要**往 `description` 里堆这些信息——它们有独立展示位置，混进描述会让角色卡两个地方对不上。

布尔字段接受 `true`/`false` 与 `"是"`/`"否"`、`"有"`/`"没有"`；「未设定」返回 null，保留原值而不是清空。

## 图库

代码在 `app/src/main/java/me/kafuuneko/rpclient/libs/gallery/`，每张图一个 `<id>.json` 存在 `filesDir/gallery/`。

字段（`GalleryImage`）里有两组容易混的：

```kotlin
val isPublic: Boolean = false        // 能不能由助手挑出来发
val isAppearance: Boolean = false    // 是不是「角色长什么样」
val appearanceNote: String = ""      // 上面那类图的一句话说明
val album: String = ""               // 相册分组，纯界面用
```

**`isAppearance` 和 `isPublic` 是彻底分开的两件事**。外观图是「角色的样子」，不是「模型可以拿去发出去的东西」——混在一起会让角色把自己的立绘当表情包发给用户。

### 外观参考图怎么进提示词

`GalleryStore.appearanceImages(characterName, limit = 3)` 取图，`MessageImageRuntime.referencesOf(uuid)` 转成 `LLMImageReference`，塞进 `PromptBuildContext.appearanceImages`，由 `ChatPromptBuilder.buildAppearancePrompt()` 生成说明。

关键不只是给图，而是**说清楚这图是谁**：

```
以下是 {角色名} 本人的样子：
- 第 1 张：立绘
- 第 2 张：被摸头时的表情
这些形象就是你。用户发来同一形象的图片、表情包或梗图时，
画里的那个就是你本人，不要当成别的角色，也不要认成别人的同类。
```

只给图不说这句，模型会把立绘当成「用户发来的参考图」。

默认三张封顶——视觉 token 不便宜。

### 聊天里挑图

`ui/widgets/GalleryPickerDialog.kt`：相册 `FilterChip` + 四列缩略图 + 选中遮罩。挑完走 `ChatUiIntent.AttachGalleryImages` → `MessageImageCoordinator.attachExisting`。

`attachExisting` 内部只把 uuid 放进草稿并 `load`，不复制文件；复制发生在真正落库时（见 03 篇「图片管线」）。

## 一个已经踩平的坑

GIF 早期「只能看首帧」。根因不在抽帧代码，而在入库时被压成了静态 PNG——`encodeCompressed` 解码 `Bitmap` 只有首帧，后续 `outMimeType` 是 `image/png`，`expandAnimated` 从来没被调用过。修法是「动图原字节直发」。这条值得记：**功能写好了不代表它被执行过**。

## 最后核对

随 `v1.13`（`dc52dfe0`）核对：`libs/assistant` 5 文件、`libs/gallery` 2 文件、`GalleryImage` 十一个字段、外观图上限 3。
