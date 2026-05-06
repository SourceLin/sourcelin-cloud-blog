export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  requestId: string
  timestamp: string
}

export interface ListResult<T> {
  items: T[]
}

export interface PageResult<T> extends ListResult<T> {
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface IdResponse {
  id: number
}

export type IdResult = IdResponse

export type InteractionTargetType = 'article' | 'say' | 'treehole'

export interface ActionState {
  likedByMe: boolean
  collectedByMe: boolean
}

export interface InteractionStat extends ActionState {
  targetType: InteractionTargetType
  targetId: number
  likeCount: number
  collectCount: number
}
