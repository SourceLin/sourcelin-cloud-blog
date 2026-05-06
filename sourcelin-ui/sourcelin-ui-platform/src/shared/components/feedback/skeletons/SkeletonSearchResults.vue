<script setup lang="ts">
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'

withDefaults(
  defineProps<{
    count?: number
  }>(),
  {
    count: 5
  }
)
</script>

<template>
  <div class="skeleton-search-results" aria-hidden="true">
    <div class="search-skel-head">
      <div class="search-skel-head__title">
        <SSkeleton :sharp="false" class="search-skel-head__icon" :width="18" :height="18" />
        <SSkeleton :sharp="false" class="search-skel-head__label" :width="108" :height="18" />
      </div>
      <SSkeleton :sharp="false" class="search-skel-head__meta" :width="92" :height="14" />
    </div>

    <div class="search-skel-list">
      <SSurfacePanel
        v-for="index in count"
        :key="`search-skel-${index}`"
        tag="article"
        variant="inset"
        class="search-skel-item"
      >
        <SSkeleton :sharp="false" class="search-skel-item__leading" :width="38" :height="38" />
        <div class="search-skel-item__body">
          <SSkeleton :sharp="false" class="search-skel-item__title" :height="18" />
          <SSkeleton :sharp="false" class="search-skel-item__line" :height="12" />
          <SSkeleton :sharp="false" class="search-skel-item__line search-skel-item__line--short" :height="12" />
        </div>
      </SSurfacePanel>
    </div>
  </div>
</template>

<style scoped lang="scss">
.skeleton-search-results,
.search-skel-list {
  display: grid;
  gap: 16px;
}

.search-skel-head,
.search-skel-head__title,
.search-skel-item {
  display: flex;
  align-items: center;
}

.search-skel-head {
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.search-skel-head__title {
  gap: 8px;
}

.search-skel-item {
  gap: 14px;
  padding: 14px 16px;
  pointer-events: none;
}

.search-skel-item__body {
  flex: 1;
  min-width: 0;
  display: grid;
  gap: 8px;
}

.search-skel-item__leading {
  border-radius: var(--border-radius-lg) !important;
}

.search-skel-item__title,
.search-skel-item__line {
  width: 100% !important;
}

.search-skel-item__line--short {
  width: 72% !important;
}
</style>
