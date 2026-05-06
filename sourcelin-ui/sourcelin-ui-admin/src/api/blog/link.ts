import request from "@/utils/request";
import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, LinkItem } from "@/types/api";

const baseApi = createBlogCrudApi<LinkItem, BlogQueryParams>("/blog/admin/link");

const LinkAPI = {
  ...baseApi,

  changeStatus(payload: Partial<LinkItem>) {
    return request({
      url: "/blog/admin/link/status",
      method: "put",
      data: payload,
    });
  },
};

export default LinkAPI;
