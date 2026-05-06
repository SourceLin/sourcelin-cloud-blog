<template>
  <NTimeline :class="['s-timeline', `s-timeline--${variant}`]" :icon-size="iconSize" v-bind="$attrs">
    <slot />
  </NTimeline>
</template>

<script setup lang="ts">
import { NTimeline } from 'naive-ui'

interface Props {
  iconSize?: number
  variant?: 'default' | 'archive'
}

withDefaults(defineProps<Props>(), {
  iconSize: 20,
  variant: 'default'
})
</script>

<style scoped lang="scss">
.s-timeline {
  --n-line-color: var(--border-panel-badge-accent);
  --n-title-text-color: var(--title-color);
  --n-content-text-color: var(--text-secondary);
}

.s-timeline--archive {
  --n-line-color: color-mix(in srgb, var(--border-panel-badge-accent) 62%, transparent);

  :deep(.n-timeline-item-timeline) {
    padding-inline-end: 14px;
  }

  :deep(.n-timeline-item-timeline__line) {
    width: 2px;
    border-radius: 999px;
    background:
      linear-gradient(
        180deg,
        color-mix(in srgb, var(--border-panel-badge-accent) 82%, transparent),
        color-mix(in srgb, var(--border-panel-subtle) 84%, transparent)
      );
    box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-20) 86%, transparent);
  }

  :deep(.n-timeline-item:last-child .n-timeline-item-timeline__line) {
    opacity: 0.62;
  }
}
</style>
