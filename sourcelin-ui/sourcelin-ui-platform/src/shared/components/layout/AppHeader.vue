<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import localLogo from '@/assets/images/logo/logo.png'
import { appRoutes } from '@/app/router'
import { collectHeaderNavigation } from '@/app/router/route-meta'
import { useSiteStore } from '@/stores/site.store'
import { useThemeStore } from '@/stores/theme'
import { useUiStore } from '@/stores/ui.store'
import { useMessageInboxBadgeStore } from '@/stores/message-inbox-badge.store'
import { useUserStore } from '@/stores/user'
import SBadge from '@/shared/components/ui/SBadge.vue'
import { appIcons } from '@/shared/components/ui/icons'
import { isMessageCenterNavEntry } from '@/shared/utils/message-center-nav'

interface HeaderNavItem {
  id: string
  label: string
  path: string
  summary?: string
  isExternal: boolean
  children?: HeaderNavItem[]
}

const router = useRouter()
const route = useRoute()
const siteStore = useSiteStore()
const uiStore = useUiStore()
const userStore = useUserStore()
const messageInboxBadgeStore = useMessageInboxBadgeStore()
const themeStore = useThemeStore()
const { siteInfo, navbarItems } = storeToRefs(siteStore)
const { totalCount: messageInboxCount } = storeToRefs(messageInboxBadgeStore)

const routeFallbackNavigation = collectHeaderNavigation(appRoutes).map((item) => ({
  id: item.path,
  label: item.label,
  path: item.path,
  isExternal: false,
  children: item.children?.map((child) => ({
    id: child.path,
    label: child.label,
    path: child.path,
    isExternal: false
  }))
}))

function filterMessageCenterFromHeaderNav(items: HeaderNavItem[]): HeaderNavItem[] {
  return items
    .filter((item) => !isMessageCenterNavEntry(item.path, item.label))
    .map((item) => {
      const children = item.children
        ?.filter((child) => !isMessageCenterNavEntry(child.path, child.label))
        .map((child) => ({ ...child }))
      return {
        ...item,
        children: children?.length ? children : undefined
      }
    })
}

const scrollProgress = ref(0)
const isScrolled = ref(false)
const isHidden = ref(false)
const lastScrollTop = ref(0)
const activeDropdown = ref<string | null>(null)
const dropdownCloseTimer = ref<ReturnType<typeof window.setTimeout> | null>(null)
const isMobile = ref(false)
const showActions = ref(true)
const scrollRafId = ref(0)

const theme = computed(() => (themeStore.isDark ? 'dark' : 'light'))
const userInfo = computed(() => userStore.userInfo as { avatar?: string; nickName?: string; userName?: string; email?: string } | null)
const webInfo = computed(() => siteInfo.value)
const siteName = computed(() => webInfo.value.siteName || webInfo.value.webName || '圆圈博客')
const siteLogo = computed(() => webInfo.value.logo || localLogo)

const primaryNavigation = computed<HeaderNavItem[]>(() => {
  const raw: HeaderNavItem[] = !navbarItems.value.length
    ? routeFallbackNavigation
    : navbarItems.value.map((item) => ({
        id: String(item.id),
        label: item.name,
        path: item.path,
        summary: item.summary,
        isExternal: item.isFrame === '0',
        children: item.children?.map((child) => ({
          id: String(child.id),
          label: child.name,
          path: child.path,
          summary: child.summary,
          isExternal: child.isFrame === '0'
        }))
      }))
  return filterMessageCenterFromHeaderNav(raw)
})

const headerIcons = {
  menu: appIcons.menu,
  chevronDown: appIcons.chevronDown,
  search: appIcons.search,
  themeLight: appIcons.themeLight,
  themeDark: appIcons.themeDark,
  create: appIcons.create,
  user: appIcons.user
}

function isActive(path: string | string[]): boolean {
  if (Array.isArray(path)) {
    return path.some((item) => route.path === item || route.path.startsWith(`${item}/`))
  }
  if (!path || /^https?:\/\//.test(path)) {
    return false
  }
  return route.path === path || route.path.startsWith(`${path}/`)
}

function getNavigationPaths(item: HeaderNavItem) {
  if (!item.children?.length) {
    return item.path
  }
  return [item.path, ...item.children.filter((child) => !child.isExternal).map((child) => child.path)]
}

function checkWindowSize() {
  const width = window.innerWidth
  isMobile.value = width < 1024
  showActions.value = !isMobile.value
}

function handleScroll() {
  if (scrollRafId.value) {
    return
  }

  scrollRafId.value = requestAnimationFrame(() => {
    scrollRafId.value = 0
    const scrollTop = Math.max(window.pageYOffset || document.documentElement.scrollTop, 0)
    const docHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight

    scrollProgress.value = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0
    isScrolled.value = scrollTop > 50
    isHidden.value = scrollTop > lastScrollTop.value && scrollTop > 200
    lastScrollTop.value = scrollTop
  })
}

function handleOpenSearchDialog() {
  uiStore.setSearchDialogVisible(true)
}

function handleLogin() {
  void router.push('/login')
}

function logout() {
  userStore.logout()
}

function openDrawer() {
  uiStore.setDrawerVisible(true)
}

function toggleTheme() {
  themeStore.toggle()
}

function openSayPublish() {
  if (!userStore.isLoggedIn) {
    uiStore.setSayPublishVisible(false)
    void router.push('/login')
    return
  }
  uiStore.setSayPublishVisible(true)
  void router.push('/say')
}

function handleMobileUserAction() {
  if (userInfo.value) {
    void router.push('/user')
    return
  }
  void router.push('/login')
}

function handleNavItemClick(item: HeaderNavItem) {
  activeDropdown.value = null
  if (item.isExternal) {
    window.open(item.path, '_blank', 'noopener,noreferrer')
    return
  }
  if (item.path) {
    void router.push(item.path)
  }
}

function clearDropdownCloseTimer() {
  if (dropdownCloseTimer.value !== null) {
    window.clearTimeout(dropdownCloseTimer.value)
    dropdownCloseTimer.value = null
  }
}

function openDropdown(id: string | null) {
  clearDropdownCloseTimer()
  activeDropdown.value = id
}

function scheduleDropdownClose() {
  clearDropdownCloseTimer()
  dropdownCloseTimer.value = window.setTimeout(() => {
    activeDropdown.value = null
    dropdownCloseTimer.value = null
  }, 180)
}

onMounted(() => {
  checkWindowSize()
  window.addEventListener('resize', checkWindowSize)
  window.addEventListener('scroll', handleScroll, { passive: true })
  void siteStore.loadNavbars().catch(() => undefined)
  void messageInboxBadgeStore.refresh()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkWindowSize)
  window.removeEventListener('scroll', handleScroll)
  if (scrollRafId.value) {
    cancelAnimationFrame(scrollRafId.value)
  }
  clearDropdownCloseTimer()
})

watch(
  () => route.path,
  () => {
    activeDropdown.value = null
  }
)

watch(
  () => userStore.isLoggedIn,
  () => {
    void messageInboxBadgeStore.refresh()
  }
)
</script>

<template>
  <header :class="['glass-header', { scrolled: isScrolled, hidden: isHidden }]">
    <div class="progress-bar" :style="{ width: `${scrollProgress}%` }" />

    <nav class="nav-container">
      <div class="nav-left">
        <div class="nav-logo">
          <router-link to="/" class="logo-link">
            <img :src="siteLogo" alt="站点 Logo" class="logo-img">
            <span class="logo-text">{{ siteName }}</span>
          </router-link>
        </div>

        <button v-if="isMobile" class="mobile-menu-btn" type="button" @click="openDrawer">
          <SIcon class="menu-icon" :icon="headerIcons.menu" :size="22" />
        </button>
      </div>

      <ul v-if="!isMobile" class="nav-menu">
        <li
          v-for="item in primaryNavigation"
          :key="item.id"
          :class="[{ 'has-dropdown': Boolean(item.children?.length) }, { active: isActive(getNavigationPaths(item)) }]"
          @mouseenter="openDropdown(item.children?.length ? item.id : null)"
          @mouseleave="scheduleDropdownClose"
        >
          <button v-if="!item.children?.length" class="nav-link-btn" type="button" @click="handleNavItemClick(item)">
            <span>{{ item.label }}</span>
          </button>
          <span v-else class="dropdown-trigger">
            <span>{{ item.label }}</span>
            <SIcon class="arrow-icon" :class="{ open: activeDropdown === item.id }" :icon="headerIcons.chevronDown" :size="14" />
          </span>
          <transition v-if="item.children?.length" name="dropdown-fade">
            <div v-show="activeDropdown === item.id" class="dropdown-menu">
              <button
                v-for="child in item.children"
                :key="child.id"
                type="button"
                class="dropdown-item dropdown-button"
                @click="handleNavItemClick(child)"
              >
                {{ child.label }}
              </button>
            </div>
          </transition>
        </li>
      </ul>

      <div class="nav-actions">
        <button v-if="!isMobile" class="action-btn search-btn" type="button" title="搜索" @click="handleOpenSearchDialog">
          <SIcon :icon="headerIcons.search" :size="20" />
        </button>

        <button
          class="action-btn theme-btn"
          type="button"
          :title="theme === 'light' ? '切换到深色主题' : '切换到浅色主题'"
          @click="toggleTheme"
        >
          <transition name="theme-icon" mode="out-in">
            <SIcon v-if="theme === 'light'" key="sun" :icon="headerIcons.themeLight" :size="20" />
            <SIcon v-else key="moon" :icon="headerIcons.themeDark" :size="20" />
          </transition>
        </button>

        <div
          v-if="showActions && userInfo"
          class="create-dropdown"
          @mouseenter="openDropdown('create')"
          @mouseleave="scheduleDropdownClose"
        >
          <button class="create-btn" type="button">
            <SIcon :icon="headerIcons.create" :size="18" />
            <span>创作</span>
          </button>
          <transition name="dropdown-fade">
            <div v-show="activeDropdown === 'create'" class="dropdown-menu">
              <router-link to="/posts/new" class="dropdown-item">写文章</router-link>
              <router-link to="/say" class="dropdown-item" @click.prevent="openSayPublish">发说说</router-link>
            </div>
          </transition>
        </div>

        <div
          v-if="showActions"
          class="user-dropdown"
          @mouseenter="openDropdown('user')"
          @mouseleave="scheduleDropdownClose"
        >
          <SBadge
            v-if="userInfo"
            class="user-dropdown__avatar-badge"
            compact
            :value="messageInboxCount"
            :max="99"
            type="error"
            :show="messageInboxCount > 0"
            :offset="[-5, 5]"
          >
            <button class="avatar-btn" type="button">
              <span class="avatar-btn__graphic">
                <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="用户头像" class="avatar-img">
                <div v-else class="avatar-placeholder">
                  <SIcon :icon="headerIcons.user" :size="22" />
                </div>
              </span>
            </button>
          </SBadge>
          <button v-else class="avatar-btn" type="button">
            <div class="avatar-placeholder">
              <SIcon :icon="headerIcons.user" :size="22" />
            </div>
          </button>
          <transition name="dropdown-fade">
            <div v-show="activeDropdown === 'user'" class="dropdown-menu user-menu">
              <template v-if="userInfo">
                <div class="user-info-card">
                  <SAvatar :size="50" :src="userInfo.avatar" round />
                  <div class="info-content">
                    <p class="info-name">{{ userInfo.nickName || userInfo.userName }}</p>
                    <p class="info-email">{{ userInfo.email || '未绑定邮箱' }}</p>
                  </div>
                </div>
                <div class="menu-divider" />
                <router-link to="/user" class="dropdown-item" @click="activeDropdown = null">个人主页</router-link>
                <router-link
                  to="/messages"
                  class="dropdown-item dropdown-item--message"
                  @click="activeDropdown = null"
                >
                  <span class="dropdown-item__label">消息通知</span>
                  <span class="dropdown-item__badge-host">
                    <SBadge
                      :value="messageInboxCount"
                      :max="99"
                      type="error"
                      :show="messageInboxCount > 0"
                      :offset="[0, 8]"
                    >
                      <span class="s-badge__inline-anchor" aria-hidden="true" />
                    </SBadge>
                  </span>
                </router-link>
                <button type="button" class="dropdown-item dropdown-button" @click="logout">退出登录</button>
              </template>
              <template v-else>
                <div class="login-prompt">
                  <p class="prompt-title">登录后可解锁更多功能</p>
                  <button class="login-btn" type="button" @click="handleLogin">登录</button>
                  <router-link to="/register" class="register-link-btn">注册</router-link>
                </div>
              </template>
            </div>
          </transition>
        </div>

        <button v-if="isMobile" class="action-btn mobile-user-btn" type="button" @click="handleMobileUserAction">
          <img v-if="userInfo && userInfo.avatar" :src="userInfo.avatar" alt="用户头像" class="mobile-avatar">
          <SIcon v-else :icon="headerIcons.user" :size="20" />
        </button>

        <button v-if="isMobile" class="action-btn mobile-search-btn" type="button" @click="handleOpenSearchDialog">
          <SIcon :icon="headerIcons.search" :size="20" />
        </button>
      </div>
    </nav>
  </header>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.glass-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 72px;
  background: color-mix(in srgb, var(--layout-surface-strong) 88%, transparent);
  background-image: var(--glass-noise);
  background-blend-mode: soft-light;
  border-bottom: 1px solid color-mix(in srgb, var(--glass-stroke) 90%, transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-soft);
  backdrop-filter: blur(calc(var(--glass-blur) - 4px)) saturate(var(--glass-saturate));
  transition:
    transform var(--transition-base),
    height var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);
  overflow: visible;
}

.glass-header.scrolled {
  height: 62px;
  background: color-mix(in srgb, var(--layout-surface-strong) 94%, transparent);
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-default);
}

.glass-header.hidden {
  transform: translateY(-100%);
}

.progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color), var(--accent-color));
  border-radius: 0 3px 3px 0;
  transition: width 0.1s linear;
  z-index: 10;
}

.nav-container {
  max-width: 1440px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.nav-logo {
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  transition: transform 0.2s;
}

.logo-link:hover {
  transform: scale(1.02);
}

.logo-img {
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius-md);
  object-fit: cover;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
}

.nav-menu > li {
  position: relative;
}

.nav-menu > li > .nav-link-btn,
.nav-menu > li > .dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 15px;
  border-radius: 999px;
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-color);
  text-decoration: none;
  cursor: pointer;
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);
  background: color-mix(in srgb, var(--surface-white-10) 30%, transparent);
  border: 1px solid transparent;
}

.nav-menu > li > .nav-link-btn:hover,
.nav-menu > li > .dropdown-trigger:hover {
  background: var(--toolbar-button-surface-hover);
  color: var(--primary-color);
  box-shadow: var(--shadow-panel-inline);
}

.nav-menu > li.active > .nav-link-btn,
.nav-menu > li.active > .dropdown-trigger {
  background: color-mix(in srgb, var(--primary-color) 12%, var(--surface-panel-chip-accent));
  color: var(--primary-color);
  font-weight: 600;
  border-color: color-mix(in srgb, var(--primary-color) 18%, transparent);
  box-shadow: var(--button-glow);
}

.nav-link-btn {
  font: inherit;
}

.arrow-icon {
  transition: transform 0.2s;
  opacity: 0.6;
}

.arrow-icon.open {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  min-width: 180px;
  margin-top: 8px;
  padding: 10px;
  background: var(--dropdown-surface);
  background-image: var(--glass-noise);
  background-blend-mode: soft-light;
  border: none;
  outline: var(--glass-panel-outline);
  border-radius: 18px;
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-default);
  backdrop-filter: blur(calc(var(--glass-blur) - 2px)) saturate(var(--glass-saturate));
  z-index: 100;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: 100%;
    height: 8px;
    background: transparent;
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
  color: var(--text-color);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
}

.dropdown-button {
  width: 100%;
  border: none;
  background: transparent;
  text-align: left;
  font: inherit;
}

.dropdown-item:hover {
  background: var(--toolbar-button-surface-hover);
  color: var(--primary-color);
  transform: translateX(2px);
  box-shadow: var(--shadow-panel-inline);
}

.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-fade-enter,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  padding: 0;
  background: var(--toolbar-button-surface);
  border: 1px solid color-mix(in srgb, var(--glass-stroke) 88%, transparent);
  border-radius: var(--border-radius-md);
  color: var(--toolbar-button-text);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base),
    border-color var(--transition-base);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-inline);
}

.action-btn:hover {
  background: var(--toolbar-button-surface-hover);
  color: var(--toolbar-button-text-hover);
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--highlight-panel-default),
    var(--shadow-panel-soft);
}

.theme-btn {
  color: var(--primary-color);
}

.theme-icon-enter-active,
.theme-icon-leave-active {
  transition: all 0.3s;
}

.theme-icon-enter {
  opacity: 0;
  transform: rotate(-90deg) scale(0.5);
}

.theme-icon-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.5);
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border: none;
  border-radius: var(--border-radius-lg);
  color: var(--text-color-light);
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--button-glow);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--button-glow-strong);
}

.create-dropdown,
.user-dropdown {
  position: relative;
}

.user-dropdown__avatar-badge {
  display: inline-flex;
  vertical-align: middle;
}

.avatar-btn__graphic {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  vertical-align: middle;
}

.dropdown-item--message {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.dropdown-item__badge-host {
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: flex-end;
  min-width: 1.5rem;
}

.s-badge__inline-anchor {
  display: inline-block;
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.avatar-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
}

.avatar-btn:hover {
  transform: scale(1.05);
}

.avatar-img,
.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid color-mix(in srgb, var(--primary-color) 20%, transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-inline);
}

.avatar-placeholder {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 12%, transparent), color-mix(in srgb, var(--secondary-color) 14%, transparent));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
}

.mobile-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.user-menu {
  right: 0;
  left: auto;
  transform: none;
  min-width: 220px;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-color);
  margin: 0;
}

.info-email {
  font-size: 0.8rem;
  color: var(--text-color-muted);
  margin: 0;
}

.menu-divider {
  height: 1px;
  margin: 8px 0;
  background: color-mix(in srgb, var(--primary-color) 16%, transparent);
}

.login-prompt {
  padding: 16px;
  text-align: center;
}

.prompt-title {
  margin: 0 0 12px;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-color);
}

.login-btn,
.register-link-btn {
  width: 100%;
  padding: 10px 20px;
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
  text-decoration: none;
}

.login-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border: none;
  color: var(--text-color-light);
  margin-bottom: 10px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-color-rgb), 0.3);
}

.register-link-btn {
  display: block;
  background: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
}

.register-link-btn:hover {
  background: var(--primary-light);
}

.mobile-menu-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  padding: 0;
  border-radius: var(--border-radius-md);
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-color);
}

.menu-icon {
  color: var(--toolbar-button-text);
}

@media (max-width: 1024px) {
  .nav-menu {
    display: none;
  }

  .nav-container {
    padding: 0 16px;
  }
}

@include sourcelin-down(md) {
  .glass-header {
    height: 60px;
  }

  .nav-actions {
    gap: 4px;
  }

  .create-dropdown {
    display: none;
  }

  .logo-text {
    font-size: 1.1rem;
  }

  .logo-img {
    width: 36px;
    height: 36px;
  }
}
</style>
