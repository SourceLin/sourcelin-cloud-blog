<template>
  <view class="community" :class="themeStore.themeClass">
    <s-loading :visible="loading && currentItems.length === 0" />

    <!-- 固定 Tab 切换器 -->
    <view class="community__tabbar">
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
      <view
        class="community__fab"
        :aria-label="activeTab === 'says' ? '发说说' : '写树洞'"
        @tap="goPublish"
      >
        <uni-icons type="plusempty" size="22" color="currentColor" />
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
                  @error="onImageError(resolveAvatar(item))"
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
            <view class="community__more" @tap="openItemAction(item)">···</view>
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
              <image class="community__media-img" :src="image" mode="aspectFill" lazy-load @error="onImageError(image)" />
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
      <view class="community__sheet" :class="{ 'community__sheet--keyboard': keyboardVisible }" :style="commentSheetStyle" @tap.stop>
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
          <view v-for="item in commentTree" :key="item.id" class="community__sheet-comment" @tap="handleCommentTap(item)">
            <view class="community__sheet-comment-head">
              <text class="community__comment-nickname">{{ item.userNickname || item.nickname || '读者' }}</text>
              <view class="community__comment-meta">
                <text class="community__comment-time">{{ formatDate(item.createTime) }}</text>
                <text class="community__comment-reply-btn" @tap.stop="handleCommentTap(item)">回复</text>
                <text 
                  v-if="userStore.isLoggedIn && currentUserId && currentUserId === item.userId" 
                  class="community__comment-delete-btn" 
                  @tap.stop="handleDeleteComment(item.id)"
                >
                  删除
                </text>
              </view>
            </view>
            <view class="community__sheet-comment-content">{{ item.content }}</view>

            <!-- 二级回复区域 -->
            <view v-if="item.replies.length > 0" class="community__reply-box" @tap.stop>
              <view 
                v-for="reply in (item.collapsed ? item.replies.slice(0, 2) : item.replies)" 
                :key="reply.id" 
                class="community__reply-item" 
                @tap="handleCommentTap(reply)"
              >
                <view class="community__reply-head">
                  <view class="community__reply-users">
                    <text class="community__reply-author">{{ reply.userNickname || reply.nickname || '读者' }}</text>
                    <text class="community__reply-arrow">▶</text>
                    <text class="community__reply-target">{{ reply.parentNickname || '读者' }}</text>
                  </view>
                  <view class="community__reply-meta">
                    <text class="community__reply-time">{{ formatDate(reply.createTime) }}</text>
                    <text class="community__reply-btn" @tap.stop="handleCommentTap(reply)">回复</text>
                    <text 
                      v-if="userStore.isLoggedIn && currentUserId && currentUserId === reply.userId" 
                      class="community__reply-delete-btn" 
                      @tap.stop="handleDeleteComment(reply.id)"
                    >
                      删除
                    </text>
                  </view>
                </view>
                <view class="community__reply-content">{{ reply.content }}</view>
              </view>

              <!-- 折叠与展开控制条 -->
              <view v-if="item.collapsed && item.replies.length > 2" class="community__reply-toggle" @tap.stop="item.collapsed = false">
                <text class="community__reply-toggle-text">—— 展开 {{ item.replies.length }} 条回复 ∨</text>
              </view>
              <view v-else-if="!item.collapsed && item.replies.length > 2" class="community__reply-toggle" @tap.stop="item.collapsed = true">
                <text class="community__reply-toggle-text">—— 收起回复 ∧</text>
              </view>
            </view>
          </view>
          <s-loading
            v-if="commentLoading"
            :visible="true"
            :fullpage="false"
            compact
            text="正在加载评论..."
          />
        </scroll-view>

        <!-- 回复提示横条 -->
        <view v-if="replyTarget" class="community__reply-indicator">
          <text class="community__reply-indicator-text">正在回复 @{{ replyTarget.userNickname || replyTarget.nickname || '读者' }}</text>
          <view class="community__reply-indicator-close" @tap="cancelReply">✕</view>
        </view>

        <textarea
          v-model="commentContent"
          class="community__textarea"
          :placeholder="commentPlaceholder"
          placeholder-class="textarea-placeholder"
          maxlength="300"
          auto-height
          fixed
          :adjust-position="false"
          :cursor-spacing="keyboardCursorSpacing"
        />
        <view class="community__sheet-actions">
          <button class="community__sheet-button community__sheet-button--ghost sl-button sl-button--secondary" @tap="closeCommentSheet">取消</button>
          <button
            class="community__sheet-button community__sheet-button--primary sl-button sl-button--primary"
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
import { computed, ref, watch } from 'vue';
import { onHide, onLoad, onPageScroll, onPullDownRefresh, onReachBottom, onShareAppMessage, onShow, onUnload } from '@dcloudio/uni-app';
import { fetchSayPage, fetchTreeholePage, deleteSay, deleteTreehole } from '@/modules/community/api/community.api';
import type { SayItem, TreeholeItem } from '@/modules/community/types/community';
import { createComment, fetchComments, deleteComment } from '@/modules/comment/api/comment.api';
import type { CommentItem, CommentSource, CommentCreatePayload } from '@/modules/comment/types/comment';
import { collectTarget, likeTarget, uncollectTarget, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import { createContentReport } from '@/modules/report/api/report.api';
import { useKeyboardInset } from '@/shared/composables/useBackToTop';
import { reportAnalyticsEvent } from '@/shared/utils/analytics';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import type { InteractionTargetType } from '@/modules/interaction/types/interaction';
import { env } from '@/config/env';
import { consumeCommunityRefresh } from '@/modules/community/utils/publish';
import { liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
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
const themeStore = useThemeStore();
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
  commentList.value.forEach((item) => {
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

  // 2. 归并子回复到对应的 floorCommentId 楼层根评论下
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

  // 3. 正序排序每栋楼子回复
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
  return '写下你的看法...';
});

function handleCommentTap(item: CommentItem): void {
  replyTarget.value = item;
}

function cancelReply(): void {
  replyTarget.value = null;
}

const activeCommentId = ref<number | null>(null);
const activeCommentSource = ref<CommentSource>('say');
const brokenUrls = ref<Set<string>>(new Set());
const activeCommentLabel = ref('');
const backToTopVisible = ref(false);
const activeTabPath = 'pages/community/community';
const {
  cursorSpacing: keyboardCursorSpacing,
  keyboardVisible,
  sheetStyle: commentSheetStyle,
  startKeyboardWatch,
  stopKeyboardWatch
} = useKeyboardInset();

const currentUserId = computed(() => {
  const id = userStore.userInfo?.id;
  return id != null ? Number(id) : undefined;
});

const currentItems = computed<CommunityItem[]>(() =>
  activeTab.value === 'says' ? says.value : treeholes.value
);
const targetType = computed<InteractionTargetType>(() =>
  activeTab.value === 'says' ? 'say' : 'treehole'
);
const finished = computed(() => pageMap.value[activeTab.value] >= totalPageMap.value[activeTab.value]);
const isEmpty = computed(() => !loading.value && currentItems.value.length === 0);
const emptyText = computed(() => activeTab.value === 'says' ? '暂无说说' : '暂无树洞');

watch([activeTab, currentItems], () => {
  const isTreehole = activeTab.value === 'treeholes';
  const firstItem = currentItems.value[0];
  applyH5Seo({
    title: buildSeoTitle(isTreehole ? '树洞广场' : '轻社区'),
    description: extractSeoSummary(
      firstItem?.content ? `浏览${isTreehole ? '匿名树洞' : '说说互动'}：${firstItem.content}` : '',
      isTreehole ? '浏览匿名树洞、轻讨论与互动内容。' : '浏览说说动态、轻互动与作者社区内容。'
    ),
    keywords: [isTreehole ? '树洞' : '社区', '说说', '互动', '评论', '轻社区']
  });
}, { immediate: true });

onLoad((options) => {
  if (options?.tab === 'treeholes') {
    activeTab.value = 'treeholes';
  }
});

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
  const urls = ids.length > 0
    ? ids.map((id) => `${env.baseURL}/file/download/${id}`)
    : splitStoredUrls(say.images);
  return urls.filter((url) => !brokenUrls.value.has(url));
}

function onImageError(url: string): void {
  if (!brokenUrls.value.has(url)) {
    brokenUrls.value = new Set([...brokenUrls.value, url]);
  }
}

function resolveGalleryImages(item: CommunityItem): string[] {
  return resolveImages(item).slice(0, 5);
}

function resolveExtraImageCount(item: CommunityItem): number {
  return Math.max(0, resolveImages(item).length - 5);
}

function resolveAvatar(item: CommunityItem): string {
  let url: string;
  if (activeTab.value === 'treeholes') {
    url = normalizeAssetUrl((item as TreeholeItem).avatar);
  } else {
    const say = item as SayItem;
    url = normalizeAssetUrl(say.user?.avatar);
  }
  return url && !brokenUrls.value.has(url) ? url : '';
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
  replyTarget.value = null;
  commentSheetVisible.value = true;
  commentLoading.value = true;
  try {
    const result = await fetchComments(item.id, activeCommentSource.value, 1, 100);
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
      const result = await fetchComments(activeCommentId.value, activeCommentSource.value, 1, 100);
      commentList.value = result.items || [];
    } finally {
      commentLoading.value = false;
    }
  }
}

function closeCommentSheet(): void {
  commentSheetVisible.value = false;
  replyTarget.value = null;
  uni.hideKeyboard();
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
    const payload: CommentCreatePayload = {
      articleId: activeCommentId.value,
      source: activeCommentSource.value,
      content
    };
    if (replyTarget.value) {
      payload.parentCommentId = replyTarget.value.id;
      payload.parentUserId = replyTarget.value.userId;
      payload.floorCommentId = replyTarget.value.floorCommentId || replyTarget.value.id;
    }
    await createComment(payload);
    commentContent.value = '';
    replyTarget.value = null;
    updateCommentCount(activeCommentId.value, 1);
    showSuccessToast('发布成功');
    closeCommentSheet();
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
    showSuccessToast('评论已删除');
    if (activeCommentId.value) {
      updateCommentCount(activeCommentId.value, -1);
      const result = await fetchComments(activeCommentId.value, activeCommentSource.value, 1, 100);
      commentList.value = result.items || [];
    }
  } catch {
    // 接口层已报错提示
  }
}

function canDelete(item: CommunityItem): boolean {
  if (!userStore.isLoggedIn) return false;
  const ownerId = activeTab.value === 'says'
    ? ((item as SayItem).user?.id || (item as SayItem).userId)
    : (item as TreeholeItem).userId;
  return ownerId != null && currentUserId.value != null && Number(ownerId) === currentUserId.value;
}

async function openItemAction(item: CommunityItem): Promise<void> {
  if (canDelete(item)) {
    await handleDelete(item);
    return;
  }
  await reportItemAction(item);
}

async function handleDelete(item: CommunityItem): Promise<void> {
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '确认删除',
      content: '删除后无法恢复，确定删除吗？',
      confirmText: '删除',
      confirmColor: '#DC2626',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;
  try {
    if (activeTab.value === 'says') {
      await deleteSay(item.id);
      says.value = says.value.filter((s) => s.id !== item.id);
    } else {
      await deleteTreehole(item.id);
      treeholes.value = treeholes.value.filter((t) => t.id !== item.id);
    }
    showSuccessToast('已删除');
  } catch {
    showInfoToast('删除失败，请稍后重试');
  }
}

async function reportItemAction(item: CommunityItem): Promise<void> {
  const reasons = ['广告营销', '不当内容', '侵权抄袭', '其他'];
  const selected = await new Promise<number>((resolve) => {
    uni.showActionSheet({
      itemList: reasons,
      success: (res) => resolve(res.tapIndex),
      fail: () => resolve(-1)
    });
  });
  if (selected < 0) return;
  const currentTargetType = activeTab.value === 'says' ? 'say' : 'treehole';
  await createContentReport({
    targetType: currentTargetType,
    targetId: item.id,
    reason: reasons[selected],
    pagePath: '/pages/community/community'
  });
  void reportAnalyticsEvent({
    eventType: 'community_report_submit',
    pagePath: '/pages/community/community',
    targetType: currentTargetType,
    targetId: item.id,
    metadata: { reason: reasons[selected] }
  });
  showInfoToast('举报已提交，我们会尽快核查');
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
  themeStore.syncNativeArea();
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

onHide(() => {
  stopKeyboardWatch();
});

onUnload(() => {
  stopKeyboardWatch();
});

watch(commentSheetVisible, (visible) => {
  if (visible) {
    startKeyboardWatch();
    return;
  }
  stopKeyboardWatch();
});

onShareAppMessage(() => {
  const isTreehole = activeTab.value === 'treeholes';
  void reportAnalyticsEvent({
    eventType: 'community_share',
    pagePath: '/pages/community/community',
    targetType: isTreehole ? 'treehole' : 'say',
    metadata: {
      activeTab: activeTab.value,
      itemCount: currentItems.value.length
    }
  });
  return {
    title: isTreehole ? '来树洞看看匿名故事 - 圆圈博客' : '来社区看看最新说说 - 圆圈博客',
    path: `/pages/community/community?tab=${activeTab.value}`
  };
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
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s ease;

  &.sl-theme--dark {
    --community-glass-border: var(--sl-border-light);
    --community-glass-highlight: rgba(255, 255, 255, 0.08);
    --community-shadow: rgba(0, 0, 0, 0.18);
    background: var(--sl-page-bg, #09090b);

    .community__tabbar {
      background: var(--sl-page-bg, #080d18);
      border-bottom-color: var(--sl-border-light);
    }

    .community__tabs {
      background:
        linear-gradient(145deg, rgba(24, 27, 38, 0.92), rgba(18, 20, 28, 0.88));
      border-color: var(--sl-border-light);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.06),
        0 8rpx 20rpx rgba(0, 0, 0, 0.24);
    }

    .community__fab {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.66), rgba(18, 27, 46, 0.4)),
        rgba(105, 129, 255, 0.08);
      border-color: rgba(105, 129, 255, 0.2);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        0 6rpx 16rpx rgba(105, 129, 255, 0.1);
    }

    .community__fab:active {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.74), rgba(18, 27, 46, 0.52)),
        rgba(105, 129, 255, 0.12);
    }

    .community__card {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .community__card:active {
      transform: scale(0.985);
      background: rgba(47, 61, 85, 0.7);
    }

    .community__time,
    .community__more {
      color: var(--sl-text-sub);
    }

    .community__author,
    .community__content {
      color: var(--sl-text-main);
      text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.5);
    }

    .community__sheet {
      background:
        linear-gradient(180deg, var(--sl-surface-bg), var(--sl-page-bg));
      border-top: 1rpx solid var(--sl-border-light);
      box-shadow:
        inset 0 2rpx 0 rgba(255, 255, 255, 0.08),
        0 -18rpx 48rpx rgba(0, 0, 0, 0.34);
    }

    .community__sheet-subtitle,
    .community__sheet-close,
    .community__sheet-comment-head,
    .community__sheet-empty,
    .community__sheet-loading {
      color: var(--sl-text-sub);
    }

    .community__sheet-comment::after {
      background-color: var(--sl-border-light);
    }

    .community__textarea {
      color: var(--sl-text-main);
      background: var(--sl-input-bg);
      border-color: var(--sl-control-border);
      transition: border-color 0.2s ease, box-shadow 0.2s ease;
    }

    .community__textarea:focus {
      border-color: var(--sl-border-focused);
      box-shadow: 0 0 12rpx rgba(112, 152, 218, 0.25);
    }

    .community__textarea-placeholder {
      color: var(--sl-text-muted);
    }

    .community__sheet-button--ghost {
      color: var(--sl-text-sub);
      background: var(--sl-control-bg);
      border-color: var(--sl-border-light);
    }
  }

  /* ─── 固定 Tab 栏 ─── */
  &__tabbar {
    position: sticky;
    top: 0;
    z-index: 80;
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 16rpx 30rpx 12rpx;
    background: var(--sl-page-bg, #f5f7fb);
    border-bottom: 1rpx solid var(--sl-border-glass);
    transition: background-color 0.24s ease, border-color 0.24s ease;
  }

  &__tabs {
    position: relative;
    display: flex;
    flex: 1;
    min-width: 0;
    padding: 8rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0.82)),
      var(--community-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.96),
      0 8rpx 20rpx rgba(17, 24, 39, 0.06);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */
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
    transition: transform 0.25s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.25s ease, color 0.25s ease, box-shadow 0.25s ease;

    &--active {
      color: var(--community-primary);
      background:
        linear-gradient(135deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
        rgba(59, 89, 255, 0.08);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
        0 10rpx 28rpx rgba(59, 89, 255, 0.14);
    }
  }

  &__tab:active {
    transform: scale(0.97);
  }

  &__body {
    position: relative;
    margin-top: 0;
    padding: 20rpx 30rpx;
  }

  &__fab {
    flex-shrink: 0;
    width: 74rpx;
    height: 74rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    color: var(--community-primary);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.18);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 8rpx 20rpx rgba(59, 89, 255, 0.1);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.2s ease, box-shadow 0.2s ease;

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */

    &:active {
      transform: scale(0.92);
      background:
        linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.58)),
        rgba(59, 89, 255, 0.12);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
        0 6rpx 14rpx rgba(59, 89, 255, 0.08);
    }
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
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--community-glass-pure);
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 var(--community-glass-highlight),
      0 18rpx 44rpx var(--community-shadow);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  &__card--treeholes {
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--sl-bg-glass-tint);
    border-color: var(--sl-border-glass);
  }

  &__card::after {
    content: '';
    position: absolute;
    left: 24rpx;
    right: 24rpx;
    top: 12rpx;
    height: 44rpx;
    border-radius: 999rpx;
    background: linear-gradient(180deg, var(--sl-panel-highlight), rgba(255, 255, 255, 0));
    pointer-events: none;
  }

  &__card-head,
  &__actions {
    display: flex;
    gap: $space-sm;
  }

  &__card-head {
    position: relative;
    z-index: 1;
    align-items: flex-start;
    justify-content: space-between;
    padding-right: 0;
  }

  &__identity {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__identity:active {
    transform: scale(0.985);
  }

  &__more {
    min-width: 54rpx;
    height: 54rpx;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: var(--sl-control-bg);
    color: rgba(75, 85, 99, 0.82);
    font-size: 28rpx;
    line-height: 1;
    flex-shrink: 0;
  }

  &__avatar {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      rgba(59, 89, 255, 0.08);
    border: 1rpx solid var(--sl-border-glass);
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
    background: var(--sl-bg-glass-tint);
    border: 1rpx solid var(--sl-border-glass);
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
      linear-gradient(180deg, var(--sl-panel-highlight), var(--sl-panel-lowlight));
    border: 1rpx solid var(--sl-border-glass);
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
    background: var(--sl-control-bg-strong);
    border-left: 1rpx solid var(--sl-border-glass);
    border-top: 1rpx solid var(--sl-border-glass);
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
    background: var(--sl-bg-glass-tint);
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.72),
      0 12rpx 28rpx rgba(17, 24, 39, 0.06);
  }

  &__media-item:active {
    transform: scale(0.985);
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
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    color: var(--community-text-sub);
    font-size: 24rpx;
    font-weight: 700;
    box-shadow: 0 4rpx 10rpx rgba(17, 24, 39, 0.01);
    transition: transform 0.2s ease, background-color 0.2s ease, color 0.2s ease;
  }

  &__action:active {
    transform: scale(0.96);
  }

  &__action--active {
    color: var(--community-primary);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
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
    position: relative;
    width: 100%;
    max-height: 78vh;
    padding: $space-lg $space-md $space-lg;
    border-radius: 36rpx 36rpx 0 0;
    background:
      linear-gradient(180deg, var(--sl-control-bg-strong), var(--sl-page-bg));
    display: flex;
    flex-direction: column;
  }

  &__sheet--keyboard {
    padding-bottom: $space-lg;
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
    height: 42vh;
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

  &__comment-nickname {
    color: var(--sl-text-main, #111827);
    font-weight: 700;
    font-size: 26rpx;
  }
  &__comment-meta {
    display: flex;
    align-items: center;
    gap: $space-sm;
  }
  &__comment-time {
    color: var(--sl-text-sub, rgba(75, 85, 99, 0.68));
    font-size: 22rpx;
  }
  &__comment-reply-btn {
    color: var(--community-primary, #3b59ff);
    font-size: 22rpx;
    font-weight: 600;
    cursor: pointer;
    &:active {
      opacity: 0.7;
    }
  }

  &__comment-delete-btn {
    color: $color-danger;
    font-size: 22rpx;
    font-weight: 600;
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
  }

  &__reply-arrow {
    font-size: 18rpx;
    color: var(--sl-text-sub, rgba(75, 85, 99, 0.5));
    margin: 0 4rpx;
  }

  &__reply-target {
    color: var(--community-primary, #3b59ff);
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
    color: var(--community-primary, #3b59ff);
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
  }

  &__reply-toggle {
    display: flex;
    align-items: center;
    padding-top: 10rpx;
    cursor: pointer;
  }

  &__reply-toggle-text {
    font-size: 22rpx;
    color: var(--community-primary, #3b59ff);
    font-weight: 700;
    opacity: 0.95;
    &:active {
      opacity: 0.7;
    }
  }

  &__reply-indicator {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16rpx;
    padding: 12rpx 20rpx;
    border-radius: $radius-sm;
    background: rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.15);
    
    .sl-theme--dark & {
      background: rgba(105, 129, 255, 0.12);
      border-color: rgba(105, 129, 255, 0.2);
    }
  }

  &__reply-indicator-text {
    font-size: 24rpx;
    color: var(--community-primary, #3b59ff);
    font-weight: 600;
  }

  &__reply-indicator-close {
    font-size: 26rpx;
    color: var(--community-primary, #3b59ff);
    padding: 0 8rpx;
    cursor: pointer;
    &:active {
      transform: scale(0.9);
    }
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
    background: rgba(31, 41, 55, 0.04);
    border: 1rpx solid rgba(17, 24, 39, 0.08);
    font-size: 28rpx;
    color: var(--sl-text-main) !important;
  }

  &__textarea-placeholder {
    color: var(--sl-text-muted) !important;
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
    background: var(--sl-control-bg);
    border: 1rpx solid rgba(229, 231, 235, 0.9);
  }

  &__sheet-button--primary {
    background: linear-gradient(135deg, var(--community-primary), var(--community-primary-soft));
    color: #fff;
    box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
  }
}
</style>
