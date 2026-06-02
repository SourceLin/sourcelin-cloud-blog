import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { MessageChannel, MessageItem, MessageUnreadStat } from '../types/message';

export interface MessagePageQuery {
  page?: number;
  pageSize?: number;
  channel?: MessageChannel;
}

export function fetchMessagePage(query: MessagePageQuery = {}): Promise<PageResult<MessageItem>> {
  const { page = 1, pageSize = 10, channel } = query;
  const params: Record<string, unknown> = { page, pageSize };
  if (channel) {
    params.channel = channel;
  }
  return http.get<PageResult<MessageItem>>('/front/messages', params);
}

export function fetchMessageDetail(id: number): Promise<MessageItem> {
  return http.get<MessageItem>(`/front/messages/${id}`);
}

export function markMessageRead(id: number): Promise<boolean> {
  return http.post<boolean>(`/front/messages/${id}/read`);
}

export function markAllMessagesRead(channel?: MessageChannel): Promise<boolean> {
  const params: Record<string, unknown> = {};
  if (channel) {
    params.channel = channel;
  }
  return http.post<boolean>('/front/messages/read-all', params);
}

export function fetchUnreadMessageCount(): Promise<MessageUnreadStat> {
  return http.get<MessageUnreadStat>('/front/messages/unread-count');
}
