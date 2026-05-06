export interface TreeholeCardItem {
  id: number
  userId?: number
  content: string
  createTime?: string
  likeCount: number
  collectCount?: number
  commentCount: number
  likedByMe?: boolean
  collectedByMe?: boolean
  nickname?: string
  avatar?: string
}

export interface TreeholeBarrageItem {
  id: number | string
  msg: string
  avatar?: string
  nickname?: string
  time?: number
}
