import { onMounted, onUnmounted, ref } from 'vue'

/** 与首页 masthead 一致：随页面滚动增加背景模糊，突出向内容区过渡 */
const MAX_BLUR_PX = 5
const SCROLL_RANGE_PX = 500

export function useMastheadScrollBlur() {
  const blurPx = ref(0)

  const syncBlur = () => {
    const scrollTop = window.scrollY || document.documentElement.scrollTop
    blurPx.value = Math.min((scrollTop / SCROLL_RANGE_PX) * MAX_BLUR_PX, MAX_BLUR_PX)
  }

  onMounted(() => {
    syncBlur()
    window.addEventListener('scroll', syncBlur, { passive: true })
  })

  onUnmounted(() => {
    window.removeEventListener('scroll', syncBlur)
  })

  return { blurPx, maxBlurPx: MAX_BLUR_PX, scrollRangePx: SCROLL_RANGE_PX }
}
