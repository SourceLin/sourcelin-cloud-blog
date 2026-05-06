<template>
  <SCard class="tag-hero" variant="lite">
    <div class="tag-header-main">
      <div v-if="loadingTags && !tags.length" class="tag-hero__solo" aria-busy="true">
        <div class="tag-hero__copy">
          <span class="article-card-prelude-label">文库</span>
          <SSkeleton :sharp="false" class="tag-hero__sk-title" />
          <SSkeleton :sharp="false" class="tag-hero__sk-sub" />
          <section class="tag-hero__summary tag-hero__summary--skeleton" aria-hidden="true">
            <div v-for="sk in 3" :key="`tag-hero-sk-${sk}`" class="tag-hero__stat tag-hero__stat--skeleton">
              <SSkeleton circle class="tag-hero__sk-icon" />
              <div class="tag-hero__sk-stat-copy">
                <SSkeleton :sharp="false" class="tag-hero__sk-num" />
                <SSkeleton :sharp="false" class="tag-hero__sk-label" />
              </div>
            </div>
          </section>
        </div>
      </div>

      <div v-else-if="tags.length" class="tag-hero__main">
        <div class="tag-hero__copy">
          <span class="article-card-prelude-label">文库</span>
          <h1 class="tag-hero__title">文章标签</h1>
          <p class="tag-hero__subtitle">先选标签，再在同一页面继续浏览相关文章。</p>

          <section class="tag-hero__summary" aria-label="标签概览">
            <HeroStatCard
              v-for="tile in statTiles"
              :key="tile.label"
              class="tag-hero__stat"
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

        <aside class="tag-hero__aside" aria-label="当前标签">
          <div class="tag-hero__current-panel">
            <div class="tag-hero__current-glow" aria-hidden="true" />

            <div class="tag-hero__current-body">
              <div class="tag-hero__current-copy">
                <p class="tag-hero__current-eyebrow">{{ currentPanelEyebrow }}</p>
                <div class="tag-hero__current-heading">
                  <SGradientText as="h2" class="tag-hero__current-title" tone="glass-strong">
                    {{ featuredTag?.name || '待选标签' }}
                  </SGradientText>
                </div>
                <p class="tag-hero__current-lede">
                  {{ featuredTag ? resolveTagDescription(featuredTag) : '选择一个标签后，将在当前区域展示相关文章。' }}
                </p>
              </div>

              <div class="tag-hero__current-keywords">
                <span v-for="keyword in activeKeywords" :key="keyword" class="tag-hero__keyword">
                  {{ keyword }}
                </span>
              </div>
            </div>
          </div>
        </aside>
      </div>

      <div v-else class="tag-hero__solo">
        <div class="tag-hero__copy">
          <span class="article-card-prelude-label">文库</span>
          <h1 class="tag-hero__title">文章标签</h1>
          <p class="tag-hero__subtitle">从关键词进入内容索引，快速找到你想继续阅读的主题。</p>
        </div>
      </div>
    </div>
  </SCard>
</template>

<script setup lang="ts">
import type { TagHeroStatTile, TagItem } from '@/modules/article/composables/useTagPageQuery'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import SGradientText from '@/shared/components/ui/SGradientText.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'

interface Props {
  loadingTags: boolean
  tags: TagItem[]
  statTiles: TagHeroStatTile[]
  currentPanelEyebrow: string
  featuredTag: TagItem | null
  activeKeywords: string[]
  resolveTagDescription: (tag: TagItem) => string
}

defineProps<Props>()
</script>
