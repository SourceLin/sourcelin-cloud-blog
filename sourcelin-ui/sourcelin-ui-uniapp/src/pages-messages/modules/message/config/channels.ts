// 消息中心频道配置（与 Web 前台 notice/config/channels.ts 对齐）

/** 消息频道 ID */
export type MessageChannel = 'system' | 'interaction' | 'star' | 'follow';

export const MESSAGE_CHANNEL_IDS: MessageChannel[] = ['system', 'interaction', 'star', 'follow'];

export function isMessageChannel(value: unknown): value is MessageChannel {
  return typeof value === 'string' && MESSAGE_CHANNEL_IDS.includes(value as MessageChannel);
}

/** 频道图标名（对应 uni-icons type） */
export type ChannelIconName = 'notification' | 'chat' | 'star' | 'heart';

export interface ChannelConfig {
  id: MessageChannel;
  name: string;
  icon: ChannelIconName;
  color: string;
  description: string;
  available: boolean;
  emptyTitle: string;
  emptyDescription: string;
}

export const MESSAGE_CHANNELS: ChannelConfig[] = [
  {
    id: 'system',
    name: '系统公告',
    icon: 'notification',
    color: '#3B59FF',
    description: '站务通知与系统公告',
    available: true,
    emptyTitle: '暂无公告',
    emptyDescription: '当前没有新的系统公告或站务通知。'
  },
  {
    id: 'interaction',
    name: '互动消息',
    icon: 'chat',
    color: '#00B42A',
    description: '评论与回复',
    available: true,
    emptyTitle: '互动消息',
    emptyDescription: '收到的评论与回复会显示在这里。'
  },
  {
    id: 'star',
    name: '点赞通知',
    icon: 'star',
    color: '#FFB800',
    description: '点点赞通知动态',
    available: true,
    emptyTitle: '点赞通知',
    emptyDescription: '收到的新点赞会显示在这里。'
  },
  {
    id: 'follow',
    name: '关注通知',
    icon: 'heart',
    color: '#FF5B75',
    description: '新增粉丝',
    available: true,
    emptyTitle: '关注通知',
    emptyDescription: '新粉丝会显示在这里。'
  }
];

export function getChannelConfig(id: MessageChannel): ChannelConfig {
  return MESSAGE_CHANNELS.find((c) => c.id === id) ?? MESSAGE_CHANNELS[0];
}

export function parseChannelFromQuery(value: unknown): MessageChannel {
  return isMessageChannel(value) ? value : 'system';
}
