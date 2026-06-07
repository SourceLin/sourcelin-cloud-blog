<template>
  <view class="discover" :class="themeStore.themeClass">
    <s-loading :visible="loading && hotArticles.length === 0" />

    <!-- 搜索条 -->
    <view class="discover__search-bar" @tap="goSearch">
      <uni-icons type="search" size="18" color="currentColor" />
      <text class="discover__search-placeholder">搜索文章、分类或标签</text>
    </view>

    <view class="discover__content">
      <!-- 热门文章板块 -->
      <view class="discover__section discover__section--hot">
        <view class="discover__section-head">
          <view>
            <text class="discover__section-title">热门文章</text>
          </view>
          <view class="discover__more sl-button sl-button--ghost sl-button--sm" @tap="goHotList">查看全部</view>
        </view>
        <s-empty v-if="!loading && hotArticles.length === 0" text="暂无热门文章" />

        <!-- 第一名：大号特色卡片 -->
        <view
          v-if="hotArticles.length > 0"
          class="discover__featured"
          @tap="goDetail(hotArticles[0].id)"
        >
          <view class="discover__featured-badge">
            <text class="discover__featured-badge-num">1</text>
            <text class="discover__featured-badge-label">TOP</text>
          </view>
          <view class="discover__featured-body">
            <view class="discover__featured-title s-ellipsis-2">{{ hotArticles[0].title }}</view>
            <view class="discover__featured-meta">
              <view class="discover__meta-chip">
                <uni-icons type="eye" size="14" color="currentColor" />
                <text>{{ hotArticles[0].viewCount || 0 }}</text>
              </view>
              <view class="discover__meta-chip discover__meta-chip--like">
                <uni-icons type="heart" size="14" color="currentColor" />
                <text>{{ hotArticles[0].likeCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 第 2～5 名列表 -->
        <view
          v-for="(item, index) in hotArticles.slice(1)"
          :key="item.id"
          class="discover__hot"
          @tap="goDetail(item.id)"
        >
          <view class="discover__rank" :class="'discover__rank--' + (index + 2)">{{ index + 2 }}</view>
          <view class="discover__hot-body">
            <view class="discover__hot-title s-ellipsis-2">{{ item.title }}</view>
            <view class="discover__hot-meta">
              <view class="discover__meta-chip">
                <uni-icons type="eye" size="13" color="currentColor" />
                <text>{{ item.viewCount || 0 }}</text>
              </view>
              <view class="discover__meta-chip discover__meta-chip--like">
                <uni-icons type="heart" size="13" color="currentColor" />
                <text>{{ item.likeCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 分类漫游板块 -->
      <view class="discover__section discover__section--categories">
        <view class="discover__section-head">
          <view>
            <text class="discover__section-title">分类漫游</text>
          </view>
          <view class="discover__more sl-button sl-button--ghost sl-button--sm" @tap="goSearch">搜索</view>
        </view>
        <view v-if="categories.length > 0" class="discover__category-mosaic">
          <!-- 第 1 个分类：跨列大卡 -->
          <view
            v-if="categories[0]"
            class="discover__category-hero discover__category-hero--0"
            @tap="goCategory(categories[0].id)"
          >
            <view class="discover__category-hero-glow" />
            <view class="discover__category-hero-body">
              <view class="discover__category-hero-badge">
                <uni-icons type="list" size="22" color="#fff" />
              </view>
              <text class="discover__category-hero-name">{{ categories[0].name }}</text>
            </view>
          </view>
          <!-- 第 2～5 个分类：小卡 -->
          <view
            v-for="(item, idx) in categories.slice(1)"
            :key="item.id"
            class="discover__category-tile"
            :class="`discover__category-tile--${idx % 4}`"
            @tap="goCategory(item.id)"
          >
            <view class="discover__category-tile-dot" />
            <text class="discover__category-tile-name">{{ item.name }}</text>
          </view>
        </view>
        <s-empty v-else-if="!loading" text="暂无分类" />
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view class="s-liquid-tabbar">
      <view class="s-liquid-tabbar__shell">
        <view
          v-for="item in visibleLiquidTabItems"
          :key="item.path"
          class="s-liquid-tabbar__item"
          :class="{ 's-liquid-tabbar__item--active': item.path === activeTabPath }"
          @tap="switchLiquidTab(item.path, activeTabPath)"
        >
          <view class="s-liquid-tabbar__icon-wrap">
            <image
              class="s-liquid-tabbar__icon"
              :src="item.path === activeTabPath ? item.activeIcon : item.icon"
              mode="aspectFit"
            />
          </view>
          <text class="s-liquid-tabbar__text">{{ item.text }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { onPageScroll, onPullDownRefresh, onShareAppMessage, onShow } from '@dcloudio/uni-app';
import { fetchCategoryList, fetchHotArticles } from '@/modules/article/api/article.api';
import type { ArticleSummary, CategoryItem } from '@/modules/article/types/article';
import { liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { reportAnalyticsEvent, scoreCategoryInterest, sortItemsByInterest } from '@/shared/utils/analytics';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';
import { useBackToTop } from '@/shared/composables/useBackToTop';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

const themeStore = useThemeStore();
const { resolveLiquidTabs } = useMiniAccess();

const loading = ref(false);
const hotArticles = ref<ArticleSummary[]>([]);
const categories = ref<CategoryItem[]>([]);
const { backToTopVisible, handlePageScroll } = useBackToTop();
const activeTabPath = 'pages/discover/discover';
const visibleLiquidTabItems = computed(() => resolveLiquidTabs(liquidTabItems));

watch([hotArticles, categories], () => {
  applyH5Seo({
    title: buildSeoTitle('发现'),
    description: extractSeoSummary(
      hotArticles.value[0]?.title ? `浏览热门文章、分类漫游与内容趋势，当前热门：${hotArticles.value[0].title}` : '',
      '浏览热门文章、分类漫游与内容趋势。'
    ),
    keywords: ['发现', '热门文章', '分类', '标签', ...categories.value.map((item) => item.name)]
  });
}, { immediate: true });

onShow(() => {
  themeStore.syncNativeArea();
  if (hotArticles.value.length === 0 && categories.value.length === 0) {
    loadDiscover();
  }
});

onPullDownRefresh(() => {
  loadDiscover().finally(() => uni.stopPullDownRefresh());
});

onPageScroll(handlePageScroll);

async function loadDiscover(): Promise<void> {
  loading.value = true;
  try {
    const [hot, categoryList] = await Promise.all([
      fetchHotArticles(1, 5),
      fetchCategoryList()
    ]);
    hotArticles.value = sortItemsByInterest(hot.items || []);
    categories.value = [...(categoryList || [])]
      .sort((a, b) => scoreCategoryInterest(b.id) - scoreCategoryInterest(a.id))
      .slice(0, 5);
    void reportAnalyticsEvent({
      eventType: 'discover_view',
      pagePath: '/pages/discover/discover',
      metadata: {
        hotCount: hotArticles.value.length,
        categoryCount: categories.value.length
      }
    });
  } finally {
    loading.value = false;
  }
}

function goSearch(): void {
  uni.navigateTo({ url: '/pages-article/search/search' });
}

function goHotList(): void {
  uni.navigateTo({ url: '/pages-article/list/list?orderBy=view_count' });
}

function goCategory(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/list/list?categoryId=${id}` });
}

function goDetail(id?: number): void {
  if (!id) return;
  void reportAnalyticsEvent({
    eventType: 'discover_open_article',
    pagePath: '/pages/discover/discover',
    targetType: 'article',
    targetId: id
  });
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

onShareAppMessage(() => {
  void reportAnalyticsEvent({
    eventType: 'discover_share',
    pagePath: '/pages/discover/discover',
    metadata: {
      hotCount: hotArticles.value.length,
      categoryCount: categories.value.length
    }
  });
  return {
    title: '发现热门文章与分类 - 圆圈博客',
    path: '/pages/discover/discover'
  };
});
</script>

<style lang="scss" scoped>
.discover {
  --discover-primary: var(--sl-color-primary);
  --discover-primary-soft: var(--sl-color-primary-soft);
  --discover-text-main: var(--sl-text-main);
  --discover-text-sub: var(--sl-text-sub);
  --discover-glass-pure: var(--sl-bg-glass-pure);
  --discover-glass-tint: var(--sl-bg-glass-tint);
  --discover-glass-border: rgba(255, 255, 255, 0.72);
  --discover-glass-highlight: rgba(255, 255, 255, 0.86);
  --discover-shadow: rgba(17, 24, 39, 0.08);
  --discover-accent-soft: rgba(59, 89, 255, 0.12);
  --discover-accent-strong: rgba(59, 89, 255, 0.2);
  --discover-surface: linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)), var(--discover-glass-pure);
  --discover-surface-shadow:
    inset 0 1rpx 0 var(--discover-glass-highlight),
    0 12rpx 30rpx rgba(17, 24, 39, 0.06);

  min-height: 100vh;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s ease;

  &.sl-theme--dark {
    --discover-glass-border: var(--sl-border-light);
    --discover-glass-highlight: rgba(255, 255, 255, 0.08);
    --discover-shadow: rgba(0, 0, 0, 0.18);
    --discover-accent-soft: rgba(105, 129, 255, 0.16);
    --discover-accent-strong: rgba(105, 129, 255, 0.24);
    --discover-surface: var(--sl-card-glass-bg);
    --discover-surface-shadow: var(--sl-shadow-soft);
    background: var(--sl-page-bg, #080d18);

    .discover__search-bar {
      background:
        linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
        var(--sl-bg-glass-pure);
      border-color: var(--sl-border-light);
      color: var(--sl-text-muted);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__search-placeholder {
      color: var(--sl-text-muted);
    }

    .discover__section,
    .discover__hot {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__featured {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__featured-title {
      text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.6);
    }

    .discover__hot:active {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.8), rgba(18, 27, 46, 0.52)),
        rgba(18, 27, 46, 0.62);
    }

    .discover__section-title,
    .discover__hot-title {
      color: var(--sl-text-main);
    }

    .discover__hot-meta {
      color: var(--sl-text-sub);
    }

    .discover__category-hero--0 {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__category-hero-name {
      text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.4);
    }

    .discover__category-tile {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__category-tile:active {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.8), rgba(18, 27, 46, 0.52)),
        rgba(18, 27, 46, 0.62);
    }

    .discover__category-tile-name {
      color: var(--sl-text-main);
    }
  }

  /* ─── 顶部搜索条 ─── */
  &__search-bar {
    position: sticky;
    top: 0;
    z-index: 80;
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin: 0 30rpx 20rpx;
    box-sizing: border-box;
    min-height: 88rpx;
    padding: 16rpx 28rpx;
    border-radius: 999rpx;
    color: var(--discover-text-sub);
    font-size: 28rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0.82)),
      var(--discover-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.96),
      0 8rpx 24rpx rgba(17, 24, 39, 0.06);
    transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */

    &:active {
      transform: scale(0.985);
    }
  }

  &__search-placeholder {
    color: var(--discover-text-sub);
    font-size: 28rpx;
  }

  /* ─── 内容区 ─── */
  &__content {
    position: relative;
    margin-top: 0;
    padding: 0 30rpx;
  }

  &__section {
    margin-bottom: 28rpx;
    padding: 28rpx 24rpx;
    border-radius: 32rpx;
    background: var(--discover-surface);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow: var(--discover-surface-shadow);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(16rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(16rpx) saturate(1.2);
    /* #endif */
  }

  &__section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 22rpx;
  }

  &__section-kicker {
    display: block;
    color: var(--discover-primary);
    font-size: 22rpx;
    font-weight: 800;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    opacity: 0.85;
  }

  &__section-title {
    display: block;
    margin-top: 4rpx;
    color: var(--discover-text-main);
    font-size: 36rpx;
    font-weight: 800;
    letter-spacing: -0.01em;
  }

  &__more {
    min-width: 120rpx;
  }

  /* ─── Featured 第一名卡片 ─── */
  &__featured {
    position: relative;
    display: flex;
    align-items: center;
    gap: 20rpx;
    padding: 28rpx 24rpx;
    margin-bottom: 18rpx;
    border-radius: 28rpx;
    background: var(--discover-surface);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 18rpx 42rpx rgba(17, 24, 39, 0.08);
    overflow: hidden;
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;

    &:active {
      transform: scale(0.985);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
        0 12rpx 28rpx rgba(17, 24, 39, 0.06);
    }
  }

  &__featured-badge {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 72rpx;
    height: 72rpx;
    border-radius: 22rpx;
    background: linear-gradient(135deg, #FFD700 0%, #FF8C00 100%);
    color: #fff;
    box-shadow:
      0 8rpx 20rpx rgba(255, 140, 0, 0.35),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
    flex-shrink: 0;
  }

  &__featured-badge-num {
    font-size: 28rpx;
    font-weight: 900;
    line-height: 1;
  }

  &__featured-badge-label {
    font-size: 16rpx;
    font-weight: 800;
    letter-spacing: 0.08em;
    line-height: 1;
    margin-top: 2rpx;
  }

  &__featured-body {
    flex: 1;
    min-width: 0;
  }

  &__featured-title {
    color: var(--discover-text-main);
    font-size: 33rpx;
    font-weight: 800;
    line-height: 1.4;
    letter-spacing: -0.01em;
  }

  &__featured-meta {
    display: flex;
    gap: 12rpx;
    margin-top: 12rpx;
  }

  /* ─── 热门列表（2～5 名） ─── */
  &__hot {
    position: relative;
    display: flex;
    align-items: center;
    gap: 18rpx;
    margin-bottom: 14rpx;
    padding: 22rpx 20rpx;
    border-radius: 24rpx;
    background: var(--discover-surface);
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.8),
      0 10rpx 24rpx var(--discover-shadow);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.2s ease, box-shadow 0.2s ease;
    overflow: hidden;
  }

  &__hot:active {
    transform: scale(0.985);
  }

  &__hot:last-child {
    margin-bottom: 0;
  }

  &__rank {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 48rpx;
    height: 48rpx;
    border-radius: 16rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      rgba(99, 102, 241, 0.06);
    color: var(--discover-text-sub);
    font-size: 24rpx;
    font-weight: 800;
    box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.7);
    flex-shrink: 0;

    &--2 {
      background: linear-gradient(135deg, #C0C0C0 0%, #9E9E9E 100%);
      color: #fff;
      box-shadow: 0 6rpx 14rpx rgba(158, 158, 158, 0.28);
    }

    &--3 {
      background: linear-gradient(135deg, #F4A460 0%, #CD853F 100%);
      color: #fff;
      box-shadow: 0 6rpx 14rpx rgba(205, 133, 63, 0.3);
    }
  }

  &__hot-body {
    flex: 1;
    min-width: 0;
  }

  &__hot-title {
    color: var(--discover-text-main);
    font-size: 29rpx;
    font-weight: 700;
    line-height: 1.45;
  }

  &__hot-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 10rpx;
    margin-top: 10rpx;
    color: rgba(75, 85, 99, 0.7);
    font-size: 21rpx;
  }

  &__meta-chip {
    display: inline-flex;
    align-items: center;
    gap: 6rpx;
    min-height: 40rpx;
    padding: 0 14rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    font-size: 21rpx;
  }

  &__meta-chip--like {
    color: var(--discover-primary);
  }

  /* ─── 分类磁贴布局 ─── */
  &__category-mosaic {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14rpx;
  }

  /* 大卡：跨 2 列，渐变叙事 */
  &__category-hero {
    grid-column: 1 / -1;
    position: relative;
    display: flex;
    align-items: center;
    gap: 20rpx;
    padding: 32rpx 28rpx;
    border-radius: 28rpx;
    overflow: hidden;
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;

    &--0 {
      background: var(--discover-surface);
      border: 1rpx solid var(--discover-glass-border);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
        0 18rpx 42rpx rgba(17, 24, 39, 0.08);
    }

    &:active {
      transform: scale(0.985);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
        0 12rpx 28rpx rgba(17, 24, 39, 0.06);
    }
  }

  &__category-hero-glow {
    position: absolute;
    top: -40%;
    right: -10%;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(59, 89, 255, 0.16) 0%, transparent 70%);
    pointer-events: none;
  }

  &__category-hero-body {
    display: flex;
    align-items: center;
    gap: 18rpx;
    position: relative;
    z-index: 1;
  }

  &__category-hero-badge {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 64rpx;
    height: 64rpx;
    border-radius: 20rpx;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    box-shadow:
      0 8rpx 20rpx rgba(99, 102, 241, 0.35),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.25);
    flex-shrink: 0;
  }

  &__category-hero-name {
    color: var(--discover-text-main);
    font-size: 34rpx;
    font-weight: 800;
    letter-spacing: -0.01em;
  }

  /* 小卡：色系渐变 + 弥散光点 */
  &__category-tile {
    position: relative;
    display: flex;
    align-items: center;
    gap: 14rpx;
    padding: 24rpx 22rpx;
    border-radius: 24rpx;
    overflow: hidden;
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;

    background: var(--discover-surface);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 10rpx 24rpx rgba(17, 24, 39, 0.04);

    &:active {
      transform: scale(0.96);
    }

    &--0 {
      border-color: rgba(236, 72, 153, 0.14);
    }

    &--1 {
      border-color: rgba(34, 197, 94, 0.14);
    }

    &--2 {
      border-color: rgba(245, 158, 11, 0.14);
    }

    &--3 {
      border-color: rgba(99, 102, 241, 0.14);
    }
  }

  &__category-tile-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__category-tile--0 &__category-tile-dot {
    background: #ec4899;
    box-shadow: 0 4rpx 12rpx rgba(236, 72, 153, 0.4);
  }

  &__category-tile--1 &__category-tile-dot {
    background: #22c55e;
    box-shadow: 0 4rpx 12rpx rgba(34, 197, 94, 0.4);
  }

  &__category-tile--2 &__category-tile-dot {
    background: #f59e0b;
    box-shadow: 0 4rpx 12rpx rgba(245, 158, 11, 0.4);
  }

  &__category-tile--3 &__category-tile-dot {
    background: #6366f1;
    box-shadow: 0 4rpx 12rpx rgba(99, 102, 241, 0.4);
  }

  &__category-tile-name {
    color: var(--discover-text-main);
    font-size: 27rpx;
    font-weight: 700;
  }
}
</style>
