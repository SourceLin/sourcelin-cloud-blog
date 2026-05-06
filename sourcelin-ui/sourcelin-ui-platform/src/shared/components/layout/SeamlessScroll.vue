<template>
  <div
    ref="wrapperRef"
    class="seamless-scroll"
    @mouseenter="pause"
    @mouseleave="resume"
  >
    <div
      class="seamless-track"
      :class="{ active: isActive }"
      :style="trackStyle"
    >
      <div ref="contentRef" class="seamless-content">
        <slot />
      </div>
      <div
        v-for="index in cloneCount"
        :key="`clone-${index}`"
        class="seamless-content clone"
      >
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useElementSize } from '@vueuse/core'

interface Props {
  speed?: number
  gap?: number
  pauseOnHover?: boolean
  autoFill?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  speed: 40,
  gap: 32,
  pauseOnHover: true,
  autoFill: false
})

const wrapperRef = ref<HTMLElement | null>(null)
const contentRef = ref<HTMLElement | null>(null)

const { width: wrapperWidth } = useElementSize(wrapperRef)
const { width: contentWidth } = useElementSize(contentRef)

const cloneCount = computed(() => {
  if (contentWidth.value <= 0) {
    return 0
  }

  if (props.autoFill) {
    const visibleCopies = Math.ceil(wrapperWidth.value / contentWidth.value)
    return Math.max(1, visibleCopies + 1)
  }

  return contentWidth.value > wrapperWidth.value ? 1 : 0
})

const isActive = computed(() => cloneCount.value > 0)
const distance = computed(() => contentWidth.value + props.gap)
const duration = computed(() => Math.max(distance.value / props.speed, 5))

const trackStyle = computed(() => ({
  '--scroll-distance': `-${distance.value}px`,
  '--scroll-duration': `${duration.value}s`,
  '--scroll-gap': `${props.gap}px`
}))

const pause = () => {
  if (!props.pauseOnHover || !wrapperRef.value) return
  const track = wrapperRef.value.querySelector('.seamless-track') as HTMLElement | null
  if (track) track.style.animationPlayState = 'paused'
}

const resume = () => {
  if (!props.pauseOnHover || !wrapperRef.value) return
  const track = wrapperRef.value.querySelector('.seamless-track') as HTMLElement | null
  if (track) track.style.animationPlayState = 'running'
}
</script>

<style scoped>
.seamless-scroll {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.seamless-track {
  display: inline-flex;
  align-items: center;
  gap: var(--scroll-gap, 32px);
  white-space: nowrap;
}

.seamless-track.active {
  animation: seamless-scroll var(--scroll-duration, 20s) linear infinite;
}

.seamless-content {
  display: inline-flex;
  align-items: center;
  gap: var(--scroll-gap, 32px);
}

@keyframes seamless-scroll {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(var(--scroll-distance, -200px));
  }
}
</style>

