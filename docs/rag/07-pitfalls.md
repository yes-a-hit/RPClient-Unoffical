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

## 最后核对

随 `v1.13`（`dc52dfe0`）核对。新增条目请保持「症状 → 根因 → 处置」三段式，别只写一句「注意 XX」——那对下一次排查没有用。
