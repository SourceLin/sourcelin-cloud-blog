# Sourcelin Blog Admin

`sourcelin-ui-admin` 是圆圈博客的管理后台，采用 Vue 3.5.46 + TypeScript 6.0.3 + Vite 8.3.8 + Pinia 4.1.0 + Vue Router 5.5.1 + Element Plus 2.14.8 + Vxe Table 4.24.4 + Vxe UI 4.21.7 + WangEditor Next 6.7.0。

## 开发命令

完整的构建和代码检查使用 Node.js 22.22.2 或更新的 22.x 版本，以及 pnpm 10.x。

```bash
pnpm install --frozen-lockfile
pnpm run dev
pnpm run type-check
pnpm run build
```

生产构建包含类型检查，输出到模块内的 `sourcelin-ui-admin/`，部署路径为 `/admin/`。全量 `pnpm run lint` 会修复并格式化文件，在验证副本执行。

## 当前清理策略

- 已移除：示例页面、代码生成器、多租户模块、全局搜索、水印、灰色模式、色弱模式
- 已保留：浅色/暗黑主题、主题色、设置抽屉、动态路由、按钮权限、Axios 封装
- 登录页、首页、错误页已替换为博客后台语义
