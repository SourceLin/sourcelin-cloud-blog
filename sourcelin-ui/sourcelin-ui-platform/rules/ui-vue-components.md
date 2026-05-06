# 业务组件规则

适用范围：

- `src/shared/components/layout/**/*.vue`
- `src/shared/components/feedback/**/*.vue`
- `src/modules/*/components/**/*.vue`

## 必须

- 优先使用 `S*` 组件
- 使用 CSS 变量消费视觉值
- 让组件职责清晰，避免把整页状态和副作用塞回组件
- 页面级编排逻辑优先进入模块 composable
- 导航展示类组件优先依赖 route meta 推导结果，而不是额外维护第二份结构

## 禁止

- 直接使用 `n-*` 标签
- 直接导入 `naive-ui`
- 使用 glass mixin
- 使用 `:deep(.n-*)`
- 编写 `.n-*` override
- 新增硬编码颜色
- 在业务组件内直接写 axios 请求

## 推荐组织方式

- 跨模块复用：放 `src/shared/components/**`
- 单一业务域组件：放 `src/modules/<domain>/components/**`
- 复杂交互逻辑：放 `src/modules/<domain>/composables/**` 或 `src/shared/composables/**`
