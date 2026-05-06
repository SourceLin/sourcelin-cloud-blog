<template>
  <NTooltip class="s-tooltip" v-bind="$attrs">
    <template v-if="$slots.trigger" #trigger>
      <slot name="trigger" />
    </template>
    <slot />
  </NTooltip>
</template>

<script setup lang="ts">
import { NTooltip } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-tooltip {
  :deep(.n-popover) {
    border-radius: var(--border-radius-lg);
  }
}
</style>

