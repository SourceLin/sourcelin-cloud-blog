import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const hotRoutes: AppRouteRecordRaw[] = [
  {
    path: '/hot',
    name: 'Hot',
    component: () => import('./pages/HotPage.vue'),
    meta: { title: '热门', icon: 'sparkles', nav: 'header', mobile: true, order: 2 }
  }
]

