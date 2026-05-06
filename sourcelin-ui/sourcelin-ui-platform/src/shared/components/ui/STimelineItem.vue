<template>
  <NTimelineItem
    :class="['s-timeline-item', `s-timeline-item--${variant}`]"
    :type="type"
    :title="title"
    :content="content"
    :time="time"
    :color="color"
    v-bind="$attrs"
  >
    <template v-if="$slots.icon" #icon>
      <slot name="icon" />
    </template>

    <slot />
  </NTimelineItem>
</template>

<script setup lang="ts">
import { NTimelineItem } from 'naive-ui'

interface Props {
  type?: 'default' | 'info' | 'success' | 'warning' | 'error'
  title?: string
  content?: string
  time?: string
  color?: string
  variant?: 'default' | 'archive'
}

withDefaults(defineProps<Props>(), {
  type: 'default',
  title: '',
  content: '',
  time: '',
  color: undefined,
  variant: 'default'
})
</script>

<style scoped lang="scss">
.s-timeline-item {
  --n-title-text-color: var(--title-color);
  --n-content-text-color: var(--text-secondary);
  --n-time-text-color: var(--text-tertiary);

  :deep(.n-timeline-item-content) {
    padding-bottom: 20px;
  }
}

.s-timeline-item--archive {
  :deep(.n-timeline-item-content) {
    padding-bottom: 28px;
  }

  :deep(.n-timeline-item-content__header) {
    margin-bottom: 0;
  }

  :deep(.n-timeline-item-content__time) {
    display: none;
  }

  :deep(.n-timeline-item-timeline__icon) {
    display: flex;
    align-items: flex-start;
    justify-content: center;
  }
}
</style>
