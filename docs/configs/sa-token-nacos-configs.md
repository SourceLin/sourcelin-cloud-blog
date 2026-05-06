# Sourcelin Sa-Token + Nacos 配置基线

> 最后更新：2026-04-04  
> 版本：1.5  
> 适用范围：`sourcelin-auth`、`sourcelin-gateway`、`sourcelin-common-security`、`sourcelin-system`、`sourcelin-blog`、`sourcelin-job`、`sourcelin-file`  
> 定位：Sa-Token 改造完成后，认证、授权、会话与 Nacos 配置的**唯一权威基线**。

---

## 0. 2026-04-01 更新摘要

### 0.1 变更内容

1. 在共享 Sa-Token 基线中增加 `is-read-cookie: false`，避免跨域 token 互相干扰。
2. 明确管理端 / 博客端登录域划分与会话隔离要求。
3. 细化网关对公开博客只读接口的白名单规则。
4. 补充 Nacos `shared-configs` 未生效的**根因**说明。

### 0.2 刷新/登出问题的真实根因

用户侧现象：

1. 在后台登录管理端。
2. 在前台登录博客。
3. 刷新后台页面。
4. 后台会话失效或表现为已登出。

表面解释是「博客的 `satoken` Cookie 干扰了管理端校验」——这句话对，但不完整。

2026-04-01 排查得到的**运行时根因**是：

1. 预期中的共享配置实际放在 `BLOG_GROUP/application-dev.yml`。
2. 多个服务在 `bootstrap.yml` 里使用了**字符串形式**的 `shared-configs`：

```yaml
shared-configs:
  - application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
```

3. 在 Spring Cloud Alibaba Nacos 中，这种写法会**回退到 `DEFAULT_GROUP`**。
4. 因此运行中的服务**并没有**真正从 `BLOG_GROUP` 加载共享 Sa-Token 基线。
5. 生效的 Sa-Token 仍停留在默认值，例如：
   - `tokenName=satoken`
   - `isReadCookie=true`
6. 一旦 `isReadCookie=true`，浏览器在同域请求时会自动带上前台的 `satoken` Cookie，可能覆盖或干扰后台校验。

### 0.3 必须的 bootstrap 修正

所有服务对共享配置必须使用**显式 `data-id + group` 绑定**：

```yaml
spring:
  cloud:
    nacos:
      config:
        group: BLOG_GROUP
        file-extension: yml
        shared-configs:
          - data-id: application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
            group: ${spring.cloud.nacos.config.group}
```

**强制要求**：不要依赖 `shared-configs` 的隐式继承行为。

### 0.4 运行时验收标准

重启后，必须通过运行时证据证明修复已生效：

1. `/actuator/env` 中出现 `bootstrapProperties-application-dev.yml,BLOG_GROUP`
2. `/actuator/configprops` 中可见：
   - `tokenName=Authorization`
   - `isReadCookie=false`
   - `isReadHeader=true`

---

## 1. 架构基线

1. 认证、会话与授权必须以 **`Sa-Token + Redis`** 为唯一事实来源。
2. 网关是统一流量入口，必须做**路由级登录校验**。
3. 业务服务执行权限码校验与业务归属校验。
4. 管理端用户与博客端用户为**不同登录域**，禁止共用上下文工具类。
5. 内部服务调用必须使用明确的内部调用鉴权，仅在确有需要时转发用户上下文。

### 1.1 目标请求路径

```text
客户端
  -> 网关
     - 白名单放行
     - 路由级登录校验
     - 管理端/博客域分发
     - 最小化身份请求头转发
  -> 认证 / 系统 / 博客 / 任务 / 代码生成 / 文件
     - Sa-Token 运行时
     - Redis 会话
     - @SaCheckLogin / @SaCheckPermission / @SaCheckRole
     - 归属与数据权限约束
```

### 1.2 域划分

1. `StpAdminUtil` 仅用于管理端 Controller 与管理端服务。
2. `StpBlogUtil` 仅用于前台博客 Controller 与前台博客服务。
3. `SecurityUtils` 不得充当跨域通用助手。
4. 前台博客代码必须使用 `BlogLoginAccessor` 或等效的博客域助手。

---

## 2. 职责边界

### 2.1 网关

网关**必须**做：

1. Nacos 服务发现与转发。
2. 白名单放行。
3. 按路由前缀通过 `SaReactorFilter` 做登录校验。
4. 快速拒绝缺失或已过期的登录态。
5. 仅向下游转发**最少必要**的身份请求头。

网关**禁止**做：

1. 权限码校验。
2. 菜单或资源授权。
3. 业务归属校验。
4. 完整用户对象的拼装。

### 2.2 认证服务

认证服务**必须**做：

1. 校验凭据、验证码、黑名单与账号状态。
2. 通过 Sa-Token 创建 `admin` 或 `blog` 登录态。
3. 在 Sa-Token 会话中存放可序列化的会话快照。
4. 返回统一的 token 契约。

认证服务**禁止**做：

1. 替代网关的登录校验。
2. 仅内存、不落库的会话状态。
3. 依赖旧版 JWT 解析逻辑。

### 2.3 业务服务

业务服务**必须**做：

1. 在合适位置使用 `@SaCheckPermission`、`@SaCheckRole`、`@SaCheckLogin`。
2. 使用当前域正确的登录访问器。
3. 在权限校验之外强制执行归属约束。
4. 对仅内部可调用的接口强制执行 `@InnerAuth`。

---

## 3. Sa-Token 基线

### 3.1 共享 `application-dev.yml`

Nacos 中的 `application-dev.yml` 必须包含跨服务基线：

```yaml
spring:
  autoconfigure:
    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

feign:
  sentinel:
    enabled: true
  okhttp:
    enabled: true
  httpclient:
    enabled: false
  client:
    config:
      default:
        connectTimeout: 10000
        readTimeout: 10000
  compression:
    request:
      enabled: true
      min-request-size: 8192
    response:
      enabled: true

sa-token:
  token-name: Authorization
  token-prefix: Bearer
  timeout: 2592000
  active-timeout: -1
  is-concurrent: true
  is-share: true
  is-log: false
  token-style: uuid
  is-jwt: false
  is-read-cookie: false
  is-read-header: true

management:
  endpoints:
    web:
      exposure:
        include: '*'
```

### 3.2 关键规则

1. `is-share: true` 用于分布式会话共享。
2. Redis 是持久化手段；`is-share` 不能替代 Redis。
3. `is-read-cookie: false` **必须**，避免前台 `satoken` Cookie 干扰后台校验。
4. 唯一接受的 token 形态为：`Authorization: Bearer <token>`。

### 3.3 会话快照

管理端会话快照至少包含：

1. `userId`
2. `username`
3. `nickName`
4. `deptId`
5. `roles`
6. `permissions`
7. `loginType=admin`

博客端会话快照至少包含：

1. `userId`
2. `username`
3. `nickName`
4. `status`
5. `loginType=blog`

---

## 4. Nacos 配置布局

### 4.1 必备配置集

1. `application-dev.yml`
2. `sourcelin-gateway-dev.yml`
3. `sourcelin-auth-dev.yml`
4. `sourcelin-system-dev.yml`
5. `sourcelin-blog-dev.yml`
6. 其他服务专属的 `*-dev.yml`（按需）

### 4.2 分组使用规则

Nacos 中可存在多个分组，当前生效基线为：

1. **`BLOG_GROUP`** 为 Sourcelin 当前权威分组。
2. **`DEFAULT_GROUP`** 可能仍存有历史或兜底数据，**不得**作为当前 Sa-Token 基线的依赖。
3. `application-dev.yml` 必须存在于 **`BLOG_GROUP`**。
4. 服务运行时必须**显式**从 `BLOG_GROUP` 加载 `application-dev.yml`。

### 4.3 Bootstrap 加载规则

各服务 `bootstrap` 应保持精简，仅声明：

1. 服务名
2. profile
3. Nacos 发现地址与分组
4. Nacos 配置地址、分组与 `file-extension`
5. 对 `application-${profile}.yml` 的**显式**共享配置引用

推荐写法：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        group: BLOG_GROUP
        server-addr: 127.0.0.1:8848
      config:
        group: BLOG_GROUP
        server-addr: 127.0.0.1:8848
        file-extension: yml
        shared-configs:
          - data-id: application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
            group: ${spring.cloud.nacos.config.group}
```

**不要**使用字符串列表形式的 `shared-configs`。

### 4.4 运行时教训

若 `shared-configs` 写成纯字符串列表，运行时可能**静默**加载：

1. `bootstrapProperties-application-dev.yml,DEFAULT_GROUP`

而不是：

1. `bootstrapProperties-application-dev.yml,BLOG_GROUP`

仅此差异就足以让整个 Sa-Token 运行时基线失效。

### 4.5 中文乱码预防（Nacos / YAML）

项目中若 Nacos 配置或 YAML 内出现**中文注释、中文业务配置值**，常见乱码原因与对策如下：

| 场景 | 建议 |
|------|------|
| Nacos 控制台编辑 | 使用 **UTF-8** 保存；避免从记事本等以 **GBK/ANSI** 保存后再粘贴。 |
| 导入 SQL / 批量脚本 | 确保脚本文件为 **UTF-8**（建议带 BOM 的编辑器在团队内统一约定）。 |
| JVM 启动 | 生产环境建议显式指定 `-Dfile.encoding=UTF-8`（与 `pom.xml` 中 `project.build.sourceEncoding` 一致）。 |
| HTTP 响应中文 | 网关与各服务 `spring.http.encoding.charset=UTF-8`、`enabled=true`（若通过 Nacos 覆盖，勿用错误编码）。 |

**原则**：所有进入 Nacos 的 YAML/Properties，均以 **UTF-8** 为唯一编码；发现乱码时优先核对「文件/控制台保存编码」与「Nacos 中该配置的发布编码」，而非先改业务代码。

---

## 5. 网关专属 Nacos 配置

网关目标配置示例：

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          lowerCaseServiceId: true
          enabled: true
      routes:
        - id: sourcelin-auth
          uri: lb://sourcelin-auth
          predicates:
            - Path=/auth/**
          filters:
            - CacheRequestFilter
            - ValidateCodeFilter
            - StripPrefix=1
        - id: sourcelin-job
          uri: lb://sourcelin-job
          predicates:
            - Path=/schedule/**
          filters:
            - StripPrefix=1
        - id: sourcelin-system
          uri: lb://sourcelin-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
        - id: sourcelin-file
          uri: lb://sourcelin-file
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=1
        - id: sourcelin-blog
          uri: lb://sourcelin-blog
          predicates:
            - Path=/blog/**
          filters:
            - StripPrefix=1

security:
  captcha:
    enabled: true
    type: math
  xss:
    enabled: true
    excludeUrls:
      - /system/notice
  ignore:
    whites:
      - /auth/login
      - /auth/register
      - /auth/logout
      - /auth/refresh
      - /code
      - /*/v2/api-docs
      - /*/v3/api-docs
      - /csrf
      - /front/articles
      - /front/articles/*
      - /front/categories
      - /front/categories/*
      - /front/categories/*/articles
      - /front/tags
      - /front/tags/*
      - /front/tags/*/articles
      - /front/links
      - /blog/front/links/apply
      - /front/notices
      - /front/notices/*
      - /front/stats
      - /front/hot/searches
      - /front/hot/categories
      - /front/hot/articles
      - /front/articles/search
      - /front/tags/search
      - /front/categories/search
      - /front/search/hot
      - /front/search/suggestions
      - /front/config
      - /front/config/siteInfo

sa-token:
  is-check: true
```

### 5.1 网关白名单规则

1. **不要**整段放行 `/blog/**`。
2. **不要**整段放行 `/front/**`。
3. 仅将**明确公开**的接口列入白名单；路径前缀须与网关 `SaHolder` 收到的 **真实 URI** 一致（常见为 `/blog/front/...`，勿与仅含 `/front/...` 的旧示例混用）。
4. 默认上前台写操作应尽量收紧；**树洞匿名发帖/评论/点赞**为产品例外时，仅放行**具体 Controller 路径**，业务层仍由 `FrontCommentController` / `FrontTreeholeController` 区分 `source` 与登录态，文章评论不因同表而误放开匿名。

### 5.2 树洞与匿名评论必配白名单（`/blog/front` 前缀）

网关 [SaTokenGatewayConfiguration](../../sourcelin-gateway/src/main/java/com/sourcelin/gateway/config/SaTokenGatewayConfiguration.java) 对 `/blog/front/**` 走博客域 `checkLogin()`，**未登录请求必须命中 `security.ignore.whites` 才会放行**。

请在 Nacos 的 `sourcelin-gateway-dev.yml`（或你环境对应的网关 data-id，`BLOG_GROUP`）中，在现有「博客公开内容」白名单块内**至少**追加（Ant 匹配，见 `StringUtils.isMatch`）：

```yaml
      - /blog/front/treeholes
      - /blog/front/treeholes/**
      - /blog/front/treehole
      - /blog/front/treehole/**
      - /blog/front/comments
      - /blog/front/comments/**
```

说明：

- 仅有 `/blog/front/comments` **一行**时，**无法**匹配 `POST /blog/front/comments/like/{id}` 等多段路径；必须增加 `/blog/front/comments/**`，否则匿名点赞树洞评论会在网关层被拦截。
- 未配置 `treeholes` / `treehole` 时，匿名用户无法访问树洞列表与发帖接口。
- 修改后需**发布 Nacos 配置并重启 `sourcelin-gateway`**。

可复制片段以本文当前白名单示例为准，不再单独维护临时 snippets 文件。

---

## 6. 目标网关鉴权策略

网关必须启用 `SaReactorFilter`。

策略示意：

```java
public SaReactorFilter saReactorFilter() {
    return new SaReactorFilter()
        .addInclude("/**")
        .setAuth(obj -> {
            String path = SaHolder.getRequest().getRequestPath();
            if (isWhitePath(path)) {
                return;
            }
            if (path.startsWith("/front/")
                || path.startsWith("/app/")
                || path.startsWith("/blog/front/")) {
                StpBlogUtil.stpLogic.checkLogin();
                return;
            }
            if (path.startsWith("/system/")
                || path.startsWith("/code/")
                || path.startsWith("/schedule/")
                || path.startsWith("/blog/admin/")
                || path.startsWith("/file/")) {
                StpAdminUtil.stpLogic.checkLogin();
            }
        });
}
```

### 6.1 为何采用该目标

1. WebFlux 不是跳过 Sa-Token 的理由；Reactor 场景有对应 Starter。
2. 仅检查请求头是否存在不足以校验真实登录态。
3. 业务服务在网关校验之后仍需保留权限注解。

---

## 7. 注解与访问规则

### 7.1 强制注解

1. 管理端受保护接口：`@SaCheckPermission` 或 `@SaCheckLogin`
2. 博客写接口：`@SaCheckLogin(type = "blog")` 或等效博客域守卫
3. 内部接口：`@InnerAuth`
4. 公开接口：不加鉴权注解，但必须在文档或清单中**显式**列为公开

### 7.2 归属规则

权限校验**不能**替代归属校验。

示例：

1. 博客用户删除收藏记录时，必须校验记录属于当前用户。
2. 博客用户删除关注关系时，必须校验归属。
3. 写接口不得仅依赖客户端提交的 ID。

---

## 8. 内部调用基线

### 8.1 必备请求头

内部调用必须使用：

1. `from-source: inner`

仅在下游**明确需要**终端用户上下文时才转发用户相关头：

1. `user_id`
2. `username`
3. `user_key`

### 8.2 规则

1. `@InnerAuth` 接口必须拒绝非内部来源。
2. 内部鉴权不得依赖公网网关白名单绕过。
3. Feign 传递应最小化且显式。

---

## 9. 前端 Token 契约

管理端与平台端均须遵守：

```json
{
  "token": "...",
  "tokenName": "Authorization",
  "tokenPrefix": "Bearer",
  "expiresIn": 2592000,
  "loginType": "admin|blog"
}
```

规则：

1. 不要解析旧版 `access_token`。
2. 请求头始终按 `tokenName` + `tokenPrefix` + `token` 组装。
3. `401` 视为未认证，`403` 视为无权限。

---

## 10. 验证清单

配置变更后请验证：

1. 网关能在不依赖旧版认证过滤器的情况下校验登录态。
2. 管理端 token 不能以博客身份访问仅博客写接口。
3. 博客 token 不能通过管理域助手读取。
4. 内部接口拒绝直接外网调用。
5. 会话数据经 Redis 在服务重启后仍可恢复。
6. Nacos 服务配置、共享配置与 bootstrap 命名一致。
7. 先登录管理端，再登录博客，刷新管理端页面：管理端应保持登录。
8. `/actuator/env` 证明 `application-dev.yml` 自 `BLOG_GROUP` 加载。
9. `/actuator/configprops` 证明生效值为 `Authorization` + `isReadCookie=false`。

---

## 11. 与当前仓库对齐情况

截至 2026-04-01，除 bootstrap 分组修正外，仓库仍存在架构层面的待补齐项。

相关长期文档入口见：

- `docs/DOCS_INDEX.md`

---

## 12. 待办：Nacos / Bootstrap

### 12.1 需保持一致的文件

1. `sourcelin-auth/src/main/resources/bootstrap.yml`
2. `sourcelin-gateway/src/main/resources/bootstrap.yml`
3. `sourcelin-modules/sourcelin-system/src/main/resources/bootstrap.yml`
4. `sourcelin-modules/sourcelin-blog/src/main/resources/bootstrap.yml`
5. `sourcelin-modules/sourcelin-job/src/main/resources/bootstrap.yml`
6. `sourcelin-modules/sourcelin-file/src/main/resources/bootstrap.yml`

### 12.2 必备 bootstrap 片段

```yaml
spring:
  cloud:
    nacos:
      config:
        group: BLOG_GROUP
        file-extension: yml
        shared-configs:
          - data-id: application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
            group: ${spring.cloud.nacos.config.group}
```

### 12.3 验证命令

Nacos 配置生效并重启服务后：

```bash
# 确认 BLOG_GROUP 下存在 application-dev.yml
curl -s "http://localhost:8848/nacos/v1/cs/configs?dataId=application-dev.yml&group=BLOG_GROUP"

# 查看服务注册
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=sourcelin-gateway"

# 确认运行时加载了 BLOG_GROUP 的共享配置
curl -s "http://localhost:9201/actuator/env"

# 查看 Sa-Token 生效项
curl -s "http://localhost:9201/actuator/configprops"
```

建议人工检索：

1. 在 `/actuator/env` 中搜索 `bootstrapProperties-application-dev.yml,BLOG_GROUP`
2. 在 `/actuator/configprops` 中搜索 `tokenName`、`isReadCookie`、`isReadHeader`

---

## 13. 变更记录

### v1.3（2026-04-01）

1. 补充根因：字符串形式 `shared-configs` 会回退到 `DEFAULT_GROUP`
2. 增加强制 bootstrap 修正：显式 `data-id + group`
3. 增加运行时验收：`/actuator/env` 与 `/actuator/configprops`

### v1.2（2026-04-01）

1. 增加 `is-read-cookie: false`，修复跨域 token 干扰
2. 增加跨域 token 隔离的验证要求

### v1.1（2026-04-01）

1. 细化公开博客 API 的网关白名单
2. 增加 `StpInterface` 条件加载说明
3. 合并 Sa-Token 与 Nacos 基线定义

### v1.4（2026-04-02）

1. 全文改为中文，便于团队统一查阅
2. 增加「中文乱码预防」小节（Nacos / YAML / JVM 编码）
