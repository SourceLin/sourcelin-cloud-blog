export interface H5SeoPayload {
  title: string;
  description?: string;
  keywords?: string[] | string;
}

const DEFAULT_SITE_NAME = '圆圈博客';
const DEFAULT_DESCRIPTION = 'Sourcelin Blog 移动端聚焦阅读、互动、回访与轻圈子体验。';

function normalizeText(value?: string | null, maxLength = 120): string {
  const plainText = (value || '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/\s+/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .trim();

  if (!plainText) return '';
  return plainText.length > maxLength ? `${plainText.slice(0, maxLength).trim()}...` : plainText;
}

function resolveKeywords(value?: string[] | string): string {
  if (!value) return '';
  const list = Array.isArray(value) ? value : value.split(',');
  return list
    .map((item) => item.trim())
    .filter(Boolean)
    .filter((item, index, source) => source.indexOf(item) === index)
    .join(', ');
}

function upsertMeta(name: string, content: string): void {
  if (typeof document === 'undefined') return;
  let meta = document.head.querySelector<HTMLMetaElement>(`meta[name="${name}"]`);
  if (!meta) {
    meta = document.createElement('meta');
    meta.setAttribute('name', name);
    document.head.appendChild(meta);
  }
  meta.setAttribute('content', content);
}

export function buildSeoTitle(pageTitle: string, siteName = DEFAULT_SITE_NAME): string {
  const normalizedPageTitle = normalizeText(pageTitle, 80);
  const normalizedSiteName = normalizeText(siteName, 40) || DEFAULT_SITE_NAME;
  if (!normalizedPageTitle) return normalizedSiteName;
  if (normalizedPageTitle.includes(normalizedSiteName)) return normalizedPageTitle;
  return `${normalizedPageTitle} - ${normalizedSiteName}`;
}

export function applyH5Seo(payload: H5SeoPayload): void {
  // #ifdef H5
  if (typeof document === 'undefined') return;
  const title = normalizeText(payload.title, 80) || DEFAULT_SITE_NAME;
  const description = normalizeText(payload.description, 160) || DEFAULT_DESCRIPTION;
  const keywords = resolveKeywords(payload.keywords);

  document.title = title;
  upsertMeta('description', description);
  if (keywords) {
    upsertMeta('keywords', keywords);
  }
  // #endif
}

export function extractSeoSummary(...values: Array<string | undefined | null>): string {
  for (const value of values) {
    const normalized = normalizeText(value, 160);
    if (normalized) return normalized;
  }
  return DEFAULT_DESCRIPTION;
}
