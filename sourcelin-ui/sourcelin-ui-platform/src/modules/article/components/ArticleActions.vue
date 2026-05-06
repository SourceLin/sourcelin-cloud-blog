<template>
  <SCard class="editorial-notes" variant="lite" hoverable>
    <div class="notes-heading">
      <SSurfaceChip class="article-card-prelude-label" tone="accent">编辑部注脚</SSurfaceChip>
      <h2 class="notes-title">把这篇特稿留在你的阅读轨迹里</h2>
    </div>
    <div class="notes-grid">
      <SSurfacePanel tag="button" class="note-card note-card--like" variant="inset" interactive type="button" :class="{ active: isLiked }" @click="emit('like')">
        <span class="note-icon"><SIcon :icon="isLiked ? appIcons.heartFilled : appIcons.heart" :size="18" /></span>
        <span class="note-copy">
          <strong>{{ isLiked ? '已点赞' : '点赞' }}</strong>
          <span>{{ likeCount }} 位读者留下认可</span>
        </span>
      </SSurfacePanel>
      <SSurfacePanel tag="button" class="note-card note-card--collect" variant="inset" interactive type="button" :class="{ active: isCollected }" @click="emit('collect')">
        <span class="note-icon"><SIcon :icon="appIcons.star" :size="18" /></span>
        <span class="note-copy">
          <strong>{{ isCollected ? '已收藏' : '收藏' }}</strong>
          <span>{{ collectCount }} 次加入个人书架</span>
        </span>
      </SSurfacePanel>
      <SSurfacePanel tag="button" class="note-card note-card--comment" variant="inset" interactive type="button" :class="{ active: commentsOpen }" @click="emit('comment')">
        <span class="note-icon"><SIcon :icon="appIcons.comment" :size="18" /></span>
        <span class="note-copy">
          <strong>{{ commentsOpen ? '收起评论' : '评论' }}</strong>
          <span>{{ commentCount }} 封读者来信</span>
        </span>
      </SSurfacePanel>
      <SSurfacePanel tag="button" class="note-card note-card--share" variant="inset" interactive type="button" @click="emit('share')">
        <span class="note-icon"><SIcon :icon="appIcons.external" :size="18" /></span>
        <span class="note-copy">
          <strong>分享</strong>
          <span>复制链接，发给下一位读者</span>
        </span>
      </SSurfacePanel>
    </div>
  </SCard>
</template>

<script setup lang="ts">
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  isLiked: boolean
  isCollected: boolean
  likeCount: number
  collectCount: number
  commentCount: number
  commentsOpen: boolean
}>()

const emit = defineEmits<{
  like: []
  collect: []
  comment: []
  share: []
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.editorial-notes { padding: var(--spacing-xxl); }
.notes-heading {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--border-panel-subtle);
}
.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
}
.notes-title {
  margin: 0;
  color: var(--title-color);
  font-size: var(--font-size-xl);
  font-weight: 700;
  line-height: 1.3;
}
.notes-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: var(--spacing-lg); margin-top: 18px; }
.note-card { display: flex; align-items: center; gap: var(--spacing-lg); padding: var(--spacing-lg); cursor: pointer; text-align: left; border-radius: calc(var(--glass-radius) - 2px); transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base), background var(--transition-base); will-change: transform, box-shadow; }
.note-card:hover { transform: translateY(-4px) scale(1.01); box-shadow: var(--shadow-panel-hover); border-color: var(--border-interactive-hover); background: var(--surface-panel-soft); }
.note-card:active { transform: translateY(1px); }
.note-card--like.active { border-color: color-mix(in srgb, var(--error-color) 36%, var(--border-interactive-active)); box-shadow: var(--shadow-panel-hover), var(--highlight-panel-chip); }
.note-card--collect.active { border-color: color-mix(in srgb, var(--warning-color) 36%, var(--border-interactive-active)); box-shadow: var(--shadow-panel-hover), var(--highlight-panel-chip); }
.note-card--comment.active { border-color: color-mix(in srgb, var(--primary-color) 36%, var(--border-interactive-active)); box-shadow: var(--shadow-panel-hover), var(--highlight-panel-chip); }
.note-icon { display: inline-flex; width: 44px; height: 44px; align-items: center; justify-content: center; border-radius: var(--border-radius-md); background: var(--surface-panel-chip-accent); box-shadow: var(--highlight-panel-chip); color: var(--text-secondary); transition: color var(--transition-base), background var(--transition-base); }
.note-card--like:hover .note-icon,
.note-card--like.active .note-icon {
  color: var(--error-color);
  background: color-mix(in srgb, var(--error-color) 10%, var(--surface-panel-chip-accent));
}
.note-card--collect:hover .note-icon,
.note-card--collect.active .note-icon {
  color: var(--warning-color);
  background: color-mix(in srgb, var(--warning-color) 14%, var(--surface-panel-chip-accent));
}
.note-card--comment:hover .note-icon,
.note-card--comment.active .note-icon {
  color: var(--primary-color);
  background: color-mix(in srgb, var(--primary-color) 10%, var(--surface-panel-chip-accent));
}
.note-copy { display: flex; flex-direction: column; gap: 4px; }
@include sourcelin-down(lg) { .notes-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@include sourcelin-down(md) { .editorial-notes { padding: 22px; } .notes-grid { grid-template-columns: 1fr; } }
</style>
