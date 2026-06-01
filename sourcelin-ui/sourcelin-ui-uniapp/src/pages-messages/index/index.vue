<template>
  <view class="messages s-container" :class="themeStore.themeClass">
    <!-- 顶部标题与未读角标 -->
    <view class="messages__header s-card">
      <view class="messages__header-left">
        <view class="messages__title">消息中心</view>
        <view class="messages__desc">系统公告、互动提醒与回复通知汇总。</view>
      </view>
      <view class="messages__meta" :class="{ 'messages__meta--zero': unreadCount <= 0 }">
        <text class="messages__meta-value">{{ unreadCount > 99 ? '99+' : unreadCount }}</text>
        <text class="messages__meta-label">未读</text>
      </view>
    </view>

    <!-- 频道 Tab 栏（多频道时显示） -->
    <scroll-view
      v-if="userStore.isLoggedIn && showTabs"
      class="messages__tabs"
      scroll-x
      :show-scrollbar="false"
    >
      <view class="messages__tabs-inner">
        <view
          v-for="ch in channels"
          :key="ch.id"
          class="messages__tab"
          :class="{ 'messages__tab--active': activeChannel === ch.id }"
          @tap="handleSwitchChannel(ch.id)"
        >
          {{ ch.name }}
        </view>
      </view>
    </scroll-view>

    <!-- 工具栏 -->
    <view
      v-if="userStore.isLoggedIn && items.length > 0"
      class="messages__toolbar"
    >
      <view class="messages__toolbar-action sl-button sl-button--ghost sl-button--sm" @tap="handleMarkAllRead">
        全部已读
      </view>
    </view>

    <!-- 未登录空态 -->
    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      title="登录后查看消息"
      text="系统公告、互动提醒和回复通知都会在这里汇总。"
    >
      <button class="messages__login sl-button sl-button--primary" @tap="goLogin">立即登录</button>
    </s-empty>

    <!-- 无数据空态 -->
    <s-empty
      v-else-if="isEmpty"
      title="暂无公告"
      text="当前没有新的系统公告或站务通知。"
    />

    <!-- 加载中（首次） -->
    <s-loading v-else-if="loading && items.length === 0" :visible="true" text="正在同步消息..." />

    <!-- 消息列表 -->
    <view v-else class="messages__list">
      <view
        v-for="item in items"
        :key="item.id"
        class="messages__item s-card s-card--interactive"
        :class="{ 'messages__item--unread': !item.isRead }"
        @tap="openDetail(item)"
      >
        <view class="messages__item-head">
          <view
            class="messages__item-indicator"
            :style="{ background: currentChannelConfig.color }"
          />
          <view class="messages__item-title s-ellipsis">{{ item.title }}</view>
          <view v-if="!item.isRead" class="messages__dot" />
        </view>
        <view class="messages__item-content s-ellipsis-2">{{ summarizeContent(item.content) }}</view>
        <view class="messages__item-foot">
          <text class="messages__item-channel">{{ currentChannelConfig.name }}</text>
          <text class="messages__item-time">{{ formatTime(item.publishTime || item.createTime) }}</text>
        </view>
      </view>

      <s-loading
        v-if="loading && items.length > 0"
        :visible="true"
        :fullpage="false"
        compact
        text="正在加载更多..."
      />
      <view v-else-if="finished && items.length > 0" class="messages__footer">已经到底了</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { showSuccessToast } from '@/utils/feedback';
import { useMessageCenter } from '@/modules/message/composables/useMessageCenter';
import type { MessageChannel } from '@/modules/message/config/channels';
import type { MessageItem } from '@/modules/message/types/message';

const userStore = useUserStore();
const themeStore = useThemeStore();

const {
  channels,
  activeChannel,
  currentChannelConfig,
  showTabs,
  isEmpty,
  items,
  loading,
  finished,
  unreadCount,
  switchChannel,
  refresh,
  loadMore,
  markAllRead
} = useMessageCenter();

onLoad(() => {
  if (userStore.isLoggedIn) {
    void refresh();
  }
});

onShow(() => {
  themeStore.syncNativeArea();
  if (!userStore.isLoggedIn) {
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

function handleSwitchChannel(channel: MessageChannel): void {
  switchChannel(channel);
}

async function handleMarkAllRead(): Promise<void> {
  if (!userStore.isLoggedIn) {
    goLogin();
    return;
  }
  await markAllRead();
  showSuccessToast('已全部标为已读');
}

function summarizeContent(content?: string): string {
  return (content || '查看详情').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim() || '查看详情';
}

function formatTime(value?: string): string {
  return value ? value.slice(0, 16).replace('T', ' ') : '刚刚';
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

  &__header-left {
    flex: 1;
    min-width: 0;
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
    flex-shrink: 0;

    &--zero {
      background: rgba(0, 0, 0, 0.03);
    }
  }

  &__meta-value {
    color: $color-primary;
    font-size: 34rpx;
    font-weight: 700;

    .messages__meta--zero & {
      color: $color-text-tertiary;
    }
  }

  &__meta-label {
    margin-top: 4rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  // 频道 Tab 栏
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
    flex-shrink: 0;

    &--active {
      color: $color-primary;
      background: rgba(31, 111, 235, 0.12);
      font-weight: 600;
    }
  }

  // 工具栏
  &__toolbar {
    display: flex;
    justify-content: flex-end;
    margin-bottom: $space-md;
  }

  &__toolbar-action {
    min-width: 132rpx;
  }

  // 登录按钮
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

  // 消息列表
  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__item {
    transition: border-color 0.2s ease;

    &--unread {
      border-color: rgba(31, 111, 235, 0.2);
    }
  }

  &__item-head {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__item-indicator {
    width: 8rpx;
    height: 32rpx;
    border-radius: 999rpx;
    flex-shrink: 0;
    opacity: 0.7;
  }

  &__item-title {
    flex: 1;
    font-size: 30rpx;
    font-weight: 700;
    min-width: 0;
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
    padding-left: 20rpx;
  }

  &__item-foot {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16rpx;
    padding-left: 20rpx;
  }

  &__item-channel {
    color: $color-text-tertiary;
    font-size: 22rpx;
    padding: 2rpx 14rpx;
    border-radius: 999rpx;
    background: rgba(0, 0, 0, 0.03);
  }

  &__item-time {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__footer {
    margin-top: 16rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
    text-align: center;
    padding: $space-md 0;
  }
}
</style>
