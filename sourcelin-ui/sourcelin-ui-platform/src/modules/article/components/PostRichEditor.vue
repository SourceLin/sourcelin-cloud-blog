<template>
  <div class="post-rich-editor">
    <div class="editor-toolbar-note">
      <span class="editor-chip">正文编辑</span>
      <span class="editor-note">支持标题、引用、代码块和图片排版，发布后会按当前样式展示</span>
    </div>

    <div class="editor-frame">
      <SEditor
        ref="editorRef"
        v-model="modelValue"
        class="rich-editor"
        placeholder="写下这篇文章的正文..."
        :options="editorOptions"
      />

      <SUpload
        class="editor-image-upload"
        accept="image/png,image/jpeg,image/jpg,image/webp"
        :max="1"
        :show-file-list="false"
        :custom-request="handleEditorImageUpload"
      >
        <button
          ref="imageUploadTriggerRef"
          type="button"
          class="editor-upload-trigger"
          aria-hidden="true"
          tabindex="-1"
        />
      </SUpload>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SEditor from '@/shared/components/ui/SEditor.vue'
import { uploadArticleFile } from '@/modules/article/api/article.api'
import SUpload from '@/shared/components/ui/SUpload.vue'
import { resolveUploadedFileDisplayUrl } from '@/shared/composables/useFileUrl'
import { useSMessage } from '@/shared/composables/useSMessage'

interface UploadPayload {
  fileId?: number
  url?: string
}

const modelValue = defineModel<string>({ required: true })

const message = useSMessage()
const editorRef = ref<InstanceType<typeof SEditor> | null>(null)
const imageUploadTriggerRef = ref<HTMLButtonElement | null>(null)

interface UploadRequestFile {
  file?: File | null
  raw?: File | null
}

interface UploadRequestOptions {
  file: UploadRequestFile | File
  onFinish: () => void
  onError: () => void
}

const editorOptions = {
  modules: {
    toolbar: {
      handlers: {
        image: () => {
          imageUploadTriggerRef.value?.click()
        }
      }
    }
  }
}

function validateImage(file: File): boolean {
  const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 <= 5
  if (!isImage) {
    message.error('正文图片仅支持 JPG、PNG、WEBP')
    return false
  }
  if (!isLt5M) {
    message.error('正文图片大小不能超过 5MB')
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

async function handleEditorImageUpload({ file, onFinish, onError }: UploadRequestOptions) {
  const selectedFile = resolveUploadFile(file)
  if (!selectedFile) {
    message.error('未识别到可上传的图片文件')
    onError()
    return
  }
  if (!validateImage(selectedFile)) {
    onError()
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile)
  try {
    const uploaded = (await uploadArticleFile(formData)) as UploadPayload
    const displayUrl = resolveUploadedFileDisplayUrl({
      url: uploaded?.url,
      fileId: uploaded?.fileId
    })
    if (!displayUrl) {
      throw new Error('正文图片上传结果无效')
    }
    const quill = editorRef.value?.getQuill()
    if (!quill) {
      throw new Error('编辑区域暂未就绪')
    }
    const selection = quill.getSelection(true)
    const index = selection?.index ?? 0
    quill.insertEmbed(index, 'image', displayUrl, 'user')
    quill.setSelection(index + 1, 0, 'user')
    onFinish()
  } catch (error) {
    console.error('上传正文图片失败', error)
    message.error('正文图片上传失败，请稍后再试')
    onError()
  }
}

function getHtml() {
  return editorRef.value?.getHtml() ?? modelValue.value
}

defineExpose({
  getHtml,
  focus: () => editorRef.value?.focus()
})
</script>

<style scoped lang="scss">
.post-rich-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.editor-toolbar-note {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.editor-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 58px;
  padding: 0.28rem 0.7rem;
  border-radius: 999px;
  color: var(--primary-color);
  background: var(--surface-panel-chip-accent);
  border: 1px solid var(--border-panel-badge-accent);
}

.editor-note {
  color: var(--text-secondary);
  font-size: 0.92rem;
}

.editor-frame {
  position: relative;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  background: var(--card-bg-color);
  min-height: 460px;
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--text-secondary) 22%, transparent);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.editor-frame:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px color-mix(in srgb, var(--primary-color) 32%, transparent);
}

:global(.post-rich-editor .editor-frame .ql-container.ql-snow),
:global(.post-rich-editor .editor-frame .rich-editor.ql-container.ql-snow) {
  border: none;
  min-height: 420px;
  height: 420px;
  font-size: 1rem;
  color: var(--text-color);
}

:global(.post-rich-editor .editor-frame .ql-editor) {
  min-height: 420px;
  height: 100%;
  line-height: 1.9;
  color: var(--text-color);
}

:global(.post-rich-editor .editor-frame .ql-editor img) {
  display: block;
  max-width: 100%;
  margin: 24px auto;
  border-radius: var(--border-radius-md);
}

.editor-upload-trigger {
  display: none;
}

.editor-image-upload {
  position: absolute;
  width: 0;
  height: 0;
  overflow: hidden;
  pointer-events: none;
}
</style>
