<script setup lang="ts">
import { computed } from 'vue'
import { darkTheme, NConfigProvider, NDialogProvider } from 'naive-ui'
import { useRoute } from 'vue-router'
import { useHead } from '@unhead/vue'
import { useThemeStore } from '@/stores/theme.store'
import { useUiStore } from '@/stores/ui.store'
import { useSiteStore } from '@/stores/site.store'
import SearchDialog from '@/shared/components/feedback/SearchDialog.vue'
import StartupLoadingOverlay from '@/shared/components/feedback/StartupLoadingOverlay.vue'
import AppFooter from '@/shared/components/layout/AppFooter.vue'
import AppHeader from '@/shared/components/layout/AppHeader.vue'
import FloatingToolbar from '@/shared/components/layout/FloatingToolbar.vue'
import MobileNavDrawer from '@/shared/components/layout/MobileNavDrawer.vue'
import ThemeTransition from '@/shared/components/layout/ThemeTransition.vue'
import SMessage from '@/shared/components/ui/SMessage.vue'
import SNotification from '@/shared/components/ui/SNotification.vue'

const themeStore = useThemeStore()
const uiStore = useUiStore()
const siteStore = useSiteStore()
const route = useRoute()

const isDark = computed(() => themeStore.isDark)
const startupLoadingVisible = computed(() => uiStore.startupLoadingVisible)
const useStandaloneShell = computed(() => route.meta?.shell === 'auth')

// 全局默认 TDK，各页面可通过 useSeoHead composable 覆盖
const siteName = computed(() => siteStore.siteInfo.webName || '圆圈博客')
const siteDescription = computed(() => {
  const titles = siteStore.siteInfo.webTitle
  return titles?.length > 0
    ? `${siteName.value} - ${titles[0]}`
    : `${siteName.value} - 记录技术、写作与长期主义`
})

useHead(
  computed(() => ({
    title: siteName.value,
    meta: [
      { name: 'description', content: siteDescription.value },
      { property: 'og:site_name', content: siteName.value },
      { property: 'og:type', content: 'website' },
      { property: 'twitter:card', content: 'summary_large_image' }
    ]
  }))
)

function readCssVariable(variableName: string, fallback: string) {
  if (typeof window === 'undefined') {
    return fallback
  }

  const value = getComputedStyle(document.documentElement).getPropertyValue(variableName).trim()
  return value || fallback
}

function isUnsupportedThemeColor(value: string) {
  return /color-mix\(|gradient\(|url\(|var\(/i.test(value)
}

function readThemeColor(variableName: string, fallback: string) {
  const value = readCssVariable(variableName, fallback)
  return isUnsupportedThemeColor(value) ? fallback : value
}

const themeOverrides = computed(() => {
  void themeStore.themeName

  return {
    common: {
      primaryColor: readThemeColor('--primary-color', '#4F46E5'),
      primaryColorHover: readThemeColor('--primary-hover', '#4338CA'),
      primaryColorPressed: readThemeColor('--primary-active', '#3730A3'),
      successColor: readThemeColor('--success-color', '#10B981'),
      warningColor: readThemeColor('--warning-color', '#F59E0B'),
      errorColor: readThemeColor('--error-color', '#EF4444'),
      infoColor: readThemeColor('--info-color', '#3B82F6'),
      borderRadius: readCssVariable('--border-radius-lg', '12px'),
      fontFamily: readCssVariable('--font-family', 'SmileySans, PingFang SC, sans-serif'),
      bodyColor: readThemeColor('--background-color', '#F5F7FB'),
      cardColor: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)'),
      modalColor: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)'),
      popoverColor: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)'),
      tableColor: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)'),
      inputColor: readThemeColor('--fallback-glass-bg-lite', 'rgba(255, 255, 255, 0.65)'),
      actionColor: readThemeColor('--hover-background-color', 'rgba(79, 70, 229, 0.08)'),
      hoverColor: readThemeColor('--primary-opacity', 'rgba(79, 70, 229, 0.12)'),
      textColorBase: readThemeColor('--text-color', '#1E293B'),
      textColor1: readThemeColor('--text-color-dark', '#0F172A'),
      textColor2: readThemeColor('--text-color-muted', '#64748B'),
      textColor3: readThemeColor('--input-placeholder', '#94A3B8'),
      borderColor: readThemeColor('--border-color', 'rgba(148, 163, 184, 0.18)'),
      dividerColor: readThemeColor('--border-light', 'rgba(148, 163, 184, 0.1)')
    },
    Button: {
      borderRadiusMedium: readCssVariable('--border-radius-lg', '12px'),
      borderRadiusLarge: readCssVariable('--border-radius-xl', '16px'),
      textColorText: readThemeColor('--text-color', '#1E293B'),
      textColorPrimary: readThemeColor('--text-color-light', '#ffffff')
    },
    Card: {
      borderRadius: readCssVariable('--glass-radius', '18px'),
      color: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)'),
      borderColor: readThemeColor('--glass-border', 'rgba(255, 255, 255, 0.4)'),
      titleTextColor: readThemeColor('--text-color', '#1E293B')
    },
    Modal: {
      borderRadius: readCssVariable('--glass-radius', '18px'),
      color: readThemeColor('--fallback-glass-bg', 'rgba(255, 255, 255, 0.78)')
    },
    Dialog: {
      borderRadius: readCssVariable('--glass-radius', '18px')
    }
  }
})
</script>

<template>
  <NConfigProvider :theme="isDark ? darkTheme : null" :theme-overrides="themeOverrides">
    <SMessage>
      <SNotification>
        <NDialogProvider>
          <div id="app">
            <AppHeader v-if="!useStandaloneShell" />
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
            <AppFooter v-if="!useStandaloneShell" />
            <FloatingToolbar v-if="!useStandaloneShell" />
            <MobileNavDrawer v-if="!useStandaloneShell" />
            <SearchDialog v-if="!useStandaloneShell" />
            <ThemeTransition />

            <transition name="startup-loading-fade">
              <StartupLoadingOverlay v-if="startupLoadingVisible" :visible="startupLoadingVisible" />
            </transition>
          </div>
        </NDialogProvider>
      </SNotification>
    </SMessage>
  </NConfigProvider>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.startup-loading-fade-enter-active,
.startup-loading-fade-leave-active {
  transition: opacity 0.68s cubic-bezier(0.22, 1, 0.36, 1);
}

.startup-loading-fade-enter-from,
.startup-loading-fade-leave-to {
  opacity: 0;
}

.startup-loading-fade-enter-active .startup-loading__content,
.startup-loading-fade-leave-active .startup-loading__content,
.startup-loading-fade-enter-active .startup-loading__brand,
.startup-loading-fade-leave-active .startup-loading__brand,
.startup-loading-fade-enter-active .startup-loading__status,
.startup-loading-fade-leave-active .startup-loading__status,
.startup-loading-fade-enter-active .startup-loading__backdrop,
.startup-loading-fade-leave-active .startup-loading__backdrop,
.startup-loading-fade-enter-active .startup-loading__veil,
.startup-loading-fade-leave-active .startup-loading__veil,
.startup-loading-fade-enter-active .startup-loading__halo,
.startup-loading-fade-leave-active .startup-loading__halo {
  transition:
    transform 0.72s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.72s cubic-bezier(0.22, 1, 0.36, 1),
    filter 0.72s cubic-bezier(0.22, 1, 0.36, 1);
}

.startup-loading-fade-enter-from .startup-loading__content {
  transform: scale(0.965);
  opacity: 0;
  filter: blur(12px);
}

.startup-loading-fade-leave-to .startup-loading__content {
  transform: scale(1.035);
  opacity: 0;
  filter: blur(16px);
}

.startup-loading-fade-enter-from .startup-loading__brand,
.startup-loading-fade-enter-from .startup-loading__status {
  transform: translateY(10px) scale(0.96);
  opacity: 0;
  filter: blur(8px);
}

.startup-loading-fade-leave-to .startup-loading__brand,
.startup-loading-fade-leave-to .startup-loading__status {
  transform: translateY(-8px) scale(1.04);
  opacity: 0;
  filter: blur(10px);
}

.startup-loading-fade-enter-from .startup-loading__backdrop,
.startup-loading-fade-enter-from .startup-loading__veil,
.startup-loading-fade-enter-from .startup-loading__halo {
  opacity: 0;
  transform: scale(1.08);
  filter: blur(20px);
}

.startup-loading-fade-leave-to .startup-loading__backdrop,
.startup-loading-fade-leave-to .startup-loading__veil,
.startup-loading-fade-leave-to .startup-loading__halo {
  opacity: 0;
  transform: scale(1.06);
  filter: blur(26px);
}

#app {
  min-height: 100vh;
  background: var(--body-gradient);
}
</style>
