import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'
import type { ArticleListItem } from '@/modules/article/api/article.api'

export interface TagOption {
  id: number
  name: string
  summary?: string
  icon?: string
  clickCount?: number
  orderNum?: number
}

export interface TagArticleQuery {
  page?: number
  pageSize?: number
}

export function getTags() {
  return requestData<{ items: TagOption[] }>({
    url: '/front/tags',
    method: 'get'
  }).then((result) => result.items)
}

export function getTagDetail(id: number) {
  return requestData<TagOption>({
    url: `/front/tags/${id}`,
    method: 'get'
  })
}

export function getTagArticles(id: number, params: TagArticleQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: `/front/tags/articles/${id}`,
    method: 'get',
    params
  })
}
