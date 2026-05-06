import { computed, nextTick, onUnmounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { searchArticles, searchTags, type SearchTag } from '@/modules/navigation/api/search.api'
import { useUiStore } from '@/stores/ui.store'

interface SearchItem {
  id: number
  title: string
  summary: string
}

export function useSearchDialog() {
  const router = useRouter()
  const uiStore = useUiStore()
  const searchContainerRef = ref<HTMLElement | null>(null)
  const searchTimer = ref<ReturnType<typeof setTimeout> | null>(null)

  const pageData = reactive({
    page: 1,
    pageSize: 10,
    keyword: ''
  })

  const pages = ref(0)
  const total = ref(0)
  const list = ref<SearchItem[]>([])
  const tagList = ref<SearchTag[]>([])
  const loading = ref(false)

  const dialogVisible = computed({
    get: () => uiStore.searchDialogVisible,
    set: (value: boolean) => uiStore.setSearchDialogVisible(value)
  })

  const clearSearch = () => {
    pageData.keyword = ''
    pageData.page = 1
    pages.value = 0
    total.value = 0
    list.value = []
  }

  const closeModal = () => {
    dialogVisible.value = false
    clearSearch()
  }

  const fetchResults = async () => {
    try {
      const res = await searchArticles(pageData)
      list.value = res.items || []
      pages.value = res.totalPages || 0
      total.value = res.total || 0
    } catch (error) {
      console.error('搜索失败:', error)
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    const keyword = pageData.keyword.replace(/\s/g, '')

    if (!keyword) {
      list.value = []
      total.value = 0
      pages.value = 0
      loading.value = false
      return
    }

    if (searchTimer.value) {
      clearTimeout(searchTimer.value)
    }

    loading.value = true
    searchTimer.value = setTimeout(() => {
      pageData.page = 1
      void fetchResults()
    }, 300)
  }

  const changePage = (page: number) => {
    pageData.page = page
    void fetchResults()
  }

  const handleToTag = (id: number) => {
    closeModal()
    void router.push({ path: '/tag', query: { id: String(id) } })
  }

  const getTagList = async () => {
    try {
      const res = await searchTags({ pageSize: 10 })
      tagList.value = res.items || []
    } catch (error) {
      console.error('获取标签失败:', error)
    }
  }

  const highlightKeyword = (text: string) => {
    if (!pageData.keyword || !text) {
      return text
    }

    const regex = new RegExp(`(${pageData.keyword})`, 'gi')
    return text.replace(regex, '<mark class="highlight">$1</mark>')
  }

  const getTagHue = (index: number) => {
    const hues = [220, 280, 320, 180, 260, 340, 200, 240]
    return hues[index % hues.length]
  }

  watch(dialogVisible, async (newValue) => {
    if (!newValue) {
      return
    }

    await nextTick()
    searchContainerRef.value?.querySelector('input')?.focus()
    void getTagList()
  })

  watch(
    () => pageData.keyword,
    () => {
      handleSearch()
    }
  )

  onUnmounted(() => {
    if (searchTimer.value) {
      clearTimeout(searchTimer.value)
    }
  })

  return {
    searchContainerRef,
    pageData,
    pages,
    total,
    list,
    tagList,
    loading,
    dialogVisible,
    closeModal,
    changePage,
    handleToTag,
    highlightKeyword,
    getTagHue
  }
}

