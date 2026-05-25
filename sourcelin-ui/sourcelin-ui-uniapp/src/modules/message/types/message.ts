export interface MessageItem {
  id: number;
  title: string;
  content?: string;
  publishTime?: string;
  expireTime?: string;
  createTime?: string;
  isRead?: number;
}

export interface MessageUnreadStat {
  total: number;
}
