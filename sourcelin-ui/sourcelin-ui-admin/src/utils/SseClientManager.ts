import request from "@/utils/request";
import { SseConnection, type SseSource } from "./SseConnection";
export type { SseMessagePayload, SseCallback } from "./SseConnection";
const baseApi = import.meta.env.VITE_APP_BASE_API || "";

function isUnreadChanged(payload: { content: string }): boolean {
  try {
    const event: unknown = JSON.parse(payload.content);
    return (
      typeof event === "object" &&
      event !== null &&
      "type" in event &&
      event.type === "unread_changed"
    );
  } catch {
    return false;
  }
}
const manager = new SseConnection({
  fetchTicket: () =>
    request<unknown, string>({
      url: "/auth/apply-ticket",
      method: "get",
      params: { loginType: "admin" },
    }),
  createSource: (ticket) =>
    new EventSource(
      `${baseApi}/message/stream?ticket=${encodeURIComponent(ticket)}`
    ) as unknown as SseSource,
});

export function subscribeUnreadChanges(refresh: () => void): () => void {
  const handleMessage = (payload: { content: string }) => {
    if (isUnreadChanged(payload)) refresh();
  };
  manager.on("message", handleMessage);
  manager.on("reconnected", refresh);
  return () => {
    manager.off("message", handleMessage);
    manager.off("reconnected", refresh);
  };
}

export default manager;
