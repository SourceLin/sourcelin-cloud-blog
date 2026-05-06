# sourcelin-ui-platform 视觉美化与关键页排版优化设计

> **文档性质**：本文件既是设计意图说明，也是**与当前仓库实现对齐的落地对照稿**（基准日期 2026-04-06）。早期「粉紫氛围 / 每页独立阅读底色」等方案已按代码实际收敛；下文「已落地」均可在 `src/assets/styles/**`、`src/shared/components/**` 与所列页面中核对。

## 0. 落地状态摘要

- **浅色主题**：已收敛为**冷感紫灰**基座——`tokens/base.scss` 中 `--body-gradient`、`--page-glow`、`--page-content-*` 以低饱和紫靛 + 灰蓝线性层叠为主，不再走偏粉的氛围底。
- **玻璃与面板**：全站提高毛玻璃可读参数（如浅色 `--glass-blur` / `--glass-saturate`），`foundation/glass.scss` 与 `foundation/page-shell.scss` 统一面板族（`sourcelin-panel*`、`--surface-panel-*`、`--shadow-panel-*` 等）；`SCard` / `SButton` / `SInput` / `SPagination` 与 `naive/override.scss` 已跟读同一套变量。
- **正文与页面背景语义**：`semantic.scss` 暴露 `--surface-page`（与全局 `--body-gradient` 对齐）、`--surface-page-content`、`--surface-page-ambient`、`--surface-page-transition`、`--surface-hero-mist` 及 `--content-max-width-readable`；`index.scss` 中 `body` 使用 `var(--surface-page)`，正文层级标题与段落绑定 `--reading-line-height` 等阅读 token。
- **树洞**：唯一保留**全屏摄影底图 + 全高 hero** 的品牌特例页；主列表区仍消费全站面板与间距变量，例外不扩散到业务结构或 Naive 直连。
- **校验**：`npm run typecheck`、`npm run build`、`npm run style:guard`、`npm run test:style-guard`、`npm run test:architecture` 均已通过；另有独立契约测试 `node --test scripts/visual-contract.test.mjs`（见 plan 文档说明）。

## 1. 背景与目标

本次工作仅针对 `sourcelin-ui-platform` 博客前台做视觉美化与排版优化，不改动业务逻辑、接口、路由、状态流和页面交互协议。目标是在不破坏现有分层架构的前提下，建立一套可同时覆盖浅色主题与深色主题的统一视觉基座，并优先精修以下关键区域：

- 首页
- 树洞页
- 说说页
- 文章详情页
- 评论区
- 顶部导航与页脚
- 全局基础 UI 抽象组件

最终效果要求：

- 整体风格为现代内容站，主基调为玻璃态与柔和氛围感，而非扁平化或极简风
- 品牌色保持紫罗兰系，所有语义色继续通过 CSS 变量消费
- 统一模块间距、卡片层级、阅读节奏和响应式排版
- 通过 `typecheck`、`build`、`style:guard`、`test:style-guard`、`test:architecture`

## 2. 范围与非范围

### 2.1 范围内

- `src/assets/styles/tokens/**`
- `src/assets/styles/foundation/**`
- `src/assets/styles/index.scss`、`src/assets/styles/responsive-enhancements.scss`（响应式与全局节奏配套）
- `src/assets/styles/naive/override.scss`
- `src/shared/components/ui/**` 中与视觉基座直接相关的组件
- `src/shared/components/layout/**` 中导航与页脚
- 首页、树洞、说说、文章详情、评论区相关页面与业务组件的样式和排版
- 其他使用 `glass-page` 的列表/导航页（如友链 `LinksPage`）随基座自然跟随，不单独开第二条视觉系统

### 2.2 范围外

- 接口定义与请求逻辑
- 路由定义与导航数据来源
- Pinia store 结构
- composables 行为与副作用
- 业务逻辑、权限逻辑、提交逻辑、评论逻辑
- 业务层直接引入 `naive-ui`

## 3. 约束

必须保持以下约束成立：

- 业务页面与业务组件不得直接使用 `n-*` 组件
- 所有新增颜色、边框、阴影、背景、渐变、间距、圆角必须优先通过 CSS 变量表达
- 业务页面与业务组件不得新增 `:deep(.n-*)`
- `glass` 能力仅允许留在 Foundation 或 UI 抽象层
- 视觉优化只能改变呈现，不改变数据、事件、请求和路由行为

## 4. 设计方向（已落地收敛）

本次采用“基座先行，关键页精修”的推进方式；实施过程中相对早期方案的两点**已定稿并在代码中体现**：

1. **浅色主题**：从偏粉氛围改为**冷感紫灰**——见 `base.scss` 中 `--background-color*`、`--body-gradient` 与 `--page-content-ambient` 等，灰蓝与白雾为主、紫靛为点缀。
2. **正文区背景语义**：首页主内容区、文章页、说说页等与 **`--surface-page-content` + ambient/transition token** 对齐；**仅树洞**保留全屏底图与独立承接层（见 7.0 / 7.4）。

核心思路（已实现路径）：

1. 先统一 `tokens -> foundation -> S* UI 抽象组件` 的视觉语言
2. 再精修关键页的布局、层级、间距和阅读体验
3. 让其他页面在不单独重绘的前提下自然跟随新的视觉基座
4. 树洞仅在**底图与主内容承接方式**上例外，面板/间距/评论线程仍跟全站

这样做的原因：

- 最符合“关键页优先，其余页面统一跟随”的目标
- 更容易同时收敛浅色和深色双主题
- 更符合仓库既有的设计系统分层和样式守卫边界

## 5. 视觉基座设计

### 5.1 双主题基调

#### 浅色主题

- 背景收敛为冷白、淡紫、灰蓝的低饱和渐变，不再引入粉色相
- 玻璃表面改为中强毛玻璃，强调通透但不发甜
- 阴影改为“近阴影 + 远辉光”双层结构，提升板块和背景的分离度
- CTA、卡片 hover 和局部强调继续使用紫罗兰弱光晕，但强度控制在产品界面可接受范围内

#### 深色主题

- 背景转向蓝黑夜色系，但保持紫罗兰品牌识别
- 玻璃表面更收敛、更深，减少发灰和发脏
- 强化前景与正文对比度，避免 glow 抢内容
- 所有强调仍基于同一套品牌变量，不建立第二套独立风格

### 5.2 玻璃态层级

建立统一的四类面板层级，所有关键页和基础组件复用：

- `soft`
  - 用于辅助卡片、统计块、空状态、信息摘要
- `default`
  - 用于主卡片、正文容器、模块主面板
- `strong`
  - 用于 hero 叠层、弹层、CTA 容器、重点视觉块
- `inset`
  - 用于评论输入区、回复区、内容包裹区、卡片内部次级容器

统一收口的变量包括：

- `glass-blur`
- `glass-saturate`
- `glass-opacity`
- `glass-stroke`
- `glass-radius`
- `glass-surface*`
- `border-panel-*`
- `shadow-panel-*`
- `highlight-panel-*`
- `page-content-ambient`
- `page-content-transition`
- `page-hero-mist`

### 5.3 阴影与光晕

阴影策略：

- 默认卡片采用“顶部高光 + 中层近阴影 + 远距离辉光 + 微弱底部内阴影”
- hover 态仅做轻微抬升和阴影扩张，不做明显位移
- CTA、导航按钮、主操作按钮使用紫罗兰弱光晕，但限制强度，避免浮夸

光晕策略：

- 光晕只出现在主按钮、重点卡片、hero 装饰性元素
- 正文区、评论区、列表区不使用高强度 glow，保证可读性
- 板块与背景的对比优先通过表面明度差、边框、高光和阴影层次建立，而不是通过提高背景饱和度建立

### 5.4 组件统一语言

基础组件需要收敛为同一视觉语言：

- `SCard`
  - 统一圆角、边框、玻璃底、hover 阴影和标题区样式
- `SButton`
  - 统一 CTA、site、glass、ghost 的层级与反馈
- `SInput`
  - 统一输入框和文本域的玻璃底、focus 态、placeholder 和计数区
- `SPagination`
  - 统一页码按钮、选中态、hover 态与玻璃面板关系

同时在 `naive/override.scss` 中统一：

- 选择器
- 消息提示
- 通知
- 分页
- radio / selection 等基础控件

## 6. 排版系统设计

### 6.1 标题层级

统一标题系统，仅调整样式，不改语义结构：

- `H1`
  - 只用于首页主视觉标题、文章标题
- `H2`
  - 用于页面主模块标题，如精选、发现、读者来信
- `H3`
  - 用于卡片标题、侧栏块标题、讨论面板标题
- 正文与辅助信息
  - 正文行高目标为 `1.75` 左右
  - 说明文字与 meta 信息保持 `1.6` 左右

### 6.2 间距与容器

统一页面节奏：

- 页面外层容器宽度统一收口
- 主栏、侧栏、section、card、内凹容器采用固定的 spacing scale
- 标题区、内容区、尾部操作区形成稳定的垂直节奏

避免的问题：

- 同页中不同模块 padding 波动过大
- 卡片之间 gap 不一致
- 模块标题与内容边线不对齐

### 6.3 对齐原则

统一以下对齐策略：

- 首页主区与侧栏采用明确双栏对齐关系
- 文章详情主内容、目录、作者卡使用稳定双栏秩序
- 评论头像、作者、时间、状态标签、操作按钮保持基线对齐
- 标签、分类、统计 badge 和分页按钮采用一致的视觉基线

### 6.4 响应式原则

移动端策略：

- 保留玻璃态但降低 blur 与层叠复杂度
- 主栏和侧栏在中小屏收敛为单栏
- 模块内边距和标题字号按断点平滑收缩
- 避免因为装饰层叠导致内容区压缩或错位

## 7. 关键页优化策略

### 7.0 页面背景与壳层边界（落地对照）

| 页面 | 页面根 / 全局底 | Hero / 首屏 | 主内容区与过渡 | 与全站一致性 |
|------|-----------------|-------------|----------------|--------------|
| **首页** | `.home-page` 使用 `var(--surface-page)` | 独立 `hero-section`（模糊背景图 + 装饰），与下方内容区分离 | `.page-container-wrap` 使用 `--surface-page-content` 纵向渐变，并以 `::before`/`::after` 挂载 `--surface-page-ambient` 等弱装饰；`home-main` 使用 `--page-section-gap` / `--page-block-gap` | 正文区不另起一套底色语义 |
| **文章详情** | `.article-page` 使用 `var(--surface-page-content)` | `ArticleHero` 独立封面区 | `FeatureCard` / `.feature-shell` 顶部细条使用 `--surface-page-transition`；主栏宽度 `min(var(--content-max-width-readable), 100%)`，与侧栏栅格 gap 为 `--page-section-gap` | 阅读宽与评论区跟同一套节奏 |
| **说说** | `.say-page.glass-page`；stage 区域配合 `--surface-page-content` / `--surface-page-ambient` | `SayPageHero`（`FeatureCard` + `surface="soft"`，装饰orb 已按刷新拿掉） | Feed、弹窗、骨架仍走 `S*` 与面板变量 | 不出现整页强紫/强蓝独立底 |
| **树洞（特例）** | `.treehole-page` 仍设 `background: var(--surface-page-content)`，但 **`::before` fixed 全屏铺 `home-bg-9.jpg`**（站点级资源，唯一此类贯穿） | `.barrage-hero` 固定 `100vh`，弹幕层与中部输入；hero 叠层实现上保持高通透（无统一重蒙层） | `.treehole-main`：**额外** `backdrop-filter` + 多层 `color-mix` 与 `--surface-page-ambient` 承接底图，列表/输入/讨论仍用 `sourcelin-panel*`、`--page-section-gap` | **例外仅限底图与承接模糊**；卡片与评论仍属全站系统 |
| **其他 `glass-page` 页** | 继承 `page-shell.scss` 中 `.glass-page` + `::before` 的 `--page-glow` | 按页面可有可无 | 使用统一 shell / 间距 token | 无第二套品牌皮肤 |

### 7.1 首页

目标：

- 保留氛围感 hero
- 提升进入内容区后的秩序感与整洁度

**已落实要点**：hero 与 `.page-container-wrap` 分离；主内容区用 content/ambient token 收口；侧栏 `sidebar-stack` 与主栏 `home-main` 的纵向节奏由 `--page-section-gap` 等约束。

### 7.2 文章详情页

目标：

- 强化长文阅读体验
- 让正文、作者、目录、评论处于统一体系

**已落实要点**：整页底为 `--surface-page-content`；双栏对齐 `content-max-width-readable`；顶部过渡条使用 `--surface-page-transition`；不再为正文单独铺一层与全站冲突的“阅读灰盒”。

### 7.3 评论区

目标：

- 明确输入、主评论、回复的层级关系
- 提升线程阅读体验

**已落实要点**：`CommentComposer` / `CommentThread` 使用 inset 面板与统一分页/回复结构 class（契约测试覆盖 `reply-item`、`comment-pagination` 等）。

### 7.4 树洞页（品牌特例）

目标：

- 保留品牌化情绪表达氛围
- 提高主内容流的稳定性和模块秩序

**已落实要点与允许偏离**：

- **允许**：全屏固定背景图、全高 hero、`.treehole-main` 更强雾面承接。
- **不允许扩散**：业务结构、接口、`n-*`、面板命名与评论组件体系仍与全站一致；特例不复制到其它频道页。

### 7.5 说说页

目标：

- 强化内容流连续阅读体验
- 让发布态、列表态、预览态属于同一视觉系统

**已落实要点**：页面落在 `glass-page`；hero 弱装饰；feed 与弹窗继续消费 `surface-panel` 与 `--page-block-gap` 等。

### 7.6 顶部导航与页脚

目标：

- 强化站点品牌感与全局一致性

**已落实要点**：`AppHeader` / `AppFooter` 使用 `layout-surface-strong`、`toolbar-button-surface-hover`、`footer-card-surface`、`footer-divider` 等变量（见 `visual-contract.test.mjs`）。

## 8. 主要涉及文件（已落地）

### 8.1 全局基座

- `src/assets/styles/tokens/base.scss`
- `src/assets/styles/tokens/semantic.scss`
- `src/assets/styles/tokens/component.scss`
- `src/assets/styles/foundation/glass.scss`
- `src/assets/styles/foundation/page-shell.scss`
- `src/assets/styles/index.scss`
- `src/assets/styles/responsive-enhancements.scss`
- `src/assets/styles/naive/override.scss`

### 8.2 UI 抽象层

- `src/shared/components/ui/SCard.vue`
- `src/shared/components/ui/SButton.vue`
- `src/shared/components/ui/SInput.vue`
- `src/shared/components/ui/SPagination.vue`

### 8.3 布局层

- `src/shared/components/layout/AppHeader.vue`
- `src/shared/components/layout/AppFooter.vue`

### 8.4 关键页与业务组件

- 首页相关页面与组件（如 `HomePage.vue`）
- 文章详情相关页面与组件（如 `ArticlePage.vue` 与子组件）
- 评论区共享组件（`CommentComposer.vue`、`CommentThread.vue`）
- 树洞页（`TreeholePage.vue`）
- 说说页（`SayPage.vue`、`SayPageHero.vue`）
- 其它 `glass-page` 列表页（如友链 `LinksPage.vue`）

说明：

- 只在必要文件中做排版与视觉调整
- 不为“统一风格”进行无关重构

### 8.5 回归与契约脚本

- `scripts/visual-contract.test.mjs`：token、`S*` 与关键页字符串契约（单独执行，见第 10 节）
- `scripts/home-layout.test.mjs`：首页结构与文案约束（与契约互补）

## 9. 风险与处理方式

### 9.1 风险

- 深色主题 glow 过强导致正文干扰
- 关键页局部已有样式较多，统一后可能暴露原有 spacing 不一致
- 全局组件调整后，非关键页会出现边缘样式变化
- Hero 与正文区之间容易出现“断层”或“正文玻璃层入侵 Hero”这类过渡问题
- 树洞这类背景图贯穿页面，若过度统一，容易损失品牌识别

### 9.2 处理方式

- 先完成 token 与 S 组件收口，再逐页校准
- 以关键页视觉和阅读体验为主，非关键页只做跟随修正
- 控制 hover、glow、blur 强度，优先可读性
- 对页面接缝问题优先通过削薄过渡带、控制顶部留白、避免负向位移侵入 Hero 解决
- 树洞允许保留品牌特例，但例外仅限视觉底图和承接方式，不扩散到业务结构

## 10. 验证方案

至少执行以下命令：

```bash
npm run typecheck
npm run build
npm run style:guard
npm run test:style-guard
npm run test:architecture
node --test scripts/visual-contract.test.mjs
```

通过标准：

- 不出现新的样式守卫违规
- 不出现新的架构边界违规
- 不引入 TypeScript 类型错误
- 不影响构建产物生成
- 视觉契约测试三项全部通过（token / `S*` / 关键页与布局）

**说明**：截至 2026-04-06，上述命令在 `sourcelin-ui-platform` 工作区已跑通（含生产构建）。

## 11. 实施顺序（已完成）

1. 调整 tokens 与 foundation，建立统一双主题视觉基座
2. 校准 Naive override 与 `S*` 基础组件
3. 调整全局布局组件，优先完成导航与页脚
4. 精修首页与文章详情页
5. 精修评论区、树洞页、说说页
6. 做非关键页跟随校准
7. 执行验证并修补边缘样式问题

## 12. 结论

本次改造以“统一视觉基座 + 关键页精修”为主线；**当前仓库状态**对应为：

- 全站统一的**冷感紫灰**浅色基座与配套的深色玻璃参数
- **全站玻璃与面板层级增强**，由 tokens + `glass.scss` + `page-shell.scss` + `S*` 统一出口
- **首页、文章详情、说说**等与 `--surface-page-content` / ambient / transition 语义对齐；**树洞**在底图与承接模糊上例外，其余仍跟面板与间距系统
- 设计系统分层边界保持不变：业务层只消费 `var(--*)` 与 `S*`，不直连 Naive、不下沉 `liquid-glass-effect`
