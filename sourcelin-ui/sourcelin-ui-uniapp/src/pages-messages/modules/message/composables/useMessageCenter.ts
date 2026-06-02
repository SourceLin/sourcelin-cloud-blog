// 消息中心 composable：频道切换、分页加载、未读计数与全部已读
import { ref, computed } from 'vue';
import {
  fetchMessagePage,
  fetchUnreadMessageCount,
  markAllMessagesRead,
  type MessagePageQuery
} from '@/modules/message/api/message.api';
import type { MessageItem } from '@/modules/message/types/message';
import {
  MESSAGE_CHANNELS,
  getChannelConfig,
  type MessageChannel,
  type ChannelConfig
} from '../config/channels';

const PAGE_SIZE = 10;

export function useMessageCenter() {
  const activeChannel = ref<MessageChannel>('system');
  const items = ref<MessageItem[]>([]);
  const loading = ref(false);
  const finished = ref(false);
  const page = ref(1);
  const unreadCount = ref(0);

  const channels = MESSAGE_CHANNELS.filter((c) => c.available);

  /** 多频道时才展示 Tab 栏 */
  const showTabs = computed(() => channels.length > 1);

  const currentChannelConfig = computed<ChannelConfig>(
    () => getChannelConfig(activeChannel.value)
  );

  const isCurrentChannelAvailable = computed(
    () => currentChannelConfig.value.available
  );

  /** 当前频道是否有消息 */
  const isEmpty = computed(
    () => !loading.value && items.value.length === 0
  );

  /** 切换频道并重新加载 */
  async function switchChannel(channel: MessageChannel): Promise<void> {
    if (channel === activeChannel.value && items.value.length > 0) return;
    activeChannel.value = channel;
    items.value = [];
    page.value = 1;
    finished.value = false;
    if (getChannelConfig(channel).available) {
      await loadPage(true);
    }
  }

  /** 刷新当前频道 */
  async function refresh(): Promise<void> {
    if (!isCurrentChannelAvailable.value) return;
    page.value = 1;
    finished.value = false;
    await Promise.all([loadPage(true), loadUnreadCount()]);
  }

  /** 加载更多 */
  async function loadMore(): Promise<void> {
    if (loading.value || finished.value || !isCurrentChannelAvailable.value) return;
    page.value += 1;
    await loadPage(false);
  }

  /** 加载分页数据 */
  async function loadPage(reset: boolean): Promise<void> {
    loading.value = true;
    try {
      const query: MessagePageQuery = {
        page: page.value,
        pageSize: PAGE_SIZE,
        channel: activeChannel.value
      };
      const result = await fetchMessagePage(query);
      const nextItems = (result.items || []).map(normalizeMessage);
      items.value = reset ? nextItems : [...items.value, ...nextItems];
      finished.value = page.value >= (result.totalPages || 1) || nextItems.length < PAGE_SIZE;
    } catch {
      if (!reset) page.value -= 1;
    } finally {
      loading.value = false;
    }
  }

  /** 加载未读计数 */
  async function loadUnreadCount(): Promise<void> {
    try {
      const stat = await fetchUnreadMessageCount();
      unreadCount.value = stat.total || 0;
    } catch {
      unreadCount.value = 0;
    }
  }

  /** 全部标为已读 */
  async function markAllRead(): Promise<void> {
    await markAllMessagesRead(activeChannel.value);
    items.value = items.value.map((item) => ({ ...item, isRead: 1 }));
    unreadCount.value = 0;
  }

  return {
    channels,
    showTabs,
    activeChannel,
    currentChannelConfig,
    isEmpty,
    items,
    loading,
    finished,
    unreadCount,
    switchChannel,
    refresh,
    loadMore,
    loadUnreadCount,
    markAllRead
  };
}

/** 标准化消息条目：统一 isRead 为 0/1 */
function normalizeMessage(item: MessageItem): MessageItem {
  return {
    ...item,
    isRead: Number(item.isRead) === 1 ? 1 : 0
  };
}
