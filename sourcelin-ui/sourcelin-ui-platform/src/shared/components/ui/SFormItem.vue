<template>
  <NFormItem
    class="s-form-item"
    v-bind="$attrs"
  >
    <slot />
  </NFormItem>
</template>

<script setup lang="ts">
import { NFormItem } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-form-item {
  :deep(.n-form-item-feedback-wrapper) {
    color: var(--text-secondary);
  }
}
</style>

