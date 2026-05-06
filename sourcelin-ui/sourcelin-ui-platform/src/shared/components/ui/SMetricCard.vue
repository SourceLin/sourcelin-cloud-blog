<template>
  <div class="s-metric-card" role="status" :aria-label="ariaLabel || `${label} ${value}`">
    <span class="s-metric-card__icon sourcelin-chip-surface">
      <SIcon :icon="icon" :size="20" />
    </span>
    <div class="s-metric-card__content">
      <SDataValue :value="value" :size="size" />
      <span class="s-metric-card__label">{{ label }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import SDataValue from '@/shared/components/ui/SDataValue.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

interface Props {
  icon: Component
  label: string
  value: string | number
  ariaLabel?: string
  size?: 'md' | 'lg'
}

withDefaults(defineProps<Props>(), {
  size: 'md'
})
</script>

<style scoped lang="scss">
.s-metric-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  min-width: 0;
  min-height: 5rem;
  padding: var(--spacing-md) var(--spacing-lg);
  overflow: hidden;
  border-radius: var(--border-radius-xl);
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  box-shadow: var(--shadow-panel-soft), var(--highlight-panel-soft);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.s-metric-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 48%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
}

.s-metric-card:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--primary-color) 22%, var(--glass-border));
  box-shadow: var(--shadow-panel-hover), var(--highlight-panel-soft);
}

.s-metric-card__icon {
  position: relative;
  z-index: 1;
  width: 3rem;
  height: 3rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: var(--border-radius-lg);
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 76%, transparent);
  color: var(--primary-color);
}

.s-metric-card__content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.s-metric-card__label {
  font-size: var(--font-size-sm, 12px);
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--text-secondary);
}
</style>
