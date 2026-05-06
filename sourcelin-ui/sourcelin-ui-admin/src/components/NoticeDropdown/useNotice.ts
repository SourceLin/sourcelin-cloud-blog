/**
 * 通知中心逻辑
 */
import { ref, onMounted } from "vue";
import type { NoticeItem, NoticeDetail, NoticeQueryParams } from "@/types/api";
import NoticeAPI from "@/api/system/notice";
import router from "@/router";

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

  async function fetchList(params?: Partial<NoticeQueryParams>) {
    const query: NoticeQueryParams = {
      page: 1,
      pageSize: PAGE_SIZE,
      isRead: 0,
      ...params,
    };
    const page = await NoticeAPI.getMyNoticePage(query);
    list.value = page.items || [];
    unreadTotal.value = page.total ?? 0;
  }

  async function read(id: string) {
    detail.value = await NoticeAPI.getDetail(id);
    dialogVisible.value = true;

    const idx = list.value.findIndex((item: NoticeItem) => item.id === id);
    if (idx >= 0) list.value.splice(idx, 1);
    if (unreadTotal.value > 0) unreadTotal.value -= 1;

    await fetchList();
  }

  async function readAll() {
    await NoticeAPI.readAll();
    list.value = [];
    unreadTotal.value = 0;
    ElMessage.success("已全部标记为已读");
  }

  function goMore() {
    router.push({ name: "MyNotice" });
  }

  // ============================================
  // 生命周期
  // ============================================

  onMounted(() => {
    fetchList();
  });

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
