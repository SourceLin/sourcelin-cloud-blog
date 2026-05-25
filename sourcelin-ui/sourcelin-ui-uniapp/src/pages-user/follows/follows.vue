<template>
  <view class="follows s-container">
    <s-loading :visible="userStore.isLoggedIn && loading && items.length === 0" />
    <view class="follows__header s-card">
      <view class="follows__eyebrow">Author Radar</view>
      <view class="follows__title">关注与粉丝</view>
      <view class="follows__desc">追更作者，也回看最近有哪些读者来到你的主页。</view>
    </view>

    <s-empty
      v-if="!userStore.isLoggedIn"
      scene="login"
      title="登录后查看关注与粉丝"
      text="移动端与博客前台共用账号体系，登录后即可同步追更作者和粉丝来访记录。"
    >
      <button class="follows__login" @tap="goLogin">立即登录</button>
    </s-empty>

    <template v-else>
      <scroll-view class="follows__tabs" scroll-x show-scrollbar="false">
        <view class="follows__tabs-inner">
          <view
            v-for="tab in tabs"
            :key="tab.value"
            class="follows__tab"
            :class="{ 'follows__tab--active': activeTab === tab.value }"
            @tap="switchTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </scroll-view>

      <s-empty
        v-if="!loading && items.length === 0"
        :title="activeTab === 'followers' ? '暂时还没有粉丝' : '暂未关注任何作者'"
        :text="activeTab === 'followers' ? '继续创作和互动后，新的关注者会出现在这里。' : '先去文章详情关注感兴趣的作者，形成内容回访入口。'"
      />

      <view v-else class="follows__list">
        <view v-for="item in items" :key="item.id" class="follows__item s-card" @tap="openUserHome(item)">
          <view class="follows__user">
            <image
              v-if="avatarUrl(resolveFollowUser(item)?.avatar)"
              class="follows__avatar"
              :src="avatarUrl(resolveFollowUser(item)?.avatar)"
              mode="aspectFill"
            />
            <view v-else class="follows__avatar follows__avatar--placeholder">
              {{ getAvatarText(resolveFollowUser(item)?.nickname || resolveFollowUser(item)?.username) }}
            </view>
            <view class="follows__info">
              <view class="follows__name">{{ resolveFollowUser(item)?.nickname || resolveFollowUser(item)?.username || 'Sourcelin 用户' }}</view>
              <view class="follows__intro s-ellipsis-2">{{ resolveFollowUser(item)?.introduction || '这个用户还没有留下简介。' }}</view>
            </view>
          </view>

          <view class="follows__meta">
            <view class="follows__meta-card">
              <text class="follows__meta-value">{{ resolveFollowUser(item)?.articleCount || 0 }}</text>
              <text class="follows__meta-label">文章</text>
            </view>
            <view class="follows__meta-card">
              <text class="follows__meta-value">{{ resolveFollowUser(item)?.followerCount || 0 }}</text>
              <text class="follows__meta-label">关注者</text>
            </view>
            <view class="follows__meta-time">
              {{ activeTab === 'followers' ? '关注了你' : '关注于' }} {{ formatTime(item.createTime) }}
            </view>
          </view>
        </view>

        <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && items.length > 0" class="follows__footer">已经到底了</view>
      </view>
    </template>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import type { FollowItem, FollowUser } from '@/modules/interaction/types/interaction';
import { fetchUserFollowerPage, fetchUserFollowingPage } from '@/modules/user/api/user.api';
import { useBackToTop } from '@/shared/composables/useBackToTop';
import { useUserStore } from '@/stores/user';
import { normalizeAssetUrl } from '@/utils/url';

type FollowTab = 'followings' | 'followers';

const tabs: Array<{ label: string; value: FollowTab }> = [
  { label: '我的关注', value: 'followings' },
  { label: '我的粉丝', value: 'followers' }
];

const userStore = useUserStore();
const { backToTopVisible } = useBackToTop();
const activeTab = ref<FollowTab>('followings');
const items = ref<FollowItem[]>([]);
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = 10;

onLoad((options) => {
  if (options?.tab === 'followers') {
    activeTab.value = 'followers';
  }
});

onShow(() => {
  if (!userStore.isLoggedIn) return;
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
  void loadMore();
});

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function switchTab(tab: FollowTab): void {
  if (loading.value || activeTab.value === tab) return;
  activeTab.value = tab;
  void refresh();
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
    const result = activeTab.value === 'followers'
      ? await fetchUserFollowerPage(Number(userStore.userInfo.id), page.value, pageSize)
      : await fetchUserFollowingPage(Number(userStore.userInfo.id), page.value, pageSize);
    const nextItems = result.items || [];
    items.value = reset ? nextItems : [...items.value, ...nextItems];
    finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
  } catch {
    if (!reset) {
      page.value -= 1;
    }
  } finally {
    loading.value = false;
  }
}

function resolveFollowUser(item: FollowItem): FollowUser | undefined {
  return activeTab.value === 'followers' ? item.user : item.targetUser;
}

function formatTime(value?: string): string {
  if (!value) return '刚刚';
  return value.slice(0, 10);
}

function getAvatarText(name?: string): string {
  return (name || 'S').slice(0, 1).toUpperCase();
}

function avatarUrl(value?: string): string {
  return normalizeAssetUrl(value);
}

function openUserHome(item: FollowItem): void {
  const targetId = resolveFollowUser(item)?.id;
  if (!targetId) return;
  uni.navigateTo({ url: `/pages-user/home/home?id=${targetId}` });
}
</script>

<style lang="scss" scoped>
.follows {
  min-height: 100vh;

  &__header {
    margin-bottom: $space-md;
  }

  &__eyebrow {
    display: inline-flex;
    padding: 8rpx 20rpx;
    border-radius: 999rpx;
    background: rgba(31, 111, 235, 0.08);
    color: $color-primary;
    font-size: 22rpx;
    font-weight: 600;
    margin-bottom: $space-sm;
  }

  &__title {
    font-size: 44rpx;
    font-weight: 700;
  }

  &__desc {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
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
    min-width: 148rpx;
    padding: 18rpx 28rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.62);
    color: $color-text-secondary;
    text-align: center;
    font-size: 24rpx;
    transition: all 0.2s ease;
  }

  &__tab--active {
    background: linear-gradient(135deg, rgba(31, 111, 235, 0.16), rgba(111, 143, 255, 0.18));
    color: $color-primary;
    font-weight: 600;
    box-shadow: 0 12rpx 28rpx rgba(31, 111, 235, 0.12);
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: $space-md;
  }

  &__avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(31, 111, 235, 0.14), rgba(111, 143, 255, 0.22));
    color: $color-primary;
    font-size: 32rpx;
    font-weight: 700;
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__name {
    font-size: 30rpx;
    font-weight: 700;
  }

  &__intro {
    margin-top: 8rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.6;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: 18rpx;
    margin-top: $space-md;
  }

  &__meta-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 112rpx;
    min-height: 92rpx;
    border-radius: $radius-lg;
    background: rgba(255, 255, 255, 0.58);
  }

  &__meta-value {
    color: $color-text;
    font-size: 28rpx;
    font-weight: 700;
  }

  &__meta-label,
  &__meta-time {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__meta-time {
    margin-left: auto;
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }
}
</style>
