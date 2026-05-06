import { requestData } from '@/shared/api/http'
import type { ListResult } from '@/shared/types/api'

/** 与后台 FriendLinkPublicVO 字段对齐 */
export interface FriendLinkPublicRow {
  id: number
  name: string
  url: string
  avatar?: string
  description?: string
}

export function getLinks() {
  return requestData<ListResult<FriendLinkPublicRow>>({
    url: '/front/links',
    method: 'get'
  }).then((result) => result.items)
}

export interface FriendLinkApplyPayload {
  name: string
  url: string
  email: string
  description?: string
  avatar?: string
}

/** 匿名友链申请（需网关放行 POST /front/links/apply） */
export function applyFriendLink(data: FriendLinkApplyPayload) {
  return requestData<void>({
    url: '/front/links/apply',
    method: 'post',
    data,
    headers: { isToken: false }
  })
}
