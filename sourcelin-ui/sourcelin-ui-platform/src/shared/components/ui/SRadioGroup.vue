<template>
  <NRadioGroup
    class="s-radio-group"
    :value="modelValue"
    v-bind="$attrs"
    @update:value="handleUpdate"
  >
    <slot />
  </NRadioGroup>
</template>

<script setup lang="ts">
import { NRadioGroup } from 'naive-ui'

type RadioValue = string | number | boolean | null

interface Props {
  modelValue?: RadioValue
}

withDefaults(defineProps<Props>(), {
  modelValue: null
})

const emit = defineEmits<{
  'update:modelValue': [value: RadioValue]
}>()

const handleUpdate = (value: RadioValue) => {
  emit('update:modelValue', value)
}
</script>

<style scoped lang="scss">
.s-radio-group {
  display: inline-flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}
</style>

