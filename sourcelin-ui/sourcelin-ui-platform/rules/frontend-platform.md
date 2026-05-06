# 前端平台规则

适用范围：整个 `sourcelin-ui-platform` 仓库。

## 项目结构

- `src/app/**`
  - 应用入口、根组件、路由聚合与 route meta
- `src/modules/**`
  - 业务模块，按域组织页面、组件、API、页面逻辑和 `routes.ts`
- `src/shared/**`
  - 跨模块共享能力
- `src/stores/**`
  - 全局状态与兼容导出
- `src/assets/**`
  - 静态资源与样式系统

## 目录职责

- `src/modules/*/api/**`
  - 业务 API，统一调用 `src/shared/api/http.ts`
- `src/modules/*/pages/**`
  - 路由页面装配层
- `src/modules/*/components/**`
  - 模块业务组件
- `src/modules/*/composables/**`
  - 模块页面逻辑
- `src/shared/components/ui/**`
  - UI 抽象层
- `src/shared/components/layout/**`
  - 全局布局和页面壳
- `src/shared/components/feedback/**`
  - 搜索、空态、启动遮罩、弹层类共享组件
- `src/shared/composables/**`
  - 跨模块复用逻辑
- `src/shared/api/http.ts`
  - 请求基座和离散消息例外

## 请求与状态

- 所有业务 API 必须走 `src/shared/api/http.ts`
- `baseURL` 来自 `VITE_APP_BASE_API`
- `/front/**`、`/app/**` 会在 `http.ts` 中自动补上 `/blog` 上下文
- `/auth/**`、`/system/**`、`/code` 保持原路径
- 匿名接口显式传 `headers: { isToken: false }`
- 跨页面状态进入 `src/stores/**`
- 页面级状态优先留在模块 composable 内，不滥用 Pinia

## 路由与导航

- 路由由 `src/modules/*/routes.ts` 导出
- `src/app/router/index.ts` 统一聚合
- 导航从 route meta 推导，不再维护第二份导航配置
- 认证页通过 `meta.shell = 'auth'` 使用独立页面壳

推荐 route meta 字段：

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

## Store 口径

- 主线 store：
  - `src/stores/site.store.ts`
  - `src/stores/ui.store.ts`
  - `src/stores/theme.ts`
  - `src/stores/user.ts`
- 兼容导出：
  - `src/stores/theme.store.ts`
  - `src/stores/auth.store.ts`

## 加载态规范

- 首屏结构化加载统一使用 `SSkeleton` 与 `src/shared/components/feedback/skeletons/**`，业务层禁止直接使用 `n-skeleton`。
- `page-container-wrap` 只做内部区块骨架，不包整页大骨架。
- 列表首屏、统计卡、导航卡、搜索结果、树洞流等优先使用骨架；`load more`、按钮提交和已有内容后的分页切换保持轻 spinner 或 quiet loading。
- 加载容器需要带 `aria-busy="true"`，骨架本身保持 `aria-hidden="true"`。
- 所有骨架视觉继续走 CSS 变量，不得新增硬编码颜色。

## 最低验证

```bash
npm run typecheck
npm run build
npm run style:guard
npm run test:style-guard
npm run test:architecture
```
