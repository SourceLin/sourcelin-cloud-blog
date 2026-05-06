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

router.afterEach(() => {
  if (startupRouteReadyMarked) {
    return
  }

  startupRouteReadyMarked = true
  useUiStore().markStartupRouteReady()
})

export default router

