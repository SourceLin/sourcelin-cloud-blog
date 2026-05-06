<template>
  <NDivider class="s-divider" v-bind="$attrs">
    <slot />
  </NDivider>
</template>

<script setup lang="ts">
import { NDivider } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-divider {
  margin: 0;
}
</style>
