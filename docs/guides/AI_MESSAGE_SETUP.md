# 可选 AI 与实时消息部署

发布基线为 JDK 8。基础部署只需要 MySQL、Redis、Nacos 和核心服务。AI 与实时消息默认关闭，可分别启用。

## 数据库

在空库分别导入 `docs/sql/sourcelin-cloud.sql` 和 `docs/sql/sourcelin-config.sql`。业务初始化包含移动端配置、公告与已读记录、7 张 AI 表、8 项默认关闭的 AI 能力及后台菜单。Nacos 初始化包含博客、AI 和多端消息路由，图形验证码由认证服务校验。

认证、网关、系统、博客和可选服务统一读取 `BLOG_GROUP / application-dev.yml` 的 `spring.redis` 配置。分服务器部署时，为各服务设置能到达同一实例的 `REDIS_HOST`、`REDIS_PORT`，并保持 `REDIS_DATABASE` 和认证配置一致。启动后核对实际生效的 Redis 配置和登录后的用户资料请求。

其他命名空间、分组、配置名或自定义路由布局，从 Nacos 控制台合并并发布配置。删除认证路由的 `ValidateCodeFilter` 条目，保留其他过滤器、已有路由顺序与白名单，再按初始化样例补充缺失路由和精确白名单。重启网关后检查实际路由，确认匿名首页、验证码和登录可达。

本轮可选 AI 初始化用于尚未安装该模块的当前项目。已有私有 AI 数据库若使用旧密钥密文或缺少 `owner_id`，应先备份并单独迁移字段、通过后台重新配置密钥，不要将旧密文原样导入。

## 服务与环境

构建命令：

```powershell
mvn package -pl sourcelin-modules/sourcelin-ai,sourcelin-modules/sourcelin-message,sourcelin-modules/sourcelin-blog,sourcelin-auth,sourcelin-gateway -am
```

对应产物为 `sourcelin-modules-ai.jar` 和 `sourcelin-modules-message.jar`，端口分别是 9205 和 9400。Compose 样例按 `ai` 和 `message` profile 分别启动可选容器。先部署基础服务，再启用需要的 profile。服务间连接应使用同一 Redis 配置和 Nacos 分组。

AI 服务使用 `AI_CONFIG_ENCRYPTION_KEY`，值为部署环境生成的 32 字节随机密钥的 Base64 表示。密钥应稳定保存于密钥管理中，更换前迁移密文。通过 Nacos 或 `AI_DB_URL`、`AI_DB_USERNAME`、`AI_DB_PASSWORD` 配置 AI 业务库连接。

在博客服务配置 `blog.ai.enabled=true` 后，管理员填写能力 Provider、Endpoint、模型/应用 ID、API Key 和 Prompt，再启用需要的能力。所有初始化能力均关闭；不支持的 Provider 不应作为生产配置选择。封面模型与端点不绑定特定供应商。

知识库同步还需配置 `blog.ai.knowledge-sync-enabled=true`；未启用时文章编辑不会访问知识库同步表。

启用消息服务时，在博客和系统服务配置 `blog.message.enabled=true`。Compose 样例由 `BLOG_MESSAGE_ENABLED` 同时传入两个服务。三端从博客服务查询实时状态，关闭时继续使用各自的未读查询。后台使用当前后台账号的 `sys_notice_read`，博客前台和 Uniapp 使用博客消息的已读记录。小程序运行验收需要真实 AppID 和合法 HTTPS 域名。

单域名和双域名 Nginx 样例都包含消息与 AI 聊天的精确流式转发。部署时运行 `nginx -t`，重新加载后验证首包与心跳。消息票据出现在 URL 查询串，流式 location 关闭访问日志。

URL 图片转存仅为内部服务接口。文件服务通过 `file.remote.allowed-hosts` 配置允许的 HTTPS 图片来源域名，以逗号分隔；未配置时拒绝远程下载。每次重定向重新验证地址，默认最大 10MB，禁止内网地址和非图片类型。

## 备份与回退

1. 切换已部署的实例前，用 `mysqldump --single-transaction` 分别备份业务库和 Nacos 配置库，导出实际运行的 YAML，保留原应用产物。在独立实例导入主线初始化 SQL，再按数据归属导入所需业务数据和环境配置。核对文章、公告和账号后切换。
2. 功能回退时，将博客的 `blog.ai.enabled`、`blog.ai.knowledge-sync-enabled` 和博客、系统的 `blog.message.enabled` 设为 `false`，重启相关核心服务生效，再停 AI 和消息服务。保留 AI 表、密文和已读记录，验收核心功能与两个关闭状态。
3. 需要回退时，切回切换前的应用和配套数据源。先冻结写入并备份现库，按保留范围处理切换后的新增数据。
4. AI 实例异常终止时，已入库的 `pending` 或 `running` 任务可能失去执行进程。暂停任务入口并停止全部 AI 实例，核对供应商是否已执行，再把确认中断且允许重试的任务按 ID 标记为 `failed`，保留输入快照。重新启动后从任务页按需重试。

## 上线前检查

- 运行受影响 Maven 单测、前台类型/样式/架构检查、后台类型/lint、Uniapp 类型/lint/微信与 H5 构建。
- 确认普通用户不能调用文章创作能力，用户不能读取他人的任务或采纳他人的记录。
- 验证后台能力分页、失败重试、内容润色和异步封面生成；测试环境先使用测试 Provider，避免意外费用。
- 验证公告发布、已读操作触发未读刷新，多标签页/多设备并存、退出不重连、空闲心跳正常。
- 确认原有博主申请、文件列表、日志分析、树洞弹幕、备案信息和移动端导航继续可用。
- 外部 Dify/模型服务的真实凭证和网络连通性需在部署环境验证，不能用本地模拟测试代替。
