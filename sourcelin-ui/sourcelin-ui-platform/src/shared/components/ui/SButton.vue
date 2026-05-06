<template>
  <NButton
    class="s-button"
    :class="`s-button--${variant}`"
    :type="resolvedType"
    v-bind="$attrs"
  >
    <template v-if="$slots.icon" #icon>
      <slot name="icon" />
    </template>
    <slot />
  </NButton>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NButton } from 'naive-ui'

interface Props {
  type?: 'default' | 'primary' | 'tertiary' | 'success' | 'warning' | 'error' | 'info'
  variant?: 'glass' | 'ghost' | 'solid' | 'cta' | 'site'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  variant: 'glass'
})

const resolvedType = computed(() => {
  if (props.variant === 'site' || props.variant === 'cta') {
    return 'default'
  }

  return props.type
})
</script>

<style scoped lang="scss">
.s-button {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-panel-inline);
  transition:
    transform var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base);
}

.s-button--glass {
  --n-color: var(--surface-panel-soft);
  --n-color-hover: var(--surface-panel-default);
  --n-color-pressed: var(--surface-panel-strong);
  --n-border: 1px solid var(--border-panel-default);
  --n-border-hover: 1px solid var(--border-interactive-hover);
  --n-border-pressed: 1px solid var(--border-interactive-active);

  :deep(.n-button__border) {
    border-color: var(--border-panel-default);
  }

  :deep(.n-button__state-border) {
    border-color: var(--border-interactive-default);
  }
}

.s-button--ghost {
  --n-color: transparent;
  --n-color-hover: color-mix(in srgb, var(--primary-color) 8%, transparent);
  --n-color-pressed: color-mix(in srgb, var(--primary-color) 12%, transparent);

  :deep(.n-button__border) {
    border-color: var(--border-color);
  }
}

.s-button--solid {
  :deep(.n-button__border) {
    border-color: transparent;
  }
}

.s-button--cta {
  --n-height: 46px;
  --n-border-radius: 18px;
  --n-border: 1px solid var(--border-interactive-default);
  --n-border-hover: 1px solid var(--border-interactive-hover);
  --n-border-pressed: 1px solid var(--border-interactive-active);
  --n-color: var(--surface-panel-chip-accent);
  --n-color-hover: var(--surface-panel-strong);
  --n-color-pressed: var(--surface-panel-default);
  --n-text-color: var(--primary-active);
  --n-text-color-hover: var(--primary-active);
  --n-text-color-pressed: var(--primary-active);
  box-shadow:
    var(--shadow-panel-soft),
    var(--highlight-panel-chip),
    var(--button-glow);

  :deep(.n-button__border) {
    border-color: var(--border-interactive-default);
  }

  :deep(.n-button__state-border) {
    border-color: var(--border-interactive-hover);
  }

  :deep(.n-button__content),
  :deep(.n-button__icon) {
    color: var(--primary-active);
    font-weight: 600;
  }

  &:hover {
    transform: translateY(-1px);
    box-shadow:
      var(--shadow-panel-hover),
      var(--highlight-panel-chip),
      var(--button-glow-strong),
      0 0 24px color-mix(in srgb, var(--companion-color) 12%, transparent); /* 琥珀金伴色 glow */
  }
}

:global(html[data-theme='dark'] .s-button--cta) {
  --n-color: var(--surface-panel-chip-accent);
  --n-color-hover: var(--surface-panel-strong);
  --n-color-pressed: var(--surface-panel-default);
}

:global(html[data-theme='dark'] .s-button--cta .n-button__content),
:global(html[data-theme='dark'] .s-button--cta .n-button__icon) {
  color: var(--text-color-light);
}

.s-button--site {
  --n-height: 46px;
  --n-border-radius: 18px;
  --n-border: 1px solid var(--border-interactive-default);
  --n-border-hover: 1px solid var(--border-interactive-hover);
  --n-border-pressed: 1px solid var(--border-interactive-active);
  --n-color: var(--surface-panel-strong);
  --n-color-hover: var(--surface-panel-default);
  --n-color-pressed: var(--surface-panel-soft);
  --n-text-color: var(--title-color);
  --n-text-color-hover: var(--title-color);
  --n-text-color-pressed: var(--title-color);
  box-shadow:
    var(--shadow-panel-soft),
    var(--highlight-panel-default),
    var(--button-glow);

  :deep(.n-button__border) {
    border-color: var(--border-interactive-default);
  }

  :deep(.n-button__state-border) {
    border-color: var(--border-interactive-hover);
  }

  :deep(.n-button__content),
  :deep(.n-button__icon) {
    color: color-mix(in srgb, var(--title-color) 92%, var(--primary-active));
    font-weight: 600;
  }

  &:hover {
    transform: translateY(-1px) scale(1.005);
    box-shadow:
      var(--shadow-panel-hover),
      var(--highlight-panel-default),
      var(--button-glow-strong),
      0 0 20px color-mix(in srgb, var(--companion-color) 10%, transparent); /* 琥珀金伴色 glow */
  }
}

:global(html[data-theme='dark'] .s-button--site) {
  --n-color: var(--surface-panel-strong);
  --n-color-hover: var(--surface-panel-default);
  --n-color-pressed: var(--surface-panel-soft);
  --n-text-color: var(--text-color-light);
  --n-text-color-hover: var(--text-color-light);
  --n-text-color-pressed: var(--text-color-light);
}

:global(html[data-theme='dark'] .s-button--site .n-button__content),
:global(html[data-theme='dark'] .s-button--site .n-button__icon) {
  color: var(--text-color-light);
}

</style>

