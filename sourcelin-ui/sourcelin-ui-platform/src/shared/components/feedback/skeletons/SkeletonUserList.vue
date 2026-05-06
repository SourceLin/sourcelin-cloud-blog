<script setup lang="ts">
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

withDefaults(
  defineProps<{
    count?: number
    action?: boolean
  }>(),
  {
    count: 4,
    action: false
  }
)
</script>

<template>
  <div class="skeleton-user-list" aria-hidden="true">
    <SSurfacePanel
      v-for="index in count"
      :key="`user-skel-${index}`"
      tag="article"
      variant="inset"
      class="user-skel-item"
    >
      <SSkeleton circle class="user-skel-avatar" :width="56" :height="56" />
      <div class="user-skel-body">
        <SSkeleton :sharp="false" class="user-skel-name" :width="112" :height="18" />
        <SSkeleton :sharp="false" class="user-skel-intro" :height="12" />
      </div>
      <SSkeleton v-if="action" :sharp="false" class="user-skel-action" :width="88" :height="34" />
    </SSurfacePanel>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.skeleton-user-list {
  display: grid;
  gap: var(--spacing-md);
}

.user-skel-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  pointer-events: none;
  border: 1px solid var(--border-panel-subtle);
}

.user-skel-body {
  flex: 1;
  min-width: 0;
  display: grid;
  gap: var(--spacing-xs);
}

.user-skel-avatar,
.user-skel-action {
  flex-shrink: 0;
}

.user-skel-intro {
  width: min(100%, 20rem) !important;
}

@include sourcelin-down(sm) {
  .user-skel-item {
    align-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
