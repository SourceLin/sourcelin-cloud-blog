import request from "@/utils/request";
import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, ContentReportItem } from "@/types/api";

const baseUrl = "/blog/admin/report";
const baseApi = createBlogCrudApi<ContentReportItem, BlogQueryParams>(baseUrl);

const ContentReportAPI = {
  ...baseApi,

  update(id: string | number, data: Partial<ContentReportItem>) {
    return request<Partial<ContentReportItem>, void>({
      url: `${baseUrl}/${id}/status`,
      method: "put",
      data: {
        status: data.status,
        remark: data.remark,
      },
    });
  },
};

export default ContentReportAPI;
