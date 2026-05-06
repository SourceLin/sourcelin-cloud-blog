<script setup lang="ts">
import type { Component } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

defineProps<{
  title: string
  description: string
  icon: Component
}>()
</script>

<template>
  <div class="channel-coming-soon" role="status">
    <div class="channel-coming-soon__icon">
      <SIcon :icon="icon" :size="32" />
    </div>
    <h3 class="channel-coming-soon__title">{{ title }}</h3>
    <p class="channel-coming-soon__desc">{{ description }}</p>
    <span class="channel-coming-soon__tag">即将开放</span>
  </div>
</template>

<style scoped lang="scss">
.channel-coming-soon {
  display: grid;
  justify-items: center;
  text-align: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xxl) var(--spacing-xl);
  min-height: 280px;
  align-content: center;
}

.channel-coming-soon__icon {
  width: 4rem;
  height: 4rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: var(--primary-color);
}

.channel-coming-soon__title {
  margin: 0;
  font-size: var(--font-size-xl, 18px);
  font-weight: 700;
  color: var(--title-color);
}

.channel-coming-soon__desc {
  margin: 0;
  max-width: 28rem;
  font-size: var(--font-size-md, 14px);
  line-height: 1.75;
  color: var(--text-reading-soft);
}

.channel-coming-soon__tag {
  padding: 0.35rem 0.75rem;
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  color: var(--text-secondary);
}
</style>
