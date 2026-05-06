<script setup lang="ts">
import { computed } from 'vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = withDefaults(
  defineProps<{
    count?: number
    /** 标签页：顶栏为双 chip + 日期；分类页：单 chip + 日期 */
    variant?: 'tag' | 'category'
    /** 标签页封面角标渐变，如 `var(--primary-color)` 或 `#hex` */
    tagAccent?: string
  }>(),
  {
    count: 3,
    variant: 'tag'
  }
)

const cardStyle = computed(() =>
  props.variant === 'tag' && props.tagAccent
    ? ({ '--tag-accent': props.tagAccent } as Record<string, string>)
    : undefined
)
</script>

<template>
  <div class="context-loading-stack" aria-hidden="true">
    <article
      v-for="index in count"
      :key="`cac-skel-${index}`"
      class="cac-skel-card"
      :style="cardStyle"
    >
      <SSkeleton :sharp="false" class="cac-skel__cover" :height="132" />

      <div class="cac-skel__body">
        <div class="cac-skel__topline">
          <template v-if="variant === 'tag'">
            <div class="cac-skel__chips">
              <SSkeleton :sharp="false" class="cac-skel__chip" :width="84" :height="28" />
              <SSkeleton :sharp="false" class="cac-skel__chip cac-skel__chip--sm" :width="64" :height="28" />
            </div>
            <SSkeleton :sharp="false" class="cac-skel__date" :width="92" :height="14" />
          </template>
          <template v-else>
            <SSkeleton :sharp="false" class="cac-skel__chip" :width="84" :height="28" />
            <SSkeleton :sharp="false" class="cac-skel__date" :width="92" :height="14" />
          </template>
        </div>

        <SSkeleton :sharp="false" class="cac-skel__title" />
        <SSkeleton :sharp="false" class="cac-skel__line" />
        <SSkeleton :sharp="false" class="cac-skel__line cac-skel__line--short" />

        <div class="cac-skel__meta">
          <SSkeleton v-for="m in 3" :key="`cac-meta-${index}-${m}`" :sharp="false" class="cac-skel__meta-bit" />
        </div>
      </div>

      <div class="cac-skel__arrow">
        <SIcon :icon="appIcons.arrowForward" :size="16" />
      </div>
    </article>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.context-loading-stack {
  display: grid;
  gap: var(--spacing-md);
}

.cac-skel-card {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr) auto;
  gap: var(--spacing-lg);
  align-items: start;
  padding: var(--spacing-md);
  pointer-events: none;
  border: 1px solid var(--border-panel-subtle);
}

.cac-skel__cover {
  width: 100% !important;
  min-height: 132px;
  border-radius: calc(var(--border-radius-lg) + 2px) !important;
}

.cac-skel__body {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.cac-skel__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.cac-skel__chips {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.cac-skel__chip {
  border-radius: 999px !important;
}

.cac-skel__title {
  width: min(72%, 24rem) !important;
  height: 20px !important;
}

.cac-skel__line {
  width: 100% !important;
  height: 13px !important;
}

.cac-skel__line--short {
  width: 68% !important;
}

.cac-skel__meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.cac-skel__meta-bit {
  width: 84px !important;
  height: 12px !important;
  border-radius: 999px !important;
}

.cac-skel__arrow {
  align-self: center;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 999px;
  opacity: 0.85;
}

@include sourcelin-down(md) {
  .cac-skel-card {
    grid-template-columns: 1fr;
  }

  .cac-skel__cover {
    min-height: 168px;
  }

  .cac-skel__arrow {
    display: none;
  }
}
</style>
