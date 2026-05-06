<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import localLogo from '@/assets/images/logo/logo.png'
import { appRoutes } from '@/app/router'
import { collectFooterNavigation } from '@/app/router/route-meta'
import { appIcons } from '@/shared/components/ui/icons'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { useSiteStore } from '@/stores/site.store'

interface FooterLinkItem {
  label: string
  path: string
}

interface FooterSocialLink {
  label: string
  href: string
  icon: typeof appIcons[keyof typeof appIcons]
}

const siteStore = useSiteStore()
const { siteInfo, siteAccess, visitorAccess } = storeToRefs(siteStore)

const footerQuickLinks = collectFooterNavigation(appRoutes)

const day = ref(0)
const hours = ref('00')
const minute = ref('00')
const second = ref('00')
let timer: ReturnType<typeof setInterval> | null = null

const currentYear = new Date().getFullYear()
const launchDateText = '2026.02.01'

const siteName = computed(() => siteInfo.value.siteName || siteInfo.value.webName || 'SourceLin')
const siteLogo = computed(() => siteInfo.value.logo || localLogo)
const siteSlogan = computed(() => siteInfo.value.footer || '分享美好，记录生活，收藏每一份心动。')

const footerLinks = computed<FooterLinkItem[]>(() => {
  const links = new Map<string, FooterLinkItem>()

  const home = footerQuickLinks.find((item) => item.path === '/')
  const about = footerQuickLinks.find((item) => item.path === '/about')

  if (home) {
    links.set(home.path, { label: home.label, path: home.path })
  } else {
    links.set('/', { label: '首页', path: '/' })
  }

  links.set('/about', { label: about?.label || '关于本站', path: '/about' })
  links.set('/user-agreement', { label: '用户协议', path: '/user-agreement' })
  links.set('/privacy-policy', { label: '隐私政策', path: '/privacy-policy' })

  return Array.from(links.values())
})

const socialLinks = computed<FooterSocialLink[]>(() => {
  const links: FooterSocialLink[] = []

  if (siteInfo.value.github) {
    links.push({ label: 'GitHub', href: siteInfo.value.github, icon: appIcons.github })
  }
  if (siteInfo.value.gitee) {
    links.push({ label: 'Gitee', href: siteInfo.value.gitee, icon: appIcons.gitee })
  }
  if (siteInfo.value.email) {
    links.push({ label: '邮箱', href: `mailto:${siteInfo.value.email}`, icon: appIcons.email })
  }
  if (siteInfo.value.qqNumber) {
    links.push({
      label: 'QQ',
      href: `https://wpa.qq.com/msgrd?v=3&uin=${siteInfo.value.qqNumber}&site=qq&menu=yes`,
      icon: appIcons.qq
    })
  }

  return links
})

const footerMetrics = computed(() => [
  { label: '累计访问', value: Number(siteAccess.value || 0), icon: appIcons.search },
  { label: '累计来访', value: Number(visitorAccess.value || 0), icon: appIcons.visitors }
])

function runTime() {
  const startTime = new Date('2026-02-01').getTime()
  const now = new Date().getTime()
  const diff = Math.max(now - startTime, 0)
  const msPerDay = 24 * 60 * 60 * 1000

  day.value = Math.floor(diff / msPerDay)

  const date = new Date()
  hours.value = String(date.getHours()).padStart(2, '0')
  minute.value = String(date.getMinutes()).padStart(2, '0')
  second.value = String(date.getSeconds()).padStart(2, '0')
}

onMounted(() => {
  runTime()
  timer = setInterval(runTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<template>
  <footer class="glass-footer">
    <div class="footer-grid">
      <SSurfacePanel tag="section" class="footer-brand-hub" variant="soft">
        <router-link to="/" class="brand-link">
          <img :src="siteLogo" alt="Logo" class="brand-logo">
          <div class="brand-copy">
            <span class="brand-copy__eyebrow">Brand Hub</span>
            <h2 class="brand-copy__title">{{ siteName }}</h2>
          </div>
        </router-link>

        <p class="brand-slogan">{{ siteSlogan }}</p>

        <div class="brand-meta-row">
          <div v-if="socialLinks.length" class="brand-socials">
            <a
              v-for="item in socialLinks"
              :key="item.label"
              :href="item.href"
              class="brand-socials__link"
              target="_blank"
              rel="noreferrer"
              :aria-label="item.label"
              :title="item.label"
            >
              <SIcon :icon="item.icon" :size="18" />
            </a>
          </div>

          <div class="brand-metrics">
            <article
              v-for="metric in footerMetrics"
              :key="metric.label"
              class="brand-metric-card"
            >
              <div class="brand-metric-card__icon">
                <SIcon :icon="metric.icon" :size="16" />
              </div>
              <div class="brand-metric-card__copy">
                <strong class="brand-metric-card__value">
                  <SNumberAnimation :from="0" :to="metric.value" :duration="1200" />
                </strong>
                <span class="brand-metric-card__label">{{ metric.label }}</span>
              </div>
            </article>
          </div>
        </div>
      </SSurfacePanel>

      <SSurfacePanel tag="section" class="footer-nav-hub" variant="inset">
        <h3 class="footer-section-title">探索站点</h3>
        <nav class="footer-link-row" aria-label="站内入口">
          <router-link
            v-for="item in footerLinks"
            :key="item.path"
            :to="item.path"
            class="footer-link-item"
          >
            <span class="footer-link-item__label">{{ item.label }}</span>
            <span class="footer-link-item__arrow">
              <SIcon :icon="appIcons.arrowForward" :size="14" />
            </span>
          </router-link>
        </nav>
      </SSurfacePanel>

      <SSurfacePanel tag="aside" class="footer-runtime-card" variant="inset">
        <div class="footer-runtime-card__head">
          <h3 class="footer-section-title">站点运行</h3>
        </div>

        <div class="footer-runtime-card__clock">
          <span class="footer-runtime-card__clock-item">
            <strong>{{ day }}</strong>
            <small>天</small>
          </span>
          <span class="footer-runtime-card__clock-item">
            <strong>{{ hours }}</strong>
            <small>时</small>
          </span>
          <span class="footer-runtime-card__clock-item">
            <strong>{{ minute }}</strong>
            <small>分</small>
          </span>
          <span class="footer-runtime-card__clock-item">
            <strong>{{ second }}</strong>
            <small>秒</small>
          </span>
        </div>
      </SSurfacePanel>
    </div>

    <div class="footer-copy-bar">
      <p class="footer-copy-bar__copyright">Copyright © 2026-{{ currentYear }} SourceLin</p>
    </div>
  </footer>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.glass-footer {
  position: relative;
  margin-top: auto;
  padding: 16px 20px 12px;
  z-index: 10;
  background:
    var(--surface-panel-specular-soft),
    var(--surface-content-card);
  background-repeat: no-repeat;
  border-top: 1px solid var(--border-content-card);
  box-shadow: var(--highlight-content-card), var(--shadow-content-card);
  overflow: hidden;
}

.glass-footer::before {
  content: '';
  position: absolute;
  inset: auto -8% -36% auto;
  width: min(18rem, 32vw);
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(circle, color-mix(in srgb, var(--primary-color) 12%, transparent) 0%, transparent 72%);
  pointer-events: none;
}

.footer-grid {
  position: relative;
  z-index: 1;
  max-width: 1240px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(260px, 1fr) minmax(340px, 1.08fr) minmax(224px, 0.82fr);
  gap: 10px;
  align-items: stretch;
}

.footer-brand-hub,
.footer-nav-hub,
.footer-runtime-card {
  min-width: 0;
}

.footer-brand-hub {
  display: grid;
  gap: 8px;
  align-content: center;
  padding: 10px 12px;
}

.brand-link {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  width: fit-content;
  text-decoration: none;
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 13px;
  object-fit: cover;
  box-shadow: var(--shadow-panel-inline);
}

.brand-copy {
  display: grid;
  gap: 2px;
}

.brand-copy__eyebrow {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.brand-copy__title {
  margin: 0;
  font-size: 1.08rem;
  line-height: 1.05;
  color: var(--title-color);
}

.brand-slogan {
  margin: 0;
  max-width: 22rem;
  font-size: 0.82rem;
  line-height: 1.54;
  color: var(--text-reading-soft);
}

.brand-meta-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px 10px;
  flex-wrap: wrap;
  min-height: 34px;
  padding-top: 0;
}

.brand-socials {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.brand-socials__link {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  overflow: hidden;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 9px;
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-subtle);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.brand-socials__link::before,
.brand-metric-card::before,
.footer-link-item::before,
.footer-runtime-card__clock-item::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 88%, transparent), transparent 42%);
  pointer-events: none;
  opacity: 0.8;
}

.brand-socials__link:hover {
  transform: translateY(-2px);
  color: var(--primary-color);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-soft);
}

.brand-metrics {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.brand-metric-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 34px;
  padding: 0 10px;
  border-radius: 9px;
  overflow: hidden;
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-subtle);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.brand-metric-card:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-soft);
}

.brand-metric-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 7px;
  color: var(--primary-color);
  background: var(--surface-panel-chip-accent);
}

.brand-metric-card__copy {
  display: grid;
  gap: 1px;
}

.brand-metric-card__value {
  font-size: 0.88rem;
  line-height: 1.05;
  color: var(--title-color);
}

.brand-metric-card__label,
.footer-link-item__label,
.footer-copy-bar__copyright {
  font-size: 0.76rem;
  color: var(--text-secondary);
}

.footer-copy-bar {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 1240px;
  min-height: 22px;
  margin: 8px auto 0;
  padding-top: 8px;
  border-top: 1px solid color-mix(in srgb, var(--footer-divider) 88%, transparent);
  text-align: center;
}

.footer-copy-bar__copyright {
  margin: 0;
}

.footer-nav-hub,
.footer-runtime-card {
  display: grid;
  gap: 8px;
  padding: 10px 12px;
}

.footer-section-title {
  margin: 0;
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--title-color);
  letter-spacing: 0.02em;
}

.footer-link-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
  align-items: center;
}

.footer-link-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 7px;
  min-height: 38px;
  padding: 0 11px;
  border-radius: 11px;
  overflow: hidden;
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-subtle);
  color: var(--text-secondary);
  text-decoration: none;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    color var(--transition-base),
    box-shadow var(--transition-base),
    background var(--transition-base);
}

.footer-link-item:hover {
  transform: translateY(-2px);
  color: var(--text-primary);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-soft);
}

.footer-link-item__arrow {
  display: inline-flex;
  align-items: center;
  opacity: 0.64;
}

.footer-runtime-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  flex-wrap: wrap;
}

.footer-runtime-card__launch {
  min-height: 28px;
  padding: 0 10px;
  font-size: 0.74rem;
}

.footer-runtime-card__clock {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 6px;
}

.footer-runtime-card__clock-item {
  position: relative;
  display: grid;
  justify-items: center;
  gap: 2px;
  min-height: 56px;
  padding: 6px 4px;
  border-radius: 11px;
  overflow: hidden;
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--primary-color) 6%, var(--glass-surface-strong)),
      color-mix(in srgb, var(--glass-surface-lite) 96%, transparent) 62%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-08) 84%, transparent), transparent);
  border: 1px solid color-mix(in srgb, var(--glass-border) 78%, transparent);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-subtle);
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.footer-runtime-card__clock-item:hover {
  transform: translateY(-2px);
  border-color: var(--border-interactive-hover);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-soft);
}

.footer-runtime-card__clock-item strong {
  font-size: 0.9rem;
  line-height: 1;
  color: var(--title-color);
}

.footer-runtime-card__clock-item small {
  font-size: 0.68rem;
  color: var(--text-secondary);
}

@include sourcelin-down(lg) {
  .footer-grid {
    grid-template-columns: 1fr;
  }

  .footer-link-row,
  .footer-runtime-card__clock {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@include sourcelin-down(md) {
  .glass-footer {
    padding: 16px 14px 14px;
  }

  .footer-grid {
    gap: 10px;
  }

  .footer-brand-hub,
  .footer-nav-hub,
  .footer-runtime-card {
    padding: 10px 12px;
  }
}

@include sourcelin-down(sm) {
  .footer-link-row,
  .footer-runtime-card__clock {
    grid-template-columns: 1fr;
  }

  .footer-nav-hub,
  .footer-runtime-card {
    padding: 12px;
  }
}
</style>
