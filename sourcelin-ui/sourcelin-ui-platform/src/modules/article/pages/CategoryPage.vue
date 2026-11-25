<script setup lang="ts">
import { computed } from 'vue'
import CategoryArticleList from '@/modules/article/components/CategoryArticleList.vue'
import CategoryFilterGrid from '@/modules/article/components/CategoryFilterGrid.vue'
import CategoryHeroSection from '@/modules/article/components/CategoryHeroSection.vue'
import { useCategoryPageQuery } from '@/modules/article/composables/useCategoryPageQuery'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import { useSeoHead } from '@/shared/composables/useSeoHead'
import '@/modules/article/styles/category-page.scss'

const {
  categorySwitchSectionRef,
  categories,
  categoryArticles,
  activeCategory,
  pendingCategoryId,
  loadingCategories,
  loadingArticles,
  articlePage,
  articlePageSize,
  articleTotal,
  articleTotalPages,
  categoryFoldExpandLabel,
  isDetailMode,
  categoryContextStyle,
  categoryHeroStatTiles,
  articleStageCopy,
  getCategorySwitchScrollAnchor,
  selectCategory,
  resetCategoryView,
  handleArticlePageChange,
  goToArticle
} = useCategoryPageQuery()

useSeoHead({
  title: computed(() => '文章分类'),
  description: computed(() => '按分类浏览所有文章，快速找到你感兴趣的主题内容。'),
  canonicalUrl: computed(() =>
    typeof window !== 'undefined' ? `${window.location.origin}/categories` : ''
  )
})
</script>

<template>
  <div class="glass-page category-page">
    <PageShell>
      <CategoryHeroSection :stat-tiles="categoryHeroStatTiles" />

      <section
        ref="categorySwitchSectionRef"
        class="category-switch-shell"
        :style="categoryContextStyle"
        aria-labelledby="category-switch-title"
      >
        <div class="section-head category-switch-head">
          <div>
            <h2 id="category-switch-title" class="section-title">
              {{ isDetailMode ? '分类文章' : '全部分类' }}
            </h2>
            <p v-if="!isDetailMode" class="section-copy">
              点击分类后，当前区域会直接切换为该分类文章。
            </p>
          </div>
          <SSurfaceChip v-if="!isDetailMode" class="section-count" size="sm" variant="counter">
            {{ categories.length }} 个
          </SSurfaceChip>
        </div>

        <CategoryFilterGrid
          v-if="!isDetailMode"
          :loading-categories="loadingCategories"
          :categories="categories"
          :pending-category-id="pendingCategoryId"
          :fold-expand-label="categoryFoldExpandLabel"
          :get-scroll-anchor="getCategorySwitchScrollAnchor"
          @select-category="selectCategory"
        />

        <CategoryArticleList
          v-else
          :active-category="activeCategory"
          :article-stage-copy="articleStageCopy"
          :loading-articles="loadingArticles"
          :articles="categoryArticles"
          :article-page="articlePage"
          :article-page-size="articlePageSize"
          :article-total="articleTotal"
          :article-total-pages="articleTotalPages"
          @reset="resetCategoryView"
          @go-article="goToArticle"
          @update-page="handleArticlePageChange"
        />
      </section>
    </PageShell>
  </div>
</template>
