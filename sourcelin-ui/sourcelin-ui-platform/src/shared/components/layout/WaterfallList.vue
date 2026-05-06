<template>
  <div class="waterfall-list">
    <Waterfall
      v-if="displayItems.length > 0"
      :items="displayItems"
      :column-width="columnWidth"
      :gap="gap"
    >
      <template #default="{ item }">
        <slot :item="item" :index="getIndex(item)" :height-type="getHeightClass(item)" />
      </template>
    </Waterfall>

    <div
      v-if="loading && !displayItems.length"
      class="loading-state loading-skeleton"
      :class="{ 'loading-state--cards': showCardLoadingGrid }"
      :style="showCardLoadingGrid ? loadingGridStyle : undefined"
      aria-busy="true"
      aria-live="polite"
    >
      <slot v-if="$slots.loading" name="loading" />
      <template v-else-if="showCardLoadingGrid">
        <SkeletonArticleCard
          v-for="index in loadingSkeletonCount"
          :key="`waterfall-card-skeleton-${index}`"
          :count="1"
          variant="card"
        />
      </template>
      <SkeletonArticleCard v-else :count="loadingSkeletonCount" :variant="loadingSkeletonVariant" />
    </div>

    <div
      v-if="!finished && displayItems.length > 0"
      class="load-more-trigger"
      :class="{
        'is-loading': showLoadingMore,
        'is-quiet': loadingMoreMode === 'quiet'
      }"
      :aria-busy="showLoadingMore"
      @click="!showLoadingMore && emit('loadMore')"
    >
      <slot v-if="showLoadingMore && loadingMoreMode === 'spinner'" name="loading-more">
        <span class="loading-spinner" aria-hidden="true" />
        <span>{{ loadingText }}</span>
      </slot>
      <slot v-else name="load-more">
        <span>{{ showLoadingMore && loadingMoreMode === 'quiet' ? '正在继续追加内容…' : loadMoreText }}</span>
      </slot>
    </div>

    <div v-if="finished && displayItems.length > 0" class="finished-tip">
      <slot name="finished">
        <span>{{ finishedText }}</span>
      </slot>
    </div>

    <div v-if="!loading && !displayItems.length" class="waterfall-empty-state">
      <slot name="empty">
        <EmptyState
          class="waterfall-empty"
          variant="section"
          size="small"
          icon="document"
          title="暂无内容"
          message="内容正在整理中，请稍后查看。"
        />
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue'
import { Waterfall } from '@zxzinn/vue-waterfall'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SkeletonArticleCard from '@/shared/components/feedback/skeletons/SkeletonArticleCard.vue'

type WaterfallInputItem = {
  coverImage?: string
  image?: string
  summary?: string
}

type DisplayWaterfallItem = WaterfallInputItem & {
  _heightType: 'short' | 'medium' | 'tall'
  _index: number
}

interface Props {
  items: unknown[]
  columnWidth?: number
  gap?: number
  loading?: boolean
  loadingMore?: boolean
  loadingSkeletonCount?: number
  loadingSkeletonVariant?: 'card' | 'list'
  loadingMoreMode?: 'spinner' | 'quiet'
  finished?: boolean
  loadMoreText?: string
  loadingText?: string
  finishedText?: string
}

const props = withDefaults(defineProps<Props>(), {
  columnWidth: 300,
  gap: 16,
  loading: false,
  loadingMore: false,
  loadingSkeletonCount: 6,
  loadingSkeletonVariant: 'card',
  loadingMoreMode: 'spinner',
  finished: false,
  loadMoreText: '加载更多',
  loadingText: '加载中...',
  finishedText: '没有更多了'
})

const emit = defineEmits<{
  loadMore: []
}>()

const slots = useSlots()

function toWaterfallInputItem(item: unknown): WaterfallInputItem {
  if (typeof item === 'object' && item !== null) {
    return item as WaterfallInputItem
  }

  return {}
}

const displayItems = computed<DisplayWaterfallItem[]>(() => {
  return props.items.map((rawItem, index) => {
    const item = toWaterfallInputItem(rawItem)
    const hasImage = Boolean(item.coverImage || item.image)
    const summary = typeof item.summary === 'string' ? item.summary : ''
    const hasSummary = summary.length > 30

    let heightType: DisplayWaterfallItem['_heightType'] = 'medium'
    if (!hasImage && !hasSummary) {
      heightType = 'short'
    } else if (hasImage && hasSummary) {
      heightType = 'tall'
    }

    return { ...(rawItem as object), _heightType: heightType, _index: index } as DisplayWaterfallItem
  })
})

const showLoadingMore = computed(() => props.loadingMore || (props.loading && displayItems.value.length > 0))
const showCardLoadingGrid = computed(() =>
  props.loading &&
  !displayItems.value.length &&
  props.loadingSkeletonVariant === 'card' &&
  !slots.loading
)
const loadingGridStyle = computed(() => ({
  '--waterfall-loading-column-width': `${props.columnWidth}px`,
  '--waterfall-loading-gap': `${props.gap}px`
}))

function getHeightClass(item: DisplayWaterfallItem) {
  return item._heightType
}

function getIndex(item: DisplayWaterfallItem) {
  return item._index
}
</script>

<style scoped lang="scss">
.waterfall-list {
  width: 100%;
  min-height: 200px;
}

.loading-state,
.load-more-trigger.is-loading:not(.is-quiet) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  gap: 12px;
  width: 100%;

  span {
    font-size: 0.9rem;
    color: var(--text-color-muted);
  }
}

.loading-spinner {
  display: inline-block;
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-skeleton {
  display: grid;
  padding: 0;
}

.loading-state--cards {
  grid-template-columns: repeat(
    auto-fit,
    minmax(min(var(--waterfall-loading-column-width), 100%), 1fr)
  );
  gap: var(--waterfall-loading-gap);
  align-items: start;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.load-more-trigger {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  width: 100%;
  cursor: pointer;
  transition: opacity var(--transition-base);

  span {
    padding: 10px 24px;
    background: var(--primary-light);
    color: var(--primary-color);
    border-radius: 20px;
    font-size: 0.9rem;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px var(--primary-light);
    }
  }
}

.load-more-trigger.is-loading {
  cursor: default;
}

.load-more-trigger.is-loading span:hover {
  transform: none;
  box-shadow: none;
}

.load-more-trigger.is-quiet {
  padding: 18px 24px 10px;
}

.load-more-trigger.is-quiet span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0;
  background: transparent;
  color: var(--text-secondary);
  box-shadow: none;
}

.load-more-trigger.is-quiet span::before {
  content: '';
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--primary-color) 72%, transparent);
  box-shadow: 0 0 0 6px color-mix(in srgb, var(--primary-color) 10%, transparent);
  opacity: 0;
  transform: scale(0.9);
  transition: opacity var(--transition-base), transform var(--transition-base);
}

.load-more-trigger.is-quiet.is-loading span::before {
  opacity: 1;
  transform: scale(1);
  animation: quiet-pulse 1.3s ease infinite;
}

.load-more-trigger.is-quiet span:hover {
  transform: none;
  box-shadow: none;
}

.finished-tip {
  display: flex;
  justify-content: center;
  padding: 24px;
  width: 100%;

  span {
    font-size: 0.85rem;
    color: var(--text-color-muted);
    position: relative;

    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 40px;
      height: 1px;
      background: var(--border-color);
    }

    &::before {
      right: calc(100% + 12px);
    }

    &::after {
      left: calc(100% + 12px);
    }
  }
}

.waterfall-empty-state {
  width: 100%;
}

.waterfall-empty {
  margin-top: var(--spacing-sm);
}

@keyframes quiet-pulse {
  0%,
  100% {
    box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary-color) 8%, transparent);
  }

  50% {
    box-shadow: 0 0 0 7px color-mix(in srgb, var(--primary-color) 16%, transparent);
  }
}
</style>
