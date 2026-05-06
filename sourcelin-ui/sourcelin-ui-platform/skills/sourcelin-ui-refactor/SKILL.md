---
name: sourcelin-ui-refactor
description: Use when modifying Sourcelin UI Platform modules, shared components, routing, style boundaries, architecture documents, rules, or skills.
---

# Sourcelin UI 重构技能

## 何时使用

- 编辑 `src/modules/**`
- 编辑 `src/shared/**`
- 编辑 `src/app/**`
- 编辑 `src/assets/styles/**`
- 调整 Naive 集成边界
- 调整架构文档、规则、技能或轻量入口

## 当前主线目录

```text
src/
  app/
  modules/
  shared/
  stores/
  assets/styles/{tokens,foundation,naive}
```

## 硬边界

- Naive UI 只允许出现在 `src/shared/components/ui/**`、`src/app/App.vue` 和 `src/shared/api/http.ts` 的显式例外中。
- 业务组件和页面不得新增 `n-*`、glass mixin、`:deep(.n-*)`、`.n-*` override 或硬编码颜色。
- 新的共享视觉值必须先进入 Token。
- 导航永远由 route meta 推导，不再维护第二份导航配置。
- `src/assets/styles/responsive-enhancements.scss` 当前为兼容层，不应继续扩张为新的主线基础设施。

## 推荐实现方向

- 页面负责装配
- 页面逻辑优先下沉到 `modules/*/composables`
- 跨模块复用进入 `shared/`
- 路由由模块 `routes.ts` 导出，并在 `app/router/index.ts` 聚合
- API 统一从 `modules/*/api/*.api.ts` 走 `shared/api/http.ts`
- 兼容导出优先保留在 `stores/*.store.ts`，真实实现优先收敛到更清晰的文件名

## 文档同步要求

当任务影响架构、边界、路由协议、请求约定或样式资产归属时，同时检查：

- `README.md`
- `AGENTS.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
- `rules/`
- `skills/`
- `.cursor/README.md`
- `.cursor/rules/project-rules.mdc`
- `.opencode/README.md`
- `.opencode/skills/`

## 最低验证

```bash
npm run typecheck
npm run build
npm run style:guard
npm run test:style-guard
npm run test:architecture
```

## 说明

- `reference.md`
- `README.md`
- `AGENTS.md`
- `docs/frontend-architecture.md`
- `rules/`
