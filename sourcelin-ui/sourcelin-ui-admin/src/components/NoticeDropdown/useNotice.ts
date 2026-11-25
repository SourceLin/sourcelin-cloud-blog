/**
 * 通知中心逻辑
 */
import { ref, onMounted, onUnmounted } from "vue";
import type { NoticeItem, NoticeDetail, NoticeQueryParams } from "@/types/api";
import { AuthStorage } from "@/utils/auth";
import NoticeAPI from "@/api/system/notice";
import router from "@/router";
import { subscribeUnreadChanges } from "@/utils/SseClientManager";

const PAGE_SIZE = 5;

export function useNotice() {
  // 状态
  const list = ref<NoticeItem[]>([]);
  const unreadTotal = ref(0);
  const detail = ref<NoticeDetail | null>(null);
  const dialogVisible = ref(false);

  // ============================================
  // 数据获取
  // ============================================

  let listRequest = 0;
  async function fetchList(params?: Partial<NoticeQueryParams>) {
    const request = ++listRequest;
    const token = AuthStorage.getAccessToken();
    const query: NoticeQueryParams = {
      page: 1,
      pageSize: PAGE_SIZE,
      isRead: 0,
      ...params,
    };
    const page = await NoticeAPI.getMyNoticePage(query);
    if (request !== listRequest || token !== AuthStorage.getAccessToken()) return;
    list.value = page.items || [];
    unreadTotal.value = page.total ?? 0;
  }

  async function read(id: string) {
    const token = AuthStorage.getAccessToken();
    const notice = await NoticeAPI.getDetail(id);
    if (token !== AuthStorage.getAccessToken()) return;
    await NoticeAPI.markRead(id);
    if (token !== AuthStorage.getAccessToken()) return;
    detail.value = notice;
    dialogVisible.value = true;

    const idx = list.value.findIndex((item: NoticeItem) => item.id === id);
    if (idx >= 0) list.value.splice(idx, 1);
    if (unreadTotal.value > 0) unreadTotal.value -= 1;

    await fetchList();
  }

  async function readAll() {
    const token = AuthStorage.getAccessToken();
    await NoticeAPI.readAll();
    if (token !== AuthStorage.getAccessToken()) return;
    listRequest++;
    list.value = [];
    unreadTotal.value = 0;
    await fetchList();
    ElMessage.success("已全部标记为已读");
  }

  function goMore() {
    router.push({ name: "MyNotice" });
  }

  // ============================================
  // 生命周期
  // ============================================

  function refreshUnread() {
    void fetchList().catch(() => undefined);
  }

  onMounted(refreshUnread);
  onUnmounted(subscribeUnreadChanges(refreshUnread));

  return {
    list,
    unreadTotal,
    detail,
    dialogVisible,
    fetchList,
    read,
    readAll,
    goMore,
  };
}
