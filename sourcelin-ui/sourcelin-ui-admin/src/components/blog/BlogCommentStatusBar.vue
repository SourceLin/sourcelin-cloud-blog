<template>
  <div class="chart-wrap">
    <div v-if="title" class="chart-head">
      <div class="chart-title">{{ title }}</div>
      <div class="chart-subtitle">评论审核结构与异常比例</div>
    </div>
    <ECharts :options="options" width="100%" :height="height" />
  </div>
</template>

<script setup lang="ts">
import { useCssVar } from "@vueuse/core";
import type { BlogStatsSummary } from "@/types/api";
import { resolveThemeColor } from "@/utils/theme";

defineOptions({
  name: "BlogCommentStatusBar",
});

const props = withDefaults(
  defineProps<{
    summary?: BlogStatsSummary;
    height?: string;
    title?: string;
  }>(),
  {
    summary: () => ({}),
    height: "300px",
    title: "评论状态分布",
  }
);

const warningColor = useCssVar("--el-color-warning");
const successColor = useCssVar("--el-color-success");
const dangerColor = useCssVar("--el-color-danger");
const infoColor = useCssVar("--el-color-info");
const textPrimaryColor = useCssVar("--el-text-color-primary");
const textSecondaryColor = useCssVar("--el-text-color-secondary");
const borderLightColor = useCssVar("--el-border-color-lighter");
const fillLightColor = useCssVar("--el-fill-color-light");

function num(value?: number) {
  const parsed = Number(value ?? 0);
  return Number.isFinite(parsed) ? parsed : 0;
}

const options = computed(() => {
  const pending = num(props.summary.commentPendingCount);
  const approved = num(props.summary.commentApprovedCount);
  const rejected = num(props.summary.commentRejectedCount);
  const other = num(props.summary.commentOtherStatusCount);
  return {
    color: [
      resolveThemeColor(warningColor.value, "#E6A23C"),
      resolveThemeColor(successColor.value, "#67C23A"),
      resolveThemeColor(dangerColor.value, "#F56C6C"),
      resolveThemeColor(infoColor.value, "#909399"),
    ],
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      backgroundColor: resolveThemeColor(fillLightColor.value, "#ffffff"),
      borderColor: resolveThemeColor(borderLightColor.value, "#dcdfe6"),
      textStyle: {
        color: resolveThemeColor(textPrimaryColor.value, "#303133"),
      },
    },
    grid: {
      left: 12,
      right: 12,
      top: 16,
      bottom: 28,
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: ["待审核", "通过", "拒绝", "其它"],
      axisLine: {
        lineStyle: {
          color: resolveThemeColor(borderLightColor.value, "#e4e7ed"),
        },
      },
      axisLabel: {
        color: resolveThemeColor(textSecondaryColor.value, "#909399"),
      },
    },
    yAxis: {
      type: "value",
      minInterval: 1,
      splitLine: {
        lineStyle: {
          color: resolveThemeColor(borderLightColor.value, "#ebeef5"),
          type: "dashed",
        },
      },
      axisLabel: {
        color: resolveThemeColor(textSecondaryColor.value, "#909399"),
      },
    },
    series: [
      {
        name: "评论状态",
        type: "bar",
        barMaxWidth: 40,
        data: [
          {
            value: pending,
            itemStyle: { color: resolveThemeColor(warningColor.value, "#E6A23C") },
          },
          {
            value: approved,
            itemStyle: { color: resolveThemeColor(successColor.value, "#67C23A") },
          },
          {
            value: rejected,
            itemStyle: { color: resolveThemeColor(dangerColor.value, "#F56C6C") },
          },
          { value: other, itemStyle: { color: resolveThemeColor(infoColor.value, "#909399") } },
        ],
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
        },
      },
    ],
  };
});
</script>

<style scoped lang="scss">
.chart-wrap {
  width: 100%;
}

.chart-head {
  margin-bottom: 10px;
}

.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--blog-title-color, var(--el-text-color-primary));
}

.chart-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: var(--blog-body-color, var(--el-text-color-secondary));
}
</style>
