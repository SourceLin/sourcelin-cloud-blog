import request from "@/utils/request";
import type { OnlineUserItem, OnlineUserQueryParams } from "@/types/api";

const ONLINE_BASE_URL = "/system/online";

function mapOnlineItem(item: Record<string, any>): OnlineUserItem {
  return {
    loginType: item.loginType,
    tokenId: String(item.tokenId ?? ""),
    userName: item.userName,
    ipaddr: item.ipaddr,
    loginLocation: item.loginLocation,
    browser: item.browser,
    os: item.os,
    loginTime: item.loginTime != null ? Number(item.loginTime) : undefined,
  };
}

function buildQueryParams(queryParams: OnlineUserQueryParams) {
  return {
    page: queryParams.page ?? 1,
    pageSize: queryParams.pageSize,
    ipaddr: queryParams.ipaddr,
    userName: queryParams.userName,
    loginType: queryParams.loginType,
  };
}

const OnlineAPI = {
  /** 获取在线用户分页列表 */
  async getPage(queryParams: OnlineUserQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${ONLINE_BASE_URL}/list`,
      method: "get",
      params: buildQueryParams(queryParams),
    });
    const items = (data.items ?? []).map(mapOnlineItem);
    return {
      ...data,
      items,
    } as PageResult<OnlineUserItem>;
  },

  /** 强退在线用户 */
  forceLogout(tokenId: string, loginType?: string) {
    return request({
      url: `${ONLINE_BASE_URL}/${tokenId}`,
      method: "delete",
      params: { loginType },
    });
  },
};

export default OnlineAPI;
