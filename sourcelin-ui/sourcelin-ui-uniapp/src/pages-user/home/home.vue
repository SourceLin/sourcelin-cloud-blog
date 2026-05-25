<template>
  <view class="user-home s-container">
    <s-loading :visible="loading && !user" text="正在同步作者主页..." />
    <s-empty
      v-if="!loading && !user"
      title="用户不存在"
      text="该作者可能已隐藏主页或暂时不可访问。"
    />

    <template v-else-if="user">
      <view class="user-home__hero s-card">
        <image v-if="avatarUrl" class="user-home__avatar" :src="avatarUrl" mode="aspectFill" />
        <view v-else class="user-home__avatar user-home__avatar--placeholder">{{ avatarText }}</view>
        <view class="user-home__name">{{ user.nickName || user.userName || 'Sourcelin 用户' }}</view>
        <view class="user-home__intro">{{ user.introduction || '这个作者还没有留下简介。' }}</view>
        <view class="user-home__stats">
          <view class="user-home__stat">
            <text class="user-home__stat-value">{{ user.articleCount || 0 }}</text>
            <text class="user-home__stat-label">文章</text>
          </view>
          <view class="user-home__stat">
            <text class="user-home__stat-value">{{ user.followerCount || 0 }}</text>
            <text class="user-home__stat-label">关注者</text>
          </view>
        </view>
      </view>

      <scroll-view class="user-home__tabs" scroll-x show-scrollbar="false">
        <view class="user-home__tabs-inner">
          <view
            v-for="tab in tabs"
            :key="tab.key"
            class="user-home__tab"
            :class="{ 'user-home__tab--active': activeTab === tab.key }"
            @tap="switchTab(tab.key)"
          >
            {{ tab.label }}
          </view>
        </view>
      </scroll-view>

      <s-empty v-if="!loading && activeItems.length === 0" text="当前没有可展示的内容" />

      <view v-else-if="activeTab === 'articles'" class="user-home__list">
        <view v-for="item in articles" :key="item.id" class="user-home__card s-card" @tap="openArticle(item.id)">
          <view class="user-home__card-title s-ellipsis-2">{{ item.title }}</view>
          <view class="user-home__card-desc s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
        </view>
      </view>

      <view v-else class="user-home__list">
        <view v-for="item in activeFollowItems" :key="item.id" class="user-home__follow s-card" @tap="openRelatedUser(item)">
          <view class="user-home__follow-head">
            <image
              v-if="resolveFollowAvatar(item)"
              class="user-home__follow-avatar"
              :src="resolveFollowAvatar(item)"
              mode="aspectFill"
            />
            <view v-else class="user-home__follow-avatar user-home__follow-avatar--placeholder">
              {{ resolveFollowName(item).slice(0, 1) }}
            </view>
            <view class="user-home__follow-info">
              <view class="user-home__follow-name">{{ resolveFollowName(item) }}</view>
              <view class="user-home__follow-intro s-ellipsis-2">{{ resolveFollowIntro(item) }}</view>
            </view>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchUserArticlePage, fetchUserDetail, fetchUserFollowerPage, fetchUserFollowingPage } from '@/modules/user/api/user.api';
import type { FrontUserInfo } from '@/modules/user/types/user';
import type { ArticleSummary } from '@/modules/article/types/article';
import type { FollowItem } from '@/modules/interaction/types/interaction';
import { normalizeAssetUrl } from '@/utils/url';

type TabKey = 'articles' | 'followers' | 'followings';

const tabs: Array<{ key: TabKey; label: string }> = [
  { key: 'articles', label: '文章' },
  { key: 'followers', label: '粉丝' },
  { key: 'followings', label: '关注' }
];

const loading = ref(false);
const userId = ref(0);
const user = ref<FrontUserInfo | null>(null);
const activeTab = ref<TabKey>('articles');
const articles = ref<ArticleSummary[]>([]);
const followers = ref<FollowItem[]>([]);
const followings = ref<FollowItem[]>([]);

const avatarUrl = computed(() => normalizeAssetUrl(user.value?.avatar));
const avatarText = computed(() => (user.value?.nickName || user.value?.userName || 'S').slice(0, 1).toUpperCase());
const activeItems = computed(() => {
  if (activeTab.value === 'articles') return articles.value;
  return activeTab.value === 'followers' ? followers.value : followings.value;
});
const activeFollowItems = computed(() => activeTab.value === 'followers' ? followers.value : followings.value);

onLoad((options) => {
  const id = Number(options?.id);
  if (!Number.isFinite(id) || id <= 0) {
    return;
  }
  userId.value = id;
  loadAll();
});

async function loadAll(): Promise<void> {
  loading.value = true;
  try {
    const [detail, articlePage, followerPage, followingPage] = await Promise.all([
      fetchUserDetail(userId.value),
      fetchUserArticlePage(userId.value, 1, 20),
      fetchUserFollowerPage(userId.value, 1, 20),
      fetchUserFollowingPage(userId.value, 1, 20)
    ]);
    user.value = detail;
    articles.value = articlePage.items || [];
    followers.value = followerPage.items || [];
    followings.value = followingPage.items || [];
  } finally {
    loading.value = false;
  }
}

function switchTab(tab: TabKey): void {
  activeTab.value = tab;
}

function openArticle(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

function resolveFollowUser(item: FollowItem) {
  return activeTab.value === 'followers' ? item.user : item.targetUser;
}

function resolveFollowName(item: FollowItem): string {
  const target = resolveFollowUser(item);
  return target?.nickname || target?.username || 'Sourcelin 用户';
}

function resolveFollowIntro(item: FollowItem): string {
  return resolveFollowUser(item)?.introduction || '这个用户还没有留下简介。';
}

function resolveFollowAvatar(item: FollowItem): string {
  return normalizeAssetUrl(resolveFollowUser(item)?.avatar);
}

function openRelatedUser(item: FollowItem): void {
  const target = resolveFollowUser(item);
  if (!target?.id || target.id === userId.value) return;
  uni.navigateTo({ url: `/pages-user/home/home?id=${target.id}` });
}
</script>

<style lang="scss" scoped>
.user-home {
  min-height: 100vh;

  &__hero {
    text-align: center;
  }

  &__avatar {
    width: 128rpx;
    height: 128rpx;
    border-radius: 50%;
    margin: 0 auto $space-md;
  }

  &__avatar--placeholder,
  &__follow-avatar--placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(31, 111, 235, 0.12);
    color: $color-primary;
    font-weight: 700;
  }

  &__name {
    font-size: 36rpx;
    font-weight: 700;
  }

  &__intro {
    margin-top: 12rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__stats {
    display: flex;
    gap: $space-md;
    margin-top: $space-lg;
  }

  &__stat {
    flex: 1;
    padding: 18rpx 0;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.42);
  }

  &__stat-value {
    display: block;
    font-size: 32rpx;
    font-weight: 700;
  }

  &__stat-label {
    margin-top: 6rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__tabs {
    margin: $space-md 0;
    white-space: nowrap;
  }

  &__tabs-inner {
    display: inline-flex;
    gap: 16rpx;
    min-width: 100%;
  }

  &__tab {
    min-width: 120rpx;
    padding: 16rpx 24rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.56);
    color: $color-text-secondary;
    text-align: center;
    font-size: 24rpx;
  }

  &__tab--active {
    color: $color-primary;
    background: rgba(31, 111, 235, 0.12);
    font-weight: 600;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__card-title,
  &__follow-name {
    font-size: 28rpx;
    font-weight: 700;
  }

  &__card-desc,
  &__follow-intro {
    margin-top: 10rpx;
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.7;
  }

  &__follow-head {
    display: flex;
    align-items: center;
    gap: $space-md;
  }

  &__follow-avatar {
    width: 84rpx;
    height: 84rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__follow-info {
    flex: 1;
    min-width: 0;
  }
}
</style>
