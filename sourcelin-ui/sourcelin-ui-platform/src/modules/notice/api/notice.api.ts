import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export interface SystemNoticeItem {
  targetType?: string
  targetId?: number
  commentId?: number
  sourceAvailable?: boolean
  id?: number
  title?: string
  content?: string
  publishTime?: string
  isRead?: number
}

export interface UnreadCountResult {
  total: number
}

export interface MessageQuery {
  page?: number
  pageSize?: number
  channel?: string
}

function channelUrl(channel = 'system') {
  return channel === 'system' ? '/front/messages' : `/front/messages/inbox/${channel}`
}

export function getMessages(query: MessageQuery = {}) {
  return requestData<PageResult<SystemNoticeItem>>({
    url: channelUrl(query.channel),
    method: 'get',
    params: {
      page: query.page ?? 1,
      pageSize: query.pageSize ?? 20,
      channel: query.channel ?? 'system'
    }
  }).then((result) => result.items)
}

export function getMessageDetail(id: number, channel = 'system') {
  return requestData<SystemNoticeItem>({
    url: `${channelUrl(channel)}/${id}`,
    method: 'get'
  })
}

// 兼容现有组合函数内部调用，后续可统一替换为 getMessages/getMessageDetail
export function getNotices() {
  return getMessages({ channel: 'system', page: 1, pageSize: 100 })
}

export function getNoticeDetail(id: number) {
  return getMessageDetail(id)
}

export function readMessage(id: number, channel = 'system') {
  return requestData<boolean>({
    url: `${channelUrl(channel)}/${id}/read`,
    method: 'post'
  })
}

export function readAllMessages(channel = 'system') {
  return requestData<boolean>({
    url: `${channelUrl(channel)}/read-all`,
    method: 'post'
  })
}

export function getUnreadCount() {
  return requestData<UnreadCountResult>({
    url: '/front/messages/unread-count',
    method: 'get'
  })
}
