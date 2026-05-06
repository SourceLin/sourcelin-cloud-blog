---
name: frontend-platform-dev
description: 用于新增或修改 Sourcelin 博客前台 Vue3 页面、组件、模块 API、composable、路由、状态和设计系统接入。
---

# 博客前台开发技能

## 使用场景

- 修改 `sourcelin-ui/sourcelin-ui-platform`。
- 拆分前台页面或业务组件。
- 新增模块 API、页面查询、分页、筛选、提交逻辑。
- 治理 Naive UI 直连、样式边界和前台架构测试。

## 输入

- 需求说明或待修改文件。
- 相关后端接口契约。
- 当前页面、组件、API、composable 实现。

## 输出

- 符合前台目录边界的代码改动。
- 明确类型定义。
- 已执行验证命令和结果。

## 执行步骤

1. 读取 `AGENTS.md`、`rules/frontend-platform.md`、`rules/api-contract.md`、`rules/coding-conventions.md`、`rules/testing-and-validation.md`。
2. 若子项目规则存在，读取 `sourcelin-ui/sourcelin-ui-platform/AGENTS.md` 与子项目 `rules/`。
3. 定位改动属于 `pages`、`components`、`composables`、`api`、`routes`、`stores` 或 `shared`。
4. API 先定义类型，再接入 `src/shared/api/http.ts` 或模块 `*.api.ts`。
5. 页面只保留装配逻辑；请求、分页、筛选、提交和复杂状态下沉到 composable。
6. 业务组件使用 `S*` UI 抽象或业务成品组件，不直连 Naive UI。
7. 修改后运行 `npm run typecheck`；涉及样式运行 `npm run style:guard`；涉及架构边界运行 `npm run test:architecture`。

## 依赖 rules

- `rules/frontend-platform.md`
- `rules/api-contract.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`

## 禁止行为

- 禁止新增 `any`。
- 禁止业务组件或页面直接使用 `<n-*>`、直接导入 `naive-ui`、新增 `createDiscreteApi`。
- 禁止新增 `glass-card`、`FeatureCard`、`:deep(.n-*)`、`@include liquid-glass-effect`、`!important`。
- 禁止页面读取 `rows/records/list/msg` 或判断 `code === 200`。
- 禁止通过降低 `style:guard` 或 `test:architecture` 规则来通过验证。
