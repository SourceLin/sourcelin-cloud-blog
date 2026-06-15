<template>
  <view v-if="ready" class="publish-treehole s-container" :class="themeStore.themeClass">
    <view class="publish-treehole__header s-card">
      <view class="publish-treehole__title">树洞投递</view>
      <view class="publish-treehole__desc">
        {{ isLoggedIn ? '记录心情，也与大家轻声对话。' : '这是访客留言树洞，保留匿名的自由，也保留被回应的可能。' }}
      </view>
    </view>

    <view class="publish-treehole__form s-card">
      <view class="publish-treehole__field publish-treehole__field--textarea">
        <view class="publish-treehole__label">内容</view>
        <textarea
          v-model.trim="content"
          class="publish-treehole__textarea"
          maxlength="500"
          auto-height
          :placeholder="isLoggedIn ? '写点什么，分享给大家也好，自己留个记号也好。' : '写下你想留下的一句话、一个念头，或一段没人知道的心情...'"
          placeholder-class="s-placeholder"
        />
      </view>
      <view class="publish-treehole__hint">{{ content.length }}/500</view>
    </view>

    <button class="publish-treehole__submit sl-button sl-button--primary" :disabled="submitting" @tap="submit">
      <s-inline-loading v-if="submitting" text="提交中" light />
      <text v-else>投递树洞</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { createTreehole } from '@/modules/community/api/community.api';
import { markCommunityRefresh } from '@/modules/community/utils/publish';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { useThemeStore } from '@/stores/theme';
import { useUserStore } from '@/stores/user';

const themeStore = useThemeStore();
const userStore = useUserStore();
const { guard } = useMiniAccess();
const content = ref('');
const ready = ref(false);
const submitting = ref(false);
const isLoggedIn = computed(() => userStore.isLoggedIn);

onLoad(() => {
  if (!guard('communityEnabled')) {
    return;
  }
  if (!userStore.isBlogger) {
    uni.switchTab({ url: '/pages/home/home' });
    return;
  }
  ready.value = true;
});

onShow(() => {
  themeStore.syncNativeArea();
});

async function submit(): Promise<void> {
  const body = content.value.trim();
  if (submitting.value) return;
  if (!body) {
    showInfoToast('请输入树洞内容');
    return;
  }
  submitting.value = true;
  try {
    await createTreehole({ content: body });
    content.value = '';
    markCommunityRefresh('treeholes');
    showSuccessToast('树洞已投递，请等待审核');
    uni.navigateBack();
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.publish-treehole {
  min-height: 100vh;

  &__header {
    margin-bottom: $space-md;
  }

  &__title {
    font-size: 38rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__field {
    padding: 16rpx 0;
    @include hairline-bottom;

    &:last-child::after {
      display: none;
    }
  }

  &__field--textarea {
    padding-bottom: 0;
  }

  &__label {
    margin-bottom: 12rpx;
    color: $color-text-tertiary;
    font-size: 24rpx;
  }

  &__textarea {
    @include sl-input;
    min-height: 240rpx;
    line-height: 1.8;
  }

  &__hint {
    margin-top: 16rpx;
    text-align: right;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__submit {
    height: 88rpx;
    line-height: 88rpx;
    margin-top: $space-lg;
    border-radius: 999rpx;
    background: linear-gradient(135deg, $color-primary, #6f8fff);
    color: #fff;
    font-size: 30rpx;
    font-weight: 600;
  }
}
</style>