import { computed, ref } from 'vue';
import type { PageResult } from '@/shared/types/api';
import type { ArticleSummary } from '../types/article';

export type ArticlePageFetcher = (
  page: number,
  pageSize: number
) => Promise<PageResult<ArticleSummary>>;

export function useArticlePaging(fetcher: ArticlePageFetcher, initialPageSize = 10) {
  const items = ref<ArticleSummary[]>([]);
  const page = ref(1);
  const pageSize = ref(initialPageSize);
  const total = ref(0);
  const loading = ref(false);
  const finished = ref(false);

  const isEmpty = computed(() => !loading.value && items.value.length === 0);

  async function fetchPage(targetPage: number): Promise<void> {
    if (loading.value) return;
    loading.value = true;
    try {
      const result = await fetcher(targetPage, pageSize.value);
      const nextItems = result.items || [];
      items.value = targetPage === 1 ? nextItems : items.value.concat(nextItems);
      page.value = result.page || targetPage;
      total.value = result.total || 0;
      finished.value = page.value >= (result.totalPages || 1) || items.value.length >= total.value;
    } finally {
      loading.value = false;
    }
  }

  async function refresh(): Promise<void> {
    page.value = 1;
    total.value = 0;
    finished.value = false;
    await fetchPage(1);
  }

  async function loadMore(): Promise<void> {
    if (loading.value || finished.value) return;
    await fetchPage(page.value + 1);
  }

  return {
    items,
    page,
    pageSize,
    total,
    loading,
    finished,
    isEmpty,
    refresh,
    loadMore
  };
}
