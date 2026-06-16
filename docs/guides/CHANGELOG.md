# 更新日志

本文档记录了 Sourcelin Blog 的所有重要变更。所有重大变更都会在此文件中列出。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

> 说明：`v1.0.0` 章节主要反映首个开源版本时的历史发布信息，不完全等同于当前代码现状。当前实现以 [README.md](../../README.md)、[PROJECT_SUMMARY.md](../product/PROJECT_SUMMARY.md) 和 [rules/](../../rules/) 为准。

## [v1.0.0] - 2024-07-19

### 新增
- 完整的博客系统核心功能
- 文章管理（CRUD、置顶、推荐）
- 分类管理
- 标签管理
- 评论系统
- 收藏功能
- 关注/粉丝功能
- 友链管理
- 用户管理
- 导航管理
- 网站配置
- 说说/树洞功能（后端）

### 功能
- 基于 Spring Cloud Alibaba 微服务架构
- Nacos 服务注册与配置中心
- Spring Security + JWT 认证授权（历史基线，当前实现已迁移到 Sa-Token 方向，现行基线见 docs/configs）
- Sentinel 流量控制
- Seata 分布式事务
- Redis 缓存
- 文件服务（FastDFS/MinIO）
- Swagger API 文档
- Spring Boot Admin 监控

### 前端
- Vue 2 + Element UI 管理后台
- Vue 2 + Element UI 博客前台
- 响应式设计
- 现代化 UI 组件

### 数据库
- MySQL 8.0+ 兼容
- 完整的博客业务表结构
- Nacos 配置数据
- Seata 事务表

## [待发布] - 开发中

### 当前基线
- 当前前端主线为 Vue 3 博客前台和 Vue 3 管理后台
- 当前认证基线为 Sa-Token
- 当前文件存储基线为 MinIO
- 当前公开部署入口包含 `docker-compose.example.yml` 与 `.env.example`

### 计划中
- [ ] 前台 API 接口完善
- [ ] 文章详情页
- [ ] 用户中心数据对接
- [ ] 点赞/收藏/关注前台功能
- [ ] 小程序支持（Uni-app）
- [ ] 一键部署脚本（Docker）
- [ ] SEO 优化
- [ ] 数据分析功能

### 待修复
- [ ] 前台页面数据对接
- [ ] 部分 Bug 修复

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024-07-19 | 首个独立开源版本，基于 sourcelin Sourcelin-Cloud v3.6.3 |

## 升级指南

### 从早期 3.6.x 基线升级

请说明 [升级文档](../deployment/UPGRADE.md)

### 全新安装

1. 导入数据库脚本
2. 配置 Nacos
3. 修改各服务配置文件
4. 启动服务

详见 [快速开始](../README.md#quick-start)

## 鸣谢

- [sourcelin](https://sourcelin.vip/) - 优秀的开源微服务框架
- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) - 微服务解决方案
- [Vue](https://vuejs.org/) - 渐进式 JavaScript 框架
- [Element Plus](https://element-plus.org/) - 管理后台组件库
- [Naive UI](https://www.naiveui.com/) - 博客前台组件库
- 所有为本项目贡献代码的朋友

## 文档导航

- [README.md](../../README.md)
- [PROJECT_SUMMARY.md](../product/PROJECT_SUMMARY.md)
- [QUICK_START.md](./QUICK_START.md)
- [SHOWCASE.md](./SHOWCASE.md)

---

*如需查看完整历史，请访问 [Commits](https://gitee.com/my_lyq/sourcelin-cloud-blog/commits/master)*
