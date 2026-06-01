<template>
  <view class="collects s-container" :class="themeStore.themeClass">
    <s-loading :visible="userStore.isLoggedIn && loading && items.length === 0" />
    <view class="collects__header s-card">
      <view class="collects__eyebrow">Collection Library</view>
      <view class="collects__title">我的收藏</view>
      <view class="collects__desc">回看文章、说说和树洞，形成内容回访闭环。</view>
    </view>

    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      title="登录后查看收藏记录"
      text="账号登录后会自动同步移动端收藏数据，并保留跨端回访入口。"
    >
      <button class="collects__login sl-button sl-button--primary" @tap="goLogin">立即登录</button>
    </s-empty>

    <template v-else>
      <scroll-view class="collects__tabs" scroll-x show-scrollbar="false">
        <view class="collects__tabs-inner">
          <view
            v-for="tab in tabs"
            :key="tab.value"
            class="collects__tab"
            :class="{ 'collects__tab--active': activeTab === tab.value }"
            @tap="switchTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </scroll-view>

      <s-empty v-if="!loading && items.length === 0" text="暂无收藏内容" />

      <view v-else class="collects__list">
        <view
          v-for="item in items"
          :key="item.id"
          class="collects__item s-card s-card--interactive"
          @tap="openCollect(item)"
        >
          <view class="collects__item-top">
            <view class="collects__badge" :class="`collects__badge--${item.targetType}`">
              {{ targetTextMap[item.targetType] }}
            </view>
            <view class="collects__time">{{ formatTime(item.createTime) }}</view>
          </view>
          <view class="collects__item-title s-ellipsis-2">{{ getCollectTitle(item) }}</view>
          <view class="collects__item-summary s-ellipsis-2">{{ getCollectSummary(item) }}</view>
          <view class="collects__item-meta">
            <text>{{ getCollectAuthor(item) }}</text>
            <text>{{ getCollectCategory(item) }}</text>
          </view>
        </view>

        <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && items.length > 0" class="collects__footer">已经到底了</view>
      </view>
    </template>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';

onLoad(() => {
  uni.redirectTo({ url: '/pages-user/interactions/interactions?tab=collect' });
});
import { fetchMyCollects } from '@/modules/interaction/api/interaction.api';
import type {
  CollectItem,
  CollectTargetSummary,
  InteractionTargetType
} from '@/modules/interaction/types/interaction';
import { useBackToTop } from '@/shared/composables/useBackToTop';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';

type CollectTab = InteractionTargetType | 'all';

interface TabOption {
  label: string;
  value: CollectTab;
}

const userStore = useUserStore();
const themeStore = useThemeStore();
const { backToTopVisible } = useBackToTop();
const tabs: TabOption[] = [
  { label: '全部', value: 'all' },
  { label: '文章', value: 'article' },
  { label: '说说', value: 'say' },
  { label: '树洞', value: 'treehole' }
];
const targetTextMap: Record<InteractionTargetType, string> = {
  article: '文章',
  say: '说说',
  treehole: '树洞'
};

const activeTab = ref<CollectTab>('all');
const items = ref<CollectItem<CollectTargetSummary>[]>([]);
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = 10;

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
  loadMore();
});

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function switchTab(tab: CollectTab): void {
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
  loading.value = true;
  try {
    const result = await fetchMyCollects(page.value, pageSize, activeTab.value);
    const nextItems = result.items || [];
    items.value = reset ? nextItems : [...items.value, ...nextItems];
    finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
  } catch {
    if (!reset) {
      page.value -= 1;
    }
  } finally {
    loading.value = false;
  }
}

function openCollect(item: CollectItem<CollectTargetSummary>): void {
  if (item.targetType === 'article' && item.targetId) {
    uni.navigateTo({ url: `/pages-article/detail/detail?id=${item.targetId}` });
    return;
  }
  uni.switchTab({ url: '/pages/community/community' });
}

function getCollectData(item: CollectItem<CollectTargetSummary>): CollectTargetSummary | undefined {
  if (item.targetType === 'article') return item.article;
  if (item.targetType === 'say') return item.say;
  return item.treehole;
}

function getCollectTitle(item: CollectItem<CollectTargetSummary>): string {
  const target = getCollectData(item);
  return target?.title || target?.content || '未命名收藏内容';
}

function getCollectSummary(item: CollectItem<CollectTargetSummary>): string {
  const target = getCollectData(item);
  return target?.summary || target?.content || '暂无内容摘要';
}

function getCollectAuthor(item: CollectItem<CollectTargetSummary>): string {
  const target = getCollectData(item);
  return target?.userNickname || target?.authorName || '圆圈博客 用户';
}

function getCollectCategory(item: CollectItem<CollectTargetSummary>): string {
  const target = getCollectData(item);
  if (item.targetType === 'article') {
    return target?.categoryName || '默认分类';
  }
  return '社区内容';
}

function formatTime(value?: string): string {
  if (!value) return '刚刚收藏';
  return value.slice(0, 10);
}
</script>

<style lang="scss" scoped>
.collects {
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
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__login {
    width: 280rpx;
    height: 84rpx;
    line-height: 84rpx;
    margin-top: $space-lg;
    border-radius: 999rpx;
    background: linear-gradient(135deg, $color-primary, #6f8fff);
    color: #fff;
    font-size: 28rpx;
    font-weight: 600;
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
    background: var(--sl-control-bg);
    color: $color-text-secondary;
    text-align: center;
    font-size: 24rpx;
    transition: transform 0.2s ease, background-color 0.2s ease, color 0.2s ease;
  }

  &__tab--active {
    background: linear-gradient(135deg, rgba(31, 111, 235, 0.16), rgba(111, 143, 255, 0.18));
    color: $color-primary;
    font-weight: 600;
    box-shadow: 0 12rpx 28rpx rgba(31, 111, 235, 0.12);
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__item-top,
  &__item-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
  }

  &__badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 80rpx;
    padding: 8rpx 16rpx;
    border-radius: 999rpx;
    font-size: 22rpx;
    font-weight: 600;
  }

  &__badge--article {
    background: rgba(31, 111, 235, 0.1);
    color: $color-primary;
  }

  &__badge--say {
    background: rgba(29, 209, 161, 0.12);
    color: #0f9f7f;
  }

  &__badge--treehole {
    background: rgba(247, 147, 30, 0.12);
    color: #d97706;
  }

  &__time,
  &__item-meta {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__item-title {
    margin-top: $space-sm;
    font-size: 30rpx;
    font-weight: 700;
    line-height: 1.5;
  }

  &__item-summary {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__item-meta {
    margin-top: $space-md;
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }
}
</style>
