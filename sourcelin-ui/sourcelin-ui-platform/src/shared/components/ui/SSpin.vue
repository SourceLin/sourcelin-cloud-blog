<template>
  <NSpin
    class="s-spin"
    v-bind="$attrs"
  >
    <slot />
  </NSpin>
</template>

<script setup lang="ts">
import { NSpin } from 'naive-ui'

// NSpin wrapper - pass all props through
</script>

<style scoped lang="scss">
.s-spin {
  :deep(.n-spin__spinner) {
    color: var(--primary-color);
  }
}
</style>

