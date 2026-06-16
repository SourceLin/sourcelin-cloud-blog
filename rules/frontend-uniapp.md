# 前端规则 - Uniapp 移动端

适用范围：`sourcelin-ui/sourcelin-ui-uniapp`，包含微信小程序、H5 和 App 端构建。

## 1. 目录边界

- MUST：Tab 页面放在 `src/pages/<tab>/<tab>.vue`，并在 `src/pages.json` 的 `tabBar.list` 中显式声明。
- MUST：业务分组页面放在 `src/pages-article/**`、`src/pages-user/**`、`src/pages-messages/**`、`src/pages-about/**`、`src/pages-publish/**` 等既有页面目录。
- MUST：跨页面业务能力放在 `src/modules/<domain>/{api,composables,types,utils}/**`。
- MUST：跨端基础能力放在 `src/utils/**`、`src/config/**`、`src/shared/**` 或 `src/stores/**`。
- MUST：全局样式 token、mixin 和液态玻璃基础变量放在 `src/styles/**` 或 `src/uni.scss`。
- MUST：通用组件使用 `src/components/s-*/s-*.vue`，并通过 `pages.json` 的 easycom `s-*` 规则自动注册。
- MUST NOT：页面文件直接堆叠大段请求编排、复杂状态机或跨页面复用逻辑。
- MUST NOT：新增 Web 前台目录结构到 Uniapp，例如 `src/modules/**/pages` 或 Naive UI 组件抽象。
- CHECK：审查新增文件路径，并执行 `npm run lint` 与 `npm run type-check`。

## 2. API 与登录契约

- MUST：遵守 `rules/api-contract.md`，页面只消费 request 层解包后的业务数据。
- MUST：Uniapp 请求统一经过 `src/utils/request.ts`，禁止页面和 composable 直接调用 `uni.request`。
- MUST：接口地址统一通过 `src/config/env.ts` 的 `baseURL + apiPrefix` 生成。
- MUST：移动端前台 API 使用 `https://sourcelin.cn` + `/blog-api`，本地调试如需切换域名只改环境配置，不在页面硬编码。
- MUST：移动端账号登录使用 `/auth/captcha`、`/auth/login`，`loginType` 固定为 `mini`。
- MUST：移动端复用博客用户体系，登录态接口使用 `/front/user/info`、`/front/user/profile` 等 `/front/**` 前台接口。
- MUST：文件资源使用统一文件前缀或后端返回的可访问 URL，不在页面拼接 `127.0.0.1` 或内网地址。
- MUST NOT：Uniapp 调用 `/prod-api/**`、`/system/**`、`/blog/admin/**`、`/monitor/**` 等后台管理域接口。
- MUST NOT：页面判断 `code === 200`，或读取 `msg/rows/records/list/pageNum/limit`。
- CHECK：涉及 API 改动时执行 `npm run type-check`，并审查 API 类型、请求路径、分页字段和 `loginType`。

## 3. 页面、路由与小程序配置

- MUST：新增页面必须同步登记到 `src/pages.json`。
- MUST：新增页面必须正确归入主包或分包：Tab 页放入顶层 `pages`，业务页放入对应 `subPackages`（pages-article/pages-user/pages-messages/pages-about/pages-publish）。
- MUST：主包仅保留 4 个页面（首页、发现、圈子、我的），其中 3 个为原生 TabBar（首页、发现、我的），圈子通过自定义液态 TabBar + `navigateTo` 承载；主包体积必须 < 2MB。
- MUST：新增 Tab 时必须同时提供普通和选中态图标，路径放在 `src/static/tabbar/**`，尺寸和体积满足微信小程序要求。圈子 Tab 图标同样需要提供，虽非原生 Tab 但液态 TabBar 依赖这些图标。
- MUST：微信小程序分包只允许使用真实存在的目录作为 `subPackages[].root`。
- MUST：分包页面路径必须位于分包 root 目录下；不满足该条件时保持普通页面注册，避免微信开发者工具报 root 非目录。
- MUST：页面跳转使用 `uni.navigateTo`、`uni.redirectTo`、`uni.switchTab`、`uni.navigateBack`，并根据页面栈选择返回策略。
- MUST：下拉刷新、上拉加载和返回顶部等移动端交互使用 Uniapp 生命周期或页面事件实现。
- MUST NOT：使用 Vue Router、浏览器 History API 或动态路由能力实现小程序页面跳转。
- MUST NOT：在小程序端直接访问 `window`、`document`、`localStorage`、`sessionStorage`。
- MUST NOT：在 `pages.json` 顶层 `pages` 中注册非 Tab 页面，避免主包膨胀超 2MB。
- CHECK：路由、Tab、分包或 manifest 改动后执行 `npm run build:mp-weixin`，并检查输出 `dist/build/mp-weixin/app.json` 和主包体积。

## 4. UI 与液态玻璃移动端规范

- MUST：移动端视觉遵守 `docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md` 与 Sourcelin Liquid Glass Mobile 设计语言。
- MUST：颜色、圆角、阴影、间距使用 `src/styles/variables.scss` 或派生 token，避免页面内散落不可复用的硬编码。
- MUST：玻璃态效果以半透明底色、渐变边框、轻阴影和弥散光背景为主。
- MUST：`backdrop-filter`、高强度 `filter: blur()` 等高耗能效果只在 H5 或 App 条件编译中使用，小程序端必须降级为伪透光样式。
- MUST：列表卡片可使用非对称杂志感布局，但必须保证移动端首屏信息清晰、触达区域不小于 `80rpx`。
- MUST：表单、抽屉、评论、登录引导等核心操作需要明确加载态、空态、错误态和禁用态。
- MUST NOT：照搬 Web 端大屏布局、密集导航、悬浮 hover 交互或桌面端信息架构。
- MUST NOT：引入重型 UI 框架替代现有自定义组件体系。
- CHECK：样式或组件改动后执行 `npm run lint`，核心页面改动后执行 `npm run build:mp-weixin`。

## 5. 性能、缓存与跨端兼容

- MUST：列表请求使用分页，分页参数使用 `page/pageSize`，返回读取 `items/total/page/pageSize/totalPages`。
- MUST：首页、文章列表、搜索、社区流等高频列表保留下拉刷新、上拉加载、加载态和到底态。
- MUST：`onPageScroll` 使用 `useBackToTop`（`src/shared/composables/useBackToTop`）的 `handlePageScroll`，禁止直接逐像素写响应式状态。
- MUST：图片使用合适尺寸、懒加载或缩略图策略；Tab 图标和静态资源必须控制体积。
- MUST：登录 Token、用户信息和轻量缓存通过 `src/utils/storage.ts` 或封装层访问。
- MUST：需要平台差异能力时使用 Uniapp 条件编译，并保证微信小程序为优先验证平台。
- MUST：微信正式小程序上线前必须确认 request 合法域名、隐私协议、授权弹窗和 HTTPS 要求。
- MUST：401 处理统一通过 `handle401()`（`src/utils/auth.ts`），禁止页面或请求层自行重复调用 `clearToken` + 跳登录页。
- MUST：登录成功后调用 `reset401Guard()` 重置 401 去重标志位。
- MUST：模板中禁止对同一 item 重复调用解析函数（如 resolveImages/resolveAvatar），应预计算为 computed Map 后查表取值。
- MUST：累积集合（brokenUrl/brokenId 等）使用 `shallowRef` + `Set.add()` + `triggerRef()`，禁止 `new Set([...old, val])` 全量重建。
- MUST NOT：为了移动端首版引入 WebSocket、复杂推荐算法或大体积富文本组件，除非任务明确要求并评估包体积。
- CHECK：发布前执行 `npm run lint`、`npm run type-check`、`npm run build:mp-weixin`；涉及 H5 输出时追加 `npm run build:h5`。
