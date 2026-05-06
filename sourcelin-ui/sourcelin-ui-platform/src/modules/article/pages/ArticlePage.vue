<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'
import ArticleHero from '@/modules/article/components/ArticleHero.vue'
import ArticleBody from '@/modules/article/components/ArticleBody.vue'
import ArticleActions from '@/modules/article/components/ArticleActions.vue'
import ArticleAuthorCard from '@/modules/article/components/ArticleAuthorCard.vue'
import ArticleTocCard from '@/modules/article/components/ArticleTocCard.vue'
import { useArticleDetail } from '@/modules/article/composables/useArticleDetail'
import { resolveArticleSourceContext } from '@/modules/article/utils/article-source'
import { useArticleComments } from '@/modules/article/composables/useArticleComments'
import CommentComposer from '@/shared/components/comments/CommentComposer.vue'
import CommentThread from '@/shared/components/comments/CommentThread.vue'

const route = useRoute()
const router = useRouter()
const articleId = ref(0)
const sidebarAreaRef = ref<HTMLElement | null>(null)
const commentsSectionRef = ref<HTMLElement | null>(null)
const sidebarStickable = ref(true)
const commentsVisible = ref(false)
let sidebarResizeObserver: ResizeObserver | null = null

const {
  article,
  articleUser,
  articleTags,
  articleTitle,
  articleSummary,
  articleIdentityLabel,
  issueLabel,
  heroKicker,
  coverImage,
  authorName,
  authorIntroduction,
  currentUserId,
  currentUserAvatar,
  isLoggedIn,
  isLiked,
  isCollected,
  isFollowed,
  likeCount,
  collectCount,
  toc,
  readingCount,
  readingMinutes,
  articleWordCount,
  sectionCount,
  leadStrip,
  loading,
  formatDate,
  handleLike,
  handleCollect,
  handleFollow,
  handleShare,
  goToTag,
  scrollToHeading,
  getTocItemClass
} = useArticleDetail(articleId)

const commentsApi = useArticleComments(articleId, currentUserId, isLoggedIn, () => { router.push('/login') })
const {
  comments,
  commentLoading,
  commentTotal,
  commentPageNum,
  commentPageSize,
  newComment,
  commentPlaceholder,
  commentSubmitting,
  replyCommentId,
  enableLike,
  enableReply,
  handleCommentLike,
  handleReply,
  cancelReply,
  submitComment,
  handleCommentPageChange,
  resetAndReload,
  onCommentInteractionBlocked
} = commentsApi

const articleAuthorId = computed(() => articleUser.value?.id)
const articleSourceContext = computed(() => resolveArticleSourceContext(route))
const breadcrumbSourceLabel = computed(() => articleSourceContext.value.label)
const articleCommentCount = computed(() =>
  Math.max(Number(article.value?.commentCount ?? 0), Number(commentTotal.value ?? 0))
)

function goToSourcePage() {
  void router.push(articleSourceContext.value.to)
}

function syncSidebarStickable() {
  const sidebarElement = sidebarAreaRef.value
  if (!sidebarElement) return
  const stickyTop = 92
  const viewportSafeHeight = window.innerHeight - stickyTop - 24
  sidebarStickable.value = sidebarElement.offsetHeight <= viewportSafeHeight
}

function scrollCommentsSectionIntoView() {
  const section = commentsSectionRef.value
  if (!(section instanceof HTMLElement)) return
  const top = section.getBoundingClientRect().top + window.scrollY - 92
  window.scrollTo({
    top: Math.max(top, 0),
    behavior: 'smooth'
  })
}

async function toggleArticleComments() {
  commentsVisible.value = !commentsVisible.value
  if (!commentsVisible.value) {
    cancelReply()
    return
  }
  await nextTick()
  scrollCommentsSectionIntoView()
}

async function handleArticleCommentPageUpdate(page: number) {
  await handleCommentPageChange(page)
  await nextTick()
  scrollCommentsSectionIntoView()
}

watch(() => route.params.id, async () => {
  articleId.value = Number(route.params.id || 0)
  commentsVisible.value = false
  await resetAndReload()
  await nextTick()
  syncSidebarStickable()
}, { immediate: true })

onMounted(async () => {
  await nextTick()
  syncSidebarStickable()
  window.addEventListener('resize', syncSidebarStickable, { passive: true })
  if (!sidebarAreaRef.value) return
  sidebarResizeObserver = new ResizeObserver(() => {
    syncSidebarStickable()
  })
  sidebarResizeObserver.observe(sidebarAreaRef.value)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', syncSidebarStickable)
  sidebarResizeObserver?.disconnect()
  sidebarResizeObserver = null
})
</script>

<template>
  <div class="article-page" :aria-busy="loading">
    <ArticleHero
      :cover-image="coverImage"
      :article-title="articleTitle"
      :article-summary="articleSummary"
      :issue-label="issueLabel"
      :hero-kicker="heroKicker"
      :author-name="authorName"
      :author-introduction="authorIntroduction"
      :author-avatar="articleUser?.avatar || currentUserAvatar"
      :publish-date="formatDate(article.createTime)"
      :reading-count="readingCount"
      :like-count="likeCount"
      :article-identity-label="articleIdentityLabel"
      :reading-minutes="readingMinutes"
      :article-word-count="articleWordCount"
      :section-count="sectionCount"
      :tag-count="articleTags.length"
      :breadcrumb-source-label="breadcrumbSourceLabel"
      @go-home="router.push('/')"
      @go-source="goToSourcePage"
    />

    <div class="feature-shell">
      <section class="lead-strip">
        <SCard
          v-for="item in leadStrip"
          :key="item.label"
          class="lead-strip-item"
          variant="lite" hoverable
        >
          <span class="lead-strip-label">{{ item.label }}</span>
          <strong class="lead-strip-value">{{ item.value }}</strong>
        </SCard>
        </section>

        <div class="main-layout">
        <div class="content-area">
          <ArticleBody :loading="loading" :summary="articleSummary" :content="article.content" :original-url="article.originalUrl" />
          <ArticleActions
            :is-liked="isLiked"
            :is-collected="isCollected"
            :like-count="likeCount"
            :collect-count="collectCount"
            :comment-count="articleCommentCount"
            :comments-open="commentsVisible"
            @like="handleLike"
            @collect="handleCollect"
            @comment="toggleArticleComments"
            @share="handleShare"
          />

          <div ref="commentsSectionRef" class="comments-section-anchor">
            <SCard class="comments-section" variant="default">
              <div class="section-header">
                <div class="section-title">
                  <SIcon class="section-icon" :icon="appIcons.comment" :size="18" />
                  <span
                    class="section-prelude-label"
                  >读者来信</span>
                  <h2 class="visually-hidden">读者来信</h2>
                </div>
                <div class="section-actions">
                  <span class="comment-badge">{{ articleCommentCount }}</span>
                  <SButton variant="ghost" size="small" @click="toggleArticleComments">
                    {{ commentsVisible ? '收起评论' : '展开评论' }}
                  </SButton>
                </div>
              </div>

              <template v-if="commentsVisible">
                <CommentComposer
                  v-model="newComment"
                  :current-user-avatar="currentUserAvatar"
                  :placeholder="commentPlaceholder"
                  :is-logged-in="isLoggedIn"
                  :comment-submitting="commentSubmitting"
                  :is-replying="Boolean(replyCommentId)"
                  @submit="submitComment"
                  @cancel-reply="cancelReply"
                />
                <CommentThread
                  :comments="comments"
                  :comment-loading="commentLoading"
                  :comment-total="commentTotal"
                  :comment-page-num="commentPageNum"
                  :comment-page-size="commentPageSize"
                  :author-id="articleAuthorId"
                  :enable-like="enableLike"
                  :enable-reply="enableReply"
                  :format-date="formatDate"
                  @like="handleCommentLike"
                  @reply="handleReply"
                  @interaction-blocked="onCommentInteractionBlocked"
                  @update:page="handleArticleCommentPageUpdate"
                />
              </template>
            </SCard>
          </div>
        </div>

        <aside ref="sidebarAreaRef" class="sidebar-area" :class="{ 'is-sticky': sidebarStickable }">
          <ArticleAuthorCard
            :avatar="articleUser?.avatar"
            :author-name="authorName"
            :author-introduction="authorIntroduction"
            :article-count="articleUser?.articleCount"
            :follower-count="articleUser?.followerCount"
            :author-id="articleUser?.id"
            :current-user-id="currentUserId"
            :is-followed="isFollowed"
            @follow="handleFollow"
          />
          <ArticleTocCard :toc="toc" :tags="articleTags" :get-toc-item-class="getTocItemClass" @scroll-to="scrollToHeading" @go-tag="goToTag" />
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.article-page { min-height: 100vh; background: var(--surface-page-content); }

/* 无渐变毛玻璃过渡；与 Hero 的衔接靠略上移 + 正文区底色，背景模糊由 ArticleHero 内滚动驱动（同首页/树洞） */
.feature-shell {
  position: relative;
  z-index: 10;
  width: min(1240px, calc(100% - 48px));
  margin: clamp(-3.25rem, -7vw, -1.75rem) auto 0;
  padding-top: clamp(2rem, 5vw, 3.5rem);
  padding-bottom: 72px;
}

.feature-shell::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: calc(var(--glass-radius) + 10px);
  border: 1px solid var(--border-panel-subtle);
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--surface-panel-soft) 84%, transparent), color-mix(in srgb, var(--surface-page-content) 92%, transparent)),
    var(--surface-page-transition);
  box-shadow:
    var(--highlight-panel-soft),
    var(--shadow-panel-default);
  pointer-events: none;
}

.lead-strip, .main-layout { position: relative; z-index: 1; }
.lead-strip {
  --lead-strip-lift: clamp(0.85rem, 2vw, 1.6rem);
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--page-block-gap);
  padding-inline: clamp(0.4rem, 1.2vw, 0.9rem);
  margin-top: calc(var(--lead-strip-lift) * -1);
  margin-bottom: calc(20px + var(--lead-strip-lift) * 0.45);
}
.lead-strip-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg) var(--spacing-xl);
  text-align: left;
  box-shadow:
    var(--highlight-panel-soft),
    0 18px 34px color-mix(in srgb, var(--background-color-deep) 14%, transparent);
}
.lead-strip-label { color: var(--text-secondary); }
.lead-strip-value { font-size: var(--font-size-lg); color: var(--title-color); }
.main-layout { display: grid; grid-template-columns: minmax(0, min(var(--content-max-width-readable), 100%)) 320px; gap: var(--page-section-gap); align-items: start; justify-content: center; }
.content-area, .sidebar-area { display: flex; flex-direction: column; gap: var(--page-section-gap); }
.comments-section { padding: clamp(1.4rem, 2vw, var(--spacing-xxl)); }
.section-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 22px; flex-wrap: wrap; }
.section-title { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.section-actions { display: inline-flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.section-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
}
.comment-badge { min-width: 44px; padding: var(--spacing-sm) var(--spacing-md); text-align: center; background: var(--surface-panel-chip-accent); color: var(--primary-color); border: 1px solid var(--border-panel-badge-accent); border-radius: 999px; box-shadow: var(--highlight-panel-chip); }
.section-icon { color: var(--primary-color); }
.sidebar-area {
  --sidebar-sticky-top: 92px;
  position: relative;
  align-self: start;
  z-index: 2;
}
.sidebar-area.is-sticky { position: sticky; top: var(--sidebar-sticky-top); }
.sidebar-area :deep(.sidebar-card) { position: relative; }

@include sourcelin-down(lg) { .lead-strip { grid-template-columns: repeat(2, minmax(0, 1fr)); } .main-layout { grid-template-columns: 1fr; } .sidebar-area, .sidebar-area.is-sticky { position: static; top: auto; } .sidebar-area :deep(.sidebar-card) { position: static; } }
@include sourcelin-down(md) {
  .feature-shell {
    width: min(100% - 32px, 100%);
    margin-top: clamp(-2.25rem, -4vw, -1rem);
    padding-top: clamp(1.5rem, 4vw, 2.75rem);
  }
  .lead-strip {
    --lead-strip-lift: clamp(0.5rem, 2vw, 0.95rem);
    padding-inline: 0;
  }
  .section-header, .section-title { display: flex; flex-direction: column; align-items: flex-start; }
  .lead-strip { grid-template-columns: 1fr; }
  .comments-section { padding: 22px; }
}
</style>
