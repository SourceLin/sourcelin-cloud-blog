import type { Component } from 'vue'
import { h } from 'vue'
import {
  HappyOutline,
  InformationCircleOutline,
  SadOutline,
  WarningOutline
} from '@vicons/ionicons5'
import { NIcon, useMessage } from 'naive-ui'

const DEFAULT_MESSAGE_DURATION = 3200

type MessageType = 'success' | 'error' | 'warning' | 'info'

type MessageApi = ReturnType<typeof useMessage>
type MessageOptions = Exclude<Parameters<MessageApi['success']>[1], undefined>
type MessageContent = Parameters<MessageApi['success']>[0]
type MessageIconRender = NonNullable<MessageOptions['icon']>

const MESSAGE_ICON_MAP: Record<MessageType, Component> = {
  success: HappyOutline,
  error: SadOutline,
  warning: WarningOutline,
  info: InformationCircleOutline
}

export function createSMessageIcon(icon: Component): MessageIconRender {
  return () =>
    h(
      NIcon,
      {
        size: 18
      },
      {
        default: () => h(icon)
      }
    )
}

export interface SMessageApi extends MessageApi {
  show: (type: MessageType, content: MessageContent, options?: MessageOptions) => ReturnType<MessageApi['success']>
  success: (content: MessageContent, options?: MessageOptions) => ReturnType<MessageApi['success']>
  error: (content: MessageContent, options?: MessageOptions) => ReturnType<MessageApi['error']>
  warning: (content: MessageContent, options?: MessageOptions) => ReturnType<MessageApi['warning']>
  info: (content: MessageContent, options?: MessageOptions) => ReturnType<MessageApi['info']>
}

function withDefaultOptions(type: MessageType, options?: MessageOptions): MessageOptions {
  return {
    ...options,
    icon: options?.icon ?? createSMessageIcon(MESSAGE_ICON_MAP[type]),
    duration: options?.duration ?? DEFAULT_MESSAGE_DURATION
  }
}

function createSMessageApi(message: MessageApi): SMessageApi {
  const show = (type: MessageType, content: MessageContent, options?: MessageOptions) =>
    message[type](content, withDefaultOptions(type, options))

  return {
    ...message,
    show,
    success: (content: MessageContent, options?: MessageOptions) => show('success', content, options),
    error: (content: MessageContent, options?: MessageOptions) => show('error', content, options),
    warning: (content: MessageContent, options?: MessageOptions) => show('warning', content, options),
    info: (content: MessageContent, options?: MessageOptions) => show('info', content, options)
  }
}

/** 由 `SMessage.vue` 内 MessageBridge 在 NMessageProvider 树内调用，注入与 App 主题一致的 message 实例。 */
let _wrappedMessage: SMessageApi | null = null

export function _captureMessageApi(api: MessageApi): void {
  _wrappedMessage = createSMessageApi(api)
}

function noopMessageMethod() {
  return undefined
}

/**
 * Use in Vue setup context（须位于 SMessage 子树内）。
 */
export const useSMessage = () => createSMessageApi(useMessage())

/**
 * 非 setup 场景（如 axios 拦截器、异步回调）：使用与根应用 NConfigProvider 同一套主题的离散能力。
 * 依赖 SMessage 已挂载并完成 _captureMessageApi。
 */
export const sDiscreteMessage: SMessageApi = new Proxy({} as SMessageApi, {
  get(_, prop: string | symbol) {
    if (!_wrappedMessage) {
      console.warn('[SMessage] Discrete API called before SMessage mounted')
      return noopMessageMethod
    }
    return Reflect.get(_wrappedMessage, prop, _wrappedMessage)
  }
})
