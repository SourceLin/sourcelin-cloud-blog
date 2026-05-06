import request from "@/utils/request";
import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, BlogUserItem } from "@/types/api";

const baseApi = createBlogCrudApi<BlogUserItem, BlogQueryParams>("/blog/admin/user");

const BlogUserAPI = {
  ...baseApi,

  resetPassword(userId: string | number, password: string) {
    return request({
      url: "/blog/admin/user/resetPwd",
      method: "put",
      params: {
        userId,
        password,
      },
    });
  },
};

export default BlogUserAPI;
