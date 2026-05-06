# 规则总览

本目录是仓库级 AI 编程规则的唯一入口。规则必须可执行，所有长期规则统一采用 `MUST / MUST NOT / CHECK` 结构。

## 规则文件

- `api-contract.md`：对外 REST 响应体、分页、错误码、前端消费和旧协议禁用规则。
- `backend.md`：后端模块、Controller、Service、Mapper、DTO/VO、异常和公共能力规则。
- `frontend-platform.md`：博客前台 Vue3 + TypeScript + Naive UI 抽象层规则。
- `frontend-admin.md`：管理后台 Vue3 + TypeScript + Element Plus 规则。
- `coding-conventions.md`：命名、编码、注释、安全配置、产物管理等跨端通用规则。
- `testing-and-validation.md`：后端、前台、后台、API 契约和规则变更验证要求。

## 执行优先级

1. 仓库根 `AGENTS.md`
2. 更近目录的 `AGENTS.md` / `rules/`
3. `rules/api-contract.md`
4. 领域规则：`backend.md` / `frontend-platform.md` / `frontend-admin.md`
5. `coding-conventions.md`
6. `testing-and-validation.md`

## 使用方式

- 后端开发：读取 `api-contract.md`、`backend.md`、`coding-conventions.md`、`testing-and-validation.md`。
- 博客前台开发：读取 `api-contract.md`、`frontend-platform.md`、`coding-conventions.md`、`testing-and-validation.md`，并联查 `sourcelin-ui/sourcelin-ui-platform/AGENTS.md`。
- 管理后台开发：读取 `api-contract.md`、`frontend-admin.md`、`coding-conventions.md`、`testing-and-validation.md`。
- API 契约治理：读取 `api-contract.md` 和 `testing-and-validation.md`。
- 架构重构：读取全部规则，按影响范围执行 Maven、npm 或 pnpm 验证。

## 开源分支边界

- MUST：开源分支可以提交项目文档，但不得提交市场分析和商业模式文档。
- MUST：规则和技能不得引用开源分支不提交的本地维护脚本。
- MUST NOT：提交 `scripts/**`。
- MUST NOT：提交 `docs/MARKET_ANALYSIS.md` 或 `docs/BUSINESS_MODEL.md`。
- CHECK：执行 `git ls-files scripts docs/MARKET_ANALYSIS.md docs/BUSINESS_MODEL.md` 必须无输出。

## 仓库发布模型

- MUST：公开发布只通过 `origin/master` 进行。
- MUST：内部开发默认在本地 `develop` 上进行，并同步到 `private/develop`。
- MUST：私有备份或私有主线对齐只推送到 `private/master`。
- MUST NOT：将 `develop`、`scripts/**`、过程文档或仅供内部使用的资料直接推送到公开仓库。
- CHECK：执行 `git branch -vv`，必须满足本地 `master` 跟踪 `origin/master`，本地 `develop` 跟踪 `private/develop`。

## 维护规则

- MUST：规则与真实目录、技术栈保持一致。
- MUST：新增长期规则必须放入本目录六个文件之一。
- MUST：删除或替换旧规则时同步更新 `AGENTS.md`、`skills/README.md` 和相关技能。
- MUST NOT：新增第二份 API 契约、第二份前端设计系统规则或第二份测试清单。
- MUST NOT：使用“建议”“尽量”等不可验收表述作为硬性规则。
- CHECK：审查 `AGENTS.md`、`rules/**`、`skills/**` 的引用路径是否真实存在。
