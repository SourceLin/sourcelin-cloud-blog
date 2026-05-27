// src/stores/theme.ts
// 全局明暗主题状态管理器：支持系统、浅色、深色三态切换与持久化

import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { getUserPreferences } from '@/shared/utils/preferences';

export type ThemeMode = 'system' | 'light' | 'dark';
export type ResolvedTheme = 'light' | 'dark';

const STORAGE_KEY = 'app.theme.mode';

export const useThemeStore = defineStore('theme', () => {
  const mode = ref<ThemeMode>('system');
  const resolvedTheme = ref<ResolvedTheme>('light');
  const initialized = ref(false);

  const isDark = computed(() => resolvedTheme.value === 'dark');
  const themeClass = computed(() => `sl-theme--${resolvedTheme.value}`);

  function getSystemTheme(): ResolvedTheme {
    try {
      const info = uni.getAppBaseInfo();
      if (info.theme === 'dark' || info.theme === 'light') {
        return info.theme;
      }
    } catch (_e) {
      // 继续使用平台专属兜底。
    }

    /* #ifdef H5 */
    if (typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    /* #endif */

    return 'light';
  }

  function syncNativeArea(): void {
    const isDarkTheme = resolvedTheme.value === 'dark';
    
    // 同步导航栏配色
    try {
      uni.setNavigationBarColor({
        frontColor: isDarkTheme ? '#ffffff' : '#000000',
        backgroundColor: isDarkTheme ? '#080d18' : '#f5f7fb',
        animation: {
          duration: 180,
          timingFunc: 'easeIn'
        }
      });
    } catch (_e) {
      // 可能会在部分页面或启动期失败，属于正常现象
    }

    // 同步 Tab 栏配色 (原生兜底)
    try {
      uni.setTabBarStyle({
        backgroundColor: isDarkTheme ? '#080d18' : '#ffffff',
        borderStyle: isDarkTheme ? 'black' : 'white',
        color: isDarkTheme ? '#687693' : '#86909c',
        selectedColor: isDarkTheme ? '#8f82ff' : '#3b59ff'
      });
    } catch (_e) {
      // 在非 TabBAR 页面中调用会报错，静默忽略
    }

    try {
      uni.setBackgroundColor({
        backgroundColor: isDarkTheme ? '#080d18' : '#f5f7fb',
        backgroundColorTop: isDarkTheme ? '#080d18' : '#f5f7fb',
        backgroundColorBottom: isDarkTheme ? '#080d18' : '#f5f7fb'
      });
      uni.setBackgroundTextStyle({
        textStyle: isDarkTheme ? 'light' : 'dark'
      });
    } catch (_e) {
      // 页面不支持背景同步时仍可保持内容区域主题正确
    }
  }

  function resolveTheme(): void {
    if (mode.value === 'system') {
      resolvedTheme.value = getSystemTheme();
    } else {
      resolvedTheme.value = mode.value;
    }
    syncNativeArea();
  }

  function setMode(newMode: ThemeMode): void {
    mode.value = newMode;
    uni.setStorageSync(STORAGE_KEY, newMode);
    resolveTheme();
  }

  function restoreFromStorage(): void {
    if (initialized.value) return;
    
    try {
      const storedMode = uni.getStorageSync(STORAGE_KEY) as ThemeMode | '';
      if (storedMode === 'system' || storedMode === 'light' || storedMode === 'dark') {
        mode.value = storedMode;
      } else {
        // 文章详情旧版夜间阅读偏好迁移到全局主题。
        const oldNightRead = getUserPreferences().nightReadingEnabled;
        if (oldNightRead === true) {
          mode.value = 'dark';
          uni.setStorageSync(STORAGE_KEY, 'dark');
        } else {
          mode.value = 'system';
          uni.setStorageSync(STORAGE_KEY, 'system');
        }
      }
    } catch {
      mode.value = 'system';
    }

    resolveTheme();
    initialized.value = true;
  }

  // 响应微信系统主题环境的动态改变
  try {
    uni.onThemeChange((res) => {
      if (mode.value === 'system') {
        resolvedTheme.value = res.theme === 'dark' ? 'dark' : 'light';
        syncNativeArea();
      }
    });
  } catch (_e) {
    // 部分非微信小程序环境可能没有 onThemeChange
  }

  /* #ifdef H5 */
  if (typeof window !== 'undefined') {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    mediaQuery.addEventListener('change', (event) => {
      if (mode.value === 'system') {
        resolvedTheme.value = event.matches ? 'dark' : 'light';
        syncNativeArea();
      }
    });
  }
  /* #endif */

  return {
    mode,
    resolvedTheme,
    initialized,
    isDark,
    themeClass,
    setMode,
    restoreFromStorage,
    resolveTheme,
    syncNativeArea
  };
});
