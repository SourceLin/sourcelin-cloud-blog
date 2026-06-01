import { computed, ref } from 'vue';
import { fetchHome } from '../api/article.api';
import { fetchMessagePage } from '@/modules/message/api/message.api';
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
  const announcementNotices = ref<string[]>([]);

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

  /**
   * 首页公告优先级链（与 Web 前台 useHomePage 对齐）：
   * 1. announcement 表数据（/front/messages）
   * 2. web config notices 兜底
   */
  const notices = computed<string[]>(() => {
    if (announcementNotices.value.length) {
      return announcementNotices.value;
    }
    return siteInfo.value?.notices?.filter(Boolean) || [];
  });

  async function loadAnnouncements(): Promise<void> {
    try {
      const result = await fetchMessagePage({ channel: 'system', page: 1, pageSize: 10 });
      announcementNotices.value = (result.items || [])
        .map((item) => String(item.title || item.content || '').trim())
        .filter(Boolean)
        .slice(0, 8);
    } catch {
      announcementNotices.value = [];
    }
  }

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
    return Promise.all([load(1), loadAnnouncements()]).then(() => undefined);
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
    notices,
    featured,
    feedItems,
    finished,
    isEmpty,
    refresh,
    loadMore
  };
}
