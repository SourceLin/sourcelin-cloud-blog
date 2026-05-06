# 前端架构说明

本文档描述 `sourcelin-ui-platform` 当前已经落地的目录结构、分层职责、运行时约束和同步边界。

## 1. 启动链路

### `src/app/main.ts`

- 引入全局样式入口 `src/assets/styles/index.scss`
- 创建并安装 Pinia
- 初始化主题 store
- 基于首屏路由启动全局 startup loading
- 安装路由并挂载 `src/app/App.vue`

### `src/app/App.vue`

- 持有根级 Naive Provider：`NConfigProvider`、`NMessageProvider`、`NDialogProvider`
- 从 CSS 变量读取主题值，并生成 Naive theme overrides
- 挂载全局布局组件、搜索弹层、主题过渡和启动遮罩
- 通过 `route.meta.shell === 'auth'` 切换认证页独立页面壳

### `src/app/router/**`

- `index.ts`
  - 聚合各模块的 `routes.ts`
  - 统一注册 `RedirectPage`、`NotFoundPage`
  - 在首次路由完成后标记 startup route ready
- `route-meta.ts`
  - 维护 `AppRouteMeta`、`AppRouteRecordRaw`
  - 统一生成 header、mobile、footer 导航数据
  - 封装路由跳转容错逻辑

## 2. 当前主线目录

```text
src/
  app/
  modules/
  shared/
  stores/
  assets/
```

### `src/modules/**`

- 以业务域组织页面、API、组件和页面逻辑
- 每个模块可包含：
  - `api/`
  - `components/`
  - `composables/`
  - `model/`
  - `pages/`
  - `routes.ts`

当前模块：

- `about`
- `article`
- `auth`
- `forum`
- `home`
- `hot`
- `navigation`
- `notice`
- `say`
- `treehole`（树洞唯一入口页，路径 `/treehole`；原独立 `comments` 留言墙模块已收敛，评论能力见下方 `shared`）
- `user`

### `src/shared/**`

- `api/`
  - `http.ts`、`response.ts`
  - `comment.api.ts`：前台评论相关 HTTP（`/front/comments`），供文章、树洞等复用
- `components/ui/`
  - `S*` UI 抽象组件和图标映射
- `components/layout/`
  - 头部、页脚、主题过渡、悬浮工具栏、移动抽屉、页面壳等跨模块布局
- `components/comments/`
  - `CommentComposer.vue`、`CommentThread.vue`：评论输入与线程展示（与 `useContentComments` 配合）
- `components/feedback/`
  - 搜索弹层、启动遮罩、空态等反馈类组件
- `composables/`
  - 跨模块复用逻辑（含 `useContentComments.ts`：按 `targetId` + `source` + `isAnonymousAllowed` 等装配评论状态）
- `types/`
  - 共享类型定义
- `utils/`
  - 认证、格式化和纯工具函数

### `src/stores/**`

- `site.store.ts`
  - 站点基础信息、访问统计、乱码兜底修正
- `ui.store.ts`
  - 搜索弹层、移动抽屉、登录标记、startup loading 状态
- `theme.ts`
  - 真正的主题 store 实现，负责系统主题同步和持久化
- `theme.store.ts`
  - 兼容导出 `useThemeStore`
- `user.ts`
  - 登录态、用户信息、角色、权限
- `auth.store.ts`
  - 兼容导出 `useAuthStore`

## 3. 路由与导航协议

### route meta 字段

| 字段 | 作用 |
|------|------|
| `title` | 导航和页面展示标题 |
| `childTitle` | 头部导航中的父级自项名称 |
| `icon` | 移动导航图标 |
| `hidden` | 是否从导航中隐藏 |
| `order` | 导航排序 |
| `nav` | `header` / `header-child` / `mobile` / `footer` |
| `parent` | `header-child` 的父级路径 |
| `mobile` | 是否进入移动导航 |
| `footer` | 是否进入页脚导航 |
| `shell` | 页面壳，当前支持 `default` / `auth` |

### 导航收口规则

- 每个模块维护自己的 `routes.ts`
- `src/app/router/index.ts` 统一聚合为 `appRoutes`
- 导航永远由 route meta 推导，不再维护第二份 header / mobile / footer 配置
- 认证相关页面通过 `shell: 'auth'` 切出默认头部和页脚

### 文章详情来源上下文

- 文章详情页支持通过 query 保留来源页面上下文，用于 Hero 面包屑返回。
- 当前约定由 `src/modules/article/utils/article-source.ts` 统一生成和解析：
  - 分类页进入文章：`/article/:id?from=category&name=<分类名>`
  - 标签页进入文章：`/article/:id?from=tag&name=<标签名>`
  - 归档页进入文章：`/article/:id?from=archive[&categoryId=<分类ID>][&categoryName=<分类名>]`
- `ArticlePage.vue` 不直接写死“首页 / 文章”返回逻辑，而是消费来源上下文决定面包屑文案与返回目标。
- 新增文章入口时，如需保留返回路径，必须复用 `article-source.ts`，不要在页面里各自拼 query。

## 4. 请求层约束

### `src/shared/api/http.ts`

- 统一创建 axios 实例
- `baseURL` 来自 `VITE_APP_BASE_API`（开发环境为 `/blog-api`）
- 默认附带 Bearer Token
- 全局错误提示通过 `src/shared/composables/useSMessage.ts` 的 `sDiscreteMessage`（由 `SMessage.vue` 桥接注入，与根主题一致），**不使用** `createDiscreteApi`

### URL 前缀规则

- `/front/**`、`/app/**`
  - 直接作为业务路径发送（在 dev 模式下由 Vite 代理 `/blog-api/front/**` → 网关 `/blog/front/**` → 博客服务）
- `/auth/**`、`/system/**`、`/code`
  - 直接作为业务路径发送（在 dev 模式下由 Vite 代理 `/blog-api/auth/**`、`/blog-api/code` → 网关 `/auth/**`、`/code`）
- 匿名接口
  - 显式传 `headers: { isToken: false }`

### 认证密码传输约定

- 登录/注册接口在前端使用 RSA 公钥加密密码后再提交（见 `src/shared/utils/jsencrypt.ts`）。
- 后端统一认证入口兼容“密文优先解密、明文兜底”，用于平滑兼容历史客户端。
- `/auth/login`、`/auth/register` 的验证码由网关 `ValidateCodeFilter` 统一校验并一次性消费。
- `sourcelin-auth` 服务不再重复读取同一 `uuid` 的验证码，避免出现“验证码已过期”的二次校验问题。
- 博客前台登录采用 Sa-Token：登录响应 token 来源为 `response.data`（字符串），不是 `response.token`。
- 请求头只在 token 为有效值时追加：`undefined`、`null`、空串均禁止写入 `Authorization`。
- 网关 JWT 解析仅用于后台管理链路；`/blog/front/**` 不进入 JWT 解析流程，由业务端 Sa-Token 规则控制私有接口。

### API 放置规则

- 业务 API 统一放 `src/modules/*/api/*.api.ts`
- 页面和组件不直接写 axios 调用

## 5. 状态与页面逻辑收口

原则：

- `pages/**` 只做装配，不堆叠复杂状态和接口适配
- 模块页面逻辑优先进入 `modules/*/composables/**`
- 跨模块复用逻辑进入 `shared/composables/**`
- 需要跨页面共享的状态才进入 Pinia

示例：

- `src/modules/home/composables/useHomePage.ts`
- `src/modules/article/composables/useArticleDetail.ts`
- `src/modules/article/composables/useArticleComments.ts`（内部委托 `useContentComments`，`source: 'article'`）
- `src/modules/treehole/composables/useTreeholePage.ts`（树洞列表、弹幕、展开评论；评论委托 `useContentComments`，`source: 'treehole'`）
- `src/shared/composables/useContentComments.ts`
- `src/shared/composables/useSearchDialog.ts`
- `src/shared/composables/useThemePalette.ts`

## 6. 设计系统分层

```text
Design Tokens
  -> Foundation
  -> UI Abstraction
  -> Naive UI
  -> Business Components
  -> Pages
```

### 路径映射

- Design Tokens
  - `src/assets/styles/tokens/**`
- Foundation
  - `src/assets/styles/foundation/**`
- Naive overrides
  - `src/assets/styles/naive/override.scss`
- UI Abstraction
  - `src/shared/components/ui/**`
- Shared style facade
  - `src/shared/styles/**`
- Business Components
  - `src/shared/components/layout/**`
  - `src/shared/components/comments/**`
  - `src/shared/components/feedback/**`
  - `src/shared/components/business/**`
  - `src/modules/*/components/**`
- Pages
  - `src/modules/*/pages/**`

### 当前边界

允许直接使用 Naive UI 的位置：

- `src/app/App.vue`
- `src/shared/components/ui/**`
- `src/shared/api/http.ts` 中显式记录的工具层例外

禁止直接使用 Naive UI 的位置：

- `src/shared/components/layout/**`
- `src/shared/components/feedback/**`
- `src/modules/*/components/**`
- `src/modules/*/pages/**`

### 样式边界

- 业务组件和页面必须通过 `var(--*)` 消费颜色、边框、表面和阴影
- `:deep(.n-*)`、`.n-*` override 只允许进入 `src/assets/styles/naive/override.scss` 或 UI 抽象层
- `glass` mixin 只允许留在 Foundation 或 UI 抽象层
- `src/assets/styles/responsive-enhancements.scss` 仍作为全局响应式兼容工具层保留
- 新的响应式底层能力优先进入 `src/assets/styles/foundation/responsive.scss`
- 业务组件、共享业务组件和页面只能通过 `@import '@/shared/styles/responsive';` 消费响应式 mixin，禁止直接导入 `@/assets/styles/foundation/responsive`

### 表面语义边界

- Foundation 中的 `glass-card`、`sourcelin-panel*`、`sourcelin-chip-surface*` 属于材料层，不应在业务组件和页面中继续扩散。
- 业务层需要消费这些表面能力时，必须先通过 UI 抽象层语义组件或 prop 收口：
  - `SSurfacePanel`
  - `SSurfaceChip`
  - `SCard`
  - `SInput` / `STextarea` / `SUpload` 的 `surface` 变体
- `src/shared/components/business/**` 用于跨页面复用的业务成品组件，例如：
  - `HeroStatCard`
  - `StatsPanelCard`
- `FeatureCard` 视为迁移期兼容入口，不再允许新增使用；后续卡片实现统一迁移到 `SCard` 或 surface 语义组件。
- `SSurfaceChip` 已升级为带语义尺寸的 UI 抽象组件，当前标准接口为：
  - `size: xs | sm | md`
  - `variant: label | counter | badge | button`
- 约定：
  - `label`：用于 section kicker / 眉题
  - `counter`：用于页码、数量、统计徽标，内置 `tabular-nums`
  - `badge`：用于静态状态标签
  - `button`：用于轻操作胶囊按钮
- 页面和业务组件不得再为上述稳定场景单独重复维护芯片几何尺寸；如现有接口无法覆盖，优先扩展 `SSurfaceChip`，而不是回到页面层手写 `padding` / `min-height`。

### 守卫策略

- `npm run style:guard`
  - 负责 Naive 边界、glass mixin、硬编码颜色等显性违规
- `npm run layering:guard`
  - 负责 `FeatureCard` 新增使用
  - 负责业务/页面层新增 `glass-card`、`sourcelin-panel*`、`sourcelin-chip-surface*` 直达用法
  - 负责业务/页面层新增 `@/assets/styles/foundation/responsive` 直达导入
  - 当前允许通过 allowlist 托管历史债务，但禁止新增结构性漂移

### 当前前台动效策略（2026-04）

- 前台当前统一**不启用** `prefers-reduced-motion` 降级。
- 页面、业务组件、共享 composable 与 Foundation 样式中，禁止新增：
  - `matchMedia('(prefers-reduced-motion: reduce)')`
  - `@media (prefers-reduced-motion: reduce)`
  - 等价的 reduced-motion 分支
- 现阶段所有前台页面统一走默认动画路径，避免树洞弹幕、首页公告、友链滚动与面板切换出现局部静止、局部动画的割裂体验。
- 如后续重启无障碍治理，必须按全站统一策略重新设计，不允许单页面私自恢复 reduced-motion 特判。

## 7. 加载态规范

- 首屏结构化占位统一使用 `SSkeleton` 与 `src/shared/components/feedback/skeletons/**`，业务层和页面层禁止直接使用 `n-skeleton`。
- `page-container-wrap` 仅承载区块内部骨架；首页等聚合页不得包整页大骨架，需按统计卡、列表、导航块等真实结构拆分加载。
- `WaterfallList.vue` 负责瀑布流默认骨架；首页文章流、用户中心列表、树洞列表、搜索弹窗结果均复用共享 skeleton 组件。
- 增量加载、load more、按钮提交等非首屏等待场景保持轻量反馈，优先 quiet 文案、小 spinner 或局部 loading。
- 加载区块必须提供 `aria-busy="true"`；`SSkeleton` 默认 `aria-hidden="true"`，避免读屏器重复朗读占位内容。

## 8. 生成文件与验证

### 生成文件

- `src/components.d.ts`
- `src/auto-imports.d.ts`

这两个文件视为生成物，不手工维护。

### 最低验证

```bash
npm run typecheck
npm run build
npm run style:guard
npm run layering:guard
npm run test:style-guard
npm run test:layering-guard
npm run test:architecture
```

## 9. 文档同步范围

以下内容发生变化时，必须同步更新：

- `README.md`
- `AGENTS.md`
- `docs/style-asset-ledger.md`
- `rules/`
- `skills/`
- `.cursor/README.md`
- `.cursor/rules/project-rules.mdc`
- `.opencode/README.md`
- `.opencode/skills/`
