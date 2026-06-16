<template>
  <div ref="containerRef" class="barrage">
    <div
      v-for="item in activeBarrages"
      :id="String(item.key)"
      :key="item.key"
      class="barrage-item"
      :style="item.style"
      @animationend="onAnimationEnd(item.key)"
    >
      <img
        class="barrage-avatar"
        :src="item.avatar"
        :alt="item.nickname || '匿名洞友'"
        @error="onAvatarError"
      >
      <span class="barrage-text">{{ item.msg }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import defaultAvatar from '@/assets/images/logo/logo.png'

interface BarrageInputItem {
  id: string | number
  msg: string
  avatar?: string
  nickname?: string
  time?: number
}

interface ActiveBarrage {
  key: string
  id: string | number
  msg: string
  avatar: string
  nickname: string
  style: {
    top: string
    animationDuration: string
  }
  trackId: number
  width: number
  startTime: number
  duration: number // 位移秒数
}

const props = defineProps<{
  list: BarrageInputItem[]
}>()

const containerRef = ref<HTMLElement | null>(null)
const activeBarrages = ref<ActiveBarrage[]>([])
const barrageQueue = ref<BarrageInputItem[]>([])

let dispatchTimer: ReturnType<typeof setInterval> | null = null
let containerWidth = 1200
let containerHeight = 300
let trackCount = 6

interface TrackState {
  lastItemKey: string | null
}
const trackStates = ref<TrackState[]>([])

// 获取头像加载失败兜底
const onAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement | null
  if (!target) return
  target.src = defaultAvatar
}

// 初始化轨道
const initTracks = () => {
  if (!containerRef.value) return
  containerWidth = containerRef.value.offsetWidth || 1200
  containerHeight = containerRef.value.offsetHeight || 300
  // 每条轨道高度设为 48px
  trackCount = Math.max(1, Math.floor(containerHeight / 48))

  trackStates.value = Array.from({ length: trackCount }, () => ({
    lastItemKey: null
  }))
}

// 防碰撞轨道计算核心算法
const getSafeTrackIndex = (): number => {
  const now = Date.now()
  const safeTracks: number[] = []

  for (let i = 0; i < trackCount; i++) {
    const track = trackStates.value[i]
    if (!track.lastItemKey) {
      safeTracks.push(i)
      continue
    }

    const lastActiveItem = activeBarrages.value.find((item) => item.key === track.lastItemKey)
    if (!lastActiveItem) {
      safeTracks.push(i)
      continue
    }

    // 经典匀速滑行位移模型计算前车当前右端 x 坐标 (以 containerWidth 为起滑点自右向左跑)
    const totalDist = containerWidth + lastActiveItem.width
    const speed = totalDist / (lastActiveItem.duration * 1000)
    const elapsed = now - lastActiveItem.startTime
    const currentDist = speed * elapsed
    const xRight = containerWidth - currentDist + lastActiveItem.width

    // 安全阀值：前车屁股已完全飞入视口且额外留出 100px 间距，判定该轨道安全可放飞
    if (xRight < containerWidth - 100) {
      safeTracks.push(i)
    }
  }

  if (safeTracks.length === 0) {
    return -1
  }
  // 随机选择一条安全轨道，防止弹幕密集扎堆在首行
  return safeTracks[Math.floor(Math.random() * safeTracks.length)]
}

// 弹幕分发派发器
const dispatchBarrage = () => {
  if (barrageQueue.value.length === 0 || activeBarrages.value.length >= 10) {
    return
  }

  const trackId = getSafeTrackIndex()
  if (trackId === -1) {
    return
  }

  const rawItem = barrageQueue.value.shift()!
  const key = `brg-${Date.now()}-${Math.floor(Math.random() * 10000)}`
  const duration = Math.max(rawItem.time ?? 8, 5) // 位移时长

  const initialWidth = 240 // 初始占位预估宽度
  const topPx = trackId * 48 + 8 // 每行加上 8px 的内边距对齐

  const activeItem: ActiveBarrage = {
    key,
    id: rawItem.id,
    msg: rawItem.msg,
    avatar: rawItem.avatar || defaultAvatar,
    nickname: rawItem.nickname || '匿名洞友',
    trackId,
    width: initialWidth,
    startTime: Date.now(),
    duration,
    style: {
      top: `${topPx}px`,
      animationDuration: `${duration}s`
    }
  }

  trackStates.value[trackId].lastItemKey = key
  activeBarrages.value.push(activeItem)

  // 挂载至 DOM 后动态获取高精度真实宽度并修正碰撞阈值
  nextTick(() => {
    const el = document.getElementById(key)
    if (el) {
      activeItem.width = el.offsetWidth || initialWidth
    }
  })
}

// 动画播放完毕触发垃圾回收注销
const onAnimationEnd = (key: string) => {
  const index = activeBarrages.value.findIndex((item) => item.key === key)
  if (index !== -1) {
    const item = activeBarrages.value[index]
    activeBarrages.value.splice(index, 1)

    if (trackStates.value[item.trackId]?.lastItemKey === key) {
      trackStates.value[item.trackId].lastItemKey = null
    }
  }
}

// 监测 Props 传值
watch(
  () => props.list,
  (newList) => {
    if (!newList || newList.length === 0) return

    const activeIds = new Set(activeBarrages.value.map((item) => item.id))
    const toAdd = newList.filter((item) => !activeIds.has(item.id))

    toAdd.forEach((item) => {
      // 区分用户在前端新发布的本地插播留言，通过 unshift 获得最高优先级
      if (String(item.id).startsWith('local-')) {
        barrageQueue.value.unshift(item)
      } else {
        if (!barrageQueue.value.some((q) => q.id === item.id)) {
          barrageQueue.value.push(item)
        }
      }
    })
  },
  { immediate: true, deep: true }
)

const handleResize = () => {
  initTracks()
}

onMounted(() => {
  initTracks()
  window.addEventListener('resize', handleResize)
  // 每隔 600ms 检测分发弹幕
  dispatchTimer = setInterval(dispatchBarrage, 600)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (dispatchTimer) {
    clearInterval(dispatchTimer)
  }
})
</script>

<style scoped>
.barrage {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.barrage-item {
  position: absolute;
  left: 0;
  display: inline-flex;
  align-items: center;
  gap: 0.52rem;
  white-space: nowrap;
  color: var(--text-color-light);
  font-size: 0.98rem;
  font-weight: 500;
  text-shadow: 0 2px 8px var(--surface-black-35);
  animation-name: barrage-move;
  animation-timing-function: linear;
  animation-iteration-count: 1;
  animation-fill-mode: forwards;
  padding: 0.42rem 0.85rem;
  border-radius: 999px;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-12) 82%, transparent),
      color-mix(in srgb, var(--surface-black-25) 94%, transparent)
    );
  border: 1px solid color-mix(in srgb, var(--surface-white-20) 72%, transparent);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-12) 92%, transparent),
    0 10px 22px color-mix(in srgb, var(--surface-black-40) 22%, transparent);
  backdrop-filter: blur(calc(var(--glass-blur) - 6px));
}

.barrage-avatar {
  width: 1.52rem;
  height: 1.52rem;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid color-mix(in srgb, var(--surface-white-25) 82%, transparent);
  box-shadow:
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-20) 68%, transparent),
    0 2px 8px color-mix(in srgb, var(--surface-black-44) 24%, transparent);
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 88%, transparent);
}

.barrage-text {
  line-height: 1;
}

@keyframes barrage-move {
  from {
    transform: translateX(100vw);
  }
  to {
    transform: translateX(-100%);
  }
}
</style>
