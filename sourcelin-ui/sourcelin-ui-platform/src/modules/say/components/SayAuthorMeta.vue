<script setup lang="ts">
import type { Component } from 'vue'
import defaultAvatar from '@/assets/images/logo/logo.png'
import type { SayItem } from '@/modules/say/model/say.types'
import SAvatar from '@/shared/components/ui/SAvatar.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

defineProps<{
  item: SayItem
  nickname: string
  cardVariantLabel: string
  cardVariantIcon: Component
  isCommentRich: boolean
  publishRelativeLabel: string
}>()
</script>

<template>
  <header class="say-header">
    <div class="say-header__main">
      <SAvatar
        :src="item.user?.avatar || defaultAvatar"
        round
        :size="50"
        class="say-avatar"
      />
      <div class="say-user-info">
        <div class="say-user-topline">
          <span class="say-nickname">{{ nickname }}</span>
          <span class="say-badge" :aria-label="cardVariantLabel">
            <SIcon
              class="say-badge__icon"
              :icon="cardVariantIcon"
              :size="14"
              aria-hidden="true"
            />
          </span>
          <span v-if="isCommentRich" class="say-badge say-badge--conversation">热议</span>
        </div>
        <div class="say-meta">
          <span class="say-time">{{ item.createTime }}</span>
          <span class="say-meta-dot" aria-hidden="true" />
        </div>
      </div>
    </div>

    <div class="say-header__elapsed" :title="item.createTime || undefined">
      <span class="say-header__elapsed-text">{{ publishRelativeLabel }}</span>
    </div>
  </header>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-lg);
}

.say-header__main {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.say-avatar {
  flex-shrink: 0;
}

.say-user-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.say-user-topline {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.say-nickname {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: var(--title-color);
}

.say-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid color-mix(in srgb, var(--say-strip) 28%, var(--border-panel-subtle));
  background: color-mix(in srgb, var(--say-strip) 10%, var(--surface-panel-chip));
  color: color-mix(in srgb, var(--say-strip) 74%, var(--text-secondary));
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.say-badge__icon {
  flex-shrink: 0;
  color: inherit;
}

.say-badge--conversation {
  border-color: color-mix(in srgb, var(--primary-color) 24%, var(--border-panel-subtle));
  background: color-mix(in srgb, var(--primary-color) 10%, var(--surface-panel-chip));
  color: color-mix(in srgb, var(--primary-color) 82%, var(--title-color));
}

.say-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  color: var(--text-secondary);
  font-size: 0.76rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.say-time {
  color: inherit;
  text-transform: none;
  letter-spacing: 0.02em;
}

.say-meta-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  flex-shrink: 0;
  background: color-mix(in srgb, var(--say-strip) 70%, transparent);
}

.say-header__elapsed {
  flex-shrink: 0;
  padding-top: 4px;
  display: grid;
  gap: 2px;
  text-align: right;
  max-width: 8.5rem;
}

.say-header__elapsed-text {
  display: inline-block;
  color: color-mix(in srgb, var(--say-strip) 48%, var(--text-secondary));
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  line-height: 1.35;
  word-break: keep-all;
}

@include sourcelin-down(md) {
  .say-header {
    gap: 12px;
  }

  .say-header__elapsed {
    max-width: 6.5rem;
  }
}

@include sourcelin-down(sm) {
  .say-user-topline {
    gap: 8px;
  }
}
</style>