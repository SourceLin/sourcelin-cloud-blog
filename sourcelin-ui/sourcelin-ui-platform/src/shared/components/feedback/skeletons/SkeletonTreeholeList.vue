<script setup lang="ts">
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

withDefaults(
  defineProps<{
    count?: number
  }>(),
  {
    count: 4
  }
)
</script>

<template>
  <div class="skeleton-treehole-list" aria-hidden="true">
    <SSurfacePanel
      v-for="index in count"
      :key="`treehole-skel-${index}`"
      tag="article"
      variant="soft"
      class="treehole-skel-card"
    >
      <div class="treehole-skel-card__shell">
        <div class="treehole-skel-header">
          <SSkeleton circle class="treehole-skel-avatar" :width="46" :height="46" />
          <div class="treehole-skel-user">
            <SSkeleton :sharp="false" class="treehole-skel-name" :width="96" :height="16" />
            <SSkeleton :sharp="false" class="treehole-skel-time" :width="78" :height="12" />
          </div>
          <SSkeleton :sharp="false" class="treehole-skel-badge" :width="58" :height="28" />
        </div>

        <SSurfacePanel class="treehole-skel-content" variant="inset">
          <SSkeleton :sharp="false" class="treehole-skel-line" :height="14" />
          <SSkeleton :sharp="false" class="treehole-skel-line" :height="14" />
          <SSkeleton :sharp="false" class="treehole-skel-line treehole-skel-line--short" :height="14" />
        </SSurfacePanel>

        <div class="treehole-skel-actions">
          <SSkeleton :sharp="false" class="treehole-skel-action" :width="86" :height="34" />
          <SSkeleton :sharp="false" class="treehole-skel-action treehole-skel-action--wide" :width="126" :height="34" />
        </div>
      </div>
    </SSurfacePanel>
  </div>
</template>

<style scoped lang="scss">
.skeleton-treehole-list {
  display: grid;
  gap: 18px;
}

.treehole-skel-card {
  padding: 1.1rem;
  border: 1px solid var(--border-panel-subtle);
  pointer-events: none;
}

.treehole-skel-card__shell,
.treehole-skel-user {
  display: grid;
}

.treehole-skel-card__shell {
  gap: 1rem;
}

.treehole-skel-header,
.treehole-skel-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.treehole-skel-user {
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.treehole-skel-badge,
.treehole-skel-action {
  border-radius: 999px !important;
}

.treehole-skel-content {
  display: grid;
  gap: 10px;
  padding: 1rem 1.05rem;
  border-radius: calc(var(--border-radius-xl) + 2px);
}

.treehole-skel-line {
  width: 100% !important;
}

.treehole-skel-line--short {
  width: 72% !important;
}
</style>
