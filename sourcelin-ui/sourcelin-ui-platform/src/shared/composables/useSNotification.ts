import type { Component } from 'vue'
import { h } from 'vue'
import { MailOpenOutline, MailOutline, MailUnreadOutline } from '@vicons/ionicons5'
import { NIcon, useNotification } from 'naive-ui'

const DEFAULT_NOTIFICATION_DURATION = 4200

type NotificationType = 'success' | 'error' | 'warning' | 'info'
export type NotificationMailState = 'unread' | 'read' | 'default'

type NotificationApi = ReturnType<typeof useNotification>
type NotificationOptions = Exclude<Parameters<NotificationApi['success']>[0], undefined>

const NOTIFICATION_MAIL_ICON_MAP: Record<NotificationMailState, Component> = {
  unread: MailUnreadOutline,
  read: MailOpenOutline,
  default: MailOutline
}

/** 与弹窗 `mail()` 语义一致，供列表等场景复用同一套邮件图标 */
export function getMailNotificationStateFromIsRead(isRead?: number | null): NotificationMailState {
  if (isRead === 1) return 'read'
  if (isRead === 0) return 'unread'
  return 'default'
}

export function getMailNotificationIconComponent(isRead?: number | null): Component {
  return NOTIFICATION_MAIL_ICON_MAP[getMailNotificationStateFromIsRead(isRead)]
}

function createMailTitle(icon: Component, title: NotificationOptions['title']) {
  const titleNode = typeof title === 'function' ? title() : (title ?? '消息提醒')

  return () =>
    h(
      'span',
      {
        style: 'display:inline-flex;align-items:center;gap:8px;'
      },
      [
        h(
          NIcon,
          {
            size: 18
          },
          {
            default: () => h(icon)
          }
        ),
        titleNode
      ]
    )
}

export interface SNotificationApi extends NotificationApi {
  show: (type: NotificationType, options: NotificationOptions) => ReturnType<NotificationApi['success']>
  success: (options: NotificationOptions) => ReturnType<NotificationApi['success']>
  error: (options: NotificationOptions) => ReturnType<NotificationApi['error']>
  warning: (options: NotificationOptions) => ReturnType<NotificationApi['warning']>
  info: (options: NotificationOptions) => ReturnType<NotificationApi['info']>
  mail: (state: NotificationMailState, options: NotificationOptions) => ReturnType<NotificationApi['info']>
  unread: (options: NotificationOptions) => ReturnType<NotificationApi['info']>
  read: (options: NotificationOptions) => ReturnType<NotificationApi['info']>
  defaultMail: (options: NotificationOptions) => ReturnType<NotificationApi['info']>
}

function withDefaultDuration(options: NotificationOptions): NotificationOptions {
  return {
    ...options,
    duration: options.duration ?? DEFAULT_NOTIFICATION_DURATION
  }
}

function createSNotificationApi(notification: NotificationApi): SNotificationApi {
  const show = (type: NotificationType, options: NotificationOptions) =>
    notification[type](withDefaultDuration(options))
  const mail = (state: NotificationMailState, options: NotificationOptions) =>
    show('info', {
      ...options,
      title: createMailTitle(NOTIFICATION_MAIL_ICON_MAP[state], options.title)
    })

  return {
    ...notification,
    show,
    success: (options: NotificationOptions) => show('success', options),
    error: (options: NotificationOptions) => show('error', options),
    warning: (options: NotificationOptions) => show('warning', options),
    info: (options: NotificationOptions) => show('info', options),
    mail,
    unread: (options: NotificationOptions) => mail('unread', options),
    read: (options: NotificationOptions) => mail('read', options),
    defaultMail: (options: NotificationOptions) => mail('default', options)
  }
}

/** 由 `SNotification.vue` 内 NotificationBridge 在 NNotificationProvider 树内调用。 */
let _wrappedNotification: SNotificationApi | null = null

export function _captureNotificationApi(api: NotificationApi): void {
  _wrappedNotification = createSNotificationApi(api)
}

function noopNotificationMethod() {
  return undefined
}

/**
 * Use in Vue setup context（须位于 SNotification 子树内）。
 */
export const useSNotification = () => createSNotificationApi(useNotification())

/**
 * 非 setup 场景：与根应用主题一致的 notification 能力（依赖 SNotification 已挂载并完成 _captureNotificationApi）。
 */
export const sDiscreteNotification: SNotificationApi = new Proxy({} as SNotificationApi, {
  get(_, prop: string | symbol) {
    if (!_wrappedNotification) {
      console.warn('[SNotification] Discrete API called before SNotification mounted')
      return noopNotificationMethod
    }
    return Reflect.get(_wrappedNotification, prop, _wrappedNotification)
  }
})
