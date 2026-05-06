<script setup lang="ts">
import { computed } from 'vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'

const props = withDefaults(
  defineProps<{
    count?: number
    variant?: 'featured' | 'card' | 'list'
  }>(),
  {
    count: 3,
    variant: 'card'
  }
)

const cardCount = computed(() => props.count)
</script>

<template>
  <div class="skeleton-article-stack" :class="variant" aria-hidden="true">
    <article
      v-for="index in cardCount"
      :key="`sac-skel-${variant}-${index}`"
      class="skel-card"
      :class="{ 'is-featured': variant === 'featured' }"
    >
      <div v-if="variant === 'featured'" class="skel-featured-layout">
        <div class="skel-cover">
          <SSkeleton :sharp="false" class="skel-img" :height="280" />
        </div>
        <div class="skel-body">
          <div class="skel-topline">
            <SSkeleton :sharp="false" class="skel-chip" :width="84" :height="28" />
            <SSkeleton :sharp="false" class="skel-chip skel-chip--sm" :width="64" :height="28" />
            <SSkeleton :sharp="false" class="skel-date" :width="120" :height="14" />
          </div>
          <SSkeleton :sharp="false" class="skel-title" />
          <SSkeleton :sharp="false" class="skel-line" />
          <SSkeleton :sharp="false" class="skel-line skel-line--short" />
          <div class="skel-tags">
            <SSkeleton v-for="t in 3" :key="`tag-${index}-${t}`" :sharp="false" class="skel-tag" />
          </div>
          <div class="skel-meta">
            <SSkeleton v-for="m in 3" :key="`meta-${index}-${m}`" :sharp="false" class="skel-meta-bit" />
          </div>
        </div>
      </div>

      <template v-else-if="variant === 'list'">
        <div class="skel-list-layout">
          <SSkeleton :sharp="false" class="skel-list-cover" :height="100" :width="160" />
          <div class="skel-list-body">
            <div class="skel-topline">
              <SSkeleton :sharp="false" class="skel-chip" :width="64" :height="24" />
              <SSkeleton :sharp="false" class="skel-date" :width="80" :height="14" />
            </div>
            <SSkeleton :sharp="false" class="skel-title skel-title--sm" />
            <SSkeleton :sharp="false" class="skel-line" />
          </div>
        </div>
      </template>

      <template v-else>
        <div class="skel-card-layout">
          <SSkeleton :sharp="false" class="skel-card-cover" :height="140" />
          <div class="skel-card-body">
            <div class="skel-topline">
              <SSkeleton :sharp="false" class="skel-chip" :width="64" :height="24" />
            </div>
            <SSkeleton :sharp="false" class="skel-title skel-title--sm" />
            <SSkeleton :sharp="false" class="skel-line" />
            <div class="skel-meta">
              <SSkeleton v-for="m in 2" :key="`meta-${index}-${m}`" :sharp="false" class="skel-meta-bit" />
            </div>
          </div>
        </div>
      </template>
    </article>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.skeleton-article-stack {
  display: grid;
  gap: var(--spacing-md);
}

.skel-card {
  pointer-events: none;
}

.skel-featured-layout {
  display: grid;
  grid-template-columns: minmax(260px, 1.05fr) minmax(0, 0.95fr);
  gap: 1rem;
  padding: 1rem;
  border-radius: calc(var(--border-radius-xl) + 4px);
  background: var(--surface-panel-soft);
  border: 1px solid var(--border-panel-subtle);
}

.skel-cover {
  min-height: 280px;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: var(--highlight-panel-soft);
}

.skel-img {
  width: 100% !important;
  border-radius: 1rem !important;
}

.skel-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.85rem;
  min-width: 0;
  padding: 0.18rem 0.24rem 0.12rem;
}

.skel-topline {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.skel-chip {
  border-radius: 999px !important;
}

.skel-chip--sm {
  width: 64px !important;
  height: 28px !important;
}

.skel-date {
  margin-inline-start: auto;
}

.skel-title {
  width: min(72%, 24rem) !important;
  height: 20px !important;
}

.skel-title--sm {
  width: 90% !important;
  height: 18px !important;
}

.skel-line {
  width: 100% !important;
  height: 13px !important;
}

.skel-line--short {
  width: 68% !important;
}

.skel-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.skel-tag {
  width: 48px !important;
  height: 24px !important;
  border-radius: 999px !important;
}

.skel-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  padding-top: 0.2rem;
}

.skel-meta-bit {
  width: 84px !important;
  height: 12px !important;
  border-radius: 999px !important;
}

.skel-card-layout {
  display: grid;
  gap: 0.72rem;
  padding: 0.95rem;
  border-radius: calc(var(--border-radius-xl) + 2px);
  background: var(--surface-panel-soft);
  border: 1px solid var(--border-panel-subtle);
}

.skel-card-cover {
  width: 100% !important;
  aspect-ratio: 16 / 10 !important;
  border-radius: 0.95rem !important;
}

.skel-card-body {
  display: grid;
  gap: 0.55rem;
  min-width: 0;
}

.skel-list-layout {
  display: grid;
  grid-template-columns: 160px minmax(0, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: calc(var(--border-radius-lg) + 2px);
  background: var(--surface-panel-soft);
  border: 1px solid var(--border-panel-subtle);
  align-items: start;
}

.skel-list-cover {
  border-radius: calc(var(--border-radius-lg) + 2px) !important;
}

.skel-list-body {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

@include sourcelin-down(md) {
  .skel-featured-layout {
    grid-template-columns: 1fr;
  }

  .skel-cover {
    min-height: 220px;
  }

  .skel-list-layout {
    grid-template-columns: 1fr;
  }

  .skel-list-cover {
    width: 100% !important;
    height: 140px !important;
  }
}

@include sourcelin-down(sm) {
  .skel-featured-layout,
  .skel-card-layout,
  .skel-list-layout {
    padding: 0.8rem;
  }

  .skel-cover {
    min-height: 180px;
  }

  .skel-body {
    gap: 0.68rem;
  }

  .skel-title {
    font-size: 1rem;
  }
}
</style>