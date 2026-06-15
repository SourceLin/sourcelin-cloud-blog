import { computed } from 'vue';
import { useCapabilityStore } from '@/stores/capability';
import { useUserStore } from '@/stores/user';
import type { LiquidTabItem } from '@/shared/utils/liquid-tabbar';
import {
  getVisibleLiquidTabItems,
  guardFeatureAccess,
  hasFeatureAccess,
  redirectToHomeSilently,
  userMeetsRole,
  type CapabilityFeature
} from '@/shared/utils/mini-access';

export function useMiniAccess() {
  const userStore = useUserStore();
  const capabilityStore = useCapabilityStore();

  const context = computed(() => ({
    capabilities: capabilityStore.capabilities,
    isBlogger: userStore.isBlogger,
    articlePublishRole: capabilityStore.capabilities.articlePublishRole
  }));

  function can(feature: CapabilityFeature): boolean {
    const ctx = context.value;
    // articlePublishEnabled: 如果 DB 配置了最小角色要求，校验角色等级
    if (feature === 'articlePublishEnabled' && ctx.articlePublishRole) {
      if (!userMeetsRole(userStore.userInfo, ctx.articlePublishRole)) {
        return false;
      }
    }
    return hasFeatureAccess(ctx, feature);
  }

  function guard(feature: CapabilityFeature): boolean {
    const ctx = context.value;
    // articlePublishEnabled: 如果 DB 配置了最小角色要求，校验角色等级
    if (feature === 'articlePublishEnabled' && ctx.articlePublishRole) {
      if (!userMeetsRole(userStore.userInfo, ctx.articlePublishRole)) {
        redirectToHomeSilently();
        return false;
      }
    }
    return guardFeatureAccess(ctx, feature);
  }

  function resolveLiquidTabs(items: LiquidTabItem[]): LiquidTabItem[] {
    return getVisibleLiquidTabItems(context.value, items);
  }

  return {
    userStore,
    capabilityStore,
    context,
    can,
    guard,
    resolveLiquidTabs
  };
}
