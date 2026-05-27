<template>
  <view class="message-detail s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading" text="正在加载消息..." />
    <s-empty
      v-if="!loading && !detail"
      title="消息不存在"
      text="这条消息可能已被撤回或暂时不可访问。"
    />

    <view v-else-if="detail" class="message-detail__card s-card">
      <view class="message-detail__title">{{ detail.title }}</view>
      <view class="message-detail__meta">
        <text>{{ detail.isRead ? '已读' : '未读' }}</text>
        <text>{{ formatTime(detail.publishTime || detail.createTime) }}</text>
      </view>
      <rich-text v-if="detail.content" class="message-detail__content" :nodes="detail.content" />
      <view v-else class="message-detail__content">暂无消息内容</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { fetchMessageDetail, markMessageRead } from '@/modules/message/api/message.api';
import type { MessageItem } from '@/modules/message/types/message';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { showInfoToast } from '@/utils/feedback';

const userStore = useUserStore();
const themeStore = useThemeStore();
const detail = ref<MessageItem | null>(null);
const loading = ref(false);

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad((options) => {
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
</script>

<style lang="scss" scoped>
.message-detail {
  min-height: 100vh;

  &__title {
    font-size: 40rpx;
    font-weight: 800;
    line-height: 1.4;
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    gap: $space-sm;
    margin-top: $space-md;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__content {
    margin-top: $space-lg;
    color: $color-text;
    font-size: 28rpx;
    line-height: 1.8;
  }
}
</style>
