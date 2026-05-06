# 说明说明

## 目录映射

| 层级 | 路径 | 职责 | 禁止 |
| --- | --- | --- | --- |
| App | `src/app/**` | 启动链路、根壳、路由聚合、route meta | 页面级业务逻辑堆积 |
| Design Tokens | `src/assets/styles/tokens/**` | CSS 变量和语义令牌 | 业务逻辑 |
| Foundation | `src/assets/styles/foundation/**` | theme、glass、animation、responsive、mixins、functions、form-controls | 页面和业务实现 |
| Naive Overrides | `src/assets/styles/naive/override.scss` | 全局 Naive override | 业务逻辑 |
| UI 抽象层 | `src/shared/components/ui/**` | Naive 封装、`S*` 组件 | 业务域逻辑 |
| Shared Layout/Feedback | `src/shared/components/{layout,feedback}/**` | 跨模块共享布局和反馈组件 | 直接依赖 Naive |
| Modules | `src/modules/**` | 模块页面、组件、API、页面逻辑 | 直接依赖 Naive |
| Pages | `src/modules/*/pages/**` | 页面装配和路由入口 | Naive override、底层视觉原语 |

## route meta 速查

| 字段 | 用途 |
| --- | --- |
| `title` | 标题 |
| `childTitle` | 头部导航父级自项 |
| `icon` | 移动导航图标 |
| `hidden` | 是否隐藏 |
| `order` | 排序 |
| `nav` | `header` / `header-child` / `mobile` / `footer` |
| `parent` | 子导航父路径 |
| `mobile` | 是否进入移动导航 |
| `footer` | 是否进入页脚导航 |
| `shell` | 页面壳，当前支持 `default` / `auth` |

## Store 速查

| 文件 | 角色 |
| --- | --- |
| `src/stores/site.store.ts` | 站点信息和访问统计 |
| `src/stores/ui.store.ts` | 搜索弹层、移动抽屉、startup loading |
| `src/stores/theme.ts` | 主题主实现 |
| `src/stores/theme.store.ts` | 主题兼容导出 |
| `src/stores/user.ts` | 登录态、用户信息、权限 |
| `src/stores/auth.store.ts` | 认证兼容导出 |

## 请求层速查

- 统一基座：`src/shared/api/http.ts`
- 模块 API：`src/modules/*/api/*.api.ts`
- `/front/**`、`/app/**` 会自动补成 `/blog/...`
- 匿名请求显式传 `headers: { isToken: false }`
- 禁止 `createDiscreteApi`；全局提示用 `useSMessage` / `useSNotification` 中的 `sDiscrete*`（由根壳 `SMessage` / `SNotification` 注入）

## 常用检查项

- 业务组件和页面里没有 `n-*`
- 业务组件和页面里没有 `:deep(.n-*)`
- 业务组件和页面里没有 glass mixin
- 业务组件和页面里没有新增硬编码颜色
- 新颜色来自 `var(--*)`
- 导航仍由 route meta 推导
- 架构变更已同步到 `README`、`AGENTS`、`docs`、`rules`、`skills` 和轻量入口

## 验证命令

```bash
npm run style:guard
npm run test:style-guard
npm run test:architecture
```
