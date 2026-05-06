<template>
  <NAvatar class="s-avatar" v-bind="$attrs">
    <slot />
  </NAvatar>
</template>

<script setup lang="ts">
import { NAvatar } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--glass-border);
  box-shadow: var(--shadow-soft);
  background: var(--glass-surface-lite);
}
</style>

