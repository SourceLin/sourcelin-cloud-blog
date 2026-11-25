<script setup lang="ts">
import type { CategoryHeroStatTile } from '@/modules/article/composables/useCategoryPageQuery'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import SCard from '@/shared/components/ui/SCard.vue'

defineProps<{
  statTiles: CategoryHeroStatTile[]
}>()
</script>

<template>
  <SCard class="category-hero" variant="lite">
    <div class="category-header-main">
      <div class="category-hero__copy">
        <span class="article-card-prelude-label">文库</span>
        <h1 class="category-hero__title">文章分类</h1>
        <p class="category-hero__subtitle">先选分类，再在同一页面继续浏览对应文章。</p>

        <section class="category-hero__summary" aria-label="分类概览">
          <HeroStatCard
            v-for="tile in statTiles"
            :key="tile.label"
            class="category-hero__stat"
            mode="metric"
            tone="tinted"
            size="lg"
            :icon="tile.icon"
            :value="tile.value"
            :label="tile.label"
            :hue="tile.hue"
            :aria-label="tile.ariaLabel"
          />
        </section>
      </div>
    </div>
  </SCard>
</template>
