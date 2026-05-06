# 文档同步规则

当任务修改以下任一内容时，必须同步更新相关文档：

- 架构边界
- 目录结构
- 路由与导航协议
- 请求前缀和 API 约定
- UI 抽象层与 Naive 的关系
- Token、Foundation、override、兼容层的归属
- 主题校准方式或 startup loading 机制
- 规则或技能中描述的开发约束

## 必须同步的目标

- `README.md`
- `AGENTS.md`
- `docs/frontend-architecture.md`
- `docs/style-asset-ledger.md`
- `rules/`
- `skills/`
- `.cursor/README.md`
- `.cursor/rules/project-rules.mdc`
- `.opencode/README.md`
- `.opencode/skills/`

## 维护原则

- `rules/` 是规则源
- `skills/` 是技能源
- `.cursor/` 和 `.opencode/` 只保留入口说明，不重复维护完整正文
- 更新规则或技能后，要反查轻量入口是否仍然指向正确文件
