import { requestData } from '@/shared/api/http'
import { uploadFile } from '@/shared/api/file.api'
import type { PageResult } from '@/shared/types/api'

export interface SayUser {
  nickname: string
  avatar: string
}

export interface Say {
  id: number
  userId: number
  content: string
  images: string
  imageFileIds: string
  likeCount: number
  collectCount?: number
  commentCount: number
  status: number
  createTime: string
  likedByMe?: boolean
  collectedByMe?: boolean
  isCollected?: boolean
  collectId?: number
  user: SayUser
}

export function getSayList(params?: { page?: number; pageSize?: number }) {
  return requestData<PageResult<Say>>({
    url: '/front/says',
    method: 'get',
    params
  })
}

export function getSayDetail(id: number) {
  return requestData<Say>({
    url: `/front/says/${id}`,
    method: 'get'
  })
}

export function deleteSay(id: number) {
  return requestData<void>({
    url: `/front/says/${id}`,
    method: 'delete'
  })
}

export interface PublishSayParams {
  content: string
  images?: string
  imageFileIds?: string
}

export function publishSay(data: PublishSayParams) {
  return requestData<number>({
    url: '/front/says',
    method: 'post',
    data
  })
}

export interface SayCommentRow {
  id: number
  userId?: number
  nickname?: string
  avatar?: string
  parentCommentId?: number | null
  parentUserId?: number | null
  parentNickname?: string
  content?: string
  createTime?: string
  likeCount?: number
}

export function getSayComments(params: { sayId: number; page?: number; pageSize?: number }) {
  return requestData<PageResult<SayCommentRow>>({
    url: '/front/comments',
    method: 'get',
    params: {
      // TODO: 待重构 — 后端 CommentDTO.articleId 是所有评论类型的通用目标 ID 字段，
      // 当前 say 评论使用 articleId 传递 say ID，语义不精确。需后续与后端统一为 targetId
      articleId: params.sayId,
      source: 'say',
      page: params.page ?? 1,
      pageSize: params.pageSize ?? 50
    }
  })
}

export function addSayComment(data: {
  /** 说说 ID — 对应后端 CommentDTO.articleId 字段（待重构为 targetId） */
  articleId: number
  content: string
  parentCommentId?: number | null
  parentUserId?: number | null
}) {
  return requestData<void>({
    url: '/front/comments',
    method: 'post',
    data: {
      // TODO: 待重构 — articleId 语义应为 targetId，需后续与后端统一
      articleId: data.articleId,
      content: data.content,
      parentCommentId: data.parentCommentId ?? undefined,
      parentUserId: data.parentUserId ?? undefined,
      source: 'say'
    }
  })
}

export function uploadSayImage(data: FormData) {
  return uploadFile(data)
}
