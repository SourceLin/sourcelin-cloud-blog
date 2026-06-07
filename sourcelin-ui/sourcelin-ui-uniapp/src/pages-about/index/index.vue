<template>
  <view class="about s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading && !about" text="正在整理站点信息..." />
    <view v-if="about" class="about__hero s-card">
      <view class="about__brand">
        <image
          class="about__logo"
          :src="displayLogoUrl"
          mode="aspectFit"
          @error="onLogoError"
        />
        <view class="about__name">{{ siteName }}</view>
        <view v-if="siteSummary" class="about__summary">{{ siteSummary }}</view>
      </view>
    </view>

    <view v-if="siteFacts.length" class="about__section s-card">
      <view class="about__section-title">站点信息</view>
      <view class="about__facts">
        <view v-for="item in siteFacts" :key="item.label" class="about__fact s-card">
          <view class="about__fact-label">{{ item.label }}</view>
          <view class="about__fact-value s-ellipsis">{{ item.value }}</view>
        </view>
      </view>
    </view>

    <view v-if="contactItems.length" class="about__section s-card">
      <view class="about__section-title">联系与入口</view>
      <view class="about__contacts">
        <view v-for="item in contactItems" :key="item.key" class="about__contact s-card">
          <view class="about__contact-label">{{ item.label }}</view>
          <view class="about__contact-value s-ellipsis">{{ item.value }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { useAboutPage } from '../modules/site/composables/useAboutPage';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { DEFAULT_BRAND_LOGO } from '@/utils/url';
import { useThemeStore } from '@/stores/theme';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

const themeStore = useThemeStore();
const { guard } = useMiniAccess();
const {
  about,
  loading,
  siteName,
  siteSummary,
  logoUrl,
  contactItems,
  siteFacts,
  loadAboutPage
} = useAboutPage();

const logoLoadFailed = ref(false);

const displayLogoUrl = computed(() => {
  if (logoLoadFailed.value) {
    return DEFAULT_BRAND_LOGO;
  }
  return logoUrl.value;
});

function onLogoError(): void {
  logoLoadFailed.value = true;
}

watch(logoUrl, () => {
  logoLoadFailed.value = false;
});

watch([about, siteName], ([currentAbout, currentSiteName]) => {
  applyH5Seo({
    title: buildSeoTitle('关于本站', currentSiteName),
    description: extractSeoSummary(
      currentAbout?.bio,
      currentAbout?.summary,
      `${currentSiteName} 的站点介绍、作者信息与联系入口。`
    ),
    keywords: [currentSiteName, '关于本站', '博客', '作者', '联系']
  });
}, { immediate: true });

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad(() => {
  if (!guard('aboutEnabled')) {
    return;
  }
  void loadAboutPage();
});
</script>

<style lang="scss" scoped>
.about {
  min-height: 100vh;

  &__hero {
    text-align: center;
  }

  &__brand {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $space-xs;
  }

  &__logo {
    display: block;
    width: 128rpx;
    height: 128rpx;
    border-radius: $radius-lg;
  }

  &__name {
    font-size: 38rpx;
    font-weight: 800;
    line-height: 1.25;
  }

  &__summary {
    margin-top: $space-sm;
    max-width: 620rpx;
    color: $color-text-secondary;
    font-size: 26rpx;
    line-height: 1.75;
    text-align: center;
  }

  &__section {
    margin-top: $space-md;
  }

  &__section-title {
    font-size: 30rpx;
    font-weight: 700;
    margin-bottom: $space-sm;
  }

  &__contacts {
    display: flex;
    flex-direction: column;
    gap: $space-sm;
  }

  &__contact {
    padding: $space-md;
  }

  &__contact-label {
    font-size: 26rpx;
    font-weight: 600;
    color: $color-text;
  }

  &__contact-value {
    margin-top: 8rpx;
    font-size: 24rpx;
    color: $color-text-secondary;
    line-height: 1.6;
  }

  &__facts {
    display: flex;
    flex-direction: column;
    gap: $space-sm;
  }

  &__fact {
    padding: $space-md;
  }

  &__fact-label {
    font-size: 26rpx;
    font-weight: 600;
    color: $color-text;
  }

  &__fact-value {
    margin-top: 8rpx;
    font-size: 24rpx;
    color: $color-text-secondary;
    line-height: 1.6;
  }
}
</style>
