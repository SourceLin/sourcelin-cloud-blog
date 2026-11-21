<script setup lang="ts">
import { ref, computed } from 'vue'
import type { AgentStatus } from '@/modules/ai-agent/composables/useAiAgentChat'

defineProps<{
  status: AgentStatus
  panelVisible: boolean
}>()

const emit = defineEmits<{
  toggle: []
}>()

// 拖拽位置状态
const posX = ref(0)
const posY = ref(0)
const hasDragged = ref(false) // 只有发生拖动，才由 JS left/top 定位接管，默认阶段完全依靠 CSS
const isDragging = ref(false)

let startX = 0
let startY = 0
let initialPosX = 0
let initialPosY = 0
let dragMoved = false // 区分拖拽和点击

const dragStyle = computed(() => {
  if (!hasDragged.value) {
    return {} // 默认完全依靠 CSS right/bottom 定位，防父容器 transform 偏移，零 resize 监听依赖
  }
  return {
    left: `${posX.value}px`,
    top: `${posY.value}px`,
    position: 'fixed' as const,
    right: 'auto',
    bottom: 'auto',
    transition: isDragging.value ? 'none' : 'transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), left 0.4s cubic-bezier(0.16, 1, 0.3, 1), top 0.4s cubic-bezier(0.16, 1, 0.3, 1)'
  }
})

function keepInBounds() {
  if (!hasDragged.value) return
  const isMobile = window.innerWidth <= 768
  const size = isMobile ? 48 : 56
  const maxX = window.innerWidth - size - 16
  const maxY = window.innerHeight - size - 16
  posX.value = Math.max(16, Math.min(posX.value, maxX))
  posY.value = Math.max(16, Math.min(posY.value, maxY))
}

function startDrag(e: MouseEvent | TouchEvent) {
  isDragging.value = true
  dragMoved = false

  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY

  startX = clientX
  startY = clientY

  // 在开始拖动的瞬间，捕捉真实尺寸并转换定位
  if (!hasDragged.value) {
    const el = e.currentTarget as HTMLElement
    const rect = el.getBoundingClientRect()
    posX.value = rect.left
    posY.value = rect.top
    hasDragged.value = true
  }

  initialPosX = posX.value
  initialPosY = posY.value

  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('touchend', endDrag)
}

function onDrag(e: MouseEvent | TouchEvent) {
  if (!isDragging.value) return

  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX
  const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY

  const dx = clientX - startX
  const dy = clientY - startY

  if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
    dragMoved = true
  }

  posX.value = initialPosX + dx
  posY.value = initialPosY + dy

  if (e.cancelable) {
    e.preventDefault()
  }
}

function endDrag() {
  if (!isDragging.value) return
  isDragging.value = false

  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', endDrag)

  keepInBounds()
}

function handleClick(e: MouseEvent) {
  if (dragMoved) {
    e.preventDefault()
    e.stopPropagation()
    return
  }
  emit('toggle')
}
</script>

<template>
  <div
    class="ai-agent-avatar"
    :class="[`ai-agent-avatar--${status}`, { 'ai-agent-avatar--active': panelVisible }]"
    :style="dragStyle"
    @mousedown="startDrag"
    @touchstart="startDrag"
    @click="handleClick"
  >
    <!-- 流体极光能量层 -->
    <div class="ai-agent-avatar__aurora" />

    <!-- 多层霓虹外发光环 -->
    <div class="ai-agent-avatar__glow" />

    <!-- 高阶双星芒 SVG 能量核 -->
    <div class="ai-agent-avatar__core">
      <svg class="ai-agent-avatar__svg" viewBox="0 0 24 24" fill="none">
        <defs>
          <linearGradient id="ai-sparkle-grad" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="var(--ai-accent-cyan)" />
            <stop offset="50%" stop-color="var(--ai-accent-purple)" />
            <stop offset="100%" stop-color="var(--ai-accent-pink)" />
          </linearGradient>
          <linearGradient id="ai-sparkle-listen" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="var(--ai-accent-success)" />
            <stop offset="100%" stop-color="var(--ai-accent-success)" />
          </linearGradient>
        </defs>

        <!-- 极细量子轨道星环 -->
        <circle
          cx="12"
          cy="12"
          r="9.5"
          :stroke="status === 'listening' ? 'url(#ai-sparkle-listen)' : 'url(#ai-sparkle-grad)'"
          stroke-width="1.2"
          stroke-dasharray="3 4"
          stroke-opacity="0.6"
          class="ai-agent-avatar__ring"
        />

        <!-- 量子小微粒 1 (在轨道上公转) -->
        <circle
          cx="12"
          cy="12"
          r="1.2"
          :fill="status === 'listening' ? 'var(--ai-accent-success)' : 'var(--ai-accent-cyan)'"
          class="ai-agent-avatar__particle ai-agent-avatar__particle--1"
        />

        <!-- 量子小微粒 2 (公转) -->
        <circle
          cx="12"
          cy="12"
          r="1"
          :fill="status === 'listening' ? 'var(--ai-accent-success)' : 'var(--ai-accent-pink)'"
          class="ai-agent-avatar__particle ai-agent-avatar__particle--2"
        />

        <!-- 辅星芒 (右上微缩星) -->
        <path
          d="M18.5 4 C18.5 5.2, 17.2 6.5, 16 6.5 C17.2 6.5, 18.5 7.8, 18.5 9 C18.5 7.8, 19.8 6.5, 21 6.5 C19.8 6.5, 18.5 5.2, 18.5 4 Z"
          :fill="status === 'listening' ? 'var(--ai-accent-success)' : 'var(--ai-accent-cyan)'"
          class="ai-agent-avatar__sparkle-sub"
        />

        <!-- 主星芒 (中大， Concave 曲线完美四角星) -->
        <path
          d="M11 4.5 C11 8, 8 11, 4.5 11 C8 11, 11 14, 11 17.5 C11 14, 14 11, 17.5 11 C14 11, 11 8, 11 4.5 Z"
          :fill="status === 'listening' ? 'url(#ai-sparkle-listen)' : 'url(#ai-sparkle-grad)'"
          class="ai-agent-avatar__sparkle-main"
        />
      </svg>
    </div>

    <!-- 极简毛玻璃 Tooltip 指示器 -->
    <div
      class="ai-agent-avatar__tooltip"
      :class="{ 'ai-agent-avatar__tooltip--visible': status !== 'idle' || panelVisible }"
    >
      <span class="ai-agent-avatar__tooltip-inner">
        <span v-if="status === 'speaking'" class="ai-agent-avatar__status-dot ai-agent-avatar__status-dot--speaking" />
        <span v-else-if="status === 'listening'" class="ai-agent-avatar__status-dot ai-agent-avatar__status-dot--listening" />
        <span v-else class="ai-agent-avatar__status-dot ai-agent-avatar__status-dot--idle" />
        <template v-if="status === 'speaking'">回答中...</template>
        <template v-else-if="status === 'listening'">思考中...</template>
        <template v-else-if="panelVisible">收起助手</template>
        <template v-else>召唤 AI</template>
      </span>
    </div>
  </div>
</template>

<style scoped lang="scss" src="./AiAgentAvatar.scss"></style>
