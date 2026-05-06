# Home 首页重设计方案（V7 - 全样式体系对齐）

## 0. 目标

V7 的任务不是再换一种更花的首页风格，而是把首页的视觉语言和项目现有的样式体系完全对齐，避免设计稿和代码各说各话。

首页下方必须同时满足三件事：

- 结构上不回到传统博客左右双栏
- 视觉上不回到杂志封面式编排
- 实现上严格使用项目已有的 tokens、glass、page-shell、utilities 和 override 体系

---

## 1. 样式体系分析

这份方案不是从抽象“审美”出发，而是直接建立在本项目已有的样式资产之上。

### 1.1 Tokens 层

#### `src/assets/styles/tokens/base.scss`

这是全站视觉的底座，负责：

- 主题色和色相关系
- `glass-*` 的透明度、模糊、边界、阴影
- 字体、字号、间距、圆角、层级变量
- 主题切换时的基准色彩变化

首页所有大块视觉都必须从这里出发，不能硬编码新颜色体系。

#### `src/assets/styles/tokens/semantic.scss`

这是语义层映射，负责：

- `--bg-page`
- `--body-bg`
- `--card-bg`
- `--text-primary`
- `--text-secondary`
- `--title-color`

首页设计文档里的所有“背景 / 内容 / 说明 / 标题”都应该落到这些语义变量，而不是继续发明新层级。

#### `src/assets/styles/tokens/component.scss`

这是组件级的光感和表面延展层，负责：

- `--surface-glass-card`
- `--surface-glass-card-muted`
- `--surface-glass-highlight-soft`
- `--surface-black-*`
- `--surface-white-*`

首页中 `Hero skirt`、精选区、浮层卡片、轻提示条都应该优先说明这一层，而不是继续堆独立渐变。

### 1.2 Foundation 层

#### `src/assets/styles/foundation/glass.scss`

这是首页玻璃语言的直接来源，决定：

- `sourcelin-glass`
- `sourcelin-glass-lite`
- `sourcelin-glass-strong`
- `sourcelin-card`
- `sourcelin-modal`
- `sourcelin-no-liquid`
- `sourcelin-force-liquid`
- `sourcelin-refraction`

V7 必须明确：不同区域使用不同玻璃层级，而不是所有模块都做成同一种“半透明大卡”。

#### `src/assets/styles/foundation/theme.scss`

负责主题切换过渡，尤其是：

- `--theme-transition`
- `--theme-transition-fast`
- `--theme-transition-slow`

首页动效必须以这个节奏为主，避免自己再定义第二套过渡逻辑。

#### `src/assets/styles/foundation/page-shell.scss`

这是首页外层节奏的说明系，不一定要完全照搬，但必须遵守它的结构语义：

- 页面级留白
- 页面标题层
- 页面统计层
- 页面容器宽度与呼吸感

V7 允许首页不使用典型 `page-header`，但不能忽略它定义出来的页面节奏。

#### `src/assets/styles/foundation/animations.scss`

首页只允许使用轻量的进入、浮动、淡入和高亮动画。

不允许用大幅翻转、夸张缩放、专题页式复杂入场来制造记忆点。

#### `src/assets/styles/foundation/form-controls.scss`

如果首页后续出现筛选、搜索或快速切换控件，必须遵循这套输入表单规则，而不是重新做一套表单玻璃层。

### 1.3 Utilities 层

#### `src/assets/styles/index.scss`

这里提供的是首页可以直接复用的全局工具类：

- `.card`
- `.sourcelin-shadow`
- `.sourcelin-tag`
- `.sourcelin-gradient-text`
- `.sourcelin-hover-lift`
- 通用间距、圆角、文本、布局工具类

V7 明确要求：首页不应该再为了某个小状态块重复造样式，而应优先消化这里已有的通用视觉原语。

### 1.4 Override 层

#### `src/assets/styles/naive/override.scss`

首页如果出现任何 Naive UI 控件，只能依赖这层覆盖语义来统一视觉。

重点不是在首页里多放控件，而是：

- 输入类组件保持统一玻璃壳
- 下拉、分页、选择器保持统一边界
- 液态效果允许降级

---

## 2. 总体气质

V7 的首页气质定义为：

`Digital Microglow Dashboard`

它的核心不是“更像设计稿”，而是“更像一个经过样式体系约束的高精度控制台”。

### 视觉原则

- 冷静
- 通透
- 平衡
- 克制
- 连续浏览

### 必须避免

- 杂志导语感
- 专题封面感
- 左右双栏博客暗示
- 大段解释布局的文字
- 过强的按钮化 Prelude

---

## 3. Prelude 系统

Prelude 必须从“板块头部”退化成“元数据标签”。

### 3.1 定位

它不再是一个可见的大块结构，而是内容容器边缘上的轻提示。

### 3.2 形态

Prelude 应该更接近：

`chip / tag / status label`

而不是：

`button / header / hero intro`

### 3.3 视觉约束

- 高度 24px 到 28px
- 单行
- 无衬线
- 最长只保留 1 个分区名 + 1 个轻量说明
- 总宽度建议控制在 240px 以内
- 不使用大号编号
- 不使用大段说明
- 不使用右侧 facts 卡
- 不使用按钮式填充块
- 不使用明显悬浮阴影
- 不使用独立点击按钮语义

### 3.4 结构约束

Prelude 只能作为内容块前缀存在，不能独立成块。

#### 推荐结构

`[细小图标] 分区名 · 轻量说明`

#### 推荐位置

- 紧贴模块内容左上角
- 与首个内容块保持 8px 到 12px 的视觉间距
- 可轻微压在内容容器边缘上，但压叠幅度不超过 8px

#### 禁止形态

- 不允许上下两层标题
- 不允许左右分栏标题
- 不允许右侧补充说明卡
- 不允许做成按钮、胶囊按钮或操作入口
- 不允许成为页面中最亮的元素

### 3.5 推荐样式来源

Prelude 的视觉应更接近：

- `.sourcelin-tag`
- `.sourcelin-glass-lite`
- `--surface-white-10`
- `--glass-border`

而不是 `.sourcelin-button`。

### 3.6 文案规则

只保留一行结构：

`[小图标] 分区名 · 轻量说明`

例如：

- `概览 · 平台状态与登录入口`
- `精选 · 全站优先内容`
- `发现 · 分类与关键词入口`
- `更新 · 持续阅读流`

---

## 4. 页面结构

### 顺序

1. `Hero`
2. `Hero skirt`
3. `Overview`
4. `Picks`
5. `Discover`
6. `Feed`

### 4.1 `Hero skirt`

`HomeMarquee` 不再是 section 的一部分，而是 Hero 的信息缝合线。

#### 样式建议

- 使用极窄玻璃带
- 保留 `backdrop-filter`
- 去掉重阴影
- 采用更冷静的 `sourcelin-glass-lite`

#### 数值建议

- 高度 28px 到 36px
- padding 仅保留横向
- 不要形成完整内容卡

### 4.2 `Overview`

这一段负责平台状态和当前登录人入口。

#### 布局规则

- `HomeSiteStats` 为主
- `HomeSiteCard` 为辅
- 不做左右对等排版
- 不做明显边栏结构

#### 数值规则

- 主看板宽度占比 65% 到 70%
- 个人入口最大宽度 320px
- 错位叠压建议 16px 到 24px
- 平板端在 1080px 以下改为上下堆叠

#### 视觉语义

- `HomeSiteStats` 使用 `.sourcelin-glass`
- `HomeSiteCard` 使用 `.sourcelin-modal`

### 4.3 `Picks`

这一段是首页最重要的精选层。

#### 布局规则

- `HomeCarouselSection` 作为液态光幕底层
- `HomeRecommendedList` 作为前景内容流
- 推荐列表不是“左列表”，轮播也不是“右展示”
- 它们共同构成一个精选场域

#### 数值规则

- 轮播区作为底层，横向占满容器
- 推荐列表前层与轮播底层产生 12px 到 20px 的视觉叠压
- 桌面端最多 3 条推荐在首屏可见
- 平板端收束为先轮播后列表
- 移动端收束为先轮播后单列推荐

#### 视觉语义

- `HomeCarouselSection` 使用 `.sourcelin-force-liquid`
- `HomeRecommendedList` 使用 `.sourcelin-glass-lite`

### 4.4 `Discover + Feed`

这两个区域必须作为一条连续阅读带来处理。

#### 布局规则

- 不允许重新拆成完整左右双栏
- 不允许 Discover 变成左侧侧栏
- 分类和标签必须贴近文章流顶部，作为发现入口层
- 文章流是主阅读层，发现入口是辅助路径层

#### 桌面端结构

- 整体为单列连续流，不允许拆成左右双栏
- 发现入口必须占据文章流上方的全宽控制带，不能落到左侧
- 分类与标签可以并排，但必须保持同一视觉行内，不形成第二列
- 文章流紧接其下，作为主阅读层单线向下延展
- 如果需要视觉分隔，只能用极细分割线或 12px 以下的垂直间距

#### 移动端结构

- 发现入口收束为文章流顶部的单列控制区
- 分类和标签纵向堆叠，最多保留 2 行
- 文章流直接接续，不形成二级侧栏
- 不允许横向切成“上控制、下内容”的双区卡片

#### 平板端结构

- 发现入口保留在文章流顶部，但允许横向滚动的 chip rail
- 分类与标签保持轻量并排，必要时换行一次
- 文章流仍然保持单主线阅读，不出现固定侧栏
- 在 1080px 以下，所有发现入口必须收束为上下堆叠

#### 视觉语义

- `HomeCategorySection` 使用 `.sourcelin-glass`
- `HomeLabelCloud` 使用 `.sourcelin-glass-lite`
- `HomeArticlesSection` 使用 `.sourcelin-card`

---

## 5. 视觉系统约束

### 5.1 玻璃层级

V7 必须按以下优先级使用玻璃层：

1. `sourcelin-modal`
2. `sourcelin-glass`
3. `sourcelin-glass-strong`
4. `sourcelin-glass-lite`
5. `sourcelin-card`

用途区分：

- `modal`：最深、最重、最明确的入口
- `glass`：主容器和状态看板
- `glass-strong`：需要稍强层级的内容块
- `glass-lite`：轻提示、精选前层、公告边缘
- `card`：稳定阅读基石

### 5.2 边界定义

- 主要靠 `outline: 1px solid var(--glass-border)` 建边界
- 少用重阴影区分层级
- 边界应清楚，但不应厚重

### 5.3 噪点与通透

- 允许 `var(--glass-noise)`
- 允许 `background-blend-mode: soft-light`
- 但噪点不能盖过内容

### 5.4 主题切换

所有主题切换必须遵循 `--theme-transition`。

Dark / Light 两种主题都要维持同一套层级逻辑，不能因为配色变化导致结构感消失。

---

## 6. 动效与性能

### 6.1 动效原则

- 只做轻微上浮
- 只做轻微聚光
- 只做必要 refraction
- 不做封面式大动效

### 6.2 局部液态

- `Picks` 允许更强液态感
- `Discover + Feed` 滚动时应降级为 `.sourcelin-no-liquid`
- 让长列表优先保流畅，不优先炫技

### 6.3 交互节奏

- hover 是微交互
- 轮播是平稳焦点
- 标签云是轻动态
- 页面进入只做淡入和轻上浮

---

## 7. 响应式策略

### 桌面端

- 保留 Overview 和 Picks 的层叠感
- 发现入口与文章流保持连续
- 不允许完整侧栏复活

### 平板端

- Picks 收束为上轮播下推荐
- Overview 收束为上下堆叠
- Discover 和 Feed 保持顺序流

### 移动端

- 全部回到单列
- Prelude 继续保持轻提示
- 内容按浏览顺序展开

---

## 8. 模块映射

| 模块单元 | 建议样式语义 | 视觉重心 |
| --- | --- | --- |
| `Prelude` | `.sourcelin-tag` 变体 | 极轻元数据提示 |
| `HomeMarquee` | `.sourcelin-glass-lite` | Hero 裙边公告 |
| `HomeSiteStats` | `.sourcelin-glass` | 平台状态看板 |
| `HomeSiteCard` | `.sourcelin-modal` | 当前登录人入口 |
| `HomeRecommendedList` | `.sourcelin-glass-lite` | 悬浮精选流 |
| `HomeCarouselSection` | `.sourcelin-force-liquid` | 液态精选光幕 |
| `HomeCategorySection` | `.sourcelin-glass` | 发现入口 |
| `HomeLabelCloud` | `.sourcelin-glass-lite` | 关键词入口 |
| `HomeArticlesSection` | `.sourcelin-card` | 稳定阅读流 |

---

## 9. 实现验收

这部分用于防止首页在实现中滑回传统博客结构。

### 9.1 结构验收

- 首页下方不得出现“左侧信息栏 + 右侧内容列”的完整双栏骨架
- `Prelude` 不得单独占据一整行卡片高度
- `Discover` 不得独立成为完整侧栏模块
- `Feed` 不得被压缩成右侧窄列
- `Picks` 不得被拆成左右两个等宽内容块

### 9.2 视觉验收

- 页面所有边界优先由细描边和玻璃层级建立
- 玻璃感只在重点区域增强，长列表默认收敛
- 标题层级清晰，但不会比内容本身更重
- 发现入口、精选入口、状态看板三者之间有主次，但没有装帧感

### 9.3 响应式验收

- 1280px 以上可以保留 Overview 和 Picks 的层叠感
- 1080px 以下 Overview 必须改为上下堆叠
- 1080px 以下 Discover 与 Feed 只能保持顺序流，不得左右并列
- 移动端必须是单列展开，所有辅助入口收敛为顶部控制区

### 9.4 组件验收

- `Prelude` 只允许使用 `Prelude` 相关 tag/chip 语义，不允许 button 语义
- `HomeMarquee` 必须保持 Hero 裙边定位，不得回到独立区块
- `HomeCategorySection` 与 `HomeLabelCloud` 只能作为发现入口，不得形成侧栏
- `HomeArticlesSection` 必须保持主阅读流角色

---

## 10. 风险控制

### 风险 1：Prelude 仍然过强

如果 Prelude 继续使用 button-like 视觉，它就会再次从“元数据标签”退化回“板块头部控制件”。

### 风险 2：Discover 重新变成侧栏

如果分类和标签在桌面端被做成完整左栏，首页会再次滑向传统博客结构。

### 风险 3：玻璃感过头

如果所有模块都维持强液态与重 refraction，页面会重新变得油、杂、像专题封面。

---

## 11. 结论

V7 的核心不是“更漂亮”，而是“更贴合项目真实样式体系”。

首页最终应该像：

`Hero skirt -> 平台状态看板 -> 液态精选光幕 -> 轻量发现入口 -> 稳定阅读流`

而不是：

`左侧信息栏 -> 右侧内容列`

这份方案把设计语义、主题变量、玻璃层级、页面节奏和现有全局样式统一到同一套语言里，避免文档和实现继续分离。
