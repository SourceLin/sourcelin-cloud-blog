# AGENTS.md

本文件是 Sourcelin Uniapp 移动端子项目的 AI 执行入口。进入 `sourcelin-ui/sourcelin-ui-uniapp` 后，必须先读取仓库根 `AGENTS.md`，再读取本文件。

默认使用中文沟通、注释和文档更新。所有文本文件保存为 UTF-8 无 BOM。

## 1. 项目定位

`sourcelin-ui-uniapp` 是 Sourcelin Blog 的移动端客户端，当前重点支持微信小程序，同时保留 H5 和 App 构建能力。

技术栈：

```text
Uniapp + Vue3 + TypeScript + Vite
Pinia 状态管理
自定义 s-* 组件体系
Sourcelin Liquid Glass Mobile 设计语言
```

## 2. 必读规则

每次移动端开发、重构、审查或测试必须读取：

- `rules/README.md`
- `rules/api-contract.md`
- `rules/frontend-uniapp.md`
- `rules/coding-conventions.md`
- `rules/testing-and-validation.md`
- `skills/frontend-uniapp-dev/SKILL.md`

涉及后端接口新增或修改时，追加读取：

- `rules/backend.md`
- `skills/backend-dev/SKILL.md`
- `skills/api-contract-governance/SKILL.md`

## 3. 目录约定

```text
src/pages/**              # Tab 页面：首页、发现、社区、我的
src/pages-article/**      # 文章详情、列表、搜索
src/pages-user/**         # 登录、资料、收藏、关注、用户主页
src/pages-messages/**     # 消息中心
src/pages-about/**        # 关于、友链、导航
src/pages-publish/**      # 发布说说、树洞
src/modules/**            # 业务域 API、types、composables、utils
src/components/s-*/       # easycom 自动注册的通用组件
src/stores/**             # Pinia 全局状态
src/utils/**              # request、auth、storage、RSA 等基础能力
src/config/env.ts         # 域名、API 前缀和运行环境
src/styles/**             # 设计 token、mixin、全局样式
src/static/**             # tabbar、图片等静态资源
```

## 4. API 规则

- 移动端 API 基础地址由 `src/config/env.ts` 统一控制，当前远程域名为 `https://sourcelin.cn`。
- 移动端前台接口前缀为 `/blog-api`。
- 移动端登录使用 `/auth/captcha`、`/auth/login`，`loginType` 固定为 `mini`。
- 当前用户资料使用 `/front/user/info`、`/front/user/profile`。
- 页面和业务 composable 不得直接调用 `uni.request`，必须经过 `src/utils/request.ts`。
- 禁止移动端调用 `/prod-api/**`、`/system/**`、`/blog/admin/**` 等管理域接口。
- 禁止读取旧响应字段 `msg/rows/records/list/pageNum/limit` 或判断 `code === 200`。

## 5. 页面与小程序配置

- 新增页面必须同步维护 `src/pages.json`。
- 新增 Tab 必须同步提供普通和选中图标，放入 `src/static/tabbar/**`。
- `subPackages[].root` 必须是真实目录；若页面不在分包 root 下，不得强行配置分包。
- 小程序页面跳转只能使用 `uni.navigateTo`、`uni.redirectTo`、`uni.switchTab`、`uni.navigateBack`。
- 禁止使用 Vue Router、浏览器 History API、`window`、`document`、`localStorage`。

## 6. UI 与体验

- UI 遵守 `docs/product/UNIAPP_MINI_PROGRAM_PRODUCT_DESIGN.md` 中的 Liquid Glass Mobile 规范。
- 小程序端玻璃态以半透明底色、渐变边框、轻阴影和弥散光为主，避免大面积 `backdrop-filter`。
- 核心页面必须提供加载态、空态、错误态、到底态或禁用态。
- 移动端必须优先考虑单手操作、触达区域、弱网反馈、下拉刷新和上拉加载。
- 禁止照搬 Web 端密集导航、大屏布局、hover 交互和桌面端信息密度。

## 7. 验证命令

移动端改动按影响范围执行：

```powershell
cd sourcelin-ui\sourcelin-ui-uniapp
npm run lint
npm run type-check
npm run build:mp-weixin
```

涉及 H5 构建或跨端兼容时追加：

```powershell
npm run build:h5
```

构建完成后，微信开发者工具导入：

```text
sourcelin-ui/sourcelin-ui-uniapp/dist/build/mp-weixin
```

## 8. 禁止事项

- MUST NOT：提交 `dist/`、`.tmp_*`、`*.zip`、`*.tgz` 等产物。
- MUST NOT：硬编码真实账号、密码、token、cookie、私钥或生产 AppSecret。
- MUST NOT：为了通过编译删除核心页面、降低 lint/type-check 规则或放宽 API 契约。
- MUST NOT：引入重型 UI 框架替代当前移动端自定义组件体系。
- MUST NOT：回退用户已有无关改动。
