<script setup lang="ts">
import defaultAvatar from '@/assets/images/logo/logo.png'

defineProps<{
  avatar?: string
  authorName: string
  authorIntroduction: string
  articleCount?: number
  followerCount?: number
  authorId?: number
  currentUserId?: number
  isFollowed: boolean
}>()

const emit = defineEmits<{ follow: [] }>()
</script>

<template>
  <SCard class="sidebar-card dossier-card" variant="lite" hoverable>
    <div class="card-heading">
      <SSurfaceChip class="article-card-prelude-label" tone="accent">作者档案</SSurfaceChip>
      <h2 class="visually-hidden">作者档案</h2>
    </div>
    <div class="dossier-profile">
      <img class="profile-avatar" :src="avatar || defaultAvatar" alt="avatar">
      <div class="profile-copy">
        <strong class="profile-name">{{ authorName }}</strong>
        <p class="profile-bio">{{ authorIntroduction }}</p>
      </div>
    </div>
    <div class="profile-stats">
      <SSurfacePanel class="stat-item" variant="inset" interactive>
        <span class="stat-value">{{ articleCount || '-' }}</span>
        <span class="stat-label">文章</span>
      </SSurfacePanel>
      <SSurfacePanel class="stat-item" variant="inset" interactive>
        <span class="stat-value">{{ followerCount || '-' }}</span>
        <span class="stat-label">关注者</span>
      </SSurfacePanel>
    </div>
    <SButton
      v-if="authorId && currentUserId !== authorId"
      :variant="isFollowed ? 'glass' : 'site'"
      size="small"
      class="follow-button"
      @click="emit('follow')"
    >
      {{ isFollowed ? '已关注作者' : '关注作者' }}
    </SButton>
  </SCard>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.sidebar-card {
  padding: var(--spacing-xxl);
}

.card-heading {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--border-panel-subtle);
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
}

.dossier-profile {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 18px;
}

.profile-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
}

.profile-copy {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.profile-bio {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.8;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 20px 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: var(--spacing-md);
  border-radius: calc(var(--border-radius-sm) + 4px);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
  will-change: transform, box-shadow;
}

.stat-item:hover {
  transform: translateY(-3px) scale(1.01);
  border-color: var(--border-interactive-hover);
  box-shadow: var(--shadow-panel-soft);
}

.follow-button {
  width: 100%;
}

@include sourcelin-down(md) {
  .sidebar-card {
    padding: 22px;
  }

  .profile-stats {
    grid-template-columns: 1fr;
  }
}
</style>
