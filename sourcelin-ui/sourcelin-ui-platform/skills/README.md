# 技能索引

`skills/` 是当前仓库唯一技能源目录。正式技能正文只在这里维护，OpenCode 入口只做轻量跳转说明。

## 当前技能

- `sourcelin-ui-refactor/SKILL.md`
  - Sourcelin UI Platform 的主线目录、边界、验证与同步要求
- `sourcelin-ui-refactor/reference.md`
  - 目录映射、route meta、store、请求层和检查项速查

## 使用原则

- 改技能，只改 `skills/`
- 技能描述必须跟随当前架构更新
- `.opencode/skills/` 只保留入口，不复制完整技能正文
- 架构边界变化后，同时回看 `rules/`、`README.md`、`AGENTS.md` 和 `docs/`
- 加载态规范变更后，同时回看 `SSkeleton`、共享 skeleton 组件和 `aria-busy` 约束
