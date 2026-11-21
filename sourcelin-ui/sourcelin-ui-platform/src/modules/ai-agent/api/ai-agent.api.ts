import type { AiInvokeResult } from '@/modules/article/api/ai.api'
import { getToken, getTokenName, getTokenPrefix } from '@/shared/utils/auth'
import { requestData } from '@/shared/api/http'

export interface AiAgentMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: number
  isError?: boolean
}

export interface AiAgentStreamEvent {
  status: 'processing' | 'completed' | 'error'
  content: string
  requestId?: string
  error?: string
}

export interface AiAgentArticleContext {
  title: string
  content?: string
  url?: string
}

function buildArticleContextPayload(articleContext?: AiAgentArticleContext | null) {
  if (!articleContext?.title) {
    return {}
  }

  return {
    article_title: articleContext.title,
    ...(articleContext.content ? { article_content: articleContext.content.slice(0, 1500) } : {}),
    ...(articleContext.url ? { article_url: articleContext.url } : {})
  }
}

/**
 * 流式调用 AI Agent 对话
 * 使用 fetch + ReadableStream 消费 SSE（因为 EventSource 不支持 POST）
 */
export async function streamAiAgentChat(
  question: string,
  onChunk: (text: string) => void,
  onDone: () => void,
  onError: (err: string) => void,
  abortSignal?: AbortSignal,
  sessionId?: string,
  articleContext?: AiAgentArticleContext | null
): Promise<void> {
  const baseApi = import.meta.env.VITE_APP_BASE_API || ''
  const url = `${baseApi}/front/ai/abilities/ai_agent_chat/invoke/stream`

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(getToken() ? { [getTokenName()]: getTokenName().toLowerCase() === 'satoken' ? getToken()! : `${getTokenPrefix()} ${getToken()}` } : {}) },
      body: JSON.stringify({
        question,
        ...(sessionId ? { session_id: sessionId } : {}),
        ...buildArticleContextPayload(articleContext)
      }),
      signal: abortSignal
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('Response body is not readable')
    }

    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed || !trimmed.startsWith('data:')) continue

        const dataStr = trimmed.slice(5).trim()
        if (dataStr === '[DONE]') {
          await reader.cancel()
          onDone()
          return
        }

        try {
          const event: AiAgentStreamEvent = JSON.parse(dataStr)
          if (event.status === 'error') {
            await reader.cancel()
            onError(event.error || '未知错误')
            return
          }
          if (event.content) {
            onChunk(event.content)
          }
          if (event.status === 'completed') {
            await reader.cancel()
            onDone()
            return
          }
        } catch {
          // SSE 协议兼容：有些 LLM 返回 OpenAI 格式的 data: {"choices":[{"delta":{"content":"..."}}]}
          try {
            const parsed = JSON.parse(dataStr)
            const delta = parsed?.choices?.[0]?.delta?.content
            if (delta) {
              onChunk(delta)
            }
          } catch {
            // 忽略无法解析的行
          }
        }
      }
    }

    onDone()
  } catch (err: unknown) {
    if (err instanceof DOMException && err.name === 'AbortError') return
    onError(err instanceof Error ? err.message : '网络请求失败')
  }
}

/**
 * 同步调用 AI 能力（兜底方案）
 */
export function invokeAiAgentChat(question: string, sessionId?: string): Promise<AiInvokeResult> {
  return requestData<AiInvokeResult>({
    url: '/front/ai/abilities/ai_agent_chat/invoke',
    method: 'post',
    params: undefined,
    data: {
      question,
      ...(sessionId ? { session_id: sessionId } : {})
    }
  })
}
