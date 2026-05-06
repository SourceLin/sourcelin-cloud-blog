<template>
  <div v-loading="pageLoading" class="app-container blog-dash">
    <div class="blog-dash__glow blog-dash__glow--primary" />
    <div class="blog-dash__glow blog-dash__glow--secondary" />

    <section class="dash-hero">
      <div class="dash-hero__copy">
        <span class="dash-pill">运营总览</span>
        <h1 class="dash-hero__title">博客数据统计</h1>
        <p class="dash-hero__desc">
          汇总文章、评论、用户、访问与树洞动态，优先暴露待处理信号，再进入分析视图。
        </p>

        <div class="dash-hero__chips">
          <span class="dash-chip">已发布 {{ num(summary.articlePublishedCount) }}</span>
          <span class="dash-chip">草稿 {{ num(summary.articleDraftCount) }}</span>
          <span class="dash-chip">用户 {{ num(summary.userCount) }}</span>
          <span class="dash-chip">树洞评论 {{ num(summary.treeholeCommentCount) }}</span>
          <span class="dash-chip">
            今日互动用户 {{ num(interactionOverview.todayActionUsers) }}
          </span>
        </div>
      </div>

      <div class="dash-hero__signals">
        <article
          v-for="item in heroSignals"
          :key="item.key"
          class="dash-hero-signal"
          :class="{ 'is-clickable': Boolean(item.action) }"
          @click="item.action?.()"
        >
          <div class="dash-hero-signal__icon">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="dash-hero-signal__content">
            <div class="dash-hero-signal__title">{{ item.title }}</div>
            <div class="dash-hero-signal__value">{{ item.value }}</div>
            <div class="dash-hero-signal__hint">{{ item.hint }}</div>
          </div>
        </article>
      </div>
    </section>

    <section class="dash-kpi-grid">
      <article v-for="item in summaryCards" :key="item.key" class="dash-kpi-card">
        <div class="dash-kpi-card__icon" :class="`dash-kpi-card__icon--${item.key}`">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="dash-kpi-card__main">
          <div class="dash-kpi-card__label">{{ item.label }}</div>
          <div class="dash-kpi-card__value">{{ item.value }}</div>
        </div>
      </article>
    </section>

    <section class="dash-workbench">
      <div class="dash-section-head">
        <div>
          <h2 class="dash-section-head__title">待办入口</h2>
          <p class="dash-section-head__desc">优先处理审核、状态和内容结构相关任务。</p>
        </div>
      </div>

      <div class="dash-workbench__grid">
        <article class="dash-workbench-card is-clickable" @click="goCommentWorkbench">
          <div class="dash-workbench-card__label">待审核评论</div>
          <div class="dash-workbench-card__value">{{ num(summary.commentPendingCount) }}</div>
          <div class="dash-workbench-card__hint">进入评论管理，继续处理审核队列</div>
        </article>

        <article class="dash-workbench-card is-clickable" @click="goFriendPending">
          <div class="dash-workbench-card__label">友链待审核</div>
          <div class="dash-workbench-card__value">{{ num(summary.friendLinkPendingCount) }}</div>
          <div class="dash-workbench-card__hint">进入友链管理，确认对外展示入口</div>
        </article>

        <article class="dash-workbench-card">
          <div class="dash-workbench-card__label">文章状态</div>
          <ul class="dash-struct-list">
            <li>
              <span>审核中</span>
              <b>{{ num(summary.articleReviewCount) }}</b>
            </li>
            <li>
              <span>已发布</span>
              <b>{{ num(summary.articlePublishedCount) }}</b>
            </li>
            <li>
              <span>草稿</span>
              <b>{{ num(summary.articleDraftCount) }}</b>
            </li>
          </ul>
        </article>

        <article class="dash-workbench-card is-clickable" @click="goTreehole">
          <div class="dash-workbench-card__label">树洞概览</div>
          <ul class="dash-struct-list">
            <li>
              <span>内容</span>
              <b>{{ num(summary.treeholeCount) }}</b>
            </li>
            <li>
              <span>评论</span>
              <b>{{ num(summary.treeholeCommentCount) }}</b>
            </li>
            <li>
              <span>置顶</span>
              <b>{{ num(summary.treeholePinnedCount) }}</b>
            </li>
          </ul>
        </article>

        <article class="dash-workbench-card is-clickable" @click="goInteractionRecords">
          <div class="dash-workbench-card__label">互动总览（点赞 / 收藏）</div>
          <ul class="dash-struct-list">
            <li>
              <span>点赞总量</span>
              <b>{{ num(interactionOverview.likeTotal) }}</b>
            </li>
            <li>
              <span>收藏总量</span>
              <b>{{ num(interactionOverview.collectTotal) }}</b>
            </li>
            <li>
              <span>今日点赞用户</span>
              <b>{{ num(interactionOverview.todayLikeUsers) }}</b>
            </li>
            <li>
              <span>今日收藏用户</span>
              <b>{{ num(interactionOverview.todayCollectUsers) }}</b>
            </li>
            <li>
              <span>今日互动用户</span>
              <b>{{ num(interactionOverview.todayActionUsers) }}</b>
            </li>
          </ul>
          <div class="dash-workbench-card__hint">
            已并入数据统计页，可直接进入互动记录查看明细。
          </div>
        </article>
      </div>
    </section>

    <section class="dash-analytics">
      <div class="dash-section-head">
        <div>
          <h2 class="dash-section-head__title">分析面板</h2>
          <p class="dash-section-head__desc">
            统一展示文章状态占比、评论审核结构与阶段性增长趋势。
          </p>
        </div>
      </div>

      <div class="dash-analytics__grid">
        <el-card shadow="never" class="dash-panel">
          <BlogArticleStatusPie :summary="summary" />
        </el-card>
        <el-card shadow="never" class="dash-panel">
          <BlogCommentStatusBar :summary="summary" />
        </el-card>
      </div>

      <el-card shadow="never" class="dash-panel dash-panel--wide">
        <BlogStatsTrendChart show-day-switch :default-days="7" />
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import {
  ChatDotRound,
  ChatLineRound,
  Document,
  Message,
  User,
  View,
} from "@element-plus/icons-vue";
import type { BlogStatsSummary } from "@/types/api";
import BlogStatsAPI from "@/api/blog/stats";
import InteractionAPI from "@/api/blog/interaction";
import type { InteractionOverview } from "@/types/api";
import BlogArticleStatusPie from "@/components/blog/BlogArticleStatusPie.vue";
import BlogCommentStatusBar from "@/components/blog/BlogCommentStatusBar.vue";
import BlogStatsTrendChart from "@/components/blog/BlogStatsTrendChart.vue";

defineOptions({
  name: "BlogStats",
  inheritAttrs: false,
});

const router = useRouter();
const summaryLoading = ref(false);
const interactionLoading = ref(false);
const summary = ref<BlogStatsSummary>({});
const interactionOverview = ref<InteractionOverview>({});
const STATS_SUMMARY_CACHE_KEY = "blog-admin:stats-summary-cache:v1";
const STATS_INTERACTION_OVERVIEW_CACHE_KEY = "blog-admin:interaction-overview-cache:v1";
const pageLoading = computed(() => summaryLoading.value || interactionLoading.value);

const heroSignals = computed(() => [
  {
    key: "pending-comment",
    title: "待审核评论",
    value: num(summary.value.commentPendingCount),
    hint: "优先处理评论审核",
    icon: ChatDotRound,
    action: goCommentWorkbench,
  },
  {
    key: "pending-link",
    title: "友链待审核",
    value: num(summary.value.friendLinkPendingCount),
    hint: "检查合作入口状态",
    icon: Message,
    action: goFriendPending,
  },
  {
    key: "views",
    title: "累计访问",
    value: num(summary.value.viewCount),
    hint: "内容总曝光规模",
    icon: View,
  },
  {
    key: "treehole",
    title: "树洞活跃",
    value: num(summary.value.treeholeCount) + num(summary.value.treeholeCommentCount),
    hint: "内容与评论活跃度",
    icon: ChatLineRound,
    action: goTreehole,
  },
]);

const summaryCards = computed(() => [
  { key: "article", label: "文章总数", value: num(summary.value.articleCount), icon: Document },
  { key: "comment", label: "评论总数", value: num(summary.value.commentCount), icon: ChatDotRound },
  { key: "user", label: "用户总数", value: num(summary.value.userCount), icon: User },
  { key: "view", label: "累计访问", value: num(summary.value.viewCount), icon: View },
  {
    key: "treehole",
    label: "树洞条数",
    value: num(summary.value.treeholeCount),
    icon: ChatLineRound,
  },
  {
    key: "treehole-msg",
    label: "树洞评论",
    value: num(summary.value.treeholeCommentCount),
    icon: Message,
  },
]);

function num(value?: number) {
  const parsed = Number(value ?? 0);
  return Number.isFinite(parsed) ? parsed : 0;
}

function loadSummary() {
  summaryLoading.value = true;
  BlogStatsAPI.getSummary()
    .then((data) => {
      const current = data || {};
      summary.value = current;
      localStorage.setItem(STATS_SUMMARY_CACHE_KEY, JSON.stringify(current));
    })
    .catch(() => {
      try {
        const cachedText = localStorage.getItem(STATS_SUMMARY_CACHE_KEY);
        if (!cachedText) return;
        summary.value = (JSON.parse(cachedText) as BlogStatsSummary) || {};
        ElMessage.warning("统计接口暂不可用，已展示本地缓存快照");
      } catch {
        // ignore
      }
    })
    .finally(() => {
      summaryLoading.value = false;
    });
}

function loadInteractionOverview() {
  interactionLoading.value = true;
  InteractionAPI.getOverview()
    .then((data) => {
      const current = data || {};
      interactionOverview.value = current;
      localStorage.setItem(STATS_INTERACTION_OVERVIEW_CACHE_KEY, JSON.stringify(current));
    })
    .catch(() => {
      try {
        const cachedText = localStorage.getItem(STATS_INTERACTION_OVERVIEW_CACHE_KEY);
        if (!cachedText) return;
        interactionOverview.value = (JSON.parse(cachedText) as InteractionOverview) || {};
        ElMessage.warning("互动总览接口暂不可用，已展示本地缓存快照");
      } catch {
        // ignore
      }
    })
    .finally(() => {
      interactionLoading.value = false;
    });
}

function goCommentWorkbench() {
  router.push({ path: "/blog/comment", query: { workbench: "1" } });
}

function goFriendPending() {
  router.push({ path: "/blog/link", query: { status: "0" } });
}

function goTreehole() {
  router.push("/blog/treehole");
}

function goInteractionRecords() {
  router.push("/blog/interactions/records");
}

onMounted(() => {
  loadSummary();
  loadInteractionOverview();
});
</script>

<style lang="scss" src="@/styles/blog-dashboard.scss"></style>
