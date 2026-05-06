<template>
  <div class="auth-view">
    <div class="auth-view__decor" aria-hidden="true">
      <span class="orbit orbit--a" />
      <span class="orbit orbit--b" />
      <span class="orbit orbit--c" />
      <span class="spark spark--1" />
      <span class="spark spark--2" />
      <span class="spark spark--3" />
    </div>
    <div class="auth-view__toolbar">
      <ThemeSwitch @toggle="handleThemeToggle" />
    </div>

    <div class="auth-view__wrapper">
      <section class="auth-feature">
        <div class="auth-feature__badge">
          <span class="auth-feature__dot" />
          Sourcelin Blog
        </div>
        <h1 class="auth-feature__title">{{ appConfig.title }}</h1>
        <p class="auth-feature__subtitle">统一管理内容发布、权限策略与系统运营配置。</p>
        <div class="auth-feature__chips">
          <span class="chip">内容</span>
          <span class="chip">评论</span>
          <span class="chip">权限</span>
          <span class="chip">系统</span>
        </div>
        <div class="auth-feature__metrics">
          <div class="metric-card">
            <span class="metric-card__label">发布效率</span>
            <strong class="metric-card__value">草稿、审核、上线一体化</strong>
          </div>
          <div class="metric-card">
            <span class="metric-card__label">运维保障</span>
            <strong class="metric-card__value">账号安全与权限边界可追踪</strong>
          </div>
        </div>
      </section>

      <section class="auth-panel">
        <div class="auth-panel__brand">
          <div class="auth-panel__logo-wrap" aria-hidden="true">
            <el-image :src="logo" class="auth-panel__logo" />
          </div>
          <div class="auth-panel__meta">
            <div class="auth-panel__title-row">
              <span class="auth-panel__title">登录</span>
            </div>
            <div v-if="appConfig.version" class="auth-panel__version-row">
              <el-text size="small" type="info">VERSION</el-text>
              <el-tag size="small" effect="light" round>
                {{ `v${appConfig.version}` }}
              </el-tag>
            </div>
          </div>
        </div>

        <Login class="auth-panel__form" />

        <footer class="auth-panel__footer">
          <el-text size="small">Copyright © 2026 Sourcelin</el-text>
        </footer>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import logo from "@/assets/images/logo.png";
import { appConfig } from "@/settings";
import ThemeSwitch from "@/components/ThemeSwitch/index.vue";
import { useThemeToggle } from "@/composables";
import Login from "./components/Login.vue";
const { handleThemeToggle } = useThemeToggle();
</script>

<style lang="scss" scoped>
.auth-view {
  --auth-primary-soft: color-mix(in srgb, var(--el-color-primary) 24%, transparent);
  --auth-primary-soft-strong: color-mix(in srgb, var(--el-color-primary) 18%, transparent);
  --auth-primary-soft-faint: color-mix(in srgb, var(--el-color-primary) 12%, transparent);
  --auth-primary-border: color-mix(in srgb, var(--el-color-primary) 16%, transparent);
  --auth-primary-border-strong: color-mix(in srgb, var(--el-color-primary) 22%, transparent);
  --auth-primary-glow: color-mix(in srgb, var(--el-color-primary) 14%, transparent);
  --auth-primary-glow-strong: color-mix(in srgb, var(--el-color-primary) 22%, transparent);
  --auth-primary-light-soft: color-mix(in srgb, var(--el-color-primary-light-3) 18%, transparent);
  --auth-primary-light-border: color-mix(in srgb, var(--el-color-primary-light-3) 18%, transparent);
  --auth-badge-text: color-mix(in srgb, var(--el-color-primary) 92%, var(--el-text-color-primary));
  --auth-title-color: var(--el-text-color-primary);
  --auth-subtitle-color: var(--el-text-color-regular);
  --auth-muted-color: var(--el-text-color-secondary);
  --auth-dark-surface: color-mix(in srgb, var(--el-bg-color) 88%, #10131b);
  --auth-dark-surface-soft: color-mix(in srgb, var(--el-bg-color) 76%, #181c2b);
  --auth-dark-shadow: color-mix(in srgb, black 48%, transparent);
  position: relative;
  z-index: 0;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100dvh;
  min-height: 100dvh;
  padding: clamp(0.75rem, 2vw, 1.5rem);
  overflow: hidden;
  background-color: var(--el-bg-color);
  isolation: isolate;

  &::before {
    position: fixed;
    inset: 0;
    z-index: -2;
    content: "";
    background: url("@/assets/images/login_bg_light.png") center/cover no-repeat;
  }

  &::after {
    position: fixed;
    inset: 0;
    z-index: -1;
    pointer-events: none;
    content: "";
    background:
      url("@/assets/images/login-bg1.svg") center / cover no-repeat,
      radial-gradient(
        760px 560px at 18% 12%,
        color-mix(in srgb, var(--auth-primary-soft) 58%, transparent),
        transparent 60%
      ),
      radial-gradient(
        560px 360px at 88% 10%,
        color-mix(in srgb, var(--auth-primary-light-soft) 56%, transparent),
        transparent 64%
      ),
      radial-gradient(
        620px 420px at 78% 78%,
        color-mix(in srgb, var(--auth-primary-soft-faint) 50%, transparent),
        transparent 60%
      ),
      linear-gradient(135deg, rgba(255, 255, 255, 0.34), rgba(255, 255, 255, 0.06));
  }
}

html.dark .auth-view {
  --auth-title-color: color-mix(
    in srgb,
    var(--el-color-white) 94%,
    var(--el-color-primary-light-5)
  );
  --auth-subtitle-color: color-mix(
    in srgb,
    var(--el-color-white) 82%,
    var(--el-color-primary-light-5)
  );
  --auth-muted-color: color-mix(
    in srgb,
    var(--el-color-white) 72%,
    var(--el-color-primary-light-5)
  );
}

html.dark .auth-view::before {
  background: url("@/assets/images/login_bg_dark.png") center/cover no-repeat;
}

html.dark .auth-view::after {
  background:
    url("@/assets/images/login-bg1.svg") center / cover no-repeat,
    radial-gradient(720px 520px at 18% 16%, var(--auth-primary-light-soft), transparent 62%),
    linear-gradient(135deg, rgba(10, 12, 20, 0.34), rgba(10, 12, 20, 0));
}

.auth-view__decor {
  position: fixed;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  mix-blend-mode: multiply;
  opacity: 0.88;

  .orbit {
    position: absolute;
    border: 1px solid var(--auth-primary-border-strong);
    border-radius: 999px;
    box-shadow:
      0 0 0 1px rgba(255, 255, 255, 0.3) inset,
      0 24px 80px var(--auth-primary-glow);
    transform-origin: center;
  }

  .orbit--a {
    top: 8%;
    left: -10%;
    width: 580px;
    height: 580px;
    animation: orbitSpin 26s linear infinite;
  }

  .orbit--b {
    right: -16%;
    bottom: -18%;
    width: 720px;
    height: 720px;
    border-color: var(--auth-primary-border);
    animation: orbitSpinReverse 34s linear infinite;
  }

  .orbit--c {
    top: 42%;
    left: 54%;
    width: 260px;
    height: 260px;
    border-color: var(--auth-primary-border);
    border-style: dashed;
    animation: orbitFloat 9s ease-in-out infinite;
  }

  .spark {
    position: absolute;
    background: radial-gradient(
      circle at 30% 30%,
      rgba(255, 255, 255, 0.92),
      color-mix(in srgb, var(--el-color-primary) 46%, transparent)
    );
    border-radius: 999px;
    box-shadow:
      0 12px 30px var(--auth-primary-glow-strong),
      0 0 0 6px var(--auth-primary-glow);
    animation: sparkTwinkle 3.6s ease-in-out infinite;
  }

  .spark--1 {
    top: 18%;
    left: 34%;
    width: 14px;
    height: 14px;
  }

  .spark--2 {
    top: 64%;
    left: 18%;
    width: 10px;
    height: 10px;
    animation-delay: 1.2s;
  }

  .spark--3 {
    top: 26%;
    right: 14%;
    width: 8px;
    height: 8px;
    animation-delay: 2.1s;
  }
}

html.dark .auth-view__decor {
  mix-blend-mode: screen;
  opacity: 0.68;

  .orbit {
    border-color: var(--auth-primary-border-strong);
    box-shadow: 0 0 0 1px var(--auth-primary-glow) inset;
  }

  .orbit--b {
    border-color: var(--auth-primary-border);
  }

  .spark {
    background: radial-gradient(
      circle at 30% 30%,
      rgba(255, 255, 255, 0.86),
      color-mix(in srgb, var(--el-color-primary-light-3) 50%, transparent)
    );
    box-shadow:
      0 12px 28px rgba(0, 0, 0, 0.42),
      0 0 0 5px var(--auth-primary-glow);
  }
}

.auth-view__toolbar {
  position: absolute;
  top: clamp(0.95rem, 3.2vw, 3rem);
  right: clamp(0.95rem, 3.2vw, 3rem);
  z-index: 10;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.25rem;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid var(--auth-primary-border);
  border-radius: 999px;
  box-shadow: 0 10px 30px var(--auth-primary-glow);

  @media (max-width: 640px) {
    position: fixed;
    top: 12px;
    right: 16px;
  }
}

html.dark .auth-view__toolbar {
  background: color-mix(in srgb, var(--el-bg-color-overlay) 88%, #181c2b);
  border-color: var(--auth-primary-border-strong);
  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.55),
    0 0 0 1px var(--auth-primary-glow) inset;
}

.auth-view__wrapper {
  --wrapper-padding-y: clamp(2rem, 4.2vh, 3.5rem);
  --wrapper-padding-x: clamp(1.25rem, 3.8vw, 4.5rem);
  --feature-offset-y: clamp(-0.5rem, -1.2vh, -0.25rem);
  --panel-offset-y: clamp(1.5rem, 2.8vh, 2.1rem);
  position: relative;
  z-index: 1;
  display: grid;
  flex: 1;
  grid-template-columns: minmax(0, 1fr) minmax(380px, 460px);
  gap: clamp(2rem, 6vw, 8rem);
  align-items: center;
  width: 82%;
  min-height: calc(100dvh - clamp(1.5rem, 4vw, 3rem));
  padding: var(--wrapper-padding-y) var(--wrapper-padding-x);
  margin: 0 0 0 auto;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0) 42%,
    rgba(255, 255, 255, 0.5) 100%
  );
  border: 1px solid var(--auth-primary-border);
  border-radius: 32px;
  box-shadow:
    0 28px 80px var(--auth-primary-glow),
    0 0 0 1px rgba(255, 255, 255, 0.45) inset;
  backdrop-filter: blur(16px);

  @media (min-width: 1980px) {
    gap: clamp(3.2rem, 5.4vw, 8.6rem);
    width: 74%;
    --wrapper-padding-y: clamp(2.3rem, 4.2vh, 3.7rem);
    --wrapper-padding-x: clamp(2.1rem, 4.8vw, 5.4rem);
    --feature-offset-y: clamp(-0.6rem, -1.2vh, -0.32rem);
    --panel-offset-y: clamp(1.85rem, 2.9vh, 2.45rem);
  }

  @media (min-width: 1680px) and (max-width: 1979px) {
    gap: clamp(3rem, 6vw, 9rem);
    width: 76%;
    --wrapper-padding-y: clamp(2.5rem, 4.8vh, 4rem);
    --wrapper-padding-x: clamp(2.25rem, 5.5vw, 6rem);
    --feature-offset-y: clamp(-0.65rem, -1.4vh, -0.35rem);
    --panel-offset-y: clamp(1.75rem, 3vh, 2.4rem);
  }

  @media (min-width: 1366px) and (max-width: 1679px) {
    gap: clamp(2.2rem, 4.8vw, 6.6rem);
    width: 79%;
    --wrapper-padding-y: clamp(1.9rem, 3.8vh, 3rem);
    --wrapper-padding-x: clamp(1.35rem, 3.3vw, 3.2rem);
    --feature-offset-y: clamp(-0.35rem, -0.9vh, -0.18rem);
    --panel-offset-y: clamp(1.25rem, 2.1vh, 1.75rem);
  }

  @media (max-height: 760px) {
    --wrapper-padding-y: clamp(1.25rem, 2.8vh, 2rem);
    --feature-offset-y: -0.3rem;
    --panel-offset-y: 1rem;
    min-height: auto;
    margin: 0 auto;
  }

  @media (max-width: 1200px) {
    width: 88%;
    --wrapper-padding-x: clamp(1rem, 3vw, 2.25rem);
    --panel-offset-y: clamp(1rem, 2vh, 1.5rem);
  }

  @media (max-width: 960px) {
    grid-template-columns: 1fr;
    justify-items: center;
    width: 100%;
    min-height: auto;
    padding-top: clamp(4.5rem, 10vh, 5rem);
    padding-bottom: clamp(1.25rem, 3.2vh, 2rem);
    margin: 0 auto;
    --wrapper-padding-y: clamp(1.75rem, 4.5vh, 2.5rem);
    --wrapper-padding-x: clamp(1rem, 4vw, 1.5rem);
    --feature-offset-y: 0;
    --panel-offset-y: 0;
  }
}

html.dark .auth-view__wrapper {
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--el-bg-color-overlay) 0%, transparent) 0%,
    color-mix(in srgb, var(--el-bg-color-overlay) 0%, transparent) 42%,
    color-mix(in srgb, var(--el-bg-color-overlay) 62%, transparent) 100%
  );
  box-shadow:
    0 28px 80px rgba(0, 0, 0, 0.28),
    0 0 0 1px var(--auth-primary-glow) inset;
}

.auth-feature {
  display: flex;
  flex-direction: column;
  gap: 1.15rem;
  align-self: center;
  justify-content: center;
  max-width: 620px;
  padding: clamp(0.75rem, 2.3vh, 1.5rem) 0;
  color: var(--el-text-color-primary);
  transform: translateY(var(--feature-offset-y));
  transition: transform 0.3s ease;

  @media (max-width: 960px) {
    display: none;
  }

  @media (min-width: 1366px) and (max-width: 1599px) {
    max-width: 600px;
  }
}

.auth-feature__badge {
  display: inline-flex;
  gap: 0.55rem;
  align-items: center;
  width: fit-content;
  padding: 0.45rem 0.75rem;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--auth-badge-text);
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid var(--auth-primary-border);
  border-radius: 999px;
  backdrop-filter: blur(14px);
}

html.dark .auth-feature__badge {
  color: color-mix(in srgb, var(--el-color-primary-light-3) 92%, var(--el-color-white));
  background: color-mix(in srgb, var(--el-bg-color-overlay) 50%, transparent);
  border-color: var(--auth-primary-border);
}

.auth-feature__dot {
  width: 0.45rem;
  height: 0.45rem;
  background: var(--el-color-primary);
  border-radius: 50%;
  box-shadow: 0 0 10px var(--auth-primary-glow-strong);
}

.auth-feature__title {
  max-width: 12.5ch;
  margin: 0;
  font-size: clamp(2.45rem, 4.6vw, 4rem);
  font-weight: 700;
  line-height: 1.12;
  color: var(--auth-title-color);
  letter-spacing: 0.01em;
  word-break: keep-all;

  @media (min-width: 1366px) and (max-width: 1599px) {
    max-width: 13.5ch;
    font-size: clamp(2.25rem, 4.2vw, 3.45rem);
    line-height: 1.14;
  }
}

.auth-feature__subtitle {
  max-width: 34ch;
  margin: 0;
  font-size: clamp(1.02rem, 1.28vw, 1.25rem);
  line-height: 1.72;
  color: var(--auth-subtitle-color);
}

.auth-feature__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  margin-top: 0.25rem;
}

.chip {
  display: inline-flex;
  align-items: center;
  padding: 0.42rem 0.72rem;
  font-size: 0.82rem;
  color: var(--auth-badge-text);
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid var(--auth-primary-border);
  border-radius: 999px;
  backdrop-filter: blur(14px);
}

html.dark .chip {
  color: color-mix(in srgb, var(--el-color-primary-light-3) 92%, var(--el-color-white));
  background: color-mix(in srgb, var(--el-bg-color-overlay) 46%, transparent);
  border-color: var(--auth-primary-border);
}

.auth-feature__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.9rem;
  max-width: 500px;
  margin-top: 0.85rem;
}

.metric-card {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  padding: 1rem 1.05rem;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid var(--auth-primary-border);
  border-radius: 18px;
  box-shadow:
    0 16px 40px var(--auth-primary-glow),
    0 0 0 1px rgba(255, 255, 255, 0.28) inset;
  backdrop-filter: blur(16px);
}

html.dark .metric-card {
  background: color-mix(in srgb, var(--el-bg-color-overlay) 50%, transparent);
  border-color: var(--auth-primary-border);
  box-shadow:
    0 18px 48px rgba(0, 0, 0, 0.36),
    0 0 0 1px var(--auth-primary-glow) inset;
}

.metric-card__label {
  font-size: 0.78rem;
  color: var(--auth-muted-color);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.metric-card__value {
  font-size: 0.95rem;
  font-weight: 600;
  line-height: 1.4;
  color: var(--auth-title-color);
}

.auth-panel {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-self: center;
  width: min(460px, 100%);
  min-height: 0;
  padding: clamp(1.6rem, 3.2vw, 2.15rem);
  margin-top: 0;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid var(--auth-primary-border);
  border-radius: 22px;
  box-shadow:
    0 24px 62px var(--auth-primary-glow),
    0 0 0 1px rgba(255, 255, 255, 0.56) inset;
  backdrop-filter: blur(20px);
  transform: translateY(var(--panel-offset-y));
  transition: transform 0.3s ease;

  @media (max-width: 960px) {
    margin-top: 0;
  }

  @media (min-width: 1366px) and (max-width: 1599px) {
    width: min(446px, 100%);
    padding: clamp(1.45rem, 2.6vw, 1.9rem);
  }
}

html.dark .auth-panel {
  background: var(--auth-dark-surface);
  border-color: var(--auth-primary-border-strong);
  box-shadow:
    0 24px 68px rgba(0, 0, 0, 0.54),
    0 0 0 1px var(--auth-primary-glow) inset;
}

.auth-panel__brand {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--auth-primary-border);
}

html.dark .auth-panel__brand {
  border-color: var(--auth-primary-border);
}

.auth-panel__logo-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.96),
    color-mix(in srgb, var(--el-color-primary-light-9) 92%, white)
  );
  border-radius: 18px;
  box-shadow:
    0 8px 20px var(--auth-primary-glow-strong),
    0 0 0 1px rgba(255, 255, 255, 0.8) inset;
}

html.dark .auth-panel__logo-wrap {
  background: linear-gradient(
    145deg,
    color-mix(in srgb, var(--el-bg-color-overlay) 96%, #232a3f),
    var(--auth-dark-surface-soft)
  );
  box-shadow:
    0 8px 20px rgba(0, 0, 0, 0.48),
    0 0 0 1px var(--auth-primary-border-strong) inset;
}

.auth-panel__logo {
  width: 32px;
  height: 32px;
}

.auth-panel__meta {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.3rem;
  min-width: 0;
}

.auth-panel__title-row {
  display: flex;
  align-items: center;
}

.auth-panel__title {
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 1.15rem;
  font-weight: 650;
  line-height: 1.4;
  color: var(--auth-title-color);
  white-space: nowrap;
}

.auth-panel__version-row {
  display: inline-flex;
  gap: 0.5rem;
  align-items: center;
  font-size: 0.78rem;
}

.auth-panel__form {
  width: 100%;
  margin-top: 0.35rem;

  :deep(.el-form-item) {
    margin-bottom: 0.95rem;
  }

  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px var(--el-border-color) inset;
    transition: box-shadow 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px var(--el-border-color-hover) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) inset;
    }
  }

  :deep(.el-input__inner::placeholder) {
    color: var(--el-text-color-placeholder);
  }
}

html.dark .auth-panel__form {
  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px color-mix(in srgb, var(--el-color-white) 14%, transparent) inset;

    &:hover {
      box-shadow: 0 0 0 1px color-mix(in srgb, var(--el-color-white) 22%, transparent) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) inset;
    }
  }

  :deep(.el-input__inner::placeholder) {
    color: color-mix(in srgb, var(--el-color-white) 58%, transparent);
  }
}

.auth-panel__footer {
  padding-top: 0.95rem;
  font-size: 0.875rem;
  text-align: center;
  border-top: 1px solid color-mix(in srgb, var(--auth-primary-border) 78%, transparent);
}

html.dark .auth-panel__footer {
  border-color: var(--auth-primary-border);
}

@keyframes orbitSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes orbitSpinReverse {
  from {
    transform: rotate(360deg);
  }
  to {
    transform: rotate(0deg);
  }
}

@keyframes orbitFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes sparkTwinkle {
  0%,
  100% {
    opacity: 0.82;
    transform: translateY(0) scale(1);
  }
  50% {
    opacity: 0.56;
    transform: translateY(-6px) scale(1.08);
  }
}
</style>
