# SCSS 与 Token 规则

## Token 规则

- 共享视觉值统一进入 `src/assets/styles/tokens/**`
- 业务组件和页面优先通过 `var(--*)` 消费视觉值
- 业务组件和页面中禁止新增硬编码颜色

## Foundation 规则

- `src/assets/styles/foundation/**` 只放底层样式能力
- 新的 theme、glass、animation、responsive、functions、mixins、form-controls 只能加入 Foundation
- 业务组件和页面不得直接使用 Foundation 中的 glass mixin

## Override 规则

- 全局 Naive override 统一放在 `src/assets/styles/naive/override.scss`
- UI 抽象层内部允许保留必要的 Naive 结构认知
- 业务层和页面层不能直接写 `.n-*` 样式覆盖

## 兼容层规则

- `src/assets/styles/responsive-enhancements.scss` 当前仍保留为全局响应式兼容工具层
- `_functions.scss`、`_mixins.scss` 当前仅作为兼容转发层
- 兼容层不再扩展新的主线能力

## 当前状态

- `src/assets/styles/legacy/**` 已删除
- 不再新增任何 legacy 样式目录或兼容样式层
- 需要新增底层响应式能力时，优先进入 `src/assets/styles/foundation/responsive.scss`
- 业务组件、共享业务组件和页面需要响应式 mixin 时，统一从 `src/shared/styles/responsive.scss` 导入
- `scripts/style-guard.allowlist.json` 当前为空，应继续保持无新增样式债务
