<template>
  <AuthScene :background-image="registerBackground">
    <template #visual>
      <AuthVisualShowcase
        badge="加入圆圈"
        title="把灵感安放下来"
        subtitle="注册后，收藏、留言和个人栏目都会跟着你留在这里。"
        :highlights="registerHighlights"
      />
    </template>

    <AuthPanelFrame
      eyebrow="Register"
      title="注册"
      subtitle="建立你的个人入口，开始留下阅读与书写的痕迹。"
    />

    <form class="auth-form" @submit.prevent="handleRegister">
      <div class="field-group">
        <label class="field-label" for="register-username">用户名</label>
        <SInput
          id="register-username"
          v-model="registerForm.username"
          class="auth-input"
          variant="filled"
          placeholder="设置一个用户名"
          autocomplete="username"
          clearable
        >
          <template #prefix>
            <SIcon :icon="registerIcons.user" :size="16" />
          </template>
        </SInput>
        <span v-if="errors.username" class="field-error">{{ errors.username }}</span>
      </div>

      <div class="field-group">
        <label class="field-label" for="register-password">密码</label>
        <SInput
          id="register-password"
          v-model="registerForm.password"
          class="auth-input"
          variant="filled"
          :type="showPassword ? 'text' : 'password'"
          placeholder="设置 5-20 位密码"
          autocomplete="new-password"
        >
          <template #prefix>
            <SIcon :icon="registerIcons.password" :size="16" />
          </template>
          <template #suffix>
            <button type="button" class="toggle-visibility" @click="showPassword = !showPassword">
              <SIcon :icon="showPassword ? registerIcons.eyeOff : registerIcons.eye" :size="16" />
            </button>
          </template>
        </SInput>
        <span v-if="errors.password" class="field-error">{{ errors.password }}</span>
      </div>

      <div class="field-group">
        <label class="field-label" for="register-confirm-password">确认密码</label>
        <SInput
          id="register-confirm-password"
          v-model="registerForm.confirmPassword"
          class="auth-input"
          variant="filled"
          :type="showConfirmPassword ? 'text' : 'password'"
          placeholder="再次输入密码"
          autocomplete="new-password"
        >
          <template #prefix>
            <SIcon :icon="registerIcons.password" :size="16" />
          </template>
          <template #suffix>
            <button
              type="button"
              class="toggle-visibility"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <SIcon :icon="showConfirmPassword ? registerIcons.eyeOff : registerIcons.eye" :size="16" />
            </button>
          </template>
        </SInput>
        <span v-if="errors.confirmPassword" class="field-error">{{ errors.confirmPassword }}</span>
      </div>

      <div class="field-group">
        <label class="field-label" for="register-email">邮箱</label>
        <SInput
          id="register-email"
          v-model="registerForm.email"
          class="auth-input"
          variant="filled"
          placeholder="填写邮箱，便于找回密码"
          autocomplete="email"
          clearable
        >
          <template #prefix>
            <SIcon :icon="registerIcons.email" :size="16" />
          </template>
        </SInput>
        <span v-if="errors.email" class="field-error">{{ errors.email }}</span>
      </div>

      <div v-if="captchaEnabled" class="field-group">
        <label class="field-label" for="register-captcha">图形验证码</label>
        <div class="captcha-row">
          <SInput
            id="register-captcha"
            v-model="registerForm.captchaCode"
            class="auth-input captcha-input"
            variant="filled"
            placeholder="输入验证码"
          >
            <template #prefix>
              <SIcon :icon="registerIcons.captcha" :size="16" />
            </template>
          </SInput>

          <button type="button" class="captcha-trigger" @click="refreshCaptcha">
            <img v-if="captchaUrl" :src="captchaUrl" alt="验证码">
            <span v-else>获取验证码</span>
          </button>
        </div>
        <span v-if="errors.captchaCode" class="field-error">{{ errors.captchaCode }}</span>
      </div>

      <div class="field-group">
        <label class="field-label" for="register-email-code">邮箱验证码</label>
        <div class="captcha-row">
          <SInput
            id="register-email-code"
            v-model="registerForm.emailCode"
            class="auth-input captcha-input"
            variant="filled"
            placeholder="输入6位验证码"
            maxlength="6"
          >
            <template #prefix>
              <SIcon :icon="registerIcons.captcha" :size="16" />
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
            <span v-else>发送验证码</span>
          </button>
        </div>
        <span v-if="errors.emailCode" class="field-error">{{ errors.emailCode }}</span>
      </div>

      <SButton
        class="submit-button"
        variant="cta"
        attr-type="submit"
        :loading="loading"
        :disabled="loading"
      >
        注册
      </SButton>

      <div class="auth-footer">
        <span>已经有账号了？</span>
        <RouterLink class="switch-link" to="/login">返回登录</RouterLink>
      </div>
    </form>
  </AuthScene>
</template>

<script setup lang="ts">
import registerBackground from '@/assets/images/backgrounds/home-bg.jpg'
import AuthPanelFrame from '@/modules/auth/components/AuthPanelFrame.vue'
import AuthScene from '@/modules/auth/components/AuthScene.vue'
import AuthVisualShowcase from '@/modules/auth/components/AuthVisualShowcase.vue'
import { useRegisterForm } from '@/modules/auth/composables/useRegisterForm'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import { appIcons } from '@/shared/components/ui/icons'

const {
  loading,
  showPassword,
  showConfirmPassword,
  captchaEnabled,
  captchaUrl,
  registerForm,
  errors,
  refreshCaptcha,
  getEmailCode,
  handleRegister,
  emailCountdown,
  emailCodeSending
} = useRegisterForm()

const registerIcons = {
  user: appIcons.person,
  password: appIcons.password,
  email: appIcons.email,
  captcha: appIcons.captcha,
  eye: appIcons.eye,
  eyeOff: appIcons.eyeOff
}

const registerHighlights = [
  {
    icon: appIcons.tag,
    title: '收藏同步',
    description: '保存你在意的文章、分类和标签入口。'
  },
  {
    icon: appIcons.user,
    title: '个人栏目',
    description: '建立资料、主题偏好和站内展示入口。'
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
  min-height: 44px;
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
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
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

@include sourcelin-down(sm) {
  .captcha-row {
    grid-template-columns: 1fr;
  }

  .captcha-trigger {
    min-height: 54px;
  }
}
</style>
