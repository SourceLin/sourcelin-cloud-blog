# 标签页 / 分类页 · 网格折叠说明

标签与分类列表的长网格交互已抽成共享组件 **`FoldableOverflowBlock`**，见 [foldable-overflow-block.md](./foldable-overflow-block.md) 与源码 [`FoldableOverflowBlock.vue`](../src/shared/components/layout/FoldableOverflowBlock.vue)。

**接入页面**：

- [`TagPage.vue`](../src/modules/article/pages/TagPage.vue)（全部标签）
- [`CategoryPage.vue`](../src/modules/article/pages/CategoryPage.vue)（全部分类）

下列设计理由仍适用于两处页面；常量与状态现由组件 props / 内部 ref 管理。

## 为何采用「折叠 + 展开全部」而非无限滚动

- 标签与分类是**可扫视的索引入口**，需要快速对比名称与元数据；无限滚动不利于扫视与回到列表顶部。
- 折叠只解决**首屏过长**，展开后仍是完整网格，与「一次拉全量列表」的接口模型一致。

## 为何展开/收起按钮用「真实溢出」判断，而非条数阈值

- 卡片高度随**文案、列数、字体**变化；条数阈值会误判。
- 组件以 `scrollHeight` 与**折叠可见区高度预算**（默认 `foldedVisibleMaxHeightPx = 320`）比较，并结合 `ResizeObserver` 与窗口 `resize` 重算。

## 为何收起时做轻量滚动修正

- 展开态向下滚动后再收起，文档高度回缩可能导致视口错位；若传入 `getScrollAnchor` 且外壳 `getBoundingClientRect().top < 0`，则 `scrollIntoView({ behavior: 'smooth', block: 'start' })`。

## 折叠遮罩与按钮视觉

- 遮罩类名：`foldable-overflow-block__hint`，轻量底部渐变。
- 按钮类名：`foldable-overflow-block__toggle`，`fit-content` 居中，辅助操作样式。

## 相关 Props / 常量（组件侧）

| 名称 | 含义 |
|------|------|
| `foldedVisibleMaxHeightPx` | 折叠预览区最大高度（像素），语义为可见区预算 |
| `overflowEpsilonPx` | 与 `scrollHeight` 比较的容差 |
| 内部 `overflow` | 内容是否高于上述预算 |
| 内部 `expanded` | 用户是否已展开完整内容 |
