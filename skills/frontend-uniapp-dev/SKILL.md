---
name: frontend-uniapp-dev
description: 用于新增或修改 Sourcelin Uniapp 移动端页面、组件、API、状态、pages.json、manifest 和跨端兼容能力。
---

# Uniapp 移动端开发技能

## 使用场景

- 修改 sourcelin-ui/sourcelin-ui-uniapp。
- 新增或调整微信小程序页面、Tab、分包、静态资源或构建配置。
- 涉及能力开关（useMiniAccess、capabilityStore、MobileCapabilities）的功能可见性控制。
- 涉及自定义液态 TabBar（liquid-tabbar.ts）的 Tab 配置与切换逻辑。
- 接入移动端前台 API、登录态、用户信息、分页列表和互动能力。
- 落地 Liquid Glass Mobile 视觉规范、移动端交互和跨端兼容处理。

## 输入

- 需求说明或待修改文件。
- 当前 src/pages.json、src/config/env.ts、请求层、store 和业务模块实现。
- 相关后端接口契约与产品设计文档。

## 输出

- 符合 Uniapp 目录边界和小程序限制的代码改动。
- 明确的 API 类型、页面状态和交互反馈。
- 已执行验证命令和结果。

## 执行步骤

1. 读取仓库根 AGENTS.md、ules/frontend-uniapp.md、ules/api-contract.md、ules/coding-conventions.md、ules/testing-and-validation.md。
2. 读取 sourcelin-ui/sourcelin-ui-uniapp/AGENTS.md。
3. 定位改动属于页面、模块 API、composable、store、组件、静态资源、pages.json、manifest.json 或环境配置。
4. 涉及 API 时先确认路径属于 /blog-api 前台域；移动端登录使用 loginType=mini。
5. 页面请求必须经过 src/utils/request.ts，复杂请求、分页、筛选和提交逻辑优先下沉到 src/modules/**/composables。
6. 新增页面同步维护 src/pages.json；新增 Tab 同步维护图标、文本和页面路径。
7. 小程序端优先验证，避免使用浏览器专有 API；平台差异使用 Uniapp 条件编译。
8. 样式使用 src/styles/** token 和自定义 s-* 组件体系，避免照搬 Web 前台布局。
9. 修改后执行 
pm run lint 和 
pm run type-check；涉及页面、路由、Tab、manifest、静态资源或小程序兼容时执行 
pm run build:mp-weixin；涉及 H5 时追加 
pm run build:h5。
10. 涉及功能可见性时使用 useMiniAccess().can() 判断能力开关，能力定义来自 src/shared/api/capability.api.ts 的 MobileCapabilities 接口，由 capabilityStore 在应用启动时从 /front/mobile/capabilities 拉取。
11. 涉及底部导航时注意区分原生 TabBar（首页、发现、我的，共 3 个）和液态 TabBar（4 个，含圈子）。圈子页通过 
avigateTo 跳转而非 switchTab，液态 TabBar 配置在 src/shared/utils/liquid-tabbar.ts。

## 依赖 rules

- ules/frontend-uniapp.md
- ules/api-contract.md
- ules/coding-conventions.md
- ules/testing-and-validation.md

## 禁止行为

- 禁止页面或 composable 直接调用 uni.request。
- 禁止移动端调用 /prod-api/**、/system/**、/blog/admin/** 等后台管理域接口。
- 禁止移动端登录继续复用 Web 前台 loginType=blog。
- 禁止新增 ny、旧分页字段或旧响应字段 fallback。
- 禁止使用 Vue Router、window、document、localStorage 等浏览器专有能力。
- 禁止配置不存在的 subPackages[].root。
- 禁止引入重型 UI 框架替代现有自定义组件体系。
- 禁止在未通过 useMiniAccess().can() 检查的情况下直接展示需要能力开关控制的功能入口。