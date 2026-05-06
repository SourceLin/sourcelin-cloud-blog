<template>
  <div class="glass-page hot-page" :aria-busy="loading">
    <PageShell>
      <SCard
        class="hot-masthead sourcelin-structural-breathe"
        variant="lite"
      >
        <div class="hot-masthead__main">
          <div class="hot-masthead__copy">
            <span class="article-card-prelude-label">热榜</span>
            <h1 class="hot-masthead__title">热门内容</h1>
            <p class="hot-masthead__subtitle">
              把最近比较受欢迎的内容集中到这里，先看前几条，再继续往下翻。
            </p>

            <section class="hot-masthead__summary" aria-label="热榜概览">
              <HeroStatCard
                v-for="tile in mastheadStatTiles"
                :key="tile.label"
                class="hot-masthead__stat"
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

          <aside class="hot-overview-panel" aria-label="榜单说明">
            <div class="hot-overview-panel__glow" aria-hidden="true" />
            <div class="hot-overview-panel__body">
              <div class="hot-overview-panel__copy">
                <p class="hot-overview-panel__eyebrow">榜单说明</p>
                <h2 class="hot-overview-panel__title">{{ leadItem?.title || '榜单整理中' }}</h2>
                <p class="hot-overview-panel__lede">
                  {{
                    leadItem
                      ? leadItem.summary || '当前最受关注的内容会显示在这里，方便你先看重点。'
                      : '当前分类下暂无内容，切换分类后再看看。'
                  }}
                </p>
              </div>

              <div class="hot-overview-panel__meta">
                <span class="hot-overview-chip">{{ activeCategoryName }}</span>
                <span class="hot-overview-chip">{{ leadItem ? formatTime(leadItem.createTime) : '待更新' }}</span>
                <span class="hot-overview-chip">{{ hotList.length }} 条收录</span>
              </div>
            </div>
          </aside>
        </div>
      </SCard>

      <SSurfacePanel tag="section" class="hot-workspace" variant="soft" aria-labelledby="hot-workspace-title">
        <div class="section-head hot-section-head">
          <div>
            <h2 id="hot-workspace-title" class="section-title">榜单分类</h2>
            <p class="section-copy">先切换分类，再查看上方推荐和下方列表。</p>
          </div>
          <SSurfaceChip class="section-count" size="sm" variant="counter">{{ activeCategoryName }}</SSurfaceChip>
        </div>

        <div class="hot-workspace__body">
          <div class="hot-workspace__tabs">
            <HotCategoryTabs
              :categories="categories"
              :active-category="activeCategory"
              @change="changeCategory"
            />
          </div>

          <div class="hot-workspace__notes">
            <SSurfacePanel
              v-for="note in hotWorkspaceNotes"
              :key="note.label"
              tag="article"
              class="hot-note"
              variant="inset"
              interactive
            >
              <span class="hot-note__label">{{ note.label }}</span>
              <strong class="hot-note__value">{{ note.value }}</strong>
              <p class="hot-note__copy">{{ note.copy }}</p>
            </SSurfacePanel>
          </div>
        </div>
      </SSurfacePanel>

      <SSurfacePanel
        v-if="featuredItems.length"
        tag="section"
        class="hot-featured-section"
        variant="soft"
        aria-labelledby="hot-featured-title"
      >
        <div class="section-head hot-section-head">
          <div>
            <h2 id="hot-featured-title" class="section-title">热门推荐</h2>
            <p class="section-copy">这里会优先展示排名靠前的几条内容。</p>
          </div>
          <SSurfaceChip class="section-count" size="sm" variant="counter">Top {{ featuredItems.length }}</SSurfaceChip>
        </div>

        <div class="hot-featured-grid">
          <SSurfacePanel
            v-for="(item, index) in featuredItems"
            :key="`featured-${item.id}`"
            tag="article"
            class="featured-card"
            :class="{ 'featured-card--lead': index === 0 }"
            :variant="index === 0 ? 'strong' : 'default'"
            interactive
            @click="openItem(item)"
          >
            <div class="featured-card__rail" aria-hidden="true" />
            <div class="featured-card__topline">
              <span class="featured-card__rank" :class="getRankClass(index)">
                {{ formatRank(index) }}
              </span>
              <span class="featured-card__time">{{ formatTime(item.createTime) }}</span>
            </div>

            <div
              v-if="item.coverImage"
              class="featured-card__cover"
              :style="{ '--featured-cover': `url(${item.coverImage})` }"
            />

            <div class="featured-card__body">
              <div class="featured-card__headline">
                <span class="featured-card__heat">热度 {{ formatHeat(item.viewCount) }}</span>
                <span class="featured-card__talk">{{ item.commentCount || 0 }} 条讨论</span>
              </div>
              <h3 class="featured-card__title">{{ item.title }}</h3>
              <p class="featured-card__summary">{{ item.summary || '这条内容正在被频繁阅读和讨论。' }}</p>
              <div class="featured-card__meta">
                <span class="featured-card__meta-chip">收藏 {{ item.likeCount }}</span>
                <span class="featured-card__meta-chip">互动 {{ item.commentCount || 0 }}</span>
              </div>
            </div>
          </SSurfacePanel>
        </div>
      </SSurfacePanel>

      <SSurfacePanel
        v-if="hotList.length"
        tag="section"
        class="hot-stream-section"
        variant="soft"
        aria-labelledby="hot-stream-title"
      >
        <div class="section-head hot-section-head">
          <div>
            <h2 id="hot-stream-title" class="section-title">完整榜单</h2>
            <p class="section-copy">其余内容会按热度继续往下显示。</p>
            <p class="hot-pagination-status" aria-live="polite">
              {{ hotPaginationStatus }}
            </p>
          </div>
          <SSurfaceChip class="section-count" size="sm" variant="counter">{{ streamItems.length }} 条内容</SSurfaceChip>
        </div>

        <div ref="hotStreamSectionRef" class="hot-stream-waterfall">
          <WaterfallList
            v-if="streamItems.length"
            :items="streamItems"
            :column-width="waterfallColumnWidth"
            :gap="waterfallGap"
            :loading="loading"
            :finished="finished"
            loading-more-mode="quiet"
            load-more-text="继续浏览榜单"
            @load-more="loadMore"
          >
            <template #default="{ item, index, heightType }">
              <SSurfacePanel
                tag="div"
                class="hot-item"
                :class="heightType"
                variant="default"
                interactive
                @click="openItem(asHotItem(item))"
              >
                <div class="hot-topline">
                  <span class="hot-rank">
                    {{ formatRank(index, featuredItems.length) }}
                  </span>
                  <span class="hot-time">{{ formatTime(asHotItem(item).createTime) }}</span>
                </div>

                <div v-if="asHotItem(item).coverImage && heightType !== 'short'" class="hot-cover">
                  <img :src="asHotItem(item).coverImage" :alt="asHotItem(item).title">
                  <div class="hot-cover-overlay" />
                </div>

                <div class="hot-content">
                  <div class="hot-kicker-row">
                    <span class="hot-heat-chip">
                      热度 {{ formatHeat(asHotItem(item).viewCount) }}
                    </span>
                    <span v-if="heightType !== 'short'" class="hot-comments">
                      讨论 {{ asHotItem(item).commentCount || 0 }}
                    </span>
                  </div>

                  <div class="hot-title">{{ asHotItem(item).title }}</div>

                  <div v-if="asHotItem(item).summary && heightType === 'tall'" class="hot-summary">
                    {{ asHotItem(item).summary }}
                  </div>

                  <div class="hot-meta">
                    <span class="hot-meta-item">收藏 {{ asHotItem(item).likeCount }}</span>
                    <span class="hot-meta-item">互动 {{ asHotItem(item).commentCount || 0 }}</span>
                  </div>
                </div>

                <div v-if="heightType !== 'short'" class="hot-actions">
                  <span class="hot-like-chip">点赞 {{ asHotItem(item).likeCount }}</span>
                </div>
              </SSurfacePanel>
            </template>
          </WaterfallList>
        </div>

        <div v-if="!streamItems.length" class="hot-stream-empty">
          <EmptyState
            variant="inline"
            icon="information"
            title="这个分类先看到这里"
            message="切换一个分类，再看看新的热门内容。"
          />
        </div>
      </SSurfacePanel>

      <EmptyState
        v-if="!loading && !hotList.length"
        class="hot-empty-state"
        variant="page"
        icon="flame"
        title="暂无热榜内容"
        message="热榜还在更新中，晚点再来看看。"
      />
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import HotCategoryTabs from '@/modules/hot/components/HotCategoryTabs.vue'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import { useHotPage, type HotItem } from '@/modules/hot/composables/useHotPage'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import WaterfallList from '@/shared/components/layout/WaterfallList.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface HotMastheadStatTile {
  label: string
  value: string
  icon: typeof appIcons[keyof typeof appIcons]
  hue: number
  ariaLabel: string
}

const {
  loading,
  finished,
  page,
  pageSize,
  hotTotal,
  activeCategory,
  categories,
  hotList,
  loadMore,
  changeCategory,
  openItem,
  getRankClass,
  formatHeat,
  formatTime
} = useHotPage()

const activeCategoryName = computed(() => {
  return categories.value.find((item) => item.id === activeCategory.value)?.name || '综合'
})

const featuredItems = computed(() => hotList.value.slice(0, 3))
const streamItems = computed(() => hotList.value.slice(3))
const leadItem = computed(() => featuredItems.value[0] ?? null)
const totalHeat = computed(() => hotList.value.reduce((sum, item) => sum + Number(item.viewCount || 0), 0))
const hotPaginationStatus = computed(() => {
  const loadedCount = hotList.value.length
  const totalCount = hotTotal.value || loadedCount
  const pageStart = (page.value - 1) * pageSize + 1
  const pageEnd = Math.min(page.value * pageSize, totalCount)

  if (!loadedCount) {
    return '当前分类暂无热榜内容'
  }

  return `已加载 ${loadedCount} 条热榜内容，当前页覆盖第 ${pageStart} 至 ${pageEnd} 条，共 ${totalCount} 条`
})
const hotStreamSectionRef = ref<HTMLElement | null>(null)
const { width: hotStreamWidth } = useElementSize(hotStreamSectionRef)
const waterfallColumnWidth = computed(() => {
  if (hotStreamWidth.value <= 0) return 300
  if (hotStreamWidth.value <= 640) return Math.max(Math.floor(hotStreamWidth.value - 6), 220)
  if (hotStreamWidth.value <= 768) return Math.max(Math.floor(hotStreamWidth.value - 18), 260)
  return 300
})
const waterfallGap = computed(() => (hotStreamWidth.value > 0 && hotStreamWidth.value <= 640 ? 14 : 18))

const mastheadStatTiles = computed((): HotMastheadStatTile[] => {
  return [
    {
      label: '榜单分类',
      value: activeCategoryName.value,
      icon: appIcons.category,
      hue: 245,
      ariaLabel: `当前查看 ${activeCategoryName.value} 榜单`
    },
    {
      label: '收录内容',
      value: String(hotList.value.length).padStart(2, '0'),
      icon: appIcons.document,
      hue: 205,
      ariaLabel: `本期收录 ${hotList.value.length} 条内容`
    },
    {
      label: '总热度',
      value: formatHeat(totalHeat.value),
      icon: appIcons.flame,
      hue: 22,
      ariaLabel: `榜单总热度 ${formatHeat(totalHeat.value)}`
    }
  ]
})

const hotWorkspaceNotes = computed(() => {
  return [
    {
      label: '当前分类',
      value: activeCategoryName.value,
      copy: '切换分类后，上方推荐和下方列表会一起更新。'
    },
    {
      label: '焦点内容',
      value: leadItem.value ? formatHeat(leadItem.value.viewCount) : '--',
      copy: leadItem.value ? '排名第一的内容会单独展示。' : '当前还没有置顶内容。'
    },
    {
      label: '列表状态',
      value: hotList.value.length > 3 ? '展开中' : '精选态',
      copy: hotList.value.length > 3 ? '下面会继续显示其余内容。' : '当前以上方推荐为主。'
    }
  ]
})

function asHotItem(item: unknown): HotItem {
  return item as HotItem
}

function formatRank(index: number, offset = 0) {
  return String(index + offset + 1).padStart(2, '0')
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.hot-page {
  padding-top: var(--header-height, 64px);
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.hot-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.hot-masthead {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.hot-masthead__main {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(260px, 0.88fr);
  gap: var(--spacing-xxl);
  align-items: start;
}

.hot-masthead__copy {
  display: grid;
  gap: var(--page-block-gap);
  min-width: 0;
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.hot-masthead__title {
  margin: 0;
  font-size: clamp(2.7rem, 5vw, 4.2rem);
  line-height: 0.96;
  letter-spacing: 0.03em;
  color: var(--title-color);
}

.hot-masthead__subtitle {
  max-width: 42rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.hot-masthead__summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}








.hot-overview-panel {
  position: relative;
  display: grid;
  width: 100%;
  min-height: 100%;
  overflow: hidden;
}

.hot-overview-panel__glow {
  position: absolute;
  inset: auto -22% -34% auto;
  width: min(12rem, 70%);
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(circle, color-mix(in srgb, var(--primary-color) 16%, transparent) 0%, transparent 68%);
  pointer-events: none;
}

.hot-overview-panel__body {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.hot-overview-panel__copy {
  display: grid;
  gap: var(--spacing-sm);
}

.hot-overview-panel__eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.hot-overview-panel__title {
  margin: 0;
  font-size: clamp(1.4rem, 2.4vw, 1.9rem);
  line-height: 1.12;
  color: var(--title-color);
}

.hot-overview-panel__lede {
  margin: 0;
  font-size: var(--font-size-md, 14px);
  line-height: 1.7;
  color: var(--text-secondary);
}

.hot-overview-panel__meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.hot-overview-chip {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.hot-workspace,
.hot-featured-section,
.hot-stream-section {
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.hot-section-head {
  align-items: center;
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
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

.section-count {
  flex-shrink: 0;
  align-self: center;
}

.hot-workspace__body {
  display: grid;
  gap: var(--spacing-lg);
}

.hot-workspace__tabs {
  min-width: 0;
  overflow: hidden;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: calc(var(--border-radius-xl) - 2px);
}

.hot-workspace__notes {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
}

.hot-note {
  position: relative;
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
  padding: var(--spacing-md) var(--spacing-lg);
  overflow: hidden;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.hot-note::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 48%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.hot-note:hover {
  transform: scale(1.015);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
}

.hot-note:hover::before {
  opacity: 1;
}

.hot-note__label {
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.hot-note__value {
  font-size: 1.02rem;
  color: var(--title-color);
}

.hot-note__copy {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.86rem;
  line-height: 1.65;
}

.hot-featured-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.24fr) repeat(2, minmax(0, 0.88fr));
  gap: var(--spacing-lg);
}

.featured-card {
  position: relative;
  display: grid;
  gap: var(--spacing-md);
  min-width: 0;
  padding: var(--spacing-lg);
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  overflow: hidden;
  cursor: pointer;
}

.featured-card--lead {
  grid-row: span 2;
  min-height: 100%;
  border-color: var(--border-content-card-featured);
  background: var(--surface-content-card-featured);
  box-shadow: var(--highlight-panel-strong), var(--shadow-content-card-hover);
}

.featured-card:hover {
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.featured-card__rail {
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--warning-color) 68%, transparent),
    color-mix(in srgb, var(--primary-color) 42%, transparent)
  );
}

.featured-card__topline,
.featured-card__headline,
.featured-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.featured-card__rank,
.featured-card__time,
.featured-card__meta-chip {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.featured-card__cover {
  min-height: 11rem;
  border-radius: calc(var(--border-radius-xl) - 4px);
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 32%, transparent), transparent),
    linear-gradient(120deg, color-mix(in srgb, var(--primary-color) 12%, transparent), transparent 55%),
    var(--featured-cover) center / cover no-repeat,
    var(--surface-panel-inset);
}

.featured-card--lead .featured-card__cover {
  min-height: 15.5rem;
}

.featured-card__body {
  display: grid;
  gap: var(--spacing-sm);
  min-width: 0;
}

.featured-card__heat,
.featured-card__talk {
  font-size: 0.78rem;
  color: var(--text-secondary);
}

.featured-card__title {
  margin: 0;
  font-size: 1.08rem;
  line-height: 1.45;
  color: var(--title-color);
}

.featured-card--lead .featured-card__title {
  font-size: 1.28rem;
  line-height: 1.35;
}

.featured-card__summary {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.75;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.hot-stream-empty {
  padding: 0;
}

.hot-stream-waterfall {
  width: 100%;
  min-width: 0;
}

.hot-empty-state {
  margin-top: var(--spacing-md);
}

.hot-pagination-status {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.hot-item {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  cursor: pointer;
  margin-bottom: var(--page-block-gap);
  overflow: hidden;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.hot-item::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 42%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.hot-item.short {
  gap: 10px;
  padding: 12px;
}

.hot-item.short .hot-title {
  font-size: 0.94rem;
}

.hot-item.short .hot-meta {
  gap: 8px;
}

.hot-item.medium .hot-cover {
  height: 156px;
}

.hot-item.tall .hot-cover {
  height: 196px;
}

.hot-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.hot-rank,
.hot-time {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hot-rank {
  min-width: 54px;
  color: var(--title-color);
}

.hot-time {
  color: var(--text-secondary);
}

.hot-cover {
  position: relative;
  width: 100%;
  height: 152px;
  border-radius: calc(var(--glass-radius, 20px) - 8px);
  overflow: hidden;
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
}

.hot-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.hot-item:hover .hot-cover img {
  transform: scale(1.04);
}

.hot-item:hover::before {
  opacity: 1;
}

.hot-item:hover {
  transform: scale(1.014);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.hot-cover-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, transparent 0%, color-mix(in srgb, var(--background-color-deep) 12%, transparent) 100%),
    linear-gradient(120deg, color-mix(in srgb, var(--primary-color) 8%, transparent), transparent 55%);
  pointer-events: none;
}

.hot-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.hot-kicker-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.hot-heat-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  padding: 0 12px;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--title-color);
}

.hot-comments {
  font-size: 0.8rem;
  color: var(--text-secondary);
  white-space: nowrap;
}

.hot-title {
  margin-bottom: 10px;
  font-size: 1.02rem;
  font-weight: 700;
  line-height: 1.58;
  color: var(--title-color);
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.hot-summary {
  margin-bottom: 14px;
  font-size: 0.88rem;
  line-height: 1.72;
  color: var(--text-secondary);
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.hot-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: auto;
}

.hot-meta-item {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
  font-size: 0.78rem;
  color: var(--text-secondary);
  transition:
    border-color var(--transition-base),
    background var(--transition-base),
    color var(--transition-base),
    transform var(--transition-base);
}

.hot-item:hover .hot-meta-item {
  border-color: var(--border-interactive-hover);
  background: var(--surface-panel-chip-accent);
  color: var(--text-primary);
  transform: scale(1.03);
}

.hot-actions {
  display: flex;
  justify-content: flex-end;
}

.hot-like-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 34px;
  padding: 0 14px;
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-primary);
}

@include sourcelin-down(lg) {
  .hot-masthead__summary,
  .hot-workspace__notes {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .hot-masthead__stat:last-child,
  .hot-note:last-child {
    grid-column: 1 / -1;
  }

  .hot-featured-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .featured-card--lead {
    grid-column: 1 / -1;
    grid-row: auto;
  }
}

@include sourcelin-down(md) {
  .hot-masthead {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .hot-masthead__main {
    grid-template-columns: 1fr;
  }

  .hot-masthead__summary {
    gap: var(--spacing-sm);
  }

  .hot-overview-panel {
    min-height: 0;
  }

  .hot-overview-panel__body,
  .hot-workspace,
  .hot-featured-section,
  .hot-stream-section {
    padding: var(--spacing-lg);
  }

  .hot-overview-panel__body {
    padding: var(--spacing-lg);
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .hot-workspace .section-count {
    display: none;
  }

  .hot-workspace__body {
    gap: var(--spacing-md);
  }

  .hot-featured-grid {
    grid-template-columns: 1fr;
  }

  .hot-workspace__notes {
    display: none;
  }

  .hot-item {
    padding: var(--spacing-md);
  }

  .hot-kicker-row,
  .hot-topline,
  .featured-card__topline,
  .featured-card__headline,
  .featured-card__meta {
    flex-wrap: wrap;
  }

  .hot-actions {
    justify-content: flex-start;
  }
}

@include sourcelin-down(sm) {
  .hot-masthead__summary,
  .hot-workspace__notes {
    grid-template-columns: 1fr;
  }

  .hot-masthead__stat:last-child,
  .hot-note:last-child {
    grid-column: auto;
  }

  .hot-overview-panel__body,
  .hot-workspace,
  .hot-featured-section,
  .hot-stream-section {
    padding: var(--spacing-lg);
  }

  .hot-overview-panel__body {
    gap: var(--spacing-sm);
  }

  .hot-overview-panel__meta {
    display: none;
  }

  .hot-workspace__body {
    gap: var(--spacing-sm);
  }

  .hot-workspace__tabs {
    padding: 8px;
  }

  .hot-workspace__notes {
    gap: 10px;
  }

  .hot-note {
    min-height: 0;
    padding: var(--spacing-md);
  }



}
</style>
