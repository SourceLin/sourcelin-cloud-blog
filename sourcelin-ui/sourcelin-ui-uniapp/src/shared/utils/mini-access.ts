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
}

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

export function hasFeatureAccess(context: MiniAccessContext, feature: CapabilityFeature): boolean {
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
