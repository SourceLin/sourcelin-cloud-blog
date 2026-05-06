<template>
  <section
    ref="sectionRef"
    class="articles-section"
    :class="{ 'articles-section--switching': switching }"
    :aria-busy="loading || switching"
  >
    <div v-if="loading && !articles.length" class="articles-loading" aria-live="polite">
      <SkeletonArticleCard variant="featured" :count="1" />
      <WaterfallList
        :items="[]"
        :column-width="waterfallColumnWidth"
        :gap="waterfallGap"
        :loading="true"
        :loading-skeleton-count="6"
        loading-skeleton-variant="card"
        loading-text="正在整理文章版面"
      />
    </div>

    <template v-else>
      <div class="articles-stage">
        <div v-if="switching && articles.length" class="articles-switch-veil">
          <InlineLoadingState
            class="articles-switch-indicator"
            title="正在切换分类"
            description="马上为你更新当前内容"
          />
        </div>

        <article
          v-if="featuredArticle"
          class="featured-article"
          @click="emit('open-article', featuredArticle.id)"
        >
          <div class="featured-image">
            <img :src="featuredArticle.image" :alt="featuredArticle.title">
          </div>

          <div class="featured-content">
            <div class="featured-source">
              <span class="source-author">{{ featuredArticle.authorName }}</span>
              <span class="source-label">最新发布</span>
            </div>

            <h2>{{ featuredArticle.title }}</h2>
            <p class="featured-summary">{{ featuredArticle.summary }}</p>

            <div v-if="featuredArticle.tags.length" class="featured-tags">
              <span
                v-for="tag in featuredArticle.tags"
                :key="tag"
                class="tag"
                @click.stop="emit('open-tag', tag)"
              >
                {{ tag }}
              </span>
            </div>

            <div class="featured-meta">
              <span>{{ featuredArticle.date }}</span>
              <span>{{ featuredArticle.views }} 阅读</span>
              <span>{{ featuredArticle.comments }} 评论</span>
            </div>
          </div>
        </article>

        <WaterfallList
          v-if="secondaryArticles.length > 0"
          :items="secondaryArticles"
          :column-width="waterfallColumnWidth"
          :gap="waterfallGap"
          :loading="loading"
          :loading-more="loadingMore"
          loading-more-mode="quiet"
          :finished="finished"
          load-more-text="继续浏览更多文章"
          @load-more="emit('load-more')"
        >
          <template #default="{ item, heightType }">
            <article
              :key="asArticle(item).id"
              class="article-card"
              :class="heightType"
              @click="emit('open-article', asArticle(item).id)"
            >
              <div class="article-image">
                <img :src="asArticle(item).image" :alt="asArticle(item).title">
              </div>

              <div class="article-content">
                <div class="article-source">
                  <span class="source-author">{{ asArticle(item).authorName }}</span>
                  <span class="source-label">文章</span>
                </div>

                <h3>{{ asArticle(item).title }}</h3>
                <p class="summary">{{ asArticle(item).summary }}</p>

                <div v-if="asArticle(item).tags.length" class="article-tags">
                  <span
                    v-for="tag in asArticle(item).tags"
                    :key="tag"
                    class="tag"
                    @click.stop="emit('open-tag', tag)"
                  >
                    {{ tag }}
                  </span>
                </div>

                <div class="article-meta">
                  <span>{{ asArticle(item).date }}</span>
                  <span>{{ asArticle(item).views }} 阅读</span>
                  <span>{{ asArticle(item).comments }} 评论</span>
                </div>
              </div>
            </article>
          </template>
        </WaterfallList>

        <div v-else-if="!featuredArticle" class="articles-empty" role="status">
          <SIcon :icon="appIcons.document" :size="48" class="articles-empty__icon" />
          <h3 class="articles-empty__title">这个分类还没有文章</h3>
          <p class="articles-empty__description">换个分类看看，或者稍后再来刷一刷。</p>
        </div>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SkeletonArticleCard from '@/shared/components/feedback/skeletons/SkeletonArticleCard.vue'
import InlineLoadingState from '@/shared/components/feedback/InlineLoadingState.vue'
import WaterfallList from '@/shared/components/layout/WaterfallList.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import type { HomeArticle } from '@/modules/home/model/home-discover'

const props = withDefaults(defineProps<{
  loading: boolean
  switching: boolean
  loadingMore: boolean
  finished: boolean
  articles: HomeArticle[]
}>(), {
  switching: false
})

const emit = defineEmits<{
  'load-more': []
  'open-article': [id: number]
  'open-tag': [name: string]
}>()

const sectionRef = ref<HTMLElement | null>(null)
const { width: sectionWidth } = useElementSize(sectionRef)
const featuredArticle = computed(() => props.articles[0] ?? null)
const secondaryArticles = computed(() => props.articles.slice(1))
const waterfallColumnWidth = computed(() => {
  if (sectionWidth.value <= 0) return 330
  if (sectionWidth.value <= 640) return Math.max(Math.floor(sectionWidth.value - 6), 220)
  if (sectionWidth.value <= 768) return Math.max(Math.floor(sectionWidth.value - 18), 260)
  return 330
})
const waterfallGap = computed(() => (sectionWidth.value > 0 && sectionWidth.value <= 640 ? 14 : 20))

function asArticle(item: unknown): HomeArticle {
  return item as HomeArticle
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.articles-section {
  display: grid;
  width: 100%;
  min-width: 0;
  gap: clamp(1.2rem, 2.5vw, 1.6rem);
}

.articles-loading {
  display: grid;
  gap: clamp(1rem, 2vw, 1.35rem);
}

.articles-stage {
  position: relative;
  display: grid;
  gap: clamp(1rem, 2vw, 1.35rem);
}

.articles-section--switching .articles-stage {
  opacity: 0.92;
}

.articles-switch-veil {
  position: absolute;
  inset: 0;
  z-index: 4;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: color-mix(in srgb, var(--surface-panel-soft) 58%, transparent);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.articles-switch-indicator {
  pointer-events: none;
}

.featured-article {
  position: relative;
  display: grid;
  grid-template-columns: minmax(260px, 1.05fr) minmax(0, 0.95fr);
  gap: 1rem;
  padding: 1rem;
  border-radius: calc(var(--border-radius-xl) + 4px);
  border: 1px solid var(--border-content-card-featured);
  background: var(--surface-content-card-featured);
  background-blend-mode: normal, normal, overlay;
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  cursor: pointer;
  isolation: isolate;
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
  overflow: hidden;
}

.featured-article:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.featured-image {
  border-radius: 1rem;
  overflow: hidden;
  min-height: 280px;
  box-shadow: var(--highlight-panel-soft);
  position: relative;
}

.featured-image::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: 20%;
  background: linear-gradient(180deg, transparent, color-mix(in srgb, var(--glass-surface-strong) 20%, transparent));
  pointer-events: none;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.featured-article:hover .featured-image img {
  transform: scale(1.03);
}

.featured-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.85rem;
  min-width: 0;
  padding: 0.18rem 0.24rem 0.12rem;
}

.featured-source,
.featured-tags,
.featured-meta,
.article-source,
.article-tags,
.article-meta {
  display: flex;
  flex-wrap: wrap;
}

.featured-source,
.article-source {
  align-items: center;
  gap: 0.4rem;
  font-size: 0.76rem;
  color: var(--text-secondary);
  width: fit-content;
  padding: 0.3rem 0.58rem;
}

.source-author {
  font-weight: 700;
  color: var(--text-primary);
}

.source-label {
  color: var(--text-secondary);
}

.featured-content h2,
.article-content h3 {
  margin: 0;
  font-weight: 700;
  color: var(--title-color);
}

.featured-content h2 {
  font-size: clamp(1.4rem, 3vw, 2rem);
  line-height: 1.24;
}

.featured-summary,
.summary {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
}

.featured-summary {
  font-size: 0.95rem;
}

.featured-tags,
.article-tags {
  gap: 0.45rem;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.56rem;
  font-size: 0.72rem;
  color: var(--text-primary);
}

.featured-meta,
.article-meta {
  gap: 0.55rem;
  font-size: 0.74rem;
  color: var(--text-secondary);
}

.article-card {
  position: relative;
  display: grid;
  gap: 0.72rem;
  padding: 0.95rem;
  border-radius: calc(var(--border-radius-xl) + 2px);
  border: 1px solid var(--border-content-card);
  background: var(--surface-content-card);
  background-blend-mode: normal, normal, overlay;
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  cursor: pointer;
  isolation: isolate;
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
  overflow: hidden;
}

.article-card:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.article-card.short {
  min-height: 220px;
}

.article-card.medium {
  min-height: 300px;
}

.article-card.tall {
  min-height: 360px;
}

.article-image {
  border-radius: 0.95rem;
  overflow: hidden;
  aspect-ratio: 16 / 10;
  box-shadow: var(--highlight-panel-soft);
  position: relative;
}

.article-image::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: 22%;
  background: var(--content-readable-scrim);
  pointer-events: none;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.03);
}

.article-content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 0.55rem;
  min-width: 0;
  padding: 0.18rem 0.18rem 0.1rem;
}

.article-content h3 {
  font-size: 1rem;
  line-height: 1.45;
  text-shadow: var(--text-shadow-glass);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.summary {
  font-size: 0.88rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta,
.featured-meta {
  padding-top: 0.2rem;
}

.articles-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: clamp(3rem, 6vw, 4.5rem) clamp(2rem, 4vw, 3rem);
  text-align: center;
  min-height: 20rem;
}

.articles-empty__icon {
  color: var(--text-secondary);
  opacity: 0.48;
  margin-bottom: 0.5rem;
}

.articles-empty__title {
  margin: 0;
  color: var(--title-color);
  font-size: clamp(1.15rem, 2.5vw, 1.35rem);
  font-weight: 700;
}

.articles-empty__description {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: clamp(0.88rem, 1.8vw, 0.95rem);
  max-width: 28rem;
}

@media (max-width: 960px) {
  .featured-article {
    grid-template-columns: 1fr;
  }

  .featured-image {
    min-height: 220px;
  }
}

@include sourcelin-down(sm) {
  .articles-switch-veil {
    padding: 0.72rem;
  }

  .featured-article,
  .article-card {
    padding: 0.8rem;
  }

  .featured-article {
    gap: 0.82rem;
  }

  .featured-image {
    min-height: 180px;
  }

  .featured-content {
    gap: 0.68rem;
    padding: 0.08rem 0.04rem 0;
  }

  .featured-content h2 {
    font-size: clamp(1.16rem, 5.8vw, 1.46rem);
    line-height: 1.28;
  }

  .featured-summary {
    font-size: 0.88rem;
    line-height: 1.62;
  }

  .featured-tags,
  .article-tags,
  .featured-meta,
  .article-meta {
    gap: 0.42rem;
  }

  .article-content {
    gap: 0.48rem;
    padding: 0.12rem 0.06rem 0.04rem;
  }

  .article-card.short {
    min-height: 200px;
  }

  .article-card.medium {
    min-height: 260px;
  }

  .article-card.tall {
    min-height: 300px;
  }

  .article-content h3 {
    font-size: 0.94rem;
    line-height: 1.38;
  }

  .summary {
    font-size: 0.84rem;
    line-height: 1.58;
  }

  .tag,
  .featured-source,
  .article-source {
    font-size: 0.68rem;
  }
}

</style>
