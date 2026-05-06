<template>
  <div class="inner-link-container">
    <iframe v-if="safeUrl" :src="safeUrl" class="inner-link-frame" frameborder="0" loading="lazy" />
    <el-empty v-else description="无效的外链地址，仅支持 http/https" />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";

defineOptions({
  name: "InnerLink",
});

const route = useRoute();

const safeUrl = computed(() => {
  const link = typeof route.meta?.link === "string" ? route.meta.link.trim() : "";
  if (!link) {
    return "";
  }
  return /^https?:\/\//.test(link) ? link : "";
});
</script>

<style scoped lang="scss">
.inner-link-container {
  width: 100%;
  height: 100%;
  min-height: 100%;
  background-color: var(--el-bg-color-page);
}

.inner-link-frame {
  width: 100%;
  height: 100%;
  background: var(--el-bg-color);
  border: 0;
}
</style>
