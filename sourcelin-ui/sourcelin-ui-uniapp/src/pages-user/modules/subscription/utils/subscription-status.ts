import { getStorage, setStorage } from '@/utils/storage';
import type { SubscribeAuthorizationStatus } from '../api/subscription.api';

const SUBSCRIPTION_STATUS_STORAGE_KEY = 'subscription.authorization.status';

export interface SubscriptionLocalStatusRecord {
  templateId: string;
  authorizationStatus: SubscribeAuthorizationStatus;
  updatedAt: number;
}

type SubscriptionLocalStatusMap = Record<string, SubscriptionLocalStatusRecord>;

export function getSubscriptionStatusMap(): SubscriptionLocalStatusMap {
  return getStorage<SubscriptionLocalStatusMap>(SUBSCRIPTION_STATUS_STORAGE_KEY, {});
}

export function saveSubscriptionStatus(templateId: string, authorizationStatus: SubscribeAuthorizationStatus): SubscriptionLocalStatusMap {
  const current = getSubscriptionStatusMap();
  const next: SubscriptionLocalStatusMap = {
    ...current,
    [templateId]: {
      templateId,
      authorizationStatus,
      updatedAt: Date.now()
    }
  };
  setStorage(SUBSCRIPTION_STATUS_STORAGE_KEY, next);
  return next;
}
