# sourcelin.cn Docker 部署文档（单域名，按当前服务器实况）

## 1. 目标与范围

- 域名：`sourcelin.cn`（可同时绑定 `www.sourcelin.cn`）
- 前台：`/`
- 后台：`/admin/`
- 网关入口：`127.0.0.1:8080`
- 当前 Compose 管理服务：`redis_local`、`nacos`、`gateway`、`auth`、`system`、`blog`、`file`、`monitor`
- 当前服务器的 `mysql`、`nginx` 为独立已有容器，不在本文这份 Compose 内统一管理

---

## 2. 产物命名（固定）

- 后台前端打包目录：`sourcelin-ui-admin`
- 前台前端打包目录：`sourcelin-ui-platform`

说明：后台构建必须使用 `--base=/admin/`，否则 `/admin/` 下静态资源会错路由。

---

## 3. 服务器目录结构

当前服务器实际目录：

- Compose 目录：`/home/sourcelin/deploy/compose`
- Jar 目录：`/home/sourcelin/deploy/jars`
- 日志目录：`/home/sourcelin/deploy/logs`
- 数据目录：`/home/sourcelin/deploy/data`
- Nginx 静态目录：`/home/sourcelin/nginx/html`

```text
/home/sourcelin/
├─ deploy/
│  ├─ compose/
│  │  └─ docker-compose.yml
│  ├─ jars/
│  │  ├─ sourcelin-gateway.jar
│  │  ├─ sourcelin-auth.jar
│  │  ├─ sourcelin-system.jar
│  │  ├─ sourcelin-blog.jar
│  │  ├─ sourcelin-file.jar
│  │  └─ sourcelin-monitor.jar
│  ├─ data/
│  │  ├─ redis-local/
│  │  └─ nacos/
│  └─ logs/
│     ├─ gateway/
│     ├─ auth/
│     ├─ system/
│     ├─ blog/
│     ├─ file/
│     └─ monitor/
└─ nginx/
   └─ html/
      ├─ sourcelin-ui-admin/
      └─ sourcelin-ui-platform/
```

---

## 4. 前端构建与上传

### 4.1 后台

```bash
cd sourcelin-ui/sourcelin-ui-admin
pnpm install
pnpm run build -- --base=/admin/
```

上传目录：

- 本地 `sourcelin-ui/sourcelin-ui-admin/sourcelin-ui-admin/`
- 服务器 `/opt/sourcelin-docker/ui/sourcelin-ui-admin/`

### 4.2 前台

```bash
cd sourcelin-ui/sourcelin-ui-platform
npm install
npm run build
```

上传目录：

- 本地 `sourcelin-ui/sourcelin-ui-platform/sourcelin-ui-platform/`
- 服务器 `/opt/sourcelin-docker/ui/sourcelin-ui-platform/`

---

## 5. 后端 Jar 准备

在代码仓库根目录构建：

```bash
mvn clean package -DskipTests
```

将以下 jar 放入服务器 `/home/sourcelin/deploy/jars/`，并重命名为文档中的固定名称：

- `sourcelin-gateway.jar`
- `sourcelin-auth.jar`
- `sourcelin-system.jar`
- `sourcelin-blog.jar`
- `sourcelin-file.jar`
- `sourcelin-monitor.jar`

---

## 6. 当前固定参数

当前服务器这份 Compose 不依赖统一 `.env`，关键参数直接写在 `docker-compose.yml` 中。

固定项：

- `network_mode: host`
- `Nacos 地址：127.0.0.1:8848`
- `Nacos 分组：BLOG_GROUP`
- `Nacos 用户名：<NACOS_USERNAME>`
- `Nacos 密码：<NACOS_PASSWORD>`
- `Nacos 鉴权：开启`
- `Nacos JWT/Identity 参数：必须显式配置，否则 Nacos 2.4.0 会启动异常`

---

## 7. `docker-compose.yml` 模板

路径：`/home/sourcelin/deploy/compose/docker-compose.yml`

```yaml
services:
  redis_local:
    image: redis:7.2-alpine
    container_name: sourcelin-redis-local
    restart: always
    network_mode: host
    command: redis-server --appendonly yes --port 6379
    volumes:
      - /home/sourcelin/deploy/data/redis-local:/data

  nacos:
    image: nacos/nacos-server:v2.4.0
    container_name: sourcelin-nacos
    restart: always
    network_mode: host
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: mysql
      MYSQL_SERVICE_HOST: 127.0.0.1
      MYSQL_SERVICE_PORT: 3306
      MYSQL_SERVICE_DB_NAME: sourcelin-config
      MYSQL_SERVICE_USER: root
      MYSQL_SERVICE_PASSWORD: <MYSQL_PASSWORD>
      NACOS_AUTH_ENABLE: true
      NACOS_AUTH_TOKEN: tEfjmmq+31Ki5hWBF5E62lyLRCgt3bdLcH9zFK/n9x8=
      NACOS_AUTH_IDENTITY_KEY: tAw/XdABaEINJN5Vt9uNng==
      NACOS_AUTH_IDENTITY_VALUE: LzINFHs5yFoTDvM/Rde8Fw==
      JVM_XMS: 160m
      JVM_XMX: 320m
      JVM_XMN: 96m
      TZ: Asia/Shanghai
    mem_limit: 640m
    volumes:
      - /home/sourcelin/deploy/data/nacos:/home/nacos/data

  gateway:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-gateway
    restart: always
    network_mode: host
    depends_on:
      - nacos
      - redis_local
    command: >
      sh -c "java -Xms128m -Xmx192m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-gateway.jar"
    mem_limit: 448m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-gateway.jar:/app/sourcelin-gateway.jar:ro
      - /home/sourcelin/deploy/logs/gateway:/app/logs

  auth:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-auth
    restart: always
    network_mode: host
    depends_on:
      - nacos
      - redis_local
    command: >
      sh -c "java -Xms96m -Xmx112m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-auth.jar"
    mem_limit: 420m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-auth.jar:/app/sourcelin-auth.jar:ro
      - /home/sourcelin/deploy/logs/auth:/app/logs

  system:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-system
    restart: always
    network_mode: host
    depends_on:
      - nacos
      - redis_local
    command: >
      sh -c "java -Xms112m -Xmx144m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-system.jar"
    mem_limit: 480m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-system.jar:/app/sourcelin-system.jar:ro
      - /home/sourcelin/deploy/logs/system:/app/logs

  blog:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-blog
    restart: always
    network_mode: host
    depends_on:
      - nacos
      - redis_local
    command: >
      sh -c "java -Xms96m -Xmx128m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-blog.jar"
    mem_limit: 420m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-blog.jar:/app/sourcelin-blog.jar:ro
      - /home/sourcelin/deploy/logs/blog:/app/logs

  file:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-file
    restart: always
    network_mode: host
    depends_on:
      - nacos
      - redis_local
    command: >
      sh -c "java -Xms80m -Xmx112m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-file.jar"
    mem_limit: 360m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-file.jar:/app/sourcelin-file.jar:ro
      - /home/sourcelin/deploy/logs/file:/app/logs

  monitor:
    image: eclipse-temurin:8-jre
    container_name: sourcelin-monitor
    restart: always
    network_mode: host
    depends_on:
      - nacos
    command: >
      sh -c "java -Xms80m -Xmx128m -Dfile.encoding=UTF-8 -Dspring.cloud.sentinel.enabled=false -Dfeign.sentinel.enabled=false -Dspring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.discovery.group=BLOG_GROUP -Dspring.cloud.nacos.config.group=BLOG_GROUP -Dspring.cloud.nacos.username=<NACOS_USERNAME> -Dspring.cloud.nacos.password=<NACOS_PASSWORD> -jar /app/sourcelin-monitor.jar"
    mem_limit: 320m
    volumes:
      - /home/sourcelin/deploy/jars/sourcelin-monitor.jar:/app/sourcelin-monitor.jar:ro
      - /home/sourcelin/deploy/logs/monitor:/app/logs
```

---

## 8. Nginx 配置（独立 nginx 容器）

当前服务器 nginx 静态根目录不是 `/usr/share/nginx/html` 默认项目目录，而是宿主机挂载：

- 前台：`/home/sourcelin/nginx/html/sourcelin-ui-platform`
- 后台：`/home/sourcelin/nginx/html/sourcelin-ui-admin`

当前 nginx 容器对后端走宿主机端口代理，因此 `proxy_pass` 指向 `127.0.0.1:8080` 或宿主可达地址均可。示例：

```nginx
server {
    listen 80;
    server_name sourcelin.cn www.sourcelin.cn;

    root /usr/share/nginx/html/sourcelin-ui-platform;
    index index.html;
    client_max_body_size 100m;

    location = /admin {
        return 301 /admin/;
    }

    location ^~ /admin/ {
        alias /usr/share/nginx/html/sourcelin-ui-admin/;
        index index.html;
        try_files $uri $uri/ /admin/index.html;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        rewrite ^/prod-api/(.*)$ /$1 break;
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /blog-api/front/ {
        rewrite ^/blog-api/front/(.*)$ /blog/front/$1 break;
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /blog-api/app/ {
        rewrite ^/blog-api/app/(.*)$ /blog/app/$1 break;
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /blog-api/ {
        rewrite ^/blog-api/(.*)$ /$1 break;
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /file/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location ~* ^/(.*/)?actuator(/|$) {
        return 403;
    }
}
```

---

## 9. 启动与运维命令

```bash
cd /home/sourcelin/deploy/compose
docker compose up -d redis_local nacos
docker compose up -d gateway auth system blog
docker compose up -d file
docker compose up -d monitor
docker compose ps
```

查看日志：

```bash
docker compose logs -f gateway
docker compose logs -f auth
docker compose logs -f system
docker compose logs -f blog
docker compose logs -f file
docker compose logs -f monitor
```

重启单个服务：

```bash
docker compose restart gateway
```

停止：

```bash
docker compose stop monitor
docker compose stop file
```

---

## 10. 验收清单

- `http://sourcelin.cn/` 前台可访问
- `http://sourcelin.cn/admin/` 后台可访问
- 后台静态资源路径为 `/admin/assets/...`
- `docker compose ps` 全部关键容器为 `Up`
- `Nacos` 端口 `8848/9848/9849` 正常监听
- `BLOG_GROUP` 下可见：
  - `sourcelin-gateway`
  - `sourcelin-auth`
  - `sourcelin-system`
  - `sourcelin-blog`
  - `sourcelin-file`
  - `sourcelin-monitor`
- 宿主机端口 `8080/9100/9200/9201/9204/9300` 正常监听

---

## 11. 关键注意事项

1. 当前实际采用 `host` 网络，不再通过 Compose bridge 网络互联。
2. `Nacos 2.4.0` 开启鉴权时，除了用户名密码，还必须显式提供：
   - `NACOS_AUTH_TOKEN`
   - `NACOS_AUTH_IDENTITY_KEY`
   - `NACOS_AUTH_IDENTITY_VALUE`
   否则 `Nacos` 会报 JWT secret key 非法，业务服务会在注册阶段出现 `Client not connected, current status: STARTING`。
3. 业务服务必须显式带上：
   - `spring.cloud.nacos.discovery.server-addr`
   - `spring.cloud.nacos.config.server-addr`
   - `spring.cloud.nacos.discovery.group=BLOG_GROUP`
   - `spring.cloud.nacos.config.group=BLOG_GROUP`
   - `spring.cloud.nacos.username`
   - `spring.cloud.nacos.password`
4. 前台/后台经过 nginx 上传文件时，站点配置需显式设置 `client_max_body_size 100m;`，否则会出现 `413 Request Entity Too Large`。
5. `monitor` 已纳入 Compose，但在 2G 机器上属于可选服务，内存吃紧时优先停它。
6. 首次接入时请确认 Nacos 中已存在各服务所需配置集，否则应用会启动失败。

---

## 12. 2G 低配服务器落地参数（已实测）

适用场景：单机 `2G RAM`，单域名 `sourcelin.cn`，Docker Compose 部署。

### 12.1 必做：开启 2G Swap

```bash
sudo fallocate -l 2G /swapfile || sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
free -h
```

### 12.2 低配版资源建议（核心）

- 不要全局复用同一个 `JAVA_OPTS`，按服务单独设置。
- 首发阶段优先启动：`redis`、`nacos`、`gateway`、`auth`、`system`、`blog`。
- `monitor` 建议在核心链路稳定后再启动，避免和业务服务抢内存。

建议值（2G 实测可用）：

- `nacos`：`-Xms256m -Xmx320m`，`mem_limit: 640m`
- `gateway`：`-Xms128m -Xmx192m`，`mem_limit: 448m`
- `auth`：`-Xms96m -Xmx112m`，`mem_limit: 420m`
- `system`：`-Xms128m -Xmx144m`，`mem_limit: 480m`
- `blog`：`-Xms96m -Xmx128m`，`mem_limit: 420m`
- `file`：`-Xms80m -Xmx112m`，`mem_limit: 360m`
- `monitor`：`-Xms80m -Xmx128m`，`mem_limit: 320m`（可选）

### 12.3 Sentinel 建议（低内存模式）

为降低运行时内存占用，建议在低配机器关闭 Sentinel：

```text
-Dspring.cloud.sentinel.enabled=false
-Dfeign.sentinel.enabled=false
```

建议加在 `gateway`、`auth`、`system`、`blog` 启动命令中。

### 12.4 分步启动（避免同时拉起触发 OOM）

```bash
# 1) 基础组件
docker compose up -d redis_local nacos

# 2) 网关（等待 15~30 秒）
docker compose up -d gateway

# 3) 业务核心
docker compose up -d auth system blog

# 4) 文件服务
docker compose up -d file

# 5) 可选监控
docker compose up -d monitor
```

### 12.5 健康检查与 OOM 判据

```bash
# 容器状态
docker compose ps

# 监听端口
ss -lntp | egrep ':(8080|8848|9100|9200|9201|9204|9300|9848|9849)\b'

# 查看重启次数（持续增加通常是异常）
docker inspect sourcelin-auth --format '{{.RestartCount}}'
docker inspect sourcelin-system --format '{{.RestartCount}}'
docker inspect sourcelin-blog --format '{{.RestartCount}}'
docker inspect sourcelin-file --format '{{.RestartCount}}'
docker inspect sourcelin-monitor --format '{{.RestartCount}}'

# OOM 直接证据
dmesg -T | egrep -i 'out of memory|killed process|memory cgroup'
```

判定标准：

- 出现 `Memory cgroup out of memory` / `Killed process`，即为内存击穿。
- 若 `RestartCount` 持续上升，优先降 `Xmx` 或先停 `monitor`。

### 12.6 低配回滚顺序（紧急止血）

```bash
# 先停非核心
docker compose stop monitor

# 再停 file
docker compose stop file

# 仍不稳定则停 blog（保后台）
docker compose stop blog

# 最后再按顺序拉起
docker compose up -d auth system
docker compose up -d blog
docker compose up -d file
docker compose up -d monitor
```

### 12.7 验收补充（数据与链路）

- 前台：`http://sourcelin.cn/`
- 后台：`http://sourcelin.cn/admin/`
- 网关转发可达：`/prod-api/**`、`/blog-api/**`
- 各核心服务日志中无持续重启、无持续 OOM。
