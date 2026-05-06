<script setup lang="ts">
import type { SayItem } from '@/modules/say/model/say.types'
import type { SayCardVariant } from '@/modules/say/composables/useSayCardState'
import SImage from '@/shared/components/ui/SImage.vue'

const props = defineProps<{
  item: SayItem
  cardVariant: SayCardVariant
  imageArray: string[]
  galleryImages: string[]
  extraImageCount: number
  formattedContent: string
}>()

const emit = defineEmits<{
  preview: [index: number]
}>()
</script>

<template>
  <div class="say-body" :class="`say-body--${cardVariant}`">
    <button
      v-if="cardVariant === 'single-image' && imageArray[0]"
      type="button"
      class="single-hero"
      @click="emit('preview', 0)"
    >
      <SImage
        :src="imageArray[0]"
        object-fit="cover"
        class="single-hero__media"
        alt=""
      />
      <div class="single-hero__veil" aria-hidden="true" />
    </button>

    <div class="say-copy" :class="{ 'say-copy--text-only': cardVariant === 'text-only' }">
      <p
        class="say-content"
        :class="{ 'say-content--lead': cardVariant === 'text-only' }"
        v-html="formattedContent"
      />
    </div>

    <div
      v-if="cardVariant === 'multi-image'"
      :class="[
        'say-images-grid',
        { 'say-images-grid--featured': galleryImages.length >= 3 }
      ]"
    >
      <button
        v-for="(img, idx) in galleryImages"
        :key="`${props.item.id}-${idx}`"
        type="button"
        class="say-image-tile"
        @click="emit('preview', idx)"
      >
        <SImage
          :src="img"
          object-fit="cover"
          class="say-images-grid__img"
          alt=""
        />
        <span
          v-if="idx === galleryImages.length - 1 && extraImageCount > 0"
          class="say-image-tile__more"
        >
          +{{ extraImageCount }}
        </span>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-body {
  display: grid;
  gap: 18px;
}

.say-body--text-only {
  gap: 16px;
}

.say-content {
  margin: 0;
  font-size: 1rem;
  line-height: 1.92;
  color: var(--text-primary);
  word-break: break-word;
}

.say-content--lead {
  font-size: 1.08rem;
  line-height: 2;
}

.say-copy {
  max-width: 100%;
}

.say-copy--text-only {
  max-width: 94%;
}

.single-hero {
  position: relative;
  display: block;
  width: 100%;
  padding: 0;
  border: none;
  border-radius: 24px;
  overflow: hidden;
  aspect-ratio: 16 / 10;
  background: var(--surface-panel-inset);
  cursor: pointer;
  box-shadow: var(--shadow-panel-inline);
}

.single-hero__media {
  position: absolute;
  inset: 0;

  :deep(img) {
    transition: transform var(--transition-base);
  }
}

.single-hero__veil {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--glass-surface-strong) 8%, transparent) 0%,
    transparent 38%,
    color-mix(in srgb, var(--text-color-dark) 22%, transparent) 100%
  );
  transition: opacity var(--transition-base);
}

.single-hero:hover .single-hero__veil {
  opacity: 0.72;
}

.single-hero:hover :deep(img) {
  transform: scale(1.04);
}

.say-images-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.say-images-grid--featured {
  .say-image-tile:first-child {
    grid-column: span 2;
    aspect-ratio: 16 / 10;
  }
}

.say-image-tile {
  position: relative;
  padding: 0;
  border: none;
  border-radius: 20px;
  overflow: hidden;
  aspect-ratio: 1;
  background: var(--surface-panel-inset);
  cursor: pointer;
  box-shadow: var(--shadow-panel-inline);

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(
      180deg,
      transparent,
      color-mix(in srgb, var(--text-color-dark) 18%, transparent)
    );
    opacity: 0;
    transition: opacity var(--transition-base);
  }

  &:hover::after {
    opacity: 1;
  }

  &:hover :deep(img) {
    transform: scale(1.04);
  }
}

.say-images-grid__img {
  width: 100%;
  height: 100%;

  :deep(img) {
    transition: transform var(--transition-base);
  }
}

.say-image-tile__more {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: color-mix(in srgb, var(--text-color-dark) 58%, transparent);
  color: var(--text-color-light);
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  backdrop-filter: blur(4px);
}

.say-body--single-image {
  grid-template-columns: minmax(0, 1.16fr) minmax(220px, 0.84fr);
  align-items: start;
  gap: 20px;
}

.say-body--multi-image {
  grid-template-columns: minmax(0, 0.9fr) minmax(280px, 1.1fr);
  align-items: start;
  gap: 22px;
}

.say-body--single-image .single-hero {
  grid-column: 1;
  grid-row: 1 / span 2;
  align-self: start;
  min-height: 0;
  min-width: 0;
}

.say-body--single-image .say-copy {
  grid-column: 2;
  grid-row: 1 / span 2;
  align-self: end;
  min-width: 0;
}

.say-body--multi-image .say-copy {
  grid-column: 1;
  min-width: 0;
}

.say-body--multi-image .say-images-grid {
  grid-column: 2;
  grid-row: 1 / span 2;
  min-width: 0;
  min-height: 0;
}

@include sourcelin-down(md) {
  .say-body--single-image,
  .say-body--multi-image {
    grid-template-columns: 1fr;
  }

  .say-body--single-image .single-hero,
  .say-body--single-image .say-copy,
  .say-body--multi-image .say-copy,
  .say-body--multi-image .say-images-grid {
    grid-column: auto;
    grid-row: auto;
  }

  .say-copy--text-only {
    max-width: 100%;
  }

  .single-hero {
    aspect-ratio: 4 / 3;
  }
}

@include sourcelin-up(lg) {
  .say-images-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@include sourcelin-down(sm) {
  .say-content {
    font-size: 0.97rem;
  }

  .say-content--lead {
    font-size: 1.02rem;
  }

  .say-images-grid {
    gap: 8px;
  }
}
</style>