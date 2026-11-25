import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { MessageChannel, MessageItem, MessageUnreadStat } from '../types/message';

const PUBLIC_SYSTEM_MESSAGE_OPTIONS = {
  skipAuthRedirect: true,
  skipErrorToast: true
} as const;

function channelUrl(channel: MessageChannel = 'system'): string {
  return channel === 'system' ? '/front/messages' : `/front/messages/inbox/${channel}`;
}

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
  return http.get<PageResult<MessageItem>>(
    channelUrl(channel),
    params,
    channel === 'system' ? PUBLIC_SYSTEM_MESSAGE_OPTIONS : undefined
  );
}

export function fetchMessageDetail(id: number, channel: MessageChannel = 'system'): Promise<MessageItem> {
  return http.get<MessageItem>(`${channelUrl(channel)}/${id}`);
}

export function markMessageRead(id: number, channel: MessageChannel = 'system'): Promise<boolean> {
  return http.post<boolean>(`${channelUrl(channel)}/${id}/read`);
}

export function markAllMessagesRead(channel?: MessageChannel): Promise<boolean> {
  const params: Record<string, unknown> = {};
  if (channel) {
    params.channel = channel;
  }
  return http.post<boolean>(`${channelUrl(channel)}/read-all`, params);
}

export function fetchUnreadMessageCount(): Promise<MessageUnreadStat> {
  return http.get<MessageUnreadStat>('/front/messages/unread-count', undefined, PUBLIC_SYSTEM_MESSAGE_OPTIONS);
}
