# sourcelin.cn 单域名部署文档（前台 + 后台）

## 1. 部署目标

- 域名：`http://sourcelin.cn`（可同时绑定 `www.sourcelin.cn`）
- 前台站点：`/`
- 后台站点：`/admin/`
- 统一反向代理入口：`Nginx -> sourcelin-gateway(127.0.0.1:8080)`

---

## 2. 文件夹命名规范（已统一）

前端打包产物目录固定如下：

- 后台管理：`sourcelin-ui-admin`
- 博客前台：`sourcelin-ui-platform`

对应代码配置：

- [sourcelin-ui-admin/vite.config.ts](../../sourcelin-ui/sourcelin-ui-admin/vite.config.ts)
- [sourcelin-ui-platform/vite.config.ts](../../sourcelin-ui/sourcelin-ui-platform/vite.config.ts)

---

## 3. 服务器目录结构（建议）

```text
/opt/sourcelin/
├─ sourcelin-ui-platform/        # 前台静态文件（index.html + assets）
├─ sourcelin-ui-admin/           # 后台静态文件（index.html + assets）
└─ services/                     # Java 服务目录（可选）
   ├─ sourcelin-gateway/
   ├─ sourcelin-auth/
   ├─ sourcelin-system/
   ├─ sourcelin-blog/
   └─ sourcelin-monitor/         # 监控服务（可选，默认 9100）
```

Nginx 配置文件建议放置：

```text
/etc/nginx/conf.d/sourcelin.cn.conf
```

---

## 4. 前端构建（本地执行）

### 4.1 构建后台管理

```bash
cd sourcelin-ui/sourcelin-ui-admin
pnpm install
pnpm run build -- --base=/admin/
```

说明：

- `--base=/admin/` 必须保留，否则后台资源会从根路径加载，和前台冲突。
- 构建后产物目录：`sourcelin-ui/sourcelin-ui-admin/sourcelin-ui-admin/`

### 4.2 构建博客前台

```bash
cd sourcelin-ui/sourcelin-ui-platform
npm install
npm run build
```

构建后产物目录：`sourcelin-ui/sourcelin-ui-platform/sourcelin-ui-platform/`

---

## 5. 上传与发布（服务器执行）

先创建目录：

```bash
mkdir -p /opt/sourcelin/sourcelin-ui-admin
mkdir -p /opt/sourcelin/sourcelin-ui-platform
```

把本地构建产物完整覆盖到服务器对应目录：

- 后台产物 -> `/opt/sourcelin/sourcelin-ui-admin/`
- 前台产物 -> `/opt/sourcelin/sourcelin-ui-platform/`

建议采用“先传新目录再原子替换”的方式，避免发布窗口白屏。

---

## 6. Nginx 配置与启用

本仓库现成模板：

- [docs/configs/nginx-sourcelin-single-domain.conf](../configs/nginx-sourcelin-single-domain.conf)

部署步骤：

```bash
cp /your-path/nginx-sourcelin-single-domain.conf /etc/nginx/conf.d/sourcelin.cn.conf
nginx -t
systemctl reload nginx
```

如果你的 Nginx 不是 systemd 管理，使用：

```bash
nginx -s reload
```

---

## 7. 后端服务启动顺序

1. MySQL、Redis、Nacos
2. `sourcelin-gateway`（必须）
3. `sourcelin-auth`
4. `sourcelin-system`
5. `sourcelin-blog`
6. `sourcelin-monitor`（可选）

说明：前端所有 API 都通过网关转发，网关未启动时页面会打开但接口全部失败。

---

## 8. 验收清单（上线后）

```text
http://sourcelin.cn/                 # 前台首页可访问
http://sourcelin.cn/admin/           # 后台登录页可访问
http://sourcelin.cn/prod-api/...     # 后台接口可通
http://sourcelin.cn/blog-api/...     # 前台接口可通
```

关键检查项：

- 前台和后台页面都无静态资源 404
- 后台 Network 中 JS/CSS 请求路径是 `/admin/assets/...`
- Nginx 日志无大量 404/502

---

## 9. 常见问题

### 9.1 后台打开白屏或样式错乱

根因通常是后台构建未使用 `--base=/admin/`。  
处理：重新执行后台构建命令并重新发布后台静态目录。

### 9.2 页面能打开但接口 502

根因通常是网关或后端服务未启动。  
处理：先确认 `127.0.0.1:8080` 可用，再检查网关到各业务服务的注册与路由。

### 9.3 前台路由刷新 404

根因通常是 Nginx 未配置 `try_files ... /index.html`。  
处理：确认使用了本文档引用的 Nginx 模板。
