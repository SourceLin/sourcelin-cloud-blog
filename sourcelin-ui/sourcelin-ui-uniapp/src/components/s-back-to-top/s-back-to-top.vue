<template>
  <view
    class="s-back-to-top"
    :class="{ 's-back-to-top--visible': visible }"
    @tap="scrollToTop"
  >
    <view class="s-back-to-top__orb">
      <uni-icons type="top" size="20" color="currentColor" />
    </view>
    <text class="s-back-to-top__label">TOP</text>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  visible?: boolean;
}>(), {
  visible: false
});

function scrollToTop(): void {
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 300
  });
}
</script>

<style lang="scss" scoped>
.s-back-to-top {
  position: fixed;
  right: 28rpx;
  bottom: calc(176rpx + env(safe-area-inset-bottom));
  z-index: 88;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5rpx;
  width: 78rpx;
  padding: 10rpx 0 12rpx;
  border-radius: 999rpx;
  color: var(--sl-color-primary);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      var(--sl-bg-glass-pure);
    border: 1rpx solid var(--sl-control-border);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.08),
    0 18rpx 42rpx rgba(17, 24, 39, 0.12),
    0 8rpx 24rpx rgba(59, 89, 255, 0.12);
  opacity: 0;
  pointer-events: none;
  transform: translate3d(22rpx, 24rpx, 0) scale(0.86);
  transition:
    opacity 0.22s ease,
    transform 0.28s cubic-bezier(0.25, 1, 0.5, 1);

  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(18rpx) saturate(1.24);
  -webkit-backdrop-filter: blur(18rpx) saturate(1.24);
  /* #endif */

  &--visible {
    opacity: 1;
    pointer-events: auto;
    transform: translate3d(0, 0, 0) scale(1);
  }

  &:active {
    transform: translate3d(0, 0, 0) scale(0.94);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.86),
      0 14rpx 34rpx rgba(59, 89, 255, 0.18);
  }
}

.s-back-to-top__orb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background:
    radial-gradient(circle at 32% 22%, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0) 42%),
    linear-gradient(145deg, rgba(59, 89, 255, 0.12), rgba(143, 112, 255, 0.08));
}

.s-back-to-top__orb::after {
  content: '';
  position: absolute;
  inset: 5rpx;
  border-radius: inherit;
  border: 1rpx solid rgba(255, 255, 255, 0.58);
  pointer-events: none;
}

.s-back-to-top__label {
  color: var(--sl-text-muted);
  font-size: 17rpx;
  font-weight: 800;
  letter-spacing: 0.12em;
  line-height: 1;
}
</style>
