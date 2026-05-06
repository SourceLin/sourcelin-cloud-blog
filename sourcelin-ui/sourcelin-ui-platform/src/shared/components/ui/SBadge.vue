<template>
  <NBadge
    :class="['s-badge', { 's-badge--compact': compact }]"
    :value="value"
    :max="max"
    :dot="dot"
    :type="type"
    :show="show"
    :offset="offset"
    :theme-overrides="mergedThemeOverrides"
    v-bind="forwardedAttrs"
  >
    <slot />
  </NBadge>
</template>

<script setup lang="ts">
import { computed, useAttrs } from 'vue'
import { NBadge } from 'naive-ui'

const props = withDefaults(
  defineProps<{
    /** 徽标数值；与 `dot` 二选一展示 */
    value?: string | number
    /** 与 Naive `max` 一致，超出显示 max+ */
    max?: number
    /** 仅显示红点（不显示数字） */
    dot?: boolean
    /** 收紧徽标容器尺寸，便于贴在头像等固定像素块边缘（NBadge 不能放在 button 内） */
    compact?: boolean
    type?: 'default' | 'success' | 'error' | 'warning' | 'info'
    show?: boolean
    offset?: readonly [number | string, number | string]
  }>(),
  {
    max: 99,
    type: 'error',
    show: true,
    dot: false,
    compact: false
  }
)

const attrs = useAttrs()

const forwardedAttrs = computed(() => {
  const raw = attrs as Record<string, unknown>
  const { themeOverrides: _omit, ...rest } = raw
  return rest
})

const mergedThemeOverrides = computed(() => {
  const extra = attrs.themeOverrides as Record<string, string> | undefined
  return {
    colorError: 'var(--error-color)',
    colorSuccess: 'var(--success-color)',
    colorWarning: 'var(--warning-color)',
    colorInfo: 'var(--info-color)',
    color: 'var(--primary-color)',
    fontSize: 'var(--font-size-xs, 10px)',
    ...extra
  }
})
</script>

<style scoped lang="scss">
.s-badge {
  display: inline-flex;
  vertical-align: middle;
}

.s-badge--compact {
  display: inline-flex;
  line-height: 0;
}

.s-badge :deep(.n-badge-sup) {
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
</style>
