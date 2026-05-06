<template>
  <div class="home-page">
    <section class="home-masthead">
      <div class="home-masthead__backdrop" :style="{ filter: `blur(${mastheadBlurValue}px)` }">
        <img :src="backgroundImage" alt="背景" class="home-masthead__image">
      </div>
      <div class="home-masthead__mist" />
      <HomeDecorations />

      <div class="home-masthead__content">
        <div class="home-masthead__title-wrap">
          <h1 class="home-masthead__title">{{ siteInfo.siteName }}</h1>
          <div class="home-masthead__title-decoration" />
        </div>

        <div class="home-masthead__subtitle-wrap">
          <div class="home-masthead__typewriter">
            <HomePrinter :printer-info="printerInfo" />
          </div>
        </div>
      </div>

      <div class="home-masthead__scroll-indicator" @click="scrollToContent">
        <span class="arrow"><SIcon :icon="appIcons.arrowDown" :size="28" /></span>
      </div>
    </section>

    <div class="page-container-wrap">
      <div class="page-container home-split">
        <aside v-if="!isMobileLayout" class="home-sidebar">
          <div class="sidebar-stack">
            <div class="sidebar-widget sidebar-card sourcelin-reveal-staggered">
              <HomeSiteCard />
            </div>

            <div class="sidebar-widget sidebar-stats sourcelin-reveal-staggered">
              <HomeSiteStats />
            </div>

            <div class="sidebar-widget sidebar-categories sourcelin-reveal-staggered">
              <HomeCategoryList :categories="displayCategories" />
            </div>

            <div class="sidebar-widget sidebar-labels sourcelin-reveal-staggered">
              <HomeLabelCloud :tags="displayTags" dense max-height="24rem" @open-tag="goToTag" />
            </div>
          </div>
        </aside>

        <main class="home-main">
          <section
            v-if="isMobileLayout"
            class="home-mobile-quick-entry content-section home-section-shell sourcelin-reveal-staggered"
            aria-label="移动端快捷入口"
          >
            <HomeSectionPrelude title="快捷入口" />
            <div class="home-mobile-quick-entry__actions">
              <SButton variant="ghost" @click="goToArchive()">文章归档</SButton>
              <SButton variant="ghost" @click="goToCategories">文章分类</SButton>
              <SButton variant="ghost" @click="goToTagsPage">文章标签</SButton>
            </div>
          </section>

          <HomeMarquee :notices="notices" />

          <section class="content-section home-section-shell sourcelin-reveal-staggered sourcelin-structural-breathe">
            <HomeSectionPrelude title="精选" />

            <div v-if="displayCarousel.length || displayRecommended.length" class="picks-stage">
              <div class="picks-carousel">
                <HomeCarouselSection :items="displayCarousel" @open="goToArticle" />
              </div>

              <div class="picks-list">
                <HomeRecommendedList :articles="displayRecommended" @open="goToArticle" />
              </div>
            </div>

            <EmptyState
              v-else
              class="picks-empty"
              variant="section"
              icon="star"
              title="暂无精选"
              message="这里还没有精选内容，先去发现区逛逛吧。"
            />
          </section>

          <section class="content-section home-section-shell sourcelin-reveal-staggered sourcelin-structural-breathe">
            <HomeSectionPrelude title="发现" />

            <div class="discover-stage">
              <HomeDiscoverFilter
                :categories="displayCategories"
                :selected-category-id="discoverFeed.selectedCategoryId.value"
                @select="discoverFeed.selectCategory"
              />

              <HomeArticlesSection
                :loading="discoverFeed.loading.value"
                :switching="discoverFeed.switching.value"
                :loading-more="discoverFeed.loadingMore.value"
                :finished="discoverFeed.finished.value"
                :articles="discoverFeed.articles.value"
                @load-more="discoverFeed.loadMore"
                @open-article="goToArticle"
                @open-tag="goToTag"
              />
            </div>
          </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import { appIcons } from '@/shared/components/ui/icons'
import HomeDecorations from '@/modules/home/components/HomeDecorations.vue'
import HomePrinter from '@/modules/home/components/HomePrinter.vue'
import HomeSiteCard from '@/modules/home/components/HomeSiteCard.vue'
import HomeCategoryList from '@/modules/home/components/HomeCategoryList.vue'
import HomeSiteStats from '@/modules/home/components/HomeSiteStats.vue'
import HomeLabelCloud from '@/modules/home/components/HomeLabelCloud.vue'
import HomeMarquee from '@/modules/home/components/HomeMarquee.vue'
import HomeRecommendedList from '@/modules/home/components/HomeRecommendedList.vue'
import HomeCarouselSection from '@/modules/home/components/HomeCarouselSection.vue'
import HomeDiscoverFilter from '@/modules/home/components/HomeDiscoverFilter.vue'
import HomeArticlesSection from '@/modules/home/components/HomeArticlesSection.vue'
import HomeSectionPrelude from '@/modules/home/components/HomeSectionPrelude.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import { useHomePage } from '@/modules/home/composables/useHomePage'
import { useDiscoverFeed } from '@/modules/home/composables/useDiscoverFeed'
import { parseDiscoverCategoryId } from '@/modules/home/model/home-discover-route'

const route = useRoute()
const router = useRouter()
const isMobileLayout = ref(false)
const {
  mastheadBlurValue,
  printerInfo,
  siteInfo,
  backgroundImage,
  notices,
  displayCarousel,
  displayRecommended,
  displayCategories,
  displayTags,
  goToArticle,
  goToArchive,
  goToTag,
  scrollToContent,
  initHomePage
} = useHomePage()

const discoverFeed = useDiscoverFeed(displayCategories)

const refreshMobileLayout = () => {
  isMobileLayout.value = window.matchMedia('(max-width: 1024px)').matches
}

const goToCategories = () => router.push('/categories')
const goToTagsPage = () => router.push('/tags')

onMounted(() => {
  refreshMobileLayout()
  window.addEventListener('resize', refreshMobileLayout, { passive: true })
  void (async () => {
    const initialCategoryId = parseDiscoverCategoryId(route.query.dc)
    const homeData = await initHomePage(initialCategoryId)
    await discoverFeed.initDiscover(homeData?.latest ?? null)
  })()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', refreshMobileLayout)
})

</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.home-page {
  min-height: 100vh;
  background: var(--surface-page);
}

.home-masthead {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
  isolation: isolate;
}

.home-masthead__backdrop {
  position: absolute;
  inset: 0 0 auto 0;
  width: 100%;
  height: 120%;
  z-index: 0;
  transform: translateY(calc(var(--scroll-y, 0) * 0.5px));
  will-change: filter, transform;
}

.home-masthead__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.home-masthead__mist {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, var(--surface-black-30) 0%, var(--surface-black-10) 50%, var(--surface-white-05) 100%);
  z-index: 1;
}

.home-masthead__content {
  position: relative;
  z-index: 2;
  display: grid;
  gap: clamp(0.9rem, 2vw, 1.25rem);
  padding: 0 var(--spacing-lg);
  max-width: min(42rem, calc(100% - 2rem));
  background: transparent;
}

.home-masthead__title-wrap {
  position: relative;
  display: inline-block;
}

.home-masthead__title {
  margin: 0 0 var(--spacing-md);
  font-size: var(--page-title-size);
  font-weight: 700;
  color: var(--text-color-light);
  text-shadow:
    0 2px 10px color-mix(in srgb, var(--background-color-deep) 24%, transparent),
    0 14px 32px var(--surface-black-30);
  letter-spacing: -0.03em;
}

.home-masthead__title-decoration {
  position: absolute;
  bottom: -10px;
  left: 50%;
  width: 126px;
  height: 3px;
  border-radius: 2px;
  transform: translateX(-50%);
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, var(--companion-color) 26%, var(--text-color-light)) 34%,
    var(--text-color-light) 50%,
    transparent
  );
}

.home-masthead__subtitle-wrap {
  display: grid;
  justify-items: center;
}

.home-masthead__typewriter {
  min-height: 40px;
  cursor: pointer;
}

.home-masthead__scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  z-index: 2;
  cursor: pointer;
  transform: translateX(-50%);
  animation: home-masthead-scroll-signal 3.8s cubic-bezier(0.19, 1, 0.22, 1) infinite;
}

.home-masthead__scroll-indicator .arrow {
  font-size: 28px;
  color: var(--surface-white-80);
}

@keyframes home-masthead-scroll-signal {
  0%,
  50%,
  100% {
    transform: translateX(-50%) scale(1);
    opacity: 0.78;
  }

  45% {
    transform: translateX(-50%) scale(1.08);
    opacity: 1;
  }
}

.page-container-wrap {
  position: relative;
  z-index: 10;
  margin-top: 0;
  overflow: hidden;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-12) 16%, transparent) 0%,
      color-mix(in srgb, var(--surface-page-content) 52%, transparent) 12%,
      color-mix(in srgb, var(--surface-page-content) 84%, transparent) 100%
    );
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) - 1px)) saturate(calc(var(--glass-saturate) - 8%));
  backdrop-filter: blur(calc(var(--glass-blur) - 1px)) saturate(calc(var(--glass-saturate) - 8%));
}

.page-container-wrap::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: var(--surface-masthead-mist), var(--surface-page-ambient);
  opacity: 0.74;
}

.page-container-wrap::after {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: clamp(2rem, 4vw, 3rem);
  pointer-events: none;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-05) 26%, transparent) 0%,
      color-mix(in srgb, var(--background-color) 8%, transparent) 58%,
      transparent 100%
    );
}

.page-container {
  position: relative;
  z-index: 1;
}

.page-container {
  max-width: min(1500px, calc(100vw - var(--spacing-xxl)));
  margin: 0 auto;
  padding: clamp(2rem, 4vw, 3rem) var(--page-shell-padding) clamp(4rem, 7vw, 5.5rem);
}

.home-split {
  display: grid;
  grid-template-columns: minmax(280px, 330px) minmax(0, 1fr);
  gap: clamp(1rem, 2vw, 1.4rem);
  align-items: start;
}

.home-sidebar {
  position: sticky;
  top: calc(var(--header-height, 64px) + 1rem);
  align-self: start;
  /* 避免 paint containment 裁切悬停态阴影/位移 */
  contain: layout style;
}

.sidebar-stack {
  --sidebar-widget-padding: 0.8rem;
  display: grid;
  gap: var(--page-block-gap);
  padding: clamp(0.75rem, 1.2vw, 1rem) var(--page-shell-padding);
}

.sidebar-widget {
  min-width: 0;
  transition:
    transform 0.38s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.38s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}

.sidebar-widget:hover {
  transform: translateY(-3px);
}

.sidebar-widget :deep(.s-card--hoverable:hover),
.sidebar-card :deep(.site-card.modern-card.card-hoverable:hover) {
  /* 侧栏里禁用缩放，避免字体在 hover 时发虚 */
  transform: none;
}

.sidebar-card {
  position: relative;
  z-index: 3;
  /* 容器不设边框 */
}

.sidebar-categories {
  position: relative;
  z-index: 2;
  padding-inline-start: 0;
}

.sidebar-categories :deep(.category-shell) {
  padding: var(--sidebar-widget-padding);
  border-color: var(--border-content-card);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
}

.sidebar-stats {
  position: relative;
  z-index: 2;
  padding-inline-start: 0;
  /* 容器不设边框 */
}

.sidebar-labels {
  position: relative;
  z-index: 1;
  padding-inline-start: 0;
  /* 容器不设边框 */
}

.sidebar-stats :deep(.stats-grid) {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
}

.sidebar-stats :deep(.stats-shell) {
  padding: var(--sidebar-widget-padding);
}

.sidebar-stats :deep(.stat-item) {
  min-height: 8.35rem;
  padding: 0.88rem;
}

.sidebar-stats :deep(.stat-item.is-primary) {
  min-height: 10.4rem;
}

.sidebar-card :deep(.site-card-wrapper) {
  --site-header-height: 146px;
  --site-avatar-size: 84px;
  --site-avatar-overlap: 48px;
  --site-content-padding-top: 0;
  --site-content-padding-inline: clamp(0.68rem, 1.2vw, 0.84rem);
  --site-content-padding-bottom: 0.92rem;
}

.sidebar-card :deep(.user-strip-shell) {
  margin-bottom: 1rem;
}

.sidebar-labels :deep(.label-card) {
  padding: var(--sidebar-widget-padding);
  border-color: var(--border-content-card);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
}

.sidebar-labels :deep(.label-sphere) {
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 97%, transparent)
    ),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--surface-white-15) 66%, transparent), transparent 54%),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--primary-color) 5%, transparent), transparent 44%),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-05) 88%, transparent), transparent);
}

.home-main {
  display: grid;
  gap: var(--page-section-gap);
  min-width: 0;
}

.home-main > * {
  min-width: 0;
}

.content-section {
  position: relative;
  display: grid;
  gap: var(--section-prelude-spacing);
  min-width: 0;
  padding: clamp(1.1rem, 2vw, 1.45rem);
}

.home-section-shell {
  position: relative;
  border-radius: calc(var(--glass-radius) + 6px);
  border: 1px solid color-mix(in srgb, var(--border-panel-default) 82%, transparent);
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--surface-white-12) 16%, transparent),
      color-mix(in srgb, var(--surface-panel-default) 92%, transparent) 18%,
      color-mix(in srgb, var(--surface-panel-soft) 84%, transparent) 58%,
      color-mix(in srgb, var(--surface-panel-strong) 92%, transparent) 100%
    ),
    radial-gradient(circle at 12% -4%, color-mix(in srgb, var(--primary-color) 8%, transparent), transparent 34%),
    radial-gradient(circle at 86% 10%, color-mix(in srgb, var(--secondary-color) 6%, transparent), transparent 28%);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 84%, transparent),
    var(--highlight-panel-soft),
    var(--shadow-panel-default),
    0 18px 42px color-mix(in srgb, var(--primary-color) 7%, transparent);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 5px)) saturate(calc(var(--glass-saturate) + 6%));
  backdrop-filter: blur(calc(var(--glass-blur) + 5px)) saturate(calc(var(--glass-saturate) + 6%));
  overflow: hidden;
  contain: layout style paint;
  content-visibility: auto;
  contain-intrinsic-size: 0 500px;
}

.home-section-shell::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 46%;
  pointer-events: none;
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-18) 84%, transparent), transparent 72%),
    linear-gradient(120deg, color-mix(in srgb, var(--surface-white-08) 36%, transparent), transparent 38%),
    radial-gradient(circle at 82% 16%, color-mix(in srgb, var(--secondary-color) 8%, transparent), transparent 42%);
  opacity: 0.92;
}

.picks-stage {
  display: grid;
  gap: 0;
  min-width: 0;
}

.picks-empty {
  min-height: 0;
}

.picks-empty.platform-empty--section :deep(.platform-empty__shell) {
  min-height: 320px;
}

.picks-carousel {
  position: relative;
  z-index: 0;
  min-width: 0;
}

.picks-list {
  position: relative;
  z-index: 2;
  width: min(100%, 880px);
  margin: -1.05rem auto 0;
  min-width: 0;
}

.discover-stage {
  display: grid;
  gap: var(--section-prelude-spacing);
  width: 100%;
  max-width: 100%;
  min-width: 0;
}

.discover-stage > :deep(*) {
  min-width: 0;
  max-width: 100%;
}

.home-mobile-quick-entry {
  gap: 0.75rem;
}

.home-mobile-quick-entry__actions {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.5rem;
}

@include sourcelin-down(lg) {
  .home-split {
    grid-template-columns: 1fr;
  }

  .home-sidebar {
    position: static;
  }

  .sidebar-stack {
    padding-block: 0.5rem;
  }

  .sidebar-stats,
  .sidebar-labels {
    padding-inline-start: 0;
  }

  .sidebar-stats :deep(.stat-item) {
    min-height: 9.8rem;
    padding: 0.98rem;
  }

  .sidebar-stats :deep(.stat-item.is-primary) {
    min-height: 12rem;
  }
}

@include sourcelin-down(md) {
  .page-container {
    padding: clamp(1.5rem, 4vw, 2.25rem) var(--spacing-md) clamp(3.5rem, 7vw, 4.5rem);
  }

  .home-masthead__title {
    font-size: clamp(2.4rem, 10vw, 3.2rem);
  }

  .picks-list {
    width: 100%;
    margin-top: -0.7rem;
  }
}

@include sourcelin-down(sm) {
  .page-container {
    padding: 1rem 12px 3rem;
  }

  .home-main {
    gap: 0.95rem;
  }

  .content-section {
    padding: 0.95rem;
    gap: 0.5rem;
  }

  .picks-stage {
    gap: 0.85rem;
  }

  .picks-list {
    margin-top: 0;
  }

  .discover-stage {
    gap: 0.6rem;
  }

  .home-mobile-quick-entry__actions {
    grid-template-columns: 1fr;
  }

  .home-masthead__scroll-indicator {
    bottom: 28px;
  }

  .home-masthead__title-decoration {
    width: 72px;
  }
}

</style>
