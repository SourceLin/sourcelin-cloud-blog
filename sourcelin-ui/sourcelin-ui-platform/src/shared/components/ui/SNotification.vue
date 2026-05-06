<template>
  <NNotificationProvider placement="bottom-right">
    <NotificationBridge />
    <slot />
  </NNotificationProvider>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'
import { NNotificationProvider, useNotification } from 'naive-ui'
import { _captureNotificationApi } from '@/shared/composables/useSNotification'

const NotificationBridge = defineComponent({
  name: 'NotificationBridge',
  setup() {
    _captureNotificationApi(useNotification())
    return () => null
  }
})
</script>
