<template>
  <NUploadDragger class="s-upload-dragger" v-bind="$attrs">
    <slot />
  </NUploadDragger>
</template>

<script setup lang="ts">
import { NUploadDragger } from 'naive-ui'
</script>

<style scoped lang="scss">
.s-upload-dragger {
  display: block;
}
</style>

