<script setup lang="ts">
import { useTagPageQuery } from '@/modules/article/composables/useTagPageQuery'
import TagArticleList from '@/modules/article/components/TagArticleList.vue'
import TagFilterBar from '@/modules/article/components/TagFilterBar.vue'
import TagHeroSection from '@/modules/article/components/TagHeroSection.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import '@/modules/article/styles/tag-page.scss'

const {
  loadingTags,
  loadingArticles,
  tags,
  tagArticles,
  activeTag,
  pendingTagId,
  articlePage,
  articlePageSize,
  articleTotal,
  tagSwitchSectionRef,
  articleTotalPages,
  tagGridFoldExpandLabel,
  queryTagName,
  sortedTags,
  featuredTag,
  isDetailMode,
  currentPanelEyebrow,
  tagContextStyle,
  tagHeroStatTiles,
  activeKeywords,
  detailStageCopy,
  getTagSwitchScrollAnchor,
  resolveTagDescription,
  selectTag,
  resetTagView,
  handleArticlePageChange,
  goToArticle
} = useTagPageQuery()
</script>

<template>
  <div class="glass-page tag-page">
    <PageShell>
      <TagHeroSection
        :loading-tags="loadingTags"
        :tags="tags"
        :stat-tiles="tagHeroStatTiles"
        :current-panel-eyebrow="currentPanelEyebrow"
        :featured-tag="featuredTag"
        :active-keywords="activeKeywords"
        :resolve-tag-description="resolveTagDescription"
      />

      <section
        v-if="tags.length"
        ref="tagSwitchSectionRef"
        class="tag-switch-shell"
        :style="tagContextStyle"
        aria-labelledby="tag-switch-title"
      >
        <TagFilterBar
          v-if="!isDetailMode"
          :tags="tags"
          :sorted-tags="sortedTags"
          :pending-tag-id="pendingTagId"
          :fold-expand-label="tagGridFoldExpandLabel"
          :get-scroll-anchor="getTagSwitchScrollAnchor"
          :resolve-tag-description="resolveTagDescription"
          @select-tag="selectTag"
        />

        <TagArticleList
          v-else
          :active-tag="activeTag"
          :detail-stage-copy="detailStageCopy"
          :loading-articles="loadingArticles"
          :articles="tagArticles"
          :article-page="articlePage"
          :article-page-size="articlePageSize"
          :article-total="articleTotal"
          :article-total-pages="articleTotalPages"
          @reset="resetTagView"
          @go-article="goToArticle"
          @update-page="handleArticlePageChange"
        />
      </section>

      <EmptyState
        v-else
        class="tag-state"
        variant="page"
        icon="tag"
        title="暂无标签数据"
        :message="queryTagName ? `还没找到“${queryTagName}”，换个标签名称试试。` : '这里还没有标签数据，稍后再来看看。'"
      />
    </PageShell>
  </div>
</template>
