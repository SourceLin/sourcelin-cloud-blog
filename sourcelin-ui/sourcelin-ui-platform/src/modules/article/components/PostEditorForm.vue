<template>
  <div class="form-container">
    <SSurfacePanel class="form-card">
      <div class="form-group">
        <label class="form-label">文章标题</label>
        <SInput v-model="form.title" placeholder="请输入文章标题" />
      </div>

      <div class="form-group">
        <label class="form-label">文章摘要</label>
        <STextarea v-model="form.summary" placeholder="请输入文章摘要（选填）" :rows="3" />
      </div>

      <div class="form-group">
        <div class="form-label-row">
          <label class="form-label">文章封面</label>
          <span class="form-tip">选填，建议上传横版图片</span>
        </div>

        <div class="cover-panel">
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
        <label class="form-label">文章标签</label>
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
        <label class="form-label">文章内容</label>
        <PostRichEditor ref="editorRef" v-model="form.content" />
      </div>

      <div class="form-actions">
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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { uploadArticleFile } from '@/modules/article/api/article.api'
import { resolveUploadedFileDisplayUrl, useFileUrl } from '@/shared/composables/useFileUrl'
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

const coverDisplayUrl = computed(() => {
  return resolveCoverUrl({
    avatar: form.value.externalAvatarUrl || form.value.avatar,
    avatarFileId: form.value.avatarFileId
  })
})

defineProps<{
  categories: Array<{ label: string; value: number }>
  tagOptions: Array<{ label: string; value: number }>
  loading: boolean
  submitText?: string
  cancelText?: string
}>()

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

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.form-container {
  width: 100%;
  max-width: 1520px;
  margin: 0;
  padding: 0 var(--spacing-xl) var(--spacing-xxxl);
}

.form-card {
  padding: 32px;
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--glass-stroke);
  background: var(--surface-glass-card-muted);
  box-shadow: var(--glass-shadow);
}

:global(html[data-theme='dark']) .form-card {
  background: var(--surface-glass-card);
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-color);
}

.form-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.form-tip {
  color: var(--text-secondary);
  font-size: 0.85rem;
}

.cover-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cover-upload {
  width: 100%;
}

.cover-preview-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cover-preview,
.cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 220px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  border: 1px dashed var(--border-color);
  background: color-mix(in srgb, var(--card-bg-color) 88%, var(--bg-page));
}

.cover-preview img {
  display: block;
  width: 100%;
  max-height: 320px;
  object-fit: cover;
}

.cover-reupload {
  width: fit-content;
}

.cover-preview {
  position: relative;
  cursor: pointer;
}

.cover-preview-actions {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  pointer-events: none;
  background: color-mix(in srgb, var(--mask-color) 78%, transparent);
  transition: opacity 0.2s ease;
}

.cover-preview:hover .cover-preview-actions {
  opacity: 1;
  pointer-events: auto;
}

.cover-placeholder {
  color: var(--text-secondary);
}

.cover-placeholder-text {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.cover-external {
  margin-top: 12px;
}

.external-label {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.cover-preview-modal__body {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
}

.cover-preview-modal__image {
  max-width: 100%;
  max-height: 76vh;
  border-radius: var(--border-radius-md);
  object-fit: contain;
}

@include sourcelin-down(md) {
  .form-card {
    padding: 24px;
  }

  .form-container {
    max-width: none;
    padding-left: var(--spacing-md);
    padding-right: var(--spacing-md);
  }

  .form-label-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .cover-preview,
  .cover-placeholder {
    min-height: 180px;
  }
}
</style>
