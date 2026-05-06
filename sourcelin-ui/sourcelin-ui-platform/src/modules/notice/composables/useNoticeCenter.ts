import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getNotices } from '@/modules/notice/api/notice.api'
import type { AppIconName } from '@/shared/components/ui/icons'
import { useSMessage } from '@/shared/composables/useSMessage'

interface SystemNoticeItem {
  noticeId?: number
  noticeTitle?: string
  noticeContent?: string
  createTime?: string
}

export interface NoticeViewItem {
  id: number
  title: string
  content: string
  createTimeStr: string
  isRead?: number
}

export interface NoticeMenuItem {
  name: string
  icon: AppIconName
}

const LEFT_ITEMS: NoticeMenuItem[] = [
  { name: '系统公告', icon: 'notice' }
]

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

export function useNoticeCenter() {
  const router = useRouter()
  const message = useSMessage()
  const pageData = ref({
    pageNo: 1,
    pageSize: 10,
    type: 0
  })
  const pages = ref(1)
  const refreshKey = ref(0)
  const title = ref('系统公告')
  const noticeList = ref<NoticeViewItem[]>([])
  const noticesLoading = ref(false)

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

  const splitIpAddress = (address: string) => address

  const loadSystemNotices = async () => {
    noticesLoading.value = true
    try {
      const rows = await getNotices()
      noticeList.value = rows.map((item) => {
        const notice = asNotice(item as SystemNoticeItem)
        return {
          id: Number(notice.noticeId ?? 0),
          title: String(notice.noticeTitle ?? '系统公告'),
          content: String(notice.noticeContent ?? ''),
          createTimeStr: formatTime(String(notice.createTime ?? ''))
        }
      })
      pages.value = 1
    } catch (error) {
      console.error('加载公告失败:', error)
    } finally {
      noticesLoading.value = false
    }
  }

  const getList = async () => {
    await loadSystemNotices()
  }

  const handlePage = () => {
    pageData.value.pageNo = 1
  }

  const handleSizeChange = () => {
    pageData.value.pageNo = 1
  }

  const clearMessageItem = (_id: number | null, index: number | null) => {
    if (index === null) {
      noticeList.value = []
    } else {
      noticeList.value.splice(index, 1)
    }
    message.success('已从当前列表移除')
  }

  const handleClick = (item: NoticeMenuItem, index: number) => {
    title.value = item.name
    pageData.value.type = index
    refreshKey.value = Date.now()
    void getList()
  }

  onMounted(() => {
    title.value = LEFT_ITEMS[0].name
    void getList()
  })

  return {
    leftItems: LEFT_ITEMS,
    pageData,
    pages,
    refreshKey,
    title,
    noticeList,
    noticesLoading,
    goBack,
    splitIpAddress,
    handlePage,
    handleSizeChange,
    clearMessageItem,
    handleClick
  }
}
