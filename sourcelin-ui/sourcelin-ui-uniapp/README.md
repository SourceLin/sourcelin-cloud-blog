# Sourcelin Blog Uniapp 客户端

> Vue3 + TypeScript + Vite + Pinia + Uniapp
> 编译目标：微信小程序（基础库 3.0+）、H5 移动端、APP-PLUS（可选）
> 上层蓝图：[`docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md`](../../docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md)、[`docs/product/UNIAPP_MINI_PROGRAM_DEV_BREAKDOWN.md`](../../docs/product/UNIAPP_MINI_PROGRAM_DEV_BREAKDOWN.md)

## 一、目录结构

```text
sourcelin-ui-uniapp/
├── package.json
├── manifest.json          # 应用配置（含 mp-weixin.appid 占位）
├── pages.json             # 页面注册、tabBar、分包、easycom
├── tsconfig.json
├── vite.config.ts
├── src/
│   ├── App.vue            # 全局生命周期入口
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

1. `manifest.json` 中 `mp-weixin.appid` 替换为真实 AppID。
2. `src/config/env.ts` 配置后端网关 baseURL。
3. 微信开发者工具导入 `unpackage/dist/dev/mp-weixin` 目录。
4. 真机预览前需在小程序后台配置服务器域名白名单。

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

## 五、不在初始化范围

- 业务接口实现、富文本组件、半屏登录面板。
- 文章 / 用户 / 社区分包页面（`pages-article`、`pages-user`、`pages-publish` 仅占位）。
- Mock 服务、CI 流水线、uview-plus / uni-ui 等 UI 库接入。
