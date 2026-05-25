// src/utils/storage.ts
// uni.storage 封装：统一键名前缀 + 过期时间

const PREFIX = 'sourcelin:';

interface StoragePayload<T> {
  value: T;
  expireAt: number;
}

function withPrefix(key: string): string {
  return PREFIX + key;
}

export function setStorage<T>(key: string, value: T, expireSeconds = 0): boolean {
  const payload: StoragePayload<T> = {
    value,
    expireAt: expireSeconds > 0 ? Date.now() + expireSeconds * 1000 : 0
  };
  try {
    uni.setStorageSync(withPrefix(key), payload);
    return true;
  } catch (e) {
    console.warn('[storage][set]', key, e);
    return false;
  }
}

export function getStorage<T>(key: string, defaultValue: T): T {
  try {
    const payload = uni.getStorageSync(withPrefix(key)) as StoragePayload<T> | '';
    if (!payload || typeof payload !== 'object') return defaultValue;
    if (payload.expireAt && payload.expireAt < Date.now()) {
      uni.removeStorageSync(withPrefix(key));
      return defaultValue;
    }
    return payload.value;
  } catch (e) {
    console.warn('[storage][get]', key, e);
    return defaultValue;
  }
}

export function removeStorage(key: string): void {
  try {
    uni.removeStorageSync(withPrefix(key));
  } catch (e) {
    console.warn('[storage][remove]', key, e);
  }
}
