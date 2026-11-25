import type { Component } from 'vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getCategoryArticles, type CategoryOption } from '@/modules/article/api/category.api'
import type { ArticleListItem } from '@/modules/article/api/article.api'
import { buildCategoryArticleRoute } from '@/modules/article/utils/article-source'
import { appIcons, type AppIconName } from '@/shared/components/ui/icons'
import { useSMessage } from '@/shared/composables/useSMessage'

export interface Category {
  id: number
  name: string
  icon: string
  description: string
  articleCount: number
  hue: number
}

export interface CategoryArticle {
  id: number
  title: string
  summary: string
  cover: string
  date: string
  views: number
  comments: number
  authorName: string
}

export interface CategoryHeroStatTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
}

const iconByName: Record<string, AppIconName> = {
  folder: 'folder',
  category: 'category',
  tag: 'tag',
  archive: 'archive',
  home: 'home',
  link: 'links'
}

function normalizeDate(value: unknown): string {
  const text = typeof value === 'string' ? value : ''
  return text ? text.split(' ')[0] : ''
}

function normalizeCategory(item: CategoryOption, index: number): Category {
  return {
    id: Number(item.id) || index + 1,
    name: String(item.name || '未命名分类'),
    icon: String(item.icon || 'folder'),
    description: String(item.summary || ''),
    articleCount: Number(item.articleCount || 0) || 0,
    hue: (index * 46) % 360
  }
}

function normalizeArticle(item: ArticleListItem): CategoryArticle {
  return {
    id: Number(item.id) || 0,
    title: String(item.title || '未命名文章'),
    summary: String(item.summary || '这篇文章暂时还没有摘要。'),
    cover: String(item.avatar || ''),
    date: normalizeDate(item.createTime),
    views: Number(item.viewCount || 0),
    comments: Number(item.commentCount || 0),
    authorName: String(item.author || '平台作者')
  }
}

export function resolveCategoryIcon(icon: string) {
  const normalized = icon.trim().toLowerCase()
  const key = iconByName[normalized] || 'folder'
  return appIcons[key]
}

export function useCategoryPageQuery() {
  const route = useRoute()
  const router = useRouter()
  const message = useSMessage()

  const categorySwitchSectionRef = ref<HTMLElement | null>(null)
  const categories = ref<Category[]>([])
  const categoryArticles = ref<CategoryArticle[]>([])
  const activeCategory = ref<Category | null>(null)
  const pendingCategoryId = ref<number | null>(null)
  const loadingCategories = ref(false)
  const loadingArticles = ref(false)
  const articlePage = ref(1)
  const articlePageSize = 12
  const articleTotal = ref(0)

  const articleTotalPages = computed(() =>
    articleTotal.value > 0 ? Math.ceil(articleTotal.value / articlePageSize) : 0
  )

  const categoryFoldExpandLabel = computed(() =>
    categories.value.length > 0 ? `展开全部 ${categories.value.length} 个分类` : '展开全部分类'
  )

  function getCategorySwitchScrollAnchor() {
    return categorySwitchSectionRef.value
  }

  const queryCategoryName = computed(() => (typeof route.query.name === 'string' ? route.query.name.trim() : ''))
  const queryCategoryId = computed(() => {
    const raw = route.query.categoryId
    const value = Array.isArray(raw) ? raw[0] : raw
    const parsed = Number(String(value || ''))
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null
  })

  const totalCategoryArticles = computed(() => categories.value.reduce((sum, c) => sum + (c.articleCount || 0), 0))
  const categoriesWithArticles = computed(() => categories.value.filter((c) => (c.articleCount || 0) > 0).length)
  const featuredCategory = computed(
    () => activeCategory.value ?? categories.value.find((item) => item.articleCount > 0) ?? categories.value[0] ?? null
  )
  const isDetailMode = computed(() => activeCategory.value !== null)
  const contextCategory = computed(
    () =>
      activeCategory.value ??
      categories.value.find((item) => item.id === pendingCategoryId.value) ??
      featuredCategory.value
  )
  const categoryContextStyle = computed(() => ({
    '--context-accent-hue': String(contextCategory.value?.hue ?? 245)
  }))

  const categoryHeroStatTiles = computed((): CategoryHeroStatTile[] => {
    const count = categories.value.length
    const articles = totalCategoryArticles.value
    const active = categoriesWithArticles.value

    return [
      {
        label: '分类总数',
        value: String(count).padStart(2, '0'),
        icon: appIcons.category,
        hue: 245,
        ariaLabel: `共 ${count} 个分类`
      },
      {
        label: '内容篇数',
        value: String(articles),
        icon: appIcons.document,
        hue: 210,
        ariaLabel: `分类下共 ${articles} 篇文章`
      },
      {
        label: '有文分类',
        value: String(active).padStart(2, '0'),
        icon: appIcons.folderOpen,
        hue: 175,
        ariaLabel: `${active} 个分类下已有文章`
      }
    ]
  })

  const articleStageCopy = computed(() => {
    if (!activeCategory.value) {
      return '选择分类后，将在当前区域展示该分类文章列表。'
    }

    return (
      activeCategory.value.description ||
      `这里汇总了“${activeCategory.value.name}”分类文章，继续下滑即可浏览并打开阅读。`
    )
  })

  async function loadCategories() {
    loadingCategories.value = true
    try {
      const response = await getCategories()
      categories.value = response.map((item, index) => normalizeCategory(item, index))
    } catch (error) {
      console.error('加载分类失败:', error)
      categories.value = []
    } finally {
      loadingCategories.value = false
    }
  }

  async function loadArticlesForCategory(categoryId: number) {
    loadingArticles.value = true
    try {
      const response = await getCategoryArticles(categoryId, {
        page: articlePage.value,
        pageSize: articlePageSize
      })
      articleTotal.value = Number(response.total ?? 0)
      categoryArticles.value = response.items.map((item) => normalizeArticle(item))
    } catch (error) {
      console.error('加载分类文章失败:', error)
      categoryArticles.value = []
      articleTotal.value = 0
    } finally {
      loadingArticles.value = false
    }
  }

  function findCategoryFromRoute() {
    if (queryCategoryId.value != null) {
      return categories.value.find((item) => item.id === queryCategoryId.value) ?? null
    }

    if (!queryCategoryName.value) {
      return null
    }

    const normalized = queryCategoryName.value.toLowerCase()
    return categories.value.find((item) => item.name.toLowerCase() === normalized) ?? null
  }

  async function applyActiveCategory(category: Category | null) {
    if (!category) {
      activeCategory.value = null
      categoryArticles.value = []
      pendingCategoryId.value = null
      articlePage.value = 1
      articleTotal.value = 0
      return
    }

    if (activeCategory.value?.id === category.id && categoryArticles.value.length) {
      return
    }

    activeCategory.value = category
    pendingCategoryId.value = category.id
    articlePage.value = 1
    await loadArticlesForCategory(category.id)
  }

  async function syncCategoryFromRoute() {
    const matched = findCategoryFromRoute()

    if (!matched) {
      await applyActiveCategory(null)
      return
    }

    if (queryCategoryName.value !== matched.name) {
      await router.replace({ path: '/categories', query: { name: matched.name } })
      return
    }

    await applyActiveCategory(matched)
  }

  async function selectCategory(category: Category) {
    if (!category.articleCount) {
      message.info('该分类下暂无文章')
      return
    }

    pendingCategoryId.value = category.id

    if (queryCategoryName.value === category.name) {
      await applyActiveCategory(category)
      return
    }

    await router.push({ path: '/categories', query: { name: category.name } })
  }

  function resetCategoryView() {
    pendingCategoryId.value = null
    void router.push('/categories')
  }

  function scrollCategorySwitchIntoView() {
    const anchor = getCategorySwitchScrollAnchor()
    if (!(anchor instanceof HTMLElement)) {
      return
    }

    const top = anchor.getBoundingClientRect().top + window.scrollY - 88
    window.scrollTo({
      top: Math.max(top, 0),
      behavior: 'smooth'
    })
  }

  async function handleArticlePageChange(page: number) {
    if (!activeCategory.value || page === articlePage.value || page < 1) {
      return
    }

    articlePage.value = page
    await loadArticlesForCategory(activeCategory.value.id)
    scrollCategorySwitchIntoView()
  }

  function goToArticle(id: number) {
    const name = activeCategory.value?.name
    if (!name) {
      void router.push(`/article/${id}`)
      return
    }

    void router.push(buildCategoryArticleRoute(id, name))
  }

  watch(
    () => [route.query.name, route.query.categoryId],
    () => {
      if (categories.value.length) {
        void syncCategoryFromRoute()
      }
    }
  )

  onMounted(async () => {
    await loadCategories()
    await syncCategoryFromRoute()
  })

  return {
    categorySwitchSectionRef,
    categories,
    categoryArticles,
    activeCategory,
    pendingCategoryId,
    loadingCategories,
    loadingArticles,
    articlePage,
    articlePageSize,
    articleTotal,
    articleTotalPages,
    categoryFoldExpandLabel,
    isDetailMode,
    categoryContextStyle,
    categoryHeroStatTiles,
    articleStageCopy,
    getCategorySwitchScrollAnchor,
    selectCategory,
    resetCategoryView,
    handleArticlePageChange,
    goToArticle
  }
}
