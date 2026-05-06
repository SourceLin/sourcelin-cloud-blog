import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'
import { collectTarget, uncollectTarget } from '@/shared/api/interaction.api'

export interface Treehole {
  id: number
  userId?: number | null
  userName: string
  avatar: string
  content: string
  likeCount: number
  collectCount?: number
  commentCount: number
  likedByMe?: boolean
  collectedByMe?: boolean
  createTime: string
}

export function getTreeholes(params?: { page?: number; pageSize?: number }) {
  return requestData<PageResult<Treehole>>({
    url: '/front/treeholes',
    method: 'get',
    params
  })
}

export interface TreeholeListQuery {
  page?: number
  pageSize?: number
}

export function getTreehole(params?: TreeholeListQuery) {
  return requestData<PageResult<Treehole>>({
    url: '/front/treehole',
    method: 'get',
    params
  })
}

export function getTreeholeDetail(id: number) {
  return requestData<Treehole>({
    url: `/front/treeholes/${id}`,
    method: 'get'
  })
}

export function createTreehole(data: { content: string }) {
  return requestData<number>({
    url: '/front/treeholes',
    method: 'post',
    data,
    skipErrorMessage: true
  })
}

export function collectTreehole(id: number) {
  return collectTarget('treehole', id)
}

export function uncollectTreehole(id: number) {
  return uncollectTarget('treehole', id)
}

export function deleteTreehole(id: number) {
  return requestData<void>({
    url: `/front/treeholes/${id}`,
    method: 'delete'
  })
}
