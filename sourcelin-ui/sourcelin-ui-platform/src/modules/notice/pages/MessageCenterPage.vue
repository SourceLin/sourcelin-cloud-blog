<template>
  <div class="glass-page notice-page">
    <PageShell>
      <SCard
        class="notice-hero"
        variant="lite"
      >
        <div class="notice-hero__main">
          <div class="notice-hero__copy">
            <SSurfaceChip class="article-card-prelude-label" tone="accent">消息</SSurfaceChip>
            <h1 class="notice-hero__title">消息通知</h1>
            <p class="notice-hero__subtitle">{{ heroSubtitle }}</p>

            <section class="notice-hero__summary" aria-label="消息概览">
              <HeroStatCard
                v-for="tile in heroStatTiles"
                :key="tile.label"
                class="notice-hero__stat"
                mode="metric"
                tone="tinted"
                size="lg"
                :icon="tile.icon"
                :value="tile.value"
                :label="tile.label"
                :hue="tile.hue"
                :aria-label="tile.ariaLabel"
              />
            </section>
          </div>

          <aside class="notice-hero__aside" aria-label="消息说明">
            <SSurfacePanel class="notice-guide-panel" variant="soft">
              <div class="notice-guide-panel__glow" aria-hidden="true" />
              <div class="notice-guide-panel__body">
                <div class="notice-guide-panel__copy">
                  <p class="notice-guide-panel__eyebrow">使用说明</p>
                  <h2 class="notice-guide-panel__title">消息聚合</h2>
                  <p class="notice-guide-panel__lede">{{ guidePanelLede }}</p>
                </div>
                <div class="notice-guide-panel__meta">
                  <SSurfaceChip class="notice-guide-chip">系统公告已接入</SSurfaceChip>
                  <SSurfaceChip class="notice-guide-chip">频道持续扩展</SSurfaceChip>
                </div>
              </div>
            </SSurfacePanel>
          </aside>
        </div>
      </SCard>

      <div class="notice-layout">
        <SSurfacePanel tag="aside" class="notice-sidebar" variant="soft">
          <MessageChannelMenu
            :items="channels"
            :active-channel="activeChannel"
            @select="setChannel"
          />
        </SSurfacePanel>

        <SSurfacePanel tag="section" class="notice-content-card">
          <SSurfacePanel class="content-header" variant="inset">
            <div class="header-left">
              <SButton class="back-btn" variant="site" @click="goBack">
                <template #icon>
                  <SIcon :icon="appIcons.back" :size="16" />
                </template>
                返回
              </SButton>
              <h3 class="content-title">{{ contentTitle }}</h3>
            </div>

            <SButton
              v-if="activeChannel === 'system' && noticeList.length"
              class="clear-btn"
              variant="glass"
              @click="clearMessageItem(null, null)"
            >
              <template #icon>
                <SIcon :icon="appIcons.trash" :size="16" />
              </template>
              清空当前列表
            </SButton>
          </SSurfacePanel>

          <NoticeList
            v-if="activeChannel === 'system'"
            :items="noticeList"
            :loading="noticesLoading"
            :refresh-key="refreshKey"
            :center-tab-type="0"
            :split-ip-address="splitIpAddress"
            @clear-item="clearMessageItem"
          />
          <ChannelComingSoon
            v-else
            :title="currentChannel.comingTitle"
            :description="currentChannel.comingDescription"
            :icon="appIcons[currentChannel.icon]"
          />
        </SSurfacePanel>
      </div>
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { computed } from 'vue'
import ChannelComingSoon from '@/modules/notice/components/ChannelComingSoon.vue'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import MessageChannelMenu from '@/modules/notice/components/MessageChannelMenu.vue'
import NoticeList from '@/modules/notice/components/NoticeList.vue'
import { useMessageCenter } from '@/modules/notice/composables/useMessageCenter'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

const {
  channels,
  activeChannel,
  currentChannel,
  contentTitle,
  noticeList,
  noticesLoading,
  refreshKey,
  goBack,
  splitIpAddress,
  clearMessageItem,
  setChannel
} = useMessageCenter()

const heroSubtitle = '按频道查看系统公告与后续开放的互动、赞藏、关注等提醒。'

const guidePanelLede =
  '系统公告已对接站点通知；评论、点赞、关注等频道将在后端接口就绪后逐步开放。'

interface HeroTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
}

const heroStatTiles = computed<HeroTile[]>(() => {
  const listCount =
    activeChannel.value === 'system' ? String(noticeList.value.length) : '—'
  const status =
    activeChannel.value === 'system'
      ? noticesLoading.value
        ? '同步中'
        : noticeList.value.length > 0
          ? '已加载'
          : '暂无数据'
      : '即将开放'

  return [
    {
      label: activeChannel.value === 'system' ? '列表条数' : '本频道',
      value: listCount,
      icon: appIcons.notice,
      hue: 245,
      ariaLabel:
        activeChannel.value === 'system'
          ? `当前列表共 ${noticeList.value.length} 条公告`
          : '该频道尚未接入数据'
    },
    {
      label: '频道数量',
      value: '4',
      icon: appIcons.grid,
      hue: 205,
      ariaLabel: '消息通知共 4 个频道'
    },
    {
      label: '当前频道',
      value: currentChannel.value.name,
      icon: noticesLoading.value ? appIcons.runtime : appIcons.verified,
      hue: 175,
      ariaLabel: `当前浏览：${currentChannel.value.name}，${status}`
    }
  ]
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.notice-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.notice-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.notice-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.notice-hero__main {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(240px, 0.88fr);
  gap: var(--spacing-xxl);
  align-items: start;
}

.notice-hero__copy {
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

.notice-hero__title {
  margin: 0;
  font-size: clamp(2.4rem, 4.6vw, 3.6rem);
  line-height: 0.98;
  letter-spacing: 0.02em;
  color: var(--title-color);
}

.notice-hero__subtitle {
  max-width: 42rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.85;
}

.notice-hero__summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}








.notice-guide-panel {
  position: relative;
  display: grid;
  width: 100%;
  min-height: 100%;
  overflow: hidden;
}

.notice-guide-panel__glow {
  position: absolute;
  inset: auto -20% -32% auto;
  width: min(11rem, 68%);
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    color-mix(in srgb, var(--primary-color) 14%, transparent) 0%,
    transparent 68%
  );
  pointer-events: none;
}

.notice-guide-panel__body {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.notice-guide-panel__copy {
  display: grid;
  gap: var(--spacing-sm);
}

.notice-guide-panel__eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.notice-guide-panel__title {
  margin: 0;
  font-size: clamp(1.25rem, 2.2vw, 1.65rem);
  line-height: 1.15;
  color: var(--title-color);
}

.notice-guide-panel__lede {
  margin: 0;
  font-size: var(--font-size-md, 14px);
  line-height: 1.75;
  color: var(--text-reading-soft);
}

.notice-guide-panel__meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.notice-guide-chip {
  padding: 0.28rem 0.65rem;
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  color: var(--text-secondary);
}

.notice-layout {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: var(--page-block-gap);
}

.notice-sidebar {
  height: fit-content;
  position: sticky;
  top: calc(var(--header-height, 64px) + var(--page-block-gap));
  padding: var(--spacing-lg);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--shadow-soft);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.notice-content-card {
  position: relative;
  min-width: 0;
  min-height: 400px;
  padding: var(--page-block-gap);
  overflow: hidden;
  border: 1px solid var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.notice-content-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 168px;
  background: var(--surface-page-highlight);
  opacity: 0.7;
  pointer-events: none;
}

.content-header {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  margin-bottom: var(--page-block-gap);
  padding: var(--spacing-md);
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-elevated);
  box-shadow: inset 0 1px 0 var(--glass-highlight);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.content-title {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--title-color);
}

.clear-btn {
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .notice-layout {
    grid-template-columns: 1fr;
  }

  .notice-sidebar {
    position: static;
  }
}

@include sourcelin-down(lg) {
  .notice-hero__summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .notice-hero__stat:last-child {
    grid-column: 1 / -1;
  }
}

@include sourcelin-down(md) {
  .notice-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .notice-hero__main {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
  }

  .notice-guide-panel__body {
    padding: var(--spacing-lg);
  }

  .content-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-left {
    justify-content: space-between;
  }
}

@include sourcelin-down(sm) {
  .notice-hero__summary {
    grid-template-columns: 1fr;
  }

  .notice-hero__stat:last-child {
    grid-column: auto;
  }



}
</style>
