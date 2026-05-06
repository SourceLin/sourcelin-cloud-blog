# 博客前台液态玻璃样式统一方案

本文基于 `sourcelin-ui-platform` 当前代码盘点，说明各板块液态玻璃样式的现状、差异原因、推荐方向和迁移优先级。目标是让页面看起来像同一套高级博客产品，而不是多个玻璃方案并存。

## 1. 当前结论

当前项目已经有比较完整的玻璃 token 和 UI 抽象层，但业务板块存在三种并行写法：

| 类型 | 主要来源 | 当前观感 | 适合场景 | 当前问题 |
|------|----------|----------|----------|----------|
| 清透面板玻璃 | `--surface-panel-soft/default/strong`、`SCard`、`SSurfacePanel` | 清、亮、少雾感，更像整片玻璃 | Hero、统计、文章详情、侧栏、主容器 | 是当前最成熟的系统语言，但部分业务卡片没有统一使用 |
| 雾面磨砂玻璃 | `--glass-frost-*` | 白雾更重，颗粒/散射明显 | 侧边栏、页脚、大面积弱背景容器 | 被业务卡片大量手写复用，导致文章卡、推荐卡通透性下降 |
| 手写混合玻璃 | 业务 SCSS 中直接组合 token | 局部好看但语义不稳定 | 历史补丁、临时恢复效果 | 同一板块出现 `surface-panel-*` 与 `glass-frost-*` 混搭 |

推荐统一方向：**内容卡片默认使用清透面板玻璃，雾面玻璃只作为背景层、侧栏层或弱信息容器，不再作为文章/推荐/分类/标签卡片的默认材质。**

## 2. 现状分析

### 2.1 基础 token

核心透明底色在 `src/assets/styles/tokens/base.scss`：

- `--glass-surface`: 标准清透玻璃底。
- `--glass-surface-lite`: 更轻、更透的辅助底。
- `--glass-surface-strong`: 更强、更成片的强调底。
- `--glass-blur` / `--glass-saturate`: 全局玻璃模糊与饱和度。

组件语义层在 `src/assets/styles/tokens/component.scss`：

- `--surface-panel-soft/default/strong`: 当前最像系统主线的清透面板。
- `--glass-ice-*`: 厚重冰晶，适合阅读容器。
- `--glass-drop-*`: 标准液态水滴，适合悬浮控件或签名小卡。
- `--glass-frost-*`: 雾面磨砂，适合侧栏、页脚、大面积弱背景。
- `--glass-gem-*`: 宝石玻璃，适合极少量品牌时刻。

### 2.2 UI 抽象层

`SCard` 是当前最完整的卡片抽象：

- `variant="lite"` 使用 `--surface-panel-soft`，观感清透柔和。
- `variant="default"` 使用 `--surface-panel-default`，适合正文容器。
- `variant="strong"` 使用 `--surface-panel-strong`，适合强调卡或选中态。
- `surface="ice/drop/frost/gem"` 已提供光学变体入口。

`SSurfacePanel` 直接映射 foundation panel：

- `default` → `sourcelin-panel`
- `soft` → `sourcelin-panel-soft`
- `strong` → `sourcelin-panel-strong`
- `inset` → `sourcelin-panel-inset`
- `modal-field` → 实心表单内嵌区

`SSurfaceChip` 已统一 chip 尺寸和视觉，业务层只应该传 `size / variant / tone`，不再手写 chip 背景、边框和尺寸。

### 2.3 主要板块差异

| 板块 | 当前实现 | 观感判断 | 建议 |
|------|----------|----------|------|
| 首页站点卡、文章详情 lead strip、文章详情侧栏 | `SCard variant="lite"` / `SSurfacePanel` | 清透、统一、成熟 | 保持为基准样式 |
| 首页推荐首条 `recommended-item.is-featured` | `--surface-panel-strong` + `glass-frost` refraction/grain | 比普通项更像清玻璃，但有混搭 | 保留 `surface-panel-strong` 方向，移除或弱化 `glass-frost-grain` |
| 首页普通推荐 `recommended-item` | 手写 `--glass-frost-*` | 雾感偏重，和精选项割裂 | 改为 `surface-panel-soft/default` |
| 首页瀑布流 `article-card` / `featured-article` | 手写 `--glass-frost-*` | 雾面重，图片卡不够清爽 | 改为清透内容卡语义 |
| 分类/标签卡 `category-card` / `tag-grid-card` | 手写 `--glass-frost-*` | 磨砂感强，和 hero/SCard 不一致 | 改为清透面板，选中态再用 strong/accent |
| 归档文章卡 | 手写 `--glass-frost-*` | 与详情页阅读卡不一致 | 改为清透内容卡，时间线容器可保留 soft |
| About 联系/兴趣/信息卡 | `SSurfacePanel` 外又手写 `glass-frost` 覆盖 | 分层语义冲突 | 回收为 `SSurfacePanel variant="inset/soft"` |
| 页脚 | 使用 `glass-frost` | 大面积背景层适配 | 可以保留 |
| Header / Drawer / Search / Modal | 使用专门 UI 抽象和局部玻璃 | 功能性强，需可读性 | 保持，逐步减少硬编码 blur |

## 3. 统一方案

### 3.1 材质分级

| 等级 | 名称 | 推荐 token / 组件 | 使用场景 | 禁止场景 |
|------|------|-------------------|----------|----------|
| L0 | 页面环境光 | `--body-gradient`、`--page-glow` | 页面背景、过渡光 | 具体卡片 |
| L0.5 | 遮罩玻璃 | `--modal-overlay-scrim`、`--modal-overlay-backdrop-filter` | 搜索弹窗、模态、抽屉遮罩 | 普通内容卡 |
| L1 | 清透容器 | `SSurfacePanel variant="soft"`、`SCard variant="lite"` | Hero 内部面板、侧栏、统计组、页面分区 | 强交互卡片的 hover 态 |
| L2 | 标准内容卡 | `SCard variant="lite"` 或局部抽象后的 `surface-panel-soft/default` | 文章卡、推荐卡、分类卡、标签卡、导航卡 | 手写 `glass-frost-*` |
| L3 | 强调内容卡 | `SCard variant="strong"` 或 `surface-panel-strong` | 精选项、当前选中、主推内容 | 普通列表项 |
| L4 | 特殊光学玻璃 | `surface="ice/drop/frost/gem"` | 阅读容器、水滴控件、品牌时刻、页脚/侧栏雾面 | 大量重复内容卡 |

### 3.2 推荐默认值

内容卡片统一采用：

```scss
border: 1px solid var(--border-panel-default);
background:
  var(--surface-panel-specular-soft, linear-gradient(135deg, color-mix(in srgb, var(--surface-white-20) 22%, transparent), transparent 56%)),
  var(--surface-panel-soft);
box-shadow:
  var(--highlight-panel-soft),
  var(--shadow-panel-soft);
-webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
```

强调/精选卡片统一采用：

```scss
border-color: var(--border-panel-strong);
background: var(--surface-panel-strong);
box-shadow:
  var(--highlight-panel-strong),
  var(--shadow-panel-default),
  var(--shadow-panel-glow);
```

不再把 `--glass-frost-grain` 作为内容卡片默认纹理。需要“磨砂”时，应显式使用 `SCard surface="frost"` 或 `SSurfacePanel variant="soft"` 所在背景层，而不是在业务卡片里手写整套 `glass-frost`。

### 3.3 亮暗主题策略

液态玻璃不能用同一套透明度策略直接套亮/暗主题。亮色主题依赖清晰边缘、轻外阴影和白色高光；暗色主题依赖更实的底色、边缘自发光和更克制的外阴影。

| 主题 | 表面策略 | 边缘策略 | 阴影策略 | 风险 |
|------|----------|----------|----------|------|
| Light | 白色玻璃可更透，保留冷调 tint | 顶部白色高光为主 | 轻外阴影 + 柔和 glow | 过白会像磨砂盒 |
| Dark | 深色玻璃更实，带少量品牌 tint | 顶部/底部双向内发光 | 外阴影更深但面积更克制 | 过透会脏，过暗会融入背景 |

建议补充 token：

```scss
:root {
  --text-shadow-glass: 0 1px 2px color-mix(in srgb, var(--text-color-light) 48%, transparent);
  --content-readable-scrim:
    linear-gradient(180deg, transparent 0%, color-mix(in srgb, var(--surface-panel-soft) 76%, transparent) 72%);
  --surface-panel-specular-soft:
    linear-gradient(135deg, color-mix(in srgb, var(--surface-white-20) 22%, transparent), transparent 56%);
}

html[data-theme='dark'] {
  --highlight-panel-soft:
    inset 0 1px 1px color-mix(in srgb, var(--surface-white-18) 62%, transparent),
    inset 0 -1px 1px color-mix(in srgb, var(--surface-white-08) 70%, transparent);
  --shadow-panel-soft: 0 8px 24px color-mix(in srgb, var(--background-color-deep) 42%, transparent);
  --text-shadow-glass: 0 1px 4px color-mix(in srgb, var(--background-color-deep) 82%, transparent);
  --content-readable-scrim:
    linear-gradient(180deg, transparent 0%, color-mix(in srgb, var(--background-color-deep) 62%, transparent) 72%);
}
```

说明：

- 暗色模式下不要只降低透明度；要增加 edge lighting，让玻璃边缘和背景分离。
- `--glass-surface-strong` 在暗色下应更实，但必须带少量主色/伴随色 tint，避免纯灰脏感。
- 外阴影在暗色背景上会被吞掉，内高光比外阴影更重要。

### 3.4 文本可读性策略

带图卡片、Hero、推荐首卡和瀑布流内容卡必须有文本保护层。不能只依赖 `backdrop-filter`，因为底图明暗不可控。

推荐规则：

- 标题、摘要、meta 使用 `text-shadow: var(--text-shadow-glass)`。
- 图片上方或文字区域底部使用 `--content-readable-scrim`。
- 大图卡片的文字区如果直接覆盖图片，必须有独立 scrim；如果文字在玻璃容器内，玻璃底色不得低于 L2。
- 正文阅读容器优先可读性，可以使用 `surface="ice"` 或更实的 default/strong 面板。

推荐卡片文字托底：

```scss
.content-card__copy {
  position: relative;
  z-index: 1;
  text-shadow: var(--text-shadow-glass);
}

.content-card__media::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: 42%;
  background: var(--content-readable-scrim);
  pointer-events: none;
}
```

### 3.5 液态质感策略

当前 `blur + saturate + border + shadow` 更接近传统 Glassmorphism。要更像 Liquid Glass，需要把“折射、光泽、流动感”作为系统规则，而不是每个业务组件临时写。

推荐规则：

- 清透面板默认加入极轻的斜向 specular highlight，不使用明显颗粒。
- hover 态不只改变底色；应增强边缘高光、局部 glow 或饱和度。
- `surface="drop"` 用于少量悬浮控件、水滴按钮、品牌提示，不用于列表批量卡。
- `surface="gem"` 只用于品牌签名时刻，不进入文章列表、分类列表、标签列表。

推荐 hover 语义：

```scss
.content-card:hover {
  border-color: var(--border-interactive-hover);
  background:
    var(--surface-panel-specular-strong, linear-gradient(135deg, color-mix(in srgb, var(--surface-white-25) 28%, transparent), transparent 52%)),
    var(--surface-panel-strong);
  box-shadow:
    var(--highlight-panel-strong),
    var(--shadow-panel-hover),
    var(--shadow-panel-glow);
}
```

### 3.6 Overlay / Modal 玻璃策略

搜索弹窗、模态和抽屉不应该直接套普通 L1-L4。它们跨越遮罩、弹窗壳、内部容器和结果列表，需要单独治理。

当前全局搜索弹窗使用 `.sourcelin-modal-mask.search-modal-root` 自建遮罩，同时内部大量使用 `SSurfacePanel variant="soft/inset"`。这会形成“遮罩 blur + 弹窗 blur + 内部 panel blur”的嵌套结构，视觉上丰富，但性能和暗色可读性风险更高。

统一规则：

| 层级 | 搜索弹窗映射 | 亮色策略 | 暗色策略 |
|------|--------------|----------|----------|
| 遮罩 | L0.5 Overlay | 轻 scrim + 低饱和径向光 + 中等 blur | 更实 scrim + 冷色边缘光 + 稍高 blur |
| 弹窗壳 | L1 default/strong | 清透但边缘清晰 | 更实底色 + 内发光边缘 |
| 左侧说明区 | L1 soft | 柔和辅助层 | 降低 blur，保留边框 |
| 输入/指标/结果项 | L2 inset | nested blur 或无 blur | 优先无强 blur，靠边框和底色分层 |

落地建议：

- `SearchDialog` 的遮罩改为复用 `--modal-overlay-scrim` 和 `--modal-overlay-backdrop-filter`，不要继续单独依赖 `--overlay-light`。
- 弹窗内部 inset 层最多使用 `--glass-nested-blur`，不再使用完整 `--glass-blur`。
- 暗色模式下先加遮罩实度和边缘高光，不靠继续加大内部 blur。

### 3.7 板块映射

| 页面/板块 | 推荐材质 | 差异策略 |
|-----------|----------|----------|
| 首页 Hero / 站点信息 | `SCard variant="lite"` + `SSurfacePanel inset` | 保持清透主视觉 |
| 首页推荐列表 | 普通项 L2，首项 L3 | 首项靠尺寸、图片占比、strong 面板突出，不靠雾面 |
| 首页发现瀑布流 | L2 内容卡，主推可 L3 | 保持图片卡干净，减少白雾 |
| 分类/标签总览卡 | L2，active/armed 为 L3 + accent border | 卡片之间只允许色相和层级差异，不改变材质体系 |
| 归档列表 | L2 内容卡 | 时间线 marker/chip 使用 `SSurfaceChip` |
| 文章详情正文 | `SCard variant="default"` 或 `surface="ice"` | 阅读容器优先可读性，允许更厚 |
| 文章详情侧栏 | `SCard variant="lite" hoverable` | 和 `lead-strip-item` 保持一致 |
| About 信息/联系卡 | `SSurfacePanel inset/soft` | 删除业务层 `glass-frost` 覆盖 |
| 页脚 / 大面积弱背景 | `glass-frost` | 允许保留雾面，避免抢内容卡层级 |
| 弹层 / 搜索 / 抽屉 | L0.5 + 专用 UI 抽象 | 遮罩、弹窗壳、内部 inset 分层治理 |

## 4. 差异与推荐

### 4.1 `surface-panel-strong` 不是“不通透”

`--surface-panel-strong` 的 `strong` 表示强调层级，不等于更雾。它基于 `--glass-surface-strong`，透明度更高的白底让它更成片；但因为没有 `glass-frost-grain` 那种散射颗粒，视觉上更像清玻璃。

因此，`recommended-item.is-featured` 目前比普通 `recommended-item` 更像玻璃，是合理感受。真正的问题是：它混用了 `surface-panel-strong` 和 `glass-frost` 的折射/颗粒层，语义不够纯。

### 4.2 `glass-frost` 应降级为场景材质

雾面玻璃适合：

- 页脚、侧栏背景、低层容器。
- 内容密度高但不需要突出单张卡的区域。
- 暗色主题中压低背景干扰。

不适合：

- 首页文章卡、推荐卡、分类/标签卡这类主要内容入口。
- 带大图的卡片，因为白雾会降低图片和玻璃边缘的高级感。

### 4.3 推荐采用“清透内容卡 + 强调态”模型

统一后的视觉关系：

- 普通内容卡：清透、轻阴影、少纹理。
- 精选/选中卡：同一材质更强，而不是换成另一种玻璃。
- 背景容器：允许更雾、更软，托住前景。
- 特殊展示：使用 `drop / ice / gem`，但必须少量且有明确语义。

## 5. 迁移优先级

### P0：统一首页核心内容卡

优先处理：

- `HomeRecommendedList.vue`
- `HomeArticlesSection.vue`

目标：

- 普通推荐、精选推荐、文章瀑布流都回到 `surface-panel-*` 清透体系。
- 精选项只提升到 `surface-panel-strong`，不叠加 `glass-frost-grain`。
- hover 使用统一的 `border-interactive-hover`、`shadow-panel-hover`。

### P1：统一分类、标签、归档内容入口

优先处理：

- `CategoryPage.vue` 的 `category-card`
- `TagPage.vue` 的 `tag-grid-card`
- `ArchivePage.vue` 的 `article-card`

目标：

- 默认 L2。
- 当前选中/armed L3。
- chip 继续使用 `SSurfaceChip`。

### P2：清理 About 和历史手写玻璃

优先处理：

- `AboutPage.vue` 中 `about-interest-group`、`info-item`、`contact-card` 的手写 `glass-frost` 覆盖。
- 业务组件中所有直接组合 `border/background/box-shadow/backdrop-filter` 的重复片段。

目标：

- 能用 `SCard` / `SSurfacePanel` 的不再手写。
- 需要业务成品组件时，封装到 `shared/components/business`，不要在页面散落材质规则。

### P3：补齐主题、可读性和降级 token

优先处理：

- `tokens/component.scss` 中补充 `--text-shadow-glass`、`--content-readable-scrim`、`--surface-panel-specular-*`。
- 暗色主题覆盖 `--highlight-panel-*`、`--shadow-panel-*`、可读性 scrim。
- `semantic.scss` / `naive/override.scss` 中统一 Overlay token 的使用口径。

目标：

- 亮暗主题下玻璃边缘都清晰。
- 图片卡标题和摘要在复杂背景上保持可读。
- 弹层遮罩不再分散使用 `overlay-light`、硬编码 blur 和单独 radial 背景。

### P4：性能与无障碍降级

优先处理：

- 新增 `prefers-reduced-transparency: reduce` 口径。
- 为瀑布流、大量列表卡、搜索结果列表定义轻量玻璃规则。
- 检查嵌套 blur：遮罩、弹窗壳、内部面板最多两层强 blur。

目标：

- 低透明度偏好用户得到高不透明实体面板。
- 大量卡片滚动时不因 `backdrop-filter` 堆叠造成明显卡顿。
- 暗色模式和复杂背景下仍保持可读性。

## 6. 落地规则

1. 业务页面不得新增完整玻璃配方：禁止同时手写 `border + background + box-shadow + backdrop-filter`。
2. 内容卡优先使用 `SCard`；轻量语义容器使用 `SSurfacePanel`。
3. 新增文章/推荐/分类/标签卡默认使用 L2，精选/选中使用 L3。
4. `glass-frost` 只用于背景层或显式 `surface="frost"`，不作为内容卡默认。
5. `glass-gem` 仅用于品牌高光或节日/活动签名时刻，不进入列表卡。
6. 所有 chip 使用 `SSurfaceChip`，业务层只控制布局，不控制 chip 材质。
7. 所有颜色、边框、阴影、背景必须使用 CSS 变量，不新增硬编码色值。
8. 若需要新的材质差异，先补 token 或 UI 抽象层，再下沉到业务组件。
9. 带图内容卡必须提供文字保护：`--content-readable-scrim`、`--text-shadow-glass` 或更实的内容面板至少使用一种。
10. 禁止强玻璃无限嵌套：同一视觉树中最多允许两层完整 `backdrop-filter`，内部 inset 使用 `--glass-nested-blur` 或无 blur。
11. 弹层、搜索、抽屉统一走 L0.5 Overlay 规则，遮罩优先使用 `--modal-overlay-*`。
12. 新增玻璃 token 必须同时定义亮色和暗色策略，不能只在 `:root` 定义。

### 6.1 无障碍与降级规则

必须支持低透明度偏好：

```scss
@media (prefers-reduced-transparency: reduce) {
  :root {
    --glass-blur: 0px;
    --surface-panel-soft: var(--solid-panel-soft, color-mix(in srgb, var(--background-color) 96%, var(--text-color-light)));
    --surface-panel-default: var(--solid-panel-default, color-mix(in srgb, var(--background-color) 98%, var(--text-color-light)));
    --surface-panel-strong: var(--solid-panel-strong, var(--background-color));
    --modal-overlay-backdrop-filter: none;
  }
}
```

说明：

- 如果浏览器不支持 `prefers-reduced-transparency`，仍需保留现有 `@supports not (backdrop-filter: blur(1px))` 降级。
- `prefers-reduced-motion` 不等于低透明度；动效和透明度需要分别治理。
- 低透明度模式下优先保证对比度，不追求玻璃质感。

### 6.2 性能规则

- 瀑布流和无限列表卡片不使用 L4，也不叠加 `glass-frost-grain`。
- 大量重复卡片的 hover 不改变 `backdrop-filter`，只改变边框、阴影、轻微高光。
- `will-change` 只给可交互且数量有限的元素使用，不能批量加到所有列表卡。
- 搜索弹窗、移动抽屉、Header 这类固定层允许强 blur，但内部子项必须降级为 nested blur。

## 7. 建议验收口径

视觉验收：

- 首页同屏内推荐卡、文章卡、分类/标签入口应像同一套玻璃系统。
- 首条推荐可以更醒目，但不应像另一种材质。
- 大图卡片边缘应清晰，不能被白雾压平。
- 侧栏/页脚可以更雾，但不能抢前景内容卡。
- 亮色主题下玻璃应清、亮、边缘明确，不能变成大面积白盒。
- 暗色主题下玻璃应有边缘高光，不能融入背景或显脏。
- 搜索弹窗打开后，背景要被有效压暗/压低，弹窗边缘和内部层级要清楚。
- 图片卡标题、摘要、meta 在复杂图片背景下必须可读。

工程验收：

- `npm run style:guard`
- `npm run layering:guard`
- `npm run typecheck`
- 搜索业务层新增代码，不应出现新的完整 `glass-frost` 手写配方。
- 搜索新增代码，不应出现新的硬编码 `backdrop-filter: blur(20px)`、`blur(32px)` 这类非 token 写法。
- 搜索新增代码，`variant="counter"` 的 `SSurfaceChip` 必须显式声明 `size`。
- 亮暗主题截图至少覆盖：首页、文章详情、搜索弹窗、分类页、标签页、归档页。

## 8. 最终推荐

以 `SCard variant="lite/default/strong"` 和 `SSurfacePanel soft/default/inset` 为主线，确立“清透玻璃面板”为博客前台默认视觉。`glass-frost` 保留为低层背景材质，不再作为内容卡默认材质。

这能同时满足三个目标：

- 视觉上更像液态玻璃，而不是白色磨砂盒子。
- 设计系统上更符合 `UI Abstraction` 统一出口。
- 后续维护时可以靠 `variant / surface` 表达层级，而不是每个业务页面复制一套玻璃 CSS。
