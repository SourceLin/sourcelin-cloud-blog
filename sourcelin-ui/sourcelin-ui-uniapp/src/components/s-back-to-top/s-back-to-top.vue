<template>
  <view
    class="s-back-to-top-group"
    :class="{ 's-back-to-top-group--visible': visible }"
  >
    <view
      class="s-back-to-top s-back-to-top--theme"
      @tap.stop="toggleTheme"
    >
      <view class="s-back-to-top__orb s-back-to-top__orb--theme">
        <text class="s-back-to-top__theme-icon">{{ themeIcon }}</text>
      </view>
    </view>

    <view
      class="s-back-to-top"
      @tap="scrollToTop"
    >
      <view class="s-back-to-top__orb">
        <uni-icons type="top" size="20" color="currentColor" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useThemeStore } from '@/stores/theme';

withDefaults(defineProps<{
  visible?: boolean;
}>(), {
  visible: false
});

const themeStore = useThemeStore();
const isDark = computed(() => themeStore.resolvedTheme === 'dark');
const themeIcon = computed(() => (isDark.value ? '☀' : '☾'));

function toggleTheme(): void {
  themeStore.setMode(isDark.value ? 'light' : 'dark');
}

function scrollToTop(): void {
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 300
  });
}
</script>

<style lang="scss" scoped>
.s-back-to-top-group {
  position: fixed;
  right: 28rpx;
  bottom: calc(224rpx + env(safe-area-inset-bottom));
  z-index: 96;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14rpx;
  opacity: 0;
  pointer-events: none;
  transform: translate3d(22rpx, 24rpx, 0) scale(0.86);
  transition:
    opacity 0.22s ease,
    transform 0.28s cubic-bezier(0.25, 1, 0.5, 1);
}

.s-back-to-top-group--visible {
  opacity: 1;
  pointer-events: auto;
  transform: translate3d(0, 0, 0) scale(1);
}

.s-back-to-top {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 82rpx;
  height: 82rpx;
  border-radius: 50%;
  color: var(--sl-color-primary);
  background:
    linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
    var(--sl-bg-glass-pure);
  border: 1rpx solid var(--sl-control-border);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.06),
    0 18rpx 42rpx rgba(17, 24, 39, 0.12),
    0 8rpx 24rpx rgba(59, 89, 255, 0.12);
  transition: transform 0.22s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.22s ease, background-color 0.22s ease;

  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(18rpx) saturate(1.24);
  -webkit-backdrop-filter: blur(18rpx) saturate(1.24);
  /* #endif */

  &--theme {
    color: var(--sl-color-primary-soft, var(--sl-color-primary));
  }

  &:active {
    transform: scale(0.94);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.86),
      0 14rpx 34rpx rgba(59, 89, 255, 0.16);
  }
}

.s-back-to-top__orb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54rpx;
  height: 54rpx;
  border-radius: 50%;
  background:
    radial-gradient(circle at 32% 22%, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0) 42%),
    linear-gradient(145deg, rgba(59, 89, 255, 0.12), rgba(143, 112, 255, 0.08));
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
    0 8rpx 16rpx rgba(59, 89, 255, 0.1);
}

.s-back-to-top__orb--theme {
  background:
    radial-gradient(circle at 32% 22%, rgba(255, 255, 255, 0.96), rgba(255, 255, 255, 0) 42%),
    linear-gradient(145deg, rgba(255, 191, 0, 0.22), rgba(59, 89, 255, 0.12));
}

.s-back-to-top__orb::after {
  content: '';
  position: absolute;
  inset: 4rpx;
  border-radius: inherit;
  border: 1rpx solid rgba(255, 255, 255, 0.62);
  pointer-events: none;
}

.s-back-to-top__theme-icon {
  font-size: 26rpx;
  line-height: 1;
  font-weight: 800;
}
</style>
