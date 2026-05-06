<template>
  <component
    :is="href ? 'a' : 'div'"
    class="s-link-card"
    :href="href"
    :target="href ? '_blank' : undefined"
    :rel="href ? 'noreferrer' : undefined"
    :aria-label="ariaLabel || title"
  >
    <span class="s-link-card__icon sourcelin-chip-surface">
      <SIcon :icon="icon" :size="20" />
    </span>
    <span class="s-link-card__title">{{ title }}</span>
  </component>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

interface Props {
  icon: Component
  title: string
  href?: string
  ariaLabel?: string
}

defineProps<Props>()
</script>

<style scoped lang="scss">
.s-link-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  min-height: 5rem;
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  text-decoration: none;
}

.s-link-card__icon {
  position: relative;
  z-index: 1;
  width: 3rem;
  height: 3rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: var(--border-radius-lg);
  color: var(--primary-color);
}

.s-link-card__title {
  position: relative;
  z-index: 1;
  font-size: clamp(1rem, 1.8vw, 1.18rem);
  font-weight: 700;
  color: var(--title-color);
  line-height: 1.3;
  letter-spacing: 0.01em;
}
</style>
