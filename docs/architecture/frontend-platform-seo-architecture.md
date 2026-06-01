# 博客前台 SEO 技术改造方案

> 适用项目：`sourcelin-ui/sourcelin-ui-platform`
> 技术栈：Vue 3 + TypeScript + Vite 8 + Pinia + Vue Router 4
> 文档状态：**评估通过 / 待实施**

---

## 一、现状诊断

| 维度 | 当前状态 | SEO 影响 |
|------|----------|----------|
| 渲染模式 | 纯 CSR（客户端渲染） | 爬虫抓取到空 HTML Shell，内容不可索引 |
| Title | 硬编码 `圆圈博客`，全站一致 | 搜索结果无法区分页面 |
| Meta Description | 无 | 搜索摘要为空或爬虫自动截取，质量低 |
| Open Graph | 无 | 社交分享无预览图/标题 |
| Sitemap / robots.txt | 无 | 爬虫缺乏索引引导 |
| 语义化 HTML | 未系统落地 | 影响内容权重识别 |
| 动态路由 `/article/:id` | 无预渲染 | 文章详情页完全不可被静态索引 |

---

## 二、渲染架构选型

### 三条路线对比

| 路线 | 改造成本 | SEO 效果 | 动态内容实时性 | 对现有架构侵入 |
|------|----------|----------|----------------|----------------|
| CSR + 预渲染（vite-plugin-prerender） | 低 | 中（仅覆盖已知静态路由） | 实时（客户端水合） | 极小，仅 Vite 插件层 |
| SSG（静态站点生成） | 中高（需改为 VitePress 或 Nuxt content 模式） | 高（构建时生成完整 HTML） | 弱（需重新构建才能更新） | 高，需重构数据获取层 |
| SSR（Nuxt 迁移） | 极高（架构重写） | 最优（服务端实时渲染） | 最佳 | 侵入整体架构 |

### 推荐落地路线：CSR + 动态 Head 注入 + Nginx 爬虫代理

**选型理由：**

1. 本项目核心 SEO 价值在于文章详情页（`/article/:id`），为动态 ID 参数路由，传统预渲染无法覆盖。
2. `@unhead/vue` 在客户端动态注入完整 TDK + OG 标签，可在零架构迁移成本下解决社交分享和搜索结果展示问题。
3. Nginx 层识别爬虫 UA，对爬虫流量走 Prerender 服务代理，普通用户流量仍走 CSR，主流量不受影响。
4. SSR/SSG 迁移成本与当前收益不成比例，且会破坏液态玻璃 CSS 体系、Naive UI 主题系统及启动 loading 等 UI 基础设施。
5. 符合 `AGENTS.md §15` 禁止引入重型框架替代当前技术栈的约束。

---

## 三、核心模块设计

### 3.1 动态 TDK 与 Open Graph 管理

**选型：`@unhead/vue`**

- VueUse 生态官方维护的新一代 Head 管理库，支持 Vue 3 Composition API，与现有 `@vueuse/core` 同生态，无额外引入风险。
- 在 `src/app/main.ts` 全局安装 `createHead()`，在各页面 composable 中调用 `useHead()`。

**管理分层：**

```
全局默认 TDK（App.vue）
  └── 模块级路由覆盖（router.afterEach + route.meta.seoTitle）
       └── 详情页动态覆盖（useSeoHead composable，从 API 响应提取）
```

**页面标题格式约定：**

- 静态页面：`{route.meta.title} - {siteInfo.webName}`
- 文章详情：`{article.title} - {siteInfo.webName}`
- 首页：`{siteInfo.webName} - {siteInfo.webTitle[0]}`

**Open Graph 字段映射（文章详情页）：**

| OG 字段 | 数据来源 |
|---------|----------|
| `og:title` | `article.title` |
| `og:description` | `article.summary`（截取 160 字） |
| `og:image` | `article.cover`（封面图绝对 URL） |
| `og:url` | `window.location.href` |
| `og:type` | `article` |
| `article:published_time` | `article.createTime` |
| `twitter:card` | `summary_large_image` |

---

### 3.2 动态路由爬虫抓取策略

**问题：** 文章详情页为 `/article/:id`，爬虫不执行 JS 时无法获取内容。

**策略 A（生产环境，推荐）：Nginx + Prerender 中间件**

- Nginx 配置识别爬虫 UA（Googlebot、Bingbot、Baidubot 等）。
- 将爬虫流量转发至 Prerender 服务（自托管 prerender/prerender 或 SaaS）。
- 普通用户流量不受影响，继续走 CSR。
- 前端代码零侵入，Nginx 配置片段维护在 `docs/configs/` 目录。

**策略 B（开发阶段过渡）：构建时预渲染静态路由**

- `vite-plugin-prerender` 仅预渲染已知静态路由（首页、归档、分类、标签）。
- 文章详情页因 ID 动态无法预渲染，仍依赖客户端渲染。
- 适合 Prerender 服务尚未部署时的临时过渡。

---

### 3.3 Sitemap.xml 与 robots.txt

**Sitemap 生成思路：**

- 静态路由：Vite 构建插件读取 `appRoutes`，过滤 `hidden: false` 路由生成静态条目。
- 动态路由：构建时请求后端 `/blog/front/article/list`（全量，仅取 ID 和更新时间），生成 `/article/:id` 条目。
- 输出：构建产物根目录 `sitemap.xml`。
- 更新：CI/CD 流水线每次构建自动重新生成。

**robots.txt 策略（静态文件，放 `public/robots.txt`）：**

```
User-agent: *
Allow: /
Disallow: /auth/
Disallow: /user/
Sitemap: https://sourcelin.cn/sitemap.xml
```

---

## 四、UI/UX 稳定性保障约束

SEO 改造只触及 `<head>` 元数据层和页面容器语义结构层，**严禁触碰 CSS 令牌体系、Naive UI 主题覆盖、液态玻璃变量和动效层。**

| 约束范围 | 规则 |
|----------|------|
| `src/assets/styles/` | SEO 改造期间只读，禁止修改任何 SCSS 令牌文件 |
| `S*` 系列 UI 抽象组件内部 | 禁止修改，语义化只在外层包裹容器标签 |
| `App.vue` 的 `themeOverrides` | 不受影响，`@unhead/vue` 仅操作 `<head>` |
| 按钮交互状态 | 不触碰，SEO 改造无需修改 interactive 组件 |
| 响应式布局 | `useHead()` 与布局媒体查询完全解耦 |
| 液态玻璃 `backdrop-filter` | 不触碰，属于视觉渲染层与 SEO 无关 |

**语义化改造边界：**

- 语义化改造**仅限**页面级 `.vue` 文件最外层容器标签（`<main>`、`<article>`、`<section>`）。
- 新增 `<h1>`、`<h2>` 时须确认与现有 CSS 选择器不冲突（优先使用 scoped 样式）。
- 不修改 `S*` 系列组件内部 DOM 结构。

---

## 五、分步实施计划

### Phase 1：SEO 基础设施搭建

> 目标：建立动态 Head 管理能力，完成全站 Title 动态化

- [ ] 安装 `@unhead/vue`，在 `src/app/main.ts` 注册 `createHead()`
- [ ] 扩展 `AppRouteMeta` 接口，新增可选字段 `seoTitle`、`seoDescription`
- [ ] 在 `src/app/router/index.ts` 的 `afterEach` 钩子中，根据 `route.meta` 自动更新页面 `<title>`
- [ ] 在 `App.vue` 设置全局默认 TDK，数据来源为 `useSiteStore().siteInfo`
- [ ] 创建 `src/shared/composables/useSeoHead.ts`，封装通用 `useHead()` 调用逻辑
- [ ] 创建 `public/robots.txt`
- [ ] 验证：`npm run typecheck`

### Phase 2：详情页动态 SEO

> 目标：文章详情页具备完整动态 TDK + Open Graph

- [ ] 在文章详情页 composable（`useArticleDetail.ts`）中，文章数据加载完成后调用 `useSeoHead()`
- [ ] 映射 OG 字段：`title / summary / cover / createTime`
- [ ] 补充 `og:url`、`og:type: 'article'`、`twitter:card`
- [ ] 补充 `<link rel="canonical">` 防止重复内容
- [ ] 验证：社交分享预览工具（Open Graph Debugger）+ `npm run typecheck`

### Phase 3：Sitemap 自动生成与爬虫策略

> 目标：建立爬虫索引引导体系

- [ ] 实现 Vite 自定义插件，构建时读取 `appRoutes` 生成静态路由 Sitemap 条目
- [ ] 构建插件调用文章列表 API，追加动态文章 URL 条目（含 `lastmod`、`changefreq`、`priority`）
- [ ] 将 `sitemap.xml` 写入 `dist/` 根目录
- [ ] 在 `robots.txt` 中声明 `Sitemap:` 地址
- [ ] 提供 Nginx 爬虫 UA 识别配置片段，维护在 `docs/configs/nginx-prerender.conf`
- [ ] 验证：`npm run build` 后检查 `sitemap.xml` 正确生成

### Phase 4：语义化 HTML 补全

> 目标：核心页面语义化，结构性 SEO 完备

- [ ] 首页 `HomePage.vue`：外层容器改为 `<main>`，添加视觉隐藏 `<h1>`（站点名称）
- [ ] 文章详情页：`<article>` 包裹内容主体，`<h1>` 承载文章标题
- [ ] 归档、分类、标签页：`<section>` 容器 + `<h1>`
- [ ] 全站语义化改造不触碰任何 `S*` 组件内部
- [ ] 验证：`npm run typecheck` + `npm run style:guard` + `npm run test:architecture` 三项全绿

---

## 六、依赖变更

| 包名 | 类型 | 说明 |
|------|------|------|
| `@unhead/vue` | `dependencies` | Head 管理核心库，VueUse 生态 |
| Vite Sitemap 自定义插件 | `devDependencies` 或内联 Vite 插件 | Sitemap 构建插件，按需引入第三方或自实现 |

> 不引入 Nuxt、Next.js、SSR 框架，不替换现有技术栈，符合 `AGENTS.md §15` 约束。

---

## 七、验证命令（与现有流程对齐）

```bash
# Phase 1 / Phase 2 / Phase 4
npm run typecheck
npm run style:guard
npm run test:architecture

# Phase 3
npm run build
# 验证 dist/sitemap.xml 存在且包含文章 URL 条目
```

---

## 八、上线后收录提交流程

> 部署完成后需主动提交搜索引擎，否则需等待自然爬取（可能数周至数月）。

### 8.1 上线即时验证

```
# 验证 robots.txt 可访问
https://sourcelin.cn/robots.txt

# 验证 sitemap 已生成
https://sourcelin.cn/sitemap.xml

# 验证文章页 TDK（浏览器 DevTools → Elements → <head>）
https://sourcelin.cn/article/{id}
```

### 8.2 Google Search Console

1. 进入 [Google Search Console](https://search.google.com/search-console)
2. 添加属性 → 输入 `https://sourcelin.cn`，完成所有权验证
3. 左侧菜单 → **站点地图** → 提交 `https://sourcelin.cn/sitemap.xml`
4. 左侧菜单 → **网址检查** → 输入首页 URL → 点击「请求编入索引」

### 8.3 百度搜索资源平台

1. 进入 [百度搜索资源平台](https://ziyuan.baidu.com/)
2. 添加网站 → 完成所有权验证
3. 左侧 → **普通收录 → sitemap** → 提交 `https://sourcelin.cn/sitemap.xml`
4. 左侧 → **普通收录 → 主动推送（API）** → 每次发布新文章后可调用接口推送 URL，加速收录

### 8.4 其他搜索引擎（推荐一次性完成）

| 平台 | 提交入口 | 说明 |
|------|----------|------|
| **Bing 网站管理员** | https://bing.com/webmasters | 可导入 Google Search Console 数据，5 分钟完成；覆盖 Yahoo、DuckDuckGo |
| **360 熊掌号** | https://zhanzhang.so.com | 提交 sitemap 即可，国内 360 搜索流量 |
| **神马搜索** | https://zhanzhang.sm.cn | 阿里系，主攻移动端搜索流量 |
| **头条搜索** | https://mp.toutiao.com | 字节系，今日头条 / 抖音搜索入口 |

### 8.5 内容平台同步（增加反向链接）

在以下平台同步精选文章（保留原文链接），可提升外链权重，加速百度收录：

| 平台 | 适合内容 |
|------|---------|
| 掘金（juejin.cn） | 技术类文章，权重高，百度收录快 |
| 博客园（cnblogs.com） | 技术博客，老牌高权重站点 |
| CSDN（csdn.net） | 技术内容，国内搜索流量大 |
| 开源中国（oschina.net） | 开源项目介绍，可绑定外链 |

> 同步时在文章头部或尾部注明「原文链接：https://sourcelin.cn/article/{id}」，有助于搜索引擎识别原创来源。

### 8.6 社交媒体 OG 调试（可选）

| 平台 | 工具地址 |
|------|----------|
| Facebook / 微信分享 | https://developers.facebook.com/tools/debug/ |
| Twitter/X | https://cards-dev.twitter.com/validator |
| 通用 OG 检测 | https://metatags.io |

### 8.7 预期收录时间线

| 阶段 | 说明 |
|------|------|
| 提交 sitemap 后 1–3 天 | Google 开始爬取首页和静态页 |
| 1–2 周 | 文章详情页陆续被 Google 索引；Bing 同步跟进 |
| 2–4 周 | 百度开始收录（收录速度慢于 Google） |
| 持续运营 | 新文章配合百度主动推送 API + 内容平台同步加速收录 |
