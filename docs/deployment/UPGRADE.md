# 已有部署切换到主线

当前主线使用 JDK 27、MySQL 8.4 LTS 和 Nacos 3.1.1。前端构建环境见[快速启动](../guides/QUICK_START.md)。

各服务从 `application.yml` 的 `spring.config.import` 加载 Nacos 配置。升级时核对 `BLOG_GROUP` 下的共享配置和服务配置，并确保认证、网关和业务服务连接同一 Redis 实例和库编号。会话续期依赖 Redis 6.0 起支持的 `KEEPTTL`。

主线通过[初始化 SQL](../sql/README.md)创建业务库和 Nacos 配置库。已有实例切换前，备份数据库、Nacos 配置、上传文件和当前应用产物。

在隔离环境按[快速启动](../guides/QUICK_START.md)导入空库，再迁入需要保留的业务数据和环境配置。验证账号登录后的用户资料、文章列表、后台权限与移动端能力后切换。可选功能的启用和回退步骤见 [AI 与实时消息部署](../guides/AI_MESSAGE_SETUP.md)。
