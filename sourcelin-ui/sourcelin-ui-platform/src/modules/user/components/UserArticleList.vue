<template>
  <div class="content-section" :aria-busy="loading">
    <div v-if="loading" class="list-loading" aria-live="polite">
      <SkeletonArticleCard variant="list" :count="3" />
    </div>
    <div v-else class="content-body">
      <div class="article-list">
        <div v-if="items.length" class="articles">
          <SSurfacePanel
            v-for="article in items"
            :key="article.id"
            class="article-item"
            variant="default"
            interactive
            @click="emit('open', article.id)"
          >
            <img :src="article.avatar" class="article-cover" :alt="article.title">
            <div class="article-info">
              <div class="article-header">
                <h4 class="article-title">{{ article.title }}</h4>
                <SStatusTag :type="tagOf(article.status) || 'default'">{{ labelOf(article.status) || '暂不可见' }}</SStatusTag>
              </div>
              <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
              <div class="article-meta">
                <span class="meta-chip">{{ formatDate(article.createTime) }}</span>
                <span class="meta-chip">{{ article.viewCount }} 浏览</span>
                <span class="meta-chip">{{ article.likeCount }} 点赞</span>
                <span class="meta-chip">{{ article.commentCount }} 评论</span>
              </div>
            </div>
            <div class="article-actions">
              <SButton size="small" @click.stop="emit('edit', article.id)">编辑</SButton>
              <SButton
                size="small"
                type="error"
                ghost
                :disabled="!canDelete(article.status)"
                @click.stop="emit('delete', article)"
              >
                删除
              </SButton>
            </div>
          </SSurfacePanel>
        </div>
        <div v-else class="empty-tip">还没有文章，开始写你的第一篇吧。</div>
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
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SkeletonArticleCard from '@/shared/components/feedback/skeletons/SkeletonArticleCard.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SStatusTag from '@/shared/components/ui/SStatusTag.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import type { UserArticleItem } from '@/modules/user/composables/useUserCenter'
import { useDict } from '@/shared/composables/useDict'

const props = defineProps<{
  loading: boolean
  items: UserArticleItem[]
  page: number
  size: number
  total: number
  formatDate: (v: string) => string
}>()

const emit = defineEmits<{
  open: [id: number]
  edit: [id: number]
  delete: [article: UserArticleItem]
  'update:page': [page: number]
}>()

const { labelOf, tagOf } = useDict('blog_status')
const totalPages = computed(() => Math.max(Math.ceil(props.total / Math.max(props.size, 1)), 1))
const currentPage = computed(() => Math.min(Math.max(props.page, 1), totalPages.value))
const showPagination = computed(() => props.total > props.size)

function canDelete(status: number) {
  return status === 1 || status === 3
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
  display: grid;
  grid-template-columns: 160px minmax(0, 1fr) auto;
  gap: 16px;
  align-items: center;
  padding: 16px;
  border-color: var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  cursor: pointer;
}

.article-item:hover {
  border-color: var(--border-interactive-hover);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.article-cover {
  width: 160px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
}

.article-info {
  min-width: 0;
}

.article-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.article-title {
  font-size: 1rem;
  font-weight: 700;
  margin: 0;
  color: var(--title-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-summary {
  font-size: 0.88rem;
  color: var(--text-secondary);
  margin: 0 0 10px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.meta-chip {
  min-height: 28px;
  padding: 0 10px;
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.article-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
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

@include sourcelin-down(md) {
  .article-item {
    grid-template-columns: 1fr;
  }

  .article-cover {
    width: 100%;
    height: 180px;
  }

  .article-actions {
    flex-direction: row;
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
