<template>
  <div class="startup-loading">
    <div class="startup-loading__backdrop" :style="backdropStyle" />
    <div class="startup-loading__veil" />
    <div class="startup-loading__halo startup-loading__halo--one" />
    <div class="startup-loading__halo startup-loading__halo--two" />

    <SSurfacePanel class="startup-loading__content" variant="soft">
      <SSurfaceChip class="startup-loading__brand" tone="accent">
        <img :src="logoImage" alt="SourceLin Logo" class="startup-loading__brand-image">
      </SSurfaceChip>

      <div class="startup-loading__copy">
        <span class="startup-loading__eyebrow">SourceLin</span>
        <h1 class="startup-loading__title">正在进入圆圈博客</h1>
        <p class="startup-loading__subtitle">{{ subtitle }}</p>
      </div>

      <SSurfacePanel class="startup-loading__status" variant="inset">
        <div class="startup-loading__rail">
          <span class="startup-loading__rail-fill" />
        </div>
        <span class="startup-loading__status-text">{{ phaseText }}</span>
      </SSurfacePanel>
    </SSurfacePanel>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, watch } from 'vue'
import loadingBackground from '@/assets/images/backgrounds/home-bg.jpg'
import logoImage from '@/assets/images/logo/logo.png'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { useUiStore } from '@/stores/ui.store'
import { createStartupScrollLock } from '@/shared/utils/startupScrollLock'

const props = defineProps<{
  visible: boolean
}>()

const uiStore = useUiStore()

const backdropStyle = computed(() => ({
  '--startup-loading-image': `url("${loadingBackground}")`
}))

const phaseText = computed(() => {
  if (!uiStore.startupRouteReady) {
    return '正在铺开页面内容'
  }

  if (uiStore.startupRequiresHomeReady && !uiStore.startupHomeReady) {
    return '正在准备首页内容'
  }

  if (!uiStore.startupMinDurationDone) {
    return '正在收尾开场效果'
  }

  return '马上进入站点'
})

const subtitle = computed(() => {
  return uiStore.startupRequiresHomeReady
    ? '首页内容正在就位，请稍等片刻。'
    : '正在为你准备当前页面。'
})

const scrollLock =
  typeof document === 'undefined'
    ? null
    : createStartupScrollLock(document)

function syncScrollLock(visible: boolean) {
  if (!scrollLock) {
    return
  }

  scrollLock.sync(visible)
}

watch(
  () => props.visible,
  (value) => {
    syncScrollLock(value)
  },
  { immediate: true }
)

onMounted(() => {
  syncScrollLock(props.visible)
})

onBeforeUnmount(() => {
  scrollLock?.release()
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.startup-loading {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(20px, 4vw, 32px);
  overflow: hidden;
}

.startup-loading__backdrop,
.startup-loading__veil,
.startup-loading__halo {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.startup-loading__backdrop {
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--body-bg) 18%, transparent), color-mix(in srgb, var(--body-bg) 72%, transparent)),
    var(--startup-loading-image) center / cover no-repeat;
  transform: scale(1.04);
  filter: saturate(0.94);
}

.startup-loading__veil {
  background:
    radial-gradient(circle at top center, color-mix(in srgb, var(--surface-white-15) 58%, transparent), transparent 34%),
    linear-gradient(135deg, color-mix(in srgb, var(--body-bg) 16%, transparent), color-mix(in srgb, var(--body-bg) 70%, transparent));
  backdrop-filter: blur(14px) saturate(1.05);
}

.startup-loading__halo {
  inset: auto;
  width: clamp(280px, 32vw, 480px);
  height: clamp(280px, 32vw, 480px);
  border-radius: 999px;
  filter: blur(92px);
  opacity: 0.28;
}

.startup-loading__halo--one {
  top: -12%;
  right: -8%;
  background: color-mix(in srgb, var(--primary-color) 34%, transparent);
}

.startup-loading__halo--two {
  bottom: -14%;
  left: -10%;
  background: color-mix(in srgb, var(--secondary-color, var(--primary-color)) 24%, transparent);
}

.startup-loading__content {
  position: relative;
  z-index: 1;
  width: min(560px, 100%);
  aspect-ratio: 1;
  display: grid;
  justify-items: center;
  align-content: center;
  gap: 18px;
  padding: clamp(34px, 6vw, 52px);
  text-align: center;
  border-radius: 999px;
  isolation: isolate;
  background:
    radial-gradient(circle at 50% 36%, color-mix(in srgb, var(--surface-white-15) 58%, transparent), transparent 38%),
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-panel-soft) 62%, transparent),
      color-mix(in srgb, var(--surface-panel-default) 54%, transparent)
    );
  border-color: color-mix(in srgb, var(--border-panel-subtle) 58%, transparent);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-15) 42%, transparent),
    0 18px 44px color-mix(in srgb, var(--background-color-deep) 10%, transparent);
  will-change: transform, box-shadow, background;
  animation: startup-orb-breathe 5.4s ease-in-out infinite;
}

.startup-loading__content::before {
  content: '';
  position: absolute;
  inset: clamp(18px, 3vw, 28px);
  border-radius: inherit;
  border: 1px solid color-mix(in srgb, var(--border-panel-badge-accent) 42%, transparent);
  opacity: 0.68;
  z-index: -1;
  box-shadow:
    0 0 0 1px color-mix(in srgb, var(--surface-white-15) 10%, transparent),
    0 0 34px color-mix(in srgb, var(--primary-color) 10%, transparent),
    0 18px 32px color-mix(in srgb, var(--background-color-deep) 8%, transparent);
  will-change: transform, opacity, box-shadow;
  animation: startup-ring-pulse 4.4s ease-in-out infinite;
}

.startup-loading__content::after {
  content: '';
  position: absolute;
  inset: clamp(42px, 6vw, 64px);
  border-radius: inherit;
  border: 1px dashed color-mix(in srgb, var(--border-panel-subtle) 54%, transparent);
  opacity: 0.5;
  z-index: -1;
  filter: drop-shadow(0 0 16px color-mix(in srgb, var(--primary-color) 8%, transparent));
  will-change: transform, opacity, filter;
  animation: startup-ring-drift 8.6s linear infinite;
}

.startup-loading__brand {
  width: 58px;
  height: 58px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  will-change: transform, box-shadow;
  animation: startup-brand-shell-breathe 2.8s ease-in-out infinite;
}

.startup-loading__brand-image {
  width: 28px;
  height: 28px;
  object-fit: contain;
  display: block;
  will-change: transform, filter;
  animation: startup-brand-core-breathe 2.3s ease-in-out infinite;
}

.startup-loading__copy {
  display: grid;
  gap: 10px;
}

.startup-loading__eyebrow {
  font-size: 0.76rem;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.startup-loading__title {
  margin: 0;
  font-size: clamp(1.8rem, 4vw, 2.5rem);
  line-height: 1.15;
  color: var(--title-color);
}

.startup-loading__subtitle {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.7;
  color: var(--text-secondary);
}

.startup-loading__status {
  width: min(320px, 100%);
  max-width: 100%;
  box-sizing: border-box;
  display: grid;
  gap: 12px;
  padding: 14px 16px;
  will-change: transform, box-shadow, border-color;
  animation: startup-status-breathe 5.4s ease-in-out infinite;
}

.startup-loading__rail {
  position: relative;
  height: 6px;
  border-radius: 999px;
  overflow: hidden;
  background: color-mix(in srgb, var(--surface-panel-chip) 88%, transparent);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-15) 16%, transparent),
    inset 0 0 0 1px color-mix(in srgb, var(--border-panel-subtle) 42%, transparent);
}

.startup-loading__rail-fill {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--primary-color) 14%, transparent),
    color-mix(in srgb, var(--primary-color) 68%, transparent),
    color-mix(in srgb, var(--primary-color) 14%, transparent)
  );
  transform-origin: left center;
  box-shadow:
    0 0 16px color-mix(in srgb, var(--primary-color) 22%, transparent),
    0 0 28px color-mix(in srgb, var(--primary-color) 10%, transparent);
  animation: startup-progress 2.8s ease-in-out infinite;
}

.startup-loading__status-text {
  font-size: 0.82rem;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
  animation: startup-status-text-glow 5.4s ease-in-out infinite;
}

@keyframes startup-orb-breathe {
  0%,
  100% {
    transform: translateY(0) scale(1);
    box-shadow:
      inset 0 1px 0 color-mix(in srgb, var(--surface-white-15) 42%, transparent),
      0 18px 44px color-mix(in srgb, var(--background-color-deep) 10%, transparent);
  }

  50% {
    transform: translateY(-3px) scale(1.018);
    box-shadow:
      inset 0 1px 0 color-mix(in srgb, var(--surface-white-15) 54%, transparent),
      0 26px 60px color-mix(in srgb, var(--background-color-deep) 13%, transparent);
  }
}

@keyframes startup-ring-pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.58;
    box-shadow:
      0 0 0 1px color-mix(in srgb, var(--surface-white-15) 8%, transparent),
      0 0 28px color-mix(in srgb, var(--primary-color) 8%, transparent),
      0 14px 24px color-mix(in srgb, var(--background-color-deep) 6%, transparent);
  }

  50% {
    transform: scale(1.026);
    opacity: 0.84;
    box-shadow:
      0 0 0 1px color-mix(in srgb, var(--surface-white-15) 14%, transparent),
      0 0 48px color-mix(in srgb, var(--primary-color) 16%, transparent),
      0 22px 38px color-mix(in srgb, var(--background-color-deep) 10%, transparent);
  }
}

@keyframes startup-ring-drift {
  0% {
    transform: rotate(0deg) scale(1);
    opacity: 0.42;
    filter: drop-shadow(0 0 12px color-mix(in srgb, var(--primary-color) 7%, transparent));
  }

  50% {
    transform: rotate(180deg) scale(1.016);
    opacity: 0.62;
    filter: drop-shadow(0 0 20px color-mix(in srgb, var(--primary-color) 12%, transparent));
  }

  100% {
    transform: rotate(360deg) scale(1);
    opacity: 0.42;
    filter: drop-shadow(0 0 12px color-mix(in srgb, var(--primary-color) 7%, transparent));
  }
}

@keyframes startup-brand-shell-breathe {
  0%,
  100% {
    transform: translateY(0) scale(1);
    box-shadow: var(--shadow-panel-soft);
  }

  50% {
    transform: translateY(-2px) scale(1.075);
    box-shadow: var(--shadow-panel-hover);
  }
}

@keyframes startup-brand-core-breathe {
  0%,
  100% {
    transform: scale(1);
    filter: drop-shadow(0 0 0 color-mix(in srgb, var(--primary-color) 0%, transparent));
  }

  50% {
    transform: scale(1.14);
    filter: drop-shadow(0 0 12px color-mix(in srgb, var(--primary-color) 26%, transparent));
  }
}

@keyframes startup-status-breathe {
  0%,
  100% {
    transform: translateY(0) scale(1);
    box-shadow: var(--shadow-panel-soft);
    border-color: color-mix(in srgb, var(--border-panel-subtle) 72%, transparent);
  }

  50% {
    transform: translateY(-1px) scale(1.012);
    box-shadow: var(--shadow-panel-hover);
    border-color: color-mix(in srgb, var(--border-panel-badge-accent) 54%, transparent);
  }
}

@keyframes startup-status-text-glow {
  0%,
  100% {
    color: var(--text-secondary);
    opacity: 0.84;
  }

  50% {
    color: color-mix(in srgb, var(--text-primary) 78%, var(--text-secondary));
    opacity: 1;
  }
}

@keyframes startup-progress {
  0% {
    transform: translateX(-46%) scaleX(0.28);
    opacity: 0.42;
  }

  45% {
    transform: translateX(-2%) scaleX(0.82);
    opacity: 1;
  }

  55% {
    transform: translateX(6%) scaleX(0.92);
    opacity: 1;
  }

  100% {
    transform: translateX(46%) scaleX(0.3);
    opacity: 0.42;
  }
}

@include sourcelin-down(sm) {
  .startup-loading__content {
    width: min(100%, 420px);
    min-height: 420px;
    aspect-ratio: auto;
    padding: 30px 24px;
  }

  .startup-loading__status {
    width: min(240px, calc(100% - var(--spacing-xl)));
    max-width: 100%;
    justify-self: center;
  }
}
</style>
