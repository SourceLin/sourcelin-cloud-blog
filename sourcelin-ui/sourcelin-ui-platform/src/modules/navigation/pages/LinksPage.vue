<template>
  <div class="glass-page links-page">
    <PageShell>
      <LinksHeroSection
        :link-hero-stat-tiles="linkHeroStatTiles"
        @scroll-to-apply="scrollToApplySection"
        @copy-site-friend-info="copySiteFriendInfo"
      />

      <div class="links-tail">
        <LinksShowcaseSection
          :links="links"
          :links-directory-badge="linksDirectoryBadge"
          @apply="scrollToApplySection"
        />

        <LinksApplySection
          ref="applyCardSectionRef"
          :apply-form="applyForm"
          :apply-expanded="applyExpanded"
          :apply-submitting="applySubmitting"
          :site-display-name="siteDisplayName"
          :site-description-for-snippet="siteDescriptionForSnippet"
          @toggle="toggleApplyExpanded"
          @submit="submitApply"
        />
      </div>
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import LinksApplySection from '@/modules/navigation/components/LinksApplySection.vue'
import LinksHeroSection from '@/modules/navigation/components/LinksHeroSection.vue'
import LinksShowcaseSection from '@/modules/navigation/components/LinksShowcaseSection.vue'
import { useLinksPage } from '@/modules/navigation/composables/useLinksPage'
import PageShell from '@/shared/components/layout/PageShell.vue'
import '@/modules/navigation/styles/links-page.scss'

const {
  links,
  applyForm,
  applySubmitting,
  applyExpanded,
  applyCardSectionRef,
  siteDisplayName,
  siteDescriptionForSnippet,
  linkHeroStatTiles,
  linksDirectoryBadge,
  scrollToApplySection,
  copySiteFriendInfo,
  submitApply,
  toggleApplyExpanded
} = useLinksPage()
</script>
