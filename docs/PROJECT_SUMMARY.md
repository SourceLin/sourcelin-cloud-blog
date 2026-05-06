# Sourcelin Cloud Blog 项目综合摘要

> 说明：这是一份阶段性项目摘要，偏向历史背景和整体概览。当前实施约束请优先说明 [AGENTS.md](../AGENTS.md)、[rules/](../rules/) 和 [skills/](../skills/)。

## 项目概览
- **项目名称**: Sourcelin Blog (圆圈博客)
- **版本**: 3.6.3
- **类型**: 前后端分离的分布式微服务博客系统
- **基于框架**: sourcelin Sourcelin-Cloud v3.6.3
- **许可证**: MIT

## 技术架构

### 后端技术栈
- **Spring Boot**: 2.7.13
- **Spring Cloud**: 2021.0.8
- **Spring Cloud Alibaba**: 2021.0.5.0
- **Java**: 1.8
- **服务注册/配置中心**: Nacos
- **流量控制**: Sentinel
- **分布式事务**: Seata
- **数据库**: MySQL + Druid + PageHelper
- **缓存**: Redis
- **文件存储**: FastDFS + MinIO
- **API文档**: Swagger
- **工具库**: Hutool + FastJSON2 + Apache POI

### 前端技术栈
- **Vue**: 2.6.12
- **Element UI**: 2.15.13
- **Vuex**: 3.6.0
- **Vue Router**: 3.4.9
- **Axios**: 0.24.0
- **ECharts**: 5.4.0
- **Vue CLI**: 5.0.8
- **Sass**: -

### 系统架构
```text
客户端层:
  ├── 管理后台 (Vue+Element)
  ├── 博客前台 (Vue+Element)
  └── 小程序/H5 (Uni-app) [计划中]

          ↓
API 网关 (8080) - Spring Cloud Gateway

          ↓
服务层:
  ├── 认证服务 (9200)
  ├── 系统服务 (9201)
  ├── 博客服务 (9204)
  ├── 文件服务 (9300)
  └── 监控中心 (9100)

          ↓
基础设施:
  ├── MySQL
  ├── Redis
  └── Nacos
```

## 系统模块结构
```text
com.sourcelin
├── sourcelin-ui              // 前端框架 [80]
├── sourcelin-gateway         // 网关模块 [8080]
├── sourcelin-auth            // 认证中心 [9200]
├── sourcelin-api             // 接口模块
│   ├── sourcelin-api-system    // 系统接口
│   ├── sourcelin-api-blog      // 博客接口
│   └── sourcelin-api-file      // 文件接口
├── sourcelin-common          // 通用模块
│   ├── sourcelin-common-core         // 核心模块
│   ├── sourcelin-common-datascope    // 权限范围
│   ├── sourcelin-common-datasource   // 多数据源
│   ├── sourcelin-common-log          // 日志记录
│   ├── sourcelin-common-redis        // 缓存服务
│   ├── sourcelin-common-seata        // 分布式事务
│   ├── sourcelin-common-security     // 安全模块
│   └── sourcelin-common-swagger      // 系统接口
├── sourcelin-modules         // 业务模块
│   ├── sourcelin-system        // 系统模块 [9201]
│   ├── sourcelin-blog          // 博客模块 [9204]
│   ├── sourcelin-job           // 定时任务 [9203]
│   └── sourcelin-file          // 文件服务 [9300]
└── sourcelin-visual           // 图形化管理模块
    └── sourcelin-visual-monitor  // 监控中心 [9100]
```

## 功能完成度

### 已完成后端模块 (100%)
| 模块 | 端口 | 功能 | 状态 |
|------|------|------|------|
| sourcelin-gateway | 8080 | 网关 | ✅ |
| sourcelin-auth | 9200 | 认证中心 | ✅ |
| sourcelin-system | 9201 | 系统管理 | ✅ |
| sourcelin-blog | 9204 | 博客业务 | ✅ |
| sourcelin-file | 9300 | 文件服务 | ✅ |
| sourcelin-job | 9203 | 定时任务 | ✅ |
| sourcelin-visual-monitor | 9100 | 监控中心 | ✅ |

### 博客功能清单
| 功能 | 后端 | 前台 | 后台 |
|------|------|------|------|
| 文章管理 | ✅ | ✅ | ✅ |
| 分类管理 | ✅ | ✅ | ✅ |
| 标签管理 | ✅ | ✅ | ✅ |
| 评论管理 | ✅ | ✅ | ✅ |
| 收藏功能 | ✅ | ✅ | - |
| 关注/粉丝 | ✅ | - | - |
| 友链管理 | ✅ | ✅ | ✅ |
| 用户管理 | ✅ | ✅ | ✅ |
| 导航管理 | ✅ | - | ✅ |
| 网站配置 | ✅ | - | ✅ |
| 文件管理 | ✅ | - | ✅ |
| 博客统计 | ✅ | - | ✅ |
| 树洞功能 | ✅ | ✅ | ✅ |
| 说说 | ✅ | ✅ | ✅ |
| 前台公告 | ✅（Feign 读系统 `sys_notice`） | ✅ | ✅（系统模块「通知公告」） |

### 已完成前端
- **sourcelin-ui-platform / sourcelin-ui-platform-vue2**: 博客前台
  - 首页、文章、标签、分类、评论、友链
  - 用户中心、登录注册、归档、关于
  - 说说、树洞、公告等（按具体子项目页面为准）

- **sourcelin-ui-admin**: 后台管理系统
  - 系统管理(用户/角色/菜单/部门/岗位/字典/配置)
  - 博客管理(文章/分类/标签/评论/友链/用户/导航/网站配置/文件/统计/**说说/树洞**)
  - 系统监控(「线用户/定时任务)
  - 系统工具(代码生成/表单构建)
  - 首页展示博客统计摘要（需 `blog:stats:view` 权限）

## 待完成事项（可选增强）

### 前台 / 产品
- 用户中心数据看板（文章数、获赞等）可按产品需求继续细化
- 多端（小程序/H5）扩展见项目路线图说明

### 后台 / 运营
- 收藏、关注等纯前台互动数据：若需运营导出，可单独增加只读列表与导出
- 说说/树洞菜单：在「菜单管理」中配置，并为角色授权；如存在对应 SQL 变更，应以 `docs/sql/` 当前脚本为准

## 产品定位和目标用户

### 产品定位
Sourcelin Blog 是一套面向个人写作与生活记录的博客平台，旨「提供：
- ✍️ **个人博客空间** - 记录生活、写作随笔与分享见闻
- 📚 **内容创作工具** - 文章、分类、标签、评论等完整功能
- 🧩 **小团队内容中心** - 支持多角色内容协作与管理
- 🗂️ **长期内容归档** - 让内容沉淀与可持续维护

### 目标用户
1. **个人写作者** - 记录生活、书影音、旅行与日常
2. **内容创作者** - 需要稳定的写作与内容管理能力
3. **小团队/社群** - 进行内容发布与协作运营
4. **品牌与工作室** - 需要对外展示与内容沉淀
5. **个人作品展示** - 建立长期可维护的内容主页

## 市场竞品分析

### 主要竞品
1. **WordPress** - 最流行的博客平台，但采用传统LAMP架构
2. **Typecho** - 轻量级博客系统，PHP为基础
3. **Hexo** - 基于Node.js的静态博客框架
4. **Ghost** - Modern blogging platform, Node.js基础
5. **微服务博客系统** - 国内较少见基于Spring Cloud的博客系统

### 差异化优势
1. **微服务架构** - 采用Spring Cloud Alibaba微服务技术栈，具备高可用性和可扩展性
2. **前后端分离** - Vue 2 + Element UI 前端，提供优雅的响应式设计
3. **完整权限体系** - 基于 Spring Security + JWT 的认证授权
4. **多端支持** - PC 管理后台 + 博客前台（支持 H5/小程序扩展）
5. **丰富的博客功能** - 文章、分类、标签、评论、点赞、收藏、关注、打赏等
6. **高扩展性** - 模块化设计，支持插件扩展
7. **开箱即用** - 提供 Docker 一键部署脚本
8. **完全国产化** - 基于sourcelin框架，符合国内技术栈习惯

## 部署和启动

### 环境要求
| 组件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| JDK | 1.8+ | 1.8 / 11 |
| Maven | 3.6+ | 3.8.x |
| MySQL | 5.7+ | 8.0 |
| Redis | 5.0+ | 6.0+ |
| Node.js | 14+ | 16 LTS |
| Nacos | 2.0+ | 2.0.x |
| Docker | 20+ | 24.x (可选) |

### 启动顺序
1. MySQL / Redis / Nacos / Seata
2. sourcelin-gateway (8080)
3. sourcelin-auth (9200)
4. sourcelin-system (9201)
5. sourcelin-blog (9204)
6. sourcelin-file (9300)
7. sourcelin-job (9203)
8. sourcelin-visual-monitor (9100)
9. sourcelin-ui-admin (80)
10. sourcelin-ui-platform (前端)

### Docker部署（推荐）
```bash
docker-compose up -d
```

## 开发建议
1. **前端分离**: 博客前台(sourcelin-ui-platform)和管理后台(sourcelin-ui-admin)已分开
2. **移动端**: 建议使用uni-app开发小程序和H5
3. **性能优化**: 可考虑引入Redis缓存、CDN加速
4. **安全加固**: 生产环境需配置HTTPS、JWT密钥等

## 项目价值
1. **内容价值**：提供完整的写作、归档、展示与互动能力
2. **使用价值**：可直接用于个人博客或品牌内容主页
3. **运营价值**：支持分类标签与推荐，提升内容组织效率
4. **可持续性**：结构清晰，便于长期维护与扩展
5. **可用性**：前后台分离，便于独立演进与部署

---
*文档生成时间: 2026年3月16日*
*基于 PROJECT_ANALYSIS.md 和 README.md 整理*
