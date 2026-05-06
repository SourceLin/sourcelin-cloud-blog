import request from "@/utils/request";
import type { KeywordSaveBody, ModerationItem } from "@/types/api";

const MODERATION_BASE_URL = "/blog/admin/moderation";

const ModerationAPI = {
  getCurrentKeyword() {
    return request<any, ModerationItem>({
      url: `${MODERATION_BASE_URL}/keyword/current`,
      method: "get",
    });
  },

  refreshKeyword() {
    return request({
      url: `${MODERATION_BASE_URL}/keyword/refresh`,
      method: "post",
    });
  },

  saveKeyword(data: KeywordSaveBody) {
    return request({
      url: `${MODERATION_BASE_URL}/keyword/save`,
      method: "post",
      data,
    });
  },
};

export default ModerationAPI;
