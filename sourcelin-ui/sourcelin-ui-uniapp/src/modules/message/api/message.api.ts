import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { MessageItem, MessageUnreadStat } from '../types/message';

export function fetchMessagePage(page = 1, pageSize = 10): Promise<PageResult<MessageItem>> {
  return http.get<PageResult<MessageItem>>('/front/messages', { page, pageSize });
}

export function fetchMessageDetail(id: number): Promise<MessageItem> {
  return http.get<MessageItem>(`/front/messages/${id}`);
}

export function markMessageRead(id: number): Promise<boolean> {
  return http.post<boolean>(`/front/messages/${id}/read`);
}

export function markAllMessagesRead(): Promise<boolean> {
  return http.post<boolean>('/front/messages/read-all');
}

export function fetchUnreadMessageCount(): Promise<MessageUnreadStat> {
  return http.get<MessageUnreadStat>('/front/messages/unread-count');
}
