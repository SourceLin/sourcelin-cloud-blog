# FoldableOverflowBlock

可复用布局块：当插槽内容高度超过「折叠可见区预算」时，提供底部轻提示、展开/收起与收起后的锚点滚动修正。

**实现**：[`src/shared/components/layout/FoldableOverflowBlock.vue`](../src/shared/components/layout/FoldableOverflowBlock.vue)

## 使用场景

- [`TagPage.vue`](../src/modules/article/pages/TagPage.vue) 全部标签网格  
- [`CategoryPage.vue`](../src/modules/article/pages/CategoryPage.vue) 全部分类网格  

## Props

| Prop | 说明 |
|------|------|
| `itemCount` | 列表条数；变化时会收起展开态并重新挂载测量逻辑 |
| `expandButtonText` | 折叠态按钮文案 |
| `collapseButtonText` | 展开态按钮文案 |
| `foldedVisibleMaxHeightPx` | 可选，默认 `320`，折叠可见区高度预算（px） |
| `overflowEpsilonPx` | 可选，默认 `2`，与 `scrollHeight` 比较的容差 |
| `getScrollAnchor` | 可选，收起后若锚点滚出视口上方则 `scrollIntoView`；返回外壳节点（如 `section` 的 ref） |

## 设计说明（摘要）

行为细节（为何不用无限滚动、为何用真实溢出、`ResizeObserver`、收起滚动修正等）见 [TagPage 标签网格折叠说明](./tag-page-tag-grid-fold.md) 中对应章节；实现已迁至本组件，页面只保留文案与锚点解析。
