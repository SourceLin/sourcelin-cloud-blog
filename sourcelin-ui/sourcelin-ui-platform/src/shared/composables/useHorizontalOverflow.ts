import { nextTick, onBeforeUnmount, onMounted, ref, type Ref } from 'vue'

interface UseHorizontalOverflowOptions {
  overflowThreshold?: number
  edgeThreshold?: number
  minStep?: number
  stepRatio?: number
}

export function useHorizontalOverflow(
  viewportRef: Ref<HTMLElement | null>,
  options: UseHorizontalOverflowOptions = {}
) {
  const overflowThreshold = options.overflowThreshold ?? 12
  const edgeThreshold = options.edgeThreshold ?? 6
  const minStep = options.minStep ?? 220
  const stepRatio = options.stepRatio ?? 0.72

  const hasOverflow = ref(false)
  const canScrollPrev = ref(false)
  const canScrollNext = ref(false)

  let resizeObserver: ResizeObserver | null = null

  function updateScrollState() {
    const viewport = viewportRef.value
    if (!viewport) {
      hasOverflow.value = false
      canScrollPrev.value = false
      canScrollNext.value = false
      return
    }

    const maxScrollLeft = Math.max(viewport.scrollWidth - viewport.clientWidth, 0)
    const overflow = maxScrollLeft > overflowThreshold

    hasOverflow.value = overflow
    canScrollPrev.value = overflow && viewport.scrollLeft > edgeThreshold
    canScrollNext.value = overflow && viewport.scrollLeft < maxScrollLeft - edgeThreshold
  }

  function scrollTrack(direction: 1 | -1) {
    const viewport = viewportRef.value
    if (!viewport) {
      return
    }

    const delta = Math.max(Math.round(viewport.clientWidth * stepRatio), minStep) * direction
    viewport.scrollBy({
      left: delta,
      behavior: 'smooth'
    })
  }

  function bindScrollState() {
    const viewport = viewportRef.value
    if (!viewport) {
      return () => {}
    }

    const handleScroll = () => updateScrollState()
    viewport.addEventListener('scroll', handleScroll, { passive: true })

    if (typeof ResizeObserver !== 'undefined') {
      resizeObserver = new ResizeObserver(() => updateScrollState())
      resizeObserver.observe(viewport)

      const track = viewport.firstElementChild
      if (track) {
        resizeObserver.observe(track)
      }
    }

    return () => {
      viewport.removeEventListener('scroll', handleScroll)
      resizeObserver?.disconnect()
      resizeObserver = null
    }
  }

  let cleanup = () => {}

  async function refreshScrollState() {
    await nextTick()
    updateScrollState()
  }

  onMounted(async () => {
    await nextTick()
    updateScrollState()
    cleanup = bindScrollState()
  })

  onBeforeUnmount(() => {
    cleanup()
  })

  return {
    hasOverflow,
    canScrollPrev,
    canScrollNext,
    updateScrollState,
    refreshScrollState,
    scrollTrack
  }
}
