<script setup lang="ts">
import ContextArticleCardSkeleton from '@/modules/article/components/ContextArticleCardSkeleton.vue'
import {
  resolveCategoryIcon,
  type Category,
  type CategoryArticle
} from '@/modules/article/composables/useCategoryPageQuery'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SPagination from '@/shared/components/ui/SPagination.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  activeCategory: Category | null
  articleStageCopy: string
  loadingArticles: boolean
  articles: CategoryArticle[]
  articlePage: number
  articlePageSize: number
  articleTotal: number
  articleTotalPages: number
}>()

const emit = defineEmits<{
  reset: []
  goArticle: [id: number]
  updatePage: [page: number]
}>()
</script>

<template>
  <div class="category-article-stage">
    <div v-if="activeCategory" class="context-focus-strip">
      <div class="context-focus-strip__icon">
        <SIcon :icon="resolveCategoryIcon(activeCategory.icon)" :size="24" />
      </div>
      <div class="context-focus-strip__copy">
        <p class="context-focus-strip__eyebrow">当前分类</p>
        <div class="context-focus-strip__heading">
          <h3 class="context-focus-strip__title">{{ activeCategory.name }}</h3>
          <span class="context-focus-strip__badge">{{ activeCategory.articleCount }} 篇</span>
        </div>
        <p class="context-focus-strip__desc">{{ articleStageCopy }}</p>
      </div>
      <div class="context-focus-strip__aside">
        <div class="context-focus-strip__metric">
          <span class="context-focus-strip__metric-label">当前页</span>
          <span class="context-focus-strip__metric-value">
            {{ articlePage }} / {{ articleTotalPages || 1 }}
          </span>
        </div>
        <button type="button" class="context-back-button" @click="emit('reset')">
          <SIcon :icon="appIcons.back" :size="14" />
          返回分类
        </button>
      </div>
    </div>

    <ContextArticleCardSkeleton v-if="loadingArticles" variant="category" />

    <TransitionGroup
      v-else-if="articles.length"
      name="article-cascade"
      tag="div"
      class="context-article-list"
    >
      <article
        v-for="(article, index) in articles"
        :key="article.id"
        class="context-article-card"
        :style="{ '--stagger-index': index }"
        @click="emit('goArticle', article.id)"
      >
        <div
          class="context-article-card__cover"
          :style="{ '--article-cover': article.cover ? `url(${article.cover})` : 'none' }"
        />
        <div class="context-article-card__body">
          <div class="context-article-card__topline">
            <span class="context-article-card__chip">{{ activeCategory?.name }}</span>
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
      title="当前分类下还没有文章"
      message="这个分类还没有文章，先去看看其他分类吧。"
    />

    <div v-if="activeCategory && articleTotal > articlePageSize" class="context-pagination">
      <div class="context-pagination__summary" aria-live="polite">
        <span>第 {{ articlePage }} / {{ articleTotalPages || 1 }} 页</span>
        <span>共 {{ articleTotal }} 篇</span>
      </div>
      <SPagination
        :page="articlePage"
        :page-size="articlePageSize"
        :item-count="articleTotal"
        simple
        @update:page="(page: number) => emit('updatePage', page)"
      />
    </div>
  </div>
</template>
