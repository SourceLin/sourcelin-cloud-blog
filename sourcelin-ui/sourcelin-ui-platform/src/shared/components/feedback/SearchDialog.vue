<script setup lang="ts">
import { computed, onUnmounted, watch } from 'vue'
import { RouterLink } from 'vue-router'
import SkeletonSearchResults from '@/shared/components/feedback/skeletons/SkeletonSearchResults.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'
import { useSearchDialog } from '@/shared/composables/useSearchDialog'
import { createStartupScrollLock } from '@/shared/utils/startupScrollLock'

const {
  searchContainerRef,
  pageData,
  pages,
  total,
  list,
  tagList,
  loading,
  dialogVisible,
  closeModal,
  changePage,
  handleToTag,
  highlightKeyword,
  getTagHue
} = useSearchDialog()

const hasKeyword = computed(() => Boolean(pageData.keyword.trim()))
const hasResults = computed(() => list.value.length > 0)
const showLoadingSkeleton = computed(() => loading.value && hasKeyword.value)
const showTags = computed(() => hasKeyword.value && !hasResults.value && tagList.value.length > 0)
const showIdle = computed(() => !hasKeyword.value && !loading.value)
const showEmpty = computed(() => hasKeyword.value && !hasResults.value && !loading.value)
const emptyState = computed(() => {
  if (showIdle.value) {
    return {
      title: '开始搜索',
      description: '输入关键词，快速定位文章与主题。'
    }
  }

  if (showEmpty.value) {
    return {
      title: '未找到相关内容',
      description: '换个关键词，或从热门标签继续浏览。'
    }
  }

  return null
})
const resultMeta = computed(() => {
  if (loading.value) {
    return '正在整理匹配内容'
  }

  if (hasResults.value) {
    return `共找到 ${total.value} 篇相关文章`
  }

  if (hasKeyword.value) {
    return '没有找到完全匹配的结果'
  }

  return '输入关键词后，快速跳转到文章与主题'
})

const searchScrollLock = typeof document !== 'undefined' ? createStartupScrollLock(document) : null

watch(
  dialogVisible,
  (visible) => {
    searchScrollLock?.sync(visible)
  },
  { immediate: true }
)

onUnmounted(() => {
  searchScrollLock?.release()
})
</script>

<template>
  <transition name="modal-fade">
    <div v-if="dialogVisible" class="sourcelin-modal-mask search-modal-root" @click.self="closeModal">
      <div ref="searchContainerRef" class="search-container sourcelin-modal">
        <div class="search-header">
          <div class="search-heading">
            <div class="search-kicker">
              <SIcon :icon="appIcons.search" :size="14" />
              <span>站内搜索</span>
            </div>
          </div>

          <div class="search-actions">
            <SSurfaceChip class="close-btn" tag="button" type="button" variant="button" aria-label="关闭" @click="closeModal">
              <SIcon :icon="appIcons.close" :size="16" />
            </SSurfaceChip>
          </div>
        </div>

        <SSurfacePanel class="search-input-shell" variant="inset">
          <SInput
            v-model="pageData.keyword"
            class="search-input"
            placeholder="搜索文章、标签与主题..."
            clearable
            @keydown.esc="closeModal"
          >
            <template #prefix>
              <SIcon :icon="appIcons.search" :size="18" />
            </template>
          </SInput>
        </SSurfacePanel>

        <div class="search-content">
          <SSurfacePanel tag="aside" class="search-aside" variant="soft">
            <div class="aside-block">
              <div class="aside-title">
                <SIcon :icon="appIcons.sparkles" :size="16" />
                <span>检索状态</span>
              </div>
              <div class="aside-metrics">
                <SSurfacePanel class="metric-card" variant="inset">
                  <span class="metric-label">关键词</span>
                  <strong class="metric-value">{{ hasKeyword ? pageData.keyword : '待输入' }}</strong>
                </SSurfacePanel>
                <SSurfacePanel class="metric-card" variant="inset">
                  <span class="metric-label">结果数</span>
                  <strong class="metric-value">{{ total }}</strong>
                </SSurfacePanel>
              </div>
            </div>

            <div class="aside-block">
              <div class="aside-title">
                <SIcon :icon="appIcons.arrowForward" :size="16" />
                <span>使用方式</span>
              </div>
              <div class="aside-notes">
                <SSurfacePanel class="aside-note" variant="inset">
                  <span class="note-label">文章</span>
                  <p>输入标题、摘要或主题词，可快速跳转到对应内容。</p>
                </SSurfacePanel>
                <SSurfacePanel class="aside-note" variant="inset">
                  <span class="note-label">标签</span>
                  <p>输入后无结果时，可先从高频主题入口继续浏览。</p>
                </SSurfacePanel>
              </div>
            </div>
          </SSurfacePanel>

          <SSurfacePanel
            tag="section"
            class="search-main"
            variant="soft"
            :class="{ 'search-main--empty': Boolean(emptyState) && !showLoadingSkeleton }"
            :aria-busy="showLoadingSkeleton"
          >
            <template v-if="showLoadingSkeleton">
              <SkeletonSearchResults :count="Math.min(pageData.pageSize, 6)" />
            </template>
            <template v-else>
              <template v-if="emptyState">
                <SIcon class="empty-icon" :icon="appIcons.search" :size="28" />
                <p class="empty-title">{{ emptyState.title }}</p>
                <span class="empty-description">{{ emptyState.description }}</span>
              </template>

              <div v-if="showTags" class="tags-section">
                <div class="section-head">
                  <div class="section-title">
                    <SIcon :icon="appIcons.tag" :size="18" />
                    <span>热门标签</span>
                  </div>
                  <span class="section-meta">没有精确结果时，可从标签继续探索</span>
                </div>

                <div class="tags-grid">
                  <SSurfacePanel
                    v-for="(tag, index) in tagList"
                    :key="tag.id"
                    tag="button"
                    type="button"
                    class="search-tag-item"
                    variant="inset"
                    :style="{ '--tag-hue': getTagHue(index) }"
                    @click="handleToTag(tag.id)"
                  >
                    <span class="tag-icon">#</span>
                    <span class="tag-name">{{ tag.name }}</span>
                  </SSurfacePanel>
                </div>
              </div>

              <div v-if="hasResults" class="results-section">
                <div class="section-head">
                  <div class="section-title">
                    <SIcon :icon="appIcons.document" :size="18" />
                    <span>搜索结果</span>
                  </div>
                  <span class="section-meta">{{ total }} 篇匹配内容</span>
                </div>

                <div class="results-list">
                  <SSurfacePanel
                    v-for="item in list"
                    :key="item.id"
                    :tag="RouterLink"
                    :to="`/article/${item.id}`"
                    class="result-item"
                    variant="inset"
                    @click="closeModal"
                  >
                    <SSurfaceChip tag="div" class="result-leading" variant="badge">
                      <SIcon :icon="appIcons.document" :size="14" />
                    </SSurfaceChip>
                    <div class="result-content">
                      <h3 class="result-title" v-html="highlightKeyword(item.title)" />
                      <p class="result-summary">{{ item.summary }}</p>
                    </div>
                    <SIcon class="result-arrow" :icon="appIcons.arrowForward" :size="18" />
                  </SSurfacePanel>
                </div>

                <div v-if="pages > 1" class="pagination">
                  <SSurfaceChip
                    class="page-btn"
                    tag="button"
                    type="button"
                    variant="button"
                    :disabled="pageData.page <= 1"
                    @click="changePage(pageData.page - 1)"
                  >
                    <SIcon :icon="appIcons.back" :size="18" />
                  </SSurfaceChip>
                  <span class="page-info">{{ pageData.page }} / {{ pages }}</span>
                  <SSurfaceChip
                    class="page-btn"
                    tag="button"
                    type="button"
                    variant="button"
                    :disabled="pageData.page >= pages"
                    @click="changePage(pageData.page + 1)"
                  >
                    <SIcon :icon="appIcons.arrowForward" :size="18" />
                  </SSurfaceChip>
                </div>
              </div>
            </template>
          </SSurfacePanel>
        </div>

      </div>
    </div>
  </transition>
</template>

<style lang="scss" scoped>
@import '@/shared/styles/responsive';

.search-modal-root {
  align-items: flex-start;
  padding: clamp(40px, 9vh, 88px) 24px 32px;
  background: var(--modal-overlay-scrim);
  -webkit-backdrop-filter: var(--modal-overlay-backdrop-filter);
  backdrop-filter: var(--modal-overlay-backdrop-filter);
}

.search-container {
  width: min(100%, 980px);
  max-height: min(82vh, 760px);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  padding: 18px;
  gap: 16px;
  overflow: hidden;
}

.search-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 4px 4px 0;
  position: relative;
  z-index: 1;
}

.search-heading,
.search-actions,
.section-title,
.aside-title {
  display: flex;
  align-items: center;
}

.search-heading {
  gap: 14px;
  min-width: 0;
  color: var(--primary-color);
}

.search-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: var(--primary-color);
  font-size: var(--font-size-lg);
  font-weight: 600;
}

.search-actions {
  gap: 10px;
  flex-shrink: 0;
}

.close-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  padding: 0;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    color var(--transition-base),
    transform var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
}

.close-btn:hover {
  color: var(--primary-color);
  border-color: var(--border-interactive-hover);
  transform: translateY(-1px);
}

.search-input-shell {
  position: relative;
  z-index: 1;
  padding: 12px;
}

.search-input {
  width: 100%;
}

.search-content {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(220px, 260px) minmax(0, 1fr);
  gap: 16px;
  overflow: hidden;
}

.search-aside,
.search-main {
  min-width: 0;
  min-height: 0;
}

.search-aside {
  padding: 18px;
  display: grid;
  align-content: start;
  gap: 18px;
  overflow-y: auto;
}

.search-aside::-webkit-scrollbar,
.search-main::-webkit-scrollbar {
  width: 6px;
}

.search-aside::-webkit-scrollbar-track,
.search-main::-webkit-scrollbar-track {
  background: transparent;
}

.search-aside::-webkit-scrollbar-thumb,
.search-main::-webkit-scrollbar-thumb {
  background: var(--scrollbar-thumb);
  border-radius: 999px;
}

.aside-block {
  display: grid;
  gap: 12px;
}

.aside-title,
.section-title {
  gap: 8px;
  color: var(--title-color);
  font-size: var(--font-size-md);
  font-weight: 700;
}

.aside-metrics,
.aside-notes {
  display: grid;
  gap: 10px;
}

.metric-card,
.aside-note {
  padding: 14px;
  display: grid;
  gap: 6px;
}

.metric-label,
.note-label,
.section-meta {
  color: var(--text-secondary);
  font-size: var(--font-size-base);
}

.metric-value {
  color: var(--title-color);
  font-size: var(--font-size-lg);
  line-height: 1.2;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.aside-note p {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-md);
  line-height: 1.6;
}

.search-main {
  padding: 18px;
  overflow-y: auto;
  display: grid;
  align-content: start;
  gap: 18px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.search-tag-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border: 1px solid var(--border-panel-chip);
  cursor: pointer;
  color: hsl(var(--tag-hue, 220), 55%, 46%);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.search-tag-item:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, hsl(var(--tag-hue, 220), 55%, 46%) 48%, transparent);
  box-shadow: var(--shadow-panel-soft);
}

.tag-icon {
  font-weight: 700;
  opacity: 0.72;
}

.tag-name {
  font-size: var(--font-size-md);
  font-weight: 600;
}

.results-list {
  display: grid;
  gap: 10px;
}

.result-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  text-decoration: none;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.result-item:hover {
  transform: translateX(4px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--shadow-panel-soft);
}

.result-leading {
  width: 38px;
  height: 38px;
  justify-content: center;
  color: var(--primary-color);
}

.result-content {
  min-width: 0;
}

.result-title {
  margin: 0 0 6px;
  color: var(--title-color);
  font-size: var(--font-size-lg);
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.result-title :deep(mark.highlight) {
  padding: 0 4px;
  border-radius: var(--border-radius-sm);
  background: color-mix(in srgb, var(--primary-color) 18%, transparent);
  color: var(--primary-active);
}

.result-summary {
  margin: 0;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.result-arrow {
  color: var(--text-secondary);
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    opacity var(--transition-base);
  opacity: 0.68;
}

.result-item:hover .result-arrow {
  color: var(--primary-color);
  transform: translateX(3px);
  opacity: 1;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding-top: 4px;
}

.page-btn {
  width: 36px;
  height: 36px;
  justify-content: center;
  padding: 0;
  border: none;
  color: var(--text-primary);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    border-color var(--transition-base);
}

.page-btn:hover:not(:disabled) {
  color: var(--primary-color);
  border-color: var(--border-interactive-hover);
  transform: translateY(-1px);
}

.page-btn:disabled {
  opacity: 0.42;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
  font-size: var(--font-size-md);
}

.search-main--empty {
  min-height: 220px;
  align-content: center;
  justify-items: center;
  text-align: center;
}

.empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
}

.empty-title {
  margin: 0;
  color: var(--title-color);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.empty-description {
  color: var(--text-secondary);
  font-size: var(--font-size-md);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .search-container,
.modal-fade-leave-active .search-container {
  transition:
    transform 0.32s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.32s cubic-bezier(0.22, 1, 0.36, 1);
}

.modal-fade-enter-from .search-container,
.modal-fade-leave-to .search-container {
  transform: translateY(-14px) scale(0.975);
  opacity: 0;
}

@include sourcelin-down(lg) {
  .search-content {
    grid-template-columns: 220px minmax(0, 1fr);
  }
}

@include sourcelin-down(md) {
  .search-modal-root {
    padding: 24px 16px;
    align-items: stretch;
  }

  .search-container {
    width: 100%;
    max-height: calc(100vh - 48px);
    padding: 14px;
    gap: 14px;
  }

  .search-header {
    align-items: stretch;
    flex-direction: row;
  }

  .search-actions {
    justify-content: space-between;
  }

  .search-content {
    grid-template-columns: 1fr;
  }

  .search-aside {
    order: 2;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
}

@include sourcelin-down(sm) {
  .search-modal-root {
    padding: 0;
  }

  .search-container {
    max-height: 100vh;
    min-height: 100vh;
    border-radius: 0;
    padding: 14px 12px;
  }

  .search-aside {
    grid-template-columns: 1fr;
  }

  .search-main {
    padding: 14px;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .result-item {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .result-arrow {
    display: none;
  }
}

</style>
