# Sourcelin 三端 API 请求规划

> 日期：2026-05-20
> 适用范围：博客前台、Uniapp 移动端、管理后台

## 一、统一域名与前缀

| 端 | 运行形态 | Base URL | API 前缀 | 说明 |
|---|---|---|---|---|
| 博客前台 | Web/H5，部署在 `https://sourcelin.cn/` | 同源 | `/blog-api` | 当前 `.env.production` 已使用 `VITE_APP_BASE_API=/blog-api` |
| Uniapp 移动端 | 微信小程序/H5/App | `https://sourcelin.cn` | `/blog-api` | 小程序必须使用完整域名，当前 `src/config/env.ts` 已配置 |
| 文件资源 | Web/移动端共用 | `https://sourcelin.cn` | `/file` | 远程文件服务前缀，需按后端权限策略访问 |
| 管理后台 | Web Admin | `https://sourcelin.cn` 或同源 | `/prod-api` | Nginx 将 `/prod-api/**` 反向代理到网关根路径，后台生产环境沿用该前缀 |

## 二、接口分层规则

### 2.1 前台与移动端

前台和移动端只调用对外前台接口：

| 能力 | 路径示例 |
|---|---|
| 首页聚合 | `GET /blog-api/front/home` |
| 文章列表 | `GET /blog-api/front/articles` |
| 文章详情 | `GET /blog-api/front/articles/{id}` |
| 搜索 | `GET /blog-api/front/articles/search` |
| 热门 | `GET /blog-api/front/hot/articles` |
| 分类 | `GET /blog-api/front/categories` |
| 标签 | `GET /blog-api/front/tags` |
| 评论 | `GET/POST /blog-api/front/comments` |
| 说说 | `GET /blog-api/front/says` |
| 树洞 | `GET /blog-api/front/treeholes` |
| 移动端验证码 | `GET /blog-api/auth/captcha?loginType=mini` |
| 移动端账号登录 | `POST /blog-api/auth/login`，body 内 `loginType=mini` |
| 移动端微信快捷登录 | `POST /blog-api/auth/wechat/mini/login` |
| 移动端微信绑定已有账号 | `POST /blog-api/auth/wechat/mini/bind` |
| 当前用户 | `GET /blog-api/front/user/info` |
| 个人资料 | `GET /blog-api/front/user/profile` |

前台与移动端禁止直接调用 `/system/**`、`/blog/admin/**`、`/monitor/**` 等管理域接口。

移动端与 Web 前台复用博客用户体系，但登录类型不再复用 `blog`。Uniapp 端统一使用 `mini`，后端将 `mini` 映射到博客用户登录态，便于后续单独统计小程序登录、验证码策略和风控规则。

### 2.2 管理后台

管理后台需要调用认证、系统、博客管理接口：

| 能力 | 路径示例 | 当前远程状态 |
|---|---|---|
| 登录与验证码 | `/prod-api/auth/**` | Nginx `rewrite ^/prod-api/(.*)$ /$1` 后转发网关 |
| 系统管理 | `/prod-api/system/**` | 需 Nginx/网关暴露 |
| 博客管理 | `/prod-api/blog/**` | 后台管理域接口 |
| 监控 | `/prod-api/monitor/**` | 后台管理域接口 |

后台不应复用 `/blog-api`，因为 `/blog-api` 当前只验证到前台博客接口可用。

## 三、当前代码配置

| 项目 | 配置文件 | 当前配置 |
|---|---|---|
| 博客前台 | `sourcelin-ui/sourcelin-ui-platform/.env.production` | `VITE_APP_BASE_API=/blog-api`，`VITE_FILE_BASE_API=/file` |
| Uniapp 移动端 | `sourcelin-ui/sourcelin-ui-uniapp/src/config/env.ts` | `baseURL=https://sourcelin.cn`，`apiPrefix=/blog-api` |
| 管理后台 | `sourcelin-ui/sourcelin-ui-admin/.env.production` | `VITE_APP_BASE_API=/prod-api` |
| 认证中心微信配置 | Nacos `application-*.yml` 或环境变量 | `sourcelin.mini-program.app-id`、`sourcelin.mini-program.app-secret` |

认证中心服务专属 Nacos 示例：

```yaml
sa-token:
  is-check: false

sourcelin:
  email:
    host: <SMTP_HOST>
    port: <SMTP_PORT>
    username: <SMTP_USERNAME>
    password: <SMTP_AUTH_CODE>
    from: <SENDER_NAME_AND_EMAIL>
    use-ssl: true
  captcha:
    email-code-expire: 300
    email-code-interval: 60
    admin-enabled: false
  mini-program:
    app-id: <WECHAT_MINI_APP_ID>
    app-secret: <WECHAT_MINI_APP_SECRET>
    code2-session-url: https://api.weixin.qq.com/sns/jscode2session
    bind-token-ttl-seconds: 600
```

## 四、上线检查项

1. 微信小程序后台需要配置 request 合法域名；正式版建议改为 HTTPS 域名，微信正式小程序通常不允许 HTTP 请求域名。
2. Nginx 需确保 `/blog-api` 代理到博客前台接口所在网关或服务，且返回标准 `ApiResponse`。
3. Nginx 需保留 `/prod-api -> gateway` 的 rewrite 规则，后台生产包依赖该前缀登录和访问管理接口。
4. 文件资源中仍可能返回 `127.0.0.1` 地址，需后端或文件服务将文件外链替换为 `https://sourcelin.cn/file/**` 或 HTTPS 文件域名。
5. 微信快捷登录上线前，认证中心必须在配置中心补齐 `sourcelin.mini-program.app-id` 与 `sourcelin.mini-program.app-secret`，否则 `/auth/wechat/mini/login` 会返回配置缺失错误。
