<template>
  <view class="user-articles s-container">
    <s-loading :visible="loading && items.length === 0" text="正在同步文章..." />
    <view class="user-articles__header s-card">
      <view class="user-articles__title">我的文章</view>
      <view class="user-articles__desc">查看已发布内容与未发布稿件状态，移动端先提供只读回访。</view>
    </view>

    <scroll-view class="user-articles__tabs" scroll-x show-scrollbar="false">
      <view class="user-articles__tabs-inner">
        <view
          v-for="tab in tabs"
          :key="tab.value"
          class="user-articles__tab"
          :class="{ 'user-articles__tab--active': activeTab === tab.value }"
          @tap="switchTab(tab.value)"
        >
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <s-empty
      v-if="!loading && items.length === 0"
      title="暂无文章"
      text="还没有符合当前筛选条件的文章内容。"
    />

    <view v-else class="user-articles__list">
      <view v-for="item in items" :key="item.id" class="user-articles__item s-card" @tap="openArticle(item)">
        <view class="user-articles__item-title s-ellipsis-2">{{ item.title }}</view>
        <view class="user-articles__item-summary s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
        <view class="user-articles__item-meta">
          <text>{{ item.categoryName || '默认分类' }}</text>
          <text>{{ formatTime(item.createTime) }}</text>
        </view>
      </view>

      <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
      <view v-else-if="finished && items.length > 0" class="user-articles__footer">已经到底了</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { fetchUserArticlePage } from '@/modules/user/api/user.api';
import type { ArticleSummary } from '@/modules/article/types/article';
import { useUserStore } from '@/stores/user';

type StatusValue = 'all' | 1 | 0;

const tabs: Array<{ label: string; value: StatusValue }> = [
  { label: '全部', value: 'all' },
  { label: '已发布', value: 1 },
  { label: '未发布', value: 0 }
];

const userStore = useUserStore();
const activeTab = ref<StatusValue>('all');
const items = ref<ArticleSummary[]>([]);
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = 10;

onLoad(() => {
  if (!userStore.isLoggedIn || !userStore.userInfo?.id) {
    uni.redirectTo({ url: '/pages-user/login/login' });
    return;
  }
  refresh();
});

onPullDownRefresh(() => {
  refresh().finally(() => uni.stopPullDownRefresh());
});

onReachBottom(() => {
  loadMore();
});

function switchTab(tab: StatusValue): void {
  if (loading.value || activeTab.value === tab) return;
  activeTab.value = tab;
  refresh();
}

async function refresh(): Promise<void> {
  page.value = 1;
  finished.value = false;
  await loadPage(true);
}

async function loadMore(): Promise<void> {
  if (loading.value || finished.value) return;
  page.value += 1;
  await loadPage(false);
}

async function loadPage(reset: boolean): Promise<void> {
  if (!userStore.userInfo?.id) return;
  loading.value = true;
  try {
    const result = await fetchUserArticlePage(
      Number(userStore.userInfo.id),
      page.value,
      pageSize,
      activeTab.value === 'all' ? undefined : activeTab.value
    );
    const nextItems = result.items || [];
    items.value = reset ? nextItems : [...items.value, ...nextItems];
    finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
  } catch {
    if (!reset) page.value -= 1;
  } finally {
    loading.value = false;
  }
}

function openArticle(item: ArticleSummary): void {
  if (!item.id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${item.id}` });
}

function formatTime(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}
</script>

<style lang="scss" scoped>
.user-articles {
  min-height: 100vh;

  &__header {
    margin-bottom: $space-md;
  }

  &__title {
    font-size: 40rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
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
    min-width: 132rpx;
    padding: 18rpx 28rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.62);
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

  &__item-title {
    font-size: 30rpx;
    font-weight: 700;
  }

  &__item-summary {
    margin-top: 12rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__item-meta,
  &__footer {
    margin-top: 16rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__item-meta {
    display: flex;
    justify-content: space-between;
    gap: $space-sm;
  }

  &__footer {
    text-align: center;
    padding: $space-md 0;
  }
}
</style>
