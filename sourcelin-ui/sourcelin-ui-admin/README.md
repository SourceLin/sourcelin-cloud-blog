# Sourcelin Blog Admin

`sourcelin-ui-admin` 是 圆圈博客 的后台管理端，基于 `Vue 3 + Vite + TypeScript + Element Plus`。

## 开发命令

```bash
pnpm install
pnpm run dev
pnpm run type-check
pnpm run build
```

## 当前清理策略

- 已移除：示例页面、代码生成器、多租户模块、全局搜索、水印、灰色模式、色弱模式
- 已保留：浅色/暗黑主题、主题色、设置抽屉、动态路由、按钮权限、Axios 封装
- 登录页、首页、错误页已替换为博客后台语义
