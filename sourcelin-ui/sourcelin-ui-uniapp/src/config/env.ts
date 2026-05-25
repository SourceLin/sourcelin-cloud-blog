// src/config/env.ts
// 环境变量与 baseURL：通过 uni.getAccountInfoSync 切换微信小程序环境
// H5/APP 端走 import.meta.env.MODE 兜底

export interface EnvConfig {
  name: 'develop' | 'trial' | 'release';
  baseURL: string;
  apiPrefix: string;
  isProd: boolean;
}

const ENV_MAP: Record<EnvConfig['name'], EnvConfig> = {
  develop: {
    name: 'develop',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    isProd: false
  },
  trial: {
    name: 'trial',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    isProd: false
  },
  release: {
    name: 'release',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    isProd: true
  }
};

function resolveEnv(): EnvConfig {
  // #ifdef MP-WEIXIN
  try {
    const accountInfo = uni.getAccountInfoSync();
    const envVersion = accountInfo?.miniProgram?.envVersion as EnvConfig['name'] | undefined;
    if (envVersion && ENV_MAP[envVersion]) return ENV_MAP[envVersion];
  } catch {
    // 兜底
  }
  // #endif

  // #ifdef H5 || APP-PLUS
  const mode = import.meta.env?.MODE;
  if (mode === 'development') return ENV_MAP.develop;
  if (mode === 'staging') return ENV_MAP.trial;
  // #endif

  return ENV_MAP.release;
}

const current = resolveEnv();

export const env: EnvConfig = current;

/**
 * 拼接完整 URL
 * @param path 业务接口路径，例如 /front/articles
 */
export function resolveUrl(path: string): string {
  if (!path) return current.baseURL;
  if (/^https?:\/\//i.test(path)) return path;
  const prefix = current.apiPrefix || '';
  if (path.startsWith(prefix)) return current.baseURL + path;
  return current.baseURL + prefix + (path.startsWith('/') ? path : '/' + path);
}
