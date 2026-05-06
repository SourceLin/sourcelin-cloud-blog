import { computed, ref, unref, watch, type Ref } from 'vue'
import { addComment, deleteComment, getComments, likeComment, type CommentItem } from '@/shared/api/comment.api'
import { resolveRequestErrorMessage } from '@/shared/api/error-message'
import { createCommentPagingStore, type CommentViewMode } from '@/shared/composables/commentPagingState'
import { useSMessage } from '@/shared/composables/useSMessage'

export interface CommentThreadItem extends CommentItem {
  replies?: CommentThreadItem[]
}

interface UseContentCommentsOptions {
  /** 评论目标 ID（文章 ID / 树洞 ID 等），请求层仍使用 articleId 字段 */
  targetId: Ref<number>
  source: string
  isLoggedIn: Ref<boolean>
  currentUserId?: Ref<number | undefined>
  onRequireLogin: () => void
  /** 未登录时是否允许参与（发评、赞、回复） */
  isAnonymousAllowed?: boolean
  /** 无昵称时的展示文案；可传 Ref 以便随登录态切换 */
  anonymousLabel?: string | Ref<string>
  /** 默认 true；为 false 时由页面隐藏点赞入口 */
  enableLike?: boolean
  /** 默认 true；为 false 时由页面隐藏回复入口 */
  enableReply?: boolean
  /** 列表每页条数，默认 10 */
  initialPageSize?: number
  /** 为不同展示模式配置分页尺寸 */
  pageSizeByMode?: Partial<Record<CommentViewMode, number>>
  /** 初始展示模式，默认 full */
  initialMode?: CommentViewMode
  /** 评论发表成功并已刷新列表后 */
  onCommentSubmitted?: () => void
  /** 评论删除成功并已刷新列表后 */
  onCommentDeleted?: (comment: CommentThreadItem) => void
  /** 加载评论列表失败 */
  onCommentsLoadError?: (error: unknown) => void
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function isCommentApproved(row: Pick<CommentThreadItem, 'status'>): boolean {
  return row.status === undefined || row.status === 1
}

function sortByTime(items: CommentThreadItem[]) {
  return [...items].sort((left, right) => `${left.createTime || ''}`.localeCompare(`${right.createTime || ''}`))
}

function buildThread(items: CommentItem[]) {
  const allItems = items.map((item) => ({ ...item, replies: [] as CommentThreadItem[] }))
  const itemMap = new Map(allItems.map((item) => [item.id, item]))
  const roots: CommentThreadItem[] = []

  for (const item of allItems) {
    if (item.parentCommentId && itemMap.has(item.parentCommentId)) {
      let parent = itemMap.get(item.parentCommentId) || null
      while (parent?.parentCommentId && itemMap.has(parent.parentCommentId)) {
        parent = itemMap.get(parent.parentCommentId) || null
      }
      parent?.replies?.push(item)
      continue
    }
    roots.push(item)
  }

  return sortByTime(roots).map((item) => ({
    ...item,
    replies: sortByTime(item.replies || [])
  }))
}

export function useContentComments(options: UseContentCommentsOptions) {
  const message = useSMessage()
  const comments = ref<CommentItem[]>([])
  const commentTotal = ref(0)
  const commentPageNum = ref(1)
  const commentPageSize = ref(options.initialPageSize ?? 10)
  const commentMode = ref<CommentViewMode>(options.initialMode ?? 'full')
  const commentTotalPages = ref(1)
  const commentLoading = ref(false)
  const newComment = ref('')
  const commentSubmitting = ref(false)
  const replyCommentId = ref<number | null>(null)
  const replyUserId = ref<number | null>(null)
  const pagingStore = createCommentPagingStore({
    defaultPageSize: options.initialPageSize ?? 10,
    pageSizeByMode: options.pageSizeByMode
  })

  const enableLike = computed(() => options.enableLike !== false)
  const enableReply = computed(() => options.enableReply !== false)

  const canParticipate = computed(() => options.isLoggedIn.value || Boolean(options.isAnonymousAllowed))
  const commentPlaceholder = computed(() => {
    if (options.isLoggedIn.value) {
      return '写点什么，留下此刻的想法。'
    }
    if (options.isAnonymousAllowed) {
      return '匿名写下你的留言，也可以回复别人的树洞。'
    }
    return '请先登录，再参与评论。'
  })

  const threadedComments = computed(() => buildThread(comments.value))

  function cancelReply() {
    replyCommentId.value = null
    replyUserId.value = null
    newComment.value = ''
  }

  const syncPagingSnapshot = () => {
    const targetId = options.targetId.value
    if (!targetId) {
      commentPageNum.value = 1
      commentPageSize.value = options.initialPageSize ?? 10
      return
    }
    const snapshot = pagingStore.getState(targetId, commentMode.value)
    commentPageNum.value = snapshot.page
    commentPageSize.value = snapshot.pageSize
  }

  watch(
    [options.targetId, commentMode],
    ([nextTargetId], previousValue) => {
      const prevTargetId = previousValue?.[0]
      if (prevTargetId !== nextTargetId) {
        cancelReply()
      }
      syncPagingSnapshot()
    },
    { immediate: true }
  )

  const loadComments = async (allowPageClamp = true) => {
    if (!options.targetId.value) return
    syncPagingSnapshot()
    commentLoading.value = true
    try {
      const response = await getComments({
        articleId: options.targetId.value,
        source: options.source,
        page: commentPageNum.value,
        pageSize: commentPageSize.value
      })
      comments.value = Array.isArray(response.items) ? response.items : []
      commentTotal.value = toNumber(response.total)
      const nextTotalPages = Math.max(
        toNumber(response.totalPages, Math.ceil(commentTotal.value / Math.max(commentPageSize.value, 1))),
        commentTotal.value > 0 ? 1 : 0
      )
      commentTotalPages.value = nextTotalPages

      if (allowPageClamp && nextTotalPages > 0 && commentPageNum.value > nextTotalPages) {
        pagingStore.updateState(options.targetId.value, commentMode.value, { page: nextTotalPages })
        syncPagingSnapshot()
        await loadComments(false)
        return
      }

      if (commentTotal.value === 0 && commentPageNum.value !== 1) {
        pagingStore.resetMode(options.targetId.value, commentMode.value)
        syncPagingSnapshot()
      }
    } catch (error) {
      console.error(error)
      options.onCommentsLoadError?.(error)
    } finally {
      commentLoading.value = false
    }
  }

  const onCommentInteractionBlocked = () => {
    message.warning('评论未通过审核，暂不支持互动')
  }

  const handleCommentLike = async (comment: CommentThreadItem) => {
    if (!enableLike.value) {
      return
    }
    if (!isCommentApproved(comment)) {
      onCommentInteractionBlocked()
      return
    }
    if (!canParticipate.value) {
      options.onRequireLogin()
      return
    }
    const alreadyLiked = Boolean((comment as CommentThreadItem & { likedByMe?: boolean }).likedByMe)
    if (alreadyLiked) {
      message.info('你已经点过赞了')
      return
    }
    try {
      await likeComment(comment.id)
    } catch (error) {
      console.error(error)
      message.error(resolveRequestErrorMessage(error, '点赞失败，请稍后再试'))
      return
    }
    const target = comments.value.find((item) => item.id === comment.id)
    if (target) {
      target.likeCount = toNumber(target.likeCount) + 1
      target.likedByMe = true
    }
  }

  const handleReply = (comment: CommentThreadItem) => {
    if (!enableReply.value) {
      return
    }
    if (!isCommentApproved(comment)) {
      onCommentInteractionBlocked()
      return
    }
    if (!canParticipate.value) {
      options.onRequireLogin()
      return
    }
    const nickname = comment.nickname?.trim() || unref(options.anonymousLabel) || '匿名洞友'
    newComment.value = `@${nickname} `
    replyCommentId.value = comment.id
    replyUserId.value = comment.userId || null
  }

  const submitComment = async () => {
    if (!canParticipate.value) {
      options.onRequireLogin()
      return
    }
    const content = newComment.value.trim()
    if (!content || commentSubmitting.value || !options.targetId.value) return
    commentSubmitting.value = true
    try {
      await addComment({
        userId: options.currentUserId?.value || null,
        articleId: options.targetId.value,
        source: options.source,
        content,
        parentCommentId: replyCommentId.value,
        parentUserId: replyUserId.value
      })
      newComment.value = ''
      replyCommentId.value = null
      replyUserId.value = null
      pagingStore.resetMode(options.targetId.value, commentMode.value)
      syncPagingSnapshot()
      await loadComments()
      options.onCommentSubmitted?.()
    } catch (error) {
      console.error(error)
    } finally {
      commentSubmitting.value = false
    }
  }

  const handleCommentDelete = async (comment: CommentThreadItem) => {
    if (!options.isLoggedIn.value) {
      options.onRequireLogin()
      return
    }
    const viewerUserId = options.currentUserId?.value
    if (!viewerUserId || !comment.userId || viewerUserId !== comment.userId) {
      message.warning('仅可删除自己的评论')
      return
    }
    try {
      await deleteComment(comment.id)
      if (replyCommentId.value === comment.id) {
        cancelReply()
      }
      await loadComments()
      message.success('评论已删除')
      options.onCommentDeleted?.(comment)
    } catch (error) {
      console.error(error)
      message.error('删除评论失败，请稍后再试')
    }
  }

  const handleCommentPageChange = async (page: number) => {
    if (!options.targetId.value) return
    cancelReply()
    pagingStore.updateState(options.targetId.value, commentMode.value, { page })
    syncPagingSnapshot()
    await loadComments()
  }

  const switchCommentMode = async (
    mode: CommentViewMode,
    switchOptions?: { resetPage?: boolean; reload?: boolean }
  ) => {
    commentMode.value = mode
    if (switchOptions?.resetPage !== false && commentMode.value === 'preview' && options.targetId.value) {
      pagingStore.resetMode(options.targetId.value, mode)
    }
    if (switchOptions?.resetPage === true && options.targetId.value) {
      pagingStore.resetMode(options.targetId.value, mode)
    }
    cancelReply()
    syncPagingSnapshot()
    if (switchOptions?.reload !== false) {
      await loadComments()
    }
  }

  const resetAndReload = async (mode?: CommentViewMode) => {
    if (!options.targetId.value) return
    if (mode) {
      commentMode.value = mode
    }
    pagingStore.resetMode(options.targetId.value, commentMode.value)
    cancelReply()
    syncPagingSnapshot()
    await loadComments()
  }

  return {
    comments,
    threadedComments,
    commentTotal,
    commentTotalPages,
    commentPageNum,
    commentPageSize,
    commentMode,
    commentLoading,
    newComment,
    commentSubmitting,
    replyCommentId,
    commentPlaceholder,
    enableLike,
    enableReply,
    loadComments,
    handleCommentLike,
    handleReply,
    cancelReply,
    submitComment,
    handleCommentDelete,
    handleCommentPageChange,
    switchCommentMode,
    resetAndReload,
    onCommentInteractionBlocked
  }
}
