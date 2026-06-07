import { computed } from 'vue';
import { useCapabilityStore } from '@/stores/capability';
import { useUserStore } from '@/stores/user';
import type { LiquidTabItem } from '@/shared/utils/liquid-tabbar';
import {
  getVisibleLiquidTabItems,
  guardFeatureAccess,
  hasFeatureAccess,
  type CapabilityFeature
} from '@/shared/utils/mini-access';

export function useMiniAccess() {
  const userStore = useUserStore();
  const capabilityStore = useCapabilityStore();

  const context = computed(() => ({
    capabilities: capabilityStore.capabilities,
    isBlogger: userStore.isBlogger
  }));

  function can(feature: CapabilityFeature): boolean {
    return hasFeatureAccess(context.value, feature);
  }

  function guard(feature: CapabilityFeature): boolean {
    return guardFeatureAccess(context.value, feature);
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
