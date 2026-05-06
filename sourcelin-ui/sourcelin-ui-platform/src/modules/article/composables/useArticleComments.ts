import type { Ref } from 'vue'
import { useContentComments } from '@/shared/composables/useContentComments'

export function useArticleComments(
  articleId: Ref<number>,
  currentUserId: Ref<number | undefined>,
  isLoggedIn: Ref<boolean>,
  onRequireLogin: () => void
) {
  const commentState = useContentComments({
    targetId: articleId,
    source: 'article',
    currentUserId,
    isLoggedIn,
    onRequireLogin,
    isAnonymousAllowed: false,
    enableLike: true,
    enableReply: true,
    anonymousLabel: '匿名用户'
  })

  return {
    ...commentState,
    comments: commentState.threadedComments
  }
}

export type { CommentThreadItem as ArticleCommentItem } from '@/shared/composables/useContentComments'
