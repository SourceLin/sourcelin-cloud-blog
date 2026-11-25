import { computed, ref, shallowRef, triggerRef, watch } from 'vue';
import {
  onHide,
  onLoad,
  onPageScroll,
  onPullDownRefresh,
  onReachBottom,
  onShareAppMessage,
  onShow,
  onUnload
} from '@dcloudio/uni-app';
import { fetchSayPage, fetchTreeholePage, deleteSay, deleteTreehole } from '@/modules/community/api/community.api';
import type { SayItem, TreeholeItem } from '@/modules/community/types/community';
import { createComment, fetchComments, deleteComment } from '@/modules/comment/api/comment.api';
import type { CommentItem, CommentSource, CommentCreatePayload } from '@/modules/comment/types/comment';
import { collectTarget, likeTarget, uncollectTarget, unlikeTarget } from '@/modules/interaction/api/interaction.api';
import type { InteractionTargetType } from '@/modules/interaction/types/interaction';
import { createContentReport } from '@/modules/report/api/report.api';
import { useBackToTop, useKeyboardInset } from '@/shared/composables/useBackToTop';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { reportAnalyticsEvent } from '@/shared/utils/analytics';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { env } from '@/config/env';
import { consumeCommunityRefresh } from '@/modules/community/utils/publish';
import { liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { useThemeStore } from '@/stores/theme';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { normalizeAssetUrl } from '@/utils/url';

export type TabKey = 'says' | 'treeholes';
export type CommunityItem = SayItem | TreeholeItem;

export interface Tab {
  key: TabKey;
  text: string;
}

export interface CommunityCardView {
  author: string;
  avatar: string;
  images: string[];
  galleryImages: string[];
  extraImageCount: number;
  timeText: string;
}

export function useCommunityPage() {
  const { userStore, can, guard, resolveLiquidTabs } = useMiniAccess();
  const themeStore = useThemeStore();
  const { backToTopVisible, handlePageScroll } = useBackToTop(360);

  const tabs: Tab[] = [
    { key: 'says', text: '说说' },
    { key: 'treeholes', text: '树洞' }
  ];

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
  const activeCommentId = ref<number | null>(null);
  const activeCommentSource = ref<CommentSource>('say');
  const activeCommentLabel = ref('');

  // 遵守 frontend-uniapp 规范：使用 shallowRef + Set.add() + triggerRef()
  const brokenUrls = shallowRef<Set<string>>(new Set());

  const activeTabPath = 'pages/community/community';
  const {
    cursorSpacing: keyboardCursorSpacing,
    keyboardVisible,
    sheetStyle: commentSheetStyle,
    startKeyboardWatch,
    stopKeyboardWatch
  } = useKeyboardInset();

  const visibleLiquidTabItems = computed(() => resolveLiquidTabs(liquidTabItems));
  const canUseComments = computed(() => can('commentEnabled'));
  const canUseUserHome = computed(() => can('userHomeEnabled'));
  const canUseCommunityTab = computed(() => can('communityEnabled'));
  const canPublishCommunity = computed(() =>
    activeTab.value === 'says' ? can('sayPublishEnabled') : can('treeholeEnabled')
  );
  const showCommunityActions = computed(() => canUseComments.value);

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
  const emptyText = computed(() => (activeTab.value === 'says' ? '暂无说说' : '暂无树洞'));

  function formatDate(value?: string): string {
    return value ? value.slice(0, 10) : '刚刚';
  }

  function splitStoredUrls(value?: string): string[] {
    return value
      ? value
          .split(',')
          .map((url) => normalizeAssetUrl(url.trim()))
          .filter(Boolean)
      : [];
  }

  // 遵守 frontend-uniapp 规范：预计算为 computed Map，禁止模板中对同一 item 重复调用解析函数
  const cardViewMap = computed(() => {
    const broken = brokenUrls.value;
    const isTreehole = activeTab.value === 'treeholes';
    const map = new Map<number, CommunityCardView>();

    for (const item of currentItems.value) {
      let author: string;
      let rawAvatar: string | undefined;
      let images: string[] = [];

      if (isTreehole) {
        const treehole = item as TreeholeItem;
        author = treehole.userNickname || treehole.nickname || '匿名洞友';
        rawAvatar = treehole.avatar;
      } else {
        const say = item as SayItem;
        author = say.userNickname || say.user?.nickname || 'Sourcelin';
        rawAvatar = say.user?.avatar;
        const ids = say.imageFileIds
          ? say.imageFileIds
              .split(',')
              .map((id) => id.trim())
              .filter((id) => /^\d+$/.test(id))
          : [];
        const urls =
          ids.length > 0 ? ids.map((id) => `${env.baseURL}/file/download/${id}`) : splitStoredUrls(say.images);
        images = urls.filter((url) => !broken.has(url));
      }

      const normalizedAvatar = normalizeAssetUrl(rawAvatar);
      const avatar = normalizedAvatar && !broken.has(normalizedAvatar) ? normalizedAvatar : '';

      map.set(item.id, {
        author,
        avatar,
        images,
        galleryImages: images.slice(0, 5),
        extraImageCount: Math.max(0, images.length - 5),
        timeText: formatDate(item.createTime)
      });
    }

    return map;
  });

  function getCardView(item: CommunityItem): CommunityCardView {
    return (
      cardViewMap.value.get(item.id) || {
        author: '读者',
        avatar: '',
        images: [],
        galleryImages: [],
        extraImageCount: 0,
        timeText: '刚刚'
      }
    );
  }

  function onImageError(url: string): void {
    if (url && !brokenUrls.value.has(url)) {
      brokenUrls.value.add(url);
      triggerRef(brokenUrls);
    }
  }

  function previewImages(images: string[], index: number): void {
    if (images.length === 0) return;
    uni.previewImage({
      urls: images,
      current: images[index] || images[0]
    });
  }

  function goPublish(): void {
    if (!canPublishCommunity.value) return;
    const url = activeTab.value === 'says' ? '/pages-publish/say/say' : '/pages-publish/treehole/treehole';
    uni.navigateTo({ url });
  }

  function openUserHome(item: CommunityItem): void {
    if (!canUseUserHome.value) return;
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
      const result =
        activeTab.value === 'says' ? await fetchSayPage(targetPage, 10) : await fetchTreeholePage(targetPage, 10);
      if (activeTab.value === 'says') {
        says.value = targetPage === 1 ? (result.items as SayItem[]) : says.value.concat(result.items as SayItem[]);
      } else {
        treeholes.value =
          targetPage === 1
            ? (result.items as TreeholeItem[])
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
    void fetchPage(pageMap.value[activeTab.value] + 1);
  }

  function switchTab(key: TabKey): void {
    if (!canUseCommunityTab.value) return;
    if (activeTab.value === key) return;
    activeTab.value = key;
    if (currentItems.value.length === 0) void refresh();
  }

  function handleReplyComment(item: CommentItem): void {
    if (!canUseComments.value) return;
    replyTarget.value = item;
  }

  function cancelReply(): void {
    replyTarget.value = null;
  }

  async function openCommentSheet(item: CommunityItem): Promise<void> {
    if (!canUseComments.value) return;
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

  function closeCommentSheet(): void {
    commentSheetVisible.value = false;
    replyTarget.value = null;
    uni.hideKeyboard();
  }

  function updateCommentCount(targetId: number, delta: number): void {
    const target = currentItems.value.find((item) => item.id === targetId);
    if (!target) return;
    target.commentCount = Math.max(0, (target.commentCount || 0) + delta);
  }

  async function submitComment(): Promise<void> {
    if (!canUseComments.value) return;
    const content = commentContent.value.trim();
    if (!activeCommentId.value || !content) {
      showInfoToast('请输入评论内容');
      return;
    }
    if (
      !requireLogin('comment:create', {
        targetId: activeCommentId.value,
        source: activeCommentSource.value,
        content
      })
    ) {
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
    if (!canUseComments.value) return;
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

  async function toggleLike(item: CommunityItem): Promise<void> {
    if (!showCommunityActions.value) return;
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
    if (!showCommunityActions.value) return;
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

  function canDelete(item: CommunityItem): boolean {
    if (!userStore.isLoggedIn) return false;
    const ownerId =
      activeTab.value === 'says'
        ? (item as SayItem).user?.id || (item as SayItem).userId
        : (item as TreeholeItem).userId;
    return ownerId != null && currentUserId.value != null && Number(ownerId) === currentUserId.value;
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

  async function openItemAction(item: CommunityItem): Promise<void> {
    if (!showCommunityActions.value) return;
    if (canDelete(item)) {
      await handleDelete(item);
      return;
    }
    await reportItemAction(item);
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

  watch(
    [activeTab, currentItems],
    () => {
      const isTreehole = activeTab.value === 'treeholes';
      const firstItem = currentItems.value[0];
      applyH5Seo({
        title: buildSeoTitle(isTreehole ? '树洞广场' : '轻圈子'),
        description: extractSeoSummary(
          firstItem?.content ? `浏览${isTreehole ? '匿名树洞' : '说说互动'}：${firstItem.content}` : '',
          isTreehole ? '浏览匿名树洞、轻讨论与互动内容。' : '浏览说说动态、轻互动与作者圈子内容。'
        ),
        keywords: [isTreehole ? '树洞' : '圈子', '说说', '互动', '评论', '轻圈子']
      });
    },
    { immediate: true }
  );

  onLoad((options) => {
    if (!guard('communityEnabled')) {
      return;
    }
    if (options?.tab === 'treeholes') {
      activeTab.value = 'treeholes';
    }
    void refresh();
  });

  onPullDownRefresh(() => {
    refresh().finally(() => uni.stopPullDownRefresh());
  });

  onReachBottom(() => {
    loadMore();
  });

  onPageScroll(handlePageScroll);

  onShow(() => {
    if (!guard('communityEnabled')) {
      return;
    }
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
    uni.$off(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
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
      title: isTreehole ? '来树洞看看匿名故事 - 圆圈博客' : '来圈子看看最新说说 - 圆圈博客',
      path: `/pages/community/community?tab=${activeTab.value}`
    };
  });

  uni.$off(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);
  uni.$on(AUTH_LOGIN_SUCCESS_EVENT, handleLoginSuccess);

  return {
    userStore,
    themeStore,
    tabs,
    activeTab,
    activeTabPath,
    visibleLiquidTabItems,
    switchLiquidTab,
    loading,
    finished,
    isEmpty,
    emptyText,
    currentItems,
    canUseComments,
    canPublishCommunity,
    showCommunityActions,
    currentUserId,
    backToTopVisible,
    commentSheetVisible,
    commentLoading,
    commentSubmitting,
    commentContent,
    commentList,
    replyTarget,
    activeCommentLabel,
    keyboardCursorSpacing,
    keyboardVisible,
    commentSheetStyle,
    getCardView,
    onImageError,
    previewImages,
    goPublish,
    openUserHome,
    isLiked,
    isCollected,
    switchTab,
    openCommentSheet,
    closeCommentSheet,
    handleReplyComment,
    cancelReply,
    submitComment,
    handleDeleteComment,
    toggleLike,
    toggleCollect,
    openItemAction
  };
}
