import { http, upload } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { FrontUserInfo } from '../types/user';
import type { ArticleSummary } from '@/modules/article/types/article';
import type { FollowItem } from '@/modules/interaction/types/interaction';

export function fetchCurrentUserInfo(): Promise<FrontUserInfo> {
  return http.get<FrontUserInfo>('/front/user/info');
}

export function fetchUserProfile(): Promise<FrontUserInfo> {
  return http.get<FrontUserInfo>('/front/user/profile');
}

export function updateUserProfile(payload: {
  nickName: string;
  email?: string;
  phonenumber?: string;
  sex?: number;
  introduction?: string;
}): Promise<void> {
  return http.put<void>('/front/user/profile', payload);
}

export function uploadUserAvatar(filePath: string): Promise<{ avatar: string; avatarFileId?: number | string }> {
  return upload<{ avatar: string; avatarFileId?: number | string }>({
    url: '/front/user/avatar',
    filePath
  });
}

export function fetchUserDetail(userId: number): Promise<FrontUserInfo> {
  return http.get<FrontUserInfo>(`/front/users/${userId}`);
}

export function fetchUserArticlePage(
  userId: number,
  page = 1,
  pageSize = 10,
  status?: number
): Promise<PageResult<ArticleSummary>> {
  return http.get<PageResult<ArticleSummary>>(`/front/users/articles/${userId}`, {
    page,
    pageSize,
    status
  });
}

export function fetchUserFollowerPage(
  userId: number,
  page = 1,
  pageSize = 10,
  state: 'all' | 'active' | 'inactive' = 'all'
): Promise<PageResult<FollowItem>> {
  return http.get<PageResult<FollowItem>>(`/front/users/followers/${userId}`, {
    page,
    pageSize,
    state
  });
}

export function fetchUserFollowingPage(
  userId: number,
  page = 1,
  pageSize = 10,
  state: 'all' | 'active' | 'inactive' = 'all'
): Promise<PageResult<FollowItem>> {
  return http.get<PageResult<FollowItem>>(`/front/users/followings/${userId}`, {
    page,
    pageSize,
    state
  });
}
