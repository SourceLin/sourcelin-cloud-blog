<template>
  <section class="dashboard-hero">
    <div class="dashboard-hero__copy">
      <div class="dashboard-hero__intro">
        <img
          v-if="userInfo.avatar"
          :src="`${userInfo.avatar}?imageView2/1/w/72/h/72`"
          alt="avatar"
          class="dashboard-hero__avatar"
        />
        <div class="dashboard-hero__intro-text">
          <div class="dashboard-chip">首页概览</div>
          <div class="dashboard-hero__identity">
            {{ userInfo.nickName || "管理员" }}
          </div>
          <div class="dashboard-hero__date">{{ todayLabel }} · 内容运营工作台</div>
        </div>
      </div>

      <h1 class="dashboard-hero__title">{{ greetings }}</h1>
      <p class="dashboard-hero__desc">
        欢迎使用圆圈博客后台，访问趋势、常用入口和核心状态都集中在这里。
      </p>

      <div class="dashboard-hero__meta">
        <span class="dashboard-meta-pill">{{ todayLabel }}</span>
        <span class="dashboard-meta-pill">最近访问 {{ recentMenusCount }} 项</span>
        <span class="dashboard-meta-pill">通知 {{ unreadNoticeHint }}</span>
      </div>

      <div class="dashboard-hero__actions">
        <el-button type="primary" @click="emit('profile')">个人中心</el-button>
        <el-button @click="emit('notice')">我的通知</el-button>
      </div>
    </div>

    <div class="dashboard-hero__metrics">
      <article class="dashboard-hero-card">
        <div class="dashboard-hero-card__label">通知概览</div>
        <div class="dashboard-hero-card__value">{{ unreadNoticeHint }}</div>
        <div class="dashboard-hero-card__foot">
          <span>消息中心</span>
          <span>{{ todayLabel }}</span>
        </div>
      </article>

      <article class="dashboard-hero-card">
        <div class="dashboard-hero-card__label">今日访客</div>
        <div class="dashboard-hero-card__value">{{ displayTransitionUvCount }}</div>
        <div class="dashboard-hero-card__foot" :class="uvGrowthClass">
          <span>{{ uvGrowthLabel }}</span>
          <span>累计 {{ displayTransitionTotalUvCount }}</span>
        </div>
      </article>

      <article class="dashboard-hero-card dashboard-hero-card--wide">
        <div class="dashboard-hero-card__label">今日浏览</div>
        <div class="dashboard-hero-card__value">{{ displayTransitionPvCount }}</div>
        <div class="dashboard-hero-card__foot" :class="pvGrowthClass">
          <span>{{ pvGrowthLabel }}</span>
          <span>累计 {{ displayTransitionTotalPvCount }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  userInfo: Record<string, any>;
  todayLabel: string;
  greetings: string;
  recentMenusCount: number;
  unreadNoticeHint: string;
  displayTransitionUvCount: number;
  displayTransitionTotalUvCount: number;
  displayTransitionPvCount: number;
  displayTransitionTotalPvCount: number;
  uvGrowthLabel: string;
  pvGrowthLabel: string;
  uvGrowthClass: string;
  pvGrowthClass: string;
}>();

const emit = defineEmits<{
  profile: [];
  notice: [];
}>();
</script>
