import request from "@/utils/request";
import type { MobileCapability } from "@/types/api";

const CAPABILITY_BASE_URL = "/blog/admin/mobile/capability";

const MobileCapabilityAPI = {
  /**
   * 获取多端能力配置列表
   */
  list() {
    return request<unknown, { items: MobileCapability[] }>({
      url: `${CAPABILITY_BASE_URL}/list`,
      method: "get",
    });
  },

  /**
   * 获取某端配置详细信息
   */
  getInfo(id: number | string) {
    return request<unknown, MobileCapability>({
      url: `${CAPABILITY_BASE_URL}/${id}`,
      method: "get",
    });
  },

  /**
   * 修改端侧能力配置
   */
  update(data: MobileCapability) {
    return request<MobileCapability, void>({
      url: `${CAPABILITY_BASE_URL}`,
      method: "put",
      data,
    });
  },
};

export default MobileCapabilityAPI;
