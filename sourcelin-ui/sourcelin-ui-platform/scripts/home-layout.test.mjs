import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const homePagePath = path.resolve('src/modules/home/pages/HomePage.vue')
const pagePreludePath = path.resolve('src/modules/home/components/HomeSectionPrelude.vue')
const bodyOnlyFiles = [
  'src/modules/home/components/HomeSiteStats.vue',
  'src/modules/home/components/HomeRecommendedList.vue',
  'src/modules/home/components/HomeCarouselSection.vue',
  'src/modules/home/components/HomeDiscoverFilter.vue',
  'src/modules/home/components/HomeLabelCloud.vue',
  'src/modules/home/components/HomeArticlesSection.vue'
]

test('Home page uses a split sidebar and main content shell', async () => {
  const source = await fs.readFile(homePagePath, 'utf8')

  assert.match(source, /class="[^"]*home-split[^"]*"/)
  assert.match(source, /class="[^"]*home-sidebar[^"]*"/)
  assert.match(source, /class="[^"]*home-main[^"]*"/)
  assert.doesNotMatch(source, /class="sidebar"/)
  assert.doesNotMatch(source, /class="content-grid"/)
})

test('Home page orders modules as sidebar, picks, and discovery', async () => {
  const source = await fs.readFile(homePagePath, 'utf8')

  assert.match(source, /HomeSiteCard[\s\S]*?HomeSiteStats[\s\S]*?HomeLabelCloud/s)
  assert.match(source, /HomeMarquee[\s\S]*?HomeSectionPrelude[\s\S]*?HomeCarouselSection[\s\S]*?HomeRecommendedList/s)
  assert.match(source, /HomeSectionPrelude[\s\S]*?HomeDiscoverFilter[\s\S]*?HomeArticlesSection/s)
})

test('Home page copy stays platform-facing and avoids magazine rationales', async () => {
  const source = await fs.readFile(homePagePath, 'utf8')

  assert.match(source, /HomeSectionPrelude title="精选"/)
  assert.match(source, /HomeSectionPrelude title="发现"/)
  assert.doesNotMatch(
    source,
    /Feature Stage|Gallery Walk|Main Exhibition|Editor's Pick|入园|花园|策展|专题封面/
  )
})

test('Home section prelude uses a compact metadata header structure', async () => {
  const source = await fs.readFile(pagePreludePath, 'utf8')

  assert.match(source, /class="section-prelude"/)
  assert.match(source, /class="prelude-shell[^"]*"/)
  assert.match(source, /class="prelude-title"/)
  assert.doesNotMatch(source, /prelude-mark/)
  assert.doesNotMatch(source, /prelude-copy/)
  assert.doesNotMatch(source, /prelude-facts/)
  assert.doesNotMatch(source, /prelude-story/)
  assert.doesNotMatch(source, /prelude-main/)
  assert.doesNotMatch(source, /prelude-aside/)
})

test('Home content modules no longer render duplicated section heading shells', async () => {
  for (const file of bodyOnlyFiles) {
    const source = await fs.readFile(path.resolve(file), 'utf8')
    assert.doesNotMatch(source, /HomeSectionHeading/, `${file} should not use HomeSectionHeading`)
    assert.doesNotMatch(source, /rail-label|recommended-rail|carousel-rail|category-rail|label-rail|articles-rail/, `${file} should not render duplicated rail headings`)
  }
})

test('Picks and feed modules expose authors and stable reading flow', async () => {
  const recommendedSource = await fs.readFile(path.resolve('src/modules/home/components/HomeRecommendedList.vue'), 'utf8')
  const carouselSource = await fs.readFile(path.resolve('src/modules/home/components/HomeCarouselSection.vue'), 'utf8')
  const articlesSource = await fs.readFile(path.resolve('src/modules/home/components/HomeArticlesSection.vue'), 'utf8')

  assert.match(recommendedSource, /authorName/)
  assert.match(recommendedSource, /recommended-source/)
  assert.match(carouselSource, /authorName/)
  assert.match(carouselSource, /carousel-source/)
  assert.match(articlesSource, /WaterfallList/)
  assert.match(articlesSource, /featured-article/)
  assert.match(articlesSource, /article-card/)
})

test('Discover filter uses surfaced chips without archive chrome or seamless marquee cards', async () => {
  const filterSource = await fs.readFile(path.resolve('src/modules/home/components/HomeDiscoverFilter.vue'), 'utf8')

  assert.match(filterSource, /SSurfaceChip/)
  assert.match(filterSource, /aria-pressed/)
  assert.doesNotMatch(filterSource, /discover-filter__archive/)
  assert.doesNotMatch(filterSource, /SeamlessScroll/)
})

test('Home section shell stays on cool glass surfaces and keeps amber out of structural container glow', async () => {
  const source = await fs.readFile(homePagePath, 'utf8')
  const shellMatch = source.match(/\.home-section-shell\s*\{[\s\S]*?\n\}/)

  assert.ok(shellMatch, 'home-section-shell style block should exist')
  assert.match(shellMatch[0], /var\(--surface-panel-soft\)/)
  assert.match(shellMatch[0], /var\(--surface-panel-strong\)/)
  assert.doesNotMatch(shellMatch[0], /var\(--companion-color\)/)

  const shellBeforeMatch = source.match(/\.home-section-shell::before\s*\{[\s\S]*?\n\}/)
  assert.ok(shellBeforeMatch, 'home-section-shell::before style block should exist')
  assert.doesNotMatch(shellBeforeMatch[0], /var\(--companion-color\)/)
})

test('Home page tag data is normalized from backend tags instead of hard-coded fallbacks', async () => {
  const source = await fs.readFile(path.resolve('src/modules/home/composables/useHomePage.ts'), 'utf8')

  assert.match(source, /const popularTags = ref<HomeTag\[]>\(\[]\)/)
  assert.match(source, /const normalizeTags = \(list: TagOption\[] \| undefined\): HomeTag\[] =>/)
  assert.match(source, /popularTags\.value = normalizeTags\(data\.tags\)/)
})

test('Label cloud uses center bias and collision spacing instead of outer-ring distribution', async () => {
  const source = await fs.readFile(path.resolve('src/modules/home/components/HomeLabelCloud.vue'), 'utf8')

  assert.match(source, /centerBias/)
  assert.match(source, /collisionRadius/)
  assert.doesNotMatch(source, /radiusBase = 38 \+ Math\.sqrt\(\(index \+ 0\.5\) \/ total\) \* 22/)
})

test('Label cloud limits visible tags from container size and keeps overflow clipping', async () => {
  const source = await fs.readFile(path.resolve('src/modules/home/components/HomeLabelCloud.vue'), 'utf8')

  assert.match(source, /ResizeObserver/)
  assert.match(source, /maxVisibleTags/)
  assert.match(source, /visibleTags/)
  assert.match(source, /slice\(0,\s*maxVisibleTags\.value\)/)
  assert.match(source, /overflow:\s*hidden/)
})

test('Label cloud animates from cached layout positions instead of re-running collisions every frame', async () => {
  const source = await fs.readFile(path.resolve('src/modules/home/components/HomeLabelCloud.vue'), 'utf8')

  assert.match(source, /const layoutTags = computed/)
  assert.match(source, /requestAnimationFrame/)
  assert.match(source, /cancelAnimationFrame/)
  assert.match(source, /return layoutTags\.value\.map/)
})

test('Home label cloud exposes tag click events and home page forwards them to tag routing', async () => {
  const labelSource = await fs.readFile(path.resolve('src/modules/home/components/HomeLabelCloud.vue'), 'utf8')
  const homeSource = await fs.readFile(path.resolve('src/modules/home/pages/HomePage.vue'), 'utf8')

  assert.match(labelSource, /defineEmits<\{\s*openTag:\s*\[name: string\]\s*\}>/)
  assert.match(labelSource, /@click="emit\('openTag', tag\.name\)"/)
  assert.match(homeSource, /HomeLabelCloud[\s\S]*@open-tag="goToTag"/s)
})

test('Tag page reads tag query and loads real tag articles', async () => {
  const tagPageSource = await fs.readFile(path.resolve('src/modules/article/pages/TagPage.vue'), 'utf8')

  assert.match(tagPageSource, /useRoute/)
  assert.match(tagPageSource, /route\.query\.name/)
  assert.match(tagPageSource, /getTagArticles/)
  assert.match(tagPageSource, /watch\(/)
  assert.doesNotMatch(tagPageSource, /实战入门指南|搭建 .* 项目/)
})
