<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMarkdown } from '../composables/useMarkdown'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = defineProps<{
  role: 'user' | 'assistant'
  content: string
  isStreaming: boolean
  isError?: boolean
  avatar?: string
}>()

const emit = defineEmits<{
  (e: 'retry'): void
}>()

const { parseMarkdown } = useMarkdown()
const messageCopied = ref(false)

const compiledContent = computed(() => {
  if (props.role === 'user') {
    return props.content
  }
  return parseMarkdown(props.content)
})

// 复制整条消息内容
async function copyMessage() {
  if (messageCopied.value) return
  try {
    await navigator.clipboard.writeText(props.content)
    messageCopied.value = true
    setTimeout(() => {
      messageCopied.value = false
    }, 2000)
  } catch (err) {
    console.error('Failed to copy message: ', err)
  }
}

// 事件代理：处理代码块内部的“复制代码”按钮点击
async function handleMarkdownClick(e: MouseEvent) {
  const target = e.target as HTMLElement
  const copyBtn = target.closest('.code-copy-btn') as HTMLButtonElement | null
  if (!copyBtn) return

  const encodedCode = copyBtn.getAttribute('data-code')
  if (!encodedCode) return

  const code = decodeURIComponent(encodedCode)
  try {
    await navigator.clipboard.writeText(code)

    // 成功反馈
    const btnTextEl = copyBtn.querySelector('.code-copy-btn-text')
    if (btnTextEl) {
      btnTextEl.textContent = '已复制'
      copyBtn.classList.add('code-copy-btn--success')
      setTimeout(() => {
        btnTextEl.textContent = '复制'
        copyBtn.classList.remove('code-copy-btn--success')
      }, 2000)
    }
  } catch (err) {
    console.error('Failed to copy code: ', err)
  }
}
</script>

<template>
  <div
    class="ai-agent-message"
    :class="{
      'ai-agent-message--user': role === 'user',
      'ai-agent-message--assistant': role === 'assistant',
      'ai-agent-message--error': isError
    }"
  >
    <div class="ai-agent-message__avatar">
      <img v-if="avatar" :src="avatar" class="ai-agent-message__avatar-img" :alt="role === 'assistant' ? 'AI' : 'User'" />
      <template v-else>
        <template v-if="role === 'assistant'">
          <!-- 机器人图标头像 -->
          <div class="ai-agent-message__avatar-bot">
            <SIcon :icon="appIcons.information" :size="16" />
          </div>
        </template>
        <template v-else>
          <!-- 用户头像 -->
          <div class="ai-agent-message__avatar-user">
            <SIcon :icon="appIcons.user" :size="16" />
          </div>
        </template>
      </template>
    </div>

    <!-- 气泡 -->
    <div class="ai-agent-message__bubble">
      <!-- 动作工具栏 (Hover 时显示，仅非打字中的消息提供) -->
      <div v-if="!isStreaming" class="ai-agent-message__actions">
        <button
          class="ai-agent-message__action-btn"
          :class="{ 'ai-agent-message__action-btn--success': messageCopied }"
          @click="copyMessage"
          :title="messageCopied ? '已复制' : '复制整条内容'"
        >
          <SIcon :icon="messageCopied ? appIcons.verified : appIcons.copy" :size="12" />
        </button>
      </div>

      <div class="ai-agent-message__text">
        <template v-if="role === 'user'">
          {{ content }}
        </template>
        <template v-else>
          <div
            class="markdown-body"
            v-html="compiledContent"
            @click="handleMarkdownClick"
          />
          <span v-if="isStreaming" class="ai-agent-message__cursor" />
        </template>
      </div>

      <!-- 重试动作区域 -->
      <div v-if="isError && !isStreaming" class="ai-agent-message__retry">
        <button class="ai-agent-message__retry-btn" @click="emit('retry')">
          <SIcon :icon="appIcons.refresh" :size="12" />
          <span>重新尝试</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss" src="./AiAgentMessageItem.scss"></style>
