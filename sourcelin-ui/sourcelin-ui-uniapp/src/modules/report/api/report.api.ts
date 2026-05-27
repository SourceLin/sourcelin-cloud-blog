import { http } from '@/utils/request';

export interface ContentReportPayload {
  targetType: string;
  targetId: number;
  reason: string;
  detail?: string;
  pagePath?: string;
}

export function createContentReport(payload: ContentReportPayload): Promise<void> {
  return http.post<void>('/front/reports', payload);
}
