<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import type { UploadFileInfo } from 'naive-ui/es/upload'
import {
  deleteSay,
  getSayList,
  publishSay,
  uploadSayImage
} from '@/modules/say/api/say.api'
import SayFeedCard from '@/modules/say/components/SayFeedCard.vue'
import SayPageHero from '@/modules/say/components/SayPageHero.vue'
import type { SayItem } from '@/modules/say/model/say.types'
import { useUserStore } from '@/stores/user'
import { useUiStore } from '@/stores/ui.store'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import { appIcons } from '@/shared/components/ui/icons'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SModal from '@/shared/components/ui/SModal.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import STextarea from '@/shared/components/ui/STextarea.vue'
import SUpload from '@/shared/components/ui/SUpload.vue'
import { useSMessage } from '@/shared/composables/useSMessage'
import { resolveUploadedFileDisplayUrl } from '@/shared/composables/useFileUrl'

const userStore = useUserStore()
const uiStore = useUiStore()
const message = useSMessage()
const router = useRouter()

const loading = ref(false)
const loadingMore = ref(false)
const sayList = ref<SayItem[]>([])
const sayTotal = ref(0)
const showPublishModal = ref(false)
const submitting = ref(false)
const content = ref('')
const imageUrls = ref<string[]>([])
const imageFileIds = ref<string[]>([])
const imageFileList = ref<UploadFileInfo[]>([])
const showPreviewModal = ref(false)
const previewImages = ref<string[]>([])
const previewImageIndex = ref(0)

const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const autoLoadUntilPage = 3
const feedLoadMode = ref<'auto' | 'manual'>('auto')
const loadMoreSentinel = ref<HTMLElement | null>(null)

const maxImages = 9

const sayIcons = {
  add: appIcons.add
} as const

const SAY_CONTENT_MAX = 500
const contentCharCount = computed(() => content.value.length)

const canPublish = computed(() => content.value.trim().length > 0)
const empty = computed(() => sayList.value.length === 0)
const loadedCount = computed(() => sayList.value.length)
const canContinueEarlierMoments = computed(() => hasMore.value && feedLoadMode.value === 'manual')
const imageMoments = computed(() =>
  sayList.value.filter((item) => Boolean(item.imageFileIds || item.images)).length
)
const activeConversations = computed(() =>
  sayList.value.filter((item) => Number(item.commentCount ?? 0) >= 3).length
)
const modalUserNickname = computed(() => {
  const userInfo = userStore.userInfo as { nickName?: string; userName?: string } | null
  return userInfo?.nickName || userInfo?.userName || '当前账号'
})

const EMPTY_FULL_TEXT = '这里还空空如也，快来发布第一条说说吧...'

function stripVarIndex(id: number): number {
  const n = Math.abs(Number(id)) || 0
  return n % 6
}

const getCardStyle = (item: SayItem) => ({
  '--say-strip': `var(--say-strip-accent-${stripVarIndex(item.id)})`
})

function handlePreview(file: { url?: string }) {
  if (file.url) {
    const images = publishOrderedImages.value
    const currentIndex = images.findIndex((url) => url === file.url)
    openImagePreview({
      images,
      index: currentIndex >= 0 ? currentIndex : 0
    })
  }
}

function openImagePreview(payload: { images: string[]; index?: number }) {
  const images = payload.images.filter(Boolean)
  if (images.length === 0) {
    return
  }
  previewImages.value = images
  previewImageIndex.value = Math.min(Math.max(payload.index ?? 0, 0), images.length - 1)
  showPreviewModal.value = true
}

const publishOrderedImages = computed(() =>
  imageFileList.value.filter((f) => f.status === 'finished' && f.url).map((f) => f.url || '')
)

const currentPreviewImage = computed(() => previewImages.value[previewImageIndex.value] || '')
const previewImageCount = computed(() => previewImages.value.length)
const canPreviewNavigate = computed(() => previewImageCount.value > 1)
const previewProgressLabel = computed(() =>
  previewImageCount.value > 0 ? `${previewImageIndex.value + 1} / ${previewImageCount.value}` : ''
)

function selectPreviewImage(index: number) {
  if (index < 0 || index >= previewImages.value.length) {
    return
  }
  previewImageIndex.value = index
}

function showPreviousPreviewImage() {
  if (!previewImages.value.length) {
    return
  }
  previewImageIndex.value =
    previewImageIndex.value === 0 ? previewImages.value.length - 1 : previewImageIndex.value - 1
}

function showNextPreviewImage() {
  if (!previewImages.value.length) {
    return
  }
  previewImageIndex.value =
    previewImageIndex.value === previewImages.value.length - 1 ? 0 : previewImageIndex.value + 1
}

async function doUploadFile(file: File): Promise<{ fileId: string; url: string }> {
  const formData = new FormData()
  formData.append('file', file)
  const data = await uploadSayImage(formData)
  return {
    fileId: String(data.fileId || Date.now()),
    url: resolveUploadedFileDisplayUrl({
      url: data.url,
      fileId: data.fileId ?? undefined
    })
  }
}

const handleUpload = async ({ file, onFinish, onError }: {
  file: UploadFileInfo & { file?: File | null }
  /** Naive customRequest：无参，传入的合并对象在实现中不会被使用 */
  onFinish: () => void
  onError: () => void
}) => {
  try {
    const fileObj = file.file
    if (!fileObj || fileObj.size === 0) {
      throw new Error('文件无效')
    }
    const result = await doUploadFile(fileObj)
    // Naive UI customRequest 提供的 onFinish 为无参函数，内部不会合并调用时传入的 file，
    // 列表项 id 会一直保持 Upload 生成的随机 id（如 seemly createId）。需在 onFinish 后用手写补丁同步服务端 fileId。
    onFinish()
    await nextTick()
    const patched: UploadFileInfo[] = imageFileList.value.map((f): UploadFileInfo =>
      f.id === file.id
        ? {
            ...f,
            id: result.fileId,
            name: file.name,
            status: 'finished',
            url: result.url
          }
        : f
    )
    imageFileList.value = patched
    const finished = patched.filter((item) => item.status === 'finished')
    imageUrls.value = finished.map((item) => item.url ?? '')
    imageFileIds.value = finished.map((item) => item.id)
  } catch (error) {
    console.error('上传失败', error)
    onError()
  }
}

function handleFileListChange(newList: UploadFileInfo[]) {
  const finishedFiles = newList.filter((file) => file.status === 'finished')
  imageUrls.value = finishedFiles.map((file) => file.url ?? '')
  imageFileIds.value = finishedFiles.map((file) => file.id)
  imageFileList.value = newList
}

const mapRows = (rows: SayItem[]) =>
  rows.map((row) => ({
    ...row,
    userId: row.userId != null ? Number(row.userId) : undefined,
    likeCount: Number(row.likeCount ?? 0),
    collectCount: Number(row.collectCount ?? 0),
    commentCount: Number(row.commentCount ?? 0),
    likedByMe: Boolean(row.likedByMe ?? row.liked),
    collectedByMe: Boolean(row.collectedByMe ?? row.isCollected),
    images: row.images ?? '',
    imageFileIds: row.imageFileIds ?? ''
  }))

async function loadSays() {
  loading.value = true
  page.value = 1
  hasMore.value = true
  try {
    const result = await getSayList({ page: 1, pageSize })
    const mapped = mapRows(result.items as SayItem[])
    sayTotal.value = Number(result.total ?? 0)
    sayList.value = mapped
    feedLoadMode.value = 'auto'
    hasMore.value = mapped.length >= pageSize
  } catch (error) {
    console.error('加载失败', error)
    sayList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value || loading.value) return

  loadingMore.value = true
  const nextPage = page.value + 1
  try {
    const result = await getSayList({ page: nextPage, pageSize })
    sayTotal.value = Number(result.total ?? sayTotal.value)

    if (Array.isArray(result.items) && result.items.length > 0) {
      const newItems = mapRows(result.items as SayItem[])
      sayList.value = [...sayList.value, ...newItems]
      page.value = nextPage
      if (nextPage >= autoLoadUntilPage && result.items.length >= pageSize) {
        feedLoadMode.value = 'manual'
      }
      if (result.items.length < pageSize) {
        hasMore.value = false
      }
    } else {
      hasMore.value = false
    }
  } catch (error) {
    console.error('加载更多失败', error)
  } finally {
    loadingMore.value = false
  }
}

let observer: IntersectionObserver | null = null

function bindLoadMoreObserver() {
  observer?.disconnect()
  const el = loadMoreSentinel.value
  if (!el || !hasMore.value || feedLoadMode.value !== 'auto') return

  observer = new IntersectionObserver(
    (entries) => {
      const hit = entries.some((entry) => entry.isIntersecting)
      if (hit && hasMore.value && !loadingMore.value && !loading.value) {
        void loadMore()
      }
    },
    { root: null, rootMargin: '120px', threshold: 0 }
  )
  observer.observe(el)
}

async function continueLoadMore() {
  if (!canContinueEarlierMoments.value) {
    return
  }

  await loadMore()
}

async function handleDeleteSay(sayId: number) {
  try {
    await deleteSay(sayId)
    sayList.value = sayList.value.filter((item) => item.id !== sayId)
    sayTotal.value = Math.max(sayTotal.value - 1, 0)
    message.success('说说已删除')
  } catch (error) {
    console.error('删除说说失败', error)
    message.error('删除说说失败，请稍后再试')
  }
}

watch(
  [sayList, hasMore, loading, loadMoreSentinel, feedLoadMode],
  async () => {
    await nextTick()
    bindLoadMoreObserver()
  },
  { flush: 'post' }
)

async function handlePublish() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再发布说说')
    return
  }
  if (!canPublish.value || submitting.value) return

  submitting.value = true
  try {
    await publishSay({
      content: content.value.trim(),
      images: imageUrls.value.length > 0 && imageFileIds.value.length === 0 ? imageUrls.value.join(',') : undefined,
      imageFileIds: imageFileIds.value.length > 0 ? imageFileIds.value.join(',') : undefined
    })
    content.value = ''
    imageUrls.value = []
    imageFileIds.value = []
    imageFileList.value = []
    showPublishModal.value = false
    uiStore.setSayPublishVisible(false)
    await loadSays()
    message.success('发布成功')
  } catch (error) {
    console.error('发布失败', error)
  } finally {
    submitting.value = false
  }
}

function closeModal() {
  showPublishModal.value = false
  uiStore.setSayPublishVisible(false)
  content.value = ''
  imageUrls.value = []
  imageFileIds.value = []
  imageFileList.value = []
}

function openPublishComposer() {
  if (!userStore.isLoggedIn) {
    showPublishModal.value = false
    uiStore.setSayPublishVisible(false)
    message.warning('请先登录后再发布说说')
    return
  }
  showPublishModal.value = true
  uiStore.setSayPublishVisible(true)
}

function goToLogin() {
  void router.push('/login')
}

function handlePublishModalVisibility(value: boolean) {
  if (value) {
    openPublishComposer()
    return
  }
  closeModal()
}

onMounted(() => {
  void loadSays()
  if (uiStore.sayPublishVisible) {
    void openPublishComposer()
  }
})

watch(
  () => uiStore.sayPublishVisible,
  (visible) => {
    if (visible && !showPublishModal.value) {
      openPublishComposer()
    }
  }
)

onUnmounted(() => {
  observer?.disconnect()
  observer = null
})
</script>

<template>
  <div class="glass-page say-page">
    <div class="say-page__bg-decor" aria-hidden="true" />

    <PageShell>
      <div class="say-page__content">
        <SayPageHero
          :is-logged-in="userStore.isLoggedIn"
          :say-total="sayTotal"
          :loaded-count="loadedCount"
          :image-moments="imageMoments"
          :active-conversations="activeConversations"
          @publish="openPublishComposer"
          @login="goToLogin"
        />

        <SSurfacePanel tag="section" class="say-stage" variant="soft" aria-label="动态列表">
          <div
            v-if="loading && sayList.length === 0"
            class="say-feed say-feed--skeleton"
            aria-busy="true"
          >
            <SSkeleton
              v-for="s in 6"
              :key="'sk-' + s"
              class="say-skeleton-card"
              :round="false"
              :repeat="1"
            />
          </div>

          <div v-else-if="sayList.length > 0" class="say-feed">
            <SayFeedCard
              v-for="item in sayList"
              :key="item.id"
              :item="item"
              :card-style="getCardStyle(item)"
              @preview="openImagePreview"
              @delete="handleDeleteSay"
            />
          </div>

          <div
            v-if="sayList.length > 0"
            ref="loadMoreSentinel"
            class="load-more-sentinel"
            :data-state="hasMore ? 'more' : 'end'"
          >
            <span v-if="loadingMore" class="load-more-sentinel__text">
              <span class="say-spinner" aria-hidden="true" />
              加载中…
            </span>
            <span v-else-if="canContinueEarlierMoments" class="load-more-sentinel__action">
              <SButton variant="ghost" size="small" @click="continueLoadMore">继续查看更早动态</SButton>
            </span>
            <span v-else-if="hasMore" class="load-more-sentinel__hint">向下滚动加载更多</span>
            <span v-else class="load-more-sentinel__end">已经到底了</span>
          </div>

          <EmptyState
            v-if="empty && !loading"
            class="say-empty-state"
            variant="section"
            icon="comment"
            title="还没有动态"
            :message="EMPTY_FULL_TEXT"
            description-mode="typewriter"
          >
            <template v-if="userStore.isLoggedIn" #action>
              <SButton type="primary" variant="cta" @click="openPublishComposer">
                <template #icon><SIcon :icon="sayIcons.add" :size="18" /></template>
                发布第一条说说
              </SButton>
            </template>
            <template #extra>
              <p v-if="!userStore.isLoggedIn" class="empty-login-hint">登录后即可发布你的第一条说说</p>
            </template>
          </EmptyState>
        </SSurfacePanel>
      </div>
    </PageShell>

    <Teleport to="body">
      <SModal
        v-model:show="showPublishModal"
        preset="card"
        surface="solid"
        title="发布说说"
        :show-icon="false"
        :closable="true"
        :mask-closable="true"
        :style="{ width: 'min(580px, calc(100vw - 48px))' }"
        @update:show="handlePublishModalVisibility"
      >
        <div class="say-compose-panel">
          <SSurfacePanel
            class="say-compose-panel__sheet say-compose-input-card"
            variant="modal-field"
            role="region"
            aria-label="编辑内容"
          >
            <div class="say-compose-panel__card-heading">
              <SSurfaceChip size="xs" variant="label" class="card-kicker say-compose-panel__chip-label">
                <SIcon :icon="appIcons.create" :size="14" aria-hidden="true" />
                新动态
              </SSurfaceChip>
              <span class="say-compose-panel__card-title">
                <SIcon :icon="appIcons.edit" :size="22" aria-hidden="true" />
                写下内容
              </span>
            </div>

            <p class="say-compose-panel__identity-hint">
              <SIcon :icon="appIcons.user" :size="15" aria-hidden="true" />
              <span>
                将以 <strong>{{ modalUserNickname }}</strong> 的身份公开发布
              </span>
            </p>

            <SSurfacePanel class="say-compose-panel__textarea-wrap" variant="modal-field">
              <STextarea
                v-model="content"
                class="say-compose-textarea"
                variant="filled"
                surface="modal-field"
                placeholder="此刻在想什么、看见了什么…"
                :maxlength="SAY_CONTENT_MAX"
                rows="6"
              />
            </SSurfacePanel>

            <div class="say-compose-panel__media">
              <div class="say-compose-panel__media-head">
                <SSurfaceChip size="xs" variant="label" class="card-kicker say-compose-panel__chip-label">
                  <SIcon :icon="appIcons.sparkles" :size="14" aria-hidden="true" />
                  配图
                </SSurfaceChip>
                <SSurfaceChip size="sm" variant="counter" class="say-compose-panel__media-meta" aria-live="polite">
                  {{ imageUrls.length }} / {{ maxImages }}
                </SSurfaceChip>
              </div>
              <SSurfacePanel class="say-compose-panel__upload-inner" variant="modal-field">
                <SUpload
                  v-model:file-list="imageFileList"
                  accept="image/*"
                  :max="maxImages"
                  list-type="image-card"
                  surface="modal-field"
                  hover-motion="scale"
                  sortable
                  sort-hint="拖拽排序，调整说说配图顺序"
                  remote-delete
                  :custom-request="handleUpload"
                  @update:file-list="handleFileListChange"
                  @preview="handlePreview"
                >
                  <SButton
                    text
                    :disabled="imageUrls.length >= maxImages"
                  >
                    <SIcon :icon="sayIcons.add" :size="18" />
                  </SButton>
                </SUpload>
              </SSurfacePanel>
            </div>

            <div class="say-compose-panel__card-footer">
              <span
                class="say-compose-panel__char-foot"
                :class="{ 'say-compose-panel__char-foot--warn': contentCharCount >= SAY_CONTENT_MAX - 50 }"
                aria-live="polite"
              >
                {{ contentCharCount }} / {{ SAY_CONTENT_MAX }}
              </span>
              <SButton
                type="primary"
                variant="cta"
                class="say-compose-panel__submit"
                :loading="submitting"
                :disabled="!canPublish"
                @click="handlePublish"
              >
                <template #icon>
                  <SIcon :icon="appIcons.send" :size="18" />
                </template>
                发布
              </SButton>
            </div>
          </SSurfacePanel>
        </div>
      </SModal>
    </Teleport>

    <Teleport to="body">
      <SModal
        v-model:show="showPreviewModal"
        preset="card"
        class="say-preview-modal"
        surface="solid"
        title="查看图片"
        :show-icon="false"
        :style="{ width: 'min(960px, calc(100vw - 48px))' }"
      >
        <div class="say-preview-viewer">
          <div class="say-preview-viewer__toolbar">
            <SSurfaceChip v-if="previewProgressLabel" size="sm" variant="counter" class="say-preview-viewer__counter">
              {{ previewProgressLabel }}
            </SSurfaceChip>
            <p class="say-preview-viewer__hint">浏览原图，点击下方缩略图切换</p>
          </div>
          <div
            class="say-preview-viewer__stage"
            :class="{ 'say-preview-viewer__stage--single': !canPreviewNavigate }"
          >
            <button
              v-if="canPreviewNavigate"
              type="button"
              class="say-preview-viewer__nav say-preview-viewer__nav--prev"
              @click="showPreviousPreviewImage"
            >
              <SIcon :icon="appIcons.back" :size="18" />
            </button>
            <div class="say-preview-viewer__frame">
              <img :src="currentPreviewImage" class="say-preview-viewer__image" alt="预览图片">
            </div>
            <button
              v-if="canPreviewNavigate"
              type="button"
              class="say-preview-viewer__nav say-preview-viewer__nav--next"
              @click="showNextPreviewImage"
            >
              <SIcon :icon="appIcons.arrowForward" :size="18" />
            </button>
          </div>
          <div v-if="canPreviewNavigate" class="say-preview-viewer__thumbs">
            <button
              v-for="(img, index) in previewImages"
              :key="`${img}-${index}`"
              type="button"
              class="say-preview-viewer__thumb"
              :class="{ 'say-preview-viewer__thumb--active': previewImageIndex === index }"
              @click="selectPreviewImage(index)"
            >
              <img :src="img" class="say-preview-viewer__thumb-image" alt="">
            </button>
          </div>
        </div>
      </SModal>
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
  --say-strip-accent-0: var(--info-color);
  --say-strip-accent-1: var(--primary-color);
  --say-strip-accent-2: var(--success-color);
  --say-strip-accent-3: var(--warning-color);
  --say-strip-accent-4: var(--secondary-color);
  --say-strip-accent-5: var(--error-color);
}

.say-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.say-page__bg-decor {
  pointer-events: none;
  position: absolute;
  inset: 0 0 auto 0;
  height: clamp(18rem, 46vh, 28rem);
  z-index: 0;
  overflow: hidden;

  &::before,
  &::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    filter: blur(30px);
    opacity: 0.14;
    animation: say-float-blob 22s ease-in-out infinite;
  }

  &::before {
    width: min(280px, 36vw);
    height: min(280px, 36vw);
    top: 2%;
    left: 4%;
    background:
      radial-gradient(circle, color-mix(in srgb, var(--primary-color) 16%, transparent), transparent 72%),
      radial-gradient(circle at 34% 30%, color-mix(in srgb, var(--glass-highlight) 52%, transparent), transparent 58%);
  }

  &::after {
    width: min(240px, 32vw);
    height: min(240px, 32vw);
    top: 8%;
    right: 8%;
    background:
      radial-gradient(circle, color-mix(in srgb, var(--info-color) 10%, transparent), transparent 72%),
      radial-gradient(circle at 40% 34%, color-mix(in srgb, var(--glass-highlight) 46%, transparent), transparent 56%);
    animation-delay: -11s;
    animation-duration: 26s;
  }
}

@keyframes say-float-blob {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(12px, -18px) scale(1.05);
  }
}

:deep(.page-shell) {
  position: relative;
  z-index: 1;
}

.say-page__content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--page-section-gap);
}

.say-stage {
  position: relative;
  display: grid;
  gap: var(--page-block-gap);
  padding: clamp(22px, 4vw, 28px);
  overflow: hidden;
}


.say-feed {
  display: grid;
  gap: var(--page-block-gap);
}

.say-feed--skeleton {
  min-height: 240px;
}

.say-skeleton-card {
  height: 220px;
  border-radius: 28px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--border-color) 80%, transparent) 0%,
    color-mix(in srgb, var(--glass-border) 60%, transparent) 50%,
    color-mix(in srgb, var(--border-color) 80%, transparent) 100%
  );
  background-size: 200% 100%;
  animation: say-shimmer 1.2s ease-in-out infinite;
}

@keyframes say-shimmer {
  0% {
    background-position: 100% 0;
  }
  100% {
    background-position: -100% 0;
  }
}

.load-more-sentinel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 54px;
  margin-top: 4px;
  padding-top: 10px;
  border-top: 1px solid color-mix(in srgb, var(--border-panel-subtle) 88%, transparent);
  color: var(--text-secondary);
  font-size: var(--font-size-md, 14px);
}

.load-more-sentinel__hint,
.load-more-sentinel__end {
  opacity: 0.74;
}

.load-more-sentinel__text {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.say-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid color-mix(in srgb, var(--primary-color) 25%, transparent);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: say-spin 0.7s linear infinite;
}

@keyframes say-spin {
  to {
    transform: rotate(360deg);
  }
}

.say-empty-state {
  width: 100%;
  margin: 8px 0 0;
  min-height: 0;
}

.empty-login-hint {
  margin: 0.2rem 0 0;
  font-size: var(--font-size-md, 14px);
  color: var(--text-secondary);
}

.say-compose-panel {
  display: grid;
}

.say-compose-input-card {
  overflow: hidden;
  padding: 20px;
  border-radius: calc(var(--glass-radius) + 2px);
}

.say-compose-panel .card-kicker {
  color: var(--text-secondary);
}

.say-compose-panel__chip-label {
  gap: 6px;
}

.say-compose-panel__chip-label :deep(.s-icon) {
  flex-shrink: 0;
  color: var(--primary-color);
}

.say-compose-panel__card-heading {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.say-compose-panel__card-title {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
  color: var(--title-color);
  font-size: var(--font-size-xl, 18px);
  font-weight: 700;
}

.say-compose-panel__card-title :deep(.s-icon) {
  color: var(--primary-color);
}

.say-compose-panel__identity-hint {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  color: var(--text-secondary);
  font-size: var(--font-size-sm, 12px);
  line-height: 1.5;
}

.say-compose-panel__identity-hint strong {
  color: var(--text-primary);
  font-weight: 700;
}

.say-compose-panel__textarea-wrap {
  margin-bottom: 18px;
  padding: var(--spacing-lg);
  border-radius: calc(var(--border-radius-xl) + 2px);
  transition: box-shadow 0.28s ease;
}

.say-compose-panel__textarea-wrap:focus-within {
  box-shadow: 0 0 8px color-mix(in srgb, var(--primary-color) 20%, transparent);
}

.say-compose-textarea {
  width: 100%;
  --input-border: color-mix(in srgb, var(--primary-color) 18%, var(--border-color));
  --input-border-hover: color-mix(in srgb, var(--primary-color) 32%, var(--border-color));
  --input-border-focus: var(--primary-color);
  --s-input-min-height: 6.5rem;
  --s-input-wrapper-padding: var(--spacing-sm);
  --s-input-border-radius: calc(var(--border-radius-lg) + 2px);
  --s-input-textarea-padding: var(--spacing-md) var(--spacing-lg);
  --s-input-textarea-font-size: var(--font-size-lg, 16px);
  --s-input-textarea-line-height: 1.72;
  --s-input-textarea-min-height: 6.5rem;
  --s-input-textarea-max-height: min(40vh, 22rem);
  --s-input-textarea-overflow-y: auto;
  --s-input-textarea-transition: min-height 0.12s ease;
  --s-input-textarea-resize: none;
}

.say-compose-panel__upload-inner {
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
}

.say-compose-panel__media {
  display: grid;
  gap: 12px;
  margin-bottom: 18px;
}

.say-compose-panel__media-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.say-compose-panel__media-meta {
  color: var(--text-secondary);
}

.say-compose-panel__card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.say-compose-panel__char-foot {
  font-size: var(--font-size-md, 14px);
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.say-compose-panel__char-foot--warn {
  color: var(--warning-color);
}

.say-compose-panel__submit {
  min-width: 124px;
}

@include sourcelin-down(md) {
  .say-page__content {
    gap: 20px;
  }

  .say-stage {
    padding: 22px 18px;
    border-radius: 28px;
  }

  .say-compose-input-card {
    padding: 16px;
  }

  .say-compose-panel__card-footer {
    flex-direction: column-reverse;
    align-items: stretch;
  }

  .say-compose-panel__submit {
    width: 100%;
  }

  .say-preview-viewer__stage {
    grid-template-columns: 1fr;
  }

  .say-preview-viewer__nav {
    display: none;
  }

  .say-preview-viewer__frame {
    min-height: min(54vh, 520px);
  }
}

@include sourcelin-down(sm) {
  .say-feed {
    gap: 18px;
  }
}

.say-preview-viewer {
  display: grid;
  gap: 16px;
  width: 100%;
}

.say-preview-viewer__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.say-preview-viewer__counter {
  color: var(--text-primary);
}

.say-preview-viewer__hint {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.86rem;
}

.say-preview-viewer__stage {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 14px;
  width: 100%;
}

.say-preview-viewer__stage--single {
  grid-template-columns: minmax(0, 1fr);
}

.say-preview-viewer__frame {
  display: grid;
  place-items: center;
  min-height: min(66vh, 680px);
  width: 100%;
  padding: 14px;
  border-radius: 20px;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-panel-soft) 94%, transparent),
      color-mix(in srgb, var(--surface-panel-inset) 88%, transparent)
    );
  border: 1px solid var(--border-panel-subtle);
}

.say-preview-viewer__image {
  width: 100%;
  max-height: min(60vh, 620px);
  object-fit: contain;
}

.say-preview-viewer__nav {
  width: 42px;
  height: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-panel-default);
  border-radius: 999px;
  background: var(--surface-panel-soft);
  color: var(--text-primary);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);

  &:hover {
    transform: translateY(-1px);
    background: var(--surface-panel-default);
    box-shadow: var(--shadow-panel-soft);
  }
}

.say-preview-viewer__thumbs {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.say-preview-viewer__thumb {
  flex-shrink: 0;
  width: 84px;
  height: 84px;
  padding: 0;
  overflow: hidden;
  border: 2px solid transparent;
  border-radius: 14px;
  background: var(--surface-panel-inset);
  cursor: pointer;
  transition:
    border-color var(--transition-base),
    transform var(--transition-base),
    box-shadow var(--transition-base);

  &:hover {
    transform: translateY(-1px);
  }

  &--active {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 16%, transparent);
  }
}

.say-preview-viewer__thumb-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>

<style lang="scss">
.say-heart-particle {
  position: fixed;
  z-index: 10050;
  pointer-events: none;
  font-size: 13px;
  line-height: 1;
  color: var(--error-color);
  text-shadow: 0 0 6px color-mix(in srgb, var(--error-color) 40%, transparent);
  animation: say-heart-rise 0.88s ease-out forwards;
}

@keyframes say-heart-rise {
  0% {
    transform: translate(0, 0) scale(1);
    opacity: 1;
  }
  100% {
    transform: translate(var(--dx, 0), -52px) scale(0.65);
    opacity: 0;
  }
}

.say-preview-modal__img {
  width: 100%;
  display: block;
}
</style>
