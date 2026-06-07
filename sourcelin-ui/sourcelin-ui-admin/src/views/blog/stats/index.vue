<template>
  <div v-loading="pageLoading" class="app-container blog-dash">
    <!-- 背景渐变高光 -->
    <div class="blog-dash__glow blog-dash__glow--primary" />
    <div class="blog-dash__glow blog-dash__glow--secondary" />

    <!-- 看板 Header 与 KPI 汇总网格 -->
    <section class="dash-hero-banner">
      <div class="dash-hero-banner__copy">
        <span class="dash-pill">运营总览</span>
        <h1 class="dash-hero-banner__title">博客数据统计</h1>
        <p class="dash-hero-banner__desc">
          汇总文章、评论、用户、访问与树洞动态，点击待办可直接在看板原地滑出进行异步审核处理。
        </p>
      </div>
    </section>

    <!-- KPI 总览栅格卡片 -->
    <section class="dash-kpi-grid">
      <article v-for="item in summaryCards" :key="item.key" class="dash-kpi-card">
        <div class="dash-kpi-card__icon" :class="`dash-kpi-card__icon--${item.key}`">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="dash-kpi-card__main">
          <div class="dash-kpi-card__label">{{ item.label }}</div>
          <div class="dash-kpi-card__value">{{ num(item.value) }}</div>
        </div>
      </article>
    </section>

    <!-- 待办工作台 -->
    <section class="dash-workbench">
      <div class="dash-section-head">
        <div>
          <h2 class="dash-section-head__title">数据工作台 / 待办中心</h2>
          <p class="dash-section-head__desc">优先处理审核、状态和内容结构相关任务。</p>
        </div>
      </div>

      <div class="dash-workbench__grid">
        <!-- 待审核评论 -->
        <article
          class="dash-workbench-card is-clickable"
          :class="{ 'has-pending': num(summary.commentPendingCount) > 0 }"
          @click="openAuditDrawer('comment')"
        >
          <div class="dash-workbench-card__label">待审核评论</div>
          <div class="dash-workbench-card__value">{{ num(summary.commentPendingCount) }}</div>
          <div class="dash-workbench-card__hint">
            <span v-if="num(summary.commentPendingCount) > 0" class="warning-tag">急需处理</span>
            点击直接在看板侧滑审核，零页面路由跳出
          </div>
        </article>

        <!-- 友链待审核 -->
        <article
          class="dash-workbench-card is-clickable"
          :class="{ 'has-pending': num(summary.friendLinkPendingCount) > 0 }"
          @click="openAuditDrawer('link')"
        >
          <div class="dash-workbench-card__label">友链待审核</div>
          <div class="dash-workbench-card__value">{{ num(summary.friendLinkPendingCount) }}</div>
          <div class="dash-workbench-card__hint">
            <span v-if="num(summary.friendLinkPendingCount) > 0" class="warning-tag">急需处理</span>
            点击原地审核合作入口，保障友情链接有效性
          </div>
        </article>

        <!-- 文章状态汇总 -->
        <article class="dash-workbench-card">
          <div class="dash-workbench-card__label">文章当前状态</div>
          <ul class="dash-struct-list">
            <li>
              <span>审核中</span>
              <b :class="{ 'warning-text': num(summary.articleReviewCount) > 0 }">
                {{ num(summary.articleReviewCount) }}
              </b>
            </li>
            <li>
              <span>已发布</span>
              <b>{{ num(summary.articlePublishedCount) }}</b>
            </li>
            <li>
              <span>草稿箱</span>
              <b>{{ num(summary.articleDraftCount) }}</b>
            </li>
          </ul>
        </article>

        <!-- 树洞概览 -->
        <article class="dash-workbench-card is-clickable" @click="goTreehole">
          <div class="dash-workbench-card__label">树洞概览</div>
          <ul class="dash-struct-list">
            <li>
              <span>信息条数</span>
              <b>{{ num(summary.treeholeCount) }}</b>
            </li>
            <li>
              <span>树洞评论</span>
              <b>{{ num(summary.treeholeCommentCount) }}</b>
            </li>
            <li>
              <span>置顶树洞</span>
              <b>{{ num(summary.treeholePinnedCount) }}</b>
            </li>
          </ul>
        </article>

        <!-- 互动总览 -->
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
              <span>今日互动用户</span>
              <b>{{ num(interactionOverview.todayActionUsers) }}</b>
            </li>
          </ul>
          <div class="dash-workbench-card__hint">点击进入明细，直接查看交互记录明细</div>
        </article>
      </div>
    </section>

    <!-- 分析面板 -->
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
        <!-- 增长趋势，引入双 Y 坐标轴自适应 -->
        <BlogStatsTrendChart show-day-switch :default-days="7" />
      </el-card>
    </section>

    <!-- 极速原地审核抽屉 (Audit Drawer) -->
    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      size="560px"
      destroy-on-close
      custom-class="audit-drawer"
    >
      <div v-loading="drawerLoading" class="audit-drawer-content">
        <!-- 无待审核数据 -->
        <div
          v-if="
            !drawerLoading &&
            ((drawerType === 'comment' && pendingComments.length === 0) ||
              (drawerType === 'link' && pendingLinks.length === 0))
          "
          class="empty-audit"
        >
          <el-empty description="当前暂无待审核的任务" />
        </div>

        <!-- 评论审核列表 -->
        <div v-else-if="drawerType === 'comment'" class="audit-list">
          <div v-for="item in pendingComments" :key="item.id" class="audit-card comment-audit-card">
            <div class="audit-card-head">
              <span class="auditor-ip">来自 IP: {{ item.ipSource || "未知地区" }}</span>
              <span class="audit-time">{{ item.createTime }}</span>
            </div>
            <div class="audit-card-body">
              <p class="comment-content-text">{{ item.content }}</p>
            </div>
            <div class="audit-card-foot">
              <el-button
                type="danger"
                size="small"
                plain
                :loading="auditActionLoading[item.id!]"
                @click="auditComment(item.id!, false)"
              >
                <el-icon><Close /></el-icon>
                拒绝违规
              </el-button>
              <el-button
                type="success"
                size="small"
                :loading="auditActionLoading[item.id!]"
                @click="auditComment(item.id!, true)"
              >
                <el-icon><Check /></el-icon>
                审核通过
              </el-button>
            </div>
          </div>
        </div>

        <!-- 友链审核列表 -->
        <div v-else-if="drawerType === 'link'" class="audit-list">
          <div v-for="item in pendingLinks" :key="item.id" class="audit-card link-audit-card">
            <div class="audit-card-head">
              <div class="link-author-info">
                <el-avatar :size="24" :src="item.avatar" class="link-avatar" />
                <span class="link-name-text">{{ item.name }}</span>
              </div>
              <span class="audit-time">{{ item.createTime }}</span>
            </div>
            <div class="audit-card-body">
              <p class="link-url-text">
                <b>URL:</b>
                <a :href="item.url" target="_blank">{{ item.url }}</a>
              </p>
              <p class="link-desc-text">
                <b>描述:</b>
                {{ item.description || "暂无描述" }}
              </p>
            </div>
            <div class="audit-card-foot">
              <el-button
                type="danger"
                size="small"
                plain
                :loading="auditActionLoading[item.id!]"
                @click="auditLink(item.id!, false)"
              >
                <el-icon><Close /></el-icon>
                拒绝审核
              </el-button>
              <el-button
                type="success"
                size="small"
                :loading="auditActionLoading[item.id!]"
                @click="auditLink(item.id!, true)"
              >
                <el-icon><Check /></el-icon>
                同意上架
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  ChatDotRound,
  ChatLineRound,
  Document,
  Message,
  User,
  View,
  Check,
  Close,
} from "@element-plus/icons-vue";
import type { BlogStatsSummary, InteractionOverview, CommentItem, LinkItem } from "@/types/api";
import BlogStatsAPI from "@/api/blog/stats";
import InteractionAPI from "@/api/blog/interaction";
import CommentAPI from "@/api/blog/comment";
import LinkAPI from "@/api/blog/link";
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

// 审核抽屉的状态控制
const drawerVisible = ref(false);
const drawerType = ref<"comment" | "link">("comment");
const drawerLoading = ref(false);
const pendingComments = ref<CommentItem[]>([]);
const pendingLinks = ref<LinkItem[]>([]);

// 精准控制具体行的审核 Loading
const auditActionLoading = reactive<Record<number, boolean>>({});

const drawerTitle = computed(() =>
  drawerType.value === "comment" ? "待审核评论极速面板" : "友情链接入站申请审核"
);

const summaryCards = computed(() => [
  { key: "article", label: "文章总数", value: summary.value.articleCount, icon: Document },
  { key: "comment", label: "评论总数", value: summary.value.commentCount, icon: ChatDotRound },
  { key: "user", label: "用户总数", value: summary.value.userCount, icon: User },
  { key: "view", label: "累计访问", value: summary.value.viewCount, icon: View },
  { key: "treehole", label: "树洞条数", value: summary.value.treeholeCount, icon: ChatLineRound },
  {
    key: "treehole-msg",
    label: "树洞评论",
    value: summary.value.treeholeCommentCount,
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

// 激活原地侧滑工作台抽屉
function openAuditDrawer(type: "comment" | "link") {
  drawerType.value = type;
  drawerVisible.value = true;
  loadDrawerData();
}

function loadDrawerData() {
  drawerLoading.value = true;
  if (drawerType.value === "comment") {
    CommentAPI.getPage({ status: 0, page: 1, pageSize: 20 })
      .then((data) => {
        // 数据解包遵守 API 契约
        pendingComments.value = data?.items || [];
      })
      .catch(() => {
        ElMessage.error("获取待审核评论数据失败");
      })
      .finally(() => {
        drawerLoading.value = false;
      });
  } else {
    // 友链使用 changeStatus 的查询，待审核为 status = 0
    LinkAPI.getList({ status: 0 })
      .then((data) => {
        pendingLinks.value = data || [];
      })
      .catch(() => {
        ElMessage.error("获取待审核友情链接数据失败");
      })
      .finally(() => {
        drawerLoading.value = false;
      });
  }
}

// 评论快速过审/拒绝
function auditComment(id: number, approved: boolean) {
  auditActionLoading[id] = true;
  const targetStatus = approved ? 1 : 2;
  CommentAPI.update(id, { id, status: targetStatus })
    .then(() => {
      ElMessage.success(approved ? "评论审核已通过" : "评论已审核拒绝");
      // 局部移除
      pendingComments.value = pendingComments.value.filter((item) => item.id !== id);
      // 重新拉取主看盘待办数
      loadSummary();
    })
    .catch(() => {
      ElMessage.error("审核操作失败，请重试");
    })
    .finally(() => {
      auditActionLoading[id] = false;
    });
}

// 友链快速过审/下架
function auditLink(id: number, approved: boolean) {
  auditActionLoading[id] = true;
  const targetStatus = approved ? 1 : 2;
  LinkAPI.changeStatus({
    id,
    status: targetStatus,
    reason: approved ? "" : "管理后台原地面板拒绝上架",
  })
    .then(() => {
      ElMessage.success(approved ? "友情链接已审核通过上架" : "已拒绝该友链申请");
      pendingLinks.value = pendingLinks.value.filter((item) => item.id !== id);
      loadSummary();
    })
    .catch(() => {
      ElMessage.error("审核操作失败，请重试");
    })
    .finally(() => {
      auditActionLoading[id] = false;
    });
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
