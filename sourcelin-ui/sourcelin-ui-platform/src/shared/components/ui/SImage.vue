<template>
  <NImage
    class="s-image"
    v-bind="$attrs"
  >
    <slot />
  </NImage>
</template>

<script setup lang="ts">
import { NImage } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-image {
  display: block;
  width: 100%;
  height: 100%;

  :deep(.n-image) {
    display: block;
    width: 100%;
    height: 100%;
  }

  :deep(img) {
    width: 100%;
    height: 100%;
  }
}
</style>

