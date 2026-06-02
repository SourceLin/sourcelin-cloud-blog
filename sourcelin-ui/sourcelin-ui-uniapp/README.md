# Sourcelin Blog Uniapp 客户端

> Vue3 + TypeScript + Vite + Pinia + Uniapp
> 编译目标：微信小程序（基础库 3.0+）、H5 移动端、APP-PLUS（可选）
> 上层蓝图：[`docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md`](../../docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md)、[`docs/product/UNIAPP_MINI_PROGRAM_DEV_BREAKDOWN.md`](../../docs/product/UNIAPP_MINI_PROGRAM_DEV_BREAKDOWN.md)

## 一、目录结构

```text
sourcelin-ui-uniapp/
├── package.json
├── tsconfig.json
├── vite.config.ts
├── src/
│   ├── App.vue            # 全局生命周期入口
│   ├── pages.json         # 页面注册、tabBar、分包、easycom、preloadRule
│   ├── manifest.json      # Uniapp / 微信小程序平台配置
│   ├── main.ts            # createSSRApp + Pinia
│   ├── env.d.ts           # 类型声明（含 @dcloudio/types）
│   ├── uni.scss           # uni-app 主题变量
│   ├── styles/
│   │   └── variables.scss # 业务样式 token + mixin（vite 全局注入）
│   ├── config/
│   │   └── env.ts         # 多环境 baseURL（按 envVersion 切换）
│   ├── utils/
│   │   ├── request.ts     # 网络层：拦截器、错误归一、Token 注入、上传、AbortSignal 清理
│   │   ├── auth.ts        # Token、登录态、401 去重、待办动作内存队列
│   │   ├── error.ts       # ApiResponse / PageResult / 错误类
│   │   └── storage.ts     # uni.storage 封装
│   ├── stores/
│   │   ├── user.ts        # 登录态、待执行动作队列
│   │   ├── app.ts         # 系统信息、站点配置、未读数
│   │   └── theme.ts       # 主题模式（跟随系统/浅/暗）、Native 颜色同步
│   ├── components/
│   │   └── s-*/           # easycom 自动注册的通用组件（s-empty、s-loading、s-back-to-top 等）
│   ├── shared/
│   │   ├── api/           # 跨模块共享 API（auth、file、http）
│   │   ├── composables/   # 跨模块共享 composable（useBackToTop、useKeyboardInset、useThrottledPageScroll）
│   │   ├── types/         # 共享类型（ApiResponse、PageResult、ListResult）
│   │   └── utils/         # 共享工具（analytics、pending-actions、seo、user-mapper）
│   ├── modules/           # 业务域（article/auth/user/comment/community/interaction/message/site/subscription/report）
│   ├── pages/             # 主包 Tab 页面（home、discover、community、mine）
│   ├── pages-article/     # 分包：文章详情、列表、搜索
│   ├── pages-user/        # 分包：登录、资料、设置、互动、收藏、关注、文章、用户主页
│   ├── pages-messages/    # 分包：消息中心、消息详情
│   ├── pages-about/       # 分包：关于、协议、政策、友链、导航
│   ├── pages-publish/     # 分包：发布说说、树洞、写文章
│   └── static/
│       └── tabbar/        # tabBar 图标
└── README.md
```

## 二、首次启动

```powershell
cd sourcelin-ui\sourcelin-ui-uniapp
npm install
npm run dev:mp-weixin   # 编译微信小程序，生成 unpackage/dist/dev/mp-weixin
npm run dev:h5          # 浏览器调试
```

启动前置：

1. 新建本机 `.env.local`，配置 `VITE_WECHAT_MINI_APP_ID=你的微信小程序AppID`；该文件已被 `.gitignore` 忽略，真实 AppID 不写入 `src/manifest.json`。
2. `src/config/env.ts` 配置后端网关 baseURL。
3. 微信开发者工具导入 `unpackage/dist/dev/mp-weixin` 目录。
4. 真机预览前需在小程序后台配置服务器域名白名单。

订阅消息模板配置：

1. 登录微信公众平台/小程序后台。
2. 进入 `功能 -> 订阅消息`，从公共模板库选用业务模板并加入“我的模板”。
3. 复制模板详情里的 `模板 ID`，按环境填入 `src/config/env.ts` 的 `subscribeMessageTemplateIds`。
4. 前端发起订阅授权时会直接读取当前环境的模板 ID 列表；未配置时只提示，不会弹出授权窗口。

当前已推荐并接入的模板：

- 模板名：`留言回复通知`
- 模板 ID：`y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM`
- 场景说明：`文章评论回复提醒`
- 推荐关键词：`文章标题 / 回复人 / 回复内容 / 留言时间 / 留言内容 / 回复时间`

微信小程序 AppID 公开边界：

- `src/manifest.json` 的 `mp-weixin.appid` 必须保持为 `__WECHAT_MINI_APP_ID__` 占位值。
- `npm run dev:mp-weixin` 和 `npm run build:mp-weixin` 会在命令执行期间读取 `.env.local` 并临时注入真实 AppID，结束后自动恢复占位源码。
- 如果未配置 `VITE_WECHAT_MINI_APP_ID`，命令仍可构建，但产物不能用于真实微信登录预览。

## 三、核心约束

- API 契约严格遵循 [`rules/api-contract.md`](../../rules/api-contract.md)：
  - `ApiResponse<T> = { code, message, data, requestId, timestamp }`，成功码 `0`。
  - `PageResult<T> = { items, total, page, pageSize, totalPages }`。
  - 业务页面禁止消费 `msg/rows/records/list/pageNum/limit`，禁止判断 `code === 200`。
- 业务页面禁止直连 `uni.request` / `uni.uploadFile`，必须经由 [`src/utils/request.ts`](src/utils/request.ts)。
- 401 处理统一通过 `handle401()`（[`src/utils/auth.ts`](src/utils/auth.ts)），禁止页面自行调用 `clearToken` + 跳登录页。
- 登录成功后必须调用 `reset401Guard()` 重置 401 去重标志位。
- 用户资料 API 从 `@/modules/user/api/user.api` 导入，认证 API 从 `@/shared/api/auth.api` 导入。
- 复杂列表分页、表单提交、登录引导封装到 `src/modules/<domain>/composables/**`。
- 组件使用 `S` 前缀（`s-empty`、`s-loading`、`s-back-to-top`...），通过 `pages.json` 的 `easycom` 自动注册。
- `onPageScroll` 必须使用 `useThrottledPageScroll`，禁止直接逐像素写响应式状态。

## 四、分包配置

`src/pages.json` 已配置分包加载，主包仅保留 4 个 Tab 页面：

| 包 | root | 页面数 | 预加载触发 |
|---|---|---|---|
| 主包 | — | 4 | — |
| pages-article | pages-article | 3 | 首页 |
| pages-user | pages-user | 8 | 我的 |
| pages-messages | pages-messages | 2 | — |
| pages-about | pages-about | 5 | 发现 |
| pages-publish | pages-publish | 3 | — |

- 新增页面时：Tab 页入主包 `pages`，业务页入对应 `subPackages`。
- 主包体积限制 2MB，子包各限制 2MB。
- 构建后验证：`npm run build:mp-weixin`，检查 `dist/build/mp-weixin/` 主包大小。

## 四、验证命令

```powershell
npm run type-check       # vue-tsc --noEmit
npm run lint             # ESLint
npm run build:mp-weixin  # 微信小程序生产构建
npm run build:h5         # H5 生产构建
```

## 五、当前闭环范围

- 已完成前置篇与产品文档 `5.1` 的 8 个阶段闭环，推文与发布口径统一以 `docs/promotion/wxapp/**` 为准。
- `pages-article`、`pages-user`、`pages-messages`、`pages-about`、`pages-publish` 已配置为微信小程序分包，主包仅含 4 个 Tab 页面。
- 请求层 401 已统一去重（`handle401`），待办动作已改为内存队列优先避免 TOCTOU 竞态。
- `onPageScroll` 已全量替换为 `useThrottledPageScroll` rAF 节流。
- 后续增量以体验优化、运维发布和证据补录为主，不再回退到”初始化脚手架”口径。
