<template>
  <SModal
    :show="show"
    preset="card"
    surface="solid"
    title="裁剪头像"
    :show-icon="false"
    :closable="!loading"
    :mask-closable="false"
    :style="{ width: 'min(400px, calc(100vw - 32px))' }"
    @update:show="handleClose"
  >
    <div class="cropper-body">
      <!-- 拖拽上传与文件选择区域 -->
      <div
        v-if="!imageSrc"
        class="cropper-dropzone"
        :class="{ 'cropper-dropzone--dragover': isDragOver }"
        @dragover.prevent="handleDragOver"
        @dragenter.prevent="isDragOver = true"
        @dragleave.prevent="handleDragLeave"
        @drop.prevent="handleFileDrop"
        @click="triggerFileInput"
      >
        <div class="dropzone-content">
          <!-- 现代化的云上传 SVG 图标 -->
          <svg class="dropzone-svg" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 16V8M12 8L9 11M12 8L15 11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M20 16.5828C19.4268 18.5284 17.6534 20 15.5 20C13.6846 20 12.1158 18.966 11.3688 17.4398C10.9701 17.5147 10.5606 17.5 10.15 17.4302C7.81432 17.0343 6 15.0069 6 12.5C6 9.87878 8.0169 7.73013 10.6007 7.525C11.5173 5.48005 13.5606 4 16 4C19.3137 4 22 6.68629 22 10C22 10.117 21.9966 10.2331 21.9898 10.3486" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <p class="dropzone-text">点击或将图片拖拽到此处上传</p>
          <p class="dropzone-hint">支持 JPG、PNG 格式，大小不超过 2MB</p>
        </div>
        <input
          ref="fileInputRef"
          type="file"
          accept="image/jpeg,image/png"
          style="display: none"
          @change="handleFileChange"
        />
      </div>

      <!-- 裁剪操作区域 -->
      <div v-else class="cropper-workspace">
        <div
          ref="containerRef"
          class="cropper-viewport"
          @mousedown="startDrag"
          @touchstart="startDrag"
        >
          <div class="cropper-image-wrapper">
            <img
              v-if="imageSrc"
              :src="imageSrc"
              class="cropper-image"
              :style="{
                width: `${displayWidth}px`,
                height: `${displayHeight}px`,
                transform: `translate(${offsetX}px, ${offsetY}px) scale(${zoom})`,
                cursor: isDragging ? 'grabbing' : 'grab'
              }"
              alt="待裁剪头像"
              @dragstart.prevent
            />
          </div>
          <!-- 遮罩与裁剪圆形指示圈 -->
          <div class="cropper-mask">
            <div class="cropper-mask-circle"></div>
          </div>
        </div>

        <!-- 控制滑动条与缩放比 -->
        <div class="cropper-controls">
          <span class="control-icon zoom-out" @click="adjustZoom(-0.1)">-</span>
          <input
            type="range"
            :min="minZoom"
            :max="maxZoom"
            step="0.01"
            :value="zoom"
            class="cropper-zoom-slider"
            @input="handleSliderInput"
          />
          <span class="control-icon zoom-in" @click="adjustZoom(0.1)">+</span>
        </div>

        <!-- 重新选择按钮 -->
        <div class="cropper-reselect">
          <SButton size="small" quaternary @click="handleReSelect">
            重新选择图片
          </SButton>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="cropper-footer">
        <SButton :disabled="loading" @click="handleClose">取消</SButton>
        <SButton
          type="primary"
          variant="site"
          :loading="loading"
          :disabled="!imageSrc"
          @click="handleConfirm"
        >
          确认上传
        </SButton>
      </div>
    </div>
  </SModal>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeUnmount } from 'vue'
import SModal from '@/shared/components/ui/SModal.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import { useSMessage } from '@/shared/composables/useSMessage'

const props = defineProps<{
  show: boolean
  loading?: boolean
  defaultImageUrl?: string
}>()

const emit = defineEmits<{
  'update:show': [value: boolean]
  confirm: [file: File]
  cancel: []
}>()

const message = useSMessage()

const CONTAINER_SIZE = 280 // 视口大小 280x280
const CROP_SIZE = 180 // 圆形裁剪区域大小 180x180

const containerRef = ref<HTMLElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const imageSrc = ref('')
const isDragging = ref(false)
const isDragOver = ref(false)

// 渲染大小
const displayWidth = ref(CONTAINER_SIZE)
const displayHeight = ref(CONTAINER_SIZE)

// 转换状态
const zoom = ref(1)
const minZoom = ref(1)
const maxZoom = ref(3)
const offsetX = ref(0)
const offsetY = ref(0)

let imgElement: HTMLImageElement | null = null
let startX = 0
let startY = 0
let startOffsetX = 0
let startOffsetY = 0

// 提取出公共的 Image 加载及比例初始化逻辑
function initImageElement(src: string) {
  imgElement = new Image()
  imgElement.crossOrigin = 'anonymous'
  imgElement.src = src
  imgElement.onload = () => {
    if (!imgElement) return
    const naturalWidth = imgElement.naturalWidth
    const naturalHeight = imgElement.naturalHeight
    const aspect = naturalWidth / naturalHeight

    // 按比例适应视口大小
    if (aspect > 1) {
      displayHeight.value = CONTAINER_SIZE
      displayWidth.value = CONTAINER_SIZE * aspect
    } else {
      displayWidth.value = CONTAINER_SIZE
      displayHeight.value = CONTAINER_SIZE / aspect
    }

    // 计算最小 zoom 使得图片不论如何都能覆盖裁剪圈 CROP_SIZE
    const reqZoom = Math.max(CROP_SIZE / displayWidth.value, CROP_SIZE / displayHeight.value)
    minZoom.value = reqZoom
    maxZoom.value = reqZoom * 3
    zoom.value = reqZoom

    // 居中对齐，偏置归零
    offsetX.value = 0
    offsetY.value = 0
  }
}

// 监听弹窗显示状态，在打开时尝试载入原有头像或进行初始化
watch(
  () => props.show,
  (showVal) => {
    if (showVal) {
      selectedFile.value = null
      isDragOver.value = false
      if (imageSrc.value && imageSrc.value !== props.defaultImageUrl) {
        URL.revokeObjectURL(imageSrc.value)
      }
      imageSrc.value = ''
      zoom.value = 1
      offsetX.value = 0
      offsetY.value = 0

      // 如果传入了原有头像，默认载入它以便用户直接微调
      if (props.defaultImageUrl) {
        imageSrc.value = props.defaultImageUrl
        initImageElement(props.defaultImageUrl)
      }
    }
  }
)

// 监听选中的文件，载入本地预览
watch(selectedFile, (file) => {
  if (imageSrc.value && imageSrc.value !== props.defaultImageUrl) {
    URL.revokeObjectURL(imageSrc.value)
    imageSrc.value = ''
  }
  if (!file) return

  const url = URL.createObjectURL(file)
  imageSrc.value = url
  initImageElement(url)
})

// 文件选择与拖拽处理
function validateAndSetFile(file: File) {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    message.error('头像格式仅支持 JPG 或 PNG')
    return false
  }
  if (!isLt2M) {
    message.error('头像大小不能超过 2MB')
    return false
  }
  selectedFile.value = file
  return true
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    validateAndSetFile(input.files[0])
  }
}

function handleDragOver(event: DragEvent) {
  event.preventDefault()
  isDragOver.value = true
}

function handleDragLeave() {
  isDragOver.value = false
}

function handleFileDrop(event: DragEvent) {
  event.preventDefault()
  isDragOver.value = false
  if (event.dataTransfer?.files && event.dataTransfer.files[0]) {
    validateAndSetFile(event.dataTransfer.files[0])
  }
}

function handleReSelect() {
  selectedFile.value = null
  if (imageSrc.value && imageSrc.value !== props.defaultImageUrl) {
    URL.revokeObjectURL(imageSrc.value)
  }
  imageSrc.value = ''
}

// 监听缩放以重新约束边界
watch(zoom, (newZoom) => {
  const maxOffsetX = Math.max(0, (displayWidth.value * newZoom - CROP_SIZE) / 2)
  const maxOffsetY = Math.max(0, (displayHeight.value * newZoom - CROP_SIZE) / 2)
  offsetX.value = Math.min(maxOffsetX, Math.max(-maxOffsetX, offsetX.value))
  offsetY.value = Math.min(maxOffsetY, Math.max(-maxOffsetY, offsetY.value))
})

// 拖拽逻辑
function startDrag(event: MouseEvent | TouchEvent) {
  if (props.loading) return
  isDragging.value = true

  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY

  startX = clientX
  startY = clientY
  startOffsetX = offsetX.value
  startOffsetY = offsetY.value

  window.addEventListener('mousemove', handleDragMove)
  window.addEventListener('mouseup', handleDragEnd)
  window.addEventListener('touchmove', handleDragMove, { passive: false })
  window.addEventListener('touchend', handleDragEnd)
}

function handleDragMove(event: MouseEvent | TouchEvent) {
  if (!isDragging.value) return
  event.preventDefault()

  const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX
  const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY

  const dx = clientX - startX
  const dy = clientY - startY

  offsetX.value = startOffsetX + dx
  offsetY.value = startOffsetY + dy

  const maxOffsetX = Math.max(0, (displayWidth.value * zoom.value - CROP_SIZE) / 2)
  const maxOffsetY = Math.max(0, (displayHeight.value * zoom.value - CROP_SIZE) / 2)
  offsetX.value = Math.min(maxOffsetX, Math.max(-maxOffsetX, offsetX.value))
  offsetY.value = Math.min(maxOffsetY, Math.max(-maxOffsetY, offsetY.value))
}

function handleDragEnd() {
  isDragging.value = false
  window.removeEventListener('mousemove', handleDragMove)
  window.removeEventListener('mouseup', handleDragEnd)
  window.removeEventListener('touchmove', handleDragMove)
  window.removeEventListener('touchend', handleDragEnd)
}

function handleSliderInput(e: Event) {
  const target = e.target as HTMLInputElement
  zoom.value = parseFloat(target.value)
}

function adjustZoom(amount: number) {
  const newZoom = Math.min(maxZoom.value, Math.max(minZoom.value, zoom.value + amount))
  zoom.value = newZoom
}

function handleClose() {
  if (props.loading) return
  emit('update:show', false)
  emit('cancel')
}

// 核心：Canvas 裁剪生成 Blob 文件
function handleConfirm() {
  if (!imgElement || !imageSrc.value) return

  const canvas = document.createElement('canvas')
  canvas.width = 400
  canvas.height = 400
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    message.error('无法创建 canvas 上下文')
    return
  }

  // 获取文件名和类型，若是原有头像则默认赋予 png 格式
  const name = selectedFile.value?.name || 'avatar.png'
  const type = selectedFile.value?.type || 'image/png'

  const scale = imgElement.naturalWidth / displayWidth.value
  const xInDisplay = displayWidth.value / 2 - offsetX.value / zoom.value
  const yInDisplay = displayHeight.value / 2 - offsetY.value / zoom.value

  const cxNatural = xInDisplay * scale
  const cyNatural = yInDisplay * scale
  const cropSizeNatural = (CROP_SIZE / zoom.value) * scale

  const sx = cxNatural - cropSizeNatural / 2
  const sy = cyNatural - cropSizeNatural / 2
  const sw = cropSizeNatural
  const sh = cropSizeNatural

  ctx.drawImage(imgElement, sx, sy, sw, sh, 0, 0, 400, 400)

  canvas.toBlob(
    (blob) => {
      if (!blob) {
        message.error('头像裁剪输出失败')
        return
      }
      const file = new File([blob], name, {
        type: type,
        lastModified: Date.now()
      })
      emit('confirm', file)
    },
    type,
    0.9
  )
}

onBeforeUnmount(() => {
  if (imageSrc.value && imageSrc.value !== props.defaultImageUrl) {
    URL.revokeObjectURL(imageSrc.value)
  }
  handleDragEnd()
})
</script>

<style scoped lang="scss">
.cropper-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-md) 0 0 0;
}

/* 拖动上传区域 */
.cropper-dropzone {
  width: 280px;
  height: 280px;
  border: 2px dashed var(--border-color);
  border-radius: var(--border-radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: var(--surface-code-inline, #1e1e1e);
  transition: border-color var(--transition-base), background var(--transition-base), transform var(--transition-base);
  text-align: center;
  padding: var(--spacing-lg);
  box-sizing: border-box;

  &:hover {
    border-color: var(--primary-color);
    background: color-mix(in srgb, var(--primary-color) 4%, var(--surface-code-inline, #1e1e1e));
    transform: scale(1.01);
  }
}

.cropper-dropzone--dragover {
  border-color: var(--primary-color);
  background: color-mix(in srgb, var(--primary-color) 8%, var(--surface-code-inline, #1e1e1e));
  transform: scale(1.02);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary-color) 12%, transparent);
}

.dropzone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.dropzone-svg {
  width: 48px;
  height: 48px;
  color: var(--text-secondary);
  transition: color var(--transition-base), transform var(--transition-base);
}

.cropper-dropzone:hover .dropzone-svg {
  color: var(--primary-color);
  transform: translateY(-4px);
}

.dropzone-text {
  margin: 0;
  font-size: 0.94rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.dropzone-hint {
  margin: 0;
  font-size: 0.8rem;
  color: color-mix(in srgb, var(--text-secondary) 70%, transparent);
}

/* 裁剪区域工作空间 */
.cropper-workspace {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
}

.cropper-viewport {
  position: relative;
  width: 280px;
  height: 280px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  background: var(--surface-code-inline, #1e1e1e);
  user-select: none;
  box-shadow: inset 0 0 12px rgba(0, 0, 0, 0.4);
}

.cropper-image-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cropper-image {
  transform-origin: center center;
  max-width: none;
  max-height: none;
  display: block;
}

.cropper-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cropper-mask-circle {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 2px dashed var(--primary-color);
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.6);
}

.cropper-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  width: 100%;
  max-width: 280px;
}

.control-icon {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--text-secondary);
  cursor: pointer;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background var(--transition-base), color var(--transition-base);
  user-select: none;

  &:hover {
    background: var(--glass-surface-lite);
    color: var(--primary-color);
  }
}

.cropper-zoom-slider {
  flex: 1;
  -webkit-appearance: none;
  appearance: none;
  height: 6px;
  border-radius: 3px;
  background: var(--border-color);
  outline: none;
  transition: background var(--transition-base);

  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: var(--primary-color);
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    transition: transform var(--transition-base);
  }

  &::-webkit-slider-thumb:hover {
    transform: scale(1.25);
  }
}

.cropper-reselect {
  display: flex;
  justify-content: center;
}

.cropper-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  width: 100%;
  margin-top: var(--spacing-md);
  border-top: 1px solid var(--border-color);
  padding-top: var(--spacing-md);
}
</style>
