const DEFAULT_REQUEST_ERROR_MESSAGE = '请求失败'

function extractMessageText(payload: unknown) {
  if (typeof payload !== 'object' || payload === null) {
    return ''
  }

  if ('message' in payload) {
    const text = (payload as { message?: unknown }).message
    if (typeof text === 'string' && text.trim()) {
      return text
    }
  }

  if ('msg' in payload) {
    const text = (payload as { msg?: unknown }).msg
    if (typeof text === 'string' && text.trim()) {
      return text
    }
  }

  return ''
}

export function resolveBusinessErrorMessage(payload: unknown, fallback = DEFAULT_REQUEST_ERROR_MESSAGE) {
  return extractMessageText(payload) || fallback
}

export function resolveRequestErrorMessage(error: unknown, fallback = DEFAULT_REQUEST_ERROR_MESSAGE) {
  if (typeof error !== 'object' || error === null) {
    return fallback
  }

  const responseData = (error as { response?: { data?: unknown } }).response?.data
  const businessMessage = extractMessageText(responseData)
  if (businessMessage) {
    return businessMessage
  }

  const message = (error as { message?: unknown }).message
  if (message === 'Network Error') {
    return '服务连接失败，请稍后再试'
  }

  if (typeof message === 'string' && message.includes('timeout')) {
    return '请求超时，请稍后重试'
  }

  if (typeof message === 'string' && message.trim()) {
    return message
  }

  return fallback
}
