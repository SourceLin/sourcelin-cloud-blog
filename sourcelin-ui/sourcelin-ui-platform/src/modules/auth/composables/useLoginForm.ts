import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCaptcha, login as loginApi, sendEmailCode, emailLogin } from '@/modules/auth/api/auth.api'
import type { LoginTokenPayload } from '@/modules/auth/api/auth.api'
import { useUserStore } from '@/stores/user'
import { sDiscreteMessage } from '@/shared/composables/useSMessage'
import { resolveRequestErrorMessage } from '@/shared/api/error-message'

const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
const CAPTCHA_DISABLED_VALUE = '__CAPTCHA_DISABLED__'

export function useLoginForm() {
  const router = useRouter()
  const route = useRoute()
  const userStore = useUserStore()

  const loading = ref(false)
  const showPassword = ref(false)
  const captchaUrl = ref('')
  const captchaUuid = ref('')
  const captchaEnabled = ref(true)
  const redirect = ref<string | undefined>(undefined)

  const loginMethod = ref<'account' | 'email'>('account')
  const emailCodeSending = ref(false)
  const emailCountdown = ref(0)

  const loginForm = reactive({
    username: '',
    password: '',
    email: '',
    captchaCode: '',
    emailCode: '',
    rememberMe: false
  })

  const errors = reactive({
    username: '',
    password: '',
    email: '',
    captchaCode: '',
    emailCode: ''
  })

  watch(
    () => route.query,
    (query) => {
      redirect.value = query.redirect as string | undefined
    },
    { immediate: true }
  )

  watch(() => loginForm.username, () => { if (errors.username) errors.username = '' })
  watch(() => loginForm.password, () => { if (errors.password) errors.password = '' })
  watch(() => loginForm.email, () => { if (errors.email) errors.email = '' })
  watch(() => loginForm.captchaCode, () => { if (errors.captchaCode) errors.captchaCode = '' })
  watch(() => loginForm.emailCode, () => { if (errors.emailCode) errors.emailCode = '' })

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
      } else {
        captchaUrl.value = ''
        captchaUuid.value = CAPTCHA_DISABLED_VALUE
        loginForm.captchaCode = ''
      }
    } catch (error) {
      console.error('获取验证码失败', error)
    }
  }

  const resolveCaptchaPayload = () => {
    if (captchaEnabled.value) {
      return {
        captchaCode: loginForm.captchaCode,
        captchaUuid: captchaUuid.value
      }
    }
    return {
      captchaCode: CAPTCHA_DISABLED_VALUE,
      captchaUuid: CAPTCHA_DISABLED_VALUE
    }
  }

  const getEmailCode = async () => {
    if (emailCountdown.value > 0 || emailCodeSending.value) return

    resetErrors()

    if (!loginForm.email.trim()) {
      errors.email = '请先填写邮箱'
      sDiscreteMessage.warning('请先填写邮箱')
      return
    }
    if (!EMAIL_REGEX.test(loginForm.email)) {
      errors.email = '邮箱格式不正确'
      sDiscreteMessage.error('邮箱格式不正确')
      return
    }
    if (captchaEnabled.value && !loginForm.captchaCode) {
      errors.captchaCode = '请先输入图形验证码'
      sDiscreteMessage.warning('请先输入图形验证码')
      return
    }

    emailCodeSending.value = true
    try {
      const captchaPayload = resolveCaptchaPayload()
      await sendEmailCode({
        email: loginForm.email,
        captchaCode: captchaPayload.captchaCode,
        captchaUuid: captchaPayload.captchaUuid
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
      loginForm.captchaCode = ''
    } finally {
      emailCodeSending.value = false
    }
  }

  const clearRememberMeCookie = () => {
    const baseConfig = 'path=/;max-age=0'
    document.cookie = `username=;${baseConfig}`
    document.cookie = `password=;${baseConfig}`
    document.cookie = `rememberMe=;${baseConfig}`
  }

  const getCookie = () => {
    try {
      const cookieMap: Record<string, string> = {}
      document.cookie.split(';').forEach((cookie) => {
        const [key, value] = cookie.trim().split('=')
        if (key && value) cookieMap[key] = decodeURIComponent(value)
      })
      if (cookieMap.rememberMe === 'true') {
        loginForm.username = cookieMap.username || ''
        loginForm.rememberMe = true
      }
      if (cookieMap.password) {
        document.cookie = 'password=;path=/;max-age=0'
      }
    } catch {
      // Ignore malformed cookie state.
    }
  }

  const setCookie = () => {
    if (!loginForm.rememberMe) {
      clearRememberMeCookie()
      return
    }

    const baseConfig = `path=/;max-age=${7 * 24 * 60 * 60}`
    document.cookie = `username=${encodeURIComponent(loginForm.username)};${baseConfig}`
    document.cookie = `rememberMe=true;${baseConfig}`
    document.cookie = 'password=;path=/;max-age=0'
  }

  const validateAccountLogin = () => {
    resetErrors()
    if (!loginForm.username.trim()) {
      errors.username = '请输入用户名或邮箱'
      return false
    }
    if (!loginForm.password) {
      errors.password = '请输入密码'
      return false
    }
    if (loginForm.password.length < 5 || loginForm.password.length > 20) {
      errors.password = '密码长度应在 5-20 位之间'
      return false
    }
    if (captchaEnabled.value && !loginForm.captchaCode) {
      errors.captchaCode = '请输入图形验证码'
      return false
    }
    return true
  }

  const validateEmailLogin = () => {
    resetErrors()
    if (!loginForm.email.trim()) {
      errors.email = '请输入邮箱'
      return false
    }
    if (!EMAIL_REGEX.test(loginForm.email)) {
      errors.email = '邮箱格式不正确'
      return false
    }
    if (!loginForm.emailCode) {
      errors.emailCode = '请输入邮箱验证码'
      return false
    }
    if (loginForm.emailCode.length !== 6) {
      errors.emailCode = '验证码为6位数字'
      return false
    }
    if (captchaEnabled.value && !loginForm.captchaCode) {
      errors.captchaCode = '请输入图形验证码'
      return false
    }
    return true
  }

  const handleSubmit = async () => {
    if (loginMethod.value === 'account') {
      if (!validateAccountLogin()) return
    } else {
      if (!validateEmailLogin()) return
    }

    try {
      loading.value = true
      const captchaPayload = resolveCaptchaPayload()

      if (loginMethod.value === 'account') {
        await userStore.login({
          username: loginForm.username,
          password: loginForm.password,
          loginType: 'blog',
          captchaCode: captchaPayload.captchaCode,
          captchaUuid: captchaPayload.captchaUuid
        })
        setCookie()
      } else {
        const tokenPayload = await emailLogin({
          email: loginForm.email,
          emailCode: loginForm.emailCode,
          loginType: 'code',
          captchaCode: captchaPayload.captchaCode,
          captchaUuid: captchaPayload.captchaUuid
        })
        if (!tokenPayload?.token) {
          throw new Error('邮箱登录响应缺少 token')
        }
        userStore.saveToken(tokenPayload)
      }

      void router.push(redirect.value || '/')
    } catch (error) {
      console.error(error)
      await refreshCaptcha()
    } finally {
      loading.value = false
    }
  }

  const switchLoginMethod = (method: 'account' | 'email') => {
    loginMethod.value = method
    resetErrors()
    loginForm.captchaCode = ''
    void refreshCaptcha()
  }

  const loginMessage = computed(() => {
    const msg = route.query.msg
    return typeof msg === 'string' ? msg : ''
  })

  onMounted(() => {
    void refreshCaptcha()
    getCookie()
  })

  return {
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
  }
}
