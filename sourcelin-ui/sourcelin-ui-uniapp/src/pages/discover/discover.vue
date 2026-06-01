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
            <text class="discover__section-kicker">Trending</text>
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
          <view class="discover__featured-arrow">
            <uni-icons type="right" size="16" color="rgba(255,255,255,0.8)" />
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
          <uni-icons class="discover__hot-arrow" type="right" size="15" color="currentColor" />
        </view>
      </view>

      <!-- 核心分类板块 -->
      <view class="discover__section discover__section--categories">
        <view class="discover__section-head">
          <view>
            <text class="discover__section-kicker">Categories</text>
            <text class="discover__section-title">核心分类</text>
          </view>
          <view class="discover__more sl-button sl-button--ghost sl-button--sm" @tap="goSearch">搜索</view>
        </view>
        <view v-if="categories.length > 0" class="discover__category-grid">
          <view
            v-for="(item, idx) in categories"
            :key="item.id"
            class="discover__category-card"
            :class="`discover__category-card--${idx % 4}`"
            @tap="goCategory(item.id)"
          >
            <view class="discover__category-icon">
              <uni-icons type="list" size="20" color="currentColor" />
            </view>
            <view class="discover__category-name">{{ item.name }}</view>
            <view class="discover__category-arrow">
              <uni-icons type="right" size="14" color="currentColor" />
            </view>
          </view>
        </view>
        <s-empty v-else-if="!loading" text="暂无分类" />
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view class="s-liquid-tabbar">
      <view class="s-liquid-tabbar__shell">
        <view
          v-for="item in liquidTabItems"
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
import { ref, watch } from 'vue';
import { onPageScroll, onPullDownRefresh, onShareAppMessage, onShow } from '@dcloudio/uni-app';
import { fetchCategoryList, fetchHotArticles } from '@/modules/article/api/article.api';
import type { ArticleSummary, CategoryItem } from '@/modules/article/types/article';
import { liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { reportAnalyticsEvent, scoreCategoryInterest, sortItemsByInterest } from '@/shared/utils/analytics';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';

const themeStore = useThemeStore();

const loading = ref(false);
const hotArticles = ref<ArticleSummary[]>([]);
const categories = ref<CategoryItem[]>([]);
const backToTopVisible = ref(false);
const activeTabPath = 'pages/discover/discover';

watch([hotArticles, categories], () => {
  applyH5Seo({
    title: buildSeoTitle('发现'),
    description: extractSeoSummary(
      hotArticles.value[0]?.title ? `浏览热门文章、核心分类与内容趋势，当前热门：${hotArticles.value[0].title}` : '',
      '浏览热门文章、核心分类与内容趋势。'
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

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});

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

  min-height: 100vh;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s ease;

  &.sl-theme--dark {
    --discover-glass-border: var(--sl-border-light);
    --discover-glass-highlight: rgba(255, 255, 255, 0.08);
    --discover-shadow: rgba(0, 0, 0, 0.18);
    background: var(--sl-page-bg, #09090b);

    .discover__search-bar {
      background:
        linear-gradient(145deg, rgba(24, 27, 38, 0.92), rgba(18, 20, 28, 0.88));
      border-color: var(--sl-border-light);
      color: var(--sl-text-muted);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.06),
        0 8rpx 24rpx rgba(0, 0, 0, 0.24);
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
      background:
        linear-gradient(135deg, rgba(99, 102, 241, 0.35) 0%, rgba(139, 92, 246, 0.2) 100%);
      border-color: rgba(99, 102, 241, 0.3);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        0 20rpx 48rpx rgba(99, 102, 241, 0.2);
    }

    .discover__featured-title {
      text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.6);
    }

    .discover__hot:active {
      background: rgba(39, 39, 42, 0.7);
    }

    .discover__section-title,
    .discover__hot-title {
      color: var(--sl-text-main);
    }

    .discover__hot-meta {
      color: var(--sl-text-sub);
    }

    .discover__hot-arrow {
      color: var(--sl-text-muted);
    }

    .discover__category-card {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .discover__category-card:active {
      background: rgba(39, 39, 42, 0.7);
    }

    .discover__category-name {
      color: var(--sl-text-main);
    }

    .discover__category-arrow {
      color: var(--sl-text-muted);
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
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--discover-glass-pure);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--discover-glass-highlight),
      var(--sl-glass-shadow);

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
    background:
      linear-gradient(135deg, rgba(59, 89, 255, 0.18) 0%, rgba(139, 92, 246, 0.1) 50%, rgba(236, 72, 153, 0.06) 100%),
      linear-gradient(180deg, var(--sl-panel-highlight), var(--sl-panel-lowlight));
    border: 1rpx solid rgba(99, 102, 241, 0.2);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 18rpx 42rpx rgba(99, 102, 241, 0.12);
    overflow: hidden;
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;

    &:active {
      transform: scale(0.985);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
        0 12rpx 28rpx rgba(99, 102, 241, 0.08);
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

  &__featured-arrow {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 52rpx;
    height: 52rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--discover-primary), var(--discover-primary-soft));
    flex-shrink: 0;
    box-shadow: 0 8rpx 20rpx rgba(99, 102, 241, 0.25);
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
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--discover-glass-pure);
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

  &__hot-arrow {
    flex-shrink: 0;
    color: rgba(75, 85, 99, 0.4);
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

  /* ─── 分类网格 ─── */
  &__category-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14rpx;
  }

  &__category-card {
    display: flex;
    align-items: center;
    gap: 16rpx;
    min-height: 100rpx;
    padding: 20rpx 20rpx;
    border-radius: 24rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--discover-glass-pure);
    border: 1rpx solid var(--discover-glass-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 12rpx 28rpx rgba(17, 24, 39, 0.04);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;
  }

  &__category-card:active {
    transform: scale(0.96);
  }

  &__category-icon {
    width: 52rpx;
    height: 52rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16rpx;
    flex-shrink: 0;
  }

  /* 四种色彩轮换 */
  &__category-card--0 .discover__category-icon {
    background: rgba(99, 102, 241, 0.12);
    color: #6366f1;
  }

  &__category-card--1 .discover__category-icon {
    background: rgba(236, 72, 153, 0.12);
    color: #ec4899;
  }

  &__category-card--2 .discover__category-icon {
    background: rgba(34, 197, 94, 0.12);
    color: #22c55e;
  }

  &__category-card--3 .discover__category-icon {
    background: rgba(245, 158, 11, 0.12);
    color: #f59e0b;
  }

  &__category-name {
    flex: 1;
    min-width: 0;
    color: var(--discover-text-main);
    font-size: 27rpx;
    font-weight: 700;
  }

  &__category-arrow {
    flex-shrink: 0;
    color: rgba(75, 85, 99, 0.4);
  }
}
</style>
