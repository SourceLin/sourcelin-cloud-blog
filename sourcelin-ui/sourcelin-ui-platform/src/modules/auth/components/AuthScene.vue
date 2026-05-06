<template>
  <div class="auth-scene">
    <div class="auth-scene__photo" :style="sceneImageStyle" />
    <div class="auth-scene__backdrop" />
    <div class="auth-scene__veil" />
    <div class="auth-scene__glow auth-scene__glow--one" />
    <div class="auth-scene__glow auth-scene__glow--two" />

    <div class="auth-scene__frame">
      <div class="auth-scene__topbar">
        <RouterLink class="auth-scene__home" to="/">
          <span class="auth-scene__home-mark">
            <img :src="logoImage" alt="圆圈博客 Logo" class="auth-scene__home-logo">
          </span>
          <span class="auth-scene__home-text">圆圈博客</span>
        </RouterLink>

        <RouterLink class="auth-scene__return" to="/">
          <SIcon :icon="appIcons.home" :size="15" />
          <span>返回首页</span>
        </RouterLink>
      </div>

      <div class="auth-scene__shell">
        <section class="auth-scene__visual">
          <div class="auth-scene__visual-photo" :style="sceneImageStyle" />
          <div class="auth-scene__visual-overlay" />
          <div class="auth-scene__visual-content">
            <slot name="visual" />
          </div>
        </section>

        <section class="auth-scene__panel">
          <slot />
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import logoImage from '@/assets/images/logo/logo.png'
import SIcon from '@/shared/components/ui/SIcon.vue'
import { appIcons } from '@/shared/components/ui/icons'

interface Props {
  backgroundImage: string
}

const props = defineProps<Props>()

const sceneImageStyle = computed(() => ({
  backgroundImage: `url("${props.backgroundImage}")`
}))
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.auth-scene {
  position: relative;
  box-sizing: border-box;
  height: 100dvh;
  min-height: 100svh;
  overflow-x: hidden;
  overflow-y: auto;
  isolation: isolate;
  padding:
    calc(env(safe-area-inset-top, 0px) + clamp(20px, 4vh, 42px))
    clamp(18px, 3vw, 32px)
    calc(env(safe-area-inset-bottom, 0px) + clamp(18px, 3vw, 28px));
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--background-color-deep) 6%, transparent) 0%,
      color-mix(in srgb, var(--body-bg) 8%, transparent) 55%,
      color-mix(in srgb, var(--body-bg) 14%, transparent) 100%
    );
}

.auth-scene__photo {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  z-index: 0;
  filter: saturate(1.02) contrast(1.02);
}

.auth-scene__backdrop,
.auth-scene__veil,
.auth-scene__glow {
  position: fixed;
  inset: 0;
  pointer-events: none;
}

.auth-scene__backdrop {
  background:
    radial-gradient(circle at top center, color-mix(in srgb, var(--surface-white-15) 14%, transparent), transparent 36%),
    linear-gradient(
      90deg,
      color-mix(in srgb, var(--background-color-deep) 2%, transparent) 0%,
      transparent 32%,
      color-mix(in srgb, var(--body-bg) 4%, transparent) 100%
    ),
    linear-gradient(180deg, color-mix(in srgb, var(--body-bg) 2%, transparent), color-mix(in srgb, var(--body-bg) 10%, transparent));
  transform: scale(1.02);
  filter: saturate(1.02) contrast(1.04);
  z-index: 1;
}

.auth-scene__veil {
  background:
    radial-gradient(circle at top center, color-mix(in srgb, var(--surface-white-15) 10%, transparent), transparent 34%),
    linear-gradient(135deg, color-mix(in srgb, var(--body-bg) 1%, transparent), color-mix(in srgb, var(--body-bg) 6%, transparent));
  backdrop-filter: blur(1.5px) saturate(1.01);
  z-index: 2;
}

.auth-scene__glow {
  inset: auto;
  width: clamp(280px, 34vw, 520px);
  height: clamp(280px, 34vw, 520px);
  border-radius: 999px;
  filter: blur(88px);
  opacity: 0.28;
  z-index: 2;
}

.auth-scene__glow--one {
  top: -10%;
  right: -8%;
  background: color-mix(in srgb, var(--primary-color) 28%, transparent);
}

.auth-scene__glow--two {
  bottom: -14%;
  left: -10%;
  background: color-mix(in srgb, var(--secondary-color, var(--primary-color)) 20%, transparent);
}

.auth-scene__frame {
  position: relative;
  z-index: 3;
  width: min(1220px, 100%);
  height: 100%;
  max-height: 100%;
  min-height: 0;
  margin: 0 auto;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: clamp(10px, 1.4vw, 16px);
}

.auth-scene__topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-inline: 4px;
}

.auth-scene__home,
.auth-scene__return {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  transition:
    transform var(--transition-base),
    opacity var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    box-shadow var(--transition-base);
}

.auth-scene__home-mark {
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.auth-scene__home-logo {
  width: 24px;
  height: 24px;
  object-fit: contain;
  display: block;
}

.auth-scene__home-text {
  font-size: 0.94rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--title-color);
}

.auth-scene__return {
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  color: var(--text-primary);
  border: 1px solid color-mix(in srgb, var(--border-panel-default) 86%, transparent);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-15) 10%, transparent),
      color-mix(in srgb, var(--surface-panel-default) 46%, transparent)
    ),
    color-mix(in srgb, var(--surface-panel-default) 88%, transparent);
  box-shadow:
    var(--shadow-panel-soft),
    var(--highlight-panel-soft);
  -webkit-backdrop-filter: blur(8px) saturate(1.02);
  backdrop-filter: blur(8px) saturate(1.02);
}

.auth-scene__home:hover,
.auth-scene__return:hover {
  transform: translateY(-1px);
}

.auth-scene__home:hover {
  opacity: 0.92;
}

.auth-scene__return:hover {
  border-color: var(--border-interactive-hover);
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 74%, transparent);
  box-shadow:
    var(--shadow-panel-hover),
    var(--highlight-panel-chip);
}

.auth-scene__shell {
  position: relative;
  min-height: 0;
  height: 100%;
  display: grid;
  grid-template-columns: minmax(0, 1.04fr) minmax(380px, 0.96fr);
  gap: clamp(6px, 1vw, 12px);
  align-items: stretch;
  overflow: hidden;
  border-radius: calc(var(--glass-radius) + 8px);
  border: 1px solid color-mix(in srgb, var(--border-panel-default) 84%, transparent);
  background:
    linear-gradient(
      120deg,
      color-mix(in srgb, var(--surface-panel-soft) 48%, transparent) 0%,
      color-mix(in srgb, var(--surface-panel-default) 38%, transparent) 45%,
      color-mix(in srgb, var(--surface-panel-default) 52%, transparent) 100%
    );
  box-shadow:
    0 28px 72px color-mix(in srgb, var(--background-color-deep) 10%, transparent),
    inset 0 1px 0 color-mix(in srgb, var(--surface-white-15) 12%, transparent);
  backdrop-filter: blur(12px) saturate(1.02);
}

.auth-scene__shell::before {
  content: '';
  position: absolute;
  inset: clamp(10px, 1.6vw, 16px);
  border-radius: calc(var(--glass-radius) + 6px);
  border: 1px solid color-mix(in srgb, var(--border-panel-subtle) 56%, transparent);
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--surface-white-15) 12%, transparent),
    transparent 48%
  );
  opacity: 0.3;
  pointer-events: none;
  z-index: 0;
}

.auth-scene__visual,
.auth-scene__panel {
  position: relative;
  min-height: 0;
  overflow: hidden;
}

.auth-scene__visual-photo,
.auth-scene__visual-overlay {
  position: absolute;
  inset: 0;
}

.auth-scene__visual-photo {
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  transform: scale(1.03);
  filter: saturate(1.04) contrast(1.06);
}

.auth-scene__visual {
  display: flex;
  align-items: flex-end;
  padding: clamp(22px, 3vw, 34px);
  background:
    linear-gradient(
      90deg,
      color-mix(in srgb, var(--background-color-deep) 3%, transparent),
      transparent 34%
    );
}

.auth-scene__visual-overlay {
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--background-color-deep) 8%, transparent) 0%,
      color-mix(in srgb, var(--body-bg) 24%, transparent) 70%,
      color-mix(in srgb, var(--body-bg) 34%, transparent) 100%
    ),
    radial-gradient(circle at top right, color-mix(in srgb, var(--primary-color) 8%, transparent), transparent 46%),
    linear-gradient(120deg, color-mix(in srgb, var(--background-color-deep) 6%, transparent), transparent 42%);
}

.auth-scene__visual-content,
.auth-scene__panel {
  position: relative;
  z-index: 1;
}

.auth-scene__visual-content {
  width: min(100%, 40rem);
}

.auth-scene__panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: clamp(18px, 2.4vw, 28px);
  background:
    linear-gradient(
      180deg,
      color-mix(in srgb, var(--surface-white-15) 10%, transparent),
      color-mix(in srgb, var(--surface-panel-default) 46%, transparent)
    );
}

.auth-scene__panel::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    radial-gradient(circle at top center, color-mix(in srgb, var(--surface-white-15) 12%, transparent), transparent 42%),
    linear-gradient(180deg, color-mix(in srgb, var(--surface-white-15) 5%, transparent), transparent 36%);
  pointer-events: none;
  opacity: 0.56;
}

@media (max-height: 860px) {
  .auth-scene {
    height: auto;
  }

  .auth-scene__frame {
    height: auto;
    min-height: auto;
  }

  .auth-scene__shell {
    height: auto;
    overflow: visible;
  }

  .auth-scene__panel {
    justify-content: flex-start;
  }
}

@include sourcelin-down(lg) {
  .auth-scene {
    padding:
      calc(env(safe-area-inset-top, 0px) + 14px)
      16px
      calc(env(safe-area-inset-bottom, 0px) + 14px);
  }

  .auth-scene__frame {
    height: auto;
    min-height: auto;
  }

  .auth-scene__shell {
    grid-template-columns: 1fr;
  }

  .auth-scene__visual {
    display: none;
  }

  .auth-scene__panel {
    min-height: auto;
    justify-content: flex-start;
    padding: 20px 18px;
  }
}

@include sourcelin-down(sm) {
  .auth-scene {
    padding:
      calc(env(safe-area-inset-top, 0px) + 12px)
      12px
      calc(env(safe-area-inset-bottom, 0px) + 12px);
  }

  .auth-scene__topbar {
    gap: 10px;
    flex-wrap: wrap;
  }

  .auth-scene__panel {
    padding: 18px 14px;
  }
}
</style>
