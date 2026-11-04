# 06 · 构建与发布

一句话：**版本号只有一个来源 `gradle.properties` 的 `uv.version`；出包必须走 `tools/publish-apk.ps1`；dist 是发布产物的唯一去处。**

## 版本号

`app/src/main/java/me/kafuuneko/rpclient/libs/AppModel.kt` 里的 `UV_VERSION` 读 `BuildConfig.UV_VERSION`，源码里**没有字面量**。

链式关系（`app/build.gradle.kts`）：

```
gradle.properties: uv.version=1.24
  ├─ uvNumeric = 1.24 去掉非数字 → 124
  ├─ versionCode = 20260301 + 124 = 20260425
  ├─ versionName = "2026.3.1-uv1.24-dev"
  └─ buildConfigField UV_VERSION = "1.24"
```

改版本只改 `uv.version` 一处。曾经有人（我）在 `AppModel.kt` 写死 `const val UV_VERSION = "0.89"`，结果发布脚本按字面量抓、抓到就再也抓不到，静默失败了十几个版本。

## applicationId 与签名

- `applicationId = "me.kafuuneko.rpclient.uv"`（debug 变体加 `.dev`），与官方版 `me.kafuuneko.rpclient` 互不覆盖，可同时安装。
- debug 签名固定用仓库内的 `app/uv-debug.jks`（storePassword / keyAlias / keyPassword 全是 `uvdebug`）。**不要**用默认 `~/.android/debug.keystore`——那台机器的签名一变，装机就报 `INSTALL_FAILED_UPDATE_INCOMPATIBLE`，用户数据连带要清。

## 出包

```powershell
# 编译 + 测试
.\gradlew.bat --offline assembleDebug testDebugUnitTest

# 发布到 dist（自动带版本号命名）
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\tools\publish-apk.ps1
```

`& .\tools\publish-apk.ps1` 会被执行策略拦（`无法加载文件…禁止运行脚本`），必须走 `powershell.exe -File`。

产物：`dist/rpclient-2026.3.1-uv-dev-debug-v<版本>.apk`。`dist/` 整个被 `.gitignore` 排除，不进提交。

## 读 Gradle 日志（这条一定会用到）

```powershell
cmd /c "gradlew.bat --offline compileDebugKotlin > %TEMP%\b.log 2>&1"
```

两个必须知道的点：

1. `PowerShell` 的 `*>` 重定向会按控制台宽度**截断** `e:` 行，只看到半句错误。用 `cmd /c ... > file 2>&1`。
2. 日志是 **UTF-16 LE 带 BOM**，Python 默认 `utf-8` 读会 `UnicodeDecodeError: 'utf-8' codec can't decode byte 0xff`。读法：

```python
raw = open(path, 'rb').read()
try: text = raw.decode('utf-8')
except: text = raw.decode('utf-16', 'replace')
```

打印前设 `$env:PYTHONIOENCODING='utf-8'`，否则中文报 `UnicodeEncodeError: 'gbk' codec`。

## 测试

```powershell
.\gradlew.bat --offline testDebugUnitTest
python tools\count_tests.py     # 解析 test-results/*.xml，输出 tests=N failures=M
```

`count_tests.py` 存在的原因很实际：Gradle 的 `BUILD SUCCESSFUL` 不告状，测试数却可能因为编译跳过而悄悄变少。

## 一条纪律

**Gradle 与模拟器不要同时跑。** 这条不是洁癖：这台机器是 i9-14900HX + AEHD，两次并行把宿主直接拖死（`Kernel-Power 41`，`BugcheckCode=0`，无 dump，是硬 hang 不是蓝屏）。`tools/run-emulator.ps1` 里已经加了硬拦截——检测到 java 进程直接 `throw`，除非显式传 `-AllowJava`。

## 最后核对

随 `v1.24` 核对：`uv.version=1.24`、`applicationId = me.kafuuneko.rpclient.uv`、`app/uv-debug.jks` 在库、`tools/publish-apk.ps1` 与 `tools/count_tests.py`、`tools/split_kt.py`、`tools/list_toplevel.py` 均在。

历史变更见 [CHANGELOG](../CHANGELOG.md)。
