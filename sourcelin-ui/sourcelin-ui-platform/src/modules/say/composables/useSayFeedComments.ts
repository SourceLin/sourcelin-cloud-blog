import type { Ref } from 'vue'
import { useContentComments } from '@/shared/composables/useContentComments'
import type { CommentThreadItem } from '@/shared/composables/useContentComments'

/**
 * 说说卡片内评论：与文章页共用 /front/comments + 审核态与互动拦截逻辑。
 */
export function useSayFeedComments(
  sayId: Ref<number>,
  isLoggedIn: Ref<boolean>,
  currentUserId: Ref<number | undefined>,
  onRequireLogin: () => void,
  onCommentSubmitted?: () => void,
  onCommentsLoadError?: (error: unknown) => void,
  onCommentDeleted?: (comment: CommentThreadItem) => void
) {
  const state = useContentComments({
    targetId: sayId,
    source: 'say',
    isLoggedIn,
    currentUserId,
    onRequireLogin,
    isAnonymousAllowed: false,
    enableLike: true,
    enableReply: true,
    anonymousLabel: '匿名用户',
    initialPageSize: 10,
    initialMode: 'preview',
    pageSizeByMode: {
      preview: 2,
      full: 10
    },
    onCommentSubmitted,
    onCommentDeleted,
    onCommentsLoadError
  })
  return {
    ...state,
    comments: state.threadedComments
  }
}

export type { CommentThreadItem as SayFeedCommentItem } from '@/shared/composables/useContentComments'
