import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

/** 收藏目标嵌套实体 — 文章 */
export interface CollectArticleVO {
  id?: number
  title?: string
  summary?: string
  avatar?: string
  createTime?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
}

/** 收藏目标嵌套实体 — 说说 */
export interface CollectSayVO {
  id?: number
  content?: string
  images?: string
  imageFileIds?: string
  createTime?: string
  likeCount?: number
  commentCount?: number
}

/** 收藏目标嵌套实体 — 树洞 */
export interface CollectTreeholeVO {
  id?: number
  content?: string
  createTime?: string
  likeCount?: number
  commentCount?: number
  collectCount?: number
}

export interface Collect {
  id?: number
  userId?: number
  targetType?: 'article' | 'say' | 'treehole'
  targetId?: number
  active?: boolean
  createTime?: string
  article?: CollectArticleVO | null
  say?: CollectSayVO | null
  treehole?: CollectTreeholeVO | null
}

export interface CollectListQuery {
  page?: number
  pageSize?: number
  type?: 'all' | 'article' | 'say' | 'treehole'
  state?: 'all' | 'active' | 'inactive'
}

export function getCollects(params?: CollectListQuery) {
  return requestData<PageResult<Collect>>({
    url: '/front/interactions/collects',
    method: 'get',
    params
  })
}
