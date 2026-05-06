<template>
  <div class="glass-page navigation-page">
    <PageShell>
      <SCard
        class="navigation-hero"
        variant="lite"
      >
        <div class="navigation-hero__main">
          <div class="navigation-hero__copy">
            <span class="article-card-prelude-label">导航</span>
            <h1 class="navigation-hero__title">网站导航</h1>
            <p class="navigation-hero__subtitle">
              聚合站内外常用资源，支持按分类浏览与关键词直达。
            </p>

            <section class="navigation-hero__summary" aria-label="导航概览" :aria-busy="showLoadingSkeleton">
              <SkeletonNavCardGrid v-if="showLoadingSkeleton" variant="hero" :count="3" />
              <template v-else>
                <HeroStatCard
                  v-for="tile in heroStatTiles"
                  :key="tile.label"
                  class="navigation-hero__stat"
                  tone="neutral"
                  size="lg"
                  :icon="tile.icon"
                  :value="tile.value"
                  :label="tile.label"
                  :aria-label="tile.ariaLabel"
                />
              </template>
            </section>
          </div>

          <aside class="navigation-hero__aside" aria-label="使用说明">
            <div class="navigation-hero__aside-panel">
              <p class="navigation-hero__aside-eyebrow">使用方式</p>
              <h2 class="navigation-hero__aside-title">先筛选，再访问</h2>
              <p class="navigation-hero__aside-copy">
                下方支持关键词过滤、分类索引和最近访问回看，帮助你更快定位目标入口。
              </p>
              <div class="navigation-hero__aside-meta">
                <span class="navigation-hero__aside-chip">关键词过滤</span>
                <span class="navigation-hero__aside-chip">分类索引</span>
                <span class="navigation-hero__aside-chip">最近访问</span>
              </div>
            </div>
          </aside>
        </div>
      </SCard>

      <SSurfacePanel tag="section" class="nav-filter-bar" variant="soft" aria-labelledby="nav-filter-title" :aria-busy="showLoadingSkeleton">
        <div class="section-head nav-section-head">
          <div>
            <h2 id="nav-filter-title" class="section-title">快速筛选</h2>
            <p class="section-copy">可按名称、描述、来源或分类关键词实时筛选。</p>
          </div>
          <SSurfaceChip v-if="!showLoadingSkeleton" class="section-count" size="sm" variant="counter">{{ filteredItemCount }} 项结果</SSurfaceChip>
          <SSkeleton v-else :sharp="false" class="section-count-skeleton" :width="92" :height="30" />
        </div>

        <div v-if="showLoadingSkeleton" class="nav-filter-skeleton" aria-live="polite">
          <div class="nav-filter-skeleton__input">
            <SSkeleton :sharp="false" class="nav-filter-skeleton__input-bar" :height="42" />
          </div>
          <SkeletonNavCardGrid variant="chip" :count="6" />
          <SkeletonNavCardGrid variant="recent" :count="4" />
        </div>

        <div v-else class="nav-filter-bar__body">
          <SInput
            v-model="searchKeyword"
            class="nav-search-input"
            placeholder="搜索名称、描述、来源或分类"
            clearable
          />

          <div v-if="filteredEntries.length" class="nav-index-grid" aria-label="分类快速索引">
            <button
              v-for="([category, items]) in filteredEntries"
              :key="`index-${category}`"
              type="button"
              class="nav-index-chip"
              @click="scrollToCategory(category)"
            >
              <span>{{ category }}</span>
              <span class="nav-index-chip__count">{{ items.length }}</span>
            </button>
          </div>

          <div v-if="recentItems.length" class="nav-recent-panel">
            <div class="nav-recent-panel__head">
              <h3 class="nav-recent-panel__title">最近访问</h3>
              <span class="nav-recent-panel__count">{{ recentItems.length }} 条</span>
            </div>
            <div class="nav-recent-panel__list">
              <a
                v-for="item in recentItems"
                :key="`recent-${item.id}`"
                :href="item.url"
                target="_blank"
                rel="noopener noreferrer"
                class="nav-recent-link"
                @click="handleOpen(item)"
              >
                {{ item.name }}
              </a>
            </div>
          </div>
        </div>
      </SSurfacePanel>

      <template v-if="showLoadingSkeleton">
        <div class="nav-skeleton-stack" aria-busy="true" aria-live="polite">
          <SSurfacePanel
            v-for="sectionIndex in 2"
            :key="`nav-skeleton-${sectionIndex}`"
            tag="section"
            class="nav-category nav-category--skeleton"
            variant="soft"
          >
            <div class="section-head nav-category-head">
              <div class="nav-category-skeleton__copy">
                <SSkeleton :sharp="false" class="nav-category-skeleton__title" :width="118" :height="20" />
                <SSkeleton :sharp="false" class="nav-category-skeleton__subtitle" :width="280" :height="14" />
              </div>
              <SSkeleton :sharp="false" class="nav-category-skeleton__count" :width="64" :height="30" />
            </div>

            <SkeletonNavCardGrid variant="card" :count="sectionIndex === 1 ? 6 : 4" />
          </SSurfacePanel>
        </div>
      </template>

      <template v-else-if="filteredEntries.length">
        <SSurfacePanel
          v-for="([category, items]) in filteredEntries"
          :id="sectionAnchor(category)"
          :key="category"
          tag="section"
          class="nav-category"
          variant="soft"
        >
          <div class="section-head nav-category-head">
            <div>
              <h2 class="section-title">{{ category }}</h2>
              <p class="section-copy">{{ resolveCategoryDescription(category, items.length) }}</p>
            </div>
            <SSurfaceChip class="section-count" size="sm" variant="counter">{{ items.length }} 项</SSurfaceChip>
          </div>

          <div class="nav-grid">
            <SSurfacePanel
              v-for="item in items"
              :key="item.id"
              tag="a"
              :href="item.url"
              target="_blank"
              rel="noopener noreferrer"
              class="nav-item"
              variant="default"
              interactive
              @click="handleOpen(item)"
            >
              <span class="nav-icon">{{ item.icon || '#' }}</span>
              <div class="nav-info">
                <div class="nav-info__head">
                  <h3 class="nav-name">{{ item.name }}</h3>
                  <span v-if="item.source" class="nav-source">{{ item.source }}</span>
                </div>
                <p class="nav-desc">{{ item.description || item.source || '暂无描述' }}</p>
              </div>
            </SSurfacePanel>
          </div>
        </SSurfacePanel>
      </template>

      <EmptyState
        v-else-if="!loading"
        class="navigation-empty-state"
        variant="page"
        icon="links"
        :title="searchKeyword ? '没找到对应导航' : '这里还没有导航资源'"
        :message="searchKeyword ? '换个关键词试试，或清空筛选看全部内容。' : '导航目录正在整理中，晚点再来看看。'"
      />
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getNavItems, reportNavItemClick, type NavItem } from '@/modules/navigation/api/navigation.api'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SkeletonNavCardGrid from '@/shared/components/feedback/skeletons/SkeletonNavCardGrid.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface RecentNavItem extends Pick<NavItem, 'id' | 'name' | 'url' | 'category'> {}

const loading = ref(false)
const navigationData = ref<Record<string, NavItem[]>>({})
const searchKeyword = ref('')
const recentItems = ref<RecentNavItem[]>([])
const RECENT_STORAGE_KEY = 'navigation-recent-items'

const totalItemCount = computed(() => {
  return Object.values(navigationData.value).reduce((sum, items) => sum + items.length, 0)
})

const filteredEntries = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return Object.entries(navigationData.value)
    .map(([category, items]) => {
      if (!keyword) {
        return [category, items] as const
      }

      const nextItems = items.filter((item) => {
        return [item.name, item.description, item.source, item.category]
          .map((value) => String(value || '').toLowerCase())
          .some((value) => value.includes(keyword))
      })

      return [category, nextItems] as const
    })
    .filter(([, items]) => items.length > 0)
})

const filteredItemCount = computed(() => {
  return filteredEntries.value.reduce((sum, [, items]) => sum + items.length, 0)
})
const showLoadingSkeleton = computed(() => loading.value && totalItemCount.value === 0)

const heroStatTiles = computed(() => {
  return [
    {
      label: '分类章节',
      value: String(filteredEntries.value.length || Object.keys(navigationData.value).length).padStart(2, '0'),
      icon: appIcons.category,
      ariaLabel: `当前展示 ${filteredEntries.value.length || Object.keys(navigationData.value).length} 个分类章节`
    },
    {
      label: '资源总数',
      value: String(filteredItemCount.value || totalItemCount.value).padStart(2, '0'),
      icon: appIcons.links,
      ariaLabel: `当前可浏览 ${filteredItemCount.value || totalItemCount.value} 个导航项`
    },
    {
      label: '最近访问',
      value: String(recentItems.value.length).padStart(2, '0'),
      icon: appIcons.archive,
      ariaLabel: `最近访问 ${recentItems.value.length} 条`
    }
  ]
})

function sectionAnchor(category: string) {
  return `nav-cat-${encodeURIComponent(category)}`
}

function resolveCategoryDescription(category: string, count: number) {
  return `${category} 分类收录 ${count} 个可直接访问的站点与工具。`
}

function pushRecentItem(item: NavItem | RecentNavItem) {
  const next = [
    {
      id: item.id,
      name: item.name,
      url: item.url,
      category: item.category
    },
    ...recentItems.value.filter((entry) => entry.id !== item.id)
  ].slice(0, 6)

  recentItems.value = next
  localStorage.setItem(RECENT_STORAGE_KEY, JSON.stringify(next))
}

function loadRecentItems() {
  const raw = localStorage.getItem(RECENT_STORAGE_KEY)
  if (!raw) {
    return
  }

  try {
    const parsed = JSON.parse(raw) as RecentNavItem[]
    recentItems.value = Array.isArray(parsed) ? parsed : []
  } catch {
    recentItems.value = []
  }
}

function scrollToCategory(category: string) {
  document.getElementById(sectionAnchor(category))?.scrollIntoView({
    behavior: 'smooth',
    block: 'start'
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const rows = await getNavItems()
    const grouped: Record<string, NavItem[]> = {}

    rows.forEach((item) => {
      const category = item.category || '未分类'
      if (!grouped[category]) {
        grouped[category] = []
      }
      grouped[category].push(item)
    })

    navigationData.value = grouped
  } catch (error) {
    console.error('加载导航目录失败', error)
  } finally {
    loading.value = false
  }
}

const handleOpen = (item: NavItem | RecentNavItem) => {
  pushRecentItem(item)
  void reportNavItemClick(item.id).catch(() => undefined)
}

onMounted(() => {
  loadRecentItems()
  void loadData()
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.navigation-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.navigation-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.navigation-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.navigation-hero__main {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.18fr) minmax(260px, 0.82fr);
  gap: var(--spacing-xxl);
  align-items: start;
}

.navigation-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.navigation-hero__title {
  margin: 0;
  font-size: clamp(2.6rem, 5vw, 4rem);
  line-height: 0.96;
  letter-spacing: 0.03em;
  color: var(--title-color);
}

.navigation-hero__subtitle {
  max-width: 40rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.navigation-hero__summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
}

.navigation-hero__aside-panel {
  display: grid;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
}

.navigation-hero__aside-eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.navigation-hero__aside-title {
  margin: 0;
  font-size: 1.5rem;
  color: var(--title-color);
}

.navigation-hero__aside-copy {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.72;
}

.navigation-hero__aside-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.navigation-hero__aside-chip,
.nav-source,
.nav-index-chip,
.nav-recent-link {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.section-count {
  flex-shrink: 0;
  align-self: center;
}

.nav-filter-bar,
.nav-category {
  position: relative;
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  overflow: hidden;
}

.nav-category::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 156px;
  background: var(--surface-page-highlight);
  opacity: 0.62;
  pointer-events: none;
}

.nav-filter-bar__body,
.nav-filter-skeleton,
.nav-recent-panel,
.nav-grid {
  position: relative;
  z-index: 1;
}

.nav-filter-bar__body,
.nav-filter-skeleton {
  display: grid;
  gap: var(--spacing-md);
}

.nav-filter-skeleton__input {
  padding: 12px;
}

.nav-filter-skeleton__input-bar {
  width: 100% !important;
}

.nav-search-input {
  width: 100%;
}

.nav-index-grid {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.nav-index-chip {
  gap: 8px;
  border: none;
  cursor: pointer;
}

.nav-index-chip__count {
  color: var(--text-primary);
  font-weight: 700;
}

.nav-recent-panel {
  display: grid;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
}

.nav-recent-panel__head,
.nav-recent-panel__list,
.section-head,
.nav-info__head {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  justify-content: space-between;
  flex-wrap: wrap;
}

.nav-recent-panel__title,
.section-title {
  margin: 0;
  color: var(--title-color);
}

.nav-recent-panel__title {
  font-size: 0.96rem;
}

.nav-recent-panel__count {
  font-size: 0.78rem;
  color: var(--text-secondary);
}

.nav-recent-panel__list {
  justify-content: flex-start;
}

.section-copy {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.7;
}

.nav-category-head {
  position: relative;
  z-index: 1;
}

.nav-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--spacing-md);
}

.nav-skeleton-stack {
  display: grid;
  gap: var(--spacing-xl);
}

.nav-category-skeleton__copy {
  display: grid;
  gap: var(--spacing-xs);
}

.section-count-skeleton,
.nav-category-skeleton__count {
  border-radius: 999px !important;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  text-decoration: none;
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  overflow: hidden;
  transition:
      transform var(--duration-medium) var(--ease-standard),
      border-color var(--duration-medium) var(--ease-standard),
      box-shadow var(--duration-medium) var(--ease-standard),
      background var(--duration-medium) var(--ease-standard);
}

.nav-item::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 46%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--duration-medium) var(--ease-standard);
}

.nav-item:hover {
  transform: translateY(-3px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  font-size: 1.15rem;
  flex-shrink: 0;
  transition:
    transform var(--duration-medium) var(--ease-standard),
    box-shadow var(--duration-medium) var(--ease-standard),
    background var(--duration-medium) var(--ease-standard);
}

.nav-item:hover .nav-icon {
  transform: translateY(-1px) scale(1.04);
  box-shadow: var(--highlight-panel-chip);
}

.nav-info {
  min-width: 0;
  flex: 1;
}

.nav-name {
  margin: 0;
  font-size: 0.98rem;
  font-weight: 700;
  color: var(--title-color);
}

.nav-desc {
  margin: 0;
  font-size: 0.84rem;
  line-height: 1.6;
  color: var(--text-secondary);
  transition: color var(--duration-medium) var(--ease-standard);
}

.nav-item:hover .nav-desc {
  color: var(--text-primary);
}

.navigation-empty-state {
  margin-top: var(--spacing-md);
}

@include sourcelin-down(lg) {
  .navigation-hero__summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .navigation-hero__stat:last-child {
    grid-column: 1 / -1;
  }
}

@include sourcelin-down(md) {
  .navigation-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .navigation-hero__main,
  .navigation-hero__summary,
  .nav-grid {
    grid-template-columns: 1fr;
  }

  .navigation-hero__stat:last-child {
    grid-column: auto;
  }

  .nav-filter-bar,
  .nav-category,
  .navigation-hero__aside-panel {
    padding: var(--spacing-lg);
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
