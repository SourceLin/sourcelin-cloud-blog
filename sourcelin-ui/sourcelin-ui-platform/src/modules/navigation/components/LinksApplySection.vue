<template>
  <SSurfacePanel
    id="friend-link-apply"
    ref="rootRef"
    tag="section"
    class="apply-card"
    :class="{ 'apply-card--collapsed': !applyExpanded }"
    variant="soft"
    aria-label="申请友情链接"
  >
    <button
      id="apply-card-toggle"
      type="button"
      class="apply-card__toggle"
      :aria-expanded="applyExpanded"
      aria-controls="apply-card-panel"
      @click="emit('toggle')"
    >
      <span class="apply-card__toggle-main">
        <h2 class="section-title apply-card__toggle-title">申请友情链接</h2>
      </span>
      <span class="apply-card__toggle-trail">
        <span v-if="!applyExpanded" class="apply-card__toggle-hint">想互换友链？点按展开</span>
        <span class="apply-card__chevron-wrap" aria-hidden="true">
          <SIcon
            :icon="appIcons.chevronDown"
            :size="20"
            class="apply-card__chevron"
            :class="{ 'apply-card__chevron--expanded': applyExpanded }"
          />
        </span>
      </span>
    </button>

    <div
      v-show="applyExpanded"
      id="apply-card-panel"
      class="apply-card__panel"
      role="region"
      aria-labelledby="apply-card-toggle"
    >
      <header class="apply-card__head">
        <div class="apply-card__head-main">
          <p class="apply-copy">
            填好必填项即可一键提交；描述与头像可选。审核通过后将出现在上方「友链列表」中。
          </p>
        </div>
      </header>

      <SSurfacePanel class="apply-card__body" variant="inset">
        <form class="apply-form" @submit.prevent="emit('submit')">
          <div class="apply-form-grid">
            <div class="apply-field">
              <label class="apply-field-label" for="apply-name">网站名称 <span class="req">*</span></label>
              <SInput
                id="apply-name"
                v-model="applyForm.name"
                placeholder="站点名称"
                :maxlength="50"
                show-count
                clearable
                autocomplete="organization"
              />
            </div>
            <div class="apply-field">
              <label class="apply-field-label" for="apply-email">联系邮箱 <span class="req">*</span></label>
              <SInput
                id="apply-email"
                v-model="applyForm.email"
                type="text"
                placeholder="方便联系你"
                :maxlength="75"
                clearable
                autocomplete="email"
              />
            </div>
          </div>
          <div class="apply-field apply-field--full">
            <label class="apply-field-label" for="apply-url">网站地址 <span class="req">*</span></label>
            <SInput
              id="apply-url"
              v-model="applyForm.url"
              placeholder="https:// 或域名（自动补全 https）"
              :maxlength="90"
              clearable
              autocomplete="url"
            />
          </div>
          <div class="apply-field apply-field--full">
            <label class="apply-field-label" for="apply-description">网站描述</label>
            <STextarea
              id="apply-description"
              v-model="applyForm.description"
              placeholder="一句话介绍（选填）"
              :rows="2"
              :maxlength="255"
              show-count
            />
          </div>
          <div class="apply-field apply-field--full">
            <label class="apply-field-label" for="apply-avatar">头像图片链接</label>
            <SInput
              id="apply-avatar"
              v-model="applyForm.avatar"
              placeholder="选填，http(s) 图片地址"
              :maxlength="255"
              clearable
            />
          </div>

          <div class="apply-actions" role="group" aria-label="提交申请">
            <SButton type="primary" variant="cta" class="apply-submit-btn" :loading="applySubmitting" attr-type="submit">
              提交申请
            </SButton>
          </div>
        </form>
      </SSurfacePanel>

      <SSurfacePanel tag="footer" class="apply-note" variant="inset">
        <span class="apply-note__icon" aria-hidden="true">
          <SIcon :icon="appIcons.information" :size="18" />
        </span>
        <p class="apply-note__text">
          本站信息：名称 {{ siteDisplayName }}，描述「{{ siteDescriptionForSnippet }}」。页顶可一键复制；交换友链请先确保贵站已放置本站链接。
        </p>
      </SSurfacePanel>
    </div>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ApplyForm } from '@/modules/navigation/composables/useLinksPage'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import STextarea from '@/shared/components/ui/STextarea.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  applyForm: ApplyForm
  applyExpanded: boolean
  applySubmitting: boolean
  siteDisplayName: string
  siteDescriptionForSnippet: string
}

interface Emits {
  (event: 'toggle'): void
  (event: 'submit'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()
const rootRef = ref<HTMLElement | { $el?: HTMLElement } | null>(null)

defineExpose({
  scrollIntoView: (options?: ScrollIntoViewOptions) => {
    const rawTarget = rootRef.value
    const target = rawTarget instanceof HTMLElement
      ? rawTarget
      : rawTarget?.$el
    target?.scrollIntoView(options)
  }
})
</script>
