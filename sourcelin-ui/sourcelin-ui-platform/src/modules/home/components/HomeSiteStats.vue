<template>
  <div class="stats-card-wrapper" :aria-busy="loading">
    <div v-if="loading" class="stats-loading" aria-live="polite">
      <SkeletonStatGrid :count="4" />
    </div>

    <div v-else class="stats-shell">
      <!-- 标题栏 -->
      <div class="stats-header">
        <RouterLink to="/about" class="stats-header__link">
          <span class="stats-header__orb" aria-hidden="true">
            <span class="stats-header__orb-core" />
          </span>
          <h3 class="stats-header__title">站点统计</h3>
        </RouterLink>
        <span class="stats-header__bridge" aria-hidden="true" />
      </div>

      <!-- 统计网格 -->
      <div class="stats-grid">
        <StatsPanelCard
          v-for="(stat, index) in stats"
          :key="index"
          class="stat-item"
          :icon="stat.icon"
          :value="stat.value"
          :label="stat.label"
          :trend="stat.trend"
          :suffix="stat.suffix"
          :hue="stat.hue"
          density="compact"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import StatsPanelCard from '@/shared/components/business/StatsPanelCard.vue'
import SkeletonStatGrid from '@/shared/components/feedback/skeletons/SkeletonStatGrid.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { getStats } from '@/modules/home/api/stats.api'
import { appIcons } from '@/shared/components/ui/icons'

interface StatsResponse {
  articleCount?: number
  articleTrend?: number
  commentCount?: number
  commentTrend?: number
  viewCount?: number
  viewTrend?: number
  userCount?: number
  userTrend?: number
}

interface Stat {
  icon: Component
  label: string
  value: number
  trend: number
  suffix: string
  hue: number
}

interface SummaryItem {
  label: string
  value: string
}

interface Props {
  summaryItems?: SummaryItem[]
}

withDefaults(defineProps<Props>(), {
  summaryItems: () => []
})

const loading = ref(false)
const stats = ref<Stat[]>([
  { icon: appIcons.document, label: '文章', value: 0, trend: 0, suffix: '篇', hue: 220 },
  { icon: appIcons.comment, label: '评论', value: 0, trend: 0, suffix: '条', hue: 180 },
  { icon: appIcons.search, label: '访问', value: 0, trend: 0, suffix: '次', hue: 280 },
  { icon: appIcons.user, label: '用户', value: 0, trend: 0, suffix: '人', hue: 150 }
])

async function loadStats() {
  loading.value = true
  try {
    const response = await getStats()
    if (response) {
      stats.value = [
        { icon: appIcons.document, label: '文章', value: response.articleCount || 0, trend: response.articleTrend || 0, suffix: '篇', hue: 220 },
        { icon: appIcons.comment, label: '评论', value: response.commentCount || 0, trend: response.commentTrend || 0, suffix: '条', hue: 180 },
        { icon: appIcons.search, label: '访问', value: response.viewCount || 0, trend: response.viewTrend || 0, suffix: '次', hue: 280 },
        { icon: appIcons.user, label: '用户', value: response.userCount || 0, trend: response.userTrend || 0, suffix: '人', hue: 150 }
      ]
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadStats()
})
</script>

<style lang="scss" scoped>
@import '@/shared/styles/responsive';

.stats-card-wrapper {
  width: 100%;
}

.stats-shell {
  padding: 0.75rem;
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-content-card);
  background:
    var(--surface-panel-specular-soft),
    var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.stats-loading {
  min-height: 16rem;
}

/* 标题栏 */
.stats-header {
  margin-bottom: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stats-header__link {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  text-decoration: none;
  color: inherit;
  transition: all var(--transition-base);
  padding: 0.25rem 0;
}

.stats-header__link:hover {
  color: var(--primary-color);
}

.stats-header__orb {
  flex-shrink: 0;
  width: 0.72rem;
  height: 0.72rem;
  border-radius: 50%;
  background: radial-gradient(
    circle at 35% 35%,
    color-mix(in srgb, var(--primary-color) 65%, var(--surface-white-12)),
    color-mix(in srgb, var(--primary-color) 85%, transparent)
  );
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 90%, transparent),
    0 2px 8px color-mix(in srgb, var(--primary-color) 20%, transparent);
  transition: all var(--transition-base);
}

.stats-header__orb-core {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--primary-color) 70%, var(--surface-white-18)),
    color-mix(in srgb, var(--primary-color) 90%, transparent)
  );
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 95%, transparent);
}

.stats-header__link:hover .stats-header__orb {
  transform: scale(1.08);
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 95%, transparent),
    0 3px 12px color-mix(in srgb, var(--primary-color) 28%, transparent);
}

.stats-header__title {
  margin: 0;
  font-size: 0.88rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--text-secondary);
  transition: color var(--transition-base);
}

.stats-header__bridge {
  display: block;
  width: 100%;
  height: 1px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--primary-color) 75%, transparent) 0%,
    color-mix(in srgb, var(--primary-color) 45%, transparent) 40%,
    transparent 100%
  );
  opacity: 0.72;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.55rem;
}

.stat-item {
  min-height: 7.5rem;
}

/* 响应式适配 */
@include sourcelin-down(sm) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stat-item {
    min-height: 6.5rem;
  }
}
</style>
