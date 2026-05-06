import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, setExpiresIn, setTokenName, setTokenPrefix, setLoginType } from '@/shared/utils/auth'
import { login as loginApi, logout as logoutApi, getInfo } from '@/modules/auth/api/auth.api'
import type { CurrentUserInfoPayload, LoginTokenPayload } from '@/modules/auth/api/auth.api'
import router from '@/app/router'

interface FrontUserInfo {
  id?: number
  userName?: string
  nickName?: string
  phonenumber?: string
  email?: string
  sex?: string | number
  introduction?: string
  avatar?: string
  avatarFileId?: number | null
  articleCount?: number
  followerCount?: number
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<unknown>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  const isLoggedIn = computed(() => !!token.value)

  function normalizeUserInfo(raw: unknown): FrontUserInfo | null {
    if (typeof raw !== 'object' || raw === null) {
      return null
    }
    const data = raw as CurrentUserInfoPayload
    if (!data.id && !data.nickName && !data.userName) {
      return null
    }
    return {
      id: Number(data.id) || undefined,
      userName: typeof data.userName === 'string' ? data.userName : '',
      nickName: typeof data.nickName === 'string' ? data.nickName : '',
      phonenumber: typeof data.phonenumber === 'string' ? data.phonenumber : '',
      email: typeof data.email === 'string' ? data.email : '',
      sex: typeof data.sex === 'string' || typeof data.sex === 'number' ? data.sex : '',
      introduction: typeof data.introduction === 'string' ? data.introduction : '',
      avatar: typeof data.avatar === 'string' ? data.avatar : '',
      avatarFileId: typeof data.avatarFileId === 'number' ? data.avatarFileId : null,
      articleCount: Number(data.articleCount) || 0,
      followerCount: Number(data.followerCount) || 0
    }
  }

  function resolveLoginToken(payload: LoginTokenPayload): LoginTokenPayload {
    if (!payload || typeof payload.token !== 'string' || !payload.token.trim()) {
      throw new Error('登录响应缺少有效 token')
    }
    return payload
  }

  async function login(userData: { 
    username: string; 
    password: string; 
    loginType?: string;
    captchaCode: string;
    captchaUuid: string;
  }) {
    const res = await loginApi(userData)
    const loginPayload = resolveLoginToken(res)
    token.value = loginPayload.token.trim()
    setToken(loginPayload.token.trim())
    setTokenName(loginPayload.tokenName || 'Authorization')
    setTokenPrefix(loginPayload.tokenPrefix || 'Bearer')
    setLoginType(loginPayload.loginType || 'blog')
    setExpiresIn(String(loginPayload.expiresIn || 0))
    return res
  }

  function saveToken(payload: LoginTokenPayload) {
    token.value = payload.token.trim()
    setToken(payload.token.trim())
    setTokenName(payload.tokenName || 'Authorization')
    setTokenPrefix(payload.tokenPrefix || 'Bearer')
    setLoginType(payload.loginType || 'blog')
    setExpiresIn(String(payload.expiresIn || 0))
  }

  async function getUserInfoAction() {
    if (!token.value) return null
    try {
      const res = await getInfo() as CurrentUserInfoPayload
      userInfo.value = normalizeUserInfo(res)
      roles.value = Array.isArray(res.roles) ? res.roles : []
      permissions.value = Array.isArray(res.permissions) ? res.permissions : []
      return userInfo.value
    } catch (error) {
      logout()
      return null
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    removeToken()
    router.push('/login')
  }

  async function resetToken() {
    try {
      await logoutApi()
    } catch {
      // ignore
    }
    logout()
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    isLoggedIn,
    login,
    saveToken,
    getUserInfoAction,
    logout,
    resetToken
  }
})
