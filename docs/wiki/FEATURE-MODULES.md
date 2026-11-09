# Feature 模块拆分路线

> 状态：**阶段 2 进行中**，已抽出 `:feature-agreement`、`:feature-privacy`、`:feature-common`。
> 相关：`docs/wiki/LAYERS.md`（依赖方向）、`docs/wiki/CI.md`（构建通道）

## 1. 拆分原则

1. **依赖单向**：`feature-*` 只能依赖 `design-system`、`core-data`、`network-core`、`settings-core` 这类库模块；
   禁止依赖 `:app`、`navigation/`、`navigation3/` 或其它 `feature-*`。
2. **入口最小**：模块对外只暴露 Compose 入口（`@Composable fun XxxScreen()`）与必要的策略函数；
   其余实现保持 `internal`（注意：`internal` 是**模块级**可见性，跨模块访问会编译失败，拆分时要逐个判定）。
3. **资源随模块走**：字符串等资源迁入模块自己的 `res/`，`R` 自动指向模块 namespace；
   确实被多处共用的资源（如 `common_close`）在模块内保留副本。
4. **一次一个模块，独立提交**：每个模块单独一个 commit，CI 绿了再拆下一个，方便单点回滚。

## 2. 首个模块：`:feature-agreement`

| 项 | 值 |
|---|---|
| 源码 | `feature-agreement/src/main/java/app/bilipai/feature/agreement/`（3 文件 / 277 行） |
| 依赖 | `:design-system`、`androidx.activity:activity-compose` |
| 对外 API | `UserAgreementGate`、`UserAgreementBody`、`UserAgreementReviewDialog`、`UserAgreementStore`、`UserAgreementText`（均为 public） |
| 资源 | 25 条 `user_agreement_*` + 1 条 `common_close` 副本（`values` / `values-en`） |
| :app 调用点 | `MainActivity`、`LoginScreen`、`OfficialQrAuthorizationContent`、`SettingsSections`（全限定名调用，无需改代码） |
| `internal` 冲突 | 无（该目录原本没有 `internal` 声明） |

选它的理由：依赖面最窄（只有 design-system）、无 `internal` 跨模块问题、资源可整体搬走，适合验证模块化机制本身。

## 3. 候选清单（按耦合度排序）

统计口径：文件数 / 代码行数 / `internal` 声明数 / 对其它 `feature` 的依赖数。

| feature | 文件 | 行数 | internal | 依赖其它 feature | 备注 |
|---|---|---|---|---|---|
| agreement | 3 | 277 | 0 | 0 | **已抽出** |
| category | 1 | 427 | 0 | home（ElegantVideoCard） | 需先拆 home |
| matchinfo | 2 | 335 | 2 | video | 需先拆 video |
| story | 4 | 773 | 15 | video | 同上 |
| privacy | 1 | 56 | 2 | 0 | **已抽出**（`shouldRequirePrivacyAuthentication` 提为 public） |
| livesite | 4 | 526 | 7 | 0 | 外部 0 引用，先确认是否死代码 |
| widget | 1 | 198 | 0 | audio、video | 依赖 :app 的 MainActivity |
| common | 2 | 55 | 2 | 0 | **已抽出**（`ListLoadError`、`resolveIndexedVideoLazyKey` 均提为 public） |
| download | 32 | 5802 | 86 | core/store | 中 |
| search | 27 | 10533 | 152 | home、video | 大 |
| settings | 152 | 40628 | 435 | 多处 | 最大，最后拆 |
| home / video | 187 / 490 | 50820 / 152142 | 1156 / 2264 | 互相依赖 | 必须先拆公共件 |

## 4. 建议顺序

1. `:feature-agreement` ✅
2. `:feature-privacy` ✅（56 行、零外部依赖；入口函数提为 public，单测仍留在 :app）
3. `:feature-common` ✅（55 行、只依赖 design-system；两个入口提为 public）
4. `:core-ui-*` 之外的公共件下沉（`core/ui/transition` 等）
5. 中等 feature：`download` → `message` → `live` → `search`
6. 最后 `home` / `video` / `settings`（内部耦合最深，需要先拆出共享 UI 与 policy）

## 5. 拆分检查表（每个模块都执行）

- [ ] `git mv` 源码目录，包名不变（package 与目录保持一致）
- [ ] 建 `feature-<name>/build.gradle.kts`（模板见 `brand-motion`）
- [ ] `settings.gradle.kts` 注册模块，`:app` 加 `implementation(project(":feature-<name>"))`
- [ ] 资源迁到模块 `res/`，`R` 指向模块 namespace
- [ ] `internal` → `public` 只放宽真正被外部使用的符号
- [ ] 确认没有 `import app.bilipai.navigation*`、`import app.bilipai.feature.<其它>`
- [ ] CI 三门禁全绿后再拆下一个
