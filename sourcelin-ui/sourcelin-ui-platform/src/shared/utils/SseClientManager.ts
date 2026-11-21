import { requestData } from '@/shared/api/http'
import { SseConnection, type SseSource } from './SseConnection'
export type { SseMessagePayload, SseCallback } from './SseConnection'
const baseApi = import.meta.env.VITE_APP_BASE_API || ''
const manager = new SseConnection({
 fetchTicket: () => requestData<string>({ url: '/auth/apply-ticket', method: 'get', params: { loginType: 'blog' } }),
 createSource: ticket => new EventSource(`${baseApi}/message/stream?ticket=${encodeURIComponent(ticket)}`) as unknown as SseSource
})
export default manager
