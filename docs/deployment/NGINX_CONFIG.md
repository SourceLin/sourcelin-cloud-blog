# Nginx 配置基线

本文档描述生产或联调环境下 Nginx 的职责边界。Nginx 只负责静态资源和前缀转发，不承担 JWT 解析、登录态判断、权限校验。

## 基本原则

1. `Nginx -> Gateway -> Auth/System/Blog` 是唯一入口链路。
2. 登录态校验由网关 `SaReactorFilter` 负责，不由 Nginx 负责。
3. Nginx 不应内置任何“后台 JWT / 前台非 JWT”的分裂假设。
4. `/prod-api` 面向后台管理端，`/blog-api` 面向博客前台。

## 示例配置

```nginx
worker_processes 1;

events {
    worker_connections 1024;
}

http {
    include mime.types;
    default_type application/octet-stream;
    sendfile on;
    keepalive_timeout 65;

    upstream sourcelin_gateway {
        server 127.0.0.1:8080;
    }

    server {
        listen 80;
        server_name localhost;

        location /admin/ {
            root /home/sourcelin/projects/sourcelin-admin;
            try_files $uri $uri/ /admin/index.html;
            index index.html index.htm;
        }

        location / {
            root /home/sourcelin/projects/sourcelin-blog;
            try_files $uri $uri/ /index.html;
            index index.html index.htm;
        }

        location /prod-api/ {
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://sourcelin_gateway/;
        }

        location /blog-api/ {
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://sourcelin_gateway/;
        }

        if ($uri ~ "/actuator") {
            return 403;
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
            root html;
        }
    }
}
```

## 路由映射

| 前端请求 | 网关路径 | 目标服务 | 用途 |
|------|------|------|------|
| `/prod-api/system/**` | `/system/**` | `sourcelin-system` | 后台管理 |
| `/prod-api/auth/**` | `/auth/**` | `sourcelin-auth` | 后台登录、登出、续期 |
| `/blog-api/auth/**` | `/auth/**` | `sourcelin-auth` | 前台登录、注册、续期 |
| `/blog-api/code` | `/code` | 路由配置决定 | 验证码 |
| `/blog-api/blog/front/**` | `/blog/front/**` | `sourcelin-blog` | 前台业务 |

## 安全约束

1. 登录、注册、验证码仍走网关统一入口。
2. 网关白名单只允许真正公开接口，不能把 `/front/**`、`/blog/**`、`/blog-api/**` 整段放开。
3. `/blog/front/user/**`、`/blog/front/follow/**`、`/blog/front/collect/**` 等私有接口不得通过 Nginx 或白名单绕过登录校验。
4. 前台密码传输、验证码消费、登录态创建细节以 [configs/sa-token-nacos-configs.md](../configs/sa-token-nacos-configs.md) 为准。

## 说明

旧文档中“网关 AuthFilter 仅处理后台 JWT、前台不参与 JWT 解析”的表述已废弃，不再适用于当前架构。
