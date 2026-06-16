import { computed, ref, watch } from 'vue'
import { collectTreehole, createTreehole, deleteTreehole, getTreeholes, uncollectTreehole, getTreeholeBarrageList } from '@/modules/treehole/api/treehole.api'
import type { TreeholeBarrageItem, TreeholeCardItem } from '@/modules/treehole/model/treehole.types'
import { likeTarget, unlikeTarget } from '@/shared/api/interaction.api'
import { useContentComments } from '@/shared/composables/useContentComments'
import { useSMessage } from '@/shared/composables/useSMessage'
import { resolveRequestErrorMessage } from '@/shared/api/error-message'
import { useUserStore } from '@/stores/user'
import type { FrontUserDetailVO } from '@/modules/user/api/user.api'
import defaultAvatar from '@/assets/images/logo/logo.png'

interface TreeholeResponseItem {
  id?: number
  userId?: number | null
  content?: string
  likeCount?: number
  collectCount?: number
  commentCount?: number
  likedByMe?: boolean
  collectedByMe?: boolean
  createTime?: string
  userName?: string
  userNickname?: string
  nickname?: string
  avatar?: string
  userAvatar?: string
  user?: {
    nickname?: string
    avatar?: string
  }
}

function asUserInfo(value: unknown): FrontUserDetailVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserDetailVO
  }
  return {}
}

function toNumber(value: unknown, fallback = 0) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

export function useTreeholePage() {
  const message = useSMessage()
  const userStore = useUserStore()
  const draft = ref('')
  const treeholes = ref<TreeholeCardItem[]>([])
  const barrageList = ref<TreeholeBarrageItem[]>([])
  const loading = ref(false)
  const submitting = ref(false)
  const activeTreeholeId = ref<number | null>(null)
  const treeholePage = ref(1)
  const treeholePageSize = 12
  const treeholeTotal = ref(0)
  const treeholeTotalPages = computed(() =>
    treeholeTotal.value > 0 ? Math.ceil(treeholeTotal.value / treeholePageSize) : 0
  )

  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const currentUserId = computed(() => {
    const info = asUserInfo(userStore.userInfo)
    return toNumber(info.id, 0) || undefined
  })
  const currentUserAvatar = computed(() => {
    const info = asUserInfo(userStore.userInfo)
    return String(info.avatar || defaultAvatar)
  })

  const activeCommentTargetId = computed(() => activeTreeholeId.value || 0)
  const commentAnonymousLabel = computed(() => (isLoggedIn.value ? '洞友' : '匿名洞友'))
  const commentApi = useContentComments({
    targetId: activeCommentTargetId,
    source: 'treehole',
    currentUserId,
    isLoggedIn,
    onRequireLogin: () => undefined,
    isAnonymousAllowed: true,
    enableLike: true,
    enableReply: true,
    anonymousLabel: commentAnonymousLabel
  })

  const normalizeTreehole = (item: TreeholeResponseItem): TreeholeCardItem => ({
    id: toNumber(item.id),
    userId: item.userId == null ? undefined : toNumber(item.userId),
    content: item.content?.trim() || '',
    createTime: item.createTime,
    likeCount: toNumber(item.likeCount),
    collectCount: toNumber(item.collectCount),
    commentCount: toNumber(item.commentCount),
    likedByMe: Boolean(item.likedByMe),
    collectedByMe: Boolean(item.collectedByMe),
    nickname: item.userNickname || item.nickname || item.userName || item.user?.nickname,
    avatar: item.avatar || item.userAvatar || item.user?.avatar || defaultAvatar
  })

  const barrageNicknameFallback = () => (isLoggedIn.value ? '洞友' : '匿名洞友')

  const createBarrageItem = (item: TreeholeCardItem): TreeholeBarrageItem => ({
    id: item.id,
    msg: item.content,
    avatar: item.avatar || defaultAvatar,
    nickname: item.nickname || barrageNicknameFallback(),
    time: Math.floor(Math.random() * 5 + 5)
  })

  const loadBarrages = async () => {
    try {
      const response = await getTreeholeBarrageList()
      if (Array.isArray(response)) {
        barrageList.value = response.map((item) => ({
          id: item.id,
          msg: item.msg,
          avatar: item.avatar || defaultAvatar,
          nickname: item.nickname || barrageNicknameFallback(),
          time: Math.floor(Math.random() * 5 + 5)
        }))
      }
    } catch (error) {
      console.error('加载弹幕失败:', error)
      const fb = barrageNicknameFallback()
      barrageList.value = [
        { id: 'default-1', msg: '欢迎来到树洞', avatar: defaultAvatar, nickname: fb, time: 8 },
        { id: 'default-2', msg: '留下你的片刻低语', avatar: defaultAvatar, nickname: fb, time: 9 }
      ]
    }
  }

  const loadTreeholes = async (pageToLoad = treeholePage.value) => {
    loading.value = true
    try {
      const response = await getTreeholes({ page: pageToLoad, pageSize: treeholePageSize })
      treeholePage.value = pageToLoad
      treeholeTotal.value = Number(response.total ?? 0)
      treeholes.value = response.items.map((item) => normalizeTreehole(item as TreeholeResponseItem))
    } catch (error) {
      console.error('加载树洞失败:', error)
      treeholes.value = []
      treeholeTotal.value = 0
    } finally {
      loading.value = false
    }
  }

  const syncCommentCount = () => {
    if (!activeTreeholeId.value) return
    const current = treeholes.value.find((item) => item.id === activeTreeholeId.value)
    if (current) {
      current.commentCount = commentApi.commentTotal.value
    }
  }

  const submitTreehole = async () => {
    const content = draft.value.trim()
    if (!content) {
      message.warning('树洞内容不能为空')
      return
    }
    if (content.length > 500) {
      message.warning('树洞内容不能超过 500 字')
      return
    }

    submitting.value = true
    try {
      const newId = await createTreehole({ content })

      // 本地迅速插播一条优先弹幕，给用户以即时反馈
      const localBarrageItem: TreeholeBarrageItem = {
        id: `local-${newId || Date.now()}`,
        msg: content,
        avatar: currentUserAvatar.value,
        nickname: isLoggedIn.value ? '你' : '匿名洞友',
        time: 5
      }
      barrageList.value = [localBarrageItem, ...barrageList.value]

      draft.value = ''
      await loadTreeholes()
      message.success('树洞已投递')
    } catch (error) {
      console.error('发布树洞失败:', error)
      message.error(resolveRequestErrorMessage(error, '发布树洞失败，请稍后再试'))
    } finally {
      submitting.value = false
    }
  }

  const handleTreeholeLike = async (treehole: TreeholeCardItem) => {
    try {
      if (treehole.likedByMe) {
        await unlikeTarget('treehole', treehole.id)
        treehole.likedByMe = false
        treehole.likeCount = Math.max(Number(treehole.likeCount || 0) - 1, 0)
        message.success('已取消点赞')
        return
      }
      await likeTarget('treehole', treehole.id)
      treehole.likedByMe = true
      treehole.likeCount = Number(treehole.likeCount || 0) + 1
      message.success('点赞成功')
    } catch (error) {
      console.error('切换树洞点赞失败:', error)
      message.error(resolveRequestErrorMessage(error, '点赞操作失败，请稍后再试'))
    }
  }

  const handleTreeholeCollect = async (treehole: TreeholeCardItem) => {
    try {
      if (treehole.collectedByMe) {
        await uncollectTreehole(treehole.id)
        treehole.collectedByMe = false
        treehole.collectCount = Math.max(Number(treehole.collectCount || 0) - 1, 0)
        message.success('已取消收藏')
        return
      }
      await collectTreehole(treehole.id)
      treehole.collectedByMe = true
      treehole.collectCount = Number(treehole.collectCount || 0) + 1
      message.success('已加入收藏')
    } catch (error) {
      console.error('切换树洞收藏失败:', error)
      message.error(resolveRequestErrorMessage(error, '收藏操作失败，请稍后再试'))
    }
  }

  const handleTreeholeDelete = async (treehole: TreeholeCardItem) => {
    const viewerUserId = currentUserId.value
    if (!viewerUserId || !treehole.userId || viewerUserId !== treehole.userId) {
      message.warning('仅可删除自己的树洞')
      return
    }
    try {
      await deleteTreehole(treehole.id)
      message.success('树洞已删除')
      if (activeTreeholeId.value === treehole.id) {
        activeTreeholeId.value = null
        commentApi.cancelReply()
      }
      const fallbackPage = treeholes.value.length === 1 && treeholePage.value > 1
        ? treeholePage.value - 1
        : treeholePage.value
      await loadTreeholes(fallbackPage)
    } catch (error) {
      console.error('删除树洞失败:', error)
      message.error(resolveRequestErrorMessage(error, '删除树洞失败，请稍后再试'))
    }
  }

  const toggleComments = async (treehole: TreeholeCardItem) => {
    if (activeTreeholeId.value === treehole.id) {
      activeTreeholeId.value = null
      commentApi.cancelReply()
      return
    }

    activeTreeholeId.value = treehole.id
    await commentApi.switchCommentMode('full', { resetPage: false, reload: true })
    syncCommentCount()
  }

  const handleTreeholePageChange = async (pageToLoad: number) => {
    if (pageToLoad === treeholePage.value || pageToLoad < 1) {
      return
    }

    activeTreeholeId.value = null
    commentApi.cancelReply()
    await loadTreeholes(pageToLoad)
  }

  watch(commentApi.commentTotal, syncCommentCount)

  watch(isLoggedIn, () => {
    void loadBarrages()
  })

  return {
    draft,
    treeholes,
    barrageList,
    loading,
    submitting,
    activeTreeholeId,
    treeholePage,
    treeholePageSize,
    treeholeTotal,
    treeholeTotalPages,
    isLoggedIn,
    currentUserId,
    currentUserAvatar,
    comments: commentApi.threadedComments,
    commentLoading: commentApi.commentLoading,
    commentTotal: commentApi.commentTotal,
    commentPageNum: commentApi.commentPageNum,
    commentPageSize: commentApi.commentPageSize,
    newComment: commentApi.newComment,
    commentPlaceholder: commentApi.commentPlaceholder,
    commentSubmitting: commentApi.commentSubmitting,
    replyCommentId: commentApi.replyCommentId,
    enableLike: commentApi.enableLike,
    enableReply: commentApi.enableReply,
    commentAnonymousLabel,
    loadTreeholes,
    loadBarrages,
    submitTreehole,
    handleTreeholeLike,
    handleTreeholeCollect,
    handleTreeholeDelete,
    handleTreeholePageChange,
    toggleComments,
    handleCommentLike: commentApi.handleCommentLike,
    handleCommentDelete: commentApi.handleCommentDelete,
    handleReply: commentApi.handleReply,
    cancelReply: commentApi.cancelReply,
    submitComment: commentApi.submitComment,
    handleCommentPageChange: commentApi.handleCommentPageChange,
    switchCommentMode: commentApi.switchCommentMode,
    onCommentInteractionBlocked: commentApi.onCommentInteractionBlocked
  }
}
