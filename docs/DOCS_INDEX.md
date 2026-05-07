# 文档索引

本索引只保留当前仍适合作为实现依据的长期文档入口。一次性计划、过程审计、迁移中间稿、临时远程操作说明和已被 `rules/`、`skills/` 取代的 AI 提示文档不再维护。

## 目录分组说明

| 目录 | 说明 |
|------|------|
| [README.md](./README.md) | `docs/` 目录导览与阅读顺序 |
| [architecture/](./architecture/) | 长期有效的架构边界与接口契约 |
| [configs/](./configs/) | Nacos、鉴权、文件中心、Nginx 配置基线 |
| [deployment/](./deployment/) | 部署、升级与网关入口配置 |
| [design/](./design/) | 分阶段设计方案与专项重构说明 |
| [guides/](./guides/) | 快速开始、贡献、案例墙、支持、变更记录 |
| [product/](./product/) | 项目分析、需求说明、综合摘要 |
| [reference/](./reference/) | URL 矩阵、专项架构分析等参考资料 |
| [sql/](./sql/) | 初始化与维护 SQL |
| [screenshot/](./screenshot/) | 开源展示截图资源 |

## 架构与规则

| 文档 | 用途 |
|------|------|
| [architecture/api-contract.md](./architecture/api-contract.md) | API 响应体、分页、ID 响应、前端消费与验收说明 |
| [architecture/backend-architecture.md](./architecture/backend-architecture.md) | 后端模块边界、WebMVC 异常处理与模块化演进 |
| [architecture/frontend-platform-architecture.md](./architecture/frontend-platform-architecture.md) | 博客前台模块、组件、composable 与设计系统边界 |
| [architecture/frontend-admin-architecture.md](./architecture/frontend-admin-architecture.md) | 管理后台模块化、共享壳和验证方式 |
| [../rules/README.md](../rules/README.md) | AI 编程规则入口 |
| [../skills/README.md](../skills/README.md) | AI 编程技能入口 |

## 配置与部署基线

| 文档 | 用途 |
|------|------|
| [configs/sa-token-nacos-configs.md](./configs/sa-token-nacos-configs.md) | 认证、授权、会话、Nacos 配置基线 |
| [configs/file-center-nacos-configs.md](./configs/file-center-nacos-configs.md) | 文件中心上传、确认、下载、网关放行基线 |
| [configs/blog-moderation-nacos.md](./configs/blog-moderation-nacos.md) | 博客评论审核与敏感词配置基线 |
| [deployment/NGINX_CONFIG.md](./deployment/NGINX_CONFIG.md) | 前端入口、网关转发、公开接口暴露边界 |
| [deployment/DEPLOY_SOURCELIN_CN_DOCKER.md](./deployment/DEPLOY_SOURCELIN_CN_DOCKER.md) | sourcelin.cn Docker 部署说明 |
| [deployment/DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md](./deployment/DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md) | sourcelin.cn 单域名前后台部署说明 |
| [deployment/UPGRADE.md](./deployment/UPGRADE.md) | 通用升级流程 |

## 项目与协作

| 文档 | 说明 |
|------|------|
| [README.md](../README.md) | 项目主页，包含启动方式与模块说明 |
| [README.md](./README.md) | `docs/` 分组导览 |
| [guides/QUICK_START.md](./guides/QUICK_START.md) | 新手快速启动与常见问题 |
| [../docker-compose.example.yml](../docker-compose.example.yml) | 可公开复用的 Docker Compose 示例 |
| [../.env.example](../.env.example) | Compose 环境变量示例 |
| [guides/SHOWCASE.md](./guides/SHOWCASE.md) | 用户案例墙与案例征集入口 |
| [guides/CHANGELOG.md](./guides/CHANGELOG.md) | 版本更新日志 |
| [guides/CONTRIBUTING.md](./guides/CONTRIBUTING.md) | 贡献指南 |
| [guides/CODE_OF_CONDUCT.md](./guides/CODE_OF_CONDUCT.md) | 行为准则 |
| [guides/SUPPORT.md](./guides/SUPPORT.md) | 支持信息 |
| [product/PROJECT_SUMMARY.md](./product/PROJECT_SUMMARY.md) | 项目综合摘要 |

## 产品与需求

| 文档 | 说明 |
|------|------|
| [product/PROJECT_ANALYSIS.md](./product/PROJECT_ANALYSIS.md) | 项目分析与阶段性建议 |
| [product/REQUIREMENTS.md](./product/REQUIREMENTS.md) | 需求与接口设计说明 |

## 领域设计与专项文档

| 文档 | 说明 |
|------|------|
| [reference/blog-api-url-matrix.md](./reference/blog-api-url-matrix.md) | 博客服务 URL 矩阵与前台只读冒烟说明 |
| [reference/like-comment-architecture-analysis.md](./reference/like-comment-architecture-analysis.md) | 点赞、收藏、评论相关架构分析 |
| [design/2026-04-09-admin-gateway-auth-single-chain.md](./design/2026-04-09-admin-gateway-auth-single-chain.md) | 网关鉴权单链路策略 |
| [design/2026-04-09-admin-path-decision-and-routing.md](./design/2026-04-09-admin-path-decision-and-routing.md) | 管理后台路由与路径决策 |
| [design/2026-04-12-admin-data-canonical-contract-baseline.md](./design/2026-04-12-admin-data-canonical-contract-baseline.md) | 管理后台数据契约基线 |
| [design/2026-04-12-admin-pagination-request-canonical.md](./design/2026-04-12-admin-pagination-request-canonical.md) | 管理后台分页协议基线 |
| [design/2026-04-16-blog-announcement-solution.md](./design/2026-04-16-blog-announcement-solution.md) | 博客公告方案说明 |
| [design/2026-04-16-blog-message-center-notice-architecture.md](./design/2026-04-16-blog-message-center-notice-architecture.md) | 消息中心与公告架构说明 |
| [design/2026-04-16-platform-dict-upgrade-plan.md](./design/2026-04-16-platform-dict-upgrade-plan.md) | 博客前台字典处理升级方案 |
| [design/2026-04-17-admin-theme-fullstack-upgrade-plan.md](./design/2026-04-17-admin-theme-fullstack-upgrade-plan.md) | 管理后台主题系统升级方案 |
| [design/2026-04-19-platform-stat-card-usage-strategy.md](./design/2026-04-19-platform-stat-card-usage-strategy.md) | 前台统计卡展示策略 |
| [design/2026-04-21-collect-polymorphic-full-rebuild.md](./design/2026-04-21-collect-polymorphic-full-rebuild.md) | 收藏模型多类型重构方案 |
| [design/2026-04-28-admin-system-config-domain-split-plan.md](./design/2026-04-28-admin-system-config-domain-split-plan.md) | 管理后台配置域拆分方案 |

## 数据库脚本

| 目录 | 说明 |
|------|------|
| [sql](./sql/) | 长期保留的初始化、升级、修复 SQL |

## 清理规则

1. 长期实现依据必须进入本索引。
2. 阶段性执行计划完成后不继续保留在 `docs/` 根目录。
3. AI 编程提示、技能说明统一维护在 `rules/` 和 `skills/`。
4. 远程登录、一次性上传、临时构建等操作说明不作为长期文档保留。
5. 开源分支不维护 `internal/MARKET_ANALYSIS.md` 和 `internal/BUSINESS_MODEL.md` 这类内部业务资料入口。
