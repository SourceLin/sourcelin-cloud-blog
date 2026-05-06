import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const noticeRoutes: AppRouteRecordRaw[] = [
  {
    path: '/messages',
    name: 'MessageCenter',
    component: () => import('./pages/MessageCenterPage.vue'),
    meta: { title: '消息通知', hidden: true }
  },
  {
    path: '/notice',
    redirect: '/messages',
    meta: { hidden: true }
  }
]
