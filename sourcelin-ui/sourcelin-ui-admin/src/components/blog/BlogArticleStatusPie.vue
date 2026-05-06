<template>
  <div class="chart-wrap">
    <div v-if="title" class="chart-head">
      <div class="chart-title">{{ title }}</div>
      <div class="chart-subtitle">文章从审核到发布的状态占比</div>
    </div>
    <ECharts :options="options" width="100%" :height="height" />
  </div>
</template>

<script setup lang="ts">
import { useCssVar } from "@vueuse/core";
import type { BlogStatsSummary } from "@/types/api";
import { resolveThemeColor } from "@/utils/theme";

defineOptions({
  name: "BlogArticleStatusPie",
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
    title: "文章状态分布",
  }
);

const successColor = useCssVar("--el-color-success");
const primaryColor = useCssVar("--el-color-primary");
const warningColor = useCssVar("--el-color-warning");
const textSecondaryColor = useCssVar("--el-text-color-secondary");
const borderLightColor = useCssVar("--el-bg-color-page");
const fillLightColor = useCssVar("--el-fill-color-light");
const textPrimaryColor = useCssVar("--el-text-color-primary");

function num(value?: number) {
  const parsed = Number(value ?? 0);
  return Number.isFinite(parsed) ? parsed : 0;
}

const options = computed(() => {
  const review = num(props.summary.articleReviewCount);
  const published = num(props.summary.articlePublishedCount);
  const draft = num(props.summary.articleDraftCount);
  const other = num(props.summary.articleOtherStatusCount);
  const data = [
    { name: "审核中", value: review },
    { name: "已发布", value: published },
    { name: "草稿", value: draft },
  ];
  if (other > 0) {
    data.push({ name: "其它", value: other });
  }
  return {
    color: [
      resolveThemeColor(warningColor.value, "#E6A23C"),
      resolveThemeColor(successColor.value, "#67C23A"),
      resolveThemeColor(primaryColor.value, "#409EFF"),
      resolveThemeColor(textSecondaryColor.value, "#909399"),
    ],
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)",
      backgroundColor: resolveThemeColor(fillLightColor.value, "#ffffff"),
      textStyle: {
        color: resolveThemeColor(textPrimaryColor.value, "#303133"),
      },
    },
    legend: {
      orient: "horizontal",
      bottom: 0,
      textStyle: {
        color: resolveThemeColor(textSecondaryColor.value, "#909399"),
      },
    },
    series: [
      {
        name: "文章状态",
        type: "pie",
        radius: ["40%", "64%"],
        center: ["50%", "44%"],
        avoidLabelOverlap: true,
        label: {
          color: resolveThemeColor(textSecondaryColor.value, "#909399"),
        },
        itemStyle: {
          borderRadius: 6,
          borderColor: resolveThemeColor(borderLightColor.value, "#ffffff"),
          borderWidth: 2,
        },
        data,
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
