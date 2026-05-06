<template>
  <el-config-provider :size="size" :locale="elementLocale">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import elementZhCn from "element-plus/es/locale/lang/zh-cn";
import { useSettingsStore } from "@/store";
import { ComponentSize } from "@/enums";

const settingsStore = useSettingsStore();

const size = computed(() => settingsStore.size as ComponentSize);
const elementLocale = elementZhCn;
</script>
