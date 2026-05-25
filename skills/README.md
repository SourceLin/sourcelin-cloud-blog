# 技能总览

本目录只维护 Sourcelin Cloud Blog 仓库级技能。技能用于把 `rules/` 转换为 AI 可执行流程，不重复定义规则。

## 技能列表

- `frontend-platform-dev`：博客前台页面、组件、API、composable、设计系统边界开发。
- `frontend-uniapp-dev`：Uniapp 移动端页面、组件、API、登录态、pages.json、manifest 和跨端兼容开发。
- `frontend-admin-dev`：管理后台页面、API、表格分页、权限和模块化组件开发。
- `backend-dev`：后端 Controller、Service、Mapper、DTO/VO、异常和公共能力开发。
- `api-contract-governance`：统一响应体、分页、错误码、前后端消费和运行态验证治理。
- `architecture-refactor`：跨端目录整理、组件拆分、模块边界、规则和文档同步重构。

## 使用顺序

1. 读取仓库根 `AGENTS.md`。
2. 读取 `rules/README.md`。
3. 按任务选择一个主技能；涉及 API 契约时叠加 `api-contract-governance`。
4. 按技能步骤执行，不跳过验证。
5. 若规则、技能或入口文件变化，检查引用路径真实存在，并确认开源分支边界未被破坏。

## 维护原则

- MUST：技能只描述执行流程，规则真源必须在 `rules/`。
- MUST：技能引用的规则文件、命令和目录必须真实存在。
- MUST：新增技能必须包含使用场景、输入输出、执行步骤、依赖 rules、禁止行为。
- MUST NOT：多个技能重复定义同一条协议规则。
- CHECK：审查 `AGENTS.md`、`rules/**`、`skills/**` 的互相引用。
