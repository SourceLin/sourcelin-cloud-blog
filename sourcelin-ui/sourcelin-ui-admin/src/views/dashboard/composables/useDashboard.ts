import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { dayjs } from "element-plus";
import { useCssVar, useTransition } from "@vueuse/core";
import LogAPI from "@/api/system/log";
import NoticeAPI from "@/api/system/notice";
import type { VisitStatsDetail, VisitTrendDetail } from "@/types/api";
import { useUserStore } from "@/store/modules/user";
import { useRecentMenus } from "@/composables";
import { formatGrowthRate } from "@/utils";
import { isElementPlusIconName } from "@/utils/element-icons";
import { resolveThemeColor, withAlpha } from "@/utils/theme";

export function useDashboard() {
  const router = useRouter();
  const userStore = useUserStore();
  const { recentMenus, clearRecentMenus } = useRecentMenus();
  const unreadNoticeCount = ref(0);

  const primaryColor = useCssVar("--el-color-primary");
  const successColor = useCssVar("--el-color-success");
  const textPrimaryColor = useCssVar("--el-text-color-primary");
  const textSecondaryColor = useCssVar("--el-text-color-secondary");
  const borderLightColor = useCssVar("--el-border-color-lighter");
  const fillLightColor = useCssVar("--el-fill-color-light");
  const visitTrendDateRange = ref(7);
  const visitStatsData = ref<VisitStatsDetail>({
    todayUvCount: 0,
    uvGrowthRate: 0,
    totalUvCount: 0,
    todayPvCount: 0,
    pvGrowthRate: 0,
    totalPvCount: 0,
  });
  const visitTrendData = ref<VisitTrendDetail>({
    dates: [],
    pvList: [],
    uvList: [],
    ipList: [],
  });

  const todayLabel = computed(() => dayjs().format("YYYY年MM月DD日"));
  const unreadNoticeHint = computed(() =>
    unreadNoticeCount.value > 99 ? "99+" : `${unreadNoticeCount.value}`
  );

  const greetings = computed(() => {
    const hours = new Date().getHours();
    const nickname = userStore.userInfo.nickName || "管理员";
    if (hours >= 6 && hours < 8) return "早安，先看今天的数据脉搏。";
    if (hours >= 8 && hours < 12) return `上午好，${nickname}`;
    if (hours >= 12 && hours < 18) return `下午好，${nickname}`;
    if (hours >= 18 && hours < 24) return `晚上好，${nickname}`;
    return `夜深了，${nickname}，别忘了留意站点动态。`;
  });

  const transitionUvCount = useTransition(
    computed(() => visitStatsData.value.todayUvCount),
    {
      duration: 1000,
      transition: [0.25, 0.1, 0.25, 1.0],
    }
  );

  const transitionTotalUvCount = useTransition(
    computed(() => visitStatsData.value.totalUvCount),
    {
      duration: 1200,
      transition: [0.25, 0.1, 0.25, 1.0],
    }
  );

  const transitionPvCount = useTransition(
    computed(() => visitStatsData.value.todayPvCount),
    {
      duration: 1000,
      transition: [0.25, 0.1, 0.25, 1.0],
    }
  );

  const transitionTotalPvCount = useTransition(
    computed(() => visitStatsData.value.totalPvCount),
    {
      duration: 1200,
      transition: [0.25, 0.1, 0.25, 1.0],
    }
  );

  const displayTransitionUvCount = computed(() => Math.round(Number(transitionUvCount.value ?? 0)));
  const displayTransitionTotalUvCount = computed(() =>
    Math.round(Number(transitionTotalUvCount.value ?? 0))
  );
  const displayTransitionPvCount = computed(() => Math.round(Number(transitionPvCount.value ?? 0)));
  const displayTransitionTotalPvCount = computed(() =>
    Math.round(Number(transitionTotalPvCount.value ?? 0))
  );

  const uvGrowthLabel = computed(() => formatGrowthLabel(visitStatsData.value.uvGrowthRate));
  const pvGrowthLabel = computed(() => formatGrowthLabel(visitStatsData.value.pvGrowthRate));
  const uvGrowthClass = computed(() => computeGrowthRateClass(visitStatsData.value.uvGrowthRate));
  const pvGrowthClass = computed(() => computeGrowthRateClass(visitStatsData.value.pvGrowthRate));

  const signalCards = computed(() => [
    {
      title: "累计访客",
      value: displayTransitionTotalUvCount.value,
      hint: "沉淀到当前为止的独立访客规模",
    },
    {
      title: "累计浏览",
      value: displayTransitionTotalPvCount.value,
      hint: "页面总体曝光表现",
    },
    {
      title: "最近访问",
      value: recentMenus.value.length,
      hint: "常用页面入口会自动沉淀在工作台",
    },
    {
      title: "观察窗口",
      value: `${visitTrendDateRange.value} 天`,
      hint: "当前趋势图所展示的数据时间范围",
    },
  ]);

  const visitTrendChartOptions = computed(() => {
    const primary = resolveThemeColor(primaryColor.value, "#409EFF");
    const success = resolveThemeColor(successColor.value, "#67C23A");
    const textPrimary = resolveThemeColor(textPrimaryColor.value, "#303133");
    const textSecondary = resolveThemeColor(textSecondaryColor.value, "#909399");
    const borderLight = resolveThemeColor(borderLightColor.value, "#e4e7ed");
    const fillLight = resolveThemeColor(fillLightColor.value, "#ffffff");
    return {
      color: [primary, success],
      tooltip: {
        trigger: "axis",
        backgroundColor: fillLight,
        borderColor: borderLight,
        textStyle: {
          color: textPrimary,
        },
      },
      legend: {
        data: ["浏览量 PV", "访客量 UV"],
        right: 0,
        textStyle: {
          color: textSecondary,
        },
      },
      grid: {
        left: 12,
        right: 12,
        top: 52,
        bottom: 12,
        containLabel: true,
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: visitTrendData.value.dates,
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
          name: "浏览量 PV",
          type: "line",
          smooth: true,
          symbolSize: 6,
          data: visitTrendData.value.pvList,
          areaStyle: {
            color: withAlpha(primary, 0.18, "#409EFF"),
          },
          lineStyle: {
            width: 3,
          },
        },
        {
          name: "访客量 UV",
          type: "line",
          smooth: true,
          symbolSize: 6,
          data: visitTrendData.value.uvList,
          areaStyle: {
            color: withAlpha(success, 0.16, "#67C23A"),
          },
          lineStyle: {
            width: 3,
          },
        },
      ],
    };
  });

  function formatGrowthLabel(growthRate?: number) {
    if (growthRate === undefined || growthRate === null) return "较昨日 --";
    return `较昨日 ${formatGrowthRate(growthRate)}`;
  }

  function computeGrowthRateClass(growthRate?: number) {
    if (!growthRate) return "is-neutral";
    if (growthRate > 0) return "is-up";
    return "is-down";
  }

  function resolveRecentElementIcon(icon?: string | null) {
    if (!icon) return "";
    const iconName = icon.startsWith("el-icon-") ? icon.replace("el-icon-", "") : icon;
    return isElementPlusIconName(iconName) ? iconName : "";
  }

  function fetchVisitStatsData() {
    LogAPI.getVisitOverview().then((data) => {
      visitStatsData.value = data;
    });
  }

  function fetchVisitTrendData() {
    const startDate = dayjs()
      .subtract(visitTrendDateRange.value - 1, "day")
      .format("YYYY-MM-DD");
    const endDate = dayjs().format("YYYY-MM-DD");
    LogAPI.getVisitTrend({ startDate, endDate }).then((data) => {
      visitTrendData.value = data;
    });
  }

  function fetchUnreadNoticeCount() {
    NoticeAPI.getMyNoticePage({ page: 1, pageSize: 1, isRead: 0 }).then((page) => {
      unreadNoticeCount.value = page.total ?? 0;
    });
  }

  function goProfile() {
    router.push("/profile");
  }

  function goNotice() {
    router.push("/my-notice");
  }

  function openRecentMenu(path: string) {
    router.push(path);
  }

  watch(
    () => visitTrendDateRange.value,
    () => {
      fetchVisitTrendData();
    },
    { immediate: true }
  );

  onMounted(() => {
    fetchVisitStatsData();
    fetchUnreadNoticeCount();
  });

  return {
    userStore,
    recentMenus,
    clearRecentMenus,
    todayLabel,
    unreadNoticeHint,
    greetings,
    visitTrendDateRange,
    displayTransitionUvCount,
    displayTransitionTotalUvCount,
    displayTransitionPvCount,
    displayTransitionTotalPvCount,
    uvGrowthLabel,
    pvGrowthLabel,
    uvGrowthClass,
    pvGrowthClass,
    signalCards,
    visitTrendChartOptions,
    resolveRecentElementIcon,
    goProfile,
    goNotice,
    openRecentMenu,
  };
}
