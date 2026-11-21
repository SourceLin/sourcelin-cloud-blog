import { http, openSseStream } from '@/utils/request';
import { resolveUrl } from '@/config/env';
import { SseStreamParser } from './SseStreamParser';
export interface SseMessagePayload { content: string; metadata: string }
type Callback = (payload: SseMessagePayload) => void;
class SseClientManager {
 private active = false;
 private generation = 0;
 private connecting = false;
 private retries = 0;
 private task: UniApp.RequestTask | null = null;
 private source: EventSource | null = null;
 private retryTimer: ReturnType<typeof setTimeout> | undefined;
 private heartbeatTimer: ReturnType<typeof setTimeout> | undefined;
 private callbacks = new Map<string, Set<Callback>>();
 on(event: string, callback: Callback): void { if (!this.callbacks.has(event)) this.callbacks.set(event, new Set()); this.callbacks.get(event)?.add(callback); }
 off(event: string, callback: Callback): void { this.callbacks.get(event)?.delete(callback); }
 async connect(): Promise<void> {
  this.active = true;
  if (this.connecting || this.task || this.source) return;
  this.connecting = true;
  const generation = this.generation;
  try {
   const ticket = await http.get<string>('/auth/apply-ticket', { loginType: 'mini' });
   if (!this.active || generation !== this.generation) return;
   const url = `${resolveUrl('/message/stream')}?ticket=${encodeURIComponent(ticket)}`;
   // #ifdef H5
   this.source = new EventSource(url);
   this.source.onopen = () => { if (generation === this.generation) this.handleConnected(); };
   this.source.addEventListener('heartbeat', () => { if (generation === this.generation) this.touch(); });
   this.source.addEventListener('message', event => { if (generation === this.generation) { this.touch(); this.receive(event.data); } });
   this.source.onerror = () => { if (generation === this.generation) this.retry(); };
   // #endif
   // #ifndef H5
   const parser = new SseStreamParser(event => { if (event.event === 'message') this.receive(event.data); });
   this.task = openSseStream(url, data => {
    if (generation !== this.generation) return;
    this.handleConnected();
    parser.push(data);
   }, () => { if (generation === this.generation) this.retry(); });
   this.touch();
   // #endif
  } catch { if (generation === this.generation) this.retry(); }
  finally { if (generation === this.generation) this.connecting = false; }
 }
 disconnect(): void {
  this.active = false; this.generation++; this.connecting = false; this.retries = 0;
  clearTimeout(this.retryTimer); this.retryTimer = undefined; this.close();
 }
 private close(): void { clearTimeout(this.heartbeatTimer); this.task?.abort(); this.task = null; this.source?.close(); this.source = null; }
 private touch(): void { clearTimeout(this.heartbeatTimer); this.heartbeatTimer = setTimeout(() => this.retry(), 65000); }
 private handleConnected(): void {
  const reconnected = this.retries > 0;
  this.retries = 0;
  this.touch();
  if (reconnected) this.callbacks.get('reconnected')?.forEach(callback => callback({ content: '', metadata: '' }));
 }
 private retry(): void {
  this.generation++; this.close(); this.connecting = false;
  if (!this.active || this.retryTimer || this.retries >= 10) return;
  const delay = Math.min(1000 * 2 ** this.retries++, 30000);
  this.retryTimer = setTimeout(() => { this.retryTimer = undefined; if (this.active) void this.connect(); }, delay);
 }
 private receive(text: string): void {
  try {
   const parsed: unknown = JSON.parse(text);
   if (!parsed || typeof parsed !== 'object') return;
   const data = parsed as Record<string, unknown>;
   if (typeof data.content !== 'string') return;
   const payload = { content: data.content, metadata: typeof data.metadata === 'string' ? data.metadata : '' };
   this.callbacks.get('message')?.forEach(callback => callback(payload));
  } catch { /* 忽略无效消息。 */ }
 }
}
export default new SseClientManager();
