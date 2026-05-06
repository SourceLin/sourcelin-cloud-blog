<template>
  <NForm
    class="s-form"
    v-bind="$attrs"
  >
    <slot />
  </NForm>
</template>

<script setup lang="ts">
import { NForm } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-form {
  :deep(.n-form-item-label__text) {
    color: var(--text-secondary);
    font-weight: 500;
  }
}
</style>

