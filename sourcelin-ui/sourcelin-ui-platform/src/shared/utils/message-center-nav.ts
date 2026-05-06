/**
 * 消息通知已迁至用户下拉菜单，需从顶栏 / 移动抽屉菜单数据源中排除。
 */
export function isMessageCenterNavEntry(path: string, label?: string): boolean {
  const normalized = (path || '').replace(/\/$/, '') || '/'
  if (normalized === '/messages' || normalized === '/notice') {
    return true
  }
  const t = label?.trim()
  return t === '消息中心' || t === '消息通知'
}
