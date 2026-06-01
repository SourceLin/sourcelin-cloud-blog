<template>
  <view class="profile s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading && !profile" text="正在同步资料..." />

    <view class="profile__hero s-card">
      <view class="profile__hero-main">
        <view class="profile__avatar-wrap" @tap="chooseAvatar">
          <image v-if="avatarUrl" class="profile__avatar" :src="avatarUrl" mode="aspectFill" />
          <view v-else class="profile__avatar profile__avatar--placeholder">{{ avatarText }}</view>
          <view v-if="avatarUploading" class="profile__avatar-mask">
            <s-inline-loading text="上传中" light />
          </view>
        </view>
        <view class="profile__hero-text">
          <view class="profile__name">{{ displayName }}</view>
          <view class="profile__desc">{{ profile?.introduction || '补充一句自我介绍，让更多读者认识你。' }}</view>
        </view>
      </view>

      <view class="profile__stats">
        <view class="profile__stat">
          <text class="profile__stat-value">{{ profile?.articleCount || 0 }}</text>
          <text class="profile__stat-label">文章</text>
        </view>
        <view class="profile__stat">
          <text class="profile__stat-value">{{ profile?.followerCount || 0 }}</text>
          <text class="profile__stat-label">关注者</text>
        </view>
      </view>
    </view>

    <view class="profile__form s-card">
      <view class="profile__field">
        <view class="profile__label">昵称</view>
        <input v-model.trim="form.nickName" class="profile__input" maxlength="24" placeholder="请输入昵称" placeholder-class="s-placeholder">
      </view>
      <view class="profile__field">
        <view class="profile__label">邮箱</view>
        <input v-model.trim="form.email" class="profile__input" maxlength="75" placeholder="请输入邮箱" placeholder-class="s-placeholder">
      </view>
      <view class="profile__field">
        <view class="profile__label">手机号</view>
        <input v-model.trim="form.phonenumber" class="profile__input" maxlength="20" placeholder="请输入手机号" placeholder-class="s-placeholder">
      </view>
      <view class="profile__field">
        <view class="profile__label">性别</view>
        <view class="profile__gender">
          <view
            v-for="item in genderOptions"
            :key="item.value"
            class="profile__gender-chip"
            :class="{ 'profile__gender-chip--active': form.sex === item.value }"
            @tap="form.sex = item.value"
          >
            {{ item.label }}
          </view>
        </view>
      </view>
      <view class="profile__field profile__field--textarea">
        <view class="profile__label">简介</view>
        <textarea
          v-model.trim="form.introduction"
          class="profile__textarea"
          maxlength="120"
          auto-height
          placeholder="介绍一下你的创作方向或兴趣。"
          placeholder-class="s-placeholder"
        />
      </view>
    </view>

    <button class="profile__submit sl-button sl-button--primary" :disabled="submitting" @tap="submitProfile">
      <s-inline-loading v-if="submitting" text="保存中" light />
      <text v-else>保存资料</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import {
  fetchUserProfile,
  updateUserProfile,
  uploadUserAvatar
} from '@/modules/user/api/user.api';
import { mapFrontUserInfo } from '@/shared/utils/user-mapper';
import { useUserStore, type UserInfo } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { pickSingleImagePath } from '@/utils/media';
import { normalizeAssetUrl } from '@/utils/url';

const userStore = useUserStore();
const themeStore = useThemeStore();
const profile = ref<UserInfo | null>(userStore.userInfo);
const loading = ref(false);
const submitting = ref(false);
const avatarUploading = ref(false);

onShow(() => {
  themeStore.syncNativeArea();
});

const form = reactive({
  nickName: '',
  email: '',
  phonenumber: '',
  sex: 0,
  introduction: ''
});

const genderOptions = [
  { label: '保密', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 }
];

const displayName = computed(() =>
  profile.value?.nickname || profile.value?.username || '未登录用户'
);
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase());
const avatarUrl = computed(() => normalizeAssetUrl(profile.value?.avatar));

onLoad(() => {
  if (!userStore.isLoggedIn) {
    uni.redirectTo({ url: '/pages-user/login/login' });
    return;
  }
  loadProfile();
});

async function loadProfile(): Promise<void> {
  loading.value = true;
  try {
    const data = await fetchUserProfile();
    const mapped = mapFrontUserInfo(data);
    profile.value = mapped;
    userStore.updateUserInfo(mapped);
    syncForm(data);
  } catch {
    profile.value = userStore.userInfo;
    syncFormFromStore();
  } finally {
    loading.value = false;
  }
}

function syncForm(data: {
  nickName?: string;
  email?: string;
  phonenumber?: string;
  sex?: number;
  introduction?: string;
}): void {
  form.nickName = data.nickName || '';
  form.email = data.email || '';
  form.phonenumber = data.phonenumber || '';
  form.sex = data.sex ?? 0;
  form.introduction = data.introduction || '';
}

function syncFormFromStore(): void {
  form.nickName = profile.value?.nickname || '';
  form.email = profile.value?.email || '';
  form.phonenumber = profile.value?.phone || '';
  form.sex = 0;
  form.introduction = profile.value?.introduction || '';
}

function validateProfile(): boolean {
  if (!form.nickName) {
    showInfoToast('请输入昵称');
    return false;
  }
  return true;
}

async function submitProfile(): Promise<void> {
  if (submitting.value || !validateProfile()) return;
  submitting.value = true;
  try {
    await updateUserProfile({
      nickName: form.nickName,
      email: form.email,
      phonenumber: form.phonenumber,
      sex: form.sex,
      introduction: form.introduction
    });
    await loadProfile();
    showSuccessToast('资料已更新');
  } finally {
    submitting.value = false;
  }
}

async function chooseAvatar(): Promise<void> {
  if (avatarUploading.value) return;
  const filePath = await pickSingleImagePath({
    sizeType: ['compressed'],
    sourceType: ['album', 'camera']
  });
  if (!filePath) return;
  avatarUploading.value = true;
  try {
    const uploaded = await uploadUserAvatar(filePath);
    profile.value = {
      ...(profile.value || { id: userStore.userInfo?.id || 0, username: userStore.userInfo?.username || '' }),
      ...profile.value,
      avatar: uploaded.avatar || profile.value?.avatar || ''
    };
    if (userStore.userInfo) {
      userStore.updateUserInfo({
        ...userStore.userInfo,
        avatar: uploaded.avatar || userStore.userInfo.avatar
      });
    }
    showSuccessToast('头像已更新');
  } finally {
    avatarUploading.value = false;
  }
}
</script>

<style lang="scss" scoped>
.profile {
  min-height: 100vh;

  &__hero-main {
    display: flex;
    align-items: center;
    gap: $space-md;
  }

  &__avatar-wrap {
    position: relative;
    width: 140rpx;
    height: 140rpx;
    flex-shrink: 0;
  }

  &__avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    overflow: hidden;
  }

  &__avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(31, 111, 235, 0.14), rgba(111, 143, 255, 0.22));
    color: $color-primary;
    font-size: 42rpx;
    font-weight: 700;
  }

  &__avatar-mask {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: rgba(17, 24, 39, 0.42);
  }

  &__hero-text {
    flex: 1;
    min-width: 0;
  }

  &__name {
    font-size: 36rpx;
    font-weight: 700;
    margin-bottom: $space-xs;
  }

  &__desc {
    color: $color-text-secondary;
    font-size: 26rpx;
    line-height: 1.7;
  }

  &__stats {
    display: flex;
    gap: $space-md;
    margin-top: $space-lg;
  }

  &__stat {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 0;
    border-radius: 24rpx;
    background: var(--sl-bg-glass-tint);
  }

  &__stat-value {
    color: $color-text;
    font-size: 34rpx;
    font-weight: 700;
  }

  &__stat-label {
    margin-top: 4rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__form {
    margin-top: $space-md;
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
    @include sl-input;
  }

  &__input {
    min-height: $input-min-height;
    line-height: $input-line-height;
  }

  &__gender {
    display: flex;
    gap: 16rpx;
  }

  &__gender-chip {
    min-width: 120rpx;
    padding: 14rpx 24rpx;
    border-radius: 999rpx;
    text-align: center;
    background: var(--sl-control-bg);
    color: $color-text-secondary;
    font-size: 24rpx;
  }

  &__gender-chip--active {
    background: rgba(31, 111, 235, 0.12);
    color: $color-primary;
    font-weight: 600;
  }

  &__textarea {
    min-height: 160rpx;
    padding-bottom: 12rpx;
    line-height: 1.7;
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
