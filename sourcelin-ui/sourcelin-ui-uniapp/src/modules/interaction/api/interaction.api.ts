import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { ArticleSummary } from '@/modules/article/types/article';
import type {
  CollectItem,
  FollowAction,
  FollowItem,
  InteractionTargetType,
  LikeItem,
  MyCommentItem,
  MyCommentSource
} from '../types/interaction';

export function likeTarget(targetType: InteractionTargetType, targetId: number): Promise<void> {
  return http.put<void>(`/front/interactions/likes/${targetType}/${targetId}`);
}

export function unlikeTarget(targetType: InteractionTargetType, targetId: number): Promise<void> {
  return http.delete<void>(`/front/interactions/likes/${targetType}/${targetId}`);
}

export function collectTarget(targetType: InteractionTargetType, targetId: number): Promise<void> {
  return http.put<void>(`/front/interactions/collects/${targetType}/${targetId}`);
}

export function uncollectTarget(targetType: InteractionTargetType, targetId: number): Promise<void> {
  return http.delete<void>(`/front/interactions/collects/${targetType}/${targetId}`);
}

export function fetchMyCollects(
  page = 1,
  pageSize = 10,
  type: InteractionTargetType | 'all' = 'all'
): Promise<PageResult<CollectItem<ArticleSummary>>> {
  return http.get<PageResult<CollectItem<ArticleSummary>>>('/front/interactions/collects', {
    page,
    pageSize,
    type,
    state: 'active',
    sortBy: 'createTime',
    sortOrder: 'desc'
  });
}

export function fetchMyLikes(
  page = 1,
  pageSize = 10,
  type: InteractionTargetType | 'all' = 'all'
): Promise<PageResult<LikeItem<ArticleSummary>>> {
  return http.get<PageResult<LikeItem<ArticleSummary>>>('/front/interactions/likes', {
    page,
    pageSize,
    type,
    state: 'active',
    sortBy: 'createTime',
    sortOrder: 'desc'
  });
}

export function fetchMyComments(
  page = 1,
  pageSize = 10,
  source: MyCommentSource = 'all'
): Promise<PageResult<MyCommentItem>> {
  return http.get<PageResult<MyCommentItem>>('/front/comments/mine', {
    page,
    pageSize,
    source: source === 'all' ? undefined : source
  });
}

export function followUser(targetUserId: number): Promise<FollowAction> {
  return http.post<FollowAction>('/front/follows', { targetUserId });
}

export function unfollowById(followId: number): Promise<void> {
  return http.delete<void>(`/front/follows/${followId}`);
}

export function fetchMyFollowings(page = 1, pageSize = 10): Promise<PageResult<FollowItem>> {
  return http.get<PageResult<FollowItem>>('/front/follows/following', { page, pageSize });
}
