<template>
  <AuthScene :background-image="authBackground">
    <template #visual>
      <AuthVisualShowcase
        badge="账号找回"
        title="忘记密码"
        subtitle="通过邮箱验证码重置密码。仅用于账号找回，不用于营销。"
        :highlights="highlights"
      />
    </template>

    <AuthPanelFrame
      eyebrow="Recover"
      title="找回密码"
      subtitle="输入邮箱并完成验证后重置登录密码。"
    />

    <form class="recover-form" @submit.prevent="handleRecover">
      <div class="field-group">
        <label class="field-label" for="recover-email">邮箱</label>
        <SInput
          id="recover-email"
          v-model="email"
          class="auth-input"
          variant="filled"
          placeholder="请输入注册邮箱"
          autocomplete="email"
          clearable
        >
          <template #prefix>
            <SIcon :icon="icons.email" :size="16" />
          </template>
        </SInput>
      </div>

      <div class="field-group">
        <label class="field-label" for="recover-code">邮箱验证码</label>
        <div class="code-row">
          <SInput
            id="recover-code"
            v-model="code"
            class="auth-input code-input"
            variant="filled"
            placeholder="输入6位验证码"
            maxlength="6"
          >
            <template #prefix>
              <SIcon :icon="icons.captcha" :size="16" />
            </template>
          </SInput>
          <SButton class="send-code-btn" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
          </SButton>
        </div>
      </div>

      <div class="field-group">
        <label class="field-label" for="recover-password">新密码</label>
        <SInput
          id="recover-password"
          v-model="newPassword"
          class="auth-input"
          variant="filled"
          type="password"
          placeholder="输入新密码（5-20位）"
          autocomplete="new-password"
        />
      </div>

      <SButton class="submit-button" variant="cta" attr-type="submit">重置密码</SButton>

      <div class="recover-footer">
        <RouterLink class="switch-link" to="/login">返回登录</RouterLink>
        <span class="divider">|</span>
        <RouterLink class="switch-link" to="/register">去注册</RouterLink>
      </div>
    </form>
  </AuthScene>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import authBackground from '@/assets/images/backgrounds/home-bg.jpg'
import AuthPanelFrame from '@/modules/auth/components/AuthPanelFrame.vue'
import AuthScene from '@/modules/auth/components/AuthScene.vue'
import AuthVisualShowcase from '@/modules/auth/components/AuthVisualShowcase.vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SInput from '@/shared/components/ui/SInput.vue'
import { useSMessage } from '@/shared/composables/useSMessage'
import { appIcons } from '@/shared/components/ui/icons'

const message = useSMessage()
const email = ref('')
const code = ref('')
const newPassword = ref('')
const countdown = ref(0)

const icons = {
  email: appIcons.email,
  captcha: appIcons.captcha
}

const highlights = [
  {
    icon: appIcons.password,
    title: '账号安全',
    description: '仅支持本人邮箱验证后重置密码。'
  },
  {
    icon: appIcons.captcha,
    title: '最小化收集',
    description: '仅用于身份确认，不做商业用途。'
  }
]

function sendCode() {
  if (!email.value.trim()) {
    message.warning('请先输入邮箱')
    return
  }
  if (countdown.value > 0) {
    return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
  message.success('验证码已发送，请查收邮箱')
}

function handleRecover() {
  if (!email.value.trim() || !code.value.trim() || !newPassword.value.trim()) {
    message.warning('请完整填写找回信息')
    return
  }
  if (newPassword.value.length < 5 || newPassword.value.length > 20) {
    message.warning('密码长度需为 5-20 位')
    return
  }
  message.success('已提交重置请求，请按邮件提示完成修改')
}
</script>

<style scoped lang="scss">
.recover-form {
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

.code-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 128px;
  gap: 10px;
}

.send-code-btn {
  width: 100%;
}

.submit-button {
  width: 100%;
}

.recover-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 0.88rem;
}

.switch-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.divider {
  color: var(--text-placeholder);
}
</style>
