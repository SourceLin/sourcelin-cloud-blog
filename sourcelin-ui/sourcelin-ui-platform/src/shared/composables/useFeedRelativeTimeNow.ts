import { createSharedComposable, useNow } from '@vueuse/core'

const RELATIVE_TIME_TICK_MS = 30_000

/**
 * 说说流等列表页共用：单一 `setInterval` 驱动「相对发布时间」重算，避免每张卡片各挂定时器。
 */
export const useFeedRelativeTimeNow = createSharedComposable(() =>
  useNow({ interval: RELATIVE_TIME_TICK_MS })
)
