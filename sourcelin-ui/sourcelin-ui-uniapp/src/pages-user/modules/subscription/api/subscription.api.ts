import { http } from '@/utils/request';

export type SubscribeAuthorizationStatus = 'accept' | 'reject' | 'ban' | 'filter' | 'fail';

export interface SubscribeAuthorizationPayload {
  templateId: string;
  authorizationStatus: SubscribeAuthorizationStatus;
  scene?: string;
  platform?: string;
}

export function createSubscribeAuthorization(payload: SubscribeAuthorizationPayload): Promise<void> {
  return http.post<void>('/front/subscriptions/authorizations', payload, {
    skipErrorToast: true,
    skipAuthRedirect: true
  });
}
