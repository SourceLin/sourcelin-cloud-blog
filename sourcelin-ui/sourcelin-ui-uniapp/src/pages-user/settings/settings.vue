<template>
  <view class="settings s-container" :class="themeStore.themeClass">
    <view class="settings__hero s-card">
      <view class="settings__title">体验设置</view>
      <view class="settings__desc">控制外观模式、个性化推荐和行为统计，让小程序更符合你的使用习惯。</view>
    </view>

    <!-- 外观卡片分组 -->
    <view class="settings__section s-card">
      <view class="settings__section-header">
        <view class="settings__row-title">外观模式</view>
        <view class="settings__row-desc">选择页面与阅读内容的显示风格</view>
      </view>

      <view class="settings__segmented">
        <view
          class="settings__segmented-item"
          :class="{ 'settings__segmented-item--active': themeStore.mode === 'system' }"
          @tap="setMode('system')"
        >
          <text>跟随系统</text>
        </view>
        <view
          class="settings__segmented-item"
          :class="{ 'settings__segmented-item--active': themeStore.mode === 'light' }"
          @tap="setMode('light')"
        >
          <text>浅色</text>
        </view>
        <view
          class="settings__segmented-item"
          :class="{ 'settings__segmented-item--active': themeStore.mode === 'dark' }"
          @tap="setMode('dark')"
        >
          <text>深色</text>
        </view>
      </view>

      <view class="settings__mode-info">
        当前状态：已选择 {{ themeStore.mode === 'system' ? '跟随系统' : (themeStore.mode === 'light' ? '浅色模式' : '深色模式') }}，正在应用 {{ themeStore.resolvedTheme === 'dark' ? '深色' : '浅色' }} 外观
      </view>
    </view>

    <view class="settings__section s-card">
      <view class="settings__row" @tap="toggleRecommendation">
        <view>
          <view class="settings__row-title">个性化推荐</view>
          <view class="settings__row-desc">根据最近阅读与搜索偏好，调整首页和发现页内容排序。</view>
        </view>
        <view class="settings__switch" :class="{ 'settings__switch--active': preferences.personalizedRecommendationEnabled }" />
      </view>

      <view class="settings__row" @tap="toggleAnalytics">
        <view>
          <view class="settings__row-title">行为统计</view>
          <view class="settings__row-desc">帮助站点统计阅读、搜索、发布与互动情况，用于后续优化体验。</view>
        </view>
        <view class="settings__switch" :class="{ 'settings__switch--active': preferences.analyticsEnabled }" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getUserPreferences, updateUserPreferences } from '@/shared/utils/preferences';
import { useThemeStore, type ThemeMode } from '@/stores/theme';
import { showSuccessToast } from '@/utils/feedback';

const preferences = reactive(getUserPreferences());
const themeStore = useThemeStore();

onShow(() => {
  themeStore.syncNativeArea();
});

function sync(next = getUserPreferences()): void {
  preferences.analyticsEnabled = next.analyticsEnabled;
  preferences.personalizedRecommendationEnabled = next.personalizedRecommendationEnabled;
  preferences.nightReadingEnabled = next.nightReadingEnabled;
}

function setMode(mode: ThemeMode): void {
  themeStore.setMode(mode);
  try {
    uni.vibrateShort();
  } catch (_e) {
    // 忽略不支持震动环境的报错
  }
  showSuccessToast('外观模式更新成功');
}

function toggleRecommendation(): void {
  sync(updateUserPreferences({
    personalizedRecommendationEnabled: !preferences.personalizedRecommendationEnabled
  }));
  showSuccessToast(preferences.personalizedRecommendationEnabled ? '已开启个性化推荐' : '已关闭个性化推荐');
}

function toggleAnalytics(): void {
  sync(updateUserPreferences({
    analyticsEnabled: !preferences.analyticsEnabled
  }));
  showSuccessToast(preferences.analyticsEnabled ? '已开启行为统计' : '已关闭行为统计');
}
</script>

<style lang="scss" scoped>
.settings {
  min-height: 100vh;
  transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

  &__hero {
    margin-bottom: $space-md;
  }

  &__title {
    font-size: 38rpx;
    font-weight: 800;
    color: var(--sl-text-main);
  }

  &__desc {
    margin-top: 12rpx;
    color: var(--sl-text-sub);
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__section {
    margin-bottom: $space-md;
  }

  &__section-header {
    margin-bottom: 24rpx;
  }

  &__segmented {
    display: flex;
    background: rgba(148, 163, 184, 0.12);
    border-radius: 20rpx;
    padding: 8rpx;
    box-sizing: border-box;

    .sl-theme--dark & {
      background: rgba(8, 13, 24, 0.36);
    }
  }

  &__segmented-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 72rpx;
    border-radius: 14rpx;
    font-size: 26rpx;
    font-weight: 700;
    color: var(--sl-text-sub);
    transition: transform 0.2s cubic-bezier(0.25, 0.8, 0.25, 1), background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;

    &:active {
      transform: scale(0.97);
    }
  }

  &__segmented-item--active {
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    box-shadow: 0 4rpx 12rpx rgba(59, 89, 255, 0.22);
  }

  &__mode-info {
    margin-top: 18rpx;
    color: var(--sl-text-muted);
    font-size: 22rpx;
    line-height: 1.4;
  }

  &__row + &__row {
    margin-top: 12rpx;
    padding-top: 28rpx;
    border-top: 1rpx solid rgba(226, 232, 240, 0.12);
  }

  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24rpx;
  }

  &__row-title {
    color: var(--sl-text-main);
    font-size: 28rpx;
    font-weight: 700;
  }

  &__row-desc {
    margin-top: 8rpx;
    color: var(--sl-text-sub);
    font-size: 23rpx;
    line-height: 1.7;
  }

  &__switch {
    position: relative;
    width: 92rpx;
    height: 54rpx;
    border-radius: 999rpx;
    background: rgba(148, 163, 184, 0.34);
    flex-shrink: 0;
    transition: background-color 0.2s ease;
  }

  &__switch::after {
    content: '';
    position: absolute;
    top: 5rpx;
    left: 5rpx;
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 6rpx 16rpx rgba(17, 24, 39, 0.12);
    transition: left 0.2s ease, background-color 0.2s ease;
  }

  &__switch--active {
    background: rgba(59, 89, 255, 0.42);
  }

  &__switch--active::after {
    left: 43rpx;
  }
}
</style>
