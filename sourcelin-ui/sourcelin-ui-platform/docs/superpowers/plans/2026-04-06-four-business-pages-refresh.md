# Four Business Pages Experience Refresh Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 重构热门、关于本站、网站导航、个人中心四个页面的头部与内容工作区，让它们统一于站点设计系统，并保留页面个性。

**Architecture:** 先用 `page-ui-contract` 扩展回归测试，锁定新的 hero / workspace 结构，再逐页改造 `HotPage.vue`、`AboutPage.vue`、`NavigationPage.vue`、`UserPage.vue`。复用 `PageShell`、`FeatureCard`、`PageHeader`、`S*` 组件，但不抽象成新的万能页面组件，避免牺牲页面个性。

**Tech Stack:** Vue 3、TypeScript、SCSS、Node `--test`、Vite build、style-guard

---

### Task 1: 扩展页面契约测试

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\hot\pages\HotPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\about\pages\AboutPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\navigation\pages\NavigationPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\user\pages\UserPage.vue`

- [ ] **Step 1: 写失败断言，锁定四个页面新的结构锚点**

```js
test('hot about navigation and user pages expose refreshed hero and workspace anchors', async () => {
  const hot = await read('src/modules/hot/pages/HotPage.vue')
  const about = await read('src/modules/about/pages/AboutPage.vue')
  const navigation = await read('src/modules/navigation/pages/NavigationPage.vue')
  const user = await read('src/modules/user/pages/UserPage.vue')

  assert.match(hot, /hot-hero__main|hot-featured-grid|hot-workspace/)
  assert.match(about, /about-hero__main|about-story-grid|about-timeline/)
  assert.match(navigation, /navigation-hero__main|nav-filter-bar|nav-index-grid/)
  assert.match(user, /user-hero__main|profile-workspace|profile-section-head/)
})
```

- [ ] **Step 2: 运行测试，确认先失败**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: FAIL，提示新锚点尚未存在

- [ ] **Step 3: 提交最小测试改动**

```bash
git add scripts/page-ui-contract.test.mjs
git commit -m "test(platform): extend page ui contract for business page refresh"
```

### Task 2: 重构热门页

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\hot\pages\HotPage.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\hot\components\HotCategoryTabs.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`

- [ ] **Step 1: 用测试驱动目标结构**

```js
assert.match(hot, /hot-featured-grid/)
assert.match(hot, /hot-overview-panel/)
assert.match(hot, /hot-stream-section/)
```

- [ ] **Step 2: 运行测试确认失败**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: FAIL

- [ ] **Step 3: 实现热门页 Hero 与榜单工作区**

```vue
<FeatureCard class="hot-hero" surface="soft" :hoverable="false" :shadow="true" shadow-level="sm">
  <div class="hot-hero__main">
    <div class="hot-hero__copy">...</div>
    <aside class="hot-overview-panel sourcelin-panel-soft">...</aside>
  </div>
</FeatureCard>
<section class="hot-workspace sourcelin-panel-soft">...</section>
<section class="hot-stream-section sourcelin-panel-soft">...</section>
```

- [ ] **Step 4: 调整分类 tabs 的频道感样式**

```scss
.hot-categories {
  padding: 0;
  gap: 12px;
}

.category-tab.active {
  box-shadow: var(--shadow-panel-hover);
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/modules/hot/pages/HotPage.vue src/modules/hot/components/HotCategoryTabs.vue scripts/page-ui-contract.test.mjs
git commit -m "feat(platform): redesign hot page as editorial ranking workspace"
```

### Task 3: 重构关于本站页

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\about\pages\AboutPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`

- [ ] **Step 1: 为关于页添加失败断言**

```js
assert.match(about, /about-hero__main/)
assert.match(about, /about-story-grid/)
assert.match(about, /about-contact-panel/)
```

- [ ] **Step 2: 运行测试确认失败**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: FAIL

- [ ] **Step 3: 实现叙事型 hero 和章节重排**

```vue
<FeatureCard class="about-hero" surface="soft" :hoverable="false" :shadow="true" shadow-level="sm">
  <div class="about-hero__main">...</div>
</FeatureCard>
<section class="about-story-grid">...</section>
<section class="about-contact-panel sourcelin-panel-soft">...</section>
```

- [ ] **Step 4: 用统一章节头重排兴趣、理念、联系模块**

```scss
.about-section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/modules/about/pages/AboutPage.vue scripts/page-ui-contract.test.mjs
git commit -m "feat(platform): redesign about page as narrative site profile"
```

### Task 4: 重构网站导航页

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\navigation\pages\NavigationPage.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`

- [ ] **Step 1: 为导航页添加失败断言**

```js
assert.match(navigation, /navigation-hero__main/)
assert.match(navigation, /nav-filter-bar/)
assert.match(navigation, /nav-index-grid/)
```

- [ ] **Step 2: 运行测试确认失败**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: FAIL

- [ ] **Step 3: 实现索引型 hero、本地过滤与最近访问**

```vue
<div class="nav-filter-bar sourcelin-panel-soft">...</div>
<div class="nav-index-grid">...</div>
```

- [ ] **Step 4: 保持分类章节，但增强章节头与入口层次**

```scss
.nav-category-head {
  display: grid;
  gap: var(--spacing-xs);
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/modules/navigation/pages/NavigationPage.vue scripts/page-ui-contract.test.mjs
git commit -m "feat(platform): redesign navigation page as indexed resource atlas"
```

### Task 5: 重构个人中心页

**Files:**
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\user\pages\UserPage.vue`
- Modify: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\src\modules\user\components\UserProfileSidebar.vue`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`

- [ ] **Step 1: 为个人中心页添加失败断言**

```js
assert.match(user, /user-hero__main/)
assert.match(user, /profile-workspace/)
assert.match(user, /profile-section-head/)
```

- [ ] **Step 2: 运行测试确认失败**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: FAIL

- [ ] **Step 3: 实现账户 hero、快捷摘要和内容区模块头**

```vue
<FeatureCard class="user-hero" surface="soft" :hoverable="false" :shadow="true" shadow-level="sm">...</FeatureCard>
<div class="profile-workspace">...</div>
<header class="profile-section-head">...</header>
```

- [ ] **Step 4: 调整侧栏为工作台导航风格**

```scss
.profile-sidebar {
  gap: var(--page-block-gap);
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/modules/user/pages/UserPage.vue src/modules/user/components/UserProfileSidebar.vue scripts/page-ui-contract.test.mjs
git commit -m "feat(platform): redesign user center as account workspace"
```

### Task 6: 全量验证

**Files:**
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\page-ui-contract.test.mjs`
- Test: `D:\Projects\sourcelin\sourcelin-cloud-blog\sourcelin-ui\sourcelin-ui-platform\scripts\visual-contract.test.mjs`

- [ ] **Step 1: 跑页面契约测试**

Run: `node --test scripts/page-ui-contract.test.mjs`
Expected: PASS

- [ ] **Step 2: 跑视觉契约测试**

Run: `node --test scripts/visual-contract.test.mjs`
Expected: PASS

- [ ] **Step 3: 跑样式守卫**

Run: `npm run style:guard`
Expected: `Active violations: 0`

- [ ] **Step 4: 跑样式守卫测试**

Run: `npm run test:style-guard`
Expected: PASS

- [ ] **Step 5: 跑生产构建**

Run: `npm run build`
Expected: exit 0

- [ ] **Step 6: Commit**

```bash
git add src/modules/hot/pages/HotPage.vue src/modules/about/pages/AboutPage.vue src/modules/navigation/pages/NavigationPage.vue src/modules/user/pages/UserPage.vue src/modules/user/components/UserProfileSidebar.vue scripts/page-ui-contract.test.mjs docs/superpowers/specs/2026-04-06-four-business-pages-refresh-design.md docs/superpowers/plans/2026-04-06-four-business-pages-refresh.md
git commit -m "feat(platform): refresh four business pages with mixed hero layout"
```