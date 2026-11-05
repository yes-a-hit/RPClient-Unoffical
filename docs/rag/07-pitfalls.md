# 07 · 坑与铁律

这一篇只收「已经真实付过代价」的条目。每条都写清症状、根因、处置，方便下次在同样症状出现时直接跳到答案。

## 一、改含中文的 Kotlin 文件

**症状**：文件变成非法 UTF-8，`read` 报 `invalid UTF-8 text`，Kotlin 报 `Syntax error`；`git diff` 出现几百行无意义变更。

**根因**：用 PowerShell `Set-Content` / `-replace` 改文件，中文的 UTF-8 多字节序列被按当前代码页重编码，第三字节被吞（顿号 `\xe3\x80\x81` 变成 `\xe3\x80?`）。

**铁律**：改 Kotlin 一律用 `edit` 工具或精确字符串替换；中文内容绝不走 PowerShell 内联。需要批处理时，用 `write` 写一个 `.py` 再用 python 跑，文件里显式 `io.open(..., encoding='utf-8', newline='')`。

## 二、状态重建丢字段

**症状**：操作看起来生效了，下一次界面刷新就消失；或者整页空白但零报错。

**根因**：`refreshUiState` / `loadNormalState` 重建整个状态对象，参数表里没列的字段回落到默认值。角色列表白屏也是同源——`CharacterListUiIntent.Init` 有定义、Activity 也发了，但 ViewModel 里没有对应的 `@UiIntentObserver`，状态永远停在 `None`，而 Layout 里 `None -> Unit` 是空实现。

**处置习惯**：加状态字段时，同步 `grep` 一遍这个字段名在状态重建处出现几次；加 Intent 时，同步确认有 observer。编译通过不代表接上了。

### 变体：拿快照写回，把期间的变化盖掉

比「漏传字段」更隐蔽。写法是

```kotlin
val uiState = getOrNull<ChatUiState.Normal>() ?: return
val x = withContext(Dispatchers.IO) { ... }   // 这里可能几百毫秒
uiState.copy(...).setup()                     // 用的是几百毫秒前的快照
```

`uiState` 是取值那一刻的快照。IO 期间任何别的意图把状态推进了（最典型：从系统文件选择器回来，
`onResume` 的刷新和文件回调几乎同时到），这一行写回时就把对方的成果整片盖回去。
日志里一切正常、提示也弹了，界面却没变。

**实例**：文档附件「提示附加成功、输入框上方什么都没有」。`onResume` 调 `loadNormalState`
时没有传 `attachedDocuments`，默认值落回 `emptyList()`，而它的 IO 比文件读取慢，
最后一步 `setup()` 把刚挂上的附件清空。

**处置**：
1. 需要在 await 之后改状态时，用 `updateState { }`（`CoreViewModel.kt`，内部走
   `MutableStateFlow.update` 的 CAS 循环），不要把 `uiState.copy()` 写回去；
2. 状态重建函数里，凡是「数据库里没有、只存在于输入区」的字段（附件、草稿、展开态），
   默认值一律取当前状态而不是空值，要清空必须显式传 `emptyList()`；
3. 重建完 `setup()` 之前，把这类现场字段从最新状态补回去——`onLoadOlderMessages`
   已经是正确范例（它在 IO 之后重新 `getOrNull()` 取最新值）。

## 三、加纯函数 + 用例 ≠ 接上线

**症状**：逻辑测试全绿，功能没反应。

**实例**：思维链隔离，函数和 8 条用例都写好了，但替换锚点写错（锚在 `.setup()` 后面，实际代码嵌在 `conversationState.copy(...)` 里），一个都没匹配上。测试永远抓不到这种问题。

**处置**：改完先 `grep` 关键字确认调用点在，再谈测试。

## 四、`mPendingPick` 与「图片无法处理」

见 03 篇。任何新增的图片入口都必须先调 `mImageCoordinator.choose(editing = false)`。

## 五、Room 自动迁移在「同版本不同结构」时炸

**症状**：升级后应用起不来。

**根因**：自动迁移拿声明的旧 schema 比对实际库；用户装过带新列的构建又回退过，同一个版本号下出现两种结构，迁移直接判不符并拒绝打开。

**处置**：从 v5→6 起改手写迁移 + `addColumnIfMissing`，重复执行不炸。

## 六、`room_master_table` 不能进备份

见 02 篇。五处都要排，漏一处复发。

## 七、上游整包合并

**结论：不做。** 试过两次，冲突从 41 涨到 81 文件；把冲突全取 ours 之后，编译错误反而从 20 文件扩散到 40 文件——因为上游的重构文件**不在冲突列表里**，被静默取了上游版，而我们的调用方还是旧接口。

**现在的策略**：用 `git merge -s ours upstream/master` 只确立历史基点（`behind` 归零），代码零改动；需要哪个功能就按文件域单点拿，且先确认它改的文件在本仓库存在、没被本地改写过。

## 八、`git add -A` 在切分支后很危险

切到上游分支后 `.gitignore` 是上游版本，本地的 `/dist`、`/上游` 规则不在，`git add -A` 一次拖进 149 个文件（截图、备份、APK）。**一律逐个点名 `git add`。**

## 九、`git reset --hard` 会带走未跟踪文件

回滚时把 `上游/RPClient-2026.3.1.apk` 一起删了。reset 前先把未跟踪的重要文件备份到 `%TEMP%`。

## 十、Gradle 偶发 `exit=-1`

日志停在 `preDebug` / `compileDebugKotlin` 且没有 `BUILD` 行 = 进程被外部终止（通常是机器在跑别的重活）。直接重跑，不要当编译错误修。

## 十一、androidTest 会积债

`testDebugUnitTest` 不编译 `app/src/androidTest/`，所以主构建永远绿。类型改过一次（`draftInputs()` 返回 `List<MessageImageInput>`、`MessageImageRuntime` 构造参数变成 `mSettings + mGifSettings`）就积了一批编译错，很久之后才发现。

**纪律**：改了被 androidTest 引用的签名，顺手跑一次 `compileDebugAndroidTestKotlin`。

## 十二、`PowerShell` 里 `python -c` 的 `\d`

`python -c "...re.compile('\d+')..."` 在 PowerShell 里 `\d` 会被当命令解析，报 `无法将"\d+"项识别为 cmdlet`。写 `.py` 文件，别内联。

## 十三、管道关闭导致的假失败

`... | Select-Object -First N` 会提前关管道，上游（如 gradle）收到 broken pipe，`$LASTEXITCODE=1` 被误判成失败。要么重定向到文件后再读，要么不要在长任务后面接 `Select-Object -First`。

## 十四、`TextButtonDefaults` 不存在

Material3 里是 `ButtonDefaults.textButtonColors(...)`。项目里用过 `Icons.Rounded.*` 的大量图标，那些来自 `material-icons-extended`，与这个不是一回事。

## 十五、资源字符串里的单引号

**症状**：`mergeDebugResources FAILED`，报 `Failed to flatten XML for resource 'xxx' with error: Invalid unicode escape sequence in string`。整包构建停在这一步，Kotlin 一行都没编译。

**根因**：aapt 把单引号当转义符的首字符。意大利语 `un'immagine`、法语 `l'appui` 这类天然带撇号的译文必然踩。

**处置**：整段用 ASCII 双引号包住 —— `<string name="x">"Questa chat ha un'immagine propria"</string>`。批量插入 13 locale 时，脚本里对含 `'` 的值统一套一层双引号，别等编译报错再回头找。

顺带一条：**值的正文里不要出现 ASCII 双引号**，那会破坏 XML 属性边界，落盘成嵌套未转义的引号，aapt 有时放行但语义不可控。

## 十六、`internal fun` 不能写在 `private companion object` 里

**症状**：`Unresolved reference 'xxx'`，明明就在同一个文件。

**根因**：`private companion object` 把内部可见性压到 private，`internal fun` 在里面退化成不可见，文件外自然看不到。

**处置**：要暴露给测试的辅助函数，companion object 写成普通的 `companion object`（可见性跟着类走），函数单独标 `internal`。

## 十七、拆文件时 `private` 是文件级可见

**症状**：把 `XxxLayout.kt` 里的一批 `@Composable` 搬到 `XxxViews.kt` 之后编译报
`Cannot access 'fun AvatarPreview(...)': it is private in file.`——而这个函数明明还在同一个包里。

**根因**：Kotlin 的 `private` 在**顶层声明**上是**文件级**，不是类级。同 package 的不同文件互相看不见 `private` 顶层声明。

**处置**：拆出去的文件里，凡是原文件也要用到的顶层声明，把 `private` 改成 `internal`（模块级）。`tools/split_kt.py` 的 `relax_visibility()` 就是这么做的。

配套两条：

- 顶层**常量**（`private const val` / `private val`）同样受这条约束，最容易漏——`StoryEditorLayout` 那次漏了 7 个 `REASONING_SCROLL_BOTTOM_TOLERANCE_PX` 之类的常量，报了好几条 `Cannot access 'val X'`。
- 反过来，**只被本文件用**的辅助函数留在原文件反而更好，改成 `internal` 会把模块内所有同名符号都暴露出去。

## 十八、包内写全限定名会解析成相对路径

**症状**：`RPClientApp.kt`（package 是 `me.kafuuneko.rpclient`）里写
`singleOf(::me.kafuuneko.rpclient.feature.chat.logic.ChatRegexApplier)`
报 `Unresolved reference 'me'` / `Unresolved reference 'kafuuneko'`。

**根因**：Kotlin 解析不带前导 `::` 或 `import` 的全限定名时，会先当相对包名，从当前 package 往下找，于是拼成了 `me.kafuuneko.rpclient.me.kafuuneko…`。

**处置**：老老实实加 `import`，然后写短名。Koin 的 `singleOf(::Xxx)` 尤其要注意——它内部是构造函数引用，不能被包名限定。

## 十九、`FileRepository.saveFile(uri)` 返回的是 uuid 字符串

**症状**：`mFileRepository.saveFile(uri)?.uuid` 报 `Cannot infer type for this parameter` / `Unresolved reference 'uuid'`。

**根因**：`saveFile(Uri): String` 直接返回 uuid，不是 `FileEntity` 也不是可空对象，没有 `.uuid` 可点。

**处置**：要 uuid 就直接用返回值；要处理失败包 `runCatching { withContext(Dispatchers.IO) { mFileRepository.saveFile(uri) } }.getOrNull()`。`ImageRequestException` 之类的异常不在函数签名里，`runCatching` 是必须的，否则选到一张坏图就整个 ViewModel 崩。

## 二十、`bind` 出来的文案键和已有键撞名

**症状**：给「全员」场景加副标题，写成 `group_chat_scene_all` —— 而这键早就存在，值是「全员」本身（按钮标题）。编译不报错，界面上副标题显示成重复的「全员」。

**根因**：场景相关文案已有 `group_chat_scene_all`（标题）、`group_chat_scene_all_present`（在场说明）、`group_chat_scene_empty_hint`（空状态）。新增一条时凭语义猜名字，很容易撞上语义相近的老键。

**处置**：加新键之前先 `Select-String -Pattern 'group_chat_scene_'` 把同前缀的键列出来，再决定名字（这次正解是 `group_chat_scene_all_subtitle`）。批量插 13 个 locale 的脚本要对已存在的键打 `SKIP` 而不是覆盖。

## 二十一、新写的 UI 文案直接写中文

**症状**：功能在中文下完全正常，切到其他语言时那几行还是中文；更糟的是**没人会发现**——因为开发机默认就是中文。

**根因**：`Text("场景「" + name + "」")` 这种写法编译、运行、看起来都没问题。项目里 13 个 locale 是硬约束，但约束只在「加字符串资源」时生效，直接写字面量绕过了整个机制。

**处置**：

1. 写 `Text(...)` 之前先问一句「这句会不会被用户看到」，会就立刻 `stringResource`。
2. 拼字符串（`"场景「" + name + "」"`）尤其危险，先把模板定成 `%1$s` 再填。
3. 复查手段：`grep -n 'Text(\"' app/src/main/java/me/kafuuneko/rpclient/feature` ——命中的每一处都该有个理由（纯符号、调试信息）。
4. 这一轮的实际案例：群聊场景提示带的四行（场景名 / 在场 / 世界书 / 悄悄走）全都是硬编码，从 v1.20 一直漏到 v1.24 才被发现，中间跨了四个版本没人注意。

## 二十二、中文写进 `.ps1` 就执行，脚本自己先乱码

**症状**：`powershell.exe -File xxx.ps1` 报一堆看不懂的解析错——`Unexpected token '杩欎竴鐗堬細'`、`The string is missing the terminator`、`Missing closing ')'`。脚本在编辑器里看着完全正常。

**根因**：`write` 工具写出来的是 **UTF-8 无 BOM**，而 `powershell.exe -File` 对无 BOM 文件按系统 ANSI（这里 GBK）解码。中文变乱码之后引号和括号的配对关系跟着崩掉，报错位置离真正的错处很远。

**处置**：

1. `.ps1` **正文只写 ASCII**。要传的中文（发布说明、提交信息）单独放一个 UTF-8 的 `.json` / `.txt`，脚本里用 `[System.IO.File]::ReadAllBytes($path)` 读字节直接当 `-Body`，别让 PowerShell 去解析那段中文。
2. 提交信息同理：写 `%TEMP%\msg.txt` 再 `git commit -F`，别用 here-string。
3. 同一根因的另外两种表现：`Get-Content` 读 Kotlin 中文变乱码（改用 `read` 工具）、PowerShell 内联 `python -c` 里带中文或 `\d`（写 `.py` 文件再跑）。
4. 判断标准：**这段文字要不要被另一个进程按自己的编码规则解析**。要，就别让它进 `.ps1`。

## 最后核对

随 `v1.25` 核对（推送与发布链路轮）。新增条目请保持「症状 → 根因 → 处置」三段式，别只写一句「注意 XX」——那对下一次排查没有用。

历史变更见 [CHANGELOG](../CHANGELOG.md)。
