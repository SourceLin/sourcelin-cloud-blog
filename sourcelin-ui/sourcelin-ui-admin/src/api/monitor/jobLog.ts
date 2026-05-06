import request from "@/utils/request";
import type { JobLogItem, JobLogQueryParams } from "@/types/api";

const JOB_LOG_BASE_URL = "/schedule/job/log";

function mapJobLogItem(item: Record<string, any>): JobLogItem {
  return {
    jobLogId: Number(item.jobLogId ?? 0),
    jobName: item.jobName,
    jobGroup: item.jobGroup,
    invokeTarget: item.invokeTarget,
    jobMessage: item.jobMessage,
    status: item.status != null ? String(item.status) : undefined,
    exceptionInfo: item.exceptionInfo,
    startTime: item.startTime,
    endTime: item.endTime,
    createTime: item.createTime,
  };
}

function buildQueryParams(queryParams: JobLogQueryParams) {
  const [beginTime, endTime] = queryParams.createTime ?? [];
  return {
    page: queryParams.page ?? 1,
    pageSize: queryParams.pageSize,
    jobName: queryParams.jobName,
    jobGroup: queryParams.jobGroup,
    status: queryParams.status,
    params: {
      beginTime,
      endTime,
    },
  };
}

const JobLogAPI = {
  /** 获取定时任务日志分页 */
  async getPage(queryParams: JobLogQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${JOB_LOG_BASE_URL}/list`,
      method: "get",
      params: buildQueryParams(queryParams),
    });
    const items = (data.items ?? []).map(mapJobLogItem);
    return {
      ...data,
      items,
    } as PageResult<JobLogItem>;
  },

  /** 获取日志详情 */
  async getDetail(jobLogId: number) {
    const data = await request<any, Record<string, any>>({
      url: `${JOB_LOG_BASE_URL}/${jobLogId}`,
      method: "get",
    });
    return mapJobLogItem(data);
  },

  /** 删除日志 */
  deleteByIds(jobLogIds: string) {
    return request({
      url: `${JOB_LOG_BASE_URL}/${jobLogIds}`,
      method: "delete",
    });
  },

  /** 清空日志 */
  clean() {
    return request({
      url: `${JOB_LOG_BASE_URL}/clean`,
      method: "delete",
    });
  },

  /** 导出日志 */
  export(queryParams: JobLogQueryParams) {
    return request({
      url: `${JOB_LOG_BASE_URL}/export`,
      method: "post",
      params: buildQueryParams(queryParams),
      responseType: "blob",
    });
  },
};

export default JobLogAPI;
