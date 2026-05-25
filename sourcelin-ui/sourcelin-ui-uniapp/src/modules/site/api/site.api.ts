import { http } from '@/utils/request';
import type { ListResult } from '@/shared/types/api';
import type {
  AboutInfo,
  FriendLinkApplyPayload,
  FriendLinkItem,
  NavigationItem
} from '../types/site';

export function fetchAboutInfo(): Promise<AboutInfo> {
  return http.get<AboutInfo>('/front/config/about');
}

export async function fetchFriendLinks(): Promise<FriendLinkItem[]> {
  const result = await http.get<ListResult<FriendLinkItem>>('/front/links');
  return result.items || [];
}

export function applyFriendLink(payload: FriendLinkApplyPayload): Promise<void> {
  return http.post<void>('/front/links/apply', payload);
}

export async function fetchNavigationList(params?: {
  category?: string;
  keyword?: string;
  isRecommend?: number;
}): Promise<NavigationItem[]> {
  const result = await http.get<ListResult<NavigationItem>>('/front/navigation', params);
  return result.items || [];
}

export function reportNavigationClick(id: number): Promise<void> {
  return http.post<void>(`/front/navigation/${id}/click`);
}
