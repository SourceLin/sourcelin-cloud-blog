// src/config/env.ts
// 环境变量与 baseURL：通过 uni.getAccountInfoSync 切换微信小程序环境
// H5/APP 端走 import.meta.env.MODE 兜底

export interface EnvConfig {
  name: 'develop' | 'trial' | 'release';
  baseURL: string;
  apiPrefix: string;
  /**
   * 微信小程序订阅消息模板 ID 列表。
   * 获取路径：微信公众平台/小程序后台 -> 功能 -> 订阅消息 -> 公共模板库/我的模板。
   * 按环境分别填写，前端会在请求订阅前读取这里的模板 ID。
   */
  subscribeMessageTemplateIds: string[];
  isProd: boolean;
}

const ENV_MAP: Record<EnvConfig['name'], EnvConfig> = {
  develop: {
    name: 'develop',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    subscribeMessageTemplateIds: ['y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM'],
    isProd: false
  },
  trial: {
    name: 'trial',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    subscribeMessageTemplateIds: ['y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM'],
    isProd: false
  },
  release: {
    name: 'release',
    baseURL: 'https://sourcelin.cn',
    apiPrefix: '/blog-api',
    subscribeMessageTemplateIds: ['y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM'],
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

function resolveRequestBaseURL(): string {
  // H5 本地调试通过 Vite 同源代理访问线上前台接口，避免跨域阻断内容预览。
  // #ifdef H5
  if (import.meta.env?.MODE === 'development') return '';
  // #endif

  return current.baseURL;
}

/**
 * 拼接完整 URL
 * @param path 业务接口路径，例如 /front/articles
 */
export function resolveUrl(path: string): string {
  const requestBaseURL = resolveRequestBaseURL();
  if (!path) return requestBaseURL;
  if (/^https?:\/\//i.test(path)) return path;
  const prefix = current.apiPrefix || '';
  if (path.startsWith(prefix)) return requestBaseURL + path;
  return requestBaseURL + prefix + (path.startsWith('/') ? path : '/' + path);
}
