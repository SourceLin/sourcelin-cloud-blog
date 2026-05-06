import type { Ref } from 'vue'
import { computed, nextTick, ref } from 'vue'
import type { SayItem } from '@/modules/say/model/say.types'
import { useSayFeedComments } from '@/modules/say/composables/useSayFeedComments'
import type { CommentThreadItem } from '@/shared/composables/useContentComments'
import { useSMessage } from '@/shared/composables/useSMessage'
import { useUserStore } from '@/stores/user'

interface UseSayCardCommentsOptions {
  item: Ref<SayItem>
  currentUserId: Ref<number | undefined>
  commentCount: Ref<number>
}

export function useSayCardComments(options: UseSayCardCommentsOptions) {
  const userStore = useUserStore()
  const message = useSMessage()

  const COMMENT_PREVIEW_COUNT = 2
  const COMMENT_RICH_THRESHOLD = 3

  const commentsLoaded = ref(false)
  const commentsExpanded = ref(false)
  const composerVisible = ref(false)
  const commentPanelVisible = ref(false)

  const sayId = computed(() => options.item.value.id)
  const isLoggedInComputed = computed(() => userStore.isLoggedIn)

  const {
    comments: threadedComments,
    commentTotal,
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
    handleCommentLike,
    handleCommentDelete,
    handleReply,
    cancelReply,
    submitComment,
    handleCommentPageChange,
    switchCommentMode,
    onCommentInteractionBlocked
  } = useSayFeedComments(
    sayId,
    isLoggedInComputed,
    options.currentUserId,
    () => {
      message.warning('请先登录后再回复评论')
    },
    () => {
      message.success('评论成功')
      options.commentCount.value = Math.max(options.commentCount.value, commentTotal.value)
      commentsExpanded.value = true
      composerVisible.value = true
      commentPanelVisible.value = true
      commentsLoaded.value = true
    },
    () => message.error('加载评论失败'),
    (comment) => {
      if (comment.status === undefined || comment.status === 1) {
        options.commentCount.value = Math.max(options.commentCount.value - 1, 0)
      }
    }
  )

  const hasComments = computed(() => options.commentCount.value > 0)
  const isCommentRich = computed(() => options.commentCount.value >= COMMENT_RICH_THRESHOLD)
  const headerCommentTotal = computed(() =>
    commentsLoaded.value ? commentTotal.value : options.commentCount.value
  )
  const displayedThreadComments = computed(() => {
    const all = threadedComments.value
    if (commentsExpanded.value) return all
    return all.slice(0, COMMENT_PREVIEW_COUNT)
  })
  const hiddenCommentCount = computed(() =>
    Math.max(headerCommentTotal.value - COMMENT_PREVIEW_COUNT, 0)
  )
  const showCommentPanel = computed(
    () => commentPanelVisible.value || commentLoading.value
  )
  const canToggleComments = computed(() => headerCommentTotal.value > COMMENT_PREVIEW_COUNT)
  const commentSectionTitle = computed(() => '评论')
  const commentSectionSummary = computed(() =>
    commentsExpanded.value ? '已展开全部' : '默认显示前两条'
  )
  const commentToggleText = computed(() =>
    commentsExpanded.value ? '收起评论' : `展开 ${hiddenCommentCount.value} 条评论`
  )
  const commentPanelId = computed(() => `say-comments-${options.item.value.id}`)

  const formatCommentDate = (value?: string) => (value ? value.split(' ')[0] : '')

  async function ensurePreviewComments() {
    if (!hasComments.value || commentsLoaded.value) return
    await switchCommentMode('preview', { resetPage: true, reload: true })
    commentsLoaded.value = true
  }

  function scrollCommentPanelIntoView() {
    const panel = document.getElementById(commentPanelId.value)
    if (!(panel instanceof HTMLElement)) return
    const top = panel.getBoundingClientRect().top + window.scrollY - 92
    window.scrollTo({
      top: Math.max(top, 0),
      behavior: 'smooth'
    })
  }

  async function closeCommentPanel() {
    composerVisible.value = false
    commentsExpanded.value = false
    commentPanelVisible.value = false
    cancelReply()
    if (commentsLoaded.value) {
      await switchCommentMode('preview', { resetPage: true, reload: false })
    }
  }

  async function openComposer() {
    if (!userStore.isLoggedIn) {
      message.warning('请先登录后再评论')
      return
    }
    commentPanelVisible.value = true
    composerVisible.value = true
    await ensurePreviewComments()
    await nextTick()
    scrollCommentPanelIntoView()
  }

  async function openCommentPanel() {
    commentPanelVisible.value = true
    if (!commentsLoaded.value) {
      await ensurePreviewComments()
    }
    await nextTick()
    scrollCommentPanelIntoView()
  }

  async function handleCommentAction() {
    if (commentPanelVisible.value) {
      await closeCommentPanel()
      return
    }
    if (userStore.isLoggedIn) {
      await openComposer()
      return
    }
    if (hasComments.value) {
      await openCommentPanel()
      return
    }
    message.warning('请先登录后再评论')
  }

  async function toggleComments() {
    if (commentsExpanded.value) {
      await closeCommentPanel()
      return
    }
    commentPanelVisible.value = true
    if (!commentsLoaded.value) {
      await ensurePreviewComments()
    }
    await switchCommentMode('full', { reload: true })
    commentsExpanded.value = true
    await nextTick()
    scrollCommentPanelIntoView()
  }

  function onReplyThread(comment: CommentThreadItem) {
    commentPanelVisible.value = true
    composerVisible.value = true
    void ensurePreviewComments()
    handleReply(comment)
  }

  async function handleCommentPageUpdate(page: number) {
    await handleCommentPageChange(page)
    await nextTick()
    scrollCommentPanelIntoView()
  }

  return {
    composerVisible,
    commentPanelVisible,
    isCommentRich,
    headerCommentTotal,
    displayedThreadComments,
    showCommentPanel,
    canToggleComments,
    commentSectionTitle,
    commentSectionSummary,
    commentToggleText,
    commentPanelId,
    commentTotal,
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
    formatCommentDate,
    handleCommentLike,
    handleCommentDelete,
    cancelReply,
    submitComment,
    onCommentInteractionBlocked,
    handleCommentAction,
    toggleComments,
    onReplyThread,
    handleCommentPageUpdate
  }
}
