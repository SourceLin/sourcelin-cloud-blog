import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { fetchMobileCapabilities, type MobileCapabilities } from '@/shared/api/capability.api';

// 默认安全配置 (最严限制：非博主下 UGC 与社交全部关闭)
const DEFAULT_SAFE_CAPABILITIES: MobileCapabilities = {
  client: 'mini',
  reviewSafeMode: true,
  articleReadEnabled: true,
  searchEnabled: true,
  profileEnabled: true,
  favoriteEnabled: true,
  readingHistoryEnabled: true,
  settingsEnabled: true,
  policyEnabled: true,
  aboutEnabled: true,
  friendLinkEnabled: true,
  navigationEnabled: true,
  articlePublishEnabled: true,
  commentEnabled: false,
  communityEnabled: false,
  sayPublishEnabled: false,
  treeholeEnabled: false,
  interactionCenterEnabled: false,
  followEnabled: false,
  messageCenterEnabled: false,
  userHomeEnabled: false,
  userUploadEnabled: false
};

export const useCapabilityStore = defineStore('capability', () => {
  const capabilities = ref<MobileCapabilities>({ ...DEFAULT_SAFE_CAPABILITIES });
  const loading = ref(false);
  const isReady = ref(false);
  
  // 初始化 Promise 控制
  let readyPromise: Promise<void> | null = null;
  let resolveReady: (() => void) | null = null;

  readyPromise = new Promise((resolve) => {
    resolveReady = resolve;
  });

  const getCapabilities = computed(() => capabilities.value);

  async function loadCapabilities(force = false): Promise<void> {
    if (isReady.value && !force) {
      return;
    }
    loading.value = true;
    try {
      // 客户端默认统一取 mini
      const data = await fetchMobileCapabilities('mini');
      if (data && data.client === 'mini') {
        capabilities.value = { ...data };
      } else {
        capabilities.value = { ...DEFAULT_SAFE_CAPABILITIES };
      }
    } catch (err) {
      console.warn('[CapabilityStore] Failed to load capabilities, using default safe mode:', err);
      capabilities.value = { ...DEFAULT_SAFE_CAPABILITIES };
    } finally {
      loading.value = false;
      isReady.value = true;
      if (resolveReady) {
        resolveReady();
      }
    }
  }

  function waitUntilReady(): Promise<void> {
    if (isReady.value) {
      return Promise.resolve();
    }
    return readyPromise || Promise.resolve();
  }

  return {
    capabilities,
    loading,
    isReady,
    getCapabilities,
    loadCapabilities,
    waitUntilReady
  };
});
