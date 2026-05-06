# 后端规则

适用范围：`sourcelin-gateway`、`sourcelin-auth`、`sourcelin-modules/*`、`sourcelin-common/*`、`sourcelin-api/*`。

## 1. 模块边界

- MUST：公共能力放在 `sourcelin-common/*`，业务逻辑放在 `sourcelin-modules/*`。
- MUST：Feign 接口和跨服务 DTO 放在 `sourcelin-api/*`。
- MUST：WebMVC 响应包装和通用 Web 异常处理放在 `sourcelin-common/sourcelin-common-webmvc`。
- MUST：安全认证、Sa-Token、安全异常处理放在 `sourcelin-common/sourcelin-common-security`。
- MUST NOT：让网关为了通用 Web 异常处理依赖 `common-security`。
- MUST NOT：在 `sourcelin-common` 写入博客、系统、文件等业务规则。
- CHECK：执行 `node scripts/architecture-guard/backend-guard.mjs`。

## 2. 分层目录

- MUST：Controller 放在 `src/main/java/**/controller/**`。
- MUST：Service 接口和实现放在 `src/main/java/**/service/**`。
- MUST：Mapper 接口放在 `src/main/java/**/mapper/**`。
- MUST：Mapper XML 放在 `src/main/resources/mapper/**`。
- MUST：DTO 放在 `domain/dto` 或当前模块既有 DTO 目录。
- MUST：VO 放在 `domain/vo` 或当前模块既有 VO 目录。
- MUST：博客模块横向支撑放在 `com.sourcelin.blog.shared/**`。
- MUST NOT：Controller 直接写 SQL、缓存细节、文件存储细节或远程调用编排。
- CHECK：代码评审时按目录定位职责；涉及博客支撑类时执行 `node scripts/architecture-guard/backend-guard.mjs`。

## 3. Controller

- MUST：Controller 只做参数接收、权限注解、校验注解和 Service 调用。
- MUST：对外接口遵守 `rules/api-contract.md`。
- MUST：下载、导出、SSE 使用明确的流式返回类型。
- MUST NOT：Controller 手工包装 `ApiResponse`。
- MUST NOT：Controller 返回 `AjaxResult`、`TableDataInfo`、`Map<String, Object>` 或对外 `R<T>`。
- CHECK：执行 `node scripts/api-contract-scan.mjs`。

## 4. Service 与业务异常

- MUST：Service 负责业务规则、事务边界、状态流转和业务异常。
- MUST：可预期业务失败抛 `BusinessException`。
- MUST：创建或更新时间按现有项目方式使用 `DateUtils.getNowDate()`。
- MUST：查询遵守既有 `deleted` / `status` 软删除和状态字段。
- MUST NOT：Service 返回前端响应体或 Web 层对象。
- CHECK：后端改动至少执行对应模块 `mvn test -pl <module> -am` 或 `mvn compile -pl <module> -am`。

## 5. Mapper 与 SQL

- MUST：MyBatis XML 与 Mapper 接口一一对应。
- MUST：数值字段 `<if test>` 只判断 `!= null`。
- MUST：字符串字段 `<if test>` 可判断 `!= null and != ''`。
- MUST NOT：数值字段与空字符串比较。
- MUST NOT：SQL 直接拼接未校验的排序字段。
- CHECK：审查 Mapper XML；涉及分页排序时确认只允许白名单字段。

## 6. 公共能力

- MUST：认证、日志、限流、缓存、文件上传、消息通知优先沉淀为公共能力或模块支撑类。
- MUST：公共能力暴露稳定接口，不反向依赖业务模块。
- MUST NOT：在业务 Controller 内重复实现通用鉴权、通用日志、通用异常映射。
- CHECK：重复出现 2 次以上的横切逻辑必须抽象或登记为治理项。
