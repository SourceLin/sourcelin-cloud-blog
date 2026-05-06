<template>
  <div class="site-card-wrapper">
    <SCard
      class="site-card"
      variant="lite"
      header-padding="none"
      content-padding="none"
      footer-padding="none"
    >
      <div class="site-layout">
        <div class="site-header">
          <div class="site-cover">
            <img
              :src="coverImage"
              alt="背景"
              @error="handleCoverError"
            >
            <div class="cover-overlay" />
          </div>
        </div>

        <div class="site-content">
          <div class="site-meta">
            <span class="site-meta-label">主页</span>
            <span class="site-meta-line" />
            <span class="site-meta-state">{{ loginStateLabel }}</span>
          </div>

          <div class="avatar-container">
            <div class="avatar-wrapper">
              <img
                :src="avatarImage"
                :alt="displayName"
                class="user-avatar"
                @error="handleAvatarError"
              >
            </div>
          </div>

          <div class="user-info">
            <div class="user-info-top">
              <div class="username">{{ displayName }}</div>
              <div class="user-role">{{ secondaryLabel }}</div>
            </div>
            <div class="user-desc" :title="displayDescription">
              {{ displayDescription }}
            </div>
          </div>

          <div class="user-strip-shell">
            <div class="user-strip">
              <div class="user-pill">
                <span class="pill-label">状态</span>
                <strong class="pill-value">{{ loginStateValue }}</strong>
              </div>
              <div class="user-pill">
                <span class="pill-label">入口</span>
                <strong class="pill-value">{{ actionHint }}</strong>
              </div>
              <div class="user-pill">
                <span class="pill-label">同步</span>
                <strong class="pill-value">{{ syncLabel }}</strong>
              </div>
            </div>
          </div>

          <div class="action-area">
            <SButton type="primary" variant="site" block @click="handlePrimaryAction">
              <template #icon>
                <SIcon :icon="primaryActionIcon" :size="18" />
              </template>
              {{ primaryActionLabel }}
            </SButton>
          </div>
        </div>
      </div>
    </SCard>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { FrontUserDetailVO } from '@/modules/user/api/user.api'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import homeCoverImg from '@/assets/images/backgrounds/home-bg.jpg'
import headerCoverImg from '@/assets/images/header/beijing.jpg'
import headerAvatarImg from '@/assets/images/header/avatar.jpg'

function asUserInfo(value: unknown): FrontUserDetailVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserDetailVO
  }
  return {}
}

function toString(value: unknown, fallback = ''): string {
  return typeof value === 'string' ? value : fallback
}

const userStore = useUserStore()
const router = useRouter()

const defaultCover = homeCoverImg
const coverImage = headerCoverImg
const defaultAvatar = headerAvatarImg

const currentUser = computed(() => asUserInfo(userStore.userInfo))
const isLoggedIn = computed(() => userStore.isLoggedIn)
const copyByState = computed(() =>
  isLoggedIn.value
    ? {
        metaState: '在线',
        identityHint: roleLabel.value || '当前账号',
        stateValue: '已登',
        actionValue: '主页',
        syncValue: '已同步',
        actionLabel: '进入个人页',
        descriptionFallback: '展示当前账号资料与个人入口。',
      }
    : {
        metaState: '未登录',
        identityHint: '快捷入口',
        stateValue: '未登',
        actionValue: '登录',
        syncValue: '待开启',
        actionLabel: '立即登录',
        descriptionFallback: '登录后可进入个人主页与账号功能。',
      },
)

const avatarImage = computed(() => {
  const avatar = toString(currentUser.value.avatar)
  return avatar || defaultAvatar
})

const displayName = computed(() => {
  if (!isLoggedIn.value) return '登录后查看主页'
  return toString(currentUser.value.nickName) || toString(currentUser.value.userName) || '当前用户'
})

const displayDescription = computed(() => {
  return toString(currentUser.value.introduction) || copyByState.value.descriptionFallback
})

const roleLabel = computed(() => userStore.roles[0] || '')
const secondaryLabel = computed(() => copyByState.value.identityHint)

const loginStateLabel = computed(() => copyByState.value.metaState)
const loginStateValue = computed(() => copyByState.value.stateValue)
const actionHint = computed(() => copyByState.value.actionValue)
const syncLabel = computed(() => copyByState.value.syncValue)
const primaryActionLabel = computed(() => copyByState.value.actionLabel)
const primaryActionIcon = computed(() => (isLoggedIn.value ? appIcons.user : appIcons.login))

async function ensureUserInfo() {
  if (!isLoggedIn.value || userStore.userInfo) {
    return
  }
  await userStore.getUserInfoAction()
}

function handlePrimaryAction() {
  router.push(isLoggedIn.value ? '/user' : '/login')
}

function handleCoverError(e: Event) {
  const target = e.target as HTMLImageElement
  target.src = defaultCover
}

function handleAvatarError(e: Event) {
  const target = e.target as HTMLImageElement
  target.src = defaultAvatar
}

onMounted(() => {
  void ensureUserInfo()
})
</script>

<style lang="scss" scoped>
@import '@/shared/styles/responsive';

.site-card-wrapper {
  width: 100%;
  --site-header-height: 166px;
  --site-avatar-size: 94px;
  --site-avatar-overlap: 50px;
}

.site-card {
  overflow: hidden;
  border-radius: calc(var(--border-radius-xl) + 4px);
}

.site-layout {
  position: relative;
  isolation: isolate;
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.site-layout::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 18% 0%, color-mix(in srgb, var(--primary-color) 5%, transparent), transparent 40%),
    radial-gradient(circle at 82% 14%, color-mix(in srgb, var(--secondary-color) 4%, transparent), transparent 34%);
  opacity: 0.72;
  pointer-events: none;
}

.site-header {
  width: 100%;
  height: var(--site-header-height);
  position: relative;
  overflow: hidden;
  border-radius: var(--glass-radius) var(--glass-radius) 0 0;
}

.site-cover {
  width: 100%;
  height: 100%;
  position: relative;
}

.site-cover img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 0.65s cubic-bezier(0.4, 0, 0.2, 1), filter 0.65s cubic-bezier(0.4, 0, 0.2, 1);
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, transparent 0%, color-mix(in srgb, var(--text-color-dark) 6%, transparent) 62%, color-mix(in srgb, var(--background-color-deep) 18%, transparent) 100%),
    linear-gradient(90deg, color-mix(in srgb, var(--primary-color) 5%, transparent), transparent 38%, color-mix(in srgb, var(--secondary-color) 5%, transparent));
  pointer-events: none;
}

.site-header::after {
  content: '';
  position: absolute;
  inset: auto 0 0;
  height: clamp(2.8rem, 4vw, 3.6rem);
  background:
    linear-gradient(180deg, transparent 0%, color-mix(in srgb, var(--glass-surface-strong) 68%, transparent) 72%, color-mix(in srgb, var(--glass-surface-strong) 88%, transparent) 100%);
  pointer-events: none;
  z-index: 1;
}

.site-card:hover .site-cover img {
  transform: scale(1.07);
  filter: saturate(1.08) contrast(1.03);
}

.site-content {
  position: relative;
  isolation: isolate;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding:
    var(--site-content-padding-top, 0)
    var(--site-content-padding-inline, clamp(1.25rem, 2vw, 1.6rem))
    var(--site-content-padding-bottom, clamp(1.25rem, 2vw, 1.55rem));
  margin-top: calc(-1 * var(--site-avatar-overlap));
  z-index: 1;
}

.site-content::before {
  content: '';
  position: absolute;
  inset: calc(var(--site-avatar-overlap) - 0.65rem) 0 0;
  border-radius: 1.3rem 1.3rem calc(var(--glass-radius) - 2px) calc(var(--glass-radius) - 2px);
  border: 1px solid var(--border-content-card);
  background: var(--surface-content-card);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-panel-subtle);
  pointer-events: none;
  z-index: -2;
}

.site-content::after {
  content: '';
  position: absolute;
  inset: calc(var(--site-avatar-overlap) - 0.65rem) 0 auto;
  height: 5rem;
  border-radius: 1.3rem 1.3rem 0 0;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
  z-index: -1;
}

.site-meta {
  display: flex;
  width: fit-content;
  align-items: center;
  gap: 0.45rem;
  margin-bottom: 0.95rem;
  padding: 0.36rem 0.62rem;
  border-radius: 999px;
  border: 1px solid var(--border-panel-chip);
  background: var(--surface-panel-chip-quiet);
  -webkit-backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-12) 70%, transparent);
}

.site-meta-label,
.site-meta-state {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.site-meta-line {
  width: 1.25rem;
  height: 1px;
  background: linear-gradient(90deg, color-mix(in srgb, var(--primary-color) 70%, transparent), transparent);
}

.avatar-container {
  position: relative;
  z-index: 2;
  margin-bottom: 1rem;
  align-self: flex-start;
  cursor: pointer;
  perspective: 960px;
}

.avatar-wrapper {
  width: var(--site-avatar-size);
  height: var(--site-avatar-size);
  position: relative;
  border-radius: var(--border-radius-full);
  padding: 4px;
  overflow: visible;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 20%, transparent), color-mix(in srgb, var(--secondary-color) 14%, transparent)),
    linear-gradient(180deg, color-mix(in srgb, var(--glass-surface-strong) 92%, transparent), color-mix(in srgb, var(--glass-surface-lite) 96%, transparent));
  background-image: var(--glass-noise);
  background-blend-mode: soft-light, normal;
  border: 1px solid color-mix(in srgb, var(--glass-border-strong) 82%, transparent);
  box-shadow: 0 10px 22px color-mix(in srgb, var(--background-color-deep) 9%, transparent);
  transition:
    transform 0.7s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.7s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
  will-change: transform, box-shadow;
}

.avatar-wrapper::after {
  content: '';
  position: absolute;
  inset: -7px;
  border-radius: inherit;
  border: 1px solid color-mix(in srgb, var(--primary-color) 24%, transparent);
  opacity: 0.55;
  transform: scale(0.94);
  transition:
    opacity 0.7s cubic-bezier(0.4, 0, 0.2, 1),
    transform 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  border-radius: var(--border-radius-full);
  transform-origin: center;
  backface-visibility: hidden;
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  will-change: transform;
}

.avatar-container:hover .avatar-wrapper,
.site-card-wrapper:hover .avatar-wrapper,
.site-card-wrapper:focus-within .avatar-wrapper {
  transform: translate3d(0, -2px, 0) scale(1.035);
  box-shadow: 0 18px 32px color-mix(in srgb, var(--background-color-deep) 14%, transparent);
}

.avatar-container:hover .avatar-wrapper::after,
.site-card-wrapper:hover .avatar-wrapper::after,
.site-card-wrapper:focus-within .avatar-wrapper::after {
  animation: avatar-ring-bloom 1.4s cubic-bezier(0.22, 1, 0.36, 1) infinite;
}

.avatar-container:hover .user-avatar,
.site-card-wrapper:hover .user-avatar,
.site-card-wrapper:focus-within .user-avatar {
  transform: rotate(360deg) scale(1.025);
  filter: saturate(1.08) contrast(1.03);
}

@keyframes avatar-ring-bloom {
  0% {
    opacity: 0.55;
    transform: scale(0.94);
  }

  48% {
    opacity: 1;
    transform: scale(1.1);
  }

  100% {
    opacity: 0.92;
    transform: scale(1.03);
  }
}

.user-info {
  width: 100%;
  text-align: left;
  display: grid;
  gap: 0.38rem;
  margin-bottom: 0.95rem;
}

.user-info-top {
  display: grid;
  gap: 0.28rem;
}

.username {
  margin: 0;
  color: var(--title-color);
  font-family: 'Georgia', 'Times New Roman', serif;
  font-size: clamp(1.3rem, 2.6vw, 1.62rem);
  font-weight: 600;
  line-height: 1.12;
}

.user-role {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  width: fit-content;
  padding: 0.25rem 0.55rem;
  border-radius: 999px;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--primary-color) 9%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--primary-color) 4%, transparent)
    );
  border: 1px solid color-mix(in srgb, var(--primary-color) 14%, var(--glass-border));
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--primary-color);
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-12) 80%, transparent);
}

.user-desc {
  max-width: 100%;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.87rem;
  font-weight: 500;
  line-height: 1.68;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.user-strip-shell {
  width: 100%;
  margin-bottom: 1rem;
  padding: 0.62rem;
  border-radius: 1.15rem;
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-inset);
  -webkit-backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  backdrop-filter: blur(var(--glass-nested-blur)) saturate(var(--glass-saturate));
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline);
}

.user-strip {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.55rem;
}

.user-pill {
  position: relative;
  display: grid;
  gap: 0.26rem;
  min-width: 0;
  min-height: 4.9rem;
  padding: 0.86rem 0.82rem;
  border-radius: 1rem;
  background: var(--surface-panel-chip-quiet);
  border: 1px solid var(--border-panel-chip);
  box-shadow: var(--highlight-panel-chip), var(--shadow-panel-inline);
  overflow: hidden;
  transition:
    transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-pill::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 42%;
  background: linear-gradient(180deg, color-mix(in srgb, var(--primary-color) 10%, transparent), transparent);
  pointer-events: none;
}

.user-pill:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--primary-color) 22%, var(--glass-border));
  box-shadow:
    0 10px 22px color-mix(in srgb, var(--background-color-deep) 10%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-22) 92%, transparent);
}

.pill-label {
  position: relative;
  z-index: 1;
  font-size: 0.66rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.pill-value {
  position: relative;
  z-index: 1;
  font-size: 0.84rem;
  font-weight: 700;
  color: var(--text-primary);
}

.action-area {
  width: 100%;
}

html[data-theme='dark'] .avatar-wrapper {
  background: color-mix(in srgb, var(--glass-surface) 88%, var(--bg-page));
}

@include sourcelin-down(md) {
  .site-card-wrapper {
    --site-header-height: 148px;
    --site-avatar-size: 84px;
    --site-avatar-overlap: 44px;
  }

  .site-content {
    padding:
      var(--site-content-padding-top, 0)
      var(--site-content-padding-inline, 1rem)
      var(--site-content-padding-bottom, 1rem);
  }

  .user-strip-shell {
    padding: 0.56rem;
  }

  .user-pill {
    min-height: 4.6rem;
    padding: 0.78rem 0.72rem;
  }

  .username {
    font-size: 1.34rem;
  }

  .user-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
