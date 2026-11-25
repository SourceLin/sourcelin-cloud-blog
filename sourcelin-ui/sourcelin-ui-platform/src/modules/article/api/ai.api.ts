import { requestData } from '@/shared/api/http'

export interface AiInvokeResult {
  requestId: string
  abilityCode: string
  bizId: string
  resultType: string
  content: string
  adoptStatus: string
  recordId?: number
  success: boolean
  errorCode?: string
  errorMessage?: string
}

export interface AiTaskStatus {
  taskId: string
  requestId?: string
  status: 'pending' | 'running' | 'succeeded' | 'failed' | 'unconfirmed'
  progress?: number
  resultSnapshot?: string
  errorMessage?: string
}

export interface AiStatusVO {
  enabled: boolean
}

/**
 * 获取 AI 功能状态
 */
export async function getAiStatus(): Promise<boolean> {
  try {
    const res = await requestData<AiStatusVO>({ url: '/front/ai/status', method: 'get' })
    return res?.enabled ?? false
  } catch {
    return false
  }
}

/**
 * 同步调用 AI 能力（通过博客服务，自动注入 userId 和业务上下文）
 */
export function invokeAiAbility(
  abilityCode: string,
  input: Record<string, unknown>,
  bizId?: string | number,
  signal?: AbortSignal
): Promise<AiInvokeResult> {
  return requestData<AiInvokeResult>({
    url: `/front/ai/abilities/${abilityCode}/invoke`,
    method: 'post',
    params: bizId ? { bizId } : undefined,
    signal,
    data: input
  })
}

/**
 * 生成文章标题
 */
export function generateArticleTitle(content: string): Promise<AiInvokeResult> {
  return invokeAiAbility('article_title_generate', { content })
}

/**
 * 生成文章摘要
 */
export function generateArticleSummary(title: string, content: string): Promise<AiInvokeResult> {
  return invokeAiAbility('article_summary', { title, content })
}

/**
 * 生成文章封面
 */
export function generateArticleCover(title: string, content: string): Promise<AiInvokeResult> {
  return invokeAiAbility('article_cover_generate', { title, content })
}

/**
 * 润色文章内容
 */
export function refineArticleContent(content: string): Promise<AiInvokeResult> {
  return invokeAiAbility('article_content_refine', { content })
}

/**
 * 创建 AI 异步任务
 */
export function createAiAsyncTask(abilityCode: string, input: Record<string, unknown>, bizId?: string | number): Promise<AiTaskStatus> {
  return requestData({
    url: '/front/ai/tasks',
    method: 'post',
    data: {
      abilityCode,
      bizId: bizId != null ? String(bizId) : undefined,
      input: input ?? {}
    }
  })
}

/**
 * 获取 AI 异步任务状态
 */
export function getAiTaskStatus(taskId: string): Promise<AiTaskStatus> {
  return requestData({
    url: `/front/ai/tasks/${taskId}`,
    method: 'get'
  })
}

/**
 * 采纳 AI 结果并回写
 */
export function adoptAiRecord(recordId: number | string): Promise<boolean> {
  return requestData({
    url: `/front/ai/records/${recordId}/adopt`,
    method: 'post'
  })
}

/**
 * 推荐文章标签
 */
export interface TagOptionItem {
  id: number
  name: string
}

export function recommendArticleTags(
  title: string,
  content: string,
  tagOptions: TagOptionItem[]
): Promise<AiInvokeResult> {
  return invokeAiAbility('article_tags', {
    title,
    content,
    tagOptions: JSON.stringify(tagOptions)
  })
}
