# API 契约与模块治理规则

> 本文档记录当前阶段已经落地的统一响应体、分页、ID 响应、前端消费和后端模块化支撑规则。新增或改造接口必须优先遵守本文档与 `rules/api-contract.md`。

## 1. 对外响应体

所有对外 HTTP JSON 接口统一由 `ApiResponse<T>` 包装，Controller 不手工创建顶层响应体。

```json
{
  "code": 0,
  "message": "OK",
  "data": {},
  "requestId": "trace-id",
  "timestamp": "2026-04-29 23:00:00"
}
```

## 2. 业务返回类型

Controller 允许直接返回以下类型：

- 业务 VO / DTO
- `ListResult<T>`
- `PageResult<T>`
- `IdResponse`
- `Void`

下载、导出、二进制流、SSE 可作为协议例外，但失败时仍应输出标准错误体。

## 3. 分页契约

分页返回固定使用 `PageResult<T>`：

```json
{
  "items": [],
  "total": 0,
  "page": 1,
  "pageSize": 20,
  "totalPages": 0
}
```

后端分页结果创建优先使用模块内支撑类，避免 Controller 重复拼装分页元数据。

当前已落地：

- `com.sourcelin.blog.shared.support.BlogPageResults`

## 4. ID 响应契约

创建资源后如只需要返回 ID，后端统一返回 `IdResponse`：

```json
{
  "id": 1001
}
```

前端类型命名：

- 管理后台：`IdResponse`
- 博客前台：`IdResponse`，存量 `IdResult` 仅作为兼容别名

## 5. 前端消费规则

前端页面和业务组件只消费 request 层解包后的业务数据。

禁止新增读取：

- `msg`
- `rows`
- `records`
- `list`
- `pageNum`
- `limit`
- `code === 200`

分页页面只读取：

- `items`
- `total`
- `page`
- `pageSize`
- `totalPages`

## 6. 前台组件状态规则

博客前台加载态、空态、错误态必须优先复用共享组件，避免每个页面重复写状态样式。

当前优先组件：

- `src/shared/components/feedback/EmptyState.vue`
- `src/shared/components/feedback/InlineLoadingState.vue`
- `src/shared/components/feedback/skeletons/**`

## 7. 后端模块化规则

博客模块新增横向公共支撑代码放在：

```text
sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/shared/support
```

后续领域化代码按以下方向渐进迁移：

```text
article/
comment/
interaction/
message/
moderation/
shared/
```

现阶段不做一次性大搬迁，优先在新增或改造代码中建立边界。

## 8. 当前落地进度

当前状态：第二阶段 API 契约统一已闭环，包含静态扫描、单元/编译验证和登录态 HTTP 动态冒烟验证。

- 后端已补齐 `IdResponse`。
- 管理后台公共 `ApiResponse` 默认泛型已从 `any` 收紧为 `unknown`。
- 管理后台博客 API 基座 `src/api/blog/base.ts` 已移除 `Record<string, any>`。
- 博客前台共享类型已提供 `IdResponse`，`IdResult` 仅保留为存量兼容别名。
- 博客前台文章正文空态、评论空态已迁移到 `EmptyState`。
- 博客管理端 Controller 的直接 `PageResult.of(...)` 已迁移到 `BlogPageResults.of(...)`。
- 公共分页参数读取已支持 `sortBy/sortOrder`，并保留 `orderByColumn/isAsc` 旧参数兜底。
- 博客前台与博客管理端 Controller 的分页结果创建已统一收口到 `BlogPageResults`。
- 已新增 `scripts/api-contract-scan.mjs`，用于扫描 Controller、前端源码和脚本是否存在旧响应结构、手动 `ApiResponse`、对外 `R<T>`、旧分页参数、旧响应字段消费和旧分页 fallback。

## 9. 接口验收说明

第二阶段接口契约验收必须执行静态与动态两类验证。

静态验证：

```bash
node scripts/api-contract-scan.mjs
```

当前扫描范围：

- `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller`
- `sourcelin-modules/sourcelin-system/src/main/java/com/sourcelin/system/controller`
- `sourcelin-auth/src/main/java/com/sourcelin/auth/controller`
- `sourcelin-ui/sourcelin-ui-platform/src`
- `sourcelin-ui/sourcelin-ui-admin/src`
- `scripts`

阻断项：

- `AjaxResult` / `TableDataInfo` / `getDataTable()` / `toAjax()`
- Controller 直接返回或手动创建 `ApiResponse`
- 对外 Controller 暴露 `R<T>`
- `pageNum` / `limit` 旧分页参数
- Controller 返回 `Map<String,Object>`
- 前端或脚本读取 `data.rows` / `data.records` / `data.list` / `data.msg`
- 前端或脚本使用 `code === 200`
- 前端或脚本使用 `items ?? list`、`items ?? rows`、`items ?? records` 等旧分页 fallback

允许项：

- `@InnerAuth` 或 `controller/internal` 下的 Feign 内部 `R<T>`。
- 下载、导出、二进制流、SSE 等协议例外。

动态验证：

```bash
node scripts/blog-api-smoke.mjs
```

动态验证要求：

- 前台与后台登录成功，并获取 token。
- 无登录接口覆盖首页、分类、标签、文章列表。
- 登录后接口覆盖评论、点赞、收藏、前台用户信息、后台分页。
- 所有响应必须为 `code: 0`、`message: string`、包含 `data`。
- 分页响应必须包含 `items/total/page/pageSize/totalPages`，且分页元数据为 number。
- 禁止出现 `code: 200`、旧分页字段和旧响应字段。

本轮静态扫描结果：

- 扫描 Controller 文件：54
- 扫描 Mapping 注解：316
- 允许的内部 `R<T>` 方法：10
- 扫描前端文件：以脚本实时输出为准
- 扫描脚本文件：以脚本实时输出为准
- 阻断问题：0

本轮动态冒烟结果：

- 前台登录：通过
- 后台登录：通过
- 成功接口：10
- 失败接口：0
- 状态：已闭环
