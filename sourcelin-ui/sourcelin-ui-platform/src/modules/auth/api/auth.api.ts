import type { AxiosRequestConfig } from 'axios'
import { requestData, requestRaw } from '@/shared/api/http'
import { encryptPassword } from '@/shared/utils/jsencrypt'
import { extractAuthResponseData } from './normalize-auth-response'

export interface CaptchaPayload {
  captchaEnabled: boolean
  img: string
  uuid: string
}

export interface LoginTokenPayload {
  token: string
  tokenName: string
  tokenPrefix: string
  expiresIn: number
  loginType: string
}

export interface CurrentUserInfoPayload {
  roles?: string[]
  permissions?: string[]
  [key: string]: unknown
}

function requestAuthData<T>(config: AxiosRequestConfig) {
  return requestRaw<unknown>({
    ...config,
    skipResponseWrapCheck: true
  }).then((payload) => extractAuthResponseData<T>(payload))
}

export function getCaptcha() {
  return requestAuthData<CaptchaPayload>({
    url: '/auth/captcha',
    headers: { isToken: false },
    method: 'get',
    timeout: 20000
  })
}

export function login(data: {
  username: string
  password: string
  loginType?: string
  captchaCode: string
  captchaUuid: string
}) {
  return requestAuthData<LoginTokenPayload>({
    url: '/auth/login',
    headers: { isToken: false },
    method: 'post',
    data: {
      username: data.username,
      password: encryptPassword(data.password),
      loginType: data.loginType || 'blog',
      captchaCode: data.captchaCode,
      captchaUuid: data.captchaUuid
    }
  })
}

export function register(data: {
  username: string
  password: string
  email: string
  emailCode: string
  registerRequestId: string
  loginType?: string
  captchaCode: string
  captchaUuid: string
}) {
  return requestAuthData<LoginTokenPayload>({
    url: '/auth/register',
    headers: { isToken: false },
    method: 'post',
    timeout: 30000,
    data: {
      username: data.username,
      password: encryptPassword(data.password),
      email: data.email,
      emailCode: data.emailCode,
      registerRequestId: data.registerRequestId,
      loginType: data.loginType || 'blog',
      captchaCode: data.captchaCode,
      captchaUuid: data.captchaUuid
    }
  })
}

export function sendEmailCode(data: {
  email: string
  captchaCode: string
  captchaUuid: string
}) {
  return requestAuthData<void>({
    url: '/auth/email/code',
    headers: { isToken: false },
    method: 'post',
    data,
    timeout: 30000
  })
}

export function emailLogin(data: {
  email: string
  emailCode?: string
  loginType: string
  captchaCode: string
  captchaUuid: string
}) {
  return requestAuthData<LoginTokenPayload>({
    url: '/auth/email/login',
    headers: { isToken: false },
    method: 'post',
    data: {
      email: data.email,
      emailCode: data.emailCode,
      loginType: data.loginType,
      captchaCode: data.captchaCode,
      captchaUuid: data.captchaUuid
    }
  })
}

export function getInfo() {
  return requestData<CurrentUserInfoPayload>({
    url: '/front/user/info',
    method: 'get'
  })
}

export function logout() {
  return requestAuthData<void>({
    url: '/auth/logout',
    method: 'delete'
  })
}
