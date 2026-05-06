<script setup lang="ts">
import STextarea from '@/shared/components/ui/STextarea.vue'

defineProps<{
  currentUserAvatar: string
  modelValue: string
  placeholder: string
  isLoggedIn: boolean
  commentSubmitting: boolean
  isReplying: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  submit: []
  'cancel-reply': []
}>()
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
        :disabled="!isLoggedIn"
        @update:model-value="emit('update:modelValue', String($event || ''))"
      />
      <div class="composer-footer">
        <span v-if="isReplying" class="reply-hint">
          正在回复一条评论
          <SButton text size="tiny" @click="emit('cancel-reply')">取消</SButton>
        </span>
        <SButton
          type="primary"
          size="small"
          :loading="commentSubmitting"
          :disabled="isLoggedIn ? !modelValue.trim() : false"
          @click="emit('submit')"
        >
          {{ isLoggedIn ? '发布评论' : '请先登录' }}
        </SButton>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.comment-composer { display: grid; grid-template-columns: 52px minmax(0, 1fr); gap: 16px; padding-bottom: 22px; margin-bottom: 8px; border-bottom: 1px solid var(--border-color); }
.composer-avatar img { width: 52px; height: 52px; border-radius: 50%; object-fit: cover; }
.composer-footer { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-md); margin-top: 14px; }
.reply-hint { display: inline-flex; align-items: center; gap: 8px; color: var(--text-secondary); }
@include sourcelin-down(md) { .comment-composer { display: grid; grid-template-columns: 1fr; gap: 12px; } .composer-avatar img { width: 48px; height: 48px; } .composer-footer { display: flex; flex-direction: column; align-items: flex-start; } }
</style>

