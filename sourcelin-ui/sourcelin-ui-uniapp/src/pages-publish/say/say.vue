<template>
  <view class="publish-say s-container">
    <view class="publish-say__header s-card">
      <view class="publish-say__title">发布说说</view>
      <view class="publish-say__desc">支持轻量文字与多图发布，适合记录即时灵感、近况与现场感。</view>
    </view>

    <view class="publish-say__form s-card">
      <textarea
        v-model.trim="content"
        class="publish-say__textarea"
        maxlength="500"
        auto-height
        placeholder="记录此刻的想法、近况或想分享的内容..."
      />
      <view class="publish-say__hint">{{ content.length }}/500</view>

      <view class="publish-say__media-head">
        <view class="publish-say__media-title">配图（最多 9 张）</view>
        <view class="publish-say__media-desc">默认使用压缩图，单张建议不超过 2MB。</view>
      </view>

      <view v-if="images.length > 0" class="publish-say__media-grid">
        <view
          v-for="(item, index) in images"
          :key="item.path"
          class="publish-say__media-item"
          @tap="previewImage(index)"
        >
          <image class="publish-say__media-image" :src="item.path" mode="aspectFill" />
          <view class="publish-say__media-remove" @tap.stop="removeImage(index)">×</view>
        </view>
      </view>

      <view
        v-if="images.length < MAX_IMAGE_COUNT"
        class="publish-say__picker"
        @tap="chooseImages"
      >
        <view class="publish-say__picker-icon">+</view>
        <view class="publish-say__picker-text">添加图片</view>
      </view>
    </view>

    <button class="publish-say__submit sl-button sl-button--primary" :disabled="submitting" @tap="submit">
      <s-inline-loading v-if="submitting" text="发布中" light />
      <text v-else>发布说说</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { createSay } from '@/modules/community/api/community.api';
import { markCommunityRefresh } from '@/modules/community/utils/publish';
import { uploadPublicFile } from '@/shared/api/file.api';
import { useUserStore } from '@/stores/user';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const DRAFT_KEY = 'publish.say.draft';
const MAX_IMAGE_COUNT = 9;

interface SayDraft {
  content: string;
  imagePaths: string[];
}

interface SelectedImage {
  path: string;
}

const userStore = useUserStore();
const content = ref('');
const submitting = ref(false);
const images = ref<SelectedImage[]>([]);

onLoad(() => {
  if (!userStore.isLoggedIn) {
    uni.redirectTo({ url: '/pages-user/login/login' });
    return;
  }
  const draft = getStorage<SayDraft | string>(DRAFT_KEY, '');
  if (typeof draft === 'string') {
    content.value = draft;
    return;
  }
  content.value = draft?.content || '';
  images.value = (draft?.imagePaths || []).map((path) => ({ path }));
});

onUnload(() => {
  persistDraft();
});

function persistDraft(): void {
  const value = content.value.trim();
  const imagePaths = images.value.map((item) => item.path);
  if (!value && imagePaths.length === 0) {
    removeStorage(DRAFT_KEY);
    return;
  }
  setStorage<SayDraft>(DRAFT_KEY, {
    content: value,
    imagePaths
  }, 3600);
}

function chooseImages(): void {
  const remainCount = MAX_IMAGE_COUNT - images.value.length;
  if (remainCount <= 0) return;
  uni.chooseImage({
    count: remainCount,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const rawPaths = Array.isArray(res.tempFilePaths)
        ? res.tempFilePaths
        : res.tempFilePaths
          ? [res.tempFilePaths]
          : [];
      const paths = rawPaths.filter((path): path is string => Boolean(path));
      if (!paths.length) return;
      const next = [...images.value];
      paths.forEach((path) => {
        if (next.length < MAX_IMAGE_COUNT) {
          next.push({ path });
        }
      });
      images.value = next;
      persistDraft();
    }
  });
}

function removeImage(index: number): void {
  images.value.splice(index, 1);
  persistDraft();
}

function previewImage(index: number): void {
  const urls = images.value.map((item) => item.path);
  if (!urls.length) return;
  uni.previewImage({
    urls,
    current: urls[index] || urls[0]
  });
}

async function uploadImages(): Promise<string> {
  if (!images.value.length) return '';
  const fileIds: number[] = [];
  for (const item of images.value) {
    const uploaded = await uploadPublicFile(item.path);
    if (!uploaded.fileId) {
      throw new Error('图片上传结果缺少 fileId');
    }
    fileIds.push(Number(uploaded.fileId));
  }
  return fileIds.join(',');
}

async function submit(): Promise<void> {
  const value = content.value.trim();
  if (submitting.value) return;
  if (!value) {
    showInfoToast('请输入说说内容');
    return;
  }
  submitting.value = true;
  try {
    const imageFileIds = await uploadImages();
    await createSay({
      content: value,
      imageFileIds: imageFileIds || undefined
    });
    content.value = '';
    images.value = [];
    removeStorage(DRAFT_KEY);
    markCommunityRefresh('says');
    showSuccessToast('说说已发布');
    uni.navigateBack();
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.publish-say {
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

  &__textarea {
    width: 100%;
    min-height: 280rpx;
    color: $color-text;
    font-size: 28rpx;
    line-height: 1.8;
  }

  &__hint {
    margin-top: $space-sm;
    text-align: right;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__media-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20rpx;
    margin-top: $space-lg;
  }

  &__media-title {
    color: $color-text;
    font-size: 26rpx;
    font-weight: 700;
  }

  &__media-desc {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__media-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 18rpx;
    margin-top: $space-md;
  }

  &__media-item,
  &__picker {
    position: relative;
    height: 180rpx;
    border-radius: 24rpx;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.56);
    border: 1rpx solid rgba(255, 255, 255, 0.78);
  }

  &__media-image {
    width: 100%;
    height: 100%;
    display: block;
  }

  &__media-remove {
    position: absolute;
    top: 10rpx;
    right: 10rpx;
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: rgba(17, 24, 39, 0.58);
    color: #fff;
    font-size: 26rpx;
    font-weight: 700;
  }

  &__picker {
    margin-top: $space-md;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    border-style: dashed;
    border-color: rgba(31, 111, 235, 0.26);
    color: $color-primary;
  }

  &__picker-icon {
    font-size: 44rpx;
    line-height: 1;
  }

  &__picker-text {
    font-size: 24rpx;
    font-weight: 600;
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
