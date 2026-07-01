<template>
  <div class="s-editor">
    <QuillEditor
      ref="editorRef"
      class="s-editor__rich"
      theme="snow"
      content-type="html"
      :options="editorOptions"
      @ready="handleReady"
      @textChange="handleTextChange"
    />
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

interface QuillRange {
  index: number
  length: number
}

interface QuillInstance {
  getSelection: (focus?: boolean) => QuillRange | null
  insertEmbed: (index: number, type: string, value: string, source?: string) => void
  setSelection: (index: number, length?: number, source?: string) => void
}

interface QuillEditorExpose {
  getQuill: () => QuillInstance
  getHTML: () => string
  setHTML: (html: string) => void
  focus: () => void
}

interface EditorModuleOptions {
  toolbar?: unknown
  [key: string]: unknown
}

interface EditorOptions {
  modules?: EditorModuleOptions
  [key: string]: unknown
}

const props = withDefaults(
  defineProps<{
    placeholder?: string
    options?: EditorOptions
  }>(),
  {
    placeholder: '请输入正文内容...',
    options: () => ({})
  }
)

const modelValue = defineModel<string>({ required: true })

const editorRef = ref<QuillEditorExpose | null>(null)
const ready = ref(false)
const syncingFromEditor = ref(false)

const defaultToolbar = [
  ['bold', 'italic', 'underline', 'strike'],
  [{ header: [1, 2, 3, 4, false] }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ align: [] }],
  ['blockquote', 'code-block'],
  ['link', 'image'],
  ['clean']
]

const editorOptions = computedOptions()

function computedOptions() {
  const mergedModules = {
    toolbar: props.options?.modules?.toolbar ?? defaultToolbar,
    ...props.options?.modules
  }
  return {
    placeholder: props.placeholder,
    ...props.options,
    modules: mergedModules
  }
}

function normalizeHtml(html: string): string {
  const trimmed = html.trim()
  return trimmed === '<p><br></p>' ? '' : trimmed
}

function getEditorHtml(): string {
  return normalizeHtml(editorRef.value?.getHTML() ?? '')
}

function setEditorHtml(html: string) {
  if (!editorRef.value) {
    return
  }
  editorRef.value.setHTML(html || '<p><br></p>')
}

function syncModelFromEditor() {
  const html = getEditorHtml()
  if (html === modelValue.value) {
    return
  }
  syncingFromEditor.value = true
  modelValue.value = html
  void nextTick(() => {
    syncingFromEditor.value = false
  })
}

function handleReady() {
  ready.value = true
  setEditorHtml(modelValue.value)
  syncModelFromEditor()
}

function handleTextChange() {
  if (!ready.value) {
    return
  }
  syncModelFromEditor()
}

watch(
  () => modelValue.value,
  (value) => {
    if (!ready.value || syncingFromEditor.value) {
      return
    }
    const nextHtml = normalizeHtml(value)
    if (nextHtml === getEditorHtml()) {
      return
    }
    setEditorHtml(nextHtml)
  }
)

function getQuill() {
  return editorRef.value?.getQuill()
}

defineExpose({
  getHtml: getEditorHtml,
  getQuill,
  focus: () => editorRef.value?.focus()
})
</script>

<style lang="scss">
/* SEditor 暗黑模式与样式统一 */
.s-editor {
  position: relative;
  width: 100%;
}

/* 覆盖雪花皮肤的边框和背景 */
.s-editor {
  .ql-toolbar.ql-snow,
  .ql-container.ql-snow {
    border: none;
    transition: background-color 0.24s ease, border-color 0.24s ease;
  }

  .ql-toolbar.ql-snow {
    border-bottom: 1px solid var(--border-color) !important;
    background: color-mix(in srgb, var(--card-bg-color) 82%, var(--bg-page));
  }

  .ql-container.ql-snow {
    color: var(--text-color);
  }

  .ql-editor {
    line-height: 1.85;
    font-size: 0.98rem;
    color: var(--text-color);
    
    &.ql-blank::before {
      color: var(--text-secondary);
      font-style: normal;
      opacity: 0.72;
    }
  }

  /* SVG 图标在暗黑下的渲染 */
  [data-theme='dark'] & {
    .ql-toolbar.ql-snow {
      .ql-stroke {
        stroke: var(--text-color) !important;
      }
      .ql-fill {
        fill: var(--text-color) !important;
      }
      .ql-picker {
        color: var(--text-color) !important;
      }
      .ql-picker-options {
        background-color: var(--card-bg-color) !important;
        border-color: var(--border-color) !important;
      }
    }
  }
}
</style>
