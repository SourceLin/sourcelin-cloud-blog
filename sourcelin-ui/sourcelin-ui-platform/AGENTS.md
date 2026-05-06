# AGENTS.md

本文档用于约束在本仓库中工作的各类智能编码工具。
默认请使用中文进行沟通、注释、提交说明和文档更新。

## 项目概览

- **项目名称**：`sourcelin-ui-platform`
- **项目类型**：Sourcelin Blog 博客前台
- **技术栈**：Vue 3 + TypeScript + Vite + Pinia + Naive UI
- **开发端口**：`80`
- **前端 API 基础路径**：开发环境 `VITE_APP_BASE_API=/blog-api`

## 常用命令

```bash
# 安装依赖
npm install

# 启动开发环境
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview

# 类型检查
npm run typecheck

# 样式边界检查
npm run style:guard

# 分层边界检查
npm run layering:guard

# 样式守卫测试
npm run test:style-guard

# 分层守卫测试
npm run test:layering-guard

# 架构边界测试
npm run test:architecture
```

## 项目结构

```text
src/
  app/              # 应用入口、根组件、路由聚合、route meta
  modules/          # 按业务域组织页面、组件、API、页面逻辑
  shared/           # 跨模块共享能力
  stores/           # Pinia stores 与兼容导出
  assets/
    styles/         # Tokens、Foundation、Naive overrides、全局样式入口
```

## 分层设计系统

本项目采用固定分层架构，必须严格遵守：

```text
Design Tokens
  -> Foundation
  -> UI 抽象层
  -> Naive UI
  -> 业务组件
  -> 页面
```

### 层级职责

| 层级 | 路径 | 职责 |
|------|------|------|
| Design Tokens | `src/assets/styles/tokens/*.scss` | 只定义 CSS 变量和语义令牌 |
| Foundation | `src/assets/styles/foundation/*.scss` | theme、glass、animation、responsive、mixins、functions、form-controls |
| UI 抽象层 | `src/shared/components/ui/` | 封装 Naive UI，统一视觉实现 |
| Shared Style Facade | `src/shared/styles/*.scss` | 给业务组件和页面提供允许消费的样式能力入口 |
| 业务组件 | `src/modules/*/components/**/*.vue`、`src/shared/components/{layout,feedback,comments,business}/**/*.vue` | 业务结构与领域逻辑 |
| 页面 | `src/modules/*/pages/*.vue` | 路由级页面装配 |

### 单一视觉来源

- 所有颜色、表面、边框、渐变、阴影优先来自 CSS 变量。
- 组件和页面中不得新增硬编码颜色值。
- `src/assets/styles/**` 可以定义底层视觉值和全局兼容样式，但业务层消费仍必须走 `var(--*)`。
- **主题桥接例外**：`src/app/App.vue` 中通过 `readThemeColor(变量名, fallback)` 传给 Naive `GlobalThemeOverrides` 的字面量 `fallback`，用于 CSS 变量未就绪时的安全网，**不视为**业务层硬编码颜色违规；但其值必须与 [`src/assets/styles/tokens/base.scss`](src/assets/styles/tokens/base.scss) 中 `:root` / `html[data-theme='dark']` 对应变量保持同步。

### 表面语义治理

- Foundation 的表面类（如 `glass-card`、`sourcelin-panel*`、`sourcelin-chip-surface*`）只负责底层材料能力，不应长期直接暴露给业务组件和页面。
- 业务和页面需要面板、内凹字段、胶囊标签等表面时，优先使用 UI 抽象层语义组件：
  - `SSurfacePanel`
  - `SSurfaceChip`
  - `SCard`
  - `SInput` / `STextarea` / `SUpload` 的 `surface` prop
- `src/shared/components/business/**` 是允许跨页面复用的业务组件层，用于承载 `HeroStatCard`、`StatsPanelCard` 一类业务成品组件；禁止把这类成品视觉塞回 `src/shared/components/ui/**`。
- `FeatureCard` 仅保留兼容迁移用途，禁止新增使用；新增业务卡片必须基于 `SCard` 或新的 surface 语义组件实现。
- `SSurfaceChip` 已具备正式语义接口：
  - `size: xs | sm | md`
  - `variant: label | counter | badge | button`
  新增芯片场景必须优先消费该接口，而不是在页面里重复补 `padding`、`min-height`、`font-variant-numeric` 等几何样式。

### 响应式样式入口

- `src/assets/styles/foundation/responsive.scss` 只作为底层断点和 mixin 归属。
- 业务组件、共享业务组件和页面如需 `sourcelin-down` / `sourcelin-up` / `sourcelin-between`，必须导入 `src/shared/styles/responsive.scss`。
- 禁止在业务组件、共享业务组件和页面中直接导入 `@/assets/styles/foundation/responsive`。

### Naive UI 边界

- `src/shared/components/ui/**` 可以直接使用 Naive UI。
- `src/app/App.vue` 可以持有根级 Naive Provider、主题覆盖和全局过渡。
- **禁止**在新代码中使用 `createDiscreteApi`（游离于根 `NConfigProvider` 之外，会导致弹窗主题与全局不一致）。全局 Message / Notification 与主题一致的能力由 `SMessage.vue` / `SNotification.vue` 内桥接组件注入，并通过 [`src/shared/composables/useSMessage.ts`](src/shared/composables/useSMessage.ts)、[`useSNotification.ts`](src/shared/composables/useSNotification.ts) 导出 `sDiscreteMessage` / `sDiscreteNotification`（懒绑定根 Provider 树内的实例）；`http.ts`、业务 composable 仅消费上述导出。
- 业务组件和页面层不得直接使用 `n-*` 标签或直接导入 `naive-ui`。

### 路由与页面壳

- 路由由 `src/modules/*/routes.ts` 导出，并在 `src/app/router/index.ts` 统一聚合。
- 顶部导航、移动导航和页脚导航由 `src/app/router/route-meta.ts` 基于 route meta 推导。
- 认证页通过 `meta.shell = 'auth'` 使用独立页面壳。

### 请求与状态

- `src/modules/*/api/*.api.ts` 统一调用 `src/shared/api/http.ts`
- `baseURL` 来自 `VITE_APP_BASE_API`（dev 下为 `/blog-api`）
- URL 规则：
  - `/front/**`、`/app/**`：作为业务路径发送，由 Vite 代理 `/blog-api/front/**` 到网关 `/blog/front/**`，再转发至博客服务
  - `/auth/**`、`/system/**`、`/code`：作为业务路径发送，由 Vite 代理 `/blog-api/auth/**`、`/blog-api/code` 到网关 `/auth/**`、`/code`
  - 匿名接口需显式传 `headers: { isToken: false }`
- 全局状态优先放在 `src/stores/`
- 当前主线 store：`site.store.ts`、`ui.store.ts`、`theme.ts`、`theme.store.ts`、`user.ts`
- `auth.store.ts` 和 `theme.store.ts` 目前承担兼容导出职责

### 加载态规范

- 首屏结构化加载统一使用 `SSkeleton` 和 `src/shared/components/feedback/skeletons/**`。
- 业务组件和页面禁止直接使用 `n-skeleton`。
- `page-container-wrap` 只做内部区块骨架，不包整页大骨架。
- 已有内容后的增量加载默认保留轻 spinner / quiet loading，不回退为大面积 skeleton。
- 加载容器需标记 `aria-busy="true"`，骨架元素保持 `aria-hidden="true"`，并继续使用 CSS 变量适配主题。

### 禁止模式

| 禁止写法 | 正确方向 |
|---------|---------|
| `<n-card>`、`<n-input>`、`<n-button>` | 使用 `S*` UI 抽象组件 |
| `@include liquid-glass-effect` 出现在业务组件或页面 | 通过 UI 抽象层或 Foundation 提供能力 |
| `:deep(.n-*)` 出现在业务组件或页面 | 移动到 UI 抽象层或 `src/assets/styles/naive/override.scss` |
| 在业务和页面样式里直接写 `#hex`、`rgb()`、`rgba()` | 使用 `var(--*)` |
| 在业务组件或页面直接写 `glass-card`、`sourcelin-panel*`、`sourcelin-chip-surface*` | 通过 `SSurfacePanel`、`SSurfaceChip`、`SCard`、组件 `surface` prop 收口 |
| 在业务组件或页面直接导入 `@/assets/styles/foundation/responsive` | 使用 `@import '@/shared/styles/responsive';` |
| 新增 `<FeatureCard>` | 使用 `SCard`、`HeroStatCard`、`StatsPanelCard` 或 surface 语义组件 |
| 在页面里堆叠接口适配和副作用 | 下沉到 `modules/*/composables` 或 `shared/composables` |

### 快速检查清单

编辑 Vue / SCSS 后，至少确认：

- [ ] 模板中没有直接使用 `n-*` 组件（`shared/components/ui/` 和 `src/app/App.vue` 除外）
- [ ] 业务组件和页面中没有 `@include liquid-glass-effect`
- [ ] 业务组件和页面中没有 `:deep(.n-*)`
- [ ] 业务组件和页面中没有新增硬编码颜色
- [ ] 业务组件和页面中没有新增 `glass-card` / `sourcelin-panel*` / `sourcelin-chip-surface*` 直达用法
- [ ] 业务组件和页面中没有直接导入 `@/assets/styles/foundation/responsive`
- [ ] 没有新增 `<FeatureCard>`
- [ ] 导航没有再维护额外的第二份配置
- [ ] 首屏结构化加载使用 `SSkeleton`，未直接写 `n-skeleton`
- [ ] 增量加载没有退化为整块骨架，且加载容器补了 `aria-busy`

### 当前前台动效策略（2026-04）

- 当前产品决策：博客前台**暂不启用** `prefers-reduced-motion` 降级。
- 不要在页面、业务组件、共享 composable、Foundation 样式中新增：
  - `matchMedia('(prefers-reduced-motion: reduce)')`
  - `@media (prefers-reduced-motion: reduce)`
  - 等价的 reduced-motion 运行时分支
- 所有前台页面统一走默认动画路径；如果后续重启无障碍治理，必须整体设计后再统一恢复，禁止局部页面私自加回 reduced-motion 特判。

## 编码约定

### Vue 文件

- 组件名使用 PascalCase
- 文件顺序：`<template>` -> `<script setup>` -> `<style scoped>`

### TypeScript

- 开启严格模式
- props 和复杂返回值使用明确类型
- 禁止使用 `any`

### 错误处理

- 在 Composition API 中优先使用 `try/catch`
- 始终处理 loading 状态
- 面向用户的提示要使用可理解的错误信息

## 文档同步规则

当任务影响以下内容之一时，必须同步更新文档和规则：

- 架构边界
- 路由与导航约定
- UI 抽象层与 Naive 的关系
- Token、Foundation、override 与兼容层的归属
- 主题校准和启动加载方式
- 规则或技能中描述的开发约束

同步目标包括：

- `README.md`
- `AGENTS.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
- `rules/`
- `.cursor/README.md`
- `.cursor/rules/project-rules.mdc`
- `skills/`
- `.opencode/README.md`
- `.opencode/skills/`

## 规则源说明

- `rules/` 是唯一规则源目录。
- `.cursor/rules/` 只保留给 Cursor 使用的轻量入口文件。
- 如需修改规则内容，应优先修改 `rules/`，而不是在 `.cursor/` 中重复维护完整规则。

## 技能源说明

- `skills/` 是唯一技能源目录。
- `.opencode/skills/` 只保留给 OpenCode 使用的轻量入口文件。
- 如需修改技能内容，应优先修改 `skills/`，而不是在 `.opencode/` 中重复维护完整技能正文。

## 额外资源

- **项目规则**：`rules/README.md`
- **架构说明**：`docs/frontend-architecture.md`
- **样式台账**：`docs/style-asset-ledger.md`
- **项目技能**：`skills/README.md`
- **Cursor 入口**：`.cursor/rules/project-rules.mdc`
- **OpenCode 入口**：`.opencode/skills/sourcelin-ui-refactor/SKILL.md`
