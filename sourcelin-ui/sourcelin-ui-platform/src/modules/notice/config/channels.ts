import type { AppIconName } from '@/shared/components/ui/icons'

/** 消息中心频道（与 URL query `channel` 对齐） */
export type MessageChannel = 'system' | 'interaction' | 'star' | 'follow'

export const MESSAGE_CHANNEL_IDS: MessageChannel[] = ['system', 'interaction', 'star', 'follow']

export function isMessageChannel(value: unknown): value is MessageChannel {
  return typeof value === 'string' && MESSAGE_CHANNEL_IDS.includes(value as MessageChannel)
}

export interface ChannelConfig {
  id: MessageChannel
  name: string
  icon: AppIconName
  description: string
  /** 阶段 B 接入后端后为 true */
  available: boolean
  /** 空态页展示用 */
  comingTitle: string
  comingDescription: string
}

export const MESSAGE_CHANNELS: ChannelConfig[] = [
  {
    id: 'system',
    name: '系统公告',
    icon: 'notice',
    description: '站务通知与系统公告',
    available: true,
    comingTitle: '系统公告',
    comingDescription: '站务通知与系统公告'
  },
  {
    id: 'interaction',
    name: '互动消息',
    icon: 'comment',
    description: '评论与回复',
    available: true,
    comingTitle: '互动消息',
    comingDescription: '收到的评论与回复会显示在这里。'
  },
  {
    id: 'star',
    name: '点赞通知',
    icon: 'heart',
    description: '收到的点赞',
    available: true,
    comingTitle: '点赞通知',
    comingDescription: '收到的新点赞会显示在这里。'
  },
  {
    id: 'follow',
    name: '关注通知',
    icon: 'visitors',
    description: '新增粉丝',
    available: true,
    comingTitle: '关注通知',
    comingDescription: '新粉丝会显示在这里。'
  }
]

export function getChannelConfig(id: MessageChannel): ChannelConfig {
  const found = MESSAGE_CHANNELS.find((c) => c.id === id)
  return found ?? MESSAGE_CHANNELS[0]
}
