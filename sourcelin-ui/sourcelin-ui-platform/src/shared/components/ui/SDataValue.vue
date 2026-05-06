<template>
  <span class="s-data-value" :class="[`s-data-value--${valueKind}`, `s-data-value--${size}`]">
    {{ displayValue }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  value: string | number
  size?: 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md'
})

const displayValue = computed(() => String(props.value ?? '').trim())

const valueKind = computed<'number' | 'cjk' | 'text'>(() => {
  if (/^[\d./:%+\-]+$/.test(displayValue.value)) {
    return 'number'
  }
  if (/[\u4e00-\u9fa5]/.test(displayValue.value)) {
    return 'cjk'
  }
  return 'text'
})
</script>

<style scoped lang="scss">
.s-data-value {
  display: inline-block;
  color: var(--text-primary);
  word-break: break-word;
}

.s-data-value--number {
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.01em;
  font-weight: 700;
  line-height: 1.15;
}

.s-data-value--cjk {
  font-weight: 600;
  line-height: 1.6;
  word-break: keep-all;
}

.s-data-value--text {
  font-weight: 600;
  line-height: 1.45;
}

.s-data-value--md.s-data-value--number {
  font-size: clamp(1.08rem, 2.3vw, 1.45rem);
}

.s-data-value--md.s-data-value--cjk,
.s-data-value--md.s-data-value--text {
  font-size: clamp(0.94rem, 1.6vw, 1.06rem);
}

.s-data-value--lg.s-data-value--number {
  font-size: clamp(1.4rem, 2.6vw, 2rem);
}

.s-data-value--lg.s-data-value--cjk,
.s-data-value--lg.s-data-value--text {
  font-size: clamp(0.98rem, 1.8vw, 1.12rem);
}
</style>
