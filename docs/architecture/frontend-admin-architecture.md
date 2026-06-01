# 管理后台架构说明

## 当前状态

管理后台位于 `sourcelin-ui/sourcelin-ui-admin`，采用 Vue 3 + Vite + TypeScript + Element Plus。业务页面主线保留现有 `src/views/**` 和 `src/api/**` 目录，不引入新框架。

已完成的第四阶段拆分：

- `components/CURD/PageContent.vue`：拆出 `parts/PageToolbar.vue`、`parts/PageTableContainer.vue`、`parts/PagePagination.vue`。
- `views/blog/shared/ModuleListShell.vue`：拆出查询表单、表格区、操作列和共享类型。
- `views/dashboard/index.vue`：拆出 Hero、指标、趋势、最近访问组件，以及 `useDashboard.ts`。
- `views/blog/report/index.vue`：新增内容举报管理页，复用博客管理共享列表壳，承接小程序和前台举报入库后的处理闭环。

## 目录边界

```text
src/api/              # 后台 API 封装
src/types/api/        # 后台 API 类型
src/components/       # 跨业务复用组件
src/views/<domain>/   # 页面与领域组件
src/views/blog/shared # 博客管理模块共享壳和类型
```

## 强制规则

- 不改变现有接口调用方式和权限逻辑。
- 表格、查询、分页、操作列优先复用共享壳组件。
- 页面组件只做装配，复杂状态进入 composables。
- 管理后台统一通过 `src/utils/request.ts` 和 `src/api/**` 调用接口。
- 分页只消费统一 `PageResult` 字段。
- 内容举报管理权限固定为 `blog:report:list/query/edit/remove`，状态处理走 `/blog/admin/report/{id}/status`。

## 验证命令

```bash
pnpm run type-check
pnpm run lint
node ../../scripts/architecture-guard/frontend-admin-guard.mjs
```
