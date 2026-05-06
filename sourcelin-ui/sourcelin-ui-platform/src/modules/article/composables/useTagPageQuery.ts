import type { Component } from 'vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTagArticles, getTags, type TagOption } from '@/modules/article/api/tag.api'
import type { ArticleListItem } from '@/modules/article/api/article.api'
import { buildTagArticleRoute } from '@/modules/article/utils/article-source'
import { useThemePalette } from '@/shared/composables/useThemePalette'
import { appIcons } from '@/shared/components/ui/icons'

export interface TagItem {
  id: number
  name: string
  description: string
  articleCount: number
  views: number
  weight: number
  color: string
}

export interface TagArticle {
  id: number
  title: string
  summary: string
  cover: string
  date: string
  views: number
  comments: number
  authorName: string
  tags: string[]
}

export interface TagHeroStatTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
}

function normalizeDate(value: unknown): string {
  const text = typeof value === 'string' ? value : ''
  return text ? text.split(' ')[0] : ''
}

function normalizeTags(value: unknown): string[] {
  return String(value || '')
    .split(/[;,，、\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

export function useTagPageQuery() {
  const route = useRoute()
  const router = useRouter()
  const palette = useThemePalette()

  const loadingTags = ref(false)
  const loadingArticles = ref(false)
  const tags = ref<TagItem[]>([])
  const tagArticles = ref<TagArticle[]>([])
  const activeTag = ref<TagItem | null>(null)
  const pendingTagId = ref<number | null>(null)
  const articlePage = ref(1)
  const articlePageSize = 12
  const articleTotal = ref(0)
  const tagSwitchSectionRef = ref<HTMLElement | null>(null)

  const articleTotalPages = computed(() =>
    articleTotal.value > 0 ? Math.ceil(articleTotal.value / articlePageSize) : 0
  )
  const tagGridFoldExpandLabel = computed(() =>
    tags.value.length > 0 ? `展开全部 ${tags.value.length} 个标签` : '展开全部标签'
  )
  const queryTagName = computed(() => (typeof route.query.name === 'string' ? route.query.name.trim() : ''))
  const totalArticles = computed(() => tags.value.reduce((sum, tag) => sum + tag.articleCount, 0))
  const sortedTags = computed(() => [...tags.value].sort((left, right) => right.weight - left.weight))
  const featuredTag = computed(() => activeTag.value ?? sortedTags.value[0] ?? null)
  const isDetailMode = computed(() => activeTag.value !== null)
  const currentPanelEyebrow = computed(() => (activeTag.value ? '当前标签' : '推荐标签'))
  const contextTag = computed(() =>
    activeTag.value ??
    tags.value.find((item) => item.id === pendingTagId.value) ??
    featuredTag.value
  )
  const tagContextStyle = computed(() => ({
    '--context-accent': contextTag.value?.color || 'var(--primary-color)'
  }))
  const tagHeroStatTiles = computed((): TagHeroStatTile[] => {
    const current = featuredTag.value
    const tagCount = tags.value.length
    const articleSum = totalArticles.value

    return [
      {
        label: '标签总数',
        value: String(tagCount).padStart(2, '0'),
        icon: appIcons.tag,
        hue: 265,
        ariaLabel: `共 ${tagCount} 个标签`
      },
      {
        label: '内容篇数',
        value: String(articleSum).padStart(2, '0'),
        icon: appIcons.document,
        hue: 210,
        ariaLabel: `标签下共 ${articleSum} 篇文章`
      },
      {
        label: '当前热度',
        value: current ? `${current.views}` : '--',
        icon: appIcons.flame,
        hue: 22,
        ariaLabel: current ? `当前标签热度 ${current.views}` : '当前未选择标签'
      }
    ]
  })
  const activeKeywords = computed(() => {
    const current = featuredTag.value
    if (!current) return []
    return [`#${current.name}`, `${current.articleCount} 篇文章`, `${current.views} 热度`]
  })
  const detailStageCopy = computed(() => {
    if (!activeTag.value) return '选择标签后，将在当前区域展示相关文章列表。'
    return resolveTagDescription(activeTag.value)
  })

  function getTagSwitchScrollAnchor() {
    return tagSwitchSectionRef.value
  }

  function normalizeTag(item: TagOption, index: number): TagItem {
    return {
      id: Number(item.id) || index + 1,
      name: String(item.name || '未命名标签'),
      description: String(item.summary || ''),
      articleCount: 0,
      views: Number(item.clickCount || 0),
      weight: Number(item.orderNum || item.clickCount || index + 1),
      color: palette.value[index % palette.value.length]
    }
  }

  function normalizeArticle(item: ArticleListItem): TagArticle {
    return {
      id: Number(item.id) || 0,
      title: String(item.title || '未命名文章'),
      summary: String(item.summary || '这篇文章暂时还没有摘要。'),
      cover: String(item.avatar || ''),
      date: normalizeDate(item.createTime),
      views: Number(item.viewCount || 0),
      comments: Number(item.commentCount || 0),
      authorName: String(item.author || '平台作者'),
      tags: normalizeTags(item.tagNames)
    }
  }

  function resolveTagDescription(tag: TagItem): string {
    if (tag.description) return tag.description
    if (tag.articleCount > 0) return `已为“${tag.name}”整理相关内容，继续下滑即可浏览。`
    return `“${tag.name}”标签已建立，内容正在持续补充。`
  }

  function findTagByName(name: string): TagItem | null {
    if (!name) return null
    const normalized = name.toLowerCase()
    return tags.value.find((tag) => tag.name.toLowerCase() === normalized) ?? null
  }

  async function loadTags() {
    loadingTags.value = true
    try {
      const response = await getTags()
      tags.value = response.map((item, index) => normalizeTag(item, index))
    } catch (error) {
      console.error('加载标签失败:', error)
      tags.value = []
    } finally {
      loadingTags.value = false
    }
  }

  async function loadArticlesForTag(tagId: number) {
    loadingArticles.value = true
    try {
      const response = await getTagArticles(tagId, {
        page: articlePage.value,
        pageSize: articlePageSize
      })
      articleTotal.value = Number(response.total ?? 0)
      tagArticles.value = response.items.map((item) => normalizeArticle(item))
    } catch (error) {
      console.error('加载标签文章失败:', error)
      tagArticles.value = []
      articleTotal.value = 0
    } finally {
      loadingArticles.value = false
    }
  }

  async function applyActiveTag(tag: TagItem | null) {
    if (!tag) {
      activeTag.value = null
      tagArticles.value = []
      pendingTagId.value = null
      articlePage.value = 1
      articleTotal.value = 0
      return
    }
    if (activeTag.value?.id === tag.id && tagArticles.value.length) return
    activeTag.value = tag
    pendingTagId.value = tag.id
    articlePage.value = 1
    await loadArticlesForTag(tag.id)
  }

  async function syncTagFromRoute() {
    const matched = findTagByName(queryTagName.value)
    if (!matched) {
      await applyActiveTag(null)
      return
    }
    if (queryTagName.value !== matched.name) {
      await router.replace({ path: '/tags', query: { name: matched.name } })
      return
    }
    await applyActiveTag(matched)
  }

  async function selectTag(tag: TagItem) {
    pendingTagId.value = tag.id
    if (queryTagName.value === tag.name) {
      await applyActiveTag(tag)
      return
    }
    await router.push({ path: '/tags', query: { name: tag.name } })
  }

  function resetTagView() {
    pendingTagId.value = null
    void router.push('/tags')
  }

  function scrollTagSwitchIntoView() {
    const anchor = getTagSwitchScrollAnchor()
    if (!(anchor instanceof HTMLElement)) return
    const top = anchor.getBoundingClientRect().top + window.scrollY - 88
    window.scrollTo({
      top: Math.max(top, 0),
      behavior: 'smooth'
    })
  }

  async function handleArticlePageChange(page: number) {
    if (!activeTag.value || page === articlePage.value || page < 1) return
    articlePage.value = page
    await loadArticlesForTag(activeTag.value.id)
    scrollTagSwitchIntoView()
  }

  function goToArticle(id: number) {
    const name = activeTag.value?.name
    if (!name) {
      void router.push(`/article/${id}`)
      return
    }
    void router.push(buildTagArticleRoute(id, name))
  }

  watch(
    () => route.query.name,
    () => {
      if (tags.value.length) void syncTagFromRoute()
    }
  )

  onMounted(async () => {
    await loadTags()
    await syncTagFromRoute()
  })

  return {
    loadingTags,
    loadingArticles,
    tags,
    tagArticles,
    activeTag,
    pendingTagId,
    articlePage,
    articlePageSize,
    articleTotal,
    tagSwitchSectionRef,
    articleTotalPages,
    tagGridFoldExpandLabel,
    queryTagName,
    sortedTags,
    featuredTag,
    isDetailMode,
    currentPanelEyebrow,
    tagContextStyle,
    tagHeroStatTiles,
    activeKeywords,
    detailStageCopy,
    getTagSwitchScrollAnchor,
    resolveTagDescription,
    selectTag,
    resetTagView,
    handleArticlePageChange,
    goToArticle
  }
}
