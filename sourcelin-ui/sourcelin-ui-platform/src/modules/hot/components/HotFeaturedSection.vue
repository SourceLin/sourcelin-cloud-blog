<script setup lang="ts">
import type { HotItem } from '@/modules/hot/composables/useHotPage'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

defineProps<{
  featuredItems: HotItem[]
  getRankClass: (index: number) => string
  formatRank: (index: number, offset?: number) => string
  formatTime: (value: string) => string
  formatHeat: (value: number) => string
}>()

const emit = defineEmits<{
  openItem: [item: HotItem]
}>()
</script>

<template>
  <SSurfacePanel
    v-if="featuredItems.length"
    tag="section"
    class="hot-featured-section"
    variant="soft"
    aria-labelledby="hot-featured-title"
  >
    <div class="section-head hot-section-head">
      <div>
        <h2 id="hot-featured-title" class="section-title">热门推荐</h2>
        <p class="section-copy">这里会优先展示排名靠前的几条内容。</p>
      </div>
      <SSurfaceChip class="section-count" size="sm" variant="counter">
        Top {{ featuredItems.length }}
      </SSurfaceChip>
    </div>

    <div class="hot-featured-grid">
      <SSurfacePanel
        v-for="(item, index) in featuredItems"
        :key="`featured-${item.id}`"
        tag="article"
        class="featured-card"
        :class="{ 'featured-card--lead': index === 0 }"
        :variant="index === 0 ? 'strong' : 'default'"
        interactive
        @click="emit('openItem', item)"
      >
        <div class="featured-card__rail" aria-hidden="true" />
        <div class="featured-card__topline">
          <span class="featured-card__rank" :class="getRankClass(index)">
            {{ formatRank(index) }}
          </span>
          <span class="featured-card__time">{{ formatTime(item.createTime) }}</span>
        </div>

        <div
          v-if="item.coverImage"
          class="featured-card__cover"
          :style="{ '--featured-cover': `url(${item.coverImage})` }"
        />

        <div class="featured-card__body">
          <div class="featured-card__headline">
            <span class="featured-card__heat">热度 {{ formatHeat(item.viewCount) }}</span>
            <span class="featured-card__talk">{{ item.commentCount || 0 }} 条讨论</span>
          </div>
          <h3 class="featured-card__title">{{ item.title }}</h3>
          <p class="featured-card__summary">
            {{ item.summary || '这条内容正在被频繁阅读和讨论。' }}
          </p>
          <div class="featured-card__meta">
            <span class="featured-card__meta-chip">收藏 {{ item.likeCount }}</span>
            <span class="featured-card__meta-chip">互动 {{ item.commentCount || 0 }}</span>
          </div>
        </div>
      </SSurfacePanel>
    </div>
  </SSurfacePanel>
</template>
