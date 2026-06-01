<template>
  <view class="home" :class="themeStore.themeClass">
    <s-loading :visible="loading && feedItems.length === 0" />

    <view
      class="home__hero-header"
      :style="headerPaddingStyle"
    >
      <view class="home__search-btn" @tap="goSearch">
        <uni-icons type="search" size="18" color="currentColor" />
      </view>
      <view :style="headerSpacerStyle" />
    </view>

    <view class="home__hero">
      <view class="home__hero-capsule-spacer" :style="headerSpacerStyle" />
      <image class="home__hero-bg" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
      <view class="home__hero-mask" />

      <view class="home__hero-body">
        <view class="home__hero-title-row">
          <view class="home__title">欢迎光临</view>
          <view class="home__hero-brand">
            <!-- <image class="home__hero-brand-logo" src="/static/logo/logo.png" mode="aspectFit" /> -->
            <view class="home__hero-brand-name">{{ siteEyebrow }}</view>
          </view>
        </view>
        <view class="home__subtitle s-ellipsis-2">{{ siteSubtitle }}</view>
      </view>
    </view>

    <view class="home__content">
      <!-- 轻公告栏 -->
      <view v-if="notices.length > 0" class="home__notice">
        <uni-icons type="sound" size="18" color="var(--home-primary)" />
        <swiper class="home__notice-swiper" vertical autoplay circular interval="4000">
          <swiper-item v-for="(notice, nIdx) in notices" :key="nIdx" class="home__notice-item">
            <text class="home__notice-text s-ellipsis">{{ notice }}</text>
          </swiper-item>
        </swiper>
      </view>

      <view class="home__showcase">
        <view class="home__showcase-mosaic">
          <view class="home__showcase-tile home__showcase-tile--0" @tap="goDiscover">
            <image class="home__showcase-slice" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
            <view class="home__showcase-overlay" />
            <view class="home__showcase-meta">
              <text class="home__showcase-kicker">Discover</text>
              <text class="home__showcase-title">发现热门</text>
              <text class="home__showcase-desc">趋势文章与核心分类</text>
            </view>
          </view>
          <view class="home__showcase-tile home__showcase-tile--1" @tap="goCommunity">
            <image class="home__showcase-slice" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
            <view class="home__showcase-overlay" />
            <view class="home__showcase-meta">
              <text class="home__showcase-kicker">Community</text>
              <text class="home__showcase-title">轻社区</text>
              <text class="home__showcase-desc">说说互动与树洞</text>
            </view>
          </view>
          <view class="home__showcase-tile home__showcase-tile--2" @tap="goHotList">
            <image class="home__showcase-slice" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
            <view class="home__showcase-overlay" />
            <view class="home__showcase-meta home__showcase-meta--compact">
              <text class="home__showcase-title">热门榜单</text>
            </view>
          </view>
          <view class="home__showcase-tile home__showcase-tile--3" @tap="goAbout">
            <image class="home__showcase-slice" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
            <view class="home__showcase-overlay" />
            <view class="home__showcase-meta home__showcase-meta--compact">
              <text class="home__showcase-title">关于本站</text>
            </view>
          </view>
        </view>
      </view>

      <s-empty v-if="isEmpty" text="暂无文章" />

      <view v-else class="home__feed">
        <!-- 精选卡片：统一采用精美模板 A 风格 -->
        <view v-if="featured" class="home__article-card home__article-card--template-a home__article-card--float" @tap="goDetail(featured.id)">
          <image v-if="imageUrl(featured.avatar) && !brokenCoverArticleIds.has(featured.id)" class="home__article-cover" :src="imageUrl(featured.avatar)" mode="aspectFill" @error="onCoverError(featured.id)" />
          <view v-else class="home__article-cover home__article-cover--fallback">
            <image class="home__article-logo" src="/static/logo/logo.png" mode="aspectFit" />
          </view>
          <view class="home__article-body">
            <view class="home__article-title s-ellipsis-2">{{ featured.title }}</view>
            <view class="home__tag-row">
              <view class="home__tag"># 精选</view>
              <view v-if="featured.categoryName" class="home__tag"># {{ featured.categoryName }}</view>
            </view>
            <view class="home__article-meta">
              <view class="home__article-meta-item">
                <uni-icons type="eye" size="15" color="currentColor" />
                <text>{{ featured.viewCount || 0 }} 阅读</text>
              </view>
              <view class="home__article-meta-item home__article-meta-item--like">
                <uni-icons type="heart" size="15" color="currentColor" />
                <text>{{ featured.likeCount || 0 }} 点赞</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 卡片流：按 A/B/C 三种杂志模板交替渲染 -->
        <view
          v-for="(item, index) in feedItems"
          :key="item.id"
          class="home__article-card"
          :class="`home__article-card--template-${index % 3 === 0 ? 'a' : (index % 3 === 1 ? 'b' : 'c')}`"
          @tap="goDetail(item.id)"
        >
          <!-- 仅模板 A 和模板 B 渲染图片（模板 C 纯文字留白） -->
          <block v-if="index % 3 !== 2">
            <image v-if="imageUrl(item.avatar) && !brokenCoverArticleIds.has(item.id)" class="home__article-cover" :src="imageUrl(item.avatar)" mode="aspectFill" @error="onCoverError(item.id)" />
            <view v-else class="home__article-cover home__article-cover--fallback">
              <image class="home__article-logo" src="/static/logo/logo.png" mode="aspectFit" />
            </view>
          </block>

          <view class="home__article-body">
            <view class="home__article-title s-ellipsis-2">{{ item.title }}</view>
            <view class="home__tag-row">
              <view class="home__tag"># {{ item.categoryName || '博客' }}</view>
              <view v-if="item.author" class="home__tag home__tag--soft">{{ item.author }}</view>
            </view>
            <view class="home__article-meta">
              <view class="home__article-meta-item">
                <uni-icons type="eye" size="15" color="currentColor" />
                <text>{{ item.viewCount || 0 }} 阅读</text>
              </view>
              <view class="home__article-meta-item home__article-meta-item--like">
                <uni-icons type="heart" size="15" color="currentColor" />
                <text>{{ item.likeCount || 0 }} 点赞</text>
              </view>
            </view>
          </view>
        </view>

        <s-loading v-if="loading && feedItems.length > 0" :visible="true" :fullpage="false" compact text="正在加载更多..." />
        <view v-else-if="finished && feedItems.length > 0" class="home__footer">已经到底了</view>
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
import { computed, ref, watch } from 'vue';
import { onPageScroll, onPullDownRefresh, onReachBottom, onShareAppMessage, onShow } from '@dcloudio/uni-app';
import { useHomeFeed } from '@/modules/article/composables/useHomeFeed';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';
import { useAppStore } from '@/stores/app';
import { reportAnalyticsEvent } from '@/shared/utils/analytics';
import { hideNativeTabbar, liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { normalizeAssetUrl } from '@/utils/url';

const { loading, siteInfo, notices, featured, feedItems, finished, isEmpty, refresh, loadMore } = useHomeFeed();
const themeStore = useThemeStore();
const appStore = useAppStore();
const backToTopVisible = ref(false);
const brokenCoverArticleIds = ref<Set<number>>(new Set());
const activeTabPath = 'pages/home/home';

function resolveCapsuleMetrics() {
  const windowInfo = appStore.systemInfo?.windowInfo ?? appStore.systemInfo?.legacy;
  return {
    menuButton: appStore.systemInfo?.menuButton,
    windowWidth: windowInfo?.windowWidth ?? 375
  };
}

const headerPaddingStyle = computed(() => {
  const { menuButton, windowWidth } = resolveCapsuleMetrics();
  const top = menuButton?.width
    ? `${menuButton.top}px`
    : 'calc(env(safe-area-inset-top) + 24rpx)';
  const paddingRight = menuButton?.width
    ? `${windowWidth - menuButton.left + 12}px`
    : '28rpx';
  const height = menuButton?.width
    ? `${menuButton.height}px`
    : '68rpx';
  return { top, paddingRight, height };
});

const headerSpacerStyle = computed(() => {
  const { menuButton } = resolveCapsuleMetrics();
  if (menuButton?.width) {
    return { height: `${menuButton.top + menuButton.height + 8}px` };
  }
  return { height: 'calc(env(safe-area-inset-top) + 24rpx + 68rpx)' };
});

function refreshCapsuleMetrics(): void {
  appStore.collectSystemInfo();
}

const siteEyebrow = computed(() =>
  siteInfo.value?.webName || siteInfo.value?.siteName || '圆圈博客'
);
const siteSubtitle = computed(() => siteInfo.value?.bio || siteInfo.value?.authorInfo || '记录技术、生活与灵感。');

watch([siteEyebrow, siteSubtitle], ([eyebrow, subtitle]) => {
  applyH5Seo({
    title: buildSeoTitle('首页', eyebrow),
    description: extractSeoSummary(subtitle, siteInfo.value?.authorInfo, notices.value[0], siteInfo.value?.title),
    keywords: [eyebrow, '首页', '博客', '技术', '生活', '灵感']
  });
}, { immediate: true });

function imageUrl(value?: string): string {
  return normalizeAssetUrl(value);
}

function onCoverError(articleId: number): void {
  if (!brokenCoverArticleIds.value.has(articleId)) {
    brokenCoverArticleIds.value = new Set([...brokenCoverArticleIds.value, articleId]);
  }
}

function goSearch(): void {
  uni.navigateTo({ url: '/pages-article/search/search' });
}

function goDiscover(): void {
  uni.switchTab({ url: '/pages/discover/discover' });
}

function goCommunity(): void {
  uni.switchTab({ url: '/pages/community/community' });
}

function goHotList(): void {
  uni.navigateTo({ url: '/pages-article/list/list?orderBy=view_count' });
}

function goAbout(): void {
  uni.navigateTo({ url: '/pages-about/index/index' });
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

onPullDownRefresh(() => {
  refresh().finally(() => uni.stopPullDownRefresh());
});

onReachBottom(() => {
  loadMore();
});

onPageScroll((event) => {
  backToTopVisible.value = event.scrollTop > 360;
});

onShow(() => {
  hideNativeTabbar();
  refreshCapsuleMetrics();
  themeStore.syncNativeArea();
});

onShareAppMessage(() => {
  void reportAnalyticsEvent({
    eventType: 'home_share',
    pagePath: '/pages/home/home',
    metadata: {
      featuredId: featured.value?.id,
      feedCount: feedItems.value.length
    }
  });
  return {
    title: `${siteEyebrow.value} - ${siteSubtitle.value}`,
    path: '/pages/home/home',
    imageUrl: '/static/backgrounds/home-bg.jpg'
  };
});

refresh();
</script>

<style lang="scss" scoped>
.home {
  --home-primary: var(--sl-color-primary);
  --home-primary-soft: var(--sl-color-primary-soft);
  --home-text-main: var(--sl-text-main);
  --home-text-sub: var(--sl-text-sub);
  --home-glass-pure: var(--sl-bg-glass-pure);
  --home-glass-tint: var(--sl-bg-glass-tint);
  --home-glass-border: rgba(255, 255, 255, 0.72);
  --home-glass-highlight: rgba(255, 255, 255, 0.86);
  --home-glass-shadow: rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s ease;

  &.sl-theme--dark {
    --home-glass-border: var(--sl-border-light);
    --home-glass-highlight: rgba(255, 255, 255, 0.08);
    --home-glass-shadow: rgba(0, 0, 0, 0.18);
    background: var(--sl-page-bg, #0f172a);

    .home__hero-mask {
      background:
        linear-gradient(180deg, rgba(8, 13, 24, 0.08) 0%, rgba(8, 13, 24, 0.28) 52%, rgba(9, 9, 11, 0.92) 100%),
        rgba(8, 13, 24, 0.18);
    }

    .home__hero-brand {
      background:
        linear-gradient(145deg, rgba(24, 27, 38, 0.28), rgba(18, 20, 28, 0.22));
      border-color: rgba(255, 255, 255, 0.18);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        0 10rpx 28rpx rgba(0, 0, 0, 0.32);
    }

    .home__search-btn {
      background:
        linear-gradient(145deg, rgba(24, 27, 38, 0.28), rgba(18, 20, 28, 0.22));
      border-color: rgba(255, 255, 255, 0.18);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        0 10rpx 28rpx rgba(0, 0, 0, 0.32);
    }

    .home__article-card {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .home__article-card:active {
      box-shadow: var(--sl-shadow-pressed);
    }

    .home__article-card::after {
      display: none;
    }

    .home__article-title {
      color: var(--sl-text-main);
      text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.5);
    }

    .home__article-meta {
      color: var(--sl-text-sub);
    }
  }

  &__hero {
    position: relative;
    height: 600rpx;
    overflow: hidden;
    box-sizing: border-box;
  }

  &__hero-bg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    display: block;
  }

  &__hero-mask {
    position: absolute;
    inset: 0;
    z-index: 1;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.04) 0%, rgba(17, 24, 39, 0.06) 52%, rgba(245, 247, 250, 0.35) 100%),
      rgba(17, 24, 39, 0.03);
    pointer-events: none;
  }

  &__hero-header {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 100;
    box-sizing: border-box;
  }

  &__hero-capsule-spacer {
    position: relative;
    z-index: 0;
    flex-shrink: 0;
    width: 100%;
  }

  &__hero-title-row {
    display: flex;
    align-items: flex-end;
    gap: 12rpx;
    flex-wrap: wrap;
  }

  &__hero-brand {
    display: inline-flex;
    align-items: center;
    gap: 12rpx;
    padding: 8rpx 20rpx 8rpx 10rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0.12)),
      rgba(255, 255, 255, 0.06);
    border: 1rpx solid rgba(255, 255, 255, 0.38);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.46),
      0 10rpx 28rpx rgba(17, 24, 39, 0.14);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(18rpx) saturate(1.3);
    -webkit-backdrop-filter: blur(18rpx) saturate(1.3);
    /* #endif */
  }

  &__hero-brand-logo {
    width: 38rpx;
    height: 38rpx;
    border-radius: 50%;
  }

  &__hero-brand-name {
    color: rgba(255, 255, 255, 0.96);
    font-size: 22rpx;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-shadow: 0 2rpx 8rpx rgba(17, 24, 39, 0.3);
  }

  &__search-btn {
    height: 100%;
    aspect-ratio: 1;
    color: rgba(255, 255, 255, 0.92);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0.1)),
      rgba(255, 255, 255, 0.06);
    border: 1rpx solid rgba(255, 255, 255, 0.36);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.44),
      0 10rpx 28rpx rgba(17, 24, 39, 0.14);
    transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(18rpx) saturate(1.3);
    -webkit-backdrop-filter: blur(18rpx) saturate(1.3);
    /* #endif */

    &:active {
      transform: scale(0.92);
      background: rgba(255, 255, 255, 0.16);
    }
  }

  &__hero-body {
    position: absolute;
    left: 32rpx;
    right: 32rpx;
    bottom: 56rpx;
    z-index: 2;
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  &__title {
    color: rgba(255, 255, 255, 0.98);
    font-size: 52rpx;
    font-weight: 800;
    letter-spacing: -0.02em;
    text-shadow: 0 4rpx 16rpx rgba(17, 24, 39, 0.25);
  }

  &__subtitle {
    max-width: 520rpx;
    color: rgba(255, 255, 255, 0.9);
    font-size: 28rpx;
    line-height: 1.6;
    letter-spacing: 0.01em;
    text-shadow: 0 2rpx 8rpx rgba(17, 24, 39, 0.2);
  }

  &__content {
    position: relative;
    margin-top: 20rpx;
    padding: 0 30rpx;
    z-index: 2;
  }

  &__notice {
    position: relative;
    z-index: 3;
    margin-top: -56rpx;
    margin-bottom: 28rpx;
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 20rpx 32rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      0 14rpx 36rpx rgba(17, 24, 39, 0.06),
      0 2rpx 8rpx rgba(17, 24, 39, 0.04);
  }

  &__notice-swiper {
    flex: 1;
    height: 38rpx;
  }

  &__notice-item {
    display: flex;
    align-items: center;
  }

  &__notice-text {
    color: var(--home-text-main);
    font-size: 24rpx;
    font-weight: 600;
  }

  &__showcase {
    margin-bottom: 28rpx;
  }

  &__showcase-mosaic {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    grid-template-rows: 220rpx 128rpx;
    gap: 16rpx;
  }

  &__showcase-tile {
    position: relative;
    overflow: hidden;
    border-radius: 24rpx;
    border: 1rpx solid var(--home-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--home-glass-highlight),
      0 14rpx 32rpx var(--home-glass-shadow);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  &__showcase-tile:active {
    transform: scale(0.985);
  }

  &__showcase-slice {
    position: absolute;
    top: 0;
    left: 0;
    width: calc(200% + 16rpx);
    height: 364rpx;
    display: block;
  }

  &__showcase-tile--1 &__showcase-slice {
    left: calc(-100% - 16rpx);
  }

  &__showcase-tile--2 &__showcase-slice {
    top: -236rpx;
  }

  &__showcase-tile--3 &__showcase-slice {
    top: -236rpx;
    left: calc(-100% - 16rpx);
  }

  &__showcase-overlay {
    position: absolute;
    inset: 0;
    z-index: 1;
    background:
      linear-gradient(180deg, rgba(17, 24, 39, 0.06) 0%, rgba(17, 24, 39, 0.38) 100%);
    pointer-events: none;
  }

  &__showcase-meta {
    position: relative;
    z-index: 2;
    display: flex;
    flex-direction: column;
    gap: 6rpx;
    height: 100%;
    justify-content: flex-end;
    padding: 22rpx 20rpx;
    box-sizing: border-box;
  }

  &__showcase-meta--compact {
    padding: 18rpx 20rpx;
  }

  &__showcase-kicker {
    color: rgba(255, 255, 255, 0.82);
    font-size: 20rpx;
    font-weight: 800;
    letter-spacing: 0.18em;
    text-transform: uppercase;
  }

  &__showcase-title {
    color: #fff;
    font-size: 32rpx;
    font-weight: 800;
    letter-spacing: -0.01em;
    text-shadow: 0 4rpx 14rpx rgba(17, 24, 39, 0.28);
  }

  &__showcase-meta--compact &__showcase-title {
    font-size: 28rpx;
  }

  &__showcase-desc {
    color: rgba(255, 255, 255, 0.88);
    font-size: 22rpx;
    line-height: 1.45;
  }

  &__feed {
    display: flex;
    flex-direction: column;
    gap: 34rpx;
    margin-top: 28rpx;
  }

  &__article-card {
    position: relative;
    overflow: hidden;
    border-radius: 34rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--home-glass-pure);
    border: 1rpx solid var(--home-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--home-glass-highlight),
      inset 0 -1rpx 0 rgba(59, 89, 255, 0.04),
      var(--sl-glass-shadow);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(18rpx) saturate(1.22);
    -webkit-backdrop-filter: blur(18rpx) saturate(1.22);
    /* #endif */

    &::after {
      content: '';
      position: absolute;
      left: 24rpx;
      right: 24rpx;
      top: 12rpx;
      height: 48rpx;
      border-radius: 999rpx;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.42), rgba(255, 255, 255, 0));
      pointer-events: none;
      z-index: 2;
    }

    &:active {
      transform: scale(0.985);
      box-shadow:
        inset 0 1rpx 0 var(--home-glass-highlight),
        0 18rpx 48rpx rgba(59, 89, 255, 0.13);
    }
  }

  &__article-card--float {
    margin-top: 0;
  }

  /* 杂志卡片模板 A：视觉出格大图 */
  &__article-card--template-a {
    padding: 22rpx;

    .home__article-cover,
    .home__article-cover--fallback {
      width: 100%;
      height: 370rpx;
      display: block;
      border-radius: 26rpx;
    }

    .home__article-body {
      position: relative;
      padding: 30rpx 4rpx 6rpx;
      z-index: 1;
    }

    .home__article-title {
      color: var(--home-text-main);
      font-size: 38rpx;
      font-weight: 800;
      line-height: 1.35;
      letter-spacing: -0.01em;
    }
  }

  /* 杂志卡片模板 B：左右重心交错 */
  &__article-card--template-b {
    display: flex;
    flex-direction: row-reverse;
    align-items: center;
    justify-content: space-between;
    gap: 24rpx;
    padding: 26rpx 28rpx;

    .home__article-cover,
    .home__article-cover--fallback {
      width: 220rpx;
      height: 176rpx;
      border-radius: 20rpx;
      flex-shrink: 0;
    }

    .home__article-body {
      flex: 1;
      padding: 0;
      min-width: 0;
    }

    .home__article-title {
      font-size: 30rpx;
      font-weight: 700;
      line-height: 1.42;
    }

    .home__tag-row {
      margin-top: 12rpx;
      gap: 12rpx;
    }

    .home__tag {
      padding: 6rpx 14rpx;
      font-size: 21rpx;
    }

    .home__article-meta {
      margin-top: 14rpx;
    }
  }

  /* 杂志卡片模板 C：纯白留白大字 */
  &__article-card--template-c {
    padding: 46rpx 36rpx;

    .home__article-body {
      padding: 0;
    }

    .home__article-title {
      font-size: 34rpx;
      font-weight: 700;
      line-height: 1.45;
    }

    .home__tag-row {
      margin-top: 16rpx;
      gap: 14rpx;
    }

    .home__article-meta {
      margin-top: 24rpx;
    }
  }

  .home__article-cover {
    background: #eef2f6;
  }

  /* 方案 A 液态玻璃氛围渐变兜底图 */
  &__article-cover--fallback {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(59, 89, 255, 0.78), rgba(143, 112, 255, 0.62));
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      width: 150rpx;
      height: 150rpx;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.38) 0%, rgba(255, 255, 255, 0) 70%);
      top: -40rpx;
      left: -40rpx;
    }

    &::after {
      content: '';
      position: absolute;
      width: 200rpx;
      height: 200rpx;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(143, 112, 255, 0.28) 0%, rgba(255, 255, 255, 0) 70%);
      bottom: -60rpx;
      right: -60rpx;
    }
  }

  &__article-logo {
    position: relative;
    z-index: 1;
    width: 96rpx;
    height: 96rpx;
    opacity: 0.88;
  }

  &__article-body {
    position: relative;
    z-index: 1;
  }

  &__article-title {
    color: var(--home-text-main);
    font-size: 37rpx;
    font-weight: 800;
    line-height: 1.38;
  }

  &__tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 18rpx;
    margin-top: 26rpx;
  }

  &__tag {
    padding: 8rpx 20rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    color: var(--home-text-main);
    font-size: 22rpx;
    font-weight: 600;
    letter-spacing: 0.02em;
  }

  &__tag--soft {
    color: var(--home-text-sub);
    font-weight: 600;
  }

  &__article-meta {
    display: flex;
    justify-content: flex-end;
    gap: 14rpx;
    margin-top: 22rpx;
    color: rgba(75, 85, 99, 0.74);
    font-size: 22rpx;
  }

  &__article-meta-item {
    display: inline-flex;
    align-items: center;
    gap: 8rpx;
    min-height: 48rpx;
    padding: 0 18rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
  }

  &__article-meta-item--like {
    color: var(--home-primary);
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
    padding-bottom: calc(#{$space-md} + env(safe-area-inset-bottom));
  }
}
</style>
