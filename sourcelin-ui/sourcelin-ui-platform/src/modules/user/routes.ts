import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const userRoutes: AppRouteRecordRaw[] = [
  {
    path: '/user',
    name: 'User',
    component: () => import('./pages/UserPage.vue'),
    meta: { title: '个人主页', hidden: true }
  }
]

