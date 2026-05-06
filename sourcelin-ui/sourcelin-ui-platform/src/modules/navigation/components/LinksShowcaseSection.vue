<template>
  <SSurfacePanel tag="section" class="links-showcase" variant="soft" aria-labelledby="links-showcase-title">
    <header class="links-showcase__head">
      <div class="links-showcase__head-main">
        <h2 id="links-showcase-title" class="links-showcase__title">友链列表</h2>
        <p class="links-showcase__desc">
          已通过审核的站点；点击卡片将在新标签页打开对方站点。
        </p>
      </div>
      <SSurfaceChip class="links-showcase__count" size="sm" variant="counter" role="status">{{ linksDirectoryBadge }}</SSurfaceChip>
    </header>

    <div v-if="links.length > 0" class="links-grid">
      <SSurfacePanel
        v-for="link in links"
        :key="link.id"
        tag="a"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-card"
        variant="default"
        interactive
      >
        <div class="link-card__rail" aria-hidden="true" />
        <div class="link-avatar">
          <img v-if="link.avatar" :src="link.avatar" :alt="link.name">
          <div v-else class="avatar-placeholder">{{ link.name.charAt(0) }}</div>
        </div>
        <div class="link-info">
          <h3 class="link-name">{{ link.name }}</h3>
          <p class="link-desc">{{ link.description }}</p>
        </div>
        <div class="link-arrow">
          <SIcon :icon="appIcons.external" :size="16" />
        </div>
      </SSurfacePanel>
    </div>

    <EmptyState
      v-else
      class="links-showcase__empty"
      variant="section"
      icon="links"
      title="暂无友情链接"
      message="这里还没有友情链接，欢迎提交你的站点。"
      action-text="申请友情链接"
      @action="emit('apply')"
    />
  </SSurfacePanel>
</template>

<script setup lang="ts">
import type { Link } from '@/modules/navigation/composables/useLinksPage'
import EmptyState from '@/shared/components/feedback/EmptyState.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSurfaceChip from '@/shared/components/ui/SSurfaceChip.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  links: Link[]
  linksDirectoryBadge: string
}

interface Emits {
  (event: 'apply'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()
</script>
