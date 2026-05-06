<template>
  <div class="profile-sidebar">
    <div class="sidebar-hero">
      <div class="sidebar-hero__topline">
        <span class="sidebar-hero__eyebrow">主页导航</span>
        <span class="sidebar-hero__status">已登录</span>
      </div>

      <div class="avatar-wrapper">
        <SUpload
          class="avatar-upload"
          accept="image/jpeg,image/png"
          :max="1"
          :show-file-list="false"
          :custom-request="handleAvatarUploadRequest"
        >
          <div class="avatar-uploader">
            <img :src="avatarUrl" class="avatar" alt="用户头像">
            <div class="avatar-mask">
              <span>更换头像</span>
            </div>
          </div>
        </SUpload>
      </div>

      <div class="sidebar-hero__copy">
        <h2 class="username">{{ displayName }}</h2>
        <p class="user-bio">{{ displayBio }}</p>
      </div>
    </div>

    <div class="menu-list">
      <template v-for="item in menuItems" :key="item.kind === 'tab' ? item.key : 'message-center'">
        <router-link
          v-if="item.kind === 'route'"
          :to="item.to"
          class="menu-item menu-item--route"
          active-class="active"
        >
          <span class="menu-icon-shell">
            <SIcon class="menu-icon" :icon="item.icon" :size="16" />
          </span>
          <span class="menu-copy">
            <span class="menu-label">{{ item.label }}</span>
            <span class="menu-description">{{ item.description }}</span>
          </span>
        </router-link>
        <button
          v-else
          type="button"
          class="menu-item"
          :class="{ active: activeTab === item.key }"
          @click="emit('tab-change', item.key)"
        >
          <span class="menu-icon-shell">
            <SIcon class="menu-icon" :icon="item.icon" :size="16" />
          </span>
          <span class="menu-copy">
            <span class="menu-label">{{ item.label }}</span>
            <span class="menu-description">{{ item.description }}</span>
          </span>
          <span
            v-if="item.badge !== null"
            class="menu-badge"
          >{{ item.badge }}</span>
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import type { UserCenterTab } from '@/modules/user/composables/useUserCenter'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SUpload from '@/shared/components/ui/SUpload.vue'
import { appIcons } from '@/shared/components/ui/icons'

type TabMenuRow = {
  kind: 'tab'
  key: UserCenterTab
  label: string
  description: string
  icon: Component
  badge: number | null
}

type RouteMenuRow = {
  kind: 'route'
  to: RouteLocationRaw
  label: string
  description: string
  icon: Component
}

const props = defineProps<{
  activeTab: UserCenterTab
  avatarUrl: string
  displayName: string
  displayBio: string
  stats: { articleCount: number; collectCount: number; followCount: number; fansCount: number }
}>()

const emit = defineEmits<{
  'avatar-change': [file: File | null]
  'tab-change': [tab: UserCenterTab]
}>()

const menuItems = computed((): (TabMenuRow | RouteMenuRow)[] => [
  { kind: 'tab', key: 'info', label: '基本资料', description: '修改昵称、联系方式和简介', icon: appIcons.user, badge: null },
  {
    kind: 'route',
    to: { name: 'MessageCenter' },
    label: '消息中心',
    description: '系统公告与后续互动提醒',
    icon: appIcons.notice
  },
  { kind: 'tab', key: 'articles', label: '我的博客', description: '查看和管理发布的文章', icon: appIcons.document, badge: props.stats.articleCount },
  { kind: 'tab', key: 'collects', label: '我的收藏', description: '查看收藏的内容', icon: appIcons.collect, badge: props.stats.collectCount },
  { kind: 'tab', key: 'follows', label: '我的关注', description: '查看你正在关注的人', icon: appIcons.add, badge: props.stats.followCount },
  { kind: 'tab', key: 'fans', label: '我的粉丝', description: '查看关注你的人', icon: appIcons.visitors, badge: props.stats.fansCount },
  { kind: 'tab', key: 'security', label: '账号安全', description: '修改密码和安全设置', icon: appIcons.password, badge: null }
])

interface UploadRequestOptions {
  file: {
    file?: File
  }
  onFinish: () => void
  onError: () => void
}

const handleAvatarUploadRequest = ({ file, onFinish, onError }: UploadRequestOptions) => {
  if (!file.file) {
    onError()
    return
  }
  emit('avatar-change', file.file)
  onFinish()
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.profile-sidebar {
  display: grid;
  gap: var(--page-block-gap);
}

.sidebar-hero {
  display: grid;
  justify-items: center;
  gap: 14px;
  padding: 22px 20px;
  text-align: center;
}

.sidebar-hero__topline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-sm);
  width: 100%;
}

.sidebar-hero__eyebrow {
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.sidebar-hero__status {
  min-height: 28px;
  padding: 0 10px;
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  letter-spacing: 0.04em;
}

.avatar-wrapper {
  position: relative;
}

.avatar-uploader {
  position: relative;
  inline-size: 116px;
  block-size: 116px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid var(--border-panel-badge-accent);
  box-shadow: var(--shadow-panel-soft);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.avatar-uploader:hover {
  transform: translateY(-2px) scale(1.02);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--shadow-panel-hover);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: color-mix(in srgb, var(--surface-black-50) 82%, transparent);
  color: var(--text-color-light);
  font-size: 0.76rem;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.avatar-uploader:hover .avatar-mask {
  opacity: 1;
}

.sidebar-hero__copy {
  display: grid;
  gap: 6px;
}

.username {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 700;
  color: var(--title-color);
}

.user-bio {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.65;
}

.menu-list {
  display: grid;
  gap: 10px;
}

.menu-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  width: 100%;
  min-height: 58px;
  padding: 12px 14px;
  border: 1px solid var(--border-panel-subtle);
  border-radius: 14px;
  cursor: pointer;
  color: var(--text-primary);
  text-align: left;
  background: transparent;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    color var(--transition-base),
    box-shadow var(--transition-base);
}

.menu-item:hover {
  transform: translateY(-1px);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--shadow-panel-soft);
}

.menu-item.active {
  color: var(--primary-color);
  border-color: var(--border-panel-badge-accent);
  background: var(--surface-panel-chip-accent);
}

.menu-icon-shell {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}

.menu-copy {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.menu-label {
  font-size: 0.94rem;
  font-weight: 700;
  color: currentColor;
}

.menu-description {
  font-size: 0.76rem;
  line-height: 1.45;
  color: var(--text-secondary);
}

.menu-item.active .menu-description {
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.menu-item--route {
  box-sizing: border-box;
}

.menu-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  min-width: 1.75rem;
  padding: 0 10px;
  font-size: var(--font-size-sm, 12px);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  line-height: 1;
}
</style>
