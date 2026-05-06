<template>
  <div class="dashboard-shell">
    <div class="dashboard-shell__glow dashboard-shell__glow--primary" />
    <div class="dashboard-shell__glow dashboard-shell__glow--secondary" />

    <DashboardHero
      :user-info="userStore.userInfo"
      :today-label="todayLabel"
      :greetings="greetings"
      :recent-menus-count="recentMenus.length"
      :unread-notice-hint="unreadNoticeHint"
      :display-transition-uv-count="displayTransitionUvCount"
      :display-transition-total-uv-count="displayTransitionTotalUvCount"
      :display-transition-pv-count="displayTransitionPvCount"
      :display-transition-total-pv-count="displayTransitionTotalPvCount"
      :uv-growth-label="uvGrowthLabel"
      :pv-growth-label="pvGrowthLabel"
      :uv-growth-class="uvGrowthClass"
      :pv-growth-class="pvGrowthClass"
      @profile="goProfile"
      @notice="goNotice"
    />

    <DashboardSignalGrid :signal-cards="signalCards" />

    <section class="dashboard-main-grid">
      <DashboardTrendPanel
        v-model:visit-trend-date-range="visitTrendDateRange"
        :visit-trend-chart-options="visitTrendChartOptions"
      />

      <DashboardRecentPanel
        :recent-menus="recentMenus"
        :resolve-recent-element-icon="resolveRecentElementIcon"
        @clear="clearRecentMenus"
        @open="openRecentMenu"
      />
    </section>
  </div>
</template>

<script setup lang="ts">
import DashboardHero from "./components/DashboardHero.vue";
import DashboardRecentPanel from "./components/DashboardRecentPanel.vue";
import DashboardSignalGrid from "./components/DashboardSignalGrid.vue";
import DashboardTrendPanel from "./components/DashboardTrendPanel.vue";
import { useDashboard } from "./composables/useDashboard";
import "./styles/dashboard.scss";

defineOptions({
  name: "Dashboard",
  inheritAttrs: false,
});

const {
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
} = useDashboard();
</script>
