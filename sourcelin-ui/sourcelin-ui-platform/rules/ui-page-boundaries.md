# 页面层规则

适用范围：`src/modules/*/pages/**`

## 页面层职责

- 作为路由入口
- 装配模块组件和共享组件
- 组织页面级布局
- 调用模块 composable 并连接页面所需数据
- 根据 `route.meta` 参与页面壳、导航和展示状态切换

## 页面层禁止项

- 直接使用 `n-*` 标签
- 直接导入 `naive-ui`
- 使用 `:deep(.n-*)`
- 编写 `.n-*` override
- 直接调用 glass mixin
- 在页面内堆积可复用业务逻辑
- 在页面内直接写请求基座逻辑

## 页面层推荐项

- 复杂状态优先放入 `src/modules/*/composables/**`
- 跨模块复用逻辑优先放入 `src/shared/composables/**`
- 重复页面壳优先抽到 `src/shared/components/layout/**`
- 页面仅依赖模块 API、共享组件和 composable，不直接依赖 Naive 结构细节
