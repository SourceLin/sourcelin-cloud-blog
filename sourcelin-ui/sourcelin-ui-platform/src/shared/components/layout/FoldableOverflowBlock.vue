<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = withDefaults(
  defineProps<{
    /** 用于列表项数量变化时收起并重测（如标签/分类条数） */
    itemCount: number
    /** 折叠态按钮文案 */
    expandButtonText: string
    /** 展开态按钮文案 */
    collapseButtonText: string
    /** 折叠可见区高度预算（px），非按行精确裁切 */
    foldedVisibleMaxHeightPx?: number
    /** 与 scrollHeight 比较的容差 */
    overflowEpsilonPx?: number
    /** 收起后将锚点滚回视口时解析外壳元素（如 switch-shell section） */
    getScrollAnchor?: () => HTMLElement | null
  }>(),
  {
    foldedVisibleMaxHeightPx: 500,
    overflowEpsilonPx: 2
  }
)

const viewportRef = ref<HTMLElement | null>(null)
const measureRef = ref<HTMLElement | null>(null)
const expanded = ref(false)
const overflow = ref(false)

let resizeObserver: ResizeObserver | null = null

const cssVars = computed(() => ({
  '--foldable-overflow-folded-max-height': `${props.foldedVisibleMaxHeightPx}px`
}))

const toggleLabel = computed(() =>
  expanded.value ? props.collapseButtonText : props.expandButtonText
)

async function measureOverflow() {
  await nextTick()
  const el = measureRef.value
  if (!el) {
    overflow.value = false
    return
  }
  overflow.value =
    el.scrollHeight > props.foldedVisibleMaxHeightPx + props.overflowEpsilonPx
}

function teardownObservers() {
  resizeObserver?.disconnect()
  resizeObserver = null
}

function setupObservers() {
  teardownObservers()
  void measureOverflow()
  if (typeof ResizeObserver === 'undefined') {
    return
  }
  const ro = new ResizeObserver(() => {
    void measureOverflow()
  })
  resizeObserver = ro
  const inner = measureRef.value
  const outer = viewportRef.value
  if (inner) {
    ro.observe(inner)
  }
  if (outer) {
    ro.observe(outer)
  }
}

function onWindowResize() {
  void measureOverflow()
}

async function scrollAnchorAfterCollapse() {
  const resolveAnchor = props.getScrollAnchor
  if (!resolveAnchor) {
    return
  }
  await nextTick()
  await new Promise<void>((resolve) => {
    requestAnimationFrame(() => resolve())
  })
  const el = resolveAnchor()
  if (!el) {
    return
  }
  if (el.getBoundingClientRect().top < 0) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function onToggleClick() {
  if (expanded.value) {
    expanded.value = false
    void scrollAnchorAfterCollapse()
    return
  }
  expanded.value = true
}

watch(
  () => props.itemCount,
  async () => {
    expanded.value = false
    await nextTick()
    setupObservers()
  },
  { flush: 'post' }
)

onMounted(async () => {
  await nextTick()
  setupObservers()
  window.addEventListener('resize', onWindowResize)
})

onBeforeUnmount(() => {
  teardownObservers()
  window.removeEventListener('resize', onWindowResize)
})
</script>

<template>
  <div class="foldable-overflow-block">
    <div
      ref="viewportRef"
      class="foldable-overflow-block__viewport"
      :class="{ 'foldable-overflow-block__viewport--clamped': overflow && !expanded }"
      :style="cssVars"
    >
      <div ref="measureRef" class="foldable-overflow-block__measure">
        <slot />
      </div>
      <div
        v-if="overflow && !expanded"
        class="foldable-overflow-block__hint"
        aria-hidden="true"
      />
    </div>
    <button
      v-if="overflow"
      type="button"
      class="foldable-overflow-block__toggle"
      :aria-expanded="expanded"
      @click="onToggleClick"
    >
      <SIcon :icon="expanded ? appIcons.chevronUp : appIcons.chevronDown" :size="16" />
      <span>{{ toggleLabel }}</span>
    </button>
  </div>
</template>

<style scoped lang="scss">
.foldable-overflow-block {
  display: grid;
  gap: 0;
  width: 100%;
}

.foldable-overflow-block__viewport {
  position: relative;
  width: 100%;
}

.foldable-overflow-block__viewport--clamped {
  max-height: var(--foldable-overflow-folded-max-height);
  overflow: hidden;
  transition: max-height 0.38s ease;
}

.foldable-overflow-block__measure {
  min-width: 0;
}

.foldable-overflow-block__hint {
  pointer-events: none;
  position: absolute;
  inset: auto 0 0;
  height: 2rem;
  background: linear-gradient(
    180deg,
    transparent 0%,
    transparent 42%,
    color-mix(in srgb, var(--bg-page) 14%, transparent) 100%
  );
}

.foldable-overflow-block__toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  width: fit-content;
  max-width: 100%;
  margin-top: var(--spacing-md);
  margin-inline: auto;
  padding: 0.32rem 0.75rem;
  border-radius: var(--border-radius-md);
  border: 1px solid color-mix(in srgb, var(--border-color) 78%, transparent);
  background: color-mix(in srgb, var(--surface-panel-soft) 55%, transparent);
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  font-weight: 500;
  letter-spacing: 0.02em;
  cursor: pointer;
  transition:
    border-color var(--transition-base),
    background var(--transition-base),
    color var(--transition-base);
}

.foldable-overflow-block__toggle:hover {
  border-color: color-mix(in srgb, var(--primary-color) 28%, var(--border-color));
  color: var(--primary-color);
  background: color-mix(in srgb, var(--primary-light) 55%, var(--surface-panel-soft));
}

.foldable-overflow-block__toggle:focus-visible {
  outline: 2px solid color-mix(in srgb, var(--primary-color) 45%, transparent);
  outline-offset: 2px;
}
</style>
