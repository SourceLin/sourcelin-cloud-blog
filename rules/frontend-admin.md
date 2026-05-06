# 前端规则 - 管理后台

适用范围：`sourcelin-ui/sourcelin-ui-admin`。`sourcelin-ui-admin-vue2` 仅作为迁移对照，不承载新功能。

## 1. 目录边界

- MUST：页面放在 `src/views/**`。
- MUST：接口封装放在 `src/api/**`。
- MUST：请求封装统一使用 `src/utils/request.ts`。
- MUST：路由放在 `src/router/**`。
- MUST：状态管理遵守现有 `src/store/**` 结构。
- MUST：跨页面复用组件放在 `src/components/**`，业务域复用组件放在对应 `src/views/<domain>/**`。
- MUST NOT：新功能落入 `sourcelin-ui-admin-vue2`。
- CHECK：审查新增文件路径，并执行 `pnpm run type-check`。

## 2. API 与类型

- MUST：遵守 `rules/api-contract.md`。
- MUST：`src/api/**` 只定义 URL、method、params、data 和类型，不处理 UI 状态。
- MUST：页面只消费 request 解包后的业务数据。
- MUST：不确定响应结构使用 `unknown` 或 `Record<string, unknown>`。
- MUST NOT：新增 `any`。
- MUST NOT：页面判断 `response.code` 或读取 `msg/rows/records/list`。
- CHECK：执行 `pnpm run type-check`，并审查 API 类型和页面消费字段。

## 3. 页面与模块化

- MUST：表格页优先复用现有 PageContent、ModuleListShell 或拆出的查询、表格、分页、操作列部件。
- MUST：页面组件只做装配，复杂查询、弹窗、表单和分页状态进入 composable 或共享壳。
- MUST：权限逻辑沿用现有路由、菜单、按钮权限体系。
- MUST NOT：为单个页面复制一套查询表单、表格分页和操作列协议。
- MUST NOT：改造页面时改变接口调用方式或权限逻辑，除非任务明确要求。
- CHECK：后台模块化改动后执行 `pnpm run type-check`、`pnpm run lint`。

## 4. UI 与错误处理

- MUST：保持 Element Plus 与现有后台布局体系。
- MUST：通用错误、401、网络异常由 request 层处理。
- MUST：页面只保留业务特定成功提示和必要业务失败提示。
- MUST NOT：引入新的 UI 库。
- MUST NOT：在页面散写网络错误、401、500 等通用错误处理。
- CHECK：执行 `pnpm run lint`。
