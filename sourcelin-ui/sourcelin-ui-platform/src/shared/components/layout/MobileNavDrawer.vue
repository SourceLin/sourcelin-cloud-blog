<template>
  <SDrawer
    v-model:show="drawer"
    :width="320"
    placement="left"
    :trap-focus="true"
    @update:show="uiStore.setDrawerVisible($event)"
  >
    <div class="drawer-shell">
      <div class="drawer-top">
        <router-link to="/" class="brand" @click="close">
          <img :src="siteLogo()" alt="站点 Logo" class="brand-logo">
          <span class="brand-name">{{ siteName() }}</span>
        </router-link>

        <button class="close-btn" type="button" @click="close">
          <SIcon :icon="appIcons.close" :size="18" />
        </button>
      </div>

      <div class="drawer-main">
        <div class="menu-section">
          <a
            v-for="item in mobileNavigation"
            :key="item.id"
            class="menu-item"
            :class="{ active: isActive(item.path) }"
            @click="handleClick(item)"
          >
            <SIcon :icon="resolveNavIcon(item.icon)" :size="18" />
            <span>{{ item.label }}</span>
          </a>
        </div>

        <div class="divider" />

        <div class="menu-section">
          <a v-if="!isLogin" class="menu-item primary" @click="openLogin">
            <SIcon :icon="appIcons.login" :size="18" />
            <span>登录</span>
          </a>
          <template v-else>
            <a class="menu-item" :class="{ active: isActive('/user') }" @click="handleRoute('/user')">
              <SIcon :icon="appIcons.user" :size="18" />
              <span>个人主页</span>
            </a>
            <a
              class="menu-item"
              :class="{ active: isActive('/messages') }"
              @click="handleRoute('/messages')"
            >
              <SIcon :icon="appIcons.notice" :size="18" />
              <span>消息通知</span>
              <span v-if="messageInboxCount > 0" class="menu-item__count" role="status">{{ messageInboxCountLabel }}</span>
            </a>
            <a class="menu-item danger" @click="logout">
              <SIcon :icon="appIcons.logout" :size="18" />
              <span>退出登录</span>
            </a>
          </template>
        </div>
      </div>
    </div>
  </SDrawer>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRoute, useRouter } from 'vue-router'
import localLogo from '@/assets/images/logo/logo.png'
import { appRoutes } from '@/app/router'
import { collectMobileNavigation } from '@/app/router/route-meta'
import { useMessageInboxBadgeStore } from '@/stores/message-inbox-badge.store'
import { useSiteStore } from '@/stores/site.store'
import { useUiStore } from '@/stores/ui.store'
import { useUserStore } from '@/stores/user'
import { isMessageCenterNavEntry } from '@/shared/utils/message-center-nav'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons, type AppIconName } from '@/shared/components/ui/icons'

interface MobileNavItem {
  id: string
  label: string
  path: string
  icon: string
  isExternal: boolean
}

const router = useRouter()
const route = useRoute()
const siteStore = useSiteStore()
const uiStore = useUiStore()
const userStore = useUserStore()
const messageInboxBadgeStore = useMessageInboxBadgeStore()
const { totalCount: messageInboxCount } = storeToRefs(messageInboxBadgeStore)

const messageInboxCountLabel = computed(() => {
  const n = messageInboxCount.value
  if (n <= 0) {
    return ''
  }
  return n > 99 ? '99+' : String(n)
})

const routeFallbackNavigation = collectMobileNavigation(appRoutes).map((item) => ({
  id: item.path,
  label: item.label,
  path: item.path,
  icon: item.icon,
  isExternal: false
}))

const mobileNavigation = computed<MobileNavItem[]>(() => {
  if (!siteStore.navbarItems.length) {
    return routeFallbackNavigation.filter((row) => !isMessageCenterNavEntry(row.path, row.label))
  }
  const rows: MobileNavItem[] = []
  siteStore.navbarItems.forEach((item) => {
    const targetRows = item.children?.length ? item.children : [item]
    targetRows.forEach((target) => {
      if (isMessageCenterNavEntry(target.path, target.name)) {
        return
      }
      rows.push({
        id: String(target.id),
        label: target.name,
        path: target.path,
        icon: target.icon || item.icon || 'grid',
        isExternal: target.isFrame === '0'
      })
    })
  })
  return rows
})

const webInfo = () => siteStore.siteInfo
const siteName = () => webInfo().siteName || webInfo().webName || '圆圈博客'
const siteLogo = () => webInfo().logo || localLogo
const authorAvatar = () => webInfo().avatar || siteLogo()
const authorName = () => webInfo().author || siteName()
const authorDesc = () => {
  const title = webInfo().webTitle

  if (Array.isArray(title) && title.length > 0) {
    return title[0]
  }

  if (typeof title === 'string' && title) {
    return title
  }

  return webInfo().webName || '欢迎来到博客'
}

const drawer = computed({
  get: () => uiStore.drawerVisible,
  set: (value: boolean) => uiStore.setDrawerVisible(value)
})

const isLogin = computed(() => Boolean(userStore.token))

function resolveNavIcon(icon: string) {
  return appIcons[icon as AppIconName] || appIcons.grid
}

function isActive(path: string) {
  if (!path || /^https?:\/\//.test(path)) {
    return false
  }
  return route.path === path || route.path.startsWith(`${path}/`)
}

function handleRoute(path: string) {
  uiStore.setDrawerVisible(false)
  void router.push(path)
}

function handleClick(item: MobileNavItem) {
  uiStore.setDrawerVisible(false)
  if (item.isExternal) {
    window.open(item.path, '_blank', 'noopener,noreferrer')
    return
  }
  void router.push(item.path)
}

function close() {
  uiStore.setDrawerVisible(false)
}

function openLogin() {
  uiStore.setDrawerVisible(false)
  void router.push('/login')
}

function logout() {
  uiStore.setDrawerVisible(false)
  if (route.path === '/user') {
    router.go(-1)
  }
  userStore.logout()
}

/** 与 AppHeader `isMobile`（宽度 < 1024）一致：进入桌面宽度时收起侧栏 */
const HEADER_MOBILE_BREAKPOINT_PX = 1024

function closeDrawerWhenViewportDesktop() {
  if (typeof window === 'undefined') {
    return
  }
  if (window.innerWidth >= HEADER_MOBILE_BREAKPOINT_PX && uiStore.drawerVisible) {
    uiStore.setDrawerVisible(false)
  }
}

onMounted(() => {
  void siteStore.loadNavbars().catch(() => undefined)
  void messageInboxBadgeStore.refresh()
  window.addEventListener('resize', closeDrawerWhenViewportDesktop, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('resize', closeDrawerWhenViewportDesktop)
})
</script>

<style scoped lang="scss">
.drawer-shell {
  --drawer-shell-radius: calc(var(--border-radius-xl) + 6px);
  --drawer-item-radius: calc(var(--border-radius-lg) + 2px);
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background:
    var(--surface-panel-specular-soft),
    var(--surface-content-card);
  background-repeat: no-repeat;
  border-radius: 0 var(--drawer-shell-radius) var(--drawer-shell-radius) 0;
  border: 1px solid var(--border-content-card);
  box-shadow:
    var(--highlight-content-card),
    var(--shadow-content-card);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
}

.drawer-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 2px 0 10px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  text-decoration: none;
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-md);
  object-fit: cover;
  border: 1px solid color-mix(in srgb, var(--border-content-card) 92%, transparent);
  box-shadow: var(--highlight-panel-chip), var(--shadow-panel-inline);
}

.brand-name {
  color: var(--text-color);
  font-size: 1.05rem;
  font-weight: 800;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.close-btn {
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-content-card);
  border-radius: var(--drawer-item-radius);
  background:
    linear-gradient(
      145deg,
      color-mix(in srgb, var(--primary-color) 6%, transparent),
      transparent 58%
    ),
    var(--surface-content-card);
  background-repeat: no-repeat;
  color: var(--text-color);
  box-shadow: var(--highlight-content-card), var(--shadow-panel-inline);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter);
  backdrop-filter: var(--content-card-backdrop-filter);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.close-btn:hover {
  color: var(--primary-color);
  border-color: var(--border-interactive-hover);
  background: var(--surface-content-card-featured);
  background-repeat: no-repeat;
  box-shadow: var(--highlight-content-card), var(--shadow-content-card-hover);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.close-btn:active {
  transform: scale(0.96);
}

.drawer-main {
  flex: 1;
  overflow: auto;
  padding-right: 4px;
}

.menu-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid transparent;
  border-radius: var(--drawer-item-radius);
  color: var(--text-color);
  text-decoration: none;
  cursor: pointer;
  background:
    linear-gradient(
      145deg,
      color-mix(in srgb, var(--surface-white-07) 72%, transparent),
      transparent 58%
    ),
    color-mix(in srgb, var(--surface-panel-chip-quiet) 92%, transparent);
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.menu-item:hover,
.menu-item.active {
  border-color: var(--border-interactive-hover);
  background: var(--surface-content-card-featured);
  background-repeat: no-repeat;
  color: var(--primary-color);
  box-shadow: var(--highlight-content-card), var(--shadow-panel-inline);
  -webkit-backdrop-filter: var(--content-card-backdrop-filter-hover);
  backdrop-filter: var(--content-card-backdrop-filter-hover);
}

.menu-item.primary {
  border-color: color-mix(in srgb, var(--primary-color) 18%, var(--border-content-card));
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 14%, transparent), transparent),
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--primary-color) 10%, var(--surface-panel-chip-accent)),
      var(--surface-panel-chip-accent)
    );
  color: var(--primary-active);
  box-shadow:
    var(--highlight-panel-chip),
    var(--shadow-panel-default),
    0 0 0 1px color-mix(in srgb, var(--primary-color) 10%, transparent);
}

.menu-item.primary:hover {
  border-color: color-mix(in srgb, var(--primary-color) 28%, var(--border-interactive-hover));
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 16%, transparent), transparent),
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--primary-color) 14%, var(--surface-panel-chip-accent)),
      color-mix(in srgb, var(--primary-color) 8%, var(--surface-panel-chip-accent))
    );
  color: var(--primary-active);
  box-shadow:
    var(--highlight-panel-chip),
    var(--shadow-content-card-hover),
    0 0 0 1px color-mix(in srgb, var(--primary-color) 12%, transparent);
}

.menu-item.danger:hover {
  color: var(--error-color);
}

.menu-item__count {
  flex-shrink: 0;
  min-width: 1.25rem;
  padding: 0.1rem 0.4rem;
  border-radius: 999px;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  line-height: 1.2;
  text-align: center;
  font-variant-numeric: tabular-nums;
  color: var(--text-color-light);
  background: color-mix(in srgb, var(--error-color) 88%, var(--primary-color));
}

.divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, var(--border-content-card) 92%, transparent) 18%,
    color-mix(in srgb, var(--primary-color) 12%, var(--border-content-card)) 50%,
    color-mix(in srgb, var(--border-content-card) 92%, transparent) 82%,
    transparent
  );
}
</style>
