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

      <view class="settings__row" @tap="requestSubscribeAuthorization">
        <view>
          <view class="settings__row-title">订阅消息</view>
          <view class="settings__row-desc">授权后可接收评论回复等小程序服务通知，当前模板和最近授权状态会保存在本地，便于后续召回能力使用。</view>
        </view>
        <view class="settings__action-chip">授权</view>
      </view>

      <view v-if="subscribeTemplates.length > 0" class="settings__subscription-panel">
        <view
          v-for="template in subscribeTemplates"
          :key="template.id"
          class="settings__subscription-item"
        >
          <view class="settings__subscription-head">
            <view class="settings__subscription-name">{{ template.name }}</view>
            <view class="settings__subscription-status" :class="`settings__subscription-status--${resolveTemplateStatus(template.id)}`">
              {{ resolveTemplateStatusLabel(template.id) }}
            </view>
          </view>
          <view class="settings__subscription-scene">{{ template.scene }}</view>
          <view v-if="template.keywords.length > 0" class="settings__subscription-keywords">
            {{ template.keywords.join(' / ') }}
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { env } from '@/config/env';
import { createSubscribeAuthorization, type SubscribeAuthorizationStatus } from '../modules/subscription/api/subscription.api';
import { resolveSubscriptionTemplateMeta } from '../modules/subscription/constants/templates';
import { getSubscriptionStatusMap, saveSubscriptionStatus } from '../modules/subscription/utils/subscription-status';
import { getUserPreferences, updateUserPreferences } from '@/shared/utils/preferences';
import { useThemeStore, type ThemeMode } from '@/stores/theme';
import { useUserStore } from '@/stores/user';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';

const preferences = reactive(getUserPreferences());
const themeStore = useThemeStore();
const userStore = useUserStore();
const subscriptionStatusMap = reactive(getSubscriptionStatusMap());
const subscribeTemplates = computed(() =>
  env.subscribeMessageTemplateIds
    .filter(Boolean)
    .map((templateId) => resolveSubscriptionTemplateMeta(templateId))
);

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

function requestSubscribeAuthorization(): void {
  const templateIds = env.subscribeMessageTemplateIds.filter(Boolean);
  if (templateIds.length === 0) {
    showInfoToast('暂未配置订阅消息模板');
    return;
  }

  if (!userStore.isLoggedIn) {
    showInfoToast('登录后可授权订阅消息');
    uni.navigateTo({ url: '/pages-user/login/login' });
    return;
  }

  // #ifdef MP-WEIXIN
  void requestWechatSubscribeAuthorization(templateIds);
  // #endif

  // #ifndef MP-WEIXIN
  showInfoToast('当前端暂不支持订阅消息');
  // #endif
}

// #ifdef MP-WEIXIN
async function requestWechatSubscribeAuthorization(templateIds: string[]): Promise<void> {
  let result: Record<string, string>;
  try {
    result = await requestWechatSubscribeMessage(templateIds);
  } catch {
    templateIds.forEach((templateId) => updateTemplateStatus(templateId, 'fail'));
    showInfoToast('订阅授权未完成');
    return;
  }

  const statuses = templateIds.map((templateId) => ({
    templateId,
    status: normalizeSubscribeStatus(result[templateId])
  }));
  statuses.forEach(({ templateId, status }) => updateTemplateStatus(templateId, status));

  const reportResults = await Promise.allSettled(
    statuses.map(({ templateId, status }) => reportSubscribeResult(templateId, status))
  );
  const reportFailed = reportResults.some((item) => item.status === 'rejected');
  showSubscribeResultToast(statuses.map((item) => item.status), reportFailed);
}

function requestWechatSubscribeMessage(templateIds: string[]): Promise<Record<string, string>> {
  return new Promise((resolve, reject) => {
    uni.requestSubscribeMessage({
      tmplIds: templateIds,
      success: (res) => resolve(res as unknown as Record<string, string>),
      fail: reject
    });
  });
}
// #endif

function normalizeSubscribeStatus(value?: string): SubscribeAuthorizationStatus {
  if (value === 'accept' || value === 'reject' || value === 'ban' || value === 'filter') {
    return value;
  }
  return 'fail';
}

function showSubscribeResultToast(statuses: SubscribeAuthorizationStatus[], reportFailed: boolean): void {
  const hasAccept = statuses.includes('accept');
  const allReject = statuses.length > 0 && statuses.every((status) => status === 'reject');
  const allBlocked = statuses.length > 0 && statuses.every((status) => status === 'ban' || status === 'filter');
  const allFail = statuses.length > 0 && statuses.every((status) => status === 'fail');

  if (allFail) {
    showInfoToast('订阅授权未完成');
    return;
  }
  if (allBlocked) {
    showInfoToast('当前账号无法接收订阅消息，请检查微信通知设置');
    return;
  }
  if (allReject) {
    showInfoToast('你已拒绝订阅，可在设置中重新授权');
    return;
  }
  if (hasAccept) {
    if (reportFailed) {
      showInfoToast('授权已完成，同步记录失败，请稍后重试');
      return;
    }
    showSuccessToast('已开启订阅通知');
    return;
  }
  if (reportFailed) {
    showInfoToast('部分授权已处理，同步记录失败');
    return;
  }
  showInfoToast('部分模板授权完成');
}

function reportSubscribeResult(templateId: string, authorizationStatus: SubscribeAuthorizationStatus): Promise<void> {
  return createSubscribeAuthorization({
    templateId,
    authorizationStatus,
    scene: 'settings',
    platform: 'mp-weixin'
  });
}

function updateTemplateStatus(templateId: string, authorizationStatus: SubscribeAuthorizationStatus): void {
  const next = saveSubscriptionStatus(templateId, authorizationStatus);
  Object.keys(subscriptionStatusMap).forEach((key) => {
    if (!next[key]) delete subscriptionStatusMap[key];
  });
  Object.assign(subscriptionStatusMap, next);
}

function resolveTemplateStatus(templateId: string): SubscribeAuthorizationStatus | 'pending' {
  return subscriptionStatusMap[templateId]?.authorizationStatus || 'pending';
}

function resolveTemplateStatusLabel(templateId: string): string {
  const status = resolveTemplateStatus(templateId);
  if (status === 'accept') return '已授权';
  if (status === 'reject') return '已拒绝';
  if (status === 'ban') return '被封禁';
  if (status === 'filter') return '被过滤';
  if (status === 'fail') return '授权失败';
  return '待授权';
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
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0.82)),
      var(--sl-bg-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.96),
      0 8rpx 20rpx rgba(17, 24, 39, 0.04);
    border-radius: 999rpx;
    padding: 8rpx;
    box-sizing: border-box;

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */

    .sl-theme--dark & {
      background:
        linear-gradient(145deg, rgba(24, 27, 38, 0.92), rgba(18, 20, 28, 0.88));
      border-color: var(--sl-border-light);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.06),
        0 8rpx 20rpx rgba(0, 0, 0, 0.24);
    }
  }

  &__segmented-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 74rpx;
    border-radius: 999rpx;
    font-size: 26rpx;
    font-weight: 700;
    color: var(--sl-text-sub);
    position: relative;
    transition: transform 0.2s cubic-bezier(0.25, 0.8, 0.25, 1), background-color 0.24s ease, color 0.24s ease, box-shadow 0.24s ease;

    &:active {
      transform: scale(0.97);
    }
  }

  &__segmented-item--active {
    color: var(--sl-color-primary);
    background:
      linear-gradient(135deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      rgba(59, 89, 255, 0.08);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 10rpx 28rpx rgba(59, 89, 255, 0.14);

    .sl-theme--dark & {
      color: var(--sl-color-primary-soft);
      background: rgba(105, 129, 255, 0.08);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        inset 0 0 0 1rpx rgba(154, 176, 255, 0.1),
        0 8rpx 18rpx rgba(105, 129, 255, 0.08);
    }
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
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 2rpx 4rpx rgba(0, 0, 0, 0.05),
      0 1rpx 0 rgba(255, 255, 255, 0.9);
    flex-shrink: 0;
    transition: all 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

    &::after {
      content: '';
      position: absolute;
      top: 4rpx;
      left: 4rpx;
      width: 44rpx;
      height: 44rpx;
      border-radius: 50%;
      background: #ffffff;
      box-shadow:
        0 4rpx 10rpx rgba(17, 24, 39, 0.12),
        inset 0 1rpx 0 rgba(255, 255, 255, 0.9);
      transition: all 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);
    }

    &--active {
      background: rgba(59, 89, 255, 0.16);
      border-color: rgba(59, 89, 255, 0.32);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.1),
        0 8rpx 20rpx rgba(59, 89, 255, 0.12);

      &::after {
        left: 42rpx;
        background: var(--sl-color-primary);
        box-shadow:
          0 6rpx 16rpx rgba(59, 89, 255, 0.35),
          inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
      }
    }

    .sl-theme--dark & {
      background: rgba(8, 13, 24, 0.26);
      border-color: var(--sl-border-light);
      box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.24);

      &::after {
        background: #a8b2c8;
        box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.3);
      }

      &--active {
        background: rgba(105, 129, 255, 0.16);
        border-color: rgba(105, 129, 255, 0.32);
        box-shadow:
          inset 0 1rpx 0 rgba(255, 255, 255, 0.05),
          0 8rpx 20rpx rgba(105, 129, 255, 0.15);

        &::after {
          background: var(--sl-color-primary-soft);
          box-shadow:
            0 6rpx 16rpx rgba(105, 129, 255, 0.4),
            inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
        }
      }
    }
  }

  &__action-chip {
    min-width: 104rpx;
    height: 58rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    color: var(--sl-color-primary);
    font-size: 24rpx;
    font-weight: 700;
    background: rgba(59, 89, 255, 0.1);
    border: 1rpx solid rgba(59, 89, 255, 0.18);
    flex-shrink: 0;

    .sl-theme--dark & {
      background: rgba(105, 129, 255, 0.14);
      border-color: rgba(105, 129, 255, 0.24);
    }
  }

  &__subscription-panel {
    margin-top: 24rpx;
    display: flex;
    flex-direction: column;
    gap: 16rpx;
  }

  &__subscription-item {
    padding: 24rpx;
    border-radius: 24rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
  }

  &__subscription-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__subscription-name {
    color: var(--sl-text-main);
    font-size: 26rpx;
    font-weight: 700;
  }

  &__subscription-status {
    min-width: 104rpx;
    height: 48rpx;
    padding: 0 16rpx;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    font-size: 22rpx;
    font-weight: 700;
    color: var(--sl-text-sub);
    background: rgba(148, 163, 184, 0.14);
  }

  &__subscription-status--accept {
    color: #16a34a;
    background: rgba(22, 163, 74, 0.12);
  }

  &__subscription-status--reject,
  &__subscription-status--fail {
    color: #dc2626;
    background: rgba(220, 38, 38, 0.12);
  }

  &__subscription-status--ban,
  &__subscription-status--filter {
    color: #b45309;
    background: rgba(245, 158, 11, 0.14);
  }

  &__subscription-scene {
    margin-top: 10rpx;
    color: var(--sl-text-sub);
    font-size: 23rpx;
    line-height: 1.5;
  }

  &__subscription-keywords {
    margin-top: 12rpx;
    color: var(--sl-text-muted);
    font-size: 22rpx;
    line-height: 1.6;
  }
}
</style>
