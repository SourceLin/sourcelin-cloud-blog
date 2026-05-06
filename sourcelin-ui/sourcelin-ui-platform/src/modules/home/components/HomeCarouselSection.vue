<template>
  <div class="carousel-shell">
    <SCarousel
      class="carousel-track"
      :autoplay="true"
      :interval="5200"
      dot-type="dot"
      dot-placement="right"
      trigger="hover"
      draggable
    >
      <article
        v-for="item in items"
        :key="item.id"
        class="carousel-slide"
        @click="emit('open', item.id)"
      >
        <div class="carousel-media">
          <img :src="item.image" :alt="item.title">
        </div>

        <div class="carousel-overlay" />

        <div class="carousel-frame">
          <div class="carousel-source">
            <span class="source-author">{{ item.authorName }}</span>
            <span v-if="item.featured" class="carousel-badge">焦点</span>
            <span class="source-label">精选</span>
          </div>

          <div class="carousel-copy">
            <h3>{{ item.title }}</h3>
            <p>{{ item.summary }}</p>

            <div v-if="item.tags.length" class="carousel-tags">
              <span v-for="tag in item.tags" :key="tag" class="tag-chip">{{ tag }}</span>
            </div>

            <div class="carousel-meta">
              <span>{{ item.date }}</span>
              <span>{{ item.views }} 阅读</span>
              <span>{{ item.comments }} 评论</span>
            </div>
          </div>
        </div>
      </article>
    </SCarousel>
  </div>
</template>

<script setup lang="ts">
import SCarousel from '@/shared/components/ui/SCarousel.vue'
import type { HomeArticle } from '@/modules/home/model/home-discover'

defineProps<{ items: HomeArticle[] }>()

const emit = defineEmits<{ open: [id: number] }>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.carousel-shell {
  position: relative;
  width: 100%;
  min-width: 0;
  overflow: hidden;
  border-radius: calc(var(--border-radius-xl) + 8px);
}

.carousel-shell::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 10%, transparent), transparent 54%),
    radial-gradient(circle at 82% 16%, color-mix(in srgb, var(--secondary-color) 8%, transparent), transparent 28%);
  pointer-events: none;
  z-index: 1;
}

.carousel-track {
  min-width: 0;
  position: relative;
  z-index: 2;
}

.carousel-slide {
  position: relative;
  min-height: clamp(360px, 42vw, 500px);
  overflow: hidden;
  cursor: pointer;
}

.carousel-media,
.carousel-media img,
.carousel-overlay {
  position: absolute;
  inset: 0;
}

.carousel-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.45s ease;
}

.carousel-slide:hover .carousel-media img {
  transform: scale(1.03);
}

.carousel-overlay {
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-black-10) 30%, transparent) 0%, color-mix(in srgb, var(--surface-black-50) 74%, transparent) 100%),
    linear-gradient(90deg, color-mix(in srgb, var(--surface-black-70) 64%, transparent) 0%, transparent 72%);
}

.carousel-frame {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 0.95rem;
  min-height: inherit;
  padding: clamp(1.35rem, 3vw, 2.1rem);
}

.carousel-frame::before {
  content: '';
  position: absolute;
  inset: auto clamp(0.95rem, 1.9vw, 1.35rem) clamp(0.9rem, 1.8vw, 1.25rem);
  height: clamp(10.8rem, 30%, 13.8rem);
  border-radius: 1.2rem;
  background: var(--surface-panel-inset);
  border: 1px solid var(--border-panel-subtle);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-inline);
  backdrop-filter: blur(calc(var(--glass-blur) - 2px));
  opacity: 0.9;
  pointer-events: none;
}

.carousel-source,
.carousel-tags,
.carousel-meta {
  display: flex;
  flex-wrap: wrap;
}

.carousel-source {
  position: relative;
  z-index: 1;
  align-items: center;
  gap: 0.55rem;
  font-size: 0.8rem;
  width: fit-content;
  padding: 0.34rem 0.62rem;
  color: var(--text-secondary);
}

.source-author {
  font-weight: 700;
  color: var(--text-primary);
}

.carousel-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.24rem 0.55rem;
  border-radius: 999px;
  background: var(--surface-panel-chip-accent);
  border: 1px solid var(--border-panel-badge-accent);
  color: var(--primary-color);
  font-size: 0.68rem;
  font-weight: 700;
  backdrop-filter: blur(var(--glass-blur));
}

.source-label {
  color: var(--text-secondary);
}

.carousel-copy {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 0.7rem;
  max-width: min(42rem, 80%);
  padding: 1rem 1rem 1.05rem;
  border-radius: 1.1rem;
}

.carousel-copy h3 {
  margin: 0;
  font-size: clamp(1.45rem, 3vw, 2.2rem);
  font-weight: 700;
  line-height: 1.18;
  color: var(--title-color);
}

.carousel-copy p {
  margin: 0;
  font-size: 0.94rem;
  line-height: 1.75;
  color: var(--text-secondary);
}

.carousel-tags,
.carousel-meta {
  gap: 0.55rem;
}

.tag-chip {
  padding: 0.26rem 0.68rem;
  color: var(--text-primary);
  font-size: 0.72rem;
}

.carousel-meta {
  font-size: 0.76rem;
  color: var(--text-secondary);
}

@media (max-width: 960px) {
  .carousel-copy {
    max-width: none;
  }
}

@include sourcelin-down(sm) {
  .carousel-slide {
    min-height: 320px;
  }

  .carousel-frame {
    gap: 0.72rem;
    padding: 0.9rem;
  }

  .carousel-frame::before {
    inset: auto 0.8rem 0.78rem;
    height: 10.2rem;
  }

  .carousel-source {
    font-size: 0.74rem;
    padding: 0.3rem 0.56rem;
  }

  .carousel-copy {
    max-width: none;
    gap: 0.58rem;
    padding: 0.82rem 0.82rem 0.88rem;
  }

  .carousel-copy h3 {
    font-size: clamp(1.12rem, 5.4vw, 1.42rem);
  }

  .carousel-copy p {
    font-size: 0.86rem;
    line-height: 1.62;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .carousel-tags,
  .carousel-meta {
    gap: 0.42rem;
  }

  .tag-chip {
    padding: 0.22rem 0.54rem;
    font-size: 0.68rem;
  }
}
</style>
