<template>
  <div class="content-section" :aria-busy="loading">
    <div v-if="loading" class="list-loading" aria-live="polite">
      <SkeletonUserList :count="4" />
    </div>
    <div v-else class="content-body">
      <div class="user-list">
        <div v-if="items.length" class="users">
          <SSurfacePanel
            v-for="item in items"
            :key="item.id"
            class="user-item"
            variant="default"
            interactive
          >
            <img
              :src="item.user?.avatar || defaultAvatar"
              class="user-avatar"
              alt="用户头像"
              @click="emit('open', item.user?.id)"
            >
            <div class="user-info">
              <h4 class="user-name" @click="emit('open', item.user?.id)">{{ item.user?.nickName || '用户' }}</h4>
              <p class="user-intro">{{ item.user?.introduction || '暂无简介' }}</p>
            </div>
          </SSurfacePanel>
        </div>
        <div v-else class="empty-tip">还没有粉丝，继续创作吸引更多关注吧。</div>
        <div v-if="showPagination" class="comment-pagination">
          <div class="comment-pagination__summary" aria-live="polite">
            <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
            <span class="comment-pagination__dot" aria-hidden="true" />
            <span>共 {{ total }} 条</span>
          </div>
          <SPagination
            :page="page"
            :page-size="size"
            :item-count="total"
            @update:page="emit('update:page', $event)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import defaultAvatar from '@/assets/images/logo/logo.png'
import SkeletonUserList from '@/shared/components/feedback/skeletons/SkeletonUserList.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import type { UserFanItem } from '@/modules/user/composables/useUserCenter'

const props = defineProps<{ loading: boolean; items: UserFanItem[]; page: number; size: number; total: number }>()
const emit = defineEmits<{ open: [userId: number | undefined]; 'update:page': [page: number] }>()
const totalPages = computed(() => Math.max(Math.ceil(props.total / Math.max(props.size, 1)), 1))
const currentPage = computed(() => Math.min(Math.max(props.page, 1), totalPages.value))
const showPagination = computed(() => props.total > props.size)
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.content-section {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.content-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.user-list,
.users {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-list {
  flex: 1;
  min-height: 0;
}

.list-loading {
  display: grid;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.user-item:hover {
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  cursor: pointer;
  border: 1px solid var(--border-panel-badge-accent);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 1rem;
  font-weight: 700;
  margin: 0 0 4px;
  color: var(--title-color);
  cursor: pointer;
}

.user-intro {
  font-size: 0.86rem;
  color: var(--text-secondary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-tip {
  text-align: center;
  color: var(--text-secondary);
  padding: 28px;
  font-size: 0.95rem;
}

.comment-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--border-panel-subtle);
}

.comment-pagination__summary {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-secondary);
  font-size: 0.86rem;
}

.comment-pagination__dot {
  width: 4px;
  height: 4px;
  border-radius: 999px;
  background: var(--border-panel-default);
}

@include sourcelin-down(sm) {
  .comment-pagination {
    justify-content: center;
  }

  .comment-pagination__summary {
    width: 100%;
    justify-content: center;
  }
}
</style>
