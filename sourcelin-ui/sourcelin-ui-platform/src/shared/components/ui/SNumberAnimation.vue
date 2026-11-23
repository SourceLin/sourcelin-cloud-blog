<template>
  <span class="s-number-animation"><NNumberAnimation v-bind="props" /></span>
</template>

<script setup lang="ts">
import { NNumberAnimation, numberAnimationProps } from 'naive-ui'

const props = defineProps(numberAnimationProps)
</script>

<style scoped lang="scss">
.s-number-animation {
  color: inherit;
  font-variant-numeric: tabular-nums;
}
</style>

