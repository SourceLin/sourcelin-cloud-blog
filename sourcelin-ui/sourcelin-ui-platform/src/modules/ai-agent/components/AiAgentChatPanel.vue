<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onBeforeUnmount, computed } from 'vue' // Added computed
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import AiAgentMessageItem from '@/modules/ai-agent/components/AiAgentMessageItem.vue'
import AiAgentTypingDots from '@/modules/ai-agent/components/AiAgentTypingDots.vue'

import type { AgentStatus } from '@/modules/ai-agent/composables/useAiAgentChat'
import type { AiAgentMessage } from '@/modules/ai-agent/api/ai-agent.api'
import { useUserStore } from '@/stores/user'
import logoImg from '@/assets/images/logo/logo.png'
import guestAvatarImg from '@/assets/images/header/avatar.jpg'

const userStore = useUserStore()

const props = defineProps<{
  visible: boolean
  status: AgentStatus
  messages: AiAgentMessage[]
  inputText: string
}>()

const emit = defineEmits<{
  close: []
  send: []
  updateInputText: [value: string]
  stop: []
  clear: []
  retry: [idx: number]
}>()

const showOnboarding = ref(!localStorage.getItem('ai_agent_onboarding_seen'))
function dismissOnboarding() { showOnboarding.value = false; localStorage.setItem('ai_agent_onboarding_seen', 'true') }

const textareaRef = ref<HTMLTextAreaElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const isLocked = ref(false)
const isFocused = ref(false)
const showScrollButton = ref(false)
let lastScrollAnimationFrame: number | null = null
const clickedPresetIndex = ref<number | null>(null) // New ref for clicked preset state
const showLongResponseTip = ref(false) // Delayed tip for long responses
let longResponseTimer: ReturnType<typeof setTimeout> | null = null

// 预设引导 Prompts
const presetPrompts = [
  {
    text: '推荐热门文章',
    icon: appIcons.trendUp,
    color: 'var(--ai-accent-blue)',
    desc: '看看大家都在看什么'
  },
  {
    text: '了解本站所用技术栈',
    icon: appIcons.brand,
    color: 'var(--ai-accent-purple)',
    desc: '探索前后台技术细节'
  },
  {
    text: '随便聊聊',
    icon: appIcons.comment,
    color: 'var(--ai-accent-pink)',
    desc: '闲聊或是寻求建议'
  }
]

function scrollToBottom(behavior: ScrollBehavior = 'auto') {
  if (lastScrollAnimationFrame) {
    cancelAnimationFrame(lastScrollAnimationFrame)
  }
  lastScrollAnimationFrame = requestAnimationFrame(() => {
    const el = containerRef.value
    if (el) {
      el.scrollTo({
        top: el.scrollHeight,
        behavior
      })
    }
  })
}

function handleScroll() {
  const el = containerRef.value
  if (!el) return
  const distanceToBottom = el.scrollHeight - el.scrollTop - el.clientHeight

  if (distanceToBottom > 60) {
    isLocked.value = true
  } else if (distanceToBottom <= 15) {
    isLocked.value = false
  }

  showScrollButton.value = isLocked.value && props.status === 'speaking'
}

function onClickScrollBottom() {
  isLocked.value = false
  showScrollButton.value = false
  scrollToBottom('smooth')
}

// 自动调整高度
function adjustTextareaHeight() {
  const ta = textareaRef.value
  if (!ta) return
  ta.style.height = 'auto'
  const newHeight = Math.min(Math.max(ta.scrollHeight, 40), 120)
  ta.style.height = `${newHeight}px`
}

function onTextareaInput(e: Event) {
  const val = (e.target as HTMLTextAreaElement).value
  emit('updateInputText', val)
  adjustTextareaHeight()
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey && !e.isComposing) {
    e.preventDefault()
    if (props.inputText.trim() && props.status !== 'speaking' && props.status !== 'listening') {
      emit('send')
    }
  }
}

// 点击引导卡片
function handlePresetClick(text: string, index: number) {
  if (props.status === 'speaking' || props.status === 'listening') return // Prevent clicks during AI activity

  clickedPresetIndex.value = index // Set clicked state
  emit('updateInputText', text)
  nextTick(() => {
    adjustTextareaHeight()
    setTimeout(() => { // Add a slight delay for visual feedback
      emit('send')
      clickedPresetIndex.value = null // Reset clicked state after sending
    }, 200) // 200ms delay for feedback
  })
}

let messagesObserver: MutationObserver | null = null

onMounted(() => {
  const el = containerRef.value
  if (el) {
    messagesObserver = new MutationObserver(() => {
      if (props.status === 'speaking' && !isLocked.value) {
        scrollToBottom('auto')
      }
    })
    messagesObserver.observe(el, {
      childList: true,
      subtree: true,
      characterData: true
    })
  }
})

watch(
  () => props.messages.length,
  () => {
    isLocked.value = false
    showScrollButton.value = false
    nextTick(() => {
      scrollToBottom('smooth')
    })
  }
)

watch(() => props.status, (newStatus) => {
  if (newStatus !== 'speaking') {
    showScrollButton.value = false
    showLongResponseTip.value = false
    if (longResponseTimer) {
      clearTimeout(longResponseTimer)
      longResponseTimer = null
    }
  }
  if (newStatus === 'speaking') {
    // Show tip after 5 seconds of continuous speaking
    longResponseTimer = setTimeout(() => {
      if (props.status === 'speaking') {
        showLongResponseTip.value = true
      }
    }, 5000)
  }
})

watch(() => props.visible, async (v) => {
  if (v) {
    isLocked.value = false
    await nextTick()
    textareaRef.value?.focus()
    adjustTextareaHeight()
    scrollToBottom('auto')
  }
})

watch(() => props.inputText, (newVal) => {
  if (!newVal) {
    nextTick(() => {
      if (textareaRef.value) {
        textareaRef.value.style.height = '40px'
      }
    })
  }
})

onBeforeUnmount(() => {
  if (lastScrollAnimationFrame) {
    cancelAnimationFrame(lastScrollAnimationFrame)
  }
  if (longResponseTimer) {
    clearTimeout(longResponseTimer)
  }
  messagesObserver?.disconnect()
})
</script>

<template>
  <Transition name="chat-panel">
    <div v-if="visible" class="ai-agent-chat-panel">
      <!-- 头部 -->
      <div class="ai-agent-chat-panel__header">
        <div class="ai-agent-chat-panel__header-left">
          <div class="ai-agent-chat-panel__header-avatar">
            <img :src="logoImg" class="ai-agent-chat-panel__header-avatar-img" alt="小圈 AI" />
            <!-- 呼吸状态小徽标 -->
            <span class="ai-agent-chat-panel__header-badge" :class="`ai-agent-chat-panel__header-badge--${status}`" />
          </div>
          <div>
            <div class="ai-agent-chat-panel__header-name">小圈 AI</div>
            <div class="ai-agent-chat-panel__header-status">
              <template v-if="status === 'speaking'">回答中...</template>
              <template v-else-if="status === 'listening'">思考中...</template>
              <template v-else>在线</template>
            </div>
          </div>
        </div>

        <div class="ai-agent-chat-panel__header-actions">
          <button
            class="ai-agent-chat-panel__action-btn"
            @click="emit('clear')"
            title="重新开始会话"
            :disabled="status === 'speaking' || status === 'listening'"
          >
            <SIcon :icon="appIcons.refresh" :size="16" />
          </button>
          <button class="ai-agent-chat-panel__action-btn" @click="emit('close')" title="收起面板">
            <SIcon :icon="appIcons.close" :size="16" />
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div ref="containerRef" class="ai-agent-chat-panel__messages" @scroll="handleScroll">
        <AiAgentMessageItem
          v-for="(msg, idx) in messages"
          :key="msg.id"
          :role="msg.role"
          :content="msg.content"
          :is-streaming="status === 'speaking' && idx === messages.length - 1 && msg.role === 'assistant'"
          :is-error="msg.isError"
          :avatar="msg.role === 'assistant' ? logoImg : (userStore.isLoggedIn && userStore.userInfo?.avatar ? userStore.userInfo.avatar : guestAvatarImg)"
          @retry="emit('retry', idx)"
        />

        <!-- 首次使用引导提示 (Onboarding) -->
        <Transition name="fade-in">
          <div
            v-if="showOnboarding && messages.length <= 1 && status !== 'listening' && status !== 'speaking'"
            class="ai-agent-chat-panel__onboarding"
          >
            <div class="ai-agent-chat-panel__onboarding-text">
              你好！我是小圈AI，能为你提供文章速览、技术解答或日常闲聊。
            </div>
            <button class="ai-agent-chat-panel__onboarding-btn" @click="dismissOnboarding">
              我知道了
            </button>
          </div>
        </Transition>

        <!-- listening 状态的 typing 指示器 -->
        <AiAgentTypingDots v-if="status === 'listening'" />

        <!-- 长响应预期管理提示 -->
        <Transition name="fade-in">
          <div v-if="showLongResponseTip" class="ai-agent-chat-panel__long-response-tip">
            小圈正在组织精彩的回答，请稍候片刻...
          </div>
        </Transition>

        <!-- 引导 Prompts 探索卡片 (当只有 welcome 消息或空会话时展示) -->
        <Transition name="fade-in">
          <div v-if="messages.length <= 1 && status !== 'listening'" class="ai-agent-chat-panel__explore">
            <div class="ai-agent-chat-panel__explore-title">你可以试着问我：</div>
            <div class="ai-agent-chat-panel__explore-grid">
              <div
                v-for="(prompt, index) in presetPrompts"
                :key="prompt.text"
                class="ai-agent-chat-panel__explore-card"
                :class="{ 'ai-agent-chat-panel__explore-card--clicked': clickedPresetIndex === index }"
                @click="handlePresetClick(prompt.text, index)"
              >
                <div class="ai-agent-chat-panel__explore-card-icon" :style="{ color: prompt.color }">
                  <SIcon :icon="prompt.icon" :size="18" />
                </div>
                <div class="ai-agent-chat-panel__explore-card-content">
                  <div class="ai-agent-chat-panel__explore-card-text">{{ prompt.text }}</div>
                  <div class="ai-agent-chat-panel__explore-card-desc">{{ prompt.desc }}</div>
                </div>
              </div>
            </div>
          </div>
        </Transition>
      </div>

      <!-- 悬浮返回最新消息气泡 -->
      <Transition name="scroll-btn">
        <button
          v-if="showScrollButton"
          class="ai-agent-chat-panel__scroll-bottom-btn"
          @click="onClickScrollBottom"
        >
          👇 返回最新消息
        </button>
      </Transition>

      <!-- 底部输入区 -->
      <div class="ai-agent-chat-panel__input-area">
        <div
          class="ai-agent-chat-panel__input-card"
          :class="{
            'ai-agent-chat-panel__input-card--focused': isFocused,
            'ai-agent-chat-panel__input-card--disabled': status === 'speaking' || status === 'listening'
          }"
        >
          <textarea
            ref="textareaRef"
            class="ai-agent-chat-panel__textarea"
            :value="inputText"
            placeholder="与小圈 AI 聊聊..."
            :disabled="status === 'speaking' || status === 'listening'"
            @input="onTextareaInput"
            @keydown="onKeydown"
            @focus="isFocused = true"
            @blur="isFocused = false"
          />
          <div class="ai-agent-chat-panel__input-footer">
            <span class="ai-agent-chat-panel__input-tip">Enter 发送 / Shift+Enter 换行</span>
            <div class="ai-agent-chat-panel__input-buttons">
              <button
                v-if="status === 'speaking' || status === 'listening'"
                class="ai-agent-chat-panel__stop-btn"
                @click="emit('stop')"
                title="停止生成"
              >
                <span class="ai-agent-chat-panel__stop-icon" />
              </button>
              <button
                v-else
                class="ai-agent-chat-panel__send-btn"
                :disabled="!inputText.trim()"
                @click="emit('send')"
                title="发送问题"
              >
                <SIcon :icon="appIcons.send" :size="14" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped lang="scss" src="./AiAgentChatPanel.scss"></style>
