<template>
  <view class="search s-container">
    <s-loading :visible="searched && loading && items.length === 0" text="正在检索内容..." />
    <view class="search__bar s-card">
      <input
        v-model="keyword"
        class="search__input"
        confirm-type="search"
        placeholder="搜索文章标题"
        @confirm="onSearch"
      >
      <view class="search__button" @tap="onSearch">搜索</view>
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
          <view class="search__section-action" @tap="clearSearchHistory">清空</view>
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
      v-if="searched && isEmpty"
      scene="search"
      title="没有找到相关文章"
      text="换个关键词，或者试试从分类和标签中继续查找。"
    />
    <s-empty
      v-else-if="!searched"
      scene="search"
      title="开始搜索"
      text="输入文章标题关键词，快速定位你想看的内容。"
    />

    <view v-else class="search__items">
      <view
        v-for="item in items"
        :key="item.id"
        class="search__item s-card"
        @tap="goDetail(item.id)"
      >
        <view class="search__title s-ellipsis-2">{{ item.title }}</view>
        <view class="search__summary s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
      </view>

      <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
      <view v-else-if="finished && items.length > 0" class="search__footer">已经到底了</view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { fetchCategoryList, fetchHotArticles, searchArticles } from '@/modules/article/api/article.api';
import { useArticlePaging } from '@/modules/article/composables/useArticlePaging';
import { showInfoToast } from '@/utils/feedback';
import { getStorage, removeStorage, setStorage } from '@/utils/storage';

const SEARCH_HISTORY_KEY = 'article.search.history';
const SEARCH_HISTORY_LIMIT = 8;
const keyword = ref('');
const searched = ref(false);
const searchHistory = ref<string[]>([]);
const hotKeywords = ref<string[]>([]);

const { items, loading, finished, isEmpty, refresh, loadMore } = useArticlePaging((page, pageSize) =>
  searchArticles(keyword.value.trim(), page, pageSize)
);
const backToTopVisible = ref(false);
const suggestions = computed(() => {
  const value = keyword.value.trim().toLowerCase();
  if (!value) return [];
  const pool = [...searchHistory.value, ...hotKeywords.value];
  return pool
    .filter((term, index, source) => source.indexOf(term) === index)
    .filter((term) => term.toLowerCase().includes(value) && term.trim() !== keyword.value.trim())
    .slice(0, 6);
});

function onSearch(): void {
  const value = keyword.value.trim();
  if (!value) {
    showInfoToast('请输入搜索关键词');
    return;
  }
  saveSearchHistory(value);
  searched.value = true;
  refresh();
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
    const [hotResult, categories] = await Promise.all([
      fetchHotArticles(1, 6),
      fetchCategoryList()
    ]);
    const articleTerms = (hotResult.items || [])
      .map((item) => item.title?.trim())
      .filter((item): item is string => !!item);
    const categoryTerms = categories
      .map((item) => item.name?.trim())
      .filter((item): item is string => !!item);
    hotKeywords.value = [...articleTerms, ...categoryTerms]
      .filter((term, index, source) => source.indexOf(term) === index)
      .slice(0, 8);
  } catch {
    hotKeywords.value = [];
  }
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
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
    background-color: $color-bg;
    padding: 0 $space-md;
    font-size: 26rpx;
  }

  &__button {
    border-radius: 999rpx;
    background-color: $color-primary;
    color: #fff;
    font-size: 26rpx;
    padding: 18rpx 28rpx;
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

  &__section-action {
    color: $color-primary;
    font-size: 23rpx;
    font-weight: 600;
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

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }
}
</style>
