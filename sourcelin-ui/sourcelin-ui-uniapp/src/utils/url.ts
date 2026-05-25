import { env } from '@/config/env';

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
