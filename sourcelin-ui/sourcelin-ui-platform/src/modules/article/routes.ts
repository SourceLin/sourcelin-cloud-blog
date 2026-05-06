import type { AppRouteRecordRaw } from '@/app/router/route-meta'

export const articleRoutes: AppRouteRecordRaw[] = [
  {
    path: '/archive',
    name: 'Archive',
    component: () => import('./pages/ArchivePage.vue'),
    meta: { title: '文章', childTitle: '文章归档', icon: 'archive', nav: 'header', mobile: true, footer: true, order: 3 }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('./pages/ArticlePage.vue'),
    meta: { title: '文章详情', hidden: true }
  },
  {
    path: '/categories',
    name: 'Category',
    component: () => import('./pages/CategoryPage.vue'),
    meta: { title: '文章分类', icon: 'category', nav: 'header-child', parent: '/archive', mobile: true, order: 31 }
  },
  {
    path: '/category',
    redirect: '/categories',
    meta: { hidden: true }
  },
  {
    path: '/tags',
    name: 'Tag',
    component: () => import('./pages/TagPage.vue'),
    meta: { title: '文章标签', icon: 'tag', nav: 'header-child', parent: '/archive', mobile: true, order: 32 }
  },
  {
    path: '/tag',
    redirect: '/tags',
    meta: { hidden: true }
  },
  {
    path: '/posts/new',
    name: 'NewPost',
    component: () => import('./pages/NewPostPage.vue'),
    meta: { title: '发布文章', hidden: true }
  },
  {
    path: '/newposts',
    redirect: '/posts/new',
    meta: { hidden: true }
  }
]
