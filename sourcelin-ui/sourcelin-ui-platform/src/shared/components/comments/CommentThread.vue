<template>
  <div class="comment-list">
    <div
      v-if="commentLoading"
      class="comment-items comment-items--skeleton"
      aria-busy="true"
      aria-label="评论加载中"
    >
      <article v-for="i in 3" :key="`cmt-sk-${i}`" class="comment-item comment-item--skeleton">
        <SSkeleton circle class="comment-sk-avatar" />
        <div class="comment-body">
          <div class="comment-header">
            <SSkeleton :sharp="false" class="comment-sk-author" />
            <SSkeleton :sharp="false" class="comment-sk-date" />
          </div>
          <SSkeleton v-for="r in 2" :key="`cmt-sk-line-${i}-${r}`" :sharp="false" class="comment-sk-line" />
          <div class="comment-footer comment-footer--skeleton">
            <SSkeleton :sharp="false" class="comment-sk-action" />
            <SSkeleton :sharp="false" class="comment-sk-action" />
          </div>
        </div>
      </article>
    </div>
    <div v-else-if="comments.length" class="comment-items">
        <article
          v-for="comment in comments"
          :key="comment.id"
          :class="['comment-item', moderationRowClass(comment)]"
        >
          <img class="comment-avatar" :src="comment.avatar || defaultAvatar" alt="avatar">
          <div class="comment-body">
            <div class="comment-header">
              <div class="comment-author-info">
                <span class="comment-author">{{ resolveNickname(comment.nickname) }}</span>
                <SStatusTag v-if="comment.status === 0 || comment.status === 2" :type="tagOf(comment.status)">{{ labelOf(comment.status) }}</SStatusTag>
                <SStatusTag v-if="authorId && authorId === comment.userId" type="primary">作者</SStatusTag>
              </div>
              <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div v-if="enableLike || enableReply || enableDelete" class="comment-footer">
              <template v-if="rowInteractAllowed(comment)">
                <button
                  v-if="enableLike"
                  class="comment-action comment-action--like"
                  :class="{ 'is-active': isLiked(comment) }"
                  type="button"
                  @click="emit('like', comment)"
                >
                  <SIcon :icon="isLiked(comment) ? appIcons.heartFilled : appIcons.heart" :size="16" />
                  <span>{{ comment.likeCount || 0 }}</span>
                </button>
                <button v-if="enableReply" class="comment-action comment-action--reply" type="button" @click="emit('reply', comment)">
                  <SIcon :icon="appIcons.arrowForward" :size="16" />
                  <span>回复</span>
                </button>
              </template>
              <button
                v-else-if="enableLike || enableReply"
                type="button"
                class="comment-action comment-action--blocked"
                @click="emit('interaction-blocked')"
              >
                互动暂不可用
              </button>
              <SPopconfirm
                v-if="canDeleteRow(comment)"
                :show-icon="false"
                positive-text="确认删除"
                negative-text="取消"
                @positive-click="emit('delete', comment)"
              >
                <template #trigger>
                  <button type="button" class="comment-action comment-action--danger" @click.stop>
                    <SIcon :icon="appIcons.trash" :size="15" />
                    <span>删除</span>
                  </button>
                </template>
                删除后不可恢复，确定继续吗？
              </SPopconfirm>
            </div>

            <div v-if="comment.replies?.length" class="reply-items">
              <SSurfacePanel
                v-for="reply in comment.replies"
                :key="reply.id"
                tag="article"
                variant="inset"
                class="reply-item"
              >
                <div class="reply-header">
                  <div class="reply-author-info">
                    <span class="reply-author">{{ resolveNickname(reply.nickname) }}</span>
                    <SStatusTag v-if="reply.status === 0 || reply.status === 2" :type="tagOf(reply.status)">{{ labelOf(reply.status) }}</SStatusTag>
                    <span v-if="reply.parentNickname" class="reply-target">回复 {{ resolveNickname(reply.parentNickname) }}</span>
                  </div>
                  <span class="reply-date">{{ formatDate(reply.createTime) }}</span>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div v-if="enableLike || enableReply || enableDelete" class="comment-footer reply-footer">
                  <template v-if="rowInteractAllowed(reply)">
                    <button
                      v-if="enableLike"
                      class="comment-action comment-action--like"
                      :class="{ 'is-active': isLiked(reply) }"
                      type="button"
                      @click="emit('like', reply)"
                    >
                      <SIcon :icon="isLiked(reply) ? appIcons.heartFilled : appIcons.heart" :size="15" />
                      <span>{{ reply.likeCount || 0 }}</span>
                    </button>
                    <button v-if="enableReply" class="comment-action comment-action--reply" type="button" @click="emit('reply', reply)">
                      <SIcon :icon="appIcons.arrowForward" :size="15" />
                      <span>回复</span>
                    </button>
                  </template>
                  <button
                    v-else-if="enableLike || enableReply"
                    type="button"
                    class="comment-action comment-action--blocked"
                    @click="emit('interaction-blocked')"
                  >
                    互动暂不可用
                  </button>
                  <SPopconfirm
                    v-if="canDeleteRow(reply)"
                    :show-icon="false"
                    positive-text="确认删除"
                    negative-text="取消"
                    @positive-click="emit('delete', reply)"
                  >
                    <template #trigger>
                      <button type="button" class="comment-action comment-action--danger" @click.stop>
                        <SIcon :icon="appIcons.trash" :size="14" />
                        <span>删除</span>
                      </button>
                    </template>
                    删除后不可恢复，确定继续吗？
                  </SPopconfirm>
                </div>
              </SSurfacePanel>
            </div>
          </div>
        </article>
      </div>
      <div v-else class="comment-empty">
        <p>{{ emptyText }}</p>
      </div>
    </div>

  <div v-if="showPagination && commentTotal > commentPageSize" class="comment-pagination">
    <div class="comment-pagination__summary" aria-live="polite">
      <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
      <span class="comment-pagination__dot" aria-hidden="true" />
      <span>共 {{ commentTotal }} 条</span>
    </div>
    <SPagination
      :page="commentPageNum"
      :page-size="commentPageSize"
      :item-count="commentTotal"
      @update:page="emit('update:page', $event)"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SPopconfirm from '@/shared/components/ui/SPopconfirm.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import SStatusTag from '@/shared/components/ui/SStatusTag.vue'
import { appIcons } from '@/shared/components/ui/icons'
import defaultAvatar from '@/assets/images/logo/logo.png'
import { useDict } from '@/shared/composables/useDict'
import type { CommentThreadItem } from '@/shared/composables/useContentComments'

const props = withDefaults(defineProps<{
  comments: CommentThreadItem[]
  commentLoading: boolean
  commentTotal: number
  commentPageNum: number
  commentPageSize: number
  authorId?: number
  currentUserId?: number
  emptyText?: string
  anonymousLabel?: string
  formatDate: (value?: string) => string
  enableLike?: boolean
  enableReply?: boolean
  enableDelete?: boolean
  /** 为 false 时不展示底部分页（如说说卡片折叠预览） */
  showPagination?: boolean
}>(), {
  emptyText: '暂无评论，欢迎写下第一条。',
  anonymousLabel: '匿名洞友',
  enableLike: true,
  enableReply: true,
  enableDelete: false,
  showPagination: true
})

const emit = defineEmits<{
  like: [comment: CommentThreadItem]
  reply: [comment: CommentThreadItem]
  delete: [comment: CommentThreadItem]
  'update:page': [page: number]
  'interaction-blocked': []
}>()

const { labelOf, tagOf } = useDict('blog_comment_audit_status')
const resolveNickname = (nickname?: string) => nickname?.trim() || props.anonymousLabel
const totalPages = computed(() => Math.max(Math.ceil(props.commentTotal / Math.max(props.commentPageSize, 1)), 1))
const currentPage = computed(() => Math.min(Math.max(props.commentPageNum, 1), totalPages.value))

function rowInteractAllowed(row: CommentThreadItem): boolean {
  const s = row.status
  return s === undefined || s === 1
}

function moderationRowClass(row: CommentThreadItem): string {
  const s = row.status
  if (s === 0 || s === 2) {
    return 'comment-item--moderation'
  }
  return ''
}

function canDeleteRow(row: CommentThreadItem): boolean {
  if (!props.enableDelete) {
    return false
  }
  if (props.currentUserId == null || row.userId == null) {
    return false
  }
  return props.currentUserId === row.userId
}

function isLiked(row: CommentThreadItem): boolean {
  return Boolean((row as CommentThreadItem & { likedByMe?: boolean }).likedByMe)
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.comment-items { display: flex; flex-direction: column; gap: var(--comment-thread-gap); }
.comment-item--skeleton { pointer-events: none; }
.comment-sk-avatar {
  width: 56px !important;
  height: 56px !important;
}
.comment-sk-author {
  width: 7rem !important;
  height: 1rem !important;
  border-radius: 999px !important;
}
.comment-sk-date {
  width: 4.5rem !important;
  height: 0.78rem !important;
}
.comment-sk-line {
  width: 100% !important;
  height: 0.88rem !important;
}
.comment-body .comment-sk-line + .comment-sk-line {
  width: 88% !important;
}
.comment-footer--skeleton {
  margin-top: 12px;
}
.comment-sk-action {
  width: 4.25rem !important;
  height: 1.85rem !important;
  border-radius: 999px !important;
}
.comment-item { display: grid; grid-template-columns: 56px minmax(0, 1fr); gap: var(--comment-thread-gap); padding: 1.1rem 0 1.3rem; border-bottom: 1px solid var(--border-panel-subtle); }
.comment-item:last-child { border-bottom: none; }
.comment-avatar { width: 56px; height: 56px; border-radius: 50%; object-fit: cover; border: 1px solid color-mix(in srgb, var(--primary-color) 16%, transparent); box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline); }
.comment-header, .reply-header { display: flex; align-items: center; justify-content: space-between; gap: 14px; margin-bottom: 10px; }
.comment-author-info, .reply-author-info { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.comment-date, .reply-date, .reply-target { color: var(--text-secondary); }
.comment-content, .reply-content { color: var(--text-reading); line-height: var(--reading-line-height-relaxed); }
.comment-footer { display: flex; align-items: center; gap: 10px; margin-top: 12px; flex-wrap: wrap; }
.comment-action {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 34px;
  padding: 0 0.78rem;
  color: var(--text-secondary);
  cursor: pointer;
  border: 1px solid var(--border-panel-subtle);
  background: color-mix(in srgb, var(--surface-panel-chip) 88%, transparent);
  border-radius: 999px;
  transition:
    color var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base),
    transform var(--transition-base),
    box-shadow var(--transition-base);
}
.comment-action:hover {
  color: var(--primary-color);
  border-color: var(--border-panel-default);
  background: var(--surface-panel-chip-accent);
  transform: translateY(-1px);
  box-shadow: var(--shadow-panel-inline);
}
.comment-action:active {
  transform: translateY(1px);
}
.comment-action--like:hover,
.comment-action--like.is-active {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 34%, var(--border-panel-default));
  background: color-mix(in srgb, var(--error-color) 10%, var(--surface-panel-chip-accent));
}
.comment-action--danger:hover {
  color: var(--error-color);
  background: color-mix(in srgb, var(--error-color) 12%, var(--surface-panel-chip-accent));
  border-color: color-mix(in srgb, var(--error-color) 30%, var(--border-panel-default));
}
.comment-action--blocked { color: var(--text-placeholder); cursor: pointer; font-size: var(--font-size-base); }
.comment-item--moderation .comment-content { opacity: 0.92; }
.comment-item--moderation { border-left: 3px solid var(--border-color); padding-left: var(--spacing-md); margin-left: calc(-1 * var(--spacing-md)); }
.reply-items { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; padding-left: var(--comment-reply-indent); }
.reply-item { padding: 14px 16px; border-radius: calc(var(--border-radius-xl) - 2px); border: 1px solid var(--border-panel-subtle); background: var(--surface-panel-inset); box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline); }
.reply-footer { margin-top: 10px; }
.comment-empty { padding: 36px 0 22px; text-align: center; color: var(--text-secondary); background: var(--surface-panel-soft); border: 1px solid var(--border-panel-subtle); border-radius: calc(var(--border-radius-xl) + 4px); box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft); }
.comment-pagination { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; padding-top: 20px; margin-top: 10px; border-top: 1px solid var(--border-panel-subtle); }
.comment-pagination__summary { display: inline-flex; align-items: center; gap: 0.5rem; color: var(--text-secondary); font-size: 0.86rem; }
.comment-pagination__dot { width: 4px; height: 4px; border-radius: 999px; background: var(--border-panel-default); }
@include sourcelin-down(md) {
  .comment-item { grid-template-columns: 1fr; gap: 12px; }
  .comment-avatar { width: 48px; height: 48px; }
  .comment-header, .reply-header { display: flex; flex-direction: column; align-items: flex-start; }
  .reply-items { padding-left: 0; }
  .comment-pagination { justify-content: center; }
  .comment-pagination__summary { width: 100%; justify-content: center; }
}
</style>
