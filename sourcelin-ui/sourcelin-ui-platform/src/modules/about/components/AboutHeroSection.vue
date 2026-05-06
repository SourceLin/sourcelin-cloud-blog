<template>
  <SCard class="about-hero" variant="lite">
    <div class="about-hero__main">
      <div class="about-hero__copy">
        <SSurfaceChip class="article-card-prelude-label" tone="accent">关于</SSurfaceChip>
        <h1 class="about-hero__title">关于本站</h1>
        <p class="about-hero__subtitle">{{ siteSummary }}</p>

        <section class="about-hero__summary" aria-label="页面重点信息">
          <HeroStatCard
            v-for="tile in heroStatTiles"
            :key="tile.label"
            class="about-hero__stat"
            mode="link"
            tone="neutral"
            size="lg"
            :icon="tile.icon"
            :title="tile.title"
            :meta="tile.meta"
            :href="tile.href"
            :aria-label="tile.ariaLabel"
          />
        </section>
      </div>

      <aside class="about-hero__aside" aria-label="站点名片">
        <SSurfacePanel class="about-identity-card" variant="soft">
          <div class="about-identity-card__avatar-shell">
            <img :src="siteInfo.avatar || defaultAvatar" alt="站点头像" class="about-identity-card__avatar">
            <div class="about-identity-card__avatar-ring" />
          </div>

          <div class="about-identity-card__copy">
            <p class="about-identity-card__eyebrow">关于作者</p>
            <h2 class="about-identity-card__name">{{ siteInfo.author }}</h2>
            <p class="about-identity-card__title">{{ siteInfo.authorInfo }}</p>
            <p class="about-identity-card__bio">{{ siteInfo.bio }}</p>
          </div>

          <div class="about-identity-card__actions">
            <SSurfacePanel tag="a" v-if="emailHref" :href="emailHref" class="about-action-chip" variant="inset" interactive>
              <SIcon :icon="appIcons.mail" :size="16" />
              发送邮件
            </SSurfacePanel>
            <SSurfacePanel
              tag="button"
              v-if="siteInfo.qqNumber"
              type="button"
              class="about-action-chip"
              variant="inset"
              interactive
              @click="copyContact({ title: 'QQ 号', copyValue: siteInfo.qqNumber })"
            >
              <SIcon :icon="appIcons.qq" :size="16" />
              {{ copiedContactTitle === 'QQ 号' ? 'QQ 已复制' : '复制 QQ 号' }}
            </SSurfacePanel>
            <SSurfacePanel
              tag="button"
              v-if="siteInfo.wechat"
              type="button"
              class="about-action-chip"
              variant="inset"
              interactive
              @click="copyContact({ title: '微信号', copyValue: siteInfo.wechat })"
            >
              <SIcon :icon="appIcons.comment" :size="16" />
              {{ copiedContactTitle === '微信号' ? '微信已复制' : '复制微信号' }}
            </SSurfacePanel>
            <SSurfacePanel
              tag="button"
              v-if="siteInfo.qqGroup"
              type="button"
              class="about-action-chip"
              variant="inset"
              interactive
              @click="copyContact({ title: 'QQ群号', copyValue: siteInfo.qqGroup })"
            >
              <SIcon :icon="appIcons.visitors" :size="16" />
              {{ copiedContactTitle === 'QQ群号' ? '群号已复制' : '复制QQ群号' }}
            </SSurfacePanel>
          </div>
        </SSurfacePanel>
      </aside>
    </div>
  </SCard>
</template>

<script setup lang="ts">
import defaultAvatar from '@/assets/images/logo/logo.png'
import type { AboutHeroStatTile, SiteInfo } from '@/modules/about/composables/useAboutPage'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  siteSummary: string
  heroStatTiles: AboutHeroStatTile[]
  siteInfo: SiteInfo
  emailHref: string
  copiedContactTitle: string
  copyContact: (item: { title: string; copyValue?: string }) => Promise<void>
}

defineProps<Props>()
</script>
