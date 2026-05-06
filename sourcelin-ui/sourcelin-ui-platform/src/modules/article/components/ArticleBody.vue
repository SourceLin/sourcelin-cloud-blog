<template>
  <SCard
    v-if="loading"
    class="story-card story-card--skeleton"
    variant="default"
    aria-busy="true"
    aria-label="正文加载中"
  >
    <div class="story-opener story-opener--skeleton">
      <SSkeleton :sharp="false" class="story-sk-label" />
      <SSurfacePanel class="story-opener-copy story-opener-copy--skeleton" variant="inset">
        <SSkeleton v-for="n in 3" :key="`op-sk-${n}`" :sharp="false" class="story-sk-line" />
      </SSurfacePanel>
    </div>
    <div class="article-content-shell article-content-shell--skeleton">
      <SSkeleton v-for="line in 8" :key="`body-sk-${line}`" :sharp="false" class="article-body-sk-line" />
    </div>
  </SCard>
  <SCard v-else class="story-card" variant="default">
    <div class="story-opener">
      <SSurfaceChip class="story-opener-label" tone="accent">特稿导语</SSurfaceChip>
      <SSurfacePanel class="story-opener-copy" variant="inset">
        <p class="story-opener-text">{{ summary }}</p>
      </SSurfacePanel>
    </div>
    <div class="article-content-shell">
      <div v-if="content" class="article-content" v-html="content" />
      <EmptyState v-else variant="inline" size="small" title="暂无内容" message="这篇文章还没有正文内容。" />
      <SSurfacePanel v-if="originalUrl" class="source-note" variant="inset">
        <SSurfaceChip class="source-note-label">来源</SSurfaceChip>
        <a :href="originalUrl" target="_blank" rel="noopener noreferrer">{{ originalUrl }}</a>
      </SSurfacePanel>
    </div>
  </SCard>
</template>

<script setup lang="ts">
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'

defineProps<{
  loading: boolean
  summary: string
  content?: string
  originalUrl?: string
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.story-card { padding: clamp(28px, 4vw, 54px); border-radius: calc(var(--glass-radius) + 2px); }
.story-opener { display: grid; grid-template-columns: 120px minmax(0, 1fr); gap: 22px; align-items: start; padding-bottom: 28px; border-bottom: 1px solid var(--border-panel-subtle); }
.story-opener--skeleton { align-items: stretch; }
.story-sk-label {
  width: 5.5rem !important;
  height: 1.75rem !important;
  border-radius: 999px !important;
  margin-top: 0.15rem;
}
.story-opener-copy--skeleton {
  display: grid;
  gap: var(--spacing-sm);
  min-height: 5rem;
}
.story-sk-line {
  width: 100% !important;
  height: 1.05rem !important;
}
.story-sk-line:last-child {
  width: 78% !important;
}
.article-content-shell--skeleton {
  display: grid;
  gap: var(--spacing-sm);
  padding-top: 30px;
}
.article-body-sk-line {
  width: 100% !important;
  height: 14px !important;
}
.article-body-sk-line:nth-child(3n) {
  width: 92% !important;
}
.story-opener-label { width: fit-content; padding: 0.34rem 0.72rem; margin-top: 0.15rem; }
.story-opener-copy { padding: 1rem 1.1rem; border-radius: calc(var(--border-radius-md) + 4px); }
.story-opener-text { margin: 0; font-size: clamp(20px, 2vw, 30px); line-height: 1.7; color: var(--title-color); }
.article-content-shell { padding-top: 30px; }
.article-content { font-size: var(--font-size-lg); line-height: 2; color: var(--text-primary); }
.article-content :deep(h1), .article-content :deep(h2), .article-content :deep(h3), .article-content :deep(h4) { color: var(--title-color); line-height: 1.2; margin-top: 52px; margin-bottom: 18px; scroll-margin-top: 110px; }
.article-content :deep(p) { margin: 0 0 1.35em; text-align: justify; }
.article-content :deep(img) { display: block; max-width: 100%; margin: 30px auto; border-radius: var(--border-radius-md); }
.article-content :deep(pre) { margin: 24px 0; padding: var(--spacing-xl); overflow-x: auto; background: var(--code-block-bg); border-radius: var(--border-radius-md); }
.source-note { display: flex; flex-direction: column; gap: 12px; margin-top: 32px; padding: 1rem 1.1rem; border-radius: calc(var(--border-radius-md) + 4px); }
.source-note-label { width: fit-content; padding: 0.28rem 0.62rem; }
.source-note a { color: var(--primary-color); word-break: break-all; text-decoration: none; }
@include sourcelin-down(md) { .story-card { padding: 22px; } .story-opener { display: flex; flex-direction: column; align-items: flex-start; } }
</style>
