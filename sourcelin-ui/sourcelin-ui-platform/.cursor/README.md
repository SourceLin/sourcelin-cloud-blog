# Cursor 入口说明

`.cursor/` 在本仓库中只承担 Cursor 入口说明作用，不维护完整规则正文。

## 当前约定

- 正式规则只在 `rules/` 维护
- 正式技能只在 `skills/` 维护
- `.cursor/rules/project-rules.mdc` 是 Cursor 的统一入口
- 架构变更时需同步检查 `README.md`、`AGENTS.md`、`docs/`、`rules/`、`skills/`
- 新增加载态规范时，同步检查 `SSkeleton`、共享 skeleton 组件、`aria-busy` 和增量 loading 口径

## 关联文件

- `README.md`
- `AGENTS.md`
- `rules/README.md`
- `skills/README.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
