<template>
  <div v-if="items.length" :key="refreshKey" class="notice-list">
    <article
      v-for="(item, index) in items"
      :key="item.id"
      class="notice-item"
    >
      <div class="notice-user-info">
        <div class="system-avatar">
          <SIcon :icon="appIcons.notice" :size="20" />
        </div>

        <div class="user-details">
          <span class="user-nickname">系统公告</span>
          <div class="user-meta">
            <span class="meta-item">{{ item.createTimeStr }}</span>
          </div>
        </div>

        <button class="delete-btn" type="button" aria-label="移除" @click="emit('clearItem', item.id, index)">
          <SIcon :icon="appIcons.trash" :size="16" />
        </button>
      </div>

      <div class="notice-content-box">
        <h4 class="notice-title">{{ item.title }}</h4>
        <div class="notice-text-content" v-html="item.content" />
      </div>
    </article>
  </div>

  <div v-else-if="!loading" class="notice-empty-state">
    <EmptyState
      variant="section"
      icon="notice"
      title="暂无系统公告"
      message="这里暂时没有新公告，晚点再来看看。"
    />
  </div>
</template>

<script setup lang="ts">
import type { NoticeViewItem } from '@/modules/notice/composables/useNoticeCenter'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  items: NoticeViewItem[]
  loading: boolean
  refreshKey: number
  centerTabType: number
  splitIpAddress: (address: string) => string
}>()

const emit = defineEmits<{
  clearItem: [id: number, index: number]
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.notice-item {
  padding: 16px;
}

.notice-user-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.system-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  min-width: 0;
  display: grid;
  gap: 6px;
}

.user-nickname {
  display: inline-block;
  font-weight: 600;
  color: var(--title-color);
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-item {
  min-height: 26px;
  padding: 0 10px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.delete-btn {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-panel-subtle);
  border-radius: 12px;
  background: var(--surface-panel-soft);
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
}

.delete-btn:hover {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 38%, transparent);
  background: color-mix(in srgb, var(--error-color) 8%, var(--surface-panel-soft));
  transform: translateY(-1px);
}

.notice-content-box {
  display: grid;
  gap: 8px;
  margin-left: 60px;
  padding: 14px 16px;
  border-radius: 16px;
  background: var(--surface-panel-soft);
  border: 1px solid var(--border-panel-subtle);
  color: var(--text-primary);
}

.notice-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: var(--title-color);
}

.notice-text-content {
  color: var(--text-primary);
  line-height: 1.8;
  font-size: 0.92rem;
}

.notice-empty-state {
  margin-top: var(--spacing-xs);
}

@include sourcelin-down(md) {
  .notice-content-box {
    margin-left: 0;
  }
}
</style>
