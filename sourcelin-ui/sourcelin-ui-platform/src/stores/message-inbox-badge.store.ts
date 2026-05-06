import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount } from '@/modules/notice/api/notice.api'
import { useUserStore } from '@/stores/user'

/**
 * 顶栏头像红点 / 下拉「消息中心」旁数字。
 * 基于博客公告未读数接口。
 */
export const useMessageInboxBadgeStore = defineStore('messageInboxBadge', () => {
  const totalCount = ref(0)
  const loading = ref(false)

  async function refresh() {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      totalCount.value = 0
      return
    }
    loading.value = true
    try {
      const data = await getUnreadCount()
      totalCount.value = Number(data.total ?? 0)
    } catch {
      totalCount.value = 0
    } finally {
      loading.value = false
    }
  }

  /** 与消息页本地列表同步，避免重复请求 */
  function setTotalCount(next: number) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      totalCount.value = 0
      return
    }
    totalCount.value = Number.isFinite(next) ? Math.max(0, Math.floor(next)) : 0
  }

  return {
    totalCount,
    loading,
    refresh,
    setTotalCount
  }
})
