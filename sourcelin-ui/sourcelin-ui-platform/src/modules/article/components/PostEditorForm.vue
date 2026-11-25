<template>
  <div class="form-container">
    <SSurfacePanel class="form-card">
      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章标题</label>
          <SButton
            v-if="aiEnabled"
            size="small"
            type="primary"
            :loading="aiLoading['title']"
            class="ai-btn"
            @click="handleAiGenerate({ prop: 'title', aiAbility: 'article_title_generate', label: '标题' })"
          >
            ✨ AI 生成
          </SButton>
        </div>
        <SInput v-model="form.title" placeholder="请输入文章标题" />
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章摘要</label>
          <SButton
            v-if="aiEnabled"
            size="small"
            type="primary"
            :loading="aiLoading['summary']"
            class="ai-btn"
            @click="handleAiGenerate({ prop: 'summary', aiAbility: 'article_summary', label: '摘要' })"
          >
            ✨ AI 生成
          </SButton>
        </div>
        <STextarea v-model="form.summary" placeholder="请输入文章摘要（选填）" :rows="3" />
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章封面</label>
          <div style="display: flex; align-items: center; gap: 12px">
            <span class="form-tip">选填，建议上传横版图片</span>
            <SButton
              v-if="aiEnabled"
              size="small"
              type="primary"
              :loading="aiLoading['cover']"
              class="ai-btn"
              @click="handleAiGenerate({ prop: 'cover', aiAbility: 'article_cover_generate', label: '封面' })"
            >
              ✨ AI 智能配图
            </SButton>
          </div>
        </div>

        <div class="cover-panel">
          <div v-if="aiLoading['cover']" class="cover-placeholder">
            <span class="cover-placeholder-text">✨ AI 正在根据文章内容绘制封面，请稍候...</span>
          </div>
          <template v-else>
          <SUpload
            v-if="!coverDisplayUrl"
            :key="coverUploadKey"
            class="cover-upload"
            accept="image/png,image/jpeg,image/jpg,image/webp"
            :max="1"
            :show-file-list="false"
            :custom-request="handleCoverUploadRequest"
          >
            <SUploadDragger>
              <div
                class="cover-placeholder"
                @dragover.prevent
                @drop.prevent="onCoverDropByDragger"
              >
                <span class="cover-placeholder-text">点击上传或拖拽图片到这里上传</span>
              </div>
            </SUploadDragger>
          </SUpload>

          <div v-else class="cover-preview-card">
            <div class="cover-preview">
              <img :src="coverDisplayUrl" alt="文章封面预览">
              <div class="cover-preview-actions">
                <SButton size="small" @click.stop="openCoverPreview">预览</SButton>
                <SButton size="small" type="error" @click.stop="clearCover">删除</SButton>
              </div>
            </div>
            <SUpload
              :key="coverUploadKey"
              class="cover-reupload"
              accept="image/png,image/jpeg,image/jpg,image/webp"
              :max="1"
              :show-file-list="false"
              :custom-request="handleCoverUploadRequest"
            >
              <SButton size="small">更换封面</SButton>
            </SUpload>
          </div>

          <div class="cover-external">
            <SInput 
              v-model="form.externalAvatarUrl" 
              placeholder="或粘贴图片链接" 
              @blur="handleExternalUrlChange"
              @keyup.enter="handleExternalUrlChange"
            >
              <template #prefix>
                <span class="external-label">链接</span>
              </template>
            </SInput>
          </div>
          </template>
        </div>
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章分类</label>
<!--          <span class="form-tip">推荐：开源项目 / 技术文章 / 随笔</span>-->
        </div>
        <SSelect v-model="form.categoryId" :options="categories" placeholder="请选择分类" clearable />
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章标签</label>
          <SButton
            v-if="aiEnabled"
            size="small"
            type="primary"
            :loading="aiLoading['tagIds']"
            class="ai-btn"
            @click="handleAiGenerate({ prop: 'tagIds', aiAbility: 'article_tags', label: '标签' })"
          >
            ✨ AI 推荐
          </SButton>
        </div>
        <SSelect v-model="form.tagIds" :options="tagOptions" multiple placeholder="请选择标签（选填）" clearable />
      </div>

      <div class="form-group">
        <label class="form-label">文章类型</label>
        <SRadioGroup v-model="form.isOriginal">
          <SRadio :value="1">原创</SRadio>
          <SRadio :value="0">转载</SRadio>
        </SRadioGroup>
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章内容</label>
          <SButton
            v-if="aiEnabled"
            size="small"
            type="primary"
            :loading="aiLoading['content']"
            class="ai-btn"
            @click="handleAiGenerate({ prop: 'content', aiAbility: 'article_content_refine', label: '内容' })"
          >
            ✨ AI 润色
          </SButton>
        </div>
        <PostRichEditor ref="editorRef" v-model="form.content" />
      </div>

      <div class="form-actions">
        <SButton v-if="Object.values(aiLoading).some(Boolean)" @click="cancelGeneration">取消等待 AI 结果</SButton>
        <SButton @click="emit('cancel')">{{ cancelText }}</SButton>
        <SButton type="primary" :loading="loading" @click="handleSubmit">{{ submitText }}</SButton>
      </div>
    </SSurfacePanel>
  </div>

  <SModal
    v-model:show="showCoverPreviewModal"
    preset="card"
    title="封面预览"
    class="cover-preview-modal"
    :style="{ width: 'min(88vw, 980px)' }"
  >
    <div class="cover-preview-modal__body">
      <img v-if="coverDisplayUrl" :src="coverDisplayUrl" alt="cover-preview" class="cover-preview-modal__image">
    </div>
  </SModal>

  <PostAiSuggestionDialog v-model:show="showAiDialog" :result="aiResult" @close="cleanTypingTimer" @discard="discardAiResult" @apply="applyAiResult" />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { uploadArticleFile } from '@/modules/article/api/article.api'
import { usePostEditorAi } from '@/modules/article/composables/usePostEditorAi'
import { resolveUploadedFileDisplayUrl, useFileUrl } from '@/shared/composables/useFileUrl'
import PostAiSuggestionDialog from '@/modules/article/components/PostAiSuggestionDialog.vue'
import PostRichEditor from '@/modules/article/components/PostRichEditor.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import SRadio from '@/shared/components/ui/SRadio.vue'
import SRadioGroup from '@/shared/components/ui/SRadioGroup.vue'
import SSelect from '@/shared/components/ui/SSelect.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import STextarea from '@/shared/components/ui/STextarea.vue'
import SModal from '@/shared/components/ui/SModal.vue'
import SUpload from '@/shared/components/ui/SUpload.vue'
import SUploadDragger from '@/shared/components/ui/SUploadDragger.vue'
import { useSMessage } from '@/shared/composables/useSMessage'

export interface PostEditorFormModel {
  title: string
  summary: string
  avatar: string
  avatarFileId: number | null
  externalAvatarUrl: string
  content: string
  categoryId: number | null
  tagIds: number[]
  isOriginal: number
}

interface PostRichEditorExpose {
  getHtml: () => string
  focus: () => void
}

const form = defineModel<PostEditorFormModel>({ required: true })
const message = useSMessage()
const editorRef = ref<PostRichEditorExpose | null>(null)
const { resolveCoverUrl } = useFileUrl()
const showCoverPreviewModal = ref(false)
const coverUploadKey = ref(0)
const coverUploading = ref(false)
const skipNextCustomRequest = ref(false)

const props = defineProps<{
  articleId?: number | null
  categories: Array<{ label: string; value: number }>
  tagOptions: Array<{ label: string; value: number }>
  loading: boolean
  submitText?: string
  cancelText?: string
  aiEnabled?: boolean
}>()
const { cancelGeneration, aiLoading, showAiDialog, aiResult, handleAiGenerate, cleanTypingTimer, applyAiResult, discardAiResult } = usePostEditorAi(form, props)

const coverDisplayUrl = computed(() => {
  return resolveCoverUrl({
    avatar: form.value.externalAvatarUrl || form.value.avatar,
    avatarFileId: form.value.avatarFileId
  })
})

const emit = defineEmits<{
  submit: []
  cancel: []
}>()

interface UploadRequestFile {
  file?: File | null
  name?: string
  type?: string
  size?: number
  raw?: File | null
}

interface UploadRequestOptions {
  file: UploadRequestFile | File
  onFinish: () => void
  onError: () => void
}

function handleSubmit() {
  form.value.content = editorRef.value?.getHtml() ?? form.value.content
  emit('submit')
}

function validateCover(file: File): boolean {
  const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 <= 5
  if (!isImage) {
    message.error('封面仅支持 JPG、PNG、WEBP')
    return false
  }
  if (!isLt5M) {
    message.error('封面大小不能超过 5MB')
    return false
  }
  return true
}

function resolveUploadFile(uploadFile: UploadRequestFile | File): File | null {
  if (uploadFile instanceof File) {
    return uploadFile
  }
  if (uploadFile.file instanceof File) {
    return uploadFile.file
  }
  if (uploadFile.raw instanceof File) {
    return uploadFile.raw
  }
  return null
}

async function handleCoverUploadRequest({ file, onFinish, onError }: UploadRequestOptions) {
  if (skipNextCustomRequest.value) {
    skipNextCustomRequest.value = false
    onFinish()
    return
  }
  const selectedFile = resolveUploadFile(file)
  if (!selectedFile) {
    message.error('未识别到可上传的图片文件')
    coverUploadKey.value += 1
    onError()
    return
  }
  try {
    await uploadCoverFile(selectedFile)
    onFinish()
  } catch (error) {
    console.error('上传封面失败(custom-request)', error)
    onError()
  }
}

async function uploadCoverFile(selectedFile: File) {
  if (coverUploading.value) {
    return
  }
  if (!validateCover(selectedFile)) {
    coverUploadKey.value += 1
    throw new Error('封面文件校验失败')
  }
  coverUploading.value = true
  const formData = new FormData()
  formData.append('file', selectedFile)
  try {
    const uploaded = await uploadArticleFile(formData) as { fileId?: number; url?: string }
    if (uploaded?.fileId) {
      form.value.avatarFileId = uploaded.fileId
      form.value.avatar = uploaded.url || ''
      form.value.externalAvatarUrl = ''
      message.success('封面上传成功')
      return
    }
    if (uploaded?.url) {
      form.value.externalAvatarUrl = resolveUploadedFileDisplayUrl({ url: uploaded.url })
      message.success('封面上传成功')
      return
    }
    throw new Error('封面上传结果无效')
  } catch (error) {
    console.error('上传封面失败(uploadCoverFile)', error)
    message.error('上传封面失败')
    coverUploadKey.value += 1
    throw error
  } finally {
    coverUploading.value = false
  }
}

function onCoverDropByDragger(event: DragEvent) {
  const droppedFile = event.dataTransfer?.files?.[0]
  if (!droppedFile) {
    message.error('未识别到拖拽文件')
    return
  }
  skipNextCustomRequest.value = true
  void uploadCoverFile(droppedFile)
}

function handleExternalUrlChange() {
  const url = form.value.externalAvatarUrl?.trim()
  if (url) {
    form.value.avatarFileId = null
    form.value.avatar = url
  }
}

function clearCover() {
  form.value.avatar = ''
  form.value.avatarFileId = null
  form.value.externalAvatarUrl = ''
}

function openCoverPreview() {
  if (!coverDisplayUrl.value) {
    return
  }
  showCoverPreviewModal.value = true
}
</script>

<style scoped lang="scss" src="./PostEditorForm.scss"></style>
