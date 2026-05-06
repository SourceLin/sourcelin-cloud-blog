<template>
  <NIcon
    class="s-icon"
    :size="size"
    :color="color"
    v-bind="$attrs"
  >
    <component :is="icon" />
  </NIcon>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { NIcon } from 'naive-ui'

interface Props {
  icon: Component
  size?: number | string
  color?: string
}

withDefaults(defineProps<Props>(), {
  size: 18,
  color: ''
})
</script>

<style scoped lang="scss">
.s-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}
</style>

