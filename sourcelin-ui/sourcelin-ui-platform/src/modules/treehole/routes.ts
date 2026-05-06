import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const treeholeRoutes: AppRouteRecordRaw[] = [
  {
    path: '/treehole',
    name: 'Treehole',
    component: () => import('./pages/TreeholePage.vue'),
    meta: { title: '树洞', icon: 'chatbubbles', nav: 'header', mobile: true, order: 6 }
  }
]

