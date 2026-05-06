<template>
  <NRadio
    class="s-radio"
    :value="value"
    v-bind="$attrs"
  >
    <slot />
  </NRadio>
</template>

<script setup lang="ts">
import { NRadio } from 'naive-ui'

interface Props {
  value?: string | number | boolean
}

withDefaults(defineProps<Props>(), {
  value: undefined
})
</script>

<style scoped lang="scss">
.s-radio {
  :deep(.n-radio__label) {
    color: var(--text-primary);
  }
}
</style>

