<p align="center">
	<img alt="logo" src="https://pic1.imgdb.cn/item/69faac182356c9fc9194b076.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sourcelin Blog</h1>
<h4 align="center">面向个人写作与生活记录的现代博客平台，支持多端访问</h4>
<p align="center">
	<a href="https://gitee.com/sourcelin/sourcelin-cloud-blog"><img src="https://gitee.com/sourcelin/sourcelin-cloud-blog/badge/star.svg?theme=dark"></a>
	<img src="https://img.shields.io/badge/version-v3.6.8-brightgreen.svg">
	<img src="https://img.shields.io/badge/java-1.8+-blue.svg">
	<img src="https://img.shields.io/badge/springboot-2.7.18-blue.svg">
	<img src="https://img.shields.io/badge/vue-3.x-green.svg">
	<a href="https://gitee.com/sourcelin/sourcelin-cloud-blog/blob/master/LICENSE"><img src="https://img.shields.io/github/license/sourcelin/sourcelin-cloud-blog"></a>
</p>


## 简介

Sourcelin Blog（圆圈博客）是一套面向**个人写作与生活记录**的博客平台，采用前后端分离微服务架构，支持多端访问与内容管理。

本项目可作为：
- ✍️ **个人博客平台** - 记录生活、写作随笔与分享见闻
- 📚 **内容创作空间** - 文章、分类、标签、评论等完整功能
- 🧩 **团队内容中心** - 适合小团队或社群的内容运营
- 🗂️ **个人作品展示** - 长期沉淀与归档你的内容资产

> 🌐 演示地址：[https://blog.sourcelin.com](https://blog.sourcelin.com)（暂未上线）
> 
> 📖 文档地址：[https://doc.sourcelin.com](https://doc.sourcelin.com)（暂未上线）

## 特性

- ✅ **微服务架构** - 基于 Spring Cloud Alibaba，服务注册、配置、网关一站式支持
- ✅ **前后端分离** - Vue 3 + Naive UI 博客前台，自研 Vue 3 + Element Plus 管理后台
- ✅ **完整权限体系** - 基于 Spring Security + JWT 的认证授权
- ✅ **多端支持** - PC 管理后台 + 博客前台（支持 H5/小程序扩展）
- ✅ **丰富的博客功能** - 文章、分类、标签、评论、点赞、收藏、关注、打赏
- ✅ **高扩展性** - 模块化设计，支持插件扩展
- ✅ **开箱即用** - 提供 Docker 一键部署脚本

## 技术栈

### 后端技术
| 技术 | 版本 |
|------|------|
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.9 |
| Spring Cloud Alibaba | 2021.0.6.1 |
| Java | 1.8+ |
| Nacos | 注册/配置中心 |
| Sentinel | 流量控制 |
| Seata | 分布式事务 |
| MySQL | 8.0+ |
| Redis | 缓存 |
| Druid | 数据库连接池 |
| PageHelper | 分页插件 |
| MinIO | 文件存储 |
| Hutool | 工具库 |
| FastJSON2 | JSON 处理 |
| Sa-Token | 认证鉴权 |

### 前端技术
| 项目 | 技术 | 版本 |
|------|------|------|
| 管理后台 | Vue 3 + TypeScript + Element Plus | 自研实现 |
| 博客前台 | Vue 3 + Naive UI + TypeScript | Vue 3.5 / Naive UI 2.44 |

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  管理后台     │  │  博客前台    │  │  小程序/H5   │     │
│  │  Vue3+EP自研 │ │  Vue3+Naive  │  │   (Uni-app)  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      API 网关 (8080)                        │
│                  Spring Cloud Gateway                       │
└─────────────────────────────────────────────────────────────┘
                               │
       ┌───────────┬───────────┼───────────┬───────────┐
       ▼           ▼           ▼           ▼           ▼
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ 认证服务  │ │ 系统服务  │ │ 博客服务  │ │ 文件服务  │ │ 监控中心  │
│ (9200)   │ │ (9201)   │ │ (9204)   │ │ (9300)   │ │ (9100)   │
└──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘
       │           │           │           │
       └───────────┴───────────┼───────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼               ▼               ▼
         ┌──────────┐   ┌──────────┐   ┌──────────┐
         │  MySQL   │   │  Redis   │   │  Nacos   │
         └──────────┘   └──────────┘   └──────────┘
```

## 系统模块

~~~
com.sourcelin     
├── sourcelin-ui              // 前端框架
│   ├── sourcelin-ui-admin   // 管理后台 [自研 Vue 3 + Element Plus]
│   └── sourcelin-ui-platform // 博客前台 [Vue 3 + Naive UI]
├── sourcelin-gateway         // 网关模块 [8080]
├── sourcelin-auth            // 认证中心 [9200]
├── sourcelin-api             // 接口模块
│       └── sourcelin-api-system                          // 系统接口
│       └── sourcelin-api-blog                            // 博客接口
│       └── sourcelin-api-file                            // 文件接口
├── sourcelin-common          // 通用模块
│       └── sourcelin-common-core                         // 核心模块
│       └── sourcelin-common-datascope                    // 权限范围
│       └── sourcelin-common-datasource                   // 多数据源
│       └── sourcelin-common-log                          // 日志记录
│       └── sourcelin-common-redis                        // 缓存服务
│       └── sourcelin-common-seata                        // 分布式事务
│       └── sourcelin-common-security                     // 安全模块
│       └── sourcelin-common-swagger                      // 系统接口
├── sourcelin-modules         // 业务模块
│       └── sourcelin-system                              // 系统模块 [9201]
│       └── sourcelin-blog                                // 博客模块 [9204]
│       └── sourcelin-job                                 // 定时任务 [9203]
│       └── sourcelin-file                                // 文件服务 [9300]
├── sourcelin-visual          // 图形化管理模块
│       └── sourcelin-visual-monitor                      // 监控中心 [9100]
└── pom.xml                // 公共依赖
~~~

## API 接口规范

### 接口路径前缀

| 前缀 | 用途 | 示例 |
|------|------|------|
| `/front` | Web 前台（浏览器访问） | `/front/articles`, `/front/categories` |
| `/app` | 移动端 App（小程序/H5） | `/app/home`, `/app/articles` |
| `/system` | 系统管理后台 | `/system/user`, `/system/role` |
| `/blog` | 博客管理后台 | `/blog/article`, `/blog/category` |

### Controller 命名规范

| 包路径 | 前缀 | 用途 |
|--------|------|------|
| `controller.front` | `Front` | Web 前台 Controller |
| `controller.app` | `App` | 移动端 App Controller |
| `controller.admin` | 无 | 管理后台 Controller |

## 快速开始

### 环境要求

| 组件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| JDK | 1.8+ | 1.8 / 11 |
| Maven | 3.6+ | 3.8.x |
| MySQL | 5.7+ | 8.0 |
| Redis | 5.0+ | 6.0+ |
| Node.js | 14+ | 18 LTS |
| Nacos | 2.0+ | 2.0.x |
| Docker | 20+ | 24.x (可选) |

### 本地开发启动

1. **克隆项目**
```bash
git clone https://gitee.com/sourcelin/sourcelin-cloud-blog.git
cd sourcelin-cloud-blog
```

2. **初始化数据库**
```bash
# 创建数据库并导入 SQL 文件
mysql -u root -p < sql/sourcelin_20231024/sourcelin-cloud.sql
mysql -u root -p < sql/blog_20240719_博客相关/sourcelin-cloud-202407191723.sql
```

3. **配置 Nacos**
```bash
# 启动 Nacos 后，导入配置文件
# sql/blog_20240719_博客相关/sourcelin-config-202407191723.sql
```

4. **修改配置**
```yaml
# 各模块的 bootstrap.yml 中修改 Nacos/Redis/MySQL 连接信息
```

5. **启动后端服务**（按顺序）
```bash
# 1. 启动 Nacos (8848)
# 2. 启动 Redis (6379)
# 3. 启动 MySQL (3306)

# 依次启动微服务:
mvn clean package -DskipTests
java -jar sourcelin-gateway/target/sourcelin-gateway.jar
java -jar sourcelin-auth/target/sourcelin-auth.jar
java -jar sourcelin-modules/sourcelin-system/target/sourcelin-system.jar
java -jar sourcelin-modules/sourcelin-blog/target/sourcelin-blog.jar
```

6. **启动前端**

```bash
# 管理后台 (自研 Vue 3 + Element Plus)
cd sourcelin-ui/sourcelin-ui-admin
pnpm install
pnpm run dev

# 博客前台 (Vue 3 + Naive UI + TypeScript)
cd sourcelin-ui/sourcelin-ui-platform
npm install
npm run dev
```

### Docker 部署（推荐）

```bash
# 一键启动（需要先配置 docker-compose.yml）
docker-compose up -d
```

## 功能清单

### 博客前台 (sourcelin-ui-platform)
- [x] 首页展示（轮播图、推荐文章、热门文章）
- [x] 文章详情页（Markdown 渲染）
- [x] 分类/标签页
- [x] 归档页
- [x] 友链页面
- [x] 导航页面
- [x] 说说/瞬间
- [x] 树洞功能
- [x] 用户中心（资料修改、头像上传）
- [x] 评论系统（支持回复、点赞）
- [x] 点赞/收藏功能
- [x] 关注/粉丝功能
- [x] 登录/注册

### 管理后台 (sourcelin-ui-admin)
- [x] 系统管理（用户、角色、菜单、部门、岗位、字典、参数、通知）
- [x] 系统监控（在线用户、定时任务）
- [x] 系统工具（代码生成、表单构建）
- [x] 博客管理（文章、分类、标签、评论、友链、用户、导航、配置）
- [x] 文件管理界面
- [x] 博客统计

### 后端服务
- [x] 用户认证（JWT、验证码）
- [x] 权限控制（Spring Security）
- [x] 接口文档（SpringDoc OpenAPI）
- [x] 日志记录
- [x] 分布式事务（Seata）
- [x] 服务监控（Spring Boot Admin）
- [x] 流量控制（Sentinel）

## 参与贡献

欢迎所有形式的贡献，包括但不限于：

- 🐛 提交 Bug 或问题
- 💡 提出新功能或改进建议
- 📝 完善文档
- 🎨 提交代码（修复 Bug 或开发新功能）
- 🌐 翻译文档
- ⭐ Star 支持

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -m 'Add some feature'`)
4. 推送分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request

### 代码规范

- 后端代码遵循阿里巴巴 Java 开发规范
- 前端代码使用 ESLint + Prettier 格式化
- 提交信息请使用中文，描述清晰

## 商务合作

### 付费运营支持

我们提供专业的运营支持服务：

| 服务类型 | 说明 | 价格 |
|----------|------|------|
| 基础咨询 | 环境配置、部署问题答疑 | 免费 |
| 部署服务 | 生产环境部署指导 | ¥299/次 |
| 故障排查 | 性能调优、问题定位 | ¥99/小时 |
| 功能定制 | 根据需求定制开发 | ¥500起 |
| 毕设指导 | 论文辅导、项目答疑 | ¥399/次 |

> 📧 联系方式：support@sourcelin.com

### 企业服务

- 🏢 **私有化部署** - 为企业提供专属部署方案
- 🔧 **定制开发** - 根据业务需求深度定制
- 📚 **培训服务** - 微服务架构培训、实战指导
- 🛡️ **安全加固** - 安全审计、渗透测试

### 商业授权

本项目采用 **MIT** 开源许可证，代码可免费使用。

如需：
- 去除源码中的作者信息
- 商业闭源使用
- 二次开发后对外提供服务

请联系商务合作获取授权。

## 常见问题

### Q: 项目可以商用吗？
> A: 可以，本项目采用 MIT 许可证，仅需保留 LICENSE 文件中的版权声明。

### Q: 支持小程序吗？
> A: 小程序正在开发中，可使用 Uni-app 快速适配。

### Q: 如何部署到服务器？
> A: 推荐使用 Docker 部署，详见上方「Docker 部署」章节。

### Q: 博客前台使用什么技术栈？
> A: 博客前台使用 Vue 3 + Naive UI + TypeScript + Vite，管理后台为自研 Vue 3 + TypeScript + Element Plus 实现。

## 文档导航

- 📖 [项目文档](./docs/)
- 📋 [项目分析](./docs/PROJECT_ANALYSIS.md)
- 📊 [项目摘要](./docs/PROJECT_SUMMARY.md)
- 📊 [需求设计](./docs/REQUIREMENTS.md)
- 🌍 [市场分析](./docs/MARKET_ANALYSIS.md)
- 💰 [商业模式](./docs/BUSINESS_MODEL.md)
- 📝 [贡献指南](./docs/CONTRIBUTING.md)
- 📜 [更新日志](./docs/CHANGELOG.md)

## 感谢

- 感谢 [sourcelin](https://sourcelin.vip/) 提供的优秀微服务框架
- 感谢所有开源项目的贡献者
- 感谢所有关注与支持本项目的朋友

## 协议与来源

- 本项目采用 [MIT License](./LICENSE)
- 管理后台（`sourcelin-ui/sourcelin-ui-admin`）为自研实现，使用 Vue 3 + TypeScript + Element Plus
- 博客前台（`sourcelin-ui/sourcelin-ui-platform`）为自研实现，使用 Vue 3 + Naive UI + TypeScript

---

<p align="center">
  <img src="https://img.shields.io/badge/Version-3.6.8-blue.svg">
  <img src="https://img.shields.io/badge/License-MIT-green.svg">
  <img src="https://img.shields.io/badge/Java-1.8+-orange.svg">
  <img src="https://img.shields.io/badge/Vue-3.x-green.svg">
</p>

<p align="center">
  <strong>欢迎 Star & Fork ⭐</strong>
</p>
