<script setup lang="ts">
import { computed } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import type { ArticleTag } from '@/modules/article/composables/useArticleDetail'

const props = defineProps<{
  toc: { id: string; text: string; level: number }[]
  tags: ArticleTag[]
  getTocItemClass: (id: string, level: number) => string
}>()

const emit = defineEmits<{
  'scroll-to': [id: string]
  'go-tag': [tagName: string]
}>()

const displayTags = computed(() =>
  props.tags.filter((tag) => typeof tag.name === 'string' && tag.name.trim().length > 0)
)
</script>

<template>
  <SCard v-if="toc.length" class="sidebar-card toc-card" variant="lite" hoverable>
    <div class="card-heading">
      <SSurfaceChip class="article-card-prelude-label" tone="accent">目录导航</SSurfaceChip>
      <h3 class="visually-hidden">目录导航</h3>
    </div>
    <div class="toc-list">
      <a
        v-for="item in toc"
        :key="item.id"
        :href="`#${item.id}`"
        class="toc-item"
        :class="getTocItemClass(item.id, item.level)"
        @click.prevent="emit('scroll-to', item.id)"
      >
        {{ item.text }}
      </a>
    </div>
  </SCard>

  <SCard v-if="displayTags.length" class="sidebar-card tags-card" variant="lite" hoverable>
    <div class="card-heading">
      <SSurfaceChip class="article-card-prelude-label" tone="accent">专题索引</SSurfaceChip>
      <h2 class="visually-hidden">专题索引</h2>
    </div>
    <div class="sidebar-tags">
      <button
        v-for="tag in tags"
        :key="tag.id || tag.name"
        class="sidebar-tag"
        type="button"
        @click="emit('go-tag', tag.name!)"
      >
        <SIcon :icon="appIcons.tag" :size="14" />
        <span>{{ tag.name }}</span>
      </button>
    </div>
  </SCard>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.sidebar-card { padding: var(--spacing-xxl); }
.card-heading {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--border-panel-subtle);
}
.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
}
.toc-list { display: flex; flex-direction: column; gap: 6px; margin-top: 18px; }
.toc-item { display: block; padding: 10px var(--spacing-md); color: var(--text-secondary); text-decoration: none; border-radius: calc(var(--border-radius-sm) + 4px); transition: background var(--transition-base), color var(--transition-base), border-color var(--transition-base); border: 1px solid transparent; }
.toc-item.level-2 { padding-left: 22px; }
.toc-item.level-3,.toc-item.level-4 { padding-left: 30px; font-size: var(--font-size-base); }
.toc-item:hover,.toc-item.active { color: var(--primary-color); background: var(--surface-panel-inset); border-color: var(--border-interactive-hover); }
.sidebar-tags {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 18px;
}
.sidebar-tag { display: inline-flex; align-items: center; justify-content: center; width: 100%; gap: 6px; padding: var(--spacing-sm) var(--spacing-md); color: var(--primary-color); border-radius: 999px; background: var(--surface-panel-chip-accent); border: 1px solid var(--border-panel-badge-accent); box-shadow: var(--highlight-panel-chip); cursor: pointer; transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base); will-change: transform, box-shadow; }
.sidebar-tag:hover { transform: translateY(-2px) scale(1.02); box-shadow: var(--shadow-panel-soft), var(--highlight-panel-chip); }
@include sourcelin-down(md) { .sidebar-card { padding: 22px; } .sidebar-tags { grid-template-columns: 1fr; } }
</style>
