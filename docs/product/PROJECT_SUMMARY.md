# Sourcelin Blog 项目综合摘要

> 当前实施约束、目录边界和验证要求以 [AGENTS.md](../../AGENTS.md)、[rules/](../../rules/) 和 [skills/](../../skills/) 为准。本文档只负责提供项目级概览。

## 项目概览

- 项目名称：Sourcelin Blog（圆圈博客）
- 项目类型：前后端分离的微服务博客平台
- 当前定位：个人写作、内容展示、小团队内容运营、毕业设计与二次开发基线
- 技术基线：基于 Sourcelin-Cloud v3.6.3 演进
- 开源协议：MIT

## 当前技术栈

### 后端

| 技术 | 版本 / 说明 |
|------|-------------|
| Java | 1.8+ |
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.9 |
| Spring Cloud Alibaba | 2021.0.6.1 |
| Nacos | 注册中心 / 配置中心 |
| Sentinel | 流量控制 |
| Seata | 分布式事务 |
| MySQL | 8.0+ |
| Redis | 缓存 |
| Druid | 数据源 |
| PageHelper | 分页 |
| MinIO | 文件存储 |
| Sa-Token | 认证鉴权 |

### 前端

| 端 | 技术栈 |
|----|--------|
| 博客前台 | Vue 3 + TypeScript + Naive UI + Vite |
| 管理后台 | Vue 3 + TypeScript + Element Plus |
| 旧版迁移对照 | `sourcelin-ui-admin-vue2`，不承载新功能 |

## 系统架构

```text
客户端层
├── 管理后台（Vue 3 + Element Plus）
├── 博客前台（Vue 3 + Naive UI）
└── 小程序 / H5（规划中）

          ↓
API 网关（8080）

          ↓
服务层
├── 认证服务（9200）
├── 系统服务（9201）
├── 博客服务（9204）
├── 文件服务（9300，可选）
├── 定时任务（9203）
└── 监控中心（9100，可选）

          ↓
基础设施
├── MySQL
├── Redis
└── Nacos
```

## 仓库结构

```text
sourcelin-cloud-blog/
├── sourcelin-api/                         # Feign 接口与跨服务 DTO
├── sourcelin-common/                      # 公共能力
├── sourcelin-gateway/                     # 网关
├── sourcelin-auth/                        # 认证中心
├── sourcelin-modules/
│   ├── sourcelin-system/                  # 系统管理
│   ├── sourcelin-blog/                    # 博客业务
│   ├── sourcelin-file/                    # 文件服务
│   └── sourcelin-job/                     # 定时任务
├── sourcelin-visual/                      # 可视化监控
├── sourcelin-ui/
│   ├── sourcelin-ui-platform/             # 博客前台
│   ├── sourcelin-ui-admin/                # 管理后台
│   └── sourcelin-ui-admin-vue2/           # 旧版迁移对照
├── docs/                                  # 长期文档
├── rules/                                 # 仓库规则
├── skills/                                # AI 协作技能
└── AGENTS.md                              # 仓库执行入口
```

## 当前核心能力

### 博客前台

- 首页推荐、热门文章、分类、标签、归档
- 文章详情、Markdown 渲染、评论互动
- 点赞、收藏、关注
- 说说、树洞、友链、导航、关于页面
- 登录注册、个人中心、资料修改、头像上传

### 管理后台

- 系统管理：用户、角色、菜单、部门、岗位、字典、参数、通知
- 博客管理：文章、分类、标签、评论、友链、用户、导航、配置
- 系统监控：在线用户、定时任务
- 系统工具：代码生成、表单构建
- 文件管理、博客统计

### 后端基础能力

- Gateway 统一入口
- Sa-Token 鉴权
- 统一 API 契约
- Redis 缓存
- MinIO 文件存储
- Nacos 配置管理
- Sentinel 流量控制
- Seata 分布式事务
- Spring Boot Admin 监控

## API 契约基线

当前对外接口统一遵守：

- 成功响应为 `ApiResponse<T>`
- 顶层字段为 `code / message / data / requestId / timestamp`
- 成功码为 `0`
- 分页响应为 `PageResult<T>`
- 分页字段为 `items / total / page / pageSize / totalPages`

详细规则见：

- [rules/api-contract.md](../../rules/api-contract.md)
- [architecture/api-contract.md](../architecture/api-contract.md)

## 适用场景

- 个人博客和内容沉淀
- 小团队内容运营站点
- Java + Vue 全栈学习项目
- 微服务博客二次开发基线
- 毕业设计 / 课程设计展示
- AI 协作开发、规则治理、全栈重构实践

## AI 协作资产

本项目的一个显著差异点是内置了可直接复用的 AI 协作资产：

- [AGENTS.md](../../AGENTS.md)：仓库总入口与执行边界
- [rules/README.md](../../rules/README.md)：规则总览
- [skills/README.md](../../skills/README.md)：技能总览

这使项目不仅可用于业务开发，也可作为 AI Coding 规则治理与仓库开源化整理的实践样板。

## 部署与启动

### 基础依赖

1. MySQL
2. Redis
3. Nacos

### 核心服务启动顺序

1. `sourcelin-gateway`
2. `sourcelin-auth`
3. `sourcelin-system`
4. `sourcelin-blog`

按需补充：

5. `sourcelin-file`
6. `sourcelin-job`
7. `sourcelin-visual-monitor`

### 参考文档

- [README.md](../../README.md)
- [QUICK_START.md](../guides/QUICK_START.md)
- [DEPLOY_SOURCELIN_CN_DOCKER.md](../internal/deployment/DEPLOY_SOURCELIN_CN_DOCKER.md)
- [DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md](../internal/deployment/DEPLOY_SOURCELIN_CN_SINGLE_DOMAIN.md)

## 当前路线图

- App / 小程序端能力完善
- AI 内容助手
- AI 审核升级
- 消息中心增强
- SSE 实时推送
- Python 转载系统
- SEO / 流量增长
- 运营看板

## 文档说明

本文档已去除历史阶段中已经失真的旧技术栈描述。涉及实现细节时，请优先回到当前代码、`README.md`、`AGENTS.md` 和 `rules/` 查阅。
