<template>
  <view class="navigation s-container" :class="themeStore.themeClass">
    <view class="navigation__bar s-card">
      <input
        v-model.trim="keyword"
        class="navigation__input"
        confirm-type="search"
        placeholder="搜索导航名称或用途"
        placeholder-class="s-placeholder"
        @confirm="applyFilter"
      >
      <view class="navigation__search sl-button sl-button--primary sl-button--sm" @tap="applyFilter">筛选</view>
    </view>

    <scroll-view v-if="categories.length > 0" class="navigation__tabs" scroll-x show-scrollbar="false">
      <view class="navigation__tabs-inner">
        <view
          v-for="item in categories"
          :key="item"
          class="navigation__tab"
          :class="{ 'navigation__tab--active': activeCategory === item }"
          @tap="switchCategory(item)"
        >
          {{ item }}
        </view>
      </view>
    </scroll-view>

    <s-loading :visible="loading && items.length === 0" text="正在整理站点导航..." />
    <s-empty
      v-if="!loading && items.length === 0"
      title="暂无导航内容"
      text="换个关键词，或稍后再来看新的站点入口。"
    />

    <view v-else class="navigation__list">
      <view v-for="item in items" :key="item.id" class="navigation__item s-card" @tap="trackNavigationClick(item)">
        <view class="navigation__item-head">
          <view class="navigation__item-title">{{ item.name }}</view>
          <view v-if="item.isRecommend === 1" class="navigation__badge">推荐</view>
        </view>
        <view class="navigation__item-desc s-ellipsis-2">{{ item.description || '常用站点入口。' }}</view>
        <view class="navigation__item-meta">
          <text>{{ item.category || '未分类' }}</text>
          <text>{{ item.clickCount || 0 }} 次访问</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { fetchNavigationList, reportNavigationClick } from '../modules/site/api/site.api';
import type { NavigationItem } from '@/modules/site/types/site';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { useThemeStore } from '@/stores/theme';

const themeStore = useThemeStore();
const { guard } = useMiniAccess();
const loading = ref(false);
const items = ref<NavigationItem[]>([]);
const allItems = ref<NavigationItem[]>([]);
const keyword = ref('');
const activeCategory = ref('全部');

onShow(() => {
  themeStore.syncNativeArea();
});

const categories = computed(() => [
  '全部',
  ...allItems.value
    .map((item) => item.category?.trim())
    .filter((item): item is string => !!item)
    .filter((item, index, source) => source.indexOf(item) === index)
]);

onLoad(() => {
  if (!guard('navigationEnabled')) {
    return;
  }
  loadNavigation();
});

async function loadNavigation(): Promise<void> {
  loading.value = true;
  try {
    const list = await fetchNavigationList();
    allItems.value = list;
    filterItems();
  } finally {
    loading.value = false;
  }
}

function switchCategory(category: string): void {
  activeCategory.value = category;
  filterItems();
}

function applyFilter(): void {
  filterItems();
}

function filterItems(): void {
  const currentKeyword = keyword.value.trim().toLowerCase();
  items.value = allItems.value.filter((item) => {
    const matchesCategory = activeCategory.value === '全部' || item.category === activeCategory.value;
    const matchesKeyword = !currentKeyword
      || item.name.toLowerCase().includes(currentKeyword)
      || (item.description || '').toLowerCase().includes(currentKeyword);
    return matchesCategory && matchesKeyword;
  });
}

async function trackNavigationClick(item: NavigationItem): Promise<void> {
  try {
    await reportNavigationClick(item.id);
  } catch {
    // 点击统计失败不阻塞展示。
  }
}
</script>

<style lang="scss" scoped>
.navigation {
  min-height: 100vh;

  &__bar {
    display: flex;
    align-items: center;
    gap: $space-sm;
    margin-bottom: $space-md;
    padding: $space-sm;
  }

  &__input {
    @include sl-input;
    flex: 1;
    min-height: $input-min-height;
    border-radius: $radius-pill;
    padding: 0 $space-md;
  }

  &__search {
    min-width: 128rpx;
  }

  &__tabs {
    margin-bottom: $space-md;
    white-space: nowrap;
  }

  &__tabs-inner {
    display: inline-flex;
    gap: 16rpx;
    min-width: 100%;
  }

  &__tab {
    min-width: 120rpx;
    padding: 16rpx 24rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    color: $color-text-secondary;
    text-align: center;
    font-size: 24rpx;
  }

  &__tab--active {
    color: $color-primary;
    background: rgba(31, 111, 235, 0.12);
    font-weight: 600;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__item-head,
  &__item-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
  }

  &__item-title {
    font-size: 30rpx;
    font-weight: 700;
  }

  &__badge {
    padding: 6rpx 14rpx;
    border-radius: 999rpx;
    background: rgba(31, 111, 235, 0.1);
    color: $color-primary;
    font-size: 22rpx;
    font-weight: 600;
  }

  &__item-desc {
    margin-top: 12rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__item-meta {
    margin-top: 18rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }
}
</style>
