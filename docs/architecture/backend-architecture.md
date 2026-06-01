# 后端架构说明

## 当前状态

后端采用 Spring Cloud Alibaba + Spring Boot，按网关、认证中心、业务服务和公共模块拆分。当前统一响应体由 WebMVC 公共能力处理，安全模块只保留安全相关异常处理，避免网关依赖 `common-security`。

关键边界：

```text
sourcelin-common/
  sourcelin-common-core      # 核心类型、分页、响应 DTO
  sourcelin-common-webmvc    # WebMVC advice、全局 Web 异常处理
  sourcelin-common-security  # Sa-Token、安全配置、安全异常
sourcelin-modules/
  sourcelin-blog             # 博客业务
  sourcelin-system           # 系统管理
sourcelin-gateway            # 网关
sourcelin-auth               # 登录认证
```

## 响应与异常边界

- 对外 HTTP JSON 接口统一返回业务对象、`ListResult`、`PageResult`、`IdResponse` 或 `Void`。
- `ApiResponseBodyAdvice` 负责统一成功响应包装。
- `GlobalMvcExceptionHandler` 位于 `common-webmvc`，处理 Web 公共异常。
- `SecurityExceptionHandler` 位于 `common-security`，只处理安全相关异常。
- Feign 内部接口可保留 `R<T>`，但必须使用 `@InnerAuth` 或 internal controller 边界。

## 模块化演进

短期保持现有技术栈，按新增代码渐进沉淀：

```text
blog/
  controller/front
  controller/admin
  dto
  vo
  service
  mapper
  shared/support
  shared/validation
```

后续可按领域拆分到 `article/comment/interaction/message/moderation/file/auth`，但不做一次性大搬迁。

## 博客管理端闭环

- 内容举报已形成“小程序/前台举报入库 -> 博客管理端列表查询 -> 详情查看 -> 状态处理/删除”的闭环。
- 管理端路径位于 `/admin/report/**`，经网关暴露为 `/blog/admin/report/**`。
- Controller 返回仍遵守统一契约：列表返回 `PageResult<T>`，详情返回业务对象，处理和删除返回 `Void`。

## 验证命令

```bash
node scripts/api-contract-scan.mjs
node scripts/architecture-guard/backend-guard.mjs
mvn test -pl sourcelin-common/sourcelin-common-core -am
```
