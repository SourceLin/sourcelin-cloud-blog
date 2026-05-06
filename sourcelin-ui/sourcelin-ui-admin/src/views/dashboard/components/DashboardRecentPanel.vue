<template>
  <el-card shadow="never" class="dashboard-panel dashboard-panel--recent">
    <template #header>
      <div class="dashboard-panel__head">
        <div>
          <div class="dashboard-panel__title">最近访问</div>
          <div class="dashboard-panel__desc">保留最近打开的业务入口</div>
        </div>
        <el-button
          v-if="recentMenus.length > 0"
          type="primary"
          link
          size="small"
          @click="emit('clear')"
        >
          清空
        </el-button>
      </div>
    </template>

    <div class="dashboard-recent">
      <div v-if="recentMenus.length > 0" class="dashboard-recent__grid">
        <button
          v-for="item in recentMenus"
          :key="item.path"
          type="button"
          class="dashboard-recent__item"
          @click="emit('open', item.path)"
        >
          <span class="dashboard-recent__icon">
            <el-icon v-if="resolveRecentElementIcon(item.icon)">
              <component :is="resolveRecentElementIcon(item.icon)" />
            </el-icon>
            <span v-else-if="item.icon" :class="`i-svg:${item.icon}`" />
            <el-icon v-else><Menu /></el-icon>
          </span>
          <span class="dashboard-recent__text">{{ item.title }}</span>
        </button>
      </div>

      <div v-else class="dashboard-recent__empty">
        <el-icon class="dashboard-recent__empty-icon"><Clock /></el-icon>
        <p>暂无访问记录</p>
        <span>访问过的页面会自动出现在这里</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { Clock, Menu } from "@element-plus/icons-vue";
import type { RecentMenuItem } from "@/composables/useRecentMenus";

defineProps<{
  recentMenus: RecentMenuItem[];
  resolveRecentElementIcon: (icon?: string | null) => string;
}>();

const emit = defineEmits<{
  clear: [];
  open: [path: string];
}>();
</script>
