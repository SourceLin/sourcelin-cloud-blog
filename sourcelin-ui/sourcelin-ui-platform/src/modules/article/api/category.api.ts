import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'
import type { ArticleListItem } from '@/modules/article/api/article.api'

export interface CategoryOption {
  id: number
  name: string
  icon?: string
  summary?: string
  articleCount?: number
}

export interface CategoryArticleQuery {
  page?: number
  pageSize?: number
}

export function getCategories() {
  return requestData<{ items: CategoryOption[] }>({
    url: '/front/categories',
    method: 'get'
  }).then((result) => result.items)
}

export function getCategoryDetail(id: number) {
  return requestData<CategoryOption>({
    url: `/front/categories/${id}`,
    method: 'get'
  })
}

export function getCategoryArticles(id: number, params: CategoryArticleQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: `/front/categories/articles/${id}`,
    method: 'get',
    params
  })
}
