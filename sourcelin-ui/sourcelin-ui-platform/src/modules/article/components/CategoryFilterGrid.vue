<script setup lang="ts">
import { resolveCategoryIcon, type Category } from '@/modules/article/composables/useCategoryPageQuery'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import FoldableOverflowBlock from '@/shared/components/layout/FoldableOverflowBlock.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  loadingCategories: boolean
  categories: Category[]
  pendingCategoryId: number | null
  foldExpandLabel: string
  getScrollAnchor: () => HTMLElement | null
}>()

const emit = defineEmits<{
  selectCategory: [category: Category]
}>()
</script>

<template>
  <div class="category-browser-stage">
    <div
      v-if="loadingCategories"
      class="category-categories-skel"
      aria-busy="true"
      aria-label="分类加载中"
    >
      <SSkeleton :sharp="false" class="category-categories-skel__title" />
      <div class="category-categories-skel__lines">
        <SSkeleton
          v-for="n in 4"
          :key="`cat-skel-${n}`"
          :sharp="false"
          class="category-categories-skel__line"
        />
      </div>
    </div>

    <div v-else-if="categories.length">
      <FoldableOverflowBlock
        :item-count="categories.length"
        :expand-button-text="foldExpandLabel"
        collapse-button-text="收起分类列表"
        :get-scroll-anchor="getScrollAnchor"
      >
        <div class="categories-grid">
          <button
            v-for="category in categories"
            :key="category.id"
            type="button"
            class="category-card"
            :class="{ 'is-armed': pendingCategoryId === category.id }"
            :style="{ '--icon-hue': category.hue }"
            @click="emit('selectCategory', category)"
          >
            <div class="category-icon">
              <SIcon :icon="resolveCategoryIcon(category.icon)" :size="28" />
            </div>
            <div class="category-info">
              <h3 class="category-name">{{ category.name }}</h3>
              <p class="category-desc">
                {{ category.description || `浏览“${category.name}”分类下的最新与历史文章。` }}
              </p>
            </div>
            <div class="category-count">
              <span class="count-value">{{ category.articleCount || 0 }}</span>
              <span class="count-label">篇文章</span>
            </div>
            <div class="category-arrow">
              <SIcon :icon="appIcons.arrowForward" :size="20" />
            </div>
          </button>
        </div>
      </FoldableOverflowBlock>
    </div>

    <EmptyState
      v-else
      class="context-state"
      variant="page"
      icon="category"
      title="暂无分类"
      message="这里还没有可用分类，稍后再来看看。"
    />
  </div>
</template>
