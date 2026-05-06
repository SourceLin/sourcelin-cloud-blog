# 平台端统计卡使用与展示策略

日期: 2026-04-19  
状态: 建议方案

## 目标

在平台前台建立一套稳定的统计展示策略，使整站同时满足三件事：

1. **统一性**：用户能感知到这些块属于同一个网站、同一套产品语言。
2. **个性化**：不同页面仍保留自己的内容气质和信息节奏，不会被做成一套“万能卡片”。
3. **贴合功能**：统计块的结构、密度、强调方式和交互形式，必须符合页面当前的功能特点。

这份方案不是继续新增组件，而是定义：

- 各页面应该使用哪个组件家族
- 各组件在页面中应该以什么形式出现
- 哪些页面允许保留局部特例，哪些页面必须强制统一

---

## 当前组件谱系

### 1. `HeroStatCard`

位置：

- `sourcelin-ui/sourcelin-ui-platform/src/shared/components/business/HeroStatCard.vue`

职责：

- 统一所有**横向 Hero 摘要卡**
- 统一结构：`icon + value/title + label/meta`
- 统一 hover、边框、高光、图标底座
- 提供两种语义变体：
  - `tone="neutral"`
  - `tone="tinted"`

适用特征：

- 位于页面 Hero 区
- 信息密度低到中
- 单卡偏“横向摘要”
- 卡片之间是并列关系
- 主要作用是“先扫一眼整体状态”

不适用：

- 纵向高密度统计面板
- 带 summary / trend / 主卡跨列的组合统计块
- 需要高度情绪化、强色相面板感的区域

### 2. `StatsPanelCard`

位置：

- `sourcelin-ui/sourcelin-ui-platform/src/shared/components/business/StatsPanelCard.vue`

职责：

- 统一所有**色相化统计面板单卡**
- 支持：
  - 常规面板
  - `primary` 强调卡
  - `compact` 紧凑卡
  - `trend`
  - `suffix`

适用特征：

- 纵向展示
- 单卡高度更高
- 色相驱动更明显
- 统计块可能属于一个更大的面板系统
- 页面会保留 grid / summary / 主次层级等组合规则

不适用：

- 纯横向 Hero 摘要栏
- 文案导向的链接型摘要卡

---

## 设计系统分层原则

本方案遵循：

`Tokens -> Foundation -> UI Abstraction -> Business/Page`

对应解释：

1. `Tokens`
- 只提供颜色、阴影、半径、表面变量

2. `Foundation`
- 只提供材料能力和基础语义变量
- 不直接决定统计卡成品视觉

3. `UI Abstraction`
- 保持中性
- 例如 `SMetricCard`、`SLinkCard` 只做中性结构能力
- 不再承担 Hero / StatsPanel 成品视觉

4. `Business`
- `HeroStatCard`
- `StatsPanelCard`
- 由这层承接最终统计展示协议

5. `Page`
- 只负责布局装配、列数、跨列、文案和局部顺序
- 不再自己维护一整套统计卡背景、hover、图标底与数值排版

---

## 总体使用规则

### 规则 A：先判断“横向摘要”还是“纵向面板”

如果页面统计块满足下面多数条件，优先用 `HeroStatCard`：

- Hero 区摘要
- 横向并列
- 用户先扫一眼
- 值与标签同权重
- 卡片高度较低

如果满足下面多数条件，优先用 `StatsPanelCard`：

- 统计块本身就是一块内容面板
- 卡片更高
- 强调数字层次
- 色相更明显
- 可能有主卡 / 副卡 / trend / suffix

### 规则 B：统一不等于完全同皮肤

统一的是：

- 卡片骨架
- 高光逻辑
- 边框逻辑
- 图标底座逻辑
- hover 节制
- 字体层次

允许个性化的是：

- `tone`
- `hue`
- `density`
- grid 列数
- 是否跨列
- 是否带附加说明面板

### 规则 C：页面个性应来自“组合方式”，不是来自“重新造卡”

页面之间的差异优先来自：

- 三列还是四列
- 是否有 aside
- 是否有 summary 卡
- 是否有主卡跨列
- 文案和图标语义

而不是：

- 每个页面都再写一套 `__stat-icon / __stat-value / __stat-label`

---

## 页面使用方案

## 一、信息型 Hero 页面

这类页面的统计块作用是“开场摘要”，应统一使用 `HeroStatCard`。

### 1. User

页面：

- `UserPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + neutral`
- 布局：横向三卡或四卡摘要

原因：

- 用户页核心是个人概览
- 统计卡应该像“个人信息面板的摘要”
- 不需要太强的情绪色相

展示建议：

- 保持中性玻璃面板
- 让头像、资料区成为视觉中心
- 统计卡只做辅助概览

### 2. About

页面：

- `AboutPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`link + neutral`
- 布局：横向链接摘要卡

原因：

- About 页不是数据页，而是信息入口页
- 需要更偏“说明 / 跳转”语义，而不是强数值面板

展示建议：

- 标题可稍强
- meta 用于补充说明
- 不要做成重色相数据卡

### 3. Navigation

页面：

- `NavigationPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + neutral`
- 布局：横向摘要栏

原因：

- 导航页的统计块是内容索引摘要，不是主题展陈
- 应与 user/about 共享同一 Hero 家族

展示建议：

- 中性色
- 信息清晰
- 不用再单独造第三套视觉

---

## 二、文库索引型页面

这类页面虽然同属 Hero 摘要，但它们天然带有“分类 / 标签 / 索引”的内容属性，允许更明确的 `tinted` 气质。

### 4. Archive

页面：

- `ArchivePage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：2 张摘要卡

原因：

- 归档页强调“时间分组”和“文章总数”
- 有明显索引感，但不需要高密度面板

展示建议：

- 使用低饱和 tinted
- 不要超过 2 卡，避免喧宾夺主

### 5. Category

页面：

- `CategoryPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要

原因：

- 分类页属于“内容索引页”
- 三张卡分别承接总分类、内容篇数、有文分类，结构标准

展示建议：

- 允许第 3 张在中断点下跨列
- 页面个性主要保留在分类卡网格和文章切换区

### 6. Tag

页面：

- `TagPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要 + 保留 skeleton

原因：

- 标签页与分类页结构同构
- 但标签页更偏轻量、高频浏览，需要保留 skeleton 体验

展示建议：

- Hero 摘要卡统一
- 当前标签高亮面板继续留在页面局部，不并进 HeroStatCard

---

## 三、运营 / 内容导向页面

这类页面也适合 `HeroStatCard`，但页面功能比普通文库索引更活跃，允许更强一点的 `tinted`。

### 7. Hot

页面：

- `HotPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要

原因：

- 热榜页是运营内容页
- Hero 统计块的职责是告诉用户：当前分类、收录内容、总热度

展示建议：

- `tinted` 可以略强于 archive/category/tag
- 但不要抢过下方精选卡和瀑布流

### 8. Links

页面：

- `LinksPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要，其中一张是按钮态

原因：

- 友情链接页需要同时承接信息展示和操作引导
- “申请通道”那张卡天然是 CTA，但仍然应该和其它两张同骨架

展示建议：

- 按钮态只改变交互，不改变视觉骨架
- 这页的个性应来自右侧“复制本站信息”面板，而不是重新造摘要卡

### 9. Notice

页面：

- `MessageCenterPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要

原因：

- 消息页需要告诉用户当前频道、频道数量、当前状态
- 但真正重心在左侧菜单和消息列表，不在 Hero 统计块

展示建议：

- `tinted` 强度控制在中低档
- 让消息区仍然是主要信息载体

### 10. NewPost

页面：

- `NewPostPage`

推荐：

- 组件：`HeroStatCard`
- 形式：`metric + tinted`
- 布局：3 卡摘要

原因：

- 创作页是“带工具属性的功能页”
- Hero 统计块的作用是补充上下文：分类数量、标签数量、当前稿件状态

展示建议：

- 维持 `tinted`，但要比 Hot 更克制
- 不能压过编辑器表单本体

---

## 四、高密度统计面板页面

这类页面不适合再继续使用 `HeroStatCard`，应统一到 `StatsPanelCard`。

### 11. Home 侧栏统计

组件：

- `HomeSiteStats`

推荐：

- 组件：`StatsPanelCard`
- 形式：`regular` + `primary`
- 布局：2 列统计网格 + summary 特例卡

原因：

- 首页侧栏统计本质上是“数据面板”
- 有主卡跨列、附属卡并列、summary 混排
- 这是 `HeroStatCard` 做不好的

展示建议：

- 主卡跨列保留
- summary 卡继续模块内维护
- 单卡统一交给 `StatsPanelCard`

### 12. Say Hero Stats

组件：

- `SayPageHero`

推荐：

- 组件：`StatsPanelCard`
- 形式：`compact`
- 布局：4 宫格或响应式 2 列

原因：

- Say 页需要更活跃、更密的摘要节奏
- 它不是“横向概览条”，而是 Hero 内嵌 stats grid
- 但它和首页统计单卡在材料语言上是同源的

展示建议：

- 使用 `compact`
- 保持多色相节奏
- 让右侧发布 / 登录引导面板继续承担主 CTA

---

## 不建议统一到同一个组件的页面

以下内容不应为了“一致性”被强行纳入这两条统计卡谱系：

### 1. 文章详情页 / 内容详情页的局部信息块

原因：

- 它们通常是阅读上下文的一部分
- 不是 Hero 摘要，也不是统计面板

建议：

- 保留详情页自己的内容型块
- 只复用 token，不复用 `HeroStatCard` / `StatsPanelCard`

### 2. 纯导航按钮区 / CTA 区

原因：

- CTA 的目标是引导点击，不是展示统计
- 如果没有“值 + 标签 / 标题 + 补充信息”的稳定结构，不要硬套统计卡

---

## 整站一致性控制建议

## 1. 一致性来源

整站统计块的一致性，应来自以下共性：

- 统一圆角体系
- 统一高光层逻辑
- 统一 hover 节奏
- 统一图标底座逻辑
- 统一数值 / 标签层级
- 统一颜色混合方式

## 2. 个性化来源

页面个性优先来自：

- `tone`
- `hue`
- `density`
- grid 列数
- 主次层级
- 页面专属 aside / summary / current-panel

## 3. 禁止模式

后续新增页面时，禁止：

1. 在页面内再写一整套 `__stat-icon / __stat-value-num / __stat-label`
2. 在 `shared/ui` 里继续绑定 Hero / StatsPanel 成品视觉
3. 通过页面自定义 hover / 边框 / 高光重新覆盖业务组件主样式
4. 把首页 / Say 这类高密度面板强塞回 `HeroStatCard`

---

## 推荐映射表

| 页面 / 组件 | 推荐组件 | 推荐形式 | 个性来源 |
|---|---|---|---|
| User | `HeroStatCard` | `metric + neutral` | 信息中性、个人概览 |
| About | `HeroStatCard` | `link + neutral` | 说明型入口 |
| Navigation | `HeroStatCard` | `metric + neutral` | 导航索引摘要 |
| Archive | `HeroStatCard` | `metric + tinted` | 时间索引、2 卡 |
| Category | `HeroStatCard` | `metric + tinted` | 分类索引、3 卡 |
| Tag | `HeroStatCard` | `metric + tinted` | 标签索引、skeleton |
| Hot | `HeroStatCard` | `metric + tinted` | 运营热度感更强 |
| Links | `HeroStatCard` | `metric + tinted + button` | 申请通道 CTA |
| Notice | `HeroStatCard` | `metric + tinted` | 消息频道上下文 |
| NewPost | `HeroStatCard` | `metric + tinted` | 创作工具上下文 |
| HomeSiteStats | `StatsPanelCard` | `regular + primary` | 主卡跨列、summary |
| SayPageHero | `StatsPanelCard` | `compact` | 紧凑活跃、四宫格 |

---

## 最终判断

如果目标是：

- 让整站统计展示拥有稳定视觉语言
- 同时保留各页面的内容个性
- 并且符合现有功能结构

那么最合理的方案不是“一套万能卡片”，而是：

1. **所有横向 Hero 摘要统一进入 `HeroStatCard` 家族**
2. **所有色相化高密度统计面板统一进入 `StatsPanelCard` 家族**
3. **页面差异主要通过 tone / hue / density / grid 组合表达**
4. **页面层不再重新发明统计卡视觉**

这套方式既能保持一致性，也不会把网站做成同一张卡片换文案。
