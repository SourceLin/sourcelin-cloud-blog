# docs 目录导览

`docs/` 只保留适合作为长期实现依据、协作入口或部署参考的文档。

如果你第一次进入这个目录，建议按下面的顺序阅读：

1. [DOCS_INDEX.md](./DOCS_INDEX.md)：完整文档索引
2. [guides/QUICK_START.md](./guides/QUICK_START.md)：快速启动与常见问题
3. [product/PROJECT_SUMMARY.md](./product/PROJECT_SUMMARY.md)：项目整体概览
4. [architecture/](./architecture/)：架构边界与 API 契约
5. [deployment/](./deployment/)：部署与升级说明

## 分组说明

| 目录 | 用途 | 适合谁看 |
|------|------|----------|
| [architecture/](./architecture/) | 长期有效的架构边界、模块职责、接口契约说明 | 开发者、维护者 |
| [configs/](./configs/) | Nacos、鉴权、文件中心、Nginx 等配置基线 | 部署者、后端开发者 |
| [deployment/](./deployment/) | Docker、单域名部署、Nginx、升级流程 | 部署者、运维 |
| [guides/](./guides/) | 快速开始、贡献、案例墙、支持、变更记录 | 新用户、贡献者 |
| [product/](./product/) | 项目分析、需求说明、综合摘要 | 产品、开发者、二开评估者 |
| [reference/](./reference/) | URL 矩阵、专项架构分析等参考资料 | 开发者、排障人员 |
| [sql/](./sql/) | 数据库初始化与配置 SQL | 部署者、后端开发者 |
| [screenshot/](./screenshot/) | 项目界面截图，用于 README 展示和开源介绍 | 新用户、访客 |

## 约定

- `docs/` 根目录只保留入口文档，例如本文件和 [DOCS_INDEX.md](./DOCS_INDEX.md)。
- 具体主题文档按目录归类放置，不再平铺堆在 `docs/` 根目录。
- 内部业务资料只允许放在 `docs/internal/`，且不进入开源发布分支。
- 一次性过程稿、临时操作记录和被 `rules/`、`skills/` 取代的 AI 提示文档不再保留。
