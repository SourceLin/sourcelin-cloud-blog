<template>
  <div>
    <div class="section-head">
      <div>
        <h2 id="tag-switch-title" class="section-title">相关文章</h2>
      </div>
    </div>

    <div class="tag-article-stage">
      <div v-if="activeTag" class="context-focus-strip">
        <div class="context-focus-strip__icon">
          <SIcon :icon="appIcons.tag" :size="22" />
        </div>
        <div class="context-focus-strip__copy">
          <p class="context-focus-strip__eyebrow">当前标签</p>
          <div class="context-focus-strip__heading">
            <h3 class="context-focus-strip__title">{{ activeTag.name }}</h3>
          </div>
          <p class="context-focus-strip__desc">{{ detailStageCopy }}</p>
        </div>
        <div class="context-focus-strip__aside">
          <div class="context-focus-strip__metric">
            <span class="context-focus-strip__metric-label">当前页</span>
            <span class="context-focus-strip__metric-value">{{ articlePage }} / {{ articleTotalPages || 1 }}</span>
          </div>
          <button type="button" class="context-back-button" @click="$emit('reset')">
            <SIcon :icon="appIcons.back" :size="14" />
            返回标签
          </button>
        </div>
      </div>

      <ContextArticleCardSkeleton
        v-if="loadingArticles"
        variant="tag"
        :tag-accent="activeTag?.color || 'var(--primary-color)'"
      />

      <TransitionGroup v-else-if="articles.length" name="article-cascade" tag="div" class="context-article-list">
        <article
          v-for="(article, index) in articles"
          :key="article.id"
          class="context-article-card"
          :style="{ '--stagger-index': index, '--tag-accent': activeTag?.color || 'var(--primary-color)' }"
          @click="$emit('go-article', article.id)"
        >
          <div
            class="context-article-card__cover"
            :style="{ '--article-cover': article.cover ? `url(${article.cover})` : 'none' }"
          />
          <div class="context-article-card__body">
            <div class="context-article-card__topline">
              <div class="context-article-card__chips">
                <span class="context-article-card__chip">{{ activeTag?.name }}</span>
                <span
                  v-for="tagName in article.tags.slice(0, 2)"
                  :key="`${article.id}-${tagName}`"
                  class="context-article-card__chip"
                >
                  {{ tagName }}
                </span>
              </div>
              <span class="context-article-card__date">{{ article.date || '最近更新' }}</span>
            </div>
            <h3 class="context-article-card__title">{{ article.title }}</h3>
            <p class="context-article-card__summary">{{ article.summary }}</p>
            <div class="context-article-card__meta">
              <span>{{ article.authorName }}</span>
              <span>{{ article.views }} 阅读</span>
              <span>{{ article.comments }} 评论</span>
            </div>
          </div>
          <div class="context-article-card__arrow">
            <SIcon :icon="appIcons.arrowForward" :size="16" />
          </div>
        </article>
      </TransitionGroup>

      <EmptyState
        v-else
        class="context-state"
        variant="page"
        icon="document"
        title="当前标签下还没有文章"
        :message="activeTag ? `“${activeTag.name}”还没有文章，换个标签看看吧。` : '先选一个标签试试吧。'"
      />

      <div v-if="activeTag && articleTotal > articlePageSize" class="context-pagination">
        <div class="context-pagination__summary" aria-live="polite">
          <span>第 {{ articlePage }} / {{ articleTotalPages || 1 }} 页</span>
          <span>共 {{ articleTotal }} 篇</span>
        </div>
        <SPagination
          :page="articlePage"
          :page-size="articlePageSize"
          :item-count="articleTotal"
          simple
          @update:page="$emit('update-page', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TagArticle, TagItem } from '@/modules/article/composables/useTagPageQuery'
import ContextArticleCardSkeleton from '@/modules/article/components/ContextArticleCardSkeleton.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SPagination from '@/shared/components/ui/SPagination.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  activeTag: TagItem | null
  detailStageCopy: string
  loadingArticles: boolean
  articles: TagArticle[]
  articlePage: number
  articlePageSize: number
  articleTotal: number
  articleTotalPages: number
}

defineProps<Props>()
defineEmits<{
  reset: []
  'go-article': [id: number]
  'update-page': [page: number]
}>()
</script>
