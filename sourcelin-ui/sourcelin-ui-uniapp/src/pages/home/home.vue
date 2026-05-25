<template>
  <view class="home">
    <s-loading :visible="loading && feedItems.length === 0" />
    <view class="home__hero">
      <image class="home__hero-bg" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
      <view class="home__brand">
        <image class="home__logo" src="/static/logo/logo.png" mode="aspectFit" />
        <view class="home__eyebrow">Sourcelin Blog</view>
      </view>
      <view class="home__title">欢迎光临</view>
      <view class="home__subtitle s-ellipsis-2">{{ siteSubtitle }}</view>
    </view>

    <view class="home__content">
      <view class="home__search" @tap="goSearch">搜索文章、分类或标签</view>

      <!-- 轻公告栏 -->
      <view v-if="siteInfo?.notices && siteInfo.notices.length > 0" class="home__notice">
        <uni-icons type="sound" size="18" color="var(--home-primary)" />
        <swiper class="home__notice-swiper" vertical autoplay circular interval="4000">
          <swiper-item v-for="(notice, nIdx) in siteInfo.notices" :key="nIdx" class="home__notice-item">
            <text class="home__notice-text s-ellipsis">{{ notice }}</text>
          </swiper-item>
        </swiper>
      </view>

      <!-- 分类快捷入口 -->
      <scroll-view v-if="categories && categories.length > 0" class="home__categories" scroll-x show-scrollbar="false">
        <view class="home__categories-wrap">
          <view
            v-for="cat in categories.slice(0, 6)"
            :key="cat.id"
            class="home__cat-chip"
            @tap="goCategory(cat.id)"
          >
            <text class="home__cat-name">{{ cat.name }}</text>
          </view>
        </view>
      </scroll-view>

      <s-empty v-if="isEmpty" text="暂无文章" />

      <view v-else class="home__feed">
        <!-- 精选卡片：统一采用精美模板 A 风格 -->
        <view v-if="featured" class="home__article-card home__article-card--template-a home__article-card--float" @tap="goDetail(featured.id)">
          <image v-if="imageUrl(featured.avatar)" class="home__article-cover" :src="imageUrl(featured.avatar)" mode="aspectFill" />
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
            <image v-if="imageUrl(item.avatar)" class="home__article-cover" :src="imageUrl(item.avatar)" mode="aspectFill" />
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
import { computed, ref } from 'vue';
import { onPageScroll, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useHomeFeed } from '@/modules/article/composables/useHomeFeed';
import { hideNativeTabbar, liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { normalizeAssetUrl } from '@/utils/url';

const { loading, siteInfo, categories, featured, feedItems, finished, isEmpty, refresh, loadMore } = useHomeFeed();
const backToTopVisible = ref(false);
const activeTabPath = 'pages/home/home';

const siteSubtitle = computed(() => siteInfo.value?.bio || siteInfo.value?.authorInfo || '记录技术、生活与灵感。');

function imageUrl(value?: string): string {
  return normalizeAssetUrl(value);
}

function goSearch(): void {
  uni.navigateTo({ url: '/pages-article/search/search' });
}

function goDetail(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/detail/detail?id=${id}` });
}

function goCategory(id?: number): void {
  if (!id) return;
  uni.navigateTo({ url: `/pages-article/list/list?categoryId=${id}` });
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
  background: $color-bg;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));

  &__hero {
    position: relative;
    overflow: hidden;
    height: 590rpx;
  }

  &__hero-bg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    display: block;
  }

  &__brand,
  &__title,
  &__subtitle,
  &__search {
    position: relative;
    z-index: 1;
  }

  &__brand {
    position: absolute;
    left: 32rpx;
    top: 150rpx;
    display: inline-flex;
    align-items: center;
    gap: 12rpx;
    padding: 7rpx 18rpx 7rpx 8rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.18)),
      rgba(255, 255, 255, 0.22);
    border: 1rpx solid var(--home-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--home-glass-highlight),
      inset 0 -1rpx 0 rgba(59, 89, 255, 0.06),
      0 12rpx 30rpx var(--home-glass-shadow);
  }

  &__logo {
    width: 38rpx;
    height: 38rpx;
    border-radius: 50%;
  }

  &__eyebrow {
    color: var(--home-primary);
    font-size: 22rpx;
    font-weight: 700;
    letter-spacing: 0.12em;
  }

  &__title {
    position: absolute;
    left: 32rpx;
    top: 228rpx;
    color: rgba(255, 255, 255, 0.96);
    font-size: 44rpx;
    font-weight: 800;
    text-shadow: 0 8rpx 28rpx rgba(17, 24, 39, 0.2);
  }

  &__subtitle {
    position: absolute;
    left: 32rpx;
    top: 300rpx;
    max-width: 460rpx;
    color: rgba(255, 255, 255, 0.9);
    font-size: 25rpx;
    line-height: 1.6;
  }

  &__content {
    position: relative;
    margin-top: -132rpx;
    padding: 0 30rpx;
  }

  &__search {
    position: relative;
    display: flex;
    align-items: center;
    min-height: 80rpx;
    box-sizing: border-box;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.22)),
      var(--home-glass-tint);
    color: rgba(75, 85, 99, 0.82);
    font-size: 24rpx;
    font-weight: 500;
    padding: 0 26rpx 0 62rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.78);
    box-shadow:
      inset 0 1rpx 0 var(--home-glass-highlight),
      inset 0 -1rpx 0 rgba(59, 89, 255, 0.05),
      0 18rpx 44rpx rgba(17, 24, 39, 0.1);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(18rpx) saturate(1.28);
    -webkit-backdrop-filter: blur(18rpx) saturate(1.28);
    /* #endif */

    &::after {
      content: '';
      position: absolute;
      left: 86rpx;
      right: 28rpx;
      top: 7rpx;
      height: 18rpx;
      border-radius: 999rpx;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.36), rgba(255, 255, 255, 0));
      pointer-events: none;
    }

    &::before {
      content: '';
      position: absolute;
      left: 28rpx;
      top: 50%;
      width: 16rpx;
      height: 16rpx;
      border: 3rpx solid rgba(59, 89, 255, 0.48);
      border-radius: 50%;
      transform: translateY(-50%);
    }

    &:active {
      transform: scale(0.985);
      box-shadow:
        inset 0 1rpx 0 var(--home-glass-highlight),
        0 16rpx 42rpx rgba(59, 89, 255, 0.12);
    }
  }

  &__notice {
    margin-top: 28rpx;
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 18rpx 28rpx;
    border-radius: 22rpx;
    background: rgba(255, 255, 255, 0.46);
    border: 1rpx solid rgba(255, 255, 255, 0.76);
    box-shadow: 0 8rpx 24rpx rgba(17, 24, 39, 0.02);
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

  &__categories {
    margin-top: 28rpx;
    white-space: nowrap;
    width: 100%;
  }

  &__categories-wrap {
    display: inline-flex;
    gap: 16rpx;
    padding: 4rpx 0;
  }

  &__cat-chip {
    padding: 12rpx 28rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(255, 255, 255, 0.38));
    border: 1rpx solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 6rpx 16rpx rgba(31, 38, 135, 0.02);
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.95);
      background: rgba(59, 89, 255, 0.08);
      border-color: rgba(59, 89, 255, 0.12);
    }
  }

  &__cat-name {
    color: var(--home-text-sub);
    font-size: 24rpx;
    font-weight: 600;
  }

  &__feed {
    display: flex;
    flex-direction: column;
    gap: 34rpx;
    margin-top: 48rpx;
  }

  &__article-card {
    position: relative;
    overflow: hidden;
    border-radius: 34rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.24)),
      var(--home-glass-pure);
    border: 1rpx solid var(--home-glass-border);
    box-shadow:
      inset 0 1rpx 0 var(--home-glass-highlight),
      inset 0 -1rpx 0 rgba(59, 89, 255, 0.06),
      0 22rpx 54rpx var(--home-glass-shadow);

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
    margin-top: -10rpx;
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
      font-size: 36rpx;
      font-weight: 800;
      line-height: 1.38;
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
    padding: 12rpx 20rpx;
    border-radius: 12rpx;
    background: rgba(255, 255, 255, 0.46);
    border: 1rpx solid rgba(255, 255, 255, 0.68);
    color: var(--home-text-main);
    font-size: 24rpx;
    font-weight: 700;
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
    background: rgba(255, 255, 255, 0.44);
    border: 1rpx solid rgba(255, 255, 255, 0.7);
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
