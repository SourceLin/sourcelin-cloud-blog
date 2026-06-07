<template>
  <view class="links s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading && items.length === 0" text="正在加载友链..." />
    <view class="links__header s-card">
      <view class="links__title">友情链接</view>
      <view class="links__desc">收录值得访问的伙伴站点，便于在移动端快速回访优秀内容。</view>
    </view>

    <s-empty
      v-if="!loading && items.length === 0"
      title="暂无友情链接"
      text="暂时还没有公开展示的伙伴站点。"
    />

    <view v-else class="links__list">
      <view v-for="item in items" :key="item.id" class="links__item s-card">
        <view class="links__item-head">
          <image v-if="item.avatar" class="links__avatar" :src="normalizeAssetUrl(item.avatar)" mode="aspectFill" />
          <view v-else class="links__avatar links__avatar--placeholder">{{ item.name.slice(0, 1) }}</view>
          <view class="links__info">
            <view class="links__name">{{ item.name }}</view>
            <view class="links__url s-ellipsis">{{ item.url }}</view>
          </view>
        </view>
        <view class="links__desc-text s-ellipsis-2">{{ item.description || '伙伴站点简介。' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { fetchFriendLinks } from '../modules/site/api/site.api';
import type { FriendLinkItem } from '@/modules/site/types/site';
import { normalizeAssetUrl } from '@/utils/url';
import { useThemeStore } from '@/stores/theme';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

const themeStore = useThemeStore();
const { guard } = useMiniAccess();
const items = ref<FriendLinkItem[]>([]);
const loading = ref(false);

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad(() => {
  if (!guard('friendLinkEnabled')) {
    return;
  }
  loadLinks();
});

async function loadLinks(): Promise<void> {
  loading.value = true;
  try {
    items.value = await fetchFriendLinks();
  } finally {
    loading.value = false;
  }
}
</script>

<style lang="scss" scoped>
.links {
  min-height: 100vh;

  &__header,
  &__apply {
    margin-bottom: $space-md;
  }

  &__title,
  &__apply-title {
    font-size: 34rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
    margin-bottom: $space-md;
  }

  &__item-head {
    display: flex;
    align-items: center;
    gap: $space-md;
  }

  &__avatar {
    width: 84rpx;
    height: 84rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(31, 111, 235, 0.12);
    color: $color-primary;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__name {
    font-size: 28rpx;
    font-weight: 700;
  }

  &__url,
  &__desc-text {
    color: $color-text-secondary;
    font-size: 24rpx;
  }

  &__url {
    margin-top: 6rpx;
  }

  &__desc-text {
    margin-top: 14rpx;
    line-height: 1.7;
  }

  &__field {
    margin-top: 16rpx;
  }

  &__field--textarea {
    padding-bottom: 8rpx;
  }

  &__input,
  &__textarea {
    @include sl-input;
  }

  &__input {
    min-height: $input-min-height;
    line-height: $input-line-height;
  }

  &__textarea {
    min-height: 140rpx;
    line-height: 1.7;
  }

  &__submit {
    height: 84rpx;
    line-height: 84rpx;
    margin-top: $space-lg;
    border-radius: 999rpx;
    background: linear-gradient(135deg, $color-primary, #6f8fff);
    color: #fff;
    font-size: 28rpx;
    font-weight: 600;
  }
}
</style>
