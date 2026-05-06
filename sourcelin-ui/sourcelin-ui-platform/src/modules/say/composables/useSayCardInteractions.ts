import type { Ref } from 'vue'
import { computed, ref, watch } from 'vue'
import type { SayItem } from '@/modules/say/model/say.types'
import type { FrontUserDetailVO } from '@/modules/user/api/user.api'
import { collectTarget, likeTarget, uncollectTarget, unlikeTarget } from '@/shared/api/interaction.api'
import { useSMessage } from '@/shared/composables/useSMessage'
import { useUserStore } from '@/stores/user'

interface UseSayCardInteractionsOptions {
  item: Ref<SayItem>
  emitDelete: (sayId: number) => void
}

function asUserInfo(value: unknown): FrontUserDetailVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserDetailVO
  }
  return {}
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

export function useSayCardInteractions(options: UseSayCardInteractionsOptions) {
  const userStore = useUserStore()
  const message = useSMessage()

  const likeCount = ref(0)
  const collectCount = ref(0)
  const commentCount = ref(0)
  const liked = ref(false)
  const collected = ref(false)

  const currentUserId = computed(() => {
    const info = asUserInfo(userStore.userInfo)
    const id = toNumber(info.id, 0)
    return id || undefined
  })

  watch(
    () => [
      options.item.value.likeCount,
      options.item.value.collectCount,
      options.item.value.commentCount,
      options.item.value.likedByMe,
      options.item.value.collectedByMe,
      options.item.value.liked,
      options.item.value.isCollected
    ] as const,
    ([nextLikeCount, nextCollectCount, nextCommentCount, nextLikedByMe, nextCollectedByMe, nextLiked, nextCollected]) => {
      likeCount.value = Number(nextLikeCount ?? 0)
      collectCount.value = Number(nextCollectCount ?? 0)
      commentCount.value = Number(nextCommentCount ?? 0)
      liked.value = Boolean(nextLikedByMe ?? nextLiked)
      collected.value = Boolean(nextCollectedByMe ?? nextCollected)
    },
    { immediate: true }
  )

  const likedIconColor = computed(() =>
    liked.value ? 'var(--error-color)' : 'var(--text-secondary)'
  )
  const collectedIconColor = computed(() =>
    collected.value ? 'var(--warning-color)' : 'var(--text-secondary)'
  )

  function spawnHeartParticles(event: MouseEvent) {
    const target = event.currentTarget as HTMLElement
    const rect = target.getBoundingClientRect()
    const count = 4 + Math.floor(Math.random() * 2)
    for (let i = 0; i < count; i++) {
      const el = document.createElement('span')
      el.className = 'say-heart-particle'
      el.setAttribute('aria-hidden', 'true')
      el.textContent = '❤'
      const dx = (Math.random() - 0.5) * 36
      el.style.setProperty('--dx', `${dx}px`)
      el.style.left = `${rect.left + rect.width / 2 - 6}px`
      el.style.top = `${rect.top + rect.height / 2 - 8}px`
      document.body.appendChild(el)
      window.setTimeout(() => el.remove(), 900)
    }
  }

  async function toggleLike(event: MouseEvent) {
    if (!userStore.isLoggedIn) {
      message.warning('请先登录后再点赞')
      return
    }
    try {
      if (liked.value) {
        await unlikeTarget('say', options.item.value.id)
        liked.value = false
        likeCount.value = Math.max(0, likeCount.value - 1)
        message.success('已取消点赞')
        return
      }
      spawnHeartParticles(event)
      await likeTarget('say', options.item.value.id)
      liked.value = true
      likeCount.value += 1
      message.success('点赞成功')
    } catch (error) {
      console.error('点赞失败', error)
      message.error('点赞失败')
    }
  }

  async function toggleCollect() {
    if (!userStore.isLoggedIn) {
      message.warning('请先登录后再收藏')
      return
    }
    try {
      if (collected.value) {
        await uncollectTarget('say', options.item.value.id)
        collected.value = false
        collectCount.value = Math.max(collectCount.value - 1, 0)
        message.success('已取消收藏')
        return
      }
      await collectTarget('say', options.item.value.id)
      collected.value = true
      collectCount.value += 1
      message.success('已加入收藏')
    } catch (error) {
      console.error('收藏失败', error)
      message.error('收藏失败')
    }
  }

  function handleDeleteConfirm() {
    options.emitDelete(options.item.value.id)
  }

  return {
    userStore,
    likeCount,
    collectCount,
    commentCount,
    liked,
    collected,
    currentUserId,
    likedIconColor,
    collectedIconColor,
    toggleLike,
    toggleCollect,
    handleDeleteConfirm
  }
}
