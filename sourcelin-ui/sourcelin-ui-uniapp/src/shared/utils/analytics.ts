import { createAnalyticsEvent } from '@/modules/analytics/api/analytics.api';
import type { ArticleSummary } from '@/modules/article/types/article';
import { getStorage, setStorage } from '@/utils/storage';
import { getUserPreferences } from './preferences';

const ARTICLE_INTEREST_KEY = 'analytics.article.interests';
const SEARCH_INTEREST_KEY = 'analytics.search.keywords';

interface ArticleInterestRecord {
  categoryScores: Record<string, number>;
  tagScores: Record<string, number>;
}

interface SearchKeywordRecord {
  keyword: string;
  count: number;
}

function getArticleInterestRecord(): ArticleInterestRecord {
  return getStorage<ArticleInterestRecord>(ARTICLE_INTEREST_KEY, {
    categoryScores: {},
    tagScores: {}
  });
}

function setArticleInterestRecord(record: ArticleInterestRecord): void {
  setStorage(ARTICLE_INTEREST_KEY, record, 60 * 60 * 24 * 365);
}

export function trackArticleInterest(article?: Pick<ArticleSummary, 'categoryId' | 'tagIds'> | null): void {
  if (!article) return;
  const preferences = getUserPreferences();
  if (!preferences.personalizedRecommendationEnabled) return;
  const record = getArticleInterestRecord();
  const categoryId = Number(article.categoryId);
  if (Number.isFinite(categoryId) && categoryId > 0) {
    record.categoryScores[String(categoryId)] = (record.categoryScores[String(categoryId)] || 0) + 3;
  }
  const tagIds = typeof article.tagIds === 'string'
    ? article.tagIds.split(',').map((item) => item.trim()).filter(Boolean)
    : [];
  tagIds.forEach((tagId) => {
    record.tagScores[tagId] = (record.tagScores[tagId] || 0) + 1;
  });
  setArticleInterestRecord(record);
}

export function trackSearchKeyword(keyword: string): void {
  const value = keyword.trim();
  if (!value) return;
  const preferences = getUserPreferences();
  if (!preferences.personalizedRecommendationEnabled) return;
  const list = getStorage<SearchKeywordRecord[]>(SEARCH_INTEREST_KEY, []);
  const next = [...list];
  const index = next.findIndex((item) => item.keyword === value);
  if (index >= 0) {
    next[index] = { keyword: value, count: next[index].count + 1 };
  } else {
    next.push({ keyword: value, count: 1 });
  }
  setStorage(
    SEARCH_INTEREST_KEY,
    next.sort((a, b) => b.count - a.count).slice(0, 12),
    60 * 60 * 24 * 365
  );
}

export function scoreArticleInterest(article?: Pick<ArticleSummary, 'categoryId' | 'tagIds'> | null): number {
  if (!article || !getUserPreferences().personalizedRecommendationEnabled) return 0;
  const record = getArticleInterestRecord();
  let score = 0;
  const categoryId = Number(article.categoryId);
  if (Number.isFinite(categoryId) && categoryId > 0) {
    score += record.categoryScores[String(categoryId)] || 0;
  }
  if (typeof article.tagIds === 'string') {
    article.tagIds
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
      .forEach((tagId) => {
        score += record.tagScores[tagId] || 0;
      });
  }
  return score;
}

export function scoreCategoryInterest(categoryId?: number): number {
  if (!categoryId || !getUserPreferences().personalizedRecommendationEnabled) return 0;
  const record = getArticleInterestRecord();
  return record.categoryScores[String(categoryId)] || 0;
}

export function sortItemsByInterest<T extends Pick<ArticleSummary, 'categoryId' | 'tagIds'>>(items: T[]): T[] {
  return [...items].sort((a, b) => scoreArticleInterest(b) - scoreArticleInterest(a));
}

export async function reportAnalyticsEvent(options: {
  eventType: string;
  pagePath?: string;
  targetType?: string;
  targetId?: number;
  metadata?: Record<string, unknown>;
}): Promise<void> {
  if (!getUserPreferences().analyticsEnabled) return;
  try {
    await createAnalyticsEvent({
      eventType: options.eventType,
      pagePath: options.pagePath,
      targetType: options.targetType,
      targetId: options.targetId,
      metadataJson: options.metadata ? JSON.stringify(options.metadata) : undefined,
      platform: 'mini',
      appVersion: '0.1.0'
    });
  } catch {
    // 埋点失败不影响主流程
  }
}
