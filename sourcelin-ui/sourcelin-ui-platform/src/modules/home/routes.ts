import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const homeRoutes: AppRouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('./pages/HomePage.vue'),
    meta: { title: '首页', icon: 'home', nav: 'header', mobile: true, footer: true, order: 1 }
  }
]

