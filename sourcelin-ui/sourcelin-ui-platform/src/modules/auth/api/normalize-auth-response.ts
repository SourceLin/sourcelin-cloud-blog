import { resolveBusinessErrorMessage } from '../../../shared/api/error-message.ts'

interface ApiEnvelope<T> {
  code: number
  data?: T
  message?: string
  msg?: string
}

const SUCCESS_CODE = 200

function isApiEnvelope<T>(payload: unknown): payload is ApiEnvelope<T> {
  return typeof payload === 'object' && payload !== null && typeof (payload as { code?: unknown }).code === 'number'
}

export function extractAuthResponseData<T>(payload: unknown): T {
  if (!isApiEnvelope<T>(payload)) {
    return payload as T
  }

  if (payload.code === SUCCESS_CODE || payload.code === 0) {
    return (payload.data as T) ?? (undefined as T)
  }

  throw new Error(resolveBusinessErrorMessage(payload))
}
