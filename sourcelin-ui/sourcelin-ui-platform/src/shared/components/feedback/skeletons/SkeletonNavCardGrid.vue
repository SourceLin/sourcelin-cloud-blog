<script setup lang="ts">
import { computed } from 'vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'

const props = withDefaults(
  defineProps<{
    count?: number
    variant?: 'hero' | 'chip' | 'recent' | 'card'
  }>(),
  {
    count: 6,
    variant: 'card'
  }
)

const items = computed(() => Array.from({ length: props.count }, (_, index) => index))
</script>

<template>
  <div class="skeleton-nav" :class="`skeleton-nav--${variant}`" aria-hidden="true">
    <template v-if="variant === 'hero'">
      <div v-for="index in items" :key="`nav-hero-${index}`" class="nav-hero-skel">
        <SSkeleton :sharp="false" class="nav-hero-skel__icon" :width="48" :height="48" />
        <div class="nav-hero-skel__body">
          <SSkeleton :sharp="false" class="nav-hero-skel__value" :width="72" :height="20" />
          <SSkeleton :sharp="false" class="nav-hero-skel__label" :width="84" :height="12" />
        </div>
      </div>
    </template>

    <template v-else-if="variant === 'chip'">
      <SSkeleton
        v-for="index in items"
        :key="`nav-chip-${index}`"
        :sharp="false"
        class="nav-chip-skel"
        :width="index % 2 === 0 ? 116 : 92"
        :height="34"
      />
    </template>

    <template v-else-if="variant === 'recent'">
      <div class="nav-recent-skel">
        <div class="nav-recent-skel__head">
          <SSkeleton :sharp="false" class="nav-recent-skel__title" :width="94" :height="18" />
          <SSkeleton :sharp="false" class="nav-recent-skel__count" :width="52" :height="16" />
        </div>
        <div class="nav-recent-skel__list">
          <SSkeleton
            v-for="index in items"
            :key="`nav-recent-${index}`"
            :sharp="false"
            class="nav-recent-skel__chip"
            :width="index % 2 === 0 ? 110 : 92"
            :height="30"
          />
        </div>
      </div>
    </template>

    <template v-else>
      <article v-for="index in items" :key="`nav-card-${index}`" class="nav-card-skel">
        <SSkeleton :sharp="false" class="nav-card-skel__icon" :width="42" :height="42" />
        <div class="nav-card-skel__body">
          <SSkeleton :sharp="false" class="nav-card-skel__title" :width="128" :height="18" />
          <SSkeleton :sharp="false" class="nav-card-skel__line" :height="12" />
          <SSkeleton :sharp="false" class="nav-card-skel__line nav-card-skel__line--short" :height="12" />
        </div>
      </article>
    </template>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.skeleton-nav {
  display: grid;
  gap: var(--spacing-md);
}

.skeleton-nav--hero {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.nav-hero-skel,
.nav-card-skel,
.nav-recent-skel {
  pointer-events: none;
}

.nav-hero-skel {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  min-height: 5rem;
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-soft);
  box-shadow: var(--shadow-panel-soft), var(--highlight-panel-soft);
}

.nav-hero-skel__icon,
.nav-card-skel__icon {
  border-radius: var(--border-radius-lg) !important;
}

.nav-hero-skel__body,
.nav-card-skel__body {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
  flex: 1;
}

.nav-chip-skel,
.nav-recent-skel__chip {
  border-radius: 999px !important;
}

.skeleton-nav--chip {
  display: flex;
  flex-wrap: wrap;
}

.nav-recent-skel {
  display: grid;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: calc(var(--border-radius-xl) - 2px);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
}

.nav-recent-skel__head,
.nav-recent-skel__list {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  justify-content: space-between;
}

.nav-recent-skel__list {
  justify-content: flex-start;
}

.skeleton-nav--card {
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
}

.nav-card-skel {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: calc(var(--border-radius-xl) - 2px);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
}

.nav-card-skel__line {
  width: 100% !important;
}

.nav-card-skel__line--short {
  width: 72% !important;
}

@include sourcelin-down(lg) {
  .skeleton-nav--hero {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .skeleton-nav--hero .nav-hero-skel:last-child {
    grid-column: 1 / -1;
  }
}

@include sourcelin-down(md) {
  .skeleton-nav--hero,
  .skeleton-nav--card {
    grid-template-columns: 1fr;
  }

  .skeleton-nav--hero .nav-hero-skel:last-child {
    grid-column: auto;
  }
}
</style>
