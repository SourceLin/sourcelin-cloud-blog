<template>
  <view class="s-inline-loading" :class="{ 's-inline-loading--light': light }">
    <view class="s-inline-loading__spinner" />
    <text v-if="text" class="s-inline-loading__text">{{ text }}</text>
  </view>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    text?: string;
    light?: boolean;
  }>(),
  {
    text: '',
    light: false
  }
);
</script>

<style lang="scss" scoped>
.s-inline-loading {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  color: var(--sl-text-sub);

  &--light {
    color: #fff;
  }

  &__spinner {
    width: 24rpx;
    height: 24rpx;
    border-radius: 50%;
    border: 3rpx solid currentColor;
    border-right-color: transparent;
    opacity: 0.88;
    animation: s-inline-loading-spin 0.75s linear infinite;
  }

  &__text {
    font-size: 24rpx;
    font-weight: 600;
    line-height: 1;
  }
}

@keyframes s-inline-loading-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
