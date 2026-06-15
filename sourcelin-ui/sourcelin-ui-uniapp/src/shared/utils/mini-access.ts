import type { MobileCapabilities } from '@/shared/api/capability.api';
import type { UserInfo } from '@/stores/user';
import type { LiquidTabItem } from './liquid-tabbar';

export type CapabilityFeature = keyof Pick<
  MobileCapabilities,
  | 'articleReadEnabled'
  | 'searchEnabled'
  | 'profileEnabled'
  | 'favoriteEnabled'
  | 'readingHistoryEnabled'
  | 'settingsEnabled'
  | 'policyEnabled'
  | 'aboutEnabled'
  | 'friendLinkEnabled'
  | 'navigationEnabled'
  | 'articlePublishEnabled'
  | 'commentEnabled'
  | 'communityEnabled'
  | 'sayPublishEnabled'
  | 'treeholeEnabled'
  | 'interactionCenterEnabled'
  | 'followEnabled'
  | 'messageCenterEnabled'
  | 'userHomeEnabled'
  | 'userUploadEnabled'
>;

export interface MiniAccessContext {
  capabilities: MobileCapabilities;
  isBlogger: boolean;
  /** DB 配置的允许写文章的最小角色, e.g. "blogger" / "admin" / "super_admin" */
  articlePublishRole?: string;
}

/** 角色层级: user < blogger < admin < super_admin */
const ROLE_HIERARCHY: Record<string, number> = {
  user: 0,
  blogger: 1,
  admin: 2,
  super_admin: 3
};

export function isBloggerIdentity(userInfo?: UserInfo | null): boolean {
  if (!userInfo) return false;
  const userPerms = userInfo.permissions || [];
  const userRoles = userInfo.roles || [];
  return userPerms.includes('mini:blogger')
    || userPerms.includes('blog:article:write')
    || userRoles.includes('blogger')
    || userRoles.includes('admin')
    || userRoles.includes('super_admin');
}

/**
 * 检查用户角色是否满足最小角色要求
 */
export function userMeetsRole(userInfo: UserInfo | null | undefined, requiredRole: string): boolean {
  if (!userInfo) return false;
  const requiredLevel = ROLE_HIERARCHY[requiredRole];
  if (requiredLevel === undefined) return false;
  const userRoles = userInfo.roles || [];
  const userMaxLevel = userRoles.reduce((max, r) => {
    const level = ROLE_HIERARCHY[r];
    return level !== undefined && level > max ? level : max;
  }, -1);
  return userMaxLevel >= requiredLevel;
}

export function hasFeatureAccess(context: MiniAccessContext, feature: CapabilityFeature): boolean {
  // articlePublishEnabled 需要额外校验角色等级
  if (feature === 'articlePublishEnabled') {
    // 角色校验由 useMiniAccess.can()/guard() 在调用前完成，
    // 此处仅作为兜底：非博主一律拒绝写文章权限
    return context.isBlogger;
  }

  if (context.isBlogger) {
    return true;
  }
  return Boolean(context.capabilities[feature]);
}

export function shouldShowCommunityTab(context: MiniAccessContext): boolean {
  return hasFeatureAccess(context, 'communityEnabled');
}

export function getVisibleLiquidTabItems(context: MiniAccessContext, items: LiquidTabItem[]): LiquidTabItem[] {
  if (shouldShowCommunityTab(context)) {
    return items;
  }
  return items.filter((item) => item.path !== 'pages/community/community');
}

export function redirectToHomeSilently(): void {
  uni.switchTab({ url: '/pages/home/home' });
}

export function guardFeatureAccess(context: MiniAccessContext, feature: CapabilityFeature): boolean {
  if (hasFeatureAccess(context, feature)) {
    return true;
  }
  redirectToHomeSilently();
  return false;
}
