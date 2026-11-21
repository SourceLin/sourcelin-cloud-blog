import { ref, onBeforeUnmount, watch } from 'vue'
import type { AiAgentArticleContext, AiAgentMessage } from '@/modules/ai-agent/api/ai-agent.api'
import { streamAiAgentChat } from '@/modules/ai-agent/api/ai-agent.api'
import { useUserStore } from '@/stores/user'

export type AgentStatus = 'idle' | 'listening' | 'speaking' | 'error'

const currentArticleContext = ref<AiAgentArticleContext | null>(null)

export function setAiAgentArticleContext(context: AiAgentArticleContext | null): void {
  currentArticleContext.value = context
}

export function clearAiAgentArticleContext(): void {
  currentArticleContext.value = null
}

let messageIdCounter = 0
function nextMessageId(): string {
  return `msg_${Date.now()}_${++messageIdCounter}`
}

function getFriendlyErrorMessage(rawErr: string): string {
  const errLower = rawErr.toLowerCase()
  if (errLower.includes('no instances available') || errLower.includes('load balancer') || errLower.includes('503')) {
    return '本站 AI 助手暂时处于离线维护状态，请稍后再试～'
  }
  if (errLower.includes('io error') || errLower.includes('unexpected end of file') || errLower.includes('socketexception') || errLower.includes('timeout')) {
    return 'AI 响应中断，网络好像开小差了，请重新尝试～'
  }
  if (errLower.includes('401') || errLower.includes('unauthorized') || errLower.includes('token expired')) {
    return '身份认证已过期，请刷新页面或重新登录后再试～'
  }
  if (errLower.includes('403') || errLower.includes('forbidden')) {
    return 'AI 助手接口拒绝访问，可能需要重新登录～'
  }
  if (errLower.includes('limit') || errLower.includes('too many requests') || errLower.includes('429')) {
    return '请求过于频繁，AI 助手正在休息，请稍等片刻再试～'
  }
  return `AI 助手刚才开小差了（${rawErr}），请再次尝试～`
}

export function useAiAgentChat() {
  const userStore = useUserStore()
  const messages = ref<AiAgentMessage[]>([])
  const status = ref<AgentStatus>('idle')
  const panelVisible = ref(false)
  const inputText = ref('')
  const sessionId = ref('')
  let abortController: AbortController | null = null

  const SESSION_KEY = 'sourcelin_chat_session'
  const ONBOARDING_SEEN_KEY = 'ai_agent_onboarding_seen' // New key for onboarding status

  // Initialize showOnboarding based on localStorage
  const showOnboarding = ref(!localStorage.getItem(ONBOARDING_SEEN_KEY))

  function generateUUID(): string {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
      return crypto.randomUUID()
    }
    return Math.random().toString(36).slice(2) + Date.now().toString(36)
  }

  function getOrCreateSessionId(): string {
    const isLogin = userStore.isLoggedIn
    const userId = userStore.userInfo?.id
    let session = sessionStorage.getItem(SESSION_KEY)

    if (isLogin && userId) {
      if (!session || !session.startsWith(`user_chat_${userId}_`)) {
        session = `user_chat_${userId}_${generateUUID()}`
        sessionStorage.setItem(SESSION_KEY, session)
      }
    } else {
      if (!session || !session.startsWith('guest_chat_')) {
        session = `guest_chat_${generateUUID()}`
        sessionStorage.setItem(SESSION_KEY, session)
      }
    }

    sessionId.value = session
    return session
  }

  const WELCOME_MESSAGE: AiAgentMessage = {
    id: 'welcome',
    role: 'assistant',
    content: '你好呀！我是小圈，本站 AI助手 ✨\n\n有什么想了解的？可以问我文章推荐、技术问题，或者随便聊聊～',
    timestamp: Date.now()
  }

  function togglePanel() {
    panelVisible.value = !panelVisible.value
    if (panelVisible.value) {
      if (messages.value.length === 0) {
        messages.value.push({ ...WELCOME_MESSAGE, timestamp: Date.now() })
      }
      getOrCreateSessionId()
    }
  }

  async function sendMessage(text?: string) {
    const content = (text ?? inputText.value).trim()
    if (!content || status.value === 'speaking' || status.value === 'listening') return

    const currentSessionId = getOrCreateSessionId()

    // 添加用户消息
    const userMsg: AiAgentMessage = {
      id: nextMessageId(),
      role: 'user',
      content,
      timestamp: Date.now()
    }
    messages.value.push(userMsg)
    inputText.value = ''

    // 添加空的 AI 消息（打字机效果目标）
    const aiMsg: AiAgentMessage = {
      id: nextMessageId(),
      role: 'assistant',
      content: '',
      timestamp: Date.now()
    }
    messages.value.push(aiMsg)

    status.value = 'listening'
    abortController = new AbortController()

    await streamAiAgentChat(
      content,
      (chunk: string) => {
        status.value = 'speaking'
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant') {
          lastMsg.content += chunk
        }
      },
      () => {
        status.value = 'idle'
        abortController = null
      },
      (err: string) => {
        status.value = 'error'
        abortController = null
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant') {
          const friendlyMsg = getFriendlyErrorMessage(err)
          lastMsg.isError = true
          if (!lastMsg.content) {
            lastMsg.content = `抱歉，出错了：${friendlyMsg}`
          } else {
            lastMsg.content += `\n\n---\n⚠️ 回复中断：${friendlyMsg}`
          }
        }
        // 3 秒后恢复 idle
        setTimeout(() => {
          if (status.value === 'error') status.value = 'idle'
        }, 3000)
      },
      abortController.signal,
      currentSessionId,
      currentArticleContext.value
    )
  }

  function stopGeneration() {
    if (abortController) {
      abortController.abort()
      abortController = null
      status.value = 'idle'
    }
  }

  function clearMessages() {
    stopGeneration()
    messages.value = [{ ...WELCOME_MESSAGE, timestamp: Date.now() }]
    sessionStorage.removeItem(SESSION_KEY)
    getOrCreateSessionId()
  }

  function retryMessage(idx: number) {
    if (status.value === 'speaking' || status.value === 'listening') return

    const errorMsg = messages.value[idx]
    if (!errorMsg || errorMsg.role !== 'assistant') return

    // 寻找在它之上的上一条用户提问消息
    let userPrompt = ''
    for (let i = idx - 1; i >= 0; i--) {
      if (messages.value[i].role === 'user') {
        userPrompt = messages.value[i].content
        break
      }
    }
    if (!userPrompt) return

    // 删除这次报错消息
    messages.value.splice(idx, 1)

    // 自动重发
    sendMessage(userPrompt)
  }

  function dismissOnboarding() {
    showOnboarding.value = false
    localStorage.setItem(ONBOARDING_SEEN_KEY, 'true')
  }

  watch(() => userStore.userInfo?.id, () => { stopGeneration(); messages.value = []; sessionStorage.removeItem(SESSION_KEY) })
  onBeforeUnmount(stopGeneration)

  return {
    messages,
    status,
    panelVisible,
    inputText,
    sessionId,
    showOnboarding, // Expose showOnboarding
    togglePanel,
    sendMessage,
    stopGeneration,
    clearMessages,
    retryMessage,
    dismissOnboarding // Expose dismissOnboarding
  }
}
