<template>
  <div class="scene-copy">
    <span class="scene-badge">{{ badge }}</span>
    <h1 class="scene-title">{{ title }}</h1>
    <p class="scene-subtitle">{{ subtitle }}</p>
  </div>

  <div v-if="highlights.length" class="scene-grid">
    <div
      v-for="item in highlights"
      :key="item.title"
      class="scene-card"
    >
      <div class="scene-card__icon">
        <SIcon :icon="item.icon" :size="16" />
      </div>
      <div class="scene-card__body">
        <span class="scene-card__title">{{ item.title }}</span>
        <p class="scene-card__desc">{{ item.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

interface HighlightItem {
  icon: Component
  title: string
  description: string
}

interface Props {
  badge: string
  title: string
  subtitle: string
  highlights: HighlightItem[]
  footerLabel?: string
  footerQuote?: string
}

defineProps<Props>()
</script>

<style scoped lang="scss">
.scene-copy,
.scene-grid {
  position: relative;
  z-index: 1;
}

.scene-copy {
  display: grid;
  gap: 14px;
}

.scene-badge {
  justify-self: flex-start;
  min-height: 30px;
  padding: 0 12px;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  box-shadow:
    var(--shadow-panel-soft),
    var(--highlight-panel-chip);
}

.scene-title {
  margin: 0;
  max-width: 8ch;
  font-size: clamp(2rem, 4vw, 3rem);
  line-height: 1.02;
  color: var(--text-color-light);
}

.scene-subtitle {
  margin: 0;
  max-width: 32rem;
  font-size: 0.92rem;
  line-height: 1.75;
  color: color-mix(in srgb, var(--text-color-light) 74%, transparent);
}

.scene-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.scene-card {
  position: relative;
  display: grid;
  gap: 12px;
  align-content: start;
  min-height: 0;
  padding: 16px;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-15) 14%, transparent),
      color-mix(in srgb, var(--surface-panel-inset) 92%, transparent)
    );
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.scene-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    radial-gradient(circle at top left, color-mix(in srgb, var(--primary-color) 12%, transparent), transparent 46%),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 10%, transparent), transparent 38%);
  opacity: 0.78;
  pointer-events: none;
}

.scene-card:hover {
  transform: translateY(-2px) scale(1.01);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-default);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-15) 20%, transparent),
      color-mix(in srgb, var(--surface-panel-chip-accent) 84%, transparent)
    );
}

.scene-card__icon {
  position: relative;
  z-index: 1;
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  box-shadow:
    var(--shadow-panel-soft),
    var(--highlight-panel-chip);
}

.scene-card__body {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 6px;
}

.scene-card__title {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-color-light);
}

.scene-card__desc {
  margin: 0;
  font-size: 0.82rem;
  line-height: 1.65;
  color: color-mix(in srgb, var(--text-color-light) 70%, transparent);
}

@media (max-width: 1200px) {
  .scene-title {
    max-width: 9ch;
    font-size: clamp(1.9rem, 4vw, 2.8rem);
  }
}

@media (max-width: 980px) {
  .scene-grid {
    grid-template-columns: 1fr;
  }
}
</style>
