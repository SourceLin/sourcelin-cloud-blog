import request from "@/utils/request";
import { createBlogCrudApi } from "./base";
import type { AnnouncementItem, BlogQueryParams } from "@/types/api";

const BASE_URL = "/blog/admin/announcement";

const AnnouncementAPI = {
  ...createBlogCrudApi<AnnouncementItem, BlogQueryParams>(BASE_URL),

  publish(id: string | number) {
    return request({
      url: `${BASE_URL}/${id}/publish`,
      method: "post",
    });
  },

  offline(id: string | number) {
    return request({
      url: `${BASE_URL}/${id}/offline`,
      method: "post",
    });
  },
};

export default AnnouncementAPI;
