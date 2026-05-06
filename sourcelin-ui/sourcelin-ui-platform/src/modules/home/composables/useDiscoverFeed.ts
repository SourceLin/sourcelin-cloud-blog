import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { ComputedRef, Ref } from 'vue'
import {
  getArticles,
  type ArticleListItem,
  type ArticlePageQuery
} from '@/modules/article/api/article.api'
import type { PageResult } from '@/shared/types/api'
import type { HomeCategory } from '@/modules/home/model/home-discover'
import { normalizeHomeArticles } from '@/modules/home/model/home-discover'
import {
  buildDiscoverQuery,
  parseDiscoverCategoryId,
  resolveDiscoverCategoryId
} from '@/modules/home/model/home-discover-route'
import { useSMessage } from '@/shared/composables/useSMessage'

type CategorySource = ComputedRef<HomeCategory[]> | Readonly<Ref<HomeCategory[]>>

export function useDiscoverFeed(categories: CategorySource) {
  const router = useRouter()
  const route = useRoute()
  const message = useSMessage()

  const initialized = ref(false)
  const requestSeq = ref(0)
  const selectedCategoryId = ref<number | null>(null)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const loading = ref(false)
  const switching = ref(false)
  const loadingMore = ref(false)
  const finished = ref(false)
  const articles = ref<ReturnType<typeof normalizeHomeArticles>>([])

  const routeCategoryId = computed(() => parseDiscoverCategoryId(route.query.dc))
  const selectedCategoryName = computed(() => {
    if (selectedCategoryId.value == null) {
      return ''
    }

    return categories.value.find((item) => item.id === selectedCategoryId.value)?.name || ''
  })

  function buildQuery(page: number): ArticlePageQuery {
    const params: ArticlePageQuery = {
      page,
      pageSize: pageSize.value
    }

    if (selectedCategoryId.value != null) {
      params.categoryId = selectedCategoryId.value
    }

    return params
  }

  async function loadDiscoverList(options: { reset: boolean }) {
    const page = options.reset ? 1 : currentPage.value + 1
    const seq = ++requestSeq.value
    const hasArticles = articles.value.length > 0

    if (options.reset) {
      finished.value = false
      if (hasArticles) {
        switching.value = true
      } else {
        loading.value = true
      }
    } else {
      if (loadingMore.value || finished.value) {
        return
      }
      loadingMore.value = true
    }

    try {
      const data = await getArticles(buildQuery(page))
      if (seq !== requestSeq.value) {
        return
      }

      const rows = normalizeHomeArticles(data.items)
      articles.value = options.reset ? rows : [...articles.value, ...rows]
      total.value = Number(data.total || 0)
      currentPage.value = page
      finished.value = data.totalPages > 0
        ? currentPage.value >= data.totalPages
        : rows.length < pageSize.value
    } catch (error) {
      if (seq === requestSeq.value) {
        console.error(error)
        message.error('文章列表加载失败，请稍后重试')
      }
    } finally {
      if (options.reset) {
        if (seq === requestSeq.value) {
          loading.value = false
          switching.value = false
        }
      } else {
        loadingMore.value = false
      }
    }
  }

  function applyPageResult(data: PageResult<ArticleListItem>, page: number, reset: boolean) {
    const rows = normalizeHomeArticles(data.items)
    articles.value = reset ? rows : [...articles.value, ...rows]
    total.value = Number(data.total || 0)
    currentPage.value = page
    finished.value = data.totalPages > 0
      ? currentPage.value >= data.totalPages
      : rows.length < pageSize.value
  }

  async function replaceDiscoverRoute(categoryId: number | null) {
    await router.replace({ query: buildDiscoverQuery(route.query, categoryId) })
  }

  async function selectCategory(categoryId: number | null) {
    if (categoryId === selectedCategoryId.value) {
      return
    }

    await replaceDiscoverRoute(categoryId)
  }

  async function loadMore() {
    await loadDiscoverList({ reset: false })
  }

  async function initDiscover(initialLatest: PageResult<ArticleListItem> | null = null) {
    if (initialized.value) {
      return
    }

    initialized.value = true
    const rawRouteCategoryId = routeCategoryId.value
    const resolvedCategoryId = resolveDiscoverCategoryId(rawRouteCategoryId, categories.value)
    selectedCategoryId.value = resolvedCategoryId

    if (rawRouteCategoryId !== resolvedCategoryId) {
      await replaceDiscoverRoute(resolvedCategoryId)
    }

    if (initialLatest && rawRouteCategoryId === resolvedCategoryId) {
      applyPageResult(initialLatest, Number(initialLatest.page || 1), true)
      return
    }

    await loadDiscoverList({ reset: true })
  }

  watch(routeCategoryId, (categoryId) => {
    if (!initialized.value || categoryId === selectedCategoryId.value) {
      return
    }

    selectedCategoryId.value = categoryId
    void loadDiscoverList({ reset: true })
  })

  watch(categories, (items) => {
    if (!initialized.value || !items.length || selectedCategoryId.value == null) {
      return
    }

    const nextCategoryId = resolveDiscoverCategoryId(selectedCategoryId.value, items)
    if (nextCategoryId == null) {
      void replaceDiscoverRoute(null)
    }
  })

  return {
    selectedCategoryId,
    selectedCategoryName,
    articles,
    currentPage,
    pageSize,
    total,
    loading,
    switching,
    loadingMore,
    finished,
    requestSeq,
    selectCategory,
    loadMore,
    initDiscover
  }
}
