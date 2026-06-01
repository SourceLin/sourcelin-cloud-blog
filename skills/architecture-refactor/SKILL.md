---
name: architecture-refactor
description: 用于执行 Sourcelin 跨端架构重构、目录整理、页面拆分、模块边界治理、规则和文档同步。
---

# 架构重构技能

## 使用场景

- 按规则执行渐进式重构。
- 拆分大页面、大组件或大 composable。
- 整理后端模块边界、公共能力、规则体系和文档。
- 对齐 `rules/`、`skills/`、`AGENTS.md`。

## 输入

- 待重构文件清单或阶段任务。
- 当前规则、技能、目录结构和验证命令。
- 已完成项与剩余项。

## 输出

- 小步可验证的重构改动。
- 同步更新的规则、技能或 SQL 文档。
- 每步验证结果和剩余风险。

## 执行步骤

1. 读取 `AGENTS.md`、`rules/README.md` 和全部相关领域规则。
2. 确认本轮只处理一个明确边界，避免跨模块大范围改动。
3. 先建立或确认自动验证，再修改代码或文档。
4. 对前台重构，按页面、组件、composable、API 分层拆分。
5. 对后台重构，按查询、表格、分页、弹窗、操作列协议拆分。
6. 对后端重构，按 Controller、Service、Mapper、domain、common 边界拆分。
7. 对规则体系重构，同步更新 `rules/`、`skills/`、`AGENTS.md`。
8. 数据库初始化变化更新 `docs/sql/**`，架构说明变化更新 `docs/architecture/**`。
9. 每个子模块完成后运行对应验证命令，失败先修复再进入下一模块。

## 依赖 rules

- `rules/api-contract.md`
- `rules/backend.md`
- `rules/frontend-platform.md`
- `rules/frontend-uniapp.md`
- `rules/frontend-admin.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`

## 禁止行为

- 禁止一次性跨多个无关模块大改。
- 禁止改变视觉表现、交互行为或权限逻辑，除非任务明确要求。
- 禁止为了通过检查删除核心功能或降低测试标准。
- 禁止保留规则、技能、入口文件之间的旧引用。
- 禁止只输出方案不落地验证。
