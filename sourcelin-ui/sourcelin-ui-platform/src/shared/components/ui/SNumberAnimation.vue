<template>
  <NNumberAnimation class="s-number-animation" v-bind="$attrs" />
</template>

<script setup lang="ts">
import { NNumberAnimation } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-number-animation {
  color: inherit;
  font-variant-numeric: tabular-nums;
}
</style>

