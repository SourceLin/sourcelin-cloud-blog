<script setup lang="ts">
import { computed } from 'vue'
import type { Component } from 'vue'
import StatsPanelCard from '@/shared/components/business/StatsPanelCard.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = defineProps<{
  isLoggedIn: boolean
  /** 服务端总条数；为 0 时由父级回退为已载入数 */
  sayTotal: number
  loadedCount: number
  imageMoments: number
  activeConversations: number
}>()

const emit = defineEmits<{
  publish: []
  login: []
}>()

interface HeroStatTile {
  label: string
  value: number
  icon: Component
  hue: number
  ariaLabel: string
}

const heroStatTiles = computed((): HeroStatTile[] => {
  const total = props.sayTotal || props.loadedCount
  return [
    {
      label: '总动态',
      value: total,
      icon: appIcons.document,
      hue: 220,
      ariaLabel: `共 ${total} 条动态`
    },
    {
      label: '已载入',
      value: props.loadedCount,
      icon: appIcons.archive,
      hue: 180,
      ariaLabel: `已载入 ${props.loadedCount} 条`
    },
    {
      label: '含图动态',
      value: props.imageMoments,
      icon: appIcons.sparkles,
      hue: 280,
      ariaLabel: `含图动态 ${props.imageMoments} 条`
    },
    {
      label: '热评动态',
      value: props.activeConversations,
      icon: appIcons.flame,
      hue: 150,
      ariaLabel: `热评动态 ${props.activeConversations} 条`
    }
  ]
})
</script>

<template>
  <SCard
    class="say-hero"
    variant="lite"
  >
    <!-- decorative orbs removed as part of non-header design refresh -->

    <div class="say-hero__main">
      <div class="say-hero__copy">
        <span class="article-card-prelude-label">说说</span>
        <h1 class="say-hero__title">动态</h1>
        <p class="say-hero__subtitle">发布与浏览个人动态，支持文字与图片。</p>

        <div class="say-hero__stats">
          <StatsPanelCard
            v-for="tile in heroStatTiles"
            :key="tile.label"
            class="say-hero__stat-card"
            density="compact"
            :icon="tile.icon"
            :value="tile.value"
            :label="tile.label"
            :hue="tile.hue"
            :aria-label="tile.ariaLabel"
          />
        </div>
      </div>

      <div class="say-hero__aside">
        <div class="say-hero__aside-panel">
          <div class="say-hero__aside-glow" aria-hidden="true" />

          <template v-if="isLoggedIn">
            <div class="say-hero__aside-copy">
              <p class="say-hero__aside-eyebrow">即刻记录</p>
              <p class="say-hero__aside-lede">用文字或配图，把这一瞬留在时间线里。</p>
            </div>

            <div class="say-hero__action-slot">
              <div class="say-hero__action-slot__inner">
                <SButton
                  type="primary"
                  variant="cta"
                  class="say-hero__action"
                  @click="emit('publish')"
                >
                  <template #icon>
                    <span class="say-hero__action-icon" aria-hidden="true">
                      <SIcon :icon="appIcons.add" :size="18" />
                    </span>
                  </template>
                  <span class="say-hero__action-text-row">
                    <span class="say-hero__action-label">发一条</span>
                    <span class="say-hero__action-hint" aria-hidden="true">
                      <SIcon :icon="appIcons.arrowForward" :size="14" />
                    </span>
                  </span>
                </SButton>
              </div>
            </div>
          </template>

          <div v-else class="say-hero__aside-guest">
            <div class="say-hero__aside-guest-icon" aria-hidden="true">
              <SIcon :icon="appIcons.login" :size="22" />
            </div>
            <p class="say-hero__aside-guest-title">登录后发布</p>
            <p class="say-hero__aside-guest-text">登录账号即可发布说说，与站内读者分享你的动态。</p>
            <SButton
              type="primary"
              variant="glass"
              class="say-hero__aside-guest-action"
              @click="emit('login')"
            >
              <template #icon>
                <SIcon :icon="appIcons.login" :size="16" />
              </template>
              去登录
            </SButton>
          </div>
        </div>
      </div>
    </div>
  </SCard>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
}

/* decorative orb styles removed to reduce visual noise on hero */ 

.say-hero__main {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(260px, 0.8fr);
  gap: 24px;
  align-items: end;
}

.say-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
  min-width: 0;
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.say-hero__title {
  margin: 0;
  font-size: clamp(2.5rem, 5vw, 4rem);
  line-height: 0.98;
  letter-spacing: 0.04em;
  color: var(--title-color);
}

.say-hero__subtitle {
  max-width: 38rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.say-hero__stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.7rem;
}

.say-hero__stat-card {
  min-width: 0;
}

@include sourcelin-down(lg) {
  .say-hero__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.65rem;
  }
}

/* 与首页 sidebar-stack 内 HomeSiteStats `.stat-item` 同源的玻璃渐变与悬浮态 */








.say-hero__aside {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-self: stretch;
  min-width: 0;
}

.say-hero__aside-panel {
  position: relative;
  display: grid;
  gap: var(--spacing-lg);
  max-width: 20rem;
  margin-inline-start: auto;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-radius: calc(var(--border-radius-xl) + 4px);
  border: 1px solid color-mix(in srgb, var(--glass-border) 80%, transparent);
  background:
    linear-gradient(
      152deg,
      color-mix(in srgb, var(--primary-color) 9%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 94%, transparent) 55%,
      color-mix(in srgb, var(--info-color) 5%, var(--glass-surface-strong))
    );
  box-shadow: var(--shadow-panel-soft), var(--highlight-panel-soft);
  overflow: hidden;
  transition:
    border-color 0.45s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.45s cubic-bezier(0.4, 0, 0.2, 1),
    transform 0.45s cubic-bezier(0.4, 0, 0.2, 1);
}

.say-hero__aside-panel:hover {
  border-color: color-mix(in srgb, var(--primary-color) 22%, var(--glass-border));
  box-shadow: var(--shadow-panel-hover), var(--highlight-panel-soft);
}

.say-hero__aside-glow {
  position: absolute;
  inset: auto -28% -42% auto;
  width: min(11rem, 70%);
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    color-mix(in srgb, var(--primary-color) 18%, transparent) 0%,
    transparent 68%
  );
  pointer-events: none;
  animation: say-hero-aside-glow 14s ease-in-out infinite;
}

@keyframes say-hero-aside-glow {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.85;
  }
  50% {
    transform: translate(-6%, -4%) scale(1.06);
    opacity: 1;
  }
}

.say-hero__aside-copy {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-xs);
}

.say-hero__aside-eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.say-hero__aside-lede {
  margin: 0;
  font-size: var(--font-size-md, 14px);
  line-height: 1.65;
  color: var(--text-secondary);
  /* 勿用 text-wrap: balance，会在窄侧栏里把一句拆成两行 */
  white-space: nowrap;
}

.say-hero__action-slot {
  position: relative;
  z-index: 1;
  padding: 1px;
  border-radius: calc(var(--border-radius-lg) + 3px);
  background: linear-gradient(
    118deg,
    color-mix(in srgb, var(--primary-color) 52%, transparent),
    color-mix(in srgb, var(--info-color) 38%, transparent),
    color-mix(in srgb, var(--secondary-color) 32%, transparent),
    color-mix(in srgb, var(--primary-color) 48%, transparent)
  );
  background-size: 220% 220%;
  animation: say-hero-cta-orbit 8s ease infinite;
}

@keyframes say-hero-cta-orbit {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.say-hero__action-slot:hover {
  animation-duration: 4.5s;
}

.say-hero__action-slot__inner {
  border-radius: var(--border-radius-lg);
  padding: 3px;
  background: color-mix(in srgb, var(--glass-surface-strong) 92%, transparent);
}

.say-hero__action {
  width: 100%;
}

.say-hero__action-text-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: var(--spacing-sm);
}

.say-hero__action-icon {
  display: inline-flex;
  transition: transform 0.42s cubic-bezier(0.34, 1.45, 0.64, 1);
}

.say-hero__action-slot:hover .say-hero__action-icon {
  transform: rotate(90deg) scale(1.06);
}

.say-hero__action-label {
  flex: 1;
  min-width: 0;
  text-align: start;
  letter-spacing: 0.04em;
}

.say-hero__action-hint {
  display: inline-flex;
  align-items: center;
  opacity: 0.5;
  transition:
    transform 0.35s cubic-bezier(0.34, 1.45, 0.64, 1),
    opacity 0.35s ease;
}

.say-hero__action-slot:hover .say-hero__action-hint {
  transform: translateX(4px);
  opacity: 0.92;
}

.say-hero__aside-guest {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-md);
  justify-items: center;
  text-align: center;
  padding-block: var(--spacing-xs);
}

.say-hero__aside-guest-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: 1rem;
  color: var(--primary-color);
}

.say-hero__aside-guest-title {
  margin: 0;
  font-size: var(--font-size-lg, 16px);
  font-weight: 700;
  color: var(--title-color);
  letter-spacing: 0.06em;
}

.say-hero__aside-guest-text {
  margin: 0;
  font-size: var(--font-size-md, 14px);
  line-height: 1.7;
  color: var(--text-secondary);
  max-width: 16rem;
}

.say-hero__aside-guest-action {
  width: fit-content;
  min-width: 124px;
}

@include sourcelin-down(md) {
  .say-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .say-hero__main {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
  }

  .say-hero__aside-panel {
    max-width: none;
    margin-inline-start: 0;
  }


}

@include sourcelin-down(sm) {
  .say-hero__aside-lede {
    white-space: normal;
  }

  .say-hero__stats {
    gap: 0.55rem;
  }



}
</style>
