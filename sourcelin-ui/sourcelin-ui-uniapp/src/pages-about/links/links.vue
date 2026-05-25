<template>
  <view class="links s-container">
    <s-loading :visible="loading && items.length === 0" text="正在加载友链..." />
    <view class="links__header s-card">
      <view class="links__title">友情链接</view>
      <view class="links__desc">收录值得访问的伙伴站点，也支持从移动端提交申请。</view>
    </view>

    <s-empty
      v-if="!loading && items.length === 0"
      title="暂无友情链接"
      text="暂时还没有公开展示的伙伴站点。"
    />

    <view v-else class="links__list">
      <view v-for="item in items" :key="item.id" class="links__item s-card" @tap="copyLink(item.url)">
        <view class="links__item-head">
          <image v-if="item.avatar" class="links__avatar" :src="normalizeAssetUrl(item.avatar)" mode="aspectFill" />
          <view v-else class="links__avatar links__avatar--placeholder">{{ item.name.slice(0, 1) }}</view>
          <view class="links__info">
            <view class="links__name">{{ item.name }}</view>
            <view class="links__url s-ellipsis">{{ item.url }}</view>
          </view>
        </view>
        <view class="links__desc-text s-ellipsis-2">{{ item.description || '点击复制站点链接后可前往访问。' }}</view>
      </view>
    </view>

    <view class="links__apply s-card">
      <view class="links__apply-title">申请友链</view>
      <view class="links__field">
        <input v-model.trim="form.name" class="links__input" maxlength="50" placeholder="网站名称">
      </view>
      <view class="links__field">
        <input v-model.trim="form.url" class="links__input" maxlength="90" placeholder="网站地址（支持直接粘贴域名）">
      </view>
      <view class="links__field">
        <input v-model.trim="form.email" class="links__input" maxlength="75" placeholder="联系邮箱">
      </view>
      <view class="links__field">
        <input v-model.trim="form.avatar" class="links__input" maxlength="255" placeholder="头像链接（可选）">
      </view>
      <view class="links__field links__field--textarea">
        <textarea v-model.trim="form.description" class="links__textarea" maxlength="255" auto-height placeholder="站点描述（可选）" />
      </view>
      <button class="links__submit" :disabled="submitting" @tap="submitApply">
        <s-inline-loading v-if="submitting" text="提交中" light />
        <text v-else>提交申请</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { applyFriendLink, fetchFriendLinks } from '@/modules/site/api/site.api';
import type { FriendLinkItem } from '@/modules/site/types/site';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';

const items = ref<FriendLinkItem[]>([]);
const loading = ref(false);
const submitting = ref(false);
const form = reactive({
  name: '',
  url: '',
  email: '',
  avatar: '',
  description: ''
});

onLoad(() => {
  loadLinks();
});

async function loadLinks(): Promise<void> {
  loading.value = true;
  try {
    items.value = await fetchFriendLinks();
  } finally {
    loading.value = false;
  }
}

function copyLink(url: string): void {
  uni.setClipboardData({
    data: url,
    success: () => showSuccessToast('链接已复制')
  });
}

function validateApply(): boolean {
  if (!form.name || !form.url || !form.email) {
    showInfoToast('请补全网站名称、网址和联系邮箱');
    return false;
  }
  return true;
}

async function submitApply(): Promise<void> {
  if (submitting.value || !validateApply()) return;
  submitting.value = true;
  try {
    await applyFriendLink({ ...form });
    form.name = '';
    form.url = '';
    form.email = '';
    form.avatar = '';
    form.description = '';
    showSuccessToast('友链申请已提交');
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.links {
  min-height: 100vh;

  &__header,
  &__apply {
    margin-bottom: $space-md;
  }

  &__title,
  &__apply-title {
    font-size: 34rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
    margin-bottom: $space-md;
  }

  &__item-head {
    display: flex;
    align-items: center;
    gap: $space-md;
  }

  &__avatar {
    width: 84rpx;
    height: 84rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(31, 111, 235, 0.12);
    color: $color-primary;
    font-size: 30rpx;
    font-weight: 700;
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__name {
    font-size: 28rpx;
    font-weight: 700;
  }

  &__url,
  &__desc-text {
    color: $color-text-secondary;
    font-size: 24rpx;
  }

  &__url {
    margin-top: 6rpx;
  }

  &__desc-text {
    margin-top: 14rpx;
    line-height: 1.7;
  }

  &__field {
    margin-top: 16rpx;
    padding: 18rpx 22rpx;
    border-radius: 22rpx;
    background: rgba(255, 255, 255, 0.54);
  }

  &__field--textarea {
    padding-bottom: 8rpx;
  }

  &__input,
  &__textarea {
    width: 100%;
    color: $color-text;
    font-size: 26rpx;
  }

  &__textarea {
    min-height: 140rpx;
    line-height: 1.7;
  }

  &__submit {
    height: 84rpx;
    line-height: 84rpx;
    margin-top: $space-lg;
    border-radius: 999rpx;
    background: linear-gradient(135deg, $color-primary, #6f8fff);
    color: #fff;
    font-size: 28rpx;
    font-weight: 600;
  }
}
</style>
