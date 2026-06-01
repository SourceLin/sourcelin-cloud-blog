<template>
  <view class="article-list s-container" :class="themeStore.themeClass">
    <s-loading :visible="loading && items.length === 0" />
    <s-empty v-if="isEmpty" text="暂无文章" />

    <view v-else class="article-list__items">
      <view
        v-for="item in items"
        :key="item.id"
        class="article-list__item s-card s-card--interactive"
        @tap="goDetail(item.id)"
      >
        <view class="article-list__title s-ellipsis-2">{{ item.title }}</view>
        <view class="article-list__summary s-ellipsis-2">{{ item.summary || '暂无摘要' }}</view>
        <view class="article-list__meta">
          <text>{{ item.categoryName || '默认分类' }}</text>
          <text>{{ item.viewCount || 0 }} 阅读</text>
        </view>
      </view>

      <s-loading v-if="loading && items.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
      <view v-else-if="finished && items.length > 0" class="article-list__footer">已经到底了</view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { onLoad, onPageScroll, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { fetchArticlePage, fetchTagArticles } from '@/modules/article/api/article.api';
import { useArticlePaging } from '@/modules/article/composables/useArticlePaging';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';

const query = {
  categoryId: undefined as number | undefined,
  tagId: undefined as number | undefined,
  orderBy: undefined as 'view_count' | 'create_time' | undefined
};

const { items, loading, finished, isEmpty, refresh, loadMore } = useArticlePaging((page, pageSize) =>
  query.tagId
    ? fetchTagArticles(query.tagId, page, pageSize)
    : fetchArticlePage({
        page,
        pageSize,
        categoryId: query.categoryId,
        orderBy: query.orderBy
      })
);
const backToTopVisible = ref(false);
const themeStore = useThemeStore();
const pageTitle = computed(() => {
  if (query.tagId) return '标签文章';
  if (query.categoryId) return '分类文章';
  if (query.orderBy === 'view_count') return '热门文章';
  return '文章列表';
});

watch([pageTitle, items], () => {
  applyH5Seo({
    title: buildSeoTitle(pageTitle.value),
    description: extractSeoSummary(
      items.value[0]?.summary,
      `${pageTitle.value}，当前已加载 ${items.value.length} 篇内容。`,
      '浏览文章列表与内容摘要。'
    ),
    keywords: [pageTitle.value, '文章列表', '博客', '阅读']
  });
}, { immediate: true });

function parseNumber(value: unknown): number | undefined {
  const n = Number(value);
  return Number.isFinite(n) && n > 0 ? n : undefined;
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

onLoad((options) => {
  query.categoryId = parseNumber(options?.categoryId);
  query.tagId = parseNumber(options?.tagId);
  query.orderBy = options?.orderBy === 'view_count' ? 'view_count' : undefined;
  refresh();
});

onShow(() => {
  themeStore.syncNativeArea();
});

onPullDownRefresh(() => {
  refresh().finally(() => uni.stopPullDownRefresh());
});

onReachBottom(() => {
  loadMore();
});

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});
</script>

<style lang="scss" scoped>
.article-list {
  min-height: 100vh;

  &__items {
    display: flex;
    flex-direction: column;
    gap: $space-md;
  }

  &__title {
    font-size: 32rpx;
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
    font-size: 24rpx;
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
