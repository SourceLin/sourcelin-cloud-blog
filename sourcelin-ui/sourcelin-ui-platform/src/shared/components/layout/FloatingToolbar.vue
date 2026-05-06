<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { appIcons } from '@/shared/components/ui/icons'

const router = useRouter()
const themeStore = useThemeStore()

const isFullscreen = ref(false)
const isVisible = ref(false)
const scrollPercentage = ref(0)
const showPercentage = ref(true)
let scrollTimer: ReturnType<typeof setTimeout> | null = null

const theme = computed(() => (themeStore.isDark ? 'dark' : 'light'))
const sidebarIcons = {
  comment: appIcons.comment,
  themeLight: appIcons.themeLight,
  themeDark: appIcons.themeDark,
  fullscreenEnter: appIcons.fullscreenEnter,
  fullscreenExit: appIcons.fullscreenExit,
  backTop: appIcons.backTop
}

function updateScrollState() {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const totalHeight = document.documentElement.scrollHeight
  const viewportHeight = window.innerHeight

  scrollPercentage.value = Math.round((scrollTop / (totalHeight - viewportHeight)) * 100) || 0
  isVisible.value = scrollTop > 150
}

function handleScroll() {
  if (scrollTimer) {
    clearTimeout(scrollTimer)
  }
  scrollTimer = setTimeout(updateScrollState, 50)
}

function toggleTheme() {
  themeStore.toggle()
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value

  if (isFullscreen.value) {
    const element = document.documentElement
    if (element.requestFullscreen) {
      element.requestFullscreen()
    } else if ((element as unknown as { webkitRequestFullScreen?: () => void }).webkitRequestFullScreen) {
      ;(element as unknown as { webkitRequestFullScreen: () => void }).webkitRequestFullScreen()
    }
    return
  }

  if (document.exitFullscreen) {
    document.exitFullscreen()
  } else if ((document as unknown as { webkitCancelFullScreen?: () => void }).webkitCancelFullScreen) {
    ;(document as unknown as { webkitCancelFullScreen: () => void }).webkitCancelFullScreen()
  }
}

function backToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  updateScrollState()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  if (scrollTimer) {
    clearTimeout(scrollTimer)
  }
})
</script>

<template>
  <div class="glass-toolbar" :class="{ visible: isVisible }">
    <button class="toolbar-btn" title="树洞" @click="router.push('/treehole')">
      <SIcon :icon="sidebarIcons.comment" :size="20" />
    </button>

    <button class="toolbar-btn theme-btn" :title="theme === 'light' ? '切换到深色主题' : '切换到浅色主题'" @click="toggleTheme">
      <transition name="theme-icon" mode="out-in">
        <SIcon v-if="theme === 'light'" key="sun" :icon="sidebarIcons.themeLight" :size="20" />
        <SIcon v-else key="moon" :icon="sidebarIcons.themeDark" :size="20" />
      </transition>
    </button>

    <button class="toolbar-btn" :title="isFullscreen ? '退出全屏' : '进入全屏'" @click="toggleFullscreen">
      <SIcon :icon="isFullscreen ? sidebarIcons.fullscreenExit : sidebarIcons.fullscreenEnter" :size="20" />
    </button>

    <button
      class="toolbar-btn back-top-btn"
      title="回到顶部"
      @click="backToTop"
      @mouseenter="showPercentage = false"
      @mouseleave="showPercentage = true"
    >
      <span v-if="scrollPercentage < 100 && showPercentage" class="percentage">{{ scrollPercentage }}%</span>
      <SIcon v-else :icon="sidebarIcons.backTop" :size="20" />
    </button>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.glass-toolbar {
  position: fixed;
  right: -70px;
  bottom: 100px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: right 0.3s ease;
}

.glass-toolbar.visible {
  right: 20px;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  padding: 0;
  background: var(--toolbar-button-surface);
  background-image: var(--glass-noise);
  background-blend-mode: soft-light;
  border: none;
  outline: 1px solid var(--glass-stroke);
  border-radius: var(--border-radius-md);
  color: var(--toolbar-button-text);
  cursor: pointer;
  box-shadow: var(--glass-shadow);
  transition: all 0.2s;
  overflow: hidden;
  position: relative;
}

.toolbar-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: var(--floating-surface-highlight);
  pointer-events: none;
  border-radius: 14px 14px 0 0;
}

.toolbar-btn:hover {
  transform: translateY(-3px) scale(1.05);
  background: var(--toolbar-button-surface-hover);
  outline-color: rgba(var(--primary-color-rgb), 0.35);
  color: var(--toolbar-button-text-hover);
  box-shadow: 0 8px 24px var(--primary-light);
}

.toolbar-btn:active {
  transform: scale(0.95);
}

.theme-btn {
  color: var(--primary-color);
}

.theme-icon-enter-active,
.theme-icon-leave-active {
  transition: all 0.3s;
}

.theme-icon-enter {
  opacity: 0;
  transform: rotate(-90deg) scale(0.5);
}

.theme-icon-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.5);
}

.back-top-btn .percentage {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--primary-color);
}

@include sourcelin-down(md) {
  .glass-toolbar {
    bottom: 80px;
  }

  .glass-toolbar.visible {
    right: 12px;
  }

  .toolbar-btn {
    width: 40px;
    height: 40px;
    border-radius: var(--border-radius-md);
  }
}

@include sourcelin-down(sm) {
  .glass-toolbar {
    left: auto;
    right: -64px;
    bottom: calc(env(safe-area-inset-bottom, 0px) + 76px);
    flex-direction: column;
    gap: 8px;
    padding: 0;
    border: none;
    border-radius: 0;
    background: transparent;
    box-shadow: none;
    transform: none;
    transition: right var(--transition-base);
  }

  .glass-toolbar.visible {
    right: 8px;
    transform: none;
  }

  .toolbar-btn {
    width: 36px;
    height: 36px;
    border-radius: var(--border-radius-md);
  }

  .toolbar-btn:hover {
    transform: translateY(-1px) scale(1.03);
  }
}
</style>

