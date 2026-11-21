# AI 与实时消息架构

AI 和消息服务均为可选能力。原有博客、博主申请、文件管理、树洞及移动端能力开关继续独立工作。

## 服务与边界

- `sourcelin-ai`（9205）：Dify/兼容 Chat Completions/兼容 Images Provider、能力配置、会话、任务和调用记录。
- `sourcelin-message`（9400）：一次性票据核销、SSE 多连接、Redis 节点路由与广播，不连接数据库。
- `sourcelin-api-ai`、`sourcelin-api-message`：跨服务 DTO 与 Feign 契约；业务模块不直接依赖服务实现。
- 博客 `/front/ai/**`、`/admin/blog/ai/**` 是对外入口。AI 服务端点均使用 `@InnerAuth`，公网网关删除伪造的 `from-source` 请求头。
- Controller 返回业务对象、`ListResult`、`PageResult` 或明确流式类型，响应统一由公共 Advice 包装。

## AI 请求与状态

`前台/后台 -> 博客权限和业务编排 -> Feign/流式中继 -> AI 能力路由 -> Provider`。

文章标题、摘要、标签和润色使用同步调用，封面使用异步任务。状态为 `pending/running/succeeded/failed`，通过受权任务查询和失败重试操作管理。执行器拒绝任务时持久化失败状态。重试按状态和重试次数原子更新，只有抢到任务的请求会投递执行。

前台文章 AI 能力沿用博主身份要求。已有文章上下文、结果采纳和任务查询都校验归属；身份统一为 `blog:<id>` 或 `admin:<id>`，后台依原有文章编辑权限操作。采纳富文本经过现有清洗器；生成本地表单建议后仍需保存文章。

聊天支持匿名和登录用户。用户提供的会话标识在服务端加入身份域，防止跨用户复用会话；文章详情页仅对可读正文注入摘要上下文。

API 密钥由管理后台写入，列表不返回密钥，编辑留空保持原值。数据库采用 AES-GCM 随机 nonce 加密，部署密钥通过 `AI_CONFIG_ENCRYPTION_KEY` 提供，禁止将密钥写入 SQL、代码或版本库。

## 文章知识库

文章新增、修改和删除在事务提交后发布知识库同步事件，独立异步监听器根据公开发布状态决定同步或删除；内容哈希避免无变化重传。后台文章接口支持单篇、批量同步。

Dify 的 Endpoint、Dataset ID 和 Knowledge API Key 由管理员配置。公开可读文章才可写入知识库，私密、待审核及已删除内容不写入。外部索引完成状态以 Dify 为准，提交成功不能解释为索引完成。

## SSE 与业务发布

1. 已登录客户端调用 `/auth/apply-ticket?loginType=blog|mini|admin` 申请 10 秒票据。
2. 客户端请求网关 `/message/stream?ticket=...`，网关 StripPrefix 后转到消息服务 `/stream`。
3. 消息服务使用 Redis Lua 原子读取并删除票据，身份包含用户域。
4. 同一身份可保留多个连接；连接关闭只清理自身，不关闭其他标签页或设备。
5. Redis 路由按身份和节点登记时间戳，发布者向仍活跃的节点投递，未命中时回落广播。
6. 每 20 秒发送命名 `heartbeat` 事件。客户端持续 65 秒无数据时重连，重连成功会补查未读。退出或切换账号使旧的状态检查和票据请求失效。Uniapp 进入后台断开连接。
7. 公告发布、下线、更新、删除以及标记已读或全部已读触发 `unread_changed`。博客公告发送至 `blog` 域，系统通知发送至 `admin` 域。客户端按当前登录身份查询未读，事件只携带类型。

小程序分块流在统一 request 基座建立，增量解码保留跨块 UTF-8 汉字及 Emoji。H5 使用 EventSource。前后台请求头与网关统一为 `Authorization: Bearer <token>`；前台保留登录响应提供的 Header 适配能力。

## 配置与验证

部署步骤见 [AI 与消息部署](../guides/AI_MESSAGE_SETUP.md)，数据库初始化见 [SQL 说明](../sql/README.md)。

回归覆盖：请求标识校验、结果/文章/任务归属、异步队列提交、密钥加密、远程图片地址边界、SSE 多连接与账号域、真实 HTTP 错误体及流式首包、浏览器退出/重连、逐字节 UTF-8 SSE 解析。

移动端保留 3 原生 Tab + 圈子液态 Tab、原有 401 处理和审核安全模式；不导入缺失页面入口或无实现的规则引用。
