<!--
  @deprecated 此组件已标记为废弃，请使用 SCard + 光学变体 surface prop (ice/drop/frost/gem)
  迁移指南：
  - variant → surface='ice' | 'drop' | 'frost' | 'gem'
  - glowEffect → 使用 .sourcelin-glass-breathe 动画类
  - surface → 已内置于光学变体 Token
  
  此组件将保留至下一版本以确保平滑迁移。
-->
<template>
  <SCard
    :class="cardClasses"
    :hoverable="false"
    :bordered="false"
    header-padding="none"
    content-padding="none"
    footer-padding="none"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <template v-if="title || $slots.header || $slots.extra" #header>
      <div class="card-header">
        <div class="card-header-left">
          <slot name="icon">
            <span v-if="icon" class="card-icon">
              <SIcon :icon="icon" :size="18" />
            </span>
          </slot>

          <slot name="header">
            <h3 v-if="title" class="card-title">{{ title }}</h3>
          </slot>
        </div>

        <div v-if="$slots.extra" class="card-header-right">
          <slot name="extra" />
        </div>
      </div>
    </template>

    <div :class="['card-body', { 'no-padding': noPadding }]">
      <slot />
    </div>

    <template v-if="$slots.footer" #footer>
      <div class="card-footer">
        <slot name="footer" />
      </div>
    </template>

    <div v-if="glowEffect" class="card-glow" />
    <div class="card-accent-glow" />
  </SCard>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed, ref } from 'vue'

interface Props {
  title?: string
  icon?: Component | null
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'error' | 'info'
  surface?: 'default' | 'panel' | 'soft' | 'strong' | 'inset' | 'chip'
  hoverable?: boolean
  shadow?: boolean
  shadowLevel?: 'sm' | 'md' | 'lg' | 'xl'
  bordered?: boolean
  noPadding?: boolean
  glowEffect?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  icon: null,
  variant: 'default',
  surface: 'default',
  hoverable: true,
  shadow: true,
  shadowLevel: 'md',
  bordered: true,
  noPadding: false,
  glowEffect: false
})

const emit = defineEmits<{
  mouseenter: []
  mouseleave: []
}>()

const isHovered = ref(false)

const cardClasses = computed(() => [
  'modern-card',
  `card-${props.variant}`,
  `surface-${props.surface}`,
  {
    'card-hoverable': props.hoverable,
    'card-shadow': props.shadow,
    [`shadow-${props.shadowLevel}`]: props.shadow,
    'card-bordered': props.bordered,
    'is-hovered': isHovered.value
  }
])

const handleMouseEnter = () => {
  isHovered.value = true
  emit('mouseenter')
}

const handleMouseLeave = () => {
  isHovered.value = false
  emit('mouseleave')
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.modern-card {
  --card-accent-color: var(--primary-color);
  --card-border-color: var(--border-color);
  --card-surface-radius: var(--border-radius-md);
  --card-surface-border: color-mix(in srgb, var(--card-border-color) 68%, transparent);
  --card-surface-border-strong: color-mix(in srgb, var(--card-border-color) 76%, transparent);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--card-accent-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  --card-surface-hover-bg:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--card-accent-color) 8%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 92%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 84%, transparent), transparent);
  --card-surface-header-bg: linear-gradient(
    180deg,
    color-mix(in srgb, var(--card-accent-color) 8%, var(--glass-surface-strong)),
    color-mix(in srgb, var(--glass-surface-lite) 92%, transparent)
  );
  --card-surface-footer-bg: color-mix(in srgb, var(--glass-surface-lite) 92%, transparent);
  --card-surface-icon-bg: color-mix(in srgb, var(--card-accent-color) 14%, var(--glass-surface-strong));
  --card-surface-shadow-sm: var(--shadow-soft);
  --card-surface-shadow-md: var(--shadow-medium);
  --card-surface-shadow-lg: var(--shadow-large);
  --card-surface-hover-shadow: var(--shadow-large);
  --card-surface-backdrop: blur(var(--glass-blur));
  --card-surface-hover-scale: 1.012;
  --card-surface-hover-inset:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 88%, transparent),
    inset 0 -1px 0 color-mix(in srgb, var(--background-color-deep) 8%, transparent);
  position: relative;
  overflow: hidden;
  border-radius: var(--card-surface-radius);
  border: 1px solid var(--card-surface-border);
  background: var(--card-surface-bg);
  backdrop-filter: var(--card-surface-backdrop);
  transition: transform var(--transition-base), box-shadow var(--transition-base), background var(--transition-base), border-color var(--transition-base);
  will-change: transform, box-shadow;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 88%, transparent), transparent 42%);
    pointer-events: none;
    opacity: 0.82;
  }

  &.card-shadow {
    &.shadow-sm {
      box-shadow: var(--card-surface-shadow-sm);
    }

    &.shadow-md {
      box-shadow: var(--card-surface-shadow-md);
    }

    &.shadow-lg,
    &.shadow-xl {
      box-shadow: var(--card-surface-shadow-lg);
    }
  }

  &.card-bordered {
    border-color: var(--card-surface-border-strong);
  }

  &.card-hoverable:hover {
    transform: scale(var(--card-surface-hover-scale));
    border-color: var(--card-surface-hover-border);
    background: var(--card-surface-hover-bg);
    box-shadow:
      var(--card-surface-hover-inset),
      var(--card-surface-hover-shadow),
      var(--highlight-panel-soft);
  }
}

.modern-card.surface-panel {
  --card-surface-border: var(--border-panel-default);
  --card-surface-border-strong: var(--border-panel-strong);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg: var(--surface-panel-default);
  --card-surface-hover-bg: var(--surface-panel-strong);
  --card-surface-header-bg: var(--surface-panel-strong);
  --card-surface-footer-bg: var(--surface-panel-soft);
  --card-surface-icon-bg: var(--surface-panel-chip-accent);
  --card-surface-shadow-sm: var(--shadow-panel-soft);
  --card-surface-shadow-md: var(--shadow-panel-default);
  --card-surface-shadow-lg: var(--shadow-panel-hover);
  --card-surface-hover-shadow: var(--shadow-panel-hover);
}

.modern-card.surface-soft {
  --card-surface-border: var(--border-panel-subtle);
  --card-surface-border-strong: var(--border-panel-default);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg: var(--surface-panel-soft);
  --card-surface-hover-bg: var(--surface-panel-default);
  --card-surface-header-bg: var(--surface-panel-default);
  --card-surface-footer-bg: var(--surface-panel-inset);
  --card-surface-icon-bg: var(--surface-panel-chip);
  --card-surface-shadow-sm: var(--shadow-panel-subtle);
  --card-surface-shadow-md: var(--shadow-panel-soft);
  --card-surface-shadow-lg: var(--shadow-panel-default);
  --card-surface-hover-shadow: var(--shadow-panel-hover);
}

.modern-card.surface-strong {
  --card-surface-border: var(--border-panel-strong);
  --card-surface-border-strong: var(--border-panel-strong);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg: var(--surface-panel-strong);
  --card-surface-hover-bg: var(--surface-panel-strong);
  --card-surface-header-bg: var(--surface-panel-strong);
  --card-surface-footer-bg: var(--surface-panel-default);
  --card-surface-icon-bg: var(--surface-panel-chip-accent);
  --card-surface-shadow-sm: var(--shadow-panel-soft);
  --card-surface-shadow-md: var(--shadow-panel-default);
  --card-surface-shadow-lg: var(--shadow-panel-hover);
  --card-surface-hover-shadow: var(--shadow-panel-hover);
}

.modern-card.surface-inset {
  --card-surface-radius: calc(var(--border-radius-md) + 2px);
  --card-surface-border: var(--border-panel-subtle);
  --card-surface-border-strong: var(--border-panel-default);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg: var(--surface-panel-inset);
  --card-surface-hover-bg: var(--surface-panel-soft);
  --card-surface-header-bg: var(--surface-panel-soft);
  --card-surface-footer-bg: var(--surface-panel-inset);
  --card-surface-icon-bg: var(--surface-panel-chip);
  --card-surface-shadow-sm: var(--shadow-panel-inline);
  --card-surface-shadow-md: var(--shadow-panel-soft);
  --card-surface-shadow-lg: var(--shadow-panel-default);
  --card-surface-hover-shadow: var(--shadow-panel-soft);
  --card-surface-backdrop: blur(calc(var(--glass-blur) - 3px));
}

.modern-card.surface-chip {
  --card-surface-radius: 999px;
  --card-surface-border: var(--border-panel-chip);
  --card-surface-border-strong: var(--border-panel-badge-accent);
  --card-surface-hover-border: var(--border-interactive-hover);
  --card-surface-bg: var(--surface-panel-chip);
  --card-surface-hover-bg: var(--surface-panel-chip-accent);
  --card-surface-header-bg: var(--surface-panel-chip);
  --card-surface-footer-bg: var(--surface-panel-chip);
  --card-surface-icon-bg: var(--surface-panel-chip-accent);
  --card-surface-shadow-sm: var(--highlight-panel-chip);
  --card-surface-shadow-md: var(--shadow-panel-soft);
  --card-surface-shadow-lg: var(--shadow-panel-default);
  --card-surface-hover-shadow: var(--shadow-panel-soft);
  --card-surface-backdrop: blur(calc(var(--glass-blur) - 4px));
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 60px;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--card-surface-border);
  background: var(--card-surface-header-bg);
}

.card-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
  padding-right: var(--spacing-lg);
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-full);
  color: var(--card-accent-color);
  background: var(--card-surface-icon-bg);
  box-shadow: var(--card-surface-shadow-sm);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
}

.card-title {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: 600;
  line-height: 1.4;
  color: var(--title-color);
}

.card-header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-left: var(--spacing-lg);
}

.card-body {
  padding: var(--spacing-xl);
}

.card-body.no-padding {
  padding: 0;
}

.card-footer {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--card-surface-border);
  background: var(--card-surface-footer-bg);
}

.card-glow {
  position: absolute;
  inset: 0 auto 0 -100%;
  width: 100%;
  pointer-events: none;
  background: linear-gradient(90deg, transparent, var(--primary-opacity), transparent);
  transition: left var(--transition-slow);
}

.modern-card:hover .card-glow {
  left: 100%;
}

.card-accent-glow {
  position: absolute;
  inset: -20% auto auto -8%;
  width: 46%;
  height: 58%;
  border-radius: 999px;
  background: radial-gradient(circle, color-mix(in srgb, var(--card-accent-color) 18%, transparent) 0%, transparent 72%);
  filter: blur(24px);
  opacity: 0.46;
  pointer-events: none;
  transition: opacity var(--transition-base), transform var(--transition-base);
}

.modern-card.card-hoverable:hover .card-accent-glow {
  opacity: 0.72;
  transform: translate3d(6px, -2px, 0) scale(1.04);
}

.modern-card.card-primary {
  --card-accent-color: var(--primary-color);
  --card-border-color: var(--primary-opacity);
}

.modern-card.card-success {
  --card-accent-color: var(--success-color);
  --card-border-color: color-mix(in srgb, var(--success-color) 30%, transparent);
}

.modern-card.card-warning {
  --card-accent-color: var(--warning-color);
  --card-border-color: color-mix(in srgb, var(--warning-color) 30%, transparent);
}

.modern-card.card-error {
  --card-accent-color: var(--error-color);
  --card-border-color: color-mix(in srgb, var(--error-color) 30%, transparent);
}

.modern-card.card-info {
  --card-accent-color: var(--info-color);
  --card-border-color: color-mix(in srgb, var(--info-color) 30%, transparent);
}

.modern-card.card-primary .card-title,
.modern-card.card-success .card-title,
.modern-card.card-warning .card-title,
.modern-card.card-error .card-title,
.modern-card.card-info .card-title {
  color: var(--card-accent-color);
}

@include sourcelin-down(md) {
  .card-header {
    padding: var(--spacing-md) var(--spacing-lg);
  }

  .card-body,
  .card-footer {
    padding: var(--spacing-lg);
  }
}
</style>
