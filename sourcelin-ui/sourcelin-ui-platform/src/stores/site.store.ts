import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getNavbars, type NavbarItem } from '@/modules/navigation/api/navigation.api'

export interface SiteInfo {
  webName: string
  webTitle: string[]
  notices: string[]
  footer: string
  backgroundImage: string
  logo: string
  avatar: string
  recordNum: string | number
  showList: number[]
  author?: string
  siteName?: string
  github?: string
  gitee?: string
  qqNumber?: string
  email?: string
}

const SITE_INFO_STORAGE_KEY = 'site-info'

function normalizeMojibakeText(value: string) {
  if (!value) {
    return value
  }

  const mojibakeHint = /[\u9366\u9286\u93b4\u9354\u951b\u93c3\u7eef\u9427\u7487]/
  if (!mojibakeHint.test(value)) {
    return value
  }

  try {
    const bytes = new TextEncoder().encode(value)
    const decoded = new TextDecoder('gbk').decode(bytes).trim()
    if (!decoded || decoded === value) {
      return value
    }
    return decoded
  } catch {
    return value
  }
}

function normalizeSiteInfo(siteInfo: SiteInfo): SiteInfo {
  return {
    ...siteInfo,
    webName: normalizeMojibakeText(siteInfo.webName),
    webTitle: siteInfo.webTitle.map(normalizeMojibakeText),
    notices: siteInfo.notices.map(normalizeMojibakeText).filter((item) => Boolean(item) && item !== '系统通知'),
    footer: normalizeMojibakeText(siteInfo.footer)
  }
}

export const useSiteStore = defineStore('site', () => {
  const siteInfo = ref<SiteInfo>({
    webName: '圆圈博客',
    webTitle: [],
    notices: [],
    footer: '',
    backgroundImage: '',
    logo: '',
    avatar: '',
    recordNum: '',
    showList: []
  })

  const siteAccess = ref(10)
  const visitorAccess = ref(100)
  const navbarItems = ref<NavbarItem[]>([])

  function setSiteInfo(nextSiteInfo: SiteInfo) {
    const normalizedSiteInfo = normalizeSiteInfo(nextSiteInfo)
    siteInfo.value = normalizedSiteInfo
    localStorage.setItem(SITE_INFO_STORAGE_KEY, JSON.stringify(normalizedSiteInfo))
  }

  function setSiteStats(nextStats: { siteAccess?: number; visitorAccess?: number }) {
    if (typeof nextStats.siteAccess === 'number') {
      siteAccess.value = nextStats.siteAccess
    }
    if (typeof nextStats.visitorAccess === 'number') {
      visitorAccess.value = nextStats.visitorAccess
    }
  }

  async function loadNavbars() {
    if (navbarItems.value.length > 0) {
      return navbarItems.value
    }
    const rows = await getNavbars()
    navbarItems.value = Array.isArray(rows) ? rows : []
    return navbarItems.value
  }

  function setNavbars(items: NavbarItem[]) {
    navbarItems.value = items
  }

  function initFromStorage() {
    const storedSiteInfo = localStorage.getItem(SITE_INFO_STORAGE_KEY)
    if (!storedSiteInfo) {
      return
    }
    try {
      const parsedSiteInfo = JSON.parse(storedSiteInfo) as SiteInfo
      setSiteInfo(parsedSiteInfo)
    } catch {
      // ignore parse error
    }
  }

  initFromStorage()

  return {
    siteInfo,
    siteAccess,
    visitorAccess,
    navbarItems,
    setSiteInfo,
    setSiteStats,
    setNavbars,
    loadNavbars,
    initFromStorage
  }
})
