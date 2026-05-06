import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const read = (relativePath) => fs.readFile(path.resolve(relativePath), 'utf8')

test('shared page shell and header expose layered editorial surfaces', async () => {
  const pageShell = await read('src/shared/components/layout/PageShell.vue')
  const pageHeader = await read('src/shared/components/layout/PageHeader.vue')
  const featureCard = await read('src/shared/components/layout/FeatureCard.vue')

  assert.match(pageShell, /width:\s*'1080px'/)
  assert.match(pageShell, /padding:\s*'0 20px 72px'/)
  assert.match(pageShell, /page-shell::before/)
  assert.match(pageShell, /page-shell::after/)
  assert.match(pageHeader, /class="header-frame"/)
  assert.match(pageHeader, /class="header-accent"/)
  assert.match(pageHeader, /\.page-header\.surface-soft\s*\{[\s\S]*width:\s*100%/)
  assert.match(pageHeader, /\.page-header\.surface-soft\s*\{[\s\S]*max-width:\s*100%/)
  assert.match(featureCard, /card-accent-glow/)
})

test('home and article pages use surfaced content sections instead of plain stacks', async () => {
  const home = await read('src/modules/home/pages/HomePage.vue')
  const article = await read('src/modules/article/pages/ArticlePage.vue')

  assert.match(home, /content-section picks-section/)
  assert.match(home, /content-section discover-section/)
  assert.match(home, /\.content-section\s*\{[\s\S]*border:\s*1px solid var\(--border-panel-subtle\)/)
  assert.match(article, /\.feature-shell::before/)
  assert.match(article, /\.lead-strip-item\s*\{[\s\S]*text-align:\s*left/)
})

test('notice and navigation pages present business content as framed workspaces', async () => {
  const notice = await read('src/modules/notice/pages/MessageCenterPage.vue')
  const navigation = await read('src/modules/navigation/pages/NavigationPage.vue')
  const treehole = await read('src/modules/treehole/pages/TreeholePage.vue')

  assert.match(notice, /\.notice-content-card::before/)
  assert.match(notice, /\.notice-sidebar\s*\{[\s\S]*backdrop-filter:/)
  assert.match(navigation, /\.nav-category::before/)
  assert.match(navigation, /\.nav-item:hover\s*\{[\s\S]*transform:\s*translateY\(-3px\)/)
  assert.match(treehole, /\.message-input-section,\s*\.messages-section\s*\{[\s\S]*max-width:\s*min\(1040px,\s*100%\)/)
  assert.match(treehole, /class="treehole-workspace sourcelin-panel-soft"/)
  assert.match(treehole, /class="input-row sourcelin-panel-soft"/)
  assert.match(treehole, /@keydown="handleHeroInputKeydown"/)
  assert.match(treehole, /function handleHeroInputKeydown\(event: KeyboardEvent\)/)
})

test('hot about navigation and user pages expose refreshed hero and workspace anchors', async () => {
  const hot = await read('src/modules/hot/pages/HotPage.vue')
  const about = await read('src/modules/about/pages/AboutPage.vue')
  const navigation = await read('src/modules/navigation/pages/NavigationPage.vue')
  const user = await read('src/modules/user/pages/UserPage.vue')

  assert.match(hot, /hot-hero__main/)
  assert.match(hot, /hot-featured-grid/)
  assert.match(hot, /hot-workspace/)
  assert.match(about, /about-hero__main/)
  assert.match(about, /about-story-grid/)
  assert.match(about, /about-contact-panel/)
  assert.match(navigation, /navigation-hero__main/)
  assert.match(navigation, /nav-filter-bar/)
  assert.match(navigation, /nav-index-grid/)
  assert.match(user, /user-hero__main/)
  assert.match(user, /profile-workspace/)
  assert.match(user, /profile-section-toolbar/)
})

test('category and tag pages use in-place browser to article-list transitions', async () => {
  const category = await read('src/modules/article/pages/CategoryPage.vue')
  const tag = await read('src/modules/article/pages/TagPage.vue')

  assert.match(category, /category-switch-shell/)
  assert.match(category, /category-browser-stage/)
  assert.match(category, /category-article-stage/)
  assert.doesNotMatch(category, /name="panel-swap"/)
  assert.match(category, /context-focus-strip/)
  assert.match(category, /'is-armed'/)
  assert.doesNotMatch(category, /animation:\s*focus-strip-in/)
  assert.match(category, /\.category-card\s*\{[\s\S]*min-height:\s*clamp\(6\.9rem,\s*10vw,\s*7\.8rem\)/)
  assert.match(category, /\.category-info\s*\{[\s\S]*row-gap:\s*clamp\(0\.28rem,\s*0\.7vw,\s*0\.42rem\)/)
  assert.match(category, /\.context-focus-strip__copy\s*\{[\s\S]*row-gap:\s*0\.38rem/)

  assert.match(tag, /tag-switch-shell/)
  assert.match(tag, /tag-browser-stage/)
  assert.match(tag, /tag-article-stage/)
  assert.doesNotMatch(tag, /name="panel-swap"/)
  assert.match(tag, /context-focus-strip/)
  assert.match(tag, /'is-armed'/)
  assert.doesNotMatch(tag, /animation:\s*focus-strip-in/)
  assert.match(tag, /\.tag-grid-card\s*\{[\s\S]*min-height:\s*clamp\(6\.9rem,\s*10vw,\s*7\.7rem\)/)
  assert.match(tag, /\.tag-grid-foot\s*\{[\s\S]*margin-top:\s*auto/)
  assert.match(tag, /\.context-focus-strip__copy\s*\{[\s\S]*row-gap:\s*0\.38rem/)
})

test('article detail preserves source-page breadcrumb context', async () => {
  const articlePage = await read('src/modules/article/pages/ArticlePage.vue')
  const articleHero = await read('src/modules/article/components/ArticleHero.vue')
  const categoryPage = await read('src/modules/article/pages/CategoryPage.vue')
  const tagPage = await read('src/modules/article/pages/TagPage.vue')
  const archivePage = await read('src/modules/article/pages/ArchivePage.vue')
  const articleSource = await read('src/modules/article/utils/article-source.ts')

  assert.match(articlePage, /resolveArticleSourceContext/)
  assert.match(articlePage, /breadcrumbSourceLabel/)
  assert.match(articlePage, /@go-source=/)
  assert.match(articleHero, /breadcrumbSourceLabel: string/)
  assert.match(articleHero, /emit\('go-source'\)/)
  assert.match(categoryPage, /buildCategoryArticleRoute/)
  assert.match(tagPage, /buildTagArticleRoute/)
  assert.match(archivePage, /buildArchiveArticleRoute/)
  assert.match(articleSource, /label: '分类'/)
  assert.match(articleSource, /label: '标签'/)
  assert.match(articleSource, /label: '归档'/)
})
