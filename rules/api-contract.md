# API 契约规则

适用范围：`sourcelin-auth`、`sourcelin-gateway`、`sourcelin-modules/*` 对外 HTTP JSON 接口，以及 `sourcelin-ui/sourcelin-ui-platform`、`sourcelin-ui/sourcelin-ui-admin` 的接口消费代码。

## 1. 统一响应体

- MUST：对外 REST JSON 成功响应统一为 `ApiResponse<T>` 顶层结构。
- MUST：顶层字段固定为 `code`、`message`、`data`、`requestId`、`timestamp`。
- MUST：成功码固定为 `0`。
- MUST：Controller 返回业务对象、`ListResult<T>`、`PageResult<T>`、`IdResponse` 或 `Void`，由 `ApiResponseBodyAdvice` 统一包装。
- MUST NOT：Controller 手工创建或返回 `ApiResponse`。
- MUST NOT：对外 Controller 返回 `AjaxResult`、`TableDataInfo`、`Map<String, Object>` 或 `R<T>`。
- CHECK：审查对外 Controller 返回类型，并执行受影响模块编译或单测。

## 2. 分页契约

- MUST：分页请求参数统一为 `page`、`pageSize`、`sortBy`、`sortOrder`。
- MUST：分页返回统一为 `PageResult<T>`。
- MUST：分页字段固定为 `items`、`total`、`page`、`pageSize`、`totalPages`。
- MUST NOT：新增 `rows`、`records`、`list`、`current`、`size`、`pages`、`pageNum`、`limit`。
- MUST NOT：前端使用 `items ?? list`、`items ?? rows`、`items ?? records` 兼容旧字段。
- CHECK：审查后端分页 VO、前端分页类型和页面消费字段。

## 3. 错误码与异常体

- MUST：错误码以 `ResultCode` 为唯一真源。
- MUST：业务失败抛 `BusinessException`。
- MUST：参数校验、未登录、无权限、资源不存在、系统异常由全局异常处理输出统一错误体。
- MUST：`GlobalMvcExceptionHandler` 位于 `sourcelin-common/sourcelin-common-webmvc`。
- MUST：安全异常处理只放在 `sourcelin-common/sourcelin-common-security` 的安全边界内。
- MUST NOT：业务代码捕获异常后返回错误字符串、裸 `Map` 或旧响应结构。
- CHECK：审查异常处理类所在模块和错误响应字段。

## 4. 协议例外

- MUST：文件下载、文件导出、二进制流、SSE 可跳过成功 JSON 包装。
- MUST：协议例外接口失败时仍返回标准 `ApiResponse` 错误体。
- MUST：例外接口必须在 Controller 返回类型上体现为 `ResponseEntity`、`Resource`、`StreamingResponseBody` 或同类流式类型。
- MUST NOT：将普通业务查询伪装成协议例外。
- CHECK：审查 Controller 返回类型和调用场景。

## 5. 服务间协议

- MUST：Feign 内部调用可使用 `R<T>`。
- MUST：内部 `R<T>` 必须处于 `@InnerAuth`、`controller/internal` 或明确内部边界。
- MUST NOT：前端可访问的 `/front`、`/blog`、`/system` REST 接口直接暴露 `R<T>`。
- CHECK：审查 Controller 路径、权限边界和返回类型。

## 6. 前端消费

- MUST：前端页面只消费 request 层解包后的业务数据。
- MUST：列表页只读取 `items`。
- MUST：分页页只读取 `items`、`total`、`page`、`pageSize`、`totalPages`。
- MUST NOT：页面判断 `code === 200`。
- MUST NOT：页面读取 `msg`、`rows`、`records`、`list`、`pageNum`、`limit`。
- CHECK：执行对应前端类型检查，并审查 API 类型定义和页面消费字段。
