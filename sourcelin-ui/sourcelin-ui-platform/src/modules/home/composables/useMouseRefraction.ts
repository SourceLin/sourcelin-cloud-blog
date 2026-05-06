import { ref, onMounted, onUnmounted, type Ref } from 'vue'

/**
 * 鼠标跟随折射 composable
 * 
 * 为水滴玻璃（Drop）变体提供动态折射光斑效果。
 * 光斑随鼠标位置在元素内部轻微偏移（±3px），模拟光线折射。
 * 
 * @param elementRef - 目标元素的 ref
 * @returns CSS 变量对象，用于绑定到元素 style
 * 
 * @example
 * ```vue
 * <template>
 *   <div ref="cardRef" class="sourcelin-glass-drop" :style="refractionStyle">
 *     内容
 *   </div>
 * </template>
 * 
 * <script setup>
 * const cardRef = ref<HTMLElement>()
 * const { refractionStyle } = useMouseRefraction(cardRef)
 * </script>
 * ```
 */
export function useMouseRefraction(elementRef: Ref<HTMLElement | undefined>) {
  const mouseX = ref(50) // 百分比
  const mouseY = ref(50) // 百分比

  let rafId: number | null = null
  let latestX = 0
  let latestY = 0

  const handleMouseMove = (e: MouseEvent) => {
    if (!elementRef.value) return

    const rect = elementRef.value.getBoundingClientRect()
    latestX = ((e.clientX - rect.left) / rect.width) * 100
    latestY = ((e.clientY - rect.top) / rect.height) * 100

    if (rafId === null) {
      rafId = requestAnimationFrame(updateRefraction)
    }
  }

  const updateRefraction = () => {
    mouseX.value = latestX
    mouseY.value = latestY
    rafId = null
  }

  onMounted(() => {
    if (elementRef.value) {
      elementRef.value.addEventListener('mousemove', handleMouseMove)
    }
  })

  onUnmounted(() => {
    if (elementRef.value) {
      elementRef.value.removeEventListener('mousemove', handleMouseMove)
    }
    if (rafId !== null) {
      cancelAnimationFrame(rafId)
    }
  })

  const refractionStyle = {
    '--refraction-x': `${mouseX.value}%`,
    '--refraction-y': `${mouseY.value}%`
  }

  return { refractionStyle }
}
