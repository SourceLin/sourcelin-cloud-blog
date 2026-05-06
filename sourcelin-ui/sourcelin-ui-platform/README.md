# Sourcelin UI Platform

Sourcelin Blog 前台项目，基于 Vue 3、TypeScript、Vite、Pinia 和 Naive UI。当前主线架构已经收敛为 `app + modules + shared + stores + assets`，并以设计令牌、Foundation、UI 抽象层和模块页面分层为核心约束。

## 技术栈

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router 4
- Naive UI
- Axios
- ECharts

## 常用命令

```bash
npm install
npm run dev
npm run build
npm run preview
npm run typecheck
npm run style:guard
npm run test:style-guard
npm run test:architecture
```

## 当前目录结构

```text
src/
  app/
    App.vue
    main.ts
    router/
  modules/
    about/
    article/
    auth/
    comments/
    forum/
    home/
    hot/
    navigation/
    notice/
    say/
    treehole/
    user/
  shared/
    api/
    components/
      feedback/
      layout/
      ui/
    composables/
    types/
    utils/
  stores/
  assets/
    images/
    styles/
      foundation/
      naive/
      tokens/
```

## 架构要点

### 启动与应用壳

- `src/app/main.ts` 负责安装 Pinia、初始化主题、启动首屏 loading，并挂载路由。
- `src/app/App.vue` 持有根级 Naive Provider、基于 CSS 变量的主题校准、全局布局组件、搜索弹层和启动遮罩。
- 认证页通过 `route.meta.shell = 'auth'` 使用独立页面壳，不复用默认头部和页脚。

### 路由与导航

- 每个模块通过 `src/modules/*/routes.ts` 导出自己的路由。
- `src/app/router/index.ts` 统一聚合模块路由，并注册启动阶段的路由就绪标记。
- 顶部导航、移动导航和页脚导航统一由 `src/app/router/route-meta.ts` 基于 route meta 推导。

推荐使用的 route meta 字段：

- `title`
- `childTitle`
- `icon`
- `hidden`
- `order`
- `nav`
- `parent`
- `mobile`
- `footer`
- `shell`

### 请求与状态

- 业务 API 统一位于 `src/modules/*/api/*.api.ts`，统一走 `src/shared/api/http.ts`。
- `http.ts` 会自动给 `/front`、`/app` 前缀请求补上 `/blog` 上下文，并在默认情况下附带 Bearer Token。
- `/auth`、`/system`、`/code` 等路径保持原样；匿名请求显式传 `headers: { isToken: false }`。
- 全局状态位于 `src/stores/`。当前主线 store 包括 `site.store.ts`、`ui.store.ts`、`theme.ts`、`theme.store.ts`、`user.ts` 和兼容入口 `auth.store.ts`。

### 设计系统边界

```text
Design Tokens
  -> Foundation
  -> UI Abstraction
  -> Naive UI
  -> Business Components
  -> Pages
```

- `src/assets/styles/tokens/**` 只定义共享视觉变量。
- `src/assets/styles/foundation/**` 承载 theme、glass、animation、responsive、form-controls、mixins 和 functions 等底层能力。
- `src/shared/components/ui/**` 是唯一正式 UI 抽象层，负责封装 Naive UI 并输出 `S*` 组件。
- `src/shared/components/layout/**`、`src/shared/components/feedback/**` 和 `src/modules/*/components/**` 按业务组件层管理。
- 页面层位于 `src/modules/*/pages/**`，只负责路由装配和组合页面逻辑。

### 加载态规范

- 全站结构化首屏加载统一使用 `src/shared/components/ui/SSkeleton.vue` 及 `src/shared/components/feedback/skeletons/**`，业务层禁止直接使用 `n-skeleton`。
- `page-container-wrap` 只在内部区块做骨架，不包整页大骨架；首页优先覆盖文章列表、瀑布流、统计卡片和导航页。
- 已有内容后的增量加载保持轻量，优先使用局部按钮 loading 或小型 spinner，不回退为大面积 skeleton。
- 骨架容器需要标记 `aria-busy="true"`，骨架元素本身保持 `aria-hidden="true"`，并继续使用 CSS 变量适配主题。

## 当前边界

- 允许直接使用 Naive UI 的位置：
  - `src/app/App.vue`
  - `src/shared/components/ui/**`
  - `src/shared/composables/useSMessage.ts` / `useSNotification.ts`（与根主题一致的 Message / Notification 桥接，见 `SMessage.vue` / `SNotification.vue`）
- 业务组件和页面不得直接使用 `n-*`、`naive-ui`、glass mixin、`:deep(.n-*)` 或 `.n-*` override。
- 业务组件和页面中的颜色、边框、表面和阴影必须通过 `var(--*)` 消费。
- `src/assets/styles/responsive-enhancements.scss` 仍作为全局响应式兼容工具层保留；新增底层响应式能力优先进入 `src/assets/styles/foundation/responsive.scss`。
- 业务组件和页面使用响应式 mixin 时必须导入 `@/shared/styles/responsive`，不得直接导入 `@/assets/styles/foundation/responsive`。

## 验证建议

涉及架构、边界、样式守卫或共享约定调整后，至少执行：

```bash
npm run typecheck
npm run build
npm run style:guard
npm run test:style-guard
npm run test:architecture
```

## 资料入口

- `AGENTS.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
- `rules/README.md`
- `skills/README.md`
- `.cursor/README.md`
- `.opencode/README.md`
