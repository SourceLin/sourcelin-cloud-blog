<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  width?: string
  padding?: string
}>(), {
  width: '1080px',
  padding: '0 20px 72px'
})

const shellStyle = computed(() => ({
  '--page-shell-width': props.width,
  '--page-shell-padding': props.padding
}))
</script>

<template>
  <div class="page-shell" :style="shellStyle">
    <slot />
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.page-shell {
  position: relative;
  display: grid;
  gap: var(--page-section-gap);
  isolation: isolate;
  width: 100%;
  max-width: var(--page-shell-width);
  margin: 0 auto;
  padding: var(--page-shell-padding);
}

.page-shell::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: clamp(6rem, 12vw, 8.5rem);
  border-radius: 0 0 calc(var(--glass-radius) + 12px) calc(var(--glass-radius) + 12px);
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-page-ambient) 78%, transparent) 0%, transparent 100%);
  pointer-events: none;
  opacity: 0.74;
  z-index: -2;
}

.page-shell::after {
  content: '';
  position: absolute;
  inset: clamp(4.5rem, 9vw, 6.5rem) 0 auto;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    color-mix(in srgb, var(--border-panel-accent) 68%, transparent) 24%,
    color-mix(in srgb, var(--border-panel-subtle) 92%, transparent) 50%,
    color-mix(in srgb, var(--border-panel-accent) 68%, transparent) 76%,
    transparent 100%
  );
  pointer-events: none;
  opacity: 0.82;
  z-index: -1;
}

@include sourcelin-down(md) {
  .page-shell {
    gap: var(--page-block-gap);
    padding: 0 16px 40px;
  }
}
</style>