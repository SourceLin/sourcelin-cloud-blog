<template>
  <NPagination
    class="s-pagination"
    v-bind="$attrs"
  >
    <slot />
  </NPagination>
</template>

<script setup lang="ts">
import { NPagination } from 'naive-ui'

// NPagination wrapper - pass all props through
</script>

<style scoped lang="scss">
.s-pagination {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem;
  border-radius: 999px;
  background: var(--pagination-shell);
  border: var(--glass-panel-outline);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-soft);

  :deep(.n-pagination-item) {
    border-radius: 999px;
    background: var(--pagination-item);
    transition:
      transform var(--transition-base),
      box-shadow var(--transition-base),
      background var(--transition-base);
  }

  :deep(.n-pagination-item:hover),
  :deep(.n-pagination-item.n-pagination-item--active) {
    background: var(--pagination-item-active);
    box-shadow: var(--button-glow);
    transform: translateY(-1px);
  }

  :deep(.n-pagination-button) {
    border-radius: 999px;
  }
}
</style>

