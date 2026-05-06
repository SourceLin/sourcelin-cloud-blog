<template>
  <header class="feature-hero">
    <div class="feature-backdrop">
      <img
        :src="coverImage"
        :alt="articleTitle"
        class="feature-backdrop-image"
        :style="mastheadBackdropScrollFilterStyle"
      >
      <div class="feature-backdrop-wash" />
    </div>

    <div class="feature-grid">
      <div class="issue-rail">
        <button class="breadcrumb-link" type="button" @click="emit('go-home')">首页</button>
        <span class="rail-divider" />
        <button class="breadcrumb-link" type="button" @click="emit('go-source')">{{ breadcrumbSourceLabel }}</button>
        <span class="rail-divider" />
        <span class="issue-mark">{{ issueLabel }}</span>
      </div>

      <div class="feature-story">
        <div class="story-kicker">
          <span class="kicker-tag">{{ heroKicker }}</span>
          <span class="kicker-text">SourceLin 专栏</span>
        </div>
        <h1 class="feature-title">{{ articleTitle }}</h1>
        <p class="feature-standfirst">{{ articleSummary }}</p>
        <div class="signature-panel">
          <div class="signature-main">
            <img class="signature-avatar" :src="authorAvatar" alt="avatar">
            <div class="signature-copy">
              <span class="signature-label">撰稿人</span>
              <strong class="signature-name">{{ authorName }}</strong>
              <span class="signature-remark">{{ authorIntroduction }}</span>
            </div>
          </div>
          <div class="signature-meta">
            <span><SIcon :icon="appIcons.calendar" :size="16" />{{ publishDate || '未发布' }}</span>
            <span><SIcon :icon="appIcons.eye" :size="16" />{{ readingCount }} 阅读</span>
            <span><SIcon :icon="appIcons.heart" :size="16" />{{ likeCount }} 点赞</span>
          </div>
        </div>
      </div>

      <div class="feature-collage">
        <div class="cover-stage">
          <img :src="coverImage" :alt="articleTitle" class="cover-stage-image">
          <div class="cover-stage-badge">{{ articleIdentityLabel }}</div>
        </div>
        <div class="collage-card excerpt-card">
          <span class="card-eyebrow">导读</span>
          <p class="excerpt-card-preview">{{ articleSummary }}</p>
        </div>
        <div class="collage-card stats-card">
          <span class="card-eyebrow">本篇速写</span>
          <div class="stats-grid">
            <div class="stats-cell"><strong>{{ readingMinutes }}</strong><span>分钟阅读</span></div>
            <div class="stats-cell"><strong>{{ articleWordCount || 0 }}</strong><span>正文字符</span></div>
            <div class="stats-cell"><strong>{{ sectionCount }}</strong><span>章节目录</span></div>
            <div class="stats-cell"><strong>{{ tagCount }}</strong><span>专题标签</span></div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import { useMastheadScrollBlur } from '@/shared/composables/useMastheadScrollBlur'

const { blurPx: backdropBlurPx } = useMastheadScrollBlur()

/** 仅全屏底图：随滚动加重 blur（与首页/树洞一致）；拼贴小封面不跟 blur，移动端更清晰 */
const mastheadBackdropScrollFilterStyle = computed(() => ({
  filter: `saturate(0.85) contrast(1.02) blur(${backdropBlurPx.value}px)`
}))

defineProps<{
  coverImage: string
  articleTitle: string
  articleSummary: string
  issueLabel: string
  heroKicker: string
  authorName: string
  authorIntroduction: string
  authorAvatar: string
  publishDate: string
  readingCount: number
  likeCount: number
  articleIdentityLabel: string
  readingMinutes: number
  articleWordCount: number
  sectionCount: number
  tagCount: number
  breadcrumbSourceLabel: string
}>()

const emit = defineEmits<{
  'go-home': []
  'go-source': []
}>()
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.feature-hero {
  --feature-hero-top-space: calc(clamp(22px, 4vw, 44px) + var(--header-height, 64px));
  --feature-hero-bottom-space: clamp(40px, 7vw, 80px);
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
  /* 全断点至少占满一屏（含刘海/地址栏友好单位） */
  min-height: 100vh;
  min-height: 100svh;
  min-height: 100dvh;
  padding: var(--feature-hero-top-space) 0 var(--feature-hero-bottom-space);
}
.feature-backdrop { position: absolute; inset: 0; }
.feature-backdrop-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.04);
  will-change: filter;
}
/* 轻遮罩：保留封面可见度，底部不再铺实色页底，避免整块「蒙死」背景图 */
.feature-backdrop-wash {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--bg-page) 4%, transparent) 0%,
    color-mix(in srgb, var(--bg-page) 14%, transparent) 35%,
    color-mix(in srgb, var(--bg-page) 32%, transparent) 72%,
    color-mix(in srgb, var(--bg-page) 46%, transparent) 100%
  );
}
.feature-grid {
  position: relative;
  z-index: 1;
  width: min(1240px, calc(100% - 48px));
  min-height: calc(100svh - var(--feature-hero-top-space) - var(--feature-hero-bottom-space));
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: clamp(24px, 4vw, 48px);
  align-items: end;
  align-content: space-between;
}
.issue-rail {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--text-secondary);
  text-shadow: 0 1px 8px color-mix(in srgb, var(--background-color-deep) 24%, transparent);
}
.breadcrumb-link { appearance: none; border: none; padding: 0; background: transparent; color: inherit; cursor: pointer; font: inherit; }
.rail-divider { width: 28px; height: 1px; background: color-mix(in srgb, var(--text-color-light) 62%, transparent); }
.issue-mark { padding: 6px var(--spacing-md); }
.feature-title {
  margin: 0;
  font-size: clamp(40px, 7vw, 88px);
  line-height: 0.95;
  letter-spacing: -0.04em;
  text-shadow:
    0 2px 12px color-mix(in srgb, var(--background-color-deep) 30%, transparent),
    0 14px 42px color-mix(in srgb, var(--background-color-deep) 18%, transparent);
}
.feature-story {
  position: relative;
  transform: translateY(clamp(-12px, -1.4vw, -20px));
}
.feature-standfirst {
  margin: 24px 0 0;
  font-size: clamp(18px, 2vw, 24px);
  line-height: 1.8;
  color: var(--text-secondary);
  text-shadow: 0 1px 10px color-mix(in srgb, var(--background-color-deep) 26%, transparent);
}
.story-kicker,.signature-panel,.signature-main,.signature-meta { display: flex; }
.story-kicker {
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.kicker-text {
  text-shadow: 0 1px 8px color-mix(in srgb, var(--background-color-deep) 22%, transparent);
}
.kicker-tag { padding: 0.35rem 0.72rem; }
.signature-panel { margin-top: 28px; align-items: center; justify-content: space-between; gap: var(--spacing-xxl); padding: var(--spacing-xl) var(--spacing-xxl); border-radius: calc(var(--border-radius-md) + 4px); transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base), background var(--transition-base); will-change: transform, box-shadow; }
.signature-main { align-items: center; gap: 16px; }
.signature-avatar { width: 56px; height: 56px; border-radius: 50%; object-fit: cover; }
.signature-copy { display: flex; flex-direction: column; gap: 4px; }
.signature-meta { flex-direction: column; gap: 10px; }
.signature-meta span { display: inline-flex; align-items: center; gap: 8px; color: var(--text-secondary); }
.feature-collage {
  position: relative;
  min-height: 420px;
  transform: translateY(clamp(-18px, -2vw, -30px));
  /* 速写卡压在导读角上时，正文不伸入遮挡带；卡片外框仍重叠 */
  --excerpt-clearance: clamp(120px, 24%, 172px);
}
.cover-stage { position: absolute; top: 0; right: 0; width: min(440px, 100%); aspect-ratio: 4/5; overflow: hidden; border-radius: var(--border-radius-md); transform: translateY(clamp(-10px, -1.2vw, -18px)); }
.cover-stage-image { width: 100%; height: 100%; object-fit: cover; }
.cover-stage-badge { position: absolute; top: 18px; left: 18px; padding: var(--spacing-sm) var(--spacing-md); }
.collage-card { position: absolute; padding: var(--spacing-xl) var(--spacing-xxl); border-radius: calc(var(--glass-radius) + 2px); transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base), background var(--transition-base); will-change: transform, box-shadow; }
.excerpt-card {
  left: 0;
  bottom: 24px;
  z-index: 1;
  width: min(320px, calc(100% - 80px));
  padding-right: calc(var(--spacing-xxl) + var(--excerpt-clearance));
}
/* 与主栏导语同源，以行数裁切形成拼贴「提要」，避免第三遍全文堆叠 */
.excerpt-card-preview {
  margin: 12px 0 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 4;
  overflow: hidden;
  font-size: clamp(15px, 1.55vw, 18px);
  line-height: 1.7;
  color: var(--text-primary);
  text-overflow: ellipsis;
}
.stats-card {
  right: 26px;
  bottom: -34px;
  z-index: 2;
  width: min(320px, calc(100% - 52px));
}
.stats-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; margin-top: 14px; }
.stats-cell { display: flex; flex-direction: column; gap: var(--spacing-xs); padding: var(--spacing-md); border-radius: calc(var(--border-radius-sm) + 2px); background: var(--surface-panel-inset); transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base); will-change: transform, box-shadow; }
.signature-panel:hover,
.collage-card:hover { transform: translateY(-4px) scale(1.01); box-shadow: var(--shadow-panel-hover); border-color: var(--border-interactive-hover); }
.stats-cell:hover { transform: translateY(-3px) scale(1.015); box-shadow: var(--shadow-panel-soft); border-color: var(--border-interactive-hover); }
@include sourcelin-down(lg) {
  .feature-grid {
    grid-template-columns: 1fr;
    min-height: calc(100svh - var(--feature-hero-top-space) - var(--feature-hero-bottom-space));
    align-content: start;
  }
}
@include sourcelin-down(md) {
  .feature-grid { width: min(100% - 32px, 100%); }
  .feature-story,
  .feature-collage,
  .cover-stage { transform: none; }
  .signature-panel {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
    padding: 1rem;
    margin-top: 1.1rem;
  }
  .signature-main {
    align-items: flex-start;
    gap: 0.85rem;
  }
  .signature-avatar {
    width: 50px;
    height: 50px;
  }
  .signature-copy {
    min-width: 0;
    gap: 0.2rem;
  }
  .signature-name,
  .signature-remark {
    line-height: 1.45;
  }
  .signature-meta {
    display: grid;
    grid-template-columns: 1fr;
    gap: 0.55rem;
    padding-top: 0.85rem;
    border-top: 1px solid var(--border-panel-subtle);
  }
  .signature-meta span {
    min-width: 0;
    font-size: 0.88rem;
  }
  .feature-collage {
    min-height: auto;
    display: flex;
    flex-direction: column;
    gap: 16px;
    --excerpt-clearance: 0;
  }
  .cover-stage,.excerpt-card,.stats-card { position: relative; inset: auto; width: 100%; }
  .excerpt-card { padding: var(--spacing-xl) var(--spacing-xxl); }
}
</style>
