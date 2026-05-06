# 博客前台架构说明

## 当前状态

博客前台位于 `sourcelin-ui/sourcelin-ui-platform`，采用 Vue 3 + TypeScript + Naive UI 封装层。当前模块化主线已经收敛到 `src/modules/**`，共享 API、UI 抽象、反馈组件和布局组件位于 `src/shared/**`。

已完成的第三阶段拆分：

- `modules/say`：`SayFeedCard.vue` 已拆为作者信息、媒体、互动栏、评论预览和状态聚合。
- `modules/article`：`TagPage.vue` 已拆为容器、Hero、筛选、列表和 `useTagPageQuery.ts`。
- `modules/about`：`AboutPage.vue` 已拆为展示区块和 `useAboutPage.ts`。
- `modules/navigation`：`LinksPage.vue` 已拆为 Hero、列表、申请表和 `useLinksPage.ts`。

## 目录边界

```text
src/modules/<domain>/
  api/              # 当前业务模块 API
  components/       # 当前业务模块组件，PascalCase
  composables/      # 页面请求、分页、筛选、提交与状态逻辑
  pages/            # 路由页面，只做装配
  styles/           # 页面级或模块级样式
src/shared/
  api/              # 跨模块 API 基座与共享接口
  components/ui/    # S* UI 抽象层
  components/feedback/
  components/business/
```

## 强制规则

- 页面优先保持容器化，复杂请求和状态下沉到 composables。
- 业务组件不得直接新增 `naive-ui` 依赖，不新增 `<n-*>` 直连。
- 新增颜色、背景、边框优先使用 CSS 变量。
- 不新增 `glass-card`、业务层 `!important`、`:deep(.n-*)`。
- 分页只消费 `items/total/page/pageSize/totalPages`。

## 验证命令

```bash
npm run typecheck
npm run style:guard
npm run test:architecture
node ../../scripts/architecture-guard/frontend-platform-guard.mjs
```
