<template>
  <div class="content-section" :aria-busy="loading">
    <div v-if="loading" class="list-loading" aria-live="polite">
      <SkeletonArticleCard variant="list" :count="3" />
    </div>
    <div v-else class="content-body">
      <div class="article-list">
        <div v-if="items.length" class="articles">
          <SSurfacePanel
            v-for="item in items"
            :key="item.id"
            class="article-item"
            :class="{
              'article-item--clickable':
                (item.targetType === 'article' && Boolean(item.article?.id))
                || (item.targetType === 'say' && Boolean(item.say?.id))
            }"
            variant="default"
            :interactive="
              (item.targetType === 'article' && Boolean(item.article?.id))
              || (item.targetType === 'say' && Boolean(item.say?.id))
            "
            @click="handleOpen(item)"
          >
            <SImage :src="resolveCover(item)" class="article-cover" object-fit="cover" alt="收藏封面">
              <template #error>
                <div class="image-error-slot">
                  <SIcon :icon="appIcons.image" :size="38" color="var(--text-placeholder)" />
                </div>
              </template>
            </SImage>
            <div class="article-info">
              <h4 class="article-title">{{ resolveTitle(item) }}</h4>
              <p class="article-summary">{{ resolveSummary(item) }}</p>
              <div class="article-foot">
                <div class="article-meta">
                  <span class="meta-chip">{{ resolveTargetLabel(item.targetType) }}</span>
                  <span class="meta-chip">{{ formatDate(item.createTime) }}</span>
                  <span v-if="item.active === false" class="meta-chip">目标失效</span>
                </div>
                <div class="article-actions">
                  <SPopconfirm
                    :show-icon="false"
                    positive-text="确认取消"
                    negative-text="再想想"
                    @positive-click="emit('cancel', item)"
                  >
                    <template #trigger>
                      <SButton size="small" variant="ghost" @click.stop>取消收藏</SButton>
                    </template>
                    确定取消收藏{{ resolveTargetLabel(item.targetType) }}吗？
                  </SPopconfirm>
                </div>
              </div>
            </div>
          </SSurfacePanel>
        </div>
        <div v-else class="empty-tip">暂无收藏，去发现值得留下的内容吧。</div>
        <div v-if="showPagination" class="comment-pagination">
          <div class="comment-pagination__summary" aria-live="polite">
            <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
            <span class="comment-pagination__dot" aria-hidden="true" />
            <span>共 {{ total }} 条</span>
          </div>
          <SPagination
            :page="page"
            :page-size="size"
            :item-count="total"
            @update:page="emit('update:page', $event)"
          />
        </div>
      </div>
    </div>

    <SModal
      v-model:show="showSayPreview"
      preset="card"
      surface="solid"
      :show-icon="false"
      :closable="true"
      :mask-closable="true"
      title="说说详情"
      :style="{ width: 'min(680px, calc(100vw - 48px))' }"
    >
      <div v-if="previewSay" class="say-preview">
        <div class="say-preview__meta">
          <span class="meta-chip">说说</span>
          <span class="meta-chip">{{ formatDate(previewSay.createTime) }}</span>
          <span class="meta-chip">{{ previewSay.likeCount }} 点赞</span>
          <span class="meta-chip">{{ previewSay.commentCount }} 评论</span>
        </div>
        <p class="say-preview__content">{{ previewSay.content || '这条说说没有正文内容。' }}</p>
        <div v-if="previewSay.images.length" class="say-preview__gallery">
          <div
            v-for="(url, index) in previewSay.images"
            :key="`${url}-${index}`"
            class="say-preview__image-tile"
          >
            <SImage
              :src="url"
              class="say-preview__image"
              object-fit="cover"
              alt="说说配图"
            >
              <template #error>
                <div class="image-error-slot image-error-slot--tile">
                  <SIcon :icon="appIcons.image" :size="30" color="var(--text-placeholder)" />
                </div>
              </template>
            </SImage>
          </div>
        </div>
        <div v-else class="say-preview__empty">
          <SIcon :icon="appIcons.image" :size="72" color="var(--text-placeholder)" />
        </div>
      </div>
    </SModal>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import defaultCover from '@/assets/hero.png'
import emptyCover from '@/assets/images/placeholders/empty-cover.svg'
import SkeletonArticleCard from '@/shared/components/feedback/skeletons/SkeletonArticleCard.vue'
import type { UserCollectItem } from '@/modules/user/composables/useUserCenter'
import { useFileUrl } from '@/shared/composables/useFileUrl'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SImage from '@/shared/components/ui/SImage.vue'
import SModal from '@/shared/components/ui/SModal.vue'
import SPopconfirm from '@/shared/components/ui/SPopconfirm.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = defineProps<{
  loading: boolean
  items: UserCollectItem[]
  page: number
  size: number
  total: number
  formatDate: (v: string) => string
}>()

const emit = defineEmits<{ open: [id: number]; cancel: [item: UserCollectItem]; 'update:page': [page: number] }>()
const { resolveImageUrls } = useFileUrl()
const showSayPreview = ref(false)
const totalPages = computed(() => Math.max(Math.ceil(props.total / Math.max(props.size, 1)), 1))
const currentPage = computed(() => Math.min(Math.max(props.page, 1), totalPages.value))
const showPagination = computed(() => props.total > props.size)
const previewSay = ref<{
  content: string
  createTime: string
  images: string[]
  likeCount: number
  commentCount: number
} | null>(null)

function resolveSayImages(item: UserCollectItem): string[] {
  if (!item.say) {
    return []
  }
  return resolveImageUrls({ imageFileIds: item.say.imageFileIds, images: item.say.images }).filter(Boolean)
}

function resolveSayCover(item: UserCollectItem): string {
  const images = resolveSayImages(item)
  return images[0] || emptyCover
}

function resolveCover(item: UserCollectItem): string {
  if (item.targetType === 'article') {
    return item.article?.avatar || defaultCover
  }
  if (item.targetType === 'treehole') {
    return emptyCover
  }
  return resolveSayCover(item)
}

function resolveTitle(item: UserCollectItem): string {
  if (item.targetType === 'article') {
    return item.article?.title || '已删除文章'
  }
  if (item.targetType === 'treehole') {
    return item.treehole?.content ? `树洞 #${item.treehole.id}` : '已删除树洞'
  }
  return item.say?.content ? `说说 #${item.say.id}` : '已删除说说'
}

function resolveSummary(item: UserCollectItem): string {
  if (item.targetType === 'article') {
    return item.article?.summary || '这篇文章暂时没有摘要。'
  }
  if (item.targetType === 'treehole') {
    return item.treehole?.content || '这条树洞已不可见。'
  }
  return item.say?.content || '这条说说已不可见。'
}

function resolveTargetLabel(targetType: UserCollectItem['targetType']): string {
  if (targetType === 'article') return '文章'
  if (targetType === 'say') return '说说'
  return '树洞'
}

function handleOpen(item: UserCollectItem) {
  if (item.targetType === 'article' && item.article?.id) {
    emit('open', item.article.id)
    return
  }
  if (item.targetType === 'say' && item.say?.id) {
    const content = item.say?.content || ''
    const createTime = item.say?.createTime || item.createTime
    const images = resolveSayImages(item)
    previewSay.value = {
      content,
      createTime,
      images,
      likeCount: Number(item.say?.likeCount ?? 0),
      commentCount: Number(item.say?.commentCount ?? 0)
    }
    showSayPreview.value = true
  }
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.content-section {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.content-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.article-list,
.articles {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-list {
  flex: 1;
  min-height: 0;
}

.list-loading {
  display: grid;
}

.article-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.article-item--clickable {
  cursor: pointer;
}

.article-item--clickable:hover {
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.article-cover {
  width: 160px;
  height: 100px;
  border-radius: 12px;
  flex-shrink: 0;
  overflow: hidden;
}

.article-info {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-size: 1rem;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--title-color);
}

.article-summary {
  font-size: 0.88rem;
  color: var(--text-secondary);
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.article-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-chip {
  min-height: 28px;
  padding: 0 10px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.empty-tip {
  text-align: center;
  color: var(--text-secondary);
  padding: 28px;
  font-size: 0.95rem;
}

.comment-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--border-panel-subtle);
}

.comment-pagination__summary {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-secondary);
  font-size: 0.86rem;
}

.comment-pagination__dot {
  width: 4px;
  height: 4px;
  border-radius: 999px;
  background: var(--border-panel-default);
}

.say-preview {
  display: grid;
  gap: 14px;
}

.say-preview__meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.say-preview__content {
  margin: 0;
  color: var(--text-primary);
  line-height: 1.72;
  white-space: pre-wrap;
  word-break: break-word;
}

.say-preview__gallery {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
}

.say-preview__image-tile {
  border-radius: 12px;
  overflow: hidden;
  background: var(--surface-panel-inset);
}

.say-preview__image {
  width: 100%;
  aspect-ratio: 1;
}

.say-preview__empty {
  display: grid;
  place-items: center;
  min-height: 140px;
  border-radius: 12px;
  background: var(--surface-panel-inset);
}

.image-error-slot {
  width: 100%;
  height: 100%;
  min-height: 100px;
  display: grid;
  place-items: center;
  background: var(--surface-panel-inset);
}

.image-error-slot--tile {
  min-height: 120px;
}

@include sourcelin-down(md) {
  .article-item {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 180px;
  }

  .comment-pagination {
    justify-content: center;
  }

  .comment-pagination__summary {
    width: 100%;
    justify-content: center;
  }
}
</style>
