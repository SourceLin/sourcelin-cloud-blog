<template>
  <div
    class="stats-panel-card"
    :class="[
      `stats-panel-card--${density}`,
      { 'stats-panel-card--primary': primary }
    ]"
    :style="rootStyle"
  >
    <div class="stats-panel-card__icon">
      <SIcon :icon="icon" :size="20" />
    </div>

    <div class="stats-panel-card__content">
      <div class="stats-panel-card__value">
        <CountTo v-if="isNumericValue" v-slot="{ value: animatedValue }" :value="numericValue" :duration="1200">
          <span class="stats-panel-card__value-number">{{ formatNumber(Number(animatedValue)) }}</span>
        </CountTo>
        <span v-else class="stats-panel-card__value-number">{{ value }}</span>
        <span v-if="suffix" class="stats-panel-card__value-suffix">{{ suffix }}</span>
      </div>
      <div class="stats-panel-card__label">{{ label }}</div>
    </div>

    <div
      v-if="typeof trend === 'number'"
      class="stats-panel-card__trend"
      :class="{
        'stats-panel-card__trend--up': trend > 0,
        'stats-panel-card__trend--down': trend < 0
      }"
    >
      <SIcon v-if="trend > 0" :icon="appIcons.trendUp" :size="10" />
      <SIcon v-else-if="trend < 0" :icon="appIcons.trendDown" :size="10" />
      <span v-if="trend !== 0">{{ Math.abs(trend) }}%</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component, CSSProperties } from 'vue'
import { computed } from 'vue'
import CountTo from '@/shared/components/layout/CountTo.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  icon: Component
  value: string | number
  label: string
  hue?: number
  trend?: number
  primary?: boolean
  suffix?: string
  density?: 'regular' | 'compact'
}

const props = withDefaults(defineProps<Props>(), {
  hue: 220,
  trend: undefined,
  primary: false,
  suffix: '',
  density: 'regular'
})

const rootStyle = computed<CSSProperties>(() => ({
  '--stats-panel-hue': String(props.hue)
}) as CSSProperties)

const isNumericValue = computed(() => typeof props.value === 'number' && Number.isFinite(props.value))
const numericValue = computed(() => (isNumericValue.value ? Number(props.value) : 0))

function formatNumber(num: number): string {
  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万`
  }
  if (num >= 1000) {
    return `${(num / 1000).toFixed(1)}千`
  }
  return num.toString()
}
</script>

<style scoped lang="scss">
.stats-panel-card {
  --stats-panel-hue: 220;
  position: relative;
  min-width: 0;
  min-height: 10rem;
  padding: clamp(1rem, 1.8vw, 1.25rem);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.9rem;
  overflow: hidden;
  border-radius: 1.15rem;
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, hsl(var(--stats-panel-hue), 78%, 80%) 14%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent)
    ),
    radial-gradient(
      circle at 14% 0%,
      color-mix(in srgb, hsl(var(--stats-panel-hue), 70%, 72%) 12%, transparent),
      transparent 36%
    );
  transition:
    transform var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.stats-panel-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 42%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
}

.stats-panel-card:hover {
  transform: translateY(-3px);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, hsl(var(--stats-panel-hue), 78%, 80%) 18%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent)
    ),
    radial-gradient(
      circle at 12% 0%,
      color-mix(in srgb, hsl(var(--stats-panel-hue), 70%, 72%) 16%, transparent),
      transparent 38%
    );
  border-color: color-mix(in srgb, var(--primary-color) 24%, var(--glass-border));
  box-shadow: var(--shadow-panel-hover), var(--highlight-panel-soft);
}

.stats-panel-card--primary {
  min-height: 13.2rem;
  padding: clamp(1.15rem, 2vw, 1.5rem);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--primary-color) 12%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent)
    ),
    radial-gradient(circle at 12% 0%, color-mix(in srgb, var(--primary-color) 14%, transparent), transparent 38%);
}

.stats-panel-card--compact {
  min-height: clamp(5.75rem, 14vw, 7.25rem);
  padding: clamp(0.75rem, 1.6vw, 1rem);
  gap: 0.65rem;
}

.stats-panel-card__icon {
  position: relative;
  z-index: 1;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.95rem;
  background: color-mix(in srgb, hsl(var(--stats-panel-hue), 70%, 92%) 90%, transparent);
  color: hsl(var(--stats-panel-hue), 55%, 42%);
}

.stats-panel-card__content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 0.3rem;
  min-width: 0;
}

.stats-panel-card--compact .stats-panel-card__icon {
  width: 2.35rem;
  height: 2.35rem;
  border-radius: 0.85rem;
}

.stats-panel-card__value {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
}

.stats-panel-card__value-number {
  font-size: clamp(1.7rem, 3vw, 2.4rem);
  font-weight: 700;
  line-height: 1;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
}

.stats-panel-card--primary .stats-panel-card__value-number {
  font-size: clamp(2.2rem, 4vw, 3.15rem);
}

.stats-panel-card--compact .stats-panel-card__value-number {
  font-size: clamp(1.35rem, 2.4vw, 1.75rem);
}

.stats-panel-card__value-suffix {
  font-size: 0.78rem;
  color: var(--text-secondary);
}

.stats-panel-card__label {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.stats-panel-card__trend {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.72rem;
  color: var(--text-secondary);
}

.stats-panel-card__trend--up {
  color: var(--success-color);
}

.stats-panel-card__trend--down {
  color: var(--error-color);
}
</style>
