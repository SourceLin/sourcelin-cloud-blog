<script setup lang="ts">
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'

defineProps<{
  count?: number
}>()
</script>

<template>
  <div class="skeleton-stat-grid" aria-hidden="true">
    <div v-for="index in (count || 4)" :key="`stat-skel-${index}`" class="skel-stat-card" :class="{ 'is-primary': index === 1 }">
      <div class="skel-stat-icon">
        <SSkeleton :sharp="false" class="skel-icon-box" :width="48" :height="48" />
      </div>
      <div class="skel-stat-body">
        <SSkeleton :sharp="false" class="skel-stat-value" :width="100" :height="36" />
        <SSkeleton :sharp="false" class="skel-stat-label" :width="60" :height="14" />
      </div>
      <div v-if="index <= 2" class="skel-stat-trend">
        <SSkeleton :sharp="false" class="skel-trend-box" :width="48" :height="16" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.skeleton-stat-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.7rem;
}

.skel-stat-card {
  position: relative;
  grid-column: span 1;
  min-height: 10rem;
  padding: clamp(1rem, 1.8vw, 1.25rem);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.9rem;
  overflow: hidden;
  border-radius: 1.15rem;
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background: var(--glass-surface-strong);
}

.skel-stat-card.is-primary {
  grid-column: 1 / -1;
  min-height: 13.2rem;
}

.skel-stat-icon {
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.95rem;
}

.skel-icon-box {
  border-radius: 0.95rem !important;
}

.skel-stat-body {
  display: grid;
  gap: 0.3rem;
  min-width: 0;
}

.skel-stat-value {
  border-radius: 6px !important;
}

.skel-stat-label {
  border-radius: 4px !important;
}

.skel-stat-trend {
  position: absolute;
  right: 1rem;
  top: 1rem;
}

.skel-trend-box {
  border-radius: 4px !important;
}

@include sourcelin-down(sm) {
  .skeleton-stat-grid {
    grid-template-columns: 1fr;
  }

  .skel-stat-card,
  .skel-stat-card.is-primary {
    grid-column: auto;
  }
}
</style>
