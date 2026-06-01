<template>
  <view class="interactions s-container" :class="themeStore.themeClass">
    <s-loading :visible="userStore.isLoggedIn && loading && isEmpty" />
    <view class="interactions__header s-card">
      <view class="interactions__eyebrow">My Interactions</view>
      <view class="interactions__title">我的互动</view>
      <view class="interactions__desc">集中查看你发出的收藏、点赞与评论记录。</view>
    </view>

    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      title="登录后查看互动记录"
      text="登录后可同步收藏、点赞与评论等个人互动数据。"
    >
      <button class="interactions__login sl-button sl-button--primary" @tap="goLogin">立即登录</button>
    </s-empty>

    <template v-else>
      <view class="interactions__tab-group">
        <view class="interactions__tab-group-track">
          <view
            v-for="tab in mainTabs"
            :key="tab.value"
            class="interactions__tab-group-item"
            :class="{ 'interactions__tab-group-item--active': activeTab === tab.value }"
            @tap="onMainTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </view>

      <scroll-view class="interactions__filter-tabs" scroll-x show-scrollbar="false">
        <view class="interactions__filter-tabs-inner">
          <view
            v-for="tab in subTabs"
            :key="tab.value"
            class="interactions__filter-tab"
            :class="{ 'interactions__filter-tab--active': activeSubTab === tab.value }"
            @tap="onSubTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </scroll-view>

      <s-empty v-if="!loading && isEmpty" :text="emptyText" />

      <view v-else class="interactions__list">
        <template v-if="activeTab !== 'comment'">
          <view
            v-for="item in targetItems"
            :key="`${activeTab}-${item.id}`"
            class="interactions__item s-card s-card--interactive"
            @tap="openTarget(item)"
          >
            <view class="interactions__item-top">
              <view class="interactions__badge" :class="`interactions__badge--${item.targetType}`">
                {{ targetTextMap[item.targetType] }}
              </view>
              <view class="interactions__time">{{ formatTime(item.createTime) }}</view>
            </view>
            <view class="interactions__item-title s-ellipsis-2">{{ getInteractionTitle(item) }}</view>
            <view class="interactions__item-summary s-ellipsis-2">{{ getInteractionSummary(item) }}</view>
            <view class="interactions__item-meta">
              <text>{{ getInteractionMeta(item) }}</text>
            </view>
          </view>
        </template>

        <template v-else>
          <view
            v-for="item in commentItems"
            :key="`comment-${item.id}`"
            class="interactions__item s-card s-card--interactive"
            @tap="openComment(item)"
          >
            <view class="interactions__item-top">
              <view class="interactions__badge" :class="`interactions__badge--${item.targetType || 'article'}`">
                {{ targetTextMap[(item.targetType || 'article') as InteractionTargetType] }}
              </view>
              <view class="interactions__status" :class="`interactions__status--${item.status ?? 0}`">
                {{ getCommentStatusText(item.status) }}
              </view>
            </view>
            <view class="interactions__item-title s-ellipsis-2">{{ item.targetTitle || '关联内容' }}</view>
            <view class="interactions__item-summary s-ellipsis-2">{{ item.content }}</view>
            <view class="interactions__item-meta">
              <text>{{ formatTime(item.createTime) }}</text>
              <text v-if="item.parentNickname">回复 {{ item.parentNickname }}</text>
            </view>
          </view>
        </template>

        <s-loading v-if="loading && !isEmpty" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && !isEmpty" class="interactions__footer">已经到底了</view>
      </view>
    </template>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import {
  getCommentStatusText,
  getInteractionMeta,
  getInteractionSummary,
  getInteractionTitle,
  useMyInteractions
} from '@/modules/interaction/composables/useMyInteractions';
import type {
  CollectItem,
  CollectTargetSummary,
  InteractionTab,
  InteractionTargetType,
  LikeItem,
  MyCommentItem,
  MyCommentSource
} from '@/modules/interaction/types/interaction';
import { useBackToTop } from '@/shared/composables/useBackToTop';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';

const userStore = useUserStore();
const themeStore = useThemeStore();
const { backToTopVisible } = useBackToTop();

const mainTabs: Array<{ label: string; value: InteractionTab }> = [
  { label: '收藏', value: 'collect' },
  { label: '点赞', value: 'like' },
  { label: '评论', value: 'comment' }
];

const subTabOptions = [
  { label: '全部', value: 'all' as const },
  { label: '文章', value: 'article' as const },
  { label: '说说', value: 'say' as const },
  { label: '树洞', value: 'treehole' as const }
];

const {
  activeTab,
  activeCollectTab,
  activeLikeTab,
  activeCommentTab,
  collectItems,
  likeItems,
  commentItems,
  loading,
  finished,
  targetTextMap,
  refresh,
  loadMore,
  switchMainTab,
  switchSubTab
} = useMyInteractions('collect');

const activeSubTab = computed(() => {
  if (activeTab.value === 'collect') return activeCollectTab.value;
  if (activeTab.value === 'like') return activeLikeTab.value;
  return activeCommentTab.value;
});

const subTabs = computed(() => subTabOptions);

const targetItems = computed(() => {
  if (activeTab.value === 'collect') return collectItems.value;
  return likeItems.value;
});

const isEmpty = computed(() => {
  if (activeTab.value === 'collect') return collectItems.value.length === 0;
  if (activeTab.value === 'like') return likeItems.value.length === 0;
  return commentItems.value.length === 0;
});

const emptyText = computed(() => {
  if (activeTab.value === 'collect') return '暂无收藏内容';
  if (activeTab.value === 'like') return '暂无点赞记录';
  return '暂无评论记录';
});

onLoad((options) => {
  const tab = typeof options?.tab === 'string' ? options.tab : '';
  if (tab === 'collect' || tab === 'like' || tab === 'comment') {
    activeTab.value = tab;
  }
});

onShow(() => {
  themeStore.syncNativeArea();
  if (!userStore.isLoggedIn) return;
  void refresh();
});

onPullDownRefresh(() => {
  if (!userStore.isLoggedIn) {
    uni.stopPullDownRefresh();
    return;
  }
  refresh().finally(() => uni.stopPullDownRefresh());
});

onReachBottom(() => {
  if (!userStore.isLoggedIn) return;
  void loadMore();
});

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function onMainTab(tab: InteractionTab): void {
  void switchMainTab(tab);
}

function onSubTab(value: string): void {
  void switchSubTab(value as InteractionTargetType | 'all' | MyCommentSource);
}

function openTarget(item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary>): void {
  if (item.targetType === 'article' && item.targetId) {
    uni.navigateTo({ url: `/pages-article/detail/detail?id=${item.targetId}` });
    return;
  }
  uni.switchTab({ url: '/pages/community/community' });
}

function openComment(item: MyCommentItem): void {
  const targetType = item.targetType || 'article';
  const targetId = item.targetId;
  if (!targetId) {
    return;
  }
  if (targetType === 'article') {
    uni.navigateTo({ url: `/pages-article/detail/detail?id=${targetId}` });
    return;
  }
  uni.switchTab({ url: '/pages/community/community' });
}

function formatTime(value?: string): string {
  if (!value) return '刚刚';
  return value.slice(0, 10);
}
</script>

<style lang="scss" scoped>
.interactions {
  min-height: 100vh;

  &__header {
    margin-bottom: $space-md;
  }

  &__eyebrow {
    display: inline-flex;
    padding: 8rpx 20rpx;
    border-radius: 999rpx;
    background: rgba(31, 111, 235, 0.08);
    color: $color-primary;
    font-size: 22rpx;
    font-weight: 600;
    margin-bottom: $space-sm;
  }

  &__title {
    font-size: 44rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 26rpx;
    line-height: 1.7;
  }

  &__login {
    margin-top: $space-md;
    min-width: 220rpx;
  }

  &__tab-group {
    margin-bottom: $space-md;
  }

  &__tab-group-track {
    @include sl-tab-group-track();
  }

  &__tab-group-item {
    @include sl-tab-group-item();

    &--active {
      @include sl-tab-group-item-active();
    }
  }

  &__filter-tabs {
    margin-bottom: $space-md;
    white-space: nowrap;
  }

  &__filter-tabs-inner {
    @include sl-filter-tabs-track();
  }

  &__filter-tab {
    @include sl-filter-tab-item();

    &--active {
      @include sl-filter-tab-item-active();
    }
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-sm;
  }

  &__item {
    padding: $space-md;
  }

  &__item-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
    margin-bottom: 10rpx;
  }

  &__badge {
    padding: 6rpx 16rpx;
    border-radius: 999rpx;
    font-size: 22rpx;
    font-weight: 600;
    color: $color-primary;
    background: rgba(59, 89, 255, 0.1);

    &--say {
      color: #8f70ff;
      background: rgba(143, 112, 255, 0.12);
    }

    &--treehole {
      color: #00b42a;
      background: rgba(0, 180, 42, 0.12);
    }
  }

  &__status {
    font-size: 22rpx;
    font-weight: 600;

    &--0 {
      color: $color-primary;
    }

    &--1 {
      color: $color-success;
    }

    &--2 {
      color: $color-danger;
    }
  }

  &__time {
    font-size: 22rpx;
    color: $color-text-tertiary;
  }

  &__item-title {
    font-size: 30rpx;
    font-weight: 700;
    color: $color-text;
    line-height: 1.45;
  }

  &__item-summary {
    margin-top: 8rpx;
    font-size: 25rpx;
    color: $color-text-secondary;
    line-height: 1.65;
  }

  &__item-meta {
    display: flex;
    flex-wrap: wrap;
    gap: $space-sm;
    margin-top: 12rpx;
    font-size: 22rpx;
    color: $color-text-tertiary;
  }

  &__footer {
    text-align: center;
    padding: $space-md 0 $space-lg;
    font-size: 24rpx;
    color: $color-text-tertiary;
  }
}
</style>
