<template>
  <view class="search s-container">
    <s-loading :visible="showLoading" text="正在检索内容..." />
    <view class="search__bar s-card">
      <input
        v-model="keyword"
        class="search__input"
        confirm-type="search"
        placeholder="搜索文章、分类或标签"
        placeholder-style="color: rgba(107, 114, 128, 0.58);"
        @confirm="onSearch"
      >
      <view class="search__button sl-button sl-button--primary sl-button--sm" @tap="onSearch">搜索</view>
    </view>

    <view v-if="suggestions.length > 0" class="search__section s-card">
      <view class="search__section-head">
        <view class="search__section-title">搜索建议</view>
      </view>
      <view class="search__chips">
        <view
          v-for="term in suggestions"
          :key="`suggestion-${term}`"
          class="search__chip search__chip--suggestion"
          @tap="applyKeyword(term)"
        >
          {{ term }}
        </view>
      </view>
    </view>

    <template v-else-if="!searched">
      <view v-if="searchHistory.length > 0" class="search__section s-card">
        <view class="search__section-head">
          <view class="search__section-title">最近搜索</view>
          <view class="search__section-action sl-button sl-button--ghost sl-button--sm" @tap="clearSearchHistory">清空</view>
        </view>
        <view class="search__chips">
          <view
            v-for="term in searchHistory"
            :key="`history-${term}`"
            class="search__chip"
            @tap="applyKeyword(term)"
          >
            {{ term }}
          </view>
        </view>
      </view>

      <view v-if="hotKeywords.length > 0" class="search__section s-card">
        <view class="search__section-head">
          <view class="search__section-title">热门搜索</view>
        </view>
        <view class="search__chips">
          <view
            v-for="term in hotKeywords"
            :key="`hot-${term}`"
            class="search__chip search__chip--hot"
            @tap="applyKeyword(term)"
          >
            {{ term }}
          </view>
        </view>
      </view>
    </template>

    <s-empty
      v-if="searched && !showLoading && !hasResults"
      scene="search"
      title="没有找到匹配内容"
      text="换个更短的关键词，或者试试从热门搜索继续浏览。"
    />
    <s-empty
      v-else-if="!searched"
      scene="search"
      title="开始搜索"
      text="输入文章、分类或标签关键词，快速定位你想看的内容。"
    />
    <view v-else class="search__results">
      <view v-if="categoryMatches.length > 0" class="search__section s-card">
        <view class="search__section-head">
          <view class="search__section-title">匹配分类</view>
          <view class="search__section-caption">{{ categoryMatches.length }} 个结果</view>
        </view>
        <view class="search__chips">
          <view
            v-for="item in categoryMatches"
            :key="`category-${item.id}`"
            class="search__chip search__chip--category"
            @tap="goCategory(item.id)"
          >
            # {{ item.name }}
          </view>
        </view>
      </view>

      <view v-if="tagMatches.length > 0" class="search__section s-card">
        <view class="search__section-head">
          <view class="search__section-title">匹配标签</view>
          <view class="search__section-caption">{{ tagMatches.length }} 个结果</view>
        </view>
        <view class="search__chips">
          <view
            v-for="item in tagMatches"
            :key="`tag-${item.id}`"
            class="search__chip search__chip--tag"
            @tap="goTag(item.id)"
          >
            # {{ item.name }}
          </view>
        </view>
      </view>

      <view v-if="items.length > 0" class="search__section-head search__section-head--articles">
        <view class="search__section-title">匹配文章</view>
        <view class="search__section-caption">{{ items.length }} 篇已加载</view>
      </view>

      <view v-if="items.length > 0" class="search__items">
        <view
          v-for="item in items"
          :key="item.id"
          class="search__item s-card s-card--interactive"
          @tap="goDetail(item.id)"
        >
          <view class="search__title s-ellipsis-2">{{ item.title }}</view>
          <view class="search__summary s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
          <view class="search__meta">
            <text>{{ item.categoryName || '默认分类' }}</text>
            <text>{{ item.viewCount || 0 }} 阅读</text>
          </view>
        </view>

        <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && items.length > 0" class="search__footer">已经到底了</view>
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import {
  fetchCategoryList,
  fetchSearchHotKeywords,
  fetchSearchSuggestions,
  fetchTagList,
  searchArticles,
  searchCategories,
  searchTags
} from '@/modules/article/api/article.api';
import { useArticlePaging } from '@/modules/article/composables/useArticlePaging';
import type { CategoryItem, TagItem } from '@/modules/article/types/article';
import { showInfoToast } from '@/utils/feedback';
import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const SEARCH_HISTORY_KEY = 'article.search.history';
const SEARCH_HISTORY_LIMIT = 8;
const keyword = ref('');
const searched = ref(false);
const searchHistory = ref<string[]>([]);
const hotKeywords = ref<string[]>([]);
const categoryMatches = ref<CategoryItem[]>([]);
const tagMatches = ref<TagItem[]>([]);
const facetLoading = ref(false);
const suggestionList = ref<string[]>([]);
let suggestionTimer: ReturnType<typeof setTimeout> | undefined;
let suggestionSerial = 0;

const { items, loading, finished, refresh, loadMore } = useArticlePaging((page, pageSize) =>
  searchArticles(keyword.value.trim(), page, pageSize)
);
const backToTopVisible = ref(false);
const hasResults = computed(() => items.value.length > 0 || categoryMatches.value.length > 0 || tagMatches.value.length > 0);
const showLoading = computed(() => searched.value && (loading.value || facetLoading.value) && !hasResults.value);
const suggestions = computed(() =>
  suggestionList.value.filter((term) => term.trim() !== keyword.value.trim()).slice(0, 6)
);

function onSearch(): void {
  const value = keyword.value.trim();
  if (!value) {
    showInfoToast('请输入搜索关键词');
    return;
  }
  saveSearchHistory(value);
  searched.value = true;
  refresh();
  void loadFacetResults(value);
}

function applyKeyword(value: string): void {
  keyword.value = value;
  onSearch();
}

function loadSearchHistory(): void {
  searchHistory.value = getStorage<string[]>(SEARCH_HISTORY_KEY, []);
}

function saveSearchHistory(value: string): void {
  const next = [value, ...searchHistory.value.filter((item) => item !== value)].slice(0, SEARCH_HISTORY_LIMIT);
  searchHistory.value = next;
  setStorage(SEARCH_HISTORY_KEY, next);
}

function clearSearchHistory(): void {
  searchHistory.value = [];
  removeStorage(SEARCH_HISTORY_KEY);
}

async function loadHotKeywords(): Promise<void> {
  try {
    const [articleTerms, categories, tags] = await Promise.all([
      fetchSearchHotKeywords(),
      fetchCategoryList(),
      fetchTagList()
    ]);
    const categoryTerms = categories
      .map((item) => item.name?.trim())
      .filter((item): item is string => !!item);
    const tagTerms = tags
      .map((item) => item.name?.trim())
      .filter((item): item is string => !!item);
    hotKeywords.value = [...articleTerms, ...categoryTerms, ...tagTerms]
      .filter((term, index, source) => source.indexOf(term) === index)
      .slice(0, 10);
  } catch {
    hotKeywords.value = [];
  }
}

async function loadFacetResults(value: string): Promise<void> {
  facetLoading.value = true;
  try {
    const [categories, tags] = await Promise.all([
      searchCategories(value, 1, 6),
      searchTags(value, 1, 8)
    ]);
    categoryMatches.value = categories.items || [];
    tagMatches.value = tags.items || [];
  } catch {
    categoryMatches.value = [];
    tagMatches.value = [];
  } finally {
    facetLoading.value = false;
  }
}

async function updateSuggestions(value: string): Promise<void> {
  const requestId = ++suggestionSerial;
  if (!value) {
    suggestionList.value = [];
    return;
  }
  try {
    const result = await fetchSearchSuggestions(value);
    if (requestId !== suggestionSerial) return;
    suggestionList.value = result;
  } catch {
    if (requestId !== suggestionSerial) return;
    suggestionList.value = [];
  }
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

function goCategory(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/list/list?categoryId=${id}` });
}

function goTag(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/list/list?tagId=${id}` });
}

onLoad((options) => {
  loadSearchHistory();
  loadHotKeywords();
  const value = typeof options?.keyword === 'string' ? decodeURIComponent(options.keyword) : '';
  if (value) {
    keyword.value = value;
    onSearch();
  }
});

onReachBottom(() => {
  if (searched.value) loadMore();
});

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});

watch(keyword, (value) => {
  const safeValue = value.trim();
  if (suggestionTimer) {
    clearTimeout(suggestionTimer);
    suggestionTimer = undefined;
  }
  if (!safeValue) {
    suggestionSerial += 1;
    suggestionList.value = [];
    return;
  }
  suggestionTimer = setTimeout(() => {
    void updateSuggestions(safeValue);
  }, 180);
});

onBeforeUnmount(() => {
  if (suggestionTimer) {
    clearTimeout(suggestionTimer);
  }
});
</script>

<style lang="scss" scoped>
.search {
  min-height: 100vh;

  &__bar {
    display: flex;
    align-items: center;
    gap: $space-sm;
    margin-bottom: $space-md;
    padding: $space-sm;
  }

  &__input {
    flex: 1;
    min-height: 72rpx;
    border-radius: 999rpx;
    background: rgba(248, 250, 252, 0.92);
    padding: 0 $space-md;
    font-size: 26rpx;
    color: $color-text;
  }

  &__button {
    min-width: 128rpx;
  }

  &__section {
    margin-bottom: $space-md;
  }

  &__section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
    margin-bottom: $space-sm;
  }

  &__section-title {
    color: $color-text;
    font-size: 28rpx;
    font-weight: 700;
  }

  &__section-caption {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__section-action {
    min-width: 104rpx;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
  }

  &__chip {
    max-width: 100%;
    padding: 14rpx 24rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.62);
    border: 1rpx solid rgba(255, 255, 255, 0.82);
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.2;
  }

  &__chip--hot {
    color: $color-primary;
    background: rgba(31, 111, 235, 0.08);
  }

  &__chip--suggestion {
    color: $color-text;
    background:
      linear-gradient(135deg, rgba(31, 111, 235, 0.08), rgba(143, 112, 255, 0.08)),
      rgba(255, 255, 255, 0.6);
  }

  &__chip--category {
    color: $color-primary;
    background:
      linear-gradient(135deg, rgba(31, 111, 235, 0.08), rgba(255, 255, 255, 0.72)),
      rgba(255, 255, 255, 0.68);
  }

  &__chip--tag {
    color: #6d4bff;
    background:
      linear-gradient(135deg, rgba(143, 112, 255, 0.12), rgba(255, 255, 255, 0.72)),
      rgba(255, 255, 255, 0.68);
  }

  &__results {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__section-head--articles {
    margin-bottom: -4rpx;
  }

  &__items {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__title {
    font-size: 31rpx;
    font-weight: 700;
    margin-bottom: $space-xs;
  }

  &__summary {
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.6;
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    color: $color-text-tertiary;
    font-size: 23rpx;
    margin-top: $space-md;
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }
}
</style>
