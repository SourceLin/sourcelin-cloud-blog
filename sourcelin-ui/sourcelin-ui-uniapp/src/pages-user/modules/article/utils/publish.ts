import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const ARTICLE_REFRESH_KEY = 'article.publish.refresh';

export function markArticleRefresh(): void {
  setStorage(ARTICLE_REFRESH_KEY, 1, 180);
}

export function consumeArticleRefresh(): boolean {
  const marked = getStorage<number>(ARTICLE_REFRESH_KEY, 0) === 1;
  if (marked) {
    removeStorage(ARTICLE_REFRESH_KEY);
  }
  return marked;
}
