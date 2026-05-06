<template>
  <el-card shadow="never" class="dashboard-panel dashboard-panel--trend">
    <template #header>
      <div class="dashboard-panel__head">
        <div>
          <div class="dashboard-panel__title">访问趋势</div>
          <div class="dashboard-panel__desc">观察最近一段时间的 UV / PV 变化</div>
        </div>
        <el-radio-group
          :model-value="visitTrendDateRange"
          size="small"
          @update:model-value="emit('update:visitTrendDateRange', Number($event))"
        >
          <el-radio-button :label="7">近7天</el-radio-button>
          <el-radio-button :label="30">近30天</el-radio-button>
        </el-radio-group>
      </div>
    </template>
    <ECharts :options="visitTrendChartOptions" height="380px" />
  </el-card>
</template>

<script setup lang="ts">
import ECharts from "@/components/ECharts/index.vue";

defineProps<{
  visitTrendDateRange: number;
  visitTrendChartOptions: Record<string, any>;
}>();

const emit = defineEmits<{
  "update:visitTrendDateRange": [value: number];
}>();
</script>
