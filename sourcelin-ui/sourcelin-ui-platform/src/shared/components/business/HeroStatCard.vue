<template>
  <component
    :is="rootTag"
    class="hero-stat-card"
    :class="[
      `hero-stat-card--${mode}`,
      `hero-stat-card--${tone}`,
      `hero-stat-card--${size}`,
      { 'hero-stat-card--interactive': isInteractive }
    ]"
    :style="rootStyle"
    v-bind="rootAttrs"
  >
    <div class="hero-stat-card__icon">
      <SIcon :icon="icon" :size="size === 'lg' ? 22 : 20" />
    </div>

    <div class="hero-stat-card__content">
      <template v-if="mode === 'metric'">
        <SDataValue class="hero-stat-card__value" :value="value ?? ''" :size="size" />
        <div class="hero-stat-card__label">{{ label }}</div>
      </template>

      <template v-else>
        <div class="hero-stat-card__title">{{ title }}</div>
        <div v-if="meta" class="hero-stat-card__meta">{{ meta }}</div>
      </template>
    </div>
  </component>
</template>

<script setup lang="ts">
import type { Component, CSSProperties } from 'vue'
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import SDataValue from '@/shared/components/ui/SDataValue.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

interface Props {
  icon: Component
  mode?: 'metric' | 'link'
  tone?: 'neutral' | 'tinted'
  value?: string | number
  label?: string
  title?: string
  meta?: string
  href?: string
  to?: string
  ariaLabel?: string
  size?: 'md' | 'lg'
  hue?: number
  action?: 'static' | 'button'
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'metric',
  tone: 'neutral',
  value: '',
  label: '',
  title: '',
  meta: '',
  href: '',
  to: '',
  ariaLabel: '',
  size: 'md',
  hue: 245,
  action: 'static'
})

const isInteractive = computed(() => Boolean(props.to || props.href || props.action === 'button'))

const rootTag = computed(() => {
  if (props.to) {
    return RouterLink
  }
  if (props.href) {
    return 'a'
  }
  if (props.action === 'button') {
    return 'button'
  }
  return 'div'
})

const rootStyle = computed<CSSProperties>(() => {
  if (props.tone !== 'tinted') {
    return {}
  }

  return {
    '--hero-stat-hue': String(props.hue)
  } as CSSProperties
})

const rootAttrs = computed<Record<string, string>>(() => {
  const attrs: Record<string, string> = {
    'aria-label': props.ariaLabel || props.title || `${props.label} ${props.value}`.trim()
  }

  if (props.to) {
    attrs.to = props.to
    return attrs
  }

  if (props.href) {
    attrs.href = props.href
    attrs.target = '_blank'
    attrs.rel = 'noreferrer'
    return attrs
  }

  if (props.action === 'button') {
    attrs.type = 'button'
    return attrs
  }

  attrs.role = 'status'
  return attrs
})
</script>

<style scoped lang="scss">
.hero-stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  width: 100%;
  margin: 0;
  min-width: 0;
  min-height: 5rem;
  padding: var(--spacing-md) var(--spacing-lg);
  overflow: hidden;
  appearance: none;
  border-radius: var(--border-radius-xl);
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  box-shadow: var(--shadow-panel-soft), var(--highlight-panel-soft);
  color: inherit;
  font: inherit;
  text-align: left;
  text-decoration: none;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.hero-stat-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 48%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
}

.hero-stat-card:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--primary-color) 22%, var(--glass-border));
  box-shadow: var(--shadow-panel-hover), var(--highlight-panel-soft);
}

.hero-stat-card--interactive {
  cursor: pointer;
}

.hero-stat-card--interactive:active {
  transform: translateY(-1px);
}

.hero-stat-card--interactive:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

.hero-stat-card--neutral {
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
}

.hero-stat-card--tinted {
  --hero-stat-hue: 245;
  background:
    linear-gradient(
      105deg,
      color-mix(
        in srgb,
        hsl(var(--hero-stat-hue), 78%, 80%) 14%,
        var(--glass-surface-strong)
      ),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    radial-gradient(
      circle at 0% 50%,
      color-mix(in srgb, hsl(var(--hero-stat-hue), 70%, 72%) 12%, transparent),
      transparent 52%
    );
}

.hero-stat-card--tinted:hover {
  border-color: color-mix(
    in srgb,
    hsl(var(--hero-stat-hue), 72%, 58%) 18%,
    var(--glass-border)
  );
}

.hero-stat-card--lg {
  min-height: 5.25rem;
  padding-inline: var(--spacing-xl);
}

.hero-stat-card__icon {
  position: relative;
  z-index: 1;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: var(--border-radius-lg);
  color: var(--primary-color);
}

.hero-stat-card--neutral .hero-stat-card__icon {
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 76%, transparent);
}

.hero-stat-card--tinted .hero-stat-card__icon {
  background: color-mix(
    in srgb,
    hsl(var(--hero-stat-hue), 70%, 92%) 90%,
    transparent
  );
  color: hsl(var(--hero-stat-hue), 55%, 42%);
}

.hero-stat-card--lg .hero-stat-card__icon {
  width: 3.25rem;
  height: 3.25rem;
}

.hero-stat-card__content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
  flex: 1;
}

.hero-stat-card__value,
.hero-stat-card__title {
  color: var(--text-primary);
}

.hero-stat-card__title {
  font-size: clamp(1.08rem, 2vw, 1.26rem);
  font-weight: 700;
  line-height: 1.2;
}

.hero-stat-card__label,
.hero-stat-card__meta {
  font-size: var(--font-size-sm, 12px);
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.hero-stat-card__meta {
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: none;
}
</style>
