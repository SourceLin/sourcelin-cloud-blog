import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export interface SearchArticle {
  id: number
  title: string
  summary: string
  coverImage: string
  viewCount: number
  createTime: string
}

export interface SearchTag {
  id: number
  name: string
  count: number
}

export interface SearchCategory {
  id: number
  name: string
  count: number
}

export function searchArticles(params: { keyword: string; page?: number; pageSize?: number }) {
  return requestData<PageResult<SearchArticle>>({
    url: '/front/articles/search',
    method: 'get',
    params
  })
}

export function searchTags(params?: { keyword?: string; pageSize?: number }) {
  return requestData<PageResult<SearchTag>>({
    url: '/front/tags/search',
    method: 'get',
    params
  })
}

export function searchCategories(params?: { keyword?: string; pageSize?: number }) {
  return requestData<PageResult<SearchCategory>>({
    url: '/front/categories/search',
    method: 'get',
    params
  })
}

export function getHotSearches() {
  return requestData<{ items: string[] }>({
    url: '/front/search/hot',
    method: 'get'
  }).then((result) => result.items)
}

export function getSearchSuggestions(params: { keyword: string }) {
  return requestData<{ items: string[] }>({
    url: '/front/search/suggestions',
    method: 'get',
    params
  }).then((result) => result.items)
}
