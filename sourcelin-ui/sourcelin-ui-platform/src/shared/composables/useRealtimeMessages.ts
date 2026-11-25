import { onBeforeUnmount, watch } from 'vue'
import { requestData } from '@/shared/api/http'
import { useUserStore } from '@/stores/user'
import { useMessageInboxBadgeStore } from '@/stores/message-inbox-badge.store'
import manager from '@/shared/utils/SseClientManager'
export function useRealtimeMessages(): void {
 const user = useUserStore()
 const badge = useMessageInboxBadgeStore()
 const refresh = () => badge.invalidateInbox()
 const receive = (payload: { content: string }) => {
  try {
   const event: unknown = JSON.parse(payload.content)
   if (event && typeof event === 'object' && 'type' in event && event.type === 'unread_changed') refresh()
  } catch { /* 忽略非业务事件。 */ }
 }
 manager.on('message', receive)
 manager.on('reconnected', refresh)
 watch(() => user.token, (token, _, onCleanup) => {
  manager.disconnect()
  badge.setTotalCount(0)
  if (!token) return
  let current = true
  onCleanup(() => { current = false })
  void badge.refresh()
  void requestData<boolean>({ url: '/front/messages/realtime-status', method: 'get', headers: { isToken: false } })
    .then(enabled => { if (enabled && current && user.token === token) void manager.connect() }).catch(() => undefined)
 }, { immediate: true })
 onBeforeUnmount(() => {
  manager.off('message', receive)
  manager.off('reconnected', refresh)
  manager.disconnect()
 })
}
