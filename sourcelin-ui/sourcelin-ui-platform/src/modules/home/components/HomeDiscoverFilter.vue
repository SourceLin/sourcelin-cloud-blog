<template>
  <SHorizontalOverflowRail
    class="discover-filter"
    group-label="发现分类筛选"
    prev-aria-label="向左查看更多分类"
    next-aria-label="向右查看更多分类"
    :refresh-keys="[categories, selectedCategoryId]"
  >
    <SSurfaceChip
      tag="button"
      variant="button"
      :tone="selectedCategoryId == null ? 'accent' : 'default'"
      class="discover-filter__chip"
      :aria-pressed="selectedCategoryId == null"
      type="button"
      @click="emit('select', null)"
    >
      <span>全部</span>
    </SSurfaceChip>

    <SSurfaceChip
      v-for="category in categories"
      :key="category.id"
      tag="button"
      variant="button"
      :tone="selectedCategoryId === category.id ? 'accent' : 'default'"
      class="discover-filter__chip"
      :class="{ 'discover-filter__chip--empty': category.count === 0 }"
      :aria-pressed="selectedCategoryId === category.id"
      :disabled="category.count === 0"
      type="button"
      @click="emit('select', category.id)"
    >
      <span class="discover-filter__chip-name">{{ category.name }}</span>
      <SSurfaceChip variant="counter" size="xs" class="discover-filter__chip-count">
        {{ category.count }}
      </SSurfaceChip>
    </SSurfaceChip>
  </SHorizontalOverflowRail>
</template>

<script setup lang="ts">
import type { HomeCategory } from '@/modules/home/model/home-discover'
import SHorizontalOverflowRail from '@/shared/components/ui/SHorizontalOverflowRail.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'

const props = defineProps<{
  categories: HomeCategory[]
  selectedCategoryId: number | null
}>()

const emit = defineEmits<{
  select: [categoryId: number | null]
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.discover-filter {
  /* 覆盖共享轨道的遮罩底色，避免和首页容器视觉层冲突 */
  --surface-page-content: var(--surface-page);
}

.discover-filter__chip {
  position: relative;
  flex: 0 0 auto;
  min-height: 42px;
  padding: 0 18px;
  overflow: hidden;
  border: 1px solid var(--border-panel-subtle);
  background: color-mix(in srgb, var(--surface-panel-chip) 72%, transparent);
  color: var(--text-secondary);
  font: inherit;
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base);
}

.discover-filter__chip::after {
  display: none;
}

.discover-filter__chip::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 58%;
  background: var(--surface-panel-specular-soft);
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--transition-base);
}

.discover-filter__chip:hover {
  transform: translateY(-1px);
  border-color: var(--border-interactive-hover);
  color: var(--text-primary);
  box-shadow: var(--shadow-panel-subtle);
}

.discover-filter__chip:hover::before {
  opacity: 1;
}

.discover-filter__chip[aria-pressed='true'] {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--primary-color) 34%, var(--border-panel-badge-accent));
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 14%, transparent), transparent),
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--primary-color) 10%, var(--surface-panel-chip-accent)),
      var(--surface-panel-chip-accent)
    );
  color: var(--primary-active);
  box-shadow:
    var(--highlight-panel-chip),
    var(--shadow-panel-default),
    0 0 0 1px color-mix(in srgb, var(--primary-color) 10%, transparent);
}

.discover-filter__chip[aria-pressed='true']::before {
  opacity: 1;
}

.discover-filter__chip[aria-pressed='true'] .discover-filter__chip-name {
  font-weight: 700;
  color: var(--title-color);
}

.discover-filter__chip[aria-pressed='true'] .discover-filter__chip-count {
  background: color-mix(in srgb, var(--surface-white-12) 72%, transparent);
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 86%, transparent);
}

.discover-filter__chip[aria-pressed='false'] {
  opacity: 1;
}

.discover-filter__chip[aria-pressed='false']:hover {
  opacity: 1;
}

.discover-filter__chip--empty {
  opacity: 0.52;
}

.discover-filter__chip-name {
  max-width: 9em;
  overflow: hidden;
  text-overflow: ellipsis;
}

.discover-filter__chip-count {
  box-shadow: none;
}

@include sourcelin-down(sm) {
  .discover-filter__chip {
    min-height: 40px;
    padding: 0 14px;
  }
}
</style>
