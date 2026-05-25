import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const READING_PROGRESS_TTL_SECONDS = 30 * 24 * 60 * 60;

export interface ArticleReadingProgress {
  articleId: number;
  scrollTop: number;
  readingMs: number;
  updatedAt: number;
}

function createStorageKey(articleId: number): string {
  return `article.read.progress.${articleId}`;
}

export function getArticleReadingProgress(articleId: number): ArticleReadingProgress | null {
  if (!articleId) return null;
  return getStorage<ArticleReadingProgress | null>(createStorageKey(articleId), null);
}

export function saveArticleReadingProgress(progress: ArticleReadingProgress): void {
  if (!progress.articleId) return;
  if (progress.scrollTop < 120 && progress.readingMs < 5000) {
    removeArticleReadingProgress(progress.articleId);
    return;
  }
  setStorage(createStorageKey(progress.articleId), progress, READING_PROGRESS_TTL_SECONDS);
}

export function removeArticleReadingProgress(articleId: number): void {
  if (!articleId) return;
  removeStorage(createStorageKey(articleId));
}
