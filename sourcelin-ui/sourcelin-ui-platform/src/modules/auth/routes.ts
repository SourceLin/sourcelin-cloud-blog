import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const authRoutes: AppRouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('./pages/LoginPage.vue'),
    meta: { title: '登录', hidden: true, shell: 'auth' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('./pages/RegisterPage.vue'),
    meta: { title: '注册', hidden: true, shell: 'auth' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('./pages/ForgotPasswordPage.vue'),
    meta: { title: '忘记密码', hidden: true, shell: 'auth' }
  }
]
