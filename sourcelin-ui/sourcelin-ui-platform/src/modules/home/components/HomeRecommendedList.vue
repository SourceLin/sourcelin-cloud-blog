<template>
  <div class="recommended-list">
    <article
      v-for="(article, index) in articles"
      :key="article.id"
      class="recommended-item"
      :class="{ 'is-featured': index === 0 }"
      @click="emit('open', article.id)"
    >
      <div class="recommended-image">
        <img :src="article.image" :alt="article.title">
      </div>

      <div class="recommended-info">
        <div class="recommended-topline">
          <div class="recommended-source">
            <span class="source-label">{{ article.authorName }}</span>
            <span class="source-divider" />
            <span class="source-copy">精选</span>
          </div>

          <span v-if="article.featured" class="featured-badge">精选</span>
        </div>

        <h3>{{ article.title }}</h3>

        <div class="recommended-meta">
          <span>{{ article.date }}</span>
          <span>{{ article.views }} 阅读</span>
        </div>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import type { HomeArticle } from '@/modules/home/model/home-discover'

defineProps<{ articles: HomeArticle[] }>()

const emit = defineEmits<{ open: [id: number] }>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.recommended-list {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  width: 100%;
  min-width: 0;
}

.recommended-item {
  position: relative;
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 0.9rem;
  padding: 0.9rem;
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
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
  overflow: hidden;
}

.recommended-item:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.recommended-item.is-featured {
  grid-template-columns: minmax(180px, 0.95fr) minmax(0, 1.05fr);
  gap: 1rem;
  padding: 1rem;
  background: var(--surface-content-card-featured);
  background-blend-mode: normal, normal, overlay;
  background-repeat: no-repeat;
  border-color: var(--border-content-card-featured);
  box-shadow:
    var(--highlight-panel-strong),
    var(--shadow-content-card-hover);
}

.recommended-image {
  position: relative;
  width: 96px;
  height: 74px;
  border-radius: 0.85rem;
  overflow: hidden;
  box-shadow: var(--highlight-panel-soft);
}

.recommended-image::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: 28%;
  background: var(--content-readable-scrim);
  pointer-events: none;
}

.recommended-item.is-featured .recommended-image {
  width: 100%;
  height: 100%;
  min-height: 148px;
}

.recommended-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.recommended-item:hover .recommended-image img {
  transform: scale(1.04);
}

.recommended-info {
  display: grid;
  gap: 0.45rem;
  min-width: 0;
}

.recommended-topline,
.recommended-source,
.recommended-meta {
  display: flex;
}

.recommended-topline {
  align-items: center;
  justify-content: space-between;
  gap: 0.65rem;
}

.recommended-source {
  align-items: center;
  gap: 0.4rem;
  min-width: 0;
  color: var(--text-secondary);
  font-size: 0.72rem;
  position: relative;
  z-index: 1;
}

.source-label {
  font-weight: 700;
  color: var(--text-primary);
}

.source-divider {
  width: 0.9rem;
  height: 1px;
  background: var(--border-panel-default);
}

.recommended-info h3 {
  margin: 0;
  font-size: 1rem;
  line-height: 1.45;
  color: var(--title-color);
  text-shadow: var(--text-shadow-glass);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.recommended-item.is-featured .recommended-info h3 {
  font-size: 1.08rem;
}

.recommended-meta {
  flex-wrap: wrap;
  gap: 0.55rem;
  font-size: 0.74rem;
  color: var(--text-secondary);
  position: relative;
  z-index: 1;
}

.featured-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.24rem 0.55rem;
  border-radius: 999px;
  background: var(--surface-panel-chip-accent);
  border: 1px solid var(--border-panel-badge-accent);
  color: var(--primary-color);
  font-size: 0.68rem;
  font-weight: 700;
}

@include sourcelin-down(md) {
  .recommended-item,
  .recommended-item.is-featured {
    grid-template-columns: 84px minmax(0, 1fr);
  }

  .recommended-item.is-featured .recommended-image {
    min-height: 120px;
  }
}

@include sourcelin-down(sm) {
  .recommended-list {
    gap: 0.72rem;
  }

  .recommended-item,
  .recommended-item.is-featured {
    grid-template-columns: 76px minmax(0, 1fr);
    gap: 0.72rem;
    padding: 0.8rem;
  }

  .recommended-image {
    width: 76px;
    height: 68px;
  }

  .recommended-item.is-featured .recommended-image {
    min-height: 0;
  }

  .recommended-topline {
    gap: 0.45rem;
  }

  .recommended-info {
    gap: 0.35rem;
  }

  .recommended-info h3,
  .recommended-item.is-featured .recommended-info h3 {
    font-size: 0.94rem;
    line-height: 1.38;
  }

  .recommended-meta {
    gap: 0.42rem;
    font-size: 0.7rem;
  }
}
</style>
