<script setup lang="ts">
interface Props {
  badge?: string
  kicker: string
  title: string
  description?: string
  size?: 'section' | 'card'
  titleTag?: 'h2' | 'h3'
}

withDefaults(defineProps<Props>(), {
  badge: '',
  description: '',
  size: 'card',
  titleTag: 'h3'
})
</script>

<template>
  <div class="home-section-heading" :class="`is-${size}`">
    <span v-if="badge" class="heading-badge">{{ badge }}</span>
    <div class="heading-copy">
      <p class="heading-kicker">{{ kicker }}</p>
      <component :is="titleTag" class="heading-title">{{ title }}</component>
      <p v-if="description" class="heading-description">{{ description }}</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.home-section-heading {
  display: grid;
  grid-template-columns: 1fr;
  align-items: start;
  gap: var(--spacing-sm);
}

.heading-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 0.35rem 0.8rem;
  border-radius: var(--border-radius-full);
  border: 1px solid color-mix(in srgb, var(--primary-color) 12%, transparent);
  background: color-mix(in srgb, var(--glass-surface-strong) 72%, transparent);
  font-size: 0.72rem;
  font-weight: 600;
  line-height: 1;
  letter-spacing: 0.16em;
  color: var(--text-secondary);
  text-transform: uppercase;
  backdrop-filter: blur(var(--glass-blur));
}

.heading-copy {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.heading-kicker {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: var(--text-secondary);
}

.heading-title {
  margin: 0;
  font-family: 'Georgia', 'Times New Roman', serif;
  font-weight: 700;
  color: var(--text-primary);
}

.heading-description {
  margin: 0;
  color: var(--text-secondary);
}

.is-section .heading-title {
  font-size: clamp(1.7rem, 3vw, 2.55rem);
  line-height: 1.18;
}

.is-section .heading-description {
  max-width: 44rem;
  font-size: 1rem;
  line-height: 1.8;
}

.is-card .heading-title {
  font-size: clamp(1.2rem, 2vw, 1.9rem);
  line-height: 1.18;
}

.is-card .heading-description {
  font-size: 0.94rem;
  line-height: 1.75;
}

@include sourcelin-down(md) {
  .is-section .heading-title {
    font-size: 2rem;
  }

  .is-section .heading-description {
    font-size: 0.95rem;
    line-height: 1.7;
  }
}
</style>
