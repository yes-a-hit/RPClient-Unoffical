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

## 这条线上的历史教训

剧本引擎是本地最能暴露「状态放错层」的地方。踩过的具体坑：

- 变量重置功能曾经「本来就实现」，缺的是**标记**——`resetOnRestart` 一加，才分清哪些该重来、哪些该保留。
- 快照字段加进 `data class` 不等于生效，三个写入点都要补，否则界面看着能存、读回来是空的。
- 导出最初只复制到剪贴板，`ExportLayout` 有 UI 但没入口。文件导出走 SAF（`CreateDocument`），因为目标可能是网盘，不能假设本地路径。

## 最后核对

随 `v1.13`（`dc52dfe0`）核对：`libs/script` 9 文件、`libs/story` 13 文件、`ScriptPlaySnapshot` 四字段齐全、`ScriptVariable.resetOnRestart` 默认 true。
