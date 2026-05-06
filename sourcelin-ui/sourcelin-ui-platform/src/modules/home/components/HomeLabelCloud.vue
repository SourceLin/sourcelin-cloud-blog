<template>
  <div class="label-card" :style="cloudStyle">
    <div class="label-head">
      <router-link class="label-head__link" :to="{ name: 'Tag' }" aria-label="进入文章标签页面">
        <span class="label-head__orb" aria-hidden="true">
          <span class="label-head__orb-core" />
        </span>
        <SGradientText as="span" class="label-head__title" tone="glass-strong">
          标签云
        </SGradientText>
      </router-link>
      <span class="label-head__bridge" aria-hidden="true" />
    </div>

    <div ref="sphereRef" class="label-sphere" :class="{ 'is-dense': dense }">
      <div class="sphere-glow" />
      <div class="sphere-orbit sphere-orbit-1" />
      <div class="sphere-orbit sphere-orbit-2" />
      <div class="sphere-orbit sphere-orbit-3" />

      <button
        v-for="tag in computedTags"
        :key="tag.id"
        type="button"
        class="label-chip"
        :class="[`label-chip-size-${tag.size}`, `label-chip-weight-${tag.weightLevel}`]"
        :style="chipStyle(tag)"
        @click="emit('openTag', tag.name)"
      >
        <span class="label-chip-icon">#</span>
        <SGradientText as="span" class="label-chip-text" tone="glass-soft">
          {{ tag.name }}
        </SGradientText>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import SGradientText from '@/shared/components/ui/SGradientText.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Tag {
  id: number
  name: string
  weight?: number
  color?: string
}

interface ComputedTag extends Tag {
  size: string
  weightLevel: number
  colorValue: string
  x: number
  y: number
  scale: number
  alpha: number
  zIndex: number
}

interface LayoutTag extends Tag {
  size: string
  weightLevel: number
  colorValue: string
  baseX: number
  baseY: number
  scale: number
  alpha: number
  zIndex: number
}

interface Props {
  tags?: Tag[]
  maxHeight?: string | number
  dense?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  tags: () => [],
  maxHeight: '40vh',
  dense: false
})

const emit = defineEmits<{
  openTag: [name: string]
}>()

const sphereRef = ref<HTMLElement | null>(null)
const sphereAngle = ref(0)
const sphereMetrics = ref({ width: 312, height: 340 })
let frameId: number | null = null
let lastTimestamp = 0
let resizeObserver: ResizeObserver | null = null

function clamp(value: number, min: number, max: number): number {
  return Math.min(Math.max(value, min), max)
}

function clampToEllipse(x: number, y: number, radiusX: number, radiusY: number) {
  const dx = x - 50
  const dy = y - 50
  const ellipse = (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY)

  if (ellipse <= 1) {
    return { x, y }
  }

  const scale = 1 / Math.sqrt(ellipse)
  return {
    x: 50 + dx * scale,
    y: 50 + dy * scale
  }
}

function updateSphereMetrics() {
  const element = sphereRef.value

  if (!element) {
    return
  }

  const { width, height } = element.getBoundingClientRect()

  if (width <= 0 || height <= 0) {
    return
  }

  sphereMetrics.value = { width, height }
}

const maxVisibleTags = computed(() => {
  const total = props.tags.length

  if (!total) {
    return 0
  }

  const area = sphereMetrics.value.width * sphereMetrics.value.height
  const densityFactor = props.dense ? 1.14 : 1.08
  const chipArea = props.dense ? 5000 : 5400
  const estimated = Math.floor((area * densityFactor) / chipArea)
  const lowerBound = props.dense ? 14 : 12
  const upperBound = props.dense ? 28 : 24

  return clamp(estimated, lowerBound, Math.min(total, upperBound))
})

const visibleTags = computed(() => (
  [...props.tags]
    .sort((left, right) => (right.weight || 1) - (left.weight || 1))
    .slice(0, maxVisibleTags.value)
))

const layoutTags = computed<LayoutTag[]>(() => {
  if (!visibleTags.value.length) {
    return []
  }

  const weights = visibleTags.value.map((tag) => tag.weight || 1)
  const min = Math.min(...weights)
  const max = Math.max(...weights)
  const range = max - min || 1
  const total = visibleTags.value.length
  const goldenAngle = Math.PI * (3 - Math.sqrt(5))
  const radiusX = props.dense ? 47 : 44
  const radiusY = props.dense ? 40 : 37
  const placedTags: Array<{ x: number; y: number; collisionRadius: number }> = []

  return visibleTags.value.map((tag, index) => {
    const normalized = ((tag.weight || 1) - min) / range
    const size = normalized > 0.66 ? 'lg' : normalized > 0.33 ? 'md' : 'sm'
    const weightLevel = normalized > 0.66 ? 3 : normalized > 0.33 ? 2 : 1
    const seed = (tag.id * 9301 + 49297) % 233280
    const rand = seed / 233280
    const depth = 0.42 + rand * 0.58
    const alpha = 0.36 + normalized * 0.38
    const radialProgress = (index + 0.72) / (total + 0.72)
    const centerBias = Math.pow(radialProgress, props.dense ? 1.72 : 1.48)
    const angleOffset = index * goldenAngle + rand * 0.35
    const weightPull = 1 - normalized * 0.2
    const radius = (12 + centerBias * 28) * weightPull + rand * 2.8
    let x = 50 + Math.cos(angleOffset) * radius
    let y = 50 + Math.sin(angleOffset) * radius * 0.82
    const collisionRadius = 4.9 + weightLevel * 1.25 + normalized * 1.45 + (props.dense ? 0.55 : 0)

    for (let iteration = 0; iteration < 3; iteration += 1) {
      for (const placed of placedTags) {
        const dx = x - placed.x
        const dy = y - placed.y
        const distance = Math.hypot(dx, dy) || 0.001
        const minDistance = collisionRadius + placed.collisionRadius

        if (distance >= minDistance) {
          continue
        }

        const push = (minDistance - distance) * 0.5
        x += (dx / distance) * push
        y += (dy / distance) * push
      }

      const ellipsePoint = clampToEllipse(x, y, radiusX, radiusY)
      x = ellipsePoint.x
      y = ellipsePoint.y

      x = 50 + (x - 50) * 0.996
      y = 50 + (y - 50) * 0.996
    }

    placedTags.push({ x, y, collisionRadius })
    const scale = 0.92 + depth * 0.18
    const zIndex = Math.round(10 + depth * 50 + normalized * 6)

    return {
      ...tag,
      size,
      weightLevel,
      colorValue: tag.color || 'var(--primary-color)',
      baseX: clamp(x, 1, 99),
      baseY: clamp(y, 3, 97),
      scale,
      alpha,
      zIndex
    }
  })
})

const computedTags = computed<ComputedTag[]>(() => {
  if (!layoutTags.value.length) {
    return []
  }

  const angle = sphereAngle.value
  const cosA = Math.cos(angle)
  const sinA = Math.sin(angle)

  return layoutTags.value.map((tag) => {
    const dx = tag.baseX - 50
    const dy = tag.baseY - 50
    const x = 50 + dx * cosA - dy * sinA
    const y = 50 + dx * sinA + dy * cosA

    return {
      ...tag,
      x: clamp(x, -4, 104),
      y: clamp(y, -2, 102)
    }
  })
})

const cloudStyle = computed(() => {
  const maxHeight = typeof props.maxHeight === 'number'
    ? `${props.maxHeight}px`
    : props.maxHeight

  return {
    '--label-cloud-max-height': maxHeight
  }
})

function chipStyle(tag: ComputedTag) {
  return {
    '--chip-color': tag.colorValue,
    '--chip-scale': String(tag.scale),
    left: `${tag.x}%`,
    top: `${tag.y}%`,
    opacity: `${tag.alpha}`,
    zIndex: String(tag.zIndex)
  }
}

function animate(timestamp: number) {
  if (!lastTimestamp) {
    lastTimestamp = timestamp
  }

  const delta = timestamp - lastTimestamp
  lastTimestamp = timestamp

  sphereAngle.value += delta * 0.00011
  if (sphereAngle.value > Math.PI * 2) {
    sphereAngle.value -= Math.PI * 2
  }

  frameId = window.requestAnimationFrame(animate)
}

onMounted(() => {
  updateSphereMetrics()

  if (typeof ResizeObserver !== 'undefined' && sphereRef.value) {
    resizeObserver = new ResizeObserver(() => {
      updateSphereMetrics()
    })
    resizeObserver.observe(sphereRef.value)
  }

  frameId = window.requestAnimationFrame(animate)
})

onUnmounted(() => {
  if (frameId !== null) {
    cancelAnimationFrame(frameId)
  }

  lastTimestamp = 0

  resizeObserver?.disconnect()
  resizeObserver = null
})
</script>

<style scoped lang="scss">
.label-card {
  width: 100%;
  padding: 0.85rem 0.85rem 0.9rem;
  border-radius: calc(var(--border-radius-xl) + 4px);
  border: 1px solid var(--border-content-card);
  background:
    var(--surface-panel-specular-soft),
    var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  transition:
    transform 0.45s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.45s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.45s cubic-bezier(0.4, 0, 0.2, 1);
}

.label-head {
  margin-bottom: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.label-head__link {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  text-decoration: none;
  color: inherit;
  transition: all var(--transition-base);
  padding: 0.25rem 0;
}

.label-head__link:hover {
  color: var(--primary-color);
}

.label-head__orb {
  flex-shrink: 0;
  width: 0.72rem;
  height: 0.72rem;
  border-radius: 50%;
  background: radial-gradient(
    circle at 35% 35%,
    color-mix(in srgb, var(--primary-color) 65%, var(--surface-white-12)),
    color-mix(in srgb, var(--primary-color) 85%, transparent)
  );
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 90%, transparent),
    0 2px 8px color-mix(in srgb, var(--primary-color) 20%, transparent);
  transition: all var(--transition-base);
}

.label-head__orb-core {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--primary-color) 70%, var(--surface-white-18)),
    color-mix(in srgb, var(--primary-color) 90%, transparent)
  );
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 95%, transparent);
}

.label-head__link:hover .label-head__orb {
  transform: scale(1.08);
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 95%, transparent),
    0 3px 12px color-mix(in srgb, var(--primary-color) 28%, transparent);
}

.label-head__title {
  font-size: 0.88rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--text-secondary);
  transition: color var(--transition-base);
  background: none;
  -webkit-text-fill-color: currentColor;
}

.label-head__bridge {
  display: block;
  width: 100%;
  height: 1px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--primary-color) 75%, transparent) 0%,
    color-mix(in srgb, var(--primary-color) 45%, transparent) 40%,
    transparent 100%
  );
  opacity: 0.72;
}

.label-chip-text {
  color: var(--text-primary);
  background: none;
  -webkit-text-fill-color: currentColor;
}

.label-sphere {
  position: relative;
  width: 100%;
  min-height: var(--label-cloud-max-height);
  overflow: hidden;
  border-radius: calc(var(--border-radius-xl) + 1px);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--primary-color) 8%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent)
    ),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--surface-white-15) 70%, transparent), transparent 54%),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--primary-color) 6%, transparent), transparent 44%),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-05) 90%, transparent), transparent);
  border: 1px solid color-mix(in srgb, var(--glass-border) 74%, transparent);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-16) 84%, transparent),
    0 8px 18px color-mix(in srgb, var(--background-color-deep) 5%, transparent);
}

.sphere-glow,
.sphere-orbit {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.sphere-glow {
  background:
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--primary-color) 12%, transparent) 0%, transparent 34%),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--secondary-color) 7%, transparent) 0%, transparent 52%);
  filter: blur(10px);
  opacity: 0.75;
}

.sphere-orbit {
  border-radius: 50%;
  border: 1px solid color-mix(in srgb, var(--glass-border) 60%, transparent);
  opacity: 0.38;
}

.sphere-orbit-1 {
  inset: 22% 20%;
}

.sphere-orbit-2 {
  inset: 12% 11%;
}

.sphere-orbit-3 {
  inset: 31% 27%;
}

.label-chip {
  position: absolute;
  display: inline-flex;
  align-items: center;
  gap: 0.34rem;
  padding: 0.46rem 0.72rem;
  border-radius: 999px;
  border: 1px solid color-mix(in srgb, var(--chip-color) 16%, var(--glass-border));
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--chip-color) 10%, transparent), color-mix(in srgb, var(--surface-white-05) 88%, transparent));
  color: var(--text-primary);
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
  cursor: pointer;
  backdrop-filter: blur(calc(var(--glass-blur) - 3px));
  box-shadow:
    0 8px 18px color-mix(in srgb, var(--background-color-deep) 8%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-18) 90%, transparent);
  transform: translate(-50%, -50%) scale(var(--chip-scale, 1));
  transition:
    transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  appearance: none;
  text-align: left;
}

.label-chip:hover {
  transform: translate(-50%, -50%) scale(calc(var(--chip-scale, 1) * 1.05));
  border-color: color-mix(in srgb, var(--chip-color) 28%, var(--glass-border));
  box-shadow:
    0 12px 24px color-mix(in srgb, var(--background-color-deep) 12%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-22) 92%, transparent);
}

.label-chip-size-lg {
  font-size: 0.92rem;
}

.label-chip-size-md {
  font-size: 0.84rem;
}

.label-chip-size-sm {
  font-size: 0.76rem;
}

.label-chip-icon {
  font-size: 0.72rem;
  color: var(--chip-color);
}

.label-chip-weight-3 {
  box-shadow:
    0 10px 22px color-mix(in srgb, var(--background-color-deep) 12%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-22) 92%, transparent);
}

.label-chip-weight-2 {
  opacity: 0.92;
}

.label-chip-weight-1 {
  opacity: 0.82;
}

@media (max-width: 720px) {
  .label-card {
    padding: 0.8rem;
  }

  .label-sphere {
    min-height: 17.5rem;
  }

  .label-chip {
    padding: 0.42rem 0.64rem;
  }
}
</style>
