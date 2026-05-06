<script setup lang="ts">
import CommentComposer from '@/shared/components/comments/CommentComposer.vue'
import CommentThread from '@/shared/components/comments/CommentThread.vue'
import type { CommentThreadItem } from '@/shared/composables/useContentComments'

withDefaults(defineProps<{
  panelId: string
  show: boolean
  isRich: boolean
  modelValue: string
  composerVisible: boolean
  currentUserAvatar: string
  commentPlaceholder: string
  isLoggedIn: boolean
  commentSubmitting: boolean
  isReplying: boolean
  comments: CommentThreadItem[]
  commentLoading: boolean
  commentTotal: number
  commentPageNum: number
  commentPageSize: number
  authorId?: number
  currentUserId?: number
  formatDate: (value?: string) => string
  enableLike: boolean
  enableReply: boolean
  showPagination: boolean
  commentSectionTitle: string
  headerCommentTotal: number
  commentSectionSummary: string
  canToggleComments: boolean
  commentToggleText: string
}>(), {
  authorId: undefined,
  currentUserId: undefined
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  toggleComments: []
  submitComment: []
  cancelReply: []
  like: [comment: CommentThreadItem]
  delete: [comment: CommentThreadItem]
  reply: [comment: CommentThreadItem]
  interactionBlocked: []
  updatePage: [page: number]
}>()
</script>

<template>
  <section v-if="show" :id="panelId" :class="['say-comments', { 'say-comments--rich': isRich }]">
    <div class="say-comments__header">
      <div class="say-comments__headline">
        <span class="say-comments__title">{{ commentSectionTitle }} {{ headerCommentTotal }}</span>
        <span class="say-comments__summary">
          {{ commentSectionSummary }}
        </span>
      </div>
      <button
        v-if="canToggleComments"
        type="button"
        class="say-comments__toggle"
        @click="emit('toggleComments')"
      >
        {{ commentToggleText }}
      </button>
    </div>

    <div v-if="composerVisible" class="say-comments__composer-wrap">
      <CommentComposer
        :model-value="modelValue"
        :current-user-avatar="currentUserAvatar"
        :placeholder="commentPlaceholder"
        :is-logged-in="isLoggedIn"
        :comment-submitting="commentSubmitting"
        :is-replying="isReplying"
        @update:model-value="emit('update:modelValue', $event)"
        @submit="emit('submitComment')"
        @cancel-reply="emit('cancelReply')"
      />
    </div>

    <CommentThread
      class="say-comments__thread"
      :comments="comments"
      :comment-loading="commentLoading"
      :comment-total="commentTotal"
      :comment-page-num="commentPageNum"
      :comment-page-size="commentPageSize"
      :author-id="authorId"
      :current-user-id="currentUserId"
      :format-date="formatDate"
      :enable-like="enableLike"
      :enable-reply="enableReply"
      :enable-delete="true"
      :show-pagination="showPagination"
      anonymous-label="匿名用户"
      empty-text="还没有评论，来写下第一句吧。"
      @like="emit('like', $event)"
      @delete="emit('delete', $event)"
      @reply="emit('reply', $event)"
      @interaction-blocked="emit('interactionBlocked')"
      @update:page="emit('updatePage', $event)"
    />
  </section>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-comments {
  display: grid;
  gap: 14px;
  padding-top: 6px;
  border-top: 1px solid color-mix(in srgb, var(--border-panel-subtle) 86%, transparent);
}

.say-comments__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.say-comments__headline {
  display: flex;
  align-items: baseline;
  gap: 10px;
  flex-wrap: wrap;
}

.say-comments__title {
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.say-comments__summary {
  color: color-mix(in srgb, var(--say-strip) 62%, var(--text-secondary));
  font-size: 0.78rem;
  letter-spacing: 0.04em;
}

.say-comments__toggle {
  padding: 0;
  border: none;
  background: none;
  color: var(--primary-color);
  font-size: 0.84rem;
  cursor: pointer;
  font-family: inherit;
}

.say-comments__composer-wrap {
  margin-bottom: var(--spacing-md);
}

.say-comments__thread {
  margin-top: var(--spacing-sm);
}

.say-comments--rich {
  margin-top: 2px;
  padding: 18px;
  border: 1px solid color-mix(in srgb, var(--border-panel-subtle) 90%, transparent);
  border-radius: 26px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--surface-panel-soft) 94%, transparent),
    color-mix(in srgb, var(--surface-panel-inset) 88%, transparent)
  );
}

.say-comments--rich :deep(.comment-items) {
  gap: 12px;
}

.say-comments--rich :deep(.comment-item) {
  padding: 16px 18px;
  background: color-mix(in srgb, var(--surface-panel-default) 86%, transparent);
}

.say-comments--rich :deep(.comment-composer) {
  padding: 18px;
  border-radius: 24px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--surface-panel-strong) 90%, transparent),
    color-mix(in srgb, var(--surface-panel-soft) 88%, transparent)
  );
}

@include sourcelin-down(sm) {
  .say-comments__header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>