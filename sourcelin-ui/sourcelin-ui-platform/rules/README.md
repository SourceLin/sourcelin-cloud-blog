# 规则索引

`rules/` 是当前仓库唯一规则源目录。正式规则只在这里维护，`AGENTS.md`、Cursor 和 OpenCode 入口只做引用和同步说明，不重复维护完整正文。

## 文件列表

- `frontend-platform.md`
  - 项目结构、路由、请求层、store、验证口径
- `design-system-layering.md`
  - 分层模型、视觉来源和业务层边界
- `ui-design-system-core.md`
  - UI 抽象层、Naive 例外范围和不可突破规则
- `ui-vue-components.md`
  - 业务组件层规范
- `ui-page-boundaries.md`
  - 页面层规范
- `ui-scss-tokens.md`
  - Token、Foundation、override、兼容层和样式守卫口径
- `documentation-sync.md`
  - 架构或边界变更时必须同步更新的目标

## 使用原则

- 改规则，只改 `rules/`
- 改技能，只改 `skills/`
- 更新架构说明时，同时检查 `README.md`、`AGENTS.md`、`docs/`、`.cursor/`、`.opencode/`
- 入口目录只保留轻量说明，不复制规则正文

## 推荐联查文件

- `README.md`
- `AGENTS.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
- `skills/README.md`
