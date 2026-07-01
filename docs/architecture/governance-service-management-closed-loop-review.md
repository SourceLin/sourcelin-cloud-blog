# 治理与服务管理闭环检查报告

检查日期：2026-06-30

## 1. 结论

当前项目已经具备较好的工程治理基础，但闭环尚未完全成立。

已经闭合的部分：

- 仓库级入口已建立：`AGENTS.md` 明确了模块边界、发布模型、开源边界、服务启动顺序和默认规则读取顺序。
- 规则体系已建立：`rules/` 覆盖 API 契约、后端、三端前端、编码规范和验证要求。
- 技能体系已建立：`skills/` 将后端、前台、移动端、后台、API 契约和架构重构流程拆成可执行入口。
- 服务治理基座已存在：Nacos 注册配置、Gateway 路由、Sa-Token 鉴权、Sentinel、Spring Boot Admin、日志文件和 Compose 示例均有落点。
- API 契约主线已落地：WebMVC 业务接口通过 `ApiResponseBodyAdvice` 收口，业务 Controller 返回类型有明确规则。

尚未闭合的部分：

- 规则、文档、实际目录之间存在冲突，部分 CHECK 当前不可执行。
- 开源边界与实际文件树不一致，`docs/internal/**`、`scripts/**`、证书文件和推广素材仍在当前工作区内。
- 网关配置、Nginx 文档、服务代码之间存在路由不一致，尤其博客服务路由未在公开 Nacos 初始化配置中显式闭合。
- 网关 WebFlux 异常和限流响应仍通过旧 `R` 结构写出，与统一 `ApiResponse<T>` 顶层契约不一致。
- 监控端点暴露策略偏粗，当前依赖 Nginx 层屏蔽 `/actuator`，服务自身未形成最小暴露闭环。
- 部署、升级、冒烟和回滚文档仍偏示例化，没有形成从变更到验证再到回滚的发布闭环。

总体判断：项目处于“治理框架已建立、局部闭环成立、仓库级治理与服务管理仍需收口”的阶段。建议先处理阻断类问题，再做服务可观测、发布验证和运维文档增强。

## 2. 检查范围

本次检查基于静态阅读和规则核对，覆盖：

- 仓库入口：`AGENTS.md`
- 规则与技能：`rules/**`、`skills/**`
- 架构与部署文档：`docs/README.md`、`docs/DOCS_INDEX.md`、`docs/architecture/**`、`docs/deployment/**`、`docs/guides/QUICK_START.md`
- 服务配置：各后端模块 `bootstrap.yml`、`docs/sql/sourcelin-config.sql`、`docker-compose.example.yml`、`.env.example`
- 关键代码：网关鉴权、网关异常、响应包装、Spring Boot Admin 配置

本次未启动完整服务集群，未执行真实 HTTP 冒烟。Git 状态检查受本机 `safe.directory` 保护阻塞，未修改全局 Git 配置。

## 3. 治理闭环检查

### 3.1 已形成闭环的治理能力

| 能力 | 当前证据 | 闭环状态 |
|------|----------|----------|
| AI 执行入口 | `AGENTS.md` 定义规则读取顺序、模块边界、发布模型 | 基本闭合 |
| API 契约 | `rules/api-contract.md`、`docs/architecture/api-contract.md`、`ApiResponseBodyAdvice` | WebMVC 主链路闭合 |
| 模块边界 | `rules/backend.md`、`rules/frontend-*.md`、各模块目录 | 基本闭合 |
| 验证要求 | `rules/testing-and-validation.md` 定义 Maven、npm、pnpm 验证 | 规则层闭合，执行层不完全 |
| 发布模型 | `AGENTS.md` 与 `rules/README.md` 定义 `develop -> master -> origin/master` | 规则层闭合 |

### 3.2 高优先级治理缺口

| 编号 | 问题 | 影响 | 建议 |
|------|------|------|------|
| G-01 | `rules/testing-and-validation.md` 要求 `git ls-files docs` 时只允许 `docs/sql/**`，但 `AGENTS.md` 又允许 `docs/**` 进入开源分支，且仓库已有大量公开文档 | 规则互相冲突，导致 CHECK 不可执行 | 将检查改为只阻断 `docs/internal/**` 和明确禁止的商业、密钥、过程文档 |
| G-02 | 多处长期文档引用 `scripts/api-contract-scan.mjs`、`scripts/blog-api-smoke.mjs` 等脚本，但开源边界要求 `scripts/**` 不进入公开分支 | 公开分支用户无法执行文档中的验证命令 | 将公开验证脚本迁移到可发布目录，或将文档改为 Maven/npm/pnpm 原生命令和手工检查清单 |
| G-03 | `docs/guides/QUICK_START.md`、`docs/deployment/NGINX_CONFIG.md` 引用 `docs/internal/**` | 公开文档指向不可发布资料，开源边界破口 | 提炼公开版配置基线到 `docs/configs/**` 或 `docs/deployment/**`，删除公开文档中的 internal 链接 |
| G-04 | 当前工作区存在 `docs/internal/configs/ngnix-https证书/sourcelin.cn.key`、`.pem` | 证书私钥进入仓库风险极高 | 私钥证书必须移出仓库，加入忽略规则，并检查历史提交与公开分支 |
| G-05 | `docs/DOCS_INDEX.md` 未覆盖 `docs/promotion/**`、`docs/configs/**` 下的实际长期资料，且 `docs/README.md` 对文档分组描述与实际目录有偏差 | 文档治理不可发现，长期资料和过程资料边界模糊 | 对 `docs/` 做一次公开资料盘点，明确“长期实现依据、推广素材、内部资料、临时产物”的保留策略 |
| G-06 | `docs/screenshot.zip` 属于压缩产物，规则禁止提交 `*.zip` | 产物治理规则未闭环 | 删除或移出压缩包，用目录图片作为公开展示资源 |

## 4. 服务管理闭环检查

### 4.1 已形成闭环的服务管理能力

| 能力 | 当前证据 | 闭环状态 |
|------|----------|----------|
| 服务注册 | 各服务 `bootstrap.yml` 配置 Nacos discovery | 基本闭合 |
| 配置中心 | 各服务共享 `application-dev.yml`，服务级配置进入 `docs/sql/sourcelin-config.sql` | 基本闭合 |
| 网关入口 | `sourcelin-gateway`、Nginx 文档、Sa-Token Reactor 鉴权 | 部分闭合 |
| 服务监控 | `sourcelin-visual/sourcelin-monitor` 启用 Spring Boot Admin | 部分闭合 |
| 容器示例 | `docker-compose.example.yml` 覆盖 Redis、Nacos、核心后端、文件、监控 | 部分闭合 |
| 服务启动顺序 | `AGENTS.md`、`README.md`、`QUICK_START.md` 均描述基础依赖和后端顺序 | 基本闭合 |

### 4.2 高优先级服务缺口

| 编号 | 问题 | 影响 | 建议 |
|------|------|------|------|
| S-01 | `docs/sql/sourcelin-config.sql` 的网关路由包含 `auth/system/job/file`，未显式包含 `sourcelin-blog` 的 `/blog/**` 路由 | 博客服务可能注册成功但经网关不可达，与 Nginx `/blog-api/blog/front/**` 文档不一致 | 在 Nacos 初始化配置中补齐 `Path=/blog/** -> lb://sourcelin-blog -> StripPrefix=1`，同步验证前台与后台博客接口 |
| S-02 | 网关 `ServletUtils.webFluxResponseWriter` 返回 `R.fail(...)`，`GatewayExceptionHandler` 和 `SentinelFallbackHandler` 依赖该方法 | 网关异常、未登录、限流不符合 `ApiResponse<T>` 顶层结构 | 新增 WebFlux 专用 `ApiResponse` 写出方法，字段固定为 `code/message/data/requestId/timestamp` |
| S-03 | `application-dev.yml` 暴露 `management.endpoints.web.exposure.include: '*'`，Nginx 文档再屏蔽 `/actuator` | 安全边界依赖外层代理，服务直连时监控端点过度暴露 | 服务配置改为最小集合，例如 `health,info,metrics`；敏感端点只在内网或监控账号下开放 |
| S-04 | `docker-compose.example.yml` 未包含 `sourcelin-job`，但 `AGENTS.md`、`README.md`、`QUICK_START.md` 和 Nacos 配置均包含定时任务服务 | 部署示例与服务清单不一致 | 明确 `job` 是否为公开部署必选。若保留，Compose 增加 job；若可选，文档标明缺省不启动的影响 |
| S-05 | `README.md` 中 jar 名示例为 `sourcelin-modules-blog.jar`、`sourcelin-modules-file.jar`，`QUICK_START.md` 中为 `sourcelin-blog.jar`、`sourcelin-file.jar` | 新用户按文档启动可能找不到 jar | 以 Maven 实际打包结果为准统一所有启动文档 |
| S-06 | Nacos 初始化 SQL 中仍保留 `username: sourcelin`、`password: change_me`、MinIO `accessKey/secretKey: change_me` | 示例占位可接受，但与环境变量化策略不一致，容易被照抄到生产 | 所有公开 SQL 配置改为 `${ENV:placeholder}` 风格，并在 `.env.example` 给出统一占位 |
| S-07 | Spring Boot Admin 监控服务允许 `/instances/**`、`/actuator/**` 匿名访问 | 监控注册与端点访问边界过宽 | 区分客户端注册接口、UI 静态资源和敏感查询接口，避免整段匿名放开 |
| S-08 | Sentinel 在 Compose 启动参数中被禁用：`-Dspring.cloud.sentinel.enabled=false`、`-Dfeign.sentinel.enabled=false` | 公开部署示例与“Sentinel 流控治理”能力描述不一致 | 文档明确轻量部署默认关闭 Sentinel；另给完整治理模式的开启条件、控制台地址和验证方式 |

## 5. API 契约与前后端消费检查

当前 WebMVC 主链路已经向统一契约收口，但仍有边缘问题：

| 编号 | 问题 | 影响 | 建议 |
|------|------|------|------|
| A-01 | 网关 WebFlux 响应使用旧 `R` 结构 | 未登录、服务未找到、限流等网关失败响应与前端统一 request 解包预期不一致 | 优先修复，纳入 API 契约扫描 |
| A-02 | `sourcelin-ui/sourcelin-ui-admin/src/components/CURD/PageContent.vue` 存在 `limitName: "pageSize"` 和动态请求字段机制 | 虽然默认是 `pageSize`，但仍保留旧 `limitName` 概念，容易被配置回 `limit` | 将共享壳参数命名收敛为 `pageSizeName` 或固定 `page/pageSize` |
| A-03 | 前台 `error-message.ts` 仍读取 `{ msg?: unknown }` | 对旧错误体仍有兼容入口 | 统一改为 `message`，如需兼容第三方异常必须明确隔离在适配层 |
| A-04 | 静态扫描脚本被文档引用但不满足开源边界 | 契约验证闭环依赖不可公开工具 | 将扫描能力公开化或改成项目内测试用例 |

## 6. 发布与运维闭环检查

当前发布模型有规则，但运维闭环不够硬：

| 环节 | 当前状态 | 缺口 | 建议 |
|------|----------|------|------|
| 变更进入 | 规则要求日常开发先进入 `develop` | Git 检查当前被 safe.directory 阻塞，无法确认本地分支状态 | 增加本地环境准备说明，或在 CI 中做分支与边界检查 |
| 构建 | README 提供 Maven/package 和前端构建命令 | 没有统一 release checklist | 新增 `docs/deployment/RELEASE_CHECKLIST.md` |
| 配置 | `.env.example` 与 Nacos SQL 均存在 | 配置真源分散在 `.env`、SQL、bootstrap、内部文档 | 建立“配置真源矩阵”，列出每个配置项来源、默认值、生产覆盖方式 |
| 发布 | Compose 示例可用 | 缺 job 服务策略、健康检查、滚动/回滚步骤 | Compose 增加 healthcheck 或文档化健康检查 |
| 验证 | 文档有人工验证顺序 | 缺少公开可执行冒烟清单 | 新增公开 HTTP 冒烟清单，覆盖登录、首页、后台、文件、监控 |
| 回滚 | `UPGRADE.md` 为占位文档 | 未定义数据库、Nacos 配置和 jar 回滚顺序 | 将占位文档升级为版本发布/回滚流程 |

## 7. 优化调整路线图

### P0：先消除治理阻断

1. 移出 `docs/internal/**` 中的私钥证书，检查是否进入 Git 历史或公开分支。
2. 修正 `rules/testing-and-validation.md` 中与 `docs/**` 公开策略冲突的 CHECK。
3. 删除公开文档中的 `docs/internal/**` 链接，提炼公开配置基线。
4. 处理 `docs/screenshot.zip` 等压缩产物。
5. 明确 `scripts/**` 的定位：内部维护脚本不被公开文档引用；公开验证脚本迁入可发布目录或转为测试。

### P1：闭合网关与 API 契约

1. `docs/sql/sourcelin-config.sql` 增加博客服务网关路由。
2. 网关异常、未登录、限流统一输出 `ApiResponse<T>`。
3. 将 WebFlux 网关响应纳入契约测试。
4. 检查 `/code`、`/auth/**`、`/blog/**`、`/system/**`、`/file/**`、`/schedule/**` 的 Nginx、Gateway、Sa-Token 三方映射一致性。

### P2：闭合服务管理

1. 统一服务清单：gateway、auth、system、blog、file、job、monitor 的必选/可选、端口、依赖、健康检查。
2. 同步 Compose、README、QUICK_START、AGENTS 的启动顺序和 jar 名。
3. 收紧 actuator 暴露范围，明确 Spring Boot Admin 客户端与服务端访问边界。
4. 为轻量部署和完整治理部署分别说明 Sentinel、Seata、Monitor 的启用策略。

### P3：闭合发布运维

1. 新增公开发布检查清单：分支、开源边界、构建、SQL、配置、冒烟、回滚。
2. 将 `UPGRADE.md` 从占位升级为真实升级流程。
3. 建立配置真源矩阵，避免 `.env.example`、bootstrap、Nacos SQL、内部文档互相漂移。
4. 建立服务运行手册，记录启动顺序、端口、依赖、日志路径、健康检查和常见故障。

## 8. 建议新增或调整的文档

| 文档 | 动作 | 目的 |
|------|------|------|
| `docs/architecture/governance-service-management-closed-loop-review.md` | 新增 | 保存本次治理与服务管理体检结论 |
| `docs/deployment/RELEASE_CHECKLIST.md` | 新增 | 发布前闭环检查 |
| `docs/deployment/SERVICE_RUNBOOK.md` | 新增 | 服务启动、健康检查、日志和故障定位 |
| `docs/configs/CONFIG_MATRIX.md` | 新增 | 配置真源矩阵 |
| `docs/deployment/UPGRADE.md` | 更新 | 从占位升级为真实升级/回滚流程 |
| `docs/guides/QUICK_START.md` | 更新 | 移除 internal 链接，统一 jar 名和服务清单 |
| `docs/deployment/NGINX_CONFIG.md` | 更新 | 移除 internal 链接，补齐 Gateway 路由一致性说明 |
| `rules/testing-and-validation.md` | 更新 | 修正不可执行的 docs 检查规则 |

## 9. 验收标准

完成优化后，建议用以下标准判断闭环是否成立：

1. `AGENTS.md`、`rules/**`、`skills/**`、`docs/**` 对同一边界的描述一致。
2. 公开文档不引用 `docs/internal/**` 或 `scripts/**` 中不会进入公开分支的资源。
3. 网关所有失败响应均为 `ApiResponse<T>` 顶层结构。
4. Nginx、Gateway、Sa-Token 的路径矩阵一致，博客、后台、认证、文件、定时任务均可解释。
5. Compose 示例与服务清单一致，必选服务可一套命令启动，可选服务有明确说明。
6. actuator 只暴露必要端点，监控服务访问边界明确。
7. 发布前有可执行清单，覆盖开源边界、构建、SQL、配置、HTTP 冒烟和回滚。
8. 所有验证命令均可在公开仓库中找到对应文件或明确说明人工执行方式。

