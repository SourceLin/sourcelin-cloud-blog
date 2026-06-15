<template>
  <view class="interactions s-container" :class="themeStore.themeClass">
    <s-loading :visible="userStore.isLoggedIn && loading && isEmpty" />
    <view class="interactions__header s-card">
      <view class="interactions__eyebrow">{{ reviewSafeCollectOnly ? 'My Favorites' : 'My Interactions' }}</view>
      <view class="interactions__title">{{ reviewSafeCollectOnly ? '我的收藏' : '我的互动' }}</view>
      <view class="interactions__desc">{{ reviewSafeCollectOnly ? '集中回看你收藏过的文章内容。' : '集中查看你发出的收藏、点赞与评论记录。' }}</view>
    </view>

    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      :title="reviewSafeCollectOnly ? '登录后查看收藏记录' : '登录后查看互动记录'"
      :text="reviewSafeCollectOnly ? '登录后可同步收藏的文章内容。' : '登录后可同步收藏、点赞与评论等个人互动数据。'"
    >
      <button class="interactions__login sl-button sl-button--primary" @tap="goLogin">立即登录</button>
    </s-empty>

    <template v-else>
      <view v-if="availableMainTabs.length > 1" class="interactions__tab-group">
        <view class="interactions__tab-group-track">
          <view
            v-for="tab in availableMainTabs"
            :key="tab.value"
            class="interactions__tab-group-item"
            :class="{ 'interactions__tab-group-item--active': activeTab === tab.value }"
            @tap="onMainTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </view>

      <scroll-view v-if="showSubTabs" class="interactions__filter-tabs" scroll-x show-scrollbar="false">
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
          <uni-swipe-action>
            <view
              v-for="item in targetItems"
              :key="`${activeTab}-${item.id}`"
              class="interactions__swipe-row"
            >
              <uni-swipe-action-item class="interactions__swipe-item">
                <view
                  class="interactions__item s-card s-card--interactive"
                  @click="openTarget(item)"
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
                <template #right>
                  <view class="interactions__delete-action" @tap.stop="handleDeleteAction(item)">
                    <uni-icons type="trash" size="18" color="#fff" />
                    <text>删除</text>
                  </view>
                </template>
              </uni-swipe-action-item>
            </view>
          </uni-swipe-action>
        </template>

        <template v-else>
          <uni-swipe-action>
            <view
              v-for="item in commentItems"
              :key="`comment-${item.id}`"
              class="interactions__swipe-row"
            >
              <uni-swipe-action-item class="interactions__swipe-item">
                <view
                  class="interactions__item s-card s-card--interactive"
                  @click="openComment(item)"
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
                <template #right>
                  <view class="interactions__delete-action" @tap.stop="handleDeleteAction(item)">
                    <uni-icons type="trash" size="18" color="#fff" />
                    <text>删除</text>
                  </view>
                </template>
              </uni-swipe-action-item>
            </view>
          </uni-swipe-action>
        </template>

        <s-loading v-if="loading && !isEmpty" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && !isEmpty" class="interactions__footer">已经到底了</view>
      </view>
    </template>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue';
import { onLoad, onPageScroll, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import {
  getCommentStatusText,
  getInteractionMeta,
  getInteractionSummary,
  getInteractionTitle,
  useMyInteractions
} from '../modules/interaction/composables/useMyInteractions';
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
import { useThemeStore } from '@/stores/theme';
import { uncollectTarget, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import { deleteComment } from '@/modules/comment/api/comment.api';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { showInfoToast } from '@/utils/feedback';

const { userStore, can } = useMiniAccess();
const themeStore = useThemeStore();
const { backToTopVisible, handlePageScroll } = useBackToTop();

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

const reviewSafeCollectOnly = computed(() => !userStore.isBlogger && !can('interactionCenterEnabled'));
const availableMainTabs = computed(() => reviewSafeCollectOnly.value ? [mainTabs[0]] : mainTabs);

const activeSubTab = computed(() => {
  if (activeTab.value === 'collect') return activeCollectTab.value;
  if (activeTab.value === 'like') return activeLikeTab.value;
  return activeCommentTab.value;
});

const subTabs = computed(() => {
  if (reviewSafeCollectOnly.value) {
    return [{ label: '文章', value: 'article' as const }];
  }
  return subTabOptions;
});
const showSubTabs = computed(() => subTabs.value.length > 1);

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

onPageScroll(handlePageScroll);

watch(reviewSafeCollectOnly, (value) => {
  if (!value) return;
  if (activeTab.value !== 'collect') {
    void switchMainTab('collect');
  }
  if (activeCollectTab.value !== 'article') {
    void switchSubTab('article');
  }
}, { immediate: true });

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
  if (!can('communityEnabled')) {
    showInfoToast('当前版本仅支持回访文章内容');
    return;
  }
  uni.navigateTo({ url: '/pages/community/community' });
}

function openComment(item: MyCommentItem): void {
  if (reviewSafeCollectOnly.value) return;
  const targetType = item.targetType || 'article';
  const targetId = item.targetId;
  if (!targetId) {
    return;
  }
  if (targetType === 'article') {
    uni.navigateTo({ url: `/pages-article/detail/detail?id=${targetId}` });
    return;
  }
  if (!can('communityEnabled')) {
    showInfoToast('当前版本暂不开放该内容入口');
    return;
  }
  uni.navigateTo({ url: '/pages/community/community' });
}

function formatTime(value?: string): string {
  if (!value) return '刚刚';
  return value.slice(0, 10);
}

async function handleDeleteAction(item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary> | MyCommentItem): Promise<void> {
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '提示',
      content: activeTab.value === 'collect'
        ? '确认取消收藏吗？'
        : activeTab.value === 'like'
          ? '确认取消点赞吗？'
          : '确认删除该评论吗？',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;

  try {
    if (activeTab.value === 'collect') {
      const collectItem = item as CollectItem<CollectTargetSummary>;
      await uncollectTarget(collectItem.targetType, collectItem.targetId);
      uni.showToast({ title: '取消收藏成功', icon: 'success' });
    } else if (activeTab.value === 'like') {
      const likeItem = item as LikeItem<CollectTargetSummary>;
      await unlikeTarget(likeItem.targetType, likeItem.targetId);
      uni.showToast({ title: '取消点赞成功', icon: 'success' });
    } else {
      await deleteComment(item.id);
      uni.showToast({ title: '评论已删除', icon: 'success' });
    }
    await refresh();
  } catch {
    // 接口层已报错提示
  }
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

  &__swipe-row {
    margin-bottom: $space-sm;

    &:last-child {
      margin-bottom: 0;
    }
  }

  &__swipe-item {
    background: transparent;
    border-radius: $glass-radius-main;
    overflow: hidden;
  }

  &__delete-action {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    width: 132rpx;
    height: 100%;
    min-height: 100%;
    color: #ffffff;
    font-size: 25rpx;
    font-weight: 800;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.03)),
      rgba(245, 63, 63, 0.82);
    border-left: 1rpx solid rgba(255, 255, 255, 0.2);
    box-shadow:
      inset 10rpx 0 30rpx rgba(255, 255, 255, 0.06),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.16);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(12rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(12rpx) saturate(1.2);
    /* #endif */

    .sl-theme--dark & {
      background:
        linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.02)),
        rgba(244, 63, 94, 0.72);
      border-left-color: rgba(255, 255, 255, 0.1);
    }
  }
}
</style>
