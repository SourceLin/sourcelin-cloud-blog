<template>
  <el-config-provider :size="size" :locale="elementLocale">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import { watch } from "vue";
import elementZhCn from "element-plus/es/locale/lang/zh-cn";
import { useUserStore } from "@/store/modules/user";
import { useSettingsStore } from "@/store";
import { ComponentSize } from "@/enums";
import request from "@/utils/request";
import { AuthStorage } from "@/utils/auth";
import sseClientManager from "@/utils/SseClientManager";

const settingsStore = useSettingsStore();

const size = computed(() => settingsStore.size as ComponentSize);
const elementLocale = elementZhCn;

const userStore = useUserStore();
watch(
  () => userStore.userInfo.userId,
  (userId, _, onCleanup) => {
    sseClientManager.disconnect();
    const token = AuthStorage.getAccessToken();
    if (!userId || !token) return;
    let current = true;
    onCleanup(() => {
      current = false;
      sseClientManager.disconnect();
    });
    void request<unknown, boolean>({ url: "/blog/front/messages/realtime-status", method: "get" })
      .then((enabled) => {
        if (enabled && current && token === AuthStorage.getAccessToken())
          void sseClientManager.connect();
      })
      .catch(() => undefined);
  },
  { immediate: true }
);
</script>
