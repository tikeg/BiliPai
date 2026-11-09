# 分层与所有权规则（LAYERS）

> 目的：消除"同一概念两份实现"与"新代码不知道放哪"的问题。本文件是约定的唯一出处；与本文件冲突的代码视为待迁移。

## 1. 模块职责

| 层 | 模块 | 拥有什么 | 不得拥有 |
|---|---|---|---|
| 模型 | `core-model` | 跨模块共享的 data class / enum | 业务逻辑、IO |
| 数据 | `core-data`、`data-danmaku`、`data-live`、`data-message` | **全部** repository、DAO、远程 API、持久化 | UI 状态、Compose |
| 网络 | `network-core` | HTTP 客户端、拦截器、序列化 | 业务模型 |
| 领域 | `core-domain` | 可复用业务流程（use case），纯 JVM 可测 | Android/Compose 依赖 |
| 播放 | `core-player` | 播放内核封装 | 业务编排 |
| UI | `design-system`、`design-tokens` | 主题、双渲染器（material3/ miuix）、通用组件 | 业务状态 |
| 应用 | `app` | 组装、导航、feature UI、settings | 长期业务规则（应下沉） |
| TV | `app-tv` | TV 入口与 TV 专属 UI | 与手机重复的 presentation（应下沉） |

## 2. 所有权规则

1. **模型唯一所有者**：同一个业务概念只允许存在一份 data class。`app/data/model` 仅允许放 `app` 私有的 UI 展示模型；业务模型一律在 `core-model` 或 `core-data`。
2. **repository 唯一所有者**：repository 只存在于 `core-data`（及 `data-*`）下，按 API 域组织（如 `SearchRepository`、`VideoCatalogRepository`）。`app/data/repository` 为历史遗留，新代码不得在此新增；存量逐个下沉，下沉期间可用 typealias 过渡。
3. **domain 不得空壳**：`core-domain/usecase` 必须承载可复用流程（续播、稍后再看、弹幕过滤、WebDAV 同步、观看历史写入）。ViewModel 只做状态编排，业务分支下沉 use case。
4. **Policy 只保留一种含义**：`*Policy` 仅表示无副作用的分支/适配规则；有副作用或跨对象编排的逻辑改名为 use case 或 repository 方法。禁止再新增"名字像规则、实际是编排"的 Policy。
5. **导航单栈、职责分明**：全仓只有一个返回栈（`navigation/AppNavigation.kt` 的 `rememberNavBackStack<BiliPaiNavKey>` + `navigation3.BiliPaiNavDisplayHost`）。共享路由键与返回策略位于 `core/navigation`（`ScreenRoutes`、`VideoRoute`、`AppSystemBackAction`、`isVideoCardReturnTargetRoute` 等），它不属于 `navigation/` 也不属于 `navigation3/`；`navigation/` 是导航编排的真相源，`navigation3/` 只做宿主、转场与返回手势，`navigation3` 不得 import `navigation`（改走 `core/navigation` 共享类型）。`miuix-navigation` 模块保留（Miuix 导航 + 视频返回补丁）。

## 3. 依赖方向

```
app ─┬─> feature(内部包) ─> core-domain ─> core-model
     ├─> core-data / data-* ─> network-core
     ├─> design-system (含 design-tokens) ─> core-model
     └─> core-player
app-tv ─> 同上（通过 :app 的公开面或 core-*，不得反向依赖 :app）
```

禁止：`core-*`/`data-*`/`design-*` 反向依赖 `app` 或 `app-tv`；feature 包之间互相 import 对方的内部实现（跨 feature 只走 core 层接口）。

## 4. UI 选项保护（Miuix / MD3）

**Miuix 与 Material 3 两套 UI 选项全程保留**，详见 [UI-PROTECTION.md](../UI-PROTECTION.md)。本文件任何迁移触及 `AppUiStyle`、双渲染器、`setUiStyle` 时，必须先读该清单并保持两分支可达。

## 5. 阶段性验收（对应结构审查报告）

- [ ] 阶段 1：`app/data/repository` 不再新增；存量逐个下沉至 `core-data`（或全部标注为 app 私有并有架构 lint 证明无跨层泄漏）
- [x] 阶段 1：`navigation3 → navigation` 反向 import 清零（共享键已下沉 `core/navigation`，`app/lint.xml` 豁免已移除）；全量手测返回栈行为待补
- [ ] 阶段 1：架构 lint 规则在 CI 生效（禁止反向依赖、禁止双份模型）
- [ ] 阶段 2：试点 feature 的 `x-impl` 不被其他 feature 直接 import
- [ ] 每阶段结束：`AppUiStyle.MIUIX` 与 `AppUiStyle.MATERIAL3` 两分支 grep 可达
