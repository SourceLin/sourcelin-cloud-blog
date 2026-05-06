import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export interface HotArticle {
  id: number
  title: string
  viewCount: number
  likeCount: number
  commentCount: number
  createTime: string
  articleId?: number
  coverImage?: string
  avatar?: string
}

export interface HotCategory {
  id: number
  name: string
  count: number
}

export interface HotSearch {
  id: number
  keyword: string
  count: number
}

export function getHotSearches() {
  return requestData<{ items: HotSearch[] }>({
    url: '/front/hot/searches',
    method: 'get'
  }).then((result) => result.items)
}

export function getHotCategories() {
  return requestData<{ items: HotCategory[] }>({
    url: '/front/hot/categories',
    method: 'get'
  }).then((result) => result.items)
}

export function getHotArticles(params?: { page?: number; pageSize?: number; categoryId?: number }) {
  return requestData<PageResult<HotArticle>>({
    url: '/front/hot/articles',
    method: 'get',
    params
  })
}
