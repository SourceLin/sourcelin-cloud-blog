# Home Curation Layout Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild the Home page below the existing hero into a curated, asymmetrical layout that matches `docs/Home_Redesign_Scheme.md` without changing the hero itself.

**Architecture:** Keep the existing home module boundaries and data flow, but replace the page-level two-column composition with four curated sections. Update section components in place so the layout shift is driven by `HomePage.vue` while each business component gets a clear visual role inside the new composition.

**Tech Stack:** Vue 3 SFCs, TypeScript, SCSS, existing shared UI wrappers, Node `node:test`, `vue-tsc`, Vite build, local style/architecture guards.

---

### Task 1: Lock The New Page Structure

**Files:**
- Create: `scripts/home-layout.test.mjs`
- Modify: `src/modules/home/pages/HomePage.vue`

- [ ] **Step 1: Write the failing test**

```js
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'

const source = await fs.readFile(new URL('../src/modules/home/pages/HomePage.vue', import.meta.url), 'utf8')

assert.match(source, /class="home-curation"/)
assert.match(source, /class="curation-section curation-prologue"/)
assert.match(source, /class="curation-section curation-feature-stage"/)
assert.match(source, /class="curation-section curation-atlas"/)
assert.match(source, /class="curation-section curation-gallery"/)
assert.doesNotMatch(source, /class="content-grid"/)
assert.doesNotMatch(source, /class="sidebar"/)
```

- [ ] **Step 2: Run test to verify it fails**

Run: `node --test scripts/home-layout.test.mjs`
Expected: FAIL because the current page still contains `content-grid` and lacks curated section classes.

- [ ] **Step 3: Write minimal implementation**

Replace the page-level layout in `src/modules/home/pages/HomePage.vue` so the hero is unchanged but the content area becomes four curated sections:

```vue
<div class="home-curation">
  <section class="curation-section curation-prologue">...</section>
  <section class="curation-section curation-feature-stage">...</section>
  <section class="curation-section curation-atlas">...</section>
  <section class="curation-section curation-gallery">...</section>
</div>
```

- [ ] **Step 4: Run test to verify it passes**

Run: `node --test scripts/home-layout.test.mjs`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add scripts/home-layout.test.mjs src/modules/home/pages/HomePage.vue
git commit -m "feat: rebuild home page layout shell"
```

### Task 2: Build The Prologue And Feature Stage

**Files:**
- Modify: `src/modules/home/components/HomeMarquee.vue`
- Modify: `src/modules/home/components/HomeSiteCard.vue`
- Modify: `src/modules/home/components/HomeSiteStats.vue`
- Modify: `src/modules/home/components/HomeCarouselSection.vue`
- Modify: `src/modules/home/components/HomeRecommendedList.vue`

- [ ] **Step 1: Write the failing test**

Extend `scripts/home-layout.test.mjs` with assertions for the new section roles:

```js
assert.match(source, /<HomeMarquee[\s\S]*?<HomeSiteCard[\s\S]*?<HomeSiteStats/s)
assert.match(source, /<HomeRecommendedList[\s\S]*?<HomeCarouselSection/s)
```

- [ ] **Step 2: Run test to verify it fails**

Run: `node --test scripts/home-layout.test.mjs`
Expected: FAIL until the page composition is updated to the new order.

- [ ] **Step 3: Write minimal implementation**

Update the components so they fit the curated roles:

```vue
<!-- HomeMarquee -->
<div class="marquee-intro">
  <span class="marquee-kicker">Exhibition Bulletin</span>
  ...
</div>
```

```vue
<!-- HomeRecommendedList -->
<div class="recommended-column">
  <header class="recommended-header">...</header>
  ...
</div>
```

- [ ] **Step 4: Run test to verify it passes**

Run: `node --test scripts/home-layout.test.mjs`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/modules/home/components/HomeMarquee.vue src/modules/home/components/HomeSiteCard.vue src/modules/home/components/HomeSiteStats.vue src/modules/home/components/HomeCarouselSection.vue src/modules/home/components/HomeRecommendedList.vue
git commit -m "feat: style curated home prologue and feature stage"
```

### Task 3: Build The Atlas And Gallery Sections

**Files:**
- Modify: `src/modules/home/components/HomeCategorySection.vue`
- Modify: `src/modules/home/components/HomeLabelCloud.vue`
- Modify: `src/modules/home/components/HomeArticlesSection.vue`

- [ ] **Step 1: Write the failing test**

Extend `scripts/home-layout.test.mjs` with assertions for atlas and gallery containers:

```js
assert.match(source, /<HomeCategorySection[\s\S]*?<HomeLabelCloud/s)
assert.match(source, /class="curation-section curation-gallery"/)
```

- [ ] **Step 2: Run test to verify it fails**

Run: `node --test scripts/home-layout.test.mjs`
Expected: FAIL until the page and supporting sections follow the new composition.

- [ ] **Step 3: Write minimal implementation**

Give the category and article sections dedicated editorial framing and responsive layout behavior:

```vue
<section class="category-section atlas-panel">...</section>
<section class="articles-section gallery-walk">...</section>
```

- [ ] **Step 4: Run test to verify it passes**

Run: `node --test scripts/home-layout.test.mjs`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/modules/home/components/HomeCategorySection.vue src/modules/home/components/HomeLabelCloud.vue src/modules/home/components/HomeArticlesSection.vue
git commit -m "feat: complete curated home atlas and gallery"
```

### Task 4: Verify Build And Guardrails

**Files:**
- Test: `scripts/home-layout.test.mjs`
- Test: `scripts/architecture-refactor.test.mjs`
- Test: `scripts/style-guard.test.mjs`

- [ ] **Step 1: Run the layout test**

Run: `node --test scripts/home-layout.test.mjs`
Expected: PASS

- [ ] **Step 2: Run the architecture test**

Run: `npm run test:architecture`
Expected: PASS with `architecture-refactor tests passed`

- [ ] **Step 3: Run the style guard**

Run: `npm run test:style-guard`
Expected: PASS without new active violations

- [ ] **Step 4: Run type/build verification**

Run: `npm run build`
Expected: PASS with successful `vue-tsc` and Vite build output

- [ ] **Step 5: Commit**

```bash
git add docs/superpowers/plans/2026-03-28-home-curation-layout.md
git commit -m "docs: add home curation implementation plan"
```
