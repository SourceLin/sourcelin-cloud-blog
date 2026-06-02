export type MessageChannel = 'system' | 'interaction' | 'star' | 'follow';

export interface MessageItem {
  id: number;
  title: string;
  content?: string;
  /** 消息所属频道（后端暂未下发时前端默认归为 system） */
  channel?: MessageChannel;
  publishTime?: string;
  expireTime?: string;
  createTime?: string;
  isRead?: number;
}

export interface MessageUnreadStat {
  total: number;
}

/** 按频道分组的未读计数 */
export interface ChannelUnreadCount {
  channel: MessageChannel;
  count: number;
}
