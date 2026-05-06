<template>
  <NSkeleton class="s-skeleton" aria-hidden="true" role="presentation" v-bind="$attrs">
    <slot />
  </NSkeleton>
</template>

<script setup lang="ts">
import { NSkeleton } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-skeleton {
  display: block;
  --n-color: color-mix(in srgb, var(--border-color) 80%, transparent);
  --n-color-end: color-mix(in srgb, var(--surface-panel-soft) 82%, transparent);
}
</style>
