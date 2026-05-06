<template>
  <div class="s-horizontal-overflow-rail" role="group" :aria-label="groupLabel">
    <SSurfaceChip
      v-show="hasOverflow"
      tag="button"
      variant="button"
      class="s-horizontal-overflow-rail__nav s-horizontal-overflow-rail__nav--prev"
      :disabled="!canScrollPrev"
      type="button"
      :aria-label="prevAriaLabel"
      @click="scrollTrack(-1)"
    >
      <SIcon
        :icon="appIcons.arrowForward"
        :size="16"
        class="s-horizontal-overflow-rail__nav-icon s-horizontal-overflow-rail__nav-icon--prev"
      />
    </SSurfaceChip>

    <div
      ref="viewportRef"
      class="s-horizontal-overflow-rail__viewport"
      :class="{
        's-horizontal-overflow-rail__viewport--overflow': hasOverflow,
        's-horizontal-overflow-rail__viewport--left-hidden': hasOverflow && !canScrollPrev,
        's-horizontal-overflow-rail__viewport--right-hidden': hasOverflow && !canScrollNext
      }"
    >
      <div class="s-horizontal-overflow-rail__track">
        <slot />
      </div>
    </div>

    <SSurfaceChip
      v-show="hasOverflow"
      tag="button"
      variant="button"
      class="s-horizontal-overflow-rail__nav s-horizontal-overflow-rail__nav--next"
      :disabled="!canScrollNext"
      type="button"
      :aria-label="nextAriaLabel"
      @click="scrollTrack(1)"
    >
      <SIcon :icon="appIcons.arrowForward" :size="16" class="s-horizontal-overflow-rail__nav-icon" />
    </SSurfaceChip>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useHorizontalOverflow } from '@/shared/composables/useHorizontalOverflow'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = withDefaults(defineProps<{
  groupLabel: string
  prevAriaLabel?: string
  nextAriaLabel?: string
  refreshKeys?: unknown[]
}>(), {
  prevAriaLabel: '向左滚动',
  nextAriaLabel: '向右滚动',
  refreshKeys: () => []
})

const viewportRef = ref<HTMLElement | null>(null)

const {
  hasOverflow,
  canScrollPrev,
  canScrollNext,
  refreshScrollState,
  scrollTrack
} = useHorizontalOverflow(viewportRef)

watch(
  () => props.refreshKeys,
  async () => {
    await refreshScrollState()
  },
  { deep: true }
)
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.s-horizontal-overflow-rail {
  position: relative;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  overflow: hidden;
}

.s-horizontal-overflow-rail__viewport {
  position: relative;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  padding: 0.15rem 0.4rem 0.45rem;
  scrollbar-width: none;
  scroll-behavior: smooth;
}

.s-horizontal-overflow-rail__viewport::-webkit-scrollbar {
  display: none;
}

.s-horizontal-overflow-rail__viewport::before,
.s-horizontal-overflow-rail__viewport::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0.4rem;
  width: clamp(1rem, 2vw, 1.5rem);
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.s-horizontal-overflow-rail__viewport::before {
  left: 0;
  background: linear-gradient(90deg, var(--surface-page-content), transparent);
}

.s-horizontal-overflow-rail__viewport::after {
  right: 0;
  background: linear-gradient(270deg, var(--surface-page-content), transparent);
}

.s-horizontal-overflow-rail__viewport--overflow {
  padding-inline: clamp(2.65rem, 4vw, 3.2rem);
}

.s-horizontal-overflow-rail__viewport--overflow::before,
.s-horizontal-overflow-rail__viewport--overflow::after {
  opacity: 1;
}

.s-horizontal-overflow-rail__viewport--left-hidden::before,
.s-horizontal-overflow-rail__viewport--right-hidden::after {
  opacity: 0;
}

.s-horizontal-overflow-rail__track {
  display: flex;
  align-items: center;
  gap: 10px;
  width: max-content;
  min-width: 100%;
  max-width: none;
  padding: 5px 0 5px;
}

.s-horizontal-overflow-rail__nav {
  position: absolute;
  top: 50%;
  z-index: 2;
  min-width: 2.1rem;
  min-height: 2.1rem;
  padding: 0;
  border: none;
  transform: translateY(calc(-50% - 0.18rem));
  box-shadow:
    var(--shadow-panel-soft),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-20) 84%, transparent);
}

.s-horizontal-overflow-rail__nav--prev {
  left: 0;
}

.s-horizontal-overflow-rail__nav--next {
  right: 0;
}

.s-horizontal-overflow-rail__nav-icon {
  color: var(--title-color);
}

.s-horizontal-overflow-rail__nav-icon--prev {
  transform: rotate(180deg);
}

@include sourcelin-down(sm) {
  .s-horizontal-overflow-rail__viewport,
  .s-horizontal-overflow-rail__viewport--overflow {
    padding-inline: 0.02rem;
  }

  .s-horizontal-overflow-rail__viewport::before,
  .s-horizontal-overflow-rail__viewport::after,
  .s-horizontal-overflow-rail__nav {
    display: none;
  }

  .s-horizontal-overflow-rail__track {
    gap: 10px;
    padding: 8px;
  }
}
</style>
