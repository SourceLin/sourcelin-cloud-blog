# Sourcelin Blog 需求设计文档

> 说明：本文档保留产品需求、功能设计和中长期规划信息；涉及当前实际代码结构、技术栈、接口契约和验证要求时，以 [README.md](../../README.md)、[AGENTS.md](../../AGENTS.md)、[rules/](../../rules/) 和 [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) 为准。

## 文档信息
- **版本**: 1.0
- **日期**: 2026-03-15
- **项目**: Sourcelin Blog 圆圈博客
- **基于代码版本**: v1.0.0

---

## 一、产品定位

### 1.1 产品概述
Sourcelin Blog 是一款基于微服务架构的**个人/团队博客系统**，支持内容创作、用户互动、社区交流等功能。系统采用前后端分离设计，支持 Web 端（PC/H5）和移动端小程序多端访问。

### 1.2 目标用户
| 用户类型 | 场景描述 |
|----------|----------|
| 博主/内容创作者 | 发布文章、记录生活、建立个人品牌 |
| 内容读者 | 浏览文章、获取资讯、收藏喜欢的内容 |
| 社区用户 | 参与评论互动、关注博主、收藏文章 |
| 运营者 | 管理内容、运营社区、数据分析 |

### 1.3 产品形态
| 端 | 说明 |
|-----|------|
| PC 管理后台 | 完整的内容管理、用户管理、系统配置 |
| PC/H5 博客前台 | 文章浏览、用户中心、社区互动 |
| 微信小程序 | 移动端阅读、评论、用户中心 |
| H5 移动端 | 响应式博客站点 |

---

## 二、功能架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         客户端层                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  管理后台     │  │  博客前台    │  │  小程序/H5   │          │
│  │  (Vue 3 + Element Plus) │  │  (Vue 3 + Naive UI) │  │  (Uni-app)   │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         网关层 (8080)                           │
│                    Spring Cloud Gateway                         │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  认证服务 (9200) │ │  系统服务 (9201) │ │  博客服务 (9204)│
│  Auth           │ │  System         │ │  Blog           │
└──────────────────┘ └──────────────────┘ └──────────────────┘
              │               │               │
              │               │               │
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  文件服务 (9300) │ │  代码生成 (9202) │ │  定时任务 (9203) │
│  File           │ │  Gen            │ │  Job            │
└──────────────────┘ └──────────────────┘ └──────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         基础设施层                               │
│        MySQL  │  Redis  │  Nacos  │  Seata  │  Sentinel      │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 功能模块总览

```
Sourcelin Blog
│
├── 博客前台 (sourcelin-ui-platform)
│   ├── 首页 (轮播、推荐、热门文章)
│   ├── 文章详情 (Markdown渲染、评论、点赞、收藏)
│   ├── 分类页 (文章分类浏览)
│   ├── 标签页 (标签云、文章筛选)
│   ├── 归档页 (时间线文章列表)
│   ├── 用户中心 (资料编辑、我的文章、收藏、关注)
│   ├── 说说 (瞬间/说说发布与展示)
│   ├── 树洞 (匿名留言)
│   ├── 友链 (友链申请与展示)
│   ├── 导航 (网址导航)
│   ├── 公告 (系统公告)
│   └── 关于 (关于本站/博主)
│
├── 管理后台 (sourcelin-ui-admin)
│   ├── 系统管理
│   │   ├── 用户管理
│   │   ├── 角色管理
│   │   ├── 菜单管理
│   │   ├── 部门管理
│   │   ├── 岗位管理
│   │   ├── 字典管理
│   │   ├── 参数管理
│   │   ├── 通知公告
│   │   ├── 操作日志
│   │   └── 登录日志
│   ├── 博客管理
│   │   ├── 文章管理 (CRUD、置顶、推荐)
│   │   ├── 分类管理
│   │   ├── 标签管理
│   │   ├── 评论管理
│   │   ├── 友链管理
│   │   ├── 用户管理 (博客用户)
│   │   ├── 导航管理
│   │   └── 网站配置
│   ├── 系统监控
│   │   ├── 在线用户
│   │   └── 定时任务
│   └── 系统工具
│       ├── 代码生成
│       └── 表单构建
│
└── 移动端 (sourcelin-ui-uniapp) [待开发]
    ├── 首页
    ├── 文章列表/详情
    ├── 用户中心
    └── 评论互动
```

---

## 三、数据库设计

### 3.1 核心业务表

#### 3.1.1 文章表 (b_article)

| 字段 | 类型 | 说明 | 备注 |
|------|------|------|------|
| id | bigint | 主键ID | 自增 |
| user_id | bigint | 作者ID | 关联b_user |
| category_id | bigint | 分类ID | 关联b_category |
| title | varchar(255) | 文章标题 | |
| avatar | varchar(255) | 封面图 | |
| summary | text | 文章摘要 | |
| content | text | 文章内容 | Markdown格式 |
| read_auth | int | 阅读权限 | 0:公开 1:登录 2:VIP |
| status | int | 状态 | 草稿/已发布/回收站 |
| is_comment | int | 是否开启评论 | 0:关闭 1:开启 |
| is_recommend | int | 是否推荐 | |
| is_top | int | 是否置顶 | |
| is_original | int | 是否原创 | |
| original_url | varchar(255) | 原文链接 | 转载时填写 |
| view_count | bigint | 浏览量 | |
| like_count | bigint | 点赞数 | |
| collect_count | bigint | 收藏数 | |
| deleted | int | 逻辑删除 | 0:正常 1:删除 |

**关联表**: b_article_tag (文章-标签关联)

#### 3.1.2 用户表 (b_user)

说明：博客前台用户表统一使用 `del_flag` 作为删除标识，不再使用 `deleted` 字段。

| 字段 | 类型 | 说明 | 备注 |
|------|------|------|------|
| id | bigint | 用户ID | 主键 |
| username | varchar(75) | 用户账号 | 唯一 |
| nickname | varchar(100) | 用户昵称 | |
| password | varchar(100) | 密码 | 加密存储 |
| user_type | tinyint | 用户类型 | 1:普通用户 2:博主 |
| email | varchar(255) | 邮箱 | |
| phone | varchar(20) | 手机号 | |
| avatar | varchar(255) | 头像 | |
| sex | tinyint | 性别 | 0:男 1:女 2:未知 |
| birthday | date | 生日 | |
| introduction | varchar(1200) | 个人简介 | |
| user_status | tinyint | 账号状态 | 0:启用 1:停用 |
| praise | bigint | 获赞数 | |
| follow | bigint | 粉丝数 | |
| login_ip | varchar(50) | 最后登录IP | |
| login_date | datetime | 最后登录时间 | |
| del_flag | char | 删除标志 | 0:存在 2:删除 |
| uuid | varchar(36) | 设备唯一标识 | |
| bind_qq_id | varchar(50) | QQ绑定ID | 第三方登录 |
| bind_wechat_id | varchar(50) | 微信绑定ID | |
| bind_sina_id | varchar(50) | 微博绑定ID | |
| open_id | varchar(100) | 微信OpenID | 小程序登录 |
| session_key | varchar(100) | 微信会话Key | |

#### 3.1.3 评论表 (b_comment)

| 字段 | 类型 | 说明 | 备注 |
|------|------|------|------|
| id | bigint | 评论ID | 主键 |
| user_id | bigint | 评论者ID | |
| parent_user_id | bigint | 被回复用户ID | |
| article_id | bigint | 文章ID | |
| parent_comment_id | bigint | 父评论ID | 用于回复 |
| floor_comment_id | bigint | 楼层评论ID | |
| content | text | 评论内容 | |
| like_count | bigint | 点赞数 | |
| source | varchar(90) | 评论来源 | 文章/友链/留言板 |
| browser | varchar(50) | 浏览器 | |
| system | varchar(50) | 操作系统 | |
| ip_address | varchar(70) | IP地址 | |
| ip_source | varchar(200) | IP来源 | |
| deleted | int | 删除标志 | 0:正常 1:删除 |

#### 3.1.4 收藏表 (b_collect)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| article_id | bigint | 文章ID |

#### 3.1.5 关注表 (b_follow)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 关注者ID |
| follow_user_id | bigint | 被关注者ID |

#### 3.1.6 分类表 (b_category)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 分类ID |
| name | varchar(255) | 分类名称 |
| summary | text | 分类简介 |
| icon | varchar(255) | 图标 |
| order_num | int | 排序 |
| click_count | bigint | 点击量 |

#### 3.1.7 标签表 (b_tag)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 标签ID |
| name | varchar(255) | 标签名称 |
| summary | text | 标签简介 |
| icon | varchar(255) | 图标 |
| order_num | int | 排序 |
| click_count | bigint | 点击量 |

#### 3.1.8 友链表 (b_friend_link)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar(50) | 网站名称 |
| avatar | varchar(64) | 头像 |
| url | varchar(90) | 链接 |
| description | varchar(255) | 描述 |
| cover | varchar(64) | 封面图 |
| email | varchar(75) | 邮箱 |
| order_num | int | 排序 |
| label | varchar(50) | 标签 |
| category | tinyint | 分类(推荐/精选/更多) |
| status | tinyint | 状态(申请/上架/下架) |
| reason | varchar(255) | 下架原因 |

#### 3.1.9 导航表 (b_web_navbar)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 导航ID |
| name | varchar(50) | 导航名称 |
| summary | varchar(255) | 简介 |
| icon | varchar(45) | 图标 |
| path | varchar(80) | 路由地址 |
| component | varchar(90) | 组件路径 |
| is_frame | varchar(2) | 是否外链 |
| type | char | 类型(目录/菜单) |
| visible | varchar(2) | 是否可见 |
| parent_id | bigint | 父级ID |
| order_num | int | 排序 |

#### 3.1.10 网站配置表 (b_web_config)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| logo | varchar(255) | 网站Logo |
| name | varchar(100) | 网站名称 |
| summary | text | 网站简介 |
| keyword | varchar(255) | 关键字 |
| record_num | varchar(100) | 备案号 |
| web_url | varchar(255) | 网站地址 |
| notice | text | 公告内容 |
| github | varchar(255) | GitHub地址 |
| gitee | varchar(255) | Gitee地址 |
| qq_number | varchar(20) | QQ号 |
| qq_group | varchar(20) | QQ群 |
| email | varchar(255) | 邮箱 |
| wechat | varchar(50) | 微信 |
| open_comment | tinyint | 是否开启评论 |
| open_admiration | tinyint | 是否开启赞赏 |
| author | varchar(100) | 作者 |
| author_info | text | 作者简介 |
| about_me | text | 关于我 |

---

## 四、接口设计

### 4.1 接口规范

- **基础路径**: 通过网关统一暴露，前台/后台以当前网关与 Nginx 路由配置为准
- **认证方式**: Sa-Token + Redis 会话（`Authorization: Bearer <token>`，区分 `admin` / `blog` 登录域）
- **请求格式**: JSON
- **响应格式**: 对外接口统一为 `ApiResponse<T>`，分页统一为 `PageResult<T>`

### 4.2 核心接口列表

#### 4.2.1 文章接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /article/list | 文章列表 | 公开 |
| GET | /article/{id} | 文章详情 | 公开 |
| GET | /article/hot | 热门文章 | 公开 |
| GET | /article/recommend | 推荐文章 | 公开 |
| GET | /article/archives | 文章归档 | 公开 |
| POST | /article | 新增文章 | 登录 |
| PUT | /article | 修改文章 | 登录 |
| DELETE | /article/{ids} | 删除文章 | 登录 |
| POST | /article/like/{id} | 点赞文章 | 登录 |
| POST | /article/collect/{id} | 收藏文章 | 登录 |

#### 4.2.2 分类/标签接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /category/list | 分类列表 | 公开 |
| GET | /category/{id}/articles | 分类下文章 | 公开 |
| GET | /tag/list | 标签列表 | 公开 |
| GET | /tag/{id}/articles | 标签下文章 | 公开 |

#### 4.2.3 评论接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /comment/list | 评论列表 | 公开 |
| GET | /comment/{articleId} | 文章评论 | 公开 |
| POST | /comment | 发表评论 | 登录 |
| POST | /comment/reply | 回复评论 | 登录 |
| DELETE | /comment/{id} | 删除评论 | 登录 |
| POST | /comment/like/{id} | 点赞评论 | 登录 |

#### 4.2.4 用户接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /user/{id} | 用户主页 | 公开 |
| GET | /user/{id}/articles | 用户文章 | 公开 |
| GET | /user/{id}/followers | 粉丝列表 | 公开 |
| GET | /user/{id}/followings | 关注列表 | 公开 |
| POST | /user/follow/{id} | 关注用户 | 登录 |
| DELETE | /user/follow/{id} | 取消关注 | 登录 |
| GET | /user/collect/list | 我的收藏 | 登录 |
| PUT | /user/profile | 修改资料 | 登录 |
| PUT | /user/avatar | 修改头像 | 登录 |

#### 4.2.5 配置接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /config/web | 网站配置 | 公开 |
| GET | /config/home | 首页配置 | 公开 |
| PUT | /config/web | 修改配置 | 管理员 |

#### 4.2.6 其他接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /link/list | 友链列表 | 公开 |
| POST | /link/apply | 友链申请 | 公开 |
| GET | /navbar/list | 导航列表 | 公开 |
| GET | /notice/list | 公告列表 | 公开 |
| GET | /say/list | 说说列表 | 公开 |
| POST | /say | 发布说说 | 登录 |
| GET | /treehole/list | 树洞列表 | 公开 |
| POST | /treehole | 发布树洞 | 公开 |

---

## 五、移动端设计 (Uni-app)

### 5.1 页面结构

```
sourcelin-ui-uniapp/
├── pages/
│   ├── index/              # 首页
│   │   └── index.vue       # 首页(轮播、推荐)
│   ├── article/            # 文章
│   │   ├── list.vue        # 文章列表
│   │   └── detail.vue      # 文章详情
│   ├── category/           # 分类
│   │   └── index.vue       # 分类列表
│   ├── tag/                # 标签
│   │   └── index.vue       # 标签云
│   ├── user/               # 用户
│   │   ├── profile.vue     # 个人资料
│   │   ├── articles.vue    # 我的文章
│   │   ├── collects.vue    # 我的收藏
│   │   └── follows.vue     # 关注/粉丝
│   ├── publish/            # 发布
│   │   └── index.vue       # 发布文章
│   └── about/              # 关于
│       └── index.vue       # 关于页
├── components/
│   ├── ArticleCard/       # 文章卡片
│   ├── CommentItem/       # 评论项
│   └── UserCard/          # 用户卡片
├── static/                # 静态资源
├── App.vue                # 应用配置
├── main.js                # 入口文件
├── manifest.json          # 应用配置
└── pages.json             # 页面配置
```

### 5.2 移动端功能清单

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 首页 | 轮播图、推荐文章、最新文章 | 高 |
| 文章列表 | 分类筛选、标签筛选、分页 | 高 |
| 文章详情 | 内容展示、点赞、收藏、评论 | 高 |
| 分类/标签 | 分类列表、标签云 | 高 |
| 用户中心 | 个人资料、头像修改 | 中 |
| 我的文章 | 我的文章列表、管理 | 中 |
| 我的收藏 | 收藏列表、取消收藏 | 中 |
| 关注/粉丝 | 关注列表、粉丝列表 | 中 |
| 搜索 | 关键词搜索文章 | 中 |
| 发布文章 | Markdown编辑器 | 低 |
| 消息通知 | 评论通知、关注通知 | 低 |

### 5.3 小程序特性适配

| 特性 | 实现方案 |
|------|----------|
| 微信登录 | 使用 open_id + session_key |
| 用户信息 | wx.getUserProfile |
| 分享 | onShareAppMessage |
| 收藏 | wx.addPhoneCalendar |
| 支付赞赏 | 微信支付 |
| 订阅消息 | 订阅通知 |
| 分享海报 | canvas 生成 |

---

## 六、非功能性需求

### 6.1 性能要求

| 指标 | 要求 |
|------|------|
| 首页加载 | < 2秒 |
| 文章详情 | < 1.5秒 |
| API响应 | < 500ms |
| 并发用户 | > 1000 |

### 6.2 安全要求

| 要求 | 说明 |
|------|------|
| 密码加密 | BCrypt |
| 会话认证 | Sa-Token + Redis，统一登录态、续期与强退能力，并区分 admin/blog 域 |
| SQL注入 | 参数绑定 |
| XSS | 输入过滤 |
| 接口限流 | Sentinel |

### 6.3 扩展性

| 扩展点 | 说明 |
|--------|------|
| 主题切换 | CSS变量 |
| 插件系统 | 预留扩展 |
| 第三方登录 | QQ/微信/微博 |
| 搜索服务 | Elasticsearch |

---

## 七、开发计划

### 阶段一：基础完善 (前台API)
1. 创建前台 Controller (14个)
2. 创建前端 API 文件 (11个)
3. 首页数据对接
4. 文章详情页

### 阶段二：用户交互
5. 用户中心完善
6. 评论系统
7. 点赞/收藏/关注

### 阶段三：扩展功能
8. 友链/导航
9. 说说/树洞
10. 搜索功能

### 阶段四：移动端
11. Uni-app 基础框架
12. 核心页面开发
13. 小程序适配

### 阶段五：优化
14. 性能优化
15. SEO优化
16. 安全加固

---

## 附录

### A. 技术选型

| 层级 | 技术 |
|------|------|
| 前端(PC) | 博客前台：Vue 3 + TypeScript + Naive UI；管理后台：Vue 3 + TypeScript + Element Plus |
| 前端(移动) | Uni-app + Vue 3 |
| 后端 | Spring Boot 2.7 + Spring Cloud |
| 微服务 | Nacos + Sentinel + Seata |
| 数据库 | MySQL 8.0 + Redis |
| 文件存储 | MinIO |

### B. 服务端口

| 服务 | 端口 |
|------|------|
| Gateway | 8080 |
| Auth | 9200 |
| System | 9201 |
| Blog | 9204 |
| File | 9300 |
| Gen | 9202 |
| Job | 9203 |
| Monitor | 9100 |

### C. 数据库命名规范

| 前缀 | 说明 |
|------|------|
| sys_ | 系统表 |
| b_ | 博客业务表 |

---

*文档生成时间: 2026-03-15*
*基于项目代码版本: v1.0.0*
