<template>
  <SHorizontalOverflowRail
    class="hot-categories"
    group-label="热榜分类筛选"
    prev-aria-label="向左查看更多分类"
    next-aria-label="向右查看更多分类"
    :refresh-keys="[categories, activeCategory]"
  >
    <button
      v-for="category in categories"
      :key="category.id"
      type="button"
      class="category-tab"
      :class="{ active: activeCategory === category.id }"
      @click="emit('change', category.id)"
    >
      <span class="category-name">{{ category.name }}</span>
    </button>
  </SHorizontalOverflowRail>
</template>

<script setup lang="ts">
import type { HotCategory } from '@/modules/hot/composables/useHotPage'
import SHorizontalOverflowRail from '@/shared/components/ui/SHorizontalOverflowRail.vue'

const props = defineProps<{
  categories: HotCategory[]
  activeCategory: string | number
}>()

const emit = defineEmits<{
  change: [categoryId: string | number]
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.hot-categories {
  --surface-page-content: var(--surface-page-content);
}

.category-tab {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  min-height: 42px;
  padding: 0 18px;
  overflow: hidden;
  border-radius: 999px;
  border: 1px solid var(--border-panel-subtle);
  background: color-mix(in srgb, var(--surface-panel-chip) 72%, transparent);
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base),
    color var(--transition-base);
}

.category-tab::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 58%;
  background: var(--surface-panel-specular-soft);
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--transition-base);
}

.category-tab:hover {
  transform: translateY(-1px);
  border-color: var(--border-interactive-hover);
  color: var(--text-primary);
  box-shadow: var(--shadow-panel-subtle);
}

.category-tab:hover::before {
  opacity: 1;
}

.category-tab.active {
  border-color: color-mix(in srgb, var(--primary-color) 34%, var(--border-panel-badge-accent));
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 14%, transparent), transparent),
    linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 10%, var(--surface-panel-chip-accent)), var(--surface-panel-chip-accent));
  color: var(--primary-active);
  box-shadow:
    var(--highlight-panel-chip),
    var(--shadow-panel-default),
    0 0 0 1px color-mix(in srgb, var(--primary-color) 10%, transparent);
}

.category-tab.active::before {
  opacity: 1;
}

.category-name {
  position: relative;
  z-index: 1;
  font-size: 0.92rem;
  font-weight: 700;
  white-space: nowrap;
}

@include sourcelin-down(md) {
  .category-tab {
    min-height: 40px;
    padding: 0 14px;
  }
}

</style>
