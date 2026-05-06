import request from "@/utils/request";
import type { WebConfigForm } from "@/types/api";

const CONFIG_BASE_URL = "/blog/admin/config";

const BlogConfigAPI = {
  getWebConfig() {
    return request<any, WebConfigForm>({
      url: `${CONFIG_BASE_URL}/getWebConfig`,
      method: "get",
    });
  },

  create(data: Partial<WebConfigForm>) {
    return request({
      url: `${CONFIG_BASE_URL}`,
      method: "post",
      data,
    });
  },

  update(data: Partial<WebConfigForm>) {
    return request({
      url: `${CONFIG_BASE_URL}`,
      method: "put",
      data,
    });
  },
};

export default BlogConfigAPI;
