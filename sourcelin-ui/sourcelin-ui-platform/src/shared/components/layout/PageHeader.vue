<script setup lang="ts">
import type { Component } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'

withDefaults(defineProps<{
  title: string
  subtitle?: string
  icon?: Component
  centered?: boolean
  kicker?: string
  surface?: 'plain' | 'soft'
}>(), {
  subtitle: '',
  icon: undefined,
  centered: true,
  kicker: '',
  surface: 'plain'
})
</script>

<template>
  <div class="page-header" :class="[{ centered, 'surface-soft': surface === 'soft' }]">
    <div class="header-accent" />
    <div class="header-frame">
      <div class="header-main">
        <span v-if="kicker" class="header-kicker">{{ kicker }}</span>
        <h1 class="page-title">
          <SIcon v-if="icon" class="title-icon" :icon="icon" :size="28" />
          <span>{{ title }}</span>
        </h1>
        <p v-if="subtitle" class="page-subtitle">{{ subtitle }}</p>
      </div>
      <div v-if="$slots.meta" class="header-meta">
        <slot name="meta" />
      </div>
      <div v-if="$slots.extra" class="header-extra">
        <slot name="extra" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.page-header {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0 auto 32px;
  padding: var(--spacing-xl) 0 var(--spacing-lg);
}

.header-accent {
  width: min(7rem, 26vw);
  height: 1px;
  margin: 0 auto;
  background: linear-gradient(90deg, transparent, var(--border-panel-accent), transparent);
  opacity: 0.82;
}

.page-header:not(.centered) .header-accent {
  margin-inline: 0 auto;
}

.header-frame {
  position: relative;
  display: grid;
  gap: 14px;
  width: 100%;
  padding: clamp(1.1rem, 2vw, 1.5rem) 0;
}

.page-header.centered {
  text-align: center;
}

.page-header.surface-soft {
  width: 100%;
  max-width: 100%;
}

.page-header.surface-soft .header-frame {
  padding: clamp(28px, 4vw, 42px) 24px;
  border-radius: calc(var(--glass-radius) + 6px);
  border: 1px solid var(--border-panel-default);
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-panel-strong) 88%, transparent), color-mix(in srgb, var(--surface-panel-soft) 92%, transparent)),
    var(--surface-page-ambient);
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-default);
  overflow: hidden;
}

.page-header.surface-soft .header-frame::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 48%;
  background: linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 88%, transparent), transparent 74%);
  pointer-events: none;
}

.header-main {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.header-kicker {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2px;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.page-header:not(.centered) .header-kicker {
  justify-content: flex-start;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0;
  font-size: clamp(2.1rem, 4vw, 3.05rem);
  font-weight: 700;
  color: var(--title-color);
  letter-spacing: -0.03em;
}

.page-header:not(.centered) .page-title {
  justify-content: flex-start;
}

.title-icon {
  color: var(--primary-color);
  filter: drop-shadow(0 10px 22px color-mix(in srgb, var(--primary-color) 20%, transparent));
}

.page-subtitle {
  margin: 0;
  max-width: min(46rem, 100%);
  font-size: 0.98rem;
  line-height: var(--reading-line-height);
  color: var(--text-reading-soft);
}

.header-meta,
.header-extra {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
}

.page-header:not(.centered) .header-meta,
.page-header:not(.centered) .header-extra {
  justify-content: flex-start;
}

@include sourcelin-down(md) {
  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .header-frame {
    gap: 12px;
    padding-top: var(--spacing-lg);
  }

  .page-header.surface-soft .header-frame {
    padding: 24px 18px;
  }
}
</style>



