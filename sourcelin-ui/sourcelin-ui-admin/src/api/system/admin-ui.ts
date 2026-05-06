import request from "@/utils/request";
import type { AdminUiConfigAggregate } from "@/types/api";

const AdminUiAPI = {
  getDefaults() {
    return request<any, AdminUiConfigAggregate>({
      url: "/system/admin-ui",
      method: "get",
    });
  },

  updateDefaults(data: AdminUiConfigAggregate) {
    return request({
      url: "/system/admin-ui",
      method: "put",
      data,
    });
  },
};

export default AdminUiAPI;
