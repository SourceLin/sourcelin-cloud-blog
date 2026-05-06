<template>
  <NDrawer class="s-drawer" v-bind="$attrs">
    <slot />
  </NDrawer>
</template>

<script setup lang="ts">
import { NDrawer } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-drawer {
  :deep(.n-drawer-body-content-wrapper) {
    background: transparent;
  }
}
</style>

