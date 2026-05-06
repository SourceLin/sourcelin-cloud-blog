<template>
  <component
    :is="tag"
    class="s-surface-panel"
    :class="panelClasses"
    v-bind="$attrs"
  >
    <slot />
  </component>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed } from 'vue'

interface Props {
  tag?: string | Component
  variant?: 'default' | 'soft' | 'strong' | 'inset' | 'modal-field'
  interactive?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  tag: 'div',
  variant: 'default',
  interactive: false
})

const foundationVariantClass = computed(() => {
  switch (props.variant) {
    case 'soft':
      return 'sourcelin-panel-soft'
    case 'strong':
      return 'sourcelin-panel-strong'
    case 'inset':
      return 'sourcelin-panel-inset'
    case 'modal-field':
      return 'sourcelin-panel-modal-field'
    default:
      return 'sourcelin-panel'
  }
})

const panelClasses = computed(() => [
  foundationVariantClass.value,
  {
    'sourcelin-panel-hoverable': props.interactive
  }
])
</script>

<style scoped lang="scss">
.s-surface-panel {
  display: block;
}
</style>
