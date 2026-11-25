import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getMessageDetail,
  getMessages,
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

  let listRequest = 0
  const loadNotices = async () => {
    const id = ++listRequest
    const token = userStore.token
    noticesLoading.value = true
    try {
      const rows = await getMessages({ channel: activeChannel.value, page: 1, pageSize: 100 })
      if (id !== listRequest || userStore.token !== token) return
      noticeList.value = rows.map((notice) => {
        return {
          sourceAvailable: notice.sourceAvailable,
          id: Number(notice.id ?? 0),
          title: String(notice.title ?? '系统公告'),
          content: String(notice.content ?? ''),
          createTimeStr: formatTime(String(notice.publishTime ?? '')),
          isRead: Number(notice.isRead ?? 0)
        }
      })
      refreshKey.value = Date.now()

    } catch (error) {
      console.error('加载公告失败:', error)
    } finally {
      if (id === listRequest) noticesLoading.value = false
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
    const token = userStore.token
    if (index === null) {
      if (userStore.isLoggedIn) {
        await readAllMessages(activeChannel.value)
      }
      if (userStore.token !== token) return
      noticeList.value = noticeList.value.map((item) => ({ ...item, isRead: 1 }))
    } else {
      if (userStore.isLoggedIn && id != null) {
        await readMessage(id, activeChannel.value)
      }
      if (userStore.token !== token) return
      noticeList.value = noticeList.value.map(item => item.id === id ? { ...item, isRead: 1 } : item)
    }
    if (userStore.isLoggedIn) {
      await messageInboxBadgeStore.refresh()
    }
    toast.success('已标为已读')
  }

  async function openMessage(id: number) {
    const channel = activeChannel.value
    const token = userStore.token
    const message = await getMessageDetail(id, channel)
    if (token !== userStore.token || channel !== activeChannel.value) return
    await readMessage(id, channel)
    if (token !== userStore.token || channel !== activeChannel.value) return
    noticeList.value = noticeList.value.map(item => item.id === id ? { ...item, isRead: 1 } : item)
    await messageInboxBadgeStore.refresh()
    if (token !== userStore.token || channel !== activeChannel.value) return
    if (!message.sourceAvailable) { toast.info('来源已不可用'); return }
    if (message.targetType === 'article') {
      await router.push({ path: `/article/${message.targetId}`, query: message.commentId ? { message: id } : {} })
    }
  }

  function setChannel(id: MessageChannel) {
    void router.replace({
      path: route.path,
      query: { ...route.query, channel: id }
    })
  }

  watch(
    [() => route.query.channel, () => userStore.token, () => messageInboxBadgeStore.inboxRevision],
    async ([q, token], previous) => {
      listRequest++
      noticesLoading.value = false
      if (token !== previous?.[1] || q !== previous?.[0]) noticeList.value = []
      if (!isMessageChannel(q)) {
        await router.replace({
          path: route.path,
          query: { ...route.query, channel: 'system' }
        })
        return
      }
      if (q === 'system' || userStore.isLoggedIn) {
        await loadNotices()
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
    openMessage,
    clearMessageItem,
    setChannel
  }
}
