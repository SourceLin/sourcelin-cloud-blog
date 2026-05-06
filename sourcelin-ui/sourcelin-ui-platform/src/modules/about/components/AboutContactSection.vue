<template>
  <SSurfacePanel tag="section" class="about-contact-panel" variant="soft" aria-labelledby="about-contact-title">
    <div class="section-head about-section-head">
      <div>
        <h2 id="about-contact-title" class="section-title">想联系我？</h2>
        <p class="section-copy">联系方式都在这里，按你的习惯选一个就行。</p>
      </div>
    </div>

    <div class="about-contact-grid">
      <SSurfacePanel
        v-for="item in contactChannels"
        :key="item.title"
        :tag="item.to ? 'router-link' : item.href ? 'a' : 'div'"
        :to="item.to"
        :href="item.href"
        class="contact-card"
        variant="default"
        interactive
        :target="item.href ? '_blank' : undefined"
        :rel="item.href ? 'noreferrer' : undefined"
        @click="item.type === 'copy' && copyContact({ title: item.title, copyValue: item.copyValue })"
      >
        <SSurfaceChip class="contact-card__icon">
          <SIcon :icon="item.icon" :size="18" />
        </SSurfaceChip>
        <span v-if="item.type === 'link'" class="contact-card__external">
          <SIcon :icon="appIcons.external" :size="14" />
        </span>
        <div class="contact-card__body">
          <h3 class="contact-card__title">{{ item.title }}</h3>
          <p class="contact-card__copy">{{ item.copy }}</p>
          <p v-if="item.type === 'copy' && item.copyValue" class="contact-card__meta">
            {{ copiedContactTitle === item.title ? '已复制' : '点击卡片即可复制' }}
          </p>
        </div>
      </SSurfacePanel>
    </div>

    <div v-if="rewardChannels.length" class="about-reward-grid">
      <SSurfacePanel tag="article" v-for="item in rewardChannels" :key="item.title" class="about-reward-card" variant="inset">
        <div class="about-reward-card__head">
          <SSurfaceChip class="contact-card__icon">
            <SIcon :icon="item.icon" :size="16" />
          </SSurfaceChip>
          <h3 class="about-reward-card__title">{{ item.title }}</h3>
        </div>
        <img :src="item.image" :alt="item.title" class="about-reward-card__image">
      </SSurfacePanel>
    </div>
  </SSurfacePanel>
</template>

<script setup lang="ts">
import type { ContactChannel, RewardChannel } from '@/modules/about/composables/useAboutPage'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

defineProps<{
  contactChannels: ContactChannel[]
  rewardChannels: RewardChannel[]
  copiedContactTitle: string
  copyContact: (item: { title: string; copyValue?: string }) => Promise<void>
}>()
</script>
