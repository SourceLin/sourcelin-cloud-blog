import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'
import { getToken, getTokenName, getTokenPrefix } from '@/shared/utils/auth'
import { sDiscreteMessage } from '@/shared/composables/useSMessage'
import { ResultCode } from '@/shared/api/result-code'
import type { ApiResponse } from '@/shared/types/api'
import { resolveBusinessErrorMessage, resolveRequestErrorMessage } from '@/shared/api/error-message'

interface ExtendedRequestConfig extends AxiosRequestConfig {
  skipResponseWrapCheck?: boolean
  skipErrorMessage?: boolean
}

const SESSION_EXPIRED_MESSAGE = '登录状态已过期，请重新登录'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '',
  timeout: 10000
})

export const isRelogin = { show: false }

service.interceptors.request.use(
  (config) => {
    const isToken = (config.headers as Record<string, unknown>)?.isToken === false
    const token = getToken()?.trim()
    const hasUsableToken = Boolean(token) && token !== 'undefined' && token !== 'null'

    if (hasUsableToken && !isToken) {
      const tokenName = getTokenName() || 'Authorization'
      const tokenPrefix = getTokenPrefix() || 'Bearer'
      config.headers[tokenName] = `${tokenPrefix} ${token}`
    }

    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data
    }
    const skipResponseWrapCheck = (response.config as ExtendedRequestConfig).skipResponseWrapCheck === true
    const skipErrorMessage = (response.config as ExtendedRequestConfig).skipErrorMessage === true
    const payload = response.data as Partial<ApiResponse<unknown>> | undefined
    if (!payload || typeof payload.code !== 'number') {
      if (skipResponseWrapCheck) {
        return response.data
      }
      const protocolErrorMessage = '响应协议错误：缺少 code 字段'
      if (!skipErrorMessage) {
        sDiscreteMessage.error(protocolErrorMessage)
      }
      return Promise.reject(new Error(protocolErrorMessage))
    }
    const code = payload.code

    if (code === 401 || code === ResultCode.UNAUTHORIZED) {
      if (!isRelogin.show) {
        isRelogin.show = true
        if (!skipErrorMessage) {
          sDiscreteMessage.error(SESSION_EXPIRED_MESSAGE)
        }
      }

      return Promise.reject(new Error('无效会话，请重新登录'))
    }

    if (code !== ResultCode.SUCCESS) {
      const errorMessage = resolveBusinessErrorMessage(payload)
      if (!skipErrorMessage) {
        sDiscreteMessage.error(errorMessage)
      }
      return Promise.reject(new Error(errorMessage))
    }

    return payload
  },
  (error) => {
    console.error('Response error:', error)
    const skipErrorMessage = (error as { config?: ExtendedRequestConfig }).config?.skipErrorMessage === true
    const messageText = resolveRequestErrorMessage(error)
    if (!skipErrorMessage) {
      sDiscreteMessage.error(messageText)
    }
    return Promise.reject(error)
  }
)

export function requestData<T>(config: ExtendedRequestConfig) {
  return service.request<ApiResponse<T>, ApiResponse<T>>(config).then((response) => response.data)
}

export function requestRaw<T>(config: ExtendedRequestConfig) {
  return service.request<T, T>(config)
}

export default service
