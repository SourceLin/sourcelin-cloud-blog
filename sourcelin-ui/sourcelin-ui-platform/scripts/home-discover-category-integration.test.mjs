import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const read = (relativePath) => fs.readFile(path.resolve(relativePath), 'utf8')

test('Home discover section uses dedicated filter component and discover feed composable', async () => {
  const homePage = await read('src/modules/home/pages/HomePage.vue')
  const homeComposable = await read('src/modules/home/composables/useHomePage.ts')
  const discoverFeed = await read('src/modules/home/composables/useDiscoverFeed.ts')
  const discoverFilter = await read('src/modules/home/components/HomeDiscoverFilter.vue')

  assert.match(homePage, /HomeDiscoverFilter/)
  assert.doesNotMatch(homePage, /HomeCategorySection/)
  assert.match(homePage, /discoverFeed\./)
  assert.match(homeComposable, /goToArchive/)
  assert.doesNotMatch(homeComposable, /const loadArticles = async/)
  assert.match(discoverFeed, /selectedCategoryId/)
  assert.match(discoverFeed, /router\.replace/)
  assert.match(discoverFeed, /requestSeq/)
  assert.doesNotMatch(discoverFeed, /scrollIntoView/)
  assert.match(discoverFilter, /SSurfaceChip/)
  assert.match(discoverFilter, /aria-pressed/)
  assert.match(discoverFilter, /discover-filter__chip\[aria-pressed='true'\]/)
  assert.match(discoverFilter, /discover-filter__chip::before/)
  assert.match(discoverFilter, /:tone="selectedCategoryId == null \? 'accent' : 'default'"/)
  assert.match(discoverFilter, /:tone="selectedCategoryId === category.id \? 'accent' : 'default'"/)
  assert.match(discoverFilter, /box-shadow:/)
  assert.match(discoverFilter, /discover-filter__viewport/)
  assert.match(discoverFilter, /discover-filter__nav/)
  assert.match(discoverFilter, /scrollBy\(/)
  assert.match(discoverFilter, /ResizeObserver/)
  assert.match(discoverFilter, /canScrollPrev/)
  assert.match(discoverFilter, /canScrollNext/)
  assert.doesNotMatch(discoverFilter, /openArchive/)
  assert.doesNotMatch(discoverFilter, /查看该分类归档/)
})

test('Home page hydrates discover feed from home latest data before fallback article requests', async () => {
  const homePage = await read('src/modules/home/pages/HomePage.vue')
  const homeComposable = await read('src/modules/home/composables/useHomePage.ts')
  const discoverFeed = await read('src/modules/home/composables/useDiscoverFeed.ts')

  assert.match(homePage, /parseDiscoverCategoryId\(route\.query\.dc\)/)
  assert.match(homePage, /const homeData = await initHomePage\(initialCategoryId\)/)
  assert.match(homePage, /await discoverFeed\.initDiscover\(homeData\?\.latest \?\? null\)/)
  assert.match(homeComposable, /const data = await getHome\(params\)/)
  assert.match(homeComposable, /return data/)
  assert.match(discoverFeed, /async function initDiscover\(initialLatest: PageResult<ArticleListItem> \| null = null\)/)
  assert.match(discoverFeed, /if \(initialLatest && rawRouteCategoryId === resolvedCategoryId\)/)
})

test('Home discover articles section uses centered shared loading state and removes toolbar chrome', async () => {
  const source = await read('src/modules/home/components/HomeArticlesSection.vue')
  const loadingState = await read('src/shared/components/feedback/InlineLoadingState.vue')

  assert.match(source, /switching:\s*boolean/)
  assert.match(source, /articles-section--switching/)
  assert.match(source, /InlineLoadingState/)
  assert.match(source, /articles-switch-veil/)
  assert.match(source, /暂无文章/)
  assert.doesNotMatch(source, /articles-toolbar/)
  assert.doesNotMatch(source, /当前筛选/)
  assert.match(loadingState, /SSpin/)
  assert.match(loadingState, /role="status"/)
  assert.match(loadingState, /aria-live="polite"/)
})
