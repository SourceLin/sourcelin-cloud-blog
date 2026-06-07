import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { CommentCreatePayload, CommentItem, CommentSource } from '../types/comment';

const PUBLIC_READ_OPTIONS = {
  skipAuthRedirect: true,
  skipErrorToast: true
} as const;

export function fetchComments(
  targetId: number,
  source: CommentSource = 'article',
  page = 1,
  pageSize = 10
): Promise<PageResult<CommentItem>> {
  return http.get<PageResult<CommentItem>>('/front/comments', {
    articleId: targetId,
    source,
    page,
    pageSize
  }, PUBLIC_READ_OPTIONS);
}

export function createComment(payload: CommentCreatePayload): Promise<void> {
  return http.post<void>('/front/comments', payload);
}

export function likeComment(id: number): Promise<void> {
  return http.post<void>(`/front/comments/like/${id}`);
}

export function deleteComment(id: number): Promise<void> {
  return http.delete<void>(`/front/comments/${id}`);
}
