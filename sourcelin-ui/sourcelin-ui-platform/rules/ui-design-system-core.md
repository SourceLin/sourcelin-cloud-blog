# UI 设计系统核心规则

## Naive UI 边界

允许直接使用 Naive UI 的位置：

- `src/shared/components/ui/**`
- `src/app/App.vue`
- `src/shared/composables/useSMessage.ts`、`useSNotification.ts`（与根 `NConfigProvider` 桥接的 Message / Notification 封装，**禁止**使用 `createDiscreteApi`）

禁止直接使用 Naive UI 的位置：

- `src/shared/components/layout/**`
- `src/shared/components/feedback/**`
- `src/modules/*/components/**`
- `src/modules/*/pages/**`

## UI 抽象层职责

- 封装 `N*` 组件
- 承担 Naive 主题和结构细节
- 输出 `S*` 组件给业务层消费
- 吸收必要的 Naive override 和交互细节

## 根级例外

- `src/app/App.vue` 可以持有根级 Provider
- `src/app/App.vue` 可以基于 CSS 变量计算 Naive theme overrides
- 认证壳切换、全局搜索弹层、主题过渡、启动 loading 由根组件控制

## 不可突破规则

- 不得在业务组件和页面中新增 `n-*` 标签
- 不得在业务组件和页面中直接导入 `naive-ui`
- 不得把 Naive override 外溢到业务层
- 不得把根级 Provider 配置下沉到业务组件
- 不得让 layout / feedback 组件绕过 UI 抽象层直接接触 Naive
