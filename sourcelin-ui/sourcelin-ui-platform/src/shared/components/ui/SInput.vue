<template>
  <NInput
    class="s-input"
    :class="[
      `s-input--${variant}`,
      `s-input--${surface}`,
      { 's-input--gem': gem }
    ]"
    :value="modelValue ?? ''"
    v-bind="$attrs"
    @update:value="handleUpdate"
    @focus="handleFocus"
    @blur="handleBlur"
  >
    <template v-if="$slots.prefix" #prefix>
      <slot name="prefix" />
    </template>
    <template v-if="$slots.suffix" #suffix>
      <slot name="suffix" />
    </template>
    <template v-if="$slots['clear-icon']" #clear-icon>
      <slot name="clear-icon" />
    </template>
    <template v-if="$slots['password-invisible-icon']" #password-invisible-icon>
      <slot name="password-invisible-icon" />
    </template>
    <template v-if="$slots['password-visible-icon']" #password-visible-icon>
      <slot name="password-visible-icon" />
    </template>
  </NInput>
</template>

<script setup lang="ts">
import { NInput } from 'naive-ui'

type SInputValue = string | null

interface Props {
  modelValue?: SInputValue
  variant?: 'default' | 'filled'
  surface?: 'default' | 'modal-field'
  gem?: boolean // 宝石色散 focus ring
}

withDefaults(defineProps<Props>(), {
  modelValue: null,
  variant: 'default',
  surface: 'default',
  gem: false
})

const emit = defineEmits<{
  'update:modelValue': [value: SInputValue]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
}>()

const handleUpdate = (value: SInputValue) => {
  emit('update:modelValue', value)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}
</script>

<style scoped lang="scss">
/* Root transparency: src/assets/styles/naive/override.scss (.n-input.s-input) — global + !important vs Naive load order */

.s-input {
  --s-input-min-height-resolved: var(--s-input-min-height, 46px);
  --s-input-wrapper-padding-resolved: var(--s-input-wrapper-padding, 4px);
  --s-input-padding-x-resolved: var(--s-input-padding-x, 0.95rem);
  --s-input-font-size-resolved: var(--s-input-font-size, inherit);
  --s-input-textarea-padding-y-resolved: var(--s-input-textarea-padding-y, 0.9rem);
  --s-input-textarea-font-size-resolved: var(--s-input-textarea-font-size, inherit);
  --s-input-textarea-line-height-resolved: var(--s-input-textarea-line-height, 1.65);
  --s-input-textarea-resize-resolved: var(--s-input-textarea-resize, vertical);
  --s-input-textarea-min-height-resolved: var(--s-input-textarea-min-height, auto);
  --s-input-textarea-max-height-resolved: var(--s-input-textarea-max-height, none);
  --s-input-textarea-overflow-y-resolved: var(--s-input-textarea-overflow-y, auto);
  --s-input-textarea-transition-resolved: var(--s-input-textarea-transition, none);
  --s-input-autofill-background: var(--input-background);
  --s-input-autofill-background-focus: var(--input-background-focus);
  --n-padding-left: var(--s-input-padding-x-resolved);
  --n-padding-right: var(--s-input-padding-x-resolved);
  --n-padding-vertical: var(--s-input-textarea-padding-y-resolved);

  :deep(.n-input-wrapper) {
    min-height: var(--s-input-min-height-resolved);
    padding: var(--s-input-wrapper-padding-resolved);
    border-radius: var(--s-input-border-radius, var(--border-radius-lg));
    border: 1px solid var(--input-border);
    background:
      linear-gradient(180deg, color-mix(in srgb, var(--surface-white-20) 36%, transparent), transparent),
      var(--input-background);
    box-shadow:
      var(--shadow-soft),
      inset 0 1px 0 color-mix(in srgb, var(--surface-white-20) 56%, transparent);
    transition:
      border-color var(--transition-base),
      background var(--transition-base),
      box-shadow var(--transition-base);
  }

  :deep(.n-input__input-el),
  :deep(.n-input__textarea-el) {
    color: var(--input-text);
    background: transparent !important;
  }

  :deep(.n-input__input-el) {
    font-size: var(--s-input-font-size-resolved);
  }

  :deep(.n-input__textarea-el) {
    font-size: var(--s-input-textarea-font-size-resolved);
    line-height: var(--s-input-textarea-line-height-resolved);
    resize: var(--s-input-textarea-resize-resolved);
    min-height: var(--s-input-textarea-min-height-resolved);
    max-height: var(--s-input-textarea-max-height-resolved);
    overflow-y: var(--s-input-textarea-overflow-y-resolved);
    transition: var(--s-input-textarea-transition-resolved);
  }

  :deep(.n-input__placeholder) {
    color: var(--input-placeholder);
  }

  :deep(.n-input-wrapper:hover) {
    border-color: var(--input-border-hover);
    background: var(--input-background-focus);
  }

  :deep(.n-input-wrapper.n-input-wrapper--focus) {
    border-color: var(--input-border-focus);
    background: var(--input-background-focus);
    box-shadow: var(--input-focus-ring), var(--shadow-medium);
  }
}

.s-input--filled {
  --s-input-autofill-background: var(--surface-panel-soft);

  :deep(.n-input-wrapper) {
    background:
      linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 36%, transparent), transparent),
      var(--surface-panel-soft);
  }
}

/* solid 模态内输入（--surface-modal-*） */
.s-input--modal-field {
  --input-background: color-mix(in srgb, var(--surface-modal-solid) 78%, var(--surface-modal-field));
  --input-background-focus: color-mix(in srgb, var(--surface-modal-solid) 90%, var(--primary-light));
  --s-input-autofill-background: var(--surface-modal-field);
  --s-input-autofill-background-focus: var(--input-background-focus);

  :deep(.n-input-wrapper) {
    border-color: color-mix(in srgb, var(--border-color) 70%, var(--input-border));
    background: var(--input-background);
    box-shadow: var(--shadow-soft);
  }

  :deep(.n-input-wrapper:hover) {
    border-color: var(--input-border-hover);
    background: var(--input-background-focus);
  }

  :deep(.n-input-wrapper.n-input-wrapper--focus) {
    border-color: var(--input-border-focus);
    background: var(--input-background-focus);
    box-shadow: var(--input-focus-ring), var(--shadow-medium);
  }
}

/* After filled overrides so disabled + filled still uses Naive disabled surface */
.s-input.n-input--disabled :deep(.n-input-wrapper) {
  background: var(--n-color-disabled);
  border-color: var(--input-border);
  box-shadow: none;
}

/* 宝石色散 focus ring */
.s-input--gem :deep(.n-input-wrapper.n-input-wrapper--focus) {
  border-color: var(--glass-gem-border);
  box-shadow:
    var(--input-focus-ring),
    var(--shadow-medium),
    0 0 0 1px color-mix(in srgb, var(--primary-color) 14%, transparent),
    -2px -2px 12px color-mix(in srgb, var(--primary-color) 10%, transparent),
    2px -2px 12px color-mix(in srgb, var(--companion-color) 8%, transparent),
    -2px 2px 12px color-mix(in srgb, var(--accent-color) 8%, transparent),
    2px 2px 12px color-mix(in srgb, var(--aurora-color) 6%, transparent);
}
</style>
