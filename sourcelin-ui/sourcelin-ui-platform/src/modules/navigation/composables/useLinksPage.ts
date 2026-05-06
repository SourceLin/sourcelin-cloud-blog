import type { Component } from 'vue'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { applyFriendLink, getLinks } from '@/modules/navigation/api/link.api'
import { appIcons } from '@/shared/components/ui/icons'
import { useSMessage } from '@/shared/composables/useSMessage'
import { copyPlainText } from '@/shared/utils/clipboard'
import { useSiteStore } from '@/stores/site.store'

export interface Link {
  id: number
  name: string
  url: string
  avatar: string
  description: string
}

export interface ApplyForm {
  name: string
  email: string
  url: string
  description: string
  avatar: string
}

export interface LinkHeroStatTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
  /** 点击后滚动并展开友链申请表 */
  action?: 'scroll-to-apply'
}

const DEFAULT_FRIEND_LINK_DESC = '分享美好，记录生活'

function truncateForStat(value: string, max = 16): string {
  if (value.length <= max) {
    return value
  }
  return `${value.slice(0, max - 1)}…`
}

export function useLinksPage() {
  const links = ref<Link[]>([])
  const message = useSMessage()
  const siteStore = useSiteStore()
  const applySubmitting = ref(false)
  /** 申请表默认折叠，突出友链列表版面 */
  const applyExpanded = ref(false)
  const applyCardSectionRef = ref<{ scrollIntoView: (options?: ScrollIntoViewOptions) => void } | null>(null)

  const applyForm = reactive<ApplyForm>({
    name: '',
    email: '',
    url: '',
    description: '',
    avatar: ''
  })

  const siteDisplayName = computed(
    () =>
      siteStore.siteInfo.webName?.trim() ||
      siteStore.siteInfo.siteName?.trim() ||
      '圆圈博客'
  )

  const siteDescriptionForSnippet = computed(
    () => siteStore.siteInfo.footer?.trim() || DEFAULT_FRIEND_LINK_DESC
  )

  /** 与申请表单字段顺序、文案一致，便于对方粘贴到友链后台 */
  const siteContactEmail = computed(() => (siteStore.siteInfo.email ?? '').trim())

  const siteAvatarForSnippet = computed(() => {
    const raw = (siteStore.siteInfo.logo || siteStore.siteInfo.avatar || '').trim()
    if (!raw) {
      return ''
    }
    if (/^https?:\/\//i.test(raw)) {
      return raw
    }
    if (typeof window !== 'undefined' && raw.startsWith('/')) {
      return `${window.location.origin}${raw}`
    }
    return raw
  })

  const snippetText = computed(() => {
    const origin = typeof window !== 'undefined' ? window.location.origin : ''
    const homeUrl = origin ? `${origin}/` : ''
    const emailLine = siteContactEmail.value || '（站点未配置公开展示邮箱）'
    const avatarLine = siteAvatarForSnippet.value || '（选填，可留空）'

    return [
      `网站名称：${siteDisplayName.value}`,
      `联系邮箱：${emailLine}`,
      `网站地址：${homeUrl || '（请在浏览器中打开本站后复制完整地址）'}`,
      `网站描述：${siteDescriptionForSnippet.value}`,
      `头像图片链接：${avatarLine}`
    ].join('\n')
  })

  const linkHeroStatTiles = computed((): LinkHeroStatTile[] => {
    const n = links.value.length
    const rawEmail = (siteStore.siteInfo.email ?? '').trim()

    return [
      {
        label: '展示友链',
        value: String(n).padStart(2, '0'),
        icon: appIcons.links,
        hue: 245,
        ariaLabel: `当前展示 ${n} 条友链`
      },
      {
        label: '申请通道',
        value: '开放',
        icon: appIcons.send,
        hue: 200,
        ariaLabel: '友链申请通道开放',
        action: 'scroll-to-apply'
      },
      {
        label: '站点邮箱',
        value: rawEmail ? truncateForStat(rawEmail, 14) : '--',
        icon: appIcons.mail,
        hue: 175,
        ariaLabel: rawEmail ? `站点联系邮箱 ${rawEmail}` : '未配置公开展示邮箱'
      }
    ]
  })

  const linksDirectoryBadge = computed(() => {
    const n = links.value.length
    return n === 0 ? '虚位以待' : `${n} 个站点`
  })

  function scrollToApplySection() {
    applyExpanded.value = true
    void nextTick(() => {
      applyCardSectionRef.value?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      })
      document.getElementById('apply-card-toggle')?.focus({ preventScroll: true })
    })
  }

  function toggleApplyExpanded() {
    applyExpanded.value = !applyExpanded.value
  }

  async function copySiteFriendInfo() {
    try {
      await copyPlainText(snippetText.value)
      message.success('已复制本站友链信息（与友链申请表字段一致），可粘贴到贵站后台')
    } catch {
      message.error('复制失败，请稍后重试或手动复制')
    }
  }

  const loadLinks = async () => {
    try {
      const rows = await getLinks()
      links.value = rows.map((item) => {
        return {
          id: item.id || 0,
          name: item.name || '未命名友情链接',
          url: item.url || '#',
          avatar: item.avatar || '',
          description: item.description || ''
        }
      })
    } catch (error) {
      console.error('加载友情链接失败:', error)
    }
  }

  function resetApplyForm() {
    applyForm.name = ''
    applyForm.email = ''
    applyForm.url = ''
    applyForm.description = ''
    applyForm.avatar = ''
  }

  async function submitApply() {
    if (applySubmitting.value) {
      return
    }
    const name = applyForm.name.trim()
    const email = applyForm.email.trim()
    const url = applyForm.url.trim()
    if (!name) {
      message.warning('请填写网站名称')
      return
    }
    if (!email) {
      message.warning('请填写联系邮箱')
      return
    }
    if (!url) {
      message.warning('请填写网站地址')
      return
    }

    applySubmitting.value = true
    try {
      await applyFriendLink({
        name,
        email,
        url,
        description: applyForm.description.trim() || undefined,
        avatar: applyForm.avatar.trim() || undefined
      })
      message.success('提交成功，审核通过后将展示在友链列表')
      resetApplyForm()
    } catch {
      /* http 拦截器已提示 */
    } finally {
      applySubmitting.value = false
    }
  }

  onMounted(() => {
    void loadLinks()
  })

  return {
    links,
    applyForm,
    applySubmitting,
    applyExpanded,
    applyCardSectionRef,
    siteDisplayName,
    siteDescriptionForSnippet,
    linkHeroStatTiles,
    linksDirectoryBadge,
    scrollToApplySection,
    copySiteFriendInfo,
    submitApply,
    toggleApplyExpanded
  }
}
