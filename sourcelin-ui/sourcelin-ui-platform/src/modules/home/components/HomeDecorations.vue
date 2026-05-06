<template>
  <div class="sourcelin-decorations">
    <div
      v-for="(circle, index) in decorationCircles"
      :key="index"
      class="decoration-circle"
      :style="{
        width: circle.size + 'px',
        height: circle.size + 'px',
        top: circle.top || 'auto',
        left: circle.left || 'auto',
        right: circle.right || 'auto',
        bottom: circle.bottom || 'auto',
        animationDelay: circle.animationDelay + 's'
      }"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

type DecorationCircle = {
  size: number
  top?: string
  left?: string
  right?: string
  bottom?: string
  animationDelay: number
}

const decorationCircles = ref<DecorationCircle[]>([])

const circleCountConfig = {
  min: 2,
  max: 4
}

const generateDecorationCircles = () => {
  const { min, max } = circleCountConfig
  const count = Math.floor(Math.random() * (max - min + 1)) + min
  const circles: DecorationCircle[] = []

  const regions = [
    { top: [8, 28], left: [8, 28] },
    { top: [8, 28], right: [8, 28] },
    { bottom: [8, 28], left: [8, 28] },
    { bottom: [8, 28], right: [8, 28] },
    { top: [8, 28], left: [36, 64] },
    { bottom: [8, 28], left: [36, 64] },
    { top: [36, 64], left: [8, 28] },
    { top: [36, 64], right: [8, 28] }
  ]

  const shuffledRegions = regions.sort(() => Math.random() - 0.5).slice(0, count)

  for (const region of shuffledRegions) {
    const position: Record<string, string> = {}
    const size = Math.floor(Math.random() * 80) + 60

    if (region.top) {
      position.top = (Math.random() * (region.top[1] - region.top[0]) + region.top[0]).toFixed(1) + '%'
    }
    if (region.bottom) {
      position.bottom = (Math.random() * (region.bottom[1] - region.bottom[0]) + region.bottom[0]).toFixed(1) + '%'
    }
    if (region.left) {
      position.left = (Math.random() * (region.left[1] - region.left[0]) + region.left[0]).toFixed(1) + '%'
    }
    if (region.right) {
      position.right = (Math.random() * (region.right[1] - region.right[0]) + region.right[0]).toFixed(1) + '%'
    }

    const animationDelay = parseFloat((Math.random() * 8).toFixed(1))

    circles.push({
      size,
      animationDelay,
      ...(position as { top?: string; left?: string; right?: string; bottom?: string })
    })
  }

  decorationCircles.value = circles
}

onMounted(() => {
  generateDecorationCircles()
})
</script>

<style lang="scss" scoped>
.sourcelin-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  animation: float-rotate 8s ease-in-out infinite;
  background: var(--surface-glass-card-soft);
  border: 1px solid var(--glass-stroke);
  backdrop-filter: blur(10px) saturate(var(--glass-saturate));
  box-shadow:
    inset 0 1px 0 var(--surface-white-20),
    0 8px 32px var(--glass-shadow);
  z-index: 1;
}

html[data-theme='light'] .decoration-circle {
  background: color-mix(in srgb, var(--glass-highlight) 12%, rgba(var(--primary-color-rgb), 0.04));
  outline-color: rgba(var(--primary-color-rgb), 0.15);
}

@keyframes float-rotate {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
    opacity: 0.6;
  }
  33% {
    transform: translateY(-30px) rotate(120deg) scale(1.1);
    opacity: 0.8;
  }
  66% {
    transform: translateY(-15px) rotate(240deg) scale(0.9);
    opacity: 0.7;
  }
}
</style>

