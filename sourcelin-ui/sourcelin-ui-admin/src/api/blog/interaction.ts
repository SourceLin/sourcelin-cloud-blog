import request from "@/utils/request";
import type {
  BlogPageResult,
  BlogQueryParams,
  InteractionOverview,
  InteractionRecordItem,
} from "@/types/api";
import { normalizePageResult } from "./base";

const INTERACTION_BASE_URL = "/blog/admin/interactions";

function normalizeInteractionPageResult(payload: unknown): BlogPageResult<InteractionRecordItem> {
  const base = normalizePageResult<InteractionRecordItem>(payload as Record<string, unknown>);
  const items = base.items.map((item) => ({
    ...item,
    recordKey: `${item.actionType || "LIKE"}:${item.id ?? ""}`,
  }));
  return { ...base, items };
}

const InteractionAPI = {
  getOverview() {
    return request<unknown, InteractionOverview>({
      url: `${INTERACTION_BASE_URL}/dashboard/overview`,
      method: "get",
    });
  },

  async getPage(
    queryParams: BlogQueryParams & {
      actionType?: "LIKE" | "COLLECT";
      userId?: number;
      targetType?: "article" | "say" | "treehole";
      targetId?: number;
      state?: "all" | "active" | "inactive";
    }
  ) {
    const data = await request<unknown, unknown>({
      url: `${INTERACTION_BASE_URL}/actions`,
      method: "get",
      params: {
        ...queryParams,
        page: queryParams.page ?? 1,
        pageSize: queryParams.pageSize ?? 10,
      },
    });
    return normalizeInteractionPageResult(data);
  },

  getActionDetail(actionType: "LIKE" | "COLLECT", id: number) {
    return request<unknown, InteractionRecordItem>({
      url: `${INTERACTION_BASE_URL}/actions/detail`,
      method: "get",
      params: { actionType, id },
    });
  },

  deleteByIds(ids: string | number | Array<string | number>) {
    const rawIds = Array.isArray(ids) ? ids : [ids];
    const likeIds: number[] = [];
    const collectIds: number[] = [];

    rawIds.forEach((rawId) => {
      const text = String(rawId || "");
      const [actionType, idText] = text.split(":");
      const parsedId = Number(idText);
      if (!Number.isFinite(parsedId) || parsedId <= 0) return;
      if (actionType === "COLLECT") {
        collectIds.push(parsedId);
      } else {
        likeIds.push(parsedId);
      }
    });

    const commands: Array<{ actionType: "LIKE" | "COLLECT"; ids: number[] }> = [];
    if (likeIds.length > 0) {
      commands.push({ actionType: "LIKE", ids: likeIds });
    }
    if (collectIds.length > 0) {
      commands.push({ actionType: "COLLECT", ids: collectIds });
    }

    if (commands.length === 0) {
      return Promise.reject(new Error("无可删除的互动记录"));
    }

    return request({
      url: `${INTERACTION_BASE_URL}/actions/delete`,
      method: "post",
      data: commands,
    });
  },
};

export default InteractionAPI;
