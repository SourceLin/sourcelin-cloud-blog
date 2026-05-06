<template>
  <Teleport to="body">
    <div
      class="theme-transition"
      :class="[
        targetTheme,
        direction,
        { active: isAnimating }
      ]"
    >
      <div class="sky-bg" :class="{ active: isAnimating }" />
      <div class="sky-overlay" :class="{ active: isAnimating }" />
      <div class="planet-container" :style="{ transform: `rotate(${rotation}deg)` }">
        <div class="celestial-body sun" :class="{ visible: showSun }" />
        <div class="celestial-body moon" :class="{ visible: showMoon }" />
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, watch, computed } from 'vue'
import { useThemeStore, type ThemeDirection } from '@/stores/theme'

const themeStore = useThemeStore()
const isAnimating = ref(false)
const targetTheme = ref(themeStore.isDark ? 'dark' : 'light')
const direction = ref<ThemeDirection>(null)
const rotation = ref(0)

let animFrameId: number | null = null

watch(isAnimating, (animating) => {
  if (animating) {
    document.body.classList.add('theme-transitioning')
  } else {
    document.body.classList.remove('theme-transitioning')
  }
})
let isInitialized = false


const showSun = computed(() => {
  const sunPos = ((rotation.value % 360) + 360) % 360
  return sunPos <= 90 || sunPos >= 270
})

const showMoon = computed(() => {
  const moonPos = ((180 + rotation.value) % 360 + 360) % 360
  return moonPos <= 90 || moonPos >= 270
})

const cancelAnimFrame = () => {
  if (animFrameId !== null) {
    cancelAnimationFrame(animFrameId)
    animFrameId = null
  }
}

function easeInOut(t: number): number {
  return t < 0.5
    ? 2 * t * t
    : 1 - Math.pow(-2 * t + 2, 2) / 2
}

function animate(from: number, to: number, duration: number) {
  const startTime = performance.now()
  
  function tick(currentTime: number) {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = easeInOut(progress)
    
    rotation.value = from + (to - from) * eased
    
    if (progress < 1) {
      animFrameId = requestAnimationFrame(tick)
    } else {
      animFrameId = null
      isAnimating.value = false
    }
  }
  
  animFrameId = requestAnimationFrame(tick)
}

watch(
  () => themeStore.isDark,
  (isDark) => {
    const nextTheme = isDark ? 'dark' : 'light'
    const nextDirection: ThemeDirection = isDark ? 'to-dark' : 'to-light'
    
    targetTheme.value = nextTheme
    direction.value = nextDirection
    
    if (!isInitialized) {
      rotation.value = isDark ? 180 : 0
      isInitialized = true
      return
    }
    
    cancelAnimFrame()
    isAnimating.value = true
    
    if (isDark) {
      animate(0, 180, 3000)
    } else {
      animate(180, 0, 3000)
    }
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  cancelAnimFrame()
})
</script>

<style lang="scss" scoped>

.theme-transition {
  --pivot-offset: 60px;
  --orbit-radius: 85vh;
  --sun-size: 100px;
  --moon-size: 90px;
}

.theme-transition {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9999;
  opacity: 0;
  transition: opacity 0.3s ease;

  &.active {
    opacity: 1;
  }
}

.sky-bg {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 1.2s ease;

  &.active {
    opacity: 1;
  }
}

.sky-overlay {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 2s ease;

  &.active {
    opacity: 1;
  }
}

.theme-transition.dark .sky-overlay {
  background: var(--theme-transition-sky-overlay-dark);
}

.theme-transition.light .sky-overlay {
  background: var(--theme-transition-sky-overlay-light);
}

.theme-transition.light .sky-bg {
  background: var(--theme-transition-sky-bg-light);
}

.theme-transition.dark .sky-bg {
  background: var(--theme-transition-sky-bg-dark);
}

.planet-container {
  position: fixed;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  transform-origin: 50% calc(100% + var(--pivot-offset));
  will-change: transform;
}

.celestial-body {
  position: absolute;
  left: 50%;
  top: calc(100% + var(--pivot-offset));
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;

  &.visible {
    opacity: 1;
  }
}

.sun {
  width: var(--sun-size);
  height: var(--sun-size);
  margin-left: calc(var(--sun-size) / -2);
  margin-top: calc(var(--sun-size) / -2);
  transform: rotate(0deg) translateY(calc(-1 * var(--orbit-radius)));
  background: var(--theme-transition-sun-gradient);
  box-shadow: var(--theme-transition-sun-shadow);
}

.moon {
  width: var(--moon-size);
  height: var(--moon-size);
  margin-left: calc(var(--moon-size) / -2);
  margin-top: calc(var(--moon-size) / -2);
  transform: rotate(180deg) translateY(calc(-1 * var(--orbit-radius)));
  background: var(--theme-transition-moon-gradient);
  border-radius: 50%;
  box-shadow: var(--theme-transition-moon-shadow);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -10%;
    left: -75%;
    width: 100%;
    height: 120%;
    background: var(--theme-transition-moon-mask);
    border-radius: 50%;
    z-index: 1;
  }

  &::after {
    content: '';
    position: absolute;
    width: 12px;
    height: 12px;
    top: 18px;
    left: 40px;
    background: var(--theme-transition-moon-crater);
    border-radius: 50%;
    box-shadow: var(--theme-transition-moon-crater-shadow);
    z-index: 2;
  }
}
</style>

<style lang="scss">
body.theme-transitioning {
  pointer-events: none !important;
  user-select: none !important;
  overflow: hidden;
}
</style>

