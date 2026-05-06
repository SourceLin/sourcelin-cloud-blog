<template>
  <NSelect
    class="s-select"
    :value="modelValue"
    v-bind="$attrs"
    @update:value="handleUpdate"
  >
    <slot />
  </NSelect>
</template>

<script setup lang="ts">
import { NSelect } from 'naive-ui'

type SelectValue = string | number | Array<string | number> | null

interface Props {
  modelValue?: SelectValue
}

withDefaults(defineProps<Props>(), {
  modelValue: null
})

const emit = defineEmits<{
  'update:modelValue': [value: SelectValue]
}>()

const handleUpdate = (value: SelectValue) => {
  emit('update:modelValue', value)
}
</script>

<style scoped lang="scss">
.s-select {
  :deep(.n-base-selection) {
    border-radius: var(--border-radius-lg);
    border: 1px solid var(--input-border);
    background: var(--input-background);
    box-shadow: var(--shadow-soft);
    transition:
      border-color var(--transition-base),
      background var(--transition-base),
      box-shadow var(--transition-base);
  }

  :deep(.n-base-selection:hover) {
    border-color: var(--input-border-hover);
    background: var(--input-background-focus);
  }

  :deep(.n-base-selection--focus) {
    border-color: var(--input-border-focus);
    background: var(--input-background-focus);
    box-shadow: var(--shadow-medium);
  }

  :deep(.n-base-selection-label),
  :deep(.n-base-selection-input__content),
  :deep(.n-base-selection-placeholder) {
    color: var(--input-text);
  }

  :deep(.n-base-selection-placeholder) {
    color: var(--input-placeholder);
  }
}
</style>

