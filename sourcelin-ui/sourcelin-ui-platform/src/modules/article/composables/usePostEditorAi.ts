import { ref, onBeforeUnmount, type Ref } from 'vue'
import { invokeAiAbility, createAiAsyncTask, getAiTaskStatus } from '@/modules/article/api/ai.api'
import { useSMessage } from '@/shared/composables/useSMessage'
import type { PostEditorFormModel } from '@/modules/article/components/PostEditorForm.vue'
interface PostEditorAiProps { articleId?: number | null; tagOptions: Array<{ label: string; value: number }> }
export function usePostEditorAi(form: Ref<PostEditorFormModel>, props: Readonly<PostEditorAiProps>) {
const message = useSMessage()
let disposed = false
let activeRequest: AbortController | null = null
function cancelGeneration() {
  activeRequest?.abort()
  activeRequest = null
  aiLoading.value = {}
}
// AI 相关状态
const aiLoading = ref<Record<string, boolean>>({})
const showAiDialog = ref(false)
const aiResult = ref({
  abilityCode: '',
  fieldName: '',
  content: '',
  fullText: '',
  originalVal: '',
  originalTags: [] as string[],
  parsedTags: [] as string[],
  parsedIds: [] as number[],
  fullTags: [] as string[],
  isTyping: false
})
const aiTypingTimer = ref<number | null>(null)



function resolveTagLabels(ids: Array<string | number>) {
  return ids.map((id) => {
    const option = props.tagOptions.find((item) => String(item.value) === String(id))
    return option ? option.label : `标签(${id})`
  })
}

function parseTagIdsFromAiResult(content: string): number[] {
  if (!content) return []
  try {
    if (content.trim().startsWith('[')) {
      return JSON.parse(content)
        .map((id: number | string) => Number(id))
        .filter((id: number) => !Number.isNaN(id))
    }
    return content
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
      .map((item) => Number(item))
      .filter((id) => !Number.isNaN(id))
  } catch {
    return []
  }
}

async function pollAsyncTaskResult(taskId: string, signal: AbortSignal): Promise<string> {
  const maxAttempts = 60
  for (let i = 0; i < maxAttempts; i++) {
    await new Promise((resolve) => setTimeout(resolve, 2000))
    if (disposed || signal.aborted) throw new Error('已离开编辑页面')
    const status = await getAiTaskStatus(taskId)
    if (status?.status === 'succeeded') {
      return status.resultSnapshot || ''
    }
    if (status?.status === 'failed' || status?.status === 'unconfirmed') {
      throw new Error(status.errorMessage || '异步任务执行失败')
    }
  }
  throw new Error(`等待结果超时，请按任务编号 ${taskId} 核对生成状态。`)
}

async function handleAiGenerate(field: { prop: string; aiAbility: string; label: string }) {
  const abilityCode = field.aiAbility
  const fieldName = field.label
  const content = form.value.content || ''

  // 前置校验：除了生成封面外，都需要一定的正文支撑
  const plainText = content.replace(/<[^>]+>/g, '').trim()
  if (plainText.length < 10) {
    message.warning('请先撰写足够的正文，AI 才能更好地生成哦~')
    return
  }

  if (activeRequest || showAiDialog.value) return
  const request = new AbortController()
  activeRequest = request
  aiLoading.value[field.prop] = true
  try {
    let result

    if (abilityCode === 'article_cover_generate') {
      const created = await createAiAsyncTask(abilityCode, { title: form.value.title, content }, props.articleId ?? undefined)
      if (!created?.taskId) throw new Error('未获取到任务 ID')
      result = { success: true, content: await pollAsyncTaskResult(created.taskId, request.signal) }
    } else {
      const input: Record<string, unknown> = { content, title: form.value.title }
      if (abilityCode === 'article_tags') {
        input.tagOptions = JSON.stringify(props.tagOptions.map(item => ({ id: item.value, name: item.label })))
      }
      result = await invokeAiAbility(abilityCode, input, props.articleId ?? undefined, request.signal)
    }

    if (disposed || request.signal.aborted) return
    if (result?.success && result.content) {
      const finalContent = result.content

      if (abilityCode === 'article_tags') {
        const parsedIds = parseTagIdsFromAiResult(finalContent)
        const isEmpty = !Array.isArray(form.value.tagIds) || form.value.tagIds.length === 0

        if (isEmpty) {
          form.value.tagIds = parsedIds
          message.success(`已自动填入 AI 推荐的${fieldName}`)
          return
        }

        const originalTags = resolveTagLabels(form.value.tagIds)
        const fullTags = resolveTagLabels(parsedIds)
        aiResult.value = {
          abilityCode,
          fieldName,
          content: finalContent,
          fullText: finalContent,
          originalVal: '',
          originalTags,
          parsedTags: [],
          parsedIds,
          fullTags,
          isTyping: true
        }
        showAiDialog.value = true
        startTagTypingEffect(fullTags)
        return
      }

      if (abilityCode === 'article_cover_generate') {
        form.value.externalAvatarUrl = finalContent
        form.value.avatarFileId = null
        message.success('已自动填入 AI 智能配图')
        return
      }

      let currentVal = ''
      if (field.prop === 'title') {
        currentVal = form.value.title || ''
      } else if (field.prop === 'summary') {
        currentVal = form.value.summary || ''
      } else if (field.prop === 'content') {
        currentVal = form.value.content || ''
      }

      const isEmpty = !currentVal || currentVal.trim() === ''

      if (isEmpty) {
        if (field.prop === 'title') {
          form.value.title = finalContent
        } else if (field.prop === 'summary') {
          form.value.summary = finalContent
        }
        message.success(`已自动填入 AI 推荐的${field.label}`)
      } else {
        aiResult.value = {
          abilityCode,
          fieldName,
          content: finalContent,
          fullText: finalContent,
          originalVal: currentVal,
          originalTags: [],
          parsedTags: [],
          parsedIds: [],
          fullTags: [],
          isTyping: true
        }
        showAiDialog.value = true
        startTypingEffect()
      }
    } else {
      message.error(result?.errorMessage || 'AI 生成失败，请稍后再试')
    }
  } catch (error) {
    if (disposed || request.signal.aborted) return
    console.error('AI 生成失败:', error)
    message.error('AI 生成失败，请稍后再试')
  } finally {
    if (activeRequest === request) { activeRequest = null; aiLoading.value[field.prop] = false }
  }
}

function startTagTypingEffect(fullTags: string[]) {
  cleanTypingTimer()
  let tagIndex = 0
  aiResult.value.parsedTags = []
  aiTypingTimer.value = window.setInterval(() => {
    if (tagIndex < fullTags.length) {
      aiResult.value.parsedTags.push(fullTags[tagIndex])
      tagIndex++
    } else {
      aiResult.value.isTyping = false
      cleanTypingTimer()
      message.success(`AI 推荐的${aiResult.value.fieldName}生成完成`)
    }
  }, 120)
}

function startTypingEffect() {
  cleanTypingTimer()
  const text = aiResult.value.fullText
  let currentIndex = 0
  aiResult.value.content = ''

  aiTypingTimer.value = window.setInterval(() => {
    if (currentIndex < text.length) {
      aiResult.value.content += text[currentIndex]
      currentIndex++
    } else {
      aiResult.value.isTyping = false
      cleanTypingTimer()
      message.success(`AI 推荐的${aiResult.value.fieldName}生成完成`)
    }
  }, 30)
}

function cleanTypingTimer() {
  if (aiTypingTimer.value) {
    clearInterval(aiTypingTimer.value)
    aiTypingTimer.value = null
  }
}

function applyAiResult() {
  if (aiResult.value.isTyping) {
    cleanTypingTimer()
    aiResult.value.content = aiResult.value.fullText
    if (aiResult.value.abilityCode === 'article_tags') {
      aiResult.value.parsedTags = [...aiResult.value.fullTags]
    }
    aiResult.value.isTyping = false
  } else {
    cleanTypingTimer()
  }
  const content = aiResult.value.content
  if (aiResult.value.abilityCode === 'article_title_generate') {
    form.value.title = content
  } else if (aiResult.value.abilityCode === 'article_summary') {
    form.value.summary = content
  } else if (aiResult.value.abilityCode === 'article_tags') {
    form.value.tagIds = [...aiResult.value.parsedIds]
  } else if (aiResult.value.abilityCode === 'article_content_refine') {
    form.value.content = content
  }
  showAiDialog.value = false
  message.success('已应用 AI 推荐内容')
}

function discardAiResult() {
  cleanTypingTimer()
  showAiDialog.value = false
}

onBeforeUnmount(() => { disposed = true; cancelGeneration(); cleanTypingTimer() })
return { cancelGeneration, aiLoading, showAiDialog, aiResult, handleAiGenerate, cleanTypingTimer, applyAiResult, discardAiResult }
}
