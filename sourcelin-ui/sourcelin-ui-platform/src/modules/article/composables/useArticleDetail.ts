import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { collectTarget, likeTarget, uncollectTarget, unlikeTarget } from '@/shared/api/interaction.api'
import { followUser, unfollowUser } from '@/modules/user/api/follow.api'
import {
  getArticleDetail,
  viewArticle,
  type ArticleDetail as ArticleDetailPayload,
  type ArticleDetailTag,
  type ArticleDetailUser
} from '@/modules/article/api/article.api'
import type { FrontUserDetailVO } from '@/modules/user/api/user.api'
import { getToken } from '@/shared/utils/auth'
import { resolveRequestErrorMessage } from '@/shared/api/error-message'
import { useSMessage } from '@/shared/composables/useSMessage'
import { useSeoHead } from '@/shared/composables/useSeoHead'
import { useUserStore } from '@/stores/user'
import defaultCover from '@/assets/images/backgrounds/home-bg.jpg'
import defaultAvatar from '@/assets/images/logo/logo.png'

export type ArticleUser = ArticleDetailUser
export type ArticleTag = ArticleDetailTag

function toUserInfo(value: unknown): FrontUserDetailVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserDetailVO
  }
  return {}
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function toBoolean(value: unknown): boolean {
  return Boolean(value)
}

export function useArticleDetail(articleId: Ref<number>) {
  const router = useRouter()
  const message = useSMessage()
  const userStore = useUserStore()

  const article = ref<ArticleDetailPayload>({})
  const loading = ref(false)
  const error = ref('')

  const isLiked = ref(false)
  const isCollected = ref(false)
  const isFollowed = ref(false)
  const likeCount = ref(0)
  const collectCount = ref(0)
  const followId = ref<number | null>(null)

  const toc = ref<{ id: string; text: string; level: number }[]>([])
  const activeHeading = ref<string | null>(null)

  const isLoggedIn = computed(() => Boolean(getToken()))
  const currentUserId = computed(() => {
    const info = toUserInfo(userStore.userInfo)
    return toNumber(info.id, 0) || undefined
  })
  const currentUserAvatar = computed(() => {
    const info = toUserInfo(userStore.userInfo)
    return String(info.avatar || defaultAvatar)
  })

  const articleUser = computed(() => article.value.user)
  const articleTags = computed(() => (Array.isArray(article.value.tags) ? article.value.tags : []))
  const articleTitle = computed(() => article.value.title || '文章详情')
  const articleSummary = computed(() => article.value.summary || '这是一篇值得慢慢读完的文章。')
  const coverImage = computed(() => article.value.avatar || defaultCover)
  const authorName = computed(() => articleUser.value?.nickname || '匿名作者')
  const authorIntroduction = computed(() => articleUser.value?.introduction || '记录技术、写作与长期主义。')
  const articleIdentityLabel = computed(() => (article.value.isOriginal === 0 ? '转载' : '原创'))
  const heroKicker = computed(() => `${articleIdentityLabel.value}专栏`)

  // 文章详情页 SEO：动态注入 TDK + Open Graph
  const articleOgImage = computed(() => {
    const cover = article.value.avatar
    if (!cover) return ''
    // 如果封面是相对路径则保持，绝对 URL 直接使用
    if (cover.startsWith('http')) return cover
    return typeof window !== 'undefined' ? `${window.location.origin}${cover}` : cover
  })

  const articleCanonicalUrl = computed(() => {
    if (typeof window === 'undefined') return ''
    return `${window.location.origin}/article/${articleId.value}`
  })

  useSeoHead({
    title: articleTitle,
    description: articleSummary,
    ogImage: articleOgImage,
    canonicalUrl: articleCanonicalUrl,
    ogType: computed(() => (article.value.title ? 'article' : 'website')),
    publishedTime: computed(() => article.value.createTime || '')
  })

  const formatDate = (value?: string) => (value ? value.split(' ')[0] : '')
  const issueLabel = computed(() => {
    const date = formatDate(article.value.createTime)
    return date ? `第 ${date.replaceAll('-', '.')} 期` : '第 01 期'
  })

  const stripHtml = (value?: string) => {
    if (!value) return ''
    return value
      .replace(/<style[\s\S]*?>[\s\S]*?<\/style>/gi, ' ')
      .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, ' ')
      .replace(/<[^>]+>/g, ' ')
      .replace(/&nbsp;/gi, ' ')
      .replace(/\s+/g, ' ')
      .trim()
  }

  const articleWordCount = computed(() => stripHtml(article.value.content).length)
  const readingMinutes = computed(() => Math.max(1, Math.ceil(articleWordCount.value / 320)))
  const readingCount = computed(() => toNumber(article.value.viewCount))
  const sectionCount = computed(() => toc.value.length || 1)
  const leadStrip = computed(() => [
    { label: '发布日期', value: formatDate(article.value.createTime) || '未发布' },
    { label: '阅读时长', value: `${readingMinutes.value} 分钟` },
    { label: '章节数量', value: `${sectionCount.value} 节` },
    { label: '阅读热度', value: `${readingCount.value} 次` }
  ])

  const ensureToc = async () => {
    await nextTick()
    const contentEl = document.querySelector('.article-content')
    if (!contentEl) {
      toc.value = []
      return
    }
    const headings = contentEl.querySelectorAll('h1, h2, h3, h4')
    const nextToc: { id: string; text: string; level: number }[] = []
    headings.forEach((heading, index) => {
      const id = `heading-${index}`
      heading.id = id
      nextToc.push({
        id,
        text: heading.textContent?.trim() || '',
        level: Number(heading.tagName.slice(1))
      })
    })
    toc.value = nextToc
  }

  const handleScroll = () => {
    let current: string | null = null
    for (const item of toc.value) {
      const heading = document.getElementById(item.id)
      if (heading && heading.getBoundingClientRect().top <= 140) {
        current = item.id
      }
    }
    activeHeading.value = current
  }

  const reload = async () => {
    if (!articleId.value) return
    loading.value = true
    error.value = ''
    try {
      const payload = await getArticleDetail(articleId.value)
      article.value = {
        ...payload,
        isCollected: toBoolean(payload.isCollected),
        isFollowed: toBoolean(payload.isFollowed),
        followId: toNumber(payload.followId) || undefined,
        isLike: toBoolean(payload.isLike)
      }
      likeCount.value = toNumber(payload.likeCount)
      collectCount.value = toNumber(payload.collectCount)
      isCollected.value = toBoolean(payload.isCollected)
      isFollowed.value = toBoolean(payload.isFollowed)
      followId.value = toNumber(payload.followId) || null
      isLiked.value = toBoolean(payload.isLike)

      await ensureToc()
      await viewArticle(articleId.value).catch(() => undefined)
      article.value.viewCount = toNumber(article.value.viewCount) + 1
    } catch (err) {
      console.error(err)
      error.value = '加载文章失败'
    } finally {
      loading.value = false
    }
  }

  const requireLogin = () => {
    router.push('/login')
  }

  const handleLike = async () => {
    if (!isLoggedIn.value) return requireLogin()
    try {
      if (isLiked.value) {
        await unlikeTarget('article', articleId.value)
        isLiked.value = false
        likeCount.value = Math.max(0, likeCount.value - 1)
        message.success('已取消点赞')
        return
      }
      await likeTarget('article', articleId.value)
      isLiked.value = true
      likeCount.value += 1
      message.success('点赞成功')
    } catch (error) {
      console.error(error)
      message.error(resolveRequestErrorMessage(error, '点赞操作失败，请稍后再试'))
    }
  }

  const handleCollect = async () => {
    if (!isLoggedIn.value) return requireLogin()
    try {
      if (isCollected.value) {
        await uncollectTarget('article', articleId.value)
        isCollected.value = false
        collectCount.value = Math.max(0, collectCount.value - 1)
        message.success('已取消收藏')
        return
      }

      await collectTarget('article', articleId.value)
      isCollected.value = true
      collectCount.value += 1
      message.success('已加入收藏')
    } catch (error) {
      console.error(error)
      message.error(resolveRequestErrorMessage(error, '收藏操作失败，请稍后再试'))
    }
  }

  const followLoading = ref(false)

  const handleFollow = async () => {
    if (!isLoggedIn.value) return requireLogin()
    if (followLoading.value) return
    const authorId = articleUser.value?.id
    if (!authorId) return
    followLoading.value = true
    try {
      if (isFollowed.value && followId.value) {
        await unfollowUser(followId.value)
        isFollowed.value = false
        followId.value = null
        return
      }
      const response = await followUser({ targetUserId: authorId })
      isFollowed.value = true
      followId.value = toNumber(response.followId) || null
    } finally {
      followLoading.value = false
    }
  }

  const handleShare = () => {
    const url = window.location.href
    navigator.clipboard.writeText(url).then(() => {
      message.success('链接已复制')
    }).catch(() => {
      const input = document.createElement('input')
      input.value = url
      document.body.appendChild(input)
      input.select()
      const copied = document.execCommand('copy')
      document.body.removeChild(input)
      if (copied) {
        message.success('链接已复制')
      } else {
        message.error('复制失败，请手动复制链接')
      }
    })
  }

  const goToTag = (tagName: string) => {
    router.push(`/tags?name=${encodeURIComponent(tagName)}`)
  }

  const scrollToHeading = (id: string) => {
    const heading = document.getElementById(id)
    if (!heading) return
    const offset = 112
    const top = heading.getBoundingClientRect().top + window.pageYOffset - offset
    window.scrollTo({ top, behavior: 'smooth' })
  }

  const getTocItemClass = (id: string, level: number) => {
    const classes = [`level-${level}`]
    if (activeHeading.value === id) classes.push('active')
    return classes.join(' ')
  }

  watch(
    articleId,
    () => {
      void reload()
    },
    { immediate: true }
  )

  onMounted(() => {
    window.addEventListener('scroll', handleScroll, { passive: true })
  })

  onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll)
  })

  return {
    article,
    articleUser,
    articleTags,
    articleTitle,
    articleSummary,
    articleIdentityLabel,
    issueLabel,
    heroKicker,
    coverImage,
    authorName,
    authorIntroduction,
    currentUserId,
    currentUserAvatar,
    isLoggedIn,
    isLiked,
    isCollected,
    isFollowed,
    likeCount,
    collectCount,
    toc,
    activeHeading,
    readingCount,
    readingMinutes,
    articleWordCount,
    sectionCount,
    leadStrip,
    loading,
    error,
    formatDate,
    reload,
    handleLike,
    handleCollect,
    handleFollow,
    handleShare,
    goToTag,
    scrollToHeading,
    getTocItemClass
  }
}
