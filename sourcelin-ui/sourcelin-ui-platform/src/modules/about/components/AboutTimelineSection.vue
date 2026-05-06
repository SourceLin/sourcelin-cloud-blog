<template>
  <SSurfacePanel tag="section" class="about-timeline" variant="soft" aria-labelledby="about-timeline-title">
    <div class="section-head about-section-head">
      <div>
        <h2 id="about-timeline-title" class="section-title">内容怎么更新</h2>
        <p class="section-copy">用 3 步简单说一下这个站平时怎么更新。</p>
      </div>
      <SSurfaceChip class="section-count" size="sm" variant="counter">{{ timelineItems.length }} 节点</SSurfaceChip>
    </div>

    <div class="about-timeline__list">
      <article v-for="item in timelineItems" :key="item.title" class="about-timeline__item">
        <span class="about-timeline__dot" aria-hidden="true" />
        <SSurfacePanel class="about-timeline__content" variant="inset">
          <p class="about-timeline__eyebrow">{{ item.kicker }}</p>
          <h3 class="about-timeline__title">{{ item.title }}</h3>
          <p class="about-timeline__copy">{{ item.copy }}</p>
        </SSurfacePanel>
      </article>
    </div>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import type { TimelineItem } from '@/modules/about/composables/useAboutPage'

defineProps<{
  timelineItems: TimelineItem[]
}>()
</script>
