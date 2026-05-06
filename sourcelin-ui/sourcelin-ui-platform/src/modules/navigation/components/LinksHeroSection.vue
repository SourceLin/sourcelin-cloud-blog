<template>
  <SCard
    class="links-hero"
    variant="lite"
  >
    <div class="links-header-main">
      <div class="links-hero__main">
        <div class="links-hero__copy">
          <span class="article-card-prelude-label">导航</span>
          <h1 class="links-hero__title">友情链接</h1>
          <p class="links-hero__subtitle">
            认识更多有趣的人；交换友链前请先在贵站添加本站链接，再提交申请。
          </p>

          <section class="links-hero__summary" aria-label="友链概览">
            <HeroStatCard
              v-for="tile in linkHeroStatTiles"
              :key="tile.label"
              class="links-hero__stat"
              mode="metric"
              tone="tinted"
              size="lg"
              :icon="tile.icon"
              :value="tile.value"
              :label="tile.label"
              :hue="tile.hue"
              :action="tile.action ? 'button' : 'static'"
              :aria-label="tile.action ? `${tile.ariaLabel}，跳转并展开下方申请表` : tile.ariaLabel"
              @click="tile.action === 'scroll-to-apply' && emit('scroll-to-apply')"
            />
          </section>
        </div>

        <aside class="links-hero__aside" aria-label="复制本站友链信息">
          <div class="links-hero__aside-panel">
            <div class="links-hero__aside-glow" aria-hidden="true" />
            <div class="links-hero__aside-body">
              <div class="links-hero__aside-copy">
                <p class="links-hero__aside-eyebrow">交换须知</p>
                <p class="links-hero__aside-lede">
                  请先在贵站添加本站链接，再在页尾展开「申请友情链接」填写表单。点击下方按钮可复制本站信息（名称、邮箱、地址、描述、头像链接），便于粘贴到贵站后台。
                </p>
              </div>
              <div class="links-hero__aside-action">
                <SButton type="primary" variant="cta" class="links-hero__copy-btn" @click="emit('copy-site-friend-info')">
                  <template #icon>
                    <span class="links-hero__copy-btn-icon" aria-hidden="true">
                      <SIcon :icon="appIcons.copy" :size="18" />
                    </span>
                  </template>
                  复制本站友链信息
                </SButton>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </SCard>
</template>

<script setup lang="ts">
import type { LinkHeroStatTile } from '@/modules/navigation/composables/useLinksPage'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  linkHeroStatTiles: LinkHeroStatTile[]
}

interface Emits {
  (event: 'scroll-to-apply'): void
  (event: 'copy-site-friend-info'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()
</script>
