<template>
  <div>
    <div class="section-head">
      <div>
        <h2 id="tag-switch-title" class="section-title">全部标签</h2>
        <p class="section-copy">点击标签后，当前区域会直接切换为相关文章。</p>
      </div>
      <SSurfaceChip class="section-count" size="sm" variant="counter">{{ tags.length }} 个</SSurfaceChip>
    </div>

    <div class="tag-browser-stage">
      <FoldableOverflowBlock
        :item-count="tags.length"
        :expand-button-text="foldExpandLabel"
        collapse-button-text="收起标签列表"
        :get-scroll-anchor="getScrollAnchor"
      >
        <div class="tag-grid">
          <button
            v-for="tag in sortedTags"
            :key="tag.id"
            type="button"
            class="tag-grid-card"
            :class="{ 'is-armed': pendingTagId === tag.id }"
            :style="{ '--tag-accent': tag.color }"
            @click="$emit('select-tag', tag)"
          >
            <div class="tag-grid-head">
              <span class="tag-grid-name">{{ tag.name }}</span>
              <span class="tag-grid-badge">{{ tag.articleCount }} 篇</span>
            </div>
            <p class="tag-grid-desc">{{ resolveTagDescription(tag) }}</p>
            <div class="tag-grid-foot">
              <span class="tag-grid-alias">#{{ tag.name }}</span>
              <span class="tag-grid-views">{{ tag.views }} 热度</span>
            </div>
          </button>
        </div>
      </FoldableOverflowBlock>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TagItem } from '@/modules/article/composables/useTagPageQuery'
import FoldableOverflowBlock from '@/shared/components/layout/FoldableOverflowBlock.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'

interface Props {
  tags: TagItem[]
  sortedTags: TagItem[]
  pendingTagId: number | null
  foldExpandLabel: string
  getScrollAnchor: () => HTMLElement | null
  resolveTagDescription: (tag: TagItem) => string
}

defineProps<Props>()
defineEmits<{
  'select-tag': [tag: TagItem]
}>()
</script>
