<template>
  <div class="glass-page hot-page" :aria-busy="loading">
    <PageShell>
      <SCard class="hot-masthead sourcelin-structural-breathe" variant="lite">
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
                <span class="hot-overview-chip">
                  {{ leadItem ? formatTime(leadItem.createTime) : '待更新' }}
                </span>
                <span class="hot-overview-chip">{{ hotList.length }} 条收录</span>
              </div>
            </div>
          </aside>
        </div>
      </SCard>

      <SSurfacePanel
        tag="section"
        class="hot-workspace"
        variant="soft"
        aria-labelledby="hot-workspace-title"
      >
        <div class="section-head hot-section-head">
          <div>
            <h2 id="hot-workspace-title" class="section-title">榜单分类</h2>
            <p class="section-copy">先切换分类，再查看上方推荐和下方列表。</p>
          </div>
          <SSurfaceChip class="section-count" size="sm" variant="counter">
            {{ activeCategoryName }}
          </SSurfaceChip>
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

      <HotFeaturedSection
        :featured-items="featuredItems"
        :get-rank-class="getRankClass"
        :format-rank="formatRank"
        :format-time="formatTime"
        :format-heat="formatHeat"
        @open-item="openItem"
      />

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
          <SSurfaceChip class="section-count" size="sm" variant="counter">
            {{ streamItems.length }} 条内容
          </SSurfaceChip>
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

                <div
                  v-if="asHotItem(item).coverImage && heightType !== 'short'"
                  class="hot-cover"
                >
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
import { useElementSize } from '@vueuse/core'
import { computed, ref } from 'vue'
import HotCategoryTabs from '@/modules/hot/components/HotCategoryTabs.vue'
import HotFeaturedSection from '@/modules/hot/components/HotFeaturedSection.vue'
import { useHotPage, type HotItem } from '@/modules/hot/composables/useHotPage'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import WaterfallList from '@/shared/components/layout/WaterfallList.vue'
import SCard from '@/shared/components/ui/SCard.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'
import '@/modules/hot/styles/hot-page.scss'

interface HotMastheadStatTile {
  label: string
  value: string
  icon: (typeof appIcons)[keyof typeof appIcons]
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
const totalHeat = computed(() =>
  hotList.value.reduce((sum, item) => sum + Number(item.viewCount || 0), 0)
)
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
const waterfallGap = computed(() =>
  hotStreamWidth.value > 0 && hotStreamWidth.value <= 640 ? 14 : 18
)

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
