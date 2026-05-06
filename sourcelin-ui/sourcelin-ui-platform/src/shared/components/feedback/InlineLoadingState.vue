<template>
  <SSurfacePanel class="inline-loading-state" variant="inset" role="status" aria-live="polite">
    <div class="inline-loading-state__spinner">
      <SSpin size="small" />
    </div>

    <div class="inline-loading-state__copy">
      <p class="inline-loading-state__title">{{ title }}</p>
      <p v-if="description" class="inline-loading-state__description">{{ description }}</p>
    </div>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import SSpin from '@/shared/components/ui/SSpin.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

withDefaults(defineProps<{
  title?: string
  description?: string
}>(), {
  title: '正在切换分类',
  description: '内容即将更新'
})
</script>

<style scoped lang="scss">
.inline-loading-state {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  min-width: min(100%, 16rem);
  padding: 0.9rem 1.1rem;
  border-radius: calc(var(--border-radius-xl) + 2px);
  border: 1px solid color-mix(in srgb, var(--border-panel-default) 82%, transparent);
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--surface-panel-strong) 92%, transparent),
      color-mix(in srgb, var(--surface-panel-chip-accent) 58%, transparent)
    );
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 88%, transparent),
    var(--highlight-panel-soft),
    var(--shadow-panel-soft);
}

.inline-loading-state__spinner {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 999px;
  background: color-mix(in srgb, var(--surface-white-12) 72%, transparent);
  color: var(--primary-color);
  flex: 0 0 auto;
}

.inline-loading-state__copy {
  display: grid;
  gap: 0.18rem;
  text-align: left;
}

.inline-loading-state__title,
.inline-loading-state__description {
  margin: 0;
}

.inline-loading-state__title {
  color: var(--title-color);
  font-size: 0.92rem;
  font-weight: 700;
  line-height: 1.2;
}

.inline-loading-state__description {
  color: var(--text-secondary);
  font-size: 0.78rem;
  line-height: 1.45;
}
</style>
