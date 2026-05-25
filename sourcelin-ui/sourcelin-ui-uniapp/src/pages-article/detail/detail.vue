<template>
  <view class="detail s-container">
    <s-loading :visible="loading" text="正在同步灵感..." />

    <s-empty
      v-if="!loading && !article"
      title="文章不存在或已下架"
      text="该内容可能已被删除、隐藏，或暂时无法访问。"
    />

    <view v-else-if="article" class="detail__content">
      <image v-if="coverUrl" class="detail__cover" :src="coverUrl" mode="aspectFill" />

      <view class="detail__header s-card">
        <view class="detail__category">{{ article.categoryName || '文章' }}</view>
        <view class="detail__title">{{ article.title }}</view>
        <view class="detail__meta">
          <text>{{ article.author || article.user?.nickname || 'Sourcelin' }}</text>
          <text>{{ formatDate(article.createTime) }}</text>
          <text>{{ article.viewCount || 0 }} 阅读</text>
        </view>
      </view>

      <view v-if="article.summary" class="detail__summary s-card">
        {{ article.summary }}
      </view>

      <view class="detail__author s-card">
        <view class="detail__author-main" @tap="openAuthorHome">
          <view class="detail__author-info">
            <view class="detail__author-name">{{ article.user?.nickname || article.author || '作者' }}</view>
            <view class="detail__author-desc">
              {{ article.user?.followerCount || 0 }} 关注者 · {{ article.user?.articleCount || 0 }} 篇文章
            </view>
          </view>
        </view>
        <button class="detail__follow sl-button sl-button--primary sl-button--sm" size="mini" @tap.stop="toggleFollow">
          {{ article.isFollowed ? '已关注' : '关注' }}
        </button>
      </view>

      <view class="detail__body s-card">
        <rich-text v-if="article.content" :nodes="article.content" />
        <view v-else class="detail__empty-content">暂无正文内容</view>
      </view>

      <view v-if="article.readFull === false" class="detail__unlock s-card">
        <s-empty
          scene="login"
          title="登录后查看完整内容"
          :text="article.unlockHint || '登录后查看完整内容，还可收藏回访。'"
        >
          <button class="detail__unlock-button sl-button sl-button--primary" @tap="goLogin">去登录</button>
        </s-empty>
      </view>

      <view class="detail__comments s-card">
        <view class="detail__section-head">
          <text class="detail__section-title">评论</text>
          <text class="detail__comment-action" @tap="openCommentSheet">写评论</text>
        </view>
        <s-loading
          v-if="commentLoading && comments.length === 0"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载评论..."
        />
        <s-empty
          v-else-if="comments.length === 0"
          title="还没有评论"
          text="来写第一条留言，开启这篇文章的讨论。"
        />
        <view v-for="item in comments" :key="item.id" class="detail__comment">
          <view class="detail__comment-head">
            <text>{{ item.userNickname || item.nickname || '读者' }}</text>
            <text>{{ formatDate(item.createTime) }}</text>
          </view>
          <view class="detail__comment-content">{{ item.content }}</view>
        </view>
        <s-loading
          v-if="commentLoading && comments.length > 0"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载评论..."
        />
      </view>
    </view>

    <view v-if="article" class="detail__action-bar">
      <view class="detail__action" :class="{ 'detail__action--active': article.isLike }" @tap="toggleLike">
        赞 {{ article.likeCount || 0 }}
      </view>
      <view class="detail__action" :class="{ 'detail__action--active': article.isCollected }" @tap="toggleCollect">
        收藏 {{ article.collectCount || 0 }}
      </view>
      <view class="detail__action" @tap="openCommentSheet">
        评论 {{ article.commentCount || 0 }}
      </view>
      <button class="detail__share sl-button sl-button--secondary" open-type="share">分享</button>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view v-if="commentSheetVisible" class="detail__sheet-mask" @tap="closeCommentSheet">
      <view class="detail__sheet" @tap.stop>
        <view class="detail__sheet-title">写评论</view>
        <textarea
          v-model="commentContent"
          class="detail__textarea"
          placeholder="说点什么..."
          maxlength="300"
          auto-height
        />
        <view class="detail__sheet-actions">
          <button class="detail__sheet-cancel sl-button sl-button--secondary" @tap="closeCommentSheet">取消</button>
          <button class="detail__sheet-submit sl-button sl-button--primary" :disabled="commentSubmitting" @tap="submitComment">
            <s-inline-loading v-if="commentSubmitting" text="发布中" light />
            <text v-else>发布</text>
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { onHide, onLoad, onPageScroll, onShareAppMessage, onUnload } from '@dcloudio/uni-app';
import { fetchArticleDetail, reportArticleView } from '@/modules/article/api/article.api';
import type { ArticleDetail } from '@/modules/article/types/article';
import {
  getArticleReadingProgress,
  saveArticleReadingProgress,
  type ArticleReadingProgress
} from '@/modules/article/utils/reading-progress';
import { fetchComments, createComment } from '@/modules/comment/api/comment.api';
import type { CommentItem } from '@/modules/comment/types/comment';
import { collectTarget, followUser, likeTarget, uncollectTarget, unfollowById, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import { useUserStore } from '@/stores/user';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';

const userStore = useUserStore();
const backToTopVisible = ref(false);
const articleId = ref(0);
const article = ref<ArticleDetail | null>(null);
const loading = ref(false);
const comments = ref<CommentItem[]>([]);
const commentLoading = ref(false);
const commentSheetVisible = ref(false);
const commentContent = ref('');
const commentSubmitting = ref(false);
const currentScrollTop = ref(0);
const enterTimestamp = ref(0);

const coverUrl = computed(() => normalizeAssetUrl(article.value?.avatar));

function formatDate(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}

async function loadDetail(id: number): Promise<void> {
  articleId.value = id;
  loading.value = true;
  try {
    article.value = await fetchArticleDetail(id);
    reportArticleView(id);
    loadComments(id);
    await tryRestoreReadingProgress(id);
  } finally {
    loading.value = false;
  }
}

async function loadComments(id: number): Promise<void> {
  commentLoading.value = true;
  try {
    const result = await fetchComments(id, 'article', 1, 20);
    comments.value = result.items || [];
  } finally {
    commentLoading.value = false;
  }
}

function requireLogin(type: string, payload: unknown): boolean {
  if (userStore.isLoggedIn) return true;
  userStore.pushPendingAction({ type, payload });
  uni.navigateTo({ url: '/pages-user/login/login' });
  return false;
}

async function toggleLike(): Promise<void> {
  if (!article.value) return;
  if (!requireLogin('interaction:like', { targetType: 'article', targetId: article.value.id })) return;
  const next = !article.value.isLike;
  article.value.isLike = next;
  article.value.likeCount = Math.max(0, (article.value.likeCount || 0) + (next ? 1 : -1));
  try {
    if (next) await likeTarget('article', article.value.id);
    else await unlikeTarget('article', article.value.id);
  } catch {
    article.value.isLike = !next;
    article.value.likeCount = Math.max(0, (article.value.likeCount || 0) + (next ? -1 : 1));
  }
}

async function toggleCollect(): Promise<void> {
  if (!article.value) return;
  if (!requireLogin('interaction:collect', { targetType: 'article', targetId: article.value.id })) return;
  const next = !article.value.isCollected;
  article.value.isCollected = next;
  article.value.collectCount = Math.max(0, (article.value.collectCount || 0) + (next ? 1 : -1));
  try {
    if (next) await collectTarget('article', article.value.id);
    else await uncollectTarget('article', article.value.id);
  } catch {
    article.value.isCollected = !next;
    article.value.collectCount = Math.max(0, (article.value.collectCount || 0) + (next ? -1 : 1));
  }
}

async function toggleFollow(): Promise<void> {
  if (!article.value?.userId) return;
  if (!requireLogin('user:follow', { targetUserId: article.value.userId })) return;
  try {
    if (article.value.isFollowed && article.value.followId) {
      await unfollowById(article.value.followId);
      article.value.isFollowed = false;
      article.value.followId = undefined;
    } else {
      const result = await followUser(article.value.userId);
      article.value.isFollowed = result.followed;
      article.value.followId = result.id;
    }
  } catch {
    // 请求层已经提示错误。
  }
}

function openCommentSheet(): void {
  commentSheetVisible.value = true;
}

function closeCommentSheet(): void {
  commentSheetVisible.value = false;
}

async function submitComment(): Promise<void> {
  const content = commentContent.value.trim();
  if (!article.value || !content) {
    showInfoToast('请输入评论内容');
    return;
  }
  if (!requireLogin('comment:create', { targetId: article.value.id, source: 'article', content })) return;
  commentSubmitting.value = true;
  try {
    await createComment({ articleId: article.value.id, source: 'article', content });
    commentContent.value = '';
    commentSheetVisible.value = false;
    article.value.commentCount = (article.value.commentCount || 0) + 1;
    await loadComments(article.value.id);
    showInfoToast('评论已提交');
  } finally {
    commentSubmitting.value = false;
  }
}

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function openAuthorHome(): void {
  if (!article.value?.userId) return;
  uni.navigateTo({ url: `/pages-user/home/home?id=${article.value.userId}` });
}

function hasCurrentCommentAction(detail?: LoginSuccessEventDetail): boolean {
  if (!detail?.actions?.length || !articleId.value) return false;
  return detail.actions.some((action) => {
    if (action.type !== 'comment:create') return false;
    const payload = action.payload as { targetId?: number; source?: string } | undefined;
    return payload?.targetId === articleId.value && payload?.source === 'article';
  });
}

function handleLoginSuccess(detail?: LoginSuccessEventDetail): void {
  if (!articleId.value) return;
  if (hasCurrentCommentAction(detail)) {
    commentContent.value = '';
    commentSheetVisible.value = false;
  }
  loadDetail(articleId.value);
}

function buildReadingProgress(): ArticleReadingProgress | null {
  if (!articleId.value) return null;
  return {
    articleId: articleId.value,
    scrollTop: currentScrollTop.value,
    readingMs: Math.max(0, Date.now() - enterTimestamp.value),
    updatedAt: Date.now()
  };
}

function persistReadingProgress(): void {
  const progress = buildReadingProgress();
  if (!progress) return;
  saveArticleReadingProgress(progress);
}

async function tryRestoreReadingProgress(id: number): Promise<void> {
  const progress = getArticleReadingProgress(id);
  if (!progress || progress.scrollTop < 160) return;
  await nextTick();
  setTimeout(() => {
    uni.pageScrollTo({
      scrollTop: progress.scrollTop,
      duration: 280
    });
  }, 120);
}

onLoad((options) => {
  const id = Number(options?.id);
  if (!Number.isFinite(id) || id <= 0) {
    showInfoToast('文章参数无效');
    return;
  }
  enterTimestamp.value = Date.now();
  uni.$on(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
  loadDetail(id);
});

onHide(() => {
  persistReadingProgress();
});

onUnload(() => {
  persistReadingProgress();
  uni.$off(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
});

onPageScroll((event) => {
  currentScrollTop.value = event.scrollTop;
  backToTopVisible.value = event.scrollTop > 360;
});

onShareAppMessage(() => ({
  title: article.value?.title || 'Sourcelin Blog',
  path: article.value?.id ? `/pages-article/detail/detail?id=${article.value.id}` : '/pages/home/home',
  imageUrl: coverUrl.value
}));
</script>

<style lang="scss" scoped>
.detail {
  min-height: 100vh;
  background:
    radial-gradient(circle at -20% 12%, var(--sl-glow-a), rgba(255, 255, 255, 0) 38%),
    radial-gradient(circle at 120% 32%, var(--sl-glow-b), rgba(255, 255, 255, 0) 38%),
    $color-bg;
  padding-bottom: calc(210rpx + env(safe-area-inset-bottom));

  .s-card {
    position: relative;
    overflow: hidden;
    border-radius: 38rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.72) 0%, rgba(255, 255, 255, 0.32) 100%),
      rgba(255, 255, 255, 0.42);
    border: 1rpx solid rgba(255, 255, 255, 0.76);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 12rpx 34rpx rgba(17, 24, 39, 0.03);
    padding: 34rpx 30rpx;
  }

  &__content {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__loading {
    color: $color-text-secondary;
    text-align: center;
  }

  &__cover {
    width: 100%;
    height: 380rpx;
    border-radius: 36rpx;
    display: block;
    box-shadow: 0 12rpx 30rpx rgba(17, 24, 39, 0.04);
  }

  &__category {
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 700;
    margin-bottom: $space-xs;
    text-transform: uppercase;
    letter-spacing: 0.06em;
  }

  &__title {
    font-size: 42rpx;
    font-weight: 800;
    line-height: 1.38;
    margin-bottom: $space-md;
    color: var(--sl-text-main);
  }

  &__meta,
  &__comment-head {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: $space-sm;
    color: $color-text-tertiary;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__summary,
  &__body {
    color: $color-text-secondary;
    font-size: 29rpx;
    line-height: 1.8;
  }

  &__body {
    color: var(--sl-text-main);
  }

  &__author {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__author-main {
    flex: 1;
    min-width: 0;
    padding: 10rpx 0;
  }

  &__author-main:active {
    transform: scale(0.985);
  }

  &__author-name {
    font-size: 30rpx;
    font-weight: 800;
    color: var(--sl-text-main);
  }

  &__author-desc,
  &__empty-content {
    margin-top: 6rpx;
    color: $color-text-tertiary;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__follow {
    min-width: 132rpx;
    height: 60rpx;
    line-height: 60rpx;
    margin: 0;
    padding: 0 20rpx;
    border-radius: 999rpx;
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    font-size: 24rpx;
    font-weight: 700;
    box-shadow: 0 6rpx 16rpx rgba(59, 89, 255, 0.2);
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.2s ease;

    &::after {
      border: none;
    }

    &:active {
      transform: scale(0.95);
    }
  }

  &__unlock-button {
    min-width: 220rpx;
    height: 80rpx;
    line-height: 80rpx;
    margin-top: 8rpx;
    padding: 0 28rpx;
    border-radius: 999rpx;
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    font-size: 26rpx;
    font-weight: 700;
    box-shadow: 0 12rpx 30rpx rgba(59, 89, 255, 0.18);

    &::after {
      border: none;
    }
  }

  &__unlock {
    border: 1rpx solid rgba(31, 111, 235, 0.18);
  }

  &__section-title {
    color: var(--sl-text-main);
    font-size: 30rpx;
    font-weight: 800;
  }

  &__section-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $space-sm;
  }

  &__comment-action {
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 700;
  }

  &__comment {
    padding: $space-sm 0;
    @include hairline-bottom;

    &:last-child::after {
      display: none;
    }
  }

  &__comment-content {
    margin-top: $space-xs;
    color: var(--sl-text-main);
    font-size: 27rpx;
    line-height: 1.7;
  }

  &__action-bar {
    position: fixed;
    left: 30rpx;
    right: 30rpx;
    bottom: calc(30rpx + env(safe-area-inset-bottom));
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 16rpx 20rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.82) 0%, rgba(255, 255, 255, 0.44) 100%),
      rgba(255, 255, 255, 0.38);
    border: 1rpx solid rgba(255, 255, 255, 0.86);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 20rpx 48rpx rgba(17, 24, 39, 0.08);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(20rpx) saturate(1.3);
    -webkit-backdrop-filter: blur(20rpx) saturate(1.3);
    /* #endif */
  }

  &__action,
  &__share {
    flex: 1;
    min-height: 76rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    color: var(--sl-text-sub);
    font-size: 24rpx;
    font-weight: 700;
    background: rgba(255, 255, 255, 0.6);
    border: 1rpx solid rgba(255, 255, 255, 0.8);
    transition: all 0.2s ease;
  }

  &__action--active {
    color: var(--sl-color-primary);
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0.54)),
      rgba(59, 89, 255, 0.12);
    border-color: rgba(59, 89, 255, 0.28);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
      0 8rpx 20rpx rgba(59, 89, 255, 0.08);
  }

  &__action:active,
  &__share:active {
    transform: scale(0.96);
  }

  &__share {
    margin: 0;
    padding: 0;
    border: 1rpx solid rgba(255, 255, 255, 0.8);
    line-height: 1;
    font-weight: 700;
  }

  &__sheet-mask {
    position: fixed;
    inset: 0;
    z-index: 120;
    background: rgba(17, 24, 39, 0.54);
    display: flex;
    align-items: flex-end;
  }

  &__sheet {
    width: 100%;
    padding: $space-lg $space-md calc(#{$space-lg} + env(safe-area-inset-bottom));
    border-radius: 36rpx 36rpx 0 0;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
  }

  &__sheet-title {
    font-size: 32rpx;
    font-weight: 800;
    color: var(--sl-text-main);
    margin-bottom: $space-md;
  }

  &__textarea {
    width: 100%;
    min-height: 180rpx;
    padding: $space-md;
    box-sizing: border-box;
    border-radius: $radius-lg;
    background: rgba(245, 247, 250, 0.92);
    border: 1rpx solid rgba(229, 231, 235, 0.88);
    font-size: 28rpx;
  }

  &__sheet-actions {
    display: flex;
    gap: $space-sm;
    margin-top: $space-md;
  }

  &__sheet-cancel,
  &__sheet-submit {
    flex: 1;
    min-height: 84rpx;
    line-height: 84rpx;
    border-radius: 999rpx;
    font-size: 28rpx;
  }

  &__sheet-cancel:active,
  &__sheet-submit:active {
    transform: scale(0.98);
  }

  &__sheet-cancel {
    color: var(--sl-text-sub);
    background: rgba(255, 255, 255, 0.72);
    border: 1rpx solid rgba(229, 231, 235, 0.9);
  }

  &__sheet-submit {
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
  }
}
</style>
