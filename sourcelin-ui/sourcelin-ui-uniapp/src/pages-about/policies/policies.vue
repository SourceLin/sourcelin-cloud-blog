<template>
  <view class="policies s-container" :class="themeStore.themeClass">
    <view class="policies__hero s-card">
      <!--      <view class="policies__chip">政策</view>-->
      <view class="policies__title">协议与政策</view>
      <view class="policies__subtitle">查看小程序当前使用的用户协议与隐私政策，登录与授权场景会统一引用这里的内容。</view>
    </view>

    <view class="policies__list">
      <view
        v-for="article in articles"
        :key="article.type"
        class="policies__item s-card s-card--interactive"
        @tap="openPolicy(article.type)"
      >
        <view class="policies__item-main">
          <view class="policies__item-icon">
            <uni-icons :type="article.icon" size="26" :color="article.iconColor" />
          </view>
          <view class="policies__item-copy">
            <view class="policies__item-title">{{ article.title }}</view>
            <view class="policies__item-desc">{{ article.subtitle }}</view>
          </view>
        </view>
        <uni-icons class="policies__item-arrow" type="right" size="18" color="currentColor" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { LEGAL_ARTICLES, type LegalArticleType } from '../modules/site/constants/legal';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

const themeStore = useThemeStore();
const { guard } = useMiniAccess();
const articles = computed(() => LEGAL_ARTICLES);

applyH5Seo({
  title: buildSeoTitle('协议与政策'),
  description: extractSeoSummary('查看小程序当前使用的用户协议与隐私政策。'),
  keywords: ['协议与政策', '用户协议', '隐私政策', '小程序']
});

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad(() => {
  guard('policyEnabled');
});

function openPolicy(type: LegalArticleType): void {
  uni.navigateTo({ url: `/pages-about/policy/policy?type=${type}` });
}
</script>

<style lang="scss" scoped>
.policies {
  min-height: 100vh;

  &__hero {
    margin-bottom: $space-md;
  }

  &__chip {
    width: fit-content;
    min-height: 46rpx;
    padding: 0 18rpx;
    display: inline-flex;
    align-items: center;
    border-radius: 999rpx;
    background: rgba(59, 89, 255, 0.12);
    color: var(--sl-color-primary);
    font-size: 22rpx;
    font-weight: 700;
  }

  &__title {
    color: var(--sl-text-main);
    font-size: 40rpx;
    font-weight: 800;
  }

  &__subtitle {
    margin-top: 12rpx;
    color: var(--sl-text-sub);
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 18rpx;
  }

  &__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 18rpx;
  }

  &__item-main {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    gap: 18rpx;
  }

  &__item-icon {
    width: 88rpx;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 26rpx;
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg));
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 8rpx 18rpx rgba(17, 24, 39, 0.03);
    flex-shrink: 0;
  }

  &__item-copy {
    flex: 1;
    min-width: 0;
  }

  &__item-title {
    color: var(--sl-text-main);
    font-size: 29rpx;
    font-weight: 800;
  }

  &__item-desc {
    margin-top: 10rpx;
    color: var(--sl-text-sub);
    font-size: 23rpx;
    line-height: 1.65;
  }

  &__item-arrow {
    color: var(--sl-text-muted);
    flex-shrink: 0;
  }
}
</style>
