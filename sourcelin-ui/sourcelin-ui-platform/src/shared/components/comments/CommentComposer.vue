<script setup lang="ts">
import { computed } from 'vue'
import SButton from '@/shared/components/ui/SButton.vue'
import STextarea from '@/shared/components/ui/STextarea.vue'

const props = defineProps<{
  currentUserAvatar: string
  modelValue: string
  placeholder: string
  isLoggedIn: boolean
  isAnonymousAllowed?: boolean
  commentSubmitting: boolean
  isReplying: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  submit: []
  'cancel-reply': []
}>()

const canParticipate = computed(() => props.isLoggedIn || props.isAnonymousAllowed)
const submitLabel = computed(() => {
  if (props.isLoggedIn) {
    return '发布评论'
  }
  if (props.isAnonymousAllowed) {
    return '匿名发布'
  }
  return '请先登录'
})
</script>

<template>
  <div class="comment-composer">
    <div class="composer-avatar"><img :src="currentUserAvatar" alt="avatar"></div>
    <div class="composer-body">
      <STextarea
        :model-value="modelValue"
        :rows="4"
        :maxlength="500"
        show-count
        :placeholder="placeholder"
        :disabled="!canParticipate"
        @update:model-value="emit('update:modelValue', String($event || ''))"
      />
      <div class="composer-footer">
        <span v-if="isReplying" class="reply-hint">
          正在回复一条评论
          <SButton text size="tiny" @click="emit('cancel-reply')">取消</SButton>
        </span>
        <span v-else-if="isAnonymousAllowed && !isLoggedIn" class="reply-hint">将以匿名身份发布</span>
        <SButton
          type="primary"
          size="small"
          :loading="commentSubmitting"
          :disabled="canParticipate ? !modelValue.trim() : false"
          @click="emit('submit')"
        >
          {{ submitLabel }}
        </SButton>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.comment-composer { display: grid; grid-template-columns: 56px minmax(0, 1fr); gap: var(--comment-thread-gap); padding-bottom: 22px; margin-bottom: 8px; border-bottom: 1px solid var(--border-panel-subtle); }
.composer-avatar img { width: 56px; height: 56px; border-radius: 50%; object-fit: cover; border: 1px solid color-mix(in srgb, var(--primary-color) 18%, transparent); box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline); }
.composer-body { padding: 0.85rem; border-radius: calc(var(--border-radius-xl) + 2px); background: var(--surface-panel-soft); border: 1px solid var(--border-panel-subtle); box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft); }
.composer-footer { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-md); margin-top: 14px; }
.reply-hint { display: inline-flex; align-items: center; gap: 8px; color: var(--text-secondary); }
@include sourcelin-down(md) { .comment-composer { display: grid; grid-template-columns: 1fr; gap: 12px; } .composer-avatar img { width: 48px; height: 48px; } .composer-footer { display: flex; flex-direction: column; align-items: flex-start; } }
</style>
