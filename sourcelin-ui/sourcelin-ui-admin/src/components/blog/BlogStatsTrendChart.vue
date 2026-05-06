<template>
  <div class="trend-wrap">
    <div v-if="showDaySwitch" class="trend-header">
      <div>
        <span class="trend-title">增长趋势</span>
        <p class="trend-subtitle">聚合文章、评论、用户和树洞的阶段变化</p>
      </div>
      <el-radio-group v-model="innerDays" size="small" @change="loadTrend">
        <el-radio-button :label="7">近7天</el-radio-button>
        <el-radio-button :label="14">近14天</el-radio-button>
        <el-radio-button :label="30">近30天</el-radio-button>
      </el-radio-group>
    </div>
    <div v-else-if="title" class="trend-header trend-header--simple">
      <span class="trend-title">{{ title }}</span>
    </div>

    <el-skeleton :loading="loading" animated :rows="6">
      <ECharts :options="options" width="100%" :height="height" />
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import { useCssVar } from "@vueuse/core";
import BlogStatsAPI from "@/api/blog/stats";
import type { BlogStatsTrend } from "@/types/api";
import { resolveThemeColor, withAlpha } from "@/utils/theme";

defineOptions({
  name: "BlogStatsTrendChart",
});

const props = withDefaults(
  defineProps<{
    height?: string;
    showDaySwitch?: boolean;
    defaultDays?: number;
    title?: string;
    embedParentData?: boolean;
    trendData?: BlogStatsTrend;
  }>(),
  {
    height: "360px",
    showDaySwitch: false,
    defaultDays: 7,
    title: "",
    embedParentData: false,
    trendData: () => ({}),
  }
);

const emit = defineEmits<{
  (e: "loaded", value: BlogStatsTrend): void;
  (e: "error"): void;
}>();

const primaryColor = useCssVar("--el-color-primary");
const successColor = useCssVar("--el-color-success");
const warningColor = useCssVar("--el-color-warning");
const dangerColor = useCssVar("--el-color-danger");
const textPrimaryColor = useCssVar("--el-text-color-primary");
const textSecondaryColor = useCssVar("--el-text-color-secondary");
const borderLightColor = useCssVar("--el-border-color-lighter");
const fillLightColor = useCssVar("--el-fill-color-light");

const loading = ref(false);
const innerDays = ref(props.defaultDays);
const trend = ref<BlogStatsTrend>({});

function alignSeries(dates: string[] = [], series: number[] = []) {
  return dates.map((_, index) => (series[index] != null ? series[index] : 0));
}

const options = computed(() => {
  const dates = trend.value?.dates || [];
  const articleCounts = trend.value?.articleCounts || [];
  const commentCounts = trend.value?.commentCounts || [];
  const userCounts = trend.value?.userCounts || [];
  const treeholeCounts = alignSeries(dates, trend.value?.treeholeCounts || []);
  const primary = resolveThemeColor(primaryColor.value, "#409EFF");
  const success = resolveThemeColor(successColor.value, "#67C23A");
  const warning = resolveThemeColor(warningColor.value, "#E6A23C");
  const danger = resolveThemeColor(dangerColor.value, "#F56C6C");
  const textPrimary = resolveThemeColor(textPrimaryColor.value, "#303133");
  const textSecondary = resolveThemeColor(textSecondaryColor.value, "#909399");
  const borderLight = resolveThemeColor(borderLightColor.value, "#e4e7ed");
  const fillLight = resolveThemeColor(fillLightColor.value, "#ffffff");
  return {
    color: [primary, success, warning, danger],
    tooltip: {
      trigger: "axis",
      backgroundColor: fillLight,
      borderColor: borderLight,
      textStyle: {
        color: textPrimary,
      },
    },
    legend: {
      data: ["文章", "评论", "用户", "树洞"],
      top: 4,
      textStyle: {
        color: textSecondary,
      },
    },
    grid: {
      left: 20,
      right: 20,
      top: 72,
      bottom: 20,
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: borderLight,
        },
      },
      axisLabel: {
        color: textSecondary,
      },
    },
    yAxis: {
      type: "value",
      splitLine: {
        lineStyle: {
          color: borderLight,
          type: "dashed",
        },
      },
      axisLabel: {
        color: textSecondary,
      },
    },
    series: [
      {
        name: "文章",
        type: "line",
        smooth: true,
        symbolSize: 6,
        lineStyle: { width: 3 },
        areaStyle: { color: withAlpha(primary, 0.12, "#409EFF") },
        data: articleCounts,
      },
      {
        name: "评论",
        type: "line",
        smooth: true,
        symbolSize: 6,
        lineStyle: { width: 3 },
        areaStyle: { color: withAlpha(success, 0.12, "#67C23A") },
        data: commentCounts,
      },
      {
        name: "用户",
        type: "line",
        smooth: true,
        symbolSize: 6,
        lineStyle: { width: 3 },
        areaStyle: { color: withAlpha(warning, 0.12, "#E6A23C") },
        data: userCounts,
      },
      {
        name: "树洞",
        type: "line",
        smooth: true,
        symbolSize: 6,
        lineStyle: { width: 3 },
        areaStyle: { color: withAlpha(danger, 0.12, "#F56C6C") },
        data: treeholeCounts,
      },
    ],
  };
});

function loadTrend() {
  if (props.embedParentData) {
    trend.value = { ...(props.trendData || {}) };
    return;
  }
  loading.value = true;
  BlogStatsAPI.getTrend(innerDays.value)
    .then((data) => {
      trend.value = data || {};
      emit("loaded", trend.value);
    })
    .catch(() => {
      emit("error");
    })
    .finally(() => {
      loading.value = false;
    });
}

watch(
  () => props.trendData,
  (value) => {
    if (props.embedParentData) {
      trend.value = { ...(value || {}) };
    }
  },
  { deep: true, immediate: true }
);

onMounted(() => {
  innerDays.value = props.defaultDays;
  loadTrend();
});
</script>

<style scoped lang="scss">
.trend-wrap {
  width: 100%;
}

.trend-header {
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.trend-header--simple {
  justify-content: flex-start;
}

.trend-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--blog-title-color, var(--el-text-color-primary));
}

.trend-subtitle {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--blog-body-color, var(--el-text-color-secondary));
}

@media (max-width: 768px) {
  .trend-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
