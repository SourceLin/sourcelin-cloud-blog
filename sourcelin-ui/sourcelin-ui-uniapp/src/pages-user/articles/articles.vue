<template>
  <view class="user-articles s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading && items.length === 0" text="正在同步文章..." />
    <view class="user-articles__header s-card">
      <view class="user-articles__header-main">
        <view class="user-articles__title">我的文章</view>
        <view class="user-articles__desc">在移动端继续维护稿件，支持草稿、送审和快速修订。</view>
      </view>
      <button class="user-articles__create sl-button sl-button--primary sl-button--sm" @tap="createArticleAction">
        写文章
      </button>
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
      <view v-for="item in items" :key="item.id" class="user-articles__item s-card s-card--interactive" @tap="openArticle(item)">
        <view class="user-articles__item-head">
          <view class="user-articles__item-title s-ellipsis-2">{{ item.title }}</view>
          <view class="user-articles__status" :class="`user-articles__status--${resolveStatusKey(item.status)}`">
            {{ resolveStatusText(item.status) }}
          </view>
        </view>
        <view class="user-articles__item-summary s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
        <view class="user-articles__item-meta">
          <text>{{ item.categoryName || '默认分类' }}</text>
          <text>{{ formatTime(item.createTime) }}</text>
        </view>
        <view class="user-articles__item-actions">
          <view class="user-articles__item-action" @tap.stop="editArticle(item)">编辑</view>
          <view
            v-if="canDelete(item.status)"
            class="user-articles__item-action user-articles__item-action--danger"
            @tap.stop="removeArticleAction(item)"
          >
            删除
          </view>
        </view>
      </view>

      <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
      <view v-else-if="finished && items.length > 0" class="user-articles__footer">已经到底了</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { deleteArticle } from '@/modules/article/api/article.api';
import { consumeArticleRefresh } from '../modules/article/utils/publish';
import type { ArticleSummary } from '@/modules/article/types/article';
import { fetchUserArticlePage } from '@/modules/user/api/user.api';
import { useThemeStore } from '@/stores/theme';
import { showSuccessToast } from '@/utils/feedback';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

type StatusValue = 'all' | 1 | 0;

const tabs: Array<{ label: string; value: StatusValue }> = [
  { label: '全部', value: 'all' },
  { label: '已发布', value: 1 },
  { label: '未发布', value: 0 }
];

const { userStore, guard } = useMiniAccess();
const themeStore = useThemeStore();
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
  if (!guard('articlePublishEnabled')) {
    return;
  }
  refresh();
});

onShow(() => {
  themeStore.syncNativeArea();
  if (consumeArticleRefresh() && userStore.isLoggedIn && userStore.userInfo?.id) {
    void refresh();
  }
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
  if (Number(item.status) === 2) {
    uni.navigateTo({ url: `/pages-article/detail/detail?id=${item.id}` });
    return;
  }
  editArticle(item);
}

function createArticleAction(): void {
  uni.navigateTo({ url: '/pages-publish/article/article' });
}

function editArticle(item: ArticleSummary): void {
  if (!item.id) return;
  uni.navigateTo({ url: `/pages-publish/article/article?id=${item.id}` });
}

function canDelete(status?: number): boolean {
  return Number(status) !== 2;
}

function resolveStatusKey(status?: number): 'draft' | 'review' | 'published' {
  if (Number(status) === 2) return 'published';
  if (Number(status) === 1) return 'review';
  return 'draft';
}

function resolveStatusText(status?: number): string {
  if (Number(status) === 2) return '已发布';
  if (Number(status) === 1) return '审核中';
  return '草稿';
}

async function removeArticleAction(item: ArticleSummary): Promise<void> {
  if (!item.id || !canDelete(item.status)) return;
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '删除稿件',
      content: '删除后不可恢复，是否继续？',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;
  await deleteArticle(item.id);
  showSuccessToast('稿件已删除');
  await refresh();
}

function formatTime(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}
</script>

<style lang="scss" scoped>
.user-articles {
  min-height: 100vh;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: $space-md;
    margin-bottom: $space-md;
  }

  &__header-main {
    flex: 1;
  }

  &__create {
    align-self: center;
    min-width: 156rpx;
    height: 72rpx;
    padding: 0 24rpx;
    margin: 0;
    border-radius: 999rpx;
    color: #fff;
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    border: 1rpx solid rgba(255, 255, 255, 0.16);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.2),
      0 12rpx 26rpx rgba(59, 89, 255, 0.22);
    font-size: 24rpx;
    line-height: 72rpx;
    white-space: nowrap;
    flex-shrink: 0;
  }

  &__create:active {
    transform: scale(0.97);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
      0 8rpx 18rpx rgba(59, 89, 255, 0.16);
  }

  &__create text {
    color: #fff;
    white-space: nowrap;
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

  &__item-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__item-title {
    font-size: 30rpx;
    font-weight: 700;
  }

  &__status {
    min-height: 48rpx;
    padding: 0 18rpx;
    display: inline-flex;
    align-items: center;
    border-radius: 999rpx;
    font-size: 21rpx;
    font-weight: 700;
    flex-shrink: 0;
  }

  &__status--draft {
    color: #7c4dff;
    background: rgba(124, 77, 255, 0.12);
  }

  &__status--review {
    color: #ff7d00;
    background: rgba(255, 125, 0, 0.12);
  }

  &__status--published {
    color: #00b42a;
    background: rgba(0, 180, 42, 0.12);
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

  &__item-actions {
    display: flex;
    justify-content: flex-end;
    gap: 18rpx;
    margin-top: 18rpx;
  }

  &__item-action {
    color: $color-primary;
    font-size: 23rpx;
    font-weight: 700;
  }

  &__item-action--danger {
    color: #ff5b75;
  }

  &__footer {
    text-align: center;
    padding: $space-md 0;
  }
}
</style>
