<template>
  <NTag
    :type="naiveType"
    :size="size"
    :round="round"
    :bordered="bordered"
    class="s-status-tag"
    :theme-overrides="themeOverrides"
  >
    <slot />
  </NTag>
</template>

<script setup lang="ts">
import { NTag } from 'naive-ui'
import { computed } from 'vue'

interface Props {
  /** 语义类型；primary 会映射到 Naive 的 default 并覆写颜色 */
  type?: 'default' | 'success' | 'warning' | 'error' | 'info' | 'primary'
  size?: 'small' | 'medium' | 'large'
  round?: boolean
  bordered?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'small',
  round: true,
  bordered: false
})

const naiveType = computed(() => {
  if (props.type === 'primary') return 'default'
  return props.type
})

const themeOverrides = computed(() => {
  const palette: Record<
    string,
    { color: string; textColor: string; borderColor: string }
  > = {
    default: {
      color: 'color-mix(in srgb, var(--text-secondary) 12%, transparent)',
      textColor: 'var(--text-secondary)',
      borderColor: 'transparent'
    },
    success: {
      color: 'var(--success-color)',
      textColor: 'var(--text-color-light)',
      borderColor: 'transparent'
    },
    warning: {
      color: 'var(--warning-color)',
      textColor: 'var(--text-color-light)',
      borderColor: 'transparent'
    },
    error: {
      color: 'var(--error-color)',
      textColor: 'var(--text-color-light)',
      borderColor: 'transparent'
    },
    info: {
      color: 'var(--info-color)',
      textColor: 'var(--text-color-light)',
      borderColor: 'transparent'
    },
    primary: {
      color: 'var(--primary-color)',
      textColor: 'var(--text-color-light)',
      borderColor: 'transparent'
    }
  }

  return palette[props.type] || palette.default
})
</script>

<style scoped lang="scss">
.s-status-tag {
  vertical-align: middle;
}
</style>
