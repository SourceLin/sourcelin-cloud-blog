---
name: frontend-admin-dev
description: 用于新增或修改 Sourcelin Vue3 管理后台页面、接口封装、表格分页、权限逻辑、模块化组件和 Element Plus 交互。
---

# 管理后台开发技能

## 使用场景

- 修改 `sourcelin-ui/sourcelin-ui-admin`。
- 新增或改造后台页面、API、表格、分页、弹窗、操作列。
- 拆分 PageContent、ModuleListShell 或领域页面。
- 修复后台类型、lint、权限或 API 消费问题。

## 输入

- 需求说明或待修改文件。
- 后台菜单、路由、权限和接口契约。
- 当前 `src/views/**`、`src/api/**`、`src/components/**` 实现。

## 输出

- 符合后台目录边界和权限体系的代码改动。
- 不破坏现有接口调用方式和权限逻辑。
- 已执行验证命令和结果。

## 执行步骤

1. 读取 `AGENTS.md`、`rules/frontend-admin.md`、`rules/api-contract.md`、`rules/coding-conventions.md`、`rules/testing-and-validation.md`。
2. 确认新功能落在 `sourcelin-ui/sourcelin-ui-admin`，不修改 `sourcelin-ui-admin-vue2`。
3. API 封装放入 `src/api/**`，统一通过 `src/utils/request.ts`。
4. 页面放入 `src/views/**`，共享结构优先复用 `src/components/**` 或领域 shared 组件。
5. 表格页统一使用标准分页字段，查询、表格、分页、操作列优先复用共享壳。
6. 保持现有路由、菜单、按钮权限模型，不无故改权限判断。
7. 修改后运行 `pnpm run type-check`；涉及样式或格式运行 `pnpm run lint`；涉及模块边界运行 `node scripts/architecture-guard/frontend-admin-guard.mjs`。

## 依赖 rules

- `rules/frontend-admin.md`
- `rules/api-contract.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`

## 禁止行为

- 禁止新增功能到 `sourcelin-ui-admin-vue2`。
- 禁止页面判断 `response.code`、读取 `msg/rows/records/list`。
- 禁止新增 `any`。
- 禁止引入新的 UI 库。
- 禁止复制一套查询表单、分页和操作列协议绕过现有共享壳。
