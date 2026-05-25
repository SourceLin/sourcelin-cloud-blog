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
