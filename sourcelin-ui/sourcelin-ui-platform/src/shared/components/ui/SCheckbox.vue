<template>
  <NCheckbox
    class="s-checkbox"
    :checked="modelValue"
    v-bind="$attrs"
    @update:checked="handleUpdate"
  >
    <slot />
  </NCheckbox>
</template>

<script setup lang="ts">
import { NCheckbox } from 'naive-ui'

interface Props {
  modelValue?: boolean
}

withDefaults(defineProps<Props>(), {
  modelValue: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const handleUpdate = (value: boolean) => {
  emit('update:modelValue', value)
}
</script>

<style scoped lang="scss">
.s-checkbox {
  :deep(.n-checkbox__label) {
    color: var(--text-secondary);
  }

  :deep(.n-checkbox-box) {
    border-color: var(--input-border);
    background: var(--input-background);
    box-shadow: var(--shadow-soft);
  }

  :deep(.n-checkbox-box:hover) {
    border-color: var(--input-border-hover);
    background: var(--input-background-focus);
  }

  :deep(.n-checkbox-box.n-checkbox-box--checked) {
    border-color: var(--primary-color);
    background: var(--primary-color);
    box-shadow: var(--input-focus-ring);
  }

  :deep(.n-checkbox-box__border) {
    border-color: transparent;
  }
}
</style>
