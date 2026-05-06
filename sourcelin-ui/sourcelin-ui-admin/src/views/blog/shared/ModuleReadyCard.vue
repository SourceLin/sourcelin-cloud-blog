<template>
  <div class="app-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <el-tag type="warning">迁移中</el-tag>
        </div>
      </template>

      <el-alert :title="description" type="info" :closable="false" show-icon />
      <p class="card-hint">
        当前页面已接入动态路由与组件占位，后续按批次补齐完整 CRUD 交互与联调证据。
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "BlogModuleReadyCard",
  inheritAttrs: false,
});

withDefaults(
  defineProps<{
    title: string;
    description?: string;
  }>(),
  {
    description: "模块页面骨架已落地。",
  }
);
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-hint {
  margin: 12px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
</style>
