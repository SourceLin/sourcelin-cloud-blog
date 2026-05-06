<template>
  <NModal :class="modalClass" v-bind="$attrs">
    <slot />
  </NModal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NModal } from 'naive-ui'

type ModalSurface = 'glass' | 'solid'

const props = withDefaults(
  defineProps<{
    /** solid 时使用 --surface-modal-solid */
    surface?: ModalSurface
  }>(),
  { surface: 'glass' }
)

const modalClass = computed(() => [
  's-modal',
  { 's-modal--solid': props.surface === 'solid' }
])
</script>

<style scoped lang="scss">
:deep(.n-card.s-modal) {
  border-radius: var(--glass-radius);
  border: 1px solid var(--glass-border);
  background: var(--glass-surface-strong);
  box-shadow: var(--shadow-large);
}

:deep(.n-card.s-modal.s-modal--solid) {
  background: var(--surface-modal-solid);
  border-color: color-mix(in srgb, var(--border-color) 72%, var(--glass-border));
  box-shadow: var(--shadow-large);
  -webkit-backdrop-filter: none;
  backdrop-filter: none;
}
</style>

