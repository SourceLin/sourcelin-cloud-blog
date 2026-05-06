<template>
  <div v-if="displayCategories.length" class="category-shell">
    <!-- 标题栏 -->
    <div class="category-header">
      <RouterLink to="/categories" class="category-header__link">
        <span class="category-header__orb" aria-hidden="true">
          <span class="category-header__orb-core" />
        </span>
        <h3 class="category-header__title">热门分类</h3>
      </RouterLink>
      <span class="category-header__bridge" aria-hidden="true" />
    </div>

    <!-- 分类列表滚动容器 -->
    <div
      ref="scrollWrapperRef"
      class="category-scroll-wrapper"
      :class="scrollWrapperClass"
      @mouseenter="onMouseEnter"
      @mouseleave="onMouseLeave"
    >
      <ul ref="scrollListRef" class="category-list" :style="scrollStyle">
        <!-- 原始列表 -->
        <li
          v-for="category in displayCategories"
          :key="`a-${category.id}`"
          class="category-item"
          :style="{ '--category-hue': category.hue || 245 }"
        >
          <RouterLink
            :to="{ path: '/categories', query: { name: category.name } }"
            class="category-item__link"
          >
            <span class="category-item__icon">
              <SIcon :icon="getCategoryIcon(category.icon)" :size="16" />
            </span>
            <span class="category-item__name">{{ category.name }}</span>
            <span class="category-item__count">{{ category.count || 0 }}</span>
          </RouterLink>
        </li>
        <!-- 复制列表：无缝衔接（仅滚动时渲染） -->
        <template v-if="shouldScroll">
          <li
            v-for="category in displayCategories"
            :key="`b-${category.id}`"
            class="category-item"
            :style="{ '--category-hue': category.hue || 245 }"
            aria-hidden="true"
          >
            <RouterLink
              :to="{ path: '/categories', query: { name: category.name } }"
              class="category-item__link"
              tabindex="-1"
            >
              <span class="category-item__icon">
                <SIcon :icon="getCategoryIcon(category.icon)" :size="16" />
              </span>
              <span class="category-item__name">{{ category.name }}</span>
              <span class="category-item__count">{{ category.count || 0 }}</span>
            </RouterLink>
          </li>
        </template>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import type { AppIconName } from '@/shared/components/ui/icons'
import type { Component } from 'vue'
import type { HomeCategory } from '@/modules/home/model/home-discover'

/* ─── Props ─── */
interface Props {
  categories?: HomeCategory[]
}

const props = withDefaults(defineProps<Props>(), {
  categories: () => []
})

/* ─── 常量 ─── */
const MAX_VISIBLE = 15       // 最多展示分类数
const SCROLL_THRESHOLD = 6   // 超过此数量启动自动滚动
const SCROLL_SPEED = 0.03    // px/ms，即 30px/s

/* ─── 响应式状态 ─── */
const scrollOffset = ref(0)
const isHovering = ref(false)
const scrollWrapperRef = ref<HTMLElement | null>(null)
const scrollListRef = ref<HTMLElement | null>(null)

let animationFrameId: number | null = null
let lastTimestamp = 0

/* ─── 计算属性 ─── */
const displayCategories = computed(() => props.categories.slice(0, MAX_VISIBLE))

const shouldScroll = computed(() => props.categories.length > SCROLL_THRESHOLD)

const scrollWrapperClass = computed(() => ({
  'is-scrolling': shouldScroll.value,
  'is-paused': isHovering.value
}))

const scrollStyle = computed(() => {
  if (!shouldScroll.value) return {}
  return { transform: `translateY(-${scrollOffset.value}px)` }
})

/* ─── 图标映射 ─── */
const iconByName: Record<string, AppIconName> = {
  folder: 'folder',
  folderopen: 'folderOpen',
  category: 'category',
  tag: 'tag',
  document: 'document',
  archive: 'archive',
  home: 'home',
  grid: 'grid',
  star: 'star',
  flame: 'flame',
  brand: 'brand',
  links: 'links',
  navigation: 'navigation',
  about: 'about',
  notice: 'notice'
}

function getCategoryIcon(icon: string | undefined): Component {
  if (!icon) return appIcons.folder
  const key = iconByName[icon.trim().toLowerCase()] || 'folder'
  return appIcons[key]
}

/* ─── 滚动交互 ─── */
function onMouseEnter() {
  isHovering.value = true
}

function onMouseLeave() {
  isHovering.value = false
}

/* ─── 无缝滚动动画 ─── */
// 获取单份列表的实际高度（DOM 渲染后 scrollHeight 是两份的高度）
function getSingleListHeight(): number {
  if (scrollListRef.value) {
    return shouldScroll.value ? scrollListRef.value.scrollHeight / 2 : scrollListRef.value.scrollHeight
  }
  // 降级：按估算计算
  return displayCategories.value.length * 44
}

function animate(timestamp: number) {
  if (!lastTimestamp) {
    lastTimestamp = timestamp
  }

  const delta = timestamp - lastTimestamp
  lastTimestamp = timestamp

  if (shouldScroll.value && !isHovering.value) {
    scrollOffset.value += delta * SCROLL_SPEED

    const singleListHeight = getSingleListHeight()
    if (scrollOffset.value >= singleListHeight) {
      scrollOffset.value -= singleListHeight
    }
  }

  animationFrameId = window.requestAnimationFrame(animate)
}

/* ─── 生命周期 ─── */
onMounted(() => {
  animationFrameId = window.requestAnimationFrame(animate)
})

onUnmounted(() => {
  if (animationFrameId !== null) {
    cancelAnimationFrame(animationFrameId)
  }
  animationFrameId = null
  lastTimestamp = 0
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

/* ─── Shell ─── */
.category-shell {
  position: relative;
  padding: 0.88rem;
  border-radius: calc(var(--border-radius-xl) + 4px);
  border: 1px solid var(--border-content-card);
  background:
    var(--surface-panel-specular-soft),
    var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  overflow: hidden;
}

/* ─── 标题栏（与 label-head / stats-header 完全对齐） ─── */
.category-header {
  margin-bottom: 0.75rem;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-header__link {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  text-decoration: none;
  color: inherit;
  transition: all var(--transition-base);
  padding: 0.25rem 0;
}

.category-header__link:hover {
  color: var(--primary-color);
}

.category-header__link:hover .category-header__orb {
  transform: scale(1.08);
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 95%, transparent),
    0 3px 12px color-mix(in srgb, var(--primary-color) 28%, transparent);
}

.category-header__link:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
  border-radius: 4px;
}

.category-header__orb {
  flex-shrink: 0;
  width: 0.72rem;
  height: 0.72rem;
  border-radius: 50%;
  background: radial-gradient(
    circle at 35% 35%,
    color-mix(in srgb, var(--primary-color) 65%, var(--surface-white-12)),
    color-mix(in srgb, var(--primary-color) 85%, transparent)
  );
  box-shadow:
    inset 0 1px 2px color-mix(in srgb, var(--surface-white-25) 90%, transparent),
    0 2px 8px color-mix(in srgb, var(--primary-color) 20%, transparent);
  transition: all var(--transition-base);
}

.category-header__orb-core {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--primary-color) 70%, var(--surface-white-18)),
    color-mix(in srgb, var(--primary-color) 90%, transparent)
  );
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 95%, transparent);
}

.category-header__title {
  margin: 0;
  font-size: 0.88rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--text-secondary);
  background: none;
  -webkit-text-fill-color: currentColor;
  transition: color var(--transition-base);
}

.category-header__bridge {
  display: block;
  width: 100%;
  height: 1px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--primary-color) 75%, transparent) 0%,
    color-mix(in srgb, var(--primary-color) 45%, transparent) 40%,
    transparent 100%
  );
  opacity: 0.72;
}

/* ─── 滚动容器 ─── */
.category-scroll-wrapper {
  position: relative;
  z-index: 1;
}

/* 超过阈值时启用滚动 */
.category-scroll-wrapper.is-scrolling {
  overflow: hidden;
  max-height: calc(6 * 2.65rem);
  mask-image: linear-gradient(180deg, black 0%, black 88%, transparent 100%);
  -webkit-mask-image: linear-gradient(180deg, black 0%, black 88%, transparent 100%);
}

/* 悬停暂停时移除底部遮罩 */
.category-scroll-wrapper.is-scrolling.is-paused {
  mask-image: none;
  -webkit-mask-image: none;
}

/* ─── 列表 ─── */
.category-list {
  display: grid;
  gap: 0;
  margin: 0;
  padding: 0;
  list-style: none;
  will-change: transform;
}

/* ─── 列表项 ─── */
.category-item {
  min-width: 0;
}

.category-item:not(:last-child) {
  border-bottom: 1px solid color-mix(in srgb, var(--glass-border) 28%, transparent);
}

.category-item__link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.55rem 0.6rem;
  text-decoration: none;
  color: var(--text-primary);
  border-radius: var(--border-radius-lg);
  transition:
    background var(--transition-base),
    color var(--transition-base),
    transform var(--transition-base);
}

.category-item__link::after {
  content: '';
  position: absolute;
  inset: auto 0.6rem 0;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(
    90deg,
    color-mix(in srgb, hsl(var(--category-hue), 68%, 54%) 0%, transparent),
    color-mix(in srgb, hsl(var(--category-hue), 68%, 54%) 82%, transparent),
    transparent
  );
  opacity: 0;
  transform: scaleX(0.24);
  transform-origin: left center;
  transition:
    opacity var(--transition-base),
    transform var(--transition-base);
}

.category-item__link:hover::after {
  opacity: 0.72;
  transform: scaleX(1);
}

.category-item__link:hover {
  background: color-mix(in srgb, hsl(var(--category-hue), 70%, 94%) 12%, transparent);
  color: hsl(var(--category-hue), 60%, 45%);
  transform: translateX(2px);
}

.category-item__link:focus-visible {
  outline: 2px solid hsl(var(--category-hue), 68%, 54%);
  outline-offset: -2px;
}

.category-item__link:active {
  transform: translateX(2px) scale(0.98);
}

/* ─── 图标 ─── */
.category-item__icon {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: var(--border-radius-md);
  background: color-mix(in srgb, hsl(var(--category-hue), 70%, 94%) 72%, transparent);
  color: hsl(var(--category-hue), 60%, 45%);
  transition:
    background var(--transition-base),
    color var(--transition-base),
    transform var(--transition-base);
}

.category-item__link:hover .category-item__icon {
  background: color-mix(in srgb, hsl(var(--category-hue), 68%, 54%) 16%, transparent);
  color: hsl(var(--category-hue), 68%, 54%);
  transform: scale(1.08) rotate(4deg);
}

/* ─── 名称 ─── */
.category-item__name {
  flex: 1;
  min-width: 0;
  font-size: 0.85rem;
  font-weight: 500;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ─── 计数 ─── */
.category-item__count {
  position: relative;
  z-index: 1;
  flex-shrink: 0;
  min-width: 1.5rem;
  padding: 0.15rem 0.45rem;
  border-radius: 999px;
  background: color-mix(in srgb, hsl(var(--category-hue), 70%, 94%) 68%, transparent);
  color: hsl(var(--category-hue), 55%, 40%);
  font-size: 0.72rem;
  font-weight: 600;
  text-align: center;
  transition:
    background var(--transition-base),
    color var(--transition-base);
}

.category-item__link:hover .category-item__count {
  background: color-mix(in srgb, hsl(var(--category-hue), 68%, 54%) 14%, transparent);
  color: hsl(var(--category-hue), 68%, 54%);
}

/* ─── 响应式 ─── */
@include sourcelin-down(sm) {
  .category-shell {
    padding: 0.78rem;
  }

  .category-header__title {
    font-size: 0.84rem;
  }

  .category-item__link {
    padding: 0.45rem 0.5rem;
  }

  .category-item__name {
    font-size: 0.82rem;
  }
}
</style>
