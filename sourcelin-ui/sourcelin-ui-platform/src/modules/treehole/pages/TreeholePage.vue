<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import treeholeHeroBg from '@/assets/images/backgrounds/home-bg.jpg'
import { useMastheadScrollBlur } from '@/shared/composables/useMastheadScrollBlur'
import PageHeader from '@/shared/components/layout/PageHeader.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import CommentComposer from '@/shared/components/comments/CommentComposer.vue'
import CommentThread from '@/shared/components/comments/CommentThread.vue'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SkeletonTreeholeList from '@/shared/components/feedback/skeletons/SkeletonTreeholeList.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import SPagination from '@/shared/components/ui/SPagination.vue'
import SPopconfirm from '@/shared/components/ui/SPopconfirm.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import STextarea from '@/shared/components/ui/STextarea.vue'
import { appIcons } from '@/shared/components/ui/icons'
import MessageBarrage from '@/modules/treehole/components/MessageBarrage.vue'
import { useTreeholePage } from '@/modules/treehole/composables/useTreeholePage'

const {
  draft,
  treeholes,
  barrageList,
  loading,
  submitting,
  activeTreeholeId,
  treeholePage,
  treeholePageSize,
  treeholeTotal,
  treeholeTotalPages,
  isLoggedIn,
  currentUserId,
  currentUserAvatar,
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
  commentAnonymousLabel,
  loadTreeholes,
  loadBarrages,
  handleTreeholePageChange,
  submitTreehole,
  handleTreeholeLike,
  handleTreeholeCollect,
  handleTreeholeDelete,
  toggleComments,
  handleCommentLike,
  handleCommentDelete,
  handleReply,
  cancelReply,
  submitComment,
  handleCommentPageChange,
  onCommentInteractionBlocked
} = useTreeholePage()
const messagesSectionRef = ref<HTMLElement | null>(null)

const { blurPx: mastheadBackdropBlurPx } = useMastheadScrollBlur()

const pageSubtitle = computed(() =>
  isLoggedIn.value ? '记录心情，也与大家轻声对话。' : '这是访客留言树洞，保留匿名的自由，也保留被回应的可能。'
)
const cardKicker = computed(() => (isLoggedIn.value ? '发布树洞' : '匿名投递'))
const discussionPanelTitle = computed(() => (isLoggedIn.value ? '讨论' : '匿名回声'))
const threadEmptyText = computed(() =>
  isLoggedIn.value ? '还没有回应，来做第一个回复吧。' : '还没有回应，做第一个在这里留声的人。'
)
const mainTextareaPlaceholder = computed(() =>
  isLoggedIn.value
    ? '写点什么，分享给大家也好，自己留个记号也好。'
    : '写下你想留下的一句话、一个念头，或一段没人知道的心情...'
)
const submitTreeholeLabel = computed(() => (isLoggedIn.value ? '投递树洞' : '留言'))
const formatNickname = (nickname?: string) => nickname?.trim() || (isLoggedIn.value ? '洞友' : '匿名洞友')
const formatCreateTime = (createTime?: string) => createTime?.split(' ')[0]?.trim() || '刚刚'
const canDeleteTreehole = (item: (typeof treeholes.value)[number]) =>
  currentUserId.value != null
  && item.userId != null
  && currentUserId.value === item.userId

function handleHeroInputKeydown(event: KeyboardEvent) {
  if (event.isComposing || event.shiftKey || event.key !== 'Enter') {
    return
  }
  event.preventDefault()
  if (!draft.value.trim() || submitting.value) {
    return
  }
  void submitTreehole()
}

function scrollActiveCommentsIntoView() {
  const activeSection = document.querySelector('.treehole-comments--open')
  if (activeSection instanceof HTMLElement) {
    const top = activeSection.getBoundingClientRect().top + window.scrollY - 92
    window.scrollTo({
      top: Math.max(top, 0),
      behavior: 'smooth'
    })
  }
}

async function handleTreeholeToggleComments(item: (typeof treeholes.value)[number]) {
  await toggleComments(item)
  if (activeTreeholeId.value === item.id) {
    await nextTick()
    scrollActiveCommentsIntoView()
  }
}

async function handleTreeholeCommentPageUpdate(page: number) {
  await handleCommentPageChange(page)
  await nextTick()
  scrollActiveCommentsIntoView()
}

function scrollMessagesSectionIntoView() {
  const section = messagesSectionRef.value
  if (!(section instanceof HTMLElement)) {
    return
  }

  const top = section.getBoundingClientRect().top + window.scrollY - 92
  window.scrollTo({
    top: Math.max(top, 0),
    behavior: 'smooth'
  })
}

async function handleTreeholeListPageUpdate(page: number) {
  await handleTreeholePageChange(page)
  await nextTick()
  scrollMessagesSectionIntoView()
}

onMounted(() => {
  void loadTreeholes()
  void loadBarrages()
})
</script>

<template>
  <div class="treehole-page">
    <div class="barrage-hero">
      <div
        class="barrage-hero-backdrop"
        :style="{
          backgroundImage: `url(${treeholeHeroBg})`,
          filter: `saturate(0.92) blur(${mastheadBackdropBlurPx}px)`
        }"
      />
      <div class="barrage-container"><MessageBarrage :list="barrageList" /></div>
      <div class="message-in">
        <h2 class="message-title">树洞</h2>
        <SSurfacePanel tag="form" class="input-row" variant="soft" @submit.prevent="submitTreehole">
          <SInput
            v-model="draft"
            class="message-input"
            variant="filled"
            placeholder="留下点什么啦~"
            maxlength="60"
            @keydown="handleHeroInputKeydown"
          />
          <button
            v-if="draft.trim()"
            class="send-btn"
            type="submit"
            :disabled="submitting"
          >发射</button>
        </SSurfacePanel>
      </div>
    </div>

    <div class="glass-page treehole-main">
      <PageShell>
        <SSurfacePanel class="treehole-workspace" variant="soft">
          <PageHeader class="treehole-header" title="树洞" :subtitle="pageSubtitle" :icon="appIcons.comment" />

          <section class="message-input-section">
            <SSurfacePanel class="input-card">
              <div class="card-header">
                <div class="card-heading">
                  <SSurfaceChip size="xs" variant="label" class="card-kicker">{{ cardKicker }}</SSurfaceChip>
                  <span class="card-title-copy"><SIcon :icon="appIcons.edit" :size="22" />写下一条树洞</span>
                </div>
              </div>
              <SSurfacePanel class="input-wrapper" variant="inset">
                <STextarea v-model="draft" :placeholder="mainTextareaPlaceholder" :rows="4" :maxlength="500" show-count />
              </SSurfacePanel>
              <div class="card-footer">
                <span class="char-count">{{ draft.length }} / 500</span>
                <SButton class="submit-btn" variant="site" :loading="submitting" :disabled="!draft.trim()" @click="submitTreehole">
                  <template #icon>
                    <SIcon :icon="appIcons.send" :size="18" />
                  </template>
                  {{ submitTreeholeLabel }}
                </SButton>
              </div>
            </SSurfacePanel>
          </section>

          <section ref="messagesSectionRef" class="messages-section">
            <div class="section-header">
              <h2 class="section-title"><SIcon :icon="appIcons.comment" :size="22" />树洞流<span class="message-count">{{ treeholes.length }}</span></h2>
            </div>

            <div v-if="loading && !treeholes.length" class="loading-state" aria-busy="true" aria-live="polite">
              <SkeletonTreeholeList :count="4" />
            </div>

            <div v-else-if="treeholes.length" class="messages-list">
              <TransitionGroup name="message-list">
                <SSurfacePanel
                  v-for="item in treeholes"
                  :key="item.id"
                  tag="article"
                  class="message-card"
                  variant="soft"
                  interactive
                >
                  <div class="message-card__shell">
                    <div class="message-header">
                      <SSurfacePanel class="avatar" variant="inset"><SIcon :icon="appIcons.user" :size="20" /></SSurfacePanel>
                      <div class="user-info">
                        <span class="username">{{ formatNickname(item.nickname) }}</span>
                        <span class="time">{{ formatCreateTime(item.createTime) }}</span>
                      </div>
                      <SSurfaceChip size="sm" variant="badge" class="message-badge">主帖</SSurfaceChip>
                    </div>

                    <SSurfacePanel class="message-content" variant="inset">
                      <p>{{ item.content }}</p>
                    </SSurfacePanel>

                    <div class="message-actions">
                      <SSurfaceChip
                        tag="button" size="sm" variant="button"
                        class="message-action message-action--like"
                        :class="{ active: item.likedByMe }"
                        type="button"
                        @click="handleTreeholeLike(item)"
                      >
                        <SIcon :icon="item.likedByMe ? appIcons.heartFilled : appIcons.heart" :size="18" />
                        <span>{{ item.likeCount || 0 }}</span>
                      </SSurfaceChip>
                      <SSurfaceChip
                        tag="button"
                        size="sm"
                        variant="button"
                        class="message-action message-action--collect"
                        :class="{ active: item.collectedByMe }"
                        type="button"
                        @click="handleTreeholeCollect(item)"
                      >
                        <SIcon :icon="appIcons.star" :size="18" />
                        <span>{{ item.collectCount || 0 }}</span>
                      </SSurfaceChip>
                      <SSurfaceChip
                        tag="button"
                        size="sm"
                        variant="button"
                        class="message-action message-action--comment"
                        type="button"
                        :class="{ active: activeTreeholeId === item.id }"
                        @click="handleTreeholeToggleComments(item)"
                      >
                        <SIcon :icon="appIcons.comment" :size="18" />
                        <span>{{ item.commentCount || 0 }}</span>
                        <span>{{ activeTreeholeId === item.id ? '收起讨论' : '展开讨论' }}</span>
                      </SSurfaceChip>
                      <SPopconfirm
                        v-if="canDeleteTreehole(item)"
                        :show-icon="false"
                        positive-text="确认删除"
                        negative-text="取消"
                        @positive-click="handleTreeholeDelete(item)"
                      >
                        <template #trigger>
                          <SSurfaceChip
                            tag="button"
                            size="sm"
                            variant="button"
                            class="message-action message-action--danger"
                            type="button"
                            @click.stop
                          >
                            <SIcon :icon="appIcons.trash" :size="18" />
                            <span>删除</span>
                          </SSurfaceChip>
                        </template>
                        删除后不可恢复，确定继续吗？
                      </SPopconfirm>
                    </div>

                    <section
                      v-if="activeTreeholeId === item.id"
                      class="treehole-comments"
                      :class="{ 'treehole-comments--open': activeTreeholeId === item.id }"
                    >
                      <div class="treehole-comments__header">
                        <div class="treehole-comments__headline">
                          <span class="treehole-comments__title">{{ discussionPanelTitle }} {{ commentTotal }}</span>
                          <span class="treehole-comments__summary">{{ activeTreeholeId === item.id ? '已展开全部' : '查看讨论' }}</span>
                        </div>
                        <span class="treehole-comments__meta">{{ commentTotal }} 条回应</span>
                      </div>

                      <div class="treehole-comments__composer-wrap">
                        <CommentComposer
                          v-model="newComment"
                          :current-user-avatar="currentUserAvatar"
                          :placeholder="commentPlaceholder"
                          :is-logged-in="isLoggedIn"
                          :is-anonymous-allowed="true"
                          :comment-submitting="commentSubmitting"
                          :is-replying="Boolean(replyCommentId)"
                          @submit="submitComment"
                          @cancel-reply="cancelReply"
                        />
                      </div>

                      <CommentThread
                        class="treehole-comments__thread"
                        :comments="comments"
                        :comment-loading="commentLoading"
                        :comment-total="commentTotal"
                        :comment-page-num="commentPageNum"
                        :comment-page-size="commentPageSize"
                        :current-user-id="currentUserId"
                        :enable-like="enableLike"
                        :enable-reply="enableReply"
                        :enable-delete="true"
                        :format-date="formatCreateTime"
                        :empty-text="threadEmptyText"
                        :anonymous-label="commentAnonymousLabel"
                        @like="handleCommentLike"
                        @delete="handleCommentDelete"
                        @reply="handleReply"
                        @interaction-blocked="onCommentInteractionBlocked"
                        @update:page="handleTreeholeCommentPageUpdate"
                      />
                    </section>
                  </div>
                </SSurfacePanel>
              </TransitionGroup>
            </div>

            <EmptyState
              v-else
              class="treehole-empty-state"
              variant="page"
              icon="comment"
              title="暂无树洞"
              message="做第一个在这里留下心情的人。"
            />

            <div
              v-if="treeholeTotal > treeholePageSize"
              class="treehole-pagination"
            >
              <div class="treehole-pagination__summary" aria-live="polite">
                <span>第 {{ treeholePage }} / {{ treeholeTotalPages || 1 }} 页</span>
                <span>共 {{ treeholeTotal }} 条树洞</span>
              </div>
              <SPagination
                :page="treeholePage"
                :page-size="treeholePageSize"
                :item-count="treeholeTotal"
                simple
                @update:page="handleTreeholeListPageUpdate"
              />
            </div>
          </section>
        </SSurfacePanel>
      </PageShell>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.treehole-page {
  position: relative;
  min-height: 100vh;
  background: var(--surface-page-content);
  isolation: isolate;
}

.treehole-page::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background: url('@/assets/images/backgrounds/home-bg.jpg') center top / cover no-repeat;
  opacity: 1;
  transform: scale(1.02);
  filter: saturate(0.92);
}

.treehole-page::after {
  content: '';
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background: none;
}

.barrage-hero { position: relative; height: 100vh; min-height: 480px; overflow: hidden; z-index: 1; background: transparent; }
.barrage-hero-backdrop {
  position: absolute;
  inset: 0;
  z-index: 0;
  background-position: center top;
  background-size: cover;
  background-repeat: no-repeat;
  transform: scale(1.02);
  pointer-events: none;
  will-change: filter;
  animation: treehole-hero-backdrop-in 2s ease forwards;
}
.barrage-container { position: absolute; inset: 0; overflow: hidden; z-index: 1; pointer-events: none; }
.message-in { position: absolute; left: 50%; top: 40%; transform: translate(-50%, -50%); color: var(--text-color-light); text-align: center; width: min(560px, calc(100% - 32px)); z-index: 10; }
.message-title { display: inline-flex; align-items: center; justify-content: center; padding: 0.58rem 1.4rem; margin-bottom: 1rem; border-radius: 999px; color: var(--text-color-light); font-size: clamp(1.8rem, 4vw, 2.5rem); background: color-mix(in srgb, var(--surface-panel-chip-accent) 52%, transparent); border: 1px solid color-mix(in srgb, var(--border-panel-badge-accent) 78%, transparent); box-shadow: var(--highlight-panel-chip), var(--shadow-panel-soft); backdrop-filter: blur(calc(var(--glass-blur) - 2px)) saturate(calc(var(--glass-saturate) + 4%)); }
.input-row {
  display: flex;
  align-items: center;
  gap: 0.42rem;
  padding: 0.25rem 0.25rem 0.25rem 0.78rem;
  border-radius: 999px;
  border: 1px solid var(--border-panel-subtle);
  background: var(--surface-panel-soft);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  backdrop-filter: blur(calc(var(--glass-blur) + 4px)) saturate(var(--glass-saturate));
  transition: border-color var(--transition-base), box-shadow var(--transition-base), background var(--transition-base);
}
.input-row:focus-within {
  border-color: var(--primary-color);
  background: var(--surface-panel-default);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 16%, transparent), var(--shadow-panel-hover);
}
.message-input {
  flex: 1;
  --s-input-min-height: 38px;
  --s-input-border-radius: 999px;
}
.message-input :deep(.n-input-wrapper) {
  padding: 0 !important;
  min-height: 38px !important;
}
.message-input,
.message-input:hover,
.message-input:focus,
.message-input.n-input--focus,
.message-input :deep(.n-input),
.message-input :deep(.n-input:hover),
.message-input :deep(.n-input:focus),
.message-input :deep(.n-input-wrapper),
.message-input :deep(.n-input-wrapper:hover),
.message-input :deep(.n-input-wrapper:focus),
.message-input :deep(.n-input-wrapper.n-input-wrapper--focus),
.message-input :deep(.n-input-wrapper.n-input-wrapper--active),
.message-input :deep(.n-input__border),
.message-input :deep(.n-input__state-border) {
  background: transparent !important;
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
}
.message-input :deep(.n-input__input-el) {
  color: var(--text-color) !important;
}
.message-input :deep(.n-input__placeholder) {
  color: var(--text-color-muted) !important;
  opacity: 0.62;
}
.send-btn {
  min-width: 76px;
  height: 38px;
  padding: 0 1rem;
  border-radius: 999px;
  border: none !important;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #FFFFFF !important;
  font-size: 0.86rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25) !important;
  transition: transform var(--transition-base), box-shadow var(--transition-base), background var(--transition-base);
}
.send-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(99, 102, 241, 0.4) !important;
}
.send-btn:disabled {
  opacity: 0.72;
  cursor: progress;
  transform: none;
}
.treehole-main {
  position: relative;
  z-index: 1;
  margin-top: 0;
  padding: clamp(1.25rem, 3vw, 2rem) var(--spacing-lg, 24px) var(--spacing-xl, 60px);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-18) 20%, transparent) 0%,
      color-mix(in srgb, var(--background-color) 52%, transparent) 14%,
      color-mix(in srgb, var(--background-color) 76%, transparent) 38%,
      color-mix(in srgb, var(--background-color) 88%, transparent) 100%
    );
  -webkit-backdrop-filter: blur(calc(var(--glass-blur) + 2px)) saturate(calc(var(--glass-saturate) - 6%));
  backdrop-filter: blur(calc(var(--glass-blur) + 2px)) saturate(calc(var(--glass-saturate) - 6%));
}

.treehole-main::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-18) 78%, transparent) 0%,
      color-mix(in srgb, var(--surface-white-08) 52%, transparent) 20%,
      color-mix(in srgb, var(--background-color) 30%, transparent) 100%
    ),
    var(--surface-page-ambient);
  opacity: 0.74;
}

.treehole-main::after {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: clamp(1.5rem, 3vw, 2.25rem);
  z-index: 0;
  pointer-events: none;
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-05) 28%, transparent) 0%,
      color-mix(in srgb, var(--background-color) 8%, transparent) 58%,
      transparent 100%
    );
}

.treehole-main :deep(.page-shell) {
  position: relative;
  z-index: 1;
}
.treehole-workspace {
  position: relative;
  display: grid;
  gap: clamp(1.1rem, 2.8vw, 1.65rem);
  padding: clamp(1rem, 2vw, 1.4rem);
  border-radius: calc(var(--glass-radius) + 10px);
  border: 1px solid var(--border-panel-subtle);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
  overflow: hidden;
}
.treehole-workspace::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 38%;
  background: var(--surface-panel-specular-soft);
  pointer-events: none;
}
.treehole-header {
  position: relative;
  z-index: 1;
  margin-bottom: 22px;
  padding-top: 0;
  padding-bottom: 10px;
  gap: 12px;
}
.message-input-section, .messages-section { position: relative; z-index: 1; width: 100%; max-width: min(1040px, 100%); margin: 0 auto var(--page-section-gap); }
.treehole-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 18px;
}

.treehole-pagination__summary {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 0.9rem;
}
.input-card,
.message-card {
  overflow: hidden;
}
.input-card, .message-card { padding: 20px; }
.input-card {
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-panel-default) 88%, transparent),
      color-mix(in srgb, var(--surface-panel-soft) 94%, transparent)
    );
}
.card-header, .section-title { display: flex; align-items: center; gap: 10px; color: var(--text-color); }
.card-header { margin-bottom: 16px; justify-content: space-between; }
.card-heading { display: flex; flex-direction: column; gap: 0.55rem; }
.card-kicker { color: var(--text-secondary); }
.card-title-copy { display: inline-flex; align-items: center; gap: 10px; color: var(--title-color); font-size: 1.08rem; font-weight: 700; }
.card-header .s-icon, .section-title .s-icon { color: var(--primary-color); }
.input-wrapper {
  margin-bottom: 16px;
  padding: 0.9rem;
  border-radius: calc(var(--border-radius-xl) + 2px);
}
.card-footer { display: flex; align-items: center; justify-content: space-between; }
.char-count { font-size: 0.85rem; color: var(--text-color-muted); }
.submit-btn { min-width: 124px; }
.section-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 18px; }
.section-title { margin: 0; font-size: 1.25rem; font-weight: 600; align-items: center; }
.message-count { margin-left: auto; display: inline-flex; align-items: center; justify-content: center; min-width: 2.35rem; padding: 0.24rem 0.72rem; font-size: 0.8rem; font-weight: 600; color: var(--primary-color); background: var(--surface-panel-chip-accent); border: 1px solid var(--border-panel-badge-accent); border-radius: 999px; box-shadow: var(--highlight-panel-chip); }
.messages-list { display: flex; flex-direction: column; gap: 18px; }
.loading-state { display: grid; gap: 18px; }
.message-card { position: relative; display: grid; gap: var(--page-block-gap); padding: 1.1rem; }
.message-card::before { content: ''; position: absolute; inset: 0 0 auto 0; height: 44%; background: linear-gradient(180deg, color-mix(in srgb, var(--surface-white-12) 84%, transparent), transparent); pointer-events: none; }
.message-card__shell { position: relative; z-index: 1; display: grid; gap: 1rem; }
.message-header { position: relative; z-index: 1; display: flex; align-items: center; gap: 12px; margin-bottom: 0; }
.avatar { width: 46px; height: 46px; display: flex; align-items: center; justify-content: center; color: var(--primary-color); border-radius: 50%; box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline); }
.user-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.username { font-size: 0.95rem; font-weight: 700; color: var(--title-color); }
.time { font-size: 0.8rem; color: var(--text-color-muted); }
.message-badge { color: var(--text-primary); }
.message-content { position: relative; z-index: 1; padding: 1rem 1.05rem; border-radius: calc(var(--border-radius-xl) + 2px); }
.message-content p { margin: 0; font-size: 0.95rem; line-height: 1.9; color: var(--text-primary); }
.message-actions { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.message-action {
  position: relative;
  z-index: 1;
  gap: 7px;
  min-height: 38px;
  padding: 0 0.9rem;
  border-radius: 999px;
  color: var(--text-secondary);
  border: 1px solid var(--border-panel-subtle);
  background: color-mix(in srgb, var(--surface-panel-chip) 88%, transparent);
  transition:
    transform var(--transition-base),
    color var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);
}

.message-action:hover {
  transform: translateY(-1px);
  border-color: var(--border-panel-default);
  background: var(--surface-panel-chip-accent);
  box-shadow: var(--shadow-panel-inline);
}

.message-action:active {
  transform: translateY(1px);
}

.message-action--like:hover,
.message-action--like.active {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 30%, var(--border-panel-default));
  background: color-mix(in srgb, var(--error-color) 10%, var(--surface-panel-chip-accent));
}

.message-action--collect:hover,
.message-action--collect.active {
  color: var(--warning-color);
  border-color: color-mix(in srgb, var(--warning-color) 34%, var(--border-panel-default));
  background: color-mix(in srgb, var(--warning-color) 14%, var(--surface-panel-chip-accent));
}

.message-action--comment:hover,
.message-action--comment.active {
  color: var(--primary-color);
}

.message-action--danger {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 26%, transparent);
}

.message-action--danger:hover {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 36%, transparent);
  background: color-mix(in srgb, var(--error-color) 12%, var(--surface-panel-chip-accent));
}
.treehole-comments {
  display: grid;
  gap: 14px;
  margin-top: 2px;
  padding: 18px;
  border: 1px solid color-mix(in srgb, var(--border-panel-subtle) 90%, transparent);
  border-radius: 26px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--surface-panel-soft) 94%, transparent),
    color-mix(in srgb, var(--surface-panel-inset) 88%, transparent)
  );
}
.treehole-comments__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.treehole-comments__headline {
  display: flex;
  align-items: baseline;
  gap: 10px;
  flex-wrap: wrap;
}
.treehole-comments__title { font-size: 0.82rem; font-weight: 700; letter-spacing: 0.12em; text-transform: uppercase; color: var(--text-secondary); }
.treehole-comments__summary { color: color-mix(in srgb, var(--primary-color) 62%, var(--text-secondary)); font-size: 0.78rem; letter-spacing: 0.04em; }
.treehole-comments__meta { font-size: 0.82rem; color: var(--text-color-muted); }
.treehole-comments__composer-wrap { margin-bottom: var(--spacing-md); }
.treehole-comments__thread { margin-top: var(--spacing-sm); }
.message-list-enter-active, .message-list-leave-active { transition: all 0.25s ease; }
.message-list-enter-from, .message-list-leave-to { opacity: 0; transform: translateY(-10px); }
.treehole-empty-state { margin-top: var(--spacing-sm); }
@keyframes treehole-hero-backdrop-in {
  from {
    opacity: 0;
    transform: scale(1.06);
  }
  to {
    opacity: 1;
    transform: scale(1.02);
  }
}
:global(html[data-theme='dark']) .treehole-page .treehole-main::before {
  opacity: 0.86;
}

:global(html[data-theme='dark']) .treehole-page::before {
  opacity: 0.82;
  filter: saturate(0.68);
}

:global(html[data-theme='dark']) .treehole-page::after {
  background: none;
}

:global(html[data-theme='dark']) .input-row {
  background: rgba(15, 23, 42, 0.55) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.24), inset 0 1px 0 rgba(255, 255, 255, 0.05) !important;
}

:global(html[data-theme='dark']) .input-row:focus-within {
  background: rgba(15, 23, 42, 0.75) !important;
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.25), 0 8px 32px rgba(0, 0, 0, 0.32) !important;
}

:global(html[data-theme='dark']) .message-input :deep(.n-input__placeholder) {
  color: rgba(255, 255, 255, 0.44) !important;
}

:global(html[data-theme='dark']) .message-input :deep(.n-input__input-el) {
  color: #FFFFFF !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
}

@include sourcelin-down(md) {
  .treehole-main {
    margin-top: 0;
    padding: clamp(1rem, 4vw, 1.5rem) var(--spacing-md, 16px) var(--spacing-xl, 40px);
  }

  .treehole-header {
    margin-bottom: 18px;
    padding-bottom: 6px;
  }
  .treehole-workspace {
    padding: var(--spacing-md);
    border-radius: 28px;
  }
  .input-row {
    flex-direction: column;
    align-items: stretch;
    padding: 0.6rem;
    border-radius: calc(var(--glass-radius) + 6px);
  }
  .message-input :deep(.n-input-wrapper) {
    padding: 0 0.25rem !important;
  }
  .send-btn,
  .submit-btn { width: 100%; justify-content: center; }
  .card-footer { flex-direction: column; gap: 12px; align-items: stretch; }
  .message-in { top: 42%; width: min(100% - 32px, 520px); }
  .message-actions { flex-direction: column; align-items: stretch; }
  .treehole-comments__header { align-items: flex-start; flex-direction: column; }
}
</style>
