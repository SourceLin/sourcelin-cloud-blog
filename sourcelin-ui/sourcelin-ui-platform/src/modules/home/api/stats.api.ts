import { requestData } from '@/shared/api/http'

export interface SiteStats {
  articleCount: number
  articleTrend: number
  commentCount: number
  commentTrend: number
  viewCount: number
  viewTrend: number
  userCount: number
  userTrend: number
}

export interface UserStats {
  articleCount: number
  collectCount: number
  followCount: number
  fansCount: number
}

export function getStats() {
  return requestData<SiteStats>({
    url: '/front/stats',
    method: 'get'
  })
}

export function getUserStats() {
  return requestData<UserStats>({
    url: '/front/user/stats',
    method: 'get'
  })
}
