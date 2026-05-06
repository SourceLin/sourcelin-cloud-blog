import request from "@/utils/request";
import type {
  LogQueryParams,
  LogItem,
  VisitTrendQueryParams,
  VisitTrendDetail,
  VisitStatsDetail,
} from "@/types/api";

const OPER_LOG_BASE_URL = "/system/operlog";
const BLOG_FRONT_STATS_URL = "/blog/front/stats";
const BLOG_ADMIN_STATS_URL = "/blog/admin/stats";

function mapOperLogItem(item: Record<string, any>): LogItem {
  return {
    id: Number(item.operId ?? 0),
    title: item.title,
    actionType: item.businessType != null ? String(item.businessType) : undefined,
    operatorName: item.operName,
    requestUri: item.operUrl,
    requestMethod: item.requestMethod,
    ip: item.operIp,
    status: Number(item.status ?? 1) === 0 ? 1 : 0, // 兼容页面：1=成功,0=失败
    executionTime: item.costTime != null ? Number(item.costTime) : undefined,
    errorMsg: item.errorMsg,
    createTime: item.operTime,
    content: item.operParam,
    module: item.title,
  };
}

function calcDays(startDate: string, endDate: string): number {
  const start = new Date(startDate).getTime();
  const end = new Date(endDate).getTime();
  if (Number.isNaN(start) || Number.isNaN(end) || end < start) {
    return 7;
  }
  const diff = Math.floor((end - start) / (24 * 60 * 60 * 1000)) + 1;
  return Math.max(1, Math.min(90, diff));
}

const LogAPI = {
  /** 获取日志分页列表 */
  async getPage(queryParams: LogQueryParams) {
    const [beginTime, endTime] = queryParams.createTime ?? [];
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${OPER_LOG_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams.page ?? 1,
        pageSize: queryParams.pageSize,
        title: queryParams.keywords,
        operName: queryParams.keywords,
        params: {
          beginTime,
          endTime,
        },
      },
    });
    const items = data.items.map(mapOperLogItem);
    return {
      ...data,
      items,
    } as PageResult<LogItem>;
  },

  /** 获取访问趋势统计 */
  async getVisitTrend(queryParams: VisitTrendQueryParams) {
    const days = calcDays(queryParams.startDate, queryParams.endDate);
    const data = await request<any, Record<string, any>>({
      url: `${BLOG_ADMIN_STATS_URL}/trend`,
      method: "get",
      params: { days },
    });
    const dates = (data.dates ?? []) as string[];
    const userCounts = (data.userCounts ?? []) as number[];
    const articleCounts = (data.articleCounts ?? []) as number[];
    return {
      dates,
      pvList: data.pvList ?? articleCounts,
      uvList: data.uvList ?? userCounts,
      ipList: data.ipList ?? userCounts,
    } as VisitTrendDetail;
  },

  /** 获取访问概览统计 */
  async getVisitOverview() {
    const data = await request<any, Record<string, any>>({
      url: BLOG_FRONT_STATS_URL,
      method: "get",
    });
    const uv = Number(data.userCount ?? 0);
    const pv = Number(data.viewCount ?? 0);
    return {
      todayUvCount: uv,
      totalUvCount: uv,
      uvGrowthRate: Number(data.userTrend ?? 0),
      todayPvCount: pv,
      totalPvCount: pv,
      pvGrowthRate: Number(data.viewTrend ?? 0),
    } as VisitStatsDetail;
  },
};

export default LogAPI;
