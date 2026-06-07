import { http } from '@/utils/request';
import type { ListResult } from '@/shared/types/api';
import type {
  AboutInfo,
  FriendLinkApplyPayload,
  FriendLinkItem,
  NavigationItem,
  SiteBrandInfo
} from '@/modules/site/types/site';

const PUBLIC_READ_OPTIONS = {
  skipAuthRedirect: true,
  skipErrorToast: true
} as const;

export function fetchAboutInfo(): Promise<AboutInfo> {
  return http.get<AboutInfo>('/front/config/about', undefined, PUBLIC_READ_OPTIONS);
}

export function fetchSiteBrandInfo(): Promise<SiteBrandInfo> {
  return http.get<SiteBrandInfo>('/front/config/siteInfo', undefined, PUBLIC_READ_OPTIONS);
}

export async function fetchFriendLinks(): Promise<FriendLinkItem[]> {
  const result = await http.get<ListResult<FriendLinkItem>>('/front/links', undefined, PUBLIC_READ_OPTIONS);
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
  const result = await http.get<ListResult<NavigationItem>>('/front/navigation', params, PUBLIC_READ_OPTIONS);
  return result.items || [];
}

export function reportNavigationClick(id: number): Promise<void> {
  return http.post<void>(`/front/navigation/${id}/click`, undefined, PUBLIC_READ_OPTIONS);
}
