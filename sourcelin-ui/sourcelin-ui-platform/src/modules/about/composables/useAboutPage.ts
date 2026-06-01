import type { Component } from 'vue'
import { computed, onMounted, ref } from 'vue'
import { getAboutInfo, type FrontAboutInfo } from '@/modules/about/api/site.api'
import { getCategories } from '@/modules/article/api/category.api'
import { getHome } from '@/modules/article/api/article.api'
import { getStats } from '@/modules/home/api/stats.api'
import { useSMessage } from '@/shared/composables/useSMessage'
import { appIcons } from '@/shared/components/ui/icons'
import { useSiteStore } from '@/stores/site.store'

export interface SiteInfo {
  author: string
  authorInfo: string
  avatar: string
  bio: string
  webName: string
  webUrl: string
  email: string
  qqNumber: string
  qqGroup: string
  wechat: string
  github: string
  gitee: string
  openComment: string
  openAdmiration: string
  aliPay: string
  weixinPay: string
  showList: number[]
  notices: string[]
}

export interface InterestGroup {
  label: string
  copy: string
  items: string[]
}

export interface TimelineItem {
  kicker: string
  title: string
  copy: string
}

export interface ContactChannel {
  title: string
  copy: string
  icon: (typeof appIcons)[keyof typeof appIcons]
  to?: string
  href?: string
  copyValue?: string
  type: 'link' | 'copy'
}

export interface RewardChannel {
  title: string
  icon: (typeof appIcons)[keyof typeof appIcons]
  image: string
}

export interface SiteStat {
  icon: Component
  label: string
  value: number
  trend: number
  suffix: string
  hue: number
}

interface SummaryItem {
  label: string
  value: string
}

export interface SummaryItemWithIcon extends SummaryItem {
  icon: Component
  to?: string
}

export interface AboutHeroStatTile {
  label: string
  title: string
  meta: string
  icon: (typeof appIcons)[keyof typeof appIcons]
  href: string
  ariaLabel: string
}

export interface SiteFact {
  label: string
  value: string
}

const splitTextList = (value: string | undefined) => {
  if (!value) return []
  return value
    .split(/[，,\n]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

function resolveLinkMeta(href: string, fallback: string): string {
  if (!href) return fallback
  if (href.startsWith('mailto:')) return '邮件联系'

  try {
    const normalized = new URL(href)
    return normalized.host || fallback
  } catch {
    return href.replace(/^https?:\/\//i, '').replace(/\/$/, '') || fallback
  }
}

export function useAboutPage() {
  const siteStore = useSiteStore()
  const message = useSMessage()

  const siteInfo = ref<SiteInfo>({
    author: siteStore.siteInfo.author || '',
    authorInfo: '',
    avatar: '',
    bio: '',
    webName: siteStore.siteInfo.webName || '',
    webUrl: '',
    email: siteStore.siteInfo.email || '',
    qqNumber: siteStore.siteInfo.qqNumber || '',
    qqGroup: '',
    wechat: '',
    github: siteStore.siteInfo.github || '',
    gitee: siteStore.siteInfo.gitee || '',
    openComment: '1',
    openAdmiration: '1',
    aliPay: '',
    weixinPay: '',
    showList: siteStore.siteInfo.showList || [],
    notices: siteStore.siteInfo.notices?.filter(Boolean) || []
  })

  const aboutInfo = ref<FrontAboutInfo | null>(null)
  const copiedContactTitle = ref('')
  const siteStatsLoading = ref(false)
  const siteStats = ref<SiteStat[]>([
    { icon: appIcons.document, label: '文章', value: 0, trend: 0, suffix: '篇', hue: 220 },
    { icon: appIcons.comment, label: '评论', value: 0, trend: 0, suffix: '条', hue: 180 },
    { icon: appIcons.search, label: '访问', value: 0, trend: 0, suffix: '次', hue: 280 },
    { icon: appIcons.user, label: '用户', value: 0, trend: 0, suffix: '人', hue: 150 }
  ])
  const categoriesCount = ref(0)
  const recommendedCount = ref(0)

  const skills = computed(() => {
    const keywordList = splitTextList(aboutInfo.value?.keyword)
    if (keywordList.length) return Array.from(new Set(keywordList)).slice(0, 18)
    const fallback = splitTextList(aboutInfo.value?.summary)
    return Array.from(new Set(fallback)).slice(0, 18)
  })

  const summaryItems = computed<SummaryItem[]>(() => {
    const noticesCount = siteInfo.value.notices?.length || 0
    const keywordCount = skills.value.length
    return [
      { label: '公告', value: `${noticesCount} 条` },
      { label: '分类', value: `${categoriesCount.value} 项` },
      { label: '标签', value: `${keywordCount} 个` },
      { label: '精选', value: `${recommendedCount.value} 篇` }
    ]
  })

  const summaryItemsWithIcons = computed<SummaryItemWithIcon[]>(() => {
    const iconMap: Record<string, Component> = {
      '公告': appIcons.notice,
      '分类': appIcons.category,
      '标签': appIcons.tag,
      '精选': appIcons.star
    }
    const routeMap: Record<string, string> = {
      '公告': '/messages',
      '分类': '/categories',
      '标签': '/tags',
      '精选': '/hot'
    }
    return summaryItems.value.map((item) => ({
      ...item,
      icon: iconMap[item.label] || appIcons.document,
      to: routeMap[item.label]
    }))
  })

  const interestGroups = computed<InterestGroup[]>(() => {
    const labels = ['核心主题', '常见关键词', '延展方向']
    const copyTemplates = [
      '这里是站里最常出现的内容，第一次来可以先看这部分。',
      '这部分更新比较频繁，适合长期关注。',
      '这里是不定期展开的话题，适合继续深挖。'
    ]
    const groups: InterestGroup[] = []
    for (let i = 0; i < skills.value.length; i += 4) {
      const chunk = skills.value.slice(i, i + 4)
      if (!chunk.length) continue
      groups.push({
        label: labels[groups.length] || `主题分组 ${groups.length + 1}`,
        copy: copyTemplates[groups.length] || `这一组主要会聊：${chunk.slice(0, 2).join('、')}。`,
        items: chunk
      })
    }
    return groups
  })

  const contentSectionCopy = computed(() => {
    if (!skills.value.length) return '这里会持续更新技术和生活相关内容，先从你感兴趣的主题开始看。'
    const preview = skills.value.slice(0, 3).join('、')
    return `目前主要围绕 ${preview} 这些主题更新，内容会持续补充。`
  })

  const timelineItems = computed<TimelineItem[]>(() => {
    const commentEnabled = aboutInfo.value?.openComment === '1'
    const admirationEnabled = aboutInfo.value?.openAdmiration === '1'
    const fallbackCopy = siteInfo.value.bio?.trim() || '持续整理站点内容，保持长期更新。'
    const summaryText = aboutInfo.value?.summary?.trim() || '持续输出可复用的经验总结。'
    return [
      { kicker: '第一步', title: '先定这次要写的主题', copy: summaryText },
      { kicker: '第二步', title: '写好后发布出来', copy: fallbackCopy },
      {
        kicker: '第三步',
        title: commentEnabled ? '评论区已开放，欢迎交流' : '评论区暂未开放',
        copy: admirationEnabled ? '赞赏已开启，感谢支持。' : '暂未开启赞赏，欢迎先留言交流。'
      }
    ]
  })

  const siteSummary = computed(() =>
    siteInfo.value.bio?.trim() || siteStore.siteInfo.footer?.trim() || '暂无站点介绍。'
  )
  const emailHref = computed(() => {
    const email = siteInfo.value.email?.trim() || siteStore.siteInfo.email?.trim()
    return email ? `mailto:${email}` : ''
  })
  const heroStatTiles = computed((): AboutHeroStatTile[] => {
    const links = [
      { label: 'GitHub', icon: appIcons.github, href: siteInfo.value.github?.trim() || '' },
      { label: 'Gitee', icon: appIcons.gitee, href: siteInfo.value.gitee?.trim() || '' },
      { label: '站点地址', icon: appIcons.navigation, href: siteInfo.value.webUrl?.trim() || '' },
      { label: '发送邮件', icon: appIcons.mail, href: emailHref.value }
    ]
    return links
      .filter((item) => Boolean(item.href))
      .slice(0, 3)
      .map((item) => ({
        label: item.label,
        title: item.label,
        meta: resolveLinkMeta(item.href, item.label),
        icon: item.icon,
        href: item.href,
        ariaLabel: `${item.label}，点击可跳转`
      }))
  })

  const siteFacts = computed((): SiteFact[] => {
    const keywordCount = skills.value.length
    const recordNum = String(aboutInfo.value?.recordNum || siteStore.siteInfo.recordNum || '-')
    return [
      { label: '站点名称', value: siteInfo.value.webName?.trim() || siteStore.siteInfo.webName?.trim() || '未设置' },
      { label: '关键词数量', value: `${keywordCount} 个` },
      { label: '站点地址', value: siteInfo.value.webUrl?.trim() || '未设置' },
      { label: '备案号', value: recordNum }
    ]
  })

  const isVisibleContact = (code: number) => {
    const allowList = siteInfo.value.showList
    return !Array.isArray(allowList) || allowList.length === 0 || allowList.includes(code)
  }

  const contactChannels = computed<ContactChannel[]>(() => {
    const channels: ContactChannel[] = []
    if (emailHref.value && isVisibleContact(1)) {
      channels.push({ title: '发送邮件', copy: `邮箱：${siteInfo.value.email}`, icon: appIcons.mail, copyValue: siteInfo.value.email, type: 'copy' })
    }
    if (siteInfo.value.qqNumber?.trim() && isVisibleContact(2)) {
      channels.push({ title: 'QQ 联系', copy: `QQ：${siteInfo.value.qqNumber}`, icon: appIcons.qq, copyValue: siteInfo.value.qqNumber, type: 'copy' })
    }
    if (siteInfo.value.wechat?.trim() && isVisibleContact(5)) {
      channels.push({ title: '微信联系', copy: `微信号：${siteInfo.value.wechat}`, icon: appIcons.comment, copyValue: siteInfo.value.wechat, type: 'copy' })
    }
    if (siteInfo.value.qqGroup?.trim() && isVisibleContact(6)) {
      channels.push({ title: 'QQ群', copy: `群号：${siteInfo.value.qqGroup}`, icon: appIcons.visitors, copyValue: siteInfo.value.qqGroup, type: 'copy' })
    }
    channels.push({ title: '去树洞', copy: '如果你想留言，可以去树洞。', icon: appIcons.comment, to: '/treehole', type: 'link' })
    channels.push({ title: '看看友链', copy: '从这里可以继续看看其他站点。', icon: appIcons.links, to: '/links', type: 'link' })
    if (siteInfo.value.webUrl?.trim()) {
      channels.push({ title: '站点地址', copy: siteInfo.value.webUrl, icon: appIcons.navigation, href: siteInfo.value.webUrl, type: 'link' })
    }
    const order: Record<ContactChannel['type'], number> = { link: 1, copy: 2 }
    return channels.sort((a, b) => order[a.type] - order[b.type])
  })

  const copyContact = async (item: { title: string; copyValue?: string }) => {
    if (!item.copyValue) return
    try {
      await navigator.clipboard.writeText(item.copyValue)
      copiedContactTitle.value = item.title
      message.success(`已复制${item.title}`)
      window.setTimeout(() => {
        copiedContactTitle.value = ''
      }, 1800)
    } catch (error) {
      console.error('复制联系方式失败:', error)
    }
  }

  const rewardChannels = computed(() => {
    if (siteInfo.value.openAdmiration !== '1') return [] as RewardChannel[]
    const channels: RewardChannel[] = []
    if (siteInfo.value.aliPay?.trim()) {
      channels.push({ title: '支付宝赞赏', icon: appIcons.send, image: siteInfo.value.aliPay })
    }
    if (siteInfo.value.weixinPay?.trim()) {
      channels.push({ title: '微信赞赏', icon: appIcons.send, image: siteInfo.value.weixinPay })
    }
    return channels
  })

  const loadSiteInfo = async () => {
    try {
      const payload = await getAboutInfo()
      const pickText = (...values: Array<string | undefined>) => {
        for (const value of values) {
          if (typeof value === 'string' && value.trim()) return value.trim()
        }
        return ''
      }
      siteInfo.value = {
        author: pickText(payload.author, siteInfo.value.author, siteStore.siteInfo.author, '作者'),
        authorInfo: pickText(payload.authorInfo, siteInfo.value.authorInfo),
        avatar: pickText(payload.avatar, siteInfo.value.avatar),
        bio: pickText(payload.bio, siteInfo.value.bio, siteStore.siteInfo.footer),
        webName: pickText(payload.webName, siteInfo.value.webName, siteStore.siteInfo.webName, '未命名站点'),
        webUrl: pickText(payload.webUrl, siteInfo.value.webUrl),
        email: pickText(payload.email, siteInfo.value.email, siteStore.siteInfo.email),
        qqNumber: pickText(payload.qqNumber, siteInfo.value.qqNumber, siteStore.siteInfo.qqNumber),
        qqGroup: pickText(payload.qqGroup, siteInfo.value.qqGroup),
        wechat: pickText(payload.wechat, siteInfo.value.wechat),
        github: pickText(payload.github, siteInfo.value.github, siteStore.siteInfo.github),
        gitee: pickText(payload.gitee, siteInfo.value.gitee, siteStore.siteInfo.gitee),
        openComment: pickText(payload.openComment, siteInfo.value.openComment, '1'),
        openAdmiration: pickText(payload.openAdmiration, siteInfo.value.openAdmiration, '1'),
        aliPay: pickText(payload.aliPay, siteInfo.value.aliPay),
        weixinPay: pickText(payload.weixinPay, siteInfo.value.weixinPay),
        showList: Array.isArray(payload.showList) && payload.showList.length ? payload.showList : siteInfo.value.showList,
        notices: Array.isArray(payload.notices) && payload.notices.length ? payload.notices.filter(Boolean) : siteInfo.value.notices
      }
      aboutInfo.value = payload
    } catch (error) {
      console.error('加载站点信息失败:', error)
    }
  }

  async function loadSiteStats() {
    siteStatsLoading.value = true
    try {
      const response = await getStats()
      if (response) {
        siteStats.value = [
          { icon: appIcons.document, label: '文章', value: response.articleCount || 0, trend: response.articleTrend || 0, suffix: '篇', hue: 220 },
          { icon: appIcons.comment, label: '评论', value: response.commentCount || 0, trend: response.commentTrend || 0, suffix: '条', hue: 180 },
          { icon: appIcons.search, label: '访问', value: response.viewCount || 0, trend: response.viewTrend || 0, suffix: '次', hue: 280 },
          { icon: appIcons.user, label: '用户', value: response.userCount || 0, trend: response.userTrend || 0, suffix: '人', hue: 150 }
        ]
      }
    } catch (error) {
      console.error('加载站点统计失败:', error)
    } finally {
      siteStatsLoading.value = false
    }
  }

  async function loadCategories() {
    try {
      const categories = await getCategories()
      categoriesCount.value = categories?.length || 0
    } catch (error) {
      console.error('加载分类失败:', error)
      categoriesCount.value = 0
    }
  }

  async function loadRecommended() {
    try {
      const data = await getHome({ page: 1, pageSize: 1 })
      recommendedCount.value = data?.recommend?.length || 0
    } catch (error) {
      console.error('加载精选文章失败:', error)
      recommendedCount.value = 0
    }
  }

  onMounted(() => {
    void loadSiteInfo()
    void loadSiteStats()
    void loadCategories()
    void loadRecommended()
  })

  return {
    siteInfo,
    copiedContactTitle,
    siteStats,
    summaryItemsWithIcons,
    skills,
    interestGroups,
    contentSectionCopy,
    timelineItems,
    siteSummary,
    emailHref,
    heroStatTiles,
    siteFacts,
    contactChannels,
    rewardChannels,
    copyContact
  }
}
