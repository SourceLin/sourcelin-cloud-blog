<template>
  <view class="discover">
    <s-loading :visible="loading && hotArticles.length === 0" />
    <view class="discover__hero">
      <view class="discover__search sl-button sl-button--secondary" @tap="goSearch">
        <uni-icons type="search" size="18" color="currentColor" />
        <text>搜索文章、分类或标签</text>
      </view>
    </view>

    <view class="discover__content">
      <view class="discover__section discover__section--hot">
        <view class="discover__section-head">
          <view>
            <text class="discover__section-kicker">Trending</text>
            <text class="discover__section-title">热门文章</text>
          </view>
          <view class="discover__more sl-button sl-button--ghost sl-button--sm" @tap="goHotList">更多</view>
        </view>
        <s-empty v-if="!loading && hotArticles.length === 0" text="暂无热门文章" />
        <view
          v-for="(item, index) in hotArticles"
          :key="item.id"
          class="discover__hot"
          :class="{ 'discover__hot--lead': index === 0 }"
          @tap="goDetail(item.id)"
        >
          <view class="discover__rank" :class="'discover__rank--' + (index + 1)">{{ index + 1 }}</view>
          <view class="discover__hot-body">
            <view class="discover__hot-title s-ellipsis-2">{{ item.title }}</view>
            <view class="discover__hot-meta">
              <view class="discover__meta-chip">
                <uni-icons type="eye" size="14" color="currentColor" />
                <text>{{ item.viewCount || 0 }} 阅读</text>
              </view>
              <view class="discover__meta-chip discover__meta-chip--like">
                <uni-icons type="heart" size="14" color="currentColor" />
                <text>{{ item.likeCount || 0 }} 点赞</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="discover__section">
        <view class="discover__section-head">
          <view>
            <text class="discover__section-kicker">Categories</text>
            <text class="discover__section-title">核心分类</text>
          </view>
          <view class="discover__more sl-button sl-button--ghost sl-button--sm" @tap="goSearch">搜索</view>
        </view>
        <view v-if="categories.length > 0" class="discover__chips">
          <view
            v-for="item in categories"
            :key="item.id"
            class="discover__chip"
            @tap="goCategory(item.id)"
          >
            {{ item.name }}
          </view>
        </view>
        <s-empty v-else-if="!loading" text="暂无分类" />
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view class="s-liquid-tabbar">
      <view class="s-liquid-tabbar__shell">
        <view
          v-for="item in liquidTabItems"
          :key="item.path"
          class="s-liquid-tabbar__item"
          :class="{ 's-liquid-tabbar__item--active': item.path === activeTabPath }"
          @tap="switchLiquidTab(item.path, activeTabPath)"
        >
          <view class="s-liquid-tabbar__icon-wrap">
            <image
              class="s-liquid-tabbar__icon"
              :src="item.path === activeTabPath ? item.activeIcon : item.icon"
              mode="aspectFit"
            />
          </view>
          <text class="s-liquid-tabbar__text">{{ item.text }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onPageScroll, onShow } from '@dcloudio/uni-app';
import { fetchCategoryList, fetchHotArticles } from '@/modules/article/api/article.api';
import type { ArticleSummary, CategoryItem } from '@/modules/article/types/article';
import { hideNativeTabbar, liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';

const loading = ref(false);
const hotArticles = ref<ArticleSummary[]>([]);
const categories = ref<CategoryItem[]>([]);
const backToTopVisible = ref(false);
const activeTabPath = 'pages/discover/discover';

onShow(() => {
  hideNativeTabbar();
  if (hotArticles.value.length === 0 && categories.value.length === 0) {
    loadDiscover();
  }
});

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});

async function loadDiscover(): Promise<void> {
  loading.value = true;
  try {
    const [hot, categoryList] = await Promise.all([
      fetchHotArticles(1, 5),
      fetchCategoryList()
    ]);
    hotArticles.value = hot.items || [];
    categories.value = (categoryList || []).slice(0, 5);
  } finally {
    loading.value = false;
  }
}

function goSearch(): void {
  uni.navigateTo({ url: '/pages-article/search/search' });
}

function goHotList(): void {
  uni.navigateTo({ url: '/pages-article/list/list?orderBy=view_count' });
}

function goCategory(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/list/list?categoryId=${id}` });
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}
</script>

<style lang="scss" scoped>
.discover {
  --discover-primary: var(--sl-color-primary);
  --discover-primary-soft: var(--sl-color-primary-soft);
  --discover-text-main: var(--sl-text-main);
  --discover-text-sub: var(--sl-text-sub);
  --discover-glass-pure: var(--sl-bg-glass-pure);
  --discover-glass-tint: var(--sl-bg-glass-tint);
  --discover-glass-border: rgba(255, 255, 255, 0.72);
  --discover-glass-highlight: rgba(255, 255, 255, 0.86);
  --discover-shadow: rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  background:
    radial-gradient(circle at -14% 8%, var(--sl-glow-a), rgba(255, 255, 255, 0) 36%),
    radial-gradient(circle at 112% 24%, var(--sl-glow-b), rgba(255, 255, 255, 0) 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.86), rgba(245, 247, 250, 0.96) 430rpx),
    $color-bg;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));

  &__hero {
    position: relative;
    padding: 28rpx 30rpx 30rpx;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      right: -160rpx;
      top: -120rpx;
      width: 420rpx;
      height: 420rpx;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(59, 89, 255, 0.12), rgba(255, 255, 255, 0) 68%);
      pointer-events: none;
    }
  }

  &__search {
    position: relative;
    z-index: 1;
    min-height: 78rpx;
    gap: 14rpx;
    padding: 0 28rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.22)),
      var(--discover-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.78);
    box-shadow:
      inset 0 1rpx 0 var(--discover-glass-highlight),
      0 18rpx 44rpx rgba(17, 24, 39, 0.1);
    color: rgba(75, 85, 99, 0.82);
    font-size: 25rpx;
    font-weight: 500;

    &::after {
      content: '';
      position: absolute;
      left: 70rpx;
      right: 32rpx;
      top: 8rpx;
      height: 18rpx;
      border-radius: 999rpx;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.38), rgba(255, 255, 255, 0));
      pointer-events: none;
    }

    &:active {
      box-shadow:
        inset 0 1rpx 0 var(--discover-glass-highlight),
        0 16rpx 42rpx rgba(59, 89, 255, 0.12);
    }
  }

  &__content {
    position: relative;
    margin-top: 0;
    padding: 0 30rpx;
  }

  &__section {
    margin-bottom: 34rpx;
    padding: 30rpx 26rpx;
    border-radius: 34rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0.28)),
      var(--discover-glass-pure);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--discover-glass-highlight),
      0 18rpx 46rpx var(--discover-shadow);
  }

  &__section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  &__section-kicker {
    display: block;
    color: var(--discover-primary);
    font-size: 20rpx;
    font-weight: 800;
    letter-spacing: 0.16em;
    text-transform: uppercase;
  }

  &__section-title {
    display: block;
    margin-top: 4rpx;
    color: var(--discover-text-main);
    font-size: 34rpx;
    font-weight: 800;
  }

  &__more {
    min-width: 104rpx;
  }

  &__hot {
    position: relative;
    display: flex;
    align-items: flex-start;
    gap: 20rpx;
    margin-bottom: 18rpx;
    padding: 24rpx;
    border-radius: 28rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0.28)),
      var(--discover-glass-pure);
    border: 1rpx solid rgba(255, 255, 255, 0.72);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.82),
      0 12rpx 30rpx var(--discover-shadow);
    transition: all 0.2s cubic-bezier(0.25, 1, 0.5, 1);
    overflow: hidden;
  }

  &__hot--lead {
    background:
      linear-gradient(135deg, rgba(59, 89, 255, 0.15) 0%, rgba(143, 112, 255, 0.08) 100%),
      linear-gradient(180deg, rgba(255, 255, 255, 0.72) 0%, rgba(255, 255, 255, 0.38) 100%);
    border-color: rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 16rpx 38rpx rgba(59, 89, 255, 0.09);
  }

  &__hot:active {
    transform: scale(0.985);
  }

  &__rank {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 54rpx;
    height: 54rpx;
    border-radius: 50%;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.74), rgba(255, 255, 255, 0.24)),
      rgba(59, 89, 255, 0.08);
    color: var(--discover-text-sub);
    font-size: 26rpx;
    font-weight: 800;
    box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.7);
    flex-shrink: 0;

    &--1 {
      background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
      color: #fff;
      box-shadow: 0 6rpx 16rpx rgba(255, 165, 0, 0.38);
    }

    &--2 {
      background: linear-gradient(135deg, #E0E0E0 0%, #9E9E9E 100%);
      color: #fff;
      box-shadow: 0 6rpx 16rpx rgba(158, 158, 158, 0.3);
    }

    &--3 {
      background: linear-gradient(135deg, #F4A460 0%, #CD853F 100%);
      color: #fff;
      box-shadow: 0 6rpx 16rpx rgba(205, 133, 63, 0.34);
    }
  }

  &__hot-body {
    flex: 1;
  }

  &__hot-title {
    color: var(--discover-text-main);
    font-size: 29rpx;
    font-weight: 700;
    line-height: 1.45;
  }

  &__hot-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-top: 14rpx;
    color: rgba(75, 85, 99, 0.74);
    font-size: 22rpx;
  }

  &__meta-chip {
    display: inline-flex;
    align-items: center;
    gap: 8rpx;
    min-height: 44rpx;
    padding: 0 18rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.46);
    border: 1rpx solid rgba(255, 255, 255, 0.68);
  }

  &__meta-chip--like {
    color: var(--discover-primary);
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: $space-sm;
  }

  &__chip {
    min-height: 80rpx;
    display: flex;
    align-items: center;
    padding: 0 $space-md;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(255, 255, 255, 0.38));
    border: 1rpx solid rgba(255, 255, 255, 0.82);
    color: var(--discover-text-sub);
    font-size: 26rpx;
    font-weight: 700;
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 12rpx 30rpx rgba(31, 38, 135, 0.03);
    transition: all 0.2s cubic-bezier(0.25, 1, 0.5, 1);
  }

  &__chip:active {
    transform: scale(0.95);
    background: rgba(59, 89, 255, 0.08);
    border-color: rgba(59, 89, 255, 0.22);
    color: var(--discover-primary);
  }
}
</style>
