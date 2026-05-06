import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const navigationRoutes: AppRouteRecordRaw[] = [
  {
    path: '/navigation',
    name: 'Navigation',
    component: () => import('./pages/NavigationPage.vue'),
    meta: { title: '网站导航', icon: 'navigation', nav: 'header-child', parent: '/about', mobile: true, footer: true, order: 61 }
  },
  {
    path: '/links',
    name: 'Links',
    component: () => import('./pages/LinksPage.vue'),
    meta: { title: '友情链接', icon: 'link', nav: 'header-child', parent: '/about', mobile: true, order: 62 }
  }
]

