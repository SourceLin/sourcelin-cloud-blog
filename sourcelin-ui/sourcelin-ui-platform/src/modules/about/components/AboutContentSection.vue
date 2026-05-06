<template>
  <div class="about-story-grid">
    <SSurfacePanel tag="section" class="about-story-panel" variant="soft">
      <div class="section-head about-section-head">
        <div>
          <h2 class="section-title">平时写哪些内容</h2>
          <p class="section-copy">{{ contentSectionCopy }}</p>
        </div>
        <SSurfaceChip class="section-count" size="sm" variant="counter">{{ skills.length }} 个关键词</SSurfaceChip>
      </div>

      <div class="about-interest-groups">
        <SSurfacePanel tag="article" v-for="group in interestGroups" :key="group.label" class="about-interest-group" variant="inset" interactive>
          <div class="about-interest-group__head">
            <span class="about-interest-group__label">{{ group.label }}</span>
            <span class="about-interest-group__count">{{ group.items.length }} 个</span>
          </div>
          <p class="about-interest-group__copy">{{ group.copy }}</p>
          <div class="about-interest-group__chips">
            <SSurfaceChip v-for="item in group.items" :key="`${group.label}-${item}`" class="skill-tag">
              {{ item }}
            </SSurfaceChip>
          </div>
        </SSurfacePanel>
      </div>

      <div class="site-info-list">
        <SSurfacePanel v-for="item in siteFacts" :key="item.label" class="info-item" variant="inset" interactive>
          <span class="info-label">{{ item.label }}</span>
          <span class="info-value">{{ item.value }}</span>
        </SSurfacePanel>
      </div>
    </SSurfacePanel>
  </div>
</template>

<script setup lang="ts">
import type { InterestGroup, SiteFact } from '@/modules/about/composables/useAboutPage'

interface Props {
  contentSectionCopy: string
  skills: string[]
  interestGroups: InterestGroup[]
  siteFacts: SiteFact[]
}

defineProps<Props>()
</script>
