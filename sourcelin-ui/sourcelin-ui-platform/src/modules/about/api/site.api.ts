import { requestData } from '@/shared/api/http'

export interface FrontSiteInfo {
  author?: string
  title?: string
  authorInfo?: string
  avatar?: string
  bio?: string
  webName?: string
  webTitle?: string[]
  notices?: string[]
  footer?: string
  backgroundImage?: string
  logo?: string
  recordNum?: string | number
  showList?: number[]
  siteName?: string
  github?: string
  gitee?: string
  qqNumber?: string
  qqGroup?: string
  email?: string
  wechat?: string
  webUrl?: string
  aliPay?: string
  weixinPay?: string
  openComment?: string
  openAdmiration?: string
  loginTypeList?: string
}

export interface FrontWebConfig {
  id?: number
  name?: string
  summary?: string
  keyword?: string
  recordNum?: string
  notice?: string
  webUrl?: string
  github?: string
  gitee?: string
  qqNumber?: string
  qqGroup?: string
  email?: string
  wechat?: string
  author?: string
  authorInfo?: string
  authorAvatar?: string
  aboutMe?: string
  openComment?: string
  openAdmiration?: string
  aliPay?: string
  weixinPay?: string
}

export interface FrontAboutInfo {
  author?: string
  authorInfo?: string
  avatar?: string
  bio?: string
  webName?: string
  summary?: string
  keyword?: string
  recordNum?: string
  webUrl?: string
  github?: string
  gitee?: string
  qqNumber?: string
  qqGroup?: string
  email?: string
  wechat?: string
  aliPay?: string
  weixinPay?: string
  openComment?: string
  openAdmiration?: string
  showList?: number[]
  notices?: string[]
}

export function getConfig() {
  return requestData<FrontWebConfig>({
    url: '/front/config',
    method: 'get'
  })
}

export function getSiteInfo() {
  return requestData<FrontSiteInfo>({
    url: '/front/config/siteInfo',
    method: 'get'
  })
}

export function getAboutInfo() {
  return requestData<FrontAboutInfo>({
    url: '/front/config/about',
    method: 'get'
  })
}
