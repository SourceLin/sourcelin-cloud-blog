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
│   ├── pages.json         # 页面注册、tabBar、分包、easycom
│   ├── manifest.json      # Uniapp / 微信小程序平台配置
│   ├── main.ts            # createSSRApp + Pinia
│   ├── env.d.ts           # 类型声明（含 @dcloudio/types）
│   ├── uni.scss           # uni-app 主题变量
│   ├── styles/
│   │   └── variables.scss # 业务样式 token + mixin（vite 全局注入）
│   ├── config/
│   │   └── env.ts         # 多环境 baseURL（按 envVersion 切换）
│   ├── utils/
│   │   ├── request.ts     # 网络层：拦截器、错误归一、Token 注入、上传
│   │   ├── auth.ts        # Token 与登录态
│   │   ├── error.ts       # ApiResponse / PageResult / 错误类
│   │   └── storage.ts     # uni.storage 封装
│   ├── stores/
│   │   ├── user.ts        # 登录态、待执行动作队列
│   │   └── app.ts         # 系统信息、站点配置、未读数
│   ├── components/
│   │   └── s-empty/       # 空状态占位（easycom 自动注册）
│   ├── pages/
│   │   ├── home/home.vue
│   │   ├── discover/discover.vue
│   │   ├── community/community.vue
│   │   └── mine/mine.vue
│   └── static/
│       └── tabbar/        # tabBar 图标占位
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
- 复杂列表分页、表单提交、登录引导封装到 `src/composables/**`（按业务域拆分）。
- 组件使用 `S` 前缀（`s-empty`、`s-card`...），通过 `pages.json` 的 `easycom` 自动注册。

## 四、验证命令

```powershell
npm run type-check       # vue-tsc --noEmit
npm run lint             # ESLint
npm run build:mp-weixin  # 微信小程序生产构建
npm run build:h5         # H5 生产构建
```

## 五、当前闭环范围

- 已完成前置篇与产品文档 `5.1` 的 8 个阶段闭环，推文与发布口径统一以 `docs/promotion/wxapp/**` 为准。
- `pages-article`、`pages-user`、`pages-messages`、`pages-about`、`pages-publish` 已承载真实页面，不再作为占位目录描述。
- 后续增量以体验优化、运维发布和证据补录为主，不再回退到“初始化脚手架”口径。
