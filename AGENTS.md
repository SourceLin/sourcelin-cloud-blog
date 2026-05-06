# AGENTS.md

本文件是 Sourcelin Cloud Blog 仓库级 AI 执行入口。所有 AI 编程工具进入本仓库后必须先读取本文件，再按任务读取对应 `rules/` 和 `skills/`。

默认使用中文进行沟通、注释、提交说明和文档更新。所有文本文件保存为 UTF-8 无 BOM。

## 1. 项目范围

Sourcelin Blog 是基于 Sourcelin-Cloud v3.6.3 的微服务博客平台。

后端：

```text
sourcelin-api/        # Feign 接口与跨服务 DTO
sourcelin-common/     # 公共能力，不放业务逻辑
sourcelin-gateway/    # 网关
sourcelin-auth/       # 认证中心
sourcelin-modules/    # 业务服务
  sourcelin-system/   # 系统管理
  sourcelin-blog/     # 博客业务
  sourcelin-file/     # 文件服务
  sourcelin-job/      # 定时任务
sourcelin-visual/     # 监控等可视化服务
```

前端：

```text
sourcelin-ui/sourcelin-ui-platform/    # 博客前台，Vue3 + TypeScript + Naive UI 抽象层
sourcelin-ui/sourcelin-ui-admin/       # 管理后台主线，Vue3 + TypeScript + Element Plus
sourcelin-ui/sourcelin-ui-admin-vue2/  # 旧版迁移对照，不承载新功能
```

## 2. 开源分支边界

- `scripts/**` 不进入开源分支提交。
- `docs/**` 可以进入开源分支，但 `docs/MARKET_ANALYSIS.md` 和 `docs/BUSINESS_MODEL.md` 不提交。
- 规则、技能和执行入口必须避免引用开源分支未提交的本地维护脚本。
- 数据库初始化文档和 SQL 维护在 `docs/sql/**`。

## 3. 仓库发布模型

- `origin/master`：公开发布分支，只承载对外可见内容。
- `private/develop`：内部开发主分支，承载日常开发、内部脚本、过程文档和规则演进。
- `private/master`：私有同步与备份分支，用于保留一份与内部开发主线对齐的稳定快照。
- 对外发布必须从本地 `master` 推送到 `origin/master`。
- 内部开发默认在本地 `develop` 进行，并推送到 `private/develop`。
- 私有备份或私有主线同步推送到 `private/master`。
- MUST NOT：将 `develop`、内部脚本或过程性资料直接推送到公开仓库。

## 4. 默认规则

每次开发、重构、审查或测试必须读取：

- `rules/README.md`
- `rules/api-contract.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`

按任务追加读取：

- 后端：`rules/backend.md`
- 博客前台：`rules/frontend-platform.md`，并联查 `sourcelin-ui/sourcelin-ui-platform/AGENTS.md`
- 管理后台：`rules/frontend-admin.md`

规则优先级：

1. 本文件
2. 更近目录的 `AGENTS.md` / `rules/`
3. `rules/api-contract.md`
4. 领域规则
5. `rules/coding-conventions.md`
6. `rules/testing-and-validation.md`

## 5. 可用技能

- `skills/frontend-platform-dev/SKILL.md`：博客前台开发。
- `skills/frontend-admin-dev/SKILL.md`：管理后台开发。
- `skills/backend-dev/SKILL.md`：后端开发。
- `skills/api-contract-governance/SKILL.md`：API 契约治理。
- `skills/architecture-refactor/SKILL.md`：架构重构、目录整理、规则和文档同步。

使用规则：

- 前台任务使用 `frontend-platform-dev`。
- 后台任务使用 `frontend-admin-dev`。
- 后端任务使用 `backend-dev`。
- 任何接口响应、分页、错误码、前后端 API 消费任务必须叠加 `api-contract-governance`。
- 跨模块重构、页面拆分、规则体系改造必须使用 `architecture-refactor`。

## 6. API 契约硬规则

- MUST：对外 HTTP JSON 接口统一为 `ApiResponse<T>` 顶层结构。
- MUST：顶层字段为 `code/message/data/requestId/timestamp`，成功码为 `0`。
- MUST：分页统一为 `PageResult<T>`，字段为 `items/total/page/pageSize/totalPages`。
- MUST：Controller 返回业务对象、`ListResult<T>`、`PageResult<T>`、`IdResponse` 或 `Void`，由 `ApiResponseBodyAdvice` 包装。
- MUST NOT：对外 Controller 返回 `AjaxResult`、`TableDataInfo`、`Map<String,Object>` 或 `R<T>`。
- MUST NOT：前端消费 `msg/rows/records/list/pageNum/limit` 或判断 `code === 200`。
- CHECK：审查 Controller 返回类型、前端 request 消费和分页字段；涉及接口改动时执行对应后端编译、单测和前端类型检查。

## 7. 后端执行流程

1. 读取 `rules/backend.md` 和 `skills/backend-dev/SKILL.md`。
2. 定位模块边界：公共能力进 `sourcelin-common/*`，业务逻辑进 `sourcelin-modules/*`，Feign 进 `sourcelin-api/*`。
3. Controller 只接收参数、校验、权限注解和调用 Service。
4. Service 处理业务规则、事务、状态流转和 `BusinessException`。
5. Mapper XML 放在 `src/main/resources/mapper/**`。
6. 按影响范围执行 Maven 验证。

Maven 命令使用当前环境可用的 `mvn`。如本机需要自定义 `settings.xml`，由本地终端环境或临时命令参数提供，不写入项目文档和规则。

```powershell
mvn <goal>
```

## 8. 博客前台执行流程

1. 读取 `rules/frontend-platform.md` 和 `skills/frontend-platform-dev/SKILL.md`。
2. 页面放 `src/modules/**/pages/*.vue`。
3. 组件放 `src/modules/**/components/*.vue`。
4. 请求、分页、筛选、提交和复杂状态放 `src/modules/**/composables/*.ts`。
5. API 放 `src/modules/**/api/*.api.ts` 或 `src/shared/api/*.api.ts`。
6. 业务组件不得直连 Naive UI，必须使用 `S*` UI 抽象或业务成品组件。
7. 按改动范围执行 `npm run typecheck`、`npm run style:guard`、`npm run test:architecture`。

## 9. 管理后台执行流程

1. 读取 `rules/frontend-admin.md` 和 `skills/frontend-admin-dev/SKILL.md`。
2. 新功能只落在 `sourcelin-ui/sourcelin-ui-admin`。
3. 页面放 `src/views/**`。
4. API 放 `src/api/**`，统一使用 `src/utils/request.ts`。
5. 表格、查询、分页、弹窗、操作列优先复用共享壳。
6. 不改变现有权限逻辑，除非任务明确要求。
7. 按改动范围执行 `pnpm run type-check`、`pnpm run lint`。

## 10. 架构重构流程

1. 读取 `skills/architecture-refactor/SKILL.md`。
2. 明确本轮只处理一个可验证边界。
3. 先确认验证命令，再修改文件。
4. 每个子模块完成后立即验证。
5. 同步更新 `docs/architecture/**`、`rules/**`、`skills/**`、`AGENTS.md`；数据库初始化变化同步 `docs/sql/**`。
6. 使用当前仓库存在的 Maven、npm、pnpm 命令完成验证，不依赖未提交的本地脚本。

## 11. 审查流程

审查默认只输出问题，不继续开发。审查时必须检查：

- API 契约是否违反 `rules/api-contract.md`。
- 目录边界是否违反领域规则。
- 类型是否新增 `any` 或旧响应字段。
- 测试是否覆盖改动范围。
- 文档、规则、技能是否与真实目录一致。

## 12. 常用命令

后端：

```powershell
mvn test
mvn compile -pl sourcelin-modules/sourcelin-blog -am
```

博客前台：

```powershell
cd sourcelin-ui\sourcelin-ui-platform
npm run typecheck
npm run style:guard
npm run test:architecture
```

管理后台：

```powershell
cd sourcelin-ui\sourcelin-ui-admin
pnpm run type-check
pnpm run lint
```

SQL 初始化验证：

```powershell
mysql -u root -p < docs/sql/sourcelin-cloud.sql
```

## 13. 服务启动顺序

1. MySQL
2. Redis
3. Nacos
4. `sourcelin-gateway`，端口 8080
5. `sourcelin-auth`，端口 9200
6. `sourcelin-system`，端口 9201
7. `sourcelin-blog`，端口 9204
8. `sourcelin-file`，端口 9300，可选
9. `sourcelin-visual-monitor`，端口 9100，可选

## 14. 禁止事项

- MUST NOT：提交真实账号、密码、token、cookie、私钥。
- MUST NOT：提交 `target/`、`dist/`、`.tmp_*`、`*.class`、`*.zip`、`*.tgz`。
- MUST NOT：开源分支提交 `scripts/**`、`docs/MARKET_ANALYSIS.md` 或 `docs/BUSINESS_MODEL.md`。
- MUST NOT：回退用户已有无关改动。
- MUST NOT：通过删除功能、降低测试或放宽扫描规则掩盖失败。
- MUST NOT：引入重型框架替代当前技术栈。
