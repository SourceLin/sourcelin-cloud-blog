<template>
  <div class="glass-page about-page">
    <PageShell>
      <AboutHeroSection
        :site-summary="siteSummary"
        :hero-stat-tiles="heroStatTiles"
        :site-info="siteInfo"
        :email-href="emailHref"
        :copied-contact-title="copiedContactTitle"
        :copy-contact="copyContact"
      />

      <AboutStatsSection
        :site-stats="siteStats"
        :summary-items="summaryItemsWithIcons"
      />

      <AboutContentSection
        :content-section-copy="contentSectionCopy"
        :skills="skills"
        :interest-groups="interestGroups"
        :site-facts="siteFacts"
      />

      <AboutTimelineSection :timeline-items="timelineItems" />

      <AboutContactSection
        :contact-channels="contactChannels"
        :reward-channels="rewardChannels"
        :copied-contact-title="copiedContactTitle"
        :copy-contact="copyContact"
      />
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import AboutContactSection from '@/modules/about/components/AboutContactSection.vue'
import AboutContentSection from '@/modules/about/components/AboutContentSection.vue'
import AboutHeroSection from '@/modules/about/components/AboutHeroSection.vue'
import AboutStatsSection from '@/modules/about/components/AboutStatsSection.vue'
import AboutTimelineSection from '@/modules/about/components/AboutTimelineSection.vue'
import { useAboutPage } from '@/modules/about/composables/useAboutPage'
import PageShell from '@/shared/components/layout/PageShell.vue'
import '@/modules/about/styles/about-page.scss'

const {
  siteInfo,
  copiedContactTitle,
  siteStats,
  summaryItemsWithIcons,
  skills,
  interestGroups,
  contentSectionCopy,
  timelineItems,
  siteSummary,
  emailHref,
  heroStatTiles,
  siteFacts,
  contactChannels,
  rewardChannels,
  copyContact
} = useAboutPage()
</script>
