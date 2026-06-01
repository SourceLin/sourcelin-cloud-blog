/** `/front/config/siteInfo` 品牌与全局站点信息 */
export interface SiteBrandInfo {
  webName?: string;
  siteName?: string;
  author?: string;
  authorInfo?: string;
  avatar?: string;
  logo?: string;
  bio?: string;
  footer?: string;
  recordNum?: string;
  github?: string;
  gitee?: string;
  qqNumber?: string;
  qqGroup?: string;
  email?: string;
  wechat?: string;
  webUrl?: string;
  showList?: number[];
  notices?: string[];
}

export interface AboutContactItem {
  key: string;
  label: string;
  value: string;
}

export interface AboutFactItem {
  label: string;
  value: string;
}

export interface AboutInfo {
  author?: string;
  authorInfo?: string;
  avatar?: string;
  bio?: string;
  webName?: string;
  summary?: string;
  keyword?: string;
  recordNum?: string;
  webUrl?: string;
  github?: string;
  gitee?: string;
  qqNumber?: string;
  qqGroup?: string;
  email?: string;
  wechat?: string;
  aliPay?: string;
  weixinPay?: string;
  openComment?: string;
  openAdmiration?: string;
  showList?: number[];
  notices?: string[];
}

export interface FriendLinkItem {
  id: number;
  name: string;
  url: string;
  avatar?: string;
  description?: string;
}

export interface FriendLinkApplyPayload {
  name: string;
  url: string;
  email: string;
  description?: string;
  avatar?: string;
}

export interface NavigationItem {
  id: number;
  name: string;
  url: string;
  description?: string;
  icon?: string;
  cover?: string;
  category?: string;
  source?: string;
  isRecommend?: number;
  orderNum?: number;
  clickCount?: number;
}
