# 博客服务 URL 矩阵（抽检用）

本文档对应 **`sourcelin-blog`** 服务内 Controller 暴露的路径。经网关访问时，通常带有统一前缀（例如 **`/blog`**），管理后台前端请求形如 **`/blog/admin/...`**，博客前台经 Vite 代理多为 **`/blog-api/front/...`** 再转发到网关 **`/blog/front/...`**，以实际 `application.yml` / Nacos 为准。

## 1. 管理端 `/admin/*`

| 方法 | 服务内路径 | 管理后台 API 典型前缀 |
|------|------------|------------------------|
| GET | `/admin/article/list` | `/blog/admin/article/list` |
| GET | `/admin/article/{id}` | `/blog/admin/article/{id}` |
| POST | `/admin/article` | `/blog/admin/article` |
| PUT | `/admin/article` | `/blog/admin/article` |
| DELETE | `/admin/article/{ids}` | `/blog/admin/article/{ids}` |
| POST | `/admin/article/export` | `/blog/admin/article/export` |
| GET | `/admin/category/list` | `/blog/admin/category/list` |
| GET | `/admin/category/{id}` | `/blog/admin/category/{id}` |
| POST/PUT/DELETE | `/admin/category` 等 | 同 pattern |
| GET | `/admin/tag/list` | `/blog/admin/tag/list` |
| POST/PUT/DELETE | `/admin/tag` 等 | 同 pattern |
| GET | `/admin/comment/list` | `/blog/admin/comment/list` |
| GET | `/admin/comment/{id}` | `/blog/admin/comment/{id}` |
| POST/PUT/DELETE | `/admin/comment` | `/blog/admin/comment` |
| GET | `/admin/moderation/keyword/current` | `/blog/admin/moderation/keyword/current` |
| POST | `/admin/moderation/keyword/refresh` | `/blog/admin/moderation/keyword/refresh` |
| POST | `/admin/moderation/keyword/save` | `/blog/admin/moderation/keyword/save` |
| GET | `/admin/say/list` | `/blog/admin/say/list` |
| POST/PUT/DELETE | `/admin/say` 等 | 同 pattern |
| GET | `/admin/treehole/list` | `/blog/admin/treehole/list` |
| POST/PUT/DELETE | `/admin/treehole` 等 | 同 pattern |
| GET | `/admin/link/list` | `/blog/admin/link/list` |
| POST | `/admin/link/export` | `/blog/admin/link/export` |
| PUT | `/admin/link/status` | `/blog/admin/link/status` |
| POST/PUT/DELETE | `/admin/link` 等 | 同 pattern |
| GET | `/admin/user/list` | `/blog/admin/user/list` |
| POST | `/admin/user/export` | `/blog/admin/user/export` |
| PUT | `/admin/user/resetPwd` | `/blog/admin/user/resetPwd` |
| POST/PUT/DELETE | `/admin/user` 等 | 同 pattern |
| GET | `/admin/follow/list` | `/blog/admin/follow/list` |
| GET | `/admin/follow/{id}` | `/blog/admin/follow/{id}` |
| DELETE | `/admin/follow/{ids}` | `/blog/admin/follow/{ids}` |
| GET | `/admin/collect/list` | `/blog/admin/collect/list` |
| GET | `/admin/collect/{id}` | `/blog/admin/collect/{id}` |
| DELETE | `/admin/collect/{ids}` | `/blog/admin/collect/{ids}` |
| GET | `/admin/interactions/dashboard/overview` | `/blog/admin/interactions/dashboard/overview` |
| GET | `/admin/interactions/actions` | `/blog/admin/interactions/actions` |
| GET | `/admin/report/list` | `/blog/admin/report/list` |
| GET | `/admin/report/{id}` | `/blog/admin/report/{id}` |
| PUT | `/admin/report/{id}/status` | `/blog/admin/report/{id}/status` |
| DELETE | `/admin/report/{ids}` | `/blog/admin/report/{ids}` |
| GET | `/admin/config/getWebConfig` | `/blog/admin/config/getWebConfig` |
| POST/PUT | `/admin/config` | `/blog/admin/config` |
| GET | `/admin/navbar/list` | `/blog/admin/navbar/list` |
| POST/PUT/DELETE | `/admin/navbar` 等 | 同 pattern |
| GET | `/admin/navigation/list` | `/blog/admin/navigation/list` |
| POST | `/admin/navigation/export` | `/blog/admin/navigation/export` |
| POST/PUT/DELETE | `/admin/navigation` 等 | 同 pattern |
| GET | `/admin/stats/summary` | `/blog/admin/stats/summary` |
| GET | `/admin/stats/trend` | `/blog/admin/stats/trend` |

**前端对照**：`sourcelin-ui-admin/src/api/blog/*.ts`（article、category、tag、comment、moderation、say、treehole、link、user、follow、collect、report、config、navbar、navigation、stats）。

---

## 2. 前台 `/front/*`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/front/home` | 首页聚合 |
| GET | `/front/config` | 站点配置（全量） |
| GET | `/front/config/siteInfo` | 站点信息（投影 VO） |
| GET | `/front/navbars` | 顶栏 |
| GET | `/front/messages` | 消息/公告分页列表 |
| GET | `/front/messages/{id}` | 消息/公告详情 |
| POST | `/front/messages/{id}/read` | 标记已读（需登录） |
| POST | `/front/messages/read-all` | 批量已读（需登录） |
| GET | `/front/messages/unread-count` | 未读数（需登录） |
| GET | `/front/navigation` | 导航列表（支持 query） |
| POST | `/front/navigation/{id}/click` | 导航点击上报 |
| GET | `/front/stats` | 全站统计 |
| GET | `/front/user/stats` | 当前登录用户统计（未登录返回空数据） |
| GET | `/front/user/info` | 当前登录博客用户信息 |
| GET | `/front/user/profile` | 当前登录用户资料（简介字段：`introduction`） |
| PUT | `/front/user/profile` | 更新当前登录用户资料（入参含 `introduction`） |
| POST | `/front/user/avatar` | 上传当前登录用户头像 |
| GET | `/front/users/{id}` | 用户卡片 |
| GET | `/front/users/articles/{id}` | 用户文章分页 |
| GET | `/front/users/followers/{id}` | 粉丝列表 |
| GET | `/front/users/followings/{id}` | 关注列表 |
| GET | `/front/articles` | 文章列表（分页） |
| GET | `/front/articles/{id}` | 文章详情 |
| POST | `/front/articles` | 发表文章 |
| PUT | `/front/articles/{id}` | 更新文章 |
| DELETE | `/front/articles/{id}` | 删除文章 |
| GET | `/front/articles/search` | 文章搜索 |
| POST | `/front/articles/view/{id}` | 浏览上报 |
| GET | `/front/categories` | 分类列表 |
| GET | `/front/categories/{id}` | 分类详情 |
| GET | `/front/categories/articles/{id}` | 分类下文章 |
| GET | `/front/categories/search` | 分类搜索 |
| GET | `/front/tags` | 标签列表 |
| GET | `/front/tags/{id}` | 标签详情 |
| GET | `/front/tags/articles/{id}` | 标签下文章 |
| GET | `/front/tags/search` | 标签搜索 |
| GET | `/front/search/hot` | 搜索热词（标题抽样） |
| GET | `/front/search/suggestions` | 搜索建议 |
| GET | `/front/hot/articles` | 热门文章 |
| GET | `/front/hot/categories` | 热门分类 |
| GET | `/front/hot/searches` | 热门搜索 |
| GET | `/front/comments` | 评论列表 |
| POST | `/front/comments` | 发表评论 |
| POST | `/front/comments/like/{id}` | 评论点赞 |
| GET | `/front/says` | 说说列表 |
| GET | `/front/says/{id}` | 说说详情 |
| POST | `/front/says` | 发表说说 |
| GET | `/front/treeholes` | 树洞列表 |
| GET | `/front/treeholes/{id}` | 树洞详情 |
| POST | `/front/treeholes` | 发布树洞 |
| DELETE | `/front/treeholes/{id}` | 删除树洞 |
| GET | `/front/treehole` | 树洞别名入口（兼容） |
| GET | `/front/links` | 友链列表 |
| POST | `/front/links/apply` | 友链申请（匿名需网关放行） |
| GET | `/front/follows/following` | 我的关注 |
| GET | `/front/follows/fans` | 我的粉丝 |
| POST | `/front/follows` | 关注 |
| DELETE | `/front/follows/{id}` | 取消关注 |
| PUT | `/front/interactions/likes/{targetType}/{targetId}` | 点赞（幂等） |
| DELETE | `/front/interactions/likes/{targetType}/{targetId}` | 取消点赞（幂等） |
| PUT | `/front/interactions/collects/{targetType}/{targetId}` | 收藏（幂等） |
| DELETE | `/front/interactions/collects/{targetType}/{targetId}` | 取消收藏（幂等） |
| GET | `/front/interactions/targets/stat` | 多目标互动状态聚合 |
| GET | `/front/interactions/collects` | 我的收藏列表（需登录） |
| GET | `/front/interactions/likes` | 我的点赞列表（需登录） |
| GET | `/front/comments/mine` | 我发出的评论列表（需登录，`source` 可选：`article` / `say` / `treehole`） |
| POST | `/front/subscriptions/authorizations` | 记录小程序订阅消息授权结果（需登录） |

**前端对照**：`sourcelin-ui-platform/src/modules/**/api/*.api.ts` 与 `src/shared/api/comment.api.ts`。

**互动域契约补充**：
- 评论来源 `source` 固定语义：`article` / `say` / `treehole`。
- 互动动作接口统一遵循当前标准 HTTP JSON 契约：顶层 `ApiResponse<T>`，业务数据放在 `data` 内，由前端统一 request 层解包处理。

**说明**：博客前台用户资料相关接口已统一收敛到 `/front/user/*`，用户简介字段固定使用 `introduction`（不再使用 `remark` 作为简介来源）。

---

## 3. 内部 Feign `/user/*`（非浏览器）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/info/{param}` | 查询用户，需 `FROM_SOURCE=INNER` |
| POST | `/user/register` | 内部注册 |
| POST | `/user/update` | 内部更新 |

实现类：`RemoteUserController`。

---

## 4. 文件中心确权 `biz_type`（博客侧写入）

| biz_type | 触发位置 |
|----------|----------|
| `article` | `ArticleServiceImpl`（封面 + 正文内文件 ID） |
| `say` | `SayServiceImpl`（`imageFileIds`） |
| `friend_link` | `FriendLinkServiceImpl`（`avatarFileId`） |

---

## 5. 建议抽检顺序（阶段 C）

1. **只读抽检（无需登录）**：`GET /front/stats`、`/front/categories`、`/front/navbars`、`/front/config/siteInfo`、`/front/links`。  
2. **登录后抽检**：`/front/user/info`、`/front/user/stats`、`/front/follows/following`、`/front/interactions/collects`。  
3. **写操作抽检**：发文、发评、友链申请（含本地上传头像）、说说带图（确认文件服务可用时成功）。  
4. **管理端**：文章与友链各走一遍增删改，并核对 `file_info` 中 `biz_type` / `biz_id`。

修改网关或 StripPrefix 后，请同步更新本文档中的「典型前缀」列。

---

## 6. 自动化冒烟脚本（阶段 D）

脚本路径：仓库根目录下的 `scripts/blog-api-smoke.mjs`（Node 18+，依赖全局 `fetch`）。

**路径说明**：`node scripts/blog-api-smoke.mjs` 里的 `scripts` 是**相对当前工作目录**的。若不在仓库根目录执行，会报 `Cannot find module ... scripts\blog-api-smoke.mjs`。

- 先 `cd` 到仓库根目录（含 `scripts` 文件夹）。
- 再执行 `node scripts/blog-api-smoke.mjs`。

**Windows 命令提示符（CMD）** — 使用 `set`，不要用 PowerShell 的 `$env:`：

```bat
set BLOG_SMOKE_BASE_URL=http://localhost:8080/blog
node scripts\blog-api-smoke.mjs
```

**Windows PowerShell**：

```powershell
$env:BLOG_SMOKE_BASE_URL = "http://localhost:8080/blog"
node scripts/blog-api-smoke.mjs
```

**直连 `sourcelin-blog`（无网关前缀时）**：把 base 改为 `http://localhost:9204` 即可。

**Linux / macOS（bash）**：

```bash
cd /path/to/sourcelin-cloud-blog
export BLOG_SMOKE_BASE_URL=http://localhost:8080/blog
node scripts/blog-api-smoke.mjs
```

- 默认探测的 path 见脚本内 `PATHS`（与 §5 中「只读抽检」高度重合）。  
- 超时可通过 `BLOG_SMOKE_TIMEOUT_MS` 覆盖（默认 15000）。  
- 退出码：`0` 全部 2xx；`1` 存在失败；未设置 `BLOG_SMOKE_BASE_URL` 为 `2`。  
- CI 中若无稳定环境，可跳过该步骤或仅在合并前流水线中配置密钥与 base URL 后执行。
