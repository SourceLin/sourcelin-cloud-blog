<template>
  <div
    v-if="showMarquee"
    ref="marqueeRootRef"
    class="marquee-notice"
    :class="{ 'is-paused': paused, 'marquee-notice--frame-fallback': !framePath }"
    :style="{ '--marquee-interval-ms': `${effectiveIntervalMs}ms` }"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <div ref="marqueeFrameRef" class="marquee-frame" aria-hidden="true">
      <svg v-if="framePath" class="marquee-frame__svg" :viewBox="frameViewBox" preserveAspectRatio="none">
        <path class="marquee-frame__track" :d="framePath" pathLength="100" />
        <path class="marquee-frame__runner" :d="framePath" pathLength="100" />
      </svg>
    </div>

    <router-link
      class="marquee-link"
      :to="{ name: 'MessageCenter' }"
      aria-label="查看公告"
    >
      <span class="marquee-signet">
        <span class="marquee-signet__icon" aria-hidden="true">
          <SIcon :icon="appIcons.notice" :size="14" />
        </span>
      </span>

      <div class="marquee-copy">
        <Transition name="marquee-swap" mode="out-in">
          <span :key="activeIndex" class="marquee-item">
            {{ activeNotice }}
          </span>
        </Transition>
      </div>
    </router-link>

    <button
      type="button"
      class="marquee-close"
      aria-label="关闭公告"
      @click="handleClose"
    >
      <SIcon :icon="appIcons.close" :size="14" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  notices?: string[]
  speed?: number
  direction?: 'left' | 'right'
  intervalMs?: number
}

const props = withDefaults(defineProps<Props>(), {
  notices: () => [],
  speed: 18,
  direction: 'left',
  intervalMs: 3600
})

const storageKey = 'home-marquee-dismissed-signature'
const dismissed = ref(false)
const activeIndex = ref(0)
const timerId = ref<number | null>(null)
const paused = ref(false)
const marqueeRootRef = ref<HTMLElement | null>(null)
const frameMetrics = ref({
  width: 0,
  height: 0,
  radius: 0
})
let frameResizeObserver: ResizeObserver | null = null

const notices = computed(() => props.notices.filter(Boolean))
const noticeSignature = computed(() => notices.value.join('||'))
const showMarquee = computed(() => notices.value.length > 0 && !dismissed.value)
const activeNotice = computed(() => notices.value[activeIndex.value] || notices.value[0] || '')
const effectiveIntervalMs = computed(() => {
  const interval = Number(props.intervalMs)
  return Number.isFinite(interval) && interval >= 1500 ? interval : 3600
})
const frameViewBox = computed(() => {
  const { width, height } = frameMetrics.value
  return `0 0 ${Math.max(width, 1)} ${Math.max(height, 1)}`
})
const framePath = computed(() => {
  const { width, height, radius } = frameMetrics.value
  if (width <= 0 || height <= 0) {
    return ''
  }

  const inset = 0.5
  const x = inset
  const y = inset
  const w = Math.max(width - inset * 2, 0)
  const h = Math.max(height - inset * 2, 0)
  const right = x + w
  const bottom = y + h
  const corner = Math.max(0, Math.min(radius, w / 2, h / 2))

  if (corner === 0) {
    return `M ${x} ${y} H ${right} V ${bottom} H ${x} Z`
  }

  return [
    `M ${x + corner} ${y}`,
    `H ${right - corner}`,
    `A ${corner} ${corner} 0 0 1 ${right} ${y + corner}`,
    `V ${bottom - corner}`,
    `A ${corner} ${corner} 0 0 1 ${right - corner} ${bottom}`,
    `H ${x + corner}`,
    `A ${corner} ${corner} 0 0 1 ${x} ${bottom - corner}`,
    `V ${y + corner}`,
    `A ${corner} ${corner} 0 0 1 ${x + corner} ${y}`
  ].join(' ')
})

function getRadius(value: string) {
  const parsed = Number.parseFloat(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function updateFrameMetrics() {
  if (typeof window === 'undefined') {
    return
  }

  const el = marqueeRootRef.value
  if (!el) {
    return
  }

  const rect = el.getBoundingClientRect()
  const style = window.getComputedStyle(el)
  const radius = getRadius(style.borderTopLeftRadius)

  frameMetrics.value = {
    width: rect.width,
    height: rect.height,
    radius
  }
}

function stopFrameObserver() {
  frameResizeObserver?.disconnect()
  frameResizeObserver = null
}

function bindFrameObserver() {
  if (typeof ResizeObserver === 'undefined' || !marqueeRootRef.value) {
    return
  }
  stopFrameObserver()
  frameResizeObserver = new ResizeObserver(() => {
    updateFrameMetrics()
  })
  frameResizeObserver.observe(marqueeRootRef.value)
}

async function refreshFrameMetrics() {
  await nextTick()
  updateFrameMetrics()
  requestAnimationFrame(() => {
    updateFrameMetrics()
  })
  bindFrameObserver()
}

function syncDismissedState() {
  if (typeof window === 'undefined') {
    dismissed.value = false
    return
  }

  try {
    dismissed.value = window.localStorage.getItem(storageKey) === noticeSignature.value
  } catch {
    dismissed.value = false
  }
}

function stopRotation() {
  if (timerId.value === null) {
    return
  }
  window.clearInterval(timerId.value)
  timerId.value = null
}

function startRotation() {
  if (typeof window === 'undefined') {
    return
  }

  stopRotation()
  const intervalMs = effectiveIntervalMs.value

  timerId.value = window.setInterval(() => {
    if (paused.value) {
      return
    }
    const list = notices.value
    if (list.length <= 1) {
      activeIndex.value = 0
      return
    }
    activeIndex.value = (activeIndex.value + 1) % list.length
  }, intervalMs)
}

function handleMouseEnter() {
  paused.value = true
}

function handleMouseLeave() {
  paused.value = false
}

function handleClose() {
  dismissed.value = true
  stopRotation()

  if (typeof window === 'undefined') {
    return
  }

  try {
    window.localStorage.setItem(storageKey, noticeSignature.value)
  } catch {
    // ignore storage errors in private mode or restricted environments
  }
}

watch(noticeSignature, () => {
  activeIndex.value = 0
  paused.value = false
  syncDismissedState()
  if (showMarquee.value) {
    startRotation()
  } else {
    stopRotation()
  }
}, { immediate: true })

watch(showMarquee, (next) => {
  if (next) {
    paused.value = false
    startRotation()
    void refreshFrameMetrics()
  } else {
    stopRotation()
    stopFrameObserver()
  }
})

onMounted(() => {
  void refreshFrameMetrics()

  if (showMarquee.value) {
    startRotation()
  }
})

onBeforeUnmount(() => {
  stopRotation()
  stopFrameObserver()
})
</script>

<style lang="scss" scoped>
.marquee-notice {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 0.72rem;
  width: 100%;
  min-width: 0;
  min-height: 4.2rem;
  padding: 0.9rem 1rem 0.9rem 1.08rem;
  border-radius: calc(var(--border-radius-xl) + 8px);
  border: 1px solid color-mix(in srgb, var(--border-panel-default) 82%, transparent);
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--surface-panel-strong) 92%, transparent),
      color-mix(in srgb, var(--surface-panel-soft) 88%, transparent)
    ),
    radial-gradient(circle at 0% 20%, color-mix(in srgb, var(--primary-color) 5%, transparent), transparent 28%);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 84%, transparent),
    var(--highlight-panel-soft),
    var(--shadow-panel-default),
    0 18px 36px color-mix(in srgb, var(--primary-color) 6%, transparent);
  overflow: hidden;
}

.marquee-link {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 0.82rem;
  flex: 1;
  min-width: 0;
  text-decoration: none;
  color: inherit;
  border-radius: inherit;
  outline: none;
  transition:
    box-shadow var(--transition-base),
    transform var(--transition-base);
}

.marquee-link:focus-visible {
  box-shadow:
    0 0 0 2px color-mix(in srgb, var(--primary-color) 24%, transparent),
    0 0 0 6px color-mix(in srgb, var(--primary-color) 8%, transparent);
}

.marquee-link:hover .marquee-item {
  color: color-mix(in srgb, var(--primary-color) 28%, var(--text-primary));
}

.marquee-link:hover .marquee-signet__icon {
  transform: translateY(-1px);
  box-shadow:
    0 10px 22px color-mix(in srgb, var(--primary-color) 10%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 86%, transparent);
}

.marquee-notice::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, color-mix(in srgb, var(--primary-color) 4%, transparent), transparent 38%),
    radial-gradient(circle at 88% 0%, color-mix(in srgb, var(--secondary-color) 5%, transparent), transparent 24%);
  pointer-events: none;
  border-radius: inherit;
}

.marquee-signet {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}

.marquee-signet__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.45rem;
  height: 2.45rem;
  border-radius: calc(var(--border-radius-lg) + 4px);
  color: color-mix(in srgb, var(--primary-color) 86%, var(--title-color));
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--surface-panel-chip-accent) 88%, transparent),
      color-mix(in srgb, var(--surface-panel-strong) 84%, transparent)
    ),
    radial-gradient(circle at 32% 24%, color-mix(in srgb, var(--surface-white-18) 84%, transparent), transparent 44%);
  box-shadow:
    0 8px 18px color-mix(in srgb, var(--primary-color) 8%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 84%, transparent);
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base);
}

.marquee-copy {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
  min-height: 1.88rem;
  overflow: hidden;
  padding-right: 0.08rem;
}

.marquee-frame {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  overflow: hidden;
  pointer-events: none;
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 24%, transparent);
}

.marquee-notice--frame-fallback .marquee-frame {
  box-shadow:
    inset 0 0 0 1px color-mix(in srgb, var(--border-panel-default) 72%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 24%, transparent);
}

.marquee-frame__svg {
  display: block;
  width: 100%;
  height: 100%;
  overflow: visible;
}

.marquee-frame__track,
.marquee-frame__runner {
  fill: none;
  shape-rendering: geometricPrecision;
}

.marquee-frame__track {
  stroke: color-mix(in srgb, var(--border-panel-default) 74%, transparent);
  stroke-width: 1;
  opacity: 0.88;
}

.marquee-frame__runner {
  stroke: color-mix(in srgb, var(--primary-color) 78%, var(--secondary-color));
  stroke-width: 1.15;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 16 84;
  stroke-dashoffset: 0;
  filter: drop-shadow(0 0 4px color-mix(in srgb, var(--primary-color) 16%, transparent));
  animation: marquee-frame-run var(--marquee-interval-ms, 3600ms) linear infinite;
}

.marquee-notice.is-paused .marquee-frame__runner,
.marquee-notice:focus-within .marquee-frame__runner {
  animation-play-state: paused;
}

.marquee-swap-enter-active,
.marquee-swap-leave-active {
  transition:
    opacity 320ms cubic-bezier(0.22, 1, 0.36, 1),
    transform 320ms cubic-bezier(0.22, 1, 0.36, 1),
    filter 320ms cubic-bezier(0.22, 1, 0.36, 1);
  will-change: opacity, transform, filter;
}

.marquee-swap-enter-from {
  opacity: 0;
  filter: blur(6px);
  transform: translateY(8px);
}

.marquee-swap-leave-to {
  opacity: 0;
  filter: blur(6px);
  transform: translateY(-8px);
}

.marquee-close {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.05rem;
  height: 2.05rem;
  flex-shrink: 0;
  border: none;
  border-radius: calc(var(--border-radius-lg) + 2px);
  background: color-mix(in srgb, var(--surface-white-08) 56%, transparent);
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    color var(--transition-base),
    transform var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);
}

.marquee-close:hover {
  color: var(--primary-color);
  transform: translateY(-1px);
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 68%, transparent);
  box-shadow:
    0 10px 20px color-mix(in srgb, var(--primary-color) 8%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 82%, transparent);
}

.marquee-item {
  position: relative;
  z-index: 1;
  display: block;
  width: 100%;
  padding-left: 0.5rem;
  background: none;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: color-mix(in srgb, var(--text-primary) 84%, var(--text-secondary));
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: 0.012em;
  line-height: 1.45;
}

@media (max-width: 720px) {
  .marquee-notice {
    align-items: center;
    min-height: 3.92rem;
    padding: 0.74rem 0.82rem 0.74rem 0.88rem;
  }

  .marquee-link {
    gap: 0.72rem;
  }

  .marquee-signet__icon {
    width: 2.18rem;
    height: 2.18rem;
  }

  .marquee-item {
    font-size: 0.84rem;
  }

  .marquee-close {
    width: 1.9rem;
    height: 1.9rem;
  }
}

@keyframes marquee-frame-run {
  0% {
    stroke-dashoffset: 0;
  }

  100% {
    stroke-dashoffset: -100;
  }
}

</style>
