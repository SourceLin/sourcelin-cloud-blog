// src/stores/user.ts
// 用户登录态、Token、待执行动作队列

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  getToken,
  setToken,
  clearToken,
  isLoggedIn as authIsLoggedIn,
  setUserInfoCache,
  getUserInfoCache,
  getPendingActionsCache,
  pushPendingActionCache,
  consumePendingActionsCache,
  type SetTokenOptions,
  type PendingAction
} from '@/utils/auth';

export interface UserInfo {
  id: number | string;
  username: string;
  nickname?: string;
  avatar?: string;
  email?: string;
  phone?: string;
  introduction?: string;
  articleCount?: number;
  followerCount?: number;
  roles?: string[];
  permissions?: string[];
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('');
  const userInfo = ref<UserInfo | null>(null);
  const pendingActions = ref<PendingAction[]>([]);

  const isLoggedIn = computed(() => !!token.value && authIsLoggedIn());

  function restoreFromStorage(): void {
    token.value = getToken();
    userInfo.value = getUserInfoCache<UserInfo>();
    pendingActions.value = getPendingActionsCache();
  }

  function loginWithToken(payload: {
    token: string;
    user?: UserInfo | null;
    options?: SetTokenOptions;
  }): void {
    setToken(payload.token, payload.options);
    token.value = payload.token;
    if (payload.user !== undefined) {
      userInfo.value = payload.user;
      setUserInfoCache(payload.user);
    }
  }

  function updateUserInfo(info: UserInfo | null): void {
    userInfo.value = info;
    setUserInfoCache(info);
  }

  function logout(): void {
    clearToken();
    token.value = '';
    userInfo.value = null;
    pendingActions.value = [];
  }

  function pushPendingAction(action: PendingAction): void {
    if (!action || !action.type) return;
    pendingActions.value = pushPendingActionCache(action);
  }

  function flushPendingActions(consumer: (action: PendingAction) => void): void {
    const queue = consumePendingActionsCache();
    pendingActions.value = [];
    queue.forEach((action) => {
      try {
        consumer(action);
      } catch (e) {
        console.warn('[user][flushPendingActions]', e);
      }
    });
  }

  function consumePendingActions(): PendingAction[] {
    const queue = consumePendingActionsCache();
    pendingActions.value = [];
    return queue;
  }

  return {
    token,
    userInfo,
    pendingActions,
    isLoggedIn,
    restoreFromStorage,
    loginWithToken,
    updateUserInfo,
    logout,
    pushPendingAction,
    flushPendingActions,
    consumePendingActions
  };
});
