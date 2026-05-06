import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'
import type { FrontUserDetailVO } from '@/modules/user/api/user.api'

export interface Follow {
  id?: number
  userId?: number
  followUserId?: number
  targetUserId?: number
  createTime?: string
  user?: FrontUserDetailVO | null
  targetUser?: FrontUserDetailVO | null
}

export interface FollowActionResult {
  followId: number
  followed: boolean
}

export function getFollowing(params?: { page?: number; pageSize?: number }) {
  return requestData<PageResult<Follow>>({
    url: '/front/follows/following',
    method: 'get',
    params
  })
}

export function getFans(params?: { page?: number; pageSize?: number }) {
  return requestData<PageResult<Follow>>({
    url: '/front/follows/fans',
    method: 'get',
    params
  })
}

export function addFollow(data: { targetUserId: number }) {
  return requestData<FollowActionResult>({
    url: '/front/follows',
    method: 'post',
    data: {
      targetUserId: data.targetUserId
    }
  })
}

export function deleteFollow(id: number) {
  return requestData<void>({
    url: `/front/follows/${id}`,
    method: 'delete'
  })
}

export const followUser = addFollow
export const unfollowUser = deleteFollow
