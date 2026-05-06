<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-title">页面不存在</div>
      <div class="error-description">抱歉，您访问的页面不存在或已被移除</div>
      <div class="error-actions">
        <SButton type="primary" size="large" @click="goHome">
          返回首页
        </SButton>
        <SButton size="large" @click="goBack">
          返回上页
        </SButton>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found-container {
  position: relative;
  isolation: isolate;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);
  background: var(--surface-page-content);
}

.not-found-container::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.not-found-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 500px;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  line-height: 1;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--spacing-lg);
}

.error-title {
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: var(--spacing-md);
}

.error-description {
  font-size: var(--font-size-lg);
  color: var(--text-color-muted);
  margin-bottom: var(--spacing-xxl);
}

.error-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
}
</style>


