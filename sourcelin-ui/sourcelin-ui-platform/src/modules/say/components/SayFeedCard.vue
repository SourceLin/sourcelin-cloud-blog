<template>
  <SSurfacePanel
    :class="['say-item', `say-item--${cardVariant}`, { 'say-item--comment-rich': isCommentRich }]"
    variant="soft"
    interactive
    :style="cardStyle"
  >
    <article class="say-card">
      <SayAuthorMeta
        :item="item"
        :nickname="modalUserNickname"
        :card-variant-label="cardVariantLabel"
        :card-variant-icon="cardVariantIcon"
        :is-comment-rich="isCommentRich"
        :publish-relative-label="publishRelativeLabel"
      />

      <SayMediaGrid
        :item="item"
        :card-variant="cardVariant"
        :image-array="imageArray"
        :gallery-images="galleryImages"
        :extra-image-count="extraImageCount"
        :formatted-content="formattedContent"
        @preview="emitPreview"
      />

      <SayInteractionBar
        :liked="liked"
        :collected="collected"
        :like-count="likeCount"
        :collect-count="collectCount"
        :comment-panel-visible="commentPanelVisible"
        :header-comment-total="headerCommentTotal"
        :can-delete-say="canDeleteSay"
        :liked-icon-color="likedIconColor"
        :collected-icon-color="collectedIconColor"
        @toggle-like="toggleLike"
        @toggle-collect="toggleCollect"
        @comment-action="handleCommentAction"
        @delete="handleDeleteConfirm"
      />

      <SayCommentPreview
        v-model="newComment"
        :panel-id="commentPanelId"
        :show="showCommentPanel"
        :is-rich="isCommentRich"
        :composer-visible="composerVisible"
        :current-user-avatar="currentUserAvatar"
        :comment-placeholder="commentPlaceholder"
        :is-logged-in="userStore.isLoggedIn"
        :comment-submitting="commentSubmitting"
        :is-replying="Boolean(replyCommentId)"
        :comments="displayedThreadComments"
        :comment-loading="commentLoading"
        :comment-total="commentTotal"
        :comment-page-num="commentPageNum"
        :comment-page-size="commentPageSize"
        :author-id="item.userId"
        :current-user-id="currentUserId"
        :format-date="formatCommentDate"
        :enable-like="enableLike"
        :enable-reply="enableReply"
        :show-pagination="commentMode === 'full' && commentTotal > commentPageSize"
        :comment-section-title="commentSectionTitle"
        :header-comment-total="headerCommentTotal"
        :comment-section-summary="commentSectionSummary"
        :can-toggle-comments="canToggleComments"
        :comment-toggle-text="commentToggleText"
        @toggle-comments="toggleComments"
        @submit-comment="submitComment"
        @cancel-reply="cancelReply"
        @like="handleCommentLike"
        @delete="handleCommentDelete"
        @reply="onReplyThread"
        @interaction-blocked="onCommentInteractionBlocked"
        @update-page="handleCommentPageUpdate"
      />
    </article>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import { toRef } from 'vue'
import type { SayItem } from '@/modules/say/model/say.types'
import SayAuthorMeta from '@/modules/say/components/SayAuthorMeta.vue'
import SayCommentPreview from '@/modules/say/components/SayCommentPreview.vue'
import SayInteractionBar from '@/modules/say/components/SayInteractionBar.vue'
import SayMediaGrid from '@/modules/say/components/SayMediaGrid.vue'
import { useSayCardState } from '@/modules/say/composables/useSayCardState'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

const props = defineProps<{
  item: SayItem
  cardStyle?: Record<string, string>
}>()

const emit = defineEmits<{
  preview: [payload: { images: string[]; index: number }]
  delete: [sayId: number]
}>()

const {
  userStore,
  likeCount,
  collectCount,
  liked,
  collected,
  composerVisible,
  commentPanelVisible,
  modalUserNickname,
  currentUserAvatar,
  currentUserId,
  isCommentRich,
  headerCommentTotal,
  displayedThreadComments,
  showCommentPanel,
  canToggleComments,
  imageArray,
  galleryImages,
  extraImageCount,
  cardVariant,
  cardVariantLabel,
  cardVariantIcon,
  publishRelativeLabel,
  commentSectionTitle,
  commentSectionSummary,
  likedIconColor,
  collectedIconColor,
  commentToggleText,
  canDeleteSay,
  formattedContent,
  commentPanelId,
  commentTotal,
  commentPageNum,
  commentPageSize,
  commentMode,
  commentLoading,
  newComment,
  commentSubmitting,
  replyCommentId,
  commentPlaceholder,
  enableLike,
  enableReply,
  formatCommentDate,
  handleCommentLike,
  handleCommentDelete,
  cancelReply,
  submitComment,
  onCommentInteractionBlocked,
  handleCommentAction,
  toggleComments,
  onReplyThread,
  handleCommentPageUpdate,
  toggleLike,
  toggleCollect,
  handleDeleteConfirm,
  emitPreview
} = useSayCardState({
  item: toRef(props, 'item'),
  emitPreview: (payload) => emit('preview', payload),
  emitDelete: (sayId) => emit('delete', sayId)
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-item {
  overflow: hidden;
  border-radius: 28px;
}

.say-card {
  position: relative;
  display: grid;
  gap: 22px;
  padding: 28px;
}

@include sourcelin-down(md) {
  .say-card {
    gap: 18px;
    padding: 22px 18px 20px;
  }
}
</style>
