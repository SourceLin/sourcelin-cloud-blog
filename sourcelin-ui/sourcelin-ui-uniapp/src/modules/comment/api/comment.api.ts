import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { CommentCreatePayload, CommentItem, CommentSource } from '../types/comment';

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
  });
}

export function createComment(payload: CommentCreatePayload): Promise<void> {
  return http.post<void>('/front/comments', payload);
}

export function likeComment(id: number): Promise<void> {
  return http.post<void>(`/front/comments/like/${id}`);
}
