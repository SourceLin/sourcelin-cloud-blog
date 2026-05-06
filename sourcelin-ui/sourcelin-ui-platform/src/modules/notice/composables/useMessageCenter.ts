import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getMessages,
  getUnreadCount,
  readAllMessages,
  readMessage
} from '@/modules/notice/api/notice.api'
import {
  getChannelConfig,
  isMessageChannel,
  type MessageChannel,
  MESSAGE_CHANNELS
} from '@/modules/notice/config/channels'
import type { NoticeViewItem } from '@/modules/notice/composables/useNoticeCenter'
import { useMessageInboxBadgeStore } from '@/stores/message-inbox-badge.store'
import { useUserStore } from '@/stores/user'
import { useSMessage } from '@/shared/composables/useSMessage'

interface SystemNoticeItem {
  id?: number
  title?: string
  content?: string
  publishTime?: string
  isRead?: number
}

function asNotice(value: unknown): SystemNoticeItem {
  if (typeof value === 'object' && value !== null) {
    return value as SystemNoticeItem
  }
  return {}
}

function formatTime(time: string) {
  if (!time) {
    return ''
  }
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) {
    return time
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

function parseChannelFromQuery(query: unknown): MessageChannel {
  return isMessageChannel(query) ? query : 'system'
}

export function useMessageCenter() {
  const route = useRoute()
  const router = useRouter()
  const toast = useSMessage()
  const userStore = useUserStore()
  const messageInboxBadgeStore = useMessageInboxBadgeStore()

  const noticeList = ref<NoticeViewItem[]>([])
  const noticesLoading = ref(false)
  const refreshKey = ref(0)

  const activeChannel = computed<MessageChannel>(() =>
    parseChannelFromQuery(route.query.channel)
  )

  const currentChannel = computed(() => getChannelConfig(activeChannel.value))

  const contentTitle = computed(() => currentChannel.value.name)

  const splitIpAddress = (address: string) => address

  const loadSystemNotices = async () => {
    noticesLoading.value = true
    try {
      const rows = await getMessages({ channel: 'system', page: 1, pageSize: 100 })
      noticeList.value = rows.map((item) => {
        const notice = asNotice(item as SystemNoticeItem)
        return {
          id: Number(notice.id ?? 0),
          title: String(notice.title ?? '系统公告'),
          content: String(notice.content ?? ''),
          createTimeStr: formatTime(String(notice.publishTime ?? '')),
          isRead: Number(notice.isRead ?? 0)
        }
      })
      refreshKey.value = Date.now()
      if (userStore.isLoggedIn) {
        const unread = await getUnreadCount()
        messageInboxBadgeStore.setTotalCount(Number(unread.total ?? 0))
      }
    } catch (error) {
      console.error('加载公告失败:', error)
    } finally {
      noticesLoading.value = false
    }
  }

  const goBack = () => {
    const from = history.state?.from as string | undefined
    if (from === 'user') {
      void router.push('/user')
      return
    }
    if (window.history.length > 1) {
      router.go(-1)
      return
    }
    void router.push('/')
  }

  const clearMessageItem = async (id: number | null, index: number | null) => {
    if (index === null) {
      if (userStore.isLoggedIn) {
        await readAllMessages()
      }
      noticeList.value = noticeList.value.map((item) => ({ ...item, isRead: 1 }))
    } else {
      if (userStore.isLoggedIn && id != null) {
        await readMessage(id)
      }
      noticeList.value.splice(index, 1)
    }
    if (userStore.isLoggedIn) {
      const unread = await getUnreadCount()
      messageInboxBadgeStore.setTotalCount(Number(unread.total ?? 0))
    }
    toast.success('已从当前列表移除')
  }

  function setChannel(id: MessageChannel) {
    void router.replace({
      path: route.path,
      query: { ...route.query, channel: id }
    })
  }

  watch(
    () => route.query.channel,
    async (q) => {
      if (!isMessageChannel(q)) {
        await router.replace({
          path: route.path,
          query: { ...route.query, channel: 'system' }
        })
        return
      }
      if (parseChannelFromQuery(q) === 'system') {
        await loadSystemNotices()
      }
    },
    { immediate: true }
  )

  return {
    channels: MESSAGE_CHANNELS,
    activeChannel,
    currentChannel,
    contentTitle,
    noticeList,
    noticesLoading,
    refreshKey,
    goBack,
    splitIpAddress,
    clearMessageItem,
    setChannel
  }
}
