<template>
  <view
    v-if="visible"
    class="s-loading"
    :class="{
      's-loading--full': fullpage,
      's-loading--inline': !fullpage,
      's-loading--compact': compact
    }"
  >
    <view class="s-loading__container">
      <view class="s-loading__spinner">
        <!-- 氛围弥散底光 -->
        <view class="s-loading__glow" />
        <!-- 环形旋转玻璃轨道 -->
        <view class="s-loading__track" />
        <!-- 炫彩极光发光头 -->
        <view class="s-loading__ring" />
        <!-- 中心微光呼吸 Logo 图标 -->
        <image class="s-loading__logo" src="/static/logo/logo.png" mode="aspectFit" />
      </view>
      <text class="s-loading__text">{{ text }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    visible?: boolean;
    text?: string;
    fullpage?: boolean;
    compact?: boolean;
  }>(),
  {
    visible: false,
    text: '正在同步灵感...',
    fullpage: true,
    compact: false
  }
);
</script>

<style lang="scss" scoped>
.s-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  transition: opacity 0.3s ease;

  &--full {
    position: fixed;
    inset: 0;
    width: 100vw;
    height: 100vh;
    background:
      radial-gradient(circle at -10% 10%, var(--sl-glow-a), rgba(255, 255, 255, 0) 34%),
      radial-gradient(circle at 110% 30%, var(--sl-glow-b), rgba(255, 255, 255, 0) 34%),
      var(--sl-page-bg, #f8fafc);
  }

  &--inline {
    position: relative;
    width: 100%;
    padding: 40rpx 0;
  }

  &--compact {
    padding: 20rpx 0 8rpx;
  }

  &__container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 38rpx;
  }

  &__spinner {
    position: relative;
    width: 154rpx;
    height: 154rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &--compact &__container {
    gap: 18rpx;
  }

  &--compact &__spinner {
    width: 88rpx;
    height: 88rpx;
  }

  &__glow {
    position: absolute;
    inset: -20rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(59, 89, 255, 0.18) 0%, rgba(143, 112, 255, 0.04) 62%, rgba(255, 255, 255, 0) 100%);
    filter: blur(8rpx);
  }

  &__track {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    border: 5rpx solid var(--sl-border-glass, rgba(255, 255, 255, 0.78));
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 12rpx 30rpx rgba(17, 24, 39, 0.03);
  }

  &__ring {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    border: 5rpx solid transparent;
    border-top-color: #3b59ff;
    border-right-color: #8f70ff;
    animation: s-spin 1s cubic-bezier(0.55, 0.085, 0.68, 0.53) infinite;
    filter: drop-shadow(0 0 8rpx rgba(59, 89, 255, 0.44));
  }

  &__logo {
    width: 58rpx;
    height: 58rpx;
    z-index: 2;
    animation: s-breath 2s ease-in-out infinite;
  }

  &--compact &__logo {
    width: 34rpx;
    height: 34rpx;
  }

  &__text {
    color: var(--sl-text-sub);
    font-size: 26rpx;
    font-weight: 700;
    letter-spacing: 0.04em;
    animation: s-pulse 2s ease-in-out infinite;
  }

  &--compact &__text {
    font-size: 22rpx;
    font-weight: 600;
  }
}

@keyframes s-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes s-breath {
  0%, 100% {
    transform: scale(0.92);
    opacity: 0.88;
  }
  50% {
    transform: scale(1.08);
    opacity: 1;
  }
}

@keyframes s-pulse {
  0%, 100% {
    opacity: 0.62;
  }
  50% {
    opacity: 1;
  }
}
</style>
