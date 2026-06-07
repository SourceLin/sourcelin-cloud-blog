import { http } from '@/utils/request';

export interface AnalyticsEventPayload {
  eventType: string;
  pagePath?: string;
  targetType?: string;
  targetId?: number;
  metadataJson?: string;
  platform?: string;
  appVersion?: string;
}

export function createAnalyticsEvent(payload: AnalyticsEventPayload): Promise<void> {
  return http.post<void>('/front/analytics/events', payload, {
    skipAuthRedirect: true,
    skipErrorToast: true
  });
}
