import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, register as registerApi, sendEmailCode } from '@/modules/auth/api/auth.api'
import type { LoginTokenPayload } from '@/modules/auth/api/auth.api'
import { useUserStore } from '@/stores/user'
import { sDiscreteMessage } from '@/shared/composables/useSMessage'
import { resolveRequestErrorMessage } from '@/shared/api/error-message'

const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

function createRegisterRequestId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `reg_${Date.now()}_${Math.random().toString(16).slice(2)}`
}

export function useRegisterForm() {
  const router = useRouter()
  const userStore = useUserStore()
  const loading = ref(false)
  const showPassword = ref(false)
  const showConfirmPassword = ref(false)
  const captchaUrl = ref('')
  const captchaUuid = ref('')
  const captchaEnabled = ref(true)
  const emailCodeSending = ref(false)
  const emailCountdown = ref(0)
  const registerRequestId = ref(createRegisterRequestId())

  const registerForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    captchaCode: '',
    emailCode: ''
  })

  const errors = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    captchaCode: '',
    emailCode: ''
  })

  watch(() => registerForm.username, () => { if (errors.username) errors.username = '' })
  watch(() => registerForm.password, () => { if (errors.password) errors.password = '' })
  watch(() => registerForm.confirmPassword, () => { if (errors.confirmPassword) errors.confirmPassword = '' })
  watch(() => registerForm.email, () => { if (errors.email) errors.email = '' })
  watch(() => registerForm.captchaCode, () => { if (errors.captchaCode) errors.captchaCode = '' })
  watch(() => registerForm.emailCode, () => { if (errors.emailCode) errors.emailCode = '' })

  const resetErrors = () => {
    Object.keys(errors).forEach((key) => {
      errors[key as keyof typeof errors] = ''
    })
  }

  const refreshCaptcha = async () => {
    try {
      const payload = await getCaptcha()
      captchaEnabled.value = payload.captchaEnabled ?? true
      if (captchaEnabled.value) {
        captchaUrl.value = `data:image/gif;base64,${payload.img || ''}`
        captchaUuid.value = payload.uuid || ''
      }
    } catch (error) {
      console.error('获取验证码失败', error)
    }
  }

  const getEmailCode = async () => {
    if (emailCountdown.value > 0 || emailCodeSending.value) return

    if (!registerForm.email) {
      errors.email = '请先填写邮箱'
      sDiscreteMessage.warning('请先填写邮箱')
      return
    }
    if (!EMAIL_REGEX.test(registerForm.email)) {
      errors.email = '邮箱格式不正确'
      sDiscreteMessage.error('邮箱格式不正确')
      return
    }
    if (!registerForm.captchaCode) {
      errors.captchaCode = '请先输入图形验证码'
      sDiscreteMessage.warning('请先输入图形验证码')
      return
    }

    emailCodeSending.value = true
    try {
      await sendEmailCode({
        email: registerForm.email,
        captchaCode: registerForm.captchaCode,
        captchaUuid: captchaUuid.value
      })
      sDiscreteMessage.success('验证码已发送到您的邮箱')
      emailCountdown.value = 60
      const timer = setInterval(() => {
        emailCountdown.value--
        if (emailCountdown.value <= 0) clearInterval(timer)
      }, 1000)
    } catch (error: unknown) {
      sDiscreteMessage.error(resolveRequestErrorMessage(error, '发送失败'))
      await refreshCaptcha()
      registerForm.captchaCode = ''
    } finally {
      emailCodeSending.value = false
    }
  }

  const validate = () => {
    resetErrors()

    if (!registerForm.username.trim()) {
      errors.username = '请输入用户名'
      return false
    }
    if (registerForm.username.length < 2 || registerForm.username.length > 20) {
      errors.username = '用户名长度应在 2-20 位之间'
      return false
    }
    if (!registerForm.password) {
      errors.password = '请输入密码'
      return false
    }
    if (registerForm.password.length < 5 || registerForm.password.length > 20) {
      errors.password = '密码长度应在 5-20 位之间'
      return false
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      errors.confirmPassword = '两次密码输入不一致'
      return false
    }
    if (!registerForm.email) {
      errors.email = '请输入邮箱'
      return false
    }
    if (!EMAIL_REGEX.test(registerForm.email)) {
      errors.email = '请输入有效的邮箱地址'
      return false
    }
    if (captchaEnabled.value && !registerForm.captchaCode) {
      errors.captchaCode = '请输入图形验证码'
      return false
    }
    if (!registerForm.emailCode) {
      errors.emailCode = '请输入邮箱验证码'
      return false
    }
    if (registerForm.emailCode.length !== 6) {
      errors.emailCode = '验证码为6位数字'
      return false
    }
    return true
  }

  const handleRegister = async () => {
    if (!validate()) return

    try {
      loading.value = true
      const tokenPayload = await registerApi({
        username: registerForm.username,
        password: registerForm.password,
        email: registerForm.email,
        emailCode: registerForm.emailCode,
        registerRequestId: registerRequestId.value,
        captchaCode: registerForm.captchaCode,
        captchaUuid: captchaUuid.value
      })

      if (tokenPayload?.token) {
        userStore.saveToken(tokenPayload)
        await userStore.getUserInfoAction()
        void router.push('/')
      } else {
        void router.push('/login?msg=注册成功，请登录')
      }
      registerRequestId.value = createRegisterRequestId()
    } catch (error: unknown) {
      sDiscreteMessage.error(resolveRequestErrorMessage(error, '注册失败'))
      await refreshCaptcha()
      registerRequestId.value = createRegisterRequestId()
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    void refreshCaptcha()
  })

  return {
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
  }
}
