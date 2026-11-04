# 领域文档

工程技能在勘察代码库之前，如何消费本仓库的领域文档。

## 本仓库上下文

- 路径：`E:\dsh-sp\rpclient`
- 技术栈：Android 原生 Kotlin + Gradle（AGP，Kotlin DSL）。`app/src/main/java/me/kafuuneko/rpclient/feature/*` 按功能分层。`origin` 是 yes-a-hit/RPClient-Unoffical，`upstream` 是 KafuuNeko/RPClient。
- 布局：单上下文（single-context），根目录一个 `GLOSSARY.md` + `docs/adr/`
- 常用验证：

```powershell
.\gradlew.bat :app:assembleDebug
```
需要本机 JDK；`dist/` 放构建产物。

从上游 fork 维护，合并上游时以 `upstream` 为准，本地改动留在 `origin`。

## 勘察前先读这些

- 根目录的 **`GLOSSARY.md`**；如果是 **`GLOSSARY-MAP.md`**，它指向每个上下文各一份 `GLOSSARY.md`，读与当前任务相关的那几份。
- **`docs/adr/`**：读涉及即将动工区域的 ADR。多上下文仓库里还要看 `src/<context>/docs/adr/`。

这些文件不存在时**静默跳过**，不要提示缺失、不要建议预先创建。`domain-modeling` 技能（由 `grill-with-docs` 与 `improve-codebase-architecture` 触达）会在术语或决策真正定下来时按需生成。

## 文件结构

单上下文（绝大多数仓库）：

```
/
├── GLOSSARY.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文（根目录存在 `GLOSSARY-MAP.md`）：

```
/
├── GLOSSARY-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── GLOSSARY.md
    │   └── docs/adr/                  ← 上下文级决策
    └── billing/
        ├── GLOSSARY.md
        └── docs/adr/
```

## 用术语表里的词

输出里出现领域概念时（issue 标题、重构提案、假设、测试名），用 `GLOSSARY.md` 定义的术语，不要漂移到术语表明确回避的同义词。

需要的概念不在术语表里，那是个信号：要么在造项目不用的词（回头想），要么确实有缺口（记下来交给 `domain-modeling`）。

## 撞上 ADR 要明说

输出与既有 ADR 冲突时，明确指出来，不要悄悄覆盖：

> _与 ADR-0007（event-sourced orders）冲突，但值得重开，因为……_
