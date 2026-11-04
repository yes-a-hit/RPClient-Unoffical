# 04 · 剧本引擎

一句话：**剧本是纯数据（JSON），引擎是纯函数；变量有类型，检查点是快照，跑偏了靠规则止损。**

## 三种入口，一套数据

| 入口 | feature 包 | 用途 |
| --- | --- | --- |
| 剧本编辑器 | `feature/scripteditor/` | 写剧本、导入导出、调试 |
| 剧本试玩 | `feature/scriptplay/` | 在编辑器里跑一遍，看分支对不对 |
| 冒险模式 | `feature/scriptadventure/` + `libs/story/` | 面向玩的正式形态 |

数据模型在 `app/src/main/java/me/kafuuneko/rpclient/libs/script/ScriptPackModels.kt`。

## 剧本结构

```
ScriptPack
├─ variables: List<ScriptVariable>     变量声明
├─ scenes: List<ScriptScene>           场景图
└─ showDashboard: Boolean              是否显示变量面板

ScriptScene
├─ id / name / text                    正文（可含变量插值）
├─ choices: List<ScriptChoice>          玩家选项 → 目标场景
├─ cast / castRules                     本场出场角色
├─ effects                              进入或选择时施加的变量变更
├─ dashboardItems / failText / timeoutSceneId
└─ 各种出边（gotoSceneId、timeoutSceneId…）
```

## 变量有四种 kind

`ScriptVariable.kind` 取值：

| kind | 用途 | 备注 |
| --- | --- | --- |
| `Number` | 数值 | 有 `min` / `max`，写入时 `coerceIn` |
| `Switch` | 开关 | 0/1，用于「左门状态」这类 |
| `Text` | 文本 | 可 `visible: false` 当隐藏状态用 |
| `Note` | 只读提示 | 给玩家的线索条 |

`resetOnRestart` 默认 `true`。重开一局时它决定这个变量是从 `initial` 重来，还是沿用上一轮的值——默认必须是 true，否则现有剧本全卡在上一轮的状态里。

## 效果与条件

`ScriptEffect` 支持的写入：

- `delta`：数值加减（超出范围自动夹住）
- `setText` / `appendText`：文本整段覆盖 / 追加（重复内容去重）
- switch 取反

`ScriptComparison` 覆盖 `>= > == != < <=`。条件里的 `keyword` 支持 `|` 分隔的备选，命中任一即真。

## 检查点

`ScriptPlaySnapshot`（`libs/script/ScriptPlaySession.kt`）记四样东西：

```kotlin
val variables: Map<String, Int>?
val textVariables: Map<String, String>?
val castOverride: List<String>?
val firedEvents: Set<String>?
```

四样全带问号是刻意的：老存档里没有这些键，读出来是 null，恢复时按「这一项没存过」处理，而不是当成空值把现场清掉。

**三个写入点必须同时改**（漏一个就出现「切场景回来状态没变」）：

1. `enterScene` —— 进场景时记快照
2. `onRestartPlaythrough` —— 重开时记
3. `onRestoreCheckpoint` —— 恢复时把四个字段全读回来

## 变量面板与失败出口

- `showDashboard = true` 时才渲染 `dashboardItems`，用来显示电量、夜数这类玩家要盯着的东西。
- `failText` + `timeoutSceneId` 组成本场超时/失败时的出口。`maxRounds` 到顶后返回 `ScriptPlayOutcome.Failed(reason = failText, gotoSceneId = timeoutSceneId)`，而不是继续傻跑。

## 越界拦截

失败条件管的是「玩家输了」，`ScriptGuardRule` 管的是「模型写飞了」。两者不是一回事：前者是规则内的结果，后者是规则外的内容。

判定的对象是**模型这一轮的回复正文**，时机在落库之前：

1. `ScriptPlayViewModel.onSend()` 拿到 `visibleReply`（已剥掉 `<<<OPTIONS` 块）
2. `ScriptPlayGuard.evaluate(pack.globalGuardRules + scene.guardRules, visibleReply)`
3. 命中 `Hit.Soft` → 那段回复**不进消息列表**，只追加一条 `SYSTEM` 提示，`roundCount` 不累加，直接 `return`（不跑判定、不跑场景事件、不自动推进）
4. 命中 `Hit.Hard` → 同上加提示，再按 `rule.failSceneId` 转惩罚场景或直接收官

为什么要卡在落库之前：一旦进了消息列表就是既定事实，后面的失败条件、通关条件、以及下一轮的历史全都在拿一段不该发生的故事做输入。事后判失败只是「承认这段真的发生了」，玩家看到的还是那段离谱内容。

规则字段（`ScriptGuardRule`）：

| 字段 | 作用 |
| --- | --- |
| `pattern` | `regex = false` 时用 `\|` 分隔多个说法；为真时整段当正则 |
| `description` | 命中后给玩家看的一句话；留空退化成「这一轮的内容超出了本场设定」 |
| `hardFail` | 命中即判本场失败，而不是只作废这一轮 |
| `failSceneId` | `hardFail` 时的去处；留空直接收官 |
| `retryHint` | 跟着作废提示回灌给下一轮。不写的话模型多半原样再写一遍 |

正则写坏按「不命中」处理——一条规则写错不该让整局玩不下去，也不该反过来把所有回复都判成越界。

提示词里也会带一份禁区清单（`ScriptPlayGuard.promptSection()`，渲染成【禁止描写】段）。拦截是兜底，能不让模型写出来最好：写出来再作废，玩家会看到一句话然后它消失。

写法示例（`samples/demo-three-nights.json` 的 `jumpscare_foxy`）：禁止「打死/崩了/杀掉」，命中只作废该轮；禁止「诈尸/又站了起来」，命中直接判失败。

**只有配置了规则才生效**，没有默认兜底——自动猜边界会把正常剧情也拦掉。

## 这条线上的历史教训

剧本引擎是本地最能暴露「状态放错层」的地方。踩过的具体坑：

- 变量重置功能曾经「本来就实现」，缺的是**标记**——`resetOnRestart` 一加，才分清哪些该重来、哪些该保留。
- 快照字段加进 `data class` 不等于生效，三个写入点都要补，否则界面看着能存、读回来是空的。
- 导出最初只复制到剪贴板，`ExportLayout` 有 UI 但没入口。文件导出走 SAF（`CreateDocument`），因为目标可能是网盘，不能假设本地路径。

## 最后核对

随 `v1.24` 核对：`libs/script` 10 文件、`libs/story` 13 文件、`ScriptPlaySnapshot` 四字段齐全、`ScriptVariable.resetOnRestart` 默认 true、`ScriptGuardRule` 六个字段 + `ScriptPlayGuardTest` 13 条用例。

历史变更见 [CHANGELOG](../CHANGELOG.md)。
