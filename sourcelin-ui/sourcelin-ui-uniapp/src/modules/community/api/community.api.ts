import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type { SayItem, TreeholeItem } from '../types/community';

export function fetchSayPage(page = 1, pageSize = 10): Promise<PageResult<SayItem>> {
  return http.get<PageResult<SayItem>>('/front/says', { page, pageSize });
}

export function fetchTreeholePage(page = 1, pageSize = 10): Promise<PageResult<TreeholeItem>> {
  return http.get<PageResult<TreeholeItem>>('/front/treeholes', { page, pageSize });
}

export function createSay(payload: {
  content: string;
  images?: string;
  imageFileIds?: string;
}): Promise<number> {
  return http.post<number>('/front/says', payload);
}

export function createTreehole(payload: {
  content: string;
  nickname?: string;
  avatar?: string;
}): Promise<number> {
  return http.post<number>('/front/treeholes', payload);
}

export function deleteSay(id: number): Promise<void> {
  return http.delete<void>(`/front/says/${id}`);
}

export function deleteTreehole(id: number): Promise<void> {
  return http.delete<void>(`/front/treeholes/${id}`);
}
