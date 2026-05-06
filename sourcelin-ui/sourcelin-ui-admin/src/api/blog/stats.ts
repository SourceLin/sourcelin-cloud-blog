import request from "@/utils/request";
import type { BlogStatsSummary, BlogStatsTrend } from "@/types/api";

const STATS_BASE_URL = "/blog/admin/stats";

const BlogStatsAPI = {
  getSummary() {
    return request<any, BlogStatsSummary>({
      url: `${STATS_BASE_URL}/summary`,
      method: "get",
    });
  },

  getTrend(days = 7) {
    return request<any, BlogStatsTrend>({
      url: `${STATS_BASE_URL}/trend`,
      method: "get",
      params: { days },
    });
  },
};

export default BlogStatsAPI;
