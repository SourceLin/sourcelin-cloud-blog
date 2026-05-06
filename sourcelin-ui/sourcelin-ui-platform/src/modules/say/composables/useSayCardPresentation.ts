import type { Component, Ref } from 'vue'
import { computed } from 'vue'
import { ImageOutline, ImagesOutline, LanguageOutline } from '@vicons/ionicons5'
import defaultAvatar from '@/assets/images/logo/logo.png'
import type { SayItem } from '@/modules/say/model/say.types'
import { useFeedRelativeTimeNow } from '@/shared/composables/useFeedRelativeTimeNow'
import { useFileUrl } from '@/shared/composables/useFileUrl'
import { formatPublishRelativeTime } from '@/shared/utils/formatPublishRelativeTime'
import { useUserStore } from '@/stores/user'

export type SayCardVariant = 'text-only' | 'single-image' | 'multi-image'

interface UseSayCardPresentationOptions {
  item: Ref<SayItem>
  currentUserId: Ref<number | undefined>
}

export function formatSayContent(text: string) {
  const safeText = text ?? ''
  return safeText
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/\n/g, '<br>')
}

export function useSayCardPresentation(options: UseSayCardPresentationOptions) {
  const userStore = useUserStore()
  const feedRelativeNow = useFeedRelativeTimeNow()
  const { resolveImageUrls } = useFileUrl()

  const modalUserNickname = computed(() => options.item.value.user?.nickname || '匿名用户')
  const currentUserAvatar = computed(
    () => (userStore.userInfo as { avatar?: string } | null)?.avatar || defaultAvatar
  )
  const imageArray = computed(() => resolveImageUrls(options.item.value))
  const galleryImages = computed(() => imageArray.value.slice(0, 5))
  const extraImageCount = computed(() => Math.max(imageArray.value.length - 5, 0))
  const cardVariant = computed<SayCardVariant>(() => {
    if (imageArray.value.length === 0) return 'text-only'
    if (imageArray.value.length === 1) return 'single-image'
    return 'multi-image'
  })
  const cardVariantLabel = computed(() => {
    switch (cardVariant.value) {
      case 'single-image':
        return '影像'
      case 'multi-image':
        return '图集'
      default:
        return '文字'
    }
  })
  const cardVariantIcon = computed<Component>(() => {
    switch (cardVariant.value) {
      case 'single-image':
        return ImageOutline
      case 'multi-image':
        return ImagesOutline
      default:
        return LanguageOutline
    }
  })
  const publishRelativeLabel = computed(() =>
    formatPublishRelativeTime(options.item.value.createTime, feedRelativeNow.value)
  )
  const canDeleteSay = computed(() =>
    options.currentUserId.value != null
    && options.item.value.userId != null
    && options.currentUserId.value === options.item.value.userId
  )
  const formattedContent = computed(() => formatSayContent(options.item.value.content))

  return {
    modalUserNickname,
    currentUserAvatar,
    imageArray,
    galleryImages,
    extraImageCount,
    cardVariant,
    cardVariantLabel,
    cardVariantIcon,
    publishRelativeLabel,
    canDeleteSay,
    formattedContent
  }
}
