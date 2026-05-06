<template>
  <div
    ref="rootRef"
    class="s-upload"
    :class="[
      attrs.class,
      `s-upload--${surface}`,
      `s-upload--hover-${hoverMotion}`,
      {
        's-upload--image-card': listType === 'image-card',
        's-upload--sortable': sortableEnabled
      }
    ]"
    :style="attrs.style"
  >
    <NUpload
      class="s-upload__control"
      :list-type="listType"
      :file-list="mergedFileList"
      v-bind="forwardedAttrs"
      :on-remove="mergedOnRemove"
      @update:file-list="handleInnerUpdateFileList"
    >
      <slot />
    </NUpload>

    <p v-if="showSortHint" class="s-upload__sort-hint">
      {{ sortHint }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, useAttrs, watch } from 'vue'
import { NUpload } from 'naive-ui'
import type { UploadFileInfo, UploadOnRemove } from 'naive-ui/es/upload'
import { tryDeletePersistedFile } from '@/shared/utils/file-remote'

defineOptions({ inheritAttrs: false })

interface ReorderPayload {
  fileList: UploadFileInfo[]
  from: number
  to: number
}

interface Props {
  surface?: 'none' | 'glass-card' | 'modal-field'
  listType?: 'text' | 'image' | 'image-card'
  hoverMotion?: 'lift' | 'scale'
  /** 为 true 时，移除已上传且 id 为数字的项会先请求 DELETE /file/{id}，失败则阻止移除 */
  remoteDelete?: boolean
  fileList?: UploadFileInfo[]
  sortable?: boolean
  sortMode?: 'image-card'
  sortHint?: string
}

const props = withDefaults(defineProps<Props>(), {
  surface: 'none',
  listType: 'text',
  hoverMotion: 'lift',
  remoteDelete: false,
  fileList: undefined,
  sortable: false,
  sortMode: 'image-card',
  sortHint: ''
})

const emit = defineEmits<{
  'update:fileList': [fileList: UploadFileInfo[]]
  reorder: [payload: ReorderPayload]
}>()

const attrs = useAttrs()
const rootRef = ref<HTMLElement | null>(null)
const internalFileList = ref<UploadFileInfo[]>([])
const dragStartIndex = ref<number | null>(null)
const dragOverIndex = ref<number | null>(null)

const mergedFileList = computed(() => props.fileList ?? internalFileList.value)
const sortableEnabled = computed(
  () => props.sortable && props.listType === 'image-card' && props.sortMode === 'image-card'
)
const showSortHint = computed(
  () => sortableEnabled.value && Boolean(props.sortHint) && mergedFileList.value.length > 1
)

const forwardedAttrs = computed(() => {
  const {
    class: _class,
    style: _style,
    fileList: _fileList,
    onRemove: _onRemove,
    'onUpdate:fileList': _onUpdateFileList,
    'onUpdate:file-list': _onUpdateFileListKebab,
    ...rest
  } = attrs

  return rest
})

function emitFileListUpdate(fileList: UploadFileInfo[]) {
  internalFileList.value = fileList
  emit('update:fileList', fileList)
}

function handleInnerUpdateFileList(fileList: UploadFileInfo[]) {
  emitFileListUpdate(fileList)
}

function clearDragState() {
  dragStartIndex.value = null
  dragOverIndex.value = null
  syncSortableDomClasses()
}

function reorderFileList(from: number, to: number) {
  if (from === to) {
    clearDragState()
    return
  }

  const list = [...mergedFileList.value]
  const [moved] = list.splice(from, 1)

  if (!moved) {
    clearDragState()
    return
  }

  list.splice(to, 0, moved)
  emitFileListUpdate(list)
  emit('reorder', { fileList: list, from, to })
  clearDragState()
}

function getSortableItems() {
  if (!rootRef.value) {
    return []
  }

  return Array.from(
    rootRef.value.querySelectorAll<HTMLElement>('.n-upload-file-list .n-upload-file')
  )
}

function syncSortableDomClasses() {
  const items = getSortableItems()
  items.forEach((item, index) => {
    item.classList.remove(
      's-upload__sortable-item',
      's-upload__sortable-item--dragging',
      's-upload__sortable-item--drag-over'
    )

    if (!sortableEnabled.value) {
      item.draggable = false
      item.ondragstart = null
      item.ondragover = null
      item.ondrop = null
      item.ondragenter = null
      item.ondragend = null
      item.ondragleave = null
      return
    }

    item.classList.add('s-upload__sortable-item')
    if (dragStartIndex.value === index) {
      item.classList.add('s-upload__sortable-item--dragging')
    }
    if (dragOverIndex.value === index && dragStartIndex.value !== index) {
      item.classList.add('s-upload__sortable-item--drag-over')
    }
  })
}

function bindSortableDom() {
  const items = getSortableItems()

  items.forEach((item, index) => {
    if (!sortableEnabled.value) {
      item.draggable = false
      item.ondragstart = null
      item.ondragover = null
      item.ondrop = null
      item.ondragenter = null
      item.ondragend = null
      item.ondragleave = null
      return
    }

    item.draggable = true
    item.ondragstart = (event) => {
      if (
        event.target instanceof HTMLElement &&
        event.target.closest('button, a, input, textarea, .n-upload-file-action')
      ) {
        event.preventDefault()
        return false
      }

      dragStartIndex.value = index
      dragOverIndex.value = index
      event.dataTransfer?.setData('text/plain', String(index))
      if (event.dataTransfer) {
        event.dataTransfer.effectAllowed = 'move'
      }
      syncSortableDomClasses()
      return true
    }

    item.ondragenter = (event) => {
      event.preventDefault()
      if (dragStartIndex.value !== null) {
        dragOverIndex.value = index
        syncSortableDomClasses()
      }
    }

    item.ondragover = (event) => {
      event.preventDefault()
      if (dragStartIndex.value !== null) {
        dragOverIndex.value = index
        if (event.dataTransfer) {
          event.dataTransfer.dropEffect = 'move'
        }
        syncSortableDomClasses()
      }
    }

    item.ondrop = (event) => {
      event.preventDefault()
      if (dragStartIndex.value === null) {
        clearDragState()
        return
      }
      reorderFileList(dragStartIndex.value, index)
    }

    item.ondragleave = () => {
      if (dragOverIndex.value === index && dragStartIndex.value !== index) {
        dragOverIndex.value = null
        syncSortableDomClasses()
      }
    }

    item.ondragend = () => {
      clearDragState()
    }
  })

  syncSortableDomClasses()
}

const mergedOnRemove: UploadOnRemove = async (data) => {
  if (props.remoteDelete) {
    const ok = await tryDeletePersistedFile({
      id: data.file.id,
      status: data.file.status
    })
    if (!ok) {
      return false
    }
  }

  const userOnRemove = attrs.onRemove as UploadOnRemove | undefined
  if (typeof userOnRemove === 'function') {
    const result = await Promise.resolve(userOnRemove(data))
    if (result === false) {
      return false
    }
  }

  return true
}

watch(
  () => props.fileList,
  (fileList) => {
    if (fileList) {
      internalFileList.value = fileList
    }
  },
  { immediate: true }
)

watch(
  [mergedFileList, sortableEnabled],
  async () => {
    await nextTick()
    bindSortableDom()
  },
  { deep: true, flush: 'post', immediate: true }
)

onBeforeUnmount(() => {
  const items = getSortableItems()
  items.forEach((item) => {
    item.draggable = false
    item.ondragstart = null
    item.ondragover = null
    item.ondrop = null
    item.ondragenter = null
    item.ondragend = null
    item.ondragleave = null
  })
})
</script>

<style scoped lang="scss">
.s-upload {
  display: grid;
  gap: 10px;
}

.s-upload__sort-hint {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.82rem;
}

.s-upload--glass-card {
  :deep(.n-upload) {
    --n-color: var(--glass-surface-lite);
    --n-color-hover: var(--glass-surface-strong);
    --n-border: 1px dashed var(--glass-stroke);
    --n-border-hover: 1px dashed var(--primary-color);
    border-radius: var(--border-radius-lg);
    border: 1px solid var(--glass-border);
    background: var(--glass-surface-lite);
    box-shadow: var(--shadow-soft);
    transition:
      border-color var(--transition-base),
      background var(--transition-base),
      transform var(--transition-base);
  }

  :deep(.n-upload:hover) {
    transform: translateY(-2px);
  }
}

.s-upload--modal-field {
  :deep(.n-upload) {
    --n-color: var(--surface-modal-field);
    --n-color-hover: color-mix(in srgb, var(--surface-modal-solid) 90%, var(--surface-modal-field));
    --n-border: 1px dashed color-mix(in srgb, var(--border-color) 72%, var(--glass-border));
    --n-border-hover: 1px dashed var(--primary-color);
    border-radius: var(--border-radius-lg);
    border: 1px solid color-mix(in srgb, var(--border-color) 72%, var(--glass-border));
    background: var(--surface-modal-field);
    box-shadow: var(--shadow-panel-inline);
    transition:
      border-color var(--transition-base),
      background var(--transition-base),
      transform var(--transition-base);
  }

  :deep(.n-upload:hover) {
    transform: translateY(-2px);
  }
}

.s-upload--glass-card.s-upload--hover-scale {
  :deep(.n-upload:hover) {
    transform: scale(1.02);
    background: color-mix(in srgb, var(--primary-color) 8%, var(--glass-surface-lite));
  }
}

.s-upload--modal-field.s-upload--hover-scale {
  :deep(.n-upload:hover) {
    transform: scale(1.02);
    background: color-mix(in srgb, var(--primary-color) 7%, var(--surface-modal-field));
  }
}

.s-upload--image-card {
  --n-item-size: 104px;
  --n-gap-narrow: var(--spacing-lg, 16px);
  --n-border-radius: var(--border-radius-md);

  :deep(.n-upload-file-list) {
    gap: var(--spacing-lg, 16px);
  }

  :deep(.n-upload-file-list .n-upload-file) {
    border-radius: var(--border-radius-md);
  }
}

.s-upload--sortable {
  :deep(.n-upload-file-list .n-upload-file.s-upload__sortable-item) {
    cursor: grab;
    transition:
      transform var(--transition-base),
      box-shadow var(--transition-base),
      border-color var(--transition-base);
  }

  :deep(.n-upload-file-list .n-upload-file.s-upload__sortable-item:active) {
    cursor: grabbing;
  }

  :deep(.n-upload-file-list .n-upload-file.s-upload__sortable-item--dragging) {
    opacity: 0.58;
    transform: scale(0.96);
  }

  :deep(.n-upload-file-list .n-upload-file.s-upload__sortable-item--drag-over) {
    transform: scale(1.03);
    box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 16%, transparent);
  }
}

:global(html[data-theme='dark']) .s-upload--glass-card {
  :deep(.n-upload) {
    --n-color: var(--glass-surface);
    --n-color-hover: color-mix(in srgb, var(--primary-color) 10%, var(--glass-surface));
  }
}
</style>
