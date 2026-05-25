<template>
  <view class="about s-container">
    <s-loading :visible="loading && !about" text="正在整理站点信息..." />
    <view v-if="about" class="about__hero s-card">
      <image v-if="about.avatar" class="about__avatar" :src="normalizeAssetUrl(about.avatar)" mode="aspectFill" />
      <view v-else class="about__avatar about__avatar--placeholder">SL</view>
      <view class="about__name">{{ about.webName || 'Sourcelin Blog' }}</view>
      <view class="about__author">{{ about.author || 'Sourcelin' }}</view>
      <view class="about__bio">{{ about.bio || about.summary || '记录内容、灵感与轻社区互动。' }}</view>
    </view>

    <view class="about__section s-card">
      <view class="about__section-title">站点说明</view>
      <view class="about__section-text">{{ about?.summary || 'Sourcelin Blog 移动端聚焦阅读、互动和回访体验。' }}</view>
    </view>

    <view class="about__section s-card">
      <view class="about__section-title">联系与入口</view>
      <view class="about__links">
        <view v-if="about?.github" class="about__link sl-button sl-button--secondary sl-button--sm" @tap="copyValue(about.github, 'GitHub 地址已复制')">GitHub</view>
        <view v-if="about?.gitee" class="about__link sl-button sl-button--secondary sl-button--sm" @tap="copyValue(about.gitee, 'Gitee 地址已复制')">Gitee</view>
        <view v-if="about?.email" class="about__link sl-button sl-button--secondary sl-button--sm" @tap="copyValue(about.email, '邮箱已复制')">邮箱</view>
        <view v-if="about?.wechat" class="about__link sl-button sl-button--secondary sl-button--sm" @tap="copyValue(about.wechat, '微信号已复制')">微信</view>
      </view>
    </view>

    <view v-if="about?.notices?.length" class="about__section s-card">
      <view class="about__section-title">站点公告</view>
      <view v-for="(notice, index) in about.notices" :key="index" class="about__notice">{{ notice }}</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchAboutInfo } from '@/modules/site/api/site.api';
import type { AboutInfo } from '@/modules/site/types/site';
import { showSuccessToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';

const about = ref<AboutInfo | null>(null);
const loading = ref(false);

onLoad(() => {
  loadAbout();
});

async function loadAbout(): Promise<void> {
  loading.value = true;
  try {
    about.value = await fetchAboutInfo();
  } finally {
    loading.value = false;
  }
}

function copyValue(value: string, message: string): void {
  uni.setClipboardData({
    data: value,
    success: () => showSuccessToast(message)
  });
}
</script>

<style lang="scss" scoped>
.about {
  min-height: 100vh;

  &__hero {
    text-align: center;
  }

  &__avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    margin: 0 auto $space-md;
  }

  &__avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(31, 111, 235, 0.14), rgba(111, 143, 255, 0.22));
    color: $color-primary;
    font-size: 34rpx;
    font-weight: 700;
  }

  &__name {
    font-size: 38rpx;
    font-weight: 800;
  }

  &__author {
    margin-top: 10rpx;
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__bio {
    margin-top: $space-md;
    color: $color-text-secondary;
    font-size: 26rpx;
    line-height: 1.8;
  }

  &__section {
    margin-top: $space-md;
  }

  &__section-title {
    font-size: 30rpx;
    font-weight: 700;
    margin-bottom: $space-sm;
  }

  &__section-text,
  &__notice {
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.75;
  }

  &__notice + &__notice {
    margin-top: 12rpx;
  }

  &__links {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
  }

  &__link {
    min-width: 120rpx;
  }
}
</style>
