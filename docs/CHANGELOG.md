# 更新日志

本文档记录了 Sourcelin Blog 的所有重要变更。所有重大变更都会在此文件中列出。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

## [v3.6.3] - 2024-07-19

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
| v3.6.3 | 2024-07-19 | 初始版本，基于sourcelin Sourcelin-Cloud v3.6.3 |

## 升级指南

### 从 v3.6.2 升级

请说明 [升级文档](./UPGRADE.md)

### 全新安装

1. 导入数据库脚本
2. 配置 Nacos
3. 修改各服务配置文件
4. 启动服务

详见 [快速开始](../README.md#快速开始)

## 鸣谢

- [sourcelin](https://sourcelin.vip/) - 优秀的开源微服务框架
- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) - 微服务解决方案
- [Vue](https://vuejs.org/) - 渐进式 JavaScript 框架
- [Element UI](https://element.eleme.io/) - 桌面端组件库
- 所有为本项目贡献代码的朋友

---

*如需查看完整历史，请访问 [Commits](https://gitee.com/sourcelin/sourcelin-cloud-blog/commits/master)*
