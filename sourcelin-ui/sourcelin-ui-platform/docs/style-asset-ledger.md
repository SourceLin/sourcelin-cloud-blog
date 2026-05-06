# 样式资产台账

本文档记录当前样式资产的归属、用途、导入顺序和治理口径。

## 1. 当前样式目录

```text
src/assets/styles/
  index.scss
  responsive-enhancements.scss
  _functions.scss
  _mixins.scss
  foundation/
  naive/
  tokens/
```

## 2. 全局样式入口

### `index.scss`

当前导入顺序：

1. `tokens/base`
2. `tokens/semantic`
3. `tokens/component`
4. `foundation/glass`
5. `foundation/theme`
6. `foundation/form-controls`
7. `foundation/animations`
8. `naive/override`
9. `foundation/page-shell`
10. `responsive-enhancements`

职责：

- 提供全局基础标签样式
- 汇总 token、foundation、override 和兼容响应式样式
- 为 `#app` 和常用通用类提供最低层样式基座

## 3. 主线样式资产

### `tokens/`

包含：

- `base.scss`
- `semantic.scss`
- `component.scss`

职责：

- 定义颜色、表面、边框、阴影、字体、间距等 CSS 变量
- 作为业务层唯一正式视觉变量来源

当前主题基准：

- 亮色主题以 `#F5F7FB` 为页面底色、`#4F46E5` 为主品牌色、`rgba(255, 255, 255, 0.65)` 为玻璃卡片底色
- 暗色主题以 `#0B101D` 为页面底色、`#6366F1` 为主品牌色、`rgba(21, 29, 47, 0.5)` 为玻璃卡片底色
- 新增或调整组件视觉时，优先沿用这组冷调编辑感蓝色体系，不再回退到旧紫色主导方案

### `foundation/`

包含：

- `theme.scss`
- `glass.scss`
- `animations.scss`
- `form-controls.scss`
- `functions.scss`
- `mixins.scss`
- `page-shell.scss`
- `responsive.scss`

职责：

- 承载主题、玻璃态、动画、页面壳、断点 mixin、表单 autofill 修复等底层能力
- 为 UI 抽象层和全局样式提供底层原语
- 不直接承载业务页面结构和业务视觉实现

说明：

- `responsive.scss` 是当前推荐的底层断点 mixin 归属
- `form-controls.scss` 负责浏览器 autofill 等全局表单兼容问题

### `naive/override.scss`

职责：

- 放置全局 Naive override
- 收纳 `.n-*` 全局覆盖规则

### `src/shared/components/feedback/skeletons/**`

职责：

- 承载全站共享结构化骨架组件
- 统一消费 `SSkeleton`、CSS 变量和面板语义，不在业务页面重复拼首屏加载样式
- 当前覆盖：首页文章卡、统计卡、导航卡、用户列表、树洞流、搜索结果等加载骨架

### `responsive-enhancements.scss`

职责：

- 当前仍保留的全局响应式兼容工具层
- 提供移动端快捷类、滚动锁定、安全区和触控增强等实用工具

治理口径：

- 可以继续被现有页面消费
- 不作为新增底层能力的首选目录
- 新的通用响应式原语优先收敛到 `foundation/responsive.scss`
- 业务组件和页面通过 `src/shared/styles/responsive.scss` 消费响应式 mixin，不直接导入 Foundation 路径

## 4. 兼容转发层

以下文件仍保留为兼容入口：

- `src/assets/styles/_functions.scss`
- `src/assets/styles/_mixins.scss`

规则：

- 新代码优先直接使用 `foundation/**`
- 兼容转发层不再扩展新的样式能力

## 5. 样式守卫边界

### style-guard 扫描范围

- `src/app`
- `src/modules`
- `src/shared`
- `src/stores`
- `src/assets/styles`

### 允许直接接触 Naive 的范围

- `src/app/App.vue`
- `src/shared/components/ui/**`
- `src/shared/api/http.ts`

### 当前守卫口径

- `naive-ui` 导入禁止出现在业务层
- `n-*` 标签禁止出现在业务层页面和组件
- `n-skeleton` 只能封装在 `SSkeleton` 内部，业务层统一消费 `SSkeleton`
- `:deep(.n-*)`、`.n-*` override 只能留在 override 或 UI 抽象层
- glass mixin 只能留在 Foundation 或 UI 抽象层
- 业务层和页面层不得新增硬编码颜色

### allowlist

- `scripts/style-guard.allowlist.json` 当前存在且为空
- 空 allowlist 代表当前没有登记中的样式守卫债务

## 6. 使用优先级

1. 新页面和新组件优先消费 `var(--*)`、`S*` 组件和局部样式。
2. 新的共享视觉变量优先进入 `tokens/`。
3. 新的底层样式能力优先进入 `foundation/`。
4. Naive 结构覆盖统一进入 `naive/override.scss` 或 UI 抽象层。
5. `responsive-enhancements.scss` 仅继续承载现有兼容类，不再扩张为新的主线样式层。
6. 首屏结构化加载优先复用 `src/shared/components/feedback/skeletons/**`；增量加载保持 spinner / quiet loading，不新建整页大骨架。

## 7. 最低验证

```bash
npm run style:guard
npm run test:style-guard
npm run test:architecture
```
