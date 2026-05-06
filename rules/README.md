# 规则总览

本目录是仓库级 AI 编程规则的唯一入口。规则必须可执行，所有长期规则统一采用 `MUST / MUST NOT / CHECK` 结构。

## 规则文件

- `api-contract.md`：对外 REST 响应体、分页、错误码、前端消费和旧协议禁用规则。
- `backend.md`：后端模块、Controller、Service、Mapper、DTO/VO、异常和公共能力规则。
- `frontend-platform.md`：博客前台 Vue3 + TypeScript + Naive UI 抽象层规则。
- `frontend-admin.md`：管理后台 Vue3 + TypeScript + Element Plus 规则。
- `coding-conventions.md`：命名、编码、注释、安全配置、产物管理等跨端通用规则。
- `testing-and-validation.md`：后端、前台、后台、API 契约和架构守卫验证命令。

## 执行优先级

1. 仓库根 `AGENTS.md`
2. 更近目录的 `AGENTS.md` / `rules/`
3. `rules/api-contract.md`
4. 领域规则：`backend.md` / `frontend-platform.md` / `frontend-admin.md`
5. `coding-conventions.md`
6. `testing-and-validation.md`
7. `docs/architecture/**`

## 使用方式

- 后端开发：读取 `api-contract.md`、`backend.md`、`coding-conventions.md`、`testing-and-validation.md`。
- 博客前台开发：读取 `api-contract.md`、`frontend-platform.md`、`coding-conventions.md`、`testing-and-validation.md`，并联查 `sourcelin-ui/sourcelin-ui-platform/AGENTS.md`。
- 管理后台开发：读取 `api-contract.md`、`frontend-admin.md`、`coding-conventions.md`、`testing-and-validation.md`。
- API 契约治理：读取 `api-contract.md` 和 `docs/architecture/api-contract.md`。
- 架构重构：读取全部规则，并执行 `node scripts/architecture-guard/run.mjs`。

## 维护规则

- MUST：规则与真实目录、脚本、技术栈保持一致。
- MUST：新增长期规则必须放入本目录六个文件之一。
- MUST：删除或替换旧规则时同步更新 `AGENTS.md`、`skills/README.md` 和相关技能。
- MUST NOT：新增第二份 API 契约、第二份前端设计系统规则或第二份测试清单。
- MUST NOT：使用“建议”“尽量”等不可验收表述作为硬性规则。
- CHECK：执行 `node scripts/architecture-guard/rules-system-guard.mjs`，确认规则、技能和入口文件互相引用一致。
