import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteArticle } from '@/modules/article/api/article.api'
import { getCollects } from '@/modules/user/api/collect.api'
import { unfollowUser } from '@/modules/user/api/follow.api'
import { uncollectTarget } from '@/shared/api/interaction.api'
import {
  getUserArticles,
  getUserCenterStats,
  getUserFans,
  getUserFollows,
  getUserProfile,
  updateUserProfile,
  updateUserPwd,
  uploadAvatar,
  type FrontUserDetailVO,
  type FrontUserProfileVO,
  type FrontUserArticleVO
} from '@/modules/user/api/user.api'
import { useSMessage } from '@/shared/composables/useSMessage'
import { useDictStore } from '@/stores/dict.store'
import { getToken } from '@/shared/utils/auth'
import { useUserStore } from '@/stores/user'
import defaultCover from '@/assets/hero.png'
import defaultAvatar from '@/assets/images/logo/logo.png'

export type UserCenterTab = 'info' | 'security' | 'articles' | 'collects' | 'follows' | 'fans'

export interface UserInfo {
  id?: number
  nickName?: string
  userName?: string
  avatar?: string
  introduction?: string
  phonenumber?: string
  email?: string
  sex?: string | number
}

export interface UserArticleItem {
  id: number
  title: string
  summary: string
  avatar: string
  createTime: string
  viewCount: number
  likeCount: number
  commentCount: number
  status: number
}

export interface UserCollectItem {
  id: number
  targetType: 'article' | 'say' | 'treehole'
  targetId: number
  createTime: string
  active: boolean
  article: UserArticleItem | null
  say: {
    id: number
    content: string
    images: string
    imageFileIds: string
    createTime: string
    likeCount: number
    commentCount: number
  } | null
  treehole: {
    id: number
    content: string
    createTime: string
    likeCount: number
    commentCount: number
    collectCount: number
  } | null
}

export interface UserFollowItem {
  id: number
  targetUser: UserInfo | null
  createTime: string
}

export interface UserFanItem {
  id: number
  user: UserInfo | null
  createTime: string
}

interface SayRaw {
  id?: number
  content?: string
  images?: string
  imageFileIds?: string
  createTime?: string
  likeCount?: number
  commentCount?: number
}

interface TreeholeRaw {
  id?: number
  content?: string
  createTime?: string
  likeCount?: number
  commentCount?: number
  collectCount?: number
}

function asProfile(value: unknown): FrontUserProfileVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserProfileVO
  }
  return {}
}

function asDetail(value: unknown): FrontUserDetailVO {
  if (typeof value === 'object' && value !== null) {
    return value as FrontUserDetailVO
  }
  return {}
}

function asSay(value: unknown): SayRaw {
  if (typeof value === 'object' && value !== null) {
    return value as SayRaw
  }
  return {}
}

function asTreehole(value: unknown): TreeholeRaw {
  if (typeof value === 'object' && value !== null) {
    return value as TreeholeRaw
  }
  return {}
}

function toString(value: unknown, fallback = ''): string {
  return typeof value === 'string' ? value : fallback
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

export function useUserCenter() {
  const router = useRouter()
  const message = useSMessage()
  const dictStore = useDictStore()
  const userStore = useUserStore()

  const activeTab = ref<UserCenterTab>('info')
  const loading = ref(false)

  const userForm = ref({
    nickName: '',
    phonenumber: '',
    email: '',
    sex: '0',
    introduction: ''
  })

  const pwdForm = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  const userStats = ref({
    articleCount: 0,
    collectCount: 0,
    followCount: 0,
    fansCount: 0
  })

  const articlesLoading = ref(false)
  const myArticles = ref<UserArticleItem[]>([])
  const articlePage = ref(1)
  const articleSize = ref(10)
  const articleTotal = ref(0)
  const articleStatus = ref<number | undefined>(undefined)

  const collectsLoading = ref(false)
  const myCollects = ref<UserCollectItem[]>([])
  const collectPage = ref(1)
  const collectSize = ref(10)
  const collectTotal = ref(0)
  const collectType = ref<'all' | 'article' | 'say' | 'treehole'>('all')

  const followsLoading = ref(false)
  const myFollows = ref<UserFollowItem[]>([])
  const followPage = ref(1)
  const followSize = ref(10)
  const followTotal = ref(0)
  const followState = ref<'all' | 'active' | 'inactive'>('all')

  const fansLoading = ref(false)
  const myFans = ref<UserFanItem[]>([])
  const fansPage = ref(1)
  const fansSize = ref(10)
  const fansTotal = ref(0)
  const fansState = ref<'all' | 'active' | 'inactive'>('all')

  const isLoggedIn = computed(() => Boolean(getToken()))
  const currentUser = computed(() => asDetail(userStore.userInfo) as UserInfo)
  const currentUserId = computed(() => {
    const id = toNumber(currentUser.value.id, 0)
    return id > 0 ? id : undefined
  })
  const avatarUrl = computed(() => currentUser.value.avatar || defaultAvatar)
  const displayName = computed(() => currentUser.value.nickName || '用户')
  const displayBio = computed(() => currentUser.value.introduction || '这个用户还没有填写简介。')

  const formatDate = (value: string) => (value ? value.split(' ')[0] : '')

  const normalizeArticle = (raw: FrontUserArticleVO): UserArticleItem => ({
    id: toNumber(raw.id),
    title: toString(raw.title, '未命名文章'),
    summary: toString(raw.summary),
    avatar: toString(raw.avatar) || defaultCover,
    createTime: toString(raw.createTime),
    viewCount: toNumber(raw.viewCount),
    likeCount: toNumber(raw.likeCount),
    commentCount: toNumber(raw.commentCount),
    status: toNumber(raw.status, 0)
  })

  const normalizeUser = (raw: unknown): UserInfo | null => {
    const data = asDetail(raw)
    if (!data.id && !data.nickName && !data.userName) {
      return null
    }
    return {
      id: toNumber(data.id),
      nickName: toString(data.nickName),
      userName: toString(data.userName),
      avatar: toString(data.avatar),
      phonenumber: toString(data.phonenumber),
      email: toString(data.email),
      sex: toString(data.sex),
      introduction: toString(data.introduction)
    }
  }

  const normalizeSay = (raw: unknown) => {
    const data = asSay(raw)
    const id = toNumber(data.id)
    if (!id) {
      return null
    }
    return {
      id,
      content: toString(data.content),
      images: toString(data.images),
      imageFileIds: toString(data.imageFileIds),
      createTime: toString(data.createTime),
      likeCount: toNumber(data.likeCount),
      commentCount: toNumber(data.commentCount)
    }
  }

  const normalizeTreehole = (raw: unknown) => {
    const data = asTreehole(raw)
    const id = toNumber(data.id)
    if (!id) {
      return null
    }
    return {
      id,
      content: toString(data.content),
      createTime: toString(data.createTime),
      likeCount: toNumber(data.likeCount),
      commentCount: toNumber(data.commentCount),
      collectCount: toNumber(data.collectCount)
    }
  }

  const loadUserInfo = async () => {
    loading.value = true
    try {
      const data = asProfile(await getUserProfile())
      userForm.value = {
        nickName: toString(data.nickName),
        phonenumber: toString(data.phonenumber),
        email: toString(data.email),
        sex: String(data.sex ?? '0'),
        introduction: toString(data.introduction)
      }
      await userStore.getUserInfoAction()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  }

  const loadMyArticles = async () => {
    if (!currentUserId.value) {
      return
    }
    articlesLoading.value = true
    try {
      const data = await getUserArticles(currentUserId.value, {
        page: articlePage.value,
        pageSize: articleSize.value,
        status: articleStatus.value
      })
      const rows = data.items || []
      myArticles.value = rows.map(normalizeArticle)
      articleTotal.value = toNumber(data.total)
    } catch (error) {
      console.error(error)
    } finally {
      articlesLoading.value = false
    }
  }

  const loadMyCollects = async () => {
    collectsLoading.value = true
    try {
      const data = await getCollects({
        page: collectPage.value,
        pageSize: collectSize.value,
        type: collectType.value
      })
      const rows = data.items || []
      myCollects.value = rows.map((item) => {
        const article = normalizeArticle(item.article as unknown as FrontUserArticleVO)
        const say = normalizeSay(item.say)
        const treehole = normalizeTreehole(item.treehole)
        const rawType = String(item.targetType || '').toLowerCase()
        const targetType =
          rawType === 'say'
            ? 'say'
            : rawType === 'treehole'
              ? 'treehole'
              : 'article'
        const active = targetType === 'article'
          ? Boolean(article.id)
          : targetType === 'say'
            ? Boolean(say?.id)
            : Boolean(treehole?.id)
        return {
          id: toNumber(item.id),
          targetType,
          targetId: toNumber(item.targetId),
          createTime: toString(item.createTime),
          active,
          article: article.id ? article : null,
          say,
          treehole
        }
      })
      collectTotal.value = toNumber(data.total)
    } catch (error) {
      console.error(error)
    } finally {
      collectsLoading.value = false
    }
  }

  const loadMyFollows = async () => {
    if (!currentUserId.value) {
      return
    }
    followsLoading.value = true
    try {
      const data = await getUserFollows(currentUserId.value, {
        page: followPage.value,
        pageSize: followSize.value,
        state: followState.value
      })
      const rows = data.items || []
      myFollows.value = rows.map((item) => ({
        id: toNumber(item.id),
        targetUser: normalizeUser(item.targetUser),
        createTime: toString(item.createTime)
      }))
      followTotal.value = toNumber(data.total)
    } catch (error) {
      console.error(error)
    } finally {
      followsLoading.value = false
    }
  }

  const loadMyFans = async () => {
    if (!currentUserId.value) {
      return
    }
    fansLoading.value = true
    try {
      const data = await getUserFans(currentUserId.value, {
        page: fansPage.value,
        pageSize: fansSize.value,
        state: fansState.value
      })
      const rows = data.items || []
      myFans.value = rows.map((item) => ({
        id: toNumber(item.id),
        user: normalizeUser(item.user),
        createTime: toString(item.createTime)
      }))
      fansTotal.value = toNumber(data.total)
    } catch (error) {
      console.error(error)
    } finally {
      fansLoading.value = false
    }
  }

  const loadUserCenterStats = async () => {
    try {
      const stats = await getUserCenterStats()
      userStats.value = {
        articleCount: toNumber(stats.articleCount),
        collectCount: toNumber(stats.collectCount),
        followCount: toNumber(stats.followCount),
        fansCount: toNumber(stats.fansCount)
      }
    } catch (error) {
      console.error(error)
    }
  }

  const handleAvatarChange = async (file: File | null) => {
    if (!file) {
      return
    }
    const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isImage) {
      message.error('头像格式仅支持 JPG 或 PNG')
      return
    }
    if (!isLt2M) {
      message.error('头像大小不能超过 2MB')
      return
    }
    const formData = new FormData()
    formData.append('file', file)
    try {
      await uploadAvatar(formData)
      await userStore.getUserInfoAction()
      message.success('头像更新成功')
    } catch (error) {
      console.error(error)
      message.error('头像上传失败，请稍后再试')
    }
  }

  const submitUserInfo = async () => {
    if (!userForm.value.nickName) {
      message.warning('昵称不能为空')
      return
    }
    loading.value = true
    try {
      await updateUserProfile(userForm.value)
      message.success('资料已更新')
      await userStore.getUserInfoAction()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  }

  const submitPwd = async () => {
    if (!pwdForm.value.oldPassword) {
      message.warning('请输入当前密码')
      return
    }
    if (!pwdForm.value.newPassword || pwdForm.value.newPassword.length < 6) {
      message.warning('新密码至少 6 位')
      return
    }
    if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
      message.warning('两次输入的新密码不一致')
      return
    }
    loading.value = true
    try {
      await updateUserPwd(pwdForm.value.oldPassword, pwdForm.value.newPassword)
      message.success('密码修改成功，请重新登录')
      userStore.logout()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  }

  const handleUnfollow = async (id: number) => {
    try {
      await unfollowUser(id)
      message.success('已取消关注')
      await loadMyFollows()
      await loadUserCenterStats()
    } catch (error) {
      console.error(error)
      message.error('这次操作没有成功，请稍后再试')
    }
  }

  const editArticle = (id: number) => {
    void router.push(`/posts/new?id=${id}`)
  }

  const removeArticle = async (article: UserArticleItem) => {
    const canDelete = article.status === 1 || article.status === 3
    if (!canDelete) {
      const draftLabel = dictStore.resolveLabel('blog_status', 3) || '草稿'
      const pendingLabel = dictStore.resolveLabel('blog_status', 1) || '审核中'
      message.warning(`这篇文章当前还不能删除，只有${draftLabel}和${pendingLabel}的文章可以删除`)
      return
    }
    if (!window.confirm(`确定删除文章《${article.title}》吗？`)) {
      return
    }
    try {
      await deleteArticle(article.id)
      message.success('文章已删除')
      if (myArticles.value.length === 1 && articlePage.value > 1) {
        articlePage.value -= 1
      }
      await loadMyArticles()
      await loadUserCenterStats()
    } catch (error) {
      console.error(error)
      message.error('删除没有成功，请稍后再试')
    }
  }

  const goToArticle = (id: number) => {
    void router.push(`/article/${id}`)
  }

  const goToUserHome = (userId?: number) => {
    if (userId) {
      void router.push(`/user?id=${userId}`)
    }
  }

  const handleTabChange = async (tab: UserCenterTab) => {
    activeTab.value = tab
    if (tab === 'articles') {
      await loadMyArticles()
    }
    if (tab === 'collects') {
      await loadMyCollects()
    }
    if (tab === 'follows') {
      await loadMyFollows()
    }
    if (tab === 'fans') {
      await loadMyFans()
    }
  }

  const handleArticlePageChange = async (page: number) => {
    articlePage.value = page
    await loadMyArticles()
  }

  const handleArticleStatusChange = async (status?: number) => {
    articleStatus.value = status
    articlePage.value = 1
    await loadMyArticles()
  }

  const handleCollectPageChange = async (page: number) => {
    collectPage.value = page
    await loadMyCollects()
  }

  const handleCollectTypeChange = async (type: 'all' | 'article' | 'say' | 'treehole') => {
    collectType.value = type
    collectPage.value = 1
    await loadMyCollects()
  }

  const handleCancelCollect = async (item: UserCollectItem) => {
    try {
      await uncollectTarget(item.targetType, item.targetId)
      message.success('已取消收藏')
      if (myCollects.value.length === 1 && collectPage.value > 1) {
        collectPage.value -= 1
      }
      await loadMyCollects()
      await loadUserCenterStats()
    } catch (error) {
      console.error(error)
      message.error('取消收藏失败，请稍后再试')
    }
  }

  const handleFollowPageChange = async (page: number) => {
    followPage.value = page
    await loadMyFollows()
  }

  const handleFollowStateChange = async (state: 'all' | 'active' | 'inactive') => {
    followState.value = state
    followPage.value = 1
    await loadMyFollows()
  }

  const handleFansPageChange = async (page: number) => {
    fansPage.value = page
    await loadMyFans()
  }

  const handleFansStateChange = async (state: 'all' | 'active' | 'inactive') => {
    fansState.value = state
    fansPage.value = 1
    await loadMyFans()
  }

  onMounted(async () => {
    if (!isLoggedIn.value) {
      void router.push('/login')
      return
    }
    await loadUserInfo()
    await loadUserCenterStats()
  })

  return {
    activeTab,
    loading,
    userForm,
    pwdForm,
    userStats,
    articlesLoading,
    myArticles,
    articlePage,
    articleSize,
    articleTotal,
    articleStatus,
    collectsLoading,
    myCollects,
    collectPage,
    collectSize,
    collectTotal,
    collectType,
    followsLoading,
    myFollows,
    followPage,
    followSize,
    followTotal,
    followState,
    fansLoading,
    myFans,
    fansPage,
    fansSize,
    fansTotal,
    fansState,
    avatarUrl,
    displayName,
    displayBio,
    formatDate,
    handleAvatarChange,
    submitUserInfo,
    submitPwd,
    handleUnfollow,
    editArticle,
    removeArticle,
    goToArticle,
    goToUserHome,
    handleTabChange,
    handleArticleStatusChange,
    handleArticlePageChange,
    handleCollectTypeChange,
    handleCancelCollect,
    handleCollectPageChange,
    handleFollowStateChange,
    handleFollowPageChange,
    handleFansStateChange,
    handleFansPageChange
  }
}
