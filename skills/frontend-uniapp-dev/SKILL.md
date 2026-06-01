---
name: frontend-uniapp-dev
description: 用于新增或修改 Sourcelin Uniapp 移动端页面、组件、API、状态、pages.json、manifest 和跨端兼容能力。
---

# Uniapp 移动端开发技能

## 使用场景

- 修改 `sourcelin-ui/sourcelin-ui-uniapp`。
- 新增或调整微信小程序页面、Tab、分包、静态资源或构建配置。
- 接入移动端前台 API、登录态、用户信息、分页列表和互动能力。
- 落地 Liquid Glass Mobile 视觉规范、移动端交互和跨端兼容处理。

## 输入

- 需求说明或待修改文件。
- 当前 `src/pages.json`、`src/config/env.ts`、请求层、store 和业务模块实现。
- 相关后端接口契约与产品设计文档。

## 输出

- 符合 Uniapp 目录边界和小程序限制的代码改动。
- 明确的 API 类型、页面状态和交互反馈。
- 已执行验证命令和结果。

## 执行步骤

1. 读取仓库根 `AGENTS.md`、`rules/frontend-uniapp.md`、`rules/api-contract.md`、`rules/coding-conventions.md`、`rules/testing-and-validation.md`。
2. 读取 `sourcelin-ui/sourcelin-ui-uniapp/AGENTS.md`。
3. 定位改动属于页面、模块 API、composable、store、组件、静态资源、`pages.json`、`manifest.json` 或环境配置。
4. 涉及 API 时先确认路径属于 `/blog-api` 前台域；移动端登录使用 `loginType=mini`。
5. 页面请求必须经过 `src/utils/request.ts`，复杂请求、分页、筛选和提交逻辑优先下沉到 `src/modules/**/composables`。
6. 新增页面同步维护 `src/pages.json`；新增 Tab 同步维护图标、文本和页面路径。
7. 小程序端优先验证，避免使用浏览器专有 API；平台差异使用 Uniapp 条件编译。
8. 样式使用 `src/styles/**` token 和自定义 `s-*` 组件体系，避免照搬 Web 前台布局。
9. 修改后执行 `npm run lint` 和 `npm run type-check`；涉及页面、路由、Tab、manifest、静态资源或小程序兼容时执行 `npm run build:mp-weixin`；涉及 H5 时追加 `npm run build:h5`。

## 依赖 rules

- `rules/frontend-uniapp.md`
- `rules/api-contract.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`

## 禁止行为

- 禁止页面或 composable 直接调用 `uni.request`。
- 禁止移动端调用 `/prod-api/**`、`/system/**`、`/blog/admin/**` 等后台管理域接口。
- 禁止移动端登录继续复用 Web 前台 `loginType=blog`。
- 禁止新增 `any`、旧分页字段或旧响应字段 fallback。
- 禁止使用 Vue Router、`window`、`document`、`localStorage` 等浏览器专有能力。
- 禁止配置不存在的 `subPackages[].root`。
- 禁止引入重型 UI 框架替代现有自定义组件体系。
