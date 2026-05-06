<template>
  <NPopconfirm
    class="s-popconfirm"
    :positive-text="positiveText"
    :negative-text="negativeText"
    v-bind="$attrs"
  >
    <template v-if="$slots.trigger" #trigger>
      <slot name="trigger" />
    </template>
    <slot />
  </NPopconfirm>
</template>

<script setup lang="ts">
import { NPopconfirm } from 'naive-ui'

withDefaults(defineProps<{
  positiveText?: string
  negativeText?: string
}>(), {
  positiveText: '确认',
  negativeText: '取消'
})
</script>

<style scoped lang="scss">
.s-popconfirm {
  :deep(.n-popover) {
    border-radius: var(--border-radius-lg);
    border: 1px solid var(--border-panel-default);
    background: var(--surface-panel-soft);
    box-shadow: var(--shadow-panel-hover);
  }

  :deep(.n-popconfirm__body) {
    color: var(--text-primary);
    line-height: 1.6;
  }

  :deep(.n-popconfirm__icon) {
    color: var(--warning-color);
  }

  :deep(.n-popconfirm__action .n-button) {
    border-radius: 999px;
  }
}
</style>
