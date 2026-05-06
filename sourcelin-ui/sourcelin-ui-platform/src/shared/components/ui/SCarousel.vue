<template>
  <NCarousel
    class="s-carousel"
    v-bind="$attrs"
  >
    <slot />
  </NCarousel>
</template>

<script setup lang="ts">
import { NCarousel } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-carousel {
  border-radius: var(--border-radius-xl);
  overflow: hidden;
}
</style>

