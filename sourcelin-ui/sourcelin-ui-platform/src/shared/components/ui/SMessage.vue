<template>
  <NMessageProvider placement="bottom-right">
    <MessageBridge />
    <slot />
  </NMessageProvider>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'
import { NMessageProvider, useMessage } from 'naive-ui'
import { _captureMessageApi } from '@/shared/composables/useSMessage'

const MessageBridge = defineComponent({
  name: 'MessageBridge',
  setup() {
    _captureMessageApi(useMessage())
    return () => null
  }
})
</script>
