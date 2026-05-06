# 设计系统分层规则

## 分层顺序

```text
Design Tokens
  -> Foundation
  -> UI Abstraction
  -> Naive UI
  -> Business Components
  -> Pages
```

## 路径映射

- Design Tokens
  - `src/assets/styles/tokens/**`
- Foundation
  - `src/assets/styles/foundation/**`
- Naive Overrides
  - `src/assets/styles/naive/override.scss`
- UI Abstraction
  - `src/shared/components/ui/**`
- Shared Style Facade
  - `src/shared/styles/**`
- Business Components
  - `src/shared/components/layout/**`
  - `src/shared/components/feedback/**`
  - `src/modules/*/components/**`
- Pages
  - `src/modules/*/pages/**`

## 规则

- Token 只定义共享视觉变量。
- Foundation 只提供底层样式能力和兼容修复能力。
- UI 抽象层负责封装 Naive UI 并输出统一视觉原语。
- Shared Style Facade 负责给业务层提供允许消费的样式能力入口。
- 业务组件层消费 `S*` 组件，不直接消费 Naive。
- 页面层只负责装配，不承载底层视觉实现和 Naive 结构认知。

## 单一视觉来源

- 颜色、表面、边框、阴影、渐变优先来自 CSS 变量。
- 新的共享视觉值必须先进入 Token，再由上层消费。
- Naive override 只能进入 `src/assets/styles/naive/override.scss` 或 `src/shared/components/ui/**`。
- 业务层和页面层不得新增硬编码颜色。

## 当前状态

- `src/assets/styles/responsive-enhancements.scss` 仍作为响应式兼容工具层保留。
- `src/assets/styles/foundation/responsive.scss` 是新增底层断点能力的首选归属。
- 业务组件、共享业务组件和页面只能通过 `src/shared/styles/responsive.scss` 间接消费响应式 mixin。
- `src/modules/**`、`src/shared/components/{layout,feedback,comments,business}/**` 禁止直接导入 `@/assets/styles/foundation/responsive`。
- `src/assets/styles/legacy/**` 已移除；即使未来重新出现，也不应获得任何自动豁免。
