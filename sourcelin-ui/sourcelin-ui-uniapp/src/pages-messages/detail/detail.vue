<template>
  <view class="message-detail s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading" text="正在加载消息..." />
    <s-empty
      v-if="!loading && !detail"
      title="消息不存在"
      text="这条消息可能已被撤回或暂时不可访问。"
    />

    <view v-else-if="detail" class="message-detail__wrapper">
      <!-- 频道标签与状态 -->
      <view class="message-detail__toolbar">
        <view
          class="message-detail__channel-tag"
          :style="{ color: channelConfig.color, backgroundColor: channelConfig.color + '0D', borderColor: channelConfig.color + '33' }"
        >
          <view class="message-detail__channel-icon">
            <uni-icons :type="channelConfig.icon" size="14" :color="channelConfig.color" />
          </view>
          <text class="message-detail__channel-label">{{ channelConfig.name }}</text>
        </view>
        <view
          class="message-detail__status-badge"
          :class="detail.isRead ? 'message-detail__status-badge--read' : 'message-detail__status-badge--unread'"
        >
          {{ detail.isRead ? '已读' : '未读' }}
        </view>
      </view>

      <!-- 消息主体卡片 -->
      <view class="message-detail__card s-card">
        <view class="message-detail__title">{{ detail.title }}</view>
        <view class="message-detail__meta">
          <text class="message-detail__meta-time">
            {{ formatTime(detail.publishTime || detail.createTime) }}
          </text>
          <text v-if="detail.expireTime" class="message-detail__meta-expire">
            有效期至 {{ formatTime(detail.expireTime) }}
          </text>
        </view>
        <view class="message-detail__divider" />
        <rich-text
          v-if="detail.content"
          class="message-detail__content"
          :nodes="detail.content"
        />
        <view v-else class="message-detail__empty-content">暂无消息内容</view>
      </view>

      <!-- 底部导航 -->
      <view class="message-detail__nav">
        <view class="message-detail__nav-btn sl-button sl-button--ghost sl-button--sm" @tap="goBack">
          返回消息列表
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { fetchMessageDetail, markMessageRead } from '@/modules/message/api/message.api';
import type { MessageItem } from '@/modules/message/types/message';
import { getChannelConfig, type MessageChannel } from '../modules/message/config/channels';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { showInfoToast } from '@/utils/feedback';

const userStore = useUserStore();
const themeStore = useThemeStore();
const { guard } = useMiniAccess();
const detail = ref<MessageItem | null>(null);
const loading = ref(false);

const channelConfig = computed(() => {
  const channel = (detail.value?.channel || 'system') as MessageChannel;
  return getChannelConfig(channel);
});

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad((options) => {
  if (!guard('messageCenterEnabled')) return;
  if (!userStore.isLoggedIn) {
    uni.redirectTo({ url: '/pages-user/login/login' });
    return;
  }
  const id = Number(options?.id);
  if (!Number.isFinite(id) || id <= 0) {
    showInfoToast('消息参数无效');
    return;
  }
  loadDetail(id);
});

async function loadDetail(id: number): Promise<void> {
  loading.value = true;
  try {
    const data = await fetchMessageDetail(id);
    detail.value = {
      ...data,
      isRead: Number(data.isRead) === 1 ? 1 : 0
    };
    if (!detail.value.isRead) {
      await markMessageRead(id);
      detail.value.isRead = 1;
    }
  } finally {
    loading.value = false;
  }
}

function formatTime(value?: string): string {
  return value ? value.slice(0, 16).replace('T', ' ') : '刚刚';
}

function goBack(): void {
  uni.navigateBack({ delta: 1, fail: () => {
    uni.redirectTo({ url: '/pages-messages/index/index' });
  }});
}
</script>

<style lang="scss" scoped>
.message-detail {
  min-height: 100vh;

  &__wrapper {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  // 频道标签栏
  &__toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4rpx;
  }

  &__channel-tag {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 10rpx 22rpx;
    border-radius: 999rpx;
    border: 1rpx solid;
    font-size: 24rpx;
    font-weight: 500;
    line-height: 1;
  }

  &__channel-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28rpx;
    height: 28rpx;
    flex-shrink: 0;
  }

  &__channel-label {
    line-height: 1;
  }

  &__status-badge {
    padding: 6rpx 18rpx;
    border-radius: 999rpx;
    font-size: 22rpx;
    font-weight: 500;
    line-height: 1;

    &--read {
      color: $color-text-tertiary;
      background: rgba(0, 0, 0, 0.04);
    }

    &--unread {
      color: #ff5b75;
      background: rgba(255, 91, 117, 0.08);
    }
  }

  // 主体卡片
  &__card {
    padding: $space-lg;
  }

  &__title {
    font-size: 38rpx;
    font-weight: 800;
    line-height: 1.4;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: $space-md;
    margin-top: $space-md;
    color: $color-text-tertiary;
    font-size: 24rpx;
  }

  &__meta-expire {
    opacity: 0.7;
  }

  &__divider {
    margin-top: $space-lg;
    height: 1rpx;
    background: rgba(0, 0, 0, 0.06);
  }

  &__content {
    margin-top: $space-lg;
    color: $color-text;
    font-size: 28rpx;
    line-height: 1.8;
  }

  &__empty-content {
    margin-top: $space-lg;
    color: $color-text-tertiary;
    font-size: 28rpx;
    text-align: center;
    padding: $space-lg 0;
  }

  // 底部导航
  &__nav {
    display: flex;
    justify-content: center;
    padding: $space-md 0 $space-lg;
  }

  &__nav-btn {
    min-width: 240rpx;
  }
}
</style>
