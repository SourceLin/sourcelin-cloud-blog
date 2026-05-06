<template>
  <AuthScene :background-image="loginBackground">
    <template #visual>
      <AuthVisualShowcase
        badge="回到圆圈"
        title="继续你的阅读旅程"
        subtitle="登录后同步收藏、留言和主题偏好，让站点重新接住你的阅读轨迹。"
        :highlights="loginHighlights"
      />
    </template>

    <AuthPanelFrame
      eyebrow="Login"
      title="登录"
      subtitle="继续你的阅读、互动和书写节奏。"
      :message="loginMessage"
    />

    <form class="auth-form" @submit.prevent="handleSubmit">
      <div class="login-method-tabs">
        <button
          type="button"
          class="method-tab"
          :class="{ active: loginMethod === 'account' }"
          @click="switchLoginMethod('account')"
        >
          账号密码
        </button>
        <button
          type="button"
          class="method-tab"
          :class="{ active: loginMethod === 'email' }"
          @click="switchLoginMethod('email')"
        >
          邮箱验证码
        </button>
      </div>

      <!-- Tab 1: 账号密码登录 -->
      <template v-if="loginMethod === 'account'">
        <div class="field-group">
          <label class="field-label" for="login-username">账号</label>
          <SInput
            id="login-username"
            v-model="loginForm.username"
            class="auth-input"
            variant="filled"
            placeholder="请输入用户名或邮箱"
            autocomplete="username"
            clearable
          >
            <template #prefix>
              <SIcon :icon="loginIcons.user" :size="16" />
            </template>
          </SInput>
          <span v-if="errors.username" class="field-error">{{ errors.username }}</span>
        </div>

        <div class="field-group">
          <label class="field-label" for="login-password">密码</label>
          <SInput
            id="login-password"
            v-model="loginForm.password"
            class="auth-input"
            variant="filled"
            :type="showPassword ? 'text' : 'password'"
            placeholder="输入密码"
            autocomplete="current-password"
          >
            <template #prefix>
              <SIcon :icon="loginIcons.password" :size="16" />
            </template>
            <template #suffix>
              <button type="button" class="toggle-visibility" @click="showPassword = !showPassword">
                <SIcon :icon="showPassword ? loginIcons.eyeOff : loginIcons.eye" :size="16" />
              </button>
            </template>
          </SInput>
          <span v-if="errors.password" class="field-error">{{ errors.password }}</span>
        </div>
      </template>

      <!-- Tab 2: 邮箱验证码登录 -->
      <template v-else>
        <div class="field-group">
          <label class="field-label" for="login-email">邮箱</label>
          <SInput
            id="login-email"
            v-model="loginForm.email"
            class="auth-input"
            variant="filled"
            placeholder="请输入邮箱"
            autocomplete="email"
            clearable
          >
            <template #prefix>
              <SIcon :icon="loginIcons.email" :size="16" />
            </template>
          </SInput>
          <span v-if="errors.email" class="field-error">{{ errors.email }}</span>
        </div>
      </template>

      <!-- 图形验证码（两个 Tab 通用） -->
      <div v-if="captchaEnabled" class="field-group">
        <label class="field-label" for="login-captcha">图形验证码</label>
        <div class="captcha-row">
          <SInput
            id="login-captcha"
            v-model="loginForm.captchaCode"
            class="auth-input captcha-input"
            variant="filled"
            placeholder="输入验证码"
          >
            <template #prefix>
              <SIcon :icon="loginIcons.captcha" :size="16" />
            </template>
          </SInput>

          <button type="button" class="captcha-trigger" @click="refreshCaptcha">
            <img v-if="captchaUrl" :src="captchaUrl" alt="验证码">
            <span v-else>获取验证码</span>
          </button>
        </div>
        <span v-if="errors.captchaCode" class="field-error">{{ errors.captchaCode }}</span>
      </div>

      <!-- Tab 2: 邮箱验证码 -->
      <div v-if="loginMethod === 'email'" class="field-group">
        <label class="field-label" for="login-email-code">邮箱验证码</label>
        <div class="captcha-row">
          <SInput
            id="login-email-code"
            v-model="loginForm.emailCode"
            class="auth-input captcha-input"
            variant="filled"
            placeholder="输入6位验证码"
            maxlength="6"
          >
            <template #prefix>
              <SIcon :icon="loginIcons.captcha" :size="16" />
            </template>
          </SInput>

          <button
            type="button"
            class="captcha-trigger"
            :disabled="emailCountdown > 0 || emailCodeSending"
            @click="getEmailCode"
          >
            <span v-if="emailCodeSending">发送中...</span>
            <span v-else-if="emailCountdown > 0">{{ emailCountdown }}s</span>
            <span v-else>获取验证码</span>
          </button>
        </div>
        <span v-if="errors.emailCode" class="field-error">{{ errors.emailCode }}</span>
      </div>

      <div v-if="loginMethod === 'account'" class="auth-options">
        <SCheckbox v-model="loginForm.rememberMe" class="remember-toggle">记住我</SCheckbox>
        <div class="auth-links">
          <RouterLink class="switch-link" to="/register">注册</RouterLink>
          <span class="link-divider">|</span>
          <RouterLink class="switch-link" to="/forgot-password">忘记密码</RouterLink>
        </div>
      </div>
      <div v-else class="auth-options">
        <span></span>
        <div class="auth-links">
          <RouterLink class="switch-link" to="/register">注册</RouterLink>
          <span class="link-divider">|</span>
          <RouterLink class="switch-link" to="/forgot-password">忘记密码</RouterLink>
        </div>
      </div>

      <SButton
        class="submit-button"
        variant="cta"
        attr-type="submit"
        :loading="loading"
        :disabled="loading"
      >
        登录
      </SButton>

      <div class="auth-footer">
        <span>还没有账号？</span>
        <RouterLink class="switch-link" to="/register">立即注册</RouterLink>
      </div>
    </form>
  </AuthScene>
</template>

<script setup lang="ts">
import loginBackground from '@/assets/images/backgrounds/home-bg.jpg'
import AuthPanelFrame from '@/modules/auth/components/AuthPanelFrame.vue'
import AuthScene from '@/modules/auth/components/AuthScene.vue'
import AuthVisualShowcase from '@/modules/auth/components/AuthVisualShowcase.vue'
import { useLoginForm } from '@/modules/auth/composables/useLoginForm'
import SButton from '@/shared/components/ui/SButton.vue'
import SCheckbox from '@/shared/components/ui/SCheckbox.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import { appIcons } from '@/shared/components/ui/icons'

const {
  loading,
  showPassword,
  captchaUrl,
  captchaEnabled,
  loginForm,
  errors,
  loginMethod,
  emailCountdown,
  emailCodeSending,
  loginMessage,
  refreshCaptcha,
  getEmailCode,
  handleSubmit,
  switchLoginMethod
} = useLoginForm()

const loginIcons = {
  user: appIcons.person,
  password: appIcons.password,
  captcha: appIcons.captcha,
  eye: appIcons.eye,
  eyeOff: appIcons.eyeOff,
  email: appIcons.email
}

const loginHighlights = [
  {
    icon: appIcons.archive,
    title: '阅读同步',
    description: '继续翻看收藏、归档和最近停留过的入口。'
  },
  {
    icon: appIcons.comment,
    title: '互动记录',
    description: '留言、评论和主题偏好都会跟着你一起回来。'
  }
]
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.auth-form {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
}

.field-group {
  display: grid;
  gap: 6px;
}

.field-label {
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--text-primary);
}

.auth-input {
  width: 100%;
}

.toggle-visibility {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: 0;
  border-radius: var(--border-radius-sm);
  background: color-mix(in srgb, var(--surface-panel-chip-accent) 68%, transparent);
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    color var(--transition-base),
    background var(--transition-base),
    transform var(--transition-base);
}

.toggle-visibility:hover {
  color: var(--primary-color);
  background: var(--surface-panel-chip);
  transform: translateY(-1px);
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 118px;
  gap: 10px;
}

.captcha-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 0;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  color: var(--text-secondary);
  transition:
    border-color var(--transition-base),
    transform var(--transition-base),
    box-shadow var(--transition-base);
}

.captcha-trigger:hover {
  border-color: var(--border-interactive-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-panel-soft);
}

.captcha-trigger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.captcha-trigger img {
  display: block;
  width: 100%;
  height: 100%;
}

.captcha-trigger span {
  padding: 0 12px;
}

.field-error {
  font-size: 0.76rem;
  color: var(--error-color);
}

.auth-options,
.auth-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.auth-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.link-divider {
  color: var(--text-placeholder);
  font-size: 0.8rem;
}

.remember-toggle {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.submit-button {
  width: 100%;
  margin-top: 2px;
  min-height: 48px;
  padding: 10px 16px;
  border: 1px solid var(--border-panel-default);
  border-radius: var(--border-radius-md);
  background: var(--surface-panel-inset);
  color: var(--text-secondary);
  font-size: 0.88rem;
  font-weight: 500;
  box-shadow: none;
  transition:
    background var(--transition-base),
    border-color var(--transition-base),
    color var(--transition-base),
    transform var(--transition-base),
    box-shadow var(--transition-base);
}

.submit-button:hover {
  background: var(--surface-panel-inset);
  border-color: var(--border-interactive-hover);
  color: var(--text-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-panel-soft);
}

.submit-button:active {
  background: var(--surface-panel-chip);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.auth-footer {
  justify-content: center;
  color: var(--text-secondary);
  font-size: 0.88rem;
}

.switch-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.switch-link:hover {
  color: var(--primary-active);
}

.login-method-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.method-tab {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid var(--border-panel-default);
  border-radius: var(--border-radius-md);
  background: var(--surface-panel-inset);
  color: var(--text-secondary);
  font-size: 0.88rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
}

.method-tab:hover {
  border-color: var(--border-interactive-hover);
  color: var(--text-primary);
}

.method-tab.active {
  background: var(--surface-panel-chip);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

@include sourcelin-down(sm) {
  .captcha-row {
    grid-template-columns: 1fr;
  }

  .captcha-trigger {
    min-height: 54px;
  }
}
</style>
