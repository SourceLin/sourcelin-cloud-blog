<template>
  <SSurfacePanel tag="section" class="about-stats-section" variant="soft" aria-labelledby="about-stats-title">
    <div class="section-head about-section-head">
      <div>
        <h2 id="about-stats-title" class="section-title">站点数据</h2>
        <p class="section-copy">以下是本站的运营数据概览</p>
      </div>
    </div>

    <div class="about-stats-grid">
      <StatsPanelCard
        v-for="(stat, index) in siteStats"
        :key="index"
        class="about-stat-card"
        :icon="stat.icon"
        :value="stat.value"
        :label="stat.label"
        :trend="stat.trend"
        :suffix="stat.suffix"
        :hue="stat.hue"
      />
    </div>

    <div class="about-summary-strip">
      <RouterLink v-for="item in summaryItems" :key="item.label" :to="item.to || '/'" class="about-summary-metric-link">
        <SMetricCard class="about-summary-metric" :icon="item.icon" :label="item.label" :value="item.value" size="md" />
      </RouterLink>
    </div>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { SiteStat, SummaryItemWithIcon } from '@/modules/about/composables/useAboutPage'
import StatsPanelCard from '@/shared/components/business/StatsPanelCard.vue'
import SMetricCard from '@/shared/components/ui/SMetricCard.vue'

interface Props {
  siteStats: SiteStat[]
  summaryItems: SummaryItemWithIcon[]
}

defineProps<Props>()
</script>
