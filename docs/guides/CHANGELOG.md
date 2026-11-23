# 更新日志

本文档记录了 Sourcelin Blog 的所有重要变更。所有重大变更都会在此文件中列出。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

> 说明：`v1.0.0` 章节主要反映首个开源版本时的历史发布信息，不完全等同于当前代码现状。当前实现以 [README.md](../../README.md)、[PROJECT_SUMMARY.md](../product/PROJECT_SUMMARY.md) 和 [rules/](../../rules/) 为准。

## 2026-11-23 主线架构升级

- 后端迁移到 JDK 27 + Spring Boot 4.0.10 + Spring Cloud 2025.1.5 + Spring Cloud Alibaba 2025.1.0.0，服务配置入口迁入 `application.yml`。
- 业务库与 Nacos 配置库的初始化基线更新为 MySQL 8.4 LTS。Redis 会话存储要求 6.0 及以上。
- 博客前台与管理后台使用 Vite 8，Uniapp 的 H5 和微信小程序使用 Vite 5。三端分别使用各自的锁文件安装依赖。
- 修正跨服务登录态反序列化、AI 长文本结果回写及文章详情窄屏布局。
- 已有部署的配置迁移见[升级指南](../deployment/UPGRADE.md)。

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
- Spring Security + JWT 认证授权（历史基线，现行主线使用 Sa-Token）
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

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024-07-19 | 首个独立开源版本，基于 sourcelin Sourcelin-Cloud v3.6.3 |

## 升级指南

### 从早期 3.6.x 基线升级

参见[升级文档](../deployment/UPGRADE.md)

### 全新安装

1. 导入数据库脚本
2. 配置 Nacos
3. 修改各服务配置文件
4. 启动服务

详见 [快速开始](./QUICK_START.md)

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
