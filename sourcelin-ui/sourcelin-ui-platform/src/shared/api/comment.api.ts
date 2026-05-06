import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export interface CommentQueryParams {
  articleId: number
  source?: string
  page?: number
  pageSize?: number
}

export interface CommentCreatePayload {
  articleId: number
  source?: string
  content: string
  parentCommentId?: number | null
  parentUserId?: number | null
  userId?: number | null
}

/** 审核状态：0 待审 1 通过 2 未通过 */
export type CommentAuditStatus = 0 | 1 | 2

export interface CommentItem {
  id: number
  articleId?: number
  userId?: number | null
  nickname?: string
  avatar?: string
  parentUserId?: number | null
  parentNickname?: string
  parentCommentId?: number | null
  content?: string
  createTime?: string
  likeCount?: number
  likedByMe?: boolean
  source?: string
  status?: CommentAuditStatus
  moderationDecision?: string
}

export function getComments(params: CommentQueryParams) {
  return requestData<PageResult<CommentItem>>({
    url: '/front/comments',
    method: 'get',
    params
  })
}

export function addComment(data: CommentCreatePayload) {
  return requestData<void>({
    url: '/front/comments',
    method: 'post',
    data
  })
}

export function likeComment(commentId: number) {
  return requestData<void>({
    url: `/front/comments/like/${commentId}`,
    method: 'post'
  })
}

export function deleteComment(commentId: number) {
  return requestData<void>({
    url: `/front/comments/${commentId}`,
    method: 'delete'
  })
}
