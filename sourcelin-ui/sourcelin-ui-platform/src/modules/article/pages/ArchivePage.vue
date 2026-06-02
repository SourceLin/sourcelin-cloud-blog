<template>
  <div class="glass-page archive-page">
    <PageShell>
      <SCard
        class="archive-hero"
        variant="lite"
      >
        <div class="archive-header-main">
          <div class="archive-hero__copy">
            <span
              class="article-card-prelude-label"
            >文库</span>

            <div
              class="archive-hero__title-wrap"
              :class="{ 'archive-hero__title-wrap--filtered': categoryFilterId != null }"
            >
              <h1 class="archive-hero__title">文章归档</h1>
              <div v-if="categoryFilterId != null" class="archive-hero__title-trail">
                <button
                  type="button"
                  class="archive-hero__filter-tag"
                  :aria-label="`清除分类筛选：${filterCategoryName || '当前分类'}`"
                  :title="`点击清除筛选，当前：${filterCategoryName || '…'}`"
                  @click="clearCategoryFilter"
                >
                  <SIcon
                    :icon="appIcons.category"
                    :size="12"
                    class="archive-hero__filter-tag-icon"
                    aria-hidden="true"
                  />
                  <span class="archive-hero__filter-tag-name">
                    <span class="archive-hero__filter-tag-name-text archive-hero__filter-tag-name-text--category">{{
                      filterCategoryName || '…'
                    }}</span>
                    <span class="archive-hero__filter-tag-name-text archive-hero__filter-tag-name-text--action" aria-hidden="true">
                      全部归档
                    </span>
                  </span>
                </button>
              </div>
            </div>

            <p class="archive-hero__subtitle">{{ archiveSubtitle }}</p>

            <section class="archive-summary" aria-label="归档概览">
              <HeroStatCard
                v-for="tile in archiveSummaryTiles"
                :key="tile.label"
                class="archive-hero__stat"
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

      <div
        v-if="loadingArchives && archiveKeys.length"
        class="archive-loading-hint"
        aria-busy="true"
        aria-label="正在更新当前归档内容"
      >
        <SSkeleton circle class="archive-loading-hint__dot-sk" :width="8" :height="8" />
        <SSkeleton :sharp="false" class="archive-loading-hint__text-sk" />
      </div>

      <div
        v-if="loadingArchives && !archiveKeys.length"
        class="archive-loading-stack"
        aria-busy="true"
        aria-label="归档加载中"
      >
        <div
          v-for="index in 3"
          :key="`archive-loading-${index}`"
          class="archive-loading-panel"
        >
          <div class="archive-loading-panel__head">
            <SSkeleton :sharp="false" class="archive-sk-head-title" />
            <SSkeleton :sharp="false" class="archive-sk-head-chip" />
          </div>
          <div class="archive-loading-panel__list">
            <div
              v-for="cardIndex in 2"
              :key="`archive-loading-${index}-${cardIndex}`"
              class="archive-loading-card"
            >
              <SSkeleton :sharp="false" class="archive-sk-date" />
              <div class="archive-loading-card__body">
                <SSkeleton :sharp="false" class="archive-sk-line archive-sk-line--strong" />
                <SSkeleton :sharp="false" class="archive-sk-line" />
                <SSkeleton :sharp="false" class="archive-sk-line archive-sk-line--short" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
        v-else-if="archiveKeys.length"
        class="archive-anchor-bar"
        aria-label="归档时间定位"
      >
        <button
          v-for="item in archiveAnchorItems"
          :key="item.key"
          type="button"
          class="archive-anchor-bar__item"
          @click="scrollToArchiveGroup(item.key)"
        >
          {{ item.label }}
        </button>
      </div>

      <STimeline v-if="archiveKeys.length" class="archive-timeline" :icon-size="18" variant="archive">
        <STimelineItem
          v-for="yearMonth in archiveKeys"
          :key="yearMonth"
          class="archive-timeline-item"
          variant="archive"
        >
          <template #icon>
            <span class="timeline-icon">
              <SIcon :icon="appIcons.calendar" :size="16" />
            </span>
          </template>

          <div :id="`archive-group-${yearMonth}`" class="archive-timeline-panel">
            <div class="timeline-group">
              <div class="timeline-marker">
                <div class="timeline-marker__copy">
                  <span class="marker-kicker">归档分组</span>
                  <span class="marker-date">{{ yearMonth }}</span>
                </div>
                <SSurfaceChip class="marker-count" size="sm" variant="counter">{{ archives[yearMonth].length }} 篇</SSurfaceChip>
              </div>

              <div class="articles-list">
                <article
                  v-for="article in archives[yearMonth]"
                  :key="article.id"
                  class="article-card"
                  @click="goToArticle(article.id)"
                >
                  <div class="article-date">
                    <span class="day">{{ getDay(article.createTime) }}</span>
                    <span class="month">{{ getMonth(article.createTime) }}</span>
                  </div>

                  <div class="article-content">
                    <h3 class="article-title">{{ article.title }}</h3>
                    <p class="article-summary">{{ article.summary }}</p>
                    <div class="article-meta">
                      <span class="meta-item">
                        <SIcon :icon="appIcons.eye" :size="14" />
                        {{ article.viewCount || 0 }}
                      </span>
                      <span class="meta-item">
                        <SIcon :icon="appIcons.comment" :size="14" />
                        {{ article.commentCount || 0 }}
                      </span>
                    </div>
                  </div>

                  <div class="article-arrow">
                    <SIcon :icon="appIcons.arrowForward" :size="16" />
                  </div>
                </article>
              </div>
            </div>
          </div>
        </STimelineItem>
      </STimeline>

      <div v-if="archiveKeys.length" class="archive-load-more">
        <SButton
          v-if="archiveHasMore"
          variant="ghost"
          :loading="loadingArchives"
          @click="archiveLoadMore"
        >
          加载更早归档
        </SButton>
        <span v-else class="archive-load-more__end">已加载全部 {{ archiveTotal || totalArticles }} 篇归档文章</span>
      </div>

      <EmptyState
        v-else
        class="archive-empty-state"
        variant="page"
        icon="calendar"
        title="暂无文章"
        message="归档区还空着，过一会儿再来看也许就有新文章了。"
      />
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles, type ArticlePageQuery } from '@/modules/article/api/article.api'
import { getCategoryDetail } from '@/modules/article/api/category.api'
import { buildArchiveArticleRoute } from '@/modules/article/utils/article-source'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import { useSeoHead } from '@/shared/composables/useSeoHead'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import STimeline from '@/shared/components/ui/STimeline.vue'
import STimelineItem from '@/shared/components/ui/STimelineItem.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Article {
  id: number
  title: string
  summary: string
  createTime: string
  viewCount: number
  commentCount: number
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

const route = useRoute()
const router = useRouter()
const archiveItems = ref<Article[]>([])
const filterCategoryName = ref('')
const loadingArchives = ref(false)
const archivePage = ref(1)

// 归档页 SEO
useSeoHead({
  title: computed(() => '文章归档'),
  description: computed(() => '浏览全部文章，按时间线序排列，查找你感兴趣的内容。'),
  canonicalUrl: computed(() => typeof window !== 'undefined' ? `${window.location.origin}/archive` : '')
})
const archivePageSize = 120
const archiveTotal = ref(0)
const archiveHasMore = ref(false)

/** 归档页按分类筛选（与分类页、首页入口约定一致） */
const categoryFilterId = computed((): number | null => {
  const raw = route.query.categoryId
  if (typeof raw === 'string' && raw.trim()) {
    const n = Number(raw)
    return Number.isFinite(n) && n > 0 ? n : null
  }
  if (Array.isArray(raw) && raw[0]) {
    const n = Number(String(raw[0]))
    return Number.isFinite(n) && n > 0 ? n : null
  }
  return null
})

const archiveSubtitle = computed(() => {
  if (categoryFilterId.value != null) {
    const name = filterCategoryName.value
    const label = name ? `「${name}」` : '该分类'
    return `仅显示 ${label} 下的已发布文章，按时间归档。`
  }
  return '按时间整理所有文章，在时间轴上回看每一次书写与更新。'
})

const archives = computed(() => buildArchiveGroups(archiveItems.value))
const totalArticles = computed(() => archiveItems.value.length)
const archiveKeys = computed(() => Object.keys(archives.value))
const archiveAnchorItems = computed(() =>
  archiveKeys.value.map((key) => ({
    key,
    label: key
  }))
)

interface ArchiveSummaryTile {
  label: string
  value: number
  icon: Component
  hue: number
  ariaLabel: string
}

const archiveSummaryTiles = computed((): ArchiveSummaryTile[] => {
  const total = totalArticles.value
  const groups = archiveKeys.value.length
  return [
    {
      label: '文章总数',
      value: total,
      icon: appIcons.document,
      hue: 220,
      ariaLabel: `文章共 ${total} 篇`
    },
    {
      label: '时间分组',
      value: groups,
      icon: appIcons.calendar,
      hue: 168,
      ariaLabel: `按月份分为 ${groups} 个时间组`
    }
  ]
})

const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

function sortByTimeDesc(items: Article[]) {
  return [...items].sort((left, right) => {
    return new Date(right.createTime).getTime() - new Date(left.createTime).getTime()
  })
}

function buildArchiveGroups(items: Article[]) {
  const grouped = sortByTimeDesc(items).reduce<Record<string, Article[]>>((result, item) => {
    if (!item.createTime) {
      return result
    }

    const date = new Date(item.createTime)
    const key = `${date.getFullYear()}年 ${String(date.getMonth() + 1).padStart(2, '0')}月`

    if (!result[key]) {
      result[key] = []
    }

    result[key].push(item)
    return result
  }, {})

  return Object.fromEntries(
    Object.entries(grouped).sort(([left], [right]) => {
      return new Date(right.replace('年 ', '-').replace('月', '-01')).getTime() -
        new Date(left.replace('年 ', '-').replace('月', '-01')).getTime()
    })
  )
}

const loadArchives = async (reset = true) => {
  const cid = categoryFilterId.value
  loadingArchives.value = true
  try {
    if (reset) {
      archivePage.value = 1
      archiveItems.value = []
    }

    const params: ArticlePageQuery = { page: archivePage.value, pageSize: archivePageSize }
    if (cid != null) {
      params.categoryId = cid
    }
    const res = await getArticles(params)
    const rows = Array.isArray(res.items) ? res.items : []

    const articles = rows
      .map((item) => ({
        id: item.id ?? 0,
        title: item.title ?? '未命名文章',
        summary: item.summary ?? '',
        createTime: item.createTime ?? '',
        viewCount: item.viewCount ?? 0,
        commentCount: item.commentCount ?? 0
      }))
      .filter((item) => item.createTime)

    archiveTotal.value = Number(res.total ?? 0)
    const merged = reset
      ? articles
      : [...archiveItems.value, ...articles].filter(
          (item, index, source) => source.findIndex((entry) => entry.id === item.id) === index
        )
    archiveItems.value = merged
    const totalPages = Number(res.totalPages ?? 0)
    archiveHasMore.value = totalPages > 0
      ? archivePage.value < totalPages
      : articles.length >= archivePageSize
  } catch (error) {
    console.error('加载归档失败:', error)
    archiveItems.value = []
    archiveTotal.value = 0
    archiveHasMore.value = false
  } finally {
    loadingArchives.value = false
  }
}

const archiveLoadMore = async () => {
  if (loadingArchives.value || !archiveHasMore.value) {
    return
  }

  archivePage.value += 1
  await loadArchives(false)
}

const loadFilterCategoryName = async () => {
  const id = categoryFilterId.value
  if (id == null) {
    filterCategoryName.value = ''
    return
  }
  try {
    const row = await getCategoryDetail(id)
    filterCategoryName.value = typeof row.name === 'string' ? row.name : ''
  } catch {
    filterCategoryName.value = ''
  }
}

const clearCategoryFilter = () => {
  void router.replace('/archive')
}

function scrollToArchiveGroup(key: string) {
  const target = document.getElementById(`archive-group-${key}`)
  if (!(target instanceof HTMLElement)) {
    return
  }

  const top = target.getBoundingClientRect().top + window.scrollY - 92
  window.scrollTo({
    top: Math.max(top, 0),
    behavior: 'smooth'
  })
}

const getDay = (date: string) => new Date(date).getDate()
const getMonth = (date: string) => months[new Date(date).getMonth()]

const goToArticle = (id: number) => {
  void router.push(
    buildArchiveArticleRoute(id, {
      categoryId: categoryFilterId.value,
      categoryName: filterCategoryName.value
    })
  )
}

watch(
  categoryFilterId,
  () => {
    void loadArchives(true)
    void loadFilterCategoryName()
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.archive-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.archive-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

/* 归档头部：单栏文案 + 通栏横向统计条（与说说页双栏 hero 刻意区分） */
.archive-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.archive-anchor-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 18px;
  padding: 14px 16px;
  border-radius: 18px;
}

.archive-anchor-bar__item {
  border: none;
  cursor: pointer;
}

.archive-load-more {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.archive-load-more__end {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.archive-header-main {
  position: relative;
  display: block;
}

.archive-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
}

/* 与文章详情侧栏 article-card-prelude-label 同源的眉题 chip */
.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

/* 筛选标签叠在标题块右下角，整块可点清除 */
.archive-hero__title-wrap {
  position: relative;
  display: block;
}

.archive-hero__title-wrap--filtered .archive-hero__title {
  padding-inline-end: min(46%, 12.5rem);
}

.archive-hero__title {
  margin: 0;
  font-size: clamp(2.5rem, 5vw, 4rem);
  line-height: 0.98;
  letter-spacing: 0.04em;
  color: var(--title-color);
}

.archive-hero__title-trail {
  position: absolute;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  max-width: min(46%, 12.5rem);
}

.archive-hero__subtitle {
  max-width: 42rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.archive-hero__filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  min-width: 0;
  max-width: 100%;
  margin: 0;
  padding: 0.18rem 0.55rem 0.18rem 0.45rem;
  font: inherit;
  font-size: var(--font-size-sm, 12px);
  line-height: 1.35;
  cursor: pointer;
  text-align: start;
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    transform var(--transition-base);
}

.archive-hero__filter-tag:hover {
  border-color: color-mix(in srgb, var(--primary-color) 28%, var(--border-panel-chip));
  box-shadow: var(--shadow-panel-soft);
  transform: translateY(-1px);
}

.archive-hero__filter-tag:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 22%, transparent);
}

.archive-hero__filter-tag-icon {
  flex-shrink: 0;
  color: color-mix(in srgb, var(--primary-color) 75%, var(--text-secondary));
  pointer-events: none;
}

/* 默认宽度随分类名收缩；悬停文案叠在左侧，不参与布局，避免两字分类也被撑宽 */
.archive-hero__filter-tag-name {
  position: relative;
  display: inline-block;
  max-width: 9.5rem;
  min-width: 0;
  vertical-align: bottom;
}

.archive-hero__filter-tag-name-text {
  font-weight: 600;
  font-size: var(--font-size-sm, 12px);
  line-height: 1.35;
  white-space: nowrap;
  transition: opacity var(--transition-base);
}

.archive-hero__filter-tag-name-text--category {
  display: inline-block;
  max-width: 100%;
  color: var(--title-color);
  overflow: hidden;
  text-overflow: ellipsis;
}

.archive-hero__filter-tag-name-text--action {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  color: var(--primary-color);
  opacity: 0;
  pointer-events: none;
}

/* 短分类名悬停时至少容纳「全部归档」四字 */
.archive-hero__filter-tag:hover .archive-hero__filter-tag-name,
.archive-hero__filter-tag:focus-visible .archive-hero__filter-tag-name {
  min-width: 4.75em;
}

.archive-hero__filter-tag:hover .archive-hero__filter-tag-name-text--category,
.archive-hero__filter-tag:focus-visible .archive-hero__filter-tag-name-text--category {
  opacity: 0;
}

.archive-hero__filter-tag:hover .archive-hero__filter-tag-name-text--action,
.archive-hero__filter-tag:focus-visible .archive-hero__filter-tag-name-text--action {
  opacity: 1;
}

/* 通栏一行：左右两块横向长方形统计卡 */
.archive-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}









.archive-timeline {
  width: 100%;
}

.archive-loading-hint {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  width: fit-content;
  padding: 0.72rem 0.9rem;
  color: var(--text-secondary);
  font-size: 0.86rem;
}

.archive-loading-hint__dot-sk {
  width: 8px !important;
  height: 8px !important;
  flex-shrink: 0;
}

.archive-loading-hint__text-sk {
  width: 12rem !important;
  height: 0.9rem !important;
}

.archive-loading-stack {
  display: grid;
  gap: var(--spacing-lg);
}

.archive-loading-panel {
  display: grid;
  gap: var(--spacing-md);
  padding: clamp(1rem, 2vw, 1.4rem);
  border-radius: calc(var(--glass-radius) + 6px);
}

.archive-loading-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.archive-loading-panel__list {
  display: grid;
  gap: 10px;
}

.archive-loading-card {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr);
  gap: 16px;
  align-items: center;
  padding: 16px;
}

.archive-loading-card__body {
  display: grid;
  gap: 10px;
}

.archive-sk-head-title {
  width: 160px !important;
  height: 18px !important;
}

.archive-sk-head-chip {
  width: 72px !important;
  height: 28px !important;
  border-radius: 999px !important;
}

.archive-sk-date {
  width: 62px !important;
  height: 62px !important;
  border-radius: 22px !important;
}

.archive-sk-line {
  width: 100% !important;
  height: 12px !important;
}

.archive-sk-line--strong {
  width: min(60%, 22rem) !important;
  height: 18px !important;
}

.archive-sk-line--short {
  width: 72% !important;
}

.archive-timeline-item {
  --timeline-shell-padding: clamp(1rem, 2vw, 1.4rem);
}

.timeline-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  color: var(--primary-color);
}

.archive-timeline-panel {
  position: relative;
  overflow: hidden;
  padding: var(--timeline-shell-padding);
  border-radius: calc(var(--glass-radius) + 6px);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
}

.archive-timeline-panel::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 44%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
}

.timeline-group {
  position: relative;
  display: grid;
  gap: 16px;
}

.timeline-marker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.timeline-marker__copy {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.marker-kicker {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.marker-date {
  font-size: clamp(1.08rem, 2vw, 1.28rem);
  font-weight: 700;
  color: var(--title-color);
  letter-spacing: 0.03em;
}

.marker-count {
  flex-shrink: 0;
  align-self: center;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.article-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: calc(var(--border-radius-xl) + 2px);
  border: 1px solid var(--border-panel-default);
  background: 
    var(--surface-panel-specular-soft),
    var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  backdrop-filter: blur(calc(var(--glass-blur) + 1px)) saturate(var(--glass-saturate));
  cursor: pointer;
  isolation: isolate;
  overflow: hidden;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.article-card:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-panel-default), var(--shadow-panel-hover);
  background: var(--surface-panel-default);
}

.article-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 42%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.article-card:hover::before {
  opacity: 1;
}

.article-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 62px;
  height: 62px;
  flex-shrink: 0;
}

.day {
  font-size: 1.3rem;
  font-weight: 700;
  line-height: 1;
  color: var(--primary-color);
}

.month {
  font-size: 0.72rem;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-title {
  margin: 0 0 6px;
  font-size: 1rem;
  font-weight: 700;
  color: var(--title-color);
}

.article-summary {
  margin: 0 0 10px;
  font-size: 0.88rem;
  line-height: 1.6;
  color: var(--text-secondary);
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
}

.article-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.meta-item,
.article-arrow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 28px;
  padding: 0 10px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.article-arrow {
  transition:
    transform var(--transition-base),
    background var(--transition-base),
    color var(--transition-base);
}

.article-card:hover .article-arrow {
  transform: translateX(3px);
  color: var(--primary-color);
  background: var(--surface-panel-chip-accent);
}

.archive-empty-state {
  margin-top: var(--spacing-md);
}

@include sourcelin-down(md) {
  .archive-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .archive-summary {
    gap: var(--spacing-sm);
  }

  .archive-timeline-panel {
    padding: var(--spacing-lg);
  }



  .archive-hero__title-wrap--filtered .archive-hero__title {
    padding-inline-end: min(52%, 11.5rem);
  }

  .archive-hero__title-trail {
    max-width: min(52%, 11.5rem);
  }

  .article-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .article-date {
    flex-direction: row;
    width: auto;
    height: auto;
    gap: 6px;
    padding: 10px 12px;
  }
}

@include sourcelin-down(sm) {
  .archive-hero__title-wrap--filtered .archive-hero__title {
    padding-inline-end: min(62%, 10rem);
  }

  .archive-hero__title-trail {
    max-width: min(62%, 10rem);
  }

  .archive-hero__filter-tag-name {
    max-width: min(7rem, 42vw);
  }

  .archive-summary {
    grid-template-columns: 1fr;
    gap: var(--spacing-sm);
  }

  .archive-timeline-panel {
    padding: var(--spacing-md);
  }





}
</style>
