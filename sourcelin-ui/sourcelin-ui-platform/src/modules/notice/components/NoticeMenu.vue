<script setup lang="ts">
import type { NoticeMenuItem } from '@/modules/notice/composables/useNoticeCenter'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  items: NoticeMenuItem[]
  activeIndex: number
}>()

const emit = defineEmits<{
  change: [item: NoticeMenuItem, index: number]
}>()
</script>

<template>
  <ul class="sidebar-menu">
    <li
      v-for="(item, index) in items"
      :key="item.name"
      :class="['menu-item', { active: activeIndex === index }]"
      @click="emit('change', item, index)"
    >
      <SIcon :icon="appIcons[item.icon]" :size="16" />
      <span>{{ item.name }}</span>
    </li>
  </ul>
</template>

<style scoped lang="scss">
.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 46px;
  padding: 12px 14px;
  border-radius: 14px;
  cursor: pointer;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    color var(--transition-base),
    box-shadow var(--transition-base);
  color: var(--text-secondary);
  position: relative;
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
}

.menu-item:hover {
  color: var(--text-primary);
  border-color: var(--border-interactive-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-panel-soft);
}

.menu-item.active {
  font-weight: 600;
  color: var(--primary-color);
  background: var(--surface-panel-chip-accent);
  border-color: var(--border-panel-badge-accent);
}

.menu-item.active::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: color-mix(in srgb, var(--primary-color) 72%, transparent);
  border-radius: 0 2px 2px 0;
}
</style>


