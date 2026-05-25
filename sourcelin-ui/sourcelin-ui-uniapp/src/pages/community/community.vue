<template>
  <view class="community">
    <s-loading :visible="loading && currentItems.length === 0" />
    <view class="community__hero">
      <view class="community__hero-head">
        <view class="community__tabs">
          <view
            v-for="tab in tabs"
            :key="tab.key"
            class="community__tab"
            :class="{ 'community__tab--active': activeTab === tab.key }"
            @tap="switchTab(tab.key)"
          >
            {{ tab.text }}
          </view>
        </view>
        <view class="community__publish" @tap="goPublish">
          {{ activeTab === 'says' ? '发说说' : '写树洞' }}
        </view>
      </view>
    </view>

    <view class="community__body">
      <s-empty v-if="isEmpty" :text="emptyText" />

      <view v-else class="community__list">
        <view
          v-for="item in currentItems"
          :key="`${activeTab}-${item.id}`"
          class="community__card"
          :class="`community__card--${activeTab}`"
        >
          <view class="community__card-head">
            <view class="community__identity" @tap="openUserHome(item)">
              <view class="community__avatar">
                <image
                  v-if="resolveAvatar(item)"
                  class="community__avatar-img"
                  :src="resolveAvatar(item)"
                  mode="aspectFill"
                />
                <text v-else>{{ resolveAuthor(item).slice(0, 1) }}</text>
              </view>
              <view>
                <view class="community__author-row">
                  <text class="community__author">{{ resolveAuthor(item) }}</text>
                  <text v-if="activeTab === 'treeholes'" class="community__inline-badge">主帖</text>
                </view>
                <view class="community__time">{{ formatDate(item.createTime) }}</view>
              </view>
            </view>
          </view>
          <view class="community__content" :class="{ 'community__content--treehole': activeTab === 'treeholes' }">
            <text>{{ item.content }}</text>
          </view>

          <view
            v-if="activeTab === 'says' && resolveImages(item).length > 0"
            class="community__media"
            :class="{
              'community__media--single': resolveImages(item).length === 1,
              'community__media--multi': resolveImages(item).length > 1
            }"
          >
            <view
              v-for="(image, index) in resolveGalleryImages(item)"
              :key="`${item.id}-image-${index}`"
              class="community__media-item"
              :class="{ 'community__media-item--featured': index === 0 && resolveImages(item).length >= 3 }"
              @tap="previewImages(resolveImages(item), index)"
            >
              <image class="community__media-img" :src="image" mode="aspectFill" lazy-load />
              <view v-if="index === resolveGalleryImages(item).length - 1 && resolveExtraImageCount(item) > 0" class="community__media-more">
                +{{ resolveExtraImageCount(item) }}
              </view>
            </view>
          </view>

          <view class="community__actions">
            <view
              class="community__action"
              :class="{ 'community__action--active': isLiked(item) }"
              @tap="toggleLike(item)"
            >
              <uni-icons type="hand-up" size="15" color="currentColor" />
              <text>{{ item.likeCount || 0 }}</text>
            </view>
            <view
              class="community__action"
              :class="{ 'community__action--active': isCollected(item) }"
              @tap="toggleCollect(item)"
            >
              <uni-icons type="star" size="15" color="currentColor" />
              <text>{{ item.collectCount || 0 }}</text>
            </view>
            <view class="community__action" @tap="openCommentSheet(item)">
              <uni-icons type="chatbubble" size="15" color="currentColor" />
              <text>{{ activeTab === 'treeholes' ? '讨论' : '' }}{{ item.commentCount || 0 }}</text>
            </view>
          </view>
        </view>

        <s-loading v-if="loading && currentItems.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && currentItems.length > 0" class="community__footer">已经到底了</view>
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view class="s-liquid-tabbar">
      <view class="s-liquid-tabbar__shell">
        <view
          v-for="item in liquidTabItems"
          :key="item.path"
          class="s-liquid-tabbar__item"
          :class="{ 's-liquid-tabbar__item--active': item.path === activeTabPath }"
          @tap="switchLiquidTab(item.path, activeTabPath)"
        >
          <view class="s-liquid-tabbar__icon-wrap">
            <image
              class="s-liquid-tabbar__icon"
              :src="item.path === activeTabPath ? item.activeIcon : item.icon"
              mode="aspectFit"
            />
          </view>
          <text class="s-liquid-tabbar__text">{{ item.text }}</text>
        </view>
      </view>
    </view>

    <view v-if="commentSheetVisible" class="community__sheet-mask" @tap="closeCommentSheet">
      <view class="community__sheet" @tap.stop>
        <view class="community__sheet-head">
          <view>
            <view class="community__sheet-title">评论互动</view>
            <view class="community__sheet-subtitle">{{ activeCommentLabel }}</view>
          </view>
          <view class="community__sheet-close" @tap="closeCommentSheet">关闭</view>
        </view>

        <s-loading
          v-if="commentLoading && commentList.length === 0"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载评论..."
        />
        <view v-else-if="commentList.length === 0" class="community__sheet-empty">
          暂无评论，来写第一条
        </view>
        <scroll-view v-else class="community__sheet-list" scroll-y>
          <view v-for="item in commentList" :key="item.id" class="community__sheet-comment">
            <view class="community__sheet-comment-head">
              <text>{{ item.userNickname || item.nickname || '读者' }}</text>
              <text>{{ formatDate(item.createTime) }}</text>
            </view>
            <view class="community__sheet-comment-content">{{ item.content }}</view>
          </view>
          <s-loading
            v-if="commentLoading"
            :visible="true"
            :fullpage="false"
            compact
            text="正在加载评论..."
          />
        </scroll-view>

        <textarea
          v-model="commentContent"
          class="community__textarea"
          placeholder="写下你的看法..."
          maxlength="300"
          auto-height
        />
        <view class="community__sheet-actions">
          <button class="community__sheet-button community__sheet-button--ghost" @tap="closeCommentSheet">取消</button>
          <button
            class="community__sheet-button community__sheet-button--primary"
            :disabled="commentSubmitting"
            @tap="submitComment"
          >
            <s-inline-loading v-if="commentSubmitting" text="发布中" light />
            <text v-else>发布</text>
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onPageScroll, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { fetchSayPage, fetchTreeholePage } from '@/modules/community/api/community.api';
import type { SayItem, TreeholeItem } from '@/modules/community/types/community';
import { createComment, fetchComments } from '@/modules/comment/api/comment.api';
import type { CommentItem, CommentSource } from '@/modules/comment/types/comment';
import { collectTarget, likeTarget, uncollectTarget, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import type { InteractionTargetType } from '@/modules/interaction/types/interaction';
import { env } from '@/config/env';
import { consumeCommunityRefresh } from '@/modules/community/utils/publish';
import { hideNativeTabbar, liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { useUserStore } from '@/stores/user';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';

type TabKey = 'says' | 'treeholes';
type CommunityItem = SayItem | TreeholeItem;

interface Tab {
  key: TabKey;
  text: string;
}

const tabs: Tab[] = [
  { key: 'says', text: '说说' },
  { key: 'treeholes', text: '树洞' }
];

const userStore = useUserStore();
const activeTab = ref<TabKey>('says');
const says = ref<SayItem[]>([]);
const treeholes = ref<TreeholeItem[]>([]);
const pageMap = ref<Record<TabKey, number>>({ says: 1, treeholes: 1 });
const totalPageMap = ref<Record<TabKey, number>>({ says: 1, treeholes: 1 });
const loading = ref(false);
const commentSheetVisible = ref(false);
const commentLoading = ref(false);
const commentSubmitting = ref(false);
const commentContent = ref('');
const commentList = ref<CommentItem[]>([]);
const activeCommentId = ref<number | null>(null);
const activeCommentSource = ref<CommentSource>('say');
const activeCommentLabel = ref('');
const backToTopVisible = ref(false);
const activeTabPath = 'pages/community/community';

const currentItems = computed<CommunityItem[]>(() =>
  activeTab.value === 'says' ? says.value : treeholes.value
);
const targetType = computed<InteractionTargetType>(() =>
  activeTab.value === 'says' ? 'say' : 'treehole'
);
const finished = computed(() => pageMap.value[activeTab.value] >= totalPageMap.value[activeTab.value]);
const isEmpty = computed(() => !loading.value && currentItems.value.length === 0);
const emptyText = computed(() => activeTab.value === 'says' ? '暂无说说' : '暂无树洞');

function formatDate(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}

function resolveAuthor(item: CommunityItem): string {
  if (activeTab.value === 'treeholes') {
    const treehole = item as TreeholeItem;
    return treehole.userNickname || treehole.nickname || '匿名洞友';
  }
  const say = item as SayItem;
  return say.userNickname || say.user?.nickname || 'Sourcelin';
}

function splitStoredUrls(value?: string): string[] {
  return value
    ? value.split(',').map((url) => normalizeAssetUrl(url.trim())).filter(Boolean)
    : [];
}

function resolveImages(item: CommunityItem): string[] {
  if (activeTab.value !== 'says') return [];
  const say = item as SayItem;
  const ids = say.imageFileIds
    ? say.imageFileIds.split(',').map((id) => id.trim()).filter((id) => /^\d+$/.test(id))
    : [];
  if (ids.length > 0) {
    return ids.map((id) => `${env.baseURL}/file/download/${id}`);
  }
  return splitStoredUrls(say.images);
}

function resolveGalleryImages(item: CommunityItem): string[] {
  return resolveImages(item).slice(0, 5);
}

function resolveExtraImageCount(item: CommunityItem): number {
  return Math.max(0, resolveImages(item).length - 5);
}

function resolveAvatar(item: CommunityItem): string {
  if (activeTab.value === 'treeholes') {
    return normalizeAssetUrl((item as TreeholeItem).avatar);
  }
  const say = item as SayItem;
  return normalizeAssetUrl(say.user?.avatar);
}

function previewImages(images: string[], index: number): void {
  if (images.length === 0) return;
  uni.previewImage({
    urls: images,
    current: images[index] || images[0]
  });
}

function goPublish(): void {
  const url = activeTab.value === 'says' ? '/pages-publish/say/say' : '/pages-publish/treehole/treehole';
  uni.navigateTo({ url });
}

function openUserHome(item: CommunityItem): void {
  if (activeTab.value !== 'says') return;
  const say = item as SayItem;
  const userId = say.user?.id || say.userId;
  if (!userId) return;
  uni.navigateTo({ url: `/pages-user/home/home?id=${userId}` });
}

function isLiked(item: CommunityItem): boolean {
  return !!item.likedByMe;
}

function isCollected(item: CommunityItem): boolean {
  if (activeTab.value === 'says') return !!(item as SayItem).isCollected;
  return !!(item as TreeholeItem).collectedByMe;
}

function setCollected(item: CommunityItem, value: boolean): void {
  if (activeTab.value === 'says') {
    (item as SayItem).isCollected = value;
  } else {
    (item as TreeholeItem).collectedByMe = value;
  }
}

function requireLogin(type: string, payload: unknown): boolean {
  if (userStore.isLoggedIn) return true;
  userStore.pushPendingAction({ type, payload });
  uni.navigateTo({ url: '/pages-user/login/login' });
  return false;
}

async function fetchPage(targetPage = 1): Promise<void> {
  if (loading.value) return;
  loading.value = true;
  try {
    const result = activeTab.value === 'says'
      ? await fetchSayPage(targetPage, 10)
      : await fetchTreeholePage(targetPage, 10);
    if (activeTab.value === 'says') {
      says.value = targetPage === 1 ? result.items as SayItem[] : says.value.concat(result.items as SayItem[]);
    } else {
      treeholes.value = targetPage === 1
        ? result.items as TreeholeItem[]
        : treeholes.value.concat(result.items as TreeholeItem[]);
    }
    pageMap.value[activeTab.value] = result.page || targetPage;
    totalPageMap.value[activeTab.value] = result.totalPages || 1;
  } finally {
    loading.value = false;
  }
}

function refresh(): Promise<void> {
  pageMap.value[activeTab.value] = 1;
  totalPageMap.value[activeTab.value] = 1;
  return fetchPage(1);
}

function loadMore(): void {
  if (loading.value || finished.value) return;
  fetchPage(pageMap.value[activeTab.value] + 1);
}

function switchTab(key: TabKey): void {
  if (activeTab.value === key) return;
  activeTab.value = key;
  if (currentItems.value.length === 0) refresh();
}

async function openCommentSheet(item: CommunityItem): Promise<void> {
  activeCommentId.value = item.id;
  activeCommentSource.value = activeTab.value === 'says' ? 'say' : 'treehole';
  activeCommentLabel.value = activeTab.value === 'says' ? '说说评论区' : '树洞评论区';
  commentContent.value = '';
  commentSheetVisible.value = true;
  commentLoading.value = true;
  try {
    const result = await fetchComments(item.id, activeCommentSource.value, 1, 20);
    commentList.value = result.items || [];
  } finally {
    commentLoading.value = false;
  }
}

function hasActiveCommentReplay(detail?: LoginSuccessEventDetail): boolean {
  if (!detail?.actions?.length || !activeCommentId.value) return false;
  return detail.actions.some((action) => {
    if (action.type !== 'comment:create') return false;
    const payload = action.payload as { targetId?: number; source?: string } | undefined;
    return payload?.targetId === activeCommentId.value && payload?.source === activeCommentSource.value;
  });
}

async function handleLoginSuccess(detail?: LoginSuccessEventDetail): Promise<void> {
  if (hasActiveCommentReplay(detail)) {
    commentContent.value = '';
  }
  await refresh();
  if (commentSheetVisible.value && activeCommentId.value) {
    commentLoading.value = true;
    try {
      const result = await fetchComments(activeCommentId.value, activeCommentSource.value, 1, 20);
      commentList.value = result.items || [];
    } finally {
      commentLoading.value = false;
    }
  }
}

function closeCommentSheet(): void {
  commentSheetVisible.value = false;
}

async function toggleLike(item: CommunityItem): Promise<void> {
  if (!requireLogin('interaction:like', { targetType: targetType.value, targetId: item.id })) return;
  const next = !isLiked(item);
  item.likedByMe = next;
  item.likeCount = Math.max(0, (item.likeCount || 0) + (next ? 1 : -1));
  try {
    if (next) await likeTarget(targetType.value, item.id);
    else await unlikeTarget(targetType.value, item.id);
  } catch {
    item.likedByMe = !next;
    item.likeCount = Math.max(0, (item.likeCount || 0) + (next ? -1 : 1));
  }
}

async function toggleCollect(item: CommunityItem): Promise<void> {
  if (!requireLogin('interaction:collect', { targetType: targetType.value, targetId: item.id })) return;
  const next = !isCollected(item);
  setCollected(item, next);
  item.collectCount = Math.max(0, (item.collectCount || 0) + (next ? 1 : -1));
  try {
    if (next) await collectTarget(targetType.value, item.id);
    else await uncollectTarget(targetType.value, item.id);
  } catch {
    setCollected(item, !next);
    item.collectCount = Math.max(0, (item.collectCount || 0) + (next ? -1 : 1));
  }
}

async function submitComment(): Promise<void> {
  const content = commentContent.value.trim();
  if (!activeCommentId.value || !content) {
    showInfoToast('请输入评论内容');
    return;
  }
  if (!requireLogin('comment:create', {
    targetId: activeCommentId.value,
    source: activeCommentSource.value,
    content
  })) {
    return;
  }
  commentSubmitting.value = true;
  try {
    await createComment({
      articleId: activeCommentId.value,
      source: activeCommentSource.value,
      content
    });
    commentContent.value = '';
    updateCommentCount(activeCommentId.value, 1);
    const result = await fetchComments(activeCommentId.value, activeCommentSource.value, 1, 20);
    commentList.value = result.items || [];
    showInfoToast('评论已提交');
  } finally {
    commentSubmitting.value = false;
  }
}

function updateCommentCount(targetId: number, delta: number): void {
  const target = currentItems.value.find((item) => item.id === targetId);
  if (!target) return;
  target.commentCount = Math.max(0, (target.commentCount || 0) + delta);
}

onPullDownRefresh(() => {
  refresh().finally(() => uni.stopPullDownRefresh());
});

onReachBottom(() => {
  loadMore();
});

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});

onShow(() => {
  hideNativeTabbar();
  const refreshTab = consumeCommunityRefresh();
  if (!refreshTab) return;
  if (refreshTab === activeTab.value) {
    void refresh();
    return;
  }
  if (refreshTab === 'says') {
    says.value = [];
    pageMap.value.says = 1;
    totalPageMap.value.says = 1;
    return;
  }
  treeholes.value = [];
  pageMap.value.treeholes = 1;
  totalPageMap.value.treeholes = 1;
});

uni.$off(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
uni.$on(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);

refresh();
</script>

<style lang="scss" scoped>
.community {
  --community-primary: var(--sl-color-primary);
  --community-primary-soft: var(--sl-color-primary-soft);
  --community-text-main: var(--sl-text-main);
  --community-text-sub: var(--sl-text-sub);
  --community-glass-pure: var(--sl-bg-glass-pure);
  --community-glass-tint: var(--sl-bg-glass-tint);
  --community-glass-border: rgba(255, 255, 255, 0.72);
  --community-glass-highlight: rgba(255, 255, 255, 0.86);
  --community-shadow: rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  background:
    radial-gradient(circle at -18% 6%, var(--sl-glow-a), rgba(255, 255, 255, 0) 36%),
    radial-gradient(circle at 114% 26%, var(--sl-glow-b), rgba(255, 255, 255, 0) 34%),
    $color-bg;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));

  &__hero {
    position: relative;
    padding: 34rpx 30rpx 30rpx;
    overflow: hidden;
  }

  &__hero-head {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__tabs {
    position: relative;
    display: flex;
    flex: 1;
    padding: 8rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.82), rgba(255, 255, 255, 0.38)),
      var(--community-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 var(--community-glass-highlight),
      0 16rpx 42rpx rgba(17, 24, 39, 0.06);
  }

  &__publish {
    flex-shrink: 0;
    min-width: 132rpx;
    min-height: 74rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(255, 255, 255, 0.44)),
      rgba(59, 89, 255, 0.12);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    color: var(--community-primary);
    font-size: 24rpx;
    font-weight: 700;
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
      0 10rpx 28rpx rgba(59, 89, 255, 0.08);
  }

  &__tab {
    flex: 1;
    min-height: 74rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    color: var(--community-text-sub);
    font-size: 28rpx;
    font-weight: 700;
    position: relative;
    border-radius: 999rpx;
    transition: all 0.25s cubic-bezier(0.25, 1, 0.5, 1);

    &--active {
      color: var(--community-primary);
      background:
        linear-gradient(135deg, rgba(255, 255, 255, 0.94) 0%, rgba(255, 255, 255, 0.68) 100%),
        rgba(59, 89, 255, 0.08);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
        0 10rpx 28rpx rgba(59, 89, 255, 0.14);
    }
  }

  &__body {
    position: relative;
    margin-top: 0;
    padding: 0 30rpx;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 28rpx;
  }

  &__card {
    position: relative;
    overflow: hidden;
    padding: 34rpx 30rpx 30rpx;
    border-radius: 38rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.68), rgba(255, 255, 255, 0.28)),
      var(--community-glass-pure);
    border: 1rpx solid rgba(255, 255, 255, 0.76);
    box-shadow:
      inset 0 1rpx 0 var(--community-glass-highlight),
      0 18rpx 44rpx var(--community-shadow);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  &__card--treeholes {
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.72) 0%, rgba(248, 250, 255, 0.32) 100%),
      rgba(255, 255, 255, 0.46);
    border-color: rgba(255, 255, 255, 0.8);
  }

  &__card::after {
    content: '';
    position: absolute;
    left: 24rpx;
    right: 24rpx;
    top: 12rpx;
    height: 44rpx;
    border-radius: 999rpx;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0));
    pointer-events: none;
  }

  &__card:active {
    transform: scale(0.985);
  }

  &__card-head,
  &__actions {
    display: flex;
    gap: $space-sm;
  }

  &__card-head {
    position: relative;
    z-index: 1;
    align-items: center;
    justify-content: flex-start;
    padding-right: 0;
  }

  &__identity {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__avatar {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.74), rgba(255, 255, 255, 0.24)),
      rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(255, 255, 255, 0.7);
    color: var(--community-primary);
    font-size: 28rpx;
    font-weight: 800;
    overflow: hidden;
    flex-shrink: 0;
  }

  &__avatar-img {
    width: 100%;
    height: 100%;
    border-radius: inherit;
    display: block;
  }

  &__author-row {
    display: flex;
    align-items: center;
    gap: 10rpx;
  }

  &__author {
    color: var(--community-text-main);
    font-size: 29rpx;
    font-weight: 700;
  }

  &__inline-badge {
    padding: 4rpx 12rpx;
    border-radius: 999rpx;
    color: var(--community-primary);
    background: rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.1);
    font-size: 19rpx;
    font-weight: 700;
  }

  &__time {
    margin-top: 4rpx;
    color: rgba(75, 85, 99, 0.62);
    font-size: 22rpx;
  }

  &__content {
    position: relative;
    z-index: 1;
    margin-top: 22rpx;
    padding: 24rpx;
    border-radius: 26rpx;
    background: rgba(255, 255, 255, 0.34);
    border: 1rpx solid rgba(255, 255, 255, 0.56);
    color: var(--community-text-main);
    font-size: 30rpx;
    line-height: 1.75;
  }

  &__content--treehole {
    margin-top: 24rpx;
    padding: 28rpx 32rpx;
    border-radius: 12rpx 32rpx 32rpx;
    background:
      linear-gradient(135deg, rgba(59, 89, 255, 0.06) 0%, rgba(143, 112, 255, 0.06) 100%),
      linear-gradient(180deg, rgba(255, 255, 255, 0.72) 0%, rgba(255, 255, 255, 0.44) 100%);
    border: 1rpx solid rgba(255, 255, 255, 0.78);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 10rpx 28rpx rgba(59, 89, 255, 0.02);
  }

  &__content--treehole::before {
    content: '';
    position: absolute;
    left: 24rpx;
    top: -12rpx;
    width: 22rpx;
    height: 22rpx;
    border-radius: 6rpx 0 0;
    background: rgba(255, 255, 255, 0.84);
    border-left: 1rpx solid rgba(255, 255, 255, 0.8);
    border-top: 1rpx solid rgba(255, 255, 255, 0.8);
    transform: rotate(45deg);
  }

  &__media {
    position: relative;
    z-index: 1;
    margin-top: 18rpx;
    display: grid;
    gap: 10rpx;
  }

  &__media--single {
    grid-template-columns: 1fr;
  }

  &__media--multi {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  &__media-item {
    position: relative;
    overflow: hidden;
    min-height: 212rpx;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.34);
    border: 1rpx solid rgba(255, 255, 255, 0.64);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.72),
      0 12rpx 28rpx rgba(17, 24, 39, 0.06);
  }

  &__media--single &__media-item {
    min-height: 360rpx;
  }

  &__media-item--featured {
    grid-column: span 2;
    min-height: 320rpx;
  }

  &__media-img {
    width: 100%;
    height: 100%;
    min-height: inherit;
    display: block;
  }

  &__media-more {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 34rpx;
    font-weight: 800;
    letter-spacing: 0.08em;
    background: rgba(17, 24, 39, 0.48);
  }

  &__actions {
    position: relative;
    z-index: 1;
    justify-content: space-between;
    margin-top: 20rpx;
  }

  &__action {
    flex: 1;
    min-height: 70rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.58);
    border: 1rpx solid rgba(255, 255, 255, 0.82);
    color: var(--community-text-sub);
    font-size: 24rpx;
    font-weight: 700;
    box-shadow: 0 4rpx 10rpx rgba(17, 24, 39, 0.01);
    transition: all 0.2s ease;
  }

  &__action--active {
    color: var(--community-primary);
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(255, 255, 255, 0.44)),
      rgba(59, 89, 255, 0.12);
    border-color: rgba(59, 89, 255, 0.26);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 8rpx 20rpx rgba(59, 89, 255, 0.08);
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }

  &__sheet-mask {
    position: fixed;
    inset: 0;
    z-index: 120;
    display: flex;
    align-items: flex-end;
    background: rgba(17, 24, 39, 0.54);
  }

  &__sheet {
    width: 100%;
    max-height: 78vh;
    padding: $space-lg $space-md calc(#{$space-lg} + env(safe-area-inset-bottom));
    border-radius: 36rpx 36rpx 0 0;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
  }

  &__sheet-head,
  &__sheet-comment-head,
  &__sheet-actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
  }

  &__sheet-title {
    color: var(--community-text-main);
    font-size: 32rpx;
    font-weight: 700;
  }

  &__sheet-subtitle,
  &__sheet-close,
  &__sheet-comment-head,
  &__sheet-empty,
  &__sheet-loading {
    color: rgba(75, 85, 99, 0.68);
    font-size: 22rpx;
  }

  &__sheet-list {
    max-height: 340rpx;
    margin-top: $space-md;
    padding-right: 8rpx;
  }

  &__sheet-comment {
    padding: $space-sm 0;
    @include hairline-bottom;

    &:last-child::after {
      display: none;
    }
  }

  &__sheet-comment-content {
    margin-top: 8rpx;
    color: var(--community-text-main);
    font-size: 26rpx;
    line-height: 1.7;
  }

  &__sheet-empty,
  &__sheet-loading {
    padding: $space-lg 0;
    text-align: center;
  }

  &__textarea {
    width: 100%;
    min-height: 148rpx;
    margin-top: $space-md;
    padding: $space-md;
    box-sizing: border-box;
    border-radius: $radius-lg;
    background: rgba(245, 247, 250, 0.92);
    border: 1rpx solid rgba(229, 231, 235, 0.88);
    font-size: 28rpx;
  }

  &__sheet-actions {
    margin-top: $space-md;
  }

  &__sheet-button {
    flex: 1;
    min-height: 84rpx;
    line-height: 84rpx;
    border-radius: 999rpx;
    font-size: 28rpx;
  }

  &__sheet-button::after {
    border: none;
  }

  &__sheet-button:active {
    transform: scale(0.98);
  }

  &__sheet-button--ghost {
    color: var(--community-text-sub);
    background: rgba(255, 255, 255, 0.72);
    border: 1rpx solid rgba(229, 231, 235, 0.9);
  }

  &__sheet-button--primary {
    background: linear-gradient(135deg, var(--community-primary), var(--community-primary-soft));
    color: #fff;
    box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
  }
}
</style>
