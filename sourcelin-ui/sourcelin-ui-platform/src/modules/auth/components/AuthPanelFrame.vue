<template>
  <div class="auth-copy">
    <span class="auth-copy__eyebrow">{{ eyebrow }}</span>
    <h2 class="auth-copy__title">{{ title }}</h2>
    <p class="auth-copy__subtitle">{{ subtitle }}</p>
  </div>

  <div v-if="message" class="auth-message">
    <SIcon :icon="appIcons.sparkles" :size="16" />
    <span>{{ message }}</span>
  </div>

  <slot />
</template>

<script setup lang="ts">
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  eyebrow: string
  title: string
  subtitle: string
  message?: string
}

defineProps<Props>()
</script>

<style scoped lang="scss">
.auth-copy,
.auth-message {
  position: relative;
  z-index: 1;
}

.auth-copy {
  display: grid;
  gap: 6px;
  margin-bottom: 12px;
}

.auth-copy__eyebrow {
  font-size: 0.76rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.auth-copy__title {
  margin: 0;
  font-size: clamp(1.8rem, 2.8vw, 2.35rem);
  color: var(--title-color);
  line-height: 1.1;
}

.auth-copy__subtitle {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.65;
}

.auth-message {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding: 10px 12px;
  color: var(--text-primary);
}
</style>
