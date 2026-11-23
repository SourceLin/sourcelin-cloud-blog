# 快速启动与常见问题

本文档面向第一次接触 Sourcelin Blog 的开发者，目标是尽快完成本地启动和基础排错。

如果你要看完整对外介绍，先读 [README.md](../../README.md)。服务器部署可参考 [Compose 示例](../../docker-compose.example.yml) 和 [Nginx 配置](../deployment/NGINX_CONFIG.md)。

## 一、最快阅读路径

如果你只有 5 分钟，建议按这个顺序：

1. 打开演示站：[https://sourcelin.cn/](https://sourcelin.cn/)
2. 阅读 [README.md](../../README.md) 的界面预览和项目定位
3. 确认本地是否具备 MySQL、Redis、Nacos
4. 按本文档完成最小本地启动

## 二、环境要求

| 组件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| JDK | 27 | 27.0.1.1 |
| Maven | 3.9 | 3.9.x |
| MySQL | 8.4 | 8.4 LTS |
| Redis | 6.0（支持 `SET ... KEEPTTL`） | 7.2.x |
| Node.js | 22.12 | 22 LTS |
| Nacos | 3.1.1 | 3.1.1 |

## 三、本地最小启动

### 1. 克隆仓库

```bash
git clone https://gitee.com/my_lyq/sourcelin-cloud-blog.git
cd sourcelin-cloud-blog
```

### 2. 初始化数据库

以下命令用于空库。前两份 SQL 一次性完成业务库和 Nacos 配置库初始化。使用 Seata 时再导入事务库。

```bash
mysql -u root -p < docs/sql/sourcelin-cloud.sql
mysql -u root -p < docs/sql/sourcelin-config.sql
mysql -u root -p < docs/sql/sourcelin-seata.sql
```

说明：

- `sourcelin-cloud.sql` 是业务库初始化脚本
- `sourcelin-config.sql` 是 Nacos 配置库初始化脚本
- `sourcelin-seata.sql` 是 Seata 事务库初始化脚本

SQL 说明见 [sql/README.md](../sql/README.md)。

### 3. 启动基础依赖

先确保以下服务已可用：

- MySQL
- Redis
- Nacos

### 4. 修改配置

配置各服务的 Nacos 地址和账号，并在 `BLOG_GROUP` 下核对实际生效的配置。

- 为 Java 服务设置业务库的 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。Nacos 服务连接自己的配置库。
- 在 `application-dev.yml` 统一 Redis 实例和库编号。样例读取 `REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`、`REDIS_DATABASE`，默认 `127.0.0.1:6379` 的第 0 库。认证签发会话与其他服务校验会话须使用同一实例和库编号。

如果需要联调 Uniapp 微信快捷登录，还必须在 Nacos 的 `BLOG_GROUP / sourcelin-auth-dev.yml` 中补齐：

- `sourcelin.mini-program.app-id`
- `sourcelin.mini-program.app-secret`

推荐直接参考：

- [AI 与实时消息部署](AI_MESSAGE_SETUP.md)

### 5. 启动后端核心服务

```bash
mvn clean package -DskipTests
```

然后依次启动：

```bash
java -jar sourcelin-gateway/target/sourcelin-gateway.jar
java -jar sourcelin-auth/target/sourcelin-auth.jar
java -jar sourcelin-modules/sourcelin-system/target/sourcelin-system.jar
java -jar sourcelin-modules/sourcelin-blog/target/sourcelin-blog.jar
```

如果你只是想先跑通博客主链路，到这里通常就够了。

按需补充：

```bash
java -jar sourcelin-modules/sourcelin-file/target/sourcelin-file.jar
java -jar sourcelin-modules/sourcelin-job/target/sourcelin-job.jar
java -jar sourcelin-visual/sourcelin-visual-monitor/target/sourcelin-visual-monitor.jar
```

### 6. 启动前端

管理后台：

```bash
cd sourcelin-ui/sourcelin-ui-admin
pnpm install
pnpm run dev
```

博客前台：

```bash
cd sourcelin-ui/sourcelin-ui-platform
npm install
npm run dev
```

## 四、推荐验证顺序

建议按这个顺序确认是否启动成功：

1. Nacos 控制台能打开
2. 网关服务启动无报错
3. `auth`、`system`、`blog` 服务完成注册
4. 管理后台页面可访问
5. 博客前台页面可访问

## 五、常见问题

### 1. 服务启动报 Nacos 相关错误

优先检查：

- Nacos 是否真的启动成功
- `sourcelin-config.sql` 是否已经导入
- `application.yml` 中 Nacos 地址、用户名、密码是否正确

### 2. 服务启动报数据库连接错误

优先检查：

- MySQL 是否启动
- 数据库名是否存在
- 账号密码是否正确
- JDBC 地址是否和本地环境一致

### 3. 页面能打开但接口异常

通常优先排查：

- 网关是否启动
- 目标微服务是否注册到 Nacos
- 前端请求地址是否和网关地址一致

### 4. 前端页面空白或资源路径错误

本地开发时优先检查：

- Node 版本是否过低
- 依赖是否完整安装
- 控制台是否有接口地址、跨域或资源路径报错

后台部署到 `/admin/` 时，构建必须带：

```bash
pnpm run build -- --base=/admin/
```

### 5. 只想快速看功能，不想一次启动全部服务

建议最小集合：

- MySQL
- Redis
- Nacos
- `gateway`
- `auth`
- `system`
- `blog`
- 博客前台 / 管理后台

### 6. Docker 能不能直接一键跑

可以参考仓库根目录提供的 Compose 示例：

- [docker-compose.example.yml](../../docker-compose.example.yml)
- [.env.example](../../.env.example)

如果你要参考现成部署方案，请看：

- [已有部署升级说明](../deployment/UPGRADE.md)
- [NGINX_CONFIG.md](../deployment/NGINX_CONFIG.md)

典型使用方式：

```bash
cp .env.example .env
# 修改 .env 中的数据库、Nacos、路径和鉴权占位值
docker compose -f docker-compose.example.yml up -d
```

## 六、建议的新手做法

第一次接触时，不建议一上来就：

- 同时改前后端
- 同时接管 Docker、Nginx、Nacos、数据库
- 一次启动所有可选服务

更稳妥的方式是：

1. 先跑通本地最小链路
2. 再看前台页面和后台页面
3. 再决定做功能开发还是做部署

## 七、进一步阅读

- [README.md](../../README.md)
- [PROJECT_SUMMARY.md](../product/PROJECT_SUMMARY.md)
- [DOCS_INDEX.md](../DOCS_INDEX.md)
- [CONTRIBUTING.md](./CONTRIBUTING.md)
- [sql/README.md](../sql/README.md)
