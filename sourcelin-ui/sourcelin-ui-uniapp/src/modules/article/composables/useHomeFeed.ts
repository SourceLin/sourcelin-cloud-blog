import { computed, ref } from 'vue';
import { fetchHome } from '../api/article.api';
import type { ArticleSummary, CategoryItem, FrontSiteInfo, TagItem } from '../types/article';
import { scoreCategoryInterest, sortItemsByInterest } from '@/shared/utils/analytics';

export function useHomeFeed() {
  const loading = ref(false);
  const latest = ref<ArticleSummary[]>([]);
  const recommend = ref<ArticleSummary[]>([]);
  const carousel = ref<ArticleSummary[]>([]);
  const categories = ref<CategoryItem[]>([]);
  const tags = ref<TagItem[]>([]);
  const siteInfo = ref<FrontSiteInfo | undefined>();
  const page = ref(1);
  const pageSize = ref(10);
  const totalPages = ref(1);

  const featured = computed(() => carousel.value[0] || recommend.value[0] || latest.value[0]);
  const feedItems = computed(() => {
    const usedId = featured.value?.id;
    const pool = sortItemsByInterest([...recommend.value, ...latest.value]);
    const seen = new Set<number>();
    return pool.filter((item) => {
      if (!item?.id || item.id === usedId || seen.has(item.id)) return false;
      seen.add(item.id);
      return true;
    });
  });
  const finished = computed(() => page.value >= totalPages.value);
  const isEmpty = computed(() => !loading.value && !featured.value && feedItems.value.length === 0);

  async function load(targetPage = 1): Promise<void> {
    if (loading.value) return;
    loading.value = true;
    try {
      const result = await fetchHome({ page: targetPage, pageSize: pageSize.value });
      siteInfo.value = result.siteInfo;
      recommend.value = sortItemsByInterest(result.recommend || []);
      carousel.value = result.carousel || [];
      categories.value = [...(result.categories || [])].sort((a, b) => scoreCategoryInterest(b.id) - scoreCategoryInterest(a.id));
      tags.value = result.tags || [];
      const latestPage = result.latest;
      const nextItems = sortItemsByInterest(latestPage.items || []);
      latest.value = targetPage === 1
        ? nextItems
        : latest.value.concat(nextItems);
      page.value = latestPage.page || targetPage;
      totalPages.value = latestPage.totalPages || 1;
    } finally {
      loading.value = false;
    }
  }

  function refresh(): Promise<void> {
    page.value = 1;
    totalPages.value = 1;
    return load(1);
  }

  function loadMore(): Promise<void> {
    if (finished.value) return Promise.resolve();
    return load(page.value + 1);
  }

  return {
    loading,
    latest,
    recommend,
    carousel,
    categories,
    tags,
    siteInfo,
    featured,
    feedItems,
    finished,
    isEmpty,
    refresh,
    loadMore
  };
}
