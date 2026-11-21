export interface SseMessagePayload {
  content: string;
  metadata: string;
}
export type SseCallback = (payload: SseMessagePayload) => void;
export interface SseSource {
  onopen: (() => void) | null;
  onerror: (() => void) | null;
  addEventListener(type: string, handler: (event: { data: string }) => void): void;
  close(): void;
}
interface Options {
  fetchTicket: () => Promise<string>;
  createSource: (ticket: string) => SseSource;
  retryDelay?: number;
}
/** 退出、切换账号和重连均使旧的异步握手失效。 */
export class SseConnection {
  private source: SseSource | null = null;
  private active = false;
  private connecting = false;
  private generation = 0;
  private retries = 0;
  private reconnectTimer: ReturnType<typeof setTimeout> | undefined;
  private heartbeatTimer: ReturnType<typeof setTimeout> | undefined;
  private callbacks = new Map<string, Set<SseCallback>>();
  private options: Options;
  constructor(options: Options) {
    this.options = options;
  }
  on(event: string, callback: SseCallback): void {
    if (!this.callbacks.has(event)) this.callbacks.set(event, new Set());
    this.callbacks.get(event)?.add(callback);
  }
  off(event: string, callback: SseCallback): void {
    this.callbacks.get(event)?.delete(callback);
  }
  async connect(): Promise<void> {
    this.active = true;
    if (this.source || this.connecting) return;
    this.connecting = true;
    const generation = this.generation;
    try {
      const ticket = await this.options.fetchTicket();
      if (!this.active || generation !== this.generation) return;
      const source = this.options.createSource(ticket);
      this.source = source;
      source.onopen = () => {
        if (generation !== this.generation) return;
        this.connecting = false;
        const reconnected = this.retries > 0;
        this.retries = 0;
        this.touch();
        if (reconnected) {
          this.callbacks
            .get("reconnected")
            ?.forEach((callback) => callback({ content: "", metadata: "" }));
        }
      };
      source.addEventListener("heartbeat", () => {
        if (generation === this.generation) this.touch();
      });
      source.addEventListener("message", (event) => {
        if (generation !== this.generation) return;
        this.touch();
        try {
          const data: unknown = JSON.parse(event.data);
          if (!data || typeof data !== "object") return;
          const item = data as Record<string, unknown>;
          if (typeof item.content !== "string") return;
          const payload = {
            content: item.content,
            metadata: typeof item.metadata === "string" ? item.metadata : "",
          };
          this.callbacks.get("message")?.forEach((callback) => {
            try {
              callback(payload);
            } catch {
              /* 一个消费者失败不影响其余消费者。 */
            }
          });
        } catch {
          /* 忽略无效事件。 */
        }
      });
      source.onerror = () => {
        if (generation === this.generation) this.retry();
      };
    } catch {
      if (generation === this.generation) this.retry();
    } finally {
      if (generation === this.generation) this.connecting = false;
    }
  }
  disconnect(): void {
    this.active = false;
    this.generation++;
    this.connecting = false;
    this.retries = 0;
    this.closeSource();
    clearTimeout(this.reconnectTimer);
    this.reconnectTimer = undefined;
  }
  private closeSource(): void {
    clearTimeout(this.heartbeatTimer);
    this.heartbeatTimer = undefined;
    this.source?.close();
    this.source = null;
  }
  private touch(): void {
    clearTimeout(this.heartbeatTimer);
    this.heartbeatTimer = setTimeout(() => this.retry(), 65000);
  }
  private retry(): void {
    this.closeSource();
    this.connecting = false;
    if (!this.active || this.reconnectTimer || this.retries >= 10) return;
    this.generation++;
    const delay = this.options.retryDelay ?? Math.min(1000 * 2 ** this.retries, 30000);
    this.retries++;
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = undefined;
      if (this.active) void this.connect();
    }, delay);
  }
}
