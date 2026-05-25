<template>
  <view class="publish-treehole s-container">
    <view class="publish-treehole__header s-card">
      <view class="publish-treehole__title">树洞投递</view>
      <view class="publish-treehole__desc">支持匿名表达。草稿会在本地保留 1 小时，提交前请确认内容适合公开展示。</view>
    </view>

    <view class="publish-treehole__form s-card">
      <view class="publish-treehole__field">
        <view class="publish-treehole__label">匿名昵称（可选）</view>
        <input v-model.trim="nickname" class="publish-treehole__input" maxlength="16" placeholder="例如：洞友 / 午后读者">
      </view>
      <view class="publish-treehole__field">
        <view class="publish-treehole__label">头像链接（可选）</view>
        <input v-model.trim="avatar" class="publish-treehole__input" maxlength="255" placeholder="http(s) 图片地址">
      </view>
      <view class="publish-treehole__field publish-treehole__field--textarea">
        <view class="publish-treehole__label">内容</view>
        <textarea
          v-model.trim="content"
          class="publish-treehole__textarea"
          maxlength="500"
          auto-height
          placeholder="把想说的话留在这里，审核通过后会展示在树洞广场。"
        />
      </view>
      <view class="publish-treehole__hint">{{ content.length }}/500</view>
    </view>

    <button class="publish-treehole__submit" :disabled="submitting" @tap="submit">
      <s-inline-loading v-if="submitting" text="提交中" light />
      <text v-else>匿名投递</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { createTreehole } from '@/modules/community/api/community.api';
import { markCommunityRefresh } from '@/modules/community/utils/publish';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const DRAFT_KEY = 'publish.treehole.draft';
const nickname = ref('');
const avatar = ref('');
const content = ref('');
const submitting = ref(false);

onLoad(() => {
  const draft = getStorage<{ nickname: string; avatar: string; content: string } | null>(DRAFT_KEY, null);
  if (draft) {
    nickname.value = draft.nickname || '';
    avatar.value = draft.avatar || '';
    content.value = draft.content || '';
  }
});

onUnload(() => {
  persistDraft();
});

function persistDraft(): void {
  const value = content.value.trim();
  if (!value && !nickname.value.trim() && !avatar.value.trim()) {
    removeStorage(DRAFT_KEY);
    return;
  }
  setStorage(DRAFT_KEY, {
    nickname: nickname.value.trim(),
    avatar: avatar.value.trim(),
    content: value
  }, 3600);
}

async function submit(): Promise<void> {
  const body = content.value.trim();
  if (submitting.value) return;
  if (!body) {
    showInfoToast('请输入树洞内容');
    return;
  }
  submitting.value = true;
  try {
    await createTreehole({
      nickname: nickname.value.trim(),
      avatar: avatar.value.trim(),
      content: body
    });
    nickname.value = '';
    avatar.value = '';
    content.value = '';
    removeStorage(DRAFT_KEY);
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

  &__input,
  &__textarea {
    width: 100%;
    color: $color-text;
    font-size: 28rpx;
  }

  &__textarea {
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
