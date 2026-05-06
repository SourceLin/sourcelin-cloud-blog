import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const aboutRoutes: AppRouteRecordRaw[] = [
  {
    path: '/about',
    name: 'About',
    component: () => import('./pages/AboutPage.vue'),
    meta: { title: '关于', childTitle: '关于本站', icon: 'information', nav: 'header', mobile: true, footer: true, order: 6 }
  },
  {
    path: '/user-agreement',
    name: 'UserAgreement',
    component: () => import('./pages/UserAgreementPage.vue'),
    meta: { title: '用户协议', hidden: true }
  },
  {
    path: '/privacy-policy',
    name: 'PrivacyPolicy',
    component: () => import('./pages/PrivacyPolicyPage.vue'),
    meta: { title: '隐私政策', hidden: true }
  }
]

