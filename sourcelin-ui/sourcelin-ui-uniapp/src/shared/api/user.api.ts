import { http } from '@/utils/request';
import type { FrontUserInfo } from '@/modules/user/types/user';

export function fetchCurrentUserInfo(): Promise<FrontUserInfo> {
  return http.get<FrontUserInfo>('/front/user/info');
}

export function fetchUserProfile(): Promise<FrontUserInfo> {
  return http.get<FrontUserInfo>('/front/user/profile');
}
