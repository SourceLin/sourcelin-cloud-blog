import { useHead, useSeoMeta } from '@unhead/vue'
import { computed, isRef, type ComputedRef, type MaybeRef } from 'vue'
import { useSiteStore } from '@/stores/site.store'

type OgType = 'website' | 'article' | 'book' | 'profile'
  | 'music.song' | 'music.album' | 'music.playlist' | 'music.radio_status'
  | 'video.movie' | 'video.episode' | 'video.tv_show' | 'video.other'

export interface SeoHeadOptions {
  /** 页面标题，最终格式：{title} - {siteName} */
  title: MaybeRef<string>
  /** 页面描述 */
  description?: MaybeRef<string>
  /** Open Graph 图片绝对 URL */
  ogImage?: MaybeRef<string>
  /** 当前页面绝对 URL，用于 og:url 和 canonical */
  canonicalUrl?: MaybeRef<string>
  /** og:type，默认为 website */
  ogType?: MaybeRef<string>
  /** 文章发布时间（ISO 字符串），用于 article:published_time */
  publishedTime?: MaybeRef<string>
}

function resolveRef<T>(value: MaybeRef<T>): T {
  return isRef(value) ? value.value : value
}

/**
 * 通用 SEO Head 注入 composable。
 * 在页面组件或 composable 中调用，自动更新 <head> 中的 TDK 与 OG 标签。
 */
export function useSeoHead(options: SeoHeadOptions) {
  const siteStore = useSiteStore()

  const siteName = computed(() => siteStore.siteInfo.webName || '圆圈博客')

  const resolvedTitle = computed(() => {
    const pageTitle = resolveRef(options.title)
    if (!pageTitle) return siteName.value
    return `${pageTitle} - ${siteName.value}`
  })

  const resolvedDescription = computed(() => {
    const desc = options.description ? resolveRef(options.description) : ''
    return desc || `${siteName.value} - 记录技术、写作与长期主义`
  })

  const resolvedOgImage = computed(() => {
    return options.ogImage ? resolveRef(options.ogImage) : ''
  })

  const resolvedCanonicalUrl = computed(() => {
    return options.canonicalUrl
      ? resolveRef(options.canonicalUrl)
      : typeof window !== 'undefined'
        ? window.location.href
        : ''
  })

  const resolvedOgType = computed((): OgType => {
    const val = options.ogType ? resolveRef(options.ogType) : 'website'
    const validTypes: OgType[] = [
      'website', 'article', 'book', 'profile',
      'music.song', 'music.album', 'music.playlist', 'music.radio_status',
      'video.movie', 'video.episode', 'video.tv_show', 'video.other'
    ]
    return (validTypes.includes(val as OgType) ? val : 'website') as OgType
  })

  const resolvedPublishedTime = computed(() => {
    return options.publishedTime ? resolveRef(options.publishedTime) : ''
  })

  // title 与 canonical link
  useHead(
    computed(() => ({
      title: resolvedTitle.value,
      link: resolvedCanonicalUrl.value
        ? [{ rel: 'canonical' as const, href: resolvedCanonicalUrl.value }]
        : []
    }))
  )

  // SEO meta + Open Graph（useSeoMeta 提供完整类型安全支持）
  useSeoMeta({
    description: resolvedDescription,
    ogTitle: resolvedTitle,
    ogDescription: resolvedDescription,
    ogType: resolvedOgType,
    ogSiteName: siteName,
    ogUrl: resolvedCanonicalUrl,
    ogImage: resolvedOgImage,
    twitterCard: 'summary_large_image' as const,
    twitterTitle: resolvedTitle,
    twitterDescription: resolvedDescription,
    twitterImage: resolvedOgImage,
    articlePublishedTime: resolvedPublishedTime
  })
}
