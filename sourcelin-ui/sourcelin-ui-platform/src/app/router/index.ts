import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { aboutRoutes } from '@/modules/about/routes'
import { articleRoutes } from '@/modules/article/routes'
import { authRoutes } from '@/modules/auth/routes'
import { homeRoutes } from '@/modules/home/routes'
import { hotRoutes } from '@/modules/hot/routes'
import { navigationRoutes } from '@/modules/navigation/routes'
import { noticeRoutes } from '@/modules/notice/routes'
import { sayRoutes } from '@/modules/say/routes'
import { treeholeRoutes } from '@/modules/treehole/routes'
import { userRoutes } from '@/modules/user/routes'
import { setupRouterNavigation, type AppRouteRecordRaw } from './route-meta'
import { resolveAppScrollPosition } from './scroll-behavior'
import { useUiStore } from '@/stores/ui.store'
import { useSiteStore } from '@/stores/site.store'

export const appRoutes: AppRouteRecordRaw[] = [
  ...homeRoutes,
  ...hotRoutes,
  ...articleRoutes,
  ...sayRoutes,
  ...treeholeRoutes,
  ...aboutRoutes,
  ...navigationRoutes,
  ...authRoutes,
  ...userRoutes,
  ...noticeRoutes,
  {
    path: '/redirect/:path(.*)',
    component: () => import('./RedirectPage.vue'),
    name: 'Redirect',
    meta: { hidden: true }
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('./NotFoundPage.vue'),
    name: 'NotFound',
    meta: { hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: resolveAppScrollPosition,
  routes: appRoutes as RouteRecordRaw[]
})

setupRouterNavigation(router)

let startupRouteReadyMarked = false

router.afterEach((to) => {
  // 动态更新页面标题：使用 seoTitle 或降级为 title
  const siteStore = useSiteStore()
  const siteName = siteStore.siteInfo.webName || '圆圈博客'
  const pageTitle = (to.meta?.seoTitle as string | undefined) || (to.meta?.title as string | undefined)
  if (pageTitle) {
    document.title = `${pageTitle} - ${siteName}`
  } else {
    document.title = siteName
  }

  if (startupRouteReadyMarked) {
    return
  }

  startupRouteReadyMarked = true
  useUiStore().markStartupRouteReady()
})

export default router

