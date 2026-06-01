import { env } from '@/config/env';

export const DEFAULT_BRAND_LOGO = '/static/logo/logo.png';

/**
 * 解析站点品牌图（logo / 作者头像），小程序端对 http 尝试升级为 https，均失败时回退本地 logo。
 */
export function resolveBrandImageUrl(...candidates: Array<string | undefined | null>): string {
  for (const candidate of candidates) {
    const raw = typeof candidate === 'string' ? candidate.trim() : '';
    if (!raw) {
      continue;
    }
    if (raw.startsWith('/static/')) {
      return raw;
    }
    const normalized = normalizeAssetUrl(raw);
    if (normalized) {
      return normalized;
    }
    if (/^http:\/\//i.test(raw)) {
      const httpsCandidate = normalizeAssetUrl(raw.replace(/^http:\/\//i, 'https://'));
      if (httpsCandidate) {
        return httpsCandidate;
      }
    }
  }
  return DEFAULT_BRAND_LOGO;
}

export function normalizeAssetUrl(url?: string | null): string {
  if (!url) return '';
  if (url.startsWith('data:') || url.startsWith('blob:')) return url;

  const filePath = url.match(/^https?:\/\/(?:127\.0\.0\.1|localhost)(?::\d+)?(\/file\/.*)$/i);
  if (filePath) {
    url = `${env.baseURL}${filePath[1]}`;
  }

  if (url.startsWith('/file/')) {
    url = `${env.baseURL}${url}`;
  }

  // 微信小程序不支持 HTTP 图片资源，服务端 HTTPS 就绪前统一降级为空占位。
  // #ifdef MP-WEIXIN
  if (url.startsWith('http://')) return '';
  // #endif

  return url;
}
