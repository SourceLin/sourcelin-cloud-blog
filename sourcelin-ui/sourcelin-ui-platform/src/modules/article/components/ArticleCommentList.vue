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
        <article v-for="comment in comments" :key="comment.id" class="comment-item">
          <img class="comment-avatar" :src="comment.avatar || defaultAvatar" alt="avatar">
          <div class="comment-body">
            <div class="comment-header">
              <div class="comment-author-info">
                <span class="comment-author">{{ comment.nickname || '匿名用户' }}</span>
                <SStatusTag v-if="authorId && authorId === comment.userId" type="primary">作者</SStatusTag>
              </div>
              <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-footer">
              <button class="comment-action" type="button" @click="emit('like', comment)">
                <SIcon :icon="appIcons.star" :size="16" />
                <span>{{ comment.likeCount || 0 }}</span>
              </button>
              <button class="comment-action" type="button" @click="emit('reply', comment)">
                <SIcon :icon="appIcons.arrowForward" :size="16" />
                <span>回复</span>
              </button>
            </div>
          </div>
        </article>
      </div>
      <EmptyState
        v-else
        variant="inline"
        size="small"
        title="暂无留言"
        message="欢迎成为第一位写下评论的读者。"
      />
    </div>

  <div v-if="commentTotal > commentPageSize" class="comment-pagination">
    <SPagination
      :page="commentPageNum"
      :page-size="commentPageSize"
      :total="commentTotal"
      @update:page="emit('update:page', $event)"
    />
  </div>
</template>

<script setup lang="ts">
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SStatusTag from '@/shared/components/ui/SStatusTag.vue'
import { appIcons } from '@/shared/components/ui/icons'
import defaultAvatar from '@/assets/images/logo/logo.png'
import type { ArticleCommentItem } from '@/modules/article/composables/useArticleComments'

defineProps<{
  comments: ArticleCommentItem[]
  commentLoading: boolean
  commentTotal: number
  commentPageNum: number
  commentPageSize: number
  authorId?: number
  formatDate: (value?: string) => string
}>()

const emit = defineEmits<{
  like: [comment: ArticleCommentItem]
  reply: [comment: ArticleCommentItem]
  'update:page': [page: number]
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.comment-items { display: flex; flex-direction: column; }
.comment-item--skeleton { pointer-events: none; }
.comment-sk-avatar {
  width: 52px !important;
  height: 52px !important;
}
.comment-sk-author {
  width: 6.5rem !important;
  height: 0.95rem !important;
  border-radius: 999px !important;
}
.comment-sk-date {
  width: 4rem !important;
  height: 0.75rem !important;
}
.comment-sk-line {
  width: 100% !important;
  height: 0.85rem !important;
}
.comment-body .comment-sk-line + .comment-sk-line {
  width: 88% !important;
}
.comment-footer--skeleton {
  margin-top: 12px;
}
.comment-sk-action {
  width: 4rem !important;
  height: 1.75rem !important;
  border-radius: 999px !important;
}
.comment-item { display: grid; grid-template-columns: 52px minmax(0, 1fr); gap: 16px; padding: 22px 0; border-bottom: 1px solid var(--border-color); }
.comment-item:last-child { border-bottom: none; }
.comment-avatar { width: 52px; height: 52px; border-radius: 50%; object-fit: cover; }
.comment-header { display: flex; align-items: center; justify-content: space-between; gap: 14px; margin-bottom: 10px; }
.comment-author-info { display: flex; align-items: center; gap: 10px; }
.comment-date { color: var(--text-secondary); }
.comment-content { color: var(--text-primary); line-height: 1.9; }
.comment-footer { display: flex; align-items: center; gap: 18px; margin-top: 12px; }
.comment-action { display: inline-flex; align-items: center; gap: 6px; color: var(--text-secondary); cursor: pointer; border: none; background: none; }
.comment-pagination { display: flex; justify-content: center; padding-top: 20px; margin-top: 10px; border-top: 1px solid var(--border-color); }
@include sourcelin-down(md) { .comment-item { grid-template-columns: 1fr; gap: 12px; } .comment-avatar { width: 48px; height: 48px; } .comment-header { display: flex; flex-direction: column; align-items: flex-start; } }
</style>
