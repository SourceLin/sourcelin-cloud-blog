# Platform Visual Refresh Implementation Plan

> **状态（2026-04-06）**：本计划中的改造已在当前仓库**落地**；下列任务勾选表示已完成。后续若再改视觉，请同步更新 `docs/superpowers/specs/2026-04-06-platform-visual-refresh-design.md` 中的「落地对照」段落。
>
> **For agentic workers:** 若从零重做同类刷新，可使用 superpowers:subagent-driven-development 或 executing-plans 按任务拆解执行。

**Goal:** 在不改动业务逻辑、接口和路由的前提下，为 `sourcelin-ui-platform` 建立统一的双主题玻璃态视觉基座，并精修首页、文章详情、评论区、树洞页、说说页和导航页脚的排版与视觉表现。

**Architecture:** 先修改 `tokens + foundation + naive override + S* UI`，把双主题背景、面板、按钮、输入框、分页、排版节奏统一收口；再在布局层和关键页做局部样式重构，让业务页面消费同一套变量与面板层级。所有业务层样式继续遵守项目的 `var(--*)`、`S*` 组件和 Naive UI 边界约束。

**Tech Stack:** Vue 3、TypeScript、Vite、SCSS、Naive UI、Node `--test` 脚本、项目自带 `style:guard / test:architecture / build / typecheck`

## 落地结果摘要（与代码一致）

- **浅色冷感紫灰**：`tokens/base.scss` 中 `--body-gradient`、`--page-glow`、`--page-content-*` 与背景色键已收敛，避免早期偏粉氛围。
- **全站玻璃增强**：提高浅色毛玻璃 blur/saturate 等；`foundation/glass.scss`、`foundation/page-shell.scss` 与 `S*` 组件统一面板阴影与高光语义。
- **正文背景统一策略**：`semantic.scss` 中 `--surface-page` / `--surface-page-content` / ambient / transition；`index.scss` 的 `body` 使用 `var(--surface-page)`；首页 `.page-container-wrap`、文章 `.article-page`、说说 `.say-page` 等与上述语义对齐。
- **树洞品牌特例**：`TreeholePage.vue` 使用 fixed 全屏底图（`home-bg-9.jpg`）+ 全高 `barrage-hero`；`.treehole-main` 以雾面 `backdrop-filter` 承接；列表/输入/讨论仍用 `sourcelin-panel*`、`--page-section-gap`、`--page-block-gap` 等，**不**扩散为第二套组件体系。
- **视觉契约测试**：`scripts/visual-contract.test.mjs` 已存在且通过；**未**并入 `scripts/architecture-refactor.test.mjs`（`npm run test:architecture` 仍为独立脚本）。契约需单独执行：`node --test scripts/visual-contract.test.mjs`。

## 验证记录（本工作区已执行）

| 命令 | 结果 |
|------|------|
| `node --test scripts/visual-contract.test.mjs` | 通过（3 tests） |
| `npm run typecheck` | 通过 |
| `npm run style:guard` | Active violations: 0 |
| `npm run test:style-guard` | 通过 |
| `npm run test:architecture` | 通过 |
| `npm run build` | 通过 |

---

## 文件结构与职责

- `src/assets/styles/tokens/base.scss`
  - 收口浅色 / 深色主题的背景、玻璃参数、主字号、间距、圆角、阴影和 glow
- `src/assets/styles/tokens/semantic.scss`
  - 暴露页面、正文、标题、输入、容器和阅读相关语义变量
- `src/assets/styles/tokens/component.scss`
  - 统一面板层级、按钮、chip、代码块、表格、分页、空态等组件级 token
- `src/assets/styles/foundation/glass.scss`
  - 用统一 token 重写面板族、玻璃块、卡片、弹层和交互反馈
- `src/assets/styles/index.scss`
  - 汇总全局背景、正文排版、滚动条、选择态、通用容器和页面壳基础样式
- `src/assets/styles/responsive-enhancements.scss`
  - 与本次刷新配套的响应式节奏与断点表现（随基座一并调整）
- `src/assets/styles/foundation/page-shell.scss`
  - `.glass-page` 壳层、`--page-glow` 与深色下的页面氛围叠加
- `src/assets/styles/naive/override.scss`
  - 收敛 Naive 的输入、选择器、消息、通知、分页等底层表现
- `src/shared/components/ui/SCard.vue`
  - 卡片的边框、表面、header/footer padding、hover 抬升规则
- `src/shared/components/ui/SButton.vue`
  - 主按钮 / CTA / site 按钮的玻璃态、渐变、发光和 hover 反馈
- `src/shared/components/ui/SInput.vue`
  - 输入框 / textarea 的表面、focus、placeholder、计数与文本色
- `src/shared/components/ui/SPagination.vue`
  - 页码容器和按钮圆角、表面、当前态的统一表达
- `src/shared/components/layout/AppHeader.vue`
  - 顶部导航、下拉菜单、创作入口、搜索和用户入口的视觉统一
- `src/shared/components/layout/AppFooter.vue`
  - 页脚品牌区、运行时间卡片、统计卡片的排版与层级
- `src/modules/home/pages/HomePage.vue`
  - 首页 hero 与主内容区的过渡、双栏节奏、模块对齐
- `src/modules/article/pages/ArticlePage.vue`
  - 文章详情双栏结构、阅读节奏、评论区入口区域
- `src/shared/components/comments/CommentComposer.vue`
  - 评论输入区的头像、输入、操作条和回复提示布局
- `src/shared/components/comments/CommentThread.vue`
  - 评论主项、回复项、空状态、分页与操作区的线程层级
- `src/modules/treehole/pages/TreeholePage.vue`
  - 树洞页 hero、发帖卡、列表卡和展开讨论区的秩序统一
- `src/modules/say/pages/SayPage.vue`、`src/modules/say/components/SayPageHero.vue`
  - 说说页 `glass-page`、hero（FeatureCard）、feed、弹窗、空状态和滚动节奏
- `src/modules/navigation/pages/LinksPage.vue` 等其它 `glass-page` 频道页
  - 随全局壳与 token 自然跟随，不单独维护第二套视觉
- `scripts/home-layout.test.mjs`
  - 首页结构与模块顺序等回归断言（与视觉契约互补）
- `scripts/visual-contract.test.mjs`
  - token、`S*`、关键页与导航页脚的字符串契约（**独立**于 `test:architecture`）

## Task 1: 建立回归基线并补视觉契约脚本

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\home-layout.test.mjs`
- Create: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\style-guard.test.mjs`

- [x] **Step 1: 写一个会失败的视觉契约测试，锁定关键文件必须继续消费统一变量与关键 class**

```js
import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'

const root = new URL('../', import.meta.url)
const read = (file) => fs.readFileSync(new URL(file, root), 'utf8')

test('关键视觉文件继续基于 CSS 变量与 glass 面板层级实现', () => {
  const header = read('src/shared/components/layout/AppHeader.vue')
  const article = read('src/modules/article/pages/ArticlePage.vue')
  const comments = read('src/shared/components/comments/CommentThread.vue')

  assert.match(header, /var\\(--/)
  assert.match(article, /var\\(--/)
  assert.match(comments, /sourcelin-panel-inset|comment-pagination/)
})
```

- [x] **Step 2: 运行新测试，确认它先失败**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: FAIL，因为测试文件尚未创建或断言尚未满足

- [x] **Step 3: 添加最小可运行测试实现**（落地为 `scripts/visual-contract.test.mjs`）

**与早期计划差异**：未在 `scripts/architecture-refactor.test.mjs` 中 `import` 本文件；`test:architecture` 仍只跑路由与 style-guard 夹具测试。视觉契约请使用：

`node --test scripts/visual-contract.test.mjs`

- [x] **Step 4: 再次运行测试，确认绿色**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

- [x] **Step 5: 提交本任务**（示例；若将来选择并入 `test:architecture` 再追加 `architecture-refactor.test.mjs`）

```bash
git add scripts/visual-contract.test.mjs
git commit -m "test(platform): add visual contract regression checks"
```

## Task 2: 收口 tokens、semantic 与 foundation 视觉基座

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\tokens\base.scss`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\tokens\semantic.scss`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\tokens\component.scss`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\foundation\glass.scss`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\index.scss`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`

- [x] **Step 1: 先写一个失败断言，要求 tokens 中出现新的阅读与页面语义变量**

```js
test('基础样式 token 暴露页面阅读和面板节奏变量', () => {
  const base = read('src/assets/styles/tokens/base.scss')
  const semantic = read('src/assets/styles/tokens/semantic.scss')
  assert.match(base, /--page-section-gap:/)
  assert.match(base, /--reading-line-height:/)
  assert.match(semantic, /--surface-page:/)
  assert.match(semantic, /--content-max-width-readable:/)
})
```

- [x] **Step 2: 运行测试确认失败**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: FAIL，提示缺少新的 token 名称

- [x] **Step 3: 在 tokens 和 foundation 中实现最小基座改造**

```scss
:root {
  --page-section-gap: clamp(1.25rem, 2vw, 2rem);
  --page-block-gap: clamp(1rem, 1.6vw, 1.5rem);
  --reading-line-height: 1.75;
  --content-max-width-readable: 840px;
  --surface-page:
    radial-gradient(circle at 12% 10%, color-mix(in srgb, var(--primary-color) 10%, transparent), transparent 34%),
    radial-gradient(circle at 88% 0%, color-mix(in srgb, var(--secondary-color) 14%, transparent), transparent 28%),
    var(--body-gradient);
}
```

- [x] **Step 4: 在 `index.scss` 落正文排版、滚动条和通用容器的最小实现**

```scss
body {
  line-height: var(--reading-line-height);
  background: var(--surface-page);
}

.sourcelin-page-shell {
  width: min(var(--content-max-width), calc(100% - 2rem));
  margin: 0 auto;
}
```

- [x] **Step 5: 运行契约测试确认通过**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

- [x] **Step 6: 提交本任务**

```bash
git add src/assets/styles/tokens/base.scss src/assets/styles/tokens/semantic.scss src/assets/styles/tokens/component.scss src/assets/styles/foundation/glass.scss src/assets/styles/index.scss scripts/visual-contract.test.mjs
git commit -m "style(platform): unify theme tokens and glass foundation"
```

## Task 3: 收口 Naive override 与 `S*` 基础组件

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\assets\styles\naive\override.scss`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\ui\SCard.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\ui\SButton.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\ui\SInput.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\ui\SPagination.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`

- [x] **Step 1: 先让契约测试要求 `SButton` 和 `SCard` 使用新的面板层级变量**

```js
test('基础 UI 抽象组件消费统一 surface token', () => {
  const button = read('src/shared/components/ui/SButton.vue')
  const card = read('src/shared/components/ui/SCard.vue')
  assert.match(button, /--button-glow|surface-panel/)
  assert.match(card, /surface-panel|shadow-panel/)
})
```

- [x] **Step 2: 运行测试确认失败**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: FAIL

- [x] **Step 3: 在 `SCard`、`SButton`、`SInput`、`SPagination` 做最小改造**

```scss
.s-card {
  background: var(--surface-panel-default);
  border: 1px solid var(--border-panel-default);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-default);
}

.s-button--cta {
  --button-glow: 0 14px 28px color-mix(in srgb, var(--primary-color) 18%, transparent);
}
```

- [x] **Step 4: 在 `naive/override.scss` 统一分页、消息、通知和选择器表面**

```scss
.n-pagination .n-pagination-item.n-pagination-item--active {
  background: var(--surface-panel-chip-accent);
  border-color: var(--border-interactive-hover);
}
```

- [x] **Step 5: 运行测试确认通过**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

- [x] **Step 6: 提交本任务**

```bash
git add src/assets/styles/naive/override.scss src/shared/components/ui/SCard.vue src/shared/components/ui/SButton.vue src/shared/components/ui/SInput.vue src/shared/components/ui/SPagination.vue scripts/visual-contract.test.mjs
git commit -m "style(platform): refine shared ui glass components"
```

## Task 4: 精修全局布局组件

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\layout\AppHeader.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\layout\AppFooter.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`

- [x] **Step 1: 先写失败断言，要求导航和页脚都使用统一的玻璃容器变量**

```js
test('导航与页脚使用统一布局表面和交互反馈', () => {
  const header = read('src/shared/components/layout/AppHeader.vue')
  const footer = read('src/shared/components/layout/AppFooter.vue')
  assert.match(header, /layout-surface-strong|toolbar-button-surface-hover/)
  assert.match(footer, /footer-card-surface|footer-divider/)
})
```

- [x] **Step 2: 运行测试确认失败**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: FAIL

- [x] **Step 3: 在 `AppHeader.vue` 调整导航高度、容器宽度、按钮与下拉面板**

```scss
.glass-header {
  height: 72px;
  background: color-mix(in srgb, var(--layout-surface) 92%, transparent);
  box-shadow: var(--shadow-panel-soft);
}
```

- [x] **Step 4: 在 `AppFooter.vue` 调整品牌区、运行卡片和统计卡的排版**

```scss
.footer-main {
  display: grid;
  grid-template-columns: 1.3fr auto auto;
  gap: clamp(1rem, 2vw, 1.75rem);
}
```

- [x] **Step 5: 运行测试确认通过**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

- [x] **Step 6: 提交本任务**

```bash
git add src/shared/components/layout/AppHeader.vue src/shared/components/layout/AppFooter.vue scripts/visual-contract.test.mjs
git commit -m "style(platform): polish header and footer layout"
```

## Task 5: 精修首页、文章详情与评论区

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\home\pages\HomePage.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\article\pages\ArticlePage.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\comments\CommentComposer.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\shared\components\comments\CommentThread.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\home-layout.test.mjs`

- [x] **Step 1: 先写首页和评论区失败断言，锁定新的 section 节奏与评论线程层级**

```js
test('首页与评论区保留统一 section 节奏和线程容器', () => {
  const home = read('src/modules/home/pages/HomePage.vue')
  const composer = read('src/shared/components/comments/CommentComposer.vue')
  const thread = read('src/shared/components/comments/CommentThread.vue')
  assert.match(home, /page-section-gap|content-section/)
  assert.match(composer, /comment-composer/)
  assert.match(thread, /reply-item|comment-pagination/)
})
```

- [x] **Step 2: 运行测试确认失败**

Run: `node --test scripts/home-layout.test.mjs scripts/visual-contract.test.mjs`
Expected: FAIL

- [x] **Step 3: 在 `HomePage.vue` 调整 hero 到正文的过渡、双栏节奏与模块对齐**

```scss
.home-main {
  gap: var(--page-section-gap);
}

.content-section {
  gap: var(--page-block-gap);
}
```

- [x] **Step 4: 在 `ArticlePage.vue`、`CommentComposer.vue`、`CommentThread.vue` 调整阅读与评论排版**

```scss
.comments-section {
  padding: clamp(1.25rem, 2vw, 2rem);
}

.comment-item {
  grid-template-columns: 56px minmax(0, 1fr);
}
```

- [x] **Step 5: 运行测试确认通过**

Run: `node --test scripts/home-layout.test.mjs scripts/visual-contract.test.mjs`
Expected: PASS

- [x] **Step 6: 提交本任务**

```bash
git add src/modules/home/pages/HomePage.vue src/modules/article/pages/ArticlePage.vue src/shared/components/comments/CommentComposer.vue src/shared/components/comments/CommentThread.vue scripts/home-layout.test.mjs scripts/visual-contract.test.mjs
git commit -m "style(platform): refine home article and comments layout"
```

## Task 6: 精修树洞、说说并完成全量校验

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\treehole\pages\TreeholePage.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\say\pages\SayPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`

- [x] **Step 1: 写失败断言，要求树洞和说说继续使用统一面板层级与页面节奏变量**

```js
test('树洞与说说页使用统一 glass panel 层级', () => {
  const treehole = read('src/modules/treehole/pages/TreeholePage.vue')
  const say = read('src/modules/say/pages/SayPage.vue')
  assert.match(treehole, /sourcelin-panel|surface-panel/)
  assert.match(say, /sourcelin-panel|surface-panel/)
})
```

- [x] **Step 2: 运行测试确认失败**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: FAIL

- [x] **Step 3: 在 `TreeholePage.vue` 和 `SayPage.vue` 实现最小排版收口**

```scss
.messages-section,
.message-input-section,
.say-stage {
  gap: var(--page-block-gap);
}
```

- [x] **Step 4: 运行局部测试和全量验证**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

Run: `npm run style:guard`
Expected: exit 0

Run: `npm run test:style-guard`
Expected: pass

Run: `npm run test:architecture`
Expected: pass

Run: `npm run typecheck`
Expected: exit 0

Run: `npm run build`
Expected: exit 0

- [x] **Step 5: 提交本任务**

```bash
git add src/modules/treehole/pages/TreeholePage.vue src/modules/say/pages/SayPage.vue scripts/visual-contract.test.mjs
git commit -m "style(platform): polish treehole say and final visual rhythm"
```

## 自检

- 规格覆盖：
  - 双主题视觉基座：Task 2
  - 玻璃态与组件统一：Task 3
  - 导航与页脚：Task 4
  - 首页、文章详情、评论区：Task 5
  - 树洞、说说：Task 6
  - 验证命令：Task 6 + 文档「验证记录」表
- 占位符检查：
  - 未使用 `TBD`、`TODO` 或“类似 Task N”的写法
- 类型与命名一致性：
  - 统一使用 `visual-contract.test.mjs`（独立运行，非 `test:architecture` 子集）
  - 统一使用 `page-section-gap / page-block-gap / reading-line-height`

## 执行方式

本批次已按 **Inline Execution** 在仓库中落地；后续迭代请在改样式/Tokens 后重跑「验证记录」中的命令，并更新 spec 中的落地对照表（尤其树洞特例与页面壳边界）。
