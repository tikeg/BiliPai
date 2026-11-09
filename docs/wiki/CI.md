# 构建与 CI 约定

> 适用文件：`.github/workflows/Build.yml`、`.github/workflows/QualityChecks.yml`、`scripts/ci_quality.sh`
> 相关：`docs/wiki/VERSIONING.md`（版本号/标签）、`docs/wiki/LAYERS.md`（分层规则）

## 1. 两条构建通道

`Build.yml` 同时服务两种用途，靠 `workflow_dispatch` 输入区分：

| 通道 | 触发 | 输入 | 签名 | google-services | 产物用途 |
|---|---|---|---|---|---|
| 发布通道 | push / tag，或 `workflow_dispatch` | `build_type=release`（默认） | `officialRelease` keystore（4 个 secret） | 需要 `FCM` secret | 对外发布 |
| dev 通道 | `workflow_dispatch` | `build_type=dev` | debug key | 不需要 | 日常验证 |
| 验证通道 | `workflow_dispatch` | `build_type=release` + `skip_fcm=true` | debug key（回落） | 不需要 | **仅验证**，禁止发布 |

触发示例：

```bash
# dev 验证包
{"ref":"<branch>","inputs":{"build_type":"dev"}}

# release 验证包（无 FCM / 无 keystore 时使用）
{"ref":"<branch>","inputs":{"build_type":"release","skip_fcm":"true"}}
```

## 2. 必需的仓库 secrets

| Secret | 用途 | 缺失时 |
|---|---|---|
| `FCM` | base64 的 `app/google-services.json` | `build` job 被跳过，只跑质量门禁 |
| `RELEASE_KEYSTORE_BASE64` | base64 的 keystore | 发布通道 `Configure release signing` 直接失败 |
| `RELEASE_STORE_PASSWORD` | keystore 口令 | 同上 |
| `RELEASE_KEY_ALIAS` | key 别名 | 同上 |
| `RELEASE_KEY_PASSWORD` | key 口令 | 同上 |

补齐 5 个 secret 后，push 即恢复「正式签名 + FCM」的发布链路；`skip_fcm` 通道保留作应急验证。
当前仓库这 5 个 secret 均为空（`GET /actions/secrets` 的 `total_count` 为 0）。

## 3. 通道护栏

- `skip_fcm=true` 且 ref 是 `refs/tags/*` 时，`Guard verification-only channel` 步骤直接失败，
  避免把 debug 签名的包当发布产物。
- `skip_fcm=true` 的产物 artifact 名为 `BiliPai-skipfcm`，与正式 `BiliPai` 区分。
- 只传 `build_type=release`、不传 `skip_fcm` 时，行为与历史一致：需要 FCM + keystore。

## 4. 内存与 R8

GitHub `ubuntu-latest` 为 4 vCPU / 16 GB。历史经验：

| 现象 | 原因 | 处理 |
|---|---|---|
| `R8: java.lang.OutOfMemoryError: GC overhead limit exceeded` | `gradle.properties` 的守护进程堆只有 4 GB | 构建前把 `org.gradle.jvmargs` 抬到 11 GB |
| `:app:compileDevKotlin` OOM | 为了给 R8 腾地方把 Kotlin 守护压到 4 GB | 改 `-Pkotlin.compiler.execution.strategy=in-process`，编译与 R8 共用一个堆 |

对应步骤：`Tune Gradle heap for R8` + `Build APK` 的 `COMMON_ARGS`。
不要把这两条改动挪回 `gradle.properties`：那会影响开发机（本仓库文档记录过本机 4 GB 堆的历史问题）。

## 5. 产物与命名

- 交付目录 `build/release-evidence/`，含 `BiliPai-<versionName>[-dev].apk`、`build-metadata.json`、`checksums.txt`。
- `build-metadata.json` 记录 `appId`、`versionName`、`versionCode`、`gitCommitSha`、`workflowRunId`。
- artifact 保留 7 天；`crashlytics-mapping-*` 保留 30 天，且不进公开产物。
- tag + release 通道才会生成 provenance attestation 并上传 GitHub Release。

## 6. 质量门禁

`quality-guards` 复用 `QualityChecks.yml`，三个 job：

| job | 内容 |
|---|---|
| Correctness | `scripts/ci_quality.sh` 的 correctness 段 |
| PerfGuard | `:ci-lint:test` 的 Performance/ModelStability 测试 + `:app:lintDebug -Pbili.ciChecks=performance` |
| StylePolicy | `StyleDetectorTest` + `:app:lintDebug -Pbili.ciChecks=style` |

注意：`lintDebug` 会编译主源码 **和** 单元测试源码，所以单测编译错误也会让质量门禁变红
（main 曾因此红过，见 PR #1）。

`BiliPaiLayerDependency`（`ci-lint` 模块）检查模块/包依赖方向，规则见 `docs/wiki/LAYERS.md`。
它目前没有豁免：`app/lint.xml` 中该 issue 不应再出现 `<ignore>`。

## 7. 本机无法编译

设备为 aarch64，而 `/opt/android-sdk/build-tools/{36,37}/aapt2` 是 x86_64 ELF，
`AAPT2 daemon startup failed`。含资源处理的 Gradle 任务（编译/打包）只能在 CI 或 arm64 环境执行；
纯 JVM 任务（`:ci-lint:test`）可以本地跑。
