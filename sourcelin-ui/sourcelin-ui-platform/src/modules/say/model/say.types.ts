export interface SayItem {
  id: number
  /** 发布者用户 ID（列表接口若返回则用于评论「作者」角标） */
  userId?: number
  content: string
  images: string
  imageFileIds: string
  likeCount: number
  collectCount?: number
  commentCount: number
  createTime: string
  likedByMe?: boolean
  collectedByMe?: boolean
  /** 兼容旧字段 */
  liked?: boolean
  /** 兼容旧字段 */
  isCollected?: boolean
  /** 兼容旧字段 */
  collectId?: number
  user?: {
    nickname: string
    avatar: string
  }
}
