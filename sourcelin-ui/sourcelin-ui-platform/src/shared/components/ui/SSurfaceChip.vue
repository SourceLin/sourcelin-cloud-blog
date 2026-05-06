<template>
  <component
    :is="tag"
    class="s-surface-chip sourcelin-chip-surface"
    :class="[
      `s-surface-chip--${size}`,
      `s-surface-chip--${variant}`,
      {
        'sourcelin-chip-surface--accent': tone === 'accent'
      }
    ]"
    v-bind="$attrs"
  >
    <slot />
  </component>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

interface Props {
  tag?: string | Component
  tone?: 'default' | 'accent'
  size?: 'xs' | 'sm' | 'md'
  variant?: 'label' | 'counter' | 'badge' | 'button'
}

withDefaults(defineProps<Props>(), {
  tag: 'span',
  tone: 'default',
  size: 'sm',
  variant: 'label'
})
</script>

<style scoped lang="scss">
.s-surface-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.35rem;
  border-radius: 999px;
  white-space: nowrap;
}

.s-surface-chip--xs {
  min-height: 1.55rem;
  padding: 0.24rem 0.6rem;
  font-size: 0.74rem;
}

.s-surface-chip--sm {
  min-height: 1.9rem;
  padding: 0.34rem 0.74rem;
  font-size: 0.8rem;
}

.s-surface-chip--md {
  min-height: 2.15rem;
  padding: 0.44rem 0.92rem;
  font-size: 0.86rem;
}

.s-surface-chip--label {
  width: fit-content;
  line-height: 1;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.s-surface-chip--counter {
  line-height: 1;
  font-weight: 700;
  letter-spacing: 0.06em;
  font-variant-numeric: tabular-nums;
  box-shadow: var(--shadow-panel-inline);
}

.s-surface-chip--badge {
  line-height: 1.1;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.s-surface-chip--button {
  line-height: 1;
  font-weight: 600;
  cursor: pointer;
  user-select: none;
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    color var(--transition-base),
    background var(--transition-base);
}

.s-surface-chip--button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>
