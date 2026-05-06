<template>
  <NPopover
    class="s-popover-menu"
    :placement="placement"
    :show-arrow="showArrow"
    :trigger="trigger"
    :flip="flip"
    v-bind="$attrs"
  >
    <template v-if="$slots.trigger" #trigger>
      <slot name="trigger" />
    </template>
    <div class="s-popover-menu__panel">
      <slot />
    </div>
  </NPopover>
</template>

<script setup lang="ts">
import { NPopover } from 'naive-ui'
import type { PopoverPlacement } from 'naive-ui'

withDefaults(defineProps<{
  placement?: PopoverPlacement
  showArrow?: boolean
  trigger?: 'hover' | 'click' | 'focus' | 'manual'
  flip?: boolean
}>(), {
  placement: 'top-end',
  showArrow: false,
  trigger: 'click',
  flip: true
})
</script>

<style scoped lang="scss">
.s-popover-menu {
  :deep(.n-popover) {
    border-radius: calc(var(--border-radius-lg) + 2px);
    border: 1px solid color-mix(in srgb, var(--border-panel-default) 94%, transparent);
    background:
      linear-gradient(
        180deg,
        color-mix(in srgb, var(--surface-panel-default) 96%, var(--surface-page-content)),
        color-mix(in srgb, var(--surface-panel-soft) 92%, var(--surface-page-content))
      );
    box-shadow: var(--highlight-panel-soft), var(--shadow-panel-hover);
    backdrop-filter: blur(calc(var(--glass-blur) - 2px)) saturate(calc(var(--glass-saturate) + 4%));
  }
}

.s-popover-menu__panel {
  display: block;
}
</style>
