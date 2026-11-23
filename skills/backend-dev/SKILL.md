---

name: backend-dev

description: 用于新增或修改 Sourcelin 后端 Controller、Service、Mapper、DTO/VO、异常处理、公共能力和微服务接口。

---



# 后端开发技能



## 使用场景



- 修改 `sourcelin-gateway`、`sourcelin-auth`、`sourcelin-modules/*`、`sourcelin-common/*`、`sourcelin-api/*`。

- 新增或改造 REST 接口、业务服务、Mapper XML、DTO/VO。

- 治理异常处理、公共能力、模块边界或 Feign 协议。



## 输入



- 需求说明或待修改接口。

- 当前 Controller、Service、Mapper、domain 代码。

- 数据库表结构或 SQL 脚本。



## 输出



- 符合后端分层和 API 契约的代码改动。

- 必要的 DTO/VO、Mapper XML、SQL 或文档同步。

- 已执行 Maven 验证命令和结果。



## 执行步骤



1. 读取 `AGENTS.md`、`rules/backend.md`、`rules/api-contract.md`、`rules/coding-conventions.md`、`rules/testing-and-validation.md`。

2. 定位模块边界：公共能力进 `sourcelin-common/*`，业务逻辑进 `sourcelin-modules/*`，Feign 进 `sourcelin-api/*`。

3. Controller 只接收参数、校验、权限注解和调用 Service。

4. Service 实现业务规则、事务、状态流转和 `BusinessException`。

5. Mapper XML 放在 `src/main/resources/mapper/**`，数值条件只判断 `!= null`。

6. 对外 REST 返回业务对象、`ListResult<T>`、`PageResult<T>`、`IdResponse` 或 `Void`。

7. 修改后按影响范围运行 Maven 编译或单测；涉及接口时同时审查 API 契约硬规则。



## 依赖 rules



- `rules/backend.md`

- `rules/api-contract.md`

- `rules/coding-conventions.md`

- `rules/testing-and-validation.md`



## 禁止行为



- 禁止对外 Controller 返回 `AjaxResult`、`TableDataInfo`、`Map<String,Object>`、`R<T>`。

- 禁止 Controller 手工包装 `ApiResponse`。

- 禁止业务代码返回错误字符串或裸 Map。

- 禁止网关因通用 Web 异常处理依赖 `common-security`。

- 禁止把业务逻辑写入 `sourcelin-common`。


## AI 与消息补充

处理可选 AI/消息模块时，读取 `docs/architecture/ai-message-architecture.md`；核实内部认证、身份域、资源归属、密钥配置、异步调度及 SSE 生命周期。部署与 SQL 步骤以 `docs/guides/AI_MESSAGE_SETUP.md` 和 `docs/sql/README.md` 为准。

## 升级验证

在仓库根目录先用 `mvn -version` 核对 JDK 27 与 Maven 3.9.x。升级后检查九个服务的配置导入与有效依赖，分别访问网关、MVC 服务和 SSE，保存实际响应及测试计数。
