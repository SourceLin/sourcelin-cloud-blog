import { requestData } from '@/shared/api/http'
import type { ListResult } from '@/shared/types/api'

export interface NavbarItem {
  id: number
  name: string
  path: string
  icon?: string
  summary?: string
  isFrame: string
  type: string
  orderNum: number
  children: NavbarItem[]
}

export interface NavItem {
  id: number
  name: string
  url: string
  description?: string
  icon?: string
  cover?: string
  category?: string
  source?: string
  isRecommend?: number
  orderNum?: number
  clickCount?: number
}

export function getNavbars() {
  return requestData<ListResult<NavbarItem>>({
    url: '/front/navbars',
    method: 'get'
  }).then((result) => result.items)
}

export function getNavItems(params?: { category?: string; keyword?: string; isRecommend?: number }) {
  return requestData<ListResult<NavItem>>({
    url: '/front/navigation',
    method: 'get',
    params
  }).then((result) => result.items)
}

export function reportNavItemClick(id: number) {
  return requestData<void>({
    url: `/front/navigation/${id}/click`,
    method: 'post'
  })
}
