import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const read = (relativePath) => fs.readFile(path.resolve(relativePath), 'utf8')

test('Target pages use unified EmptyState component for empty-state rendering', async () => {
  const [
    homePage,
    hotPage,
    archivePage,
    navigationPage,
    treeholePage,
    categoryPage,
    tagPage,
    sayPage,
    linksPage,
    noticeList,
    waterfallList,
    emptyStateComponent,
    themeFoundation,
    pageShellFoundation
  ] = await Promise.all([
    read('src/modules/home/pages/HomePage.vue'),
    read('src/modules/hot/pages/HotPage.vue'),
    read('src/modules/article/pages/ArchivePage.vue'),
    read('src/modules/navigation/pages/NavigationPage.vue'),
    read('src/modules/treehole/pages/TreeholePage.vue'),
    read('src/modules/article/pages/CategoryPage.vue'),
    read('src/modules/article/pages/TagPage.vue'),
    read('src/modules/say/pages/SayPage.vue'),
    read('src/modules/navigation/pages/LinksPage.vue'),
    read('src/modules/notice/components/NoticeList.vue'),
    read('src/shared/components/layout/WaterfallList.vue'),
    read('src/shared/components/feedback/EmptyState.vue'),
    read('src/assets/styles/foundation/theme.scss'),
    read('src/assets/styles/foundation/page-shell.scss')
  ])

  assert.match(homePage, /<EmptyState[\s\S]*class="picks-empty"/)
  assert.doesNotMatch(homePage, /picks-empty__icon/)

  assert.match(hotPage, /<EmptyState[\s\S]*class="hot-empty-state"/)
  assert.match(hotPage, /<EmptyState[\s\S]*variant="inline"/)
  assert.doesNotMatch(hotPage, /class="empty-state sourcelin-panel-soft"/)

  assert.match(archivePage, /<EmptyState[\s\S]*class="archive-empty-state"/)
  assert.match(archivePage, /message="归档区还空着，过一会儿再来看也许就有新文章了。"/)

  assert.match(navigationPage, /<EmptyState[\s\S]*class="navigation-empty-state"/)
  assert.doesNotMatch(navigationPage, /class="empty-state sourcelin-panel-soft"/)

  assert.match(treeholePage, /<EmptyState[\s\S]*class="treehole-empty-state"/)
  assert.match(treeholePage, /message="做第一个在这里留下心情的人。"/)

  assert.match(categoryPage, /<EmptyState[\s\S]*class="context-state"/)
  assert.doesNotMatch(categoryPage, /context-state__title/)

  assert.match(tagPage, /<EmptyState[\s\S]*class="tag-state"/)
  assert.doesNotMatch(tagPage, /tag-state-title/)

  assert.match(sayPage, /<EmptyState[\s\S]*description-mode="typewriter"/)
  assert.match(sayPage, /:message="EMPTY_FULL_TEXT"/)
  assert.doesNotMatch(sayPage, /empty-illustration/)

  assert.match(linksPage, /<EmptyState[\s\S]*class="links-showcase__empty"/)
  assert.match(linksPage, /action-text="申请友情链接"/)

  assert.match(noticeList, /<EmptyState[\s\S]*title="暂无系统公告"/)
  assert.match(noticeList, /<EmptyState[\s\S]*icon="notice"/)
  assert.doesNotMatch(noticeList, /<EmptyState\s+message="暂无系统公告"/)

  assert.match(waterfallList, /<EmptyState[\s\S]*class="waterfall-empty"/)
  assert.doesNotMatch(waterfallList, /class="empty-state"/)

  assert.match(emptyStateComponent, /--empty-shell-gap:/)
  assert.match(emptyStateComponent, /--empty-content-gap:/)
  assert.match(emptyStateComponent, /--empty-actions-offset:/)
  assert.match(emptyStateComponent, /row-gap: var\(--empty-shell-gap\)/)
  assert.match(emptyStateComponent, /row-gap: var\(--empty-content-gap\)/)

  assert.doesNotMatch(themeFoundation, /\.empty-state/)
  assert.doesNotMatch(pageShellFoundation, /\.empty-state/)
})
