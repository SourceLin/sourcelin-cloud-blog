# 文档索引

本索引只保留当前仍适合作为实现依据的长期文档入口。一次性计划、过程审计、迁移中间稿、临时远程操作说明和已被 `rules/`、`skills/` 取代的 AI 提示文档不再维护。

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
| [NGINX_CONFIG.md](./NGINX_CONFIG.md) | 前端入口、网关转发、公开接口暴露边界 |
| [DEPLOY_SOURCELIN_CN_DOCKER.md](./DEPLOY_SOURCELIN_CN_DOCKER.md) | sourcelin.cn Docker 部署说明 |
| [DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md](./DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md) | sourcelin.cn 单域名前后台部署说明 |
| [UPGRADE.md](./UPGRADE.md) | 通用升级流程 |

## 项目与协作

| 文档 | 说明 |
|------|------|
| [README.md](../README.md) | 项目主页，包含启动方式与模块说明 |
| [CHANGELOG.md](./CHANGELOG.md) | 版本更新日志 |
| [CONTRIBUTING.md](./CONTRIBUTING.md) | 贡献指南 |
| [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) | 行为准则 |
| [SUPPORT.md](./SUPPORT.md) | 支持信息 |
| [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) | 项目综合摘要 |

## 产品与业务

| 文档 | 说明 |
|------|------|
| [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md) | 项目分析与阶段性建议 |
| [REQUIREMENTS.md](./REQUIREMENTS.md) | 需求与接口设计说明 |
| [MARKET_ANALYSIS.md](./MARKET_ANALYSIS.md) | 市场分析 |
| [BUSINESS_MODEL.md](./BUSINESS_MODEL.md) | 商业模式 |

## 领域设计与专项文档

| 文档 | 说明 |
|------|------|
| [blog-api-url-matrix.md](./blog-api-url-matrix.md) | 博客服务 URL 矩阵与前台只读冒烟说明 |
| [like-comment-architecture-analysis.md](./like-comment-architecture-analysis.md) | 点赞、收藏、评论相关架构分析 |
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
