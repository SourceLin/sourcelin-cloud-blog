<template>
  <view v-if="visible" class="s-theme-sheet">
    <!-- 遮罩底层 -->
    <view class="s-theme-sheet__mask" @tap="close" />
    
    <!-- 弹层容器主体 -->
    <view class="s-theme-sheet__shell" :class="themeStore.themeClass">
      <view class="s-theme-sheet__header">
        <text class="s-theme-sheet__title">外观模式</text>
        <text class="s-theme-sheet__subtitle">选择更适合当前环境的阅读氛围</text>
      </view>

      <view class="s-theme-sheet__list">
        <!-- 跟随系统 -->
        <view 
          class="s-theme-sheet__item" 
          :class="{ 's-theme-sheet__item--active': themeStore.mode === 'system' }"
          @tap="select('system')"
        >
          <view class="s-theme-sheet__item-body">
            <text class="s-theme-sheet__item-title">跟随系统</text>
            <text class="s-theme-sheet__item-desc">根据手机或微信当前外观自动变化（当前使用：{{ themeStore.resolvedTheme === 'dark' ? '深色' : '浅色' }}）</text>
          </view>
          <view v-if="themeStore.mode === 'system'" class="s-theme-sheet__item-check">
            <uni-icons type="checkmarkempty" size="18" color="var(--sl-color-primary)" />
          </view>
        </view>

        <!-- 浅色模式 -->
        <view 
          class="s-theme-sheet__item" 
          :class="{ 's-theme-sheet__item--active': themeStore.mode === 'light' }"
          @tap="select('light')"
        >
          <view class="s-theme-sheet__item-body">
            <text class="s-theme-sheet__item-title">浅色模式</text>
            <text class="s-theme-sheet__item-desc">清透明亮，适合日间或户外浏览</text>
          </view>
          <view v-if="themeStore.mode === 'light'" class="s-theme-sheet__item-check">
            <uni-icons type="checkmarkempty" size="18" color="var(--sl-color-primary)" />
          </view>
        </view>

        <!-- 深色模式 -->
        <view 
          class="s-theme-sheet__item" 
          :class="{ 's-theme-sheet__item--active': themeStore.mode === 'dark' }"
          @tap="select('dark')"
        >
          <view class="s-theme-sheet__item-body">
            <text class="s-theme-sheet__item-title">深色模式</text>
            <text class="s-theme-sheet__item-desc">夜色玻璃，柔和偏光，适合弱光或长时间阅读</text>
          </view>
          <view v-if="themeStore.mode === 'dark'" class="s-theme-sheet__item-check">
            <uni-icons type="checkmarkempty" size="18" color="var(--sl-color-primary)" />
          </view>
        </view>
      </view>

      <view class="s-theme-sheet__footer">
        <view class="s-theme-sheet__cancel-btn sl-button sl-button--secondary" @tap="close">取消</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useThemeStore, type ThemeMode } from '@/stores/theme';

defineProps<{
  visible?: boolean;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const themeStore = useThemeStore();

function select(mode: ThemeMode): void {
  themeStore.setMode(mode);
  // 点击轻微震动反馈
  try {
    uni.vibrateShort();
  } catch (_e) {
    // 忽略不支持震动环境的报错
  }
  close();
}

function close(): void {
  emit('close');
}
</script>

<style lang="scss" scoped>
.s-theme-sheet {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  z-index: 990;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;

  &__mask {
    position: absolute;
    inset: 0;
    background: rgba(8, 13, 24, 0.46);
    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    /* #endif */
  }

  &__shell {
    position: relative;
    z-index: 1;
    width: 100%;
    box-sizing: border-box;
    border-radius: 46rpx 46rpx 0 0;
    padding: 44rpx 44rpx calc(44rpx + env(safe-area-inset-bottom));
    background: linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)), var(--sl-bg-glass-pure);
    border-top: 1rpx solid var(--sl-border-glass);
    box-shadow: 0 -12rpx 48rpx rgba(17, 24, 39, 0.08);
    transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), border-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);
    
    &.sl-theme--dark {
      background: linear-gradient(145deg, rgba(18, 27, 46, 0.94), rgba(18, 27, 46, 0.86)), rgba(18, 27, 46, 0.72);
      box-shadow: 0 -12rpx 48rpx rgba(0, 0, 0, 0.32);
    }
  }

  &__header {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
    margin-bottom: 38rpx;
    text-align: center;
  }

  &__title {
    color: var(--sl-text-main);
    font-size: 34rpx;
    font-weight: 800;
    letter-spacing: 0.02em;
  }

  &__subtitle {
    color: var(--sl-text-muted);
    font-size: 24rpx;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 22rpx;
  }

  &__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx 36rpx;
    border-radius: 28rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-border-glass);
    transition: transform 0.2s ease, background-color 0.2s ease, border-color 0.2s ease;

    .sl-theme--dark & {
      background: rgba(18, 27, 46, 0.36);
    }

    &:active {
      transform: scale(0.985);
      background: rgba(59, 89, 255, 0.06);
    }

    &--active {
      border-color: var(--sl-color-primary);
      background: rgba(59, 89, 255, 0.05);
      
      .sl-theme--dark & {
        background: rgba(105, 129, 255, 0.06);
      }
    }
  }

  &__item-body {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
    flex: 1;
    min-width: 0;
  }

  &__item-title {
    color: var(--sl-text-main);
    font-size: 28rpx;
    font-weight: 700;
  }

  &__item-desc {
    color: var(--sl-text-sub);
    font-size: 22rpx;
    line-height: 1.4;
  }

  &__item-check {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    margin-left: 20rpx;
  }

  &__footer {
    margin-top: 44rpx;
  }

  &__cancel-btn {
    text-align: center;
    font-weight: 700;
  }
}
</style>
