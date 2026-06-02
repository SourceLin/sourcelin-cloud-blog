<script setup lang="ts">
import type { Component } from 'vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getCategoryArticles } from '@/modules/article/api/category.api'
import type { CategoryOption } from '@/modules/article/api/category.api'
import type { ArticleListItem } from '@/modules/article/api/article.api'
import { buildCategoryArticleRoute } from '@/modules/article/utils/article-source'
import ContextArticleCardSkeleton from '@/modules/article/components/ContextArticleCardSkeleton.vue'
import { useSeoHead } from '@/shared/composables/useSeoHead'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import FoldableOverflowBlock from '@/shared/components/layout/FoldableOverflowBlock.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SPagination from '@/shared/components/ui/SPagination.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import { appIcons, type AppIconName } from '@/shared/components/ui/icons'
import { useSMessage } from '@/shared/composables/useSMessage'

interface Category {
  id: number
  name: string
  icon: string
  description: string
  articleCount: number
  hue: number
}

interface CategoryArticle {
  id: number
  title: string
  summary: string
  cover: string
  date: string
  views: number
  comments: number
  authorName: string
}

interface CategoryHeroStatTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
}

const route = useRoute()
const router = useRouter()
const message = useSMessage()

// 分类页 SEO
useSeoHead({
  title: computed(() => '文章分类'),
  description: computed(() => '按分类浏览所有文章，快速找到你感兴趣的主题内容。'),
  canonicalUrl: computed(() => typeof window !== 'undefined' ? `${window.location.origin}/categories` : '')
})

const categorySwitchSectionRef = ref<HTMLElement | null>(null)

const categoryFoldExpandLabel = computed(() =>
  categories.value.length > 0 ? `展开全部 ${categories.value.length} 个分类` : '展开全部分类'
)

function getCategorySwitchScrollAnchor() {
  return categorySwitchSectionRef.value
}

const categories = ref<Category[]>([])
const categoryArticles = ref<CategoryArticle[]>([])
const activeCategory = ref<Category | null>(null)
const pendingCategoryId = ref<number | null>(null)
const loadingCategories = ref(false)
const loadingArticles = ref(false)
const articlePage = ref(1)
const articlePageSize = 12
const articleTotal = ref(0)
const articleTotalPages = computed(() =>
  articleTotal.value > 0 ? Math.ceil(articleTotal.value / articlePageSize) : 0
)

const iconByName: Record<string, AppIconName> = {
  folder: 'folder',
  category: 'category',
  tag: 'tag',
  archive: 'archive',
  home: 'home',
  link: 'links'
}

function normalizeDate(value: unknown): string {
  const text = typeof value === 'string' ? value : ''
  return text ? text.split(' ')[0] : ''
}

function normalizeCategory(item: CategoryOption, index: number): Category {
  return {
    id: Number(item.id) || index + 1,
    name: String(item.name || '未命名分类'),
    icon: String(item.icon || 'folder'),
    description: String(item.summary || ''),
    articleCount: Number(item.articleCount || 0) || 0,
    hue: (index * 46) % 360
  }
}

function normalizeArticle(item: ArticleListItem): CategoryArticle {
  return {
    id: Number(item.id) || 0,
    title: String(item.title || '未命名文章'),
    summary: String(item.summary || '这篇文章暂时还没有摘要。'),
    cover: String(item.avatar || ''),
    date: normalizeDate(item.createTime),
    views: Number(item.viewCount || 0),
    comments: Number(item.commentCount || 0),
    authorName: String(item.author || '平台作者')
  }
}

const queryCategoryName = computed(() => (typeof route.query.name === 'string' ? route.query.name.trim() : ''))
const queryCategoryId = computed(() => {
  const raw = route.query.categoryId
  const value = Array.isArray(raw) ? raw[0] : raw
  const parsed = Number(String(value || ''))
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null
})

const totalCategoryArticles = computed(() => categories.value.reduce((sum, c) => sum + (c.articleCount || 0), 0))
const categoriesWithArticles = computed(() => categories.value.filter((c) => (c.articleCount || 0) > 0).length)
const featuredCategory = computed(() => activeCategory.value ?? categories.value.find((item) => item.articleCount > 0) ?? categories.value[0] ?? null)
const isDetailMode = computed(() => activeCategory.value !== null)
const contextCategory = computed(() =>
  activeCategory.value ??
  categories.value.find((item) => item.id === pendingCategoryId.value) ??
  featuredCategory.value
)
const categoryContextStyle = computed(() => ({
  '--context-accent-hue': String(contextCategory.value?.hue ?? 245)
}))

const categoryHeroStatTiles = computed((): CategoryHeroStatTile[] => {
  const count = categories.value.length
  const articles = totalCategoryArticles.value
  const active = categoriesWithArticles.value

  return [
    {
      label: '分类总数',
      value: String(count).padStart(2, '0'),
      icon: appIcons.category,
      hue: 245,
      ariaLabel: `共 ${count} 个分类`
    },
    {
      label: '内容篇数',
      value: String(articles),
      icon: appIcons.document,
      hue: 210,
      ariaLabel: `分类下共 ${articles} 篇文章`
    },
    {
      label: '有文分类',
      value: String(active).padStart(2, '0'),
      icon: appIcons.folderOpen,
      hue: 175,
      ariaLabel: `${active} 个分类下已有文章`
    }
  ]
})

const articleStageCopy = computed(() => {
  if (!activeCategory.value) {
    return '选择分类后，将在当前区域展示该分类文章列表。'
  }

  return activeCategory.value.description ||
    `这里汇总了“${activeCategory.value.name}”分类文章，继续下滑即可浏览并打开阅读。`
})

function resolveCategoryIcon(icon: string) {
  const normalized = icon.trim().toLowerCase()
  const key = iconByName[normalized] || 'folder'
  return appIcons[key]
}

async function loadCategories() {
  loadingCategories.value = true
  try {
    const response = await getCategories()
    categories.value = response.map((item, index) => normalizeCategory(item, index))
  } catch (error) {
    console.error('加载分类失败:', error)
    categories.value = []
  } finally {
    loadingCategories.value = false
  }
}

async function loadArticlesForCategory(categoryId: number) {
  loadingArticles.value = true
  try {
    const response = await getCategoryArticles(categoryId, {
      page: articlePage.value,
      pageSize: articlePageSize
    })
    articleTotal.value = Number(response.total ?? 0)
    categoryArticles.value = response.items.map((item) => normalizeArticle(item))
  } catch (error) {
    console.error('加载分类文章失败:', error)
    categoryArticles.value = []
    articleTotal.value = 0
  } finally {
    loadingArticles.value = false
  }
}

function findCategoryFromRoute() {
  if (queryCategoryId.value != null) {
    return categories.value.find((item) => item.id === queryCategoryId.value) ?? null
  }

  if (!queryCategoryName.value) {
    return null
  }

  const normalized = queryCategoryName.value.toLowerCase()
  return categories.value.find((item) => item.name.toLowerCase() === normalized) ?? null
}

async function applyActiveCategory(category: Category | null) {
  if (!category) {
    activeCategory.value = null
    categoryArticles.value = []
    pendingCategoryId.value = null
    articlePage.value = 1
    articleTotal.value = 0
    return
  }

  if (activeCategory.value?.id === category.id && categoryArticles.value.length) {
    return
  }

  activeCategory.value = category
  pendingCategoryId.value = category.id
  articlePage.value = 1
  await loadArticlesForCategory(category.id)
}

async function syncCategoryFromRoute() {
  const matched = findCategoryFromRoute()

  if (!matched) {
    await applyActiveCategory(null)
    return
  }

  if (queryCategoryName.value !== matched.name) {
    await router.replace({ path: '/categories', query: { name: matched.name } })
    return
  }

  await applyActiveCategory(matched)
}

async function selectCategory(category: Category) {
  if (!category.articleCount) {
    message.info('该分类下暂无文章')
    return
  }

  pendingCategoryId.value = category.id

  if (queryCategoryName.value === category.name) {
    await applyActiveCategory(category)
    return
  }

  await router.push({ path: '/categories', query: { name: category.name } })
}

function resetCategoryView() {
  pendingCategoryId.value = null
  void router.push('/categories')
}

function scrollCategorySwitchIntoView() {
  const anchor = getCategorySwitchScrollAnchor()
  if (!(anchor instanceof HTMLElement)) {
    return
  }

  const top = anchor.getBoundingClientRect().top + window.scrollY - 88
  window.scrollTo({
    top: Math.max(top, 0),
    behavior: 'smooth'
  })
}

async function handleArticlePageChange(page: number) {
  if (!activeCategory.value || page === articlePage.value || page < 1) {
    return
  }

  articlePage.value = page
  await loadArticlesForCategory(activeCategory.value.id)
  scrollCategorySwitchIntoView()
}

function goToArticle(id: number) {
  const name = activeCategory.value?.name
  if (!name) {
    void router.push(`/article/${id}`)
    return
  }

  void router.push(buildCategoryArticleRoute(id, name))
}

watch(
  () => [route.query.name, route.query.categoryId],
  () => {
    if (categories.value.length) {
      void syncCategoryFromRoute()
    }
  }
)

onMounted(async () => {
  await loadCategories()
  await syncCategoryFromRoute()
})
</script>

<template>
  <div class="glass-page category-page">
    <PageShell>
      <SCard
        class="category-hero"
        variant="lite"
      >
        <div class="category-header-main">
          <div class="category-hero__copy">
            <span
              class="article-card-prelude-label"
            >文库</span>
            <h1 class="category-hero__title">文章分类</h1>
            <p class="category-hero__subtitle">先选分类，再在同一页面继续浏览对应文章。</p>

            <section class="category-hero__summary" aria-label="分类概览">
              <HeroStatCard
                v-for="tile in categoryHeroStatTiles"
                :key="tile.label"
                class="category-hero__stat"
                mode="metric"
                tone="tinted"
                size="lg"
                :icon="tile.icon"
                :value="tile.value"
                :label="tile.label"
                :hue="tile.hue"
                :aria-label="tile.ariaLabel"
              />
            </section>
          </div>
        </div>
      </SCard>

      <section
        ref="categorySwitchSectionRef"
        class="category-switch-shell"
        :style="categoryContextStyle"
        aria-labelledby="category-switch-title"
      >
        <div class="section-head category-switch-head">
          <div>
            <h2 id="category-switch-title" class="section-title">
              {{ isDetailMode ? '分类文章' : '全部分类' }}
            </h2>
            <p v-if="!isDetailMode" class="section-copy">点击分类后，当前区域会直接切换为该分类文章。</p>
          </div>
          <SSurfaceChip v-if="!isDetailMode" class="section-count" size="sm" variant="counter">{{ categories.length }} 个</SSurfaceChip>
        </div>

        <div v-if="!isDetailMode" class="category-browser-stage">
          <div
            v-if="loadingCategories"
            class="category-categories-skel"
            aria-busy="true"
            aria-label="分类加载中"
          >
            <SSkeleton :sharp="false" class="category-categories-skel__title" />
            <div class="category-categories-skel__lines">
              <SSkeleton v-for="n in 4" :key="`cat-skel-${n}`" :sharp="false" class="category-categories-skel__line" />
            </div>
          </div>

          <div v-else-if="categories.length">
            <FoldableOverflowBlock
              :item-count="categories.length"
              :expand-button-text="categoryFoldExpandLabel"
              collapse-button-text="收起分类列表"
              :get-scroll-anchor="getCategorySwitchScrollAnchor"
            >
              <div class="categories-grid">
                <button
                  v-for="category in categories"
                  :key="category.id"
                  type="button"
                  class="category-card"
                  :class="{ 'is-armed': pendingCategoryId === category.id }"
                  :style="{ '--icon-hue': category.hue }"
                  @click="selectCategory(category)"
                >
                  <div class="category-icon">
                    <SIcon :icon="resolveCategoryIcon(category.icon)" :size="28" />
                  </div>
                  <div class="category-info">
                    <h3 class="category-name">{{ category.name }}</h3>
                    <p class="category-desc">{{ category.description || `浏览“${category.name}”分类下的最新与历史文章。` }}</p>
                  </div>
                  <div class="category-count">
                    <span class="count-value">{{ category.articleCount || 0 }}</span>
                    <span class="count-label">篇文章</span>
                  </div>
                  <div class="category-arrow">
                    <SIcon :icon="appIcons.arrowForward" :size="20" />
                  </div>
                </button>
              </div>
            </FoldableOverflowBlock>
          </div>

          <EmptyState
            v-else
            class="context-state"
            variant="page"
            icon="category"
            title="暂无分类"
            message="这里还没有可用分类，稍后再来看看。"
          />
        </div>

          <div v-else class="category-article-stage">
            <div v-if="activeCategory" class="context-focus-strip">
              <div class="context-focus-strip__icon">
                <SIcon :icon="resolveCategoryIcon(activeCategory.icon)" :size="24" />
              </div>
              <div class="context-focus-strip__copy">
                <p class="context-focus-strip__eyebrow">当前分类</p>
                <div class="context-focus-strip__heading">
                  <h3 class="context-focus-strip__title">{{ activeCategory.name }}</h3>
                  <span class="context-focus-strip__badge">
                    {{ activeCategory.articleCount }} 篇
                  </span>
                </div>
                <p class="context-focus-strip__desc">{{ articleStageCopy }}</p>
              </div>
                <div class="context-focus-strip__aside">
                  <div class="context-focus-strip__metric">
                  <span class="context-focus-strip__metric-label">当前页</span>
                  <span class="context-focus-strip__metric-value">{{ articlePage }} / {{ articleTotalPages || 1 }}</span>
                  </div>
                <button type="button" class="context-back-button" @click="resetCategoryView">
                  <SIcon :icon="appIcons.back" :size="14" />
                  返回分类
                </button>
              </div>
            </div>

          <ContextArticleCardSkeleton v-if="loadingArticles" variant="category" />

          <TransitionGroup
            v-else-if="categoryArticles.length"
            name="article-cascade"
            tag="div"
            class="context-article-list"
          >
            <article
              v-for="(article, index) in categoryArticles"
              :key="article.id"
              class="context-article-card"
              :style="{ '--stagger-index': index }"
              @click="goToArticle(article.id)"
            >
              <div
                class="context-article-card__cover"
                :style="{ '--article-cover': article.cover ? `url(${article.cover})` : 'none' }"
              />
              <div class="context-article-card__body">
                <div class="context-article-card__topline">
                  <span class="context-article-card__chip">{{ activeCategory?.name }}</span>
                  <span class="context-article-card__date">{{ article.date || '最近更新' }}</span>
                </div>
                <h3 class="context-article-card__title">{{ article.title }}</h3>
                <p class="context-article-card__summary">{{ article.summary }}</p>
                <div class="context-article-card__meta">
                  <span>{{ article.authorName }}</span>
                  <span>{{ article.views }} 阅读</span>
                  <span>{{ article.comments }} 评论</span>
                </div>
              </div>
              <div class="context-article-card__arrow">
                <SIcon :icon="appIcons.arrowForward" :size="16" />
              </div>
            </article>
          </TransitionGroup>

          <EmptyState
            v-else
            class="context-state"
            variant="page"
            icon="document"
            title="当前分类下还没有文章"
            message="这个分类还没有文章，先去看看其他分类吧。"
          />

          <div
            v-if="activeCategory && articleTotal > articlePageSize"
            class="context-pagination"
          >
            <div class="context-pagination__summary" aria-live="polite">
              <span>第 {{ articlePage }} / {{ articleTotalPages || 1 }} 页</span>
              <span>共 {{ articleTotal }} 篇</span>
            </div>
            <SPagination
              :page="articlePage"
              :page-size="articlePageSize"
              :item-count="articleTotal"
              simple
              @update:page="handleArticlePageChange"
            />
          </div>
        </div>
      </section>
    </PageShell>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.category-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.category-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.category-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.category-header-main {
  position: relative;
  display: block;
}

.category-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.category-hero__title {
  margin: 0;
  font-size: clamp(2.5rem, 5vw, 4rem);
  line-height: 0.98;
  letter-spacing: 0.04em;
  color: var(--title-color);
}

.context-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 18px;
}

.context-pagination__summary {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.category-hero__subtitle {
  max-width: 42rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.category-hero__summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}








.category-switch-shell {
  --context-shell-radius: calc(var(--border-radius-xl) + 4px);
  --context-entry-radius: calc(var(--border-radius-xl) + 2px);
  --context-inset-radius: calc(var(--border-radius-lg) + 2px);
  position: relative;
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  border: 1px solid var(--border-panel-default);
  border-radius: var(--context-shell-radius);
  background:
    var(--surface-panel-specular-soft),
    var(--surface-panel-soft);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  overflow: hidden;
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.category-switch-shell:hover {
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-hover);
}

.category-switch-shell::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 34%, transparent) 22%,
    color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 68%, transparent) 50%,
    color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 34%, transparent) 78%,
    transparent
  );
  pointer-events: none;
}

.category-browser-stage,
.category-article-stage {
  display: grid;
  gap: var(--spacing-lg);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.section-count {
  flex-shrink: 0;
  align-self: center;
}

.section-title {
  margin: 0 0 0.2rem;
  font-size: 1.16rem;
  color: var(--title-color);
}

.section-copy {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.7;
}

.context-article-card__chip {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-lg);
}

.category-card {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: clamp(0.85rem, 1.2vw, 1.05rem);
  width: 100%;
  min-height: clamp(6.9rem, 10vw, 7.8rem);
  padding: var(--spacing-lg);
  border-radius: var(--context-entry-radius);
  border: 1px solid var(--border-panel-default);
  background: 
    var(--surface-panel-specular-soft),
    var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  cursor: pointer;
  isolation: isolate;
  text-align: left;
  overflow: hidden;
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
}

.category-card:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, hsl(var(--icon-hue), 72%, 56%) 34%, var(--border-interactive-hover));
  background: var(--surface-panel-default);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-hover);
}

.category-card::after {
  content: '';
  position: absolute;
  inset: auto var(--spacing-lg) 0;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, hsl(var(--icon-hue), 68%, 54%) 0%, transparent),
    color-mix(in srgb, hsl(var(--icon-hue), 68%, 54%) 82%, transparent),
    transparent
  );
  opacity: 0;
  transform: scaleX(0.24);
  transform-origin: left center;
  transition:
    opacity var(--transition-base),
    transform var(--transition-base);
}

.category-card:hover .category-arrow {
  opacity: 1;
  transform: translateX(4px);
}

.category-card:hover .category-icon {
  transform: scale(1.08) rotate(4deg);
}

.category-card:hover::after,
.category-card.is-armed::after {
  opacity: 1;
  transform: scaleX(1);
}

.category-card.is-armed {
  transform: translateY(-2px) scale(0.995);
  border-color: color-mix(in srgb, hsl(var(--icon-hue), 72%, 56%) 34%, var(--border-panel-strong));
  background: var(--surface-panel-strong);
  box-shadow: var(--highlight-panel-strong), var(--shadow-panel-hover), var(--shadow-panel-glow);
}

.category-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  color: hsl(var(--icon-hue), 60%, 45%);
  background: color-mix(in srgb, hsl(var(--icon-hue), 70%, 94%) 92%, transparent);
  border-radius: var(--border-radius-lg);
  flex-shrink: 0;
  transition: transform var(--transition-base);
}

.category-info {
  flex: 1;
  display: grid;
  row-gap: clamp(0.28rem, 0.7vw, 0.42rem);
  align-content: start;
  min-width: 0;
  padding-top: 0.08rem;
}

.category-name {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
  line-height: 1.28;
  color: var(--text-primary);
}

.category-desc {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.62;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-count {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  flex-shrink: 0;
  align-self: center;
  margin-left: auto;
}

.count-value {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--primary-color);
  line-height: 1;
}

.count-label {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.category-arrow {
  color: var(--text-secondary);
  opacity: 0;
  transition: all var(--transition-base);
  flex-shrink: 0;
  align-self: center;
}

.context-focus-strip {
  position: relative;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) minmax(168px, 220px);
  gap: var(--spacing-lg);
  align-items: center;
  min-height: 6.8rem;
  padding: var(--spacing-md) var(--spacing-lg);
  overflow: hidden;
  border: 1px solid var(--border-panel-strong);
  border-radius: var(--context-entry-radius);
  background: var(--surface-panel-strong);
  box-shadow: var(--highlight-panel-strong), var(--shadow-panel-default);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
}

.context-focus-strip::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 88%, transparent),
    color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 18%, transparent)
  );
}

.context-focus-strip__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: var(--context-inset-radius);
  color: hsl(var(--context-accent-hue, 245), 58%, 42%);
  border: 1px solid color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 18%, var(--border-content-card));
  background: color-mix(in srgb, hsl(var(--context-accent-hue, 245), 76%, 88%) 72%, var(--surface-panel-chip-quiet));
  box-shadow: var(--highlight-panel-chip), var(--shadow-panel-inline);
}

.context-focus-strip__copy {
  display: grid;
  row-gap: 0.38rem;
  align-content: start;
  min-width: 0;
}

.context-focus-strip__eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 72%, var(--text-secondary));
}

.context-focus-strip__heading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 0;
  flex-wrap: wrap;
}

.context-focus-strip__title {
  margin: 0;
  font-size: 1.08rem;
  color: var(--title-color);
}

.context-focus-strip__badge {
  min-height: 28px;
  padding: 0 10px;
  font-size: 0.74rem;
}

.context-focus-strip__desc {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.92rem;
  line-height: 1.7;
}

.context-focus-strip__aside {
  display: grid;
  justify-items: stretch;
  gap: var(--spacing-sm);
  min-width: 0;
}

.context-focus-strip__metric {
  display: grid;
  gap: 2px;
  padding: 0.8rem 0.95rem;
  border: 1px solid var(--border-panel-subtle);
  border-radius: var(--context-inset-radius);
  background: var(--surface-panel-inset);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline);
  -webkit-backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
}

.context-focus-strip__metric-label {
  font-size: 0.72rem;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.context-focus-strip__metric-value {
  font-size: 1.12rem;
  font-weight: 700;
  line-height: 1.1;
  color: var(--title-color);
}

.context-back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 34px;
  padding: 0 14px;
  width: 100%;
  font: inherit;
  line-height: 1;
  cursor: pointer;
  border: 1px solid var(--border-panel-default);
  border-radius: var(--context-inset-radius);
  background: var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline);
  -webkit-backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    transform var(--transition-base),
    background var(--transition-base);
}

.context-back-button:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, hsl(var(--context-accent-hue, 245), 72%, 56%) 26%, var(--border-interactive-hover));
  background: var(--surface-panel-default);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-hover);
}

.context-state {
  width: 100%;
  margin-top: var(--spacing-sm);
}

.category-categories-skel {
  display: grid;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  width: 100%;
}

.category-categories-skel__title {
  width: 12rem !important;
  height: 1.35rem !important;
}

.category-categories-skel__lines {
  display: grid;
  gap: var(--spacing-sm);
}

.category-categories-skel__line {
  width: 100% !important;
  height: 3.25rem !important;
  border-radius: calc(var(--border-radius-lg) + 4px) !important;
}

.category-categories-skel__line:nth-child(even) {
  width: 92% !important;
}

.context-article-list {
  display: grid;
  gap: var(--spacing-md);
}

.context-article-card {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr) auto;
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
  cursor: pointer;
  border: 1px solid var(--border-panel-default);
  border-radius: var(--context-entry-radius);
  background: 
    var(--surface-panel-specular-soft),
    var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    transform var(--transition-base),
    background var(--transition-base);
}

.context-article-card:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--primary-color) 24%, var(--border-interactive-hover));
  background: var(--surface-panel-default);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-hover);
}

.context-article-card__cover {
  min-height: 132px;
  border-radius: var(--context-inset-radius);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-05) 16%, transparent),
      color-mix(in srgb, var(--background-color-deep) 18%, transparent)
    ),
    var(--article-cover) center / cover no-repeat,
    radial-gradient(circle at 18% 18%, color-mix(in srgb, var(--primary-color) 12%, transparent), transparent 42%),
    var(--surface-panel-inset);
  border: 1px solid color-mix(in srgb, var(--border-panel-subtle) 88%, transparent);
}

.context-article-card__body {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.context-article-card__topline,
.context-article-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.context-article-card__date,
.context-article-card__meta {
  color: var(--text-secondary);
  font-size: 0.8rem;
}

.context-article-card__title {
  margin: 0;
  color: var(--title-color);
  font-size: 1.06rem;
  line-height: 1.45;
}

.context-article-card__summary {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.72;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.context-article-card__arrow {
  align-self: center;
}

.article-cascade-enter-active,
.article-cascade-leave-active {
  transition:
    opacity 0.34s ease,
    transform 0.34s ease;
  transition-delay: calc(var(--stagger-index, 0) * 36ms);
}

.article-cascade-enter-from,
.article-cascade-leave-to {
  opacity: 0;
  transform: translateY(18px);
}

@include sourcelin-down(lg) {
  .category-hero__summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .category-hero__stat:nth-child(3) {
    grid-column: 1 / -1;
  }
}

@include sourcelin-down(md) {
  .category-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .category-hero__summary {
    gap: var(--spacing-sm);
  }



  .categories-grid {
    grid-template-columns: 1fr;
  }

  .context-focus-strip,
  .context-article-card {
    grid-template-columns: 1fr;
  }

  .context-focus-strip__aside {
    justify-items: stretch;
  }

  .context-article-card__cover {
    min-height: 168px;
  }

  .category-arrow,
  .context-article-card__arrow {
    display: none;
  }
}

@include sourcelin-down(sm) {
  .category-hero__summary {
    grid-template-columns: 1fr;
  }

  .category-hero__stat:nth-child(3) {
    grid-column: auto;
  }





  .category-switch-shell {
    padding: var(--spacing-lg);
  }

  .category-card {
    min-height: 0;
    padding: var(--spacing-md);
  }

  .category-icon {
    width: 58px;
    height: 58px;
  }

  .context-focus-strip__icon {
    width: 52px;
    height: 52px;
  }
}
</style>
