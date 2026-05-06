<template>
  <div class="glass-page user-center-page" :aria-busy="loading">
    <PageShell>
      <SCard
        class="user-hero"
        variant="lite"
      >
        <div class="user-hero__main">
          <div class="user-hero__copy">
            <SSurfaceChip class="article-card-prelude-label" tone="accent">我的</SSurfaceChip>
            <h1 class="user-hero__title">个人主页</h1>
            <p class="user-hero__subtitle">{{ userHeroSubtitle }}</p>
          </div>

          <aside class="user-hero__aside" aria-label="个人摘要">
            <SSurfacePanel class="user-hero__profile-panel" variant="soft">
              <div class="user-hero__profile-head">
                <SUpload
                  class="user-hero__avatar-upload"
                  accept="image/jpeg,image/png"
                  :max="1"
                  :show-file-list="false"
                  :custom-request="handleAvatarUploadRequest"
                >
                  <div class="user-hero__avatar-uploader">
                    <img :src="avatarUrl" class="user-hero__avatar" alt="我的头像">
                    <div class="user-hero__avatar-mask">
                      <span>更换头像</span>
                    </div>
                  </div>
                </SUpload>
                <div class="user-hero__profile-copy">
                  <p class="user-hero__profile-eyebrow">我的信息</p>
                  <h2 class="user-hero__profile-name">{{ displayName }}</h2>
                  <p class="user-hero__profile-bio">{{ displayBio }}</p>
                </div>
              </div>

              <div class="user-hero__actions">
                <SButton type="primary" variant="cta" @click="switchTab('info')">个人资料</SButton>
                <SButton @click="goToNewPost">发布文章</SButton>
                <SButton @click="goToMessageCenter">消息中心</SButton>
              </div>
            </SSurfacePanel>
          </aside>

          <section class="user-hero__summary user-hero__summary--tabs" aria-label="账户数据与功能切换">
            <HeroStatCard
              v-for="tile in heroTabTiles"
              :key="tile.tab"
              class="user-hero__stat"
              :class="{ 'user-hero__stat--active': activeTab === tile.tab }"
              :tone="activeTab === tile.tab ? 'tinted' : 'neutral'"
              :hue="tile.hue"
              :icon="tile.icon"
              :label="tile.label"
              :value="tile.value"
              :aria-label="tile.ariaLabel"
              size="lg"
              action="button"
              @click="switchTab(tile.tab)"
            />
          </section>
        </div>
      </SCard>

      <div class="profile-workspace">
        <SSurfacePanel tag="section" class="profile-content">
          <header class="profile-section-toolbar">
            <div class="profile-section-toolbar__lead">
              <span class="profile-section-toolbar__dot" aria-hidden="true" />
              <div class="profile-section-toolbar__text">
                <h2 class="profile-section-toolbar__title">{{ sectionMeta.title }}</h2>
                <p class="profile-section-toolbar__desc">{{ sectionMeta.description }}</p>
              </div>
            </div>
            <div class="profile-section-toolbar__chips">
              <template v-if="isAccountConfigTab">
                <SSurfaceChip
                  tag="button"
                  variant="button"
                  type="button"
                  class="profile-section-chip-btn"
                  :class="{ 'is-active': activeTab === 'info' }"
                  @click="switchTab('info')"
                >
                  个人资料
                </SSurfaceChip>
                <SSurfaceChip
                  tag="button"
                  variant="button"
                  type="button"
                  class="profile-section-chip-btn"
                  :class="{ 'is-active': activeTab === 'security' }"
                  @click="switchTab('security')"
                >
                  安全设置
                </SSurfaceChip>
                <SSurfaceChip class="profile-section-chip profile-section-chip--muted">资料完成度 {{ profileCompletion }}%</SSurfaceChip>
              </template>
              <template v-else>
                <div class="profile-section-toolbar__chip-group">
                  <SSurfaceChip
                    v-for="chip in activeContentFilterChips"
                    :key="chip.key"
                    tag="button"
                    variant="button"
                    type="button"
                    class="profile-section-chip-btn"
                    :class="{ 'is-active': activeContentFilterKey === chip.key }"
                    @click="setActiveContentFilter(chip.key)"
                  >
                    {{ chip.label }}
                  </SSurfaceChip>
                </div>
                <SSurfaceChip v-if="activeMetricValue" class="profile-section-chip profile-section-chip--muted">{{ activeMetricValue }}</SSurfaceChip>
              </template>
            </div>
          </header>

          <div class="profile-section-body">
            <UserInfoForm
              v-if="activeTab === 'info'"
              v-model="userForm"
              :loading="loading"
              @submit="submitUserInfo"
            />
            <UserPasswordForm
              v-if="activeTab === 'security'"
              v-model="pwdForm"
              :loading="loading"
              @submit="submitPwd"
            />
            <UserArticleList
              v-if="activeTab === 'articles'"
              :loading="articlesLoading"
              :items="filteredMyArticles"
              :page="displayArticlePage"
              :size="articleSize"
              :total="displayArticleTotal"
              :format-date="formatDate"
              @open="goToArticle"
              @edit="editArticle"
              @delete="removeArticle"
              @update:page="handleArticlePageChange"
            />
            <UserCollectList
              v-if="activeTab === 'collects'"
              :loading="collectsLoading"
              :items="filteredMyCollects"
              :page="displayCollectPage"
              :size="collectSize"
              :total="displayCollectTotal"
              :format-date="formatDate"
              @open="goToArticle"
              @cancel="handleCancelCollect"
              @update:page="handleCollectPageChange"
            />
            <UserFollowList
              v-if="activeTab === 'follows'"
              :loading="followsLoading"
              :items="filteredMyFollows"
              :page="displayFollowPage"
              :size="followSize"
              :total="displayFollowTotal"
              @open="goToUserHome"
              @unfollow="handleUnfollow"
              @update:page="handleFollowPageChange"
            />
            <UserFanList
              v-if="activeTab === 'fans'"
              :loading="fansLoading"
              :items="filteredMyFans"
              :page="displayFansPage"
              :size="fansSize"
              :total="displayFansTotal"
              @open="goToUserHome"
              @update:page="handleFansPageChange"
            />
          </div>
        </SSurfacePanel>
      </div>
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, type Component } from 'vue'
import { useRouter } from 'vue-router'
import UserArticleList from '@/modules/user/components/UserArticleList.vue'
import UserCollectList from '@/modules/user/components/UserCollectList.vue'
import UserFanList from '@/modules/user/components/UserFanList.vue'
import UserFollowList from '@/modules/user/components/UserFollowList.vue'
import UserInfoForm from '@/modules/user/components/UserInfoForm.vue'
import UserPasswordForm from '@/modules/user/components/UserPasswordForm.vue'
import { useUserCenter, type UserCenterTab } from '@/modules/user/composables/useUserCenter'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SUpload from '@/shared/components/ui/SUpload.vue'
import { appIcons } from '@/shared/components/ui/icons'

const router = useRouter()

const {
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
  fansLoading,
  myFans,
  fansPage,
  fansSize,
  fansTotal,
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
} = useUserCenter()

const sectionMetaMap: Record<UserCenterTab, { title: string; description: string }> = {
  info: {
    title: '基本资料',
    description: '修改昵称、手机号、邮箱和简介。'
  },
  security: {
    title: '安全设置',
    description: '修改登录密码，保护账号安全。'
  },
  articles: {
    title: '我的博客',
    description: '查看、编辑或删除已发布的文章。'
  },
  collects: {
    title: '我的收藏',
    description: '浏览你收藏过的文章与内容。'
  },
  follows: {
    title: '关注列表',
    description: '管理你关注的用户。'
  },
  fans: {
    title: '我的粉丝',
    description: '查看关注你的用户。'
  }
}

const userHeroSubtitle = '从上方卡片切换模块，核心数据与常用入口集中在头部区域。'

const sectionMeta = computed(() => sectionMetaMap[activeTab.value])
const isAccountConfigTab = computed(() => activeTab.value === 'info' || activeTab.value === 'security')

const profileCompletion = computed(() => {
  const fields = [userForm.value.nickName, userForm.value.phonenumber, userForm.value.email, userForm.value.introduction]
  const filled = fields.filter((item) => Boolean(String(item || '').trim())).length
  return Math.round((filled / fields.length) * 100)
})

type HeroTabTile = {
  tab: UserCenterTab
  label: string
  value: string
  icon: Component
  ariaLabel: string
  hue: number
}

type ContentToolbarChip = {
  key: string
  label: string
}

type ArticleFilterKey = 'all' | 'published' | 'unpublished'
type CollectFilterKey = 'all' | 'article' | 'say' | 'treehole'
type RelationFilterKey = 'all' | 'active' | 'inactive'

const articleFilterKey = ref<ArticleFilterKey>('all')
const collectFilterKey = ref<CollectFilterKey>(collectType.value)
const followFilterKey = ref<RelationFilterKey>('all')
const fansFilterKey = ref<RelationFilterKey>('all')

const heroTabTiles = computed<HeroTabTile[]>(() => {
  const s = userStats.value
  return [
    {
      tab: 'articles',
      label: '我的博客',
      value: String(s.articleCount),
      icon: appIcons.document,
      ariaLabel: `博客 ${s.articleCount} 篇`,
      hue: 218
    },
    {
      tab: 'collects',
      label: '我的收藏',
      value: String(s.collectCount),
      icon: appIcons.collect,
      ariaLabel: `收藏 ${s.collectCount} 条`,
      hue: 152
    },
    {
      tab: 'follows',
      label: '我的关注',
      value: String(s.followCount),
      icon: appIcons.add,
      ariaLabel: `关注用户 ${s.followCount} 人`,
      hue: 36
    },
    {
      tab: 'fans',
      label: '我的粉丝',
      value: String(s.fansCount),
      icon: appIcons.visitors,
      ariaLabel: `我的粉丝 ${s.fansCount} 人`,
      hue: 336
    }
  ]
})

const contentToolbarChips = computed<ContentToolbarChip[]>(() => {
  if (activeTab.value === 'articles') {
    return [
      { key: 'all', label: '全部' },
      { key: 'published', label: '已发布' },
      { key: 'unpublished', label: '未发布' }
    ]
  }
  if (activeTab.value === 'collects') {
    return [
      { key: 'all', label: '全部' },
      { key: 'article', label: '文章' },
      { key: 'say', label: '说说' },
      { key: 'treehole', label: '树洞' }
    ]
  }
  if (activeTab.value === 'follows' || activeTab.value === 'fans') {
    return [
      { key: 'all', label: '全部' },
      { key: 'active', label: '有效' },
      { key: 'inactive', label: '失效' }
    ]
  }
  return []
})

const activeContentFilterChips = computed(() => contentToolbarChips.value)

const activeContentFilterKey = computed(() => {
  if (activeTab.value === 'articles') {
    return articleFilterKey.value
  }
  if (activeTab.value === 'collects') {
    return collectFilterKey.value
  }
  if (activeTab.value === 'follows') {
    return followFilterKey.value
  }
  if (activeTab.value === 'fans') {
    return fansFilterKey.value
  }
  return 'all'
})

const filteredMyArticles = computed(() => {
  return myArticles.value
})

const filteredMyCollects = computed(() => {
  return myCollects.value
})

const filteredMyFollows = computed(() => {
  return myFollows.value
})

const filteredMyFans = computed(() => {
  return myFans.value
})

const displayArticleTotal = computed(() => articleTotal.value)
const displayCollectTotal = computed(() => collectTotal.value)
const displayFollowTotal = computed(() => followTotal.value)
const displayFansTotal = computed(() => fansTotal.value)

const displayArticlePage = computed(() => articlePage.value)
const displayCollectPage = computed(() => collectPage.value)
const displayFollowPage = computed(() => followPage.value)
const displayFansPage = computed(() => fansPage.value)

const activeMetricValue = computed(() => {
  if (isAccountConfigTab.value) {
    return ''
  }
  switch (activeTab.value) {
    case 'articles':
      return `${displayArticleTotal.value} 篇`
    case 'collects':
      return `${displayCollectTotal.value} 条`
    case 'follows':
      return `${displayFollowTotal.value} 人`
    case 'fans':
      return `${displayFansTotal.value} 人`
    default:
      return ''
  }
})

interface UploadRequestOptions {
  file: {
    file?: File
  }
  onFinish: () => void
  onError: () => void
}

const handleAvatarUploadRequest = async ({ file, onFinish, onError }: UploadRequestOptions) => {
  if (!file.file) {
    onError()
    return
  }
  await handleAvatarChange(file.file)
  onFinish()
}

function switchTab(tab: UserCenterTab) {
  void handleTabChange(tab)
}

function setActiveContentFilter(filterKey: string) {
  if (activeTab.value === 'articles' && (filterKey === 'all' || filterKey === 'published' || filterKey === 'unpublished')) {
    articleFilterKey.value = filterKey
    if (filterKey === 'published') {
      void handleArticleStatusChange(2)
    } else if (filterKey === 'unpublished') {
      void handleArticleStatusChange(0)
    } else {
      void handleArticleStatusChange(undefined)
    }
    return
  }
  if (activeTab.value === 'collects' && (filterKey === 'all' || filterKey === 'article' || filterKey === 'say' || filterKey === 'treehole')) {
    collectFilterKey.value = filterKey
    void handleCollectTypeChange(filterKey)
    return
  }
  if (activeTab.value === 'follows' && (filterKey === 'all' || filterKey === 'active' || filterKey === 'inactive')) {
    followFilterKey.value = filterKey
    void handleFollowStateChange(filterKey)
    return
  }
  if (activeTab.value === 'fans' && (filterKey === 'all' || filterKey === 'active' || filterKey === 'inactive')) {
    fansFilterKey.value = filterKey
    void handleFansStateChange(filterKey)
  }
}

function goToNewPost() {
  void router.push('/posts/new')
}

function goToMessageCenter() {
  void router.push({ name: 'MessageCenter' })
}
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.user-center-page {
  --content-max-width: 1120px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.user-center-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.user-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

/* 窄屏：与 profile 工作区同为单列栈式，行距适中 */
.user-hero__main {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto;
  column-gap: var(--spacing-xxl);
  row-gap: var(--spacing-xxl);
  align-items: start;
}

/* 上排：文案 + 资料卡；下排：账户数据通栏 */
.user-hero__main > .user-hero__summary {
  grid-column: 1 / -1;
}

.user-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.user-hero__title {
  margin: 0;
  font-size: clamp(2.6rem, 5vw, 4rem);
  line-height: 0.96;
  letter-spacing: 0.03em;
  color: var(--title-color);
}

.user-hero__subtitle {
  max-width: 40rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.9;
}

.user-hero__summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}

@include sourcelin-up(md) {
  .user-hero__summary--tabs {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@include sourcelin-up(lg) {
  .user-hero__summary--tabs {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

.user-hero__profile-panel {
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.user-hero__profile-head {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.user-hero__avatar-upload {
  flex-shrink: 0;
}

.user-hero__avatar-uploader {
  position: relative;
  inline-size: 88px;
  block-size: 88px;
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

.user-hero__avatar-uploader:hover {
  transform: translateY(-2px) scale(1.02);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--shadow-panel-hover);
}

.user-hero__avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.user-hero__avatar-mask {
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

.user-hero__avatar-uploader:hover .user-hero__avatar-mask {
  opacity: 1;
}

.user-hero__profile-copy {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.user-hero__profile-eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.user-hero__profile-name {
  margin: 0;
  font-size: 1.4rem;
  color: var(--title-color);
}

.user-hero__profile-bio {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.65;
}

.user-hero__actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--spacing-sm);
}

.user-hero__stat--active {
  border-color: var(--border-panel-badge-accent);
  box-shadow: var(--shadow-panel-hover), var(--highlight-panel-soft);
}

.profile-workspace {
  display: block;
}

/* 与 Foundation `$breakpoint-lg`（1080px）一致：hero 保持双列信息布局 */
@include sourcelin-up(lg) {
  .user-hero__main {
    grid-template-columns: minmax(0, 1.18fr) minmax(280px, 0.82fr);
    row-gap: calc(var(--spacing-xxxl) + var(--spacing-md));
  }
}

.profile-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 680px;
  padding: clamp(1.25rem, 2vw, var(--spacing-xxl)) clamp(1rem, 1.8vw, var(--spacing-xxl));
}

.profile-section-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
  padding-bottom: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-panel-subtle);
}

.profile-section-toolbar__lead {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;
  flex: 1 1 12rem;
}

.profile-section-toolbar__text {
  display: grid;
  gap: var(--spacing-xs);
  min-width: 0;
}

.profile-section-toolbar__dot {
  width: 10px;
  height: 10px;
  margin-top: 10px;
  border-radius: 999px;
  background: color-mix(in srgb, var(--primary-color) 78%, transparent);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary-color) 12%, transparent);
  flex-shrink: 0;
}

.profile-section-toolbar__title {
  margin: 0;
  font-size: clamp(1.15rem, 2.2vw, 1.35rem);
  font-weight: 700;
  line-height: 1.25;
  letter-spacing: 0.02em;
  color: var(--title-color);
}

.profile-section-toolbar__desc {
  margin: 0;
  max-width: 36rem;
  font-size: var(--font-size-md, 14px);
  line-height: 1.65;
  color: var(--text-secondary);
}

.profile-section-toolbar__chips {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.profile-section-toolbar__chip-group {
  display: inline-flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.profile-section-chip {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0.28rem 0.72rem;
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  letter-spacing: 0.04em;
}

.profile-section-chip-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0.32rem 0.78rem;
  border-radius: 999px;
  border: 1px solid var(--border-panel-chip);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: border-color var(--transition-base), color var(--transition-base), background var(--transition-base), transform var(--transition-base), box-shadow var(--transition-base);
}

.profile-section-chip-btn:hover {
  transform: translateY(-1px);
  border-color: var(--border-interactive-hover);
  color: var(--primary-color);
}

.profile-section-chip-btn.is-active {
  color: var(--primary-color);
  border-color: var(--border-panel-badge-accent);
  background: var(--surface-panel-chip-accent);
  box-shadow: var(--highlight-panel-chip);
}

.profile-section-chip--muted {
  font-weight: 500;
  color: var(--text-secondary);
}

.profile-section-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  min-height: 0;
}

.profile-section-body > :deep(.content-section) {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  height: auto;
}

.profile-section-body > :deep(.content-section) .empty-tip {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

@include sourcelin-down(lg) {
  .user-hero__summary--tabs {
    gap: var(--spacing-sm);
  }
}

@include sourcelin-down(md) {
  .user-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .user-hero__profile-panel,
  .profile-content {
    padding: var(--spacing-lg);
  }

  .user-hero__summary--tabs {
    gap: var(--spacing-xs);
  }

  .user-hero__actions {
    grid-template-columns: 1fr;
  }

  .user-hero__stat {
    gap: var(--spacing-md);
    min-height: 4.5rem;
    padding: var(--spacing-sm) var(--spacing-md);
  }
}

</style>
