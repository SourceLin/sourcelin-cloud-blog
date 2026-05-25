<template>
  <view class="messages s-container">
    <s-loading :visible="loading && items.length === 0" text="正在同步消息..." />
    <view class="messages__header s-card">
      <view>
        <view class="messages__title">消息中心</view>
        <view class="messages__desc">先承载系统公告，后续逐步并入评论回复与互动提醒。</view>
      </view>
      <view class="messages__meta">
        <text class="messages__meta-value">{{ unreadCount }}</text>
        <text class="messages__meta-label">未读</text>
      </view>
    </view>

    <view v-if="userStore.isLoggedIn" class="messages__toolbar">
      <view class="messages__toolbar-action sl-button sl-button--ghost sl-button--sm" @tap="markAllReadAction">全部已读</view>
    </view>

    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      title="登录后查看消息"
      text="系统公告、互动提醒和后续的回复通知都会在这里汇总。"
    >
      <button class="messages__login sl-button sl-button--primary" @tap="goLogin">立即登录</button>
    </s-empty>

    <s-empty
      v-else-if="!loading && items.length === 0"
      title="暂无消息"
      text="当前没有新的系统公告或互动提醒。"
    />

    <view v-else class="messages__list">
      <view
        v-for="item in items"
        :key="item.id"
        class="messages__item s-card s-card--interactive"
        :class="{ 'messages__item--unread': !item.isRead }"
        @tap="openDetail(item)"
      >
        <view class="messages__item-head">
          <view class="messages__item-title s-ellipsis">{{ item.title }}</view>
          <view v-if="!item.isRead" class="messages__dot" />
        </view>
        <view class="messages__item-content s-ellipsis-2">{{ summarizeContent(item.content) }}</view>
        <view class="messages__item-time">{{ formatTime(item.publishTime || item.createTime) }}</view>
      </view>

      <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
      <view v-else-if="finished && items.length > 0" class="messages__footer">已经到底了</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import {
  fetchMessagePage,
  fetchUnreadMessageCount,
  markAllMessagesRead
} from '@/modules/message/api/message.api';
import type { MessageItem } from '@/modules/message/types/message';
import { useUserStore } from '@/stores/user';
import { showSuccessToast } from '@/utils/feedback';

const userStore = useUserStore();
const items = ref<MessageItem[]>([]);
const loading = ref(false);
const finished = ref(false);
const unreadCount = ref(0);
const page = ref(1);
const pageSize = 10;

onLoad(() => {
  if (userStore.isLoggedIn) {
    void refresh();
  }
});

onShow(() => {
  if (!userStore.isLoggedIn) {
    items.value = [];
    unreadCount.value = 0;
    return;
  }
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

async function refresh(): Promise<void> {
  page.value = 1;
  finished.value = false;
  await Promise.all([loadPage(true), loadUnreadCount()]);
}

async function loadMore(): Promise<void> {
  if (loading.value || finished.value) return;
  page.value += 1;
  await loadPage(false);
}

async function loadPage(reset: boolean): Promise<void> {
  loading.value = true;
  try {
    const result = await fetchMessagePage(page.value, pageSize);
    const nextItems = (result.items || []).map(normalizeMessage);
    items.value = reset ? nextItems : [...items.value, ...nextItems];
    finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
  } catch {
    if (!reset) page.value -= 1;
  } finally {
    loading.value = false;
  }
}

async function loadUnreadCount(): Promise<void> {
  try {
    const stat = await fetchUnreadMessageCount();
    unreadCount.value = stat.total || 0;
  } catch {
    unreadCount.value = 0;
  }
}

function normalizeMessage(item: MessageItem): MessageItem {
  return {
    ...item,
    isRead: Number(item.isRead) === 1 ? 1 : 0
  };
}

function summarizeContent(content?: string): string {
  return (content || '查看详情').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim() || '查看详情';
}

function formatTime(value?: string): string {
  return value ? value.slice(0, 16).replace('T', ' ') : '刚刚';
}

async function markAllReadAction(): Promise<void> {
  if (!userStore.isLoggedIn) {
    goLogin();
    return;
  }
  await markAllMessagesRead();
  items.value = items.value.map((item) => ({ ...item, isRead: 1 }));
  unreadCount.value = 0;
  showSuccessToast('已全部标为已读');
}

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function openDetail(item: MessageItem): void {
  uni.navigateTo({ url: `/pages-messages/detail/detail?id=${item.id}` });
}
</script>

<style lang="scss" scoped>
.messages {
  min-height: 100vh;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: $space-md;
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

  &__meta {
    min-width: 120rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 18rpx 0;
    border-radius: 24rpx;
    background: rgba(31, 111, 235, 0.08);
  }

  &__meta-value {
    color: $color-primary;
    font-size: 34rpx;
    font-weight: 700;
  }

  &__meta-label {
    margin-top: 4rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__toolbar {
    display: flex;
    justify-content: flex-end;
    margin: $space-md 0;
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

  &__toolbar-action {
    min-width: 132rpx;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__item--unread {
    border-color: rgba(31, 111, 235, 0.2);
  }

  &__item-head {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__item-title {
    flex: 1;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__dot {
    width: 14rpx;
    height: 14rpx;
    border-radius: 50%;
    background: #ff5b75;
    flex-shrink: 0;
  }

  &__item-content {
    margin-top: 12rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__item-time,
  &__footer {
    margin-top: 16rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__footer {
    text-align: center;
    padding: $space-md 0;
  }
}
</style>
