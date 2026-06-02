// src/utils/auth.ts
// 登录态与 Token 管理
// 默认请求头：Authorization: Bearer <token>

import { getStorage, setStorage, removeStorage } from './storage';
import { showInfoToast } from './feedback';

const KEY_TOKEN = 'auth.token';
const KEY_TOKEN_NAME = 'auth.tokenName';
const KEY_TOKEN_PREFIX = 'auth.tokenPrefix';
const KEY_EXPIRE_AT = 'auth.expireAt';
const KEY_USER_INFO = 'auth.userInfo';
const KEY_PENDING_ACTIONS = 'auth.pendingActions';

const DEFAULT_TOKEN_NAME = 'Authorization';
const DEFAULT_TOKEN_PREFIX = 'Bearer';
export const AUTH_LOGIN_SUCCESS_EVENT = 'auth:login-success';

export interface SetTokenOptions {
  tokenName?: string;
  tokenPrefix?: string;
  /** 单位秒 */
  expiresIn?: number;
}

export interface AuthHeader {
  name: string;
  value: string;
}

export interface PendingAction {
  type: string;
  payload?: unknown;
}

export interface LoginSuccessEventDetail {
  actions: PendingAction[];
}

export function setToken(token: string, options: SetTokenOptions = {}): void {
  setStorage(KEY_TOKEN, token);
  setStorage(KEY_TOKEN_NAME, options.tokenName || DEFAULT_TOKEN_NAME);
  setStorage(
    KEY_TOKEN_PREFIX,
    typeof options.tokenPrefix === 'string' ? options.tokenPrefix : DEFAULT_TOKEN_PREFIX
  );
  if (typeof options.expiresIn === 'number' && options.expiresIn > 0) {
    setStorage(KEY_EXPIRE_AT, Date.now() + options.expiresIn * 1000);
  } else {
    removeStorage(KEY_EXPIRE_AT);
  }
}

export function getToken(): string {
  return getStorage<string>(KEY_TOKEN, '');
}

export function getTokenName(): string {
  return getStorage<string>(KEY_TOKEN_NAME, DEFAULT_TOKEN_NAME);
}

export function getTokenPrefix(): string {
  return getStorage<string>(KEY_TOKEN_PREFIX, DEFAULT_TOKEN_PREFIX);
}

export function isTokenExpired(): boolean {
  const expireAt = getStorage<number>(KEY_EXPIRE_AT, 0);
  if (!expireAt) return false;
  return Date.now() >= expireAt;
}

export function isLoggedIn(): boolean {
  return !!getToken() && !isTokenExpired();
}

export function clearToken(): void {
  removeStorage(KEY_TOKEN);
  removeStorage(KEY_TOKEN_NAME);
  removeStorage(KEY_TOKEN_PREFIX);
  removeStorage(KEY_EXPIRE_AT);
  removeStorage(KEY_USER_INFO);
  clearPendingActionsCache();
}

let isRedirecting401 = false;

export function handle401(pendingAction?: PendingAction): void {
  if (isRedirecting401) return;
  isRedirecting401 = true;
  clearToken();
  if (pendingAction) {
    pushPendingActionCache(pendingAction);
  }
  if (isLoginPageActive()) {
    showInfoToast('请先登录');
    setTimeout(() => { isRedirecting401 = false; }, 1000);
    return;
  }
  uni.navigateTo({
    url: '/pages-user/login/login',
    fail() {
      showInfoToast('请先登录');
    },
    complete() {
      setTimeout(() => { isRedirecting401 = false; }, 1000);
    }
  });
}

export function reset401Guard(): void {
  isRedirecting401 = false;
}

export function buildAuthHeader(): AuthHeader | null {
  const token = getToken();
  if (!token) return null;
  const name = getTokenName();
  const prefix = getTokenPrefix();
  return {
    name,
    value: prefix ? `${prefix} ${token}` : token
  };
}

export function setUserInfoCache<T>(userInfo: T | null): void {
  setStorage<T | null>(KEY_USER_INFO, userInfo);
}

export function getUserInfoCache<T>(): T | null {
  return getStorage<T | null>(KEY_USER_INFO, null);
}

let pendingActionMemoryQueue: PendingAction[] | null = null;

function getMemoryQueue(): PendingAction[] {
  if (pendingActionMemoryQueue === null) {
    pendingActionMemoryQueue = getStorage<PendingAction[]>(KEY_PENDING_ACTIONS, []);
  }
  return pendingActionMemoryQueue;
}

function syncQueueToStorage(): void {
  if (pendingActionMemoryQueue !== null) {
    setStorage(KEY_PENDING_ACTIONS, pendingActionMemoryQueue);
  }
}

export function getPendingActionsCache(): PendingAction[] {
  return [...getMemoryQueue()];
}

export function pushPendingActionCache(action: PendingAction): PendingAction[] {
  if (!action || !action.type) return getMemoryQueue();
  const queue = getMemoryQueue();
  queue.push(action);
  syncQueueToStorage();
  return [...queue];
}

export function consumePendingActionsCache(): PendingAction[] {
  const queue = [...getMemoryQueue()];
  pendingActionMemoryQueue = [];
  removeStorage(KEY_PENDING_ACTIONS);
  return queue;
}

export function clearPendingActionsCache(): void {
  pendingActionMemoryQueue = [];
  removeStorage(KEY_PENDING_ACTIONS);
}

function isLoginPageActive(): boolean {
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  return currentPage?.route === 'pages-user/login/login';
}

export { isLoginPageActive };
