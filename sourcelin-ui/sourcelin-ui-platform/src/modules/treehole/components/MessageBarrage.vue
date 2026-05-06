<template>
  <div ref="containerRef" class="barrage">
    <div
      v-for="item in computedItems"
      :key="item.key"
      class="barrage-item"
      :style="item.style"
    >
      <img class="barrage-avatar" :src="item.avatar" :alt="item.nickname || '匿名洞友'" @error="onAvatarError">
      <span class="barrage-text">{{ item.msg }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import defaultAvatar from '@/assets/images/logo/logo.png'

interface BarrageItem {
  id: string | number
  msg: string
  avatar?: string
  nickname?: string
  time?: number
}

const props = defineProps<{
  list: BarrageItem[]
}>()

const containerRef = ref<HTMLElement | null>(null)
const cachedStyles = ref<Map<string | number, { top: number; delay: number }>>(new Map())

const normalizeItem = (item: BarrageItem, index: number) => {
  const key = item.id ?? `${index}`
  if (!cachedStyles.value.has(key)) {
    cachedStyles.value.set(key, {
      top: Math.random(),
      delay: Math.random() * 2
    })
  }
  const cached = cachedStyles.value.get(key)!
  const top = Math.min(0.92, cached.top) * 100
  const duration = Math.max(item.time ?? 10, 6)

  return {
    key,
    msg: item.msg,
    avatar: item.avatar || defaultAvatar,
    nickname: item.nickname || '匿名洞友',
    style: {
      top: `${top}%`,
      animationDuration: `${duration}s`,
      animationDelay: `-${cached.delay}s`
    }
  }
}

const onAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement | null
  if (!target) return
  target.src = defaultAvatar
}

const computedItems = computed(() => {
  const list = props.list || []
  return list.map(normalizeItem)
})

watch(
  () => props.list,
  () => {
    const ids = new Set((props.list || []).map((item) => item.id))
    for (const key of cachedStyles.value.keys()) {
      if (!ids.has(key)) {
        cachedStyles.value.delete(key)
      }
    }
  },
  { deep: true }
)
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
  left: 100%;
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
  animation-iteration-count: infinite;
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
    transform: translateX(0);
  }
  to {
    transform: translateX(-120vw);
  }
}
</style>
