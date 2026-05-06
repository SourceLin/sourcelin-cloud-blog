<template>
  <NCard
    class="s-card"
    :class="[
      `s-card--${variant}`,
      `s-card--surface-${surface}`,
      `s-card--header-padding-${headerPadding}`,
      `s-card--content-padding-${contentPadding}`,
      `s-card--footer-padding-${footerPadding}`,
      {
        's-card--hoverable': hoverable,
        's-card--borderless': !bordered
      }
    ]"
    :bordered="bordered"
    :hoverable="hoverable"
    :header-style="resolvedHeaderStyle"
    :content-style="resolvedContentStyle"
    :footer-style="resolvedFooterStyle"
    v-bind="$attrs"
  >
    <template v-if="$slots.header" #header>
      <slot name="header" />
    </template>
    <template v-if="$slots['header-extra']" #header-extra>
      <slot name="header-extra" />
    </template>
    <slot />
    <template v-if="$slots.footer" #footer>
      <slot name="footer" />
    </template>
    <template v-if="$slots.action" #action>
      <slot name="action" />
    </template>
  </NCard>
</template>

<script setup lang="ts">
import type { CSSProperties } from 'vue'
import { computed } from 'vue'
import { NCard } from 'naive-ui'

interface Props {
  variant?: 'default' | 'lite' | 'strong' | 'editorial'
  surface?: 'default' | 'ice' | 'drop' | 'frost' | 'gem'
  hoverable?: boolean
  bordered?: boolean
  headerPadding?: 'default' | 'none'
  contentPadding?: 'default' | 'none'
  footerPadding?: 'default' | 'none'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  surface: 'default',
  hoverable: false,
  bordered: true,
  headerPadding: 'default',
  contentPadding: 'default',
  footerPadding: 'default'
})

const nonePaddingStyle: CSSProperties = {
  padding: '0'
}

const resolvedHeaderStyle = computed<CSSProperties | undefined>(() => {
  return props.headerPadding === 'none' ? nonePaddingStyle : undefined
})

const resolvedContentStyle = computed<CSSProperties | undefined>(() => {
  return props.contentPadding === 'none' ? nonePaddingStyle : undefined
})

const resolvedFooterStyle = computed<CSSProperties | undefined>(() => {
  return props.footerPadding === 'none' ? nonePaddingStyle : undefined
})
</script>

<style scoped lang="scss">
.s-card {
  border-radius: var(--glass-radius);
  border: 1px solid var(--border-panel-default);
  background: var(--surface-panel-default);
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-default),
    var(--shadow-panel-glow);
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);

  :deep(.n-card__content),
  :deep(.n-card-header),
  :deep(.n-card__footer) {
    position: relative;
    z-index: 1;
  }

  :deep(.n-card-header) {
    padding: var(--spacing-lg) var(--spacing-xl);
    border-bottom: 1px solid var(--border-panel-subtle);
    background:
      linear-gradient(
        180deg,
        color-mix(in srgb, var(--surface-white-20) 50%, transparent),
        transparent
      ),
      var(--surface-panel-soft);
  }

  :deep(.n-card__content) {
    padding: var(--spacing-xl);
    color: var(--text-primary);
    background: transparent;
  }

  :deep(.n-card__footer) {
    padding: var(--spacing-lg) var(--spacing-xl);
    border-top: 1px solid var(--border-panel-subtle);
    background: color-mix(in srgb, var(--surface-panel-inset) 84%, transparent);
  }
}

.s-card--borderless {
  border: none;
}

.s-card--hoverable:hover {
  transform: translateY(-3px);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-hover),
    var(--shadow-panel-glow);
  background: var(--surface-panel-strong);
}

.s-card--lite {
  background: var(--surface-panel-soft);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-soft),
    color-mix(in srgb, var(--shadow-panel-glow) 72%, transparent);
}

.s-card--strong {
  border-color: var(--border-panel-strong);
  background: var(--surface-panel-strong);
  box-shadow:
    var(--highlight-panel-strong),
    var(--shadow-panel-default),
    var(--shadow-panel-glow);
}

.s-card--editorial {
  border-color: var(--border-panel-subtle);
  border-radius: calc(var(--glass-radius) + 8px);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--glass-surface-strong) 96%, transparent),
      color-mix(in srgb, var(--glass-surface-lite) 82%, transparent)
    );
  box-shadow: var(--shadow-soft);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-soft),
    color-mix(in srgb, var(--shadow-panel-glow) 48%, transparent);

  :deep(.n-card__content) {
    background: transparent;
  }
}

.s-card--editorial.s-card--hoverable:hover {
  transform: translateY(-3px);
  border-color: var(--border-panel-default);
  box-shadow:
    var(--shadow-panel-hover),
    color-mix(in srgb, var(--shadow-panel-glow) 72%, transparent);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--glass-surface-strong) 98%, transparent),
      color-mix(in srgb, var(--glass-surface-lite) 88%, transparent)
    );
}

.s-card--header-padding-none {
  :deep(.n-card-header) {
    padding: 0;
  }
}

.s-card--content-padding-none {
  :deep(.n-card__content) {
    padding: 0;
  }
}

.s-card--footer-padding-none {
  :deep(.n-card__footer) {
    padding: 0;
  }
}

/* ========== 光学变体表面 ========== */

/* —— 冰晶玻璃 (Ice) —— */
.s-card--surface-ice {
  background: var(--glass-ice-surface);
  border-color: var(--glass-ice-border);
  box-shadow: var(--glass-ice-highlight), var(--glass-ice-shadow);

  :deep(.n-card-header) {
    background: var(--glass-ice-refraction);
  }
}

.s-card--surface-ice.s-card--hoverable:hover {
  transform: translateY(-3px);
  box-shadow: var(--glass-ice-highlight),
    var(--highlight-panel-strong),
    var(--shadow-panel-hover),
    0 0 56px color-mix(in srgb, var(--primary-color) 8%, transparent);
}

/* —— 水滴玻璃 (Drop) —— */
.s-card--surface-drop {
  background: var(--glass-drop-surface);
  border-color: var(--glass-drop-border);
  box-shadow: var(--glass-drop-shadow);

  :deep(.n-card-header) {
    background: var(--glass-drop-refraction);
  }
}

.s-card--surface-drop.s-card--hoverable:hover {
  transform: translateY(-4px);
  box-shadow: var(--glass-drop-glow-hover);
}

/* —— 雾面玻璃 (Frost) —— */
.s-card--surface-frost {
  background: var(--glass-frost-surface);
  border-color: var(--glass-frost-border);
  box-shadow: var(--glass-frost-highlight), var(--glass-frost-shadow);

  :deep(.n-card-header) {
    background: var(--glass-frost-refraction);
  }
}

.s-card--surface-frost.s-card--hoverable:hover {
  transform: translateY(-2px);
  box-shadow: var(--glass-frost-highlight),
    var(--highlight-panel-soft),
    var(--shadow-panel-default);
}

/* —— 宝石玻璃 (Gem) —— */
.s-card--surface-gem {
  position: relative;
  background: var(--glass-gem-surface);
  border-color: var(--glass-gem-border);
  box-shadow: var(--glass-gem-highlight), var(--glass-gem-shadow);

  :deep(.n-card-header) {
    background: var(--glass-gem-refraction);
  }
}

/* Gem 变体：conic-gradient 色散边框 */
.s-card--surface-gem::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 0.5px;
  background: var(--glass-gem-chromatic-border);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
  z-index: 1;
}

.s-card--surface-gem.s-card--hoverable:hover {
  transform: translateY(-5px);
  box-shadow: var(--glass-gem-highlight),
    var(--highlight-panel-strong),
    var(--shadow-panel-hover),
    var(--glass-gem-chromatic-fallback);
}

@supports not (background: conic-gradient(#fff, #000)) {
  .s-card--surface-gem {
    box-shadow: var(--glass-gem-highlight), var(--glass-gem-shadow), var(--glass-gem-chromatic-fallback);
  }

  .s-card--surface-gem::before {
    display: none;
  }
}
</style>

