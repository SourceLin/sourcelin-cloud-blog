import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getHome, type HomeData, type HomeSiteInfoVO } from '@/modules/article/api/article.api'
import type { TagOption } from '@/modules/article/api/tag.api'
import lightBg from '@/assets/images/backgrounds/home-bg.jpg'
import darkBg from '@/assets/images/backgrounds/home-bg.jpg'
import { useSiteStore } from '@/stores/site.store'
import { useThemeStore } from '@/stores/theme'
import { useUiStore } from '@/stores/ui.store'
import { useMastheadScrollBlur } from '@/shared/composables/useMastheadScrollBlur'
import { useThemePalette } from '@/shared/composables/useThemePalette'
import { useSMessage } from '@/shared/composables/useSMessage'
import { getMessages } from '@/modules/notice/api/notice.api'
import {
  normalizeHomeArticles,
  normalizeHomeCategories,
  type HomeArticle,
  type HomeCategory
} from '@/modules/home/model/home-discover'

interface HomeTag {
  id: number
  name: string
  weight: number
  color: string
}

function asSiteInfo(value: unknown): HomeSiteInfoVO {
  if (typeof value === 'object' && value !== null) {
    return value as HomeSiteInfoVO
  }
  return {}
}

function toString(value: unknown, fallback = ''): string {
  return typeof value === 'string' ? value : fallback
}

function splitTextList(value: unknown): string[] {
  if (typeof value !== 'string') {
    return []
  }
  return value
    .split(/[，,\n]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

export function useHomePage() {
  const router = useRouter()
  const siteStore = useSiteStore()
  const themeStore = useThemeStore()
  const uiStore = useUiStore()
  const themePalette = useThemePalette()
  const message = useSMessage()
  const homeLoading = ref(false)
  const { blurPx: mastheadBlurValue } = useMastheadScrollBlur()
  const printerInfo = ref('')

  const siteInfo = ref({
    siteName: siteStore.siteInfo.siteName || siteStore.siteInfo.webName || '',
    logo: siteStore.siteInfo.logo || '',
    description: siteStore.siteInfo.footer || ''
  })

  const carouselItems = ref<HomeArticle[]>([])
  const recommendedArticles = ref<HomeArticle[]>([])
  const categories = ref<HomeCategory[]>([])
  const popularTags = ref<HomeTag[]>([])
  const backgroundImage = ref(lightBg)
  const homeNotices = ref<string[]>([])
  const homeAnnouncementNotices = ref<string[]>([])

  const notices = computed(() => {
    if (homeAnnouncementNotices.value.length) {
      return homeAnnouncementNotices.value
    }
    if (homeNotices.value.length) {
      return homeNotices.value
    }
    return siteStore.siteInfo.notices?.filter(Boolean) || []
  })

  const displayCarousel = computed(() => carouselItems.value)
  const displayRecommended = computed(() => recommendedArticles.value)
  const displayCategories = computed(() => categories.value)
  const displayTags = computed(() => popularTags.value)

  const applyThemeBackground = (theme: string) => {
    backgroundImage.value = theme === 'dark' ? darkBg : lightBg
  }

  const normalizeTags = (list: TagOption[] | undefined): HomeTag[] => {
    if (!Array.isArray(list)) {
      return []
    }

    const palette = themePalette.value

    return list.map((item, index) => {
      const rawWeight = Number(item.clickCount || item.orderNum || 1)
      return {
        id: Number(item.id) || index + 1,
        name: String(item.name || 'Tag'),
        weight: Number.isFinite(rawWeight) && rawWeight > 0 ? rawWeight : 1,
        color: palette[index % palette.length]
      }
    })
  }

  const getGuShi = () => {
    const xhr = new XMLHttpRequest()
    xhr.open('get', 'https://v1.jinrishici.com/rensheng/shiguang')
    xhr.onreadystatechange = function() {
      if (xhr.readyState !== 4) {
        return
      }

      try {
        const data = JSON.parse(xhr.responseText) as { content?: string }
        printerInfo.value = data.content || ''
      } catch {
        printerInfo.value = ''
      }
    }
    xhr.send()
  }

  const scrollToContent = () => {
    const contentTarget = document.querySelector('.home-main') ?? document.querySelector('.page-container-wrap')
    contentTarget?.scrollIntoView({ behavior: 'smooth' })
  }

  const loadHome = async (categoryId: number | null = null): Promise<HomeData | null> => {
    try {
      homeLoading.value = true
      const params = { page: 1, pageSize: 10, ...(categoryId == null ? {} : { categoryId }) }
      const data = await getHome(params)
      const info = asSiteInfo(data.siteInfo)
      homeNotices.value = splitTextList(info.notice)
      siteInfo.value = {
        siteName: toString(info.siteName) || toString(info.name) || toString(info.webName) || siteInfo.value.siteName,
        logo: toString(info.logo) || siteInfo.value.logo,
        description: toString(info.description) || toString(info.summary) || toString(info.footer) || siteInfo.value.description
      }
      carouselItems.value = normalizeHomeArticles(data.carousel)
      recommendedArticles.value = normalizeHomeArticles(data.recommend)
      categories.value = normalizeHomeCategories(data.categories)
      popularTags.value = normalizeTags(data.tags)
      return data
    } catch (error) {
      console.error(error)
      message.warning('首页数据加载失败，请稍后重试')
      return null
    } finally {
      homeLoading.value = false
    }
  }

  const loadHomeAnnouncements = async () => {
    try {
      const rows = await getMessages({ channel: 'system', page: 1, pageSize: 10 })
      homeAnnouncementNotices.value = (Array.isArray(rows) ? rows : [])
        .map((item) => String(item?.title || item?.content || '').trim())
        .filter(Boolean)
        .slice(0, 8)
    } catch {
      homeAnnouncementNotices.value = []
    }
  }

  const goToArticle = (id: number) => router.push(`/article/${id}`)
  const goToArchive = (categoryId: number | null = null) =>
    router.push(
      categoryId == null
        ? '/archive'
        : { path: '/archive', query: { categoryId: String(categoryId) } }
    )
  const goToTag = (name: string) => router.push(`/tags?name=${encodeURIComponent(name)}`)

  async function initHomePage(categoryId: number | null = null) {
    getGuShi()
    const [homeResult] = await Promise.allSettled([loadHome(categoryId), loadHomeAnnouncements()])
    uiStore.markStartupHomeReady()
    return homeResult.status === 'fulfilled' ? homeResult.value : null
  }

  watch(
    () => themeStore.themeName,
    (themeName) => {
      applyThemeBackground(themeName)
    },
    { immediate: true }
  )

  return {
    homeLoading,
    mastheadBlurValue,
    printerInfo,
    siteInfo,
    backgroundImage,
    notices,
    displayCarousel,
    displayRecommended,
    displayCategories,
    displayTags,
    goToArticle,
    goToArchive,
    goToTag,
    scrollToContent,
    initHomePage
  }
}
