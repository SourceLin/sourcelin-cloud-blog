<script setup lang="ts">
import type { ChannelConfig, MessageChannel } from '@/modules/notice/config/channels'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  items: ChannelConfig[]
  activeChannel: MessageChannel
}>()

const emit = defineEmits<{
  select: [id: MessageChannel]
}>()
</script>

<template>
  <ul class="channel-menu" role="tablist" aria-label="消息频道">
    <li
      v-for="item in items"
      :key="item.id"
      role="tab"
      :aria-selected="activeChannel === item.id"
      :class="['channel-menu__item', { 'channel-menu__item--active': activeChannel === item.id }]"
      @click="emit('select', item.id)"
    >
      <SIcon :icon="appIcons[item.icon]" :size="16" />
      <span class="channel-menu__label">{{ item.name }}</span>
      <span
        v-if="!item.available"
        class="channel-menu__badge"
        aria-hidden="true"
      >即将</span>
    </li>
  </ul>
</template>

<style scoped lang="scss">
.channel-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 8px;
}

.channel-menu__item {
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

.channel-menu__item:hover {
  color: var(--text-primary);
  border-color: var(--border-interactive-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-panel-soft);
}

.channel-menu__item--active {
  font-weight: 600;
  color: var(--primary-color);
  background: var(--surface-panel-chip-accent);
  border-color: var(--border-panel-badge-accent);
}

.channel-menu__item--active::before {
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

.channel-menu__label {
  flex: 1;
  min-width: 0;
}

.channel-menu__badge {
  flex-shrink: 0;
  padding: 0.15rem 0.45rem;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.06em;
  color: var(--text-secondary);
}
</style>
