<template>
  <SInput
    class="s-textarea"
    :model-value="modelValue ?? ''"
    :surface="surface"
    type="textarea"
    v-bind="$attrs"
    @update:model-value="handleUpdate"
    @focus="handleFocus"
    @blur="handleBlur"
  />
</template>

<script setup lang="ts">
import SInput from '@/shared/components/ui/SInput.vue'

type STextareaValue = string | null

interface Props {
  modelValue?: STextareaValue
  surface?: 'default' | 'modal-field'
}

withDefaults(defineProps<Props>(), {
  modelValue: null,
  surface: 'default'
})

const emit = defineEmits<{
  'update:modelValue': [value: STextareaValue]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
}>()

const handleUpdate = (value: STextareaValue) => {
  emit('update:modelValue', value)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}
</script>
