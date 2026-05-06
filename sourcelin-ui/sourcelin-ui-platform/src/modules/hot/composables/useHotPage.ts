import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHotArticles, getHotCategories, type HotArticle } from '@/modules/hot/api/hot.api'

export interface HotItem extends HotArticle {
  url?: string
  articleId?: number
  coverImage?: string
  summary?: string
  heightType?: 'short' | 'medium' | 'tall'
}

export interface HotCategory {
  id: string | number
  name: string
}

const DEFAULT_CATEGORIES: HotCategory[] = [
  { id: 'all', name: '全部' }
]

export function useHotPage() {
  const router = useRouter()
  const loading = ref(false)
  const loadingMore = ref(false)
  const finished = ref(false)
  const page = ref(1)
  const pageSize = 20
  const hotTotal = ref(0)
  const activeCategory = ref<string | number>('all')
  const categories = ref<HotCategory[]>(DEFAULT_CATEGORIES)
  const hotList = ref<HotItem[]>([])

  const toCategoryId = (value: string | number) => {
    const parsed = Number(value)
    return Number.isFinite(parsed) && parsed > 0 ? parsed : undefined
  }

  const loadCategories = async () => {
    try {
      const rows = await getHotCategories()
      if (!rows.length) {
        categories.value = DEFAULT_CATEGORIES
        return
      }
      categories.value = [
        DEFAULT_CATEGORIES[0],
        ...rows
          .filter((item) => item.id && item.name)
          .map((item) => ({
            id: item.id as number,
            name: item.name as string
          }))
      ]
    } catch (error) {
      console.error('加载热榜分类失败:', error)
      categories.value = DEFAULT_CATEGORIES
    }
  }

  const loadData = async (reset = false) => {
    if (reset) {
      page.value = 1
      finished.value = false
    }

    const shouldShowInitialLoading = hotList.value.length === 0
    if (shouldShowInitialLoading) {
      loading.value = true
    }
    try {
      const params: { page: number; pageSize: number; categoryId?: number } = { page: page.value, pageSize }
      if (activeCategory.value !== 'all') {
        const categoryId = toCategoryId(activeCategory.value)
        if (categoryId !== undefined) {
          params.categoryId = categoryId
        }
      }

      const res = await getHotArticles(params)
      const rows = res.items.map((item) => ({
        ...item,
        articleId: item.articleId || item.id,
        coverImage: item.coverImage || item.avatar
      })) as HotItem[]
      hotTotal.value = Number(res.total ?? 0)

      if (reset) {
        hotList.value = rows
      } else {
        hotList.value = [...hotList.value, ...rows]
      }

      finished.value = res.totalPages > 0 ? page.value >= res.totalPages : rows.length < pageSize
    } catch (error) {
      console.error('加载热榜失败:', error)
      if (reset) {
        hotList.value = []
      }
      finished.value = true
      hotTotal.value = 0
    } finally {
      if (shouldShowInitialLoading) {
        loading.value = false
      }
    }
  }

  const loadMore = async () => {
    if (loadingMore.value || finished.value) {
      return
    }

    loadingMore.value = true
    page.value += 1
    await loadData()
    loadingMore.value = false
  }

  const changeCategory = (categoryId: string | number) => {
    if (activeCategory.value === categoryId) {
      return
    }

    activeCategory.value = categoryId
    void loadData(true)
  }

  const openItem = (item: HotItem) => {
    if (item.url) {
      window.open(item.url, '_blank')
      return
    }

    if (item.articleId) {
      void router.push(`/article/${item.articleId}`)
    }
  }

  const getRankClass = (index: number) => {
    if (index === 0) return 'rank-1'
    if (index === 1) return 'rank-2'
    if (index === 2) return 'rank-3'
    return ''
  }

  const formatHeat = (heat: number) => {
    if (heat >= 10000) {
      return `${(heat / 10000).toFixed(1)}万`
    }

    if (heat >= 1000) {
      return `${(heat / 1000).toFixed(1)}千`
    }

    return String(heat || 0)
  }

  const formatTime = (time: string) => {
    if (!time) {
      return ''
    }

    const date = new Date(time)
    const now = new Date()
    const diff = now.getTime() - date.getTime()

    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`

    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}-${day}`
  }

  onMounted(() => {
    void loadCategories()
    void loadData(true)
  })

  return {
    loading,
    loadingMore,
    finished,
    page,
    pageSize,
    hotTotal,
    activeCategory,
    categories,
    hotList,
    loadMore,
    changeCategory,
    openItem,
    getRankClass,
    formatHeat,
    formatTime
  }
}

