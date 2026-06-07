<template>
  <page-meta :page-style="'overflow: ' + (commentSheetVisible ? 'hidden' : 'visible')" />
  <view class="detail s-container" :class="[themeStore.themeClass]">
    <s-loading :visible="loading" text="正在同步灵感..." />

    <s-empty
      v-if="!loading && !article"
      title="文章不存在或已下架"
      text="该内容可能已被删除、隐藏，或暂时无法访问。"
    />

    <view v-else-if="article" class="detail__content">
      <image v-if="coverUrl" class="detail__cover" :src="coverUrl" mode="aspectFill" />

      <view class="detail__header s-card">
        <view class="detail__header-top">
          <view class="detail__category">{{ article.categoryName || '文章' }}</view>
          <view class="detail__header-actions">
            <view class="detail__header-action" @tap="themeSheetVisible = true">
              外观
            </view>
            <view class="detail__header-action" @tap="reportArticleAction">举报</view>
          </view>
        </view>
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
        <button v-if="showFollowAction" class="detail__follow sl-button sl-button--primary sl-button--sm" size="mini" @tap.stop="toggleFollow">
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

      <view v-if="showCommentsSection" class="detail__comments s-card">
        <view class="detail__section-head">
          <text class="detail__section-title">评论</text>
          <text v-if="canUseComments" class="detail__comment-action" @tap="handleWriteCommentTap">写评论</text>
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
          :title="canUseComments ? '还没有评论' : '暂无公开评论'"
          :text="canUseComments ? '来写第一条留言，开启这篇文章的讨论。' : '当前版本未开放评论发布，欢迎先收藏回访。'"
        />
        <view v-for="item in commentTree" :key="item.id" class="detail__comment" @tap="handleCommentTap(item)">
          <view class="detail__comment-head">
            <text class="detail__comment-nickname">{{ item.userNickname || item.nickname || '读者' }}</text>
            <view class="detail__comment-meta">
              <text class="detail__comment-time">{{ formatDate(item.createTime) }}</text>
              <text v-if="canUseComments" class="detail__comment-reply-btn" @tap.stop="handleCommentTap(item)">回复</text>
              <text 
                v-if="userStore.isLoggedIn && userStore.userInfo && Number(userStore.userInfo.id) === item.userId" 
                class="detail__comment-delete-btn" 
                @tap.stop="handleDeleteComment(item.id)"
              >
                删除
              </text>
            </view>
          </view>
          <view class="detail__comment-content">{{ item.content }}</view>

          <!-- 二级回复区域 -->
          <view v-if="item.replies.length > 0" class="detail__reply-box" @tap.stop>
            <view 
              v-for="reply in (item.collapsed ? item.replies.slice(0, 2) : item.replies)" 
              :key="reply.id" 
              class="detail__reply-item" 
              @tap="handleCommentTap(reply)"
            >
              <view class="detail__reply-head">
                <view class="detail__reply-users">
                  <text class="detail__reply-author">{{ reply.userNickname || reply.nickname || '读者' }}</text>
                  <text class="detail__reply-arrow">▶</text>
                  <text class="detail__reply-target">{{ reply.parentNickname || '读者' }}</text>
                </view>
                <view class="detail__reply-meta">
                  <text class="detail__reply-time">{{ formatDate(reply.createTime) }}</text>
                  <text v-if="canUseComments" class="detail__reply-btn" @tap.stop="handleCommentTap(reply)">回复</text>
                  <text 
                    v-if="userStore.isLoggedIn && userStore.userInfo && Number(userStore.userInfo.id) === reply.userId" 
                    class="detail__reply-delete-btn" 
                    @tap.stop="handleDeleteComment(reply.id)"
                  >
                    删除
                  </text>
                </view>
              </view>
              <view class="detail__reply-content">{{ reply.content }}</view>
            </view>

            <!-- 折叠与展开控制条 -->
            <view v-if="item.collapsed && item.replies.length > 2" class="detail__reply-toggle" @tap.stop="item.collapsed = false">
              <text class="detail__reply-toggle-text">—— 展开 {{ item.replies.length }} 条回复 ∨</text>
            </view>
            <view v-else-if="!item.collapsed && item.replies.length > 2" class="detail__reply-toggle" @tap.stop="item.collapsed = true">
              <text class="detail__reply-toggle-text">—— 收起回复 ∧</text>
            </view>
          </view>
        </view>
        <s-loading
          v-if="commentLoading && comments.length > 0"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载评论..."
        />
      </view>

      <view class="detail__bottom-spacer" />
    </view>

    <view v-if="article" class="detail__action-bar">
      <view class="detail__action" :class="{ 'detail__action--active': article.isLike }" @tap="toggleLike">
        赞 {{ article.likeCount || 0 }}
      </view>
      <view class="detail__action" :class="{ 'detail__action--active': article.isCollected }" @tap="toggleCollect">
        收藏 {{ article.collectCount || 0 }}
      </view>
      <view v-if="canUseComments" class="detail__action" @tap="handleWriteCommentTap">
        评论 {{ article.commentCount || 0 }}
      </view>
      <button class="detail__share sl-button sl-button--secondary" open-type="share">分享</button>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <s-theme-sheet :visible="themeSheetVisible" @close="themeSheetVisible = false" />

    <view v-if="commentSheetVisible" class="detail__sheet-mask" @tap="closeCommentSheet">
      <view class="detail__sheet" :class="{ 'detail__sheet--keyboard': keyboardVisible }" :style="commentSheetStyle" @tap.stop>
        <view class="detail__sheet-title">写评论</view>

        <!-- 被回复内容预览气泡 -->
        <view v-if="replyTarget" class="detail__reply-preview">
          <view class="detail__reply-preview-head">
            <text class="detail__reply-preview-label">回复 @{{ replyTarget.userNickname || replyTarget.nickname || '读者' }}</text>
            <view class="detail__reply-preview-close" @tap="cancelReply">✕</view>
          </view>
          <text class="detail__reply-preview-content s-ellipsis">{{ replyTarget.content }}</text>
        </view>

        <textarea
          v-model="commentContent"
          class="detail__textarea"
          :placeholder="commentPlaceholder"
          placeholder-class="textarea-placeholder"
          maxlength="300"
          auto-height
          fixed
          :adjust-position="false"
          :cursor-spacing="keyboardCursorSpacing"
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
import { computed, nextTick, ref, watch } from 'vue';
import { onHide, onLoad, onPageScroll, onShareAppMessage, onShow, onUnload } from '@dcloudio/uni-app';
import { fetchArticleDetail, reportArticleView } from '@/modules/article/api/article.api';
import type { ArticleDetail } from '@/modules/article/types/article';
import {
  getArticleReadingProgress,
  saveArticleReadingProgress,
  type ArticleReadingProgress
} from '../modules/article/utils/reading-progress';
import { fetchComments, createComment, deleteComment } from '@/modules/comment/api/comment.api';
import type { CommentItem, CommentCreatePayload } from '@/modules/comment/types/comment';
import { collectTarget, followUser, likeTarget, uncollectTarget, unfollowById, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import { createContentReport } from '@/modules/report/api/report.api';
import { useKeyboardInset, useBackToTop } from '@/shared/composables/useBackToTop';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { reportAnalyticsEvent, trackArticleInterest } from '@/shared/utils/analytics';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';
import SThemeSheet from '../components/s-theme-sheet/s-theme-sheet.vue';

const { userStore, can } = useMiniAccess();
const themeStore = useThemeStore();
const articleId = ref(0);
const article = ref<ArticleDetail | null>(null);
const loading = ref(false);
const comments = ref<CommentItem[]>([]);
const replyTarget = ref<CommentItem | null>(null);

interface CommentNode extends CommentItem {
  replies: CommentItem[];
  collapsed: boolean;
  visibleCount: number;
}

const commentTree = computed<CommentNode[]>(() => {
  const rootComments: CommentNode[] = [];
  const childComments: CommentItem[] = [];

  // 1. 分流一级和子回复
  comments.value.forEach((item) => {
    if (!item.parentCommentId) {
      rootComments.push({
        ...item,
        replies: [],
        collapsed: true,
        visibleCount: 2
      });
    } else {
      childComments.push(item);
    }
  });

  // 2. 归并子回复
  childComments.forEach((child) => {
    const root = rootComments.find((r) => r.id === child.floorCommentId);
    if (root) {
      root.replies.push(child);
    } else {
      // 容错：如果找不到根，作为一级评论呈现
      rootComments.push({
        ...child,
        replies: [],
        collapsed: true,
        visibleCount: 2
      });
    }
  });

  // 3. 正序排序
  rootComments.forEach((root) => {
    root.replies.sort((a, b) => {
      const aTime = a.createTime ? new Date(a.createTime).getTime() : 0;
      const bTime = b.createTime ? new Date(b.createTime).getTime() : 0;
      return aTime - bTime;
    });
  });

  return rootComments;
});

const commentPlaceholder = computed(() => {
  if (replyTarget.value) {
    const targetNickname = replyTarget.value.userNickname || replyTarget.value.nickname || '读者';
    return `回复 @${targetNickname}:`;
  }
  return '说点什么...';
});

function handleCommentTap(item: CommentItem): void {
  if (!canUseComments.value) return;
  replyTarget.value = item;
  openCommentSheet();
}

function cancelReply(): void {
  replyTarget.value = null;
}

const commentLoading = ref(false);
const commentSheetVisible = ref(false);
const commentContent = ref('');
const commentSubmitting = ref(false);
const currentScrollTop = ref(0);
const { backToTopVisible, handlePageScroll } = useBackToTop();
const enterTimestamp = ref(0);
const themeSheetVisible = ref(false);
const {
  cursorSpacing: keyboardCursorSpacing,
  keyboardVisible,
  sheetStyle: commentSheetStyle,
  startKeyboardWatch,
  stopKeyboardWatch
} = useKeyboardInset();

const coverUrl = computed(() => normalizeAssetUrl(article.value?.avatar));
const canUseComments = computed(() => can('commentEnabled'));
const canOpenUserHome = computed(() => can('userHomeEnabled'));
const showFollowAction = computed(() => can('followEnabled'));
const showCommentsSection = computed(() => canUseComments.value || comments.value.length > 0);

watch(article, (currentArticle) => {
  applyH5Seo({
    title: buildSeoTitle(currentArticle?.title || '文章详情'),
    description: extractSeoSummary(
      currentArticle?.summary,
      currentArticle?.content,
      currentArticle?.unlockHint,
      '浏览文章详情、评论互动与作者信息。'
    ),
    keywords: [
      currentArticle?.categoryName || '文章',
      currentArticle?.author || currentArticle?.user?.nickname || 'Sourcelin',
      currentArticle?.title || '文章详情',
      '博客',
      '阅读'
    ]
  });
}, { immediate: true });

function formatDate(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}

async function loadDetail(id: number): Promise<void> {
  articleId.value = id;
  loading.value = true;
  try {
    article.value = await fetchArticleDetail(id);
    reportArticleView(id);
    trackArticleInterest(article.value);
    void reportAnalyticsEvent({
      eventType: 'article_view',
      pagePath: '/pages-article/detail/detail',
      targetType: 'article',
      targetId: id,
      metadata: {
        categoryId: article.value.categoryId,
        readAuth: article.value.readAuth
      }
    });
    loadComments(id);
    await tryRestoreReadingProgress(id);
  } finally {
    loading.value = false;
  }
}

async function loadComments(id: number): Promise<void> {
  commentLoading.value = true;
  try {
    const result = await fetchComments(id, 'article', 1, 100);
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
  if (!showFollowAction.value) return;
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

function handleWriteCommentTap(): void {
  if (!canUseComments.value) return;
  replyTarget.value = null;
  openCommentSheet();
}

function openCommentSheet(): void {
  if (!canUseComments.value) return;
  commentSheetVisible.value = true;
}

function closeCommentSheet(): void {
  commentSheetVisible.value = false;
  replyTarget.value = null;
  uni.hideKeyboard();
}

async function submitComment(): Promise<void> {
  if (!canUseComments.value) return;
  const content = commentContent.value.trim();
  if (!article.value || !content) {
    showInfoToast('请输入评论内容');
    return;
  }
  if (!requireLogin('comment:create', { targetId: article.value.id, source: 'article', content })) return;
  commentSubmitting.value = true;
  try {
    const payload: CommentCreatePayload = {
      articleId: article.value.id,
      source: 'article',
      content
    };
    if (replyTarget.value) {
      payload.parentCommentId = replyTarget.value.id;
      payload.parentUserId = replyTarget.value.userId;
      payload.floorCommentId = replyTarget.value.floorCommentId || replyTarget.value.id;
    }
    await createComment(payload);
    commentContent.value = '';
    commentSheetVisible.value = false;
    replyTarget.value = null;
    article.value.commentCount = (article.value.commentCount || 0) + 1;
    await loadComments(article.value.id);
    void reportAnalyticsEvent({
      eventType: 'article_comment_submit',
      pagePath: '/pages-article/detail/detail',
      targetType: 'article',
      targetId: article.value.id
    });
    showInfoToast('评论已提交');
  } finally {
    commentSubmitting.value = false;
  }
}

async function handleDeleteComment(id: number): Promise<void> {
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '提示',
      content: '确认删除该评论吗？',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;
  try {
    await deleteComment(id);
    showInfoToast('评论已删除');
    if (article.value) {
      article.value.commentCount = Math.max(0, (article.value.commentCount || 0) - 1);
      await loadComments(article.value.id);
    }
  } catch {
    // 接口层已报错提示
  }
}



async function reportArticleAction(): Promise<void> {
  if (!article.value?.id) return;
  const reasons = ['广告营销', '不当内容', '侵权抄袭', '其他'];
  const selected = await new Promise<number>((resolve) => {
    uni.showActionSheet({
      itemList: reasons,
      success: (res) => resolve(res.tapIndex),
      fail: () => resolve(-1)
    });
  });
  if (selected < 0) return;
  await createContentReport({
    targetType: 'article',
    targetId: article.value.id,
    reason: reasons[selected],
    pagePath: '/pages-article/detail/detail'
  });
  void reportAnalyticsEvent({
    eventType: 'article_report_submit',
    pagePath: '/pages-article/detail/detail',
    targetType: 'article',
    targetId: article.value.id,
    metadata: { reason: reasons[selected] }
  });
  showInfoToast('举报已提交，我们会尽快核查');
}

function goLogin(): void {
  uni.navigateTo({ url: '/pages-user/login/login' });
}

function openAuthorHome(): void {
  if (!canOpenUserHome.value) return;
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

onShow(() => {
  themeStore.syncNativeArea();
});

onHide(() => {
  stopKeyboardWatch();
  persistReadingProgress();
});

onUnload(() => {
  stopKeyboardWatch();
  persistReadingProgress();
  uni.$off(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
});

watch(commentSheetVisible, (visible) => {
  if (visible) {
    startKeyboardWatch();
    return;
  }
  stopKeyboardWatch();
});

onPageScroll((event) => {
  handlePageScroll(event);
  currentScrollTop.value = event.scrollTop;
});

onShareAppMessage(() => {
  void reportAnalyticsEvent({
    eventType: 'article_share',
    pagePath: '/pages-article/detail/detail',
    targetType: 'article',
    targetId: article.value?.id,
    metadata: {
      title: article.value?.title
    }
  });
  return {
    title: article.value?.title || '圆圈博客',
    path: article.value?.id ? `/pages-article/detail/detail?id=${article.value.id}` : '/pages/home/home',
    imageUrl: coverUrl.value
  };
});
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

  &__header-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16rpx;
  }

  &__header-actions {
    display: inline-flex;
    gap: 10rpx;
    flex-shrink: 0;
  }

  &__header-action {
    min-height: 46rpx;
    padding: 0 16rpx;
    display: inline-flex;
    align-items: center;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    color: $color-text-secondary;
    font-size: 22rpx;
    font-weight: 700;
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

  &__comment-meta {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__comment-reply-btn {
    color: var(--sl-color-primary, $color-primary);
    font-weight: 700;
    cursor: pointer;
    &:active {
      opacity: 0.7;
    }
  }

  &__comment-delete-btn {
    color: $color-danger;
    font-weight: 700;
    cursor: pointer;
    &:active {
      opacity: 0.7;
    }
  }

  &__reply-box {
    margin-top: $space-sm;
    margin-left: 20rpx;
    padding: 16rpx 20rpx;
    border-radius: $radius-md;
    background: rgba(0, 0, 0, 0.024);
    border: 1rpx solid rgba(17, 24, 39, 0.03);
    
    .sl-theme--dark & {
      background: rgba(255, 255, 255, 0.025);
      border-color: rgba(255, 255, 255, 0.04);
    }
  }

  &__reply-item {
    padding: 10rpx 0;
    border-bottom: 1rpx dashed rgba(17, 24, 39, 0.04);
    &:last-of-type {
      border-bottom: none;
    }
    .sl-theme--dark & {
      border-bottom-color: rgba(255, 255, 255, 0.04);
    }
  }

  &__reply-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-xs;
  }

  &__reply-users {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 24rpx;
  }

  &__reply-author {
    color: var(--sl-text-main, #111827);
    font-weight: 700;
    .sl-theme--dark & {
      color: rgba(241, 245, 249, 0.96);
    }
  }

  &__reply-arrow {
    font-size: 18rpx;
    color: var(--sl-text-sub, rgba(75, 85, 99, 0.5));
    margin: 0 4rpx;
  }

  &__reply-target {
    color: var(--sl-color-primary, $color-primary);
    font-weight: 600;
  }

  &__reply-meta {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__reply-time {
    color: var(--sl-text-sub, rgba(75, 85, 99, 0.5));
    font-size: 20rpx;
  }

  &__reply-btn {
    color: var(--sl-color-primary, $color-primary);
    font-size: 20rpx;
    font-weight: 600;
    cursor: pointer;
    &:active {
      opacity: 0.7;
    }
  }

  &__reply-delete-btn {
    color: $color-danger;
    font-size: 20rpx;
    font-weight: 600;
    cursor: pointer;
    &:active {
      opacity: 0.7;
    }
  }

  &__reply-content {
    margin-top: 6rpx;
    color: var(--sl-text-main, #111827);
    font-size: 24rpx;
    line-height: 1.6;
    .sl-theme--dark & {
      color: rgba(226, 232, 240, 0.82);
    }
  }

  &__reply-toggle {
    display: flex;
    align-items: center;
    padding-top: 10rpx;
    cursor: pointer;
  }

  &__reply-toggle-text {
    font-size: 22rpx;
    color: var(--sl-color-primary, $color-primary);
    font-weight: 700;
    opacity: 0.95;
    &:active {
      opacity: 0.7;
    }
  }

  &__reply-preview {
    margin-bottom: $space-md;
    padding: 16rpx 20rpx;
    border-radius: $radius-md;
    background: rgba(59, 89, 255, 0.06);
    border: 1rpx solid rgba(59, 89, 255, 0.12);
    
    .sl-theme--dark & {
      background: rgba(105, 129, 255, 0.1);
      border-color: rgba(105, 129, 255, 0.16);
    }
  }

  &__reply-preview-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8rpx;
  }

  &__reply-preview-label {
    font-size: 24rpx;
    font-weight: 700;
    color: var(--sl-color-primary, $color-primary);
  }

  &__reply-preview-close {
    font-size: 26rpx;
    color: var(--sl-text-sub);
    padding: 4rpx 12rpx;
    cursor: pointer;
    &:active {
      transform: scale(0.9);
    }
  }

  &__reply-preview-content {
    font-size: 24rpx;
    color: var(--sl-text-main);
    line-height: 1.4;
    display: block;
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

  &__bottom-spacer {
    height: 24rpx;
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
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    transition: transform 0.2s ease, background-color 0.2s ease, color 0.2s ease;
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
    position: relative;
    width: 100%;
    padding: $space-lg $space-md $space-lg;
    border-radius: 36rpx 36rpx 0 0;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
    display: flex;
    flex-direction: column;
  }

  &__sheet--keyboard {
    padding-bottom: $space-lg;
  }

  &__sheet-title {
    font-size: 32rpx;
    font-weight: 800;
    color: var(--sl-text-main);
    margin-bottom: $space-md;
  }

  &__textarea {
    @include sl-input;
    min-height: 180rpx;
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
    background: var(--sl-control-bg);
    border: 1rpx solid rgba(229, 231, 235, 0.9);
  }

  &__sheet-submit {
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
  }
}

.sl-theme--dark {
  background:
    radial-gradient(circle at -20% 12%, rgba(89, 115, 255, 0.09), rgba(12, 18, 31, 0) 40%),
    radial-gradient(circle at 120% 32%, rgba(143, 112, 255, 0.08), rgba(12, 18, 31, 0) 42%),
    #080d18;

  .s-card {
    background:
      linear-gradient(145deg, rgba(18, 27, 46, 0.72) 0%, rgba(18, 27, 46, 0.36) 100%),
      rgba(18, 27, 46, 0.62);
    border-color: rgba(154, 176, 255, 0.12);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
      inset 0 18rpx 30rpx rgba(255, 255, 255, 0.02),
      inset 0 -1rpx 0 rgba(105, 129, 255, 0.04),
      0 10rpx 24rpx rgba(0, 0, 0, 0.12),
      0 22rpx 54rpx rgba(0, 0, 0, 0.18);
  }

  .detail__title,
  .detail__author-name,
  .detail__section-title,
  .detail__body {
    color: rgba(241, 245, 249, 0.96);
  }

  .detail__summary,
  .detail__author-desc,
  .detail__comment-content {
    color: rgba(226, 232, 240, 0.82);
  }

  .detail__meta,
  .detail__comment-head,
  .detail__header-action,
  .detail__empty-content {
    color: rgba(148, 163, 184, 0.88);
  }

  .detail__header-action {
    background:
      linear-gradient(145deg, rgba(18, 27, 46, 0.66), rgba(18, 27, 46, 0.4)),
      rgba(18, 27, 46, 0.44);
    border-color: rgba(154, 176, 255, 0.12);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
      0 8rpx 18rpx rgba(0, 0, 0, 0.1);
  }

  .detail__sheet {
    background:
      linear-gradient(180deg, var(--sl-surface-bg), var(--sl-page-bg));
    border-top: 1rpx solid var(--sl-border-light);
    box-shadow:
      inset 0 2rpx 0 rgba(255, 255, 255, 0.08),
      0 -18rpx 48rpx rgba(0, 0, 0, 0.34);
  }

  .detail__textarea:focus {
    border-color: var(--sl-border-focused);
    box-shadow: 0 0 12rpx rgba(112, 152, 218, 0.25);
  }

  .detail__textarea-placeholder {
    color: var(--sl-text-muted) !important;
  }

  .detail__sheet-cancel {
    color: var(--sl-text-sub);
    background: var(--sl-control-bg);
    border-color: var(--sl-border-light);
  }

  .detail__action-bar {
    background:
      linear-gradient(145deg, rgba(18, 27, 46, 0.78) 0%, rgba(18, 27, 46, 0.42) 100%),
      rgba(18, 27, 46, 0.68);
    border-color: rgba(154, 176, 255, 0.14);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
      inset 0 18rpx 28rpx rgba(255, 255, 255, 0.02),
      0 18rpx 42rpx rgba(0, 0, 0, 0.22);
  }

  .detail__action,
  .detail__share {
    background:
      linear-gradient(145deg, rgba(18, 27, 46, 0.66), rgba(18, 27, 46, 0.42)),
      rgba(18, 27, 46, 0.5);
    border-color: rgba(154, 176, 255, 0.12);
    color: rgba(226, 232, 240, 0.82);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.06),
      0 8rpx 20rpx rgba(0, 0, 0, 0.12);
  }

  .detail__action--active {
    color: rgba(196, 208, 255, 0.98);
    background:
      linear-gradient(145deg, rgba(59, 89, 255, 0.32), rgba(143, 112, 255, 0.26)),
      rgba(59, 89, 255, 0.18);
    border-color: rgba(126, 146, 255, 0.28);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
      0 8rpx 20rpx rgba(59, 89, 255, 0.12);
  }
}
</style>
