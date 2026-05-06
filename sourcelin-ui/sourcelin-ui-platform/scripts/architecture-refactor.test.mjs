import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import os from 'node:os'
import path from 'node:path'
import { pathToFileURL } from 'node:url'

import { scanProject } from './style-guard.mjs'

async function testStyleGuardCoversModulesAndShared() {
  const tempRoot = await fs.mkdtemp(path.join(os.tmpdir(), 'sourcelin-style-guard-'))

  try {
    await fs.mkdir(path.join(tempRoot, 'src/modules/demo/pages'), { recursive: true })
    await fs.mkdir(path.join(tempRoot, 'src/shared/components/ui'), { recursive: true })

    await fs.writeFile(
      path.join(tempRoot, 'src/modules/demo/pages/DemoPage.vue'),
      `<template><n-card /></template>\n<style scoped lang="scss">\n.demo { color: #fff; }\n</style>\n`,
      'utf8'
    )

    await fs.writeFile(
      path.join(tempRoot, 'src/shared/components/ui/DemoGlass.vue'),
      `<template><div /></template>\n<style scoped lang="scss">\n.demo { @include liquid-glass-effect('lite'); }\n</style>\n`,
      'utf8'
    )

    const violations = await scanProject(tempRoot)
    const moduleViolations = violations.filter((item) => item.file.includes('src/modules/demo/pages/DemoPage.vue'))
    const sharedUiViolations = violations.filter((item) => item.file.includes('src/shared/components/ui/DemoGlass.vue'))

    assert.equal(moduleViolations.some((item) => item.rule === 'naive-tag'), true, 'style guard should scan modules pages')
    assert.equal(sharedUiViolations.length, 0, 'style guard should allow glass mixins inside shared ui abstraction')
  } finally {
    await fs.rm(tempRoot, { recursive: true, force: true })
  }
}

async function testRouteMetaBuildsNavigation() {
  const routeMetaModuleUrl = pathToFileURL(path.resolve('src/app/router/route-meta.ts')).href
  const routeMetaModule = await import(routeMetaModuleUrl)

  assert.equal(typeof routeMetaModule.collectHeaderNavigation, 'function', 'route meta helper should export collectHeaderNavigation')
  assert.equal(typeof routeMetaModule.collectMobileNavigation, 'function', 'route meta helper should export collectMobileNavigation')
  assert.equal(typeof routeMetaModule.collectFooterNavigation, 'function', 'route meta helper should export collectFooterNavigation')

  const sampleRoutes = [
    {
      path: '/',
      meta: { title: '首页', nav: 'header', order: 1, icon: 'home' }
    },
    {
      path: '/archive',
      meta: { title: '文章', nav: 'header', order: 2 },
      children: [
        {
          path: '/archive',
          meta: { title: '文章归档', nav: 'header-child', parent: '/archive', order: 1 }
        },
        {
          path: '/categories',
          meta: { title: '文章分类', nav: 'header-child', parent: '/archive', order: 2 }
        }
      ]
    },
    {
      path: '/treehole',
      meta: { title: '树洞', nav: 'mobile', icon: 'comment', order: 2 }
    },
    {
      path: '/about',
      meta: { title: '关于', nav: 'footer', order: 1 }
    }
  ]

  const headerNavigation = routeMetaModule.collectHeaderNavigation(sampleRoutes)
  const mobileNavigation = routeMetaModule.collectMobileNavigation(sampleRoutes)
  const footerNavigation = routeMetaModule.collectFooterNavigation(sampleRoutes)

  assert.equal(headerNavigation.length, 2, 'header navigation should include top-level header routes')
  assert.deepEqual(headerNavigation[1].children?.map((item) => item.path), ['/archive', '/categories'])
  assert.deepEqual(mobileNavigation, [{ label: '树洞', path: '/treehole', icon: 'comment' }])
  assert.deepEqual(footerNavigation, [{ label: '关于', path: '/about' }])
}

async function testSkeletonRolloutContracts() {
  const checks = [
    {
      file: 'src/modules/home/components/HomeArticlesSection.vue',
      includes: ['SkeletonArticleCard', 'aria-busy', 'WaterfallList'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/shared/components/layout/WaterfallList.vue',
      includes: ['SkeletonArticleCard', 'loading-skeleton', 'aria-busy'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/modules/home/components/HomeSiteStats.vue',
      includes: ['SkeletonStatGrid', 'aria-busy'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/modules/navigation/pages/NavigationPage.vue',
      includes: ['SkeletonNavCardGrid', 'aria-busy'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/modules/treehole/pages/TreeholePage.vue',
      includes: ['SkeletonTreeholeList', 'aria-busy'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/shared/components/feedback/SearchDialog.vue',
      includes: ['SkeletonSearchResults', 'aria-busy'],
      excludes: ['<div class="loading-spinner" />']
    },
    {
      file: 'src/modules/user/components/UserArticleList.vue',
      includes: ['SkeletonArticleCard', 'aria-busy'],
      excludes: ['<SSpin']
    },
    {
      file: 'src/modules/user/components/UserCollectList.vue',
      includes: ['SkeletonArticleCard', 'aria-busy'],
      excludes: ['<SSpin']
    },
    {
      file: 'src/modules/user/components/UserFollowList.vue',
      includes: ['SkeletonUserList', 'aria-busy'],
      excludes: ['<SSpin']
    },
    {
      file: 'src/modules/user/components/UserFanList.vue',
      includes: ['SkeletonUserList', 'aria-busy'],
      excludes: ['<SSpin']
    },
    {
      file: 'docs/frontend-architecture.md',
      includes: ['加载态规范', 'SSkeleton'],
      excludes: []
    },
    {
      file: 'rules/frontend-platform.md',
      includes: ['加载态', 'SSkeleton'],
      excludes: []
    }
  ]

  for (const check of checks) {
    const source = await fs.readFile(path.resolve(check.file), 'utf8')

    for (const expected of check.includes) {
      assert.equal(
        source.includes(expected),
        true,
        `${check.file} should include "${expected}" to satisfy skeleton rollout contract`
      )
    }

    for (const unexpected of check.excludes) {
      assert.equal(
        source.includes(unexpected),
        false,
        `${check.file} should not include "${unexpected}" after skeleton rollout`
      )
    }
  }
}

async function testMastheadNamingContracts() {
  const checks = [
    {
      file: 'src/modules/home/pages/HomePage.vue',
      includes: ['home-masthead', 'home-masthead__backdrop', 'home-masthead__content'],
      excludes: ['hero-section', 'hero-background', 'hero-content', 'hero-overlay']
    },
    {
      file: 'src/modules/hot/pages/HotPage.vue',
      includes: ['hot-masthead', 'hot-masthead__main', 'hot-masthead__summary'],
      excludes: ['hot-hero', 'hot-hero__main', 'hot-hero__summary']
    }
  ]

  for (const check of checks) {
    const source = await fs.readFile(path.resolve(check.file), 'utf8')

    for (const expected of check.includes) {
      assert.equal(
        source.includes(expected),
        true,
        `${check.file} should include "${expected}" to satisfy masthead naming contract`
      )
    }

    for (const unexpected of check.excludes) {
      assert.equal(
        source.includes(unexpected),
        false,
        `${check.file} should not include "${unexpected}" after masthead naming rollout`
      )
    }
  }
}

async function testLayeringDocsContracts() {
  const checks = [
    {
      file: 'AGENTS.md',
      includes: ['src/shared/components/business/**', 'SSurfacePanel', 'FeatureCard', 'layering:guard']
    },
    {
      file: 'docs/frontend-architecture.md',
      includes: ['src/shared/components/business/**', 'SSurfacePanel', 'FeatureCard', 'layering:guard']
    }
  ]

  for (const check of checks) {
    const source = await fs.readFile(path.resolve(check.file), 'utf8')

    for (const expected of check.includes) {
      assert.equal(
        source.includes(expected),
        true,
        `${check.file} should include "${expected}" to satisfy layering governance contract`
      )
    }
  }
}

async function testAuthSceneAllowsScrollOnConstrainedScreens() {
  const source = await fs.readFile(
    path.resolve('src/modules/auth/components/AuthScene.vue'),
    'utf8'
  )

  assert.equal(
    source.includes('overflow-y: auto;'),
    true,
    'AuthScene should allow vertical scrolling so long register forms remain reachable on laptop-height screens'
  )

  assert.equal(
    source.includes('justify-content: flex-start;'),
    true,
    'AuthScene should top-align the auth panel on constrained screens instead of vertically centering it'
  )
}

async function testCommentPagingStateSeparatesTargetsAndModes() {
  const moduleUrl = pathToFileURL(path.resolve('src/shared/composables/commentPagingState.ts')).href
  const { createCommentPagingStore } = await import(moduleUrl)

  const store = createCommentPagingStore({
    defaultPageSize: 10,
    pageSizeByMode: {
      preview: 2,
      full: 10
    }
  })

  assert.deepEqual(store.getState(101, 'preview'), { page: 1, pageSize: 2 })
  assert.deepEqual(store.getState(101, 'full'), { page: 1, pageSize: 10 })

  store.updateState(101, 'preview', { page: 3 })
  store.updateState(101, 'full', { page: 4 })
  store.updateState(202, 'full', { page: 2 })

  assert.deepEqual(store.getState(101, 'preview'), { page: 3, pageSize: 2 })
  assert.deepEqual(store.getState(101, 'full'), { page: 4, pageSize: 10 })
  assert.deepEqual(store.getState(202, 'full'), { page: 2, pageSize: 10 })

  store.resetMode(101, 'preview')
  assert.deepEqual(store.getState(101, 'preview'), { page: 1, pageSize: 2 })
  assert.deepEqual(
    store.getState(101, 'full'),
    { page: 4, pageSize: 10 },
    'resetting preview mode should not erase the full-mode page state'
  )
}

async function testUseContentCommentsDefinesCancelReplyBeforeImmediateWatch() {
  const source = await fs.readFile(
    path.resolve('src/shared/composables/useContentComments.ts'),
    'utf8'
  )

  const cancelReplyIndex = source.indexOf('function cancelReply()')
  const immediateWatchIndex = source.indexOf('watch(\n    [options.targetId, commentMode],')

  assert.notEqual(cancelReplyIndex, -1, 'useContentComments should define cancelReply')
  assert.notEqual(immediateWatchIndex, -1, 'useContentComments should keep the immediate target/mode watch')
  assert.equal(
    cancelReplyIndex < immediateWatchIndex,
    true,
    'useContentComments should define cancelReply before the immediate target/mode watch to avoid TDZ runtime crashes'
  )
}

async function testCommentThreadUsesNaivePaginationItemCountContract() {
  const source = await fs.readFile(
    path.resolve('src/shared/components/comments/CommentThread.vue'),
    'utf8'
  )

  assert.equal(
    source.includes(':item-count="commentTotal"'),
    true,
    'CommentThread should pass item-count to SPagination so Naive pagination can compute page items correctly'
  )
  assert.equal(
    source.includes(':total="commentTotal"'),
    false,
    'CommentThread should not pass total to SPagination because NPagination uses item-count/page-count'
  )
}

async function testSayPageSeparatesImageViewingFromPublishOrdering() {
  const source = await fs.readFile(
    path.resolve('src/modules/say/pages/SayPage.vue'),
    'utf8'
  )

  assert.equal(
    source.includes('拖拽调整顺序，第一张将成为封面'),
    false,
    'SayPage image viewer modal should not reuse publish-ordering copy'
  )
  assert.equal(
    source.includes('sort-hint="拖拽排序，调整说说配图顺序"'),
    true,
    'SayPage should pass image ordering guidance into SUpload instead of rendering a dedicated page-level ordering area'
  )
  assert.equal(
    source.includes('title="查看图片"'),
    true,
    'SayPage should expose a dedicated image viewer modal'
  )
  assert.equal(
    source.includes('say-compose-panel__order-manager'),
    false,
    'SayPage should not keep a standalone order manager once upload sorting is promoted into SUpload'
  )
  assert.equal(
    source.includes('sortable'),
    true,
    'SayPage should enable sortable image-card uploads through SUpload'
  )
}

async function testSUploadOwnsImageCardSortingCapability() {
  const source = await fs.readFile(
    path.resolve('src/shared/components/ui/SUpload.vue'),
    'utf8'
  )

  assert.equal(
    source.includes('sortable?: boolean'),
    true,
    'SUpload should expose sortable as a UI-layer capability instead of forcing pages to build standalone ordering shells'
  )
  assert.equal(
    source.includes("sortMode?: 'image-card'"),
    true,
    'SUpload should scope sorting capability through an explicit image-card sort mode'
  )
  assert.equal(
    source.includes("sortHint?: string"),
    true,
    'SUpload should own the sorting hint surface when sortable image-card uploads are enabled'
  )
  assert.equal(
    source.includes("emit('reorder'"),
    true,
    'SUpload should emit reorder events after drag sorting so business pages can observe ordering changes without owning drag state'
  )
}

async function testSSurfaceChipOwnsSizeAndVariantSemantics() {
  const chipSource = await fs.readFile(
    path.resolve('src/shared/components/ui/SSurfaceChip.vue'),
    'utf8'
  )
  const saySource = await fs.readFile(
    path.resolve('src/modules/say/pages/SayPage.vue'),
    'utf8'
  )
  const treeholeSource = await fs.readFile(
    path.resolve('src/modules/treehole/pages/TreeholePage.vue'),
    'utf8'
  )

  assert.equal(
    chipSource.includes("size?: 'xs' | 'sm' | 'md'"),
    true,
    'SSurfaceChip should expose explicit size semantics instead of forcing pages to hand-author chip geometry'
  )
  assert.equal(
    chipSource.includes("variant?: 'label' | 'counter' | 'badge' | 'button'"),
    true,
    'SSurfaceChip should expose explicit variant semantics for labels, counters, badges, and button chips'
  )
  assert.equal(
    saySource.includes('size="xs" variant="label"'),
    true,
    'SayPage should use semantic label chips for compose-section kickers'
  )
  assert.equal(
    saySource.includes('size="sm" variant="counter"'),
    true,
    'SayPage should use semantic counter chips for media and preview counts'
  )
  assert.equal(
    treeholeSource.includes('size="xs" variant="label"'),
    true,
    'TreeholePage should use semantic label chips for its composer kicker'
  )
  assert.equal(
    treeholeSource.includes('size="sm" variant="badge"'),
    true,
    'TreeholePage should use semantic badge chips for static status labels'
  )
  assert.equal(
    treeholeSource.includes('size="sm" variant="button"'),
    true,
    'TreeholePage should use semantic button chips for interactive actions'
  )
}

async function testPlatformDisablesReducedMotionBranches() {
  const source = await fs.readFile(
    path.resolve('src/modules/treehole/components/MessageBarrage.vue'),
    'utf8'
  )
  const marqueeSource = await fs.readFile(
    path.resolve('src/modules/home/components/HomeMarquee.vue'),
    'utf8'
  )
  const linksSource = await fs.readFile(
    path.resolve('src/modules/navigation/pages/LinksPage.vue'),
    'utf8'
  )
  const panelSwapSource = await fs.readFile(
    path.resolve('src/shared/composables/usePanelSwapTransition.ts'),
    'utf8'
  )

  assert.equal(
    source.includes('prefersReducedMotion'),
    false,
    'MessageBarrage should no longer keep a reduced-motion fallback branch once the platform animation policy is unified'
  )
  assert.equal(
    source.includes("barrage--static"),
    false,
    'MessageBarrage should use a single animated barrage path instead of a static reduced-motion layout'
  )
  assert.equal(
    marqueeSource.includes('prefers-reduced-motion'),
    false,
    'HomeMarquee should not branch on reduced-motion once the platform disables the feature globally'
  )
  assert.equal(
    linksSource.includes('prefers-reduced-motion'),
    false,
    'LinksPage should not branch on reduced-motion for scroll or animation behavior'
  )
  assert.equal(
    panelSwapSource.includes('prefersReducedMotion'),
    false,
    'usePanelSwapTransition should always execute the panel animation path'
  )
}

async function testHomeDiscoverSkeletonUsesWaterfallAlignedLoadingLayout() {
  const homeArticlesSource = await fs.readFile(
    path.resolve('src/modules/home/components/HomeArticlesSection.vue'),
    'utf8'
  )
  const waterfallListSource = await fs.readFile(
    path.resolve('src/shared/components/layout/WaterfallList.vue'),
    'utf8'
  )

  assert.equal(
    homeArticlesSource.includes(':column-width="waterfallColumnWidth"'),
    true,
    'HomeArticlesSection loading branch should pass the same column width into WaterfallList as the real waterfall layout'
  )
  assert.equal(
    homeArticlesSource.includes(':gap="waterfallGap"'),
    true,
    'HomeArticlesSection loading branch should pass the same gap into WaterfallList as the real waterfall layout'
  )
  assert.equal(
    waterfallListSource.includes("loading-state--cards"),
    true,
    'WaterfallList should expose a dedicated card-skeleton loading grid instead of relying on SkeletonArticleCard single-column stack layout'
  )
  assert.equal(
    waterfallListSource.includes(":count=\"1\""),
    true,
    'WaterfallList should render card skeletons as individual items so the loading grid can actually form multiple columns'
  )
}

async function testPlatformContentPaginationContracts() {
  const treeholeComposable = await fs.readFile(
    path.resolve('src/modules/treehole/composables/useTreeholePage.ts'),
    'utf8'
  )
  const treeholePage = await fs.readFile(
    path.resolve('src/modules/treehole/pages/TreeholePage.vue'),
    'utf8'
  )
  const categoryPage = await fs.readFile(
    path.resolve('src/modules/article/pages/CategoryPage.vue'),
    'utf8'
  )
  const tagPage = await fs.readFile(
    path.resolve('src/modules/article/pages/TagPage.vue'),
    'utf8'
  )
  const tagArticleList = await fs.readFile(
    path.resolve('src/modules/article/components/TagArticleList.vue'),
    'utf8'
  )
  const tagPageQuery = await fs.readFile(
    path.resolve('src/modules/article/composables/useTagPageQuery.ts'),
    'utf8'
  )
  const sayPage = await fs.readFile(
    path.resolve('src/modules/say/pages/SayPage.vue'),
    'utf8'
  )
  const hotComposable = await fs.readFile(
    path.resolve('src/modules/hot/composables/useHotPage.ts'),
    'utf8'
  )
  const hotPage = await fs.readFile(
    path.resolve('src/modules/hot/pages/HotPage.vue'),
    'utf8'
  )
  const archivePage = await fs.readFile(
    path.resolve('src/modules/article/pages/ArchivePage.vue'),
    'utf8'
  )

  assert.equal(
    treeholeComposable.includes('const treeholePage = ref(1)'),
    true,
    'useTreeholePage should manage an explicit main-list page state instead of hardcoding page 1'
  )
  assert.equal(
    treeholeComposable.includes('const treeholeTotal = ref(0)'),
    true,
    'useTreeholePage should keep the main-list total count for explicit pagination'
  )
  assert.equal(
    treeholePage.includes('<SPagination'),
    true,
    'TreeholePage should expose explicit pagination for the main treehole list'
  )

  assert.equal(
    categoryPage.includes('<SPagination'),
    true,
    'CategoryPage detail mode should expose explicit article pagination instead of truncating to 12 items'
  )
  assert.equal(
    categoryPage.includes('page: articlePage.value'),
    true,
    'CategoryPage should request category articles with the current page state'
  )

  assert.equal(
    tagPage.includes('<SPagination') || tagArticleList.includes('<SPagination'),
    true,
    'TagPage detail mode should expose explicit article pagination instead of truncating to 12 items'
  )
  assert.equal(
    tagPage.includes('page: articlePage.value') || tagPageQuery.includes('page: articlePage.value'),
    true,
    'TagPage should request tag articles with the current page state'
  )

  assert.equal(
    sayPage.includes('const autoLoadUntilPage = 3'),
    true,
    'SayPage should cap automatic feed loading after the first few pages and transition into manual continuation'
  )
  assert.equal(
    sayPage.includes('const feedLoadMode = ref<\'auto\' | \'manual\'>'),
    true,
    'SayPage should model automatic and manual feed loading explicitly'
  )
  assert.equal(
    sayPage.includes('继续查看更早动态'),
    true,
    'SayPage should offer a manual continuation action after automatic feed loading stops'
  )

  assert.equal(
    hotComposable.includes('const hotTotal = ref(0)'),
    true,
    'useHotPage should keep total count so the hot list can expose progress feedback'
  )
  assert.equal(
    hotPage.includes('hot-pagination-status'),
    true,
    'HotPage should expose a weak pagination status summary for the progressive hot list'
  )

  assert.equal(
    archivePage.includes('pageSize: 1000'),
    false,
    'ArchivePage should not request an effectively unbounded first page for archive content'
  )
  assert.equal(
    archivePage.includes('const archiveLoadMore'),
    true,
    'ArchivePage should expose segmented archive loading instead of one-shot fetching all rows'
  )
  assert.equal(
    archivePage.includes('const archiveAnchorItems = computed'),
    true,
    'ArchivePage should derive archive anchors from loaded groups so time navigation remains available with segmented loading'
  )
}

async function main() {
  await testStyleGuardCoversModulesAndShared()
  await testRouteMetaBuildsNavigation()
  await testSkeletonRolloutContracts()
  await testMastheadNamingContracts()
  await testLayeringDocsContracts()
  await testAuthSceneAllowsScrollOnConstrainedScreens()
  await testCommentPagingStateSeparatesTargetsAndModes()
  await testUseContentCommentsDefinesCancelReplyBeforeImmediateWatch()
  await testCommentThreadUsesNaivePaginationItemCountContract()
  await testSayPageSeparatesImageViewingFromPublishOrdering()
  await testSUploadOwnsImageCardSortingCapability()
  await testSSurfaceChipOwnsSizeAndVariantSemantics()
  await testPlatformDisablesReducedMotionBranches()
  await testHomeDiscoverSkeletonUsesWaterfallAlignedLoadingLayout()
  await testPlatformContentPaginationContracts()
  console.log('architecture-refactor tests passed')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
