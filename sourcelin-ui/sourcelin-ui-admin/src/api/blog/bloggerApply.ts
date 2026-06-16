import request from "@/utils/request";
import type { BloggerApplyItem, BloggerApplyQueryParams } from "@/types/api";

const BloggerApplyAPI = {
  listApply(params: BloggerApplyQueryParams) {
    return request<any, any>({
      url: "/blog/admin/blogger/apply/list",
      method: "get",
      params,
    });
  },

  auditApply(payload: { id: number; status: number; auditOpinion?: string }) {
    return request<any, any>({
      url: "/blog/admin/blogger/apply/audit",
      method: "put",
      data: payload,
    });
  },
};

export default BloggerApplyAPI;
