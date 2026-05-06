# 前端规则 - 博客前台

适用范围：`sourcelin-ui/sourcelin-ui-platform`。

## 1. 目录边界

- MUST：页面放在 `src/modules/**/pages/*.vue`。
- MUST：模块业务组件放在 `src/modules/**/components/*.vue`。
- MUST：页面请求、分页、筛选、提交和状态逻辑放在 `src/modules/**/composables/*.ts`。
- MUST：模块 API 放在 `src/modules/**/api/*.api.ts`。
- MUST：跨模块共享 API 放在 `src/shared/api/*.api.ts`。
- MUST：请求基座使用 `src/shared/api/http.ts`，`src/shared/api/request.ts` 只作为兼容转发入口。
- MUST：UI 抽象组件放在 `src/shared/components/ui/**`。
- MUST NOT：页面文件承载大段请求编排、状态机和业务组件实现。
- CHECK：执行 `node scripts/architecture-guard/frontend-platform-guard.mjs`。

## 2. API 与类型

- MUST：遵守 `rules/api-contract.md`。
- MUST：接口类型显式声明，返回值与后端真实契约一致。
- MUST：前台用户能力走 `/front/*`。
- MUST NOT：前台调用 `/system/*` 管理域接口。
- MUST NOT：新增 `any`、旧分页字段或旧响应字段 fallback。
- CHECK：执行 `node scripts/api-contract-scan.mjs` 和 `npm run typecheck`。

## 3. 组件拆分

- MUST：页面组件只做布局装配、路由参数读取和少量事件转发。
- MUST：业务组件按展示职责拆分，单文件超过 400 行必须优先拆分。
- MUST：页面超过 500 行必须拆出组件或 composable。
- MUST：组合式函数按职责命名为 `useXxxQuery`、`useXxxForm`、`useXxxInteractions`、`useXxxState`。
- MUST NOT：把新的大状态泥球集中到单个 composable。
- CHECK：拆分后执行 `npm run typecheck`、`npm run style:guard`、`npm run test:architecture`。

## 4. 设计系统

- MUST：业务组件和页面优先使用 `S*` UI 抽象、`SSurfacePanel`、`SSurfaceChip` 或业务成品组件。
- MUST：加载态、空态、错误态优先复用 `src/shared/components/feedback/**`。
- MUST：颜色、背景、边框、阴影、渐变使用 CSS 变量或 token 派生写法。
- MUST NOT：业务组件和页面直接使用 `<n-*>`。
- MUST NOT：业务组件和页面直接导入 `naive-ui` 或新增 `createDiscreteApi`。
- MUST NOT：新增 `<FeatureCard>`、`glass-card`、`sourcelin-panel*`、`sourcelin-chip-surface*` 直达用法。
- MUST NOT：新增 `:deep(.n-*)`、`@include liquid-glass-effect`、`!important`。
- CHECK：执行 `npm run style:guard` 和 `npm run test:architecture`。

## 5. 路由与状态

- MUST：模块路由放在 `src/modules/**/routes.ts`，由 `src/app/router/index.ts` 聚合。
- MUST：全局状态放在 `src/stores/**`，局部页面状态放在 composable。
- MUST NOT：为了跨组件通信滥用全局 store。
- CHECK：路由或状态改动后执行 `npm run test:architecture`。
