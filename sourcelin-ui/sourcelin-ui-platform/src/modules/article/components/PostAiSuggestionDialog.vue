<template>
  <!-- AI 建议对话框 -->
  <SModal
    v-model:show="show"
    preset="card"
    title="AI 智能建议"
    class="ai-dialog"
    :style="{ width: 'min(90vw, 640px)' }"
    @close="emit('close')"
  >
    <div class="ai-dialog__body">
      <div class="ai-dialog__header">
        <span class="ai-dialog__badge">AI 推荐的{{ result.fieldName }}已拟定</span>
      </div>

      <div class="ai-dialog__content">
        <!-- 当前内容 -->
        <div
          v-if="result.abilityCode === 'article_tags' ? result.originalTags.length > 0 : result.originalVal"
          class="ai-dialog__card ai-dialog__card--current"
        >
          <div class="ai-dialog__card-label">当前内容</div>
          <div v-if="result.abilityCode !== 'article_tags'" class="ai-dialog__card-value">
            {{ result.originalVal }}
          </div>
          <div v-else class="ai-dialog__tags">
            <span v-for="tag in result.originalTags" :key="`orig-${tag}`" class="ai-dialog__tag ai-dialog__tag--current">
              {{ tag }}
            </span>
          </div>
        </div>

        <!-- AI 推荐 -->
        <div class="ai-dialog__card ai-dialog__card--ai">
          <div class="ai-dialog__card-label ai-dialog__card-label--ai">AI 智能推荐</div>
          <div v-if="result.abilityCode !== 'article_tags'" class="ai-dialog__card-value">
            {{ result.content }}
            <span v-if="result.isTyping" class="ai-dialog__cursor">|</span>
          </div>
          <div v-else class="ai-dialog__tags">
            <span
              v-for="tag in result.parsedTags"
              :key="`ai-${tag}`"
              class="ai-dialog__tag ai-dialog__tag--ai"
            >
              {{ tag }}
            </span>
            <span v-if="result.parsedTags.length === 0 && !result.isTyping" class="ai-dialog__empty">暂无推荐标签</span>
            <span v-if="result.isTyping" class="ai-dialog__cursor">|</span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="ai-dialog__footer">
        <SButton @click="emit('discard')">放弃</SButton>
        <SButton type="primary" @click="emit('apply')">
          {{ result.isTyping ? '立即采纳 (跳过动画)' : '采纳建议' }}
        </SButton>
      </div>
    </template>
  </SModal>
</template>
<script setup lang="ts">
import SModal from '@/shared/components/ui/SModal.vue'
import SButton from '@/shared/components/ui/SButton.vue'
defineProps<{ result: { abilityCode: string; fieldName: string; content: string; originalVal: string; originalTags: string[]; parsedTags: string[]; isTyping: boolean } }>()
const show = defineModel<boolean>('show', { required: true })
const emit = defineEmits<{ close: []; discard: []; apply: [] }>()
</script>
<style scoped lang="scss" src="./PostAiSuggestionDialog.scss"></style>
