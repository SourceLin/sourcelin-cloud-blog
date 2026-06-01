// src/stores/app.ts
// 应用全局状态：系统信息、站点配置、未读数

import { defineStore } from 'pinia';
import { ref } from 'vue';

export interface SystemInfo {
  windowInfo?: UniApp.GetWindowInfoResult;
  appBaseInfo?: UniApp.GetAppBaseInfoResult;
  deviceInfo?: UniApp.GetDeviceInfoResult;
  menuButton?: UniApp.GetMenuButtonBoundingClientRectRes;
  /** 兼容低版本基础库的兜底 */
  legacy?: UniApp.GetSystemInfoResult;
}

export interface SiteInfo {
  name: string;
  description?: string;
  logoUrl?: string;
  icpRecord?: string;
}

export const useAppStore = defineStore('app', () => {
  const launchReady = ref(false);
  const systemInfo = ref<SystemInfo | null>(null);
  const siteInfo = ref<SiteInfo | null>(null);
  const unreadCount = ref(0);

  function markLaunchReady(): void {
    launchReady.value = true;
  }

  function collectSystemInfo(): void {
    try {
      systemInfo.value = {
        windowInfo: uni.getWindowInfo(),
        appBaseInfo: uni.getAppBaseInfo(),
        deviceInfo: uni.getDeviceInfo(),
        menuButton: uni.getMenuButtonBoundingClientRect()
      };
    } catch {
      try {
        systemInfo.value = { legacy: uni.getSystemInfoSync() };
      } catch {
        systemInfo.value = null;
      }
    }
  }

  function setSiteInfo(info: SiteInfo | null): void {
    siteInfo.value = info;
  }

  function setUnreadCount(count: number): void {
    unreadCount.value = Math.max(0, count | 0);
  }

  return {
    launchReady,
    systemInfo,
    siteInfo,
    unreadCount,
    markLaunchReady,
    collectSystemInfo,
    setSiteInfo,
    setUnreadCount
  };
});
