import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const sayRoutes: AppRouteRecordRaw[] = [
  {
    path: '/say',
    name: 'Say',
    component: () => import('./pages/SayPage.vue'),
    meta: { title: '说说', icon: 'sparkles', nav: 'header', mobile: true, order: 4 }
  }
]

