import { ref } from 'vue';
import { fetchMyCollects, fetchMyComments, fetchMyLikes } from '@/modules/interaction/api/interaction.api';
import type {
  CollectItem,
  CollectTargetSummary,
  InteractionTab,
  InteractionTargetType,
  LikeItem,
  MyCommentItem,
  MyCommentSource
} from '@/modules/interaction/types/interaction';

type CollectTab = InteractionTargetType | 'all';
type CommentTab = MyCommentSource;

const targetTextMap: Record<InteractionTargetType, string> = {
  article: '文章',
  say: '说说',
  treehole: '树洞'
};

const statusTextMap: Record<number, string> = {
  0: '待审核',
  1: '已通过',
  2: '未通过'
};

function getTargetData(
  item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary>
): CollectTargetSummary | undefined {
  if (item.targetType === 'article') return item.article;
  if (item.targetType === 'say') return item.say;
  return item.treehole;
}

export function getInteractionTitle(
  item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary>
): string {
  const target = getTargetData(item);
  return target?.title || target?.content || '未命名内容';
}

export function getInteractionSummary(
  item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary>
): string {
  const target = getTargetData(item);
  return target?.summary || target?.content || '暂无内容摘要';
}

export function getInteractionMeta(
  item: CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary>
): string {
  const target = getTargetData(item);
  if (item.targetType === 'article') {
    return target?.categoryName || '默认分类';
  }
  return '圈子内容';
}

export function getCommentStatusText(status?: number): string {
  if (status === undefined || status === null) return '审核中';
  return statusTextMap[status] || '未知状态';
}

export function useMyInteractions(initialTab: InteractionTab = 'collect') {
  const activeTab = ref<InteractionTab>(initialTab);
  const activeCollectTab = ref<CollectTab>('all');
  const activeLikeTab = ref<CollectTab>('all');
  const activeCommentTab = ref<CommentTab>('all');

  const collectItems = ref<CollectItem<CollectTargetSummary>[]>([]);
  const likeItems = ref<LikeItem<CollectTargetSummary>[]>([]);
  const commentItems = ref<MyCommentItem[]>([]);

  const loading = ref(false);
  const finished = ref(false);
  const page = ref(1);
  const pageSize = 10;

  const currentItems = ref<Array<CollectItem<CollectTargetSummary> | LikeItem<CollectTargetSummary> | MyCommentItem>>([]);

  function syncCurrentItems(): void {
    if (activeTab.value === 'collect') {
      currentItems.value = collectItems.value;
      return;
    }
    if (activeTab.value === 'like') {
      currentItems.value = likeItems.value;
      return;
    }
    currentItems.value = commentItems.value;
  }

  async function loadPage(reset: boolean): Promise<void> {
    loading.value = true;
    try {
      if (activeTab.value === 'collect') {
        const result = await fetchMyCollects(page.value, pageSize, activeCollectTab.value);
        const nextItems = result.items || [];
        collectItems.value = reset ? nextItems : [...collectItems.value, ...nextItems];
        finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
      } else if (activeTab.value === 'like') {
        const result = await fetchMyLikes(page.value, pageSize, activeLikeTab.value);
        const nextItems = result.items || [];
        likeItems.value = reset ? nextItems : [...likeItems.value, ...nextItems];
        finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
      } else {
        const result = await fetchMyComments(page.value, pageSize, activeCommentTab.value);
        const nextItems = result.items || [];
        commentItems.value = reset ? nextItems : [...commentItems.value, ...nextItems];
        finished.value = page.value >= (result.totalPages || 1) || nextItems.length < pageSize;
      }
      syncCurrentItems();
    } catch {
      if (!reset) {
        page.value -= 1;
      }
    } finally {
      loading.value = false;
    }
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

  async function switchMainTab(tab: InteractionTab): Promise<void> {
    if (loading.value || activeTab.value === tab) return;
    activeTab.value = tab;
    await refresh();
  }

  async function switchSubTab(value: CollectTab | CommentTab): Promise<void> {
    if (loading.value) return;
    if (activeTab.value === 'collect') {
      if (activeCollectTab.value === value) return;
      activeCollectTab.value = value as CollectTab;
    } else if (activeTab.value === 'like') {
      if (activeLikeTab.value === value) return;
      activeLikeTab.value = value as CollectTab;
    } else {
      if (activeCommentTab.value === value) return;
      activeCommentTab.value = value as CommentTab;
    }
    await refresh();
  }

  return {
    activeTab,
    activeCollectTab,
    activeLikeTab,
    activeCommentTab,
    collectItems,
    likeItems,
    commentItems,
    loading,
    finished,
    targetTextMap,
    statusTextMap,
    refresh,
    loadMore,
    switchMainTab,
    switchSubTab
  };
}
