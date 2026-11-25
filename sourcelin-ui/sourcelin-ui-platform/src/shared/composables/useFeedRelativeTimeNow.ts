import { createSharedComposable, useIntervalFn } from '@vueuse/core'
import { shallowRef } from 'vue'

const RELATIVE_TIME_TICK_MS = 30_000

/**
 * 说说流等列表页共用：单一 `setInterval` 驱动「相对发布时间」重算，避免每张卡片各挂定时器。
 */
export const useFeedRelativeTimeNow = createSharedComposable(() => {
  const now = shallowRef(new Date())
  useIntervalFn(() => {
    now.value = new Date()
  }, RELATIVE_TIME_TICK_MS)
  return now
})
